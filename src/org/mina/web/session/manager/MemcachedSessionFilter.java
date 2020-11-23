package org.mina.web.session.manager;


import org.apache.log4j.Logger;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.mina.web.session.minahttp.HttpServletRequest;
import org.mina.web.session.minahttp.HttpServletResponse;
import org.mina.web.session.minahttp.MinaHttpServletRequestWrapper;
import org.mina.web.session.minahttp.MinaHttpServletResponseWrapper;
import org.mina.web.session.minahttp.RequestEventSubject;

/**
 * @author  prog
 * @version  [版本号v1.0, 2017年7月10日]
 * 
 */
public class MemcachedSessionFilter extends IoFilterAdapter
{
    
    private Logger log = Logger.getLogger(getClass());
    
    public static final String[] IGNORE_SUFFIX = new String[] { ".png", ".jpg",
            ".jpeg", ".gif", ".css", ".js", ".html", ".htm" };
    
    private MemcachedSessionManager sessionManager;
    
    public void setSessionManager(MemcachedSessionManager sessionManager)
    {
        this.sessionManager = sessionManager;
    }
    
    @Override
    public void messageReceived(NextFilter nextFilter, IoSession session,
            Object message) throws Exception
    {
        // TODO Auto-generated method stub
        HttpServletRequest request = (HttpServletRequest) message;
        System.out.println("contentpath = " + request.getContext());
        if (shouldFilter(request) == false)
        {
            nextFilter.messageReceived(session, message);
            return;
        }
        HttpServletResponse response = this.errResponseMessage();
        RequestEventSubject eventSubject = new RequestEventSubject();
        MinaHttpServletRequestWrapper requestWrapper = new MinaHttpServletRequestWrapper(
                request, response, sessionManager, eventSubject);
        
        session.setAttribute("mina_response", response);
        session.setAttribute("event_subject", eventSubject);
        session.setAttribute("request", request);
        try
        {
            nextFilter.messageReceived(session, requestWrapper);
        }
        finally
        {
            //when request is completed,refresh session event,write cookie or save into memcached
            //eventSubject.completed(request, response);
            if (log.isDebugEnabled())
            {
                log.debug("请求结束 ,contentpath = " + request.getContext());
            }
        }
    }
    
    private HttpServletResponse errResponseMessage()
    {
        HttpServletResponse hrm = new MinaHttpServletResponseWrapper();
        hrm.setResponseCode(HttpServletResponse.HTTP_STATUS_NOT_FOUND);
        return hrm;
    }
    
    private boolean shouldFilter(HttpServletRequest request)
    {
        String uri = request.getContext().toLowerCase();
        for (String suffix : IGNORE_SUFFIX)
        {
            if (uri.endsWith(suffix))
                return false;
        }
        return true;
    }
    
    @Override
    public void messageSent(NextFilter nextFilter, IoSession session,
            WriteRequest writeRequest) throws Exception
    {
        nextFilter.messageSent(session, writeRequest);
        
        HttpServletResponse response = (HttpServletResponse) session.getAttribute("mina_response");
        HttpServletRequest request = (HttpServletRequest) session.setAttribute("request");
        RequestEventSubject eventSubject = (RequestEventSubject) session.getAttribute("event_subject");
        eventSubject.completed(request, response);
        
    }
    
}
