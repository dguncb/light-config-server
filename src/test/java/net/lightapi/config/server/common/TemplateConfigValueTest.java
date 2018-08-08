package net.lightapi.config.server.common;

import net.lightapi.config.server.common.template.TemplateConfigValue;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TemplateConfigValueTest {


    private static List<ConfigValue> configValues;

    @BeforeClass
    public static void setUp() {


        ConfigValue  configValue1 = new ConfigValue("server/keystoreName", "tls/server.keystore");
        ConfigValue  configValue2 = new ConfigValue("server/truststoreName", "tls/server.truststore");
        ConfigValue  configValue3 = new ConfigValue("user/email", "aaa@gmail.com");

        configValues = new ArrayList<>();
        configValues.add(configValue1);
        configValues.add(configValue2);
        configValues.add(configValue3);

    }


    @Test
    public void testCase1() {

        TemplateConfigValue templateConfigValue = TemplateConfigValue.builder().with(configValues).build();
        assertTrue(templateConfigValue.transform("user").size()==1);
        
        assertTrue(templateConfigValue.transform("server").size()==2);
    }
}
