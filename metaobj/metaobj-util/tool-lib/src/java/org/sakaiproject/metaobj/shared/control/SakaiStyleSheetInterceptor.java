/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/SakaiStyleSheetInterceptor.java $
 * $Id: SakaiStyleSheetInterceptor.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.site.cover.SiteService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class SakaiStyleSheetInterceptor extends HandlerInterceptorAdapter {
   protected WorksiteManager worksiteManager;
   private static Log M_log = LogFactory.getLog(SakaiStyleSheetInterceptor.class);

   public SakaiStyleSheetInterceptor() {
   }

   public void postHandle(HttpServletRequest request,
                          HttpServletResponse response,
                          Object handler,
                          ModelAndView modelAndView) throws Exception {
      // code borrowed from sakai's VmServlet.setVmStdRef() method

      // form the skin based on the current site, and the defaults as configured
      //String skinRoot = ServerConfigurationService.getString("skin.root", "/sakai-shared/css/");
      String skinRoot = ServerConfigurationService.getString("skin.repo", "/library/skin");
      String skin = ServerConfigurationService.getString("skin.default", "default");

      Id siteId = getWorksiteManager().getCurrentWorksiteId();

      if (siteId != null) {
         String siteSkin = SiteService.getSiteSkin(siteId.getValue());

         if (siteSkin != null) {
            skin = siteSkin;
         }

         request.setAttribute("sakai_skin_base", skinRoot + "/tool_base.css");
         request.setAttribute("sakai_skin", skinRoot + "/" + skin + "/tool.css");

         //TODO figure out if this is still needed
         // form the portal root for the skin - removing the .css and adding "portalskins" before
         int pos = skin.indexOf(".css");
         if (pos != -1) {
            skin = skin.substring(0, pos);
         }

         request.setAttribute("sakai_portalskin", skinRoot + "portalskins" + "/" + skin + "/");
         request.setAttribute("sakai_skin_id", skin);
      }
   }

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }
}
