/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool-lib/src/java/org/theospi/utils/mvc/impl/servlet/HelperConsumerFormControllerImpl.java $
* $Id:HelperConsumerFormControllerImpl.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.utils.mvc.impl.servlet;

import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.metaobj.utils.mvc.impl.servlet.FormControllerImpl;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolSession;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jul 22, 2005
 * Time: 4:14:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class HelperConsumerFormControllerImpl extends FormControllerImpl {

   private static String TOOL_RETURNING_ATTRIBUTE = "theospi.toolReturning";

   private SessionManager sessionManager;

   protected boolean isFormSubmission(HttpServletRequest request) {
      ToolSession currentSession = sessionManager.getCurrentToolSession();

      if (currentSession.getAttribute(TOOL_RETURNING_ATTRIBUTE) != null) {
         currentSession.removeAttribute(TOOL_RETURNING_ATTRIBUTE);
         return true;
      }
      else {
         currentSession.setAttribute(TOOL_RETURNING_ATTRIBUTE, "true");
         return false;
      }
   }

   public SessionManager getSessionManager() {
      return sessionManager;
   }

   public void setSessionManager(SessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }

}
