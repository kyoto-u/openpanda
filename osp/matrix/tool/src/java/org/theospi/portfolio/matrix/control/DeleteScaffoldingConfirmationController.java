/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.1/matrix/tool/src/java/org/theospi/portfolio/matrix/control/DeleteScaffoldingConfirmationController.java $
 * $Id: DeleteScaffoldingConfirmationController.java 68687 2009-11-09 16:45:06Z chmaurer@iupui.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2007, 2008 The Sakai Foundation
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.security.AuthorizationFailedException;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.taggable.api.Link;
import org.sakaiproject.taggable.api.LinkManager;
import org.sakaiproject.taggable.api.TaggableActivity;
import org.sakaiproject.taggable.api.TaggingManager;
import org.sakaiproject.taggable.api.TaggingProvider;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Matrix;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.wizard.taggable.api.WizardActivityProducer;


/**
 * Delete scaffolding and associated matrix data if user confirms
 */
public class DeleteScaffoldingConfirmationController implements Controller {

	private MatrixManager matrixManager = null;

	private IdManager idManager = null;

	private AuthorizationFacade authzManager = null;
	
	private AuthenticationManager authnManager = null;

	private TaggingManager taggingManager = null;

	private WizardActivityProducer wizardActivityProducer = null;
	
	private LinkManager linkManager = null;

	private final Log logger = LogFactory.getLog(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object,
	 *      java.util.Map, java.util.Map, java.util.Map,
	 *      org.springframework.validation.Errors)
	 */
	public ModelAndView handleRequest(Object requestModel, Map request,
			Map session, Map application, Errors errors) {
		String viewName = "success";
		Id id = idManager.getId((String) request.get("scaffolding_id"));
		Scaffolding scaffolding = getMatrixManager().getScaffolding(id);

		Map model = new HashMap();
		model.put("scaffolding_published", scaffolding.isPublished());
		
		int linkedSitesNum = 0, totalLinksNum = 0;
		try {
			List<String> uniqueSites = new ArrayList<String>();
			Set<ScaffoldingCell> sCells = getMatrixManager().getScaffoldingCells(scaffolding.getId());
			//go through each cell and look up the links to that cell
			for (Iterator iterator = sCells.iterator(); iterator.hasNext();) {
				ScaffoldingCell sCell = (ScaffoldingCell) iterator.next();
				List<Link> linksList = getLinkManager().getLinks(sCell.getWizardPageDefinition().getReference(), true);
				for (Iterator iterator2 = linksList.iterator(); iterator2.hasNext();) {
					//for each link check to see if the site is a new site and increment counter
					Link link = (Link) iterator2.next();								
					if(!uniqueSites.contains(link.getActivityRef())){
						//if activity already exists, then we know the site already exists
						uniqueSites.add(link.getActivityRef());
						String context = getTaggingManager().getContext(link.getActivityRef());
						if(!uniqueSites.contains(context)){
							//if this is a new site, then increment counter
							uniqueSites.add(context);
							linkedSitesNum++;
						}
					}					
				}
				totalLinksNum += linksList.size();
			}			
		} catch (PermissionException e1) {
			e1.printStackTrace();
		}
		
		
		model.put("linkedSitesNum", linkedSitesNum);
		model.put("totalLinksNum", totalLinksNum);
		
		

		String cancel = (String) request.get("cancel");
		String doit = (String) request.get("continue");

		if (cancel != null)
			return new ModelAndView("cancel", model);
		else if (doit == null)
			return new ModelAndView("delete", model);

		try{
			getAuthzManager().checkPermission(
					MatrixFunctionConstants.DELETE_SCAFFOLDING_ANY, id);
		}catch(AuthorizationFailedException e){
			//if exception thrown, then check to see if both: user owns matrix and has permission to 
			//delete own matrices
			getAuthzManager().checkPermission(
						MatrixFunctionConstants.DELETE_SCAFFOLDING_OWN, id);
			
		}

		if (scaffolding.getExposedPageId() != null
				&& !scaffolding.getExposedPageId().equals("")) {
			getMatrixManager().removeExposedMatrixTool(scaffolding);
		}

		// First delete any associated matrix data (if published scaffolding)
		List matrices = getMatrixManager().getMatrices(id);
		for (Iterator matrixIt = matrices.iterator(); matrixIt.hasNext();) {
			Matrix matrix = (Matrix) matrixIt.next();
			getMatrixManager().deleteMatrix(matrix.getId());
		}

		// if taggable, remove tags for all page defs
		try {
			if (getTaggingManager().isTaggable()) {
				Set<ScaffoldingCell> cells = getMatrixManager().getScaffoldingCells(scaffolding.getId());
				for (ScaffoldingCell cell : cells) {
					for (TaggingProvider provider : getTaggingManager()
							.getProviders()) {

						//Remove stuff where the cells are the activities
						TaggableActivity activity = getWizardActivityProducer()
								.getActivity(cell.getWizardPageDefinition());
						provider.removeTags(activity);
						
						//Remove stuff where the cell is the linked item
						List<Link> links = getLinkManager().getLinks(cell.getWizardPageDefinition().getReference(), true);
						for (Link link : links) {
							getLinkManager().removeLink(link);
						}
					}
				}
			}
		} catch (PermissionException pe) {
			logger.error(pe.getMessage(), pe);
		}

		// Next delete the scaffolding
		getMatrixManager().deleteScaffolding(id);

		return new ModelAndView("success", model);
	}

	public AuthorizationFacade getAuthzManager() {
		return authzManager;
	}

	public void setAuthzManager(AuthorizationFacade facade) {
		authzManager = facade;
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

	public AuthenticationManager getAuthnManager() {
		return authnManager;
	}

	public void setAuthnManager(AuthenticationManager authnManager) {
		this.authnManager = authnManager;
	}

	public LinkManager getLinkManager() {
		return linkManager;
	}

	public void setLinkManager(LinkManager linkManager) {
		this.linkManager = linkManager;
	}
}
