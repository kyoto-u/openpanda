/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/utils/mvc/intf/FormController.java $
 * $Id: FormController.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

/*
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/utils/mvc/intf/FormController.java $
 * $Revision: 105079 $
 * $Date: 2012-02-25 08:08:11 +0900 (Sat, 25 Feb 2012) $
 */

/**
 * This controller is useful for handling form submissions.  In a normal form submission
 * formBackingObject is called to create the backing object.  Next the system binds
 * request params into this object, and performs validation.  If validation errors are
 * detected the system re-renders the form view.  This flow creates a problem if you need
 * to populate the model with something for the form, because if you try to do this
 * work in formBackingObject, you notice the system doesn't call this again in the case
 * of validation erros.  The referenceData methods provides an convenient place to do this
 * work.  This method will be called before rendering the form the first time, and before
 * rendering the form after validation errors.
 *
 * @author John Ellis (john.ellis@rsmart.com)
 * @author John Bush (john.bush@rsmart.com)
 */
public interface FormController extends Controller {
   /**
    * Create a map of all data the form requries.
    * Useful for building up drop down lists, etc.
    *
    * @param request
    * @param command
    * @param errors
    * @return
    */
   public Map referenceData(Map request, Object command, Errors errors);
}
