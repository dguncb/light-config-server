package net.lightapi.config.server.jdbc;


import net.lightapi.config.server.common.ConfigSecret;
import net.lightapi.config.server.common.ConfigValue;

import java.util.List;

/**
 * ConfigRepository implement class
 */
public class ConfigRepositoryImpl implements ConfigRepository{

    public int  deleteServiceSecret(String key, String serviceId ) {
        return 0;
    }


    public int  deleteServiceValue(String key, String serviceId ) {
        return 0;
    }


    public int  deleteServiceValues(String serviceId ) {
        return 0;
    }


    public ConfigValue createServiceValue(ConfigValue configValue, String serviceId){
        return null;
    }


    public ConfigSecret createServiceSecret(ConfigSecret configSecret, String serviceId){
        return null;

    }



    public ConfigValue createCommonService(ConfigValue configValue) {
        return null;

    }



    public int createCommonServices(List<ConfigValue> configValues){
        return 0;
    }



    public int createServiceValues(List<ConfigValue> configValues, String serviceId){
        return 0;
    }



    public ConfigValue updateServiceValue(ConfigValue configValue, String serviceId){
        return null;
    }


    public ConfigSecret updateServiceSecret(ConfigSecret configSecret, String serviceId){
        return null;
    }


    public ConfigValue updateCommonService(ConfigValue configValue){
        return null;

    }

     public ConfigValue queryServiceValue(String key, String serviceId){
        return null;
     }

     public ConfigSecret queryServiceSecret(String key, String serviceId){
        return null;
     }


     public ConfigValue queryCommonValue(String key){
        return null;
     }

     public List<ConfigValue> queryServiceValues(String serviceId){
        return null;
     }


    public List<ConfigSecret> queryServiceSecrets(String serviceId){
        return null;
    }



    public List<ConfigValue> queryCommonValues(){
        return null;
    }

}
