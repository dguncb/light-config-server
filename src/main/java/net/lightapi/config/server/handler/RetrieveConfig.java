
package net.lightapi.config.server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.NioUtils;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import io.undertow.server.HttpServerExchange;
import net.lightapi.config.server.common.ConfigService;
import net.lightapi.config.server.common.ConfigValue;
import net.lightapi.config.server.common.template.TemplateConfigValue;
import net.lightapi.config.server.common.template.TemplatesFileLoader;
import net.lightapi.config.server.jdbc.ConfigRepository;
import net.lightapi.config.server.service.ConfigValueProcessorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceHandler(id="lightapi.net/config/retrieve-config/0.1.0")
public class RetrieveConfig implements Handler {
    static final Logger logger = LoggerFactory.getLogger(RetrieveConfig.class);
    private ConfigRepository configRepository = (ConfigRepository) SingletonServiceFactory.getBean(ConfigRepository.class);
    private TemplatesFileLoader fileLoader = new TemplatesFileLoader();
    private ConfigValueProcessorImpl configValueProcessor = new  ConfigValueProcessorImpl(configRepository, fileLoader);

    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {

        ObjectMapper mapper = new ObjectMapper();
        String result;

        try {
            String json = mapper.writeValueAsString(input);

            Map<String, String> configValueMap = mapper.readValue(json, Map.class);
            String serviceId = configValueMap.get("serviceId");
            String profile = configValueMap.get("profile");
            String version = configValueMap.get("version");
            ConfigService configService = configRepository.queryConfigService(serviceId, profile, version);
            ConfigService commonConfigService = configRepository.queryConfigService(ConfigRepository.COMMON_KEY, profile, version);
            TemplateConfigValue templateConfigValue = TemplateConfigValue.builder().with(configRepository.queryServiceValues(configService.getConfigServiceId()))
                    .with(configRepository.queryServiceValues(commonConfigService.getConfigServiceId())).build();

            configValueProcessor.getTemplateFromRepo(configService);
            configValueProcessor.processTemplate(templateConfigValue, configService);

        } catch (Exception e) {
            result = e.getMessage();
        }


        return NioUtils.toByteBuffer("");
    }
}
