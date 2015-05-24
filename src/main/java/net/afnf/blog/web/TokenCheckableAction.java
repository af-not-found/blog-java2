package net.afnf.blog.web;

import javax.servlet.http.HttpServletRequest;

import net.afnf.blog.common.JsonResponseDemoSiteErrorException;
import net.afnf.blog.common.JsonResponseException;
import net.afnf.blog.config.AppConfig;
import net.afnf.blog.service.TokenService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class TokenCheckableAction {

    protected static final String SUCCESS_JSON = "{\"res\":1}";

    //private static Logger logger = LoggerFactory.getLogger(TokenCheckableAction.class);

    @Autowired
    protected HttpServletRequest request;

    public void checkToken() {

        if (AppConfig.getInstance().isDemoSite()) {
            throw new JsonResponseDemoSiteErrorException();
        }

        String errorMsg = null;

        if (StringUtils.equalsIgnoreCase(request.getMethod(), "post") == false) {
            errorMsg = "checkToken : not POST method";
        }
        else {
            if (StringUtils.equalsIgnoreCase(request.getHeader("X-Requested-With"), "XMLHttpRequest") == false) {
                errorMsg = "checkToken : not Ajax";
            }
            else {
                String token = request.getHeader("X-FORM-TOKEN");
                if (token == null) {
                    errorMsg = "checkToken : null token";
                }
                else if (TokenService.validateToken(token) == false) {
                    errorMsg = "checkToken : invalid token";
                }
            }
        }

        if (errorMsg != null) {
            throw new JsonResponseException(errorMsg);
        }
    }

    protected String getClientInfo() {

        StringBuilder sb = new StringBuilder();

        String remote = null;
        String fowarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(fowarded)) {
            String[] hosts = StringUtils.split(fowarded, ",");
            if (hosts != null) {
                int len = hosts.length;

                if (len >= 1) {
                    remote = hosts[len - 1];
                }
                if (len >= 2 && StringUtils.indexOf(remote, "unix:") != -1) {
                    remote = hosts[len - 2];
                }
            }
        }

        if (StringUtils.isBlank(remote)) {
            remote = request.getRemoteAddr();
        }

        sb.append(remote);
        sb.append(", ");
        sb.append(request.getHeader("User-Agent"));

        return sb.toString();
    }
}
