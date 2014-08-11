package org.theospi.portfolio.presentation.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.security.AudienceSelectionHelper;
import org.theospi.portfolio.security.AuthorizationFacade;

public class NotifyViewersController implements Controller {

	private ListScrollIndexer listScrollIndexer;
	private IdManager idManager;
	private PresentationManager presentationManager;
	private AuthenticationManager authManager;
	private AuthorizationFacade authzManager;
	
	public ModelAndView handleRequest(Object requestModel, Map request,
			Map session, Map application, Errors errors) {

		Map model = new HashMap();
		
		//this checks if the user requested feedback.  There are two return values: 1. inviteFeedbackReturn (user clicked cancel or just finish)
		//2. inviteFeedbackNotify (user clicked finish and notify button).  Both values return the cell id.  inviteFeedbackNotify needs to call another
		//helper to finish the notify part.
		
		if(request.get("inviteFeedbackReturn") != null){		
			Id id = getIdManager().getId(request.get("inviteFeedbackReturn").toString());
			Presentation presentation = getPresentationManager().getPresentation(id);
			return setupPresentationList(model, request, presentation);
		}else if(request.get("presentationId") != null){
			Id id = getIdManager().getId(request.get("presentationId").toString());
			Presentation presentation = getPresentationManager().getPresentation(id);
			setAudienceSelectionVariables(presentation, session);
			return new ModelAndView("notifyAudience");
		}
			
		
		return null;
	}
	
	protected ModelAndView setupPresentationList(Map model, Map request, Presentation presentation) {
        model.put("isMaintainer", isMaintainer());

        List presentations = new ArrayList(getPresentationManager().findSharedPresentations(getAuthManager().getAgent(),
                ToolManager.getCurrentPlacement().getId(), PresentationManager.PRESENTATION_VIEW_HIDDEN));


        model.put("presentations", getListScrollIndexer().indexList(request, model, presentations));
        model.put("osp_agent", getAuthManager().getAgent());

        return new ModelAndView("success", model);
    }
	
	protected Map setAudienceSelectionVariables(Presentation presentation, Map session) {

		String id = presentation.getId()!=null ? presentation.getId().getValue() : presentation.getNewId().getValue();
		
		session.put(AudienceSelectionHelper.AUDIENCE_FUNCTION, 
				AudienceSelectionHelper.AUDIENCE_FUNCTION_PORTFOLIO);

		session.put(AudienceSelectionHelper.AUDIENCE_QUALIFIER, id);
		session.put(AudienceSelectionHelper.AUDIENCE_CANCEL_TARGET, "inviteFeedbackReturn=" + id);
		session.put(AudienceSelectionHelper.AUDIENCE_SAVE_NOTIFY_TARGET, "inviteFeedbackNotify=" + id);
		session.put(AudienceSelectionHelper.AUDIENCE_SAVE_TARGET, "inviteFeedbackReturn=" + id);
		session.put(AudienceSelectionHelper.CONTEXT, presentation.getName());

		
		return session;
	}
	
	protected Boolean isMaintainer(){
	      return Boolean.valueOf(getAuthzManager().isAuthorized(WorksiteManager.WORKSITE_MAINTAIN,
	            getIdManager().getId(ToolManager.getCurrentPlacement().getContext())));
	   }

	public ListScrollIndexer getListScrollIndexer() {
		return listScrollIndexer;
	}

	public void setListScrollIndexer(ListScrollIndexer listScrollIndexer) {
		this.listScrollIndexer = listScrollIndexer;
	}

	public IdManager getIdManager() {
		return idManager;
	}

	public void setIdManager(IdManager idManager) {
		this.idManager = idManager;
	}

	public PresentationManager getPresentationManager() {
		return presentationManager;
	}

	public void setPresentationManager(PresentationManager presentationManager) {
		this.presentationManager = presentationManager;
	}

	public AuthenticationManager getAuthManager() {
		return authManager;
	}

	public void setAuthManager(AuthenticationManager authManager) {
		this.authManager = authManager;
	}

	public AuthorizationFacade getAuthzManager() {
		return authzManager;
	}

	public void setAuthzManager(AuthorizationFacade authzManager) {
		this.authzManager = authzManager;
	}

}
