
package net.lightapi.config.server.handler;

import com.networknt.config.Config;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.NioUtils;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import java.nio.ByteBuffer;
import java.util.Map;

import io.undertow.server.HttpServerExchange;
import net.lightapi.config.server.common.ConfigValue;
import net.lightapi.config.server.common.crypto.AESConfigSecurity;
import net.lightapi.config.server.jdbc.ConfigRepository;
import net.lightapi.config.server.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceHandler(id="lightapi.net/config/update-service-secret/0.1.0")
public class UpdateServiceSecret implements Handler {
    static final Logger logger = LoggerFactory.getLogger(UpdateServiceSecret.class);
    private ConfigRepository configRepository = (ConfigRepository) SingletonServiceFactory.getBean(ConfigRepository.class);
    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {
        String result;

        try {
            Map<String, String> configValueMap = Config.getInstance().getMapper().convertValue(input, Map.class);
            String serviceId = configValueMap.get("configServiceId");
            ConfigValue configValue;
            if (InitializeServer.key==null) {
                logger.error("config server doesn't initialized the encrpt key yet....");
                configValue = new ConfigValue(configValueMap.get("key"), configValueMap.get("value"));
            } else {
                //TODO if the the service EncryptionAlgorithm is EncryptionAlgorithm.AES
                AESConfigSecurity AESConfigSecurity = new AESConfigSecurity(InitializeServer.key);
                configValue = new ConfigValue(configValueMap.get("key"), AESConfigSecurity.ecrypt(configValueMap.get("value")));
            }

            configValue = configRepository.updateServiceValue(configValue,serviceId);
            result =Config.getInstance().getMapper().writeValueAsString(configValue);
        } catch (Exception e) {
            result = ResponseUtil.populateErrorResponse(getClass().getName(), e.getMessage());
        }

        return NioUtils.toByteBuffer(result);
    }
}
