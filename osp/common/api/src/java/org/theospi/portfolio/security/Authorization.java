/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api/src/java/org/theospi/portfolio/security/Authorization.java $
* $Id:Authorization.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
package org.theospi.portfolio.security;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;

/**
 * Created by IntelliJ IDEA.
 * User: jbush
 * Date: Apr 29, 2004
 * Time: 4:58:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class Authorization {
   private Id id;
   private Agent agent;
   private String function;
   private Id qualifier;

   public Authorization() {
   }

   public Authorization(Agent agent, String function, Id qualifier) {
      this.agent = agent;
      this.function = function;
      this.qualifier = qualifier;
   }

   public Id getId() {
      return id;
   }

   public void setId(Id id) {
      this.id = id;
   }

   public Agent getAgent() {
      return agent;
   }

   public String getFunction() {
      return function;
   }

   public Id getQualifier() {
      return qualifier;
   }

   public void setAgent(Agent agent) {
      this.agent = agent;
   }

   public void setFunction(String function) {
      this.function = function;
   }

   public void setQualifier(Id qualifier) {
      this.qualifier = qualifier;
   }

   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Authorization)) return false;

      final Authorization authorization = (Authorization) o;

      if (!agent.equals(authorization.agent)) return false;
      if (!function.equals(authorization.function)) return false;
      if (!qualifier.equals(authorization.qualifier)) return false;

      return true;
   }

   public int hashCode() {
      int result;
      result = agent.hashCode();
      result = 29 * result + function.hashCode();
      result = 29 * result + qualifier.hashCode();
      return result;
   }

}
