/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/branches/sakai-2.8.x/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/mgt/StructuredArtifactValidationService.java $
 * $Id: StructuredArtifactValidationService.java 59676 2009-04-03 23:18:23Z arwhyte@umich.edu $
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

package org.sakaiproject.metaobj.shared.mgt;

import java.util.List;

import org.sakaiproject.metaobj.shared.model.ElementBean;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Aug 18, 2005
 * Time: 2:49:14 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * This interface allows for the validation of ElementBean objects
 */
public interface StructuredArtifactValidationService {

   /**
    * Validate this element from the root.
    *
    * @param element filled in element to be validated.
    * @return list of ValidationError objects.  If this list is
    *         returned empty, then the element validated successfully
    * @see org.sakaiproject.metaobj.shared.model.ValidationError
    */
   public List validate(ElementBean element);

   /**
    * Validate this element from the root.
    *
    * @param element    filled in element to be validated.
    * @param parentName this is the name of the parent of this object.
    *                   All fields that have errors will have this name prepended with a "."
    * @return list of ValidationError objects.  If this list is
    *         returned empty, then the element validated successfully
    * @see org.sakaiproject.metaobj.shared.model.ValidationError
    */
   public List validate(ElementBean element, String parentName);

}
