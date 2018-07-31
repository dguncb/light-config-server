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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Override
    public TemplateConfigValue processConfigValues(List<ConfigValue> configValues, String serviceId  ) {
        return TemplateConfigValue.builder().with(configValues)
                .build();
    }

    @Override
    public TemplateConfigValue processConfigValues( String serviceId  ) {
        return TemplateConfigValue.builder().with(configRepository.queryCommonValues()).with(configRepository.queryServiceValues(serviceId))
                .build();
    }

    @Override
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
}
