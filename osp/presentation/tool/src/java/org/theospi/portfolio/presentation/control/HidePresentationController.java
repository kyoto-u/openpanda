/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/branches/sakai-2.8.x/presentation/tool/src/java/org/theospi/portfolio/presentation/control/HidePresentationController.java $
* $Id: HidePresentationController.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
***********************************************************************************
*
 * Copyright (c) 2007, 2008 The Sakai Foundation
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

/**
 * 
 */
package org.theospi.portfolio.presentation.control;

import java.util.Map;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.PresentationFunctionConstants;

/**
 * @author chrismaurer
 *
 */
public class HidePresentationController extends ListPresentationController {

   
   
   /* (non-Javadoc)
    * @see org.sakaiproject.metaobj.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
    */
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Agent current = getAuthManager().getAgent();
      String hideAction = (String)request.get("hideAction");
      String id = (String)request.get("id");
      
      if ("hide".equals(hideAction)) {
         getAuthzManager().createAuthorization(current, 
               PresentationFunctionConstants.HIDE_PRESENTATION, getIdManager().getId(id));
      }
      else {
         getAuthzManager().deleteAuthorization(current, 
               PresentationFunctionConstants.HIDE_PRESENTATION, getIdManager().getId(id));
      }
      
      return super.handleRequest(requestModel, request, session, application, errors);
   }

}
