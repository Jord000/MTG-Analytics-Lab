package com.mtganalytics.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
class RequestLoggingFilterConfig implements Filter {

    private static final Logger log = LoggerFactory.getLogger("RequestLogger");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        String method = httpReq.getMethod();
        String uri = httpReq.getRequestURI();

        long start = System.currentTimeMillis();
        chain.doFilter(request, response);
        long ms = System.currentTimeMillis() - start;

        if (!uri.equals("/") && !uri.startsWith("/error")) {
            log.info("{} {} ({}ms)", method, uri, ms);
        }
    }
}
