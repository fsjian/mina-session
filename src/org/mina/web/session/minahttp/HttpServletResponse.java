package org.mina.web.session.minahttp;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

/**
 * @author  prog
 * @version  [版本号v1.0, 2017年7月1日]
 * 
 */

public class HttpServletResponse
{
	private Logger logger = Logger.getLogger(getClass());
    
    /** HTTP response codes */
    public static final int HTTP_STATUS_SUCCESS = 200;
    
    public static final int HTTP_STATUS_NOT_FOUND = 404;
    
    public static final int HTTP_STATUS_MOVED_PERMANENTLY = 301;
    
    public static final int HTTP_STATUS_FOUND = 302;
    
    public static final int HTTP_STATUS_FAILD = 500;
    
    
    
    private final Map<String, String> headers = new HashMap<String, String>();
    
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
            2048);
    
    private int responseCode = HTTP_STATUS_SUCCESS;
    
    public HttpServletResponse()
    {
    };
    
   
    
    public HttpServletResponse(int code, String location)
    {
        switch (code)
        {
            case HTTP_STATUS_SUCCESS:
                headers.put("Server", "HttpServer (" + "xw 2.0" + ')');
                headers.put("Cache-Control", "private");
                headers.put("Content-Type", "text/html; charset=utf-8");
                headers.put("Connection", "keep-alive");
                headers.put("Date", new SimpleDateFormat(
                        "EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date()));
                headers.put("Last-Modified", new SimpleDateFormat(
                        "EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date()));
                break;
                //自行扩展
            case HTTP_STATUS_NOT_FOUND:
                
                break;
        
        }
        
    }
    
    public Map<String, String> getHeaders()
    {
        return headers;
    }
    
    public void setContentType(String contentType)
    {
        headers.put("Content-Type", contentType);
    }
    
    public void setHeader(String key, String value)
    {
        headers.put(key, value);
    }
    
    public void setResponseCode(int responseCode)
    {
        this.responseCode = responseCode;
    }
    
    public int getResponseCode()
    {
        return this.responseCode;
    }
    
    public void appendBody(byte[] b)
    {
        try
        {
        	outputStream.write(b);
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }
    }
    
    public void appendBody(String s)
    {
        try
        {
            appendBody(s.getBytes("UTF-8"));
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }
    }
    
    public IoBuffer getBody()
    {
        return IoBuffer.wrap(outputStream.toByteArray());
    }
    
    public int getBodyLength()
    {
        return outputStream.size();
    }
    
    public ByteArrayOutputStream getOutputStream()
    {
        return outputStream;
    }
    
    public void sendRedirect(String location)
    {
        this.setResponseCode(HttpServletResponse.HTTP_STATUS_FOUND);
        this.headers.put("Location", location);
    }
    
    public void addCookie(Cookie cookie)
    {
        
    }
}