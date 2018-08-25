package net.lightapi.config.server.handler;

import com.networknt.rpc.Handler;
import com.networknt.rpc.router.ServiceHandler;
import com.networknt.utility.NioUtils;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * When a new light-config-server instance is deployed, the first step is to initialize
 * it before using it. None of the other services are allowed to be called if the server
 * instance is not initialized.
 *
 * On the UI, there would be two pages to collect two parts of the encryption key that is
 * used to encrypt the secret values in the database. Two operators should each supply half
 * of the secret to ensure that no single person can obtain the key to decrypt the database.
 *
 * In the case that the existing config server instance is damaged and a new instance must
 * be deployed, the two operators must supply the same key in order to connect to the same
 * database. Without the proper key, even the database is stolen, secrets are still kept.
 *
 * The service implementation accept only one key which is formed by two parts concat on
 * the UI javascript single page application.
 *
 * As the key is used to decrypt the database values, it won't be saved into the database
 * but inside the contain at /etc/light-config-server.conf
 *
 * @author Steve Hu
 *
 */
@ServiceHandler(id="lightapi.net/config/initialize-server/0.1.0")
public class InitializeServer implements Handler {
    static final Logger logger = LoggerFactory.getLogger(InitializeServer.class);
    static final String filename = System.getProperty("user.home") + File.separator + "light-config-server.conf";
    static final String CONFIG_SERVER_INITIALIZED = "ERR11400";
    static final String INVALID_INITIALIZE_KEY_FORMAT = "ERR11401";
    static final String ERROR_WRITING_KEY_FILE = "ERR11402";

    // This is a public static so that it can be accessed by other classes that need the key
    // for encryption. It is possible to be null so other classes need to check if it is null.
    public static String key = null;

    static {
        // a static block tries to load the key from filesystem. If the key exists,
        // then the initialize server action will return an error.
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            key = br.readLine();
        } catch (IOException e) {
            // file not found but this is no an error. It just means the server is not inited.
            logger.info("The light-config-server is not initialized yet.", e);
        }
    }

    @Override
    public ByteBuffer handle(HttpServerExchange exchange, Object input)  {
        if(logger.isDebugEnabled()) logger.debug("input = " + input);
        String result = "";
        // if key has been retrieved from the filesystem, then there is no need to set it.
        if(key != null && key.trim().length() > 0) {
            return NioUtils.toByteBuffer(getStatus(CONFIG_SERVER_INITIALIZED));
        }
        try {
            if(input instanceof Map) {
                key = (String)((Map)input).get("key");
                BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
                writer.write(key);
                writer.close();
            } else {
                // error wrong format
                return NioUtils.toByteBuffer(getStatus(INVALID_INITIALIZE_KEY_FORMAT));
            }
        } catch (Exception e) {
            logger.error("ERR11402", e);
            return NioUtils.toByteBuffer(getStatus(ERROR_WRITING_KEY_FILE, filename));
        }
        // TODO return empty body. Should we return something more meaningful?
        return NioUtils.toByteBuffer(result);
    }
}
