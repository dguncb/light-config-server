package net.lightapi.config.server.service;


import net.lightapi.config.server.common.ConfigValue;
import net.lightapi.config.server.common.TemplateConfigValue;

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
    TemplateConfigValue processConfigValues( List<ConfigValue> configValues );

    void processTemplate( String resourceFolder, TemplateConfigValue templateConfigValue );


}
