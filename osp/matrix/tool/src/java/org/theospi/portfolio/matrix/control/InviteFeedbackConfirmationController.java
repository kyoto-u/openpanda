package org.theospi.portfolio.matrix.control;

import java.util.HashMap;
import java.util.Map;

import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.WizardPage;

public class InviteFeedbackConfirmationController implements LoadObjectController, CustomCommandController{

	private IdManager idManager = null;
	private MatrixManager matrixManager = null;
	
	public ModelAndView handleRequest(Object requestModel, Map request,
			Map session, Map application, Errors errors) {

		String cancel = (String) request.get("cancel");
		String doit = (String) request.get("submit");

		WizardPage page = (WizardPage) session.get(WizardPageHelper.WIZARD_PAGE);
		if(page == null){
			Id pageId = getIdManager().getId((String) request.get("page_id"));
	        Cell cell = getMatrixManager().getCellFromPage(pageId);
	         page = cell.getWizardPage();
		}
		if (cancel != null){
			return new ModelAndView("cancel", "page_id", page.getId());
		}
		if (doit != null){
			session.put("submitForReview", "submitForReview");	
			session.put("feedbackCellId", request.get("feedbackCellId"));
			return new ModelAndView("feedbackHelper");
		}
		
		return new ModelAndView("success", "page", page);
	}


	public Object fillBackingObject(Object incomingModel, Map request,
			Map session, Map application) throws Exception {
		return incomingModel;
	}


	public Object formBackingObject(Map request, Map session, Map application) {
		return new HashMap();
	}


	public IdManager getIdManager() {
		return idManager;
	}


	public void setIdManager(IdManager idManager) {
		this.idManager = idManager;
	}


	public MatrixManager getMatrixManager() {
		return matrixManager;
	}


	public void setMatrixManager(MatrixManager matrixManager) {
		this.matrixManager = matrixManager;
	}

}
