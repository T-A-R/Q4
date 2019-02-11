package pro.quizer.quizerexit.utils;


import android.util.Base64;

import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Sequence;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Enumeration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import pro.quizer.quizerexit.model.config.ParseServerModel;

public class CryptoController {

    public static String privateKey =
            "<RSAKeyValue>"
                    + "<Modulus>0xp1ux1gDERsUyGUpl+UZB/MK2TgZCDENQaM2cDsqiluTnW9jtTScLTrgyBhsCNVjDB7ZnJnWpMMdfFeJkxWSFEjFoKlJAqzs9VmHheLql+hUZavxY3q2x9whXc/IpXIvGXlWYzaLAuHEwbpSY8C0b93elkw1zN4GI3h19Yj+1WVgfPvpmweADocllZEIi26oBHNlcDlBGM/PE+YgownWbBCtr8kzaiZz9TUHjnbNEr8BkK/dKkv9BICBTF98A6c7gf/kiI0mqLAm5l3Eq8PL26kmjju5Bsa5ja4WywTT7CgFHBzlU/OzbHsiQYSKPVrFw7YyXfyZHy4qvtDXA7afQ==</Modulus>"
                    + "<Exponent>AQAB</Exponent>"
                    + "<P>7Dg2zQNrf4n+gsXt9awhom6ye6mL2Sd6CRomf72MKxk23W1XR2bEWUnHe24esT/jFadL1qHzkdccQ7tiOD2wk+GtwA4KX2InRmMIMAqO21Xqk6uPP3FzK/Yxzp1hf1RzaZCLZwfxYm2+SZlQULTEuRy+K3LMZT1T1v8Hgdaa7GM=</P>"
                    + "<Q>5MfVvdIZd7f4oNLPHWVymz1MTJxxoBtczCRyKbmLnKGYhXj8SpljIueyxhYqDND5CfIPbbrDfATqkvRm+GpBiJ/qQsHFmaZ4FMYP/VUQxt8clliGjHy/YyzflgSoPz8HSvejzH252YKA2obv6ao3oWgGIN7JEA8d4wYyAHZRo58=</Q>"
                    + "<DP>XYIXN2rOKAuhQ3VEsIa2Pc2iO8+u+4RkC6xdZx3Kmdj+XgMFlM86lLnfDJ5X6gSHSTATlmA/whCsMKfBxs+s0a15kVg9hOqe738OY64aRPsqw9AbAZtQYyuvJcIpMTVmBnjgpiV/yV32AL5/C6OzaC/Xm+1yufTf9nC4MQ6Z/08=</DP>"
                    + "<DQ>YCb81kKvIXnYrP7svVYdzND+eFRoDPpzDfljYdXxr2rbR32Jy3CcEdd/c3FtSuWpRVzOzzdnYYBPdmPxwwYldN6A/n2iJumoHlL/9WEDJBIxWZ7rLbBgX7ZEYjCi5bH/xqo91Xwh2CDZNv7vZi18JAIsUTRRHMa9sOSxrzALL9E=</DQ>"
                    + "<InverseQ>oyoeGvhz+TVA+jCr6URzONjKDB8RK1Vz2eyblP6gQVTxvkNE3WMAIj5mGwtfHJpyDcL+r7nVM1fYcsyJpnTGxVMMj+S04C1jO2cxVBZT1qZ2iVDcpqLMd3mwc1R8/P1F43t1oRjAw7ERVkxR/LdvKkzYmnvkmqVpuhg5IiXfBYk=</InverseQ>"
                    + "<D>hYkHUAWU7C2cGDn1vghX5b33eLum9a+EbcZm8peHHVx32knATslxFLpc/+VL5g9z3eoNJRDZMAI0r6au16sSKUyp1WNu8w2R/v/OSNq8DlnPwbyAE4diOJn6o3J7DXWSNRp/qdXfbF0eZHrKty0vq15iRZKFwptcLKwTYGSk/iZO951XuI1/hHr45fIxhz6QPBSMF5iWYShhI4zESYqjseytpzlk83npMnI4qghLVk6aQIls5AjWaD8oei4wNJ1S30U3rfQ2mnZrhbMi25G2be9nK/Gt+7/OKPNDsqh00VmKVn4v97Uy8cHZ4+zCQ5C5WtCtamhqmPrbeh7F8LzQQQ==</D>"
                    + "</RSAKeyValue>";

    private static int lengthKey = 2048;

    private static boolean _optimalAsymmetricEncryptionPadding = false;

    public static ParseServerModel parseServer(final String pServer) throws Exception {
        final String cleanString = clearStringFromGarbage(pServer);
        final String urlString = RSADecrypt(cleanString.getBytes());
//        final String urlString = decrypt(cleanString);
//        final String urlString = RSAUtil.decrypt(cleanString, privateKey);

        String[] arrayParams = urlString.split("|");

        final String serverUrl = arrayParams[1];
        final String loginAdmin = arrayParams[0];

        return new ParseServerModel(serverUrl, loginAdmin);
    }

    private static byte[] decodeBase64(String dataToDecode) {
        byte[] dataDecoded = Base64.decode(dataToDecode, Base64.DEFAULT);
        return dataDecoded;
    }

//    public static String decrypt(String s) throws Exception {
//
//        String modulus = "0xp1ux1gDERsUyGUpl+UZB/MK2TgZCDENQaM2cDsqiluTnW9jtTScLTrgyBhsCNVjDB7ZnJnWpMMdfFeJkxWSFEjFoKlJAqzs9VmHheLql+hUZavxY3q2x9whXc/IpXIvGXlWYzaLAuHEwbpSY8C0b93elkw1zN4GI3h19Yj+1WVgfPvpmweADocllZEIi26oBHNlcDlBGM/PE+YgownWbBCtr8kzaiZz9TUHjnbNEr8BkK/dKkv9BICBTF98A6c7gf/kiI0mqLAm5l3Eq8PL26kmjju5Bsa5ja4WywTT7CgFHBzlU/OzbHsiQYSKPVrFw7YyXfyZHy4qvtDXA7afQ==";
//        String dD = "hYkHUAWU7C2cGDn1vghX5b33eLum9a+EbcZm8peHHVx32knATslxFLpc/+VL5g9z3eoNJRDZMAI0r6au16sSKUyp1WNu8w2R/v/OSNq8DlnPwbyAE4diOJn6o3J7DXWSNRp/qdXfbF0eZHrKty0vq15iRZKFwptcLKwTYGSk/iZO951XuI1/hHr45fIxhz6QPBSMF5iWYShhI4zESYqjseytpzlk83npMnI4qghLVk6aQIls5AjWaD8oei4wNJ1S30U3rfQ2mnZrhbMi25G2be9nK/Gt+7/OKPNDsqh00VmKVn4v97Uy8cHZ4+zCQ5C5WtCtamhqmPrbeh7F8LzQQQ==";
//
//        byte[] modBytes = decodeBase64(modulus.trim());
//        byte[] dBytes = decodeBase64(dD.trim());
//
//        BigInteger modules = new BigInteger(1, modBytes);
//        BigInteger d = new BigInteger(1, dBytes);
//
//        KeyFactory factory = KeyFactory.getInstance("RSA");
//        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//
//        RSAPrivateKeySpec privSpec = new RSAPrivateKeySpec(modules, d);
//        PrivateKey privKey = factory.generatePrivate(privSpec);
//        cipher.initTextSize(Cipher.DECRYPT_MODE, privKey);
//        byte[] decrypted = cipher.doFinal(decodeBase64(s));
//
//        return new String(decrypted);
//    }

    public static PrivateKey getPrivateKey() {
        byte[] encodedPrivateKey = Base64.decode(privateKey, Base64.DEFAULT);

        try {
            ASN1Sequence primitive = (ASN1Sequence) ASN1Sequence
                    .fromByteArray(encodedPrivateKey);
            Enumeration<?> e = primitive.getObjects();
            BigInteger v = ((ASN1Integer) e.nextElement()).getValue();

            int version = v.intValue();
            if (version != 0 && version != 1) {
                throw new IllegalArgumentException("wrong version for RSA private key");
            }
            /**
             * In fact only modulus and private exponent are in use.
             */
            BigInteger modulus = ((ASN1Integer) e.nextElement()).getValue();
            BigInteger publicExponent = ((ASN1Integer) e.nextElement()).getValue();
            BigInteger privateExponent = ((ASN1Integer) e.nextElement()).getValue();

            RSAPrivateKeySpec spec = new RSAPrivateKeySpec(modulus, privateExponent);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        } catch (IOException e2) {
            throw new IllegalStateException();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeySpecException e) {
            throw new IllegalStateException(e);
        }

    }

    public static String RSADecrypt(final byte[] encryptedBytes) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher1 = Cipher.getInstance("RSA");
        cipher1.init(Cipher.DECRYPT_MODE, getPrivateKey());
        final byte[] decryptedBytes = cipher1.doFinal(encryptedBytes);
        final String decrypted = new String(decryptedBytes);
        System.out.println("DDecrypted?????" + decrypted);
        return decrypted;
    }

    private static String clearStringFromGarbage(String dirtyStr) {
        String cleanStr = "";
        String[] arrayParts = new String[9];
        //$gen6.$arr[7].$gen28.$arr[4].$gen2.$arr[6].$gen18.$arr[2].$gen16.$arr[8].$gen12.$arr[0]
        //.$gen14.$arr[5].$gen21.$arr[3].$gen31.$arr[1].$gen17;
        //$arr[0] = substr($stroka, 0, 19);//элемент массива содержит 19 символов
        //$arr[1] = substr($stroka, 19, 52);//элемент массива содержит 52 символа
        //$arr[2] = substr($stroka, 71, 30);//элемент массива содержит 30 символа
        //$arr[3] = substr($stroka, 101, 45);//элемент массива содержит 45 символов
        //$arr[4] = substr($stroka, 146, 25);//элемент массива содержит 25 символов
        //$arr[5] = substr($stroka, 171, 49);//элемент массива содержит 49 символов
        //$arr[6] = substr($stroka, 220, 58);//элемент массива содержит 58 символов
        //$arr[7] = substr($stroka, 278, 47);//элемент массива содержит 47 символов
        //$arr[8] = substr($stroka, 325, 17);//элемент массива содержит 17 символов
        //итого 342 символ (как в исходной строке после удаления "==")

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

        for (String str : arrayParts) {
            cleanStr += str;
        }

        return cleanStr + "==";
    }

//    private static String decryptText(String text) throws Exception {
//        byte[] decrypted = Decrypt(text.getBytes(), lengthKey, privateKey);
//        return new String(decrypted);
//    }

//    private static byte[] Decrypt(byte[] data, int keySize, String publicAndPrivateKeyXml) throws Exception {
//        if (data == null || data.length == 0)
//            throw new Exception("Data are empty");
//        if (!IsKeySizeValid(keySize))
//            throw new Exception("Key size is not valid");
//        if (publicAndPrivateKeyXml == null || publicAndPrivateKeyXml.isEmpty())
//            throw new Exception("Key is null or empty");
//
//        using(var provider = new RSACRSACryptoServiceProvider(keySize))
//        {
//            provider.FromXmlString(publicAndPrivateKeyXml);
//            return provider.Decrypt(data, _optimalAsymmetricEncryptionPadding);
//        }
//    }
//
//    private static boolean IsKeySizeValid(int keySize) {
//        return keySize >= 384 &&
//                keySize <= 16384 &&
//                keySize % 8 == 0;
//    }


//    public static String Decrypt(String dataToDecrypt, int keySize, String publicAndPrivateKeyXml) throws Exception {
//        if (dataToDecrypt == null || dataToDecrypt.length() == 0)
//            throw new Exception("Data are empty");
//        if (!IsKeySizeValid(keySize))
//            throw new Exception("Key size is not valid");
//        if (publicAndPrivateKeyXml == null || publicAndPrivateKeyXml.isEmpty())
//            throw new Exception("Key is null or empty");
//
//        //// The bytearray to hold all of our data after decryption
//        byte[] decryptedBytes;
//
//        //Create a new instance of RSACryptoServiceProvider.
//        using(var RSA = new RSACryptoServiceProvider(keySize))
//        {
//            try {
//                byte[] bytesToDecrypt = Convert.FromBase64String(dataToDecrypt);
//
//                //// Import the private key info
//                RSA.FromXmlString(publicAndPrivateKeyXml);
//
//                //// No need to subtract padding size when decrypting (OR do I?)
//                int blockSize = RSA.KeySize / 8;
//
//                //// buffer to write byte sequence of the given block_size
//                byte[] buffer = new byte[blockSize];
//
//                //// buffer containing decrypted information
//                byte[] decryptedBuffer = new byte[blockSize];
//
//                //// Initializes our array to make sure it can hold at least the amount needed to decrypt.
//                decryptedBytes = new byte[dataToDecrypt.length()];
//
//                for (int i = 0; i < bytesToDecrypt.length; i += blockSize) {
//                    if (2 * i > bytesToDecrypt.length && ((bytesToDecrypt.length - i) % blockSize != 0)) {
//                        buffer = new byte[bytesToDecrypt.length - i];
//                        blockSize = bytesToDecrypt.length - i;
//                    }
//
//                    //// If the amount of bytes we need to decrypt isn't enough to fill out a block, only decrypt part of it
//                    if (bytesToDecrypt.length < blockSize) {
//                        buffer = new byte[bytesToDecrypt.length];
//                        blockSize = bytesToDecrypt.length;
//                    }
//
//                    Buffer.BlockCopy(bytesToDecrypt, i, buffer, 0, blockSize);
//                    decryptedBuffer = RSA.Decrypt(buffer, false);
//                    decryptedBuffer.CopyTo(decryptedBytes, i);
//                }
//            } finally {
//                //// Clear the RSA key container, deleting generated keys.
//                RSA.PersistKeyInCsp = false;
//            }
//        }
//
//
//        //// We encode each byte with UTF8 and then write to a String while trimming off the extra empty data created by the overhead.
//        var encoder = new UTF8Encoding();
//        return encoder.GetString(decryptedBytes, 0, decryptedBytes.length).TrimEnd(new[]{
//            '\0'
//        });
//    }
}