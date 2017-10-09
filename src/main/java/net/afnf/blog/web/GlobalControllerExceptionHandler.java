package net.afnf.blog.web;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mybatis.spring.MyBatisSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import net.afnf.blog.common.JsonResponseDemoSiteErrorException;
import net.afnf.blog.common.JsonResponseException;

@ControllerAdvice
class GlobalDefaultExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public String defaultErrorHandler(HttpServletRequest req, HttpServletResponse res, Exception e) throws Exception {

        String url = req != null ? req.getRequestURI() : "";

        String estr_short = "ip=" + req.getRemoteAddr() + ", url=" + url;
        String estr_long = estr_short + ", e=" + e.toString();

        boolean jsondemo = false;
        boolean jsonreq = (req != null && req.getHeader("X-Requested-With") != null) ? true : false;
        boolean dberror = false;

        if (e instanceof JsonResponseDemoSiteErrorException) {
            jsondemo = true;
            logger.debug(estr_long);
        }
        else if (e instanceof JsonResponseException) {
            logger.warn(estr_long);
        }
        // path variableの型不一致、Validationエラー、意図しないパラメータ（脆弱性を狙った攻撃）
        else if (e instanceof TypeMismatchException || e instanceof BindException
                || e instanceof UnsatisfiedServletRequestParameterException) {
            logger.info(estr_long);
        }
        // DB障害
        else if (e instanceof MyBatisSystemException || e instanceof SQLException || e instanceof DataAccessException) {
            dberror = true;
            logger.info(estr_long);
        }
        else {
            logger.warn(estr_short, e);
        }

        if (jsondemo) {
            res.setStatus(HttpServletResponse.SC_OK);
            res.setContentType("application/json; charset=UTF-8");
            return "{\"demoSite\":true}";
        }
        else if (jsonreq) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setContentType("application/json; charset=UTF-8");
            return "{\"error\":true}";
        }
        else if (dberror) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "db error";
        }
        else {
            res.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            res.setHeader("Location", contextPath);
            return "";
        }
    }
}