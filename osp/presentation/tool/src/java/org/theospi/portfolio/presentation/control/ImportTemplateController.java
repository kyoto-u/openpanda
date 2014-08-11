/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/ImportTemplateController.java $
* $Id:ImportTemplateController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.presentation.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.FilePickerHelper;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.shared.model.InvalidUploadException;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.ToolManager;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.theospi.portfolio.presentation.model.TemplateUploadForm;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.model.OspException;

public class ImportTemplateController extends AbstractPresentationController implements Validator{
   protected final transient Log logger = LogFactory.getLog(getClass());
   
   private SessionManager sessionManager;
   private ContentHostingService contentHosting = null;
   private EntityManager entityManager;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
                                     Map application, Errors errors) {

      TemplateUploadForm templateForm = (TemplateUploadForm)requestModel;
      
      if (templateForm.getSubmitAction().equals("pickImport")) {
         if (templateForm.getUploadedTemplate() != null) {
            String id = getContentHosting().resolveUuid(templateForm.getUploadedTemplate().getValue());
            Reference ref;
            List files = new ArrayList();
            try {
               ref = getEntityManager().newReference(getContentHosting().getResource(id).getReference());
               files.add(ref);
            } catch (PermissionException e) {
               logger.error("", e);
            } catch (IdUnusedException e) {
               logger.error("", e);
            } catch (TypeException e) {
               logger.error("", e);
            }
            session.put(FilePickerHelper.FILE_PICKER_ATTACHMENTS, files);
         }
         
         session.put(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER, 
               ComponentManager.get("org.sakaiproject.content.api.ContentResourceFilter.templateImportFile"));
         session.put(FilePickerHelper.FILE_PICKER_MAX_ATTACHMENTS, Integer.valueOf(1));
         
         return new ModelAndView("pickImport");
      }
      else {
         try {
            Node file = getPresentationManager().getNode(templateForm.getUploadedTemplate());
            PresentationTemplate template = getPresentationManager().uploadTemplate(
                  file.getDisplayName(),
                  ToolManager.getCurrentPlacement().getContext(),
                  file.getInputStream());
            Map model = new Hashtable();
            model.put("newPresentationTemplateId", template.getId().getValue());
      
            return new ModelAndView("success", model);
         } catch (InvalidUploadException e) {
            logger.warn("Failed uploading template", e);
            errors.rejectValue(e.getFieldName(), e.getMessage(), e.getMessage());
            return null;
         } catch (Exception e) {
            logger.error("Failed importing template", e);
            throw new OspException(e);
         }
      }
   }

   public Map referenceData(Map request, Object command, Errors errors) {
      TemplateUploadForm templateForm = (TemplateUploadForm)command;
      Map model = new HashMap();
      
      ToolSession session = getSessionManager().getCurrentToolSession();
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
            session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
         // here is where we setup the id
         List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         if (refs.size() == 1) {
            Reference ref = (Reference)refs.get(0);
            Node node = getPresentationManager().getNode(ref);
            templateForm.setUploadedTemplate(node.getId());
            model.put("name", node.getDisplayName());
         }
         else {
            templateForm.setUploadedTemplate(null);
         }
      }
      
      session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
      session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);
      return model;
   }

   public boolean supports(Class clazz) {
      return (TemplateUploadForm.class.isAssignableFrom(clazz));
   }

   public void validate(Object obj, Errors errors) {
      TemplateUploadForm templateForm = (TemplateUploadForm) obj;
      if (templateForm.getUploadedTemplate() == null && templateForm.isValidate()){
         errors.rejectValue("uploadedTemplate", "error.required", "required");
      }
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
