/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/CheckForTimeout.java $
 * $Id: CheckForTimeout.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.shared.control;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class CheckForTimeout implements HandlerInterceptor {
   private AuthenticationManager authenticationManager;
   private String timeoutUrl;
   private List ignoreList;

   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
      if (getIgnoreList().contains(request.getServletPath())) {
         return true;
      }
      Agent agent = getAuthenticationManager().getAgent();
      boolean timeOut = (agent == null || agent.getId() == null || agent.getId().getValue().length() == 0);
      if (timeOut) {
         response.sendRedirect(getTimeoutUrl());
      }
      return !timeOut;
   }


   public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
   }

   public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
   }

   public AuthenticationManager getAuthenticationManager() {
      return authenticationManager;
   }

   public void setAuthenticationManager(AuthenticationManager authenticationManager) {
      this.authenticationManager = authenticationManager;
   }

   public String getTimeoutUrl() {
      return timeoutUrl;
   }

   public void setTimeoutUrl(String timeoutUrl) {
      this.timeoutUrl = timeoutUrl;
   }

   public List getIgnoreList() {
      return ignoreList;
   }

   public void setIgnoreList(List ignoreList) {
      this.ignoreList = ignoreList;
   }
}
