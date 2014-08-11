package org.theospi.portfolio.security.model;

import java.util.Iterator;
import java.util.List;

import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupAlreadyDefinedException;
import org.sakaiproject.authz.api.GroupIdInvalidException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.RoleAlreadyDefinedException;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;
import org.theospi.portfolio.security.DefaultRealmManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 27, 2006
 * Time: 2:47:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultRealmManagerImpl implements DefaultRealmManager {

   protected final transient Log logger = LogFactory.getLog(getClass());

   private AuthzGroupService authzGroupService;
   private String newRealmName;
   private List roles;
   private boolean newlyCreated;
   private boolean recreate = false;
   private boolean autoDdl = true;

   public void init() {
      logger.info("init()");
      
      if (isAutoDdl()) {
         Session sakaiSession = SessionManager.getCurrentSession();
         String userId = sakaiSession.getUserId();
         try {
            sakaiSession.setUserId("admin");
            sakaiSession.setUserEid("admin");
            try {
               AuthzGroup group = getAuthzGroupService().getAuthzGroup(newRealmName);
               if (group != null) {
                  if (recreate){
                     getAuthzGroupService().removeAuthzGroup(group);
                  }
                  else {
                     newlyCreated = false;
                     return;
                  }
               }
            } catch (GroupNotDefinedException e) {
               // no worries... must not be created yet.
            } catch (AuthzPermissionException e) {
               logger.error("Failed to recreate realm.", e);
               newlyCreated = false;
               return;
            }
   
            newlyCreated = true;
   
            try {
               AuthzGroup newRealm = getAuthzGroupService().addAuthzGroup(newRealmName);
               addRoles(newRealm);
               getAuthzGroupService().save(newRealm);
            } catch (GroupNotDefinedException e) {
               throw new RuntimeException(e);
            } catch (AuthzPermissionException e) {
               throw new RuntimeException(e);
            } catch (GroupAlreadyDefinedException e) {
               throw new RuntimeException(e);
            } catch (GroupIdInvalidException e) {
               throw new RuntimeException(e);
            } catch (RoleAlreadyDefinedException e) {
                 throw new RuntimeException(e);
         }
         } finally {
            sakaiSession.setUserId(userId);
            sakaiSession.setUserEid(userId);
         }
      }
   }

   protected void addRoles(AuthzGroup newRealm) throws RoleAlreadyDefinedException {
      for (Iterator i=getRoles().iterator();i.hasNext();) {
         Object roleInfo = i.next();
         if (roleInfo instanceof String) {
            newRealm.addRole((String) roleInfo);
         }
         else {
            RealmRole role = (RealmRole) roleInfo;
            Role newRole = newRealm.addRole(role.getRole());
            if (role.isMaintain()) {
               newRealm.setMaintainRole(newRole.getId());
            }
         }
      }
   }

   public AuthzGroupService getAuthzGroupService() {
      return authzGroupService;
   }

   public void setAuthzGroupService(AuthzGroupService authzGroupService) {
      this.authzGroupService = authzGroupService;
   }

   public String getNewRealmName() {
      return newRealmName;
   }

   public void setNewRealmName(String newRealmName) {
      this.newRealmName = newRealmName;
   }

   public List getRoles() {
      return roles;
   }

   public void setRoles(List roles) {
      this.roles = roles;
   }

   public boolean isNewlyCreated() {
      return newlyCreated;
   }

   public void setNewlyCreated(boolean newlyCreated) {
      this.newlyCreated = newlyCreated;
   }

   public boolean isRecreate() {
      return recreate;
   }

   public void setRecreate(boolean recreate) {
      this.recreate = recreate;
   }

   public boolean isAutoDdl() {
      return autoDdl;
   }

   public void setAutoDdl(boolean autoDdl) {
      this.autoDdl = autoDdl;
   }
}

