/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.1/glossary/tool/src/java/org/theospi/portfolio/help/control/GlossaryEntryValidator.java $
* $Id: GlossaryEntryValidator.java 83138 2010-10-07 14:22:28Z aaronz@vt.edu $
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

/**
 * 
 */
package org.theospi.portfolio.help.control;

import org.springframework.validation.Errors;
import org.theospi.portfolio.help.model.GlossaryEntry;
import org.theospi.utils.mvc.impl.ValidatorBase;
import org.sakaiproject.util.FormattedText;

/**
 * @author chrismaurer
 *
 */
public class GlossaryEntryValidator extends ValidatorBase {


	public boolean supports(Class clazz) {
		if (GlossaryEntry.class.isAssignableFrom(clazz)) return true;
		else return false;
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	public void validate(Object obj, Errors errors) {
		GlossaryEntry entry = (GlossaryEntry)obj;
		if (entry.getTerm() == null || entry.getTerm().equals("")) {
         errors.rejectValue("term", "error.required", "required");
		}
		if (entry.getTerm() != null && entry.getTerm().length() > 255) {
         errors.rejectValue("description", "error.lengthExceded", new Object[]{"255"}, "Value must be less than {0} characters");
		}
		if (entry.getDescription() == null || entry.getDescription().equals("")) {
         errors.rejectValue("description", "error.required", "required");
		}
		if (entry.getDescription() != null && entry.getDescription().length() > 255) {
         errors.rejectValue("description", "error.lengthExceded", new Object[]{"255"}, "Value must be less than {0} characters");
		}
      if (entry.getLongDescription() == null || entry.getLongDescription().equals("")
			|| entry.getLongDescription().equals("<br />")) {
         errors.rejectValue("longDescription", "error.required", "required");
		}

		StringBuilder sbError = new StringBuilder();
		String testLongDesc = FormattedText.processFormattedText(entry.getLongDescription(), sbError);
		if (sbError.length() > 0) {
         errors.rejectValue("longDescription", "error.html.format", sbError.toString());
		}
		else {
			entry.setLongDescription(testLongDesc);
		}

		//TODO Should there be a length check on the long description?
		
	}

}
