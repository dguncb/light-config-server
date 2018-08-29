
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
import static com.networknt.utility.Decryptor.CRYPT_PREFIX;

@ServiceHandler(id="lightapi.net/config/query-service-value/0.1.0")
public class QueryServiceValue implements Handler {
    static final Logger logger = LoggerFactory.getLogger(QueryServiceValue.class);
    private ConfigRepository configRepository = (ConfigRepository) SingletonServiceFactory.getBean(ConfigRepository.class);
    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {

        String result;

        try {
            Map<String, String> configValueMap = Config.getInstance().getMapper().convertValue(input, Map.class);
            String configServiceId = configValueMap.get("configServiceId");
            String key = configValueMap.get("key");
            ConfigValue configValue = configRepository.queryServiceValue(key, configServiceId);
            if (configValue.getValue()!=null && configValue.getValue().startsWith(CRYPT_PREFIX) && InitializeServer.key!=null) {
                AESConfigSecurity aESConfigSecurity= new AESConfigSecurity(InitializeServer.key);
                configValue.setValue(aESConfigSecurity.decrypt(configValue.getValue()));
            }
            result = configValue==null?"no records return":Config.getInstance().getMapper().writeValueAsString(configValue);

        } catch (Exception e) {
            result = ResponseUtil.populateErrorResponse(getClass().getName(), e.getMessage());
        }

        return NioUtils.toByteBuffer(result);
    }
}
