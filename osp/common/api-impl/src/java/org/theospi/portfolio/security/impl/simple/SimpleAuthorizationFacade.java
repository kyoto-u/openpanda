/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api-impl/src/java/org/theospi/portfolio/security/impl/simple/SimpleAuthorizationFacade.java $
* $Id:SimpleAuthorizationFacade.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.security.impl.simple;

import java.util.*;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspRole;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.authz.api.SecurityService;
import org.springframework.orm.hibernate3.HibernateObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.AuthorizationFailedException;
import org.theospi.portfolio.shared.model.OspException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 19, 2004
 * Time: 4:55:05 PM
 * To change this template use File | Settings | File Templates.
 * @jira OSP-323 PostgreSQL Table Creation
 */
public class SimpleAuthorizationFacade extends HibernateDaoSupport implements AuthorizationFacade {

   private AuthenticationManager authManager = null;
   private SecurityService securityService = null;
   private org.sakaiproject.metaobj.security.AuthorizationFacade shim;
	
   // OSP 2.5 Users should enable in sakai.properties (osp.upgrade25 = true)
   private boolean DEFAULT_UPGRADE25 = false;
   
   public void init() {
      boolean upgradeTo25 = ServerConfigurationService.getBoolean("osp.upgrade25", DEFAULT_UPGRADE25);
      if (upgradeTo25) {
         org.sakaiproject.tool.api.Session sakaiSession = SessionManager.getCurrentSession();
         String userId = sakaiSession.getUserId();
         sakaiSession.setUserId("admin");
         sakaiSession.setUserEid("admin");
         try {
            processUpgradeTo25();
         }
         finally {
            sakaiSession.setUserEid(userId);
            sakaiSession.setUserId(userId);
         }
      }
   }

   protected void processUpgradeTo25() {
      List authzList = getHibernateTemplate().loadAll(Authorization.class);
      Map<String, List<Authorization>> qualifierAuthz = new Hashtable<String, List<Authorization>>();
      
      for (Iterator<Authorization> i = authzList.iterator();i.hasNext();) {
         Authorization authz = i.next();
         List<Authorization> current = qualifierAuthz.get(authz.getQualifier().getValue());
         if (current == null) {
            current = new ArrayList<Authorization>();
            qualifierAuthz.put(authz.getQualifier().getValue(), current);
         }
         current.add(authz);
      }
      
      for (Iterator<Map.Entry<String,List<Authorization>>> i=
         qualifierAuthz.entrySet().iterator();i.hasNext();) {
         Map.Entry<String,List<Authorization>> entry = i.next();
         processUpgradeTo25Qualifier(entry.getKey(), entry.getValue());
      }
   }

   protected void processUpgradeTo25Qualifier(String qualifier, List<Authorization> authorizations) {
      try {
         Site site = SiteService.getSite(qualifier);
         processUpgradeTo25Site(site, authorizations);
         return;
      } catch (IdUnusedException e) {
         // ignore, this just isn't a site
      }
      
      // check if this is a placement
      ToolConfiguration tool = SiteService.findTool(qualifier);
      
      if (tool != null) {
         processUpgradeTo25Site(tool.getContainingPage().getContainingSite(), authorizations);
      }
   }

   protected void processUpgradeTo25Site(Site site, List<Authorization> authorizations) {
      try {
         processUpgradeTo25Group(AuthzGroupService.getAuthzGroup(site.getReference()), authorizations);
      } catch (GroupNotDefinedException e) {
         throw new OspException(e);
      }
   }
   
   protected void processUpgradeTo25Group(AuthzGroup group, List<Authorization> authorizations) {
      
      for (Iterator<Authorization> i=authorizations.iterator();i.hasNext();) {
         Authorization authz = i.next();
         if (authz.getAgent() instanceof OspRole) {
            Role role = group.getRole(((OspRole)authz.getAgent()).getRoleName());
            role.allowFunction(authz.getFunction());
         }
         else {
            i.remove();
         }
      }

      try {
         AuthzGroupService.save(group);
         getHibernateTemplate().deleteAll(authorizations);
      } catch (org.sakaiproject.authz.api.AuthzPermissionException e) {
         throw new OspException(e);
      } catch (org.sakaiproject.authz.api.GroupNotDefinedException e) {
         throw new OspException(e);
      }
   }

   public void checkPermission(String function, Id id) throws AuthorizationFailedException {
      if (!isAuthorized(function, id)) {
         throw new AuthorizationFailedException(function, id);
      }
   }

   public void checkPermission(Agent agent, String function, Id id) throws AuthorizationFailedException {
      if (!isAuthorized(agent, function, id)) {
         throw new AuthorizationFailedException(agent, function, id);
      }
   }

   /**
    * @param function
    * @param id
    * @return
    */
   public boolean isAuthorized(String function, Id id) {
      return isAuthorized(getAuthManager().getAgent(), function, id);
   }

   /**
    * @param agent
    * @param function
    * @param id
    * @return
    */
   public boolean isAuthorized(Agent agent, String function, Id id) {
	  boolean isSuperUser = getSecurityService().isSuperUser(agent.getId().getValue());
 	  if (isSuperUser)
 		  return isSuperUser;
 	  
      return (getAuthorization(agent, function, id) != null);

   }

   /**
    * @jira OSP-323 PostgreSQL Table Creation
    */
   protected Authorization getAuthorization(Agent agent, String function, Id id) {
      if (id == null) {
         throw new NullPointerException("The id was null while getting the authorization");
      }
      if (agent == null || agent.getId() == null) {
         throw new NullPointerException("The agent was null while getting the authorization");
      }

      if (id.getValue() == null) {
         return null;
      }
      
      Placement placement = ToolManager.getCurrentPlacement();
      
      if (placement != null && 
         (placement.getContext().equals(id.getValue()) || placement.getId().equals(id.getValue()))) {
         if (shim.isAuthorized(agent, function, id)) {
            return new Authorization(agent, function, id);
         }
         else {
            return null;
         }
      }
      
      Site site = findSite(id.getValue());
      if (site != null) {
         if (site.isAllowed(agent.getId().getValue(), function)) {
            return new Authorization(agent, function, id);
         }
         else {
            return null;
         }
      }
      
      ToolConfiguration toolConfig = SiteService.findTool(id.getValue());
      
      if (toolConfig != null) {
         site = toolConfig.getContainingPage().getContainingSite();
         if (site.isAllowed(agent.getId().getValue(), function)) {
            return new Authorization(agent, function, id);
         }
         else {
            return null;
         }
      }
      
      try {
         return (Authorization) safePopList(getHibernateTemplate().findByNamedQuery("getAuthorization",
            new Object[]{agent.getId().getValue(), function, id.getValue()}));
      } catch (HibernateObjectRetrievalFailureException e) {
         logger.error("",e);
         throw new OspException(e);
      }
   }

   protected Site findSite(String siteId) {
      try {
         return SiteService.getSite(siteId);
      } catch (IdUnusedException e) {
         // ignore... the id must not be a site...
         return null;
      }
   }    

   protected Object safePopList(List list) {
      if (list == null) {
         return null;
      }
      if (list.size() == 0) {
         return null;
      }
      return list.get(0);
   }

   /**
    * at least one param must be non-null
    *
    * @param agent
    * @param function
    * @param id
    * @return
    */
   public List getAuthorizations(Agent agent, String function, Id id) {
      List returned = null;

      if (agent != null && function != null && id != null) {
         returned = new ArrayList();
         Authorization authz = getAuthorization(agent, function, id);

         if (authz != null) {
            returned.add(authz);
         }
      }
      // agent stuff
      else if (agent != null && function != null && id == null) {
         returned = findByAgentFunction(agent, function);
      } else if (agent != null && function == null && id != null) {
         returned = findByAgentId(agent, id);
      } else if (agent != null && function == null && id == null) {
         returned = findByAgent(agent);
      }
      // function
      else if (agent == null && function != null && id != null) {
         returned = findByFunctionId(function, id);
      } else if (agent == null && function != null && id == null) {
         returned = findByFunction(function);
      }
      // id
      else if (agent == null && function == null && id != null) {
         returned = findById(id);
      }

      return correctList(returned);
   }

   protected List correctList(List returned) {
	   if(returned !=null){
		   for (Iterator i=returned.iterator();i.hasNext();) {
			   Authorization authz = (Authorization)i.next();
			   if (authz.getAgent() == null) {
				   i.remove();
			   }
		   }
	   }else{
		   //return an empty list
		   returned = new ArrayList();
	   }
	   
      return returned;
   }

   /**
    * @jira OSP-323 PostgreSQL Table Creation
    */
   protected List findById(Id id) {
      return getHibernateTemplate().findByNamedQuery("byId",
         new Object[]{id.getValue()});
   }

   /**
    * @jira OSP-323 PostgreSQL Table Creation
    */
   protected List findByFunction(String function) {
      return getHibernateTemplate().findByNamedQuery("byFunction",
         new Object[]{function});
   }

   /**
    * @jira OSP-323 PostgreSQL Table Creation
    */
   protected List findByFunctionId(String function, Id id) {
      return getHibernateTemplate().findByNamedQuery("byFunctionAndId",
         new Object[]{function, id.getValue()});
   }

   /**
    * @jira OSP-323 PostgreSQL Table Creation
    */
   protected List findByAgent(Agent agent) {
      return getHibernateTemplate().findByNamedQuery("byAgent",
         new Object[]{agent.getId().getValue()});
   }

   /**
    * @jira OSP-323 PostgreSQL Table Creation
    */
   protected List findByAgentId(Agent agent, Id id) {
      return getHibernateTemplate().findByNamedQuery("byAgentAndId",
         new Object[]{agent.getId().getValue(), id.getValue()});
   }

   /**
    * @jira OSP-323 PostgreSQL Table Creation
    */
   protected List findByAgentFunction(Agent agent, String function) {
      return getHibernateTemplate().findByNamedQuery("byAgentAndFunction",
         new Object[]{agent.getId().getValue(), function});
   }


   /**
    * @param agent
    * @param function
    * @param id
    */
   public void createAuthorization(Agent agent, String function, Id id) {
      Authorization auth = getAuthorization(agent, function, id);
      if (auth == null) {
         auth = new Authorization(agent, function, id);
      }

      getHibernateTemplate().saveOrUpdate(auth);
   }

   public void deleteAuthorization(Agent agent, String function, Id id) {
      Authorization auth = getAuthorization(agent, function, id);
      if (auth != null) {
         getHibernateTemplate().delete(auth);
      }
   }

   public void deleteAuthorizations(Id qualifier) {
      getHibernateTemplate().deleteAll(findById(qualifier));
   }

   public void pushAuthzGroups(Collection authzGroups) {
      // does nothing... this impl does not care about groups
   }

   public void pushAuthzGroups(String siteId) {
      // does nothing... this impl does not care about groups
   }

   public AuthenticationManager getAuthManager() {
      return authManager;
   }

   public void setAuthManager(AuthenticationManager authManager) {
      this.authManager = authManager;
   }

   public void setSecurityService(SecurityService securityService) {
	   this.securityService = securityService;
   }

   public SecurityService getSecurityService() {
	   return securityService;
   }

   public org.sakaiproject.metaobj.security.AuthorizationFacade getShim() {
      return shim;
   }

   public void setShim(org.sakaiproject.metaobj.security.AuthorizationFacade shim) {
      this.shim = shim;
   }
}
