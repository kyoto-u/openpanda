
/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.2/matrix/tool/src/java/org/theospi/portfolio/matrix/control/SubmitCellConfirmationController.java $
* $Id: SubmitCellConfirmationController.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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

import java.util.HashMap;
import java.util.Map;

import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.wizard.WizardFunctionConstants;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.WizardPageSequence;

/**
 * @author chmaurer
 */
public class SubmitCellConfirmationController implements LoadObjectController, CustomCommandController {

	IdManager idManager = null;
	MatrixManager matrixManager = null;
	WizardManager wizardManager = null;

	/* (non-Javadoc)
	 * @see org.theospi.utils.mvc.intf.CustomCommandController#formBackingObject(java.util.Map, java.util.Map, java.util.Map)
	 */
	public Object formBackingObject(Map request, Map session, Map application) {
		return new HashMap();
	}

	/* (non-Javadoc)
	 * @see org.theospi.utils.mvc.intf.LoadObjectController#fillBackingObject(java.lang.Object, java.util.Map, java.util.Map, java.util.Map)
	 */
	public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
		return incomingModel;
	}

	/* (non-Javadoc)
	 * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
	 */
	public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
		boolean isCellPage = false;
		String view = "continueSeq";

		WizardPage page = (WizardPage) session.get(WizardPageHelper.WIZARD_PAGE);

		Cell cell = null;
		if (page == null) {
			Id pageId = idManager.getId((String) request.get("page_id"));
			cell = getMatrixManager().getCellFromPage(pageId);
			page = cell.getWizardPage();
			isCellPage = true;
		} else {
			WizardPageSequence seq = wizardManager.getWizardPageSeqByDef(page.getPageDefinition().getId());

			if(seq.getCategory().getWizard().getType().equals(WizardFunctionConstants.WIZARD_TYPE_HIERARCHICAL))
				view = "continueHier";
		}
		String submitAction = (String)request.get("submit");
		String cancelAction = (String)request.get("cancel");
		if (submitAction != null) {
			Id reviewObjectId = null;
			String parentTitle = "";
			if(isCellPage){
				if(cell.getScaffoldingCell().isDefaultEvaluators()){
					reviewObjectId = cell.getScaffoldingCell().getScaffolding().getId();
				}else{
					reviewObjectId = cell.getScaffoldingCell().getWizardPageDefinition().getId();
				}
				parentTitle = cell.getScaffoldingCell().getScaffolding().getTitle();
			}else{
				
				parentTitle = wizardManager.getWizardPageSeqByDef(page.getPageDefinition().getId()).getCategory().getWizard().getName();
				reviewObjectId = page.getPageDefinition().getId();
			}
			getMatrixManager().notifyAudience(page, reviewObjectId, true, null, null, parentTitle, MatrixFunctionConstants.EVALUATE_MATRIX);

			if (!isCellPage) {
				getMatrixManager().submitPageForEvaluation(page);
				session.put("altDoneURL", "submitWizardPage");
				session.put("submittedPage", page);

				if (isLast(session)) {
					view = "done";
				}

				return new ModelAndView(view, "page_id", page.getId().getValue());
			}
			else {
				getMatrixManager().submitCellForEvaluation(cell);
			}


			return new ModelAndView(view, "page_id", page.getId().getValue());
		}
		if (cancelAction != null) {
			// the current page is set to the next page after the submitted page for confirmation.
			//    So the current step needs to be rolled back
			Object stepObj = (Object) session.get(WizardPageHelper.SEQUENTIAL_WIZARD_CURRENT_STEP);
			if (stepObj != null && stepObj instanceof Integer && !isLast(session)) {
				session.put(WizardPageHelper.SEQUENTIAL_WIZARD_CURRENT_STEP, Integer.valueOf(((Integer)stepObj).intValue() - 1) );
			}
			return new ModelAndView(view, "page_id", page.getId().getValue());
		}
		return new ModelAndView("success", "page", page);
	}

	protected boolean isLast(Map session) {
		return session.get(WizardPageHelper.IS_LAST_STEP) != null;
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

	public WizardManager getWizardManager() {
		return wizardManager;
	}

	public void setWizardManager(WizardManager wizardManager) {
		this.wizardManager = wizardManager;
	}
}
