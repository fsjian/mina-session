package org.mina.web.session.manager;

import java.util.UUID;

import javax.servlet.http.Cookie;

import net.rubyeye.xmemcached.MemcachedClient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.mina.web.session.minahttp.HttpServletRequest;
import org.mina.web.session.minahttp.HttpServletResponse;
import org.mina.web.session.minahttp.MinaHttpServletRequestWrapper;
import org.mina.web.session.minahttp.RequestEventObserver;
import org.mina.web.session.minahttp.RequestEventSubject;

/**
 * @author  prog
 * @version  [版本号v1.0, 2017年7月10日]
 * 
 */
public class MemcachedSessionManager
{
    public static final String SESSION_ID_PREFIX = "JSID_";
    
    public static final String SESSION_ID_COOKIE = "JSESSIONID";
    
    private MemcachedClient memcachedClient;
    
    /*如果session没有变化，则5分钟更新一次memcached*/
    private int expirationUpdateInterval = 5 * 60;
    
    //失效时间  12小时 ，单位秒
    //private int maxInactiveInterval = 720 * 60;
    
    private int maxInactiveInterval;
    
    //private int maxInactiveInterval = 10 * 60;
    private Logger log = Logger.getLogger(getClass());
    
    public void setMemcachedClient(MemcachedClient memcachedClient)
    {
        this.memcachedClient = memcachedClient;
    }
    
    public void setExpirationUpdateInterval(int expirationUpdateInterval)
    {
        this.expirationUpdateInterval = expirationUpdateInterval;
    }
    
    public void setMaxInactiveInterval(int maxInactiveInterval)
    {
        this.maxInactiveInterval = maxInactiveInterval;
    }
    
    public CacheHttpSession createSession(
            MinaHttpServletRequestWrapper request,
            HttpServletResponse response,
            RequestEventSubject requestEventSubject, boolean create)
    {
        String sessionId = request.getRequestedSessionId();
        CacheHttpSession session = null;
        if (StringUtils.isEmpty(sessionId) && create == false)
            return null;
        if (StringUtils.isNotEmpty(sessionId))
        {
            session = loadSession(sessionId);
        }
        if (session == null && create)
        {
            session = createEmptySession(request, response);
        }
        if (session != null)
            attachEvent(session, request, response, requestEventSubject);
        return session;
    }
    
    private CacheHttpSession createEmptySession(
            MinaHttpServletRequestWrapper request, HttpServletResponse response)
    {
        CacheHttpSession session = new CacheHttpSession();
        session.id = createSessionId();
        session.creationTime = System.currentTimeMillis();
        session.maxInactiveInterval = maxInactiveInterval;
        session.isNew = true;
        if (log.isDebugEnabled())
            log.debug("MemcachedHttpSession Create [ID=" + session.id + "]");
        saveCookie(session, request, response);
        return session;
    }
    
    /**
     * when request is completed,write session into memcached and write cookie into response
     * @param session  MemcachedHttpSession
     * @param request  HttpServletRequestWrapper
     * @param response HttpServletResponse
     * @param requestEventSubject  RequestEventSubject
     */
    private void attachEvent(final CacheHttpSession session,
            final MinaHttpServletRequestWrapper request,
            final HttpServletResponse response,
            RequestEventSubject requestEventSubject)
    {
        session.setListener(new SessionListenerAdaptor()
        {
            public void onInvalidated(CacheHttpSession session)
            {
                saveCookie(session, request, response);
            }
        });
        requestEventSubject.attach(new RequestEventObserver()
        {
            public void completed(HttpServletRequest servletRequest,
                    HttpServletResponse response)
            {
                int updateInterval = (int) ((System.currentTimeMillis() - session.lastAccessedTime) / 1000);
                if (log.isDebugEnabled())
                    log.debug("MemcachedHttpSession Request completed [ID="
                            + session.id + ",lastAccessedTime="
                            + session.lastAccessedTime + ",updateInterval="
                            + updateInterval + "]");
                if (session.isNew == false && session.isDirty == false
                        && updateInterval < expirationUpdateInterval)
                    return;
                if (session.isNew && session.expired)
                    return;
                session.lastAccessedTime = System.currentTimeMillis();
                saveSession(session);
            }
        });
    }
    
    private void saveSession(CacheHttpSession session)
    {
        try
        {
            if (log.isDebugEnabled())
                log.debug("MemcachedHttpSession saveSession [ID=" + session.id
                        + ",isNew=" + session.isNew + ",isDiry="
                        + session.isDirty + ",isExpired=" + session.expired
                        + "]");
            if (session.expired)
                memcachedClient.delete(generatorSessionKey(session.id));
            else
                memcachedClient.set(generatorSessionKey(session.id),
                        session.maxInactiveInterval + expirationUpdateInterval,
                        session);
        }
        catch (Exception e)
        {
            throw new SessionException(e);
        }
    }
    
    private void saveCookie(CacheHttpSession session,
            MinaHttpServletRequestWrapper request, HttpServletResponse response)
    {
        if (session.isNew == false && session.expired == false)
            return;
        
        Cookie cookie = new Cookie(SESSION_ID_COOKIE, null);
        cookie.setPath("/");//带实现
        if (session.expired)
        {
            cookie.setMaxAge(0);
        }
        else if (session.isNew)
        {
            cookie.setValue(session.getId());
        }
        response.addCookie(cookie);
        
        if (log.isDebugEnabled())
            log.debug("MemcachedHttpSession saveCookie [ID=" + session.id + "]");
    }
    
    private CacheHttpSession loadSession(String sessionId)
    {
        try
        {
            
            CacheHttpSession session = memcachedClient.get(generatorSessionKey(sessionId));
            if (log.isDebugEnabled())
                log.debug("MemcachedHttpSession Load [ID=" + sessionId
                        + ",exist=" + (session != null) + "]");
            if (session != null)
            {
                session.isNew = false;
                session.isDirty = false;
            }
            return session;
        }
        catch (Exception e)
        {
            log.warn("exception loadSession [Id=" + sessionId + "]", e);
            return null;
        }
        
    }
    
    private String generatorSessionKey(String sessionId)
    {
        return SESSION_ID_PREFIX.concat(sessionId);
    }
    
    private String createSessionId()
    {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
}
