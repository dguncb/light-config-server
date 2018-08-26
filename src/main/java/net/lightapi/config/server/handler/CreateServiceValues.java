
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
import net.lightapi.config.server.common.ServiceConfigValues;
import net.lightapi.config.server.jdbc.ConfigRepository;
import net.lightapi.config.server.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceHandler(id="lightapi.net/config/create-service-values/0.1.0")
public class CreateServiceValues  implements Handler {
    static final Logger logger = LoggerFactory.getLogger(CreateServiceValues.class);
    private ConfigRepository configRepository = (ConfigRepository) SingletonServiceFactory.getBean(ConfigRepository.class);

    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {

        ObjectMapper mapper = new ObjectMapper();

        String result;

        try {
            String json = mapper.writeValueAsString(input);
            ServiceConfigValues configValues = mapper.readValue(json, ServiceConfigValues.class);

            int recs = configRepository.createServiceValues(configValues.getValues(),configValues.getConfigServiceId());
            result = mapper.writeValueAsString(recs);

        } catch (Exception e) {
            result = ResponseUtil.populateErrorResponse(getClass().getName(), e.getMessage());
        }

        return NioUtils.toByteBuffer(result);
    }
}
