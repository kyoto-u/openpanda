/**********************************************************************************
 * $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/WizardPageController.java $
 * $Id:WizardPageController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
import java.util.List;
import java.util.Map;

import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.control.ToolFinishedView;
import org.sakaiproject.spring.util.SpringTool;
import org.sakaiproject.tool.api.ToolSession;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.style.model.Style;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.CompletedWizard;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.model.WizardPageSequence;

/**
 * openEvaluationPageHierRedirect will put the user here
 * 
 * Created by IntelliJ IDEA. User: John Ellis Date: Jan 24, 2006 Time: 3:46:49
 * PM To change this template use File | Settings | File Templates.
 */
public class WizardPageController extends CellController {

	private static ResourceLoader rb = new ResourceLoader("org.theospi.portfolio.matrix.bundle.Messages");
	private WizardManager wizardManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.theospi.utils.mvc.intf.FormController#referenceData(java.util.Map,
	 *      java.lang.Object, org.springframework.validation.Errors)
	 */
	public Map referenceData(Map request, Object command, Errors errors) {
		//this is so CellController knows that WizardPageController called it
		request.put("comingFromWizard", true);
		
		
		// Call superclass first -- code below depends on this
		Map model = super.referenceData(request, command, errors);
		
		ToolSession session = getSessionManager().getCurrentToolSession();

		Boolean wizardPreview = Boolean.valueOf( (String)request.get("wizardPreview") );
		CellFormBean cell = (CellFormBean) command;
		String pageId = cell.getCell().getWizardPage().getId().getValue();

		Agent owner = cell.getCell().getWizardPage().getOwner();
		if (owner == null)
			owner = (Agent) session.getAttribute(WizardPageHelper.WIZARD_OWNER);

		session.setAttribute(WizardPageHelper.WIZARD_OWNER, owner);

		Wizard wizard = getWizard(pageId);
		model.put("objectId", wizard.getId().getValue());
		model.put("objectTitle", wizard.getName());
		model.put("objectDesc", wizard.getDescription());

		List reviews = (List)model.get("reviews");
		List cellForms = (List)model.get("cellForms");
		
		model.put("allowItemFeedback", 
					 getAllowItemFeedback( wizard.getItemFeedbackOption(), reviews, cellForms, cell.getNodes()) );
		model.put("allowGeneralFeedback", 
					 getAllowGeneralFeedback( wizard.getGeneralFeedbackOption(), reviews) );
		model.put("generalFeedbackNone", wizard.isGeneralFeedbackNone());
		
		model.put("readOnlyMatrix", super.isReadOnly(owner, null));
		model.put("wizardOwner", rb.getFormattedMessage("wizard_of", new Object[]{owner.getDisplayName()}) );
		model.put("pageTitleKey", "view_wizardPage");
		model.put("helperPage", "true");
		model.put("isWizard", "true");
		model.put("isMatrix", "false");
		model.put("categoryTitle", request.get("categoryTitle"));
		model.put("wizardTitle", request.get("wizardTitle"));
		model.put("wizardDescription", request.get("wizardDescription"));
		
		
		//this is for directly linked wizard cells that an evaluator has clicked.  This will avoid a null pointed for
		//the wizard tool when calling getCurrent().
		if((session.getAttribute("CURRENT_WIZARD_ID") == null || request.get("directLinked") != null) && request.get("page_id") != null){
			WizardPage currentWizPage = getMatrixManager().getWizardPage(getIdManager().getId((String) request.get("page_id")));
			Id wizPageDefId = currentWizPage.getPageDefinition().getId();
			String wizardId = getWizardManager().getWizardPageSeqByDef(wizPageDefId).getCategory().getWizard().getId().getValue();
			session.setAttribute("WIZARD_RESET_CURRENT", "true");
			session.setAttribute("CURRENT_WIZARD_ID", wizardId);		
			session.setAttribute("WIZARD_USER_ID", currentWizPage.getOwner().getId().getValue());
		}
		
		return model;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Style getDefaultStyle(Id pageId) {
		// Get the wizard default style
		CompletedWizard cw = getWizardManager()
				.getCompletedWizardByPage(pageId);
		return cw.getWizard().getStyle();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Wizard getWizard(String pageId) {
		WizardPage page = getMatrixManager().getWizardPage(
				getIdManager().getId(pageId));
		WizardPageSequence seq = wizardManager.getWizardPageSeqByDef(page
				.getPageDefinition().getId());
		Wizard wizard = seq.getCategory().getWizard();
		return wizard;
	}

	/**
	 * If there is a page in the session we want to display that. Otherwise look
	 * in the request for "page_id" If you are getting the wrong page displayed
	 * then you should make sure that the appropriate session/request variables
	 * are set.
	 * 
	 * @param incomingModel
	 * @param request
	 * @param session
	 * @param application
	 * @throws Exception
	 */
	public Object fillBackingObject(Object incomingModel, Map request,
			Map session, Map application) throws Exception {
		Id pageId = null;
		WizardPage page = null;
		Object pageObj = session.get(WizardPageHelper.WIZARD_PAGE);
		 
		if (pageObj != null && pageObj instanceof WizardPage) {
			page = (WizardPage) pageObj;
			pageId = page.getId();
		}
		else
			pageId = getIdManager().getId((String) request.get("page_id"));
		page = getMatrixManager().getWizardPage(pageId);
		session.put(WizardPageHelper.WIZARD_PAGE, page);
		session.remove(WizardPageHelper.CANCELED);

		String overrideLastView = (String)request.get("override." + SpringTool.LAST_VIEW_VISITED);
        if (overrideLastView != null && !"".equalsIgnoreCase(overrideLastView)) {
        	session.put(SpringTool.LAST_VIEW_VISITED, overrideLastView);
        }

		Agent owner = (Agent) session.get(WizardPageHelper.WIZARD_OWNER);
		request.put(WizardPageHelper.WIZARD_OWNER, owner);

		WizardPageSequence seq = wizardManager.getWizardPageSeqByDef(page
				.getPageDefinition().getId());
		if (seq.getCategory().getParentCategory() != null)
			request.put("categoryTitle", seq.getCategory().getTitle());
		else
			request.put("categoryTitle", "");

		request.put("wizardPreview", Boolean.toString(seq.getCategory().getWizard().isPreview()));
		request.put("wizardTitle", seq.getCategory().getWizard().getName());
		request.put("wizardDescription", seq.getCategory().getWizard()
				.getDescription());

		Cell cell = getMatrixManager().createCellWrapper(page);

		CellFormBean cellBean = (CellFormBean) incomingModel;
		cellBean.setCell(cell);
		List nodeList = new ArrayList(getMatrixManager().getPageContents(page));
		cellBean.setNodes(nodeList);

		return cellBean;
	}

	public ModelAndView handleRequest(Object requestModel, Map request,
			Map session, Map application, Errors errors) {

		String submitWizardAction = (String) request.get("submitWizard");

		if (submitWizardAction != null) {
			session.put(ToolFinishedView.ALTERNATE_DONE_URL, "submitWizard");
			return new ModelAndView("confirmWizard", "", "");
		}

		return super.handleRequest(requestModel, request, session, application,
				errors);
	}

	public WizardManager getWizardManager() {
		return wizardManager;
	}

	public void setWizardManager(WizardManager wizardManager) {
		this.wizardManager = wizardManager;
	}
}
