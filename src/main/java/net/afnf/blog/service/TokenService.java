package net.afnf.blog.service;

import net.afnf.blog.common.Crypto;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenService {

    private static Logger logger = LoggerFactory.getLogger(TokenService.class);

    public static final int VALID_MS = 20 * 1000;

    public static String getToken() {
        String until = "" + (System.currentTimeMillis() + VALID_MS);
        String encrypted = Crypto.encrypt(until);
        return encrypted;
    }

    public static boolean validateToken(String encryptedToken) {

        String token = Crypto.decrypt(encryptedToken);
        if (token == null) {
            return false;
        }

        if (StringUtils.isNumeric(token) == false) {
            logger.warn("token is not numeric");
            return false;
        }

        long until = NumberUtils.toLong(token, -1);
        long now = System.currentTimeMillis();
        long diff = until - now;
        logger.debug("valid token, diff=" + diff + ", until=" + until + ", now=" + now);
        if (0 <= diff && diff <= VALID_MS) {
            return true;
        }
        else {
            logger.warn("token is expired, diff=" + (until - now));
            return false;
        }
    }
}
