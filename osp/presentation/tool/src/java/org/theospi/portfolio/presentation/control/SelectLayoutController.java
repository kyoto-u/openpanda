/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.2/presentation/tool/src/java/org/theospi/portfolio/presentation/control/SelectLayoutController.java $
* $Id: SelectLayoutController.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2006, 2008 The Sakai Foundation
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
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.PresentationLayoutHelper;
import org.theospi.portfolio.presentation.model.PresentationLayout;

public class SelectLayoutController extends AbstractPresentationController {
   
   protected final Log logger = LogFactory.getLog(getClass());
   
   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
                                     Map application, Errors errors) {

      String layoutId = (String)request.get("layout_id");
      String selectAction = (String)request.get("selectAction");
      
      if (selectAction != null && selectAction.equals("on")) {
         PresentationLayout layout = getPresentationManager().getPresentationLayout(getIdManager().getId(layoutId));
         session.put(PresentationLayoutHelper.CURRENT_LAYOUT, layout);
      }
      else if (selectAction != null && selectAction.equals("off")){
         session.remove(PresentationLayoutHelper.CURRENT_LAYOUT);
         session.put(PresentationLayoutHelper.UNSELECTED_LAYOUT, "true");
      }

      return new ModelAndView("success");
   }
}
