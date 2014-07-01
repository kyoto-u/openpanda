/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.0/presentation/tool/src/java/org/theospi/portfolio/presentation/control/AddLayoutController.java $
* $Id: AddLayoutController.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
package org.theospi.portfolio.presentation.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.FilePickerHelper;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.ToolManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.model.PresentationLayout;
import org.theospi.portfolio.shared.model.Node;

public class AddLayoutController extends AbstractPresentationController 
      implements CustomCommandController, FormController, LoadObjectController {
   
   public static final String XHTML_FILE = "osp.presentation.layout.xhtmlFile";
   public static final String PREVIEW_IMAGE = "osp.presentation.layout.previewImage";
   protected static final String LAYOUT_SESSION_TAG =
      "osp.presentation.AddLayoutController.layout";

   private SessionManager sessionManager;
   private ContentHostingService contentHosting;
   private EntityManager entityManager;

   public Object formBackingObject(Map request, Map session, Map application) {
      PresentationLayout layout;
      if (request.get("layout_id") != null && !request.get("layout_id").equals("")) {
         Id id = getIdManager().getId((String)request.get("layout_id"));
         layout = getPresentationManager().getPresentationLayout(id);
      }
      else {
         layout = new PresentationLayout();
         layout.setOwner(getAuthManager().getAgent());
         layout.setToolId(ToolManager.getCurrentPlacement().getId());
         layout.setSiteId(ToolManager.getCurrentPlacement().getContext());
      }
      return layout;
   }

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      if (session.get(LAYOUT_SESSION_TAG) != null) {
         return session.remove(LAYOUT_SESSION_TAG);
      }
      else {
         return incomingModel;
      }
   }

   public ModelAndView handleRequest(Object requestModel, Map request,
                                     Map session, Map application, Errors errors) {
      PresentationLayout layout = (PresentationLayout) requestModel;

      if (XHTML_FILE.equals(layout.getFilePickerAction()) ||
            PREVIEW_IMAGE.equals(layout.getFilePickerAction())) {
         session.put(LAYOUT_SESSION_TAG, layout);
         //session.put(FilePickerHelper.FILE_PICKER_FROM_TEXT, request.get("filePickerFrom"));
         String filter = "";
         
         List files = new ArrayList();
         String id = "";
         if (XHTML_FILE.equals(layout.getFilePickerAction())) {
            filter = "org.sakaiproject.content.api.ContentResourceFilter.layoutFile";
            if (layout.getXhtmlFileId() != null)
               id = getContentHosting().resolveUuid(layout.getXhtmlFileId().getValue());
         }
         else if (PREVIEW_IMAGE.equals(layout.getFilePickerAction())) {
            filter = "org.sakaiproject.content.api.ContentResourceFilter.layoutImageFile";
            if (layout.getPreviewImageId() != null)
               id = getContentHosting().resolveUuid(layout.getPreviewImageId().getValue());
         }
         if (id != null && !id.equals("")) {
            Reference ref;
            try {
               ref = getEntityManager().newReference(getContentHosting().getResource(id).getReference());
               files.add(ref);              
               session.put(FilePickerHelper.FILE_PICKER_ATTACHMENTS, files);
            } catch (PermissionException e) {
               logger.error("", e);
            } catch (IdUnusedException e) {
               logger.error("", e);
            } catch (TypeException e) {
               logger.error("", e);
            }            
         }
         
         if (!filter.equals(""))
            session.put(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER, 
                  ComponentManager.get(filter));
         
         session.put(FilePickerHelper.FILE_PICKER_MAX_ATTACHMENTS, Integer.valueOf(1));
       
         
         return new ModelAndView("pickLayoutFiles");
      }
/*
      String action = "";
      Object actionObj = request.get("action");
      if (actionObj instanceof String) {
         action = (String)actionObj;
      }
      else if (actionObj instanceof String[]) {
         action = ((String[])actionObj)[0];
      }
*/
      if (request.get("save") != null)
         save(layout, errors);

      return new ModelAndView("success");
   }
   
   protected void save(PresentationLayout layout, Errors errors) {
        getPresentationManager().storeLayout(layout);
   }

   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = new HashMap();
      model.put("XHTML_FILE", XHTML_FILE);
      model.put("PREVIEW_IMAGE", PREVIEW_IMAGE);
      PresentationLayout layout = (PresentationLayout) command;
      
      ToolSession session = getSessionManager().getCurrentToolSession();
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
            session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
         // here is where we setup the id
         List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         Id nodeId = null;
         String nodeName = "";
         
         if (refs.size() == 1) {
            Reference ref = (Reference)refs.get(0);
            Node node = getPresentationManager().getNode(ref);
            nodeId = node.getId();
            nodeName = node.getDisplayName();
         }

         if (XHTML_FILE.equals(layout.getFilePickerAction())) {
            layout.setXhtmlFileId(nodeId);
            layout.setXhtmlFileName(nodeName);
         }
         else if (PREVIEW_IMAGE.equals(layout.getFilePickerAction())) {
            layout.setPreviewImageId(nodeId);
            layout.setPreviewImageName(nodeName);
         }
      }
      
      if (layout.getXhtmlFileId() != null) {
         Node xhtmlFile = getPresentationManager().getNode(layout.getXhtmlFileId());
         model.put("xhtmlFileName", xhtmlFile.getDisplayName());
      }
      if (layout.getPreviewImageId() != null) {
         Node previewFile = getPresentationManager().getNode(layout.getPreviewImageId());
         model.put("previewImageName", previewFile.getDisplayName());
      }
      
      session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
      session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);

      return model;
   }

   public SessionManager getSessionManager() {
      return sessionManager;
   }

   public void setSessionManager(SessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }

   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   public EntityManager getEntityManager() {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

}

