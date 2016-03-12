package net.afnf.blog.common;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.servlet.resource.ResourceUrlProviderExposingInterceptor;

public class CachingResourceUrlEncodingFilter extends ResourceUrlEncodingFilter {

    private static final Log logger = LogFactory.getLog(CachingResourceUrlEncodingFilter.class);

    private String prefix;

    public CachingResourceUrlEncodingFilter() {
    }

    public CachingResourceUrlEncodingFilter(String prefix) {
        this.prefix = prefix;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        filterChain.doFilter(request, new CachingResourceUrlEncodingResponseWrapper(request, response, prefix));
    }

    private static class CachingResourceUrlEncodingResponseWrapper extends HttpServletResponseWrapper {
        private HttpServletRequest request;
        private Integer indexLookupPath;
        private String contextPathAndPrefix;
        private static Map<String, String> resolveCache = new ConcurrentHashMap<>();

        public CachingResourceUrlEncodingResponseWrapper(HttpServletRequest request, HttpServletResponse wrapped, String prefix) {
            super(wrapped);
            this.request = request;
            if (prefix != null) {
                this.contextPathAndPrefix = request.getContextPath() + prefix;
            }
        }

        @Override
        public String encodeURL(String url) {

            String value = resolveCache.get(url);
            if (value != null) {
                return value;
            }
            else {
                if (contextPathAndPrefix == null || url.startsWith(contextPathAndPrefix)) {

                    ResourceUrlProvider resourceUrlProvider = getResourceUrlProvider();
                    if (resourceUrlProvider == null) {
                        logger.debug("Request attribute exposing ResourceUrlProvider not found");
                        return super.encodeURL(url);
                    }
                    initIndexLookupPath(resourceUrlProvider);
                    if (url.length() >= this.indexLookupPath) {
                        String prefix = url.substring(0, this.indexLookupPath);
                        int suffixIndex = getQueryParamsIndex(url);
                        String suffix = url.substring(suffixIndex);
                        String lookupPath = url.substring(this.indexLookupPath, suffixIndex);
                        lookupPath = resourceUrlProvider.getForLookupPath(lookupPath);
                        if (lookupPath != null) {
                            value = super.encodeURL(prefix + lookupPath + suffix);
                            resolveCache.put(url, value);
                            return value;
                        }
                    }
                }
                value = super.encodeURL(url);
                return value;
            }
        }

        private ResourceUrlProvider getResourceUrlProvider() {
            String name = ResourceUrlProviderExposingInterceptor.RESOURCE_URL_PROVIDER_ATTR;
            return (ResourceUrlProvider) this.request.getAttribute(name);
        }

        private void initIndexLookupPath(ResourceUrlProvider urlProvider) {
            if (this.indexLookupPath == null) {
                String requestUri = urlProvider.getPathHelper().getRequestUri(this.request);
                String lookupPath = urlProvider.getPathHelper().getLookupPathForRequest(this.request);
                this.indexLookupPath = requestUri.lastIndexOf(lookupPath);
            }
        }

        private int getQueryParamsIndex(String url) {
            int index = url.indexOf("?");
            return index > 0 ? index : url.length();
        }
    }
}
