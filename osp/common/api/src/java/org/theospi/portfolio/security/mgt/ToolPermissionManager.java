/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api/src/java/org/theospi/portfolio/security/mgt/ToolPermissionManager.java $
* $Id:ToolPermissionManager.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.security.mgt;

import java.util.List;

import org.sakaiproject.site.api.ToolConfiguration;
import org.theospi.portfolio.security.model.PermissionsEdit;

public interface ToolPermissionManager {

   /**
    * Get a list of functions that this tool is interested in setting.
    * This list should be in some reasonable order (read to delete, etc).
    * @param edit contains information about the permissions edit such as
    * qualifier, etc.
    * @return list of strings that name the functions in some reasonable order
    */
   public List getFunctions(PermissionsEdit edit);

   /**
    * This method is called to see if the qualifier being edited
    * has some parent qualifiers that imply permissions for this qualifier.
    * One example might be a directory that has implied permissions of
    * the parent directory.  Since the permissions are implied, the
    * set permissions screen will not allow these permissions to be turned off.
    * @param edit contains information about the permissions edit such as
    * qualifier, etc.
    * @return list of Id objects that are parents of the passed in qualifier.
    */
   public List getReadOnlyQualifiers(PermissionsEdit edit);

   public void duplicatePermissions(ToolConfiguration fromTool, ToolConfiguration toTool);

}
