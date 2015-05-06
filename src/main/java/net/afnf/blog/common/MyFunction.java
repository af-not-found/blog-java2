package net.afnf.blog.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import net.afnf.blog.config.AppConfig;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyFunction {

    private static Logger logger = LoggerFactory.getLogger(MyFunction.class);

    private static MyFunction instance = new MyFunction();

    public static MyFunction getInstance() {
        return instance;
    }

    public String urlEncodeUtf8(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            logger.error("urlEncodeUtf8 failed", e);
            return "";
        }
    }

    public String escapeXmlString(String str) {
        return StringEscapeUtils.escapeXml11(str);
    }

    public String escapeJavascriptString(String str) {
        return StringEscapeUtils.escapeEcmaScript(str);
    }

    protected String newline2br(String str) {
        str = StringUtils.replace(str, "\r", "");
        if (StringUtils.isNotBlank(str)) {
            str = str.replaceAll("\n{3,}", "\n\n");
            str = str.replaceAll("\n", "<br/>");
        }
        return str;
    }

    public String renderComment(String str) {
        str = StringEscapeUtils.escapeXml11(str);
        str = newline2br(str);
        return str;
    }

    public String formatDate(Date date) {

        if (date == null) {
            return "";
        }

        String fmt = "yyyy/MM/dd HH:mm";
        Locale locale = Locale.JAPANESE;

        // i18nはJavascript側でやる
        //        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //        if (requestAttributes instanceof ServletRequestAttributes) {
        //            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        //            String lang = request.getHeader("Accept-Language");
        //            if (StringUtils.indexOfIgnoreCase(lang, "ja") != 0) {
        //                fmt = "d MMM yyyy HH:mm z";
        //                locale = Locale.ENGLISH;
        //            }
        //        }

        SimpleDateFormat sdf = new SimpleDateFormat(fmt, locale);
        sdf.setTimeZone(TimeZone.getTimeZone("JST"));
        String ret = sdf.format(date);
        return ret;
    }

    public String formatLongTime(long time) {

        String fmt = "yyyy/MM/dd HH:mm:ss.SSS";
        Locale locale = Locale.JAPANESE;

        SimpleDateFormat sdf = new SimpleDateFormat(fmt, locale);
        sdf.setTimeZone(TimeZone.getTimeZone("JST"));
        String ret = sdf.format(new Date(time));
        return ret;
    }

    public String formatPubDate(Date date) {

        if (date == null) {
            return "";
        }

        String fmt = "EEE, dd MMM yyyy HH:mm:ss Z";
        Locale locale = Locale.ENGLISH;

        SimpleDateFormat sdf = new SimpleDateFormat(fmt, locale);
        sdf.setTimeZone(TimeZone.getTimeZone("JST"));
        String ret = sdf.format(date);
        return ret;
    }

    public String generateTitle(String prefix) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(prefix)) {
            sb.append(prefix);
            sb.append(" - ");
        }
        sb.append(AppConfig.getInstance().getTitle());
        return sb.toString();
    }
}
