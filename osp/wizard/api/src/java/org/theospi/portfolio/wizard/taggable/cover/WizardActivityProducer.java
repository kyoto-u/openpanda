package org.theospi.portfolio.wizard.taggable.cover;

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.taggable.api.TaggableItem;
import org.theospi.portfolio.matrix.model.WizardPage;

public class WizardActivityProducer {

	private static org.theospi.portfolio.wizard.taggable.api.WizardActivityProducer m_instance = null;

	public static org.theospi.portfolio.wizard.taggable.api.WizardActivityProducer getInstance() {
		if (ComponentManager.CACHE_COMPONENTS) {
			if (m_instance == null)
				m_instance = (org.theospi.portfolio.wizard.taggable.api.WizardActivityProducer) ComponentManager
				.get(org.theospi.portfolio.wizard.taggable.api.WizardActivityProducer.class);
			return m_instance;
		} else {
			return (org.theospi.portfolio.wizard.taggable.api.WizardActivityProducer) ComponentManager
			.get(org.theospi.portfolio.wizard.taggable.api.WizardActivityProducer.class);
		}
	}

	public static TaggableItem getItem(WizardPage wizardPage){
		org.theospi.portfolio.wizard.taggable.api.WizardActivityProducer service = getInstance();
		if (service == null)
			return null;

		return service.getItem(wizardPage);
	}
}
