/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/DeletePresentationController.java $
* $Id:DeletePresentationController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2008, 2009 The Sakai Foundation
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
package org.theospi.portfolio.presentation.support;

import java.util.List;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;

/**
 ** Wrapper for Agents that adds worksiteName for roles
 **/
public class AgentWrapper implements Agent {
   String worksiteName = null;
   Agent agent = null;

   public AgentWrapper( Agent agent, String worksiteName ) {
      this.agent = agent;
      this.worksiteName = worksiteName;
   }
      
   public Id getId() {
      return agent.getId();
   }
      
   public Id getEid() {
      return agent.getEid();
   }
      
   public Artifact getProfile() {
      return agent.getProfile();
   }

   public Object getProperty(String key) {
      return agent.getProperty(key);
   }

   public String getDisplayName() {
      return agent.getDisplayName()+" ("+worksiteName+")";
   }
      
   public String getName() {
      return this.getDisplayName();
   }
      
   public boolean isInRole(String role) {
      return agent.isInRole(role);
   }

   public boolean isInitialized() {
      return agent.isInitialized();
   }

   public String getRole() {
      return agent.getRole();
   }

   public List getWorksiteRoles(String worksiteId) {
      return agent.getWorksiteRoles(worksiteId);
   }

   public List getWorksiteRoles() {
      return agent.getWorksiteRoles();
   }

   public boolean isRole() {
      return agent.isRole();
   }

   public String getPassword() {
      return agent.getPassword();
   }
}   
