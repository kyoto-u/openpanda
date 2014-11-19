/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.2/presentation/tool/src/java/org/theospi/portfolio/presentation/control/PublishLayoutController.java $
* $Id: PublishLayoutController.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
package org.theospi.portfolio.presentation.control;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.tool.cover.ToolManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.PresentationFunctionConstants;
import org.theospi.portfolio.presentation.model.PresentationLayout;

public class PublishLayoutController extends AbstractPresentationController {
   protected final Log logger = LogFactory.getLog(getClass());

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      if (request.get("layout_id") != null && !request.get("layout_id").equals("")) {
         Id id = getIdManager().getId((String)request.get("layout_id"));
         PresentationLayout layout = getPresentationManager().getPresentationLayout(id);
         
         String suggest = (String)request.get("suggest");
         if (suggest == null) {
            Id siteId = getIdManager().getId(ToolManager.getCurrentPlacement().getContext());
            getAuthzManager().checkPermission(PresentationFunctionConstants.PUBLISH_LAYOUT, siteId);
            layout.setSiteId(siteId.getValue());
            layout.setGlobalState(PresentationLayout.STATE_PUBLISHED);
         }
         else {
            getAuthzManager().checkPermission(PresentationFunctionConstants.SUGGEST_PUBLISH_LAYOUT, layout.getId());
            layout.setGlobalState(PresentationLayout.STATE_WAITING_APPROVAL);
         }
         
//       Don't need to check authz for saving as we should already have perms via the publish
         getPresentationManager().storeLayout(layout, false);
         request.put("newPresentationLayoutId", layout.getId().getValue());
      }
      return new ModelAndView("success");
      
      
   }

}
