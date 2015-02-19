/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/AddStructuredArtifactDefinitionController.java $
 * $Id: AddStructuredArtifactDefinitionController.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Hashtable;
import java.io.InputStream;

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.FilePickerHelper;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.security.AuthorizationFailedException;
import org.sakaiproject.metaobj.shared.SharedFunctionConstants;
import org.sakaiproject.metaobj.shared.FormHelper;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.util.FormattedText;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

/**
 * @author chmaurer
 */
public class AddStructuredArtifactDefinitionController extends AbstractStructuredArtifactDefinitionController
      implements CustomCommandController, FormController, LoadObjectController {

   private SessionManager sessionManager;
   private ContentHostingService contentHosting;
   private TransformerFactory transformerFactory;
   private URIResolver uriResolver;

   public Object formBackingObject(Map request, Map session, Map application) {

      //check to see if you have create permissions
      checkPermission(SharedFunctionConstants.CREATE_ARTIFACT_DEF);

      StructuredArtifactDefinitionBean backingObject = new StructuredArtifactDefinitionBean();
      backingObject.setOwner(getAuthManager().getAgent());
      return backingObject;
   }

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      if (session.get(StructuredArtifactDefinitionManager.SAD_SESSION_TAG) != null) {
         return session.remove(StructuredArtifactDefinitionManager.SAD_SESSION_TAG);
      }
      else {
         return incomingModel;
      }
   }

   public ModelAndView handleRequest(Object requestModel, Map request,
                                     Map session, Map application, Errors errors) {
      StructuredArtifactDefinitionBean sad = (StructuredArtifactDefinitionBean) requestModel;

      if (StructuredArtifactDefinitionValidator.PICK_SCHEMA_ACTION.equals(sad.getFilePickerAction()) ||
            StructuredArtifactDefinitionValidator.PICK_TRANSFORM_ACTION.equals(sad.getFilePickerAction()) ||
            StructuredArtifactDefinitionValidator.PICK_ALTCREATEXSLT_ACTION.equals(sad.getFilePickerAction()) ||
            StructuredArtifactDefinitionValidator.PICK_ALTVIEWXSLT_ACTION.equals(sad.getFilePickerAction())) {
         session.put(StructuredArtifactDefinitionManager.SAD_SESSION_TAG, sad);
         
         //set the filter for xsl files since it is 3 out of 4 cases
         session.put(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER,
               ComponentManager.get("org.sakaiproject.content.api.ContentResourceFilter.metaobjFile.xsl"));
         
         if (StructuredArtifactDefinitionValidator.PICK_SCHEMA_ACTION.equals(sad.getFilePickerAction())) {
            //set the filter for xsd files only in this case
            session.put(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER,
                  ComponentManager.get("org.sakaiproject.content.api.ContentResourceFilter.metaobjFile"));
            session.put(FilePickerHelper.FILE_PICKER_TITLE_TEXT, getMessage("text_selectXSD"));
            session.put(FilePickerHelper.FILE_PICKER_INSTRUCTION_TEXT, getMessage("text_selectXSD_instructions"));
         }
         
         session.put(FilePickerHelper.FILE_PICKER_MAX_ATTACHMENTS, new Integer(1));
         
         List files = new ArrayList();
         if (StructuredArtifactDefinitionValidator.PICK_ALTCREATEXSLT_ACTION.equals(sad.getFilePickerAction())) {
            if (sad.getAlternateCreateXslt() != null) {
               String id = getContentHosting().resolveUuid(sad.getAlternateCreateXslt().getValue());
               Reference ref = getEntityManager().newReference(getContentHosting().getReference(id));
               files.add(ref);
            }
            
            session.put(FilePickerHelper.FILE_PICKER_ATTACHMENTS, files);
            session.put(FilePickerHelper.FILE_PICKER_TITLE_TEXT, getMessage("text_selectAltCreateXsl"));
            session.put(FilePickerHelper.FILE_PICKER_INSTRUCTION_TEXT, getMessage("text_selectAltCreateXsl_instructions"));
         }
         else if (StructuredArtifactDefinitionValidator.PICK_ALTVIEWXSLT_ACTION.equals(sad.getFilePickerAction())) {
            if (sad.getAlternateViewXslt() != null) {
               String id = getContentHosting().resolveUuid(sad.getAlternateViewXslt().getValue());
               Reference ref = getEntityManager().newReference(getContentHosting().getReference(id));
               files.add(ref);
            }
            
            session.put(FilePickerHelper.FILE_PICKER_ATTACHMENTS, files);
            session.put(FilePickerHelper.FILE_PICKER_TITLE_TEXT, getMessage("text_selectAltViewXsl"));
            session.put(FilePickerHelper.FILE_PICKER_INSTRUCTION_TEXT, getMessage("text_selectAltViewXsl_instructions"));
         }
         
         return new ModelAndView("pickSchema");
      }

      if (request.get("systemOnly") == null) {
         sad.setSystemOnly(false);
      }

      if (sad.getSchemaFile() != null) {
         try {
            getStructuredArtifactDefinitionManager().validateSchema(sad);
         }
         catch (Exception e) {
            logger.warn("", e);
            String errorMessage = "error reading schema file: " + e.getMessage();
            sad.setSchemaFile(null);
            errors.rejectValue("schemaFile", "schema_file_error", 
               new Object[]{e.getMessage()}, errorMessage);
         }
      }

      if (sad.getAlternateCreateXslt() != null) {
         if (!validateXslt(sad.getAlternateCreateXslt(), "alternateCreateXslt", errors)) {
            sad.setAlternateCreateXslt(null);
            sad.setAlternateCreateXsltName(null);
         }
      }
      
      if (sad.getAlternateViewXslt() != null) {
         if (!validateXslt(sad.getAlternateViewXslt(), "alternateViewXslt", errors)) {
            sad.setAlternateViewXslt(null);
            sad.setAlternateViewXsltName(null);
         }
      }
      
      if (sad.getInstruction() != null) {
         StringBuilder htmlErrors = new StringBuilder();
         String newText = FormattedText.processFormattedText(sad.getInstruction(), htmlErrors);
      
         if (htmlErrors.length() > 0) {
            errors.rejectValue("instruction", "instruction_error", 
               new Object[]{htmlErrors.toString()}, htmlErrors.toString());
         }
         else {
            sad.setInstruction(newText);
         }
      }
      
      if (errors.hasErrors()) {
         return new ModelAndView("failure");
      }
      
      if ("preview".equals(request.get("previewAction"))) {
         session.put(StructuredArtifactDefinitionManager.SAD_SESSION_TAG, sad);
         session.put(FormHelper.PREVIEW_HOME_TAG, sad);
         return new ModelAndView("preview");
      }

      try {
         if (!getStructuredArtifactDefinitionManager().isGlobal()) {
            sad.setSiteId(getWorksiteManager().getCurrentWorksiteId().getValue());
         }

         save(sad, errors);
      }
      catch (AuthorizationFailedException e) {
         throw e;
      }
      catch (PersistenceException e) {
         errors.rejectValue(e.getField(), e.getErrorCode(), e.getErrorInfo(),
               e.getDefaultMessage());
      }
      catch (Exception e) {
         logger.warn("", e);
         String errorMessage = "error transforming or saving artifacts: " + e.getMessage();
         errors.rejectValue("xslConversionFileId", errorMessage, errorMessage);
         sad.setXslConversionFileId(null);
         return new ModelAndView("failure");
      }

      if (errors.getErrorCount() > 0) {
         return new ModelAndView("failure");
      }
      Map model = new Hashtable();
      model.put("newFormId", sad.getId().getValue());
      
      return new ModelAndView("success", model); //prepareListView(request, sad.getId().getValue());
   }

   protected boolean validateXslt(Id xsltResource, String field, Errors errors) {
      try {
         ContentResource resource = getContentResource(xsltResource);
         InputStream is = resource.streamContent();
         getTransformerFactory().newTransformer(new StreamSource(is));
         return true;
      }
      catch (Exception e) {
         logger.warn("", e);
         String errorMessage = "error validating xslt file: " + e.getMessage();
         errors.rejectValue(field, "render_file_error", 
            new Object[]{e.getMessage()}, errorMessage);
      }
      return false;
   }

   protected void save(StructuredArtifactDefinitionBean sad, Errors errors) {
      //check to see if you have create permissions
      checkPermission(SharedFunctionConstants.CREATE_ARTIFACT_DEF);

      getStructuredArtifactDefinitionManager().save(sad);
   }

   public Map referenceData(Map request, Object command, Errors errors) {
      Map base = super.referenceData(request, command, errors);
      StructuredArtifactDefinitionBean sad = (StructuredArtifactDefinitionBean) command;

      ToolSession session = getSessionManager().getCurrentToolSession();
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
            session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
         // here is where we setup the id
         List refs = (List) session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         
         Id nodeId = null;
         Id nodeUuid = null;
         String nodeName = "";
         
         if (refs != null && refs.size() > 0) {
            Reference ref = (Reference) refs.get(0);
            
            nodeId = getIdManager().getId(ref.getId());
            nodeUuid = getIdManager().getId(getContentHosting().getUuid(ref.getId()));
            nodeName = ref.getProperties().getProperty(ref.getProperties().getNamePropDisplayName());
         }
   
         if (StructuredArtifactDefinitionValidator.PICK_SCHEMA_ACTION.equals(sad.getFilePickerAction())) {
            sad.setSchemaFile(nodeId);
            sad.setSchemaFileName(nodeName);
         }
         else if (StructuredArtifactDefinitionValidator.PICK_ALTCREATEXSLT_ACTION.equals(sad.getFilePickerAction())) {
            sad.setAlternateCreateXslt(nodeUuid);
            sad.setAlternateCreateXsltName(nodeName);
         }
         else if (StructuredArtifactDefinitionValidator.PICK_ALTVIEWXSLT_ACTION.equals(sad.getFilePickerAction())) {
            sad.setAlternateViewXslt(nodeUuid);
            sad.setAlternateViewXsltName(nodeName);
         }
         else if (StructuredArtifactDefinitionValidator.PICK_TRANSFORM_ACTION.equals(sad.getFilePickerAction())) {
            sad.setXslConversionFileId(nodeId);
            sad.setXslFileName(nodeName);
         }
      }

      session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
      session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);

      if (sad.getSchemaFile() != null) {
         try {
            base.put("elements", getStructuredArtifactDefinitionManager().getRootElements(sad));
         }
         catch (Exception e) {
            String errorMessage = "error reading schema file: " + e.getMessage();
            sad.setSchemaFile(null);
            sad.setSchemaFileName(null);
            errors.rejectValue("schemaFile", errorMessage, errorMessage);
         }
      }
      if (sad.getAlternateCreateXslt() != null){
         ContentResource resource = getContentResource(sad.getAlternateCreateXslt());
         if ( resource != null ) {
            String name = resource.getProperties().getProperty(
               resource.getProperties().getNamePropDisplayName());
            sad.setAlternateCreateXsltName(name);
         }
         else {
            logger.warn( this+".referenceData: invalid alternateCreateXslt "+sad.getAlternateCreateXslt() );
            sad.setAlternateCreateXslt(null); 
         }
      }
      if (sad.getAlternateViewXslt() != null){
         ContentResource resource = getContentResource(sad.getAlternateViewXslt());
         if ( resource != null ) {
            String name = resource.getProperties().getProperty(
               resource.getProperties().getNamePropDisplayName());
            sad.setAlternateViewXsltName(name);
         }
         else {
            logger.warn( this+".referenceData: invalid alternateViewXslt "+sad.getAlternateViewXslt() );
            sad.setAlternateViewXslt(null); 
         }
      }      
      return base;
   }
   
   protected ContentResource getContentResource(Id fileId) {
      String id = getContentHosting().resolveUuid(fileId.getValue());
      if ( id == null )
         return null;
		
      ContentResource resource = null;
      try {
         resource = getContentHosting().getResource(id);
      } catch (PermissionException e) {
         logger.error("", e);
         throw new RuntimeException(e);
      } catch (IdUnusedException e) {
         logger.error("", e);
         throw new RuntimeException(e);
      } catch (TypeException e) {
         logger.error("", e);
         throw new RuntimeException(e);
      }
      return resource;
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

   public TransformerFactory getTransformerFactory() {
      if (transformerFactory == null) {
         transformerFactory = TransformerFactory.newInstance();         
      }
      return transformerFactory;
   }

   public void setTransformerFactory(TransformerFactory transformerFactory) {
      this.transformerFactory = transformerFactory;
   }
   
   public URIResolver getUriResolver() {
      return uriResolver;
   }

   public void setUriResolver(URIResolver uriResolver) {
      this.uriResolver = uriResolver;
      getTransformerFactory().setURIResolver(uriResolver);
   }
   
}
