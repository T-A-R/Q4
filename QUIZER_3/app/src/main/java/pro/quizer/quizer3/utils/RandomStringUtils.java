package pro.quizer.quizer3.utils;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

public class RandomStringUtils {

    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = symbols[random.nextInt(symbols.length)];
        }

        return new String(buf);
    }

    public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String lower = upper.toLowerCase(Locale.ROOT);

    public static final String digits = "0123456789";

    public static final String alphanum = upper + lower + digits;

    private final Random random;

    private final char[] symbols;

    private final char[] buf;

    public RandomStringUtils(int length, Random random, String symbols) {
        if (length < 1) throw new IllegalArgumentException();
        if (symbols.length() < 2) throw new IllegalArgumentException();
        this.random = random;
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    public RandomStringUtils(int length, Random random) {
        this(length, random, alphanum);
    }

    public RandomStringUtils(int length) {
        this(length, new SecureRandom());
    }
}