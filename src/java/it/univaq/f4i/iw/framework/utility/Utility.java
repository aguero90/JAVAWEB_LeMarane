package it.mam.REST.utility;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author alex
 */
public class Utility {

    public static String stringToMD5(String string) {
        String result = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(string.getBytes(Charset.forName("UTF-8")), 0, string.length());
            result = new BigInteger(1, messageDigest.digest()).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            //
        }
        return result;
    }

}
