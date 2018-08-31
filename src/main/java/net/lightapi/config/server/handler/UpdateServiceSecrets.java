
package net.lightapi.config.server.handler;

import com.networknt.config.Config;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.NioUtils;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import java.nio.ByteBuffer;
import io.undertow.server.HttpServerExchange;
import net.lightapi.config.server.common.ServiceConfigValues;
import net.lightapi.config.server.jdbc.ConfigRepository;
import net.lightapi.config.server.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceHandler(id="lightapi.net/config/update-service-secrets/0.1.0")
public class UpdateServiceSecrets implements Handler {
    static final Logger logger = LoggerFactory.getLogger(UpdateServiceSecrets.class);
    private ConfigRepository configRepository = (ConfigRepository) SingletonServiceFactory.getBean(ConfigRepository.class);

    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {
        String result;
        try {
            ServiceConfigValues configValues = Config.getInstance().getMapper().convertValue(input, ServiceConfigValues.class);
            int recs = configRepository.updateServiceSecrets(configValues.getValues(),configValues.getConfigServiceId());
            result = Config.getInstance().getMapper().writeValueAsString(recs);

        } catch (Exception e) {
            result = ResponseUtil.populateErrorResponse(getClass().getName(), e.getMessage());
        }

        return NioUtils.toByteBuffer(result);
    }
}
