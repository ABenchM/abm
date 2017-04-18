package de.fraunhofer.abm.app.auth;

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

/**
 * A filter, which delays the request a little bit.
 * Can be used for frontend testing.
 */
public class RequestDelayFilter implements Filter {

    private static final transient Logger logger = LoggerFactory.getLogger(RequestDelayFilter.class);

    @Override
    public void init(FilterConfig cfg) throws ServletException {
        logger.debug("Initializing " + getClass().getSimpleName());
    }

    @Override
    public void destroy() {
        logger.debug("Destroying " + getClass().getSimpleName());
    }

    @Override
    public void doFilter(ServletRequest _req, ServletResponse _resp, FilterChain chain) throws IOException, ServletException {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            logger.error("Couldn't sleep", e);
        }

        HttpServletRequest req = (HttpServletRequest) _req;
        HttpServletResponse res = (HttpServletResponse) _resp;
        chain.doFilter(req, res);
    }
}
