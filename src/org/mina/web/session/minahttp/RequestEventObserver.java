package org.mina.web.session.minahttp;

/**
 * @author  prog
 * @version  [版本号v1.0, 2017年7月20日]
 * 
 */
public interface RequestEventObserver
{
    public void completed(HttpServletRequest servletRequest,
            HttpServletResponse response);
}
