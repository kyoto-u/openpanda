package org.theospi.portfolio.presentation.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.metaobj.shared.model.Id;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.support.PresentationService;
import org.theospi.portfolio.presentation.support.UpdatePresentationValidator;
import org.theospi.portfolio.security.AuthorizationFailedException;

public class UpdatePresentationController extends AbstractCommandController {
	private PresentationService presentationService;
	
	public UpdatePresentationController() {
		setCommandClass(Presentation.class);
		setValidator(new UpdatePresentationValidator());
	}
	
	@Override
	protected ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
		if (errors.hasErrors()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "You have submitted bad input -- check the API");
			// This call should return a MaV that contains the error information. 
			// return new ModelAndView("editPresentation", errors.getModel());
			return null;
		}
		
		Boolean active = null;
		if (request.getParameter("active") != null)
			active = Boolean.valueOf(request.getParameter("active"));
		
		Boolean allowComments = null;
		if (request.getParameter("allowComments") != null)
			allowComments = Boolean.valueOf(request.getParameter("allowComments"));
		
		Presentation presentation = (Presentation) command;
		try {
			if (!presentationService.updatePresentation(presentation.getId().getValue(), presentation.getName(), presentation.getDescription(), active, allowComments)) {				
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		catch (AuthorizationFailedException e) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
		}
		return null;
	}
	
	@Override
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(Id.class, presentationService.getIdCustomEditor());
		binder.setAllowedFields(new String[] {"id", "name", "description"});
	}

	public void setPresentationService(PresentationService presentationService) {
		this.presentationService = presentationService;
	}

}
