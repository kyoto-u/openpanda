package org.theospi.portfolio.security.model;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 28, 2006
 * Time: 4:01:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class CrossRealmRoleWrapper {

   private Map siteTypeRoles;

   public Map getSiteTypeRoles() {
      return siteTypeRoles;
   }

   public void setSiteTypeRoles(Map siteTypeRoles) {
      this.siteTypeRoles = siteTypeRoles;
   }
}
