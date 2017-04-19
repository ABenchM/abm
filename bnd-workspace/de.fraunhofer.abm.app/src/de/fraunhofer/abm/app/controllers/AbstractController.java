package de.fraunhofer.abm.app.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

public abstract class AbstractController {

    void sendError(HttpServletResponse resp, int code, String mesg) {
        resp.setStatus(code);
        try {
            resp.getWriter().println(mesg);
        } catch (IOException e) {
            getLogger().error("Couldn't send error response", e);
        }
    }

    abstract Logger getLogger();
}
