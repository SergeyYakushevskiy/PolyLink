package dstu.csae.config;

import dstu.csae.galois.GaloisField;
import dstu.csae.polynomial.Polynomial;

import java.util.Arrays;

public class SettingsConfig {

    private static final String KEY = "settings.";

    public static GaloisField getGaloisField(){
        return new GaloisField(Integer.parseInt(Config.getProperty(KEY + "galois.field")));
    }

    public static Polynomial getGaloisPolynomial(){
        return new Polynomial(
                Arrays.stream(Config.getProperty(KEY + "galois.polynomial").split(" "))
                .mapToInt(Integer::parseInt)
                .toArray()
        );
    }

    public static int getD(){
        return Integer.parseInt(Config.getProperty(KEY + "code.d"));
    }

    public static WorkModeEnum getWorkMode(){
        return WorkModeEnum.valueOf(Config.getProperty(KEY + "work.mode"));
    }
}
