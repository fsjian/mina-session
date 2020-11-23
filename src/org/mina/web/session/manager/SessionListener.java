package org.mina.web.session.manager;

/**
 * @author  prog
 * @version  [版本号v1.0, 2017年7月13日]
 * 
 */
public interface SessionListener
{
    public void onAttributeChanged(CacheHttpSession session);
    
    public void onInvalidated(CacheHttpSession session);
}
