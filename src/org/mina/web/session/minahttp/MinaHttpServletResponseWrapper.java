package org.mina.web.session.minahttp;

import javax.servlet.http.Cookie;

import org.mina.web.session.util.ServerCookie;

/**
 * @author  prog
 * @version  [版本号v1.0, 2017年7月21日]
 * 
 */
public class MinaHttpServletResponseWrapper extends HttpServletResponse
{
    public void addCookie(Cookie cookie)
    {
        StringBuffer sb = this.generateCookieString(cookie, false);
        this.setHeader("Set-Cookie", sb.toString());
    }
    
    public StringBuffer generateCookieString(final Cookie cookie,
            final boolean httpOnly)
    {
        final StringBuffer sb = new StringBuffer();
        ServerCookie.appendCookieValue(sb,
                cookie.getVersion(),
                cookie.getName(),
                cookie.getValue(),
                cookie.getPath(),
                cookie.getDomain(),
                cookie.getComment(),
                cookie.getMaxAge(),
                cookie.getSecure(),
                httpOnly);
        return sb;
    }
}
