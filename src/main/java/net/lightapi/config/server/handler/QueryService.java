
package net.lightapi.config.server.handler;

import com.networknt.config.Config;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.NioUtils;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import java.nio.ByteBuffer;
import java.util.Map;

import io.undertow.server.HttpServerExchange;
import net.lightapi.config.server.common.ConfigService;
import net.lightapi.config.server.jdbc.ConfigRepository;
import net.lightapi.config.server.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceHandler(id="lightapi.net/config/query-service/0.1.0")
public class QueryService implements Handler {
    static final Logger logger = LoggerFactory.getLogger(DeleteService.class);
    private ConfigRepository configRepository = (ConfigRepository) SingletonServiceFactory.getBean(ConfigRepository.class);
    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {

        String result;

        try {
            Map<String, String> configValueMap = Config.getInstance().getMapper().convertValue(input, Map.class);
            String serviceId = configValueMap.get("serviceId");
            String profile = configValueMap.get("profile");
            String version = configValueMap.get("version");
            ConfigService configService = configRepository.queryConfigService(serviceId, profile, version);
            result = configService==null?"no record return":Config.getInstance().getMapper().writeValueAsString(configService);
        } catch (Exception e) {
            result = ResponseUtil.populateErrorResponse(getClass().getName(), e.getMessage());

        }

        return NioUtils.toByteBuffer(result);
    }
}
