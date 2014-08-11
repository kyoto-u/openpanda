/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api/src/java/org/theospi/portfolio/security/AuthorizationFailedException.java $
* $Id:AuthorizationFailedException.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
import org.theospi.portfolio.shared.model.OspException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 22, 2004
 * Time: 9:27:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class AuthorizationFailedException extends OspException {

   private Agent agent = null;
   private String function = null;
   private Id qualifier = null;

   /**
    *
    */
   public AuthorizationFailedException(String function, Id qualifier) {
      super("Authorizing (" + function + ", " + qualifier.toString() + ")");
      this.function = function;
      this.qualifier = qualifier;
   }

   /**
    *
    */
   public AuthorizationFailedException(Agent agent, String function, Id qualifier) {
      super("Authorizing (" + agent.toString() + ", " + function + ", " + qualifier.toString() + ")");
      this.agent = agent;
      this.function = function;
      this.qualifier = qualifier;
   }

   /**
    *
    */
   public AuthorizationFailedException() {
      super();
   }

   /**
    * @param cause
    */
   public AuthorizationFailedException(Throwable cause) {
      super(cause);
   }

   /**
    * @param message
    */
   public AuthorizationFailedException(String message) {
      super(message);
   }

   /**
    * @param message
    * @param cause
    */
   public AuthorizationFailedException(String message, Throwable cause) {
      super(message, cause);
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
}
