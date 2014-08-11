/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool-lib/src/java/org/theospi/utils/mvc/impl/ToolFinishedView.java $
* $Id:ToolFinishedView.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
package org.sakaiproject.metaobj.shared.control;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.metaobj.shared.control.HelperView;
import org.sakaiproject.spring.util.SpringTool;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jul 15, 2005
 * Time: 2:06:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToolFinishedView extends HelperView {

   /** the alternate next view */
   public static final String ALTERNATE_DONE_URL = "altDoneURL";

   /** the set of alternate views */
   public static final String ALTERNATE_DONE_URL_MAP = "altDoneURLSet";

   public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
      ToolSession toolSession = SessionManager.getCurrentToolSession();
      Tool tool = ToolManager.getCurrentTool();

      String url = (String) toolSession.getAttribute(
            tool.getId() + Tool.HELPER_DONE_URL);

      toolSession.removeAttribute(tool.getId() + Tool.HELPER_DONE_URL);
      toolSession.removeAttribute(SpringTool.LAST_VIEW_VISITED);

      String pathObj = (String)toolSession.getAttribute(tool.getId() + "thetoolPath");
      toolSession.removeAttribute(tool.getId() + "thetoolPath");

      if(toolSession.getAttribute(tool.getId() + ALTERNATE_DONE_URL) != null) {
         String path = "";
         Object altObj = toolSession.getAttribute(tool.getId() + ALTERNATE_DONE_URL);
         Map urlMap = (Map)toolSession.getAttribute(tool.getId() + ALTERNATE_DONE_URL_MAP);

         if(urlMap != null) {
            url = (String) urlMap.get((String)altObj);

            if(pathObj != null) {
               path = (String) pathObj;
            }

            if(!url.startsWith("/"))
               url = path + "/" + url;
         }

         toolSession.removeAttribute(tool.getId() + ALTERNATE_DONE_URL);
      }

      setUrl(url);

      if (getModelPrefix() == null) {
         setModelPrefix("");
      }

      super.render(model, request, response);
   }


}
