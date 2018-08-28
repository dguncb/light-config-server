package net.lightapi.config.server.service;

import com.networknt.service.SingletonServiceFactory;
import net.lightapi.config.server.common.ConfigService;
import net.lightapi.config.server.common.ConfigValue;
import net.lightapi.config.server.common.paths.ConfigKeyValuePath;
import net.lightapi.config.server.common.template.TemplateConfigValue;
import net.lightapi.config.server.common.template.TemplatesFileLoader;
import net.lightapi.config.server.jdbc.ConfigRepository;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jnlp.IntegrationService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class ConfigValueProcessorTest {

    private ConfigRepository configRepository = (ConfigRepository) SingletonServiceFactory.getBean(ConfigRepository.class);

    private  TemplatesFileLoader fileLoader = new TemplatesFileLoader();
    private ConfigValueProcessorImpl configValueProcessor = new  ConfigValueProcessorImpl(configRepository, fileLoader);
    private static List<ConfigValue> configValues;
    private static String content;
    private static  List<ConfigKeyValuePath> values;
    private static ConfigService configService;

    @BeforeClass
    public static void setUp() {
        ConfigValue  configValue1 = new ConfigValue("server/keystoreName", "tls/server.keystore");
        ConfigValue  configValue2 = new ConfigValue("server/truststoreName", "tls/server.truststore");
        ConfigValue  configValue3 = new ConfigValue("user/email", "aaa@gmail.com");

        configValues = new ArrayList<>();
        configValues.add(configValue1);
        configValues.add(configValue2);
        configValues.add(configValue3);

        TemplateConfigValue templateConfigValue = TemplateConfigValue.builder().with(configValues)
                .build();
        values = templateConfigValue.transform("server");
        content = "keystoreName: {server/keystoreName} \n truststoreName:{server/truststoreName}";

        configService = new ConfigService();
        configService.setTemplateRepository("https://github.com/chenyan71/light-config-template.git");
    }


    @Test
    public void testReplacePlaceHolder()  {
        System.out.println("origin:" + content);
        String result = configValueProcessor.replacePlaceHolder(content, values);
        System.out.println("result:" + result);

    }


    @Test
    public void testReplacePlaceHolder2()  throws IOException {
/*
        List<File> files =  fileLoader.getTemplates(System.getProperty("user.home")+ "/workspace/light-config-server/src/test/resources/template");
        String result = configValueProcessor.replacePlaceHolder(files.get(0), values);
        System.out.println("result:" + result);
*/
    }

    @Test
    public void testGetTemplateFromRepo()  {

/*
        configValueProcessor.getTemplateFromRepo(configService);
        File file = new File(configValueProcessor.getRepoPath(configService));
        assertTrue(file.exists());
        assertTrue(file.listFiles().length>0);*/
    }

    
}
