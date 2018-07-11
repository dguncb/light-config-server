package net.lightapi.config.server.jdbc;


import com.networknt.service.SingletonServiceFactory;
import net.lightapi.config.server.common.ConfigSecret;
import net.lightapi.config.server.common.ConfigValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * ConfigRepository implement class
 */
public class ConfigRepositoryArrangoImpl implements ConfigRepository{

    static final Logger logger = LoggerFactory.getLogger(ConfigRepositoryArrangoImpl.class);

    public int  deleteServiceSecret(String key, String serviceId ) {

        return 0;
    }

    public int  deleteServiceValue(String key, String serviceId ) {
        if(logger.isDebugEnabled()) logger.debug("Delete the service value key=:" + key + "; serviceId = " + serviceId);


        return 0;
    }

    public int  deleteServiceValues(String serviceId ) {
        if(logger.isDebugEnabled()) logger.debug("Delete the service values serviceId = " + serviceId);

        return 0;
    }

    public ConfigValue createServiceValue(ConfigValue configValue, String serviceId){

        return configValue;
    }

    public ConfigSecret createServiceSecret(ConfigSecret configSecret, String serviceId){

        return configSecret;

    }

    public ConfigValue createCommonService(ConfigValue configValue) {
        return createServiceValue(configValue, COMMON_KEY );
    }

    public int createCommonServices(List<ConfigValue> configValues){

        return configValues.size();
    }

    public int createServiceValues(List<ConfigValue> configValues, String serviceId){

        return configValues.size();
    }

    public ConfigValue updateServiceValue(ConfigValue configValue, String serviceId){

        return configValue;

    }


    public ConfigSecret updateServiceSecret(ConfigSecret configSecret, String serviceId){

        return configSecret;
    }


    public ConfigValue updateCommonService(ConfigValue configValue){

        return configValue;

    }

     public ConfigValue queryServiceValue(String key, String serviceId){
         ConfigValue configValue = null;

        return configValue;
     }

     public ConfigSecret queryServiceSecret(String key, String serviceId){
         ConfigSecret configSecret = null;

         return configSecret;
     }


     public ConfigValue queryCommonValue(String key){
         ConfigValue configValue = null;

         return configValue;
     }

     public List<ConfigValue> queryServiceValues(String serviceId){
         List<ConfigValue> configValues = new ArrayList<>();

         return configValues;
     }


    public List<ConfigSecret> queryServiceSecrets(String serviceId){
        List<ConfigSecret> configSecrets = new ArrayList<>();

        return configSecrets;
    }



    public List<ConfigValue> queryCommonValues(){
        List<ConfigValue> configValues = new ArrayList<>();

        return configValues;
    }

}
