package org.mina.web.session.util;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author  prog
 * @version  [版本号v1.0, 2017年7月3日]
 * 
 */
public class ServerCookie
{
    // Other fields
    private static final String OLD_COOKIE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss z";
    
    private static final ThreadLocal<DateFormat> OLD_COOKIE_FORMAT = new ThreadLocal<DateFormat>()
    {
        protected DateFormat initialValue()
        {
            DateFormat df = new SimpleDateFormat(OLD_COOKIE_PATTERN, Locale.US);
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            return df;
        }
    };
    
    public static final boolean ALWAYS_ADD_EXPIRES = true;
    
    private static final String ancientDate;
    
    static
    {
        ancientDate = OLD_COOKIE_FORMAT.get().format(new Date(10000));
    }
    
    public static void appendCookieValue(StringBuffer headerBuf, int version,
            String name, String value, String path, String domain,
            String comment, int maxAge, boolean isSecure, boolean isHttpOnly)
    {
        StringBuffer buf = new StringBuffer();
        buf.append(name);
        buf.append("=");
        maybeQuote2(version, buf, value, true);
        if (version == 1)
        {
            // Version=1 ... required
            buf.append("; Version=1");
            
            // Comment=comment
            if (comment != null)
            {
                buf.append("; Comment=");
                maybeQuote2(version, buf, comment);
            }
        }
        
        if (domain != null)
        {
            buf.append("; Domain=");
            maybeQuote2(version, buf, domain);
        }
        
        if (maxAge >= 0)
        {
            if (version > 0)
            {
                buf.append("; Max-Age=");
                buf.append(maxAge);
            }
            // IE6, IE7 and possibly other browsers don't understand Max-Age.
            // They do understand Expires, even with V1 cookies!
            if (version == 0 || ALWAYS_ADD_EXPIRES)
            {
                // Wdy, DD-Mon-YY HH:MM:SS GMT ( Expires Netscape format )
                buf.append("; Expires=");
                // To expire immediately we need to set the time in past
                if (maxAge == 0)
                    buf.append(ancientDate);
                else
                    OLD_COOKIE_FORMAT.get().format(new Date(
                            System.currentTimeMillis() + maxAge * 1000L),
                            buf,
                            new FieldPosition(0));
            }
        }
        
        // Path=path
        if (path != null)
        {
            buf.append("; Path=");
            maybeQuote2(version, buf, path);
        }
        
        // Secure
        if (isSecure)
        {
            buf.append("; Secure");
        }
        
        // HttpOnly
        if (isHttpOnly)
        {
            buf.append("; HttpOnly");
        }
        headerBuf.append(buf);
    }
    
    public static int maybeQuote2(int version, StringBuffer buf, String value)
    {
        return maybeQuote2(version, buf, value, false);
    }
    
    public static int maybeQuote2(int version, StringBuffer buf, String value,
            boolean allowVersionSwitch)
    {
        if (value == null || value.length() == 0)
        {
            buf.append("\"\"");
        }
        else
        {
            buf.append(value);
        }
        return 0;
    }
}
