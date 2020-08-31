package com.example.password_manager;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {

    /* Take the SHA-256 hash of an arbitrary string, return the hex string representation*/
    public static String sha256hash(String plaintext) {
        String password_sha256;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] sha256_hash = digest.digest(plaintext.getBytes(StandardCharsets.UTF_8));
            password_sha256 = String.format("%064x", new BigInteger(1, sha256_hash));
        } catch (java.security.NoSuchAlgorithmException ex) {
            password_sha256 = plaintext;
        }
        return password_sha256;
    }

    public static byte[] aesEncrypt(byte[] plaintext, byte[] key) {
        return aesOperation(plaintext, key, Cipher.ENCRYPT_MODE);
    }

    public static byte[] aesDecrypt(byte[] plaintext, byte[] key) {
        return aesOperation(plaintext, key, Cipher.DECRYPT_MODE);
    }

    public static byte[] aesOperation(byte[] message, byte[] key, int mode) {
        byte[] result = message;
        try {
            byte[] keyBytes = new byte[32];
            System.arraycopy(key, 0, keyBytes, 0, keyBytes.length);
            SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(mode, secretKey);
            result = cipher.doFinal(message);
        } catch (java.security.NoSuchAlgorithmException ex) {
            System.out.println("No such algorithm exception");
        } catch (javax.crypto.NoSuchPaddingException ex) {
            System.out.println("No such padding exception");
        } catch (java.security.InvalidKeyException ex) {
            System.out.println("Invalid key exception");
        } catch (javax.crypto.BadPaddingException ex) {
            System.out.println("Bad padding exception");
        } catch (javax.crypto.IllegalBlockSizeException ex) {
            System.out.println("Illegal block size exception");
        }
        return result;
    }

    public static String encryptAesB64String(String plaintext, byte[] key) {
        byte[] encryptedBytes = aesEncrypt(plaintext.getBytes(), key);
        final String encodedString = base64encode(encryptedBytes);
        return encodedString;
    }

    public static String decryptAesB64String(String base64string, byte[] key) {
        byte[] ciphertextBytes = base64decode(base64string);
        byte[] plaintextBytes = aesDecrypt(ciphertextBytes, key);
        String plaintext = "";
        try {
            plaintext = new String(plaintextBytes, "UTF-8");
        } catch (java.io.UnsupportedEncodingException ex) {
            // UTF-8 is supported }
        }
        return plaintext;
    }

    public static String base64encode(byte[] unencoded) {
        return Base64.getEncoder().encodeToString(unencoded);
    }

    public static byte[] base64decode(String encoded) {
        return Base64.getDecoder().decode(encoded);
    }
}
