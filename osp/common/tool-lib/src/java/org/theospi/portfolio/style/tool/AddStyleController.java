/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.3/common/tool-lib/src/java/org/theospi/portfolio/style/tool/AddStyleController.java $
* $Id: AddStyleController.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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
package org.theospi.portfolio.style.tool;


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
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.style.model.Style;

public class AddStyleController extends AbstractStyleController 
      implements CustomCommandController, FormController, LoadObjectController {
   
   public static final String STYLE_FILE = "osp.style.styleFile";
  
   protected static final String STYLE_SESSION_TAG =
      "osp.style.AddStyleController.style";

   private SessionManager sessionManager;
   private ContentHostingService contentHosting;
   private EntityManager entityManager;

   public Object formBackingObject(Map request, Map session, Map application) {
      Style style;
      if (request.get("style_id") != null && !request.get("style_id").equals("")) {
         Id id = getIdManager().getId((String)request.get("style_id"));
         style = getStyleManager().getStyle(id);
      }
      else {
         style = new Style();
         style.setOwner(getAuthManager().getAgent());
         style.setSiteId(ToolManager.getCurrentPlacement().getContext());
      }
      return style;
   }

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      if (session.get(STYLE_SESSION_TAG) != null) {
         return session.remove(STYLE_SESSION_TAG);
      }
      else {
         return incomingModel;
      }
   }

   public ModelAndView handleRequest(Object requestModel, Map request,
                                     Map session, Map application, Errors errors) {
      Style style = (Style) requestModel;

      if (STYLE_FILE.equals(style.getFilePickerAction())) {
         session.put(STYLE_SESSION_TAG, style);
         //session.put(FilePickerHelper.FILE_PICKER_FROM_TEXT, request.get("filePickerFrom"));
         String filter = "";
         
         List files = new ArrayList();
         String id = "";
         if (STYLE_FILE.equals(style.getFilePickerAction())) {
            filter = "org.sakaiproject.content.api.ContentResourceFilter.styleFile";
            if (style.getStyleFile() != null)
               id = getContentHosting().resolveUuid(style.getStyleFile().getValue());
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
       
         
         return new ModelAndView("pickStyleFiles");
      }

      if (request.get("save") != null) {
         style.setSiteId(ToolManager.getCurrentPlacement().getContext());
         save(style, errors);
      }

      return new ModelAndView("success");
   }
   
   protected void save(Style style, Errors errors) {
        getStyleManager().mergeStyle(style);
   }

   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = new HashMap();
      model.put("STYLE_FILE", STYLE_FILE);
      
      Style style = (Style) command;
      
      ToolSession session = getSessionManager().getCurrentToolSession();
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
            session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
         // here is where we setup the id
         List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         Id nodeId = null;
         String nodeName = "";
         
         if (refs.size() == 1) {
            Reference ref = (Reference)refs.get(0);
            Node node = getStyleManager().getNode(ref);
            nodeId = node.getId();
            nodeName = node.getDisplayName();
         }

         if (STYLE_FILE.equals(style.getFilePickerAction())) {
            style.setStyleFile(nodeId);
            style.setStyleFileName(nodeName);
         }
      }
      
      if (style.getStyleFile() != null) {
         Node styleFile = getStyleManager().getNode(style.getStyleFile());
         model.put("styleFileName", styleFile.getDisplayName());
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

