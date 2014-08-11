package org.theospi.portfolio.presentation.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.sakaiproject.metaobj.shared.FormHelper;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationItem;
import org.theospi.portfolio.presentation.model.PresentationItemDefinition;

public class EditPresentationFormController extends AbstractCalloutController {

	//NOTE: This controller handles the creation/edits of forms from the context
	//      of an existing presentation. It sets the return view to the contents
	//      of that presentation.
	
	protected IdManager idManager;
	
	public EditPresentationFormController() {
		setReturnView("editContentRedirect");
	}
	
	@Override
	protected Map<String, Object> getSessionParams(String presentationId, HttpServletRequest request) {
		String formTypeId = request.getParameter("formTypeId");
		String formId = request.getParameter("formId");
		String itemDefId = request.getParameter("itemDefId");
		
		if (formId != null)
			return presentationService.editForm(presentationId, formTypeId, formId, itemDefId);
		else {
			return presentationService.createForm(presentationId, formTypeId, itemDefId);
		}
	}

	@Override
	protected void save(String presentationId, String reference,
			HttpSession session) {
		String itemDefId = (String) session.getAttribute(FormHelper.PRESENTATION_ITEM_DEF_ID);
		//Check for an itemDefId...if none, was an edit and don't need to do anything
		if (itemDefId != null) {
			Presentation presentation = presentationService.getPresentation(presentationId);
			PresentationItemDefinition itemDef = presentationService.getPresentationItemDefinition(itemDefId);
			PresentationItem pi = new PresentationItem();
			pi.setArtifactId(idManager.getId(reference));
			pi.setDefinition(itemDef);
			Set<PresentationItem> items = (Set<PresentationItem>)presentation.getPresentationItems();
			int size = items.size();
			if (!itemDef.isAllowMultiple() && size > 0) {
				//If I can only have one item and there is already one, clear it so the new one wins
				clearItemsByType(itemDef, items);
			}
			items.add(pi);
			presentationService.savePresentation(presentation, false, true);
		}
		return;
	}
	
	/**
	 * Iterate through the presentation items and remove the once that match the passed item definition
	 * @param itemDef
	 * @param items
	 * @return The number of items removed
	 */
	private int clearItemsByType(PresentationItemDefinition itemDef, Set<PresentationItem> items) {
		int countRemoved = 0;
		List<PresentationItem> toRemove = new ArrayList<PresentationItem>();
		for (PresentationItem item : items) {
			if (item.getDefinition() == itemDef) {
				toRemove.add(item);
			}
		}
		for (PresentationItem item : toRemove) {
			items.remove(item);
			countRemoved++;
		}
		return countRemoved;
	}
	
	@Override
	protected void cleanUpSession(HttpSession session) {
		super.cleanUpSession(session);
		session.removeAttribute(FormHelper.PRESENTATION_ITEM_DEF_ID);
		session.removeAttribute(FormHelper.PRESENTATION_ID);
		session.removeAttribute(FormHelper.PRESENTATION_ITEM_DEF_NAME);
		session.removeAttribute(FormHelper.PRESENTATION_TEMPLATE_ID);
	}
	
	public void setIdManager(IdManager idManager) {
		this.idManager = idManager;
	}
	
}
