/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool-lib/src/java/org/theospi/portfolio/shared/tool/HelperToolBase.java $
* $Id:HelperToolBase.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.shared.tool;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 16, 2005
 * Time: 2:57:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class HelperToolBase extends ToolBase {

   protected Object getAttribute(String attributeName) {
      ToolSession session = SessionManager.getCurrentToolSession();
      return session.getAttribute(attributeName);
   }

   protected Object getAttributeOrDefault(String attributeName) {
      ToolSession session = SessionManager.getCurrentToolSession();
      Object returned = session.getAttribute(attributeName);
      if (returned == null) {
         return attributeName;
      }
      return returned;
   }

   protected String returnToCaller() {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      Tool tool = ToolManager.getCurrentTool();
      ToolSession session = SessionManager.getCurrentToolSession();
       String url = (String) session.getAttribute(
            tool.getId() + Tool.HELPER_DONE_URL);
      String param =  (String) session.getAttribute("target");
      if (param != null){
          url = url.concat("?"+ param);
      }
      session.removeAttribute("target");
      session.removeAttribute(tool.getId() + Tool.HELPER_DONE_URL);


       try {

         context.redirect(url);
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to caller", e);
      }
      return null;
   }

   protected void removeAttribute(String attrib) {
      removeAttributes(new String[]{attrib});
   }

   protected void removeAttributes(String[] attribs) {
      ToolSession session = SessionManager.getCurrentToolSession();

      for (int i=0;i<attribs.length;i++) {
         session.removeAttribute(attribs[i]);
      }
   }

   protected void setAttribute(String attributeName, Object value) {
      ToolSession session = SessionManager.getCurrentToolSession();
      session.setAttribute(attributeName, value);      
   }

}
