package org.theospi.portfolio.presentation.control;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sakaiproject.content.api.ResourceEditingHelper;
import org.sakaiproject.metaobj.shared.FormHelper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.theospi.portfolio.presentation.support.PresentationService;

public abstract class AbstractCalloutController extends AbstractController {
	protected PresentationService presentationService;
	protected String helperView = "formHelper";
	protected String returnView = "editPresentationRedirect";
		
	//There are only three ways this controller gets invoked
	// 1: Initial request -- set up session and call out to helper
	// 2: Return from helper -- handle helper action, tear down session, return to the return view
	// 2a: Save callback -- act on the details returned from helper to commit a change
	// 2b: Cancel callback -- clean up any extra materials required
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		Object helperAction = session.getAttribute(FormHelper.RETURN_ACTION_TAG);
		String cachedId = (String) session.getAttribute(FormHelper.PRESENTATION_ID);
		
		if (FormHelper.RETURN_ACTION_SAVE.equals(helperAction)) {
			return handleSave(cachedId, session);
		}
		else if (FormHelper.RETURN_ACTION_CANCEL.equals(helperAction)) {
			return handleCancel(cachedId, session);
		}
		else {
			return handleEdit(request);
		}
	}
	
	protected ModelAndView handleEdit(HttpServletRequest request) {
		String presentationId = request.getParameter("id");
		HttpSession session = request.getSession();
		cleanUpSession(session);
		for (Entry<String, Object> entry : getSessionParams(presentationId, request).entrySet())
			session.setAttribute(entry.getKey(), entry.getValue());
		session.setAttribute(FormHelper.PRESENTATION_ID, presentationId);
		return sendToHelper();
	}
	
	protected Map<String, Object> getSessionParams(String presentationId, HttpServletRequest request) {
		return new HashMap<String, Object>();
	}
	
	protected ModelAndView handleSave(String presentationId, HttpSession session) {
		String reference = (String) session.getAttribute(FormHelper.RETURN_REFERENCE_TAG);
		save(presentationId, reference, session);
		cleanUpSession(session);
		return sendToReturn(presentationId);
	}
		
	protected void save(String presentationId, String reference, HttpSession session) {
		return;
	}
	
	protected ModelAndView handleCancel(String presentationId, HttpSession session) {
		cancel(presentationId);
		cleanUpSession(session);
		return sendToReturn(presentationId);
	}	
	
	protected void cancel(String presentationId) {
		return;
	}
	
	protected void cleanUpSession(HttpSession session) {
        session.removeAttribute(ResourceEditingHelper.CREATE_TYPE);
        session.removeAttribute(ResourceEditingHelper.CREATE_SUB_TYPE);
        session.removeAttribute(ResourceEditingHelper.CREATE_PARENT);
        session.removeAttribute(ResourceEditingHelper.ATTACHMENT_ID);
        session.removeAttribute(FormHelper.RETURN_ACTION_TAG);
        session.removeAttribute(FormHelper.PARENT_ID_TAG);
        session.removeAttribute(FormHelper.NEW_FORM_DISPLAY_NAME_TAG);
        session.removeAttribute(FormHelper.PRESENTATION_ID);
	}
	
	private ModelAndView sendToHelper() {
		return new ModelAndView(helperView);
	}
	
	private ModelAndView sendToHelper(Map model) {
		return new ModelAndView(helperView, model);
	}
	
	private ModelAndView sendToReturn() {
		return new ModelAndView(returnView);
	}
	
	private ModelAndView sendToReturn(String presentationId) {
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("id", presentationId);
		return new ModelAndView(returnView, model);
	}
	
	private ModelAndView sendToReturn(Map model) {
		return new ModelAndView(returnView, model);
	}

	public String getHelperView() {
		return helperView;
	}
	
	public String getReturnView() {
		return returnView;
	}

	public void setReturnView(String returnView) {
		this.returnView = returnView;
	}

	public void setHelperView(String helperView) {
		this.helperView = helperView;
	}

	public void setPresentationService(PresentationService presentationService) {
		this.presentationService = presentationService;
	}

}
