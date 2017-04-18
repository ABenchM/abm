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

public class AuthenticationFilter implements Filter {

    private static final transient Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

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
        HttpServletRequest req = (HttpServletRequest) _req;
        HttpServletResponse res = (HttpServletResponse) _resp;
        if("/rest/login".equals(req.getRequestURI()) || "/rest/logout".equals(req.getRequestURI())) {
            chain.doFilter(req, res);
        } else {
            try {
                String user = (String) req.getSession().getAttribute("user");
                if(user != null) {
                    logger.debug("Logged in as {}", user);
                    SecurityContext.getInstance().setUser(user);
                    chain.doFilter(req, res);
                } else {
                    logger.debug("Not logged in");
                    SecurityContext.getInstance().setUser("anonymous");
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
    }
}
