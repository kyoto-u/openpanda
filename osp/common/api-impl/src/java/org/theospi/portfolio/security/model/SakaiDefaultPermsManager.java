/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api-impl/src/java/org/theospi/portfolio/security/model/SakaiDefaultPermsManager.java $
* $Id:SakaiDefaultPermsManager.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2006, 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*
**********************************************************************************/
package org.theospi.portfolio.security.model;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.FunctionManager;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;
import org.theospi.portfolio.security.DefaultRealmManager;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 8, 2006
 * Time: 4:04:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class SakaiDefaultPermsManager {

   private Map defaultPermissions;
   private List functions;
   private FunctionManager functionManager;
   private AuthzGroupService authzGroupService;
   private String prefix;
   private List realmManagers;
   private boolean autoDdl = true;
   
   protected final transient Log logger = LogFactory.getLog(getClass());

   public void init() {
      logger.info("init()");
      // need to register functions... set defaults on the ones that are not there
      Session sakaiSession = SessionManager.getCurrentSession();
      String userId = sakaiSession.getUserId();

      try {
         sakaiSession.setUserId("admin");
         sakaiSession.setUserEid("admin");

         if (getPrefix() != null) {
            List currentFunctions = getFunctionManager().getRegisteredFunctions(getPrefix());

            for (Iterator i=getFunctions().iterator();i.hasNext();) {
               String function = (String) i.next();
               if (currentFunctions.contains(function)) {
                  i.remove();
               }
               else {
                  getFunctionManager().registerFunction(function);
               }
            }
         }

         if (isAutoDdl()) {
            // set the defaults for anything in functions
            for (Iterator i=getDefaultPermissions().entrySet().iterator();i.hasNext();){
               Map.Entry entry = (Map.Entry) i.next();
               processRealm((String)entry.getKey(), (Map)entry.getValue());
            }
         }
   } finally {
      sakaiSession.setUserEid(userId);
      sakaiSession.setUserId(userId);
   }

   }

   protected void processRealm(String realm, Map defaultPerms) {
      try {
         AuthzGroup group = getAuthzGroupService().getAuthzGroup(realm);
         boolean isNew = isRealmNew(group);
         for (Iterator i=defaultPerms.entrySet().iterator();i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            Role role = group.getRole((String) entry.getKey());
            setupRole(role, (List)entry.getValue(), isNew);
         }
         getAuthzGroupService().save(group);
      }
      catch (GroupNotDefinedException e) {
         throw new RuntimeException(e);
      }
      catch (AuthzPermissionException e) {
         throw new RuntimeException(e);
      }
   }

   protected boolean isRealmNew(AuthzGroup group) {
      for (Iterator i=getRealmManagers().iterator();i.hasNext();) {
         DefaultRealmManager manager = (DefaultRealmManager) i.next();
         if (manager.getNewRealmName().equals(group.getId())) {
            return manager.isNewlyCreated();
         }
      }

      return false;
   }

   protected void setupRole(Role role, List functions, boolean isNew) {
      for (Iterator i=functions.iterator();i.hasNext();) {
         String func = (String) i.next();
         if (isNew || getFunctions().contains(func)) {
            role.allowFunction(func);
         }
      }
   }

   public Map getDefaultPermissions() {
      return defaultPermissions;
   }

   public void setDefaultPermissions(Map defaultPermissions) {
      this.defaultPermissions = defaultPermissions;
   }

   public List getFunctions() {
      return functions;
   }

   public void setFunctions(List functions) {
      this.functions = functions;
   }

   public FunctionManager getFunctionManager() {
      return functionManager;
   }

   public void setFunctionManager(FunctionManager functionManager) {
      this.functionManager = functionManager;
   }

   public String getPrefix() {
      return prefix;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public AuthzGroupService getAuthzGroupService() {
      return authzGroupService;
   }

   public void setAuthzGroupService(AuthzGroupService authzGroupService) {
      this.authzGroupService = authzGroupService;
   }

   public List getRealmManagers() {
      return realmManagers;
   }

   public void setRealmManagers(List realmManagers) {
      this.realmManagers = realmManagers;
   }

   public boolean isAutoDdl() {
      return autoDdl;
   }

   public void setAutoDdl(boolean autoDdl) {
      this.autoDdl = autoDdl;
   }
}
