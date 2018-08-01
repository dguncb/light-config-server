package net.lightapi.config.server.service;


import net.lightapi.config.server.common.ConfigService;
import net.lightapi.config.server.common.ConfigValue;
import net.lightapi.config.server.common.template.TemplateConfigValue;


import java.io.IOException;
import java.util.List;

/**
 * Process  the config value result list from persist store
 */
public interface ConfigValueProcessor {

    /**
     * Process the ConfigValue key-value list to template based map
     *
     * @param configValues list of ConfigValue object
     * @param serviceId service Id for the config value
     * @return result config value map, key is the template name
     */
    TemplateConfigValue processConfigValues(List<ConfigValue> configValues, String serviceId );

    TemplateConfigValue processConfigValues(String serviceId );

    void processTemplate( TemplateConfigValue templateConfigValue, ConfigService configService  ) throws Exception;


    void processTemplate( String sourceFolder, TemplateConfigValue templateConfigValue, ConfigService configService  ) throws Exception;

    void getTemplateFromRepo( ConfigService configService  );

}
