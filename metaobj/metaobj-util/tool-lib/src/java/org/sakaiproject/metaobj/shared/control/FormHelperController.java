/*******************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/FormHelperController.java $
 * $Id: FormHelperController.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 * **********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
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
 ******************************************************************************/

package org.sakaiproject.metaobj.shared.control;

import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.content.api.ResourceEditingHelper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.Errors;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 7, 2006
 * Time: 11:34:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class FormHelperController implements Controller {

   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
                                     Map application, Errors errors) {
      if (session.get(ResourceEditingHelper.ATTACHMENT_ID) != null) {
         return new ModelAndView("edit");
      }
      else {
         return new ModelAndView("create");
      }
   }

}
