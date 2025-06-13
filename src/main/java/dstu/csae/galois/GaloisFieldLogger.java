package dstu.csae.galois;

import dstu.csae.galois.extended.GaloisFieldExtension;
import dstu.csae.polynomial.Polynomial;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GaloisFieldLogger{
    private static final Logger logger = LogManager.getLogger(GaloisFieldLogger.class);

    public static GaloisFieldExtension build(GaloisField galoisField, Polynomial polynomial, long timePoint) throws IllegalArgumentException {
        GaloisFieldExtension field = new GaloisFieldExtension(galoisField, polynomial);
        logger.info("Поле {} создано за {} мс", field, System.currentTimeMillis() - timePoint);
        return field;
    }

}
