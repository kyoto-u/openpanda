/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.3/matrix/tool/src/java/org/theospi/portfolio/matrix/control/ReviewHelperController.java $
* $Id: ReviewHelperController.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008, 2009 The Sakai Foundation
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
package org.theospi.portfolio.matrix.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.LockManager;
import org.sakaiproject.content.api.ResourceEditingHelper;
import org.sakaiproject.email.cover.DigestService;
import org.sakaiproject.email.cover.EmailService;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.event.api.NotificationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.FormHelper;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.api.UserNotificationPreferencesRegistration;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.util.ResourceLoader;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.matrix.util.FormNameGeneratorUtil;
import org.theospi.portfolio.review.ReviewHelper;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.model.ObjectWithWorkflow;
import org.theospi.portfolio.style.mgt.StyleManager;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.CompletedWizard;
import org.theospi.portfolio.workflow.mgt.WorkflowManager;
import org.theospi.portfolio.workflow.model.Workflow;

public class ReviewHelperController implements Controller {

   private static ResourceLoader myResources = new ResourceLoader("org.theospi.portfolio.matrix.bundle.Messages");
   
   protected final Log LOG = LogFactory.getLog(getClass());
	
   private MatrixManager matrixManager;
   private IdManager idManager = null;
   private ReviewManager reviewManager;
   private WizardManager wizardManager;
   private LockManager lockManager;
   private ContentHostingService contentHosting;
   private StyleManager styleManager;
   private AuthenticationManager authManager = null;
   private SecurityService securityService;
   private WorkflowManager workflowManager;
   private UserNotificationPreferencesRegistration matrixPreferencesConfig = null;
   private UserNotificationPreferencesRegistration wizardPreferencesConfig = null;
	
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String strId = null;
      String lookupId = null;
      String returnView = "return";
      String manager = "";
	   
	   Placement placement = ToolManager.getCurrentPlacement();
	   

      if (request.get("process_type_key") != null) {
         session.put("process_type_key", request.get("process_type_key"));
         session.put(ReviewHelper.REVIEW_TYPE_KEY, request.get(ReviewHelper.REVIEW_TYPE_KEY));
      }

      String processTypeKey = (String)session.get("process_type_key");


      if (processTypeKey != null && !processTypeKey.equals(WizardPage.PROCESS_TYPE_KEY)) {
         lookupId = processTypeKey;
         returnView = "helperDone";
         manager = "org.theospi.portfolio.wizard.mgt.WizardManager";
      }
      else if (processTypeKey != null) {
         lookupId = processTypeKey;
         manager = "matrixManager";
      }
      strId = (String) request.get(lookupId);
      if (strId==null) {
         strId = (String) session.get(lookupId);
      }

      // 
      // If this is the second pass, 
      // then we are creating a new [feedback | evaluation | reflection] review
      //
      String secondPass = (String)session.get("secondPass");
      if (secondPass != null) {
         strId = (String)session.get(lookupId);
         String formType = (String)session.get(ResourceEditingHelper.CREATE_SUB_TYPE);
         String itemId = (String)session.get(ReviewHelper.REVIEW_ITEM_ID);
         String currentReviewId = (String)session.get(ResourceEditingHelper.ATTACHMENT_ID);

         Map<String, Object> model = new HashMap<String, Object>();
         model.put(lookupId, strId);

         String currentSite = placement.getContext();

         // check if this is a new review
         if ( currentReviewId == null && 
              !FormHelper.RETURN_ACTION_CANCEL.equals((String)session.get(FormHelper.RETURN_ACTION_TAG)))  {
            Review review = getReviewManager().createNew("New Review", currentSite);
            review.setDeviceId(formType);
            review.setParent(strId);
            review.setItemId(itemId);
            String strType = (String)session.get(ReviewHelper.REVIEW_TYPE);
            // (note: strType may be null if user hits submit twice)
            if ( strType != null && ! strType.equals("") )
               review.setType(Integer.parseInt(strType));

            if (FormHelper.RETURN_ACTION_SAVE.equals((String)session.get(FormHelper.RETURN_ACTION_TAG)) 
                && session.get(FormHelper.RETURN_REFERENCE_TAG) != null) 
            {
               String artifactId = (String)session.get(FormHelper.RETURN_REFERENCE_TAG);
               session.remove(FormHelper.RETURN_REFERENCE_TAG);
               Node node = getMatrixManager().getNode(getIdManager().getId(artifactId));
               
               review.setReviewContentNode(node);
               review.setReviewContent(node.getId());
               getReviewManager().saveReview(review);
            }
            
            // Lock review content (reflection, feedback, evaluation)
            // (note: reviewContent may be null if user hits submit twice)
            if ( review.getReviewContent() != null )
               getLockManager().lockObject(review.getReviewContent().getValue(),
                                           strId, "lock all review content", true);
            
            boolean isEval = review.getType() == Review.EVALUATION_TYPE;
            boolean isFeedback = review.getType() == Review.FEEDBACK_TYPE;
                        
            if (isEval || isFeedback) {
               processEvalFeedbackNotifications(lookupId, strId, isEval, isFeedback,
            		   currentSite, placement);
            }
            
         }
         
         // otherwise this is an existing review being edited
         else if (currentReviewId != null) {
            // Lock review content (reflection, feedback, evaluation)
            currentReviewId = contentHosting.getUuid( currentReviewId );
            getLockManager().lockObject(currentReviewId,
                                        strId, "lock all review content", true);
                  
         }
       

         // Clean up session attributes         
         session.remove(ResourceEditingHelper.CREATE_TYPE);
         session.remove(ResourceEditingHelper.CREATE_SUB_TYPE);
         session.remove(ReviewHelper.REVIEW_TYPE);
         session.remove(ResourceEditingHelper.CREATE_PARENT);
         session.remove(ReviewHelper.REVIEW_ITEM_ID);
         session.remove(lookupId);
         //session.remove("process_type_key");
         session.remove("secondPass");

		// Check for workflow post process
        if (session.get(ReviewHelper.REVIEW_POST_PROCESSOR_WORKFLOWS) != null) {
           Set workflows = (Set)session.get(ReviewHelper.REVIEW_POST_PROCESSOR_WORKFLOWS);
           List wfList = Arrays.asList(workflows.toArray());
           if(!isSuperUser()){
        	   wfList = removeResetWorkflow(wfList);
           }
           Collections.sort(wfList, Workflow.getComparator());
           model.put("workflows", wfList);
           model.put("manager", manager);
           model.put("obj_id", strId);
           return new ModelAndView("postProcessor", model);
        }
         
         return new ModelAndView(returnView, model);
      }

      //
      // This is the first pass, 
      // so we are presenting the form to create a new [feedback | evaluation | reflection] review
      //
      Id id = getIdManager().getId(strId);
      ObjectWithWorkflow obj = null;
      Set<Workflow> evalWorkflows = new HashSet<Workflow>();

      if (lookupId.equals(WizardPage.PROCESS_TYPE_KEY)) {
    	
    	  WizardPage page = matrixManager.getWizardPage(id);
       	 if(request.get("scaffoldingId") != null){
       		 //scaffoldingId is used for the reflection form when the default user forms
          	 //are selected for a matrix cell
       		 obj = matrixManager.getScaffolding(idManager.getId((String) request.get("scaffoldingId")));
       		WizardPageDefinition wpd = page.getPageDefinition();
       		 evalWorkflows = wpd.getEvalWorkflows();
       		if (evalWorkflows == null || evalWorkflows.size() == 0) {
       			
       			evalWorkflows = getWorkflowManager().createEvalWorkflows(wpd, ((Scaffolding)obj).getEvaluationDevice());
       			ScaffoldingCell sc = getMatrixManager().getScaffoldingCellByWizardPageDef(wpd.getId());
       			sc.getWizardPageDefinition().setEvalWorkflows(evalWorkflows);
       			getMatrixManager().storeScaffoldingCell(sc);
       		}
       	 }else{
       		obj = page.getPageDefinition();
       		evalWorkflows = obj.getEvalWorkflows();
       	 }
       	 
      }else {
    	  CompletedWizard cw = wizardManager.getCompletedWizard(id);
    	  obj = cw.getWizard();
    	  evalWorkflows = obj.getEvalWorkflows();
      }


      String type = (String)session.get(ReviewHelper.REVIEW_TYPE_KEY);
      session.remove(ReviewHelper.REVIEW_TYPE_KEY);
      int intType = Integer.parseInt(type);

      String formTypeId = "";
      String formTypeTitleKey = "";
      session.remove(ReviewHelper.REVIEW_POST_PROCESSOR_WORKFLOWS);
      switch (intType) {
         case Review.FEEDBACK_TYPE:
            formTypeId = obj.getReviewDevice().getValue();
            formTypeTitleKey = "osp.reviewType." + Review.FEEDBACK_TYPE;
            break;
         case Review.EVALUATION_TYPE:
            formTypeId = obj.getEvaluationDevice().getValue();
            session.put(ReviewHelper.REVIEW_POST_PROCESSOR_WORKFLOWS,
            		evalWorkflows);
            formTypeTitleKey = "osp.reviewType." + Review.EVALUATION_TYPE;
            break;
         case Review.REFLECTION_TYPE:
            formTypeId = obj.getReflectionDevice().getValue();
            formTypeTitleKey = "osp.reviewType." + Review.REFLECTION_TYPE;
            break;
      }


      String formView = "formCreator";
      session.put(ReviewHelper.REVIEW_TYPE, type);
      session.put(ResourceEditingHelper.CREATE_TYPE,
            ResourceEditingHelper.CREATE_TYPE_FORM);


      formView = setupSessionInfo(request, session, formTypeId, formTypeTitleKey, strId, placement);
      session.put("page_id", strId);
      session.put("secondPass", "true");
      return new ModelAndView(formView);

   }
   
   /**
    * Determine if a notification should be sent out and send it if needed
    * @param lookupId
    * @param strId
    * @param isEval
    * @param isFeedback
    * @param currentSite
    * @param cellPageType
    * @param cellPageTypeBig
    * @param matrixWizardType
    */
   private void processEvalFeedbackNotifications(String lookupId, String strId, 
		   boolean isEval, boolean isFeedback, String currentSite, Placement placement) {
	 //send out notification emails
       boolean sendNotification = true;
       boolean skipNotification = false;
       
       String cellPageType = "";
       String cellPageTypeBig = "";
       String matrixWizardType = "";
       String cellPageName = "";
       String matrixWizardName = "";
       String to = null;
       String toUserId = null;
       Agent owner = null;
       String notificationId = "";
       
       if ("page_id".equals(lookupId)) {
    	   Id pageId = getIdManager().getId(strId);
    	   WizardPage wizPage = getMatrixManager().getWizardPage(pageId);
    	   owner = wizPage.getOwner();
    	   try {
    		   User user = UserDirectoryService.getUser(owner.getId().getValue()); 
    		   if (user != null) {
    			   to = user.getEmail();
    			   toUserId = user.getId();
    		   }
    	   } catch (UserNotDefinedException e) {
    		   LOG.warn("Unable to find user " + e.getId() + " to get email address for notification");
    	   }
    	   
    	   WizardPageDefinition wpd = wizPage.getPageDefinition();
    	   cellPageName = wpd.getTitle();
    	   if (wpd.getType().equals(WizardPageDefinition.WPD_MATRIX_TYPE)) {
    		   cellPageType = myResources.getString("cellType");
    		   cellPageTypeBig = myResources.getString("cellTypeBig");
    		   matrixWizardType = myResources.getString("matrixType");
    		   Scaffolding scaffolding = getMatrixManager().getMatrixByPage(pageId).getScaffolding();
    		   matrixWizardName = scaffolding.getTitle();
    		   if(wpd.isDefaultEvaluationForm()){            			   
    			   sendNotification = !scaffolding.isHideEvaluations();
    		   }else{
    			   sendNotification = !wpd.isHideEvaluations();
    		   }
    		   notificationId = getMatrixPreferencesConfig().getType();
    	   }
    	   else {
    		   //at a wizard page?
    		   cellPageType = myResources.getString("pageType");
    		   cellPageTypeBig = myResources.getString("pageTypeBig");
    		   matrixWizardType = myResources.getString("wizardType");
    		   CompletedWizard cWizard = getWizardManager().getCompletedWizardByPage(pageId);
    		   matrixWizardName = cWizard.getWizard().getName();
    		   notificationId = getWizardPreferencesConfig().getType();
    	   }
       }
       else {
    	   //At a wizard?
    	   skipNotification = true;
       }
       
       String typeStr = myResources.getString("legend_feedback");
       String typeIntroStr = myResources.getString("notification_newFeedback");
       if (isEval) {
    	   typeStr = myResources.getString("legend_evaluation");
    	   typeIntroStr = myResources.getString("notification_aNewEval");
    	   //TODO only send if hide evals is toggled off
    	   
       }
       
       //send email if
       if (!skipNotification && (isFeedback || (sendNotification && isEval))) {
    	   
    	   
    	   String siteName;
    	   try {
    		   siteName = SiteService.getSite(currentSite).getTitle();
    	   } catch (IdUnusedException e) {
    		   siteName="<site not found>";
    	   }
    	    
    	   String directLink = ServerConfigurationService.getServerUrl() + 
    	   		"/direct/matrixcell/" + strId + "/" + placement.getId() + "/viewCell.osp";

    	   String[] subjArray = {cellPageName, matrixWizardName, matrixWizardType, typeStr};
    	   String message_subject = myResources.getFormattedMessage("feedbackEvalNotificationSubject", subjArray);

    	   String[] contentArray = {cellPageType, cellPageName, cellPageTypeBig, matrixWizardType, matrixWizardName, siteName, typeIntroStr, directLink};
    	   String content = myResources.getFormattedMessage("feedbackEvalNotificationBody", contentArray);
         String from = "postmaster@".concat(ServerConfigurationService.getServerName());
    	   from = ServerConfigurationService.getString("setup.request", from);
    	   //String to = ownerUser.getEmail();
    	   
    	   int userPref = getMatrixManager().getNotificationOption(owner.getId().getValue(), notificationId, currentSite);
			
			if (userPref == NotificationService.PREF_DIGEST) {
				LOG.debug("processEvalFeedbackNotifications() - Sending digest to " + to);
				DigestService.digest(toUserId, message_subject, content);
			}
			else if (userPref == NotificationService.PREF_IMMEDIATE) {
				LOG.debug("processEvalFeedbackNotifications() - Sending message to " + to);
				EmailService.send(from, to,
						message_subject, content, to, from, null);
			}
			else {
				LOG.debug("processEvalFeedbackNotifications() - Sending nothing to " + to);
			}
    	   
       }
   }

   private List removeResetWorkflow(List wfList){
	   List newList = new ArrayList();
	   for (Iterator iter = wfList.iterator(); iter.hasNext();) {
		   Workflow wf = (Workflow) iter.next();
		   if(!wf.getTitle().equals("Return Workflow")){
			  newList.add(wf);
		   }
	   }
	   
	   return newList;
   }
   
   private boolean isSuperUser(){
	     return (getSecurityService().isSuperUser(authManager.getAgent().getId().getValue())) ? true : false;
   }
	
   

   /**
    * 
    * @param request
    * @param session
    * @param pageTitle
    * @param formTypeId
    * @param formTypeTitleKey
    * @param ownerEid The eid of the user that owns the object in question (wizard or page)
    * @param pageId the id of the page
    * @return
    */
   protected String setupSessionInfo(Map request, Map<String, Object> session,
                                     String formTypeId, String formTypeTitleKey,
                                     String pageId, Placement placement) {
      String retView = "formCreator";

      // check if this is a request for a new rewiew (i.e. no current_review_id)
      if (request.get("current_review_id") == null) {
         session.remove(ResourceEditingHelper.ATTACHMENT_ID);
         session.put(ResourceEditingHelper.CREATE_TYPE,
               ResourceEditingHelper.CREATE_TYPE_FORM);
         session.put(ResourceEditingHelper.CREATE_SUB_TYPE, formTypeId);

         String objectId = (String)request.get("objectId");
         String objectTitle = (String)request.get("objectTitle");

         String itemId = (String)request.get("itemId");
         session.put(ReviewHelper.REVIEW_ITEM_ID, itemId);

         String formTypeTitle = myResources.getString(formTypeTitleKey);

         List contentResourceList = null;
         try {
            String folderBase = getUserCollection().getId();

            String currentSite = placement.getContext();

            String rootDisplayName = myResources.getString("portfolioInteraction.displayName");
            String rootDescription = myResources.getString("portfolioInteraction.description");

            String folderPath = createFolder(folderBase, "portfolio-interaction", rootDisplayName, rootDescription);
            folderPath = createFolder(folderPath, currentSite, SiteService.getSiteDisplay(currentSite), null);
            folderPath = createFolder(folderPath, objectId, objectTitle, null);
            folderPath = createFolder(folderPath, formTypeId, formTypeTitle, null);
           
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
         session.put(FormHelper.NEW_FORM_DISPLAY_NAME_TAG, FormNameGeneratorUtil.getFormDisplayName(formTypeTitle, 1, contentResourceList));
      } 
		
      // Otherwise, editting an existing review
      else {
         String currentReviewId = (String)request.get("current_review_id");
         session.remove(ResourceEditingHelper.CREATE_TYPE);
         session.remove(ResourceEditingHelper.CREATE_SUB_TYPE);
         session.remove(ResourceEditingHelper.CREATE_PARENT);
         session.put(ResourceEditingHelper.CREATE_TYPE,
            ResourceEditingHelper.CREATE_TYPE_FORM);
         session.put(ResourceEditingHelper.ATTACHMENT_ID, currentReviewId);
         
         // unlock review content for edit
         String reviewContentId = contentHosting.getUuid( currentReviewId );
         if ( getLockManager().isLocked(reviewContentId) ) {
            getLockManager().removeLock(reviewContentId, pageId );
         }
         
         retView = "formEditor";
      }
      session.put(FormHelper.FORM_STYLES,
         getStyleManager().createStyleUrlList(getStyleManager().getStyles(getIdManager().getId(pageId))));

      return retView;
   }

   /**
    * 
    * @param base
    * @param append
    * @param appendDisplay
    * @param appendDescription
    * @return
    */
   protected String createFolder(String base, String append, String appendDisplay, String appendDescription) {
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

   /**
    * 
    * @return
    * @throws TypeException
    * @throws IdUnusedException
    * @throws PermissionException
    */
   protected ContentCollection getUserCollection() throws TypeException, IdUnusedException, PermissionException {
      User user = UserDirectoryService.getCurrentUser();
      String userId = user.getId();
      String wsId = SiteService.getUserSiteId(userId);
      String wsCollectionId = getContentHosting().getSiteCollection(wsId);
      ContentCollection collection = getContentHosting().getCollection(wsCollectionId);
      return collection;
   }

   /**
    * @return Returns the idManager.
    */
   public IdManager getIdManager() {
      return idManager;
   }

   /**
    * @param idManager The idManager to set.
    */
   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   /**
    * @return Returns the matrixManager.
    */
   public MatrixManager getMatrixManager() {
      return matrixManager;
   }

   /**
    * @param matrixManager The matrixManager to set.
    */
   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }

   /**
    * @return Returns the reviewManager.
    */
   public ReviewManager getReviewManager() {
      return reviewManager;
   }

   /**
    * @param reviewManager The reviewManager to set.
    */
   public void setReviewManager(ReviewManager reviewManager) {
      this.reviewManager = reviewManager;
   }

   /**
    * @return Returns the wizardManager.
    */
   public WizardManager getWizardManager() {
      return wizardManager;
   }

   /**
    * @param wizardManager The wizardManager to set.
    */
   public void setWizardManager(WizardManager wizardManager) {
      this.wizardManager = wizardManager;
   }

   public LockManager getLockManager() {
      return lockManager;
   }
   public void setLockManager(LockManager lockManager) {
      this.lockManager = lockManager;
   }

   /**
    * @return the contentHosting
    */
   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   /**
    * @param contentHosting the contentHosting to set
    */
   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   public StyleManager getStyleManager() {
      return styleManager;
   }

   public void setStyleManager(StyleManager styleManager) {
	   this.styleManager = styleManager;
   }

   public SecurityService getSecurityService() {
	   return securityService;
   }

   public void setSecurityService(SecurityService securityService) {
	   this.securityService = securityService;
   }

   public AuthenticationManager getAuthManager() {
	   return authManager;
   }

   public void setAuthManager(AuthenticationManager authManager) {
	   this.authManager = authManager;
   }

   public WorkflowManager getWorkflowManager() {
	   return workflowManager;
   }

   public void setWorkflowManager(WorkflowManager workflowManager) {
	   this.workflowManager = workflowManager;
   }

   public UserNotificationPreferencesRegistration getMatrixPreferencesConfig() {
	   return matrixPreferencesConfig;
   }

   public void setMatrixPreferencesConfig(
		   UserNotificationPreferencesRegistration matrixPreferencesConfig) {
	   this.matrixPreferencesConfig = matrixPreferencesConfig;
   }

   public UserNotificationPreferencesRegistration getWizardPreferencesConfig() {
	   return wizardPreferencesConfig;
   }

   public void setWizardPreferencesConfig(
		   UserNotificationPreferencesRegistration wizardPreferencesConfig) {
	   this.wizardPreferencesConfig = wizardPreferencesConfig;
   }

}
