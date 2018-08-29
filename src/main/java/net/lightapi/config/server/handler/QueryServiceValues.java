
package net.lightapi.config.server.handler;

import com.networknt.config.Config;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.NioUtils;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.undertow.server.HttpServerExchange;
import net.lightapi.config.server.common.ConfigValue;
import net.lightapi.config.server.common.crypto.AESConfigSecurity;
import net.lightapi.config.server.jdbc.ConfigRepository;
import net.lightapi.config.server.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.networknt.utility.Decryptor.CRYPT_PREFIX;

@ServiceHandler(id="lightapi.net/config/query-service-values/0.1.0")
public class QueryServiceValues implements Handler {
    static final Logger logger = LoggerFactory.getLogger(QueryServiceValues.class);
    private ConfigRepository configRepository = (ConfigRepository) SingletonServiceFactory.getBean(ConfigRepository.class);
    private AESConfigSecurity aESConfigSecurity;
    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {

        String result;

        try {
            Map<String, String> configValueMap = Config.getInstance().getMapper().convertValue(input, Map.class);
            String configServiceId = configValueMap.get("configServiceId");
            List<ConfigValue> configValues = configRepository.queryServiceValues( configServiceId);
            if ( InitializeServer.key!=null) {
                 aESConfigSecurity= new AESConfigSecurity(InitializeServer.key);
            }
            result = configValues==null?"no records return": Config.getInstance().getMapper().writeValueAsString(getClearTextConfigValues(configValues));

        } catch (Exception e) {
            result = ResponseUtil.populateErrorResponse(getClass().getName(), e.getMessage());
        }

        return NioUtils.toByteBuffer(result);
    }

    protected  List<ConfigValue> getClearTextConfigValues (List<ConfigValue> configValues) {

        if (aESConfigSecurity !=null) {
            configValues = configValues.stream().map(c->decrptValue(c)).collect(Collectors.toList());
        }
        return configValues;
    }

    protected  ConfigValue decrptValue (ConfigValue sourceConfig) {
        if (sourceConfig.getValue().startsWith(CRYPT_PREFIX) ) {
            sourceConfig.setValue(aESConfigSecurity.decrypt(sourceConfig.getValue()));

        }
        return sourceConfig;
    }

}
