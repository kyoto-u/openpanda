/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/SessionConditionalRedirect.java $
* $Id:SessionConditionalRedirect.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.matrix.control;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.metaobj.shared.control.RedirectView;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 25, 2006
 * Time: 11:31:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class SessionConditionalRedirect extends RedirectView {

   private String sessionVariable;
   private String redirectUrl;

   protected void sendRedirect(HttpServletRequest request, HttpServletResponse response,
                               String targetUrl, boolean http10Compatible) throws IOException {
      String newTargetUrl = targetUrl;

      if (sessionVariable != null && request.getSession().getAttribute(sessionVariable) != null) {
         newTargetUrl = targetUrl.replaceFirst(getUrl(), redirectUrl);
      }

      super.sendRedirect(request, response, newTargetUrl, http10Compatible);
   }


   public String getSessionVariable() {
      return sessionVariable;
   }

   public void setSessionVariable(String sessionVariable) {
      this.sessionVariable = sessionVariable;
   }

   public String getRedirectUrl() {
      return redirectUrl;
   }

   public void setRedirectUrl(String redirectUrl) {
      this.redirectUrl = redirectUrl;
   }

}
