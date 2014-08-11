/**********************************************************************************
 * $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/CellController.java $
 * $Id:CellController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.taggable.api.TaggableActivity;
import org.sakaiproject.taggable.api.TaggableItem;
import org.sakaiproject.taggable.api.TaggingHelperInfo;
import org.sakaiproject.taggable.api.TaggingManager;
import org.sakaiproject.taggable.api.TaggingProvider;
import org.sakaiproject.assignment.api.Assignment;
import org.sakaiproject.assignment.api.AssignmentSubmission;
import org.sakaiproject.assignment.cover.AssignmentService;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.content.api.FilePickerHelper;
import org.sakaiproject.content.api.ResourceEditingHelper;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.FormHelper;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.taggable.api.TaggableItem;
import org.sakaiproject.taggable.api.TaggingHelperInfo;
import org.sakaiproject.taggable.api.TaggingManager;
import org.sakaiproject.taggable.api.TaggingProvider;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.util.ResourceLoader;

import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.theospi.portfolio.assignment.AssignmentHelper;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import org.theospi.portfolio.matrix.HibernateMatrixManagerImpl;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.impl.MatrixContentEntityProducer;
import org.theospi.portfolio.review.ReviewHelper;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.CommonFormBean;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.style.mgt.StyleManager;
import org.theospi.portfolio.shared.model.WizardMatrixConstants;
import org.theospi.portfolio.style.model.Style;
import org.theospi.portfolio.tagging.api.DTaggingPager;
import org.theospi.portfolio.tagging.api.DTaggingSort;
import org.theospi.portfolio.tagging.api.DecoratedTaggableItem;
import org.theospi.portfolio.tagging.api.DecoratedTaggingProvider;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.taggable.api.WizardActivityProducer;

public class CellController implements FormController, LoadObjectController {

	protected final Log logger = LogFactory.getLog(getClass());

	private static ResourceLoader rb = new ResourceLoader("org.theospi.portfolio.matrix.bundle.Messages");
	
	private MatrixManager matrixManager;

	private AuthenticationManager authManager = null;
	
	private SecurityService securityService;

	private IdManager idManager = null;

	private ReviewManager reviewManager;

	private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;

	private AuthorizationFacade authzManager = null;

	private TaggingManager taggingManager;	

	private WizardActivityProducer wizardActivityProducer;
	
	private WizardManager wizardManager;

	private SessionManager sessionManager;

	private List<String> ratingProviderIds;

   private StyleManager styleManager;

   public static final String WHICH_HELPER_KEY = "filepicker.helper.key";

	public static final String KEEP_HELPER_LIST = "filepicker.helper.keeplist";

	protected static final int METADATA_ID_INDEX = 0;

	protected static final int METADATA_TITLE_INDEX = 1;

	protected static final int METADATA_DESC_INDEX = 2;

	
	
	protected boolean enableReviewEdit = true;

    private class NodeNameComparator implements java.util.Comparator<Node> {
        public int compare(Node n1, Node n2) {
            return n1.getName().compareToIgnoreCase(n2.getName());
        }

        public boolean equals(Object o) {
            return (this == o || o instanceof NodeNameComparator);
        }
    }

	private ServerConfigurationService serverConfigurationService;
	
	public Map referenceData(Map request, Object command, Errors errors) {
		
		Map model = new HashMap();
		model.put("feedbackSent", false);
		ToolSession session = getSessionManager().getCurrentToolSession();
		
		CellFormBean cell = (CellFormBean) command;
		if (cell == null || cell.getCell() == null){
			logger.error("Cell backing bean or cell.getCell() is null");
			clearSession(session);
			model.put("nullCellError", true);
			return model;
		}
				
		if(request.get("feedbackReturn") != null){
			//feedbackReturn is returned from FeedbackHelperController and is the Id of the wizardPage of the cell.
			cell.setCell(matrixManager.getCellFromPage(idManager.getId(request.get("feedbackReturn").toString())));
			if(request.get("feedbackAction") != null && request.get("feedbackAction").toString().equals("save")){
				model.put("feedbackSent", true);
			}
		}
	

		
		
		model.put("matrixCanViewCell", false);
		if(request.get("comingFromWizard") == null){
			//depending on isDefaultFeedbackEval, either send the scaffolding id or the scaffolding cell's id
			boolean matrixCanEvaluate = getMatrixManager().hasPermission(cell.getCell()
					.getScaffoldingCell().isDefaultEvaluators() ? cell.getCell()
							.getScaffoldingCell().getScaffolding().getId() : cell.getCell()
							.getScaffoldingCell().getWizardPageDefinition().getId(),
							cell.getCell().getScaffoldingCell().getScaffolding().getWorksiteId(),
							MatrixFunctionConstants.EVALUATE_MATRIX);
			model.put("matrixCanEvaluate", matrixCanEvaluate);
			//depending on isDefaultFeedbackEval, either send the scaffolding id or the scaffolding cell's id
			//also, compare first result with the user's cell review list by sending the user's cell id
			boolean allowParticipantFeedback = cell.getCell()
					.getScaffoldingCell().isDefaultReviewers() ? cell.getCell()
					.getScaffoldingCell().getScaffolding()
					.isAllowRequestFeedback() : cell.getCell()
					.getScaffoldingCell().getWizardPageDefinition()
					.isAllowRequestFeedback();
			boolean matrixCanReview = getMatrixManager().hasPermission(cell.getCell()
					.getScaffoldingCell().isDefaultReviewers() ? cell.getCell()
							.getScaffoldingCell().getScaffolding().getId() : cell.getCell()
							.getScaffoldingCell().getWizardPageDefinition().getId(),
							cell.getCell().getScaffoldingCell().getScaffolding().getWorksiteId(),
							MatrixFunctionConstants.REVIEW_MATRIX)
							|| (allowParticipantFeedback && getMatrixManager().hasPermission(cell.getCell().getWizardPage().getId(),
									cell.getCell().getScaffoldingCell().getScaffolding().getWorksiteId(),
									MatrixFunctionConstants.FEEDBACK_MATRIX));
			model.put("matrixCanReview", matrixCanReview);
			
			boolean hasAnyReviewers =	cell.getCell().getScaffoldingCell().isDefaultReviewers() ? 
					!getMatrixManager().getSelectedUsers(cell.getCell().getScaffoldingCell().getScaffolding(), MatrixFunctionConstants.REVIEW_MATRIX).isEmpty()
					: !getMatrixManager().getSelectedUsers(cell.getCell().getScaffoldingCell().getWizardPageDefinition(), MatrixFunctionConstants.REVIEW_MATRIX).isEmpty();
				model.put("hasAnyReviewers", hasAnyReviewers);
				
			// NOTE: matrixCanEval or Review both return true if the user is a
			// super user:
			if (getMatrixManager().canAccessMatrixCell(cell.getCell())) {
				model.put("matrixCanViewCell", true);
			}
		}else{
			WizardPage currentWizPage = getMatrixManager().getWizardPage(cell.getCell().getWizardPage().getId());
			Id wizPageDefId = currentWizPage.getPageDefinition().getId();
			String wizardId = getWizardManager().getWizardPageSeqByDef(wizPageDefId).getCategory().getWizard().getId().getValue();
			model.put("wizardId", wizardId);
			model.put("isWizardOwner", getSessionManager().getCurrentSessionUserId().equals(currentWizPage.getOwner().getId().getValue()));
		}
		
		if(request.get("decPageId") != null && request.get("decWrapperTag") != null && request.get("decSiteId") != null){
			//make sure that we are not coming from another wizard page which should grant you access to this page
			String pageId = (String) request.get("decPageId");
			String siteId = (String) request.get("decSiteId");
			
			if(getMatrixManager().canUserAccessWizardPageAndLinkedArtifcact(siteId, pageId, "/wizard/page/" + cell.getCell().getWizardPage().getId().getValue())){
				model.put("matrixCanViewCell", true);
			}
			
		}
		
		model.put("isMatrix", "true");
		model.put("isWizard", "false");
		model.put("enableReviewEdit", getEnableReviewEdit());
		model.put("currentUser", getSessionManager().getCurrentSessionUserId());
		model.put("CURRENT_GUIDANCE_ID_KEY", "session."
				+ GuidanceManager.CURRENT_GUIDANCE_ID);

		model.put("isEvaluation", "false");

		// This is the tool session so evaluation tool gets "is_eval_page_id"
		// and the matrix/wizard does not
		if (session.getAttribute("is_eval_page_id") != null) {
			String eval_page_id = (String) session
					.getAttribute("is_eval_page_id");
			model.put("isEvaluation", "true");
		}

		model.put("pageTitleKey", "view_cell");

		// Check for cell being deleted while user was attempting to view
		if (cell.getCell() == null) {
			clearSession(session);
			return model;
		}

		
		String pageId = cell.getCell().getWizardPage().getId().getValue();
		String siteId = cell.getCell().getWizardPage().getPageDefinition().getSiteId();
		model.put("siteId", idManager.getId(siteId));
		List reviews =	
			getReviewManager().getReviewsByParentAndType( pageId, Review.FEEDBACK_TYPE, siteId, getEntityProducer() );
		ArrayList<Node> cellForms = new ArrayList<Node>(getMatrixManager().getPageForms(cell.getCell().getWizardPage()));
		Collections.sort(cellForms, new NodeNameComparator());
		
		if(cell.getCell().getScaffoldingCell().getWizardPageDefinition().isDefaultCustomForm() && request.get("comingFromWizard") == null){
			model.put("cellFormDefs", processAdditionalForms(cell.getCell()
					.getScaffoldingCell().getScaffolding().getAdditionalForms()));
		}else{
			model.put("cellFormDefs", processAdditionalForms(cell.getCell()
					.getScaffoldingCell().getAdditionalForms()));
		}

		model.put("assignments", getUserAssignments(cell)); 
		model.put("reviews", reviews ); // feedback
		model.put("evaluations", getReviewManager().getReviewsByParentAndType(
				pageId, Review.EVALUATION_TYPE, siteId, getEntityProducer()));
		model.put("reflections", getReviewManager().getReviewsByParentAndType(
				pageId, Review.REFLECTION_TYPE, siteId, getEntityProducer()));
		model.put("cellForms", cellForms );
		model.put("numCellForms", cellForms.size() );		

		Boolean readOnly = Boolean.valueOf(false);
				
		// Matrix-only initializations
		if (cell.getCell().getMatrix() != null) {
			model.put("allowItemFeedback", 
						 getAllowItemFeedback( cell.getCell().getScaffoldingCell().getScaffolding().getItemFeedbackOption(), 
                                         reviews, cellForms, cell.getNodes()) );
			model.put("allowGeneralFeedback", 
						 getAllowGeneralFeedback( cell.getCell().getScaffoldingCell().getScaffolding().getGeneralFeedbackOption(), reviews) );
			model.put("generalFeedbackNone", cell.getCell().getScaffoldingCell().getScaffolding().isGeneralFeedbackNone());
						 
			Agent owner = cell.getCell().getMatrix().getOwner();
			readOnly = isReadOnly(owner, getIdManager().getId(cell.getCell().getMatrix()
					.getScaffolding().getReference()));
					
			Cell pageCell = getMatrixManager().getCellFromPage(getIdManager().getId(pageId));
			Scaffolding scaffolding = pageCell.getMatrix().getScaffolding();
				
			model.put("objectId", scaffolding.getId().getValue());
			model.put("objectTitle", scaffolding.getTitle());
			model.put("objectDesc", scaffolding.getDescription());
			model.put("wizardOwner", rb.getFormattedMessage("matrix_of", new Object[]{owner.getDisplayName()}) );
		}

		model.put("readOnlyMatrix", readOnly);

      model.put("styles",
    		  getStyleManager().createStyleUrlList(getStyleManager().getStyles(getIdManager().getId(pageId))));

      if (getTaggingManager().isTaggable()) {
			TaggableItem item = wizardActivityProducer.getItem(cell.getCell()
					.getWizardPage());
			model.put("taggable", "true");

			//getMatrixManager().getTaggableItems will put the providers into the session
			Set<DecoratedTaggableItem> decoTaggableItems = getMatrixManager().getDecoratedTaggableItems(item, cell.getCell().getWizardPage().getPageDefinition().getReference(), cell.getCell().getWizardPage().getOwner().getId().getValue());
			List<DecoratedTaggableItem> decoTaggableItemList = new ArrayList<DecoratedTaggableItem>(decoTaggableItems);
			
			Collections.sort(decoTaggableItemList, decoTaggableItemComparator);
			model.put("taggableItems", decoTaggableItemList);

			
			ToolSession toolSession = getSessionManager()
					.getCurrentToolSession();
			List<DecoratedTaggingProvider> providers = (List) toolSession
					.getAttribute(HibernateMatrixManagerImpl.PROVIDERS_PARAM);
			//but just double check to make sure that providers doesn't exist
			if (providers == null) {
				providers = getMatrixManager().getDecoratedProviders(item.getActivity());
				toolSession.setAttribute(HibernateMatrixManagerImpl.PROVIDERS_PARAM, providers);
			}
			model.put("helperInfoList", getHelperInfo(item));
			model.put("providers", providers);
			model.put("criteriaRef", cell.getCell().getWizardPage().getPageDefinition().getReference());
						
			model.put("decoWrapper", "ospMatrix_" + siteId + "_" + pageId);
		}

		clearSession(session);
		return model;
	}
	
	public static Comparator<DecoratedTaggableItem> decoTaggableItemComparator;
	static {
		decoTaggableItemComparator = new Comparator<DecoratedTaggableItem>() {
			public int compare(DecoratedTaggableItem o1, DecoratedTaggableItem o2) {
				return o1.getTypeName().toLowerCase().compareTo(
						o2.getTypeName().toLowerCase());
			}
		};
	}
	
	/**
	 ** Return true if general feedback is allowed based on feedback options
	 **/
	protected Boolean getAllowGeneralFeedback( int feedbackOption, List reviews )
	{
		boolean allowGeneralFeedback = true;
		
		if ( feedbackOption==WizardMatrixConstants.FEEDBACK_OPTION_SINGLE )
		{
			for (Iterator it=reviews.iterator(); it.hasNext();)
			{
				if ( ((Review)it.next()).getItemId() == null )
				{
					allowGeneralFeedback = false;
					break;
				}
			}
		}
		else if ( feedbackOption==WizardMatrixConstants.FEEDBACK_OPTION_NONE )
		{
			allowGeneralFeedback = false;
		}
		
		return Boolean.valueOf(allowGeneralFeedback);
	}
	
	/**
	 ** Return boolean array if item feedback is allowed based on feedback options
	 **/
	protected Boolean[] getAllowItemFeedback( int feedbackOption, List reviews, List<Node> cellForms, List attachments )
	{
		Boolean[] allowItemFeedback = new Boolean[cellForms.size()+attachments.size()];
		int index = -1;

      // First loop through forms		
		for (Iterator cIt=cellForms.iterator(); cIt.hasNext();)
		{
			index++;
			Node   thisNode   = (Node)cIt.next();
				
			if ( feedbackOption==WizardMatrixConstants.FEEDBACK_OPTION_SINGLE )
			{
				allowItemFeedback[index] = true;
				for (Iterator rIt=reviews.iterator(); rIt.hasNext();)
				{
					Review thisReview = (Review)rIt.next();
					if ( thisReview.getItemId() != null &&
						  thisReview.getItemId().equals(thisNode.getId().getValue()) )
					{
						allowItemFeedback[index] = false;
						break;
					}
				}
			}
			else if ( feedbackOption==WizardMatrixConstants.FEEDBACK_OPTION_NONE )
			{
				allowItemFeedback[index] = false;
			}
			else
			{
				allowItemFeedback[index] = true;
			}
		}
		
		// Second loop through attachments
		for (Iterator aIt=attachments.iterator(); aIt.hasNext();)
		{
			index++;
			Node   thisNode   = (Node)aIt.next();
				
			if ( feedbackOption==WizardMatrixConstants.FEEDBACK_OPTION_SINGLE )
			{
				allowItemFeedback[index] = true;
				for (Iterator rIt=reviews.iterator(); rIt.hasNext();)
				{
					Review thisReview = (Review)rIt.next();
					if ( thisReview.getItemId() != null &&
						  thisReview.getItemId().equals(thisNode.getId().getValue()) )
					{
						allowItemFeedback[index] = false;
						break;
					}
				}
			}
			else if ( feedbackOption==WizardMatrixConstants.FEEDBACK_OPTION_NONE )
			{
				allowItemFeedback[index] = false;
			}
			else
			{
				allowItemFeedback[index] = true;
			}
		}
		
		return allowItemFeedback;
	}
	
	/**
	 ** Return list of AssignmentSubmissions, associated with this cell
	 ** for the current user
	 **/
	protected List getUserAssignments(CellFormBean cell) {
		ArrayList submissions = new ArrayList();
		try {
			Agent owner = cell.getCell().getWizardPage().getOwner();
			User user = UserDirectoryService.getUser(owner.getId().getValue());
			ArrayList assignments = 
				AssignmentHelper.getSelectedAssignments(cell.getCell().getWizardPage().getPageDefinition().getAttachments());
			
			for ( Iterator it=assignments.iterator(); it.hasNext(); ) {
				Assignment assign = (Assignment)it.next();
				AssignmentSubmission assignSubmission = AssignmentService.getSubmission( assign.getId(),
																												 user );
				if (assignSubmission != null)
					submissions.add(assignSubmission);
			}
		}
		catch ( Exception e ) {
			logger.warn(".getUserAssignments: " + e.toString());
		}
		
		return submissions;
	}

	protected String getEntityProducer() {
		return MatrixContentEntityProducer.MATRIX_PRODUCER;
	}

	protected Boolean isReadOnly(Agent owner, Id id) {
      if ((owner != null && owner.equals(getAuthManager().getAgent()))
         && (id == null || getAuthzManager().isAuthorized(
         MatrixFunctionConstants.CAN_USE_SCAFFOLDING, id))) {
         return Boolean.valueOf(false);
      }
		return Boolean.valueOf(true);
	}
	
   protected String getStyleUrl(Style style) {
      Node styleNode = getMatrixManager().getNode(style.getStyleFile());
      return styleNode.getExternalUri();
   }

	public Object fillBackingObject(Object incomingModel, Map request,
			Map session, Map application) throws Exception {
		// coming from matrix cell, not helper
		session.remove(WizardPageHelper.WIZARD_PAGE);

		CellFormBean cellBean = (CellFormBean) incomingModel;

		String strId = (String) request.get("page_id");
		if (strId == null) {
			strId = (String) session.get("page_id");
			session.remove("page_id");
		}

		Cell cell;
		Id id = getIdManager().getId(strId);

		// Check if the cell has been removed, which can happen if:
		// (1) user views matrix
		// (2) owner removes column or row (the code verifies that no one has
		// modified the matrix)
		// (3) user selects a cell that has just been removed with the column or
		// row
		try {
			cell = matrixManager.getCellFromPage(id);

			cellBean.setCell(cell);

			List nodeList = new ArrayList(matrixManager.getPageContents(cell
					.getWizardPage()));
			cellBean.setNodes(nodeList);

         if (request.get("view_user") != null) {
            session.put("view_user", cell.getWizardPage().getOwner()
               .getId().getValue());
         }
		} catch (Exception e) {
			logger.error("Error with cell: " + strId + " " + e.toString());
			// tbd how to report error back to user?
		}

		clearSession(getSessionManager().getCurrentToolSession());
		return cellBean;
	}

	private String ListToString(String[] strArray) {
		String result = "";
		if (strArray != null) {
			for (int i = 0; i < strArray.length; i++) {
            if (i == 0) {
               result = strArray[i];
            } else {
               result = result.concat(",").concat(strArray[i]);
            }
			}
		}
		return result;
	}

   protected List createStylesList(List styles) {
      List returned = new ArrayList(styles.size());
      for (Iterator<Style> i=styles.iterator();i.hasNext();) {
         returned.add(getStyleUrl(i.next()));
      }

      return returned;
   }
   
	public ModelAndView handleRequest(Object requestModel, Map request,
			Map session, Map application, Errors errors) {
		CellFormBean cellBean = (CellFormBean) requestModel;
		Cell cell = cellBean.getCell();

		// Check for cell being deleted while user was attempting to view
      if (cell == null) {
         return new ModelAndView("matrixError");
      }

		// String action = (String)request.get("action");
		String submit = (String) request.get("submit");
		String matrixAction = (String) request.get("matrix");
		String submitAction = (String) request.get("submitAction");
		String inviteFeedback = (String) request.get("inviteFeedback");
		String submitForReview = (String) request.get("submitForReview");

		
		if(inviteFeedback != null){
			session.put("feedbackCellId", cell.getId().getValue());
			session.put("feedbackMatrixCall", "feedbackMatrixCall");

			return new ModelAndView("feedbackHelper");
		}
		if(submitForReview != null){
			Map map = new HashMap();
			map.put("page_id", cell.getWizardPage().getId());
			map.put("feedbackCellId", cell.getId().getValue());
			map.put("cellBean", cellBean);
			return new ModelAndView("inviteFeedbackConfirm", map);
		}

		if ("tagItem".equals(submitAction)) {
			return tagItem(cell, request, session);
		} else if ("sortList".equals(submitAction)) {
			return sortList(request, session);
		} else if ("pageList".equals(submitAction)) {
			return pageList(request, session);
		}

		if (submit != null) {
			Map map = new HashMap();
			map.put("page_id", cell.getWizardPage().getId());
			map.put("selectedArtifacts", ListToString(cellBean
					.getSelectedArtifacts()));
			map.put("cellBean", cellBean);
			// cwm change this to use the reflection submission confirmation
			return new ModelAndView("confirm", map);
		}

		if (matrixAction != null) {
			Map map = new HashMap();
			String scaffId = "";
			String viewUser = "";
			if (getTaggingManager().isTaggable()) {
				session.remove(HibernateMatrixManagerImpl.PROVIDERS_PARAM);
			}

			if (cell.getMatrix() != null) {
				scaffId = cell.getMatrix().getScaffolding().getId().getValue();
				viewUser = cell.getMatrix().getOwner().getId().getValue();
			}

			map.put("scaffolding_id", scaffId);
			map.put("view_user", viewUser);
			
			if (session.get("is_eval_page_id") != null) {
				String eval_page_id = (String) session.get("is_eval_page_id");
				String pageId = cell.getWizardPage().getId().getValue();
				if (eval_page_id.equals(pageId)) {
					return new ModelAndView("cancelEvaluation");
				}
			}

			return new ModelAndView("cancel", map);
		}

		return new ModelAndView("success", "cellBean", cellBean);
	}

	

	protected ModelAndView tagItem(Cell cell, Map request, Map session) {
		ModelAndView view = null;
		// Get appropriate helperInfo
		for (TaggingHelperInfo info : getHelperInfo(wizardActivityProducer
				.getItem(cell.getWizardPage()))) {
			if (info.getProvider().getId().equals(request.get("providerId"))) {
				// Add parameters to session
				for (String key : info.getParameterMap().keySet()) {
					session.put(key, info.getParameterMap().get(key));
				}
				session.put("page_id", (String) request.get("page_id"));
				view = new ModelAndView(new RedirectView(info.getHelperId()
						+ ".helper"));
				break;
			}
		}
		return view;
	}

	protected ModelAndView sortList(Map request, Map session) {
		String providerId = (String) request.get("providerId");
		String criteria = (String) request.get("criteria");

		List<DecoratedTaggingProvider> providers = (List) getSessionManager()
				.getCurrentToolSession().getAttribute(HibernateMatrixManagerImpl.PROVIDERS_PARAM);
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
		session.put("page_id", (String) request.get("page_id"));
		return new ModelAndView(new RedirectView((String) request.get("view")));
	}

	protected ModelAndView pageList(Map request, Map session) {
		String page = (String) request.get("page");
		String pageSize = (String) request.get("pageSize");
		String providerId = (String) request.get("providerId");

		List<DecoratedTaggingProvider> providers = (List) getSessionManager()
				.getCurrentToolSession().getAttribute(HibernateMatrixManagerImpl.PROVIDERS_PARAM);
		for (DecoratedTaggingProvider dtp : providers) {
			if (dtp.getProvider().getId().equals(providerId)) {
				DTaggingPager pager = dtp.getPager();
				pager.setPageSize(Integer.valueOf(pageSize));
				if (DTaggingPager.FIRST.equals(page)) {
					pager.setFirstItem(0);
				} else if (DTaggingPager.PREVIOUS.equals(page)) {
					pager.setFirstItem(pager.getFirstItem()
							- pager.getPageSize());
				} else if (DTaggingPager.NEXT.equals(page)) {
					pager.setFirstItem(pager.getFirstItem()
							+ pager.getPageSize());
				} else if (DTaggingPager.LAST.equals(page)) {
					pager.setFirstItem((pager.getTotalItems() / pager
							.getPageSize())
							* pager.getPageSize());
				}
				break;
			}
		}
		session.put("page_id", (String) request.get("page_id"));
		return new ModelAndView(new RedirectView((String) request.get("view")));
	}

	protected List processAdditionalForms(List formTypes) {
		List retList = new ArrayList();
		for (Iterator iter = formTypes.iterator(); iter.hasNext();) {
			String strFormDefId = (String) iter.next();
			StructuredArtifactDefinitionBean bean = getStructuredArtifactDefinitionManager()
					.loadHome(strFormDefId);
			if (bean != null) {
				bean.getDescription();
				// cwm use a different bean below, as the name has implications
				retList.add(new CommonFormBean(strFormDefId, bean
						.getDescription(), strFormDefId, bean.getOwner()
						.getName(), bean.getModified()));
			}
		}
		return retList;
	}

	protected void clearSession(ToolSession session) {
		session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
		session.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);
		session.removeAttribute(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER);

		session.removeAttribute(ResourceEditingHelper.CREATE_TYPE);
		session.removeAttribute(ResourceEditingHelper.CREATE_PARENT);
		session.removeAttribute(ResourceEditingHelper.CREATE_SUB_TYPE);
		session.removeAttribute(ResourceEditingHelper.ATTACHMENT_ID);

		session.removeAttribute(ReviewHelper.REVIEW_TYPE);
		session.removeAttribute(ReviewHelper.REVIEW_TYPE_KEY);

		session.removeAttribute(FormHelper.XSL_OBJECT_ID);
		session.removeAttribute(FormHelper.XSL_OBJECT_TITLE);
		session.removeAttribute(FormHelper.XSL_WIZARD_PAGE_ID);

		session.removeAttribute(WHICH_HELPER_KEY);
		session.removeAttribute(KEEP_HELPER_LIST);

	}

	protected List<TaggingHelperInfo> getHelperInfo(TaggableItem item) {
		List<TaggingHelperInfo> infoList = new ArrayList<TaggingHelperInfo>();
		if (getTaggingManager().isTaggable()) {
			for (TaggingProvider provider : getTaggingManager().getProviders()) {
				// Only get helpers for accepted rating providers
				if (ratingProviderIds.contains(provider.getId())) {
					TaggingHelperInfo info = provider.getItemHelperInfo(item
							.getReference());
					if (info != null) {
						infoList.add(info);
					}
				}
			}
		}
		return infoList;
	}


	
	/**
	 ** If enabled, users may edit/delete reviews (feedback, evaluation, reflection) according to these rules:
	 **    Feedback -- edit/delete option (even after status COMPLETE)
	 **    Evaluations - edit/delete option (but not allowed when status COMPLETE)
	 **    Relections - edit/delete option, prior to submitting (status READY)
	 */
	public boolean getEnableReviewEdit() {
		return enableReviewEdit;
	}
	public void setEnableReviewEdit( boolean enableReviewEdit ) {
		this.enableReviewEdit = enableReviewEdit;
	}
	
	/**
	 * @return
	 */
	public AuthenticationManager getAuthManager() {
		return authManager;
	}

	/**
	 * @param manager
	 */
	public void setAuthManager(AuthenticationManager manager) {
		authManager = manager;
	}

	/**
	 * @return
	 */
	public IdManager getIdManager() {
		return idManager;
	}

	/**
	 * @param manager
	 */
	public void setIdManager(IdManager manager) {
		idManager = manager;
	}

	/**
	 * @return Returns the matrixManager.
	 */
	public MatrixManager getMatrixManager() {
		return matrixManager;
	}

	/**
	 * @param matrixManager
	 *            The matrixManager to set.
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
	 * @param reviewManager
	 *            The reviewManager to set.
	 */
	public void setReviewManager(ReviewManager reviewManager) {
		this.reviewManager = reviewManager;
	}

	/**
	 * @return Returns the structuredArtifactDefinitionManager.
	 */
	public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
		return structuredArtifactDefinitionManager;
	}

	/**
	 * @param structuredArtifactDefinitionManager
	 *            The structuredArtifactDefinitionManager to set.
	 */
	public void setStructuredArtifactDefinitionManager(
			StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
		this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
	}

	public AuthorizationFacade getAuthzManager() {
		return authzManager;
	}

	public void setAuthzManager(AuthorizationFacade authzManager) {
		this.authzManager = authzManager;
	}

	public TaggingManager getTaggingManager() {
		return taggingManager;
	}

	public void setTaggingManager(TaggingManager taggingManager) {
		this.taggingManager = taggingManager;
	}
	
	public WizardActivityProducer getWizardActivityProducer() {
		return wizardActivityProducer;
	}

	public void setWizardActivityProducer(
			WizardActivityProducer wizardActivityProducer) {
		this.wizardActivityProducer = wizardActivityProducer;
	}

	public List<String> getRatingProviderIds() {
		return ratingProviderIds;
	}

	public void setRatingProviderIds(List<String> ratingProviderIds) {
		this.ratingProviderIds = ratingProviderIds;
	}

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public StyleManager getStyleManager() {
		return styleManager;
	}

	public void setStyleManager(StyleManager styleManager) {
		this.styleManager = styleManager;
	}

	public ServerConfigurationService getServerConfigurationService() {
		return serverConfigurationService;
	}

	public void setServerConfigurationService(
			ServerConfigurationService serverConfigurationService) {
		this.serverConfigurationService = serverConfigurationService;
	}

	public SecurityService getSecurityService() {
		return securityService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public WizardManager getWizardManager() {
		return wizardManager;
	}

	public void setWizardManager(WizardManager wizardManager) {
		this.wizardManager = wizardManager;
	}

}
