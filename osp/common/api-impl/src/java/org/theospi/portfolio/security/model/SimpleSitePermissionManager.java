/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api-impl/src/java/org/theospi/portfolio/security/model/SimpleSitePermissionManager.java $
* $Id:SimpleSitePermissionManager.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2006, 2008 The Sakai Foundation
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

package org.theospi.portfolio.security.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;

public class SimpleSitePermissionManager extends SimpleToolPermissionManager {

   private String functionPrefix;
   
   /**
    * sets up the default perms for a tool.  Uses the site id as the qualifier.
    * Assumes that if no perms exist for the tool, the perms should be set to the defaults.
    * @param toolConfig
    */
   public void toolSiteChanged(ToolConfiguration toolConfig) {
      
      //Id toolId = getIdManager().getId(toolConfig.getId());
      PermissionsEdit edit = new PermissionsEdit();
      edit.setName(getPermissionEditName());
      Site containingSite = toolConfig.getContainingPage().getContainingSite();

      if (!isSpecial(containingSite)) {
         Id siteId = getIdManager().getId(containingSite.getId());
         edit.setQualifier(siteId);
         edit.setSiteId(containingSite.getId());
         getPermissionManager().fillPermissions(edit);
         List perms = filterPermissions(edit);
         if (perms == null || perms.size() == 0){
            createDefaultPermissions(edit.getSiteId(), siteId, containingSite.getType());
         }
      }
   }
   
   /**
    * sets up the default perms for a helper tool.  Uses the site id as the qualifier.
    * Assumes that if no perms exist for the tool, the perms should be set to the defaults.
    * @param site
    */
   public void helperSiteChanged(Site site) {
      if (!isSpecial(site)) {
         Id siteId = getIdManager().getId(site.getId());
         PermissionsEdit edit = new PermissionsEdit();
         edit.setQualifier(siteId);
         edit.setName(getPermissionEditName());
         edit.setSiteId(site.getId());
         getPermissionManager().fillPermissions(edit);
         List perms = filterPermissions(edit);
         if (perms == null || perms.size() == 0){
            createDefaultPermissions(edit.getSiteId(), siteId, site.getType());
         }
      }
   }
   
   protected List filterPermissions(PermissionsEdit edit) {
      List filteredPermissions = new ArrayList();
      
      for (Iterator iter = edit.getPermissions().iterator(); iter.hasNext();) {
         Permission perm = (Permission) iter.next();
         if (perm.getFunction().startsWith(functionPrefix))
            filteredPermissions.add(perm);         
      }
      return filteredPermissions;
   }

   public String getFunctionPrefix() {
      return functionPrefix;
   }

   public void setFunctionPrefix(String functionPrefix) {
      this.functionPrefix = functionPrefix;
   }
}
