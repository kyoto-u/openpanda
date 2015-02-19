/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/TitleController.java $
 * $Id: TitleController.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import java.util.HashMap;
import java.util.Map;

import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.util.Web;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

public class TitleController implements Controller {

   private WorksiteManager worksiteManager;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Map model = new HashMap();
      model.put("tool", getWorksiteManager().getTool(ToolManager.getCurrentPlacement().getId()));
      model.put("mainPanel", Web.escapeJavascript("Main" + ToolManager.getCurrentPlacement().getId()));
      return new ModelAndView("success", model);
   }

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }
}