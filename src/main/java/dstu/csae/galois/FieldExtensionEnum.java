package dstu.csae.galois;

import dstu.csae.galois.extended.GaloisFieldExtension;
import dstu.csae.polynomial.Polynomial;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum FieldExtensionEnum {

    GF4(GaloisFieldLogger.build(PrimeFields.GF2.field, new Polynomial(new int[]{1, 1, 1}), currentTime())),
    GF8(GaloisFieldLogger.build(PrimeFields.GF2.field, new Polynomial(new int[]{1, 0, 1, 1}), currentTime())),
    GF9(GaloisFieldLogger.build(PrimeFields.GF3.field, new Polynomial(new int[]{2, 1, 1}), currentTime())),
    GF16(GaloisFieldLogger.build(PrimeFields.GF2.field, new Polynomial(new int[]{1, 1, 0, 0, 1}), currentTime())),
    GF256(GaloisFieldLogger.build(PrimeFields.GF2.field, new Polynomial(new int[]{1, 0, 1, 1, 1, 0, 0, 0, 1}), currentTime()));

    private final GaloisFieldExtension field;

    FieldExtensionEnum(GaloisFieldExtension field) {
        this.field = field;
    }

    static long currentTime() {
        return System.currentTimeMillis();
    }

    public static Optional<FieldExtensionEnum> fromField(GaloisFieldExtension target) {
        return Arrays.stream(values())
                .filter(e -> e.field.equals(target))
                .findFirst();
    }

    private enum PrimeFields{
        GF2(new GaloisField(2)),
        GF3(new GaloisField(3));

        private final GaloisField field;

        PrimeFields(GaloisField field){
            this.field = field;
        }
    }

    @Override
    public String toString() {
        return field.toString();
    }
}
