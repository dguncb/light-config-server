package net.lightapi.config.server.jdbc;



import com.networknt.service.SingletonServiceFactory;
import net.lightapi.config.server.common.ConfigValue;
import org.h2.tools.RunScript;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Junit test class for ConfigRepositoryJdbcImpl.
 * use H2 test database for data source
 */
public class ConfigRepositoryJdbcImplTest {

    public static DataSource ds;

    static {
        ds = (DataSource) SingletonServiceFactory.getBean(DataSource.class);
       try (Connection connection = ds.getConnection()) {
            // Runscript doesn't work need to execute batch here.
            String schemaResourceName = "/config_server_h2.sql";
            InputStream in = ConfigRepositoryJdbcImplTest.class.getResourceAsStream(schemaResourceName);

            if (in == null) {
                throw new RuntimeException("Failed to load resource: " + schemaResourceName);
            }
            InputStreamReader reader = new InputStreamReader(in, Charset.defaultCharset());
            RunScript.execute(connection, reader);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ConfigRepository configRepository = (ConfigRepository) SingletonServiceFactory.getBean(ConfigRepository.class);

    private static List<ConfigValue> configValues;
    private static  ConfigValue  configValue;
    private static  String  serviceId;
    @BeforeClass
    public static void setUp() {
        serviceId = "com.networknt.oauth2-key-1.0.0";
        ConfigValue  configValue1 = new ConfigValue("sever|keystoreName", "tls/server.keystore");
        ConfigValue  configValue2 = new ConfigValue("sever|truststoreName", "tls/server.truststore");

        configValues = new ArrayList<> ();
        configValues.add(configValue1);
        configValues.add(configValue2);

        configValue =  new ConfigValue("sever|dynamicPort", "false");
    }

    @Test
    public void testCreateServiceValue() {
        ConfigValue result = configRepository.createServiceValue(configValue, serviceId);
        assertNotNull(result);
    }

    @Test
    public void testCreateServiceValues() {
        int result = configRepository.createServiceValues(configValues, serviceId);
        assertNotNull(result>0);
    }

}
