package net.lightapi.config.server.common.template;

import net.lightapi.config.server.common.ConfigValue;
import net.lightapi.config.server.common.paths.ConfigKeyValuePath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TemplateConfigValue implements  TemplateMapping {

    private Map<String, List<ConfigValue>> mappings;

    public TemplateConfigValue(Map<String, List<ConfigValue>> mappings) {
        this.mappings = mappings;
    }

    public Map<String, List<ConfigValue>> getMappings() {
        return mappings;
    }

    public static class TemplateConfigValueBuilder {

        private  Map<String, List<ConfigValue>> mappings = new HashMap<>();

        public TemplateConfigValueBuilder with(List<ConfigValue> configValues) {

            for (ConfigValue configValue:configValues) {
                String templateName = ConfigKeyValuePath.parse(configValue).getTemplate();
                if (mappings.containsKey(templateName)) {
                    mappings.get(templateName).add(configValue);
                } else {
                    List<ConfigValue> configValueList = new ArrayList<> ();
                    configValueList.add(configValue);
                    mappings.put(templateName, configValueList);
                }
            }
            return this;
        }

        public TemplateConfigValue build() {
            return new TemplateConfigValue(mappings);
        }
    }

    public static TemplateConfigValueBuilder builder() {
        return new TemplateConfigValueBuilder();
    }

    @Override
    public List<ConfigKeyValuePath> transform(String template) {
        List<ConfigValue> configValues = mappings.getOrDefault(template, new ArrayList<>());
        return configValues.stream().map(c->ConfigKeyValuePath.parse(c)).collect(Collectors.toList());

    }
}
