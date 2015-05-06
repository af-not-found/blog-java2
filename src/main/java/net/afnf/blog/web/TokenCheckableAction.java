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

        if (StringUtils.equalsIgnoreCase(request.getMethod(), "POST")) {
            if (StringUtils.equalsIgnoreCase(request.getHeader("X-Requested-With"), "XMLHttpRequest")) {
                String token = request.getHeader("X-FORM-TOKEN");
                if (TokenService.validateToken(token)) {
                    return;
                }
            }
        }

        throw new JsonResponseException();
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
