/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/security/AnonymousAgent.java $
 * $Id: AnonymousAgent.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008, 2009 The Sakai Foundation
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

package org.sakaiproject.metaobj.security;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.metaobj.shared.mgt.IdManagerImpl;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 26, 2004
 * Time: 10:36:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class AnonymousAgent implements Agent {
   public static Id ANONYMOUS_AGENT_ID = new IdManagerImpl().getId("anonymous");

   public Id getId() {
      return ANONYMOUS_AGENT_ID;
   }
   
   public Id getEid() {
      return ANONYMOUS_AGENT_ID;
   }

   public Artifact getProfile() {
      return null;
   }

   public void setProfile(Artifact profile) {

   }

   public Object getProperty(String key) {
      return null;
   }

   public String getDisplayName() {
      return "anonymous";
   }

   public boolean isInRole(String role) {
      return role.equals(Agent.ROLE_ANONYMOUS);
   }

   public boolean isInitialized() {
      return true;
   }

   public String getRole() {
      return Agent.ROLE_ANONYMOUS;
   }

   public List getWorksiteRoles(String worksiteId) {
      return new ArrayList();
   }

   public List getWorksiteRoles() {
      return new ArrayList();
   }

   public boolean isRole() {
      return false;
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
