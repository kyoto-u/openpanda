package org.theospi.portfolio.presentation.support;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.theospi.portfolio.presentation.model.Presentation;

public class CreatePresentationValidator implements Validator {
	
	public CreatePresentationValidator() {}
	
	public boolean supports(Class clazz) {
		return (clazz.equals(CreatePresentationCommand.class));
	}

	public void validate(Object obj, Errors errors) {
		CreatePresentationCommand bean = (CreatePresentationCommand) obj;
		String type = bean.getPresentationType();
		String templateId = bean.getTemplateId();
		if (type == null || "".equals(type)
				|| (!Presentation.FREEFORM_TYPE.equals(type) && !Presentation.TEMPLATE_TYPE.equals(type))
				|| (templateId == null || "".equals(templateId))) {
            errors.reject("error.portfolioTypeRequired", "You must select a portfolio type.");
		}
	}
}
