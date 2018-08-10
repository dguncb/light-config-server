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
     * @return result config value map, key is the template name
     */
    TemplateConfigValue processConfigValues(List<ConfigValue> configValues);

    /**
     * Process the ConfigValue key-value list to template based map
     *
     * @param serviceId service Id for the config value
     * @param profile list of config server profile which indicate environment: DIT/SIT/DEV...
     * @param version service version
     * @return result config value map, key is the template name
     */
    TemplateConfigValue processConfigValues(String serviceId,  String profile, String version);

    String  processTemplate( TemplateConfigValue templateConfigValue, ConfigService configService  ) throws Exception;


    String processTemplate( String sourceFolder, TemplateConfigValue templateConfigValue, ConfigService configService  ) throws Exception;

    void getTemplateFromRepo( ConfigService configService  );

}
