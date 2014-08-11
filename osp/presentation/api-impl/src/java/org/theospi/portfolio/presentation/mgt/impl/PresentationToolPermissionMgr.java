/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/api-impl/src/java/org/theospi/portfolio/presentation/mgt/impl/PresentationToolPermissionMgr.java $
* $Id:PresentationToolPermissionMgr.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.presentation.mgt.impl;

import java.util.Iterator;

import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.model.SimpleToolPermissionManager;
import org.theospi.portfolio.worksite.model.SiteTool;

public class PresentationToolPermissionMgr extends SimpleToolPermissionManager {

   private PresentationManager presentationManager;
   private AuthorizationFacade authzManager;
   private IdManager idManager;

   public void toolRemoved(SiteTool siteTool) {
      Id toolId = getIdManager().getId(siteTool.getToolId());
      try {
         for (Iterator i=getPresentationManager().findPresentationsByTool(toolId).iterator();i.hasNext();){
            Presentation presentation = (Presentation) i.next();
            getPresentationManager().deletePresentation(presentation.getId());
         }
         getAuthzManager().deleteAuthorizations(toolId);
      } catch (Exception e){
         e.printStackTrace();
      }
   }

   public PresentationManager getPresentationManager() {
      return presentationManager;
   }

   public void setPresentationManager(PresentationManager presentationManager) {
      this.presentationManager = presentationManager;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }
}
