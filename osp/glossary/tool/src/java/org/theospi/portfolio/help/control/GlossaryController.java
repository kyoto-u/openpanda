/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/glossary/tool/src/java/org/theospi/portfolio/help/control/GlossaryController.java $
* $Id:GlossaryController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.help.control;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.help.model.GlossaryEntry;
import org.theospi.portfolio.help.model.HelpManager;

public class GlossaryController implements Controller {
   private HelpManager helpManager;

   protected final Log logger = LogFactory.getLog(getClass());

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      GlossaryEntry entry = (GlossaryEntry)requestModel;
      entry = getHelpManager().getGlossary().load(entry.getId());
      return new ModelAndView("success", "entry", entry);
   }

   public HelpManager getHelpManager() {
      return helpManager;
   }

   public void setHelpManager(HelpManager helpManager) {
      this.helpManager = helpManager;
   }

}
