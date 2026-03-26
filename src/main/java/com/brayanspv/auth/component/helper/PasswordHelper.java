package com.brayanspv.auth.component.helper;

import lombok.experimental.UtilityClass;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@UtilityClass
public class PasswordHelper {

    private static SecretKeySpec secretKeySpec;

    static void init(String aesKey) {
        secretKeySpec = new SecretKeySpec(aesKey.getBytes(), "AES");
    }

    public static String createPassword(String password) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedValue = cipher.doFinal(password.getBytes());
            return Base64.getEncoder().encodeToString(encryptedValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decryptPassword(String encryptedPassword) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decodedValue = Base64.getDecoder().decode(encryptedPassword);
            byte[] decryptedValue = cipher.doFinal(decodedValue);
            return new String(decryptedValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
