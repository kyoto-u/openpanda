package org.theospi.portfolio.wizard.taggable.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.metaobj.shared.mgt.EntityProducerBase;

public class WizardReferenceEntityProducer extends EntityProducerBase {

	protected final Log logger = LogFactory.getLog(getClass());


	public void init() {
		logger.info("init()");
		try {
			getEntityManager().registerEntityProducer(this, WizardReference.REF_SEPARATOR + WizardReference.REF_BASE);
		}
		catch (Exception e) {
			logger.warn("Error registering WizardReference Entity Producer", e);
		}
	}

	public void destroy() {
		logger.info("destroy()");
	}

	public boolean parseEntityReference(String reference, Reference ref) {
		if (reference.startsWith(getContext())) {
			String[] parts = reference.split(WizardReference.REF_SEPARATOR, 4);
			if (parts.length < 4) {
				return false;
			}
			String type = parts[1];
			/*
			 * This is only really used so we know what kind of object we are
			 * referencing
			 */
			String subtype = parts[2];

			//String context = parts[2];
			/*
			 * This is only used when we have a reference to a specific object
			 */
			String id = parts[3];

			ref.set(type, subtype, id, null, null);

			return true;
		}
		return false;
	}

	public String getLabel()
	{
		return WizardReference.REF_BASE;
	}
	
	/*
	public Entity getEntity(Reference ref) {
		return null;
	}
	 */
}
