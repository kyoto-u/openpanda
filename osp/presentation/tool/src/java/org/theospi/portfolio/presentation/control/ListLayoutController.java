/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.0/presentation/tool/src/java/org/theospi/portfolio/presentation/control/ListLayoutController.java $
* $Id: ListLayoutController.java 85378 2010-11-23 17:35:53Z ottenhoff@longsight.com $
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
package org.theospi.portfolio.presentation.control;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScroll;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.tool.cover.ToolManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.PresentationLayoutHelper;
import org.theospi.portfolio.presentation.model.PresentationLayout;

public class ListLayoutController extends AbstractPresentationController {

   protected final Log logger = LogFactory.getLog(getClass());
   private ListScrollIndexer listScrollIndexer;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
                                     Map application, Errors errors) {

      boolean global = getPresentationManager().isGlobal();
      Hashtable model = new Hashtable();
      Agent agent = getAuthManager().getAgent();
      String selectable = (String)session.get(PresentationLayoutHelper.LAYOUT_SELECTABLE);
      
      Set layoutSet = new HashSet(
         getPresentationManager().findLayoutsByOwner(agent, ToolManager.getCurrentPlacement().getContext()));
      layoutSet.addAll(getPresentationManager().findPublishedLayouts(ToolManager.getCurrentPlacement().getContext()));
      
      if (selectable != null) {
         model.put("selectableLayout", selectable);
         layoutSet.addAll(getPresentationManager().findMyGlobalLayouts());
      }
      else if (global) {
         layoutSet.addAll(getPresentationManager().findAllGlobalLayouts());
      }
      
      List layouts = new ArrayList(layoutSet);
      
      model.put("layoutCount", String.valueOf(layouts.size()));

      if (request.get("newPresentationLayoutId") != null) {
         request.put(ListScroll.ENSURE_VISIBLE_TAG, "" + getPresentationIndex(layouts,
            (String)request.get("newPresentationLayoutId")));
      }

      layouts = getListScrollIndexer().indexList(request, model, layouts);
      model.put("layouts", layouts);

      model.put("osp_agent", agent);
      String worksiteId = getWorksiteManager().getCurrentWorksiteId().getValue();
      model.put("worksite", getWorksiteManager().getSite(worksiteId));
      model.put("tool", getWorksiteManager().getTool(ToolManager.getCurrentPlacement().getId()));
      model.put("isMaintainer", isMaintainer());
      
      if (session.get(PresentationLayoutHelper.CURRENT_LAYOUT_ID) != null)
         model.put("selectedLayout", session.get(PresentationLayoutHelper.CURRENT_LAYOUT_ID));

      model.put("isGlobal", Boolean.valueOf(global));
      
      return new ModelAndView("success", model);
   }

   protected int getPresentationIndex(List layouts, String layoutId) {
      if (layoutId == null) {
         return 0;
      }

      for (int i=0;i<layouts.size();i++){
         PresentationLayout current = (PresentationLayout)layouts.get(i);
         if (current.getId().getValue().equals(layoutId)) {
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

}
