package org.theospi.portfolio.presentation.support;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.theospi.portfolio.presentation.model.Presentation;

public class UpdatePresentationValidator implements Validator {

	public boolean supports(Class clazz) {
		return (clazz.equals(Presentation.class));
	}

	public void validate(Object obj, Errors errors) {
		Presentation presentation = (Presentation) obj;
		if (presentation.getId() == null || "".equals(presentation.getId().getValue()))
			errors.rejectValue("id", "error.required", "Portfolio ID required");
		
		// validate name not blank and length <= 255 characters
		if (presentation.getName() != null && "".equals(presentation.getName())) {
			errors.rejectValue("name", "error.required", "Portfolio Name required");
		} else {
			if (presentation.getName() != null && presentation.getName().length() > 255) {
				errors.rejectValue("name", "error.lengthExceeded", new Object[]{"255"}, "Name must be less than {0} characters");
			}
		}
		
		if (presentation.getDescription() != null && presentation.getDescription().length() > 255)
			errors.rejectValue("description", "error.lengthExceeded", new Object[]{"255"}, "Description must be less than {0} characters");
	}

}
