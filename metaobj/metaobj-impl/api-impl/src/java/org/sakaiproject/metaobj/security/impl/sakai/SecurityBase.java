/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/security/impl/sakai/SecurityBase.java $
 * $Id: SecurityBase.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
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

package org.sakaiproject.metaobj.security.impl.sakai;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.WritableObjectHome;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.user.api.User;

public class SecurityBase {
   protected final transient Log logger = LogFactory.getLog(getClass());
   private IdManager ospiIdManager = null;
   private WritableObjectHome agentHome = null;

   protected Agent morphAgent(User sakaiUser) {
      Id ospiId = getOspiIdManager().getId(sakaiUser.getId());
      Id ospiEid = getOspiIdManager().getId(sakaiUser.getEid());
      //StructuredArtifact profile = (StructuredArtifact)agentHome.createInstance();

      //profile.put("email", sakaiUser.getEmail());

      //return new AgentWrapper(ospiId,  sakaiUser, profile, this);
      return new AgentWrapper(ospiId, ospiEid, sakaiUser, null, this);
   }


   public IdManager getOspiIdManager() {
      return ospiIdManager;
   }

   public void setOspiIdManager(IdManager ospiIdManager) {
      this.ospiIdManager = ospiIdManager;
   }


   public WritableObjectHome getAgentHome() {
      return agentHome;
   }

   public void setAgentHome(WritableObjectHome agentHome) {
      this.agentHome = agentHome;
   }

   public RoleWrapper convertRole(Role sakaiRole, AuthzGroup siteRealm) {
      if (sakaiRole == null) {
         return null;
      }
      Id roleId = getOspiIdManager().getId(siteRealm.getId() + "/" + sakaiRole.getId());

      //TODO using the same value for id and eid
      return new RoleWrapper(roleId, roleId, sakaiRole, siteRealm);
   }
}
