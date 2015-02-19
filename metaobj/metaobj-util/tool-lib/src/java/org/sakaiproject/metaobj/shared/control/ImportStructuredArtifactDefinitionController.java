/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/ImportStructuredArtifactDefinitionController.java $
 * $Id: ImportStructuredArtifactDefinitionController.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.shared.control;

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.FilePickerHelper;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.exception.*;
import org.sakaiproject.metaobj.shared.model.FormUploadForm;
import org.sakaiproject.metaobj.shared.model.InvalidUploadException;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.tool.api.ToolSession;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

public class ImportStructuredArtifactDefinitionController extends AddStructuredArtifactDefinitionController
      implements Controller, Validator {

   

   public Object formBackingObject(Map request, Map session, Map application) {

      FormUploadForm backingObject = new FormUploadForm();
      return backingObject;
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
                                     Map application, Errors errors) {

      FormUploadForm templateForm = (FormUploadForm) requestModel;
      if (templateForm == null) {
         return new ModelAndView("success");
      }
      if (templateForm.getSubmitAction() != null && templateForm.getSubmitAction().equals("pickImport")) {
         if (templateForm.getUploadedForm() != null && templateForm.getUploadedForm().length() > 0) {
            Reference ref;
            List files = new ArrayList();
            String ids[] = templateForm.getUploadedForm().split(",");
            for (int i = 0; i < ids.length; i++) {
               try {
                  String id = ids[i];
                  id = getContentHosting().resolveUuid(id);
                  String rid = getContentHosting().getResource(id).getReference();
                  ref = getEntityManager().newReference(rid);
                  files.add(ref);
               }
               catch (PermissionException e) {
                  logger.error("", e);
               }
               catch (IdUnusedException e) {
                  logger.error("", e);
               }
               catch (TypeException e) {
                  logger.error("", e);
               }
            }
            session.put(FilePickerHelper.FILE_PICKER_ATTACHMENTS, files);
         }
         return new ModelAndView("pickImport");
      }
      else {
         String view = "success";
         if (templateForm.getUploadedForm().length() > 0) {
            String ids[] = templateForm.getUploadedForm().split(",");
            for (int i = 0; i < ids.length; i++) {
               try {
                  String id = ids[i];
                  if (!getStructuredArtifactDefinitionManager().importSADResource(getWorksiteManager().getCurrentWorksiteId(), id, true)) {
                     errors.rejectValue("uploadedForm", "error.format", "File format not recognized");

                     view = "failed";
                  }
               }
               catch (InvalidUploadException e) {
                  logger.warn("Failed uploading template", e);
                  errors.rejectValue(e.getFieldName(), e.getMessage(), e.getMessage());
                  view = "failed";
               }
               catch(UnsupportedFileTypeException ufte) {
                  logger.warn("Failed uploading template", ufte);
                  errors.rejectValue("uploadedForm", ufte.getMessage(), ufte.getMessage());
                  view = "failed";
               }
               catch(ImportException ie) {
                  logger.warn("Failed uploading template", ie);
                  errors.rejectValue("uploadedForm", ie.getMessage(), ie.getMessage());
                  view = "failed";
               }
               catch (Exception e) {
                  logger.error("Failed importing template", e);
                  view = "failed";
               }
            }
         }
         Map model = new Hashtable();
         return new ModelAndView(view, model);
      }
   }

   public Map referenceData(Map request, Object command, Errors errors) {
      FormUploadForm templateForm = (FormUploadForm) command;
      Map model = new HashMap();

      ToolSession session = getSessionManager().getCurrentToolSession();
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
            session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
         // here is where we setup the id
         List refs = (List) session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         if (refs.size() >= 1) {
            String ids = "";
            StringBuffer names = new StringBuffer();

            for (Iterator iter = refs.iterator(); iter.hasNext();) {
               Reference ref = (Reference) iter.next();
               String nodeId = getContentHosting().getUuid(ref.getId());
               String id = getContentHosting().resolveUuid(nodeId);

               ContentResource resource = null;
               try {
                  resource = getContentHosting().getResource(id);
               }
               catch (PermissionException pe) {
                  throw new RuntimeException("Failed loading content: no permission to view file", pe);
               }
               catch (TypeException pe) {
                  throw new RuntimeException("Wrong type", pe);
               }
               catch (IdUnusedException pe) {
                  throw new RuntimeException("UnusedId: ", pe);
               }


               if (ids.length() > 0) {
                  ids += ",";
               }
               ids += nodeId;
               names.append(resource.getProperties().getProperty(resource.getProperties().getNamePropDisplayName()) + " ");
            }
            templateForm.setUploadedForm(ids);
            model.put("name", names.toString());
         }
         else {
            templateForm.setUploadedForm(null);
         }
      }

      session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
      session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);
      session.setAttribute(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER,
            ComponentManager.get("org.sakaiproject.content.api.ContentResourceFilter.formUploadStyleFile"));
      return model;
   }

   public boolean supports(Class clazz) {
      return (FormUploadForm.class.isAssignableFrom(clazz));
   }

   public void validate(Object obj, Errors errors) {
      FormUploadForm templateForm = (FormUploadForm) obj;
      if (templateForm.getUploadedForm() == null && templateForm.isValidate()) {
         errors.rejectValue("uploadedForm", "error.required", "required");
      }
   }

   /**
    * override to prevent pulling it from the session.
    * @param incomingModel
    * @param request
    * @param session
    * @param application
    * @return
    * @throws Exception
    */
   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      return incomingModel;
   }
}


