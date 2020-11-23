package org.mina.web.session.minahttp;


import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.mina.web.session.manager.CacheHttpSession;
import org.mina.web.session.manager.MemcachedSessionManager;

/**
 * @author  prog
 * @version  [版本号v1.0, 2017年7月3日]
 * 
 */
public class MinaHttpServletRequestWrapper extends HttpServletRequest
{
    private HttpServletResponse response;
    
    private CacheHttpSession httpSession;
    
    private MemcachedSessionManager sessionManager;
    
    private RequestEventSubject requestEventSubject;
    
    private Logger log = Logger.getLogger(getClass());
    
    public MinaHttpServletRequestWrapper(HttpServletRequest request,
            HttpServletResponse response,
            MemcachedSessionManager sessionManager,
            RequestEventSubject requestEventSubject)
    {
        this.setHeaders(request.getHeaders());
        this.setBody(request.getBody());
        this.response = response;
        this.sessionManager = sessionManager;
        this.requestEventSubject = requestEventSubject;
    }
    
    @Override
    public HttpSession getSession(boolean create)
    {
        if (httpSession != null && httpSession.expired == false)
            return httpSession;
        httpSession = sessionManager.createSession(this,
                response,
                requestEventSubject,
                create);
        return httpSession;
    }
    
    @Override
    public HttpSession getSession()
    {
        return getSession(true);
    }
    
    @Override
    public String getRequestedSessionId()
    {
        String requestCookie = this.getHeader("Cookie");
        if (StringUtils.isBlank(requestCookie))
        {
            requestCookie = this.getHeader("cookie");
        }
        String cookieArray[] = null;
        if (StringUtils.isNotBlank(requestCookie))
        {
            requestCookie = requestCookie.trim();
            cookieArray = StringUtils.split(requestCookie, ";");
            if (cookieArray == null || cookieArray.length == 0)
            {
                log.info("requst cookie  is null ");
                return "";
            }
            for (String cookie : cookieArray)
            {
                if (StringUtils.isNotBlank(cookie))
                {
                    String item[] = StringUtils.split(cookie, "=");
                    if (item == null || item.length == 0)
                        continue;
                    if (StringUtils.isNotBlank(item[0])
                            && item[0].trim().equals("JSESSIONID"))
                        return StringUtils.isBlank(item[1]) ? ""
                                : item[1].trim();
                }
            }
        }
        log.info("cookie carry SESSIONID is null ");
        return "";
    }
    
    protected String getContextPath()
    {
        String content = this.getContext();
        if (StringUtils.isEmpty(content))
            return "/";
        int index = content.indexOf("/");
        if (index != -1)
        {
            String contentPath = content.substring(0, index);
            return contentPath;
        }
        return content;
    }
}
