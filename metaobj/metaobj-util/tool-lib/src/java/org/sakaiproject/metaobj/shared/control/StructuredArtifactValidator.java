/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/StructuredArtifactValidator.java $
 * $Id: StructuredArtifactValidator.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.shared.control;

import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;
import org.springframework.validation.Errors;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 19, 2004
 * Time: 3:31:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class StructuredArtifactValidator extends XmlValidator {


   public boolean supports(Class clazz) {
      if (super.supports(clazz)) {
         return true;
      }
      return (StructuredArtifact.class.isAssignableFrom(clazz));
   }

   public void validate(Object obj, Errors errors) {
      validateInternal(obj, errors);
      super.validate(obj, errors);
   }

   protected void validateInternal(Object obj, Errors errors) {
      if (obj instanceof StructuredArtifact) {
         StructuredArtifact artifact = (StructuredArtifact) obj;

         if (artifact.getDisplayName() == null ||
            artifact.getDisplayName().length() == 0) {
            errors.rejectValue("displayName", "required value {0}", new Object[]{"displayName"},
               "required value displayName");
         }
      }
   }

   public void validate(Object obj, Errors errors, boolean checkListNumbers) {
      validateInternal(obj, errors);
      super.validate(obj, errors, checkListNumbers);
   }

}
