/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/AddTemplateController.java $
* $Id:AddTemplateController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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


import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.FilePickerHelper;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.utils.mvc.intf.TypedPropertyEditor;
import org.sakaiproject.metaobj.utils.xml.SchemaFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaInvalidException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.ToolManager;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;
import org.theospi.portfolio.presentation.PresentationFunctionConstants;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.PresentationItemDefinition;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.theospi.portfolio.presentation.model.TemplateFileRef;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.model.CommonFormBean;

public class AddTemplateController extends AbstractWizardFormController {
   final public static int DESCRIBE_PAGE = 0;
   final public static int TEMPLATE_PAGE = 1;
   final public static int CONTENT_PAGE = 2;
   final public static int FILES_PAGE = 3;
   final public static int PICKER_PAGE = 4;
   
   public static final String TEMPLATE_RENDERER = "osp.presentation.template.renderer";
   public static final String TEMPLATE_PROPERTYFILE = "osp.presentation.template.propertyFile";
   public static final String TEMPLATE_SUPPORTFILE = "osp.presentation.template.supportFile";
   public static final String TEMPLATE_PICKER = "osp.presentation.template.picker";
   private static final String STARTING_PAGE = "osp.presentation.template.startingPage";


   private WorksiteManager worksiteManager;
   private AuthenticationManager authManager;
   private PresentationManager presentationManager;
   private List customTypedEditors;
   private AuthorizationFacade authzManager;
   private IdManager idManager;
   private HomeFactory homeFactory;
   private Collection mimeTypes;
   private SessionManager sessionManager;
   private ContentHostingService contentHosting;
   private EntityManager entityManager;
   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;
   
   public static Comparator worksiteHomesComparator;
   static {
    worksiteHomesComparator = new Comparator() {
			public int compare(Object o1, Object o2) {
                return ((ReadableObjectHome)o1).getType().getDescription().toLowerCase().compareTo(((ReadableObjectHome)o2).getType().getDescription().toLowerCase());
			}
        };
   }

   protected ModelAndView processFinish(HttpServletRequest request, HttpServletResponse response, Object o, BindException e) throws Exception {
      PresentationTemplate template = (PresentationTemplate) o;
      Agent agent = getAuthManager().getAgent();
      template.setOwner(agent);
      template.setSiteId(ToolManager.getCurrentPlacement().getContext());

      // remove id's from new dependent object, so hibernate doesn't freak out
      removeTemporaryIds(template);

      template = getPresentationManager().storeTemplate(template);

      Map model = new Hashtable();
      model.put("newPresentationTemplateId", template.getId().getValue());

      return new ModelAndView("listTemplateRedirect", model);
   }

   protected void removeTemporaryIds(PresentationTemplate template){
      PresentationTemplate oldTemplate = new PresentationTemplate();
      if (template.getId() != null && template.getId().getValue().length() > 0){
         oldTemplate = getPresentationManager().getPresentationTemplate(template.getId());
      }

      for (Iterator i= template.getItems().iterator();i.hasNext();){
         PresentationItemDefinition item = (PresentationItemDefinition) i.next();
         if (!oldTemplate.getItems().contains(item)){
            item.setId(null);
         }
      }

      for (Iterator i= template.getFiles().iterator();i.hasNext();){
         TemplateFileRef file = (TemplateFileRef) i.next();
         if (!oldTemplate.getFiles().contains(file)){
            file.setId(null);
         }
      }
   }

   public Object formBackingObject(HttpServletRequest request) throws Exception {
      PresentationTemplate template = new PresentationTemplate();

      // this is an edit, load model
      if (request.getParameter("id") != null) {
         Id id = getIdManager().getId(request.getParameter("id"));
         getAuthzManager().checkPermission(PresentationFunctionConstants.EDIT_TEMPLATE, id);
         template = getPresentationManager().getPresentationTemplate(id);
      } else {
         getAuthzManager().checkPermission(PresentationFunctionConstants.CREATE_TEMPLATE,
               getIdManager().getId(ToolManager.getCurrentPlacement().getContext()));
         template.setNewObject(true);
      }
      ToolSession session = getSessionManager().getCurrentToolSession();
      if (session.getAttribute("SessionPresentationTemplate") != null) {
         template = (PresentationTemplate)session.getAttribute("SessionPresentationTemplate");
         session.removeAttribute("SessionPresentationTemplate");
         request.setAttribute(STARTING_PAGE, Integer.valueOf((String)session.getAttribute(STARTING_PAGE)));
         session.removeAttribute(STARTING_PAGE);
      }
      
      return template;
   }
   
   

   protected ModelAndView processCancel(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) {
      PresentationTemplate template = (PresentationTemplate)command;
      Map model = new Hashtable();
      if (template.getId() != null) {
         model.put("newPresentationTemplateId", template.getId().getValue());
      }

      return new ModelAndView("listTemplateRedirect", model);
   }

   protected void onBindAndValidate(javax.servlet.http.HttpServletRequest request,
                                 java.lang.Object command,
                                 BindException errors,
                                 int page)
                          throws java.lang.Exception {

   }

   protected void validatePage(Object model, Errors errors, int page) {
      PresentationValidator validator = (PresentationValidator) getValidator();
      switch (page) {
         case DESCRIBE_PAGE:
            validator.validateTemplateFirstPage(model, errors);
            break;
         case TEMPLATE_PAGE:
            if (((PresentationTemplate)model).isValidate()) {
               validator.validateTemplateSecondPage(model, errors);
            }
            break;
         case CONTENT_PAGE:
            validator.validateTemplateThirdPage(model, errors);
            break;
         case FILES_PAGE:
            validator.validateTemplateFourthPage(model, errors);
            break;
      }
   }
   
   protected Collection getFormsForSelect(String type) {
      Placement placement = ToolManager.getCurrentPlacement();
      String currentSiteId = placement.getContext();
      Collection commentForms = getAvailableForms(currentSiteId, type);
      
      List retForms = new ArrayList();
      for(Iterator iter = commentForms.iterator(); iter.hasNext();) {
         StructuredArtifactDefinitionBean sad = (StructuredArtifactDefinitionBean) iter.next(); 
         retForms.add(new CommonFormBean(sad.getId().getValue(), sad.getDecoratedDescription(), "form",
                  sad.getOwner().getName(), sad.getModified()));
      }
      
      Collections.sort(retForms, CommonFormBean.beanComparator);
      return retForms;
   }
   
   protected Collection getAvailableForms(String siteId, String type) {
	   String currentUserId = "";
	   Agent currentAgent = getAuthManager().getAgent();
	   if (currentAgent != null && currentAgent.getId() != null)
		   currentUserId = currentAgent.getId().getValue();
      return getStructuredArtifactDefinitionManager().findAvailableHomes(
            getIdManager().getId(siteId), currentUserId, true, true);
   }

   protected Map referenceData(HttpServletRequest request,
                               Object command,
                               Errors errors,
                               int page)
                        throws Exception{
      Map model = new HashMap();
      PresentationTemplate template = (PresentationTemplate) command;
      model.put("currentPage", Integer.valueOf(page + 1));
      model.put("totalPages", Integer.valueOf(4));
      model.put("template", template);
      ToolSession session = getSessionManager().getCurrentToolSession();
      
      
      
      model.put("STARTING_PAGE", STARTING_PAGE);

      switch (page) {
         case DESCRIBE_PAGE :
            break;
         case TEMPLATE_PAGE :
            model.put("TEMPLATE_RENDERER", TEMPLATE_RENDERER);
            model.put("TEMPLATE_PROPERTYFILE", TEMPLATE_PROPERTYFILE);
        	 //ToolSession session = getSessionManager().getCurrentToolSession();
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
                if (session.getAttribute(TEMPLATE_PICKER).equals(TEMPLATE_RENDERER)) {
                   template.setRendererName(nodeName);
                   template.setRenderer(nodeId);
                }
                else {
                   template.setPropertyPageName(nodeName);
                   template.setPropertyPage(nodeId);
                }
                
                session.removeAttribute(TEMPLATE_PICKER);
                session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
                session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);
             }
        	 
        	 
            if (template.getRenderer() != null){
               Node artifact = (Node) getPresentationManager().getNode(template.getRenderer());
               model.put("rendererName",artifact.getDisplayName());
            }
            if (template.getPropertyPage() != null){
               Node artifact = (Node) getPresentationManager().getNode(template.getPropertyPage());
               SchemaNode schemaNode;
               try {
                  schemaNode = SchemaFactory.getInstance().getSchema(artifact.getInputStream());
                  model.put("propertyPageName",artifact.getDisplayName());
                  model.put("elements", schemaNode.getRootChildren());
               }
               catch (SchemaInvalidException e) {
                  template.setPropertyPage(null);
                  String errorMessage = "Invalid outline properties file: " + e.getMessage();
                  errors.rejectValue("propertyPage", errorMessage, errorMessage);
               }
            }
            
            model.put("propertyFormTypes", getFormsForSelect(null));
            break;
         case CONTENT_PAGE :
            Collection mimeTypes = getMimeTypes();
            model.put("mimeTypeListSize", Integer.valueOf(mimeTypes.size()));
            model.put("mimeTypeList", mimeTypes);
            model.put("homes", getHomes());            
            break;
         case FILES_PAGE :
            model.put("TEMPLATE_SUPPORTFILE", TEMPLATE_SUPPORTFILE);
            
            if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
                  session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
               // here is where we setup the id
               
               String fileId = "";
               String nodeName = "";
               String fileType = "";
               List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
               if (refs.size() == 1) {
                  Reference ref = (Reference)refs.get(0);
                  Node node = getPresentationManager().getNode(ref);
                  fileId = node.getId().getValue();
                  nodeName = node.getDisplayName();
                  fileType = node.getFileType();
               }
               if (session.getAttribute(TEMPLATE_PICKER).equals(TEMPLATE_SUPPORTFILE)) {
                  template.getFileRef().setFileId(fileId);
                  template.getFileRef().setArtifactName(nodeName);
                  template.getFileRef().setFileType(fileType);
               }
            
               session.removeAttribute(TEMPLATE_PICKER);
               session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
               session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);
            }
            break;
         case PICKER_PAGE :       
            session.setAttribute(TEMPLATE_PICKER, request.getParameter("pickerField"));
            session.setAttribute("SessionPresentationTemplate", template);
            session.setAttribute(STARTING_PAGE, request.getParameter("returnPage"));
            
            List files = new ArrayList();
            String filter = "";
            
            String pickField = (String)request.getParameter("pickerField");
            String id = "";
            if (pickField.equals(TEMPLATE_RENDERER)) {
               filter = "org.sakaiproject.content.api.ContentResourceFilter.xslFile";
               if (template.getRenderer() != null) {
                  id = getContentHosting().resolveUuid(template.getRenderer().getValue());
               }
            }
            else if (pickField.equals(TEMPLATE_PROPERTYFILE)) {
               filter = "org.sakaiproject.content.api.ContentResourceFilter.metaobjFile";
               if (template.getPropertyPage() != null) {
                  id = getContentHosting().resolveUuid(template.getPropertyPage().getValue());
               }
            }
            else if (pickField.equals(TEMPLATE_SUPPORTFILE) && template.getFileRef() != null && template.getFileRef().getFileId() != null) {
               id = getContentHosting().resolveUuid(template.getFileRef().getFileId());
            }
            if (id != null && !id.equals("")) {
               Reference ref = getEntityManager().newReference(getContentHosting().getResource(id).getReference());
               files.add(ref);              
               session.setAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS, files);
            }
            
            if (!filter.equals(""))
               session.setAttribute(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER, 
                     ComponentManager.get(filter));
            else
               session.removeAttribute(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER);
            
            session.setAttribute(FilePickerHelper.FILE_PICKER_MAX_ATTACHMENTS, Integer.valueOf(1));
            
            break;
      }
      return model;

   }

   protected Collection getHomes() {
      ArrayList list = new ArrayList();
      Map homeMap =  getHomeFactory().getWorksiteHomes(
         getWorksiteManager().getCurrentWorksiteId(), getAuthManager().getAgent().getId().getValue(), true);
      for (Iterator i = homeMap.values().iterator(); i.hasNext();){
           list.add(i.next());
      }
      Collections.sort(list, worksiteHomesComparator);
      return list;
   }


   protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
      dateFormat.setLenient(false);
      binder.registerCustomEditor(Date.class, null, new CustomDateEditor(dateFormat, true));

      for (Iterator i = getCustomTypedEditors().iterator(); i.hasNext();) {
         TypedPropertyEditor editor = (TypedPropertyEditor) i.next();
         binder.registerCustomEditor(editor.getType(), editor);
      }
   }

	protected int getTargetPage(HttpServletRequest request, Object command, Errors errors, int currentPage) {
		int retVal = super.getTargetPage(request, command, errors, currentPage);
      if (isFormSubmission(request)){
         onSubmit(request, command, errors, currentPage);
      }
      return retVal;
	}

   protected int getInitialPage(HttpServletRequest request, Object command) {
      Integer startingPage = (Integer)request.getAttribute(STARTING_PAGE);
      if (startingPage != null) {
         request.removeAttribute(STARTING_PAGE);
         return startingPage.intValue();
      }
      else {
         return super.getInitialPage(request, command);
      }
   }

   /**
    * perform page specific business logic after bind and validate
    * @param request
    * @param command
    * @param errors
    * @param currentPage - page just submitted
    */
   protected void onSubmit(HttpServletRequest request, Object command, Errors errors, int currentPage){
      PresentationTemplate template = (PresentationTemplate) command;
      switch (currentPage) {
         case CONTENT_PAGE :
            // save add item to backing object
            if (template.getItem().getAction() != null &&
                  template.getItem().getAction().equalsIgnoreCase("addItem") &&
                  !errors.hasErrors() ) {

               PresentationItemDefinition itemDefinition = template.getItem();
               if (itemDefinition.getId() == null || itemDefinition.getId().getValue().length() == 0){
                  itemDefinition.setId(getIdManager().createId());
               }
               itemDefinition.setPresentationTemplate(template);
               template.getItemDefinitions().remove(itemDefinition);
               if (itemDefinition.getSequence() == -1) {
                  itemDefinition.setSequence(Integer.MAX_VALUE);
               }
               template.getItemDefinitions().add(itemDefinition);
               template.setItem(new PresentationItemDefinition());
               template.orderItemDefs();
            }
            break;
         case FILES_PAGE :
            if (template.getFileRef().getAction() != null &&
                  template.getFileRef().getAction().equalsIgnoreCase("addFile") &&
                  !errors.hasErrors() ){

               TemplateFileRef file = (TemplateFileRef)template.getFileRef();
               file.setPresentationTemplate(template);
               if (file.getId() == null || file.getId().getValue().length() == 0){
                  file.setId(getIdManager().createId());
               }
               template.getFiles().remove(file);
               template.getFiles().add(file);
               template.setFileRef(new TemplateFileRef());
            }
            break;
      }
   }

   public String getFormAttributeName(){
      return getFormSessionAttributeName();
   }

   protected boolean isFormSubmission(HttpServletRequest request){
      if (request.getParameter("formSubmission") != null &&
            request.getParameter("formSubmission").equalsIgnoreCase("true")){
         return true;
      }
      return super.isFormSubmission(request);
   }

   public Collection getMimeTypes() {
      return mimeTypes;
   }

   public void setMimeTypes(Collection mimeTypes) {
      this.mimeTypes = mimeTypes;
   }

   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
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

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public List getCustomTypedEditors() {
      return customTypedEditors;
   }

   public void setCustomTypedEditors(List customTypedEditors) {
      this.customTypedEditors = customTypedEditors;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }

   public AuthenticationManager getAuthManager() {
      return authManager;
   }

   public void setAuthManager(AuthenticationManager authManager) {
      this.authManager = authManager;
   }

   public PresentationManager getPresentationManager() {
      return presentationManager;
   }

   public void setPresentationManager(PresentationManager presentationManager) {
      this.presentationManager = presentationManager;
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
    * @return the structuredArtifactDefinitionManager
    */
   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
      return structuredArtifactDefinitionManager;
   }

   /**
    * @param structuredArtifactDefinitionManager the structuredArtifactDefinitionManager to set
    */
   public void setStructuredArtifactDefinitionManager(
         StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }
}

