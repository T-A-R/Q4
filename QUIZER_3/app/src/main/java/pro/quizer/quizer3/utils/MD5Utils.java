package pro.quizer.quizer3.utils;

import org.apache.commons.codec.digest.DigestUtils;

public final class MD5Utils {

    public static String formatPassword(final String pLogin, final String pPassword) {
        return DigestUtils.md5Hex(DigestUtils.md5Hex(pPassword) + DigestUtils.md5Hex(pLogin.substring(1, 3)));
    }
}
