
package net.lightapi.config.server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.NioUtils;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import java.nio.ByteBuffer;
import io.undertow.server.HttpServerExchange;
import net.lightapi.config.server.common.ConfigService;
import net.lightapi.config.server.jdbc.ConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceHandler(id="lightapi.net/config/create-service/0.1.0")
public class CreateService implements Handler {
    static final Logger logger = LoggerFactory.getLogger(CreateService.class);
    private ConfigRepository configRepository = (ConfigRepository) SingletonServiceFactory.getBean(ConfigRepository.class);
    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {


        ObjectMapper mapper = new ObjectMapper();

        String result;

        try {
            String json = mapper.writeValueAsString(input);
            ConfigService configService = mapper.readValue(json, ConfigService.class);
            result = configRepository.createConfigService(configService);
        } catch (Exception e) {
            result = e.getMessage();
        }

        return NioUtils.toByteBuffer(result);
    }
}
