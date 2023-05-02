package utils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import javax.crypto.Cipher;

public class RSASender {

    public static byte[] cipher(String message, Key key) throws Exception {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        byte[] cipheredMessageBytes = encryptCipher.doFinal(messageBytes);
        return cipheredMessageBytes;
    }

}