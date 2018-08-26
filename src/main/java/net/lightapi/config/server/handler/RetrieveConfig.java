
package net.lightapi.config.server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.utility.NioUtils;
import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import io.undertow.util.HttpString;
import java.util.Map;

import io.undertow.server.HttpServerExchange;
import net.lightapi.config.server.common.ConfigService;
import net.lightapi.config.server.common.template.TemplateConfigValue;
import net.lightapi.config.server.common.template.TemplatesFileLoader;
import net.lightapi.config.server.jdbc.ConfigRepository;
import net.lightapi.config.server.service.ConfigValueProcessorImpl;
import net.lightapi.config.server.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceHandler(id="lightapi.net/config/retrieve-config/0.1.0")
public class RetrieveConfig implements Handler {
    static final Logger logger = LoggerFactory.getLogger(RetrieveConfig.class);
    private ConfigRepository configRepository = (ConfigRepository) SingletonServiceFactory.getBean(ConfigRepository.class);
    private TemplatesFileLoader fileLoader = new TemplatesFileLoader();
    private ConfigValueProcessorImpl configValueProcessor = new  ConfigValueProcessorImpl(configRepository, fileLoader);

    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {

        ObjectMapper mapper = new ObjectMapper();
        String resultFile = "config.zip";

        try {
            String json = mapper.writeValueAsString(input);

            Map<String, String> configValueMap = mapper.readValue(json, Map.class);
            String serviceId = configValueMap.get("serviceId");
            String profile = configValueMap.get("profile");
            String version = configValueMap.get("version");
            ConfigService configService = configRepository.queryConfigService(serviceId, profile, version);
            TemplateConfigValue templateConfigValue = configValueProcessor.processConfigValues(serviceId, profile, version);

            configValueProcessor.getTemplateFromRepo(configService);
            resultFile =  configValueProcessor.processTemplate(templateConfigValue, configService);
          //  writeToOutputStream(resultFile, exchange.getOutputStream());
        } catch (Exception e) {
            logger.error( e.getMessage());
            return NioUtils.toByteBuffer(ResponseUtil.populateErrorResponse(getClass().getName(), e.getMessage()));
        }


        exchange.getResponseHeaders()
                .add(new HttpString("Content-Type"), "application/zip")
                .add(new HttpString("Content-Disposition"), "attachment");
        File file = new File(resultFile);

        return NioUtils.toByteBuffer(file);
    }

    private void writeToOutputStream(String filename, OutputStream oos) throws Exception {
        File f = new File(filename);
        byte[] buf = new byte[8192];
        InputStream is = new FileInputStream(f);
        int c = 0;
        while ((c = is.read(buf, 0, buf.length)) > 0) {
            oos.write(buf, 0, c);
            oos.flush();
        }
        oos.close();
        is.close();
    }
}
