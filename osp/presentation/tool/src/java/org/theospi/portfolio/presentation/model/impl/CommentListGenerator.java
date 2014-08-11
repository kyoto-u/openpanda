/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/model/impl/CommentListGenerator.java $
* $Id:CommentListGenerator.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.presentation.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.event.api.SessionState;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.control.servlet.SakaiComponentDispatchServlet;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.site.api.ToolConfiguration;
import org.theospi.portfolio.list.impl.BaseListGenerator;
import org.theospi.portfolio.list.intf.ActionableListGenerator;
import org.theospi.portfolio.presentation.CommentSortBy;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationComment;
import org.theospi.portfolio.presentation.model.PresentationTemplate;

public class CommentListGenerator extends BaseListGenerator implements ActionableListGenerator {
   private PresentationManager presentationManager;
   private static final String TOOL_ID_PARAM = "toolId";
   private static final String COMMENT_ID_PARAM = "commentId";
   private static final String PRESENTATION_ID_PARAM = "presentationId";

   private WorksiteManager worksiteManager;
   private AuthenticationManager authnManager;

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public AuthenticationManager getAuthnManager() {
      return authnManager;
   }

   public void setAuthnManager(AuthenticationManager authnManager) {
      this.authnManager = authnManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }

   public PresentationManager getPresentationManager() {
      return presentationManager;
   }

   public void setPresentationManager(PresentationManager presentationManager) {
      this.presentationManager = presentationManager;
   }

   public List getObjects() {
      CommentSortBy sortBy = new CommentSortBy();
      sortBy.setDirection(CommentSortBy.DESCENDING);
      sortBy.setSortByColumn(CommentSortBy.SORT_BY_DATE);
      Agent agent = getAuthnManager().getAgent();
      return new ArrayList(getPresentationManager().getOwnerComments(agent, sortBy));
   }

   public Map getToolParams(Object entry) {
      Map params = new HashMap();
      PresentationComment comment = (PresentationComment) entry;
      params.put(COMMENT_ID_PARAM, comment.getId());
      params.put(PRESENTATION_ID_PARAM, comment.getPresentationId());
      params.put(TOOL_ID_PARAM, comment.getPresentation().getToolId());
      return params;
   }

   public ToolConfiguration getToolInfo(Map request) {
      String toolId = (String) request.get(TOOL_ID_PARAM);
      if (toolId != null && toolId.length() > 0 ){
         return getWorksiteManager().getTool(toolId);
      }
      return null;
   }

   public boolean isNewWindow(Object entry) {
      PresentationComment comment = (PresentationComment) entry;

      return !internalWindow(comment.getPresentation());
   }

   public void setToolState(String toolId, Map request) {
      SessionState sessionState = UsageSessionService.getSessionState(toolId);
      sessionState.setAttribute(SakaiComponentDispatchServlet.TOOL_STATE_VIEW_KEY,
            "viewPresentation.osp");
      Map requestParams = new HashMap();
      requestParams.put("id", request.get(PRESENTATION_ID_PARAM));
      sessionState.setAttribute(SakaiComponentDispatchServlet.TOOL_STATE_VIEW_REQUEST_PARAMS_KEY,
            requestParams);

   }
   
   protected boolean internalWindow(Presentation pres) {
      PresentationTemplate template = pres.getTemplate();

      if (!template.isIncludeHeaderAndFooter()) {
         return false;
      }

      WorksiteManager manager = getWorksiteManager();

      return manager.isUserInSite(pres.getTemplate().getSiteId());
   }

   public String getCustomLink(Object entry) {
      PresentationComment comment = (PresentationComment) entry;
      if (!internalWindow(comment.getPresentation())) {
         return comment.getPresentation().getExternalUri();
      }
      return null;
   }
}
