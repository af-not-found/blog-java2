package net.afnf.blog.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.afnf.blog.common.JsonResponseDemoSiteErrorException;
import net.afnf.blog.common.JsonResponseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
class GlobalDefaultExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

    @Value("${server.context-path}")
    private String contextPath;

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public String defaultErrorHandler(HttpServletRequest req, HttpServletResponse res, Exception e) throws Exception {

        String url = req != null ? req.getRequestURI() : "";
        String estr = "url=" + url + ", e=" + e.getClass().getName();
        boolean jsondemo = false;
        boolean jsonreq = (req != null && req.getHeader("X-Requested-With") != null) ? true : false;

        if (e instanceof JsonResponseDemoSiteErrorException) {
            jsondemo = true;
            logger.debug(estr);
        }
        else if (e instanceof JsonResponseException) {
            logger.warn(estr);
        }
        else if (e instanceof TypeMismatchException || e instanceof BindException) {
            logger.info(estr);
        }
        else {
            logger.warn("url=" + url, e);
        }

        if (jsondemo || jsonreq) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType("application/json; charset=UTF-8");
            return jsondemo ? "{\"demoSite\":true}" : "{\"error\":true}";
        }
        else {
            res.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            res.setHeader("Location", contextPath);
            return "";
        }
    }
}