/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool-lib/src/java/org/theospi/portfolio/style/tool/StyleValidator.java $
* $Id:StyleValidator.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.style.tool;

import org.springframework.validation.Errors;
import org.theospi.portfolio.style.model.Style;
import org.theospi.utils.mvc.impl.ValidatorBase;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 23, 2004
 * Time: 2:37:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class StyleValidator extends ValidatorBase {

   /**
    * Return whether or not this object can validate objects
    * of the given class.
    */
   public boolean supports(Class clazz) {
      if (Style.class.isAssignableFrom(clazz)) return true;
      return false;
   }

   /**
    * Validate a presentation object, which must be of a class for which
    * the supports() method returned true.
    *
    * @param obj    Populated object to validate
    * @param errors Errors object we're building. May contain
    *               errors for this field relating to types.
    */
   public void validate(Object obj, Errors errors) {
      if (obj instanceof Style) validateStyle((Style)obj, errors);
   }

   protected void validateStyle(Style style, Errors errors) {
      if (style.isValidate()) {
         if (style.getName() == null || style.getName().length() == 0) {
            errors.rejectValue("name", "error.required", "name is required");
         }
         if (style.getStyleFile() == null || 
               style.getStyleFile().getValue() == null || 
               style.getStyleFile().getValue().length() == 0) {
            errors.rejectValue("styleFile", "error.required", "Style file is required");
         }
         if (style.getDescription() != null && style.getDescription().length() > 255) {
            errors.rejectValue("description", "error.lengthExceded", new Object[]{"255"}, "Value must be less than {0} characters");
         }
      }
   }
}
