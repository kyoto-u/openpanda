
/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.4/matrix/tool/src/java/org/theospi/portfolio/matrix/control/AddScaffoldingController.java $
* $Id: AddScaffoldingController.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.tool.api.SessionManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.shared.model.WizardMatrixConstants;
import org.theospi.portfolio.security.AudienceSelectionHelper;
import org.theospi.portfolio.shared.model.CommonFormBean;
import org.theospi.portfolio.wizard.WizardFunctionConstants;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.Wizard;


/**
 * @author chmaurer
 */
public class AddScaffoldingController extends BaseScaffoldingController 
   implements FormController, CustomCommandController {

   private SessionManager sessionManager;
   private ContentHostingService contentHosting;
   private EntityManager entityManager;
   private ReviewManager reviewManager;

   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;

   private WizardManager wizardManager;

   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.FormController#referenceData(java.util.Map, java.lang.Object, org.springframework.validation.Errors)
    */
   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = new HashMap();
      model.put("isInSession", EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG);
      
      Scaffolding scaffolding = null;
      if ( command instanceof Scaffolding )
         scaffolding = (Scaffolding)command;
      
      if ( scaffolding != null ){
    	  String worksiteId = scaffolding.getWorksiteId().getValue();
         model.put("isMatrixUsed", scaffolding.isPublished() && getMatrixManager().isScaffoldingUsed( scaffolding ) );
         model.put("evaluators", getMatrixManager().getSelectedUsers(scaffolding, MatrixFunctionConstants.EVALUATE_MATRIX));
         model.put("reviewers", getMatrixManager().getSelectedUsers(scaffolding, MatrixFunctionConstants.REVIEW_MATRIX));
         model.put("evaluationDevices", getEvaluationDevices(worksiteId, scaffolding));
         model.put("reviewDevices", getReviewDevices(worksiteId, scaffolding));
         model.put("reflectionDevices", getReflectionDevices(worksiteId, scaffolding));
         model.put("additionalFormDevices", getAdditionalFormDevices(worksiteId));
         model.put("selectedAdditionalFormDevices",
        		 getSelectedAdditionalFormDevices(scaffolding,worksiteId));
      } 
      else{
         model.put("isMatrixUsed", false );
         model.put("evaluators",null);
         model.put("reviewers",null);         
         model.put("evaluationDevices", null);
         model.put("reviewDevices", null);
         model.put("reflectionDevices", null);
         model.put("additionalFormDevices", null);
         model.put("selectedAdditionalFormDevices", null);

      }
      model.put("ignoreReviewerGroups", ServerConfigurationService.getBoolean(WizardMatrixConstants.PROP_GROUPS_ALLOW_ALL_GLOBAL, false));
      
      
      model.put("enableDafaultMatrixOptions", getMatrixManager().isEnableDafaultMatrixOptions());
      
      return model;
   }
   

   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
    */
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String action = (String) request.get("action");
      if (action == null) action = (String) request.get("submitAction");
      String generateAction = (String)request.get("generateAction");
      String cancelAction = (String)request.get("cancelAction");
      String cancelActionExisting = (String)request.get("cancelActionExisting");
      String addFormAction = (String) request.get("addForm");
      
      Map model = new HashMap();
      
      EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage)session.get(
            EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
      Scaffolding scaffolding = sessionBean.getScaffolding();      
      
      if(request.get("allowRequestFeedback") == null || "false".equals(request.get("allowRequestFeedback").toString())){
    	  scaffolding.setAllowRequestFeedback(false);
      }else{
    	  scaffolding.setAllowRequestFeedback(true);  
      }
      
      if(request.get("hideEvaluations") == null || "false".equals(request.get("hideEvaluations").toString())){
    	  scaffolding.setHideEvaluations(false);
      }else{
    	  scaffolding.setHideEvaluations(true);  
      }

      if (addFormAction != null) {

			String id = (String) request.get("selectAdditionalFormId");
			if ( id != null && !id.equals("") && !scaffolding.getAdditionalForms().contains(id) )
				scaffolding.getAdditionalForms().add(id);
			session.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG,
			"true");
			model.put("scaffolding", scaffolding);
			return new ModelAndView("success", model);
      }

      if (generateAction != null) {
    	  
    	  Map confirmFlags = getMatrixManager().getConfirmFlagsForScaffolding(scaffolding);

    	  if(confirmFlags.size() > 0){
    		  model.putAll(confirmFlags);
    		  return new ModelAndView("editScaffoldingConfirm", model);   
    	  }

    	  scaffolding = saveScaffolding(scaffolding);
    	  
    	  session.remove(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG);
    	  session.remove(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
    	  model.put("scaffolding_id", scaffolding.getId());
    	  return new ModelAndView("view", model);
      }
      if (cancelAction != null) {
    	  return new ModelAndView("return");
      }
      if(cancelActionExisting != null){
    	  //return to edit scaffolding page instead of list scaffolding page
    	  return new ModelAndView("view", "scaffolding_id", scaffolding.getId());
      }

      if (action != null) {
    	  if (action.equals("removeFormDef")) {
    		  String params = (String) request.get("params");
    		  Map parmModel = parseParams(params);
    		  String formDefId = (String) parmModel.get("id");
    		  scaffolding.getAdditionalForms()
    		  .remove(formDefId);
    		  session.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG,
    		  "true");
    		  model.put("scaffolding", scaffolding);
    		  return new ModelAndView("success", model);
    	  }else  if (action.equals("forward")) {
    		  String forwardView = (String)request.get("dest");
    		  model.put("label", request.get("label"));
    		  model.put("finalDest", request.get("finalDest"));
    		  model.put("displayText", request.get("displayText"));
    		  String params = (String)request.get("params");
    		  model.put("params", params);
    		  if (!params.equals("")) {
    			  String[] paramsList = params.split(":");
    			  for (int i=0; i<paramsList.length; i++) {
    				  String[] pair = paramsList[i].split("=");
    				  String val = null;
    				  if (pair.length>1)
    					  val = pair[1];
    				  model.put(pair[0], val);
    			  }
    		  }
    		  if(forwardView.compareTo("selectEvaluators") == 0){
    			  session.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG,
    			  "true");
    			  model.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");
    			  setAudienceSelectionVariables(session, scaffolding, true);
    		  }else if(forwardView.compareTo("selectReviewers") == 0){
    			  session.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG,
    			  "true");
    			  model.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");
    			  setAudienceSelectionVariables(session, scaffolding, false);
    		  }
    		  //matrixManager.storeScaffolding(scaffolding);

    		  //touchAllCells(scaffolding);
    		  sessionBean.setScaffolding(scaffolding);
    		  model.put("scaffolding_id", scaffolding.getId());

    		  return new ModelAndView(forwardView, model);

    	  }
      }
      return new ModelAndView("success");
   }
   
   
   protected void setAudienceSelectionVariables(Map session,
			Scaffolding scaffolding, boolean evaluate) {
	   	Id scaffid;
		//if scaffolding id does not exists (add matrix), 
		//check if there is a "new"id, which acts like a temp id,
		//if not, create one, then use the "new"id as a reference
		if(scaffolding.getId() == null){
			if(scaffolding.getNewId() == null){
				scaffolding.setNewId(getIdManager().createId());
			}
			scaffid = scaffolding.getNewId();
		}else{
			scaffid = scaffolding.getId();
		}
		String id = scaffid.getValue();

		if(evaluate){
			session.put(AudienceSelectionHelper.AUDIENCE_FUNCTION,
						AudienceSelectionHelper.AUDIENCE_FUNCTION_MATRIX);
		}else{
			session.put(AudienceSelectionHelper.AUDIENCE_FUNCTION,
					AudienceSelectionHelper.AUDIENCE_FUNCTION_MATRIX_REVIEW);
		}
		session.put(AudienceSelectionHelper.AUDIENCE_QUALIFIER, id);
		session.put(AudienceSelectionHelper.AUDIENCE_SITE, scaffolding.getWorksiteId().getValue());
		
		//cleans up any previous context values
		session.remove(AudienceSelectionHelper.CONTEXT);
		session.remove(AudienceSelectionHelper.CONTEXT2);
		
		session.put(AudienceSelectionHelper.CONTEXT, scaffolding.getTitle());
	}  
   
   
   protected Collection getEvaluationDevices(String siteId, Scaffolding scaffolding) {
	   List wizards = getWizardManager().listWizardsByType(
				getSessionManager().getCurrentSessionUserId(), siteId,
				WizardFunctionConstants.EVALUATION_TYPE);
	   return getMatrixManager().getTypeDevices(wizards, siteId, scaffolding.getEvaluationDevice(), WizardFunctionConstants.EVALUATION_TYPE, getSessionManager().getCurrentSessionUserId());
   }
	   
   protected Collection getReviewDevices(String siteId, Scaffolding scaffolding) {
	   List wizards = getWizardManager().listWizardsByType(
				getSessionManager().getCurrentSessionUserId(), siteId,
				WizardFunctionConstants.COMMENT_TYPE);
	   return getMatrixManager().getTypeDevices(wizards, siteId, scaffolding.getReviewDevice(), WizardFunctionConstants.COMMENT_TYPE, getSessionManager().getCurrentSessionUserId());
   }

   protected Collection getReflectionDevices(String siteId, Scaffolding scaffolding) {
	   List wizards = getWizardManager().listWizardsByType(
				getSessionManager().getCurrentSessionUserId(), siteId,
				WizardFunctionConstants.REFLECTION_TYPE);
	   return getMatrixManager().getTypeDevices(wizards, siteId, scaffolding.getReflectionDevice(), WizardFunctionConstants.REFLECTION_TYPE, getSessionManager().getCurrentSessionUserId());
   }
   
   protected Collection getAdditionalFormDevices( String siteId ) {
		// Return all forms
		return getMatrixManager().getFormsForSelect(null, siteId, getSessionManager().getCurrentSessionUserId());
	}
   
   protected Collection getSelectedAdditionalFormDevices(Scaffolding scaffolding, String siteId) {
	   return getMatrixManager().getSelectedAdditionalFormDevices(scaffolding.getAdditionalForms(), siteId, getSessionManager().getCurrentSessionUserId());
   }
   
   protected Map parseParams(String params) {
		Map model = new HashMap();
		if (!params.equals("")) {
			String[] paramsList = params.split(":");
			for (int i = 0; i < paramsList.length; i++) {
				String[] pair = paramsList[i].split("=");
				String val = null;
				if (pair.length > 1)
					val = pair[1];
				model.put(pair[0], val);
			}
		}
		return model;
	}
   
   protected void touchAllCells(Scaffolding scaffolding) {
      for (Iterator iter = scaffolding.getScaffoldingCells().iterator(); iter.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell) iter.next();
         sCell.getCells().size();
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


   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
	   return structuredArtifactDefinitionManager;
   }


   public void setStructuredArtifactDefinitionManager(
		   StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
	   this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }

   public WizardManager getWizardManager() {
	   return wizardManager;
   }

   public void setWizardManager(WizardManager wizardManager) {
	   this.wizardManager = wizardManager;
   }
}
