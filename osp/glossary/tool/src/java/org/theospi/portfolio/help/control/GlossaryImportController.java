/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.3/glossary/tool/src/java/org/theospi/portfolio/help/control/GlossaryImportController.java $
* $Id: GlossaryImportController.java 85378 2010-11-23 17:35:53Z ottenhoff@longsight.com $
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

package org.theospi.portfolio.help.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.input.JDOMParseException;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.FilePickerHelper;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.exception.UnsupportedFileTypeException;
import org.sakaiproject.metaobj.shared.model.InvalidUploadException;
import org.sakaiproject.metaobj.utils.mvc.intf.CancelableController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolSession;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.help.model.GlossaryUploadForm;
import org.theospi.portfolio.shared.model.Node;


public class GlossaryImportController extends HelpController implements Validator, CancelableController, FormController {
   protected final transient Log logger = LogFactory.getLog(getClass());

   public static final String PARAM_CANCEL = "_cancel";
   private SessionManager sessionManager;
   private ContentHostingService contentHosting = null;
   private EntityManager entityManager;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
                                     Map application, Errors errors) {

	   GlossaryUploadForm templateForm = (GlossaryUploadForm)requestModel;
      if(templateForm == null)
    	   return new ModelAndView("success");
      
      //  if we are picking the file to import
      if (templateForm.getSubmitAction() != null && templateForm.getSubmitAction().equals("pickImport")) {
         
         // if there are files selected already, then put them into the session
         if (templateForm.getUploadedGlossary() != null && templateForm.getUploadedGlossary().length() > 0) {
            Reference ref;
            List files = new ArrayList();
            String ids[] = templateForm.getUploadedGlossary().split(",");
            
            // get a list of references of the selected files
            for(int i = 0; i < ids.length; i++) {
	            try {
		                String id = ids[i];
		                id = getContentHosting().resolveUuid(id);
		                String rid = getContentHosting().getResource(id).getReference();
		            	ref = getEntityManager().newReference(rid);
		                files.add(ref);
	            } catch (PermissionException e) {
	               logger.error("", e);
	            } catch (IdUnusedException e) {
	               logger.error("", e);
	            } catch (TypeException e) {
	               logger.error("", e);
	            }
            }
            session.put(FilePickerHelper.FILE_PICKER_ATTACHMENTS, files);
         }
         session.put(FilePickerHelper.FILE_PICKER_MAX_ATTACHMENTS, Integer.valueOf(1));
         return new ModelAndView("pickImport");
         
      } else {
         
         //  if there are files, then we want to import them
    	   if(templateForm.getUploadedGlossary().length() > 0) {
	         String ids[] = templateForm.getUploadedGlossary().split(",");
	         for(int i = 0; i < ids.length; i++) {
		        try {
	              String id = ids[i];
	              
		        	  getHelpManager().importTermsResource(id, templateForm.getReplaceExistingTerms());
                 session.put(TRANSFER_CONTROLLER_SESSION_MESSAGE, 
                                      TRANSFER_MESSAGE_IMPORT_SUCCESS);
              } catch (UnsupportedFileTypeException e) {
                 logger.error("Failed uploading glossary terms", e);
                 session.put(TRANSFER_CONTROLLER_SESSION_MESSAGE, 
                                      TRANSFER_MESSAGE_IMPORT_BAD_FILE);
              } catch (InvalidUploadException e) {
                 logger.error("Failed uploading glossary terms", e);
                 //errors.rejectValue(e.getFieldName(), e.getMessage(), e.getMessage());
                 session.put(TRANSFER_CONTROLLER_SESSION_MESSAGE, 
                                      TRANSFER_MESSAGE_IMPORT_FAILED);
              } catch (JDOMParseException e) {
                 logger.error("Failed uploading glossary terms: Couldn't parse the file", e);
                 //errors.rejectValue(e.getFieldName(), e.getMessage(), e.getMessage());
                 session.put(TRANSFER_CONTROLLER_SESSION_MESSAGE, 
                                      TRANSFER_MESSAGE_IMPORT_BAD_PARSE);
		        } catch (Exception e) {
		           logger.error("Failed importing glossary terms", e);
                 session.put(TRANSFER_CONTROLLER_SESSION_MESSAGE, 
                                      TRANSFER_MESSAGE_IMPORT_FAILED);
		        }
	         }
    	   }
        session.remove(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
        session.remove(FilePickerHelper.FILE_PICKER_MAX_ATTACHMENTS);
         Map model = new Hashtable();
    	   return new ModelAndView("success", model);
      }
   }

   public Map referenceData(Map request, Object command, Errors errors) {
	  GlossaryUploadForm templateForm = (GlossaryUploadForm)command;
      Map model = new HashMap();
      
      ToolSession session = getSessionManager().getCurrentToolSession();
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
            session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
         // here is where we setup the id
         List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         if (refs.size() >= 1) {
        	 StringBuffer idsBuffer = new StringBuffer();
        	String ids = "";
        	StringBuffer namesBuffer = new StringBuffer();
        	String names = "";
        	
        	for(Iterator iter = refs.iterator(); iter.hasNext(); ) {
	            Reference ref = (Reference)iter.next();
	    		String nodeId = getContentHosting().getUuid(ref.getId());
	
	            Node node = getHelpManager().getNode(getIdManager().getId(nodeId));
	            
	            if(idsBuffer.length() > 0)
	            	idsBuffer.append(",");
	            idsBuffer.append(node.getId());
	            namesBuffer.append(node.getDisplayName()).append(" ");
        	}
        	names = namesBuffer.toString();
        	ids = idsBuffer.toString();
            templateForm.setUploadedGlossary(ids);
            model.put("name", names);
         }
         else {
            templateForm.setUploadedGlossary(null);
         }
      }
      
      session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
      session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);
      session.setAttribute(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER,
              ComponentManager.get("org.sakaiproject.content.api.ContentResourceFilter.glossaryStyleFile"));
      return model;
   }

   public boolean supports(Class clazz) {
      return (GlossaryUploadForm.class.isAssignableFrom(clazz));
   }

   public void validate(Object obj, Errors errors) {
	   GlossaryUploadForm templateForm = (GlossaryUploadForm) obj;
      if ((templateForm.getUploadedGlossary() == null || templateForm.getUploadedGlossary().length() == 0) && templateForm.isValidate()){
         errors.rejectValue("uploadedGlossary", "error.required", "required");
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
   
	/**
	 * Return if cancel action is specified in the request.
	 * <p>Default implementation looks for "_cancel" parameter in the request.
	 * @param request current HTTP request
	 * @see #PARAM_CANCEL
	 */
   public boolean isCancel(Map request) {
       return request.containsKey(PARAM_CANCEL);
   }

   public ModelAndView processCancel(Map request, Map session, Map application,
                                     Object command, Errors errors) throws Exception {

       return new ModelAndView("cancel");
   }
}
