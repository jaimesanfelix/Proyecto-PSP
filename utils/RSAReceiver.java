package utils;

import java.security.Key;
import javax.crypto.Cipher;

public class RSAReceiver {

    public static byte[] decipher(byte[] cipheredMessage, Key key) throws Exception {
        Cipher decipherer = Cipher.getInstance("RSA");
        decipherer.init(Cipher.DECRYPT_MODE, key);
        byte[] decipheredMessage = decipherer.doFinal(cipheredMessage);
        return decipheredMessage;
    }

}
