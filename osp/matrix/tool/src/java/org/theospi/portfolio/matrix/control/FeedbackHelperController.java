package org.theospi.portfolio.matrix.control;

import java.util.HashMap;
import java.util.Map;

import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.security.AudienceSelectionHelper;


public class FeedbackHelperController implements Controller {

	private MatrixManager matrixManager;
	private IdManager idManager = null;

	
	public ModelAndView handleRequest(Object requestModel, Map request,
			Map session, Map application, Errors errors) {
		
		if(session.get("feedbackMatrixCall") != null){
			if(session.get("feedbackCellId") != null){
				Cell cell = matrixManager.getCell(idManager.getId(session.get("feedbackCellId").toString()));
				setAudienceSelectionVariables(cell, session);
				session.remove("feedbackMatrixCall");
				session.remove("feedbackCellId");
				return new ModelAndView("inviteFeedback");	
			}
		}
		
		Map model = new HashMap();
		
		//this checks if the user requested feedback.  There are two return values: 1. inviteFeedbackReturn (user clicked cancel or just finish)
		//2. inviteFeedbackNotify (user clicked finish and notify button).  Both values return the cell id.  inviteFeedbackNotify needs to call another
		//helper to finish the notify part.
		if(request.get("inviteFeedbackReturn") != null || request.get("inviteFeedbackNotify") != null){
			if(request.get("inviteFeedbackReturn") != null){
				model.put("page_id", request.get("inviteFeedbackReturn"));
				model.put("feedbackReturn", request.get("inviteFeedbackReturn"));
				String action = (session.get("feedbackAction") != null && session.get(
						"feedbackAction").toString().equals("save")) ? "save"
						: "cancel";
				model.put("feedbackAction", action);
				HashMap<String, String> extraEmailAddrs = new HashMap<String, String>();
				if(session.get("extraEmailAddrs") != null){
					extraEmailAddrs = (HashMap<String, String>) session.get("extraEmailAddrs");
					session.remove("extraEmailAddrs");
				}
				String emailMessage = null;
				if(session.get("emailMessage") != null){
					emailMessage = (String) session.get("emailMessage");
					session.remove("emailMessage");
				}
				//Send email notification to Author Selected Reviewers:
				if("save".equals(action)){
					Cell cell = matrixManager.getCellFromPage(idManager.getId(request.get("inviteFeedbackReturn").toString()));
					
					
					//by passing null for reviewObjectId or/and function, we are saying to ignore any selected reviewers in
					//the matrix settings and only send emails to the ones the user selected.  To append
					//matrix selected reviewers, all you have to do is change null to 
					//MatrixFunctionConstants.REVIEW_MATRIX for function and pass the reviewObjectId
					getMatrixManager().notifyAudience(cell.getWizardPage(), null, true, extraEmailAddrs, emailMessage, cell.getScaffoldingCell().getScaffolding().getTitle(), null);				
				}
				session.remove("feedbackAction");
				return new ModelAndView("viewCell", model);
			}else if(request.get("inviteFeedbackNotify") != null){
				//inviteFeedbackNotify is returned from FeedbackHelperController and is the Id of the wizardPage of the cell.
				Cell cell = matrixManager.getCellFromPage(idManager.getId(request.get("inviteFeedbackNotify").toString()));
				setAudienceSelectionVariables(cell, session);				
				return new ModelAndView("notifyAudience");
			}
		}
		
		if(session.get("submitForReview") != null){
			session.remove("submitForReview");
			if(session.get("feedbackCellId") != null){
				Cell cell = matrixManager.getCell(idManager.getId(session.get("feedbackCellId").toString()));		
				session.remove("feedbackMatrixCall");
				session.remove("feedbackCellId");
				Id reviewObjectId = null;
				if(cell.getScaffoldingCell().isDefaultReviewers()){
					reviewObjectId = cell.getScaffoldingCell().getScaffolding().getId();
				}else{
					reviewObjectId = cell.getScaffoldingCell().getWizardPageDefinition().getId();
				}
				getMatrixManager().notifyAudience(cell.getWizardPage(), reviewObjectId, true, null, null, cell.getScaffoldingCell().getScaffolding().getTitle(), MatrixFunctionConstants.REVIEW_MATRIX);
				model.put("page_id", cell.getWizardPage().getId());
				model.put("feedbackReturn", cell.getWizardPage().getId());
				model.put("feedbackAction", "save");
				return new ModelAndView("viewCell", model);
			//	return new ModelAndView("viewScaffolding", "scaffolding_id", cell.getScaffoldingCell().getScaffolding().getId().getValue());	
			}			
		}
		
		return null;
	}
	
	protected Map setAudienceSelectionVariables(Cell cell, Map session) {

		session.put(AudienceSelectionHelper.AUDIENCE_FUNCTION, 
				AudienceSelectionHelper.AUDIENCE_FUNCTION_INVITE_FEEDBACK );

		String id = cell.getWizardPage().getId()!=null ? cell.getWizardPage().getId().getValue() : cell.getWizardPage().getNewId().getValue();
		session.put(AudienceSelectionHelper.AUDIENCE_QUALIFIER, id);
		session.put(AudienceSelectionHelper.AUDIENCE_SITE,cell.getWizardPage().getPageDefinition().getSiteId());

		session.put(AudienceSelectionHelper.AUDIENCE_CANCEL_TARGET, "inviteFeedbackReturn=" + id);
		session.put(AudienceSelectionHelper.AUDIENCE_SAVE_NOTIFY_TARGET, "inviteFeedbackNotify=" + id);
		session.put(AudienceSelectionHelper.AUDIENCE_SAVE_TARGET, "inviteFeedbackReturn=" + id);

		if(cell.getScaffoldingCell().isDefaultReviewers()){
			session.put(AudienceSelectionHelper.MATRIX_REVIEWER_OBJECT_ID, cell.getScaffoldingCell().getScaffolding().getId().getValue());
		}else{
			session.put(AudienceSelectionHelper.MATRIX_REVIEWER_OBJECT_ID, cell.getScaffoldingCell().getWizardPageDefinition().getId().getValue());
		}
		session.put(AudienceSelectionHelper.MATRIX_REVIEWER_FUNCTION, MatrixFunctionConstants.REVIEW_MATRIX);

		//cleans up any previous context values
		session.remove(AudienceSelectionHelper.CONTEXT);
		session.remove(AudienceSelectionHelper.CONTEXT2);

		if(cell.getScaffoldingCell().getScaffolding() != null){ 
			session.put(AudienceSelectionHelper.CONTEXT,
					cell.getScaffoldingCell().getScaffolding().getTitle());
		}
		session.put(AudienceSelectionHelper.CONTEXT2,
				cell.getScaffoldingCell().getWizardPageDefinition().getTitle());
		
		return session;
	}
	

	public MatrixManager getMatrixManager() {
		return matrixManager;
	}

	public void setMatrixManager(MatrixManager matrixManager) {
		this.matrixManager = matrixManager;
	}

	public IdManager getIdManager() {
		return idManager;
	}

	public void setIdManager(IdManager idManager) {
		this.idManager = idManager;
	}	
}
