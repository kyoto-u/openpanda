/**********************************************************************************
 * $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/EditScaffoldingCellController.java $
 * $Id:EditScaffoldingCellController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.taggable.api.TaggableActivity;
import org.sakaiproject.taggable.api.TaggingHelperInfo;
import org.sakaiproject.taggable.api.TaggingProvider;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.util.ResourceLoader;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.theospi.portfolio.assignment.AssignmentHelper;
import org.theospi.portfolio.guidance.mgt.GuidanceHelper;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.security.AudienceSelectionHelper;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.CommonFormBean;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.tagging.api.DTaggingPager;
import org.theospi.portfolio.tagging.api.DTaggingSort;
import org.theospi.portfolio.tagging.api.DecoratedTaggingProvider;
import org.theospi.portfolio.tagging.impl.DecoratedTaggingProviderImpl.Pager;
import org.theospi.portfolio.wizard.WizardFunctionConstants;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.taggable.api.WizardActivityProducer;

/**
 * @author chmaurer
 */
public class EditScaffoldingCellController extends
		BaseScaffoldingCellController implements FormController,
		LoadObjectController {

	protected final Log logger = LogFactory.getLog(getClass());

	private WorksiteManager worksiteManager = null;

	private AgentManager agentManager;

	private AuthorizationFacade authzManager = null;

	private WizardActivityProducer wizardActivityProducer;

	private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;

	private ReviewManager reviewManager;
	
	private boolean customFormUsed = false;
	private boolean reflectionFormUsed = false;
	private boolean feedbackFormUsed = false;
	private boolean evaluationFormUsed = false;
	private boolean isCellUsed = false;
	private WizardManager wizardManager;

	protected static ResourceLoader myResources = new ResourceLoader("org.theospi.portfolio.matrix.bundle.Messages");
   
	protected final static String audienceSelectionFunction = AudienceSelectionHelper.AUDIENCE_FUNCTION_MATRIX;
   
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.theospi.utils.mvc.intf.FormController#referenceData(java.util.Map,
	 *      java.lang.Object, org.springframework.validation.Errors)
	 */
	public Map referenceData(Map request, Object command, Errors errors) {
		ScaffoldingCell sCell = (ScaffoldingCell) command;
		Map model = new HashMap();

		WizardPageDefinition def = sCell.getWizardPageDefinition();
		// make sure security advisor is set for guidance attachments
		if ( def.getGuidance() != null )
			getGuidanceManager().assureAccess( def.getGuidance() );
			
		// taggable support
		if (def.getId() != null) {
			TaggableActivity activity = wizardActivityProducer.getActivity(def);
			if (getTaggingManager().isTaggable()) {
				model.put("taggable", "true");
				ToolSession session = getSessionManager()
						.getCurrentToolSession();
				List<DecoratedTaggingProvider> providers = (List) session
						.getAttribute(PROVIDERS_PARAM);
				if (providers == null) {
					providers = getMatrixManager().getDecoratedProviders(activity);
					session.setAttribute(PROVIDERS_PARAM, providers);
				}
				model.put("helperInfoList", getHelperInfo(activity));
				model.put("providers", providers);
			}
		}
		
		customFormUsed = false;
		reflectionFormUsed = false;
		feedbackFormUsed = false;
		evaluationFormUsed = false;
		isCellUsed = false;
		
		model.put("reflectionDevices", getReflectionDevices(def.getSiteId(), sCell));
		model.put("evaluationDevices", getEvaluationDevices(def.getSiteId(), sCell));
		model.put("reviewDevices", getReviewDevices(def.getSiteId(), sCell));
		model.put("additionalFormDevices", getAdditionalFormDevices(def.getSiteId()));
		model.put("selectedAdditionalFormDevices",
				getSelectedAdditionalFormDevices(sCell, def.getSiteId()));
		model.put("usedAdditionalForms", getUsedFormList(sCell));
		
		model.put("selectedAssignments",
                AssignmentHelper.getSelectedAssignments(sCell.getWizardPageDefinition().getAttachments()) );
		model.put("evaluators", getMatrixManager().getSelectedUsers(sCell.getWizardPageDefinition(), MatrixFunctionConstants.EVALUATE_MATRIX));
		model.put("reviewers", getMatrixManager().getSelectedUsers(sCell.getWizardPageDefinition(), MatrixFunctionConstants.REVIEW_MATRIX));
		model.put("pageTitleKey", "title_editCell");
		model.put("pageInstructionsKey", "instructions_cellSettings");
		model.put("returnView", getReturnView());
		model.put("enableAssignments", ServerConfigurationService.getBoolean("osp.experimental.assignments",false) );
		model.put("feedbackOpts", sCell.getScaffolding());

		if (sCell.getScaffolding() != null){
			//after the cell used booleans are set to false, have "icCellUsed(sCell)" update them accordingly
			if(sCell.getScaffolding().isPublished()){
				customFormUsed = getMatrixManager().getFormCountByPageDef(sCell.getWizardPageDefinition().getId()) > 0;
				Map<Integer, Integer> reviewTypeCountMap = getMatrixManager().getReviewCountListByType(sCell.getWizardPageDefinition().getId());
				
				if (reviewTypeCountMap
						.containsKey(MatrixFunctionConstants.FEEDBACK_REVIEW_TYPE)
						&& reviewTypeCountMap
								.get(MatrixFunctionConstants.FEEDBACK_REVIEW_TYPE) > 0) {
					feedbackFormUsed = true;
					isCellUsed = true;
				}
				if (reviewTypeCountMap
						.containsKey(MatrixFunctionConstants.REFLECTION_REVIEW_TYPE)
						&& reviewTypeCountMap
								.get(MatrixFunctionConstants.REFLECTION_REVIEW_TYPE) > 0) {
					reflectionFormUsed = true;
					isCellUsed = true;
				}
				if (reviewTypeCountMap
						.containsKey(MatrixFunctionConstants.EVALUATION_REVIEW_TYPE)
						&& reviewTypeCountMap
								.get(MatrixFunctionConstants.EVALUATION_REVIEW_TYPE) > 0) {
					evaluationFormUsed = true;
					isCellUsed = true;
				}
				

				//to save a db connection, cellUsed will be set to true already if any of the review booleans are true
				if(!isCellUsed)
					isCellUsed = getMatrixManager().isScaffoldingCellUsed(sCell);

			}			
			model.put("defaultEvaluators", getMatrixManager().getSelectedUsers(sCell.getScaffolding(), MatrixFunctionConstants.EVALUATE_MATRIX));
			model.put("defaultReviewers", getMatrixManager().getSelectedUsers(sCell.getScaffolding(), MatrixFunctionConstants.REVIEW_MATRIX));
			model.put("defaultSelectedAssignments",
	                AssignmentHelper.getSelectedAssignments(sCell.getScaffolding().getAttachments()) );
			model.put("defaultSelectedAdditionalFormDevices",
					getDefaultSelectedAdditionalFormDevices(sCell,def.getSiteId()));
			
			//update guidance to keep it up to date
			if(sCell.getGuidance() != null){
				sCell.setGuidance(getGuidanceManager().getGuidance(sCell.getGuidance().getId()));
			}
				
		}

		
		model.put("isCellUsed", isCellUsed);
		model.put("evaluationFormUsed", evaluationFormUsed);
		model.put("feedbackFormUsed", feedbackFormUsed);
		model.put("reflectionFormUsed", reflectionFormUsed);
		model.put("customFormUsed", customFormUsed);
		
		model.put("enableDafaultMatrixOptions", getMatrixManager().isEnableDafaultMatrixOptions());
		
		return model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object,
	 *      java.util.Map, java.util.Map, java.util.Map,
	 *      org.springframework.validation.Errors)
	 */

	public ModelAndView handleRequest(Object requestModel, Map request,
			Map session, Map application, Errors errors) {
		ScaffoldingCell scaffoldingCell = (ScaffoldingCell) requestModel;
		String action = (String) request.get("action");
		String addFormAction = (String) request.get("addForm");
		String saveAction = (String) request.get("saveAction");
		Map model = new HashMap();


		//Deal with checkbox save's:
		
	    String suppressItems = (String) request.get("suppressItems");
		if(suppressItems == null || suppressItems.equalsIgnoreCase("false")){
			scaffoldingCell.setSuppressItems(false);
		}else{
			scaffoldingCell.setSuppressItems(true);  
		}
		
		if(request.get("hiddenDefaultCustomForm") == null || request.get("hiddenDefaultCustomForm").toString().equals("false")){
			scaffoldingCell.setDefaultCustomForm(false);
		}else{
			scaffoldingCell.setDefaultCustomForm(true);
		}
		
		if(request.get("hiddenDefaultFeedbackForm") == null || request.get("hiddenDefaultFeedbackForm").toString().equals("false")){
			scaffoldingCell.setDefaultFeedbackForm(false);
		}else{
			scaffoldingCell.setDefaultFeedbackForm(true);
		}
		
		if(request.get("hiddenDefaultReflectionForm") == null || request.get("hiddenDefaultReflectionForm").toString().equals("false")){
			scaffoldingCell.setDefaultReflectionForm(false);
		}else{
			scaffoldingCell.setDefaultReflectionForm(true);
		}
		
		if(request.get("defaultReviewers") == null || request.get("defaultReviewers").toString().equals("false")){
			scaffoldingCell.setDefaultReviewers(false);
		}else{
			scaffoldingCell.setDefaultReviewers(true);
		}
		
		if(request.get("hiddenDefaultEvaluationForm") == null || request.get("hiddenDefaultEvaluationForm").toString().equals("false")){
			scaffoldingCell.setDefaultEvaluationForm(false);
		}else{
			scaffoldingCell.setDefaultEvaluationForm(true);
		}
		
		if(request.get("defaultEvaluators") == null || "false".equals(request.get("defaultEvaluators").toString())){
			scaffoldingCell.setDefaultEvaluators(false);
		}else{
			scaffoldingCell.setDefaultEvaluators(true);
		}
		
		if(request.get("allowRequestFeedback") == null || "false".equals(request.get("allowRequestFeedback").toString())){
			scaffoldingCell.setAllowRequestFeedback(false);
		}else{
			scaffoldingCell.setAllowRequestFeedback(true);  
		}
		
		  if(request.get("hideEvaluations") == null || "false".equals(request.get("hideEvaluations").toString())){
			  scaffoldingCell.setHideEvaluations(false);
	      }else{
	    	  scaffoldingCell.setHideEvaluations(true);  
	      }
		//End Checkbox saves

		if (addFormAction != null) {

			String id = (String) request.get("selectAdditionalFormId");
			if ( id != null && !id.equals("") && !scaffoldingCell.getAdditionalForms().contains(id) )
				scaffoldingCell.getAdditionalForms().add(id);
			session.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG,
					"true");
			model.put("scaffoldingCell", scaffoldingCell);
			return new ModelAndView("success", model);
		}
		if (saveAction != null) {

			Map confirmFlags = getMatrixManager().getConfirmFlagsForScaffoldingCell(scaffoldingCell);
			if(confirmFlags.size() > 0){
				model.put("scaffoldingCell", scaffoldingCell);
				model.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG,
						"true");
				model.putAll(confirmFlags);
				return new ModelAndView("editScaffoldingCellConfirm", model);
			}

			if (getTaggingManager().isTaggable()) {
				session.remove(PROVIDERS_PARAM);
			}

			saveScaffoldingCell(request, scaffoldingCell);
			session.remove(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG);
			session
					.remove(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
			prepareModelWithScaffoldingId(model, scaffoldingCell);
			return new ModelAndView("return", model);
		}

		if (action == null)
			action = (String) request.get("submitAction");

		if (action != null && action.length() > 0) {

			if (request.get("reviewers") == null) {
				scaffoldingCell.getEvaluators().clear();
			}
			if (action.equals("removeFormDef")) {
				String params = (String) request.get("params");
				Map parmModel = parseParams(params);
				String formDefId = (String) parmModel.get("id");
				scaffoldingCell.getWizardPageDefinition().getAdditionalForms()
						.remove(formDefId);
				session.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG,
						"true");
				model.put("scaffoldingCell", scaffoldingCell);
				return new ModelAndView("success", model);
			} else if (action.equals("forward")) {
				String forwardView = (String) request.get("dest");
				Map forwardModel = doForwardAction(forwardView, request,
						session, scaffoldingCell);
				model.putAll(forwardModel);
				return new ModelAndView(forwardView, model);
			} else if (action.equals("cancel")) {
				session.remove(PROVIDERS_PARAM);
				return new ModelAndView(new RedirectView(
						"viewScaffolding.osp?scaffolding_id="
								+ scaffoldingCell.getScaffolding().getId()));
			} else if (action.equals("tagActivity")) {
				return tagActivity(scaffoldingCell, model, request, session);
			} else if (action.equals("sortList")) {
				return sortList(scaffoldingCell, model, request, session);
			} else if (action.equals("pageList")) {
				return pageList(scaffoldingCell, model, request, session);
			} else if (action.equals("listPageActivities")) {
				model.put("criteriaRef", scaffoldingCell.getWizardPageDefinition().getReference());
				return new ModelAndView("listPageActivities", model);
			}
			prepareModelWithScaffoldingId(model, scaffoldingCell);
			return new ModelAndView("return", model);
		}

		return new ModelAndView("success");
	}

	protected ModelAndView tagActivity(ScaffoldingCell scaffoldingCell,
			Map model, Map request, Map session) {
		EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage) session
				.get(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
		sessionBean.setScaffoldingCell(scaffoldingCell);
		session.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");
		ModelAndView view = null;
		// Get appropriate helperInfo
		for (TaggingHelperInfo info : getHelperInfo(wizardActivityProducer
				.getActivity(scaffoldingCell.getWizardPageDefinition()))) {
			if (info.getProvider().getId().equals(request.get("providerId"))) {
				// Add parameters to session
				for (String key : info.getParameterMap().keySet()) {
					session.put(key, info.getParameterMap().get(key));
				}
				session.remove(PROVIDERS_PARAM);
				view = new ModelAndView(new RedirectView(info.getHelperId()
						+ ".helper"));
				break;
			}
		}
		return view;
	}

	protected ModelAndView sortList(ScaffoldingCell scaffoldingCell, Map model,
			Map request, Map session) {
		EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage) session
				.get(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
		sessionBean.setScaffoldingCell(scaffoldingCell);
		prepareModelWithScaffoldingId(model, scaffoldingCell);
		model.put("scaffoldingCell_id", scaffoldingCell.getId());

		String providerId = (String) request.get("providerId");
		String criteria = (String) request.get("criteria");

		List<DecoratedTaggingProvider> providers = (List) getSessionManager()
				.getCurrentToolSession().getAttribute(PROVIDERS_PARAM);
		for (DecoratedTaggingProvider dtp : providers) {
			if (dtp.getProvider().getId().equals(providerId)) {
				DTaggingSort sort = dtp.getSort();
				if (sort.getSort().equals(criteria)) {
					sort.setAscending(sort.isAscending() ? false : true);
				} else {
					sort.setSort(criteria);
					sort.setAscending(true);
				}
				break;
			}
		}
		return new ModelAndView("success", model);
	}

	protected ModelAndView pageList(ScaffoldingCell scaffoldingCell, Map model,
			Map request, Map session) {
		EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage) session
				.get(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
		sessionBean.setScaffoldingCell(scaffoldingCell);
		prepareModelWithScaffoldingId(model, scaffoldingCell);
		model.put("scaffoldingCell_id", scaffoldingCell.getId());

		String page = (String) request.get("page");
		String pageSize = (String) request.get("pageSize");
		String providerId = (String) request.get("providerId");

		List<DecoratedTaggingProvider> providers = (List) getSessionManager()
				.getCurrentToolSession().getAttribute(PROVIDERS_PARAM);
		for (DecoratedTaggingProvider dtp : providers) {
			if (dtp.getProvider().getId().equals(providerId)) {
				DTaggingPager pager = dtp.getPager();
				pager.setPageSize(Integer.valueOf(pageSize));
				if (Pager.FIRST.equals(page)) {
					pager.setFirstItem(0);
				} else if (Pager.PREVIOUS.equals(page)) {
					pager.setFirstItem(pager.getFirstItem()
							- pager.getPageSize());
				} else if (Pager.NEXT.equals(page)) {
					pager.setFirstItem(pager.getFirstItem()
							+ pager.getPageSize());
				} else if (Pager.LAST.equals(page)) {
					pager.setFirstItem((pager.getTotalItems() / pager
							.getPageSize())
							* pager.getPageSize());
				}
				break;
			}
		}
		return new ModelAndView("success", model);
	}

	protected void prepareModelWithScaffoldingId(Map model,
			ScaffoldingCell scaffoldingCell) {
		model.put("scaffolding_id", scaffoldingCell.getScaffolding().getId());
	}

	protected String getGuidanceViewPermission() {
		return MatrixFunctionConstants.VIEW_SCAFFOLDING_GUIDANCE;
	}

	protected String getGuidanceEditPermission() {
		return MatrixFunctionConstants.EDIT_SCAFFOLDING_GUIDANCE;
	}

	protected String getGuidanceTitle() {
		return myResources.getString("cell_guidance_title");
		// return "Guidance for Cell";
	}

	protected String getReturnView() {
		return "cell";
	}

	private Map doForwardAction(String forwardView, Map request, Map session,
			ScaffoldingCell scaffoldingCell) {
		Map model = new HashMap();

		EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage) session
				.get(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
		sessionBean.setScaffoldingCell(scaffoldingCell);
		prepareModelWithScaffoldingId(model, scaffoldingCell);
		model.put("scaffoldingCell_id", scaffoldingCell.getId());

		if (forwardView.equals("createGuidance")
				|| forwardView.equals("editInstructions")
				|| forwardView.equals("editRationale")
				|| forwardView.equals("editExamples")
				|| forwardView.equals("editRubrics")
				|| forwardView.equals("editExpectations")) {
			Boolean bTrue = Boolean.valueOf(true);
			Boolean bFalse = Boolean.valueOf(false);
			//guidance context
			session.remove(GuidanceHelper.CONTEXT);
			session.remove(GuidanceHelper.CONTEXT2);

			if(scaffoldingCell.getScaffolding() != null){
				session.put(GuidanceHelper.CONTEXT,
						scaffoldingCell.getScaffolding().getTitle());
			}
			session.put(GuidanceHelper.CONTEXT2,
					scaffoldingCell.getTitle());
			
			session.put(GuidanceHelper.SHOW_INSTRUCTION_FLAG, bFalse);
			session.put(GuidanceHelper.SHOW_RATIONALE_FLAG, bFalse);
			session.put(GuidanceHelper.SHOW_EXAMPLE_FLAG, bFalse);
			session.put(GuidanceHelper.SHOW_RUBRIC_FLAG, bFalse);
			session.put(GuidanceHelper.SHOW_EXPECTATIONS_FLAG, bFalse);

			if (forwardView.equals("editInstructions")
					|| forwardView.equals("createGuidance"))
				session.put(GuidanceHelper.SHOW_INSTRUCTION_FLAG, bTrue);
			if (forwardView.equals("editRationale")
					|| forwardView.equals("createGuidance"))
				session.put(GuidanceHelper.SHOW_RATIONALE_FLAG, bTrue);
			if (forwardView.equals("editExamples")
					|| forwardView.equals("createGuidance"))
				session.put(GuidanceHelper.SHOW_EXAMPLE_FLAG, bTrue);
			if (forwardView.equals("editRubrics")
					|| forwardView.equals("createGuidance"))
				session.put(GuidanceHelper.SHOW_RUBRIC_FLAG, bTrue);
			if (forwardView.equals("editExpectations")
					|| forwardView.equals("createGuidance"))
				session.put(GuidanceHelper.SHOW_EXPECTATIONS_FLAG, bTrue);

			String currentSite = scaffoldingCell.getWizardPageDefinition().getSiteId();
			session.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG,
					"true");
			model.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");

			Guidance guidance = scaffoldingCell.getGuidance();
			if (guidance == null) {
				String title = getGuidanceTitle();
				guidance = getGuidanceManager().createNew(title, currentSite,
						scaffoldingCell.getWizardPageDefinition().getId(),
						getGuidanceViewPermission(),
						getGuidanceEditPermission());
			}

			session.put(GuidanceManager.CURRENT_GUIDANCE, guidance);
		} else if (forwardView.equals("deleteGuidance")) {
			scaffoldingCell.setDeleteGuidanceId(scaffoldingCell.getGuidance()
					.getId());
			scaffoldingCell.setGuidance(null);
			session.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG,
					"true");
			model.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");
		} else if (!forwardView.equals("selectEvaluators") && !forwardView.equals("selectReviewers")) {
			model.put("label", request.get("label"));
			model.put("finalDest", request.get("finalDest"));
			model.put("displayText", request.get("displayText"));
			String params = (String) request.get("params");
			model.put("params", params);
			if (!params.equals("")) {
				model.putAll(parseParams(params));
			}
		} 
      else {
    	  session.remove("PRESENTATION_VIEWERS");
    	  session.remove("audience");
    	  session.remove("osp.audiencesakai.tool.helper.done.url");
    	  
    	  if(forwardView.compareTo("selectEvaluators") == 0){
    		  session.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG,
    		  "true");
    		  model.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");
    		  setAudienceSelectionVariables(session, scaffoldingCell, AudienceSelectionHelper.AUDIENCE_FUNCTION_MATRIX);
		  }else if(forwardView.compareTo("selectReviewers") == 0){
			  session.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG,
			  "true");
			  model.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");
			  setAudienceSelectionVariables(session, scaffoldingCell, AudienceSelectionHelper.AUDIENCE_FUNCTION_MATRIX_REVIEW);
		  }
		}
		return model;

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

//	/**
//	 ** Set and Return default list of evaluators for this matrix cell or wizard page
//	 **/
//	protected List getDefaultEvaluators(WizardPageDefinition wpd) {
//		List evalList = new ArrayList();
//		Set roles;
//		try {
//			roles = SiteService.getSite(wpd.getSiteId()).getRoles();
//		}
//		catch (IdUnusedException e) {
//			logger.warn(".getDefaultEvaluators unknown siteid", e);
//			return evalList;
//		}
//		
//		for (Iterator i = roles.iterator(); i.hasNext();) {
//			Role role = (Role) i.next();
//			if ( !role.isAllowed(audienceSelectionFunction) )
//				continue;
//					
//			Agent roleAgent = getAgentManager().getWorksiteRole(role.getId(), wpd.getSiteId());
//			evalList.add(myResources.getFormattedMessage("decorated_role_format",
//																		new Object[] { roleAgent.getDisplayName() }));
//
//			getAuthzManager().createAuthorization(roleAgent, 
//															  audienceSelectionFunction, 
//															  (wpd.getId()==null?wpd.getNewId():wpd.getId()));
//		}
//		return evalList;
//	}
	
//	/**
//	 ** Return list of evaluators for this matrix cell or wizard page
//	 **/
//	protected List getEvaluators(WizardPageDefinition wpd) {
//		Id id = wpd.getId() == null ? wpd.getNewId() : wpd.getId();
//
//		List evaluators = getAuthzManager().getAuthorizations(null, audienceSelectionFunction, id);
//		
//		// If no evaluators defined, add all qualified roles as default list
//		if ( evaluators.size() == 0 ) 
//			return getDefaultEvaluators(wpd);
//
//		// Otherwise, return list of selected evaluator roles and users
//		List evalList = new ArrayList();
//		for (Iterator iter = evaluators.iterator(); iter.hasNext();) {
//			Authorization az = (Authorization) iter.next();
//			Agent agent = az.getAgent();
//			if (agent.isRole()) {
//				evalList.add(myResources.getFormattedMessage("decorated_role_format",
//																			new Object[] { agent.getDisplayName() }));
//			} 
//			else {
//				String userId = az.getAgent().getEid().getValue();
//				evalList.add(myResources.getFormattedMessage("decorated_user_format", 
//																			new Object[] { agent.getDisplayName(), userId }));
//			}
//		}
//
//		return evalList;
//	}

	protected void setAudienceSelectionVariables(Map session,
			ScaffoldingCell scaffoldingCell, String audienceFunction) {
		WizardPageDefinition wpd = scaffoldingCell.getWizardPageDefinition();
		
		session.put(AudienceSelectionHelper.AUDIENCE_FUNCTION,
				audienceFunction);

		String id = wpd.getId() != null ? wpd.getId().getValue() : wpd
				.getNewId().getValue();

		session.put(AudienceSelectionHelper.AUDIENCE_QUALIFIER, id);
		session.put(AudienceSelectionHelper.AUDIENCE_SITE, wpd.getSiteId());
		
		session.remove(AudienceSelectionHelper.CONTEXT);
		session.remove(AudienceSelectionHelper.CONTEXT2);
		
		if(scaffoldingCell.getScaffolding() != null){ 
			session.put(AudienceSelectionHelper.CONTEXT,
					scaffoldingCell.getScaffolding().getTitle());
		}
		session.put(AudienceSelectionHelper.CONTEXT2,
				scaffoldingCell.getTitle());

	}
	
	protected Collection getAdditionalFormDevices( String siteId ) {
		return getMatrixManager().getFormsForSelect(null, siteId, getSessionManager().getCurrentSessionUserId());	
	}	
	
	protected Collection getReviewDevices(String siteId, ScaffoldingCell scaffoldingCell) {
		List wizards = getWizardManager().listWizardsByType(
				getSessionManager().getCurrentSessionUserId(), siteId,
				WizardFunctionConstants.COMMENT_TYPE);
		return getMatrixManager().getTypeDevices(wizards, siteId, scaffoldingCell.getReviewDevice(), WizardFunctionConstants.COMMENT_TYPE, getSessionManager().getCurrentSessionUserId());
	}
	
	protected Collection getReflectionDevices(String siteId, ScaffoldingCell scaffoldingCell) {
		List wizards = getWizardManager().listWizardsByType(
				getSessionManager().getCurrentSessionUserId(), siteId,
				WizardFunctionConstants.REFLECTION_TYPE);
		return getMatrixManager().getTypeDevices(wizards, siteId, scaffoldingCell.getReflectionDevice(), WizardFunctionConstants.REFLECTION_TYPE, getSessionManager().getCurrentSessionUserId());
	}
	
	protected Collection getEvaluationDevices(String siteId, ScaffoldingCell scaffoldingCell) {
		List wizards = getWizardManager().listWizardsByType(
				getSessionManager().getCurrentSessionUserId(), siteId,
				WizardFunctionConstants.EVALUATION_TYPE);
		return getMatrixManager().getTypeDevices(wizards, siteId, scaffoldingCell.getEvaluationDevice(), WizardFunctionConstants.EVALUATION_TYPE, getSessionManager().getCurrentSessionUserId());
	}

	protected Collection getSelectedAdditionalFormDevices(ScaffoldingCell sCell, String siteId) {
		return getMatrixManager().getSelectedAdditionalFormDevices(sCell.getAdditionalForms(), siteId, getSessionManager().getCurrentSessionUserId());
	}
	
	protected Collection getDefaultSelectedAdditionalFormDevices(ScaffoldingCell sCell, String siteId){
		return getMatrixManager().getSelectedAdditionalFormDevices(sCell.getScaffolding().getAdditionalForms(), siteId, getSessionManager().getCurrentSessionUserId());
	}


	/**
	 * @return Returns the worksiteManager.
	 */
	public WorksiteManager getWorksiteManager() {
		return worksiteManager;
	}

	/**
	 * @param worksiteManager
	 *            The worksiteManager to set.
	 */
	public void setWorksiteManager(WorksiteManager worksiteManager) {
		this.worksiteManager = worksiteManager;
	}

	/**
	 * @return Returns the agentManager.
	 */
	public AgentManager getAgentManager() {
		return agentManager;
	}

	/**
	 * @param agentManager
	 *            The agentManager to set.
	 */
	public void setAgentManager(AgentManager agentManager) {
		this.agentManager = agentManager;
	}

	public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
		return structuredArtifactDefinitionManager;
	}

	public void setStructuredArtifactDefinitionManager(
			StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
		this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
	}

	/**
	 * @return Returns the authzManager.
	 */
	public AuthorizationFacade getAuthzManager() {
		return authzManager;
	}

	/**
	 * @param authzManager
	 *            The authzManager to set.
	 */
	public void setAuthzManager(AuthorizationFacade authzManager) {
		this.authzManager = authzManager;
	}

	public WizardActivityProducer getWizardActivityProducer() {
		return wizardActivityProducer;
	}

	public void setWizardActivityProducer(
			WizardActivityProducer wizardActivityProducer) {
		this.wizardActivityProducer = wizardActivityProducer;
	}

	/**
	 * @return Returns the reviewManager.
	 */
	public ReviewManager getReviewManager() {
		return reviewManager;
	}

	/**
	 * @param reviewManager
	 *            The reviewManager to set.
	 */
	public void setReviewManager(ReviewManager reviewManager) {
		this.reviewManager = reviewManager;
	}

	protected List<TaggingHelperInfo> getHelperInfo(TaggableActivity activity) {
		List<TaggingHelperInfo> infoList = new ArrayList<TaggingHelperInfo>();
		if (getTaggingManager().isTaggable()) {
			for (TaggingProvider provider : getTaggingManager().getProviders()) {
				TaggingHelperInfo info = provider
						.getActivityHelperInfo(activity.getReference());
				if (info != null) {
					infoList.add(info);
				}
			}
		}
		return infoList;
	}

	
	private List getUsedFormList(ScaffoldingCell sCell){
		List<String> usedForms = new ArrayList<String>();

		List cells = getMatrixManager().getCellsByScaffoldingCell(sCell.getId());

		for (Iterator cellIt = cells.iterator(); cellIt.hasNext();) {
			Cell cell = (Cell) cellIt.next();
			WizardPage wizardPage = cell.getWizardPage();
			Set pageForms = getMatrixManager().getPageForms(wizardPage);

			for (Iterator cellIter = pageForms.iterator(); cellIter.hasNext();) {
				Node cellPageForm = (Node) cellIter.next();

				boolean found = false;
				for (Iterator newFormsIter = sCell.getAdditionalForms().iterator(); newFormsIter.hasNext();) {
					String newFormDefId = (String) newFormsIter.next();
					if(cellPageForm.getFileType().equals(newFormDefId)){
						if(!usedForms.contains(newFormDefId)){
							usedForms.add(newFormDefId);
						}
					}
				}
			}
		}
		   
		return usedForms;
	}
	
	
	public boolean isCustomFormUsed() {
		return customFormUsed;
	}

	public boolean isReflectionFormUsed() {
		return reflectionFormUsed;
	}

	public boolean isFeedbackFormUsed() {
		return feedbackFormUsed;
	}

	public boolean isEvaluationFormUsed() {
		return evaluationFormUsed;
	}

	public boolean isCellUsed() {
		return isCellUsed;
	}

	public WizardManager getWizardManager() {
		return wizardManager;
	}

	public void setWizardManager(WizardManager wizardManager) {
		this.wizardManager = wizardManager;
	}
}
