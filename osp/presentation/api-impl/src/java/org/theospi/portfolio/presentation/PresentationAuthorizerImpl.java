/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/api-impl/src/java/org/theospi/portfolio/presentation/PresentationAuthorizerImpl.java $
* $Id:PresentationAuthorizerImpl.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.presentation;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationLayout;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.app.ApplicationAuthorizer;

public class PresentationAuthorizerImpl implements ApplicationAuthorizer{
   private PresentationManager presentationManager;
   private IdManager idManager;
   private List functions;

   /**
    * This method will ask the application specific functional authorizer to determine authorization.
    *
    * @param facade   this can be used to do explicit auths if necessary
    * @param agent
    * @param function
    * @param id
    * @return null if the authorizer has no opinion, true if authorized, false if explicitly not authorized.
    */
   public Boolean isAuthorized(AuthorizationFacade facade, Agent agent,
                               String function, Id id) {

      // return null if we don't know what is up...
      if (function.equals(PresentationFunctionConstants.VIEW_PRESENTATION)) {
         return isPresentationViewAuth(facade, agent, id, true);
      } else if (function.equals(PresentationFunctionConstants.COMMENT_PRESENTATION)) {
         return isPresentationCommentAuth(facade, agent, id);
      } else if (function.equals(PresentationFunctionConstants.CREATE_TEMPLATE)) {
         return Boolean.valueOf(facade.isAuthorized(agent,function,id));
      } else if (function.equals(PresentationFunctionConstants.EDIT_TEMPLATE)) {
         return isTemplateAuth(facade, id, agent, PresentationFunctionConstants.EDIT_TEMPLATE);
      } else if (function.equals(PresentationFunctionConstants.PUBLISH_TEMPLATE)) {
         PresentationTemplate template = getPresentationManager().getPresentationTemplate(id);
         Id siteId = getIdManager().getId(template.getSiteId());
         return Boolean.valueOf(facade.isAuthorized(agent,function,siteId));
      } else if (function.equals(PresentationFunctionConstants.DELETE_TEMPLATE)) {
         return isTemplateAuth(facade, id, agent, PresentationFunctionConstants.DELETE_TEMPLATE);
      } else if (function.equals(PresentationFunctionConstants.COPY_TEMPLATE)) {
         return isTemplateAuth(facade, id, agent, PresentationFunctionConstants.COPY_TEMPLATE);
      } else if (function.equals(PresentationFunctionConstants.EXPORT_TEMPLATE)) {
         return isTemplateAuth(facade, id, agent, PresentationFunctionConstants.EXPORT_TEMPLATE);
      } else if (function.equals(PresentationFunctionConstants.CREATE_PRESENTATION)) {
         return Boolean.valueOf(facade.isAuthorized(agent,function,id));
      } else if (function.equals(PresentationFunctionConstants.EDIT_PRESENTATION)) {
         return isPresentationAuth(facade, id, agent, PresentationFunctionConstants.EDIT_PRESENTATION);
      } else if (function.equals(PresentationFunctionConstants.DELETE_PRESENTATION)) {
         return isPresentationAuth(facade, id, agent, PresentationFunctionConstants.DELETE_PRESENTATION);
      } else if (function.equals(ContentHostingService.EVENT_RESOURCE_READ)) {
         return isFileAuth(facade, agent, id);
      } else if (function.equals(PresentationFunctionConstants.CREATE_LAYOUT)) {
         return Boolean.valueOf(facade.isAuthorized(agent,function,id));
      } else if (function.equals(PresentationFunctionConstants.EDIT_LAYOUT)) {
         return isLayoutAuth(facade, id, agent, function);
      } else if (function.equals(PresentationFunctionConstants.PUBLISH_LAYOUT)) {
         return this.canPublishLayout(facade, id, agent, function);
      } else if (function.equals(PresentationFunctionConstants.SUGGEST_PUBLISH_LAYOUT)) {
         PresentationLayout layout = getPresentationManager().getPresentationLayout(id);
         Id siteId = getIdManager().getId(layout.getSiteId());
         return Boolean.valueOf(facade.isAuthorized(agent,function,siteId));
      } else if (function.equals(PresentationFunctionConstants.DELETE_LAYOUT)) {
         return isLayoutAuth(facade, id, agent, function);
      } else {
         return null;
      }
   }
   protected Boolean isPresentationAuth(AuthorizationFacade facade, Id qualifier, Agent agent, String function){
      Presentation presentation = getPresentationManager().getLightweightPresentation(qualifier);

      if (presentation == null) {
         // must be tool id
         return Boolean.valueOf(facade.isAuthorized(function,qualifier));
      }
      
      //owner can do anything
      if (presentation.getOwner().equals(agent)){
         return Boolean.valueOf(true);
      }
      Id toolId = getIdManager().getId(presentation.getToolId());
      return Boolean.valueOf(facade.isAuthorized(function,toolId));
   }

   protected Boolean isTemplateAuth(AuthorizationFacade facade, Id qualifier, Agent agent, String function){
      PresentationTemplate template = getPresentationManager().getPresentationTemplate(qualifier);
      //owner can do anything
      if (template.getOwner().equals(agent)){
         return Boolean.valueOf(true);
      }
      Id siteId = getIdManager().getId(template.getSiteId());
      return Boolean.valueOf(facade.isAuthorized(function,siteId));
   }
   
   protected Boolean isLayoutAuth(AuthorizationFacade facade, Id qualifier, Agent agent, String function){
      PresentationLayout layout = getPresentationManager().getPresentationLayout(qualifier);
      //owner can do anything
      if (agent.equals(layout.getOwner())){
         return Boolean.valueOf(true);
      }
      Id toolId = getIdManager().getId(layout.getToolId());
      return Boolean.valueOf(facade.isAuthorized(function,toolId));
   }

   protected Boolean canPublishLayout(AuthorizationFacade facade, Id qualifier, Agent agent, String function) {
      PresentationLayout layout = getPresentationManager().getPresentationLayout(qualifier);
      if (layout == null) {
         return Boolean.valueOf(facade.isAuthorized(function,qualifier));
      }

      Id siteId = getIdManager().getId(layout.getSiteId());
      return Boolean.valueOf(facade.isAuthorized(function,siteId));  
   }
   
   protected Boolean isPresentationCommentAuth(AuthorizationFacade facade, Agent agent, Id id) {
      Presentation pres = getPresentationManager().getLightweightPresentation(id);

      if (!pres.isAllowComments()){
         return Boolean.valueOf(false);
      }

      if (pres.getIsPublic()) {
         return Boolean.valueOf(true);
      } else if (pres.getOwner().equals(agent)) {
         return Boolean.valueOf(true);
      } else {
         Id toolId = getIdManager().getId(pres.getToolId());
         return Boolean.valueOf(facade.isAuthorized(agent, PresentationFunctionConstants.COMMENT_PRESENTATION, toolId));
      }
   }

   protected Boolean isPresentationViewAuth(AuthorizationFacade facade, Agent agent, Id id, boolean allowAnonymous) {
      Presentation pres = getPresentationManager().getLightweightPresentation(id);

      return isPresentationViewAuth(pres, facade, agent, id, allowAnonymous);
   }

   protected Boolean isPresentationViewAuth(Presentation pres, AuthorizationFacade facade,
                                            Agent agent, Id id, boolean allowAnonymous) {
      if (pres.getIsPublic() && (allowAnonymous || !agent.isInRole(Agent.ROLE_ANONYMOUS))) {
         return Boolean.valueOf(true);
      } else if (pres.getOwner().equals(agent)) {
         return Boolean.valueOf(true);
      } else {
         return Boolean.valueOf(facade.isAuthorized(agent, PresentationFunctionConstants.VIEW_PRESENTATION, id));
      }
   }

   protected Boolean isFileAuth(AuthorizationFacade facade, Agent agent, Id id) {
      // check if this id is attached to any pres

      if (id == null) return null;

      Collection presItems = getPresentationManager().getPresentationItems(id);
      presItems.addAll(getPresentationManager().getPresentationsBasedOnTemplateFileRef(id));

      if (presItems.size() == 0) {
         return null;
      }

      // does this user have access to any of the above pres
      for (Iterator i = presItems.iterator(); i.hasNext();) {
         Presentation pres = (Presentation) i.next();

         Boolean returned = isPresentationViewAuth(pres, facade, agent, pres.getId(), true);
         if (returned != null && returned.booleanValue()) {
            return returned;
         }
      }

      return null;
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

   public List getFunctions() {
      return functions;
   }

   public void setFunctions(List functions) {
      this.functions = functions;
   }
}
