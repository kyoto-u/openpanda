package org.theospi.portfolio.presentation.control;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationComment;
import org.theospi.portfolio.presentation.support.PresentationService;
import org.theospi.portfolio.presentation.intf.FreeFormHelper;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.cover.ContentHostingService;
import org.sakaiproject.entity.api.Entity;

public class EditPresentationController extends SimpleFormController {
	private PresentationService presentationService;
	private static String REFERENCE_ROOT_METAOBJ = Entity.SEPARATOR+"metaobj";
	
	public EditPresentationController() {
		setCommandClass(Presentation.class);
		setCommandName("presentation");
		setFormView("editPresentation");
		setSuccessView("listPresentationRedirect");
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		//NOTE: Authorization failures and bad IDs throw exceptions here
		ToolSession session = SessionManager.getCurrentToolSession();
		String presentationId = request.getParameter("id");
		if ( presentationId != null && ! presentationId.equals("") )
			return presentationService.getPresentation(presentationId);
		else 
			return session.getAttribute(FreeFormHelper.FREE_FORM_PREFIX + "presentation");
	}
	
	@Override
	protected Map referenceData(HttpServletRequest request, Object command, Errors errors) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		Presentation presentation = (Presentation) command;
		if (presentation.getExpiresOn() == null || presentation.getExpiresOn().after(new Date())) {
			model.put("active", Boolean.TRUE);
		}
		else {
			model.put("active", Boolean.FALSE);
		}
		List<PresentationComment> comments = presentationService.getComments(presentation.getId().getValue());
		model.put("comments", comments);
		model.put("numComments", Integer.valueOf(comments.size()));
      
		boolean isOwner = presentationService.isOwner(presentation);
		boolean optionsAreNull = presentation.getTemplate().getPropertyFormType() != null && presentation.getPropertyForm() == null;
		
		model.put("baseUrl", PresentationService.VIEW_PRESENTATION_URL);
		model.put("optionsAreNull", Boolean.valueOf(optionsAreNull));
		model.put("disableShare", Boolean.valueOf(!isOwner));
		model.put("disableOptions", Boolean.valueOf(!isOwner));
      
		return model;
	}

	/** Return access url for given formId
	 **/	 
	private String getAccessUrl( String formId ) {
		StringBuilder formUrl = new StringBuilder();
		formUrl.append( ServerConfigurationService.getAccessUrl() );
		formUrl.append( REFERENCE_ROOT_METAOBJ );
		formUrl.append( ContentHostingService.getReference(ContentHostingService.resolveUuid(formId)) );
		return formUrl.toString();
	}
	
	@Override
	protected ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)	throws Exception {	
		Presentation presentation = (Presentation) command;
		if ( presentation.getIsFreeFormType() && 
			  request.getParameter("freeFormContent")!= null && 
			  request.getParameter("freeFormContent").equals("true") )
		{
			ToolSession session = SessionManager.getCurrentToolSession();
			session.setAttribute(FreeFormHelper.FREE_FORM_PREFIX + "presentation", presentation);
			return new ModelAndView("freeFormPresentationRedirect");
		}

		else
		{
			// refresh portfolio prior to redisplay
			HashMap map = new HashMap();
			map.put("id", presentation.getId().getValue());
			return new ModelAndView("editPresentationRedirect", map );
		}
	}
	
	public void setPresentationService(PresentationService presentationService) {
		this.presentationService = presentationService;
	}

}
