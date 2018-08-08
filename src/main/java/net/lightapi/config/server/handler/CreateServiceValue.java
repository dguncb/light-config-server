
package net.lightapi.config.server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.NioUtils;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import java.nio.ByteBuffer;
import java.util.Map;

import io.undertow.server.HttpServerExchange;
import net.lightapi.config.server.common.ConfigValue;
import net.lightapi.config.server.jdbc.ConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceHandler(id="lightapi.net/config/create-service-value/0.1.0")
public class CreateServiceValue implements Handler {
    static final Logger logger = LoggerFactory.getLogger(CreateServiceValue.class);
    private ConfigRepository configRepository = (ConfigRepository) SingletonServiceFactory.getBean(ConfigRepository.class);

    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {

        ObjectMapper mapper = new ObjectMapper();

        String result;

        try {
            String json = mapper.writeValueAsString(input);
            Map<String, String> configValueMap = mapper.readValue(json, Map.class);
            String configServiceId = configValueMap.get("configServiceId");
            ConfigValue configValue = new ConfigValue(configValueMap.get("key"), configValueMap.get("value"));
            configValue = configRepository.createServiceValue(configValue,configServiceId);
            result = mapper.writeValueAsString(configValue);

        } catch (Exception e) {
            result = e.getMessage();
        }

        return NioUtils.toByteBuffer(result);
    }
}
