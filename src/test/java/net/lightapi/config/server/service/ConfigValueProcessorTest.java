package net.lightapi.config.server.service;

import com.networknt.service.SingletonServiceFactory;
import net.lightapi.config.server.common.ConfigValue;
import net.lightapi.config.server.common.paths.ConfigKeyValuePath;
import net.lightapi.config.server.common.template.TemplateConfigValue;
import net.lightapi.config.server.common.template.TemplatesFileLoader;
import net.lightapi.config.server.jdbc.ConfigRepository;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigValueProcessorTest {

    private ConfigRepository configRepository = (ConfigRepository) SingletonServiceFactory.getBean(ConfigRepository.class);

    private  TemplatesFileLoader fileLoader = new TemplatesFileLoader();
    private ConfigValueProcessorImpl configValueProcessor = new  ConfigValueProcessorImpl(configRepository, fileLoader);
    private static List<ConfigValue> configValues;
    private static String content;
    private static  List<ConfigKeyValuePath> values;

    @BeforeClass
    public static void setUp() {
        ConfigValue  configValue1 = new ConfigValue("sever/keystoreName", "tls/server.keystore");
        ConfigValue  configValue2 = new ConfigValue("sever/truststoreName", "tls/server.truststore");
        ConfigValue  configValue3 = new ConfigValue("user/email", "aaa@gmail.com");

        configValues = new ArrayList<>();
        configValues.add(configValue1);
        configValues.add(configValue2);
        configValues.add(configValue3);

        TemplateConfigValue templateConfigValue = TemplateConfigValue.builder().with(configValues)
                .build();
        values = templateConfigValue.transform("sever");
        content = "keystoreName: {sever/keystoreName} \n truststoreName:{sever/truststoreName}";

    }


    @Test
    public void testReplacePlaceHolder()  {
        System.out.println("origin:" + content);
        String result = configValueProcessor.replacePlaceHolder(content, values);
        System.out.println("result:" + result);

    }

}
