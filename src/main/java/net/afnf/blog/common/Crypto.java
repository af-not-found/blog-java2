package net.afnf.blog.common;

import java.security.Key;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import net.afnf.blog.config.AppConfig;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Crypto {

    private static Logger logger = LoggerFactory.getLogger(Crypto.class);

    private static final String TMPKEY_ALGO = "PBKDF2WithHmacSHA1";
    private static final String KEY_ALGO = "AES";
    private static final String CRYPTO_ALGO = "AES/CBC/PKCS5Padding";
    private static final String MD_ALGO = "SHA-256";
    private static final int KEN_LEN = 128;
    private static final int ITERATION_COUNT = 1;
    private static final String CHARSET = "UTF-8";
    private static final int IV_LEN = 16;

    private static Key encKey = null;

    public static String encrypt(String text) {

        try {
            Cipher cipher = Cipher.getInstance(CRYPTO_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, genKey());
            byte[] iv = cipher.getIV();
            byte[] encryptedBytes = cipher.doFinal(text.getBytes(CHARSET));

            int dataLen = IV_LEN + encryptedBytes.length;
            byte[] data = new byte[dataLen];
            System.arraycopy(iv, 0, data, 0, IV_LEN);
            System.arraycopy(encryptedBytes, 0, data, IV_LEN, dataLen - IV_LEN);

            String encrypted = Base64.encodeBase64URLSafeString(data);

            return encrypted;
        }
        catch (Exception e) {
            logger.error("encrypt failure", e);
            return null;
        }
    }

    public static String decrypt(String encrypted) {

        try {
            byte[] textBytes = Base64.decodeBase64(encrypted);
            int textBytesLen = textBytes.length;

            byte[] iv = new byte[IV_LEN];
            System.arraycopy(textBytes, 0, iv, 0, IV_LEN);

            int dataLen = textBytesLen - IV_LEN;
            byte[] data = new byte[dataLen];
            System.arraycopy(textBytes, IV_LEN, data, 0, dataLen);

            Cipher cipher = Cipher.getInstance(CRYPTO_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, genKey(), new IvParameterSpec(iv));
            byte[] decryptedBytes = cipher.doFinal(data);

            return new String(decryptedBytes, CHARSET);
        }
        catch (Exception e) {
            logger.warn("decrypt failure");
            return null;
        }
    }

    private static Key genKey() throws Exception {

        synchronized (Crypto.class) {

            if (encKey == null) {
                byte[] salt = AppConfig.getInstance().getSalt().getBytes(CHARSET);
                String cipherSeed = AppConfig.getInstance().getCipherSeed();

                MessageDigest md = MessageDigest.getInstance(MD_ALGO);
                char[] password = Hex.encodeHex(md.digest(cipherSeed.getBytes(CHARSET)));

                PBEKeySpec encPBESpec = new PBEKeySpec(password, salt, ITERATION_COUNT, KEN_LEN);
                SecretKeyFactory encKeyFact = SecretKeyFactory.getInstance(TMPKEY_ALGO);
                Key tmpKey = encKeyFact.generateSecret(encPBESpec);

                encKey = new SecretKeySpec(tmpKey.getEncoded(), KEY_ALGO);
            }
        }

        return encKey;
    }
}
