package org.mina.web.session.manager;

/**
 * @author  prog
 * @version  [版本号v1.0, 2017年7月11日]
 * 
 */
public class SessionException extends RuntimeException
{
    public SessionException()
    {
        super();
    }
    
    public SessionException(String message)
    {
        super(message);
    }
    
    public SessionException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public SessionException(Throwable cause)
    {
        super(cause);
    }
}
