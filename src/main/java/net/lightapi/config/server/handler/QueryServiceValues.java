
package net.lightapi.config.server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.NioUtils;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import io.undertow.server.HttpServerExchange;
import net.lightapi.config.server.common.ConfigValue;
import net.lightapi.config.server.jdbc.ConfigRepository;
import net.lightapi.config.server.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceHandler(id="lightapi.net/config/query-service-values/0.1.0")
public class QueryServiceValues implements Handler {
    static final Logger logger = LoggerFactory.getLogger(QueryServiceValues.class);
    private ConfigRepository configRepository = (ConfigRepository) SingletonServiceFactory.getBean(ConfigRepository.class);
    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {
        ObjectMapper mapper = new ObjectMapper();

        String result;

        try {
            String json = mapper.writeValueAsString(input);
            Map<String, String> configValueMap = mapper.readValue(json, Map.class);
            String configServiceId = configValueMap.get("configServiceId");
            List<ConfigValue> configValues = configRepository.queryServiceValues( configServiceId);
            result = configValues==null?"no records return":mapper.writeValueAsString(configValues);

        } catch (Exception e) {
            result = ResponseUtil.populateErrorResponse(getClass().getName(), e.getMessage());
        }

        return NioUtils.toByteBuffer(result);
    }
}
