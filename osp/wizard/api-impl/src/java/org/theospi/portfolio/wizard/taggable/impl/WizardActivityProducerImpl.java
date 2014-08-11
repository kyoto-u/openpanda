/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/osp/branches/sakai-2.8.x/wizard/api-impl/src/java/org/theospi/portfolio/wizard/taggable/impl/WizardActivityProducerImpl.java $
 * $Id: WizardActivityProducerImpl.java 85788 2010-12-01 19:16:22Z arwhyte@umich.edu $
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

package org.theospi.portfolio.wizard.taggable.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.taggable.api.TaggableActivity;
import org.sakaiproject.taggable.api.TaggableItem;
import org.sakaiproject.taggable.api.TaggingManager;
import org.sakaiproject.taggable.api.TaggingProvider;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.util.ResourceLoader;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.wizard.WizardFunctionConstants;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.CompletedWizard;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.model.WizardPageSequence;
import org.theospi.portfolio.wizard.taggable.api.WizardActivityProducer;

public class WizardActivityProducerImpl implements WizardActivityProducer {

	private static final Log logger = LogFactory
			.getLog(WizardActivityProducerImpl.class);

	protected static final ResourceLoader messages = new ResourceLoader(
			"org.theospi.portfolio.wizard.bundle.Messages");

	MatrixManager matrixManager;

	WizardManager wizardManager;

	IdManager idManager;

	TaggingManager taggingManager;

	AuthorizationFacade authzManager;
	
	private AuthenticationManager authnManager = null;

	SessionManager sessionManager;

	List<String> ratingProviderIds;

	public boolean allowGetItems(TaggableActivity activity,
			TaggingProvider provider, boolean getMyItemsOnly, String taggedItem) {
		// FIXME http://bugs.sakaiproject.org/jira/browse/GM-84
		return !getItems(activity, provider, getMyItemsOnly, taggedItem).isEmpty();
	}

	public boolean allowRemoveTags(TaggableActivity activity) {
		WizardPageDefinition pageDef = (WizardPageDefinition) activity
				.getObject();
		// Try to get a wizard page sequence
		WizardPageSequence ps = wizardManager.getWizardPageSeqByDef(pageDef
				.getId());
		boolean authorized = false;
		if (ps != null) {
			Wizard wizard = ps.getCategory().getWizard();
			/*
			 * If you own the wizard, or if you can delete wizards, or if you
			 * can revise wizards, then you are able to delete page definitions
			 * and can, therefore, remove tags.
			 */
			authorized = sessionManager.getCurrentSessionUserId()
					.equalsIgnoreCase(wizard.getOwner().getId().getValue())
					|| authzManager
							.isAuthorized(WizardFunctionConstants.EDIT_WIZARD,
									wizard.getId())
					|| authzManager.isAuthorized(
							WizardFunctionConstants.DELETE_WIZARD, wizard
									.getId());
		} else {
			ScaffoldingCell cell = matrixManager
					.getScaffoldingCellByWizardPageDef(pageDef.getId());
			/*
			 * If you can create or delete scaffolding, then you are able to
			 * delete scaffolding cells and can, therefore, remove tags.
			 */
			authorized = authzManager.isAuthorized(
					MatrixFunctionConstants.CREATE_SCAFFOLDING, cell
							.getScaffolding().getId())
					|| authzManager.isAuthorized(
							MatrixFunctionConstants.DELETE_SCAFFOLDING_ANY, cell
									.getScaffolding().getId()) || (authzManager.isAuthorized(
											MatrixFunctionConstants.DELETE_SCAFFOLDING_OWN, cell
											.getScaffolding().getId()) && cell.getScaffolding().getOwner().getId().equals(getAuthnManager().getAgent().getId()));
		}
		return authorized;
	}

	public boolean allowRemoveTags(TaggableItem item) {
		// It doesn't appear that you can remove individual items (pages)
		return false;
	}

	public boolean allowTransferCopyTags(TaggableActivity activity) {
		return false;
	}

	private boolean canEvaluate(WizardPage page) {
		boolean allowed = false;
		CompletedWizard cw = wizardManager.getCompletedWizardByPage(page
				.getId());
		allowed = authzManager
				.isAuthorized(
						(cw != null) ? WizardFunctionConstants.EVALUATE_SPECIFIC_WIZARDPAGE
								: MatrixFunctionConstants.EVALUATE_SPECIFIC_MATRIXCELL,
						page.getId());
		return allowed;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Checks that this can be returned as a valid instance of
	 * {@link WizardReference}.
	 */
	public boolean checkReference(String ref) {
		return (WizardReference.getReference(ref) != null ? true : false);
	}

	public List<TaggableActivity> getActivities(String context,
			TaggingProvider provider) {
		// We aren't picky about the provider, so ignore that argument.
		List<TaggableActivity> activities = new ArrayList<TaggableActivity>();
		for (WizardPageDefinition def : wizardManager.findWizardPageDefs(
				context, true)) {
			activities.add(getActivity(def));
		}
		return activities;
	}

	public TaggableActivity getActivity(String activityRef,
			TaggingProvider provider) {
		// We aren't picky about the provider, so ignore that argument.
		TaggableActivity activity = null;
		if (checkReference(activityRef)) {
			WizardReference reference = WizardReference
					.getReference(activityRef);
			if (reference != null) {
				WizardPageDefinition def = wizardManager
						.getWizardPageDefinition(idManager.getId(reference
								.getId()), true);
				activity = getActivity(def);
			}
		}
		return activity;
	}

	public TaggableActivity getActivity(WizardPageDefinition wizardPageDef) {
		return new WizardActivityImpl(wizardPageDef, this);
	}

	public String getContext(String ref) {
		String context = null;
		WizardReference reference = WizardReference.getReference(ref);
		if (reference != null) {
			if (WizardReference.REF_DEF.equals(reference.getType())) {
				context = wizardManager.getWizardPageDefinition(
						idManager.getId(reference.getId())).getSiteId();
			} else {
				context = matrixManager.getWizardPage(
						idManager.getId(reference.getId())).getPageDefinition()
						.getSiteId();
			}
		}
		return context;
	}

	public String getId() {
		return WizardActivityProducer.PRODUCER_ID;
	}

	public TaggableItem getItem(String itemRef, TaggingProvider provider, boolean getMyItemOnly, String taggedItem) {
		TaggableItem item = null;
		if (checkReference(itemRef)) {
			// Only return item to a specified rating (evalutation) provider
			if (ratingProviderIds.contains(provider.getId())) {
				WizardReference reference = WizardReference
						.getReference(itemRef);
				if (reference != null) {
					WizardPage page = matrixManager.getWizardPage(idManager
							.getId(reference.getId()));
					if (page != null
							&& (page.getStatus().equals(
									MatrixFunctionConstants.PENDING_STATUS) || page
									.getStatus()
									.equals(
											MatrixFunctionConstants.COMPLETE_STATUS))
							&& (page.getOwner().getId().getValue().equals(
									sessionManager.getCurrentSessionUserId()) || (!getMyItemOnly && canEvaluate(page)))) {
						item = getItem(page);
					}
				}
			} else {
				// Notify other tagging providers that they aren't accepted here
				// yet
				logger.warn(this + ".getItem(): Provider with id "
						+ provider.getId() + " not allowed!");
			}
		}
		return item;
	}

	public TaggableItem getItem(WizardPage wizardPage) {
		return new WizardItemImpl(wizardPage, getActivity(wizardPage
				.getPageDefinition()));
	}

	public List<TaggableItem> getItems(TaggableActivity activity,
			String userId, TaggingProvider provider, boolean getMyItemsOnly, String taggedItem) {
		List<TaggableItem> items = new ArrayList<TaggableItem>();
		// Return custom list of items to rating providers. This
		// list should match that seen in the evaluation item list (?)
		if (ratingProviderIds.contains(provider.getId())) {
			WizardPageDefinition def = (WizardPageDefinition) activity
					.getObject();
			for (Iterator<WizardPage> i = def.getPages().iterator(); i
					.hasNext();) {
				// Make sure this page is evaluatable by the current
				// user
				WizardPage page = i.next();
				if (page != null
						&& (page.getStatus().equals(
								MatrixFunctionConstants.PENDING_STATUS) || page
								.getStatus()
								.equals(MatrixFunctionConstants.COMPLETE_STATUS))
						&& (page.getOwner().getId().getValue().equals(userId) || (!getMyItemsOnly && canEvaluate(page)))) {
					items.add(getItem(page));
					// There is only one submitted page per definition, so break
					// here
					break;
				}
			}
		} else {
			// Notify other tagging providers that they aren't accepted here yet
			logger.warn(this + ".getItems() 2: Provider with id "
					+ provider.getId() + " not allowed!");
		}
		return items;
	}

	public List<TaggableItem> getItems(TaggableActivity activity,
			TaggingProvider provider, boolean getMyItemsOnly, String taggedItem) {
		List<TaggableItem> items = new ArrayList<TaggableItem>();
		// Only return items to a specified rating provider
		if (ratingProviderIds.contains(provider.getId())) {
			WizardPageDefinition def = (WizardPageDefinition) activity
					.getObject();
			for (Iterator<WizardPage> i = def.getPages().iterator(); i
					.hasNext();) {
				// Make sure this page is evaluatable by the current
				// user
				WizardPage page = i.next();
				if (page != null
						&& (page.getStatus().equals(
								MatrixFunctionConstants.PENDING_STATUS) || page
								.getStatus()
								.equals(MatrixFunctionConstants.COMPLETE_STATUS))
						&& (!getMyItemsOnly && canEvaluate(page))) {
					items.add(getItem(page));
				}
			}
		} else {
			// Notify other tagging providers that they aren't accepted here yet
			logger.warn(this + ".getItems(): Provider with id "
					+ provider.getId() + " not allowed!");
		}
		return items;
	}
	
	public boolean hasSubmissions(TaggableActivity activity,
			TaggingProvider provider, boolean getMyItemsOnly, String taggedItem) {
		List<TaggableItem> items = getItems(activity, provider, getMyItemsOnly, taggedItem);
		return items.size() > 0;
	}
	
	public boolean hasSubmissions(TaggableActivity activity, String userId,
			TaggingProvider provider, boolean getMyItemsOnly, String taggedItem) {
		List<TaggableItem> items = getItems(activity, userId, provider, getMyItemsOnly, taggedItem);
		return items.size() > 0;
	}
	
	public String getItemPermissionOverride() {
		//TODO figure out what perm to use here
		return null;
	}

	public String getName() {
		return messages.getString("service_name");
	}

	public List<String> getRatingProviderIds() {
		return ratingProviderIds;
	}

	public void init() {
		logger.info("init()");

		taggingManager.registerProducer(this);
	}

	public void setAuthzManager(AuthorizationFacade authzManager) {
		this.authzManager = authzManager;
	}

	public void setIdManager(IdManager idManager) {
		this.idManager = idManager;
	}

	public void setMatrixManager(MatrixManager matrixManager) {
		this.matrixManager = matrixManager;
	}

	public void setRatingProviderIds(List<String> ratingProviderIds) {
		this.ratingProviderIds = ratingProviderIds;
	}

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public void setTaggingManager(TaggingManager taggingManager) {
		this.taggingManager = taggingManager;
	}

	public void setWizardManager(WizardManager wizardManager) {
		this.wizardManager = wizardManager;
	}

	public AuthenticationManager getAuthnManager() {
		return authnManager;
	}

	public void setAuthnManager(AuthenticationManager authnManager) {
		this.authnManager = authnManager;
	}
}
