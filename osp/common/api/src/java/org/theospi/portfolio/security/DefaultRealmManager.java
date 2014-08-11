package org.theospi.portfolio.security;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 27, 2006
 * Time: 4:03:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DefaultRealmManager {

   public boolean isNewlyCreated();

   String getNewRealmName();
}
