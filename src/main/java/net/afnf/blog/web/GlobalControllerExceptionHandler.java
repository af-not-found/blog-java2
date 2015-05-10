package net.afnf.blog.web;

import javax.servlet.http.HttpServletRequest;

import net.afnf.blog.common.JsonResponseDemoSiteErrorException;
import net.afnf.blog.common.JsonResponseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
class GlobalDefaultExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {

        String viewName = "redirect:/";
        if (e instanceof JsonResponseDemoSiteErrorException) {
            viewName = "error_demosite_json";
            logger.warn(e.getClass().getName());
        }
        else if (e instanceof JsonResponseException) {
            logger.warn(e.getClass().getName());
        }
        else {
            logger.warn("", e);
        }

        ModelAndView mav = new ModelAndView();
        mav.setViewName(viewName);
        return mav;
    }
}