package net.afnf.blog.common;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.servlet.resource.ResourceUrlProviderExposingInterceptor;
import org.springframework.web.util.UrlPathHelper;

public class CachingResourceUrlEncodingFilter extends ResourceUrlEncodingFilter {

    private static final Log logger = LogFactory.getLog(CachingResourceUrlEncodingFilter.class);

    private String encTargetPrefix;

    public CachingResourceUrlEncodingFilter() {
    }

    public CachingResourceUrlEncodingFilter(String encTargetPrefix) {
        this.encTargetPrefix = encTargetPrefix;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException("ResourceUrlEncodingFilter just supports HTTP requests");
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        filterChain.doFilter(request, new CachingResourceUrlEncodingResponseWrapper(httpRequest, httpResponse, encTargetPrefix));
    }

    private static class CachingResourceUrlEncodingResponseWrapper extends HttpServletResponseWrapper {

        private final HttpServletRequest request;

        /* Cache the index and prefix of the path within the DispatcherServlet mapping */
        private Integer indexLookupPath;

        private String prefixLookupPath;

        private String encTargetPrefixWithContextPath;

        private static Map<String, String> resolveCache = new ConcurrentHashMap<>();

        public CachingResourceUrlEncodingResponseWrapper(HttpServletRequest request, HttpServletResponse wrapped,
                String encTargetPrefix) {
            super(wrapped);
            this.request = request;
            if (encTargetPrefix != null) {
                this.encTargetPrefixWithContextPath = request.getContextPath() + encTargetPrefix;
            }
        }

        @Override
        public String encodeURL(String url) {

            String value = resolveCache.get(url);
            if (value != null) {
                return value;
            }

            if (encTargetPrefixWithContextPath == null || url.startsWith(encTargetPrefixWithContextPath)) {

                ResourceUrlProvider resourceUrlProvider = getResourceUrlProvider();
                if (resourceUrlProvider == null) {
                    logger.debug("Request attribute exposing ResourceUrlProvider not found");
                    return super.encodeURL(url);
                }

                initLookupPath(resourceUrlProvider);
                if (url.startsWith(this.prefixLookupPath)) {
                    int suffixIndex = getQueryParamsIndex(url);
                    String suffix = url.substring(suffixIndex);
                    String lookupPath = url.substring(this.indexLookupPath, suffixIndex);
                    lookupPath = resourceUrlProvider.getForLookupPath(lookupPath);
                    if (lookupPath != null) {
                        value = super.encodeURL(this.prefixLookupPath + lookupPath + suffix);
                        if (value != null) {
                            resolveCache.put(url, value);
                        }
                        return value;
                    }
                }
            }

            return super.encodeURL(url);
        }

        private ResourceUrlProvider getResourceUrlProvider() {
            return (ResourceUrlProvider) this.request
                    .getAttribute(ResourceUrlProviderExposingInterceptor.RESOURCE_URL_PROVIDER_ATTR);
        }

        private void initLookupPath(ResourceUrlProvider urlProvider) {
            if (this.indexLookupPath == null) {
				UrlPathHelper pathHelper = urlProvider.getUrlPathHelper();
				String requestUri = pathHelper.getRequestUri(this.request);
				String lookupPath = pathHelper.getLookupPathForRequest(this.request);
                this.indexLookupPath = requestUri.lastIndexOf(lookupPath);
                this.prefixLookupPath = requestUri.substring(0, this.indexLookupPath);

                if ("/".equals(lookupPath) && !"/".equals(requestUri)) {
					String contextPath = pathHelper.getContextPath(this.request);
                    if (requestUri.equals(contextPath)) {
                        this.indexLookupPath = requestUri.length();
                        this.prefixLookupPath = requestUri;
                    }
                }
            }
        }

        private int getQueryParamsIndex(String url) {
            int index = url.indexOf("?");
            return (index > 0 ? index : url.length());
        }
    }

}
