package com.divofmod.quizer;

import android.content.Context;
import android.util.Base64;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.spec.RSAPrivateKeySpec;

import javax.crypto.Cipher;

public abstract class RSA {

    private static String ClearStringFromGarbage(String dirtyStr) {
        String cleanStr = "";
        String[] arrayParts = new String[9];
        dirtyStr = dirtyStr.substring(6);
        arrayParts[7] = dirtyStr.substring(0, 47);
        dirtyStr = dirtyStr.substring(28 + 47);
        arrayParts[4] = dirtyStr.substring(0, 25);
        dirtyStr = dirtyStr.substring(2 + 25);
        arrayParts[6] = dirtyStr.substring(0, 58);
        dirtyStr = dirtyStr.substring(18 + 58);
        arrayParts[2] = dirtyStr.substring(0, 30);
        dirtyStr = dirtyStr.substring(16 + 30);
        arrayParts[8] = dirtyStr.substring(0, 17);
        dirtyStr = dirtyStr.substring(12 + 17);
        arrayParts[0] = dirtyStr.substring(0, 19);
        dirtyStr = dirtyStr.substring(14 + 19);
        arrayParts[5] = dirtyStr.substring(0, 49);
        dirtyStr = dirtyStr.substring(21 + 49);
        arrayParts[3] = dirtyStr.substring(0, 45);
        dirtyStr = dirtyStr.substring(31 + 45);
        arrayParts[1] = dirtyStr.substring(0, 52);

        for (String str : arrayParts)
            cleanStr += str;

        return cleanStr + "==";
    }

    private static byte[] decodeBase64(String dataToDecode) {
        return Base64.decode(dataToDecode, Base64.DEFAULT);
    }

    public static String decrypt(String s, Context context) throws Exception {

        String temp = ClearStringFromGarbage(s);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        cipher.init(Cipher.DECRYPT_MODE,
                KeyFactory.getInstance("RSA").
                        generatePrivate(new RSAPrivateKeySpec(
                                new BigInteger(1, decodeBase64((context.getString(R.string.modulus)).trim())),
                                new BigInteger(1, decodeBase64(context.getString(R.string.DD))))));

        return new String(cipher.doFinal(decodeBase64(temp)));
    }
}


