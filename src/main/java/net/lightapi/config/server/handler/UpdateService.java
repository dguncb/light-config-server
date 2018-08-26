
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
import net.lightapi.config.server.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceHandler(id="lightapi.net/config/update-service/0.1.0")
public class UpdateService implements Handler {
    static final Logger logger = LoggerFactory.getLogger(UpdateService.class);
    private ConfigRepository configRepository = (ConfigRepository) SingletonServiceFactory.getBean(ConfigRepository.class);
    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {
        ObjectMapper mapper = new ObjectMapper();

        String result;

        try {
            String json = mapper.writeValueAsString(input);
            ConfigService configService = mapper.readValue(json, ConfigService.class);
            configService = configRepository.updateConfigService(configService);
            result = configService ==null?"empty":mapper.writeValueAsString(configService);
        } catch (Exception e) {
            result = ResponseUtil.populateErrorResponse(getClass().getName(), e.getMessage());
        }

        return NioUtils.toByteBuffer(result);
    }
}
