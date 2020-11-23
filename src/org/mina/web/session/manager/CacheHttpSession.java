package org.mina.web.session.manager;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * @author  prog
 * @version  [版本号v1.0, 2017年7月3日]
 * 
 */
public class CacheHttpSession implements HttpSession, Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    protected long creationTime = 0L;
    
    protected String id;
    
    protected int maxInactiveInterval;
    
    protected long lastAccessedTime = 0;
    
    public transient boolean expired = false;
    
    protected transient boolean isNew = false;
    
    protected transient boolean isDirty = false;
    
    private transient SessionListener listener;
    
    private Map<String, Object> data = new HashMap<String, Object>();
    
    public void setListener(SessionListener listener)
    {
        this.listener = listener;
    }
    
    public long getCreationTime()
    {
        return creationTime;
    }
    
    public String getId()
    {
        return id;
    }
    
    public long getLastAccessedTime()
    {
        return lastAccessedTime;
    }
    
    public void setLastAccessedTime(long lastAccessedTime)
    {
        this.lastAccessedTime = lastAccessedTime;
    }
    
    public ServletContext getServletContext()
    {
        return null;
    }
    
    public void setMaxInactiveInterval(int i)
    {
        this.maxInactiveInterval = i;
    }
    
    public int getMaxInactiveInterval()
    {
        return maxInactiveInterval;
    }
    
    public HttpSessionContext getSessionContext()
    {
        return null;
    }
    
    public Object getAttribute(String key)
    {
        return data.get(key);
    }
    
    public Object getValue(String key)
    {
        return data.get(key);
    }
    
    public Enumeration getAttributeNames()
    {
        final Iterator iterator = data.keySet().iterator();
        return new Enumeration()
        {
            public boolean hasMoreElements()
            {
                return iterator.hasNext();
            }
            
            public Object nextElement()
            {
                return iterator.next();
            }
        };
    }
    
    public String[] getValueNames()
    {
        String[] names = new String[data.size()];
        return data.keySet().toArray(names);
    }
    
    public void setAttribute(String s, Object o)
    {
        data.put(s, o);
        isDirty = true;
    }
    
    public void putValue(String s, Object o)
    {
        data.put(s, o);
        isDirty = true;
    }
    
    public void removeAttribute(String s)
    {
        data.remove(s);
        isDirty = true;
    }
    
    public void removeValue(String s)
    {
        data.remove(s);
        isDirty = true;
    }
    
    public void invalidate()
    {
        expired = true;
        isDirty = true;
        if (listener != null)
            listener.onInvalidated(this);
    }
    
    public boolean isNew()
    {
        return isNew;
    }
    
}
