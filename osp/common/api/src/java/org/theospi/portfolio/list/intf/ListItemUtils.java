package org.theospi.portfolio.list.intf;

/**
 * Created by IntelliJ IDEA.
 * User: bbiltimier
 * Date: Mar 13, 2006
 * Time: 10:52:06 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ListItemUtils {

    public String formatMessage(String key, Object[] args); 
    
    public boolean lookUpInBundle(String field);

}
