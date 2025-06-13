package dstu.csae.config;

import dstu.csae.galois.GaloisField;
import dstu.csae.galois.extended.GaloisFieldExtension;
import dstu.csae.rs_code.RSCode;
import dstu.csae.rs_code.RSFCoder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Settings {

    private static final Logger logger = LogManager.getLogger(Settings.class);

    private static volatile Settings instance;
    @Getter @Setter
    private @NonNull RSFCoder coder;
    @Getter @Setter
    private @NonNull WorkModeEnum mode;

    private Settings(){
        logger.info("Настройки инициализированы");
        GaloisField field = SettingsConfig.getGaloisField();
        GaloisFieldExtension fieldExtension = new GaloisFieldExtension(field, SettingsConfig.getGaloisPolynomial());
        RSCode code = new RSCode(fieldExtension, SettingsConfig.getD());
        this.coder = new RSFCoder(code);
        this.mode = SettingsConfig.getWorkMode();
        logger.info("Заданы настройки по умолчанию");
    }

    public static Settings getInstance(){
        if(instance == null){
            synchronized (Settings.class){
                if(instance == null){
                    instance = new Settings();
                }
            }
        }
        return instance;
    }


}
