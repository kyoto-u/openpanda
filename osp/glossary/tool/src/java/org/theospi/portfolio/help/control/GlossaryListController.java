/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/glossary/tool/src/java/org/theospi/portfolio/help/control/GlossaryListController.java $
* $Id:GlossaryListController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.help.control;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScroll;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.tool.api.ToolManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.help.model.GlossaryEntry;
import org.theospi.portfolio.help.model.HelpManager;

public class GlossaryListController extends HelpController {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private ListScrollIndexer listScrollIndexer;
   private ToolManager toolManager;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String worksiteId = getWorksiteManager().getCurrentWorksiteId().getValue();

      Hashtable model = new Hashtable();
      model.put("worksite", getWorksiteManager().getSite(worksiteId));
      model.put("tool", toolManager.getCurrentPlacement());
      model.put("global", Boolean.valueOf(getHelpManager().isGlobal()));

      List terms = new ArrayList(getHelpManager().getWorksiteTerms());

      if (request.get("newTermId") != null) {
         request.put(ListScroll.ENSURE_VISIBLE_TAG, "" + getTermIndex(terms,
            (String)request.get("newTermId")));
      }

      terms = getListScrollIndexer().indexList(request, model, terms);

      model.put("glossary", terms);
      
      String msg = (String)session.get(TRANSFER_CONTROLLER_SESSION_MESSAGE);
      if(msg != null) {
         if(msg.equals(TRANSFER_MESSAGE_IMPORT_SUCCESS))
            model.put("import_success", "true");
         if(msg.equals(TRANSFER_MESSAGE_IMPORT_BAD_FILE))
            model.put("import_unrecognized_file", "true");
         if(msg.equals(TRANSFER_MESSAGE_IMPORT_FAILED))
            model.put("import_failed", "true");
         if(msg.equals(TRANSFER_MESSAGE_IMPORT_BAD_PARSE))
            model.put("import_bad_parse", "true");
         session.remove(TRANSFER_CONTROLLER_SESSION_MESSAGE);
      }

      return new ModelAndView("success", model);
   }

   protected int getTermIndex(List terms, String termId) {
      for (int i=0;i<terms.size();i++) {
         GlossaryEntry entry = (GlossaryEntry)terms.get(i);
         if (entry.getId().getValue().equals(termId)) {
            return i;
         }
      }
      return 0;
   }

   public ListScrollIndexer getListScrollIndexer() {
      return listScrollIndexer;
   }

   public void setListScrollIndexer(ListScrollIndexer listScrollIndexer) {
      this.listScrollIndexer = listScrollIndexer;
   }

   public ToolManager getToolManager() {
      return toolManager;
   }

   public void setToolManager(ToolManager toolManager) {
      this.toolManager = toolManager;
   }
}


