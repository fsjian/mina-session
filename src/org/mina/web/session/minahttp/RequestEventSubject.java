package org.mina.web.session.minahttp;


/**
 * @author  prog
 * @version  [版本号v1.0, 2017年7月20日]
 * 
 */
public class RequestEventSubject
{
    private RequestEventObserver listener;
    
    public void attach(RequestEventObserver eventObserver)
    {
        listener = eventObserver;
    }
    
    public void detach()
    {
        listener = null;
    }
    
    public void completed(HttpServletRequest servletRequest,
            HttpServletResponse response)
    {
        if (listener != null)
            listener.completed(servletRequest, response);
    }
}
