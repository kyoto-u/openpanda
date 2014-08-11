/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/DeletePresentationController.java $
* $Id:DeletePresentationController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
* Copyright (c) 2009 The Sakai Foundation
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
package org.theospi.portfolio.presentation.control;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.support.PresentationService;

public class EditContentController extends SimpleFormController {
	private PresentationService presentationService;
	
	public EditContentController() {
		setCommandClass(Presentation.class);
		setCommandName("presentation");
		setFormView("editContent");
		setSuccessView("editContentRedirect");
	}
	
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		String presentationId = request.getParameter("id");
		return presentationService.getPresentation(presentationId);
	}
	
	@Override
	protected Map referenceData(HttpServletRequest request, Object command, Errors errors) throws Exception {
		Presentation presentation = (Presentation) command;
		Map model = presentationService.getPresentationArtifacts(presentation.getId().getValue());
      
		boolean disableShare = !presentationService.isOwner(presentation);
		boolean optionsAreNull = presentation.getTemplate().getPropertyFormType() != null && presentation.getPropertyForm() == null;
      
		model.put("baseUrl", PresentationService.VIEW_PRESENTATION_URL);
		model.put("optionsAreNull", Boolean.valueOf(optionsAreNull));
		model.put("disableShare", Boolean.valueOf(disableShare));
		model.put("currentUser", SessionManager.getCurrentSessionUserId());
      
		return model;
	}
   
	@Override
   protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
		Presentation presentation = presentationService.savePresentation((Presentation) command, false, true);
		if (presentation == null)
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return showForm(request,response,errors);
	}
   
   
	@Override
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(Id.class, presentationService.getIdCustomEditor());
		binder.registerCustomEditor(presentationService.getPresentationItemCustomEditor().getType(), presentationService.getPresentationItemCustomEditor());
	}
		
	public void setPresentationService(PresentationService presentationService) {
		this.presentationService = presentationService;
	}
	
}
