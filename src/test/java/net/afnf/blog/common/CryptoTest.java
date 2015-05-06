package net.afnf.blog.common;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import net.afnf.blog.config.AppConfig;

import org.junit.Test;

public class CryptoTest {

    @Test
    public void testEncryptDecrypt1() {
        AppConfig appConfig = new AppConfig();
        appConfig.setSalt("a");
        appConfig.setCipherSeed("b");

        String encrypted, decrypted;

        encrypted = Crypto.encrypt("test");
        assertEquals("test", Crypto.decrypt(encrypted));

        encrypted += "a";
        decrypted = Crypto.decrypt(encrypted);
        assertThat(decrypted, not(equalTo("test")));
        assertThat(decrypted, nullValue());

        encrypted = encrypted.substring(0, encrypted.length() - 2);
        decrypted = Crypto.decrypt(encrypted);
        assertThat(decrypted, not(equalTo("test")));
        assertThat(decrypted, nullValue());
    }

    @Test
    public void testEncryptDecrypt2() {
        AppConfig appConfig = new AppConfig();
        appConfig.setSalt("a");
        appConfig.setCipherSeed("b");

        String encrypted, decrypted;

        encrypted = "IUP5OMMUMnjAI5VGqVJ8HD4UtzZ2q5LOpbZr51g3V1Y";
        decrypted = Crypto.decrypt(encrypted);
        assertThat(decrypted, equalTo("test"));
        assertThat(decrypted, not(equalTo("TEST")));
    }

    @Test
    public void testEncryptDecrypt3() {
        AppConfig appConfig = new AppConfig();
        appConfig.setSalt("aaaaaaaaaaaaaaaaaaaaaa");
        appConfig.setCipherSeed("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");

        assertThat(Crypto.decrypt(Crypto.encrypt("test")), equalTo("test"));
    }

}
