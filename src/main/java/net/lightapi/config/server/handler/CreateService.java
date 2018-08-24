
package net.lightapi.config.server.handler;

import com.networknt.config.Config;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.status.Status;
import com.networknt.utility.NioUtils;
import io.undertow.server.HttpServerExchange;
import net.lightapi.config.server.common.ConfigService;
import net.lightapi.config.server.jdbc.ConfigRepository;
import net.lightapi.config.server.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

@ServiceHandler(id="lightapi.net/config/create-service/0.1.0")
public class CreateService implements Handler {
    static final Logger logger = LoggerFactory.getLogger(CreateService.class);
    private ConfigRepository configRepository = (ConfigRepository) SingletonServiceFactory.getBean(ConfigRepository.class);
    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {
        if(logger.isDebugEnabled()) logger.debug("input = " + input);
        String result;
        try {
            ConfigService configService = Config.getInstance().getMapper().convertValue(input, ConfigService.class);
            result = configRepository.createConfigService(configService);
        } catch (Exception e) {
            result = ResponseUtil.populateErrorResponse(getClass().getName(), e.getMessage());
        }
        return NioUtils.toByteBuffer(result);
    }
}
