/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.3/matrix/tool/src/java/org/theospi/portfolio/matrix/control/CellFormPickerController.java $
* $Id: CellFormPickerController.java 91945 2011-04-15 16:39:00Z botimer@umich.edu $
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
package org.theospi.portfolio.matrix.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.FilePickerHelper;
import org.sakaiproject.content.api.ResourceEditingHelper;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.shared.FormHelper;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.WizardPageForm;
import org.theospi.portfolio.matrix.util.FormNameGeneratorUtil;
import org.sakaiproject.metaobj.security.AllowMapSecurityAdvisor;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.tool.BaseFormResourceFilter;

public class CellFormPickerController extends CellController implements FormController, LoadObjectController {

   private static ResourceLoader myResources = new ResourceLoader("org.theospi.portfolio.matrix.bundle.Messages");
	
   protected final Log logger = LogFactory.getLog(getClass());
   private ContentHostingService contentHosting;
   private EntityManager entityManager;
   private SecurityService securityService = null;

   public static final String HELPER_CREATOR = "filepicker.helper.creator";
   public static final String HELPER_PICKER = "filepicker.helper.picker";

   public Object fillBackingObject(Object incomingModel, Map request,
                                   Map session, Map application) throws Exception {

      String pageId = (String) request.get("page_id");
      if (pageId == null) {
         pageId = (String)session.get("page_id");
      }
      WizardPage page = getMatrixManager().getWizardPage(getIdManager().getId(pageId));
      if ( page == null ) // error should already be logged
         return null;

      if ((session.get(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
            session.get(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) ||
            (FormHelper.RETURN_ACTION_SAVE.equals((String)session.get(FormHelper.RETURN_ACTION_TAG)) &&
            session.get(FormHelper.RETURN_REFERENCE_TAG) != null)) {


         if (session.get(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
               session.get(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {
            // here is where we setup the id
            List refs = (List)session.get(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
            //if (session.get(WHICH_HELPER_KEY).equals(HELPER_PICKER))
            if (HELPER_PICKER.equals((String)session.get(WHICH_HELPER_KEY)) &&
                  !"true".equals((String)session.get(KEEP_HELPER_LIST)))
               page.getPageForms().clear();

            for (Iterator iter = refs.iterator(); iter.hasNext();) {
               Reference ref = (Reference) iter.next();
               Node node = getMatrixManager().getNode(ref);
               processPageForm(node, page);
            }
            //getMatrixManager().storePage(page);

            session.remove(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
            session.remove(FilePickerHelper.FILE_PICKER_CANCEL);

         }
         if (FormHelper.RETURN_ACTION_SAVE.equals((String)session.get(FormHelper.RETURN_ACTION_TAG)) &&
               session.get(FormHelper.RETURN_REFERENCE_TAG) != null) {
            String artifactId = (String)session.get(FormHelper.RETURN_REFERENCE_TAG);
            Node node = getMatrixManager().getNode(getIdManager().getId(artifactId));
            processPageForm(node, page);

            session.remove(FormHelper.RETURN_REFERENCE_TAG);
            session.remove(FormHelper.RETURN_ACTION_TAG);


         }
         getMatrixManager().storePage(page);
         session.remove(ResourceEditingHelper.CREATE_TYPE);

         session.remove(ResourceEditingHelper.CREATE_PARENT);
         session.remove(ResourceEditingHelper.CREATE_SUB_TYPE);
         session.remove(ResourceEditingHelper.ATTACHMENT_ID);
         session.remove(WHICH_HELPER_KEY);
         session.remove(KEEP_HELPER_LIST);
      }
      return null;
   }

   protected void processPageForm(Node node, WizardPage page) {
      Id id = node.getId();
      WizardPageForm wpf = new WizardPageForm();
      wpf.setArtifactId(id);
      wpf.setFormType(node.getResource().getProperties().getProperty(
            node.getResource().getProperties().getNamePropStructObjType()));
      wpf.setWizardPage(page);
      wpf.setNewId(getIdManager().createId());
      page.getPageForms().add(wpf);
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String attachFormAction = (String) request.get("attachFormAction");
      String createFormAction = (String) request.get("createFormAction");
      String viewFormAction = (String) request.get("viewFormAction");
      String pageId = (String) request.get("page_id");
      if (pageId == null) {
         pageId = (String)session.get("page_id");
         session.remove("page_id");
      }
      WizardPage page = getMatrixManager().getWizardPage(getIdManager().getId(pageId));
      String pageTitle = page.getPageDefinition().getTitle();

      if (attachFormAction != null) {
         //session.setAttribute(TEMPLATE_PICKER, request.getParameter("pickerField"));
         //session.setAttribute("SessionPresentationTemplate", template);
         //session.setAttribute(STARTING_PAGE, request.getParameter("returnPage"));

         List<Reference> files = new ArrayList<Reference>();

         //String pickField = (String)request.get("formType");
         String id = "";
         for (Iterator iter = page.getPageForms().iterator(); iter.hasNext();) {
            WizardPageForm wpf = (WizardPageForm) iter.next();
            if (attachFormAction.equals(wpf.getFormType())) {
               id = getContentHosting().resolveUuid(wpf.getArtifactId().getValue());
               Reference ref = getEntityManager().newReference(getContentHosting().getReference(id));
               files.add(ref);
            }
         }
         BaseFormResourceFilter crf = new BaseFormResourceFilter();

         crf.getFormTypes().add(attachFormAction);
         session.put(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER, crf);
         session.put("page_id", pageId);
         session.put(FilePickerHelper.FILE_PICKER_ATTACHMENTS, files);
         session.put(WHICH_HELPER_KEY, HELPER_PICKER);
         session.put(KEEP_HELPER_LIST, "false");

         //Start in user's resources area
         //osp-ui-05
         String siteId = SiteService.getUserSiteId(getSessionManager().getCurrentSessionUserId());
         String collectionId = getContentHosting().getSiteCollection(siteId);
         session.put(FilePickerHelper.DEFAULT_COLLECTION_ID, collectionId);

         return new ModelAndView("formPicker");

      }
      else if (createFormAction != null) {
         String view = setupSessionInfo(request, session, pageId, createFormAction);
         session.put(WHICH_HELPER_KEY, HELPER_CREATOR);
         return new ModelAndView(view);
      }
      else if (viewFormAction != null) {
         setupSessionInfo(request, session, pageId, viewFormAction);
         getSecurityService().pushAdvisor(new AllowMapSecurityAdvisor(
               ContentHostingService.EVENT_RESOURCE_READ,
               (String)request.get("current_form_id")));
         return new ModelAndView("formViewer");
      }
      session.remove(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER);
      return new ModelAndView("page", "page_id", pageId);
   }

   protected String setupSessionInfo(Map request, Map<String, Object> session,
                                     String pageId, String formTypeId) {
      String retView = "formCreator";
      session.put("page_id", pageId);
      session.put(FormHelper.FORM_STYLES, getStyleManager().createStyleUrlList(getStyleManager().getStyles(getIdManager().getId(pageId))));

      Placement placement = ToolManager.getCurrentPlacement();
      String currentSite = placement.getContext();
      
      String objectId = (String)request.get("objectId");
      String objectTitle = (String)request.get("objectTitle");

      session.put(FormHelper.XSL_SITE_ID, currentSite);
      session.put(FormHelper.XSL_WIZARD_PAGE_ID, pageId);
      session.put(FormHelper.XSL_OBJECT_ID, objectId);
      session.put(FormHelper.XSL_OBJECT_TITLE, objectTitle);
      
      if (request.get("current_form_id") == null) {
         session.remove(ResourceEditingHelper.ATTACHMENT_ID);
         session.put(ResourceEditingHelper.CREATE_TYPE,
               ResourceEditingHelper.CREATE_TYPE_FORM);
         session.put(ResourceEditingHelper.CREATE_SUB_TYPE, formTypeId);

         StructuredArtifactDefinitionBean bean = getStructuredArtifactDefinitionManager().loadHome(formTypeId);
         List contentResourceList = null;
         try {
            String folderBase = getUserCollection().getId();

            String rootDisplayName = myResources.getString("portfolioInteraction.displayName");
            String rootDescription = myResources.getString("portfolioInteraction.description");

            String folderPath = createFolder(folderBase, "portfolio-interaction", rootDisplayName, rootDescription);
            folderPath = createFolder(folderPath, currentSite, SiteService.getSiteDisplay(currentSite), null);
            folderPath = createFolder(folderPath, objectId, objectTitle, null);
            folderPath = createFolder(folderPath, formTypeId, bean.getDescription(), null);

            contentResourceList = this.getContentHosting().getAllResources(folderPath);
            
            session.put(FormHelper.PARENT_ID_TAG, folderPath);
         } catch (TypeException e) {
            throw new RuntimeException("Failed to redirect to helper", e);
         } catch (IdUnusedException e) {
            throw new RuntimeException("Failed to redirect to helper", e);
         } catch (PermissionException e) {
            throw new RuntimeException("Failed to redirect to helper", e);
         }

         //CWM OSP-UI-09 - for auto naming
         session.put(FormHelper.NEW_FORM_DISPLAY_NAME_TAG, FormNameGeneratorUtil.getFormDisplayName(bean.getDescription(), 1, contentResourceList));
      } else {
         //session.put(ResourceEditingHelper.ATTACHMENT_ID, request.get("current_form_id"));
         session.remove(ResourceEditingHelper.CREATE_TYPE);
         session.remove(ResourceEditingHelper.CREATE_SUB_TYPE);
         session.remove(ResourceEditingHelper.CREATE_PARENT);
         session.put(ResourceEditingHelper.CREATE_TYPE,
            ResourceEditingHelper.CREATE_TYPE_FORM);
         session.put(ResourceEditingHelper.ATTACHMENT_ID, request.get("current_form_id"));
         retView = "formEditor";
      }
      return retView;
   }

   protected String createFolder(String base, String append, String appendDisplay, String appendDescription) {
      //String folder = "/user/" +
      //SessionManager.getCurrentSessionUserId() +
      //PresentationManager.PRESENTATION_PROPERTIES_FOLDER_PATH;
      String folder = base + append + "/";

      try {
         ContentCollectionEdit propFolder = getContentHosting().addCollection(folder);
         propFolder.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, appendDisplay);
         propFolder.getPropertiesEdit().addProperty(ResourceProperties.PROP_DESCRIPTION, appendDescription);
         getContentHosting().commitCollection(propFolder);
         return propFolder.getId();
      }
      catch (IdUsedException e) {
         // ignore... it is already there.
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }
      return folder;
   }

   protected ContentCollection getUserCollection() throws TypeException, IdUnusedException, PermissionException {
      User user = UserDirectoryService.getCurrentUser();
      String userId = user.getId();
      String wsId = SiteService.getUserSiteId(userId);
      String wsCollectionId = getContentHosting().getSiteCollection(wsId);
      ContentCollection collection = getContentHosting().getCollection(wsCollectionId);
      return collection;
   }

   /**
    * @return Returns the contentHosting.
    */
   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   /**
    * @param contentHosting The contentHosting to set.
    */
   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   /**
    * @return Returns the entityManager.
    */
   public EntityManager getEntityManager() {
      return entityManager;
   }

   /**
    * @param entityManager The entityManager to set.
    */
   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

   public SecurityService getSecurityService() {
      return securityService;
   }

   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }

}
