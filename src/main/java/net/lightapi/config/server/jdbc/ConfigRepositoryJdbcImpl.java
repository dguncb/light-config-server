package net.lightapi.config.server.jdbc;


import com.networknt.service.SingletonServiceFactory;
import net.lightapi.config.server.common.ConfigSecret;
import net.lightapi.config.server.common.ConfigValue;
import net.lightapi.config.server.common.SecretValue;
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
public class ConfigRepositoryJdbcImpl implements ConfigRepository{

    static final Logger logger = LoggerFactory.getLogger(ConfigRepositoryJdbcImpl.class);
    static final DataSource ds = (DataSource) SingletonServiceFactory.getBean(DataSource.class);

    private static final String INSERT_SERVICE_SECRET = "INSERT INTO config_secret (config_key, service_id, config_secret_hash, config_secret_salt) VALUES (? , ? , ?, ?)";
    private static final String INSERT_SERVICE_VALUE = "INSERT INTO config_value (config_key, service_id, config_value) VALUES (? , ? , ?)";

    private static final String DELETE_SERVICE_SECRET = "DELETE FROM config_secret WHERE config_key = ? AND service_id = ?";
    private static final String DELETE_SERVICE_VALUE = "DELETE FROM config_value WHERE config_key = ? AND service_id = ?";
    private static final String DELETE_SERVICE_VALUES = "DELETE FROM config_value WHERE service_id = ?";

    private static final String UPDATE_SERVICE_VALUE = "UPDATE config_value SET  config_value=? WHERE config_key = ? and service_id = ? ";
    private static final String UPDATE_SERVICE_SECRET = "UPDATE config_secret SET  config_secret_hash=?, config_secret_salt = ?  WHERE config_key = ? and service_id = ? ";

    private static final String QUERY_SERVICE_SECRET = "SELECT config_key, config_secret_hash, config_secret_salt FROM config_secret  WHERE config_key = ? and service_id = ? ";
    private static final String QUERY_SERVICE_VALUE = "SELECT config_key, config_value FROM config_value  WHERE config_key = ? and service_id = ? ";
    private static final String QUERY_SERVICE_SECRETS = "SELECT config_key, config_secret_hash, config_secret_salt FROM config_secret  WHERE  service_id = ? ";
    private static final String QUERY_SERVICE_VALUES = "SELECT config_key, config_value FROM config_value  WHERE  service_id = ? ";

    public int  deleteServiceSecret(String key, String serviceId ) {
        if(logger.isDebugEnabled()) logger.debug("Delete the service secret key=:" + key + "; serviceId = " + serviceId);

        int result = 0;
        try (Connection connection = ds.getConnection(); PreparedStatement stmt = connection.prepareStatement(DELETE_SERVICE_SECRET)) {
            stmt.setString(1, key);
            stmt.setString(2, serviceId);
            result = stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception:", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    public int  deleteServiceValue(String key, String serviceId ) {
        if(logger.isDebugEnabled()) logger.debug("Delete the service value key=:" + key + "; serviceId = " + serviceId);

        int result = 0;
        try (Connection connection = ds.getConnection(); PreparedStatement stmt = connection.prepareStatement(DELETE_SERVICE_VALUE)) {
            stmt.setString(1, key);
            stmt.setString(2, serviceId);
            result = stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception:", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    public int  deleteServiceValues(String serviceId ) {
        if(logger.isDebugEnabled()) logger.debug("Delete the service values serviceId = " + serviceId);

        int result = 0;
        try (Connection connection = ds.getConnection(); PreparedStatement stmt = connection.prepareStatement(DELETE_SERVICE_VALUES)) {
            stmt.setString(1, serviceId);
            result = stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception:", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    public ConfigValue createServiceValue(ConfigValue configValue, String serviceId){
        if(logger.isDebugEnabled()) logger.debug("Store config value :"  + configValue.getKey() + "; " + configValue.getValue());
        try (Connection connection = ds.getConnection(); PreparedStatement stmt = connection.prepareStatement(INSERT_SERVICE_VALUE)) {
            stmt.setString(1, configValue.getKey());
            stmt.setString(2, serviceId);
            stmt.setString(3, configValue.getValue());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception:", e);
            throw new RuntimeException(e);
        }
        return configValue;
    }

    public ConfigSecret createServiceSecret(ConfigSecret configSecret, String serviceId){
        if(logger.isDebugEnabled()) logger.debug("Store config secret :"  + configSecret.getKey() + "; " + configSecret.getSecret());
        //TODO convert secret value first
        try (Connection connection = ds.getConnection(); PreparedStatement stmt = connection.prepareStatement(INSERT_SERVICE_SECRET)) {
            stmt.setString(1, configSecret.getKey());
            stmt.setString(2, serviceId);
            stmt.setString(3, configSecret.getSecretValue().getSecretHash());
            stmt.setString(4, configSecret.getSecretValue().getSecretSalt());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception:", e);
            throw new RuntimeException(e);
        }
        return configSecret;

    }

    public ConfigValue createCommonService(ConfigValue configValue) {
        return createServiceValue(configValue, COMMON_KEY );
    }

    public int createCommonServices(List<ConfigValue> configValues){

        try (Connection connection = ds.getConnection(); PreparedStatement stmt = connection.prepareStatement(INSERT_SERVICE_VALUE)) {
            for (ConfigValue configValue : configValues) {
                stmt.setString(1, configValue.getKey());
                stmt.setString(2,  COMMON_KEY);
                stmt.setString(3, configValue.getValue());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            logger.error("Exception:", e);
            throw new RuntimeException(e);
        }
        return configValues.size();
    }

    public int createServiceValues(List<ConfigValue> configValues, String serviceId){
        try (Connection connection = ds.getConnection(); PreparedStatement stmt = connection.prepareStatement(INSERT_SERVICE_VALUE)) {
            for (ConfigValue configValue : configValues) {
                stmt.setString(1, configValue.getKey());
                stmt.setString(2,  serviceId);
                stmt.setString(3, configValue.getValue());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            logger.error("Exception:", e);
            throw new RuntimeException(e);
        }
        return configValues.size();
    }

    public ConfigValue updateServiceValue(ConfigValue configValue, String serviceId){
        if(logger.isDebugEnabled()) logger.debug("update config value :"  + configValue.getKey() + "; " + configValue.getValue());
        try (Connection connection = ds.getConnection(); PreparedStatement stmt = connection.prepareStatement(UPDATE_SERVICE_VALUE)) {
            stmt.setString(1, configValue.getValue());
            stmt.setString(2, configValue.getKey());
            stmt.setString(3, serviceId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception:", e);
            throw new RuntimeException(e);
        }
        return configValue;

    }


    public ConfigSecret updateServiceSecret(ConfigSecret configSecret, String serviceId){
        if(logger.isDebugEnabled()) logger.debug("update config secret :"  + configSecret.getKey() + "; " + configSecret.getSecret());

        try (Connection connection = ds.getConnection(); PreparedStatement stmt = connection.prepareStatement(UPDATE_SERVICE_SECRET)) {
            stmt.setString(1, configSecret.getSecretValue().getSecretHash());
            stmt.setString(2, configSecret.getSecretValue().getSecretSalt());
            stmt.setString(3, configSecret.getKey());
            stmt.setString(4, serviceId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception:", e);
            throw new RuntimeException(e);
        }
        return configSecret;
    }


    public ConfigValue updateCommonService(ConfigValue configValue){
        if(logger.isDebugEnabled()) logger.debug("update config value :"  + configValue.getKey() + "; " + configValue.getValue());
        try (Connection connection = ds.getConnection(); PreparedStatement stmt = connection.prepareStatement(UPDATE_SERVICE_VALUE)) {
            stmt.setString(1, configValue.getValue());
            stmt.setString(2, configValue.getKey());
            stmt.setString(3, COMMON_KEY);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception:", e);
            throw new RuntimeException(e);
        }
        return configValue;

    }

     public ConfigValue queryServiceValue(String key, String serviceId){
         ConfigValue configValue = null;
         try (Connection connection = ds.getConnection(); PreparedStatement stmt = connection.prepareStatement(QUERY_SERVICE_VALUE)) {
             stmt.setString(1, key);
             stmt.setString(2, serviceId);
             try (ResultSet rs = stmt.executeQuery()) {
                 if (rs.next()) {
                     configValue = new ConfigValue(rs.getString("config_key"), rs.getString("config_value"));
                 }
             }
         } catch (SQLException e) {
             logger.error("Exception:", e);
             throw new RuntimeException(e);
         }
        return configValue;
     }

     public ConfigSecret queryServiceSecret(String key, String serviceId){
         ConfigSecret configSecret = null;
         try (Connection connection = ds.getConnection(); PreparedStatement stmt = connection.prepareStatement(QUERY_SERVICE_SECRET)) {
             stmt.setString(1, key);
             stmt.setString(2, serviceId);
             try (ResultSet rs = stmt.executeQuery()) {
                 if (rs.next()) {
                     configSecret = new ConfigSecret(rs.getString("config_key"), new SecretValue(rs.getString("config_secret_hash"), rs.getString("config_secret_salt")));
                 }
             }
         } catch (SQLException e) {
             logger.error("Exception:", e);
             throw new RuntimeException(e);
         }
         return configSecret;
     }


     public ConfigValue queryCommonValue(String key){
         ConfigValue configValue = null;
         try (Connection connection = ds.getConnection(); PreparedStatement stmt = connection.prepareStatement(QUERY_SERVICE_VALUE)) {
             stmt.setString(1, key);
             stmt.setString(2, COMMON_KEY);
             try (ResultSet rs = stmt.executeQuery()) {
                 if (rs.next()) {
                     configValue = new ConfigValue(rs.getString("config_key"), rs.getString("config_value"));
                 }
             }
         } catch (SQLException e) {
             logger.error("Exception:", e);
             throw new RuntimeException(e);
         }
         return configValue;
     }

     public List<ConfigValue> queryServiceValues(String serviceId){
         List<ConfigValue> configValues = new ArrayList<>();
         try (Connection connection = ds.getConnection(); PreparedStatement stmt = connection.prepareStatement(QUERY_SERVICE_VALUES)) {
             stmt.setString(1, serviceId);
             try (ResultSet rs = stmt.executeQuery()) {
                 while (rs.next()) {
                     ConfigValue  configValue = new ConfigValue(rs.getString("config_key"), rs.getString("config_value"));
                     configValues.add(configValue);
                 }
             }
         } catch (SQLException e) {
             logger.error("Exception:", e);
             throw new RuntimeException(e);
         }
         return configValues;
     }


    public List<ConfigSecret> queryServiceSecrets(String serviceId){
        List<ConfigSecret> configSecrets = new ArrayList<>();
        try (Connection connection = ds.getConnection(); PreparedStatement stmt = connection.prepareStatement(QUERY_SERVICE_SECRETS)) {
            stmt.setString(1, serviceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ConfigSecret  configSecret = new ConfigSecret(rs.getString("config_key"), new SecretValue(rs.getString("config_secret_hash"), rs.getString("config_secret_salt")));
                    configSecrets.add(configSecret);
                }
            }
        } catch (SQLException e) {
            logger.error("Exception:", e);
            throw new RuntimeException(e);
        }
        return configSecrets;
    }



    public List<ConfigValue> queryCommonValues(){
        List<ConfigValue> configValues = new ArrayList<>();
        try (Connection connection = ds.getConnection(); PreparedStatement stmt = connection.prepareStatement(QUERY_SERVICE_VALUES)) {
            stmt.setString(1, COMMON_KEY);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ConfigValue  configValue = new ConfigValue(rs.getString("config_key"), rs.getString("config_value"));
                    configValues.add(configValue);
                }
            }
        } catch (SQLException e) {
            logger.error("Exception:", e);
            throw new RuntimeException(e);
        }
        return configValues;
    }

}
