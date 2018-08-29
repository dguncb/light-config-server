package net.lightapi.config.server.service;

import net.lightapi.config.server.common.ConfigService;
import net.lightapi.config.server.common.ConfigValue;
import net.lightapi.config.server.common.crypto.AESConfigSecurity;
import net.lightapi.config.server.common.paths.ConfigKeyValuePath;
import net.lightapi.config.server.common.template.FileLoader;
import net.lightapi.config.server.common.template.TemplateConfigValue;
import net.lightapi.config.server.handler.InitializeServer;
import net.lightapi.config.server.jdbc.ConfigRepository;
import net.lightapi.config.server.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import static com.networknt.utility.Decryptor.CRYPT_PREFIX;

public class ConfigValueProcessorImpl implements  ConfigValueProcessor{


    private static final Logger logger = LoggerFactory.getLogger(ConfigValueProcessorImpl.class);

    static final String BASE_FOLDER = System.getProperty("user.home");
    static final String WORKING_FOLDER = "/working";
    static final String ABSOLUTE_WORKING = BASE_FOLDER + WORKING_FOLDER;
    static final String REPOSITORIES_FOLDER = "/repositories";
    static final String ABSOLUTE_REPOSITORIES = BASE_FOLDER + REPOSITORIES_FOLDER;


    private final ConfigRepository configRepository;
    private final FileLoader fileLoader;
    private AESConfigSecurity aESConfigSecurity;

    public ConfigValueProcessorImpl (ConfigRepository configRepository,  FileLoader fileLoader){
        this.configRepository = configRepository;
        this.fileLoader = fileLoader;

        if(logger.isDebugEnabled()) logger.debug("initialize working directory " + ABSOLUTE_WORKING);
        initializeDirectory(ABSOLUTE_WORKING);
        if(logger.isDebugEnabled()) logger.debug("initialize repositories directory " + ABSOLUTE_REPOSITORIES);
        initializeDirectory(ABSOLUTE_REPOSITORIES);
        if (InitializeServer.key!=null) {
            aESConfigSecurity= new AESConfigSecurity(InitializeServer.key);
        }
    }

    @Override
    public TemplateConfigValue processConfigValues(List<ConfigValue> configValues) {
        return TemplateConfigValue.builder().with(configValues)
                .build();
    }

    @Override
    public TemplateConfigValue processConfigValues( String serviceId,  String profile, String version ) {
        TemplateConfigValue templateConfigValue = null;
        ConfigService configService = configRepository.queryConfigService(serviceId, profile, version);
        ConfigService commonConfigService = configRepository.queryConfigService(ConfigRepository.COMMON_KEY, profile, version);
        if (configService!=null) {
            TemplateConfigValue.TemplateConfigValueBuilder builder = TemplateConfigValue.builder().with(getClearTextConfigValues(configService.getConfigServiceId()));
            if (commonConfigService!=null) builder.with(getClearTextConfigValues(commonConfigService.getConfigServiceId()));
            templateConfigValue = builder.build();
        }
        return  templateConfigValue;
    }

    @Override
    public String processTemplate(  TemplateConfigValue templateConfigValue, ConfigService configService ) throws Exception {
        return processTemplate(ABSOLUTE_REPOSITORIES, templateConfigValue, configService);
    }

    @Override
    public String processTemplate(  String sourceFolder, TemplateConfigValue templateConfigValue, ConfigService configService ) throws Exception {

        String targetFolder = ABSOLUTE_WORKING + "/" + configService.getVersion() + "/" + configService.getProfile() + "/" + configService.getServiceId() + "/config";
        String zipFile = ABSOLUTE_WORKING + "/" + configService.getVersion() + "/" + configService.getProfile()  + "/" + configService.getServiceId() + "/config.zip";

        if(logger.isDebugEnabled()) logger.debug("version = " + configService.getVersion() + " profile = " + configService.getProfile() + " service = " + configService.getServiceId());
        if(!Files.exists(Paths.get(targetFolder))) {
            Files.createDirectories(Paths.get(targetFolder));
        }

        List<File> templateFiles = fileLoader.getTemplates(sourceFolder);
        for (File templateFile:templateFiles) {
            String template = templateFile.getName();
            fileLoader.saveProcessedTemplate(targetFolder, template, replacePlaceHolder(templateFile, templateConfigValue.transform(FileUtils.removeExtension(template))));
        }

        if(logger.isDebugEnabled()) logger.debug("zipFile " + zipFile);
        Path path = Paths.get(zipFile);
        if (!Files.exists(path) || configService.isRefreshed()) {
            try (FileOutputStream fos = new FileOutputStream(zipFile)) {
                try(ZipOutputStream zos = new ZipOutputStream(fos)) {
                    addDirToZipArchive(zos, new File(targetFolder), null);
                    zos.flush();
                    fos.flush();
                }
            }

        }
        return zipFile;
    }

    @Override
    public void getTemplateFromRepo (ConfigService configService ) {
        File file = new File(ABSOLUTE_REPOSITORIES + getRepoName(configService.getTemplateRepository()));

        if(!file.exists()) {
            // clone
            try (Git result = Git.cloneRepository()
                    .setURI(configService.getTemplateRepository())
                    .setDirectory(file)
                    .call()) {
                // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
                if(logger.isDebugEnabled()) logger.debug("Having repository: " + result.getRepository().getDirectory());
            } catch (GitAPIException e) {
                logger.error("GitAPIException", e);
                // TODO return error
            }
        } else {
              // pull
            try {
                FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
                repositoryBuilder.setMustExist( true );
                repositoryBuilder.readEnvironment();
                repositoryBuilder.addCeilingDirectory(file);
                repositoryBuilder.findGitDir();
                try (Repository repository = repositoryBuilder.build()) {
                    if(logger.isDebugEnabled()) logger.debug("Starting fetch" + file.getAbsolutePath());
                    try (Git git = new Git(repository)) {
                        PullResult result = git.pull().call();
                        if(logger.isDebugEnabled()) logger.debug("Messages: " + result.getMergeResult().toString());
                    } catch (GitAPIException e) {
                        logger.error("GitAPIException", e);
                        // TODO
                    }
                }
            } catch (IOException e) {
                logger.error("IOException", e);
                // TODO return error
            }
        }
    }

    protected String replacePlaceHolder (File template, List<ConfigKeyValuePath> values) throws IOException {
        Charset charset = StandardCharsets.UTF_8;

        String content = new String(Files.readAllBytes(template.toPath()), charset);
        return replacePlaceHolder(content, values);
    }

    protected String replacePlaceHolder (String content,  List<ConfigKeyValuePath> values) {
        Map<String, String> replacementMap = convert(values);
        Pattern pattern = Pattern.compile("\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(content);

        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (matcher.find()) {
            String replacement = replacementMap.get(matcher.group(1));
            builder.append(content.substring(i, matcher.start()));
            if (replacement == null)
                builder.append(matcher.group(0));
            else
                builder.append(replacement);
            i = matcher.end();
        }
        builder.append(content.substring(i, content.length()));
        return builder.toString();
    }

    private Map<String, String> convert(List<ConfigKeyValuePath> values) {
        Map<String, String> replaceValues = new HashMap<>();
        for (ConfigKeyValuePath value:values) {
            replaceValues.put(value.toPath(), value.getValue());
        }
        return replaceValues;
    }

    protected void addDirToZipArchive(ZipOutputStream zos, File fileToZip, String parrentDirectoryName) throws Exception {
        if (fileToZip == null || !fileToZip.exists()) {
            return;
        }

        String zipEntryName = fileToZip.getName();
        if (parrentDirectoryName!=null && !parrentDirectoryName.isEmpty()) {
            zipEntryName = parrentDirectoryName + "/" + fileToZip.getName();
        }

        if (fileToZip.isDirectory()) {
            System.out.println("+" + zipEntryName);
            for (File file : fileToZip.listFiles()) {
                addDirToZipArchive(zos, file, zipEntryName);
            }
        } else {
            System.out.println("   " + zipEntryName);
            byte[] buffer = new byte[1024];
            FileInputStream fis = new FileInputStream(fileToZip);
            zos.putNextEntry(new ZipEntry(zipEntryName));
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
            fis.close();
        }
    }

    private void initializeDirectory(String absPath) {
        // check if it exists
        File file = new File(absPath);
        if (file.exists()) {
            if (file.isFile()) {
                logger.info("path is a file, remove it");
                file.delete();
                createDirectory(absPath);
            }
        } else {
            // create a directory here
            createDirectory(absPath);
        }
    }

    private void createDirectory(String p) {
        // create a directory here
        Path path = Paths.get(p);
        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            logger.error("IOException", e);
        }
    }

    private String getRepoName(String repoUri) {
        return repoUri.substring(repoUri.lastIndexOf("/"), repoUri.indexOf(".git"));
    }

    protected String getRepoPath(ConfigService configService) {
        return ABSOLUTE_REPOSITORIES + getRepoName(configService.getTemplateRepository());
    }

    protected  List<ConfigValue> getClearTextConfigValues (String configServiceId) {
        List<ConfigValue>  configValues = configRepository.queryServiceValues(configServiceId);

        if (aESConfigSecurity !=null) {
            configValues = configValues.stream().map(c->decrptValue(c)).collect(Collectors.toList());
        }
        return configValues;
    }

    protected  ConfigValue decrptValue (ConfigValue sourceConfig) {
        if (sourceConfig.getValue().startsWith(CRYPT_PREFIX) ) {
            sourceConfig.setValue(aESConfigSecurity.decrypt(sourceConfig.getValue()));

        }
        return sourceConfig;
    }
}
