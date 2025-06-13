package dstu.csae.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

public class Config {
    private static final Logger logger = LogManager.getLogger(Config.class);

    private static final String PROPERTIES_PATH = "/dstu/csae/application.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
        try(InputStreamReader stream = new InputStreamReader(
                Objects.requireNonNull(MainWindowConfig.class.getResourceAsStream(PROPERTIES_PATH)),
                StandardCharsets.UTF_8)
        ){
            PROPERTIES.load(stream);
            logger.info("Файл настроек успешно считан");
        }catch (IOException ex){
            logger.error(ex);
        }
    }

    static String getProperty(String key){
        return PROPERTIES.getProperty(key);
    }

}
