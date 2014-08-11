/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/EditScaffoldingConfirmationController.java $
* $Id:EditScaffoldingConfirmationController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.matrix.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolSession;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.review.mgt.ReviewManager;


public class EditScaffoldingConfirmationController extends BaseScaffoldingController
implements Controller, FormController {

   protected final Log logger = LogFactory.getLog(getClass());
   private ReviewManager reviewManager;
   private SessionManager sessionManager;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String viewName = "success";
      
      EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage)session.get(
            EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
      Scaffolding scaffolding = sessionBean.getScaffolding();
      
      Id id = scaffolding.getId();
      
      Map model = new HashMap();
      model.put("scaffolding_id", id);
      
      
      String cancel = (String)request.get("cancel");
      String next = (String)request.get("continue");
      if (cancel != null) {
         viewName = "cancel";
         session.put(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY,
                 sessionBean);
         model.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");
      }
      else if (next != null) {
    	  if(scaffolding.getId() != null){
    		  scaffolding = usedCellDefaultSettingAdjustment(scaffolding);
    	  }
    	  scaffolding = saveScaffolding(scaffolding);
    	  model.put("scaffolding_id", scaffolding.getId());
            
    	  session.remove(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG);
    	  session.remove(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
      }
      return new ModelAndView(viewName, model);
   }

   public Map referenceData(Map request, Object command, Errors errors) {
	   ToolSession session = getSessionManager().getCurrentToolSession();
	   EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage)session.getAttribute(
			   EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
	   Scaffolding scaffolding = sessionBean.getScaffolding();

	   Map model = new HashMap();

	   model.put("label", "Scaffolding");

	   Collection changedCells = new ArrayList<String>();
	   if(scaffolding.getId() != null){
		   changedCells = getChangedCells(scaffolding);
	   }
	   if(request.containsKey(MatrixManager.CONFIRM_PUBLISHED_FLAG)){
		   model.put("published", request.get(MatrixManager.CONFIRM_PUBLISHED_FLAG));
	   }
	   if(request.containsKey(MatrixManager.CONFIRM_EVAL_VIEW_ALL_GROUPS_FLAG)){
		   model.put("warnViewAllGroupsEval", request.get(MatrixManager.CONFIRM_EVAL_VIEW_ALL_GROUPS_FLAG));
	   }
	   
	   model.put("changedCells", changedCells);
	   model.put("changedCellsSize", changedCells.size());
	   model.put("isInSession", EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG);
	   return model;
   }
   
   /**
    * If any default forms in the matrix has changed and scaffolding is published, then
    * this function loops through each cell in a matrix and sets used default to false for
    * each particular instance (reflection, custom, feedback, evaluation) and sets the old default
    * form to the cell's form, only if the useDefault flag is set to true
    * 
    * 
    * Once a student submits information to the cell, the form cannot be changed. If the program administrator is using the
	* default forms for cell A and a student submits information to cell A and then the instructor changes the default form, ensure
	* the setting at the cell level to use default settings is no longer selected.
    * 
    * @param scaffolding
    * @return
    */


   private Scaffolding usedCellDefaultSettingAdjustment(Scaffolding scaffolding) {
	   

	   Scaffolding dbScaffolding = getMatrixManager().getScaffolding(scaffolding.getId());

	   if(dbScaffolding != null){
		   boolean reflectChange = false;
		   boolean customChange = false;
		   boolean feedbackChange = false;
		   boolean evalChange = false;

		   // find out what default forms have been changed:
		   if ((dbScaffolding.getReflectionDevice() == null && scaffolding
				   .getReflectionDevice() != null)
				   || (dbScaffolding.getReflectionDevice() != null && scaffolding
						   .getReflectionDevice() == null)
						   || (dbScaffolding.getReflectionDevice() != null
								   && scaffolding.getReflectionDevice() != null && !dbScaffolding
								   .getReflectionDevice().equals(
										   scaffolding.getReflectionDevice()))) {
			   reflectChange = true;
		   }

		   if ((dbScaffolding.getAdditionalForms() != null && scaffolding
				   .getAdditionalForms() == null)
				   || (dbScaffolding.getAdditionalForms() == null && scaffolding
						   .getAdditionalForms() != null)
						   || (dbScaffolding.getAdditionalForms() != null
								   && scaffolding.getAdditionalForms() != null && !dbScaffolding
								   .getAdditionalForms().equals(
										   scaffolding.getAdditionalForms()))) {
			   customChange = true;
		   }

		   if ((dbScaffolding.getReviewDevice() != null && scaffolding
				   .getReviewDevice() == null)
				   || (dbScaffolding.getReviewDevice() == null && scaffolding
						   .getReviewDevice() != null)
						   || (dbScaffolding.getReviewDevice() != null
								   && scaffolding.getReviewDevice() != null && !dbScaffolding
								   .getReviewDevice().equals(
										   scaffolding.getReviewDevice()))) {
			   feedbackChange = true;
		   }

		   if ((dbScaffolding.getEvaluationDevice() == null && scaffolding
				   .getEvaluationDevice() != null)
				   || (dbScaffolding.getEvaluationDevice() != null && scaffolding
						   .getEvaluationDevice() == null)
						   || (dbScaffolding.getEvaluationDevice() != null
								   && scaffolding.getEvaluationDevice() != null && !dbScaffolding
								   .getEvaluationDevice().equals(
										   scaffolding.getEvaluationDevice()))) {
			   evalChange = true;
		   }
		   
		   //only iterate through the matrix if there is a default form change:
		   if(reflectChange || customChange || feedbackChange || evalChange){

			   Set<ScaffoldingCell> newScaffoldingCells = new HashSet<ScaffoldingCell>();
			   
			   for (Iterator iterator = scaffolding.getScaffoldingCells().iterator(); iterator.hasNext();) {
				   ScaffoldingCell sCell = (ScaffoldingCell) iterator.next();

				   if(sCell.isDefaultCustomForm() && customChange){
					   if(getMatrixManager().getFormCountByPageDef(sCell.getWizardPageDefinition().getId()) > 0){
						   //since a form that was removed is being used by this cell, 
						   //use the old default for the cell's custom form list and set
						   //useDefault to false
						   sCell.getWizardPageDefinition().setDefaultCustomForm(false);
						   sCell.getWizardPageDefinition().getAdditionalForms().clear();
						   sCell.getWizardPageDefinition().getAdditionalForms().addAll(dbScaffolding.getAdditionalForms());
					   }				   
				   }
				   
				   
				   boolean feedbackFormUsed = false, reflectionFormUsed = false, evaluationFormUsed = false;
				  
				   Map<Integer, Integer> reviewTypeCountMap = getMatrixManager()
							.getReviewCountListByType(
									sCell.getWizardPageDefinition().getId());

					// Feedback
					if (reviewTypeCountMap
							.containsKey(MatrixFunctionConstants.FEEDBACK_REVIEW_TYPE)
							&& reviewTypeCountMap
									.get(MatrixFunctionConstants.FEEDBACK_REVIEW_TYPE) > 0) {
						feedbackFormUsed = true;
					}
					// Reflection
					if (reviewTypeCountMap
							.containsKey(MatrixFunctionConstants.REFLECTION_REVIEW_TYPE)
							&& reviewTypeCountMap
									.get(MatrixFunctionConstants.REFLECTION_REVIEW_TYPE) > 0) {
						reflectionFormUsed = true;
					}
					// Evaluation
					if (reviewTypeCountMap
							.containsKey(MatrixFunctionConstants.EVALUATION_REVIEW_TYPE)
							&& reviewTypeCountMap
									.get(MatrixFunctionConstants.EVALUATION_REVIEW_TYPE) > 0) {
						evaluationFormUsed = true;
					}



				   //feedback
				   if(sCell.isDefaultFeedbackForm() && feedbackChange && feedbackFormUsed){
						   sCell.getWizardPageDefinition().setDefaultFeedbackForm(false);
						   sCell.getWizardPageDefinition().setReviewDevice(dbScaffolding.getReviewDevice());
				   }
				   
				   //reflection
				   if(sCell.isDefaultReflectionForm() && reflectChange && reflectionFormUsed){
						   sCell.getWizardPageDefinition().setDefaultReflectionForm(false);
						   sCell.getWizardPageDefinition().setReflectionDevice(dbScaffolding.getReflectionDevice());
				   }
				   
				   //evaluation
				   if(sCell.isDefaultEvaluationForm() && evalChange && evaluationFormUsed){			
						   sCell.getWizardPageDefinition().setDefaultEvaluationForm(false);
						   sCell.getWizardPageDefinition().setEvaluationDevice(dbScaffolding.getEvaluationDevice());
				   }
				   
				   
				   newScaffoldingCells.add(sCell);
			   }
			   
			   scaffolding.setScaffoldingCells(newScaffoldingCells);
		   }

	   }
	   
	   return scaffolding;
   }
   
   
   /**
    * 
    * 
    * Does the same looping as usedCellDefaultSettingAdjustment but doesn't make any changes and only
    * returns a list of scaffolding cell names that will change a default setting
    * 
    * Closely tied to usedCellDefaultSettingAdjustment
    * 
    * @param scaffolding
    * @return
    */
   private Collection getChangedCells(Scaffolding scaffolding) {
	   List<String> changedCells = new ArrayList<String>();

	   Scaffolding dbScaffolding = getMatrixManager().getScaffolding(scaffolding.getId());

	   if(dbScaffolding != null){
		   boolean reflectChange = false;
		   boolean customChange = false;
		   boolean feedbackChange = false;
		   boolean evalChange = false;

		   // find out what default forms have been changed:
		   if ((dbScaffolding.getReflectionDevice() == null && scaffolding
				   .getReflectionDevice() != null)
				   || (dbScaffolding.getReflectionDevice() != null && scaffolding
						   .getReflectionDevice() == null)
						   || (dbScaffolding.getReflectionDevice() != null
								   && scaffolding.getReflectionDevice() != null && !dbScaffolding
								   .getReflectionDevice().equals(
										   scaffolding.getReflectionDevice()))) {
			   reflectChange = true;
		   }

		   if ((dbScaffolding.getAdditionalForms() != null && scaffolding
				   .getAdditionalForms() == null)
				   || (dbScaffolding.getAdditionalForms() == null && scaffolding
						   .getAdditionalForms() != null)
						   || (dbScaffolding.getAdditionalForms() != null
								   && scaffolding.getAdditionalForms() != null && !dbScaffolding
								   .getAdditionalForms().equals(
										   scaffolding.getAdditionalForms()))) {
			   customChange = true;
		   }

		   if ((dbScaffolding.getReviewDevice() != null && scaffolding
				   .getReviewDevice() == null)
				   || (dbScaffolding.getReviewDevice() == null && scaffolding
						   .getReviewDevice() != null)
						   || (dbScaffolding.getReviewDevice() != null
								   && scaffolding.getReviewDevice() != null && !dbScaffolding
								   .getReviewDevice().equals(
										   scaffolding.getReviewDevice()))) {
			   feedbackChange = true;
		   }

		   if ((dbScaffolding.getEvaluationDevice() == null && scaffolding
				   .getEvaluationDevice() != null)
				   || (dbScaffolding.getEvaluationDevice() != null && scaffolding
						   .getEvaluationDevice() == null)
						   || (dbScaffolding.getEvaluationDevice() != null
								   && scaffolding.getEvaluationDevice() != null && !dbScaffolding
								   .getEvaluationDevice().equals(
										   scaffolding.getEvaluationDevice()))) {
			   evalChange = true;
		   }
		   
		   //only iterate through the matrix if there is a default form change:
		   if(reflectChange || customChange || feedbackChange || evalChange){

			   for (Iterator iterator = scaffolding.getScaffoldingCells().iterator(); iterator.hasNext();) {
				   ScaffoldingCell sCell = (ScaffoldingCell) iterator.next();

				   //custom forms:
				   if(sCell.isDefaultCustomForm() && customChange){
					   if(getMatrixManager().getFormCountByPageDef(sCell.getWizardPageDefinition().getId()) > 0){
						   changedCells.add(sCell.getTitle());
					   }				   
				   }
				   				   
				   
				   boolean feedbackFormUsed = false, reflectionFormUsed = false, evaluationFormUsed = false;
				   Map<Integer, Integer> reviewTypeCountMap = getMatrixManager().getReviewCountListByType(sCell.getWizardPageDefinition().getId());
					
				   // Feedback
					if (reviewTypeCountMap
							.containsKey(MatrixFunctionConstants.FEEDBACK_REVIEW_TYPE)
							&& reviewTypeCountMap
									.get(MatrixFunctionConstants.FEEDBACK_REVIEW_TYPE) > 0) {
						feedbackFormUsed = true;
					}
					// Reflection
					if (reviewTypeCountMap
							.containsKey(MatrixFunctionConstants.REFLECTION_REVIEW_TYPE)
							&& reviewTypeCountMap
									.get(MatrixFunctionConstants.REFLECTION_REVIEW_TYPE) > 0) {
						reflectionFormUsed = true;
					}
					// Evaluation
					if (reviewTypeCountMap
							.containsKey(MatrixFunctionConstants.EVALUATION_REVIEW_TYPE)
							&& reviewTypeCountMap
									.get(MatrixFunctionConstants.EVALUATION_REVIEW_TYPE) > 0) {
						evaluationFormUsed = true;
					}
				   

				   //Feedback
				   if(sCell.isDefaultFeedbackForm() && feedbackChange && feedbackFormUsed){					   
						   changedCells.add(sCell.getTitle());				   
				   }
				   
				   //Reflection
				   if(sCell.isDefaultReflectionForm() && reflectChange && reflectionFormUsed){
						   changedCells.add(sCell.getTitle());
				   }
				   
				   //Evaluation
				   if(sCell.isDefaultEvaluationForm() && evalChange && evaluationFormUsed){
						   changedCells.add(sCell.getTitle());
				   }
			   }
		   }

	   }

	   return changedCells;
   }
   
   
   
   
   
   
public ReviewManager getReviewManager() {
	return reviewManager;
}

public void setReviewManager(ReviewManager reviewManager) {
	this.reviewManager = reviewManager;
}

public SessionManager getSessionManager() {
	return sessionManager;
}

public void setSessionManager(SessionManager sessionManager) {
	this.sessionManager = sessionManager;
}
   
}
