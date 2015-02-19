/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/security/mgt/impl/PermissionManagerImpl.java $
 * $Id: PermissionManagerImpl.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.metaobj.security.mgt.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.metaobj.security.Authorization;
import org.sakaiproject.metaobj.security.AuthorizationFacade;
import org.sakaiproject.metaobj.security.mgt.PermissionManager;
import org.sakaiproject.metaobj.security.mgt.ToolPermissionManager;
import org.sakaiproject.metaobj.security.model.Permission;
import org.sakaiproject.metaobj.security.model.PermissionsEdit;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.metaobj.shared.model.OspRole;
import org.sakaiproject.site.api.Site;

public class PermissionManagerImpl implements PermissionManager {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private AgentManager agentManager;
   private AuthorizationFacade authzManager;

   private Map tools;

   public List getWorksiteRoles(PermissionsEdit edit) {
      try {
         AuthzGroup siteRealm = AuthzGroupService.getAuthzGroup("/site/" +
               edit.getSiteId());

         Set roles = siteRealm.getRoles();
         List returned = new ArrayList();
         returned.addAll(siteRealm.getRoles());
         return returned;
      }
      catch (GroupNotDefinedException e) {
         logger.error("", e);
         throw new OspException(e);
      }
   }

   public PermissionsEdit fillPermissions(PermissionsEdit edit) {
      edit.setPermissions(new ArrayList());

      edit = fillPermissionsInternal(edit, edit.getQualifier(), false);

      ToolPermissionManager mgr = getToolManager(edit);
      List quals = mgr.getReadOnlyQualifiers(edit);

      for (Iterator i = quals.iterator(); i.hasNext();) {
         Id qualifier = (Id) i.next();
         fillPermissionsInternal(edit, qualifier, true);
      }

      return edit;
   }

   protected PermissionsEdit fillPermissionsInternal(PermissionsEdit edit, Id qualifier, boolean readOnly) {

      try {
         AuthzGroup siteRealm = AuthzGroupService.getAuthzGroup("/site/" +
               edit.getSiteId());

         Set roles = siteRealm.getRoles();

         for (Iterator i = roles.iterator(); i.hasNext();) {
            Role role = (Role) i.next();
            Agent currentRole = getAgentManager().getWorksiteRole(role.getId(), edit.getSiteId());
            List authzs = getAuthzManager().getAuthorizations(currentRole, null, qualifier);

            for (Iterator j = authzs.iterator(); j.hasNext();) {
               Authorization authz = (Authorization) j.next();
               edit.getPermissions().add(new Permission(currentRole, authz.getFunction(), readOnly));
            }
         }
      }
      catch (GroupNotDefinedException e) {
         logger.error("", e);
         throw new OspException(e);
      }

      return edit;
   }

   public void updatePermissions(PermissionsEdit edit) {
      AuthorizationFacade manager = getAuthzManager();
      List origPermissions = null;

      PermissionsEdit orig = (PermissionsEdit) edit.clone();
      orig = fillPermissions(orig);
      origPermissions = orig.getPermissions();

      for (Iterator i = edit.getPermissions().iterator(); i.hasNext();) {
         Permission perm = (Permission) i.next();

         if (origPermissions.contains(perm)) {
            origPermissions.remove(perm);
         }
         else if (!perm.isReadOnly()) {
            manager.createAuthorization(perm.getAgent(), perm.getFunction(), edit.getQualifier());
         }
      }

      for (Iterator i = origPermissions.iterator(); i.hasNext();) {
         Permission perm = (Permission) i.next();

         manager.deleteAuthorization(perm.getAgent(), perm.getFunction(), edit.getQualifier());
      }

   }

   public void duplicatePermissions(Id srcQualifier, Id targetQualifier, Site newSite) {
      AuthorizationFacade manager = getAuthzManager();
      List origPermissions = manager.getAuthorizations(null, null, srcQualifier);

      for (Iterator i = origPermissions.iterator(); i.hasNext();) {
         Authorization authz = (Authorization) i.next();
         Agent agent = authz.getAgent();
         if (newSite != null && agent instanceof OspRole) {
            agent = getAgentManager().getTempWorksiteRole(((OspRole) agent).getRoleName(), newSite.getId());
         }

         if (agent != null) {
            manager.createAuthorization(agent, authz.getFunction(), targetQualifier);
         }
      }
   }

   public List getAppFunctions(PermissionsEdit edit) {
      ToolPermissionManager mgr = getToolManager(edit);

      return mgr.getFunctions(edit);
   }

   protected ToolPermissionManager getToolManager(PermissionsEdit edit) {
      return (ToolPermissionManager) getTools().get(edit.getName());
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public Map getTools() {
      return tools;
   }

   public void setTools(Map tools) {
      this.tools = tools;
   }
}
