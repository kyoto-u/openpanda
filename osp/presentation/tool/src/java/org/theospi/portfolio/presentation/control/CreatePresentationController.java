package org.theospi.portfolio.presentation.control;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.support.CreatePresentationCommand;
import org.theospi.portfolio.presentation.support.CreatePresentationValidator;
import org.theospi.portfolio.presentation.support.PresentationService;

public class CreatePresentationController extends SimpleFormController {
	private PresentationService presentationService;
	
	public CreatePresentationController() {
		setFormView("createPresentation");
		setSuccessView("editPresentationRedirect");
		setCommandClass(CreatePresentationCommand.class);
		setValidator(new CreatePresentationValidator());
	}
	
	@Override
	protected Map referenceData(HttpServletRequest request, Object command, Errors errors) throws Exception {
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("availableTemplates", presentationService.getAvailableTemplates());
		model.put("freeFormTemplateId", Presentation.FREEFORM_TEMPLATE_ID);
		model.put("freeFormEnabled", presentationService.isFreeFormEnabled());
		return model;
	}
	
	@Override
	protected ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		if (request.getParameter("cancel") != null)
			return new ModelAndView("listPresentationRedirect");
		return super.processFormSubmission(request, response, command, errors);
	}
	
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
		CreatePresentationCommand bean = (CreatePresentationCommand) command;
		Presentation presentation = presentationService.createPresentation(bean.getPresentationType(), bean.getTemplateId());
		if(null != bean.getPresentationName() && !"".equals(bean.getPresentationName().trim())){
			presentation.setName(bean.getPresentationName());
		}
		if (presentation == null) {
			errors.reject("error.presentationTypeRequired");
			return showForm(request, response, errors);
		}
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("id", presentation.getId().getValue());
		return new ModelAndView(getSuccessView(), model);
	}

	public void setPresentationService(PresentationService presentationService) {
		this.presentationService = presentationService;
	}
}
