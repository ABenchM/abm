package de.fraunhofer.abm.app.download;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import de.fraunhofer.abm.collection.dao.HermesResultDao;
import de.fraunhofer.abm.domain.HermesResultDTO;


public class HermesHttpContext implements HttpContext {

    private static final transient Logger logger = LoggerFactory.getLogger(HermesHttpContext.class);

    private HermesResultDao dao;
    private File archive;
    private String result;
    
    public HermesHttpContext(HermesResultDao hermesResultDao,String result) {
        this.dao = hermesResultDao;
        this.result = result;
    }

    @Override
    public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // abusing this call to set response headers
        
        if(result=="/downloadHermes") {
        String hermesResultID = request.getRequestURI().replaceAll(result+"/", "");
        
        HermesResultDTO hermesResult = dao.findById(hermesResultID);	
        archive = new File(hermesResult.dir, "hermesResults.csv");	
        response.setContentLengthLong(archive.length());
        response.setHeader("Content-Disposition", "attachment; filename=hermesResults.csv;");
        }
        return true; // allow all requests
    }

    @Override
    public URL getResource(String resource) {
        if(archive != null && archive.exists()) {
            try {
                return archive.toURI().toURL();
            } catch (MalformedURLException e) {
                logger.error("Couldn't create URL for archive download", e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public String getMimeType(String name) {
        return "application/zip";
    }
}
