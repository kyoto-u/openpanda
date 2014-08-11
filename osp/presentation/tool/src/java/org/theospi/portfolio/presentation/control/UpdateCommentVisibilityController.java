/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/UpdateCommentVisibilityController.java $
* $Id:UpdateCommentVisibilityController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.util.Map;

import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.model.PresentationComment;
import org.theospi.portfolio.security.AuthorizationFailedException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jun 1, 2004
 * Time: 2:27:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateCommentVisibilityController extends AbstractPresentationController
   implements Controller {

   public ModelAndView handleRequest(Object requestModel, Map request,
                                     Map session, Map application, Errors errors) {
      PresentationComment newComment = (PresentationComment) requestModel;

      PresentationComment oldComment = getPresentationManager().getPresentationComment(newComment.getId());

      if (!oldComment.getCreator().equals(getAuthManager().getAgent()) &&
         !oldComment.getPresentation().getOwner().equals(getAuthManager().getAgent())) {
         throw new AuthorizationFailedException();
      }

      oldComment.setVisibility(newComment.getVisibility());

      getPresentationManager().updatePresentationComment(oldComment);

      return new ModelAndView("success", "id",
         oldComment.getPresentation().getId().getValue());
   }

}
