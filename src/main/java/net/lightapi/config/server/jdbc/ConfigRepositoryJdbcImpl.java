package net.lightapi.config.server.jdbc;


import com.networknt.service.SingletonServiceFactory;
import net.lightapi.config.server.common.ConfigSecret;
import net.lightapi.config.server.common.ConfigService;
import net.lightapi.config.server.common.ConfigValue;
import net.lightapi.config.server.common.SecretValue;
import net.lightapi.config.server.common.crypto.AESConfigSecurity;
import net.lightapi.config.server.handler.InitializeServer;
import net.lightapi.config.server.util.IdGenerator;
import net.lightapi.config.server.util.IdGeneratorImpl;
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

    private static final String INSERT_SERVICE = "INSERT INTO config_service (config_service_id, service_profile, service_id, service_version, template_repository, service_owner) VALUES (? , ? , ?, ?, ?,  ?)";
    private static final String GET_SERVICE_CONFIG_ID = "SELECT config_service_id,service_id,service_profile, service_version, template_repository, service_owner, refreshed   FROM config_service  WHERE service_id = ? and service_profile = ? ";

    private static final String INSERT_SERVICE_SECRET = "INSERT INTO config_secret (config_key, config_service_id, config_secret_hash, config_secret_salt) VALUES (? , ? , ?, ?)";
    private static final String INSERT_SERVICE_VALUE = "INSERT INTO config_value (config_key, config_service_id, config_value) VALUES (? , ? , ?)";

    private static final String DELETE_SERVICE_SECRET = "DELETE FROM config_secret WHERE config_key = ? AND config_service_id = ?";
    private static final String DELETE_SERVICE_VALUE = "DELETE FROM config_value WHERE config_key = ? AND config_service_id = ?";
    private static final String DELETE_SERVICE_VALUES = "DELETE FROM config_value WHERE config_service_id = ?";
    private static final String DELETE_CONFIG_SERVICE = "DELETE FROM config_service WHERE service_id = ? AND service_profile = ? AND service_version = ?";

    private static final String UPDATE_SERVICE_VALUE = "UPDATE config_value SET  config_value=? WHERE config_key = ? and config_service_id = ? ";
    private static final String UPDATE_SERVICE_SECRET = "UPDATE config_secret SET  config_secret_hash=?, config_secret_salt = ?  WHERE config_key = ? and config_service_id = ? ";
    private static final String UPDATE_SERVICE = "UPDATE config_service  SET template_repository =? , service_owner = ?  WHERE service_id = ? AND service_profile =? AND  service_version = ?";

    private static final String QUERY_SERVICE_SECRET = "SELECT config_key, config_secret_hash, config_secret_salt FROM config_secret  WHERE config_key = ? and config_service_id = ? ";
    private static final String QUERY_SERVICE_VALUE = "SELECT config_key, config_value FROM config_value  WHERE config_key = ? and config_service_id = ? ";
    private static final String QUERY_SERVICE_SECRETS = "SELECT config_key, config_secret_hash, config_secret_salt FROM config_secret  WHERE  config_service_id = ? ";
    private static final String QUERY_SERVICE_VALUES = "SELECT config_key, config_value FROM config_value  WHERE  config_service_id = ? ";
    @Override
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

    @Override
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

    @Override
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

    @Override
    public String deleteConfigService(ConfigService configService){
        if(logger.isDebugEnabled()) logger.debug("Delete the service  key=:" + configService.getServiceId() + "; profile = " + configService.getProfile());

        try (Connection connection = ds.getConnection(); PreparedStatement stmt = connection.prepareStatement(DELETE_CONFIG_SERVICE)) {
            stmt.setString(1, configService.getServiceId());
            stmt.setString(2, configService.getProfile());
            stmt.setString(3, configService.getVersion());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception:", e);
            throw new RuntimeException(e);
        }
        return configService.toString();
    }

    @Override
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

    @Override
    public ConfigService createConfigService(ConfigService configService){
        if(logger.isDebugEnabled()) logger.debug("Store config service :"  + configService.getServiceId() + "; " + configService.getProfile());
        if (configService.getConfigServiceId()==null) {
            IdGenerator idGenerator = new IdGeneratorImpl();
            configService.setConfigServiceId(idGenerator.genId().asString());
        }
        try (Connection connection = ds.getConnection(); PreparedStatement stmt = connection.prepareStatement(INSERT_SERVICE)) {
            stmt.setString(1, configService.getConfigServiceId());
            stmt.setString(2, configService.getProfile());
            stmt.setString(3, configService.getServiceId()==null? COMMON_KEY : configService.getServiceId());
            stmt.setString(4, configService.getVersion());
            stmt.setString(5, configService.getTemplateRepository());
            stmt.setString(6, configService.getServiceOwner());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception:", e);
            throw new RuntimeException(e);
        }
        return configService;
    }

    @Override
    public ConfigValue createServiceSecret(ConfigValue configValue, String serviceId){
        if(logger.isDebugEnabled()) logger.debug("Store config secret :"  + configValue.getKey() + "; " + configValue.getValue());
        if (InitializeServer.key==null) {
            logger.error("config server doesn't initialized the encrpt key yet....");
        } else {
            //TODO verify if the the service EncryptionAlgorithm is EncryptionAlgorithm.AES
            AESConfigSecurity AESConfigSecurity = new AESConfigSecurity(InitializeServer.key);
            configValue.setValue(AESConfigSecurity.ecrypt(configValue.getValue()));
        }
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

    @Override
    public int createServiceSecrets(List<ConfigValue> configValues, String serviceId) {
        try (Connection connection = ds.getConnection(); PreparedStatement stmt = connection.prepareStatement(INSERT_SERVICE_VALUE)) {
            for (ConfigValue configValue : configValues) {
                if (InitializeServer.key==null) {
                    logger.error("config server doesn't initialized the encrpt key yet....");
                } else {
                    //TODO verify if the the service EncryptionAlgorithm is EncryptionAlgorithm.AES
                    AESConfigSecurity AESConfigSecurity = new AESConfigSecurity(InitializeServer.key);
                    configValue.setValue(AESConfigSecurity.ecrypt(configValue.getValue()));
                }
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


    @Override
    public ConfigValue createCommonService(ConfigValue configValue) {
        return createServiceValue(configValue, COMMON_KEY );
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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


    @Override
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

    @Override
    public ConfigService queryConfigService(String serviceId, String profile , String version){
        ConfigService configService = null;
        try (Connection connection = ds.getConnection(); PreparedStatement stmt = connection.prepareStatement(GET_SERVICE_CONFIG_ID)) {
            stmt.setString(1, serviceId);
            stmt.setString(2, profile);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    configService = new  ConfigService();
                    configService.setConfigServiceId(rs.getString("config_service_id"));
                    configService.setServiceId(serviceId);
                    configService.setProfile(profile);
                    configService.setServiceOwner(rs.getString("config_service_id"));
                    configService.setVersion(rs.getString("service_version"));
                    configService.setTemplateRepository(rs.getString("template_repository"));
                    configService.setRefreshed(rs.getString("template_repository")=="Y"?true:false );
                }
            }
        } catch (SQLException e) {
            logger.error("Exception:", e);
            throw new RuntimeException(e);
        }
        return configService;
    }


    @Override
    public ConfigService updateConfigService(ConfigService configService) {
        if(logger.isDebugEnabled()) logger.debug("update config SERVICE :"  + configService.getServiceId() + "; " + configService.getProfile());
        try (Connection connection = ds.getConnection(); PreparedStatement stmt = connection.prepareStatement(UPDATE_SERVICE)) {
            stmt.setString(1, configService.getTemplateRepository());
            stmt.setString(2, configService.getServiceOwner());
            stmt.setString(3, configService.getServiceId());
            stmt.setString(4, configService.getProfile());
            stmt.setString(5, configService.getVersion());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception:", e);
            throw new RuntimeException(e);
        }
        return configService;
    }
}
