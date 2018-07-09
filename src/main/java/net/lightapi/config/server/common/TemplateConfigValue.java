package net.lightapi.config.server.common;

import java.util.List;
import java.util.Map;

public class TemplateConfigValue {

    private Map<String, List<ConfigValue>> templateValues;

    public Map<String, List<ConfigValue>> getTemplateValues() {
        return templateValues;
    }

    public void setTemplateValues(Map<String, List<ConfigValue>> templateValues) {
        this.templateValues = templateValues;
    }
}
