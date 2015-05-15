package net.afnf.blog.common;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import net.afnf.blog.config.AppConfig;

import org.junit.Test;

public class CryptoTest {

    @Test
    public void testEncryptDecrypt1() {
        AppConfig appConfig = new AppConfig();
        appConfig.setSalt("a");
        appConfig.setCipherSeed("b");

        String encrypted;

        encrypted = Crypto.encrypt("test123");
        assertEquals("test123", Crypto.decrypt(encrypted));

        assertNull(Crypto.decrypt(encrypted + "a"));
        assertNull(Crypto.decrypt("b" + encrypted));
    }

    @Test
    public void testEncryptDecrypt2() {
        AppConfig appConfig = new AppConfig();
        appConfig.setSalt("a");
        appConfig.setCipherSeed("b");

        String encrypted, decrypted;

        encrypted = "UewpfcYg-s_znsPvQPALIfsYwhuBd_y1u5rBbIA1HmI=";
        decrypted = Crypto.decrypt(encrypted);
        assertThat(decrypted, equalTo("test123"));
    }

    @Test
    public void testEncryptDecrypt3() {
        AppConfig appConfig = new AppConfig();
        appConfig.setSalt("aaaaaaaaaaaaaaaaaaaaaa");
        appConfig.setCipherSeed("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");

        assertThat(Crypto.decrypt(Crypto.encrypt("test")), equalTo("test"));
    }

}
