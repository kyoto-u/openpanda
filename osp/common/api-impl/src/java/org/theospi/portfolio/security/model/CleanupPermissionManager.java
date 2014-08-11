/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api-impl/src/java/org/theospi/portfolio/security/model/CleanupPermissionManager.java $
* $Id:CleanupPermissionManager.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.security.model;

import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.worksite.model.SiteTool;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Aug 16, 2005
 * Time: 11:29:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class CleanupPermissionManager extends SimpleToolPermissionManager {

   private CleanupableService service;
   private AuthorizationFacade authzManager;
   private IdManager idManager;

   public void toolRemoved(SiteTool siteTool) {
      Id toolId = getIdManager().getId(siteTool.getToolId());
      getService().cleanupTool(toolId);
      getAuthzManager().deleteAuthorizations(toolId);
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public CleanupableService getService() {
      return service;
   }

   public void setService(CleanupableService service) {
      this.service = service;
   }

}
