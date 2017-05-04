package de.fraunhofer.abm.http.client;

import java.net.HttpCookie;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private int responseCode;
    private String content;
    private Map<String, List<String>> header;

    public HttpResponse(int responseCode, String content, Map<String, List<String>> header) {
        super();
        this.responseCode = responseCode;
        this.content = content;
        this.header = header;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, List<String>> getHeader() {
        return header;
    }

    public void setHeader(Map<String, List<String>> header) {
        this.header = header;
    }

    public List<HttpCookie> getCookies() {
        return HttpUtils.getCookies(getHeader());
    }

}
