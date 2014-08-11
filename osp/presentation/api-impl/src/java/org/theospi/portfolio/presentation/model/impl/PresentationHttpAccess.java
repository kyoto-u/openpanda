/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/api-impl/src/java/org/theospi/portfolio/presentation/model/impl/PresentationHttpAccess.java $
* $Id:PresentationHttpAccess.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.presentation.model.impl;

import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.entity.api.EntityAccessOverloadException;
import org.sakaiproject.entity.api.EntityCopyrightException;
import org.sakaiproject.entity.api.EntityNotDefinedException;
import org.sakaiproject.entity.api.EntityPermissionException;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.ReferenceParser;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.tool.cover.SessionManager;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.security.AuthorizationFailedException;
import org.theospi.portfolio.security.mgt.OspHttpAccessBase;
import org.theospi.portfolio.presentation.PresentationFunctionConstants;

import java.util.Collection;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 8, 2005
 * Time: 2:39:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class PresentationHttpAccess extends OspHttpAccessBase {

   private PresentationManager presentationManager;
   private IdManager idManager;

   public void handleAccess(HttpServletRequest req, HttpServletResponse res, Reference ref,
                            Collection copyrightAcceptedRefs) 
      throws EntityPermissionException, EntityNotDefinedException, EntityAccessOverloadException, EntityCopyrightException
   {
      // Check if this reference is for presentation content
      String[] parts = ref.getReference().split(Entity.SEPARATOR);
      if ( parts.length != 4 )
         super.handleAccess( req, res, ref, copyrightAcceptedRefs );
         
      // otherwise redirect to view the given presentation
      String redirectUrl = ServerConfigurationService.getServerUrl()
         + "/osp-presentation-tool/viewPresentation.osp?id=" 
         + parts[3];

      try
      {
         res.sendRedirect(res.encodeRedirectURL(redirectUrl));
      }
      catch ( IOException e )
      {
         throw new EntityNotDefinedException(ref.getReference());
      }
   }
               
   protected void checkSource(Reference ref, ReferenceParser parser)
      throws EntityPermissionException, EntityNotDefinedException, EntityAccessOverloadException, EntityCopyrightException {

      Presentation pres = presentationManager.getPresentation( getIdManager().getId(parser.getId()), false);
      if (pres == null) 
         throw new EntityNotDefinedException(parser.getId());
         
      boolean viewAll = ServerConfigurationService.getBoolean("osp.presentation.viewall", false);
      boolean canReview = getAuthzManager().isAuthorized(PresentationFunctionConstants.REVIEW_PRESENTATION,
                                                         getIdManager().getId(pres.getSiteId() ) );
      boolean canView = getAuthzManager().isAuthorized(PresentationFunctionConstants.VIEW_PRESENTATION, pres.getId());
      
      if ( !canView && (!viewAll || !canReview) )
         throw new EntityPermissionException(SessionManager.getCurrentSessionUserId(), 
                                             ContentHostingService.EVENT_RESOURCE_READ, ref.getReference());
   }

   public PresentationManager getPresentationManager() {
      return presentationManager;
   }

   public void setPresentationManager(PresentationManager presentationManager) {
      this.presentationManager = presentationManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }
}
