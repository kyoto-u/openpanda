/**********************************************************************************
 * $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/BaseScaffoldingCellController.java $
 * $Id:BaseScaffoldingCellController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.taggable.api.TaggingManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.tool.api.SessionManager;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.workflow.mgt.WorkflowManager;
import org.theospi.portfolio.workflow.model.Workflow;

public class BaseScaffoldingCellController {

	private AuthorizationFacade authzManager;

	private MatrixManager matrixManager;

	private IdManager idManager;

	private GuidanceManager guidanceManager;

	private WorkflowManager workflowManager;

	private TaggingManager taggingManager;

	private SessionManager sessionManager;

	protected static final String PROVIDERS_PARAM = "providers";

	public Object fillBackingObject(Object incomingModel, Map request,
			Map session, Map application) throws Exception {
		ScaffoldingCell scaffoldingCell = (ScaffoldingCell) incomingModel;
		if (request.get(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG) == null
				&& session
						.get(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG) == null) {
			Id sCellId = ((ScaffoldingCell) incomingModel).getId();
			if (sCellId == null) {
				sCellId = getIdManager().getId(
						(String) request.get("scaffoldingCell_id"));
			}

			scaffoldingCell = getMatrixManager().getScaffoldingCell(sCellId);
			EditedScaffoldingStorage sessionBean = new EditedScaffoldingStorage(
					scaffoldingCell);
			session
					.put(
							EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY,
							sessionBean);
		} else {
			EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage) session
					.get(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
			scaffoldingCell = sessionBean.getScaffoldingCell();
		}
		// Check for guidance
		checkForGuidance(session, scaffoldingCell);
		// Traversing the collection to un-lazily load
		scaffoldingCell.getWizardPageDefinition().getEvalWorkflows().size();
		for (Iterator i = scaffoldingCell.getWizardPageDefinition()
				.getEvalWorkflows().iterator(); i.hasNext();) {
			Workflow w = (Workflow) i.next();
			w.getItems().size();
		}

		getMatrixManager().removeFromSession(scaffoldingCell);
		scaffoldingCell.getScaffolding().isPublished();
		return scaffoldingCell;
	}

	protected void checkForGuidance(Map session, ScaffoldingCell scaffoldingCell) {
		if (session.get(GuidanceManager.CURRENT_GUIDANCE) != null) {
			Guidance guidance = (Guidance) session
					.get(GuidanceManager.CURRENT_GUIDANCE);
			scaffoldingCell.setGuidanceId(guidance.getId());

			session.remove(GuidanceManager.CURRENT_GUIDANCE);
		}
		if (scaffoldingCell.getGuidanceId() != null
				&& scaffoldingCell.getGuidance() == null) {
			scaffoldingCell.setGuidance(getGuidanceManager().getGuidance(
					scaffoldingCell.getGuidanceId()));
			scaffoldingCell.setGuidanceId(null);
		}
	}

	protected void saveScaffoldingCell(Map request,
			ScaffoldingCell scaffoldingCell) {

		getMatrixManager().removeFromSession(scaffoldingCell);
		ScaffoldingCell oldScaffoldingCell = getMatrixManager()
				.getScaffoldingCell(scaffoldingCell.getRootCriterion(),
						scaffoldingCell.getLevel());
		// String oldStatus =
		// matrixManager.getScaffoldingCellsStatus(scaffoldingCell.getId());
		getMatrixManager().removeFromSession(oldScaffoldingCell);

		String oldStatus = oldScaffoldingCell.getInitialStatus();

		Set<Workflow> evalWorkflows = new HashSet<Workflow>();
		if (scaffoldingCell.isDefaultEvaluationForm()) {
			evalWorkflows = getWorkflowManager().createEvalWorkflows(scaffoldingCell.getWizardPageDefinition(), 
					scaffoldingCell.getScaffolding().getEvaluationDevice());
		}
		else {
			evalWorkflows = getWorkflowManager().createEvalWorkflows(scaffoldingCell.getWizardPageDefinition());
		}
		scaffoldingCell.getWizardPageDefinition().setEvalWorkflows(new HashSet(evalWorkflows));
		
		getMatrixManager().storeScaffoldingCell(scaffoldingCell);
		scaffoldingCell.getScaffolding().setModifiedDate(new Date(System.currentTimeMillis()));
		getMatrixManager().storeScaffolding(scaffoldingCell.getScaffolding());
		List cells = getMatrixManager().getCellsByScaffoldingCell(
				scaffoldingCell.getId());
		for (Iterator iter = cells.iterator(); iter.hasNext();) {
			Cell cell = (Cell) iter.next();
			if (!oldStatus.equals(scaffoldingCell.getInitialStatus())
					&& (cell.getStatus().equals(
							MatrixFunctionConstants.LOCKED_STATUS) || cell
							.getStatus().equals(
									MatrixFunctionConstants.READY_STATUS))) {
				cell.setStatus(scaffoldingCell.getInitialStatus());
				getMatrixManager().storeCell(cell);
			}
		}
		if (scaffoldingCell.getDeleteGuidanceId() != null) {
			Guidance guidance = getGuidanceManager().getGuidance(
					scaffoldingCell.getDeleteGuidanceId());
			getGuidanceManager().deleteGuidance(guidance);
		}
	}

	protected Set createEvalWorkflows(WizardPageDefinition wpd) {
		return getWorkflowManager().createEvalWorkflows(wpd);
	}

	public AuthorizationFacade getAuthzManager() {
		return authzManager;
	}

	public void setAuthzManager(AuthorizationFacade authzManager) {
		this.authzManager = authzManager;
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

	/**
	 * @return Returns the guidanceManager.
	 */
	public GuidanceManager getGuidanceManager() {
		return guidanceManager;
	}

	/**
	 * @param guidanceManager
	 *            The guidanceManager to set.
	 */
	public void setGuidanceManager(GuidanceManager guidanceManager) {
		this.guidanceManager = guidanceManager;
	}

	/**
	 * @return Returns the workflowManager.
	 */
	public WorkflowManager getWorkflowManager() {
		return workflowManager;
	}

	/**
	 * @param workflowManager
	 *            The workflowManager to set.
	 */
	public void setWorkflowManager(WorkflowManager workflowManager) {
		this.workflowManager = workflowManager;
	}

	public TaggingManager getTaggingManager() {
		return taggingManager;
	}

	public void setTaggingManager(TaggingManager taggingManager) {
		this.taggingManager = taggingManager;
	}

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
}
