package net.afnf.blog.common;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.afnf.blog.config.AppConfig;

public class IfModifiedSinceFilter implements Filter {

    private static Logger logger = LoggerFactory.getLogger(IfModifiedSinceFilter.class);

    private static long lastModified = 0;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;

            String method = req.getMethod();
            if (method != null && (method.equals("GET") || method.equals("HEAD"))) {

                // staticと_adminは対象外
                // ※staticはFixedVersionStrategyで数字が付く場合がある
                String servletPath = req.getServletPath();
                if (servletPath != null
                        && (servletPath.indexOf("/static/") >= 0 || servletPath.indexOf("/_admin/") == 0) == false) {

                    // Developmentなら無効化
                    if (AppConfig.getInstance().isDevelopment() == false) {

                        // 304でもLast-Modifiedを入れておく
                        res.setDateHeader("Last-Modified", getLastModified());

                        // Expiresをここで入れると、proxy_cache_revalidateと機能衝突を起こすようなのでコメントアウト
                        //res.setDateHeader("Expires", getLastModified() + 1000);

                        // lastModifiedと一致すれば304を返す
                        long since = req.getDateHeader("If-Modified-Since");
                        boolean notModified = since == getLastModified();
                        if (logger.isDebugEnabled()) {
                            logger.debug(" since=" + since + ", lastmod=" + getLastModified() + ", "
                                    + (notModified ? "notModified" : "modified"));
                        }
                        if (notModified) {
                            res.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                            return;
                        }
                    }
                }
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    public static void updateLastModified() {
        // HTTP Header Dateはミリ秒を扱えないのでここで取り除く
        long now = System.currentTimeMillis();
        now = now - now % 1000;
        lastModified = now;
    }

    public static long getLastModified() {
        return lastModified;
    }
}
