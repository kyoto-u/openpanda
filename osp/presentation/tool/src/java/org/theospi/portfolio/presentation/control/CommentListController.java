/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/CommentListController.java $
* $Id:CommentListController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.tool.cover.ToolManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.CommentSortBy;
import org.theospi.portfolio.presentation.PresentationManager;
import org.sakaiproject.metaobj.shared.model.Id;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jun 1, 2004
 * Time: 4:36:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommentListController extends AbstractPresentationController implements CustomCommandController {

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Agent agent = getAuthManager().getAgent();
      PresentationManager presentationManager = getPresentationManager();
      
      Id id = getIdManager().getId((String)request.get("id"));
      List commentList = presentationManager.getPresentationComments(id, agent);

      Map model = new Hashtable();
      model.put("comments", commentList);
      model.put("id", id.getValue());
      
      if (request.get("returnView") != null) {
    	  model.put("returnView", request.get("returnView"));
      }

      return new ModelAndView("success", model);
   }

   public Object formBackingObject(Map request, Map session, Map application) {
      return new CommentSortBy(); // not currently used
   }
}
