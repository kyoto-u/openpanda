/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool-lib/src/java/org/theospi/utils/mvc/impl/servlet/AbstractFormController.java $
* $Id:AbstractFormController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.utils.mvc.impl.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.sakaiproject.metaobj.utils.mvc.intf.CancelableController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by IntelliJ IDEA.
 * User: jbush
 * Date: Dec 15, 2004
 * Time: 9:52:19 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractFormController implements CancelableController, FormController {
    public static final String PARAM_CANCEL = "_cancel";
	/**
	 * Return if cancel action is specified in the request.
	 * <p>Default implementation looks for "_cancel" parameter in the request.
	 * @param request current HTTP request
	 * @see #PARAM_CANCEL
	 */
    public boolean isCancel(Map request) {
        return request.containsKey(PARAM_CANCEL);
    }

    public ModelAndView processCancel(Map request, Map session, Map application,
                                      Object command, Errors errors) throws Exception {
        throw new ServletException(
                "Wizard form controller class [" + getClass().getName() + "] does not support a cancel operation");
    }

   public Map referenceData(Map request, Object command, Errors errors){
       return new HashMap();
   }
}
