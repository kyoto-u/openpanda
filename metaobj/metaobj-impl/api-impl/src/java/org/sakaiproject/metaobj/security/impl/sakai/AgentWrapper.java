/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.2/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/security/impl/sakai/AgentWrapper.java $
 * $Id: AgentWrapper.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008, 2009 The Sakai Foundation
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

package org.sakaiproject.metaobj.security.impl.sakai;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.model.*;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.util.ResourceLoader;

import java.util.ArrayList;
import java.util.List;


public class AgentWrapper extends IdentifiableObject implements Agent {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private User sakaiUser = null;
   private Id id = null;
   private Id eid = null;
   private StructuredArtifact profile = null;
   private SecurityBase securityBase;
   private ResourceLoader rb = new ResourceLoader("org/sakaiproject/metaobj/messages");

   public AgentWrapper() {
   	logger.info("An AgentWrapper was created without any parameters");
   }
   public AgentWrapper(Id id, Id eid, User sakaiUser, StructuredArtifact profile, SecurityBase securityBase) {
      this.id = id;
      this.eid = eid;
      this.sakaiUser = sakaiUser;
      this.profile = profile;
      this.securityBase = securityBase;
   }

   public Id getId() {
      return id;
   }
   
   public Id getEid() {
      return eid;
   }

   public Artifact getProfile() {
      return profile;
   }

   public void setProfile(StructuredArtifact profile) {
      this.profile = profile;
   }

   public Object getProperty(String key) {
      return profile.get(key);
   }

   public String getDisplayName() {
      if (sakaiUser != null)
         return sakaiUser.getDisplayName();
      else if (eid != null)
         return eid.getValue();
      else if (id != null)
         return id.getValue();
      return rb.getString("user_not_found");
   }

   public boolean isInRole(String role) {
      return role.equals(sakaiUser.getType());
   }

   public boolean isInitialized() {
      return true;
   }

   public String getRole() {
      return sakaiUser.getType();
   }

   public List getWorksiteRoles(String worksiteId) {
      List returned = new ArrayList();

      try {
         AuthzGroup siteRealm = AuthzGroupService.getAuthzGroup("/site/" +
               worksiteId);

         Role role = siteRealm.getUserRole(getSakaiUser().getId());

         if (role != null) {
            returned.add(getSecurityBase().convertRole(role, siteRealm));
         }
      }
      catch (GroupNotDefinedException e) {
         logger.error("", e);
         throw new OspException(e);
      }

      return returned;
   }

   public List getWorksiteRoles() {
      Placement currentPlacement = ToolManager.getCurrentPlacement();

      if (currentPlacement == null) {
         return new ArrayList();
      }

      return getWorksiteRoles(currentPlacement.getContext());
   }

   public boolean isRole() {
      return false;
   }

   public User getSakaiUser() {
      return sakaiUser;
   }

   public org.sakaiproject.metaobj.shared.mgt.AgentManager getAgentManager() {
      return (org.sakaiproject.metaobj.shared.mgt.AgentManager) ComponentManager.getInstance().get("agentManager");
   }

   public SecurityBase getSecurityBase() {
      return securityBase;
   }

   /**
    * Returns the name of this principal.
    *
    * @return the name of this principal.
    */
   public String getName() {
      return getDisplayName();
   }
	
	public String getPassword() {
		return null; // not implemented
	}
}
