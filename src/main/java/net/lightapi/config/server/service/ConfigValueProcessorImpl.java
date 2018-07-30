package net.lightapi.config.server.service;

import net.lightapi.config.server.common.ConfigValue;
import net.lightapi.config.server.common.paths.ConfigKeyValuePath;
import net.lightapi.config.server.common.template.FileLoader;
import net.lightapi.config.server.common.template.TemplateConfigValue;
import net.lightapi.config.server.jdbc.ConfigRepository;
import net.lightapi.config.server.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigValueProcessorImpl implements  ConfigValueProcessor{


    private static final Logger logger = LoggerFactory.getLogger(ConfigValueProcessorImpl.class);

    //TODO change it to use config value
    private static final String GEN_CONFIG_FOLDER = "//Users/result";

    private final ConfigRepository configRepository;
    private final FileLoader fileLoader;

    public ConfigValueProcessorImpl (ConfigRepository configRepository,  FileLoader fileLoader){
        this.configRepository = configRepository;
        this.fileLoader = fileLoader;
    }

    public TemplateConfigValue processConfigValues(List<ConfigValue> configValues, String serviceId  ) {
        return TemplateConfigValue.builder().with(configRepository.queryCommonValues()).with(configRepository.queryServiceValues(serviceId))
                .build();
    }

    public void processTemplate( String resourceFolder, TemplateConfigValue templateConfigValue ) throws IOException {
        List<File> templateFiles = fileLoader.getTemplates(resourceFolder);
        for (File templateFile:templateFiles) {
            String template = templateFile.getName();

            fileLoader.saveProcessedTemplate(GEN_CONFIG_FOLDER, template, replacePlaceHolder(templateFile, templateConfigValue.transform(FileUtils.removeExtension(template))));
        }

    }

    private String replacePlaceHolder (File template, List<ConfigKeyValuePath> values) {

        return null;
    }
}
