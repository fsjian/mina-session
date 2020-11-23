package org.mina.web.session.minahttp;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * @author  prog
 * @version  [版本号v1.0, 2017年7月1日]
 * 
 */

public class HttpServletRequest
{
    private Map<String, String[]> headers = null;
    
    private String body;
    
    public Map<String, String[]> getHeaders()
    {
        return headers;
    }
    
    public void setHeaders(Map<String, String[]> headers)
    {
        this.headers = headers;
    }
    
    public HttpSession getSession(boolean create)
    {
        return null;
    }
    
    public HttpSession getSession()
    {
        return null;
    }
    
    public String getRequestedSessionId()
    {
        return null;
    }
    
    public void setHeader(String name, String value)
    {
        this.headers.put(name, new String[] { value });
    }
    
    public String getContext()
    {
        String[] context = headers.get("Context");
        return context == null ? "" : context[0].trim();
    }
    
    public String getQueryString()
    {
        String[] queryString = headers.get("_query_string_URL_after_the_path");
        return queryString == null ? "" : queryString[0];
    }
    
    public String getParameter(String name)
    {
        String[] param = headers.get("@".concat(name));
        
        if (param == null || param.length == 0)
        {
            return "";
        }
        
        try
        {
            return URLDecoder.decode(param[0], "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return "";
        }
    }
    
    public String[] getParameters(String name)
    {
        String[] param = headers.get("@".concat(name));
        return param == null ? new String[] {} : param;
    }
    
    public String getHeader(String name)
    {
        
        if (headers.get(name) == null || headers.get(name).length == 0)
        {
            return "";
        }
        
        try
        {
            return URLDecoder.decode(headers.get(name)[0],"UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return "";
        }
        
    }
    
    
    public static String arrayToString(String[] s, char sep)
    {
        if (s == null || s.length == 0)
        {
            return "";
        }
        StringBuffer buf = new StringBuffer();
        if (s != null)
        {
            for (int i = 0; i < s.length; i++)
            {
                if (i > 0)
                {
                    buf.append(sep);
                }
                buf.append(s[i]);
            }
        }
        return buf.toString();
    }
    
    public String getBody()
    {
        return body;
    }
    
    public void setBody(String body)
    {
        this.body = body;
    }
    
}
