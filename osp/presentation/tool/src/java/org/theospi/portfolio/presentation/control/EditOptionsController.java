package org.theospi.portfolio.presentation.control;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class EditOptionsController extends AbstractCalloutController {

	//NOTE: This is a very simple callout controller. It does not override the
	//      default helper or return views (form edit and presentation edit).
	//      It only provides the right IDs to work on the singleton options form
	//      and makes sure that the form is attached to the presentation on save.
	
	@Override
	protected Map<String, Object> getSessionParams(String presentationId, HttpServletRequest request) {
		return presentationService.editOptions(presentationId);
	}
	
	@Override
	protected void save(String presentationId, String reference, HttpSession session) {
		presentationService.saveOptions(presentationId, reference);
	}
}
