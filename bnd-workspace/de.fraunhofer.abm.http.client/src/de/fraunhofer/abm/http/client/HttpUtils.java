package de.fraunhofer.abm.http.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.http.client.cache.Cache;

public class HttpUtils {
    private static transient Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static TimeUnit tu = TimeUnit.MINUTES;
    private static int period = 5;
    private static Cache<String, String> stringCache = new Cache<>("Page Content Cache", 1000, period, tu);
    private static Cache<String, HttpResponse> responseCache = new Cache<>("Http Response Cache", 1000, period, tu);

    private static boolean cacheEnabled = true;

    // private static Cache<String, Map<String, List<String>>> headerCache = new Cache<String, Map<String, List<String>>>(1000, period, tu);

    /**
     * Downloads a web page.
     *
     * @param url
     *            the webpage to download
     * @param headers
     *            the HTTP headers to send
     * @param charset
     *            the charset used to decode the webpage
     *
     */
    public static String get(String url, Map<String, String> headers, String charset) throws IOException {
        return get(url, headers, charset, null, null);
    }

    /**
     * Performs a HTTP basic auth and downloads a web page.
     *
     * @param url
     *            the webpage to download
     * @param headers
     *            the HTTP headers to send
     * @param charset
     *            the charset used to decode the webpage
     * @param user
     *            the user name for the login
     * @param pass
     *            the password for the login
     *
     */
    public static String get(String url, Map<String, String> headers, String charset, String user, String pass) throws IOException {
        if (headers != null) {
            headers.put("redirected", url);
        }
        String cachedPage = stringCache.get(url);
        if (cacheEnabled && cachedPage != null) {
            logger.trace("Page found in cache: {}", url);
            return cachedPage;
        } else {
            logger.trace("Downloading page {}", url);
            URL page = new URL(url);
            URLConnection con = page.openConnection();
            if (headers != null) {
                for (Iterator<Entry<String, String>> iterator = headers.entrySet().iterator(); iterator.hasNext();) {
                    Entry<String, String> entry = iterator.next();
                    con.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            con.setRequestProperty("Accept-Encoding", "gzip");

            // set up basic athentication
            if (user != null && pass != null) {
                con.setRequestProperty("Authorization", userNamePasswordBase64(user, pass));
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int length = -1;
            byte[] b = new byte[1024];
            InputStream in = con.getInputStream();
            if ("gzip".equalsIgnoreCase(con.getHeaderField("Content-Encoding"))) {
                in = new GZIPInputStream(in);
            }
            while ((length = in.read(b)) > 0) {
                bos.write(b, 0, length);
            }

            if (headers != null) {
                headers.put("redirected", con.getURL().toString());
            }
            String pageContent = new String(bos.toByteArray(), charset);
            stringCache.put(url, pageContent);
            return pageContent;
        }
    }

    private static String userNamePasswordBase64(String username, String password) {
        String s = username + ":" + password;
        String encs = Base64.encodeBytes(s.getBytes());
        return "Basic " + encs;
    }

    public static HttpResponse getResponse(String url, Map<String, String> headers, String charset) throws IOException {
        HttpResponse response = responseCache.get(url);
        if (cacheEnabled && response != null) {
            logger.trace("Page found in cache: {}", url);
            return response;
        } else {
            logger.trace("Downloading page {}", url);
            URL page = new URL(url);
            HttpURLConnection con = (HttpURLConnection) page.openConnection();
            if (headers != null) {
                for (Iterator<Entry<String, String>> iterator = headers.entrySet().iterator(); iterator.hasNext();) {
                    Entry<String, String> entry = iterator.next();
                    con.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            con.setRequestProperty("Accept-Encoding", "gzip");

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int length = -1;
            byte[] b = new byte[1024];
            InputStream in = con.getInputStream();
            if ("gzip".equalsIgnoreCase(con.getHeaderField("Content-Encoding"))) {
                in = new GZIPInputStream(in);
            }
            while ((length = in.read(b)) > 0) {
                bos.write(b, 0, length);
            }

            int code = con.getResponseCode();
            String responseContent = new String(bos.toByteArray(), charset);
            response = new HttpResponse(code, responseContent, con.getHeaderFields());
            responseCache.put(url, response);
            return response;
        }
    }

    /**
     *
     * @param url
     * @param headers
     * @param content
     *            the post body
     * @param responseCharset
     *            the expected charset of the response
     * @return
     * @throws IOException
     */
    public static HttpResponse post(String url, Map<String, String> headers, byte[] content, String responseCharset) throws IOException {
        return putOrPost(url, headers, content, responseCharset, "POST");
    }

    /**
     *
     * @param url
     * @param headers
     * @param content
     *            the put body
     * @param responseCharset
     *            the expected charset of the response
     * @return
     * @throws IOException
     */
    public static HttpResponse put(String url, Map<String, String> headers, byte[] content, String responseCharset) throws IOException {
        return putOrPost(url, headers, content, responseCharset, "PUT");
    }

    private static HttpResponse putOrPost(String url, Map<String, String> headers, byte[] content, String responseCharset, String method) throws IOException {
        logger.trace("Downloading page {}", url);
        // initialize the connection
        URL page = new URL(url);
        HttpURLConnection con = (HttpURLConnection) page.openConnection();
        con.setRequestMethod(method);
        if (headers != null) {
            for (Iterator<Entry<String, String>> iterator = headers.entrySet().iterator(); iterator.hasNext();) {
                Entry<String, String> entry = iterator.next();
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        con.setRequestProperty("Accept-Encoding", "gzip");
        con.setDoOutput(true);

        // send the post
        OutputStream os = con.getOutputStream();
        os.write(content);
        os.flush();

        // read the response
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int length = -1;
        byte[] b = new byte[1024];
        InputStream in = con.getInputStream();
        if ("gzip".equalsIgnoreCase(con.getHeaderField("Content-Encoding"))) {
            in = new GZIPInputStream(in);
        }
        while ((length = in.read(b)) > 0) {
            bos.write(b, 0, length);
        }

        int code = con.getResponseCode();
        String responseContent = new String(bos.toByteArray(), responseCharset);
        HttpResponse response = new HttpResponse(code, responseContent, con.getHeaderFields());
        return response;
    }

    public static HttpResponse delete(String url, Map<String, String> headers, String responseCharset) throws IOException {
        logger.trace("Downloading page {}", url);
        // initialize the connection
        URL page = new URL(url);
        HttpURLConnection con = (HttpURLConnection) page.openConnection();
        con.setRequestMethod("DELETE");
        if (headers != null) {
            for (Iterator<Entry<String, String>> iterator = headers.entrySet().iterator(); iterator.hasNext();) {
                Entry<String, String> entry = iterator.next();
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        con.setRequestProperty("Accept-Encoding", "gzip");
        con.setDoOutput(true);

        // read the response
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int length = -1;
        byte[] b = new byte[1024];
        InputStream in = con.getInputStream();
        if ("gzip".equalsIgnoreCase(con.getHeaderField("Content-Encoding"))) {
            in = new GZIPInputStream(in);
        }
        while ((length = in.read(b)) > 0) {
            bos.write(b, 0, length);
        }

        int code = con.getResponseCode();
        String responseContent = new String(bos.toByteArray(), responseCharset);
        HttpResponse response = new HttpResponse(code, responseContent, con.getHeaderFields());
        return response;
    }

    public static Map<String, List<String>> head(String url, Map<String, String> headers, String charset) throws IOException {
        logger.trace("Request HEAD for page {}", url);
        URL page = new URL(url);
        URLConnection con = page.openConnection();
        if (headers != null) {
            for (Iterator<Entry<String, String>> iterator = headers.entrySet().iterator(); iterator.hasNext();) {
                Entry<String, String> entry = iterator.next();
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        return con.getHeaderFields();
    }

    public static String getHeaderField(Map<String, List<String>> headers, String headerField) {
        if (!headers.containsKey(headerField)) {
            return null;
        }

        List<String> value = headers.get(headerField);
        if (value.size() == 1) {
            return value.get(0);
        } else {
            throw new RuntimeException("Header contains several values and cannot be mapped to a single String");
        }
    }

    public static Map<String, List<String>> parseQuery(String query) throws UnsupportedEncodingException {
        Map<String, List<String>> parameters = new HashMap<>();
        if (query != null) {
            StringTokenizer st = new StringTokenizer(query, "&");
            while (st.hasMoreTokens()) {
                String keyValue = st.nextToken();
                StringTokenizer st2 = new StringTokenizer(keyValue, "=");
                String key = null;
                String value = "";
                if (st2.hasMoreTokens()) {
                    key = st2.nextToken();
                    key = URLDecoder.decode(key, "utf-8");
                }

                if (st2.hasMoreTokens()) {
                    value = st2.nextToken();
                    value = URLDecoder.decode(value, "utf-8");
                }

                logger.debug("Found key value pair: " + key + "," + value);
                List<String> values = parameters.get(key);
                if (values == null) {
                    values = new ArrayList<>();
                    parameters.put(key, values);
                }
                values.add(value);
            }
        }
        return parameters;
    }

    public static Map<String, String> createFirefoxHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:45.0) Gecko/20100101 Firefox/45.0");
        header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        header.put("Accept-Language", "en-us;q=0.5,en;q=0.3");
        header.put("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        header.put("Keep-Alive", "300");
        header.put("Connection", "keep-alive");
        return header;
    }

    public static List<HttpCookie> getCookies(Map<String, List<String>> header) {
        List<HttpCookie> cookies = new ArrayList<>();
        if (header != null && header.containsKey("Set-Cookie")) {
            List<String> cookieList = header.get("Set-Cookie");
            for (String cookie : cookieList) {
                cookies.addAll(HttpCookie.parse(cookie));
            }
        } else if (header != null && header.containsKey("Set-Cookie2")) {
            List<String> cookieList = header.get("Set-Cookie2");
            for (String cookie : cookieList) {
                cookies.addAll(HttpCookie.parse(cookie));
            }
        }
        return cookies;
    }

    public static String toString(List<HttpCookie> cookies) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<HttpCookie> iterator = cookies.iterator(); iterator.hasNext();) {
            HttpCookie httpCookie = iterator.next();
            sb.append(httpCookie.getName()).append('=').append(httpCookie.getValue());
            if (iterator.hasNext()) {
                sb.append("; ");
            }
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public static URL appendParameter(URL url, String parameter, String value) throws MalformedURLException, UnsupportedEncodingException {
        StringBuilder tempUrl = new StringBuilder(url.getProtocol());
        tempUrl.append("://").append(url.getAuthority());

        if (url.getPath().length() <= 0) {
            tempUrl.append("/?");
        } else {
            tempUrl.append(url.getPath()).append('?');
        }

        String query = url.getQuery();
        Map<String, Object> parameters = new HashMap<>();
        parseQuery(query, parameters);

        boolean added = false;
        for (Iterator<Entry<String, Object>> iterator = parameters.entrySet().iterator(); iterator.hasNext();) {
            Entry<String, Object> param = iterator.next();
            if (param.getKey().equals(parameter)) {
                tempUrl.append(parameter).append('=').append(value);
                added = true;
            } else {
                if (param.getValue() instanceof String) {
                    tempUrl.append(param.getKey()).append('=').append(param.getValue());
                } else if (param.getValue() instanceof List) {
                    List<String> list = (List<String>) param.getValue();
                    for (Iterator<String> iter = list.iterator(); iter.hasNext();) {
                        String v = iter.next();
                        tempUrl.append(param.getKey()).append('=').append(v);
                        if (iter.hasNext()) {
                            tempUrl.append('&');
                        }
                    }
                }
            }

            if (iterator.hasNext()) {
                tempUrl.append('&');
            }
        }

        if (!added) {
            tempUrl.append('&').append(parameter).append('=').append(value);
        }

        tempUrl.append(url.getRef() != null ? url.getRef() : "");

        return new URL(tempUrl.toString());
    }

    public static URL appendParameters(URL url, Map<String, String> params) throws MalformedURLException, UnsupportedEncodingException {
        for (Entry<String, String> entry : params.entrySet()) {
            url = appendParameter(url, entry.getKey(), entry.getValue());
        }
        return url;
    }

    @SuppressWarnings("unchecked")
    public static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {
        logger.trace("Parsing query " + query);
        if (query != null) {
            StringTokenizer st = new StringTokenizer(query, "&");
            while (st.hasMoreTokens()) {
                String keyValue = st.nextToken();
                StringTokenizer st2 = new StringTokenizer(keyValue, "=");
                String key = null;
                String value = "";
                if (st2.hasMoreTokens()) {
                    key = st2.nextToken();
                    key = URLDecoder.decode(key, "UTF-8");
                }

                if (st2.hasMoreTokens()) {
                    value = st2.nextToken();
                    value = URLDecoder.decode(value, "UTF-8");
                }

                logger.trace("Found key value pair: " + key + "," + value);
                if (parameters.containsKey(key)) {
                    logger.trace("Key already exists. Assuming array of values. Will bes tored in a list");
                    Object o = parameters.get(key);
                    if (o instanceof List) {
                        List<String> values = (List<String>) o;
                        values.add(value);
                    } else if (o instanceof String) {
                        List<String> values = new ArrayList<>();
                        values.add((String) o);
                        values.add(value);
                        parameters.put(key, values);
                    }
                } else {
                    parameters.put(key, value);
                }
            }
        }
    }

    public static String extractParameter(String query, String parameterName) throws UnsupportedEncodingException {
        if (query == null) {
            return null;
        }

        Map<String, Object> parameters = new HashMap<>();
        parseQuery(query, parameters);
        return parameters.get(parameterName).toString();
    }

    public static void useCache(boolean useCache) {
        HttpUtils.cacheEnabled = useCache;
    }

    public static void download(String uri, File target) throws IOException {
        logger.info("Downloading {} to {}", uri, target);
        HttpURLConnection http = null;
        FileOutputStream fos = null;
        try {
            http = (HttpURLConnection) new URL(uri).openConnection();
            System.out.println(http.getResponseCode());
            Map<String,String> headers = createFirefoxHeader();
            for (Iterator<Entry<String, String>> iterator = headers.entrySet().iterator(); iterator.hasNext();) {
                Entry<String, String> entry = iterator.next();
                http.setRequestProperty(entry.getKey(), entry.getValue());
            }
            InputStream in = http.getInputStream();
            int length = -1;
            byte[] buffer = new byte[1024];
            fos = new FileOutputStream(target);
            while ((length = in.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        } finally {
            if(http != null) {
                http.disconnect();
            }
            if(fos != null) {
                fos.close();
            }
        }
    }
    
    public static void downloadJar(String uri, File target) throws IOException {
    	 
    	URL url = new URL(uri);
    	InputStream inStream = url.openStream();
    	System.out.println(inStream.available());
    	BufferedInputStream bufIn = new BufferedInputStream(inStream);
    	             
    	
    	    OutputStream out= new FileOutputStream(target);
    	    BufferedOutputStream bufOut = new BufferedOutputStream(out);
    	                    byte buffer[] = new byte[1024];
    	            while (true) {
    	int nRead = bufIn.read(buffer, 0, buffer.length);
    	if (nRead <= 0)
    	  break;
    	bufOut.write(buffer, 0, nRead);
    	        }
    	             
    	            bufOut.flush();
    	            out.close();
    	            inStream.close();
    }
}
