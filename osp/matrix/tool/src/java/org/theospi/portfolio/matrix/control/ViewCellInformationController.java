package org.theospi.portfolio.matrix.control;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ObjectNotFoundException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.spring.util.SpringTool;
import org.sakaiproject.tool.api.SessionManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.WizardPageSequence;

public class ViewCellInformationController implements Controller{

	private MatrixManager matrixManager;
	private WizardManager wizardManager;
	private IdManager idManager = null;
	private SessionManager sessionManager;
	protected final Log logger = LogFactory.getLog(getClass());
	
	public ModelAndView handleRequest(Object requestModel, Map request,
			Map session, Map application, Errors errors) {
		
		
		WizardPageDefinition wizPageDef = null;
		Map<String, Object> model = new HashMap<String, Object>();
		
		String siteTitle = "";
		
		String printFriendly = (String) request.get("printFriendly");
		if (printFriendly != null) {
			model.put("printFriendly", true);
		}
		
		String strId = (String) request.get("sCell_id");
		if (strId == null) {
			strId = (String) session.get("sCell_id");
			session.remove("sCell_id");
		}

		if (strId == null) {
			//must have passed something else
			strId = (String) session.get("page_def_id");
			session.remove("page_def_id");
		}

		ScaffoldingCell sCell = null;
		Id id = getIdManager().getId(strId);


		try {
			sCell = matrixManager.getScaffoldingCell(id);
		} catch (ObjectNotFoundException e) {
			logger.debug("Can't find scaffolding cell with id: " + strId + ".  Trying as a wizard page definition.");
		}
		if (sCell == null) {
			sCell = matrixManager.getScaffoldingCellByWizardPageDef(id);
		}

		if(sCell != null) {
			wizPageDef = sCell.getWizardPageDefinition();

			siteTitle = sCell.getScaffolding().getWorksiteName();
			String matrixTitle = sCell.getScaffolding().getTitle();
			model.put("matrix_title", matrixTitle);
		}
		else {
			//it must be a wizard
			wizPageDef = matrixManager.getWizardPageDefinition(id);
			try {
				Site site = SiteService.getSite(wizPageDef.getSiteId());
				siteTitle = site.getTitle();
				WizardPageSequence wps = wizardManager.getWizardPageSeqByDef(wizPageDef.getId());
				String wizardTitle = wps.getCategory().getWizard().getName();
				model.put("wizard_title", wizardTitle);
			} catch (IdUnusedException e) {
				logger.warn("unable to find site: " + wizPageDef.getSiteId());
			}
		}
		
		model.put("site_title", siteTitle);
		model.put("wizardPageDef", wizPageDef);
		
		String overrideLastView = (String)request.get("override." + SpringTool.LAST_VIEW_VISITED);
        if (overrideLastView != null && !"".equalsIgnoreCase(overrideLastView)) {
        	session.put(SpringTool.LAST_VIEW_VISITED, overrideLastView);
        }
		
		return new ModelAndView("success", model);
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

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public WizardManager getWizardManager() {
		return wizardManager;
	}

	public void setWizardManager(WizardManager wizardManager) {
		this.wizardManager = wizardManager;
	}

}
