package net.lightapi.config.server.jdbc;



import net.lightapi.config.server.common.ConfigSecret;
import net.lightapi.config.server.common.ConfigService;
import net.lightapi.config.server.common.ConfigValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.List;

/**
 * ConfigRepository implement class
 */
public class ConfigRepositoryArrangoImpl implements ConfigRepository{

    static final Logger logger = LoggerFactory.getLogger(ConfigRepositoryArrangoImpl.class);

    @Override
    public int  deleteServiceSecret(String key, String serviceId ) {

        return 0;
    }

    @Override
    public String deleteConfigService(ConfigService configService){
        return null;
    }

    @Override
    public int  deleteServiceValue(String key, String serviceId ) {
        if(logger.isDebugEnabled()) logger.debug("Delete the service value key=:" + key + "; serviceId = " + serviceId);


        return 0;
    }

    @Override
    public int  deleteServiceValues(String serviceId ) {
        if(logger.isDebugEnabled()) logger.debug("Delete the service values serviceId = " + serviceId);

        return 0;
    }

    @Override
    public ConfigValue createServiceValue(ConfigValue configValue, String serviceId){

        return configValue;
    }

    @Override
    public ConfigValue createServiceSecret(ConfigValue configValue, String serviceId){

        return configValue;

    }

    @Override
    public int createServiceSecrets(List<ConfigValue> configValues, String serviceId) {
        return 0;
    }

    @Override
    public ConfigValue createCommonService(ConfigValue configValue) {
        return createServiceValue(configValue, COMMON_KEY );
    }

    @Override
    public int createCommonServices(List<ConfigValue> configValues){

        return configValues.size();
    }

    @Override
    public int createServiceValues(List<ConfigValue> configValues, String serviceId){

        return configValues.size();
    }

    @Override
    public ConfigValue updateServiceValue(ConfigValue configValue, String serviceId){

        return configValue;

    }

    @Override
    public ConfigSecret updateServiceSecret(ConfigSecret configSecret, String serviceId){

        return configSecret;
    }

    @Override
    public ConfigValue updateCommonService(ConfigValue configValue){

        return configValue;

    }

    @Override
     public ConfigValue queryServiceValue(String key, String serviceId){
         ConfigValue configValue = null;

        return configValue;
     }

     public ConfigSecret queryServiceSecret(String key, String serviceId){
         ConfigSecret configSecret = null;

         return configSecret;
     }

    @Override
     public ConfigValue queryCommonValue(String key){
         ConfigValue configValue = null;

         return configValue;
     }

    @Override
     public List<ConfigValue> queryServiceValues(String serviceId){
         List<ConfigValue> configValues = new ArrayList<>();

         return configValues;
     }

    @Override
    public List<ConfigSecret> queryServiceSecrets(String serviceId){
        List<ConfigSecret> configSecrets = new ArrayList<>();

        return configSecrets;
    }


    @Override
    public List<ConfigValue> queryCommonValues(){
        List<ConfigValue> configValues = new ArrayList<>();

        return configValues;
    }

    @Override
    public ConfigService queryConfigService(String serviceId, String profile , String version){
        return null;
    }

    @Override
    public ConfigService createConfigService(ConfigService configService){
        return null;
    }

    @Override
    public ConfigService updateConfigService(ConfigService configService) {
        return null;
    }
}
