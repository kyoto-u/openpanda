/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-2.9.0/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/utils/mvc/intf/CustomCommandController.java $
 * $Id: CustomCommandController.java 59676 2009-04-03 23:18:23Z arwhyte@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.utils.mvc.intf;

import java.util.Map;

/*
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-2.9.0/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/utils/mvc/intf/CustomCommandController.java $
 * $Revision: 59676 $
 * $Date: 2009-04-04 08:18:23 +0900 (Sat, 04 Apr 2009) $
 */

/**
 * Use this controller when you need to override the way the backing object is created.
 * If all you are doing is return new MyBackingObject() you won't need to implement this,
 * Spring will handle that for you.  Do not use this method to populate helping data
 * into the model.  This method does not get called if there is an error in validation.
 * If you rely on this information for putting stuff in the request, you will not have
 * it there in the case of validation error.  If you need to put helping data in the model,
 * implement FormController's referenceData method.
 *
 * @author John Ellis (john.ellis@rsmart.com)
 * @author John Bush (john.bush@rsmart.com)
 * @see Controller
 */
public interface CustomCommandController extends Controller {
   public Object formBackingObject(Map request,
                                   Map session,
                                   Map application);
}
