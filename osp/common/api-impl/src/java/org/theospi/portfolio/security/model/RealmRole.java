package org.theospi.portfolio.security.model;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 27, 2006
 * Time: 3:42:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class RealmRole {

   private String role;
   private boolean maintain = false;

   public String getRole() {
      return role;
   }

   public void setRole(String role) {
      this.role = role;
   }

   public boolean isMaintain() {
      return maintain;
   }

   public void setMaintain(boolean maintain) {
      this.maintain = maintain;
   }
}
