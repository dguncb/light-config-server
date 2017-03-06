package com.networknt.config.server.handler;

import com.networknt.config.Config;
import com.networknt.config.server.repository.CopyFileVisitor;
import com.networknt.config.server.repository.DeleteFileVisitor;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.FetchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * curl http://localhost:8080/v1/config/1.2.4/dev/com.networknt.petstore-1.0.0
 */
public class ConfigVersionProfileServiceGetHandler implements HttpHandler {
    static final Logger logger = LoggerFactory.getLogger(ConfigVersionProfileServiceGetHandler.class);
    static final String CONFIG_NAME = "config";
    static final String WORKING_FOLDER = "/working";
    static final String REPOSITORIES_FOLDER = "/repositories";
    static final String BASE_FLODER = System.getProperty("user.home");

    static final String ABSOLUTE_WORKING = BASE_FLODER + WORKING_FOLDER;
    static final String ABSOLUTE_REPOSITORIES = BASE_FLODER + REPOSITORIES_FOLDER;
    static final Map<String, Object> config = Config.getInstance().getJsonMapConfig(CONFIG_NAME);
    static final String defaultRepoUri = (String)config.get("default_repo_uri");

    public ConfigVersionProfileServiceGetHandler() {
        // get the working directories to initialize them if necessary
        if(logger.isDebugEnabled()) logger.debug("initialize working directory " + ABSOLUTE_WORKING);
        initializeDirectory(ABSOLUTE_WORKING);
        if(logger.isDebugEnabled()) logger.debug("initialize repositories directory " + ABSOLUTE_REPOSITORIES);
        initializeDirectory(ABSOLUTE_REPOSITORIES);

        // check if default config repo is there. clone if it not there
        File file = new File(ABSOLUTE_REPOSITORIES + getRepoName(defaultRepoUri));
        if(!file.exists()) {
            // clone
            try (Git result = Git.cloneRepository()
                    .setURI(defaultRepoUri)
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

    private String getRepoName(String repoUri) {
        return repoUri.substring(repoUri.indexOf("/"), repoUri.indexOf(".git"));
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

    public void addDirToZipArchive(ZipOutputStream zos, File fileToZip, String parrentDirectoryName) throws Exception {
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

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        exchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/octet-stream");
        String version = exchange.getQueryParameters().get("version").getFirst();
        String profile = exchange.getQueryParameters().get("profile").getFirst();
        String service = exchange.getQueryParameters().get("service").getFirst();
        if(logger.isDebugEnabled()) logger.debug("version = " + version + " profile = " + profile + " service = " + service);

        // concat file name
        String sourceFolder = ABSOLUTE_REPOSITORIES + getRepoName(defaultRepoUri) + "/" + version;
        String targetFolder = ABSOLUTE_WORKING + "/" + version + "/" + profile + "/" + service + "/config";
        String zipFile = ABSOLUTE_WORKING + "/" + version + "/" + profile + "/" + service + "/config.zip";
        // try to find the file and if it doesn't exist, create it by merging several repos.
        if(logger.isDebugEnabled()) logger.debug("zipFile " + zipFile);
        Path path = Paths.get(zipFile);
        if (!Files.exists(path)) {
            // create a zip from repos.
            if(!Files.exists(Paths.get(targetFolder))) {
                Files.createDirectories(Paths.get(targetFolder));
            }
            logger.debug("sourceFolder = " + sourceFolder);
            logger.debug("targetFolder = " + targetFolder);
            Files.walkFileTree(Paths.get(sourceFolder), new CopyFileVisitor(Paths.get(targetFolder)));
            try (FileOutputStream fos = new FileOutputStream(zipFile)) {
                try(ZipOutputStream zos = new ZipOutputStream(fos)) {
                    addDirToZipArchive(zos, new File(targetFolder), null);
                    zos.flush();
                    fos.flush();
                }
            }
            Files.walkFileTree(Paths.get(targetFolder), new DeleteFileVisitor());
        }
        exchange.getResponseSender().send("OK");

    }
}
