/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api-impl/src/java/org/theospi/portfolio/security/impl/sakai/WorksiteAwareAuthorizationFacade.java $
* $Id:WorksiteAwareAuthorizationFacade.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
package org.theospi.portfolio.security.impl.sakai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.security.impl.sakai.SecurityBase;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspRole;
import org.sakaiproject.thread_local.api.ThreadLocalManager;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.site.api.SiteService.SelectionType;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.impl.simple.SimpleAuthorizationFacade;

public class WorksiteAwareAuthorizationFacade extends SimpleAuthorizationFacade {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private AgentManager agentManager = null;
   private SecurityBase sakaiSecurityBase;
   private ThreadLocalManager threadLocalManager;
   private static final String AUTHZ_GROUPS_LIST =
         "org.theospi.portfolio.security.impl.sakai.WorksiteAwareAuthorizationFacade.authzGroups";


   /**
    * @param agent
    * @param function
    * @param id
    */
   public void createAuthorization(Agent agent, String function, Id id) {
      // don't want to include roles here otherwise can't explicitly create authz for both role and user
      // see bug http://cvs.theospi.org:14443/jira/browse/OSP-459
      Authorization auth = getAuthorization(agent, function, id, false);
      if (auth == null) {
         auth = new Authorization(agent, function, id);
      }

      getHibernateTemplate().saveOrUpdate(auth);
   }

   public void deleteAuthorization(Agent agent, String function, Id id) {
      Authorization auth = getAuthorization(agent, function, id, false);
      if (auth != null) {
         getHibernateTemplate().delete(auth);
      }
   }

   public void pushAuthzGroups(Collection authzGroups) {
      List authzGroupList = getAuthzGroupsList();
      authzGroupList.addAll(authzGroups);
   }

   public void pushAuthzGroups(String siteId) {
      getAuthzGroupsList().add(siteId);
   }

   protected Authorization getAuthorization(Agent agent, String function, Id id, boolean includeRoles) {
      // Try the direct user check first
      Authorization userAuthz = super.getAuthorization(agent, function, id);
      if (userAuthz == null && includeRoles) {

         if (logger.isDebugEnabled())
            logger.debug("userAuthz was null, so checking roles, agent, function, id: " + agent.getDisplayName() + "(" + agent.getId().getValue() + "), " + function + ", " + id.getValue());

         // Retrieve the set of object-level grants for this object and function.
         // Multiple roles in a realm may be permitted, so we use a realm->roles hash.
         HashMap<String, List<String>> azgs = new HashMap<String, List<String>>();
         for (Authorization authz : (List<Authorization>) findByFunctionId(function, id)) {
            Agent a = authz.getAgent();
            if (a instanceof OspRole) {
               OspRole role = (OspRole) a;
               String realmId = role.getSakaiRealm().getId();
               if (!azgs.containsKey(realmId)) {
                  azgs.put(realmId, new ArrayList<String>());
               }
               
               if (logger.isDebugEnabled())
                  logger.debug("Building object grant list, adding role for azg: " + role + ", " + realmId);

               azgs.get(realmId).add(role.getRoleName());
            }
         }

         // Compare the permitted roles against the ones granted to this user.
         if (azgs.size() > 0) {
            Map<String, String> grants = AuthzGroupService.getUserRoles(agent.getId().getValue(), azgs.keySet());
            for (Map.Entry entry : grants.entrySet()) {
               if (azgs.containsKey(entry.getKey())) {
                  // Grab the list of permitted roles for this realm, since the user is a member, and compare.
                  List<String> needed = azgs.get(entry.getKey());
                  String granted = entry.getValue().toString();
                  if (needed.contains(granted)) {
                     if (logger.isDebugEnabled())
                        logger.debug("Generating realmRole authz - (realmId, role), function, id: (" + entry.getKey() + ", " + granted + "), " + function + ", " + id);
                     
                     return new Authorization(agentManager.getRealmRole(granted, entry.getKey().toString()), function, id);
                  }
               }
            }
         }
      }

      // If we've fallen through, return whatever the result of the user check
      return userAuthz;
   }

   protected Authorization getAuthorization(Agent agent, String function, Id id) {
      return getAuthorization(agent, function, id, true);
   }

   protected List findByAgent(Agent agent) {
      Set roles = getAgentRoles(agent);

      List authzs = new ArrayList();

      for (Iterator i=roles.iterator();i.hasNext();) {
         Agent next = (Agent)i.next();
         if (next != null) {
            authzs.addAll(super.findByAgent((Agent)i.next()));
         }
      }

      authzs.addAll(super.findByAgent(agent));
      return authzs;
   }

   protected List findByAgentFunction(Agent agent, String function) {
      Set roles = getAgentRoles(agent);

      List authzs = new ArrayList();

      for (Iterator i=roles.iterator();i.hasNext();) {
         Agent next = (Agent)i.next();

         if (next != null) {
            authzs.addAll(super.findByAgentFunction(
               next, function));
         }
      }

      authzs.addAll(super.findByAgentFunction(agent, function));
      return authzs;
   }

   protected List findByAgentId(Agent agent, Id id) {
      Set roles = getAgentRoles(agent);

      List authzs = new ArrayList();

      for (Iterator i=roles.iterator();i.hasNext();) {
         Agent next = (Agent)i.next();
         if (next != null) {
            authzs.addAll(super.findByAgentId(
               (Agent)i.next(), id));
         }
      }

      authzs.addAll(super.findByAgentId(agent, id));
      return authzs;
   }

   protected Set getAgentRoles(Agent agent) {
      Set agentRoles = new HashSet();
      List authzGroups = getAuthzGroupsList();

      for (Iterator i = authzGroups.iterator();i.hasNext();) {
         String site = (String)i.next();
         agentRoles.addAll(agent.getWorksiteRoles(site));
      }

      // If this is a user's My Workspace, aggregate roles from all member sites
      if ( ToolManager.getCurrentPlacement() != null && SiteService.isUserSite(ToolManager.getCurrentPlacement().getContext()) ) {
         List allSites = SiteService.getSites(SelectionType.ACCESS, null, null,
                                            null, null, null);
         allSites.addAll ( SiteService.getSites(SelectionType.UPDATE, null, null,
                                            null, null, null) );
         
         Set<Site> siteSet = new HashSet<Site>(allSites);
         for (Site site : siteSet) {
       	  agentRoles.addAll(agent.getWorksiteRoles( site.getId() ));
         }
         
         // finally, add user agent for user-based aurhorizations
         agentRoles.add(agent);
      }

      // Otherwise just get roles from current worksite
      else  {
         agentRoles.addAll(agent.getWorksiteRoles());
      }
         
      return agentRoles;
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public SecurityBase getSakaiSecurityBase() {
      return sakaiSecurityBase;
   }

   public void setSakaiSecurityBase(SecurityBase sakaiSecurityBase) {
      this.sakaiSecurityBase = sakaiSecurityBase;
   }

   public ThreadLocalManager getThreadLocalManager() {
      return threadLocalManager;
   }

   public void setThreadLocalManager(ThreadLocalManager threadLocalManager) {
      this.threadLocalManager = threadLocalManager;
   }

   protected List getAuthzGroupsList() {
      List returned = (List)threadLocalManager.get(AUTHZ_GROUPS_LIST);

      if (returned == null) {
         returned = new ArrayList();
         threadLocalManager.set(AUTHZ_GROUPS_LIST, returned);
      }
      return returned;
   }
}
