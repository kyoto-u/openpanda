/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/utils/mvc/intf/Controller.java $
 * $Id: Controller.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.utils.mvc.intf;

import java.util.Map;

import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

/**
 * Our Controller framework is build on top of the Spring's mvc framework.  We provide
 * an extra layer of abstraction that is not bound to any particular technology
 * (servlet, portlet, thick GUI, etc).  In order to use our framework you really
 * should have good understanding of spring's mvc architecture especially the
 * SimpleFormController and AbstractCommandController.  We assume you understand
 * what a backing object is, and the flow for form submission, and validation.
 * <p/>
 * This interface provides the basic Controller functionality.  The system
 * binds request params into the requestModel, and calls handleRequest.  The requestModel
 * type is configured in the spring config using the commandClass property.  By default
 * the no argument constructor will be used to create this object.  If you require more
 * control over how this backing object is created implement CustomCommandController.
 *
 * @author John Ellis (john.ellis@rsmart.com)
 * @author John Bush (john.bush@rsmart.com)
 */
public interface Controller {

   public ModelAndView handleRequest(Object requestModel, Map request,
                                     Map session, Map application, Errors errors);

}
