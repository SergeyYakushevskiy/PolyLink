package dstu.csae.codec;

import dstu.csae.galois.extended.GaloisFieldExtension;
import dstu.csae.polynomial.FieldPolynomial;
import dstu.csae.rs_code.RSCode;
import lombok.Getter;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

public class TextConverter {

    private static final Logger logger = LogManager.getLogger(TextConverter.class);

    private static volatile TextConverter instance;
    @Getter
    private volatile Codec<RSCode, FieldPolynomial, FieldPolynomial> coder;

    private TextConverter(){
        logger.info("Конвертер инициализирован");
    }

    public void setCoder(@NonNull Codec<RSCode, FieldPolynomial, FieldPolynomial> coder){
        if (this.coder == null || !this.coder.equals(coder)){
            this.coder = coder;
            logger.info("Задан новый кодек: {}", coder);
        }
    }

    public byte[] getBase64Bytes(String base64Text){
        return Base64.getDecoder().decode(base64Text);
    }

    public byte[] getBytes(String text){
        return text.getBytes(StandardCharsets.UTF_8);
    }

    public String getString(byte[] bytes){
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public String getBase64String(byte[] base64Bytes){
        return Base64.getEncoder().encodeToString(base64Bytes);
    }

    public ArrayList<FieldPolynomial> bytesToPolynomials(byte[] bytes, int polynomialSize)
            throws  IllegalStateException{
        if(coder == null) {
            throw new IllegalStateException("Код Рида-Соломона не задан");
        }
        ArrayList<FieldPolynomial> polynomials = new ArrayList<>();
        ArrayList<Integer> elements = new ArrayList<>();

        BigInteger big = new BigInteger(1, bytes);

        GaloisFieldExtension field = coder.code.getField();
        BigInteger fieldSize = BigInteger.valueOf(field.getCharacteristic());

        while (big.compareTo(BigInteger.ONE) >= 0){
            BigInteger[] divRem = big.divideAndRemainder(fieldSize);
            elements.add(divRem[1].intValue());
            big = divRem[0];
        }

        for(int i = 0; i < elements.size(); i += polynomialSize){
            int[] coefficients = elements.subList(i, Math.min(elements.size(), i + polynomialSize))
                    .stream()
                    .mapToInt(Integer::intValue)
                    .toArray();
            FieldPolynomial polynomial = new FieldPolynomial(field, coefficients);
            polynomials.add(polynomial);
        }

        return polynomials;
    }

    public byte[] polynomialsToBytes(ArrayList<FieldPolynomial> polynomials, int polynomialSize)
        throws  IllegalStateException{
        if(coder == null) {
            throw new IllegalStateException("Код Рида-Соломона не задан");
        }
        GaloisFieldExtension field = coder.getCode().getField();
        BigInteger fieldSize = BigInteger.valueOf(field.getCharacteristic());

        BigInteger big = BigInteger.ZERO;
        List<Integer> elements = new ArrayList<>(polynomials.stream()
                .peek(x -> x.removeLastZero())
                .map(FieldPolynomial::getCoefficients)
                .map(x -> {
                    int[] tmp = new int[polynomialSize];
                    System.arraycopy(x, 0, tmp, 0, x.length);
                    return tmp;
                })
                .flatMapToInt(Arrays::stream)
                .boxed()
                .toList());
        Collections.reverse(elements);
        for (Integer element : elements) {
            big = big.multiply(fieldSize).add(BigInteger.valueOf(element));
        }

        byte[] bytes = big.toByteArray();
        if (bytes.length > 1 && bytes[0] == 0) {
            byte[] tmp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, tmp, 0, tmp.length);
            bytes = tmp;
        }
        return bytes;
    }

    public ArrayList<FieldPolynomial> encode(ArrayList<FieldPolynomial> polynomials){
        return convert(polynomials, coder::encode);
    }

    public ArrayList<FieldPolynomial> decode(ArrayList<FieldPolynomial> polynomials){
        return convert(polynomials, coder::decode);
    }

    private static ArrayList<FieldPolynomial> convert(ArrayList<FieldPolynomial> polynomials,
                                                      Function<FieldPolynomial, FieldPolynomial> function){
        return new ArrayList<>(polynomials.stream()
                .map(function)
                .toList());
    }

    public static TextConverter getInstance(){
        if(instance == null){
            synchronized (TextConverter.class){
                if(instance == null){
                    instance = new TextConverter();
                }
            }
        }
        return instance;
    }

}
