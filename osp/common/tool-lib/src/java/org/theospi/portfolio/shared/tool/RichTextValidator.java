package org.theospi.portfolio.shared.tool;

import org.sakaiproject.util.FormattedText;

import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.application.FacesMessage;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Jul 28, 2008
 * Time: 10:55:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class RichTextValidator implements Validator, Serializable {

    /**
	 * @see javax.faces.validator.Validator#validate(javax.faces.context.FacesContext,
	 *      javax.faces.component.UIComponent, java.lang.Object)
	 */
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		if (value != null) {
         StringBuilder sbError = new StringBuilder();
         FormattedText.processFormattedText(value.toString(), sbError);
         
         if (sbError.length() > 0) {
            throw new ValidatorException(new FacesMessage(sbError.toString()));  
         }
      }
	}

}
