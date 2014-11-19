/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.2/wizard/api/src/java/org/theospi/portfolio/wizard/mgt/WizardManager.java $
 * $Id: WizardManager.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.theospi.portfolio.wizard.mgt;

import java.util.Collection;
import java.util.List;

import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.exception.ImportException;
import org.sakaiproject.exception.UnsupportedFileTypeException;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.shared.mgt.WorkflowEnabledManager;
import org.theospi.portfolio.wizard.model.CompletedWizard;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.model.WizardPageSequence;

public interface WizardManager extends WorkflowEnabledManager {

	public static final int WIZARD_NO_CHECK = 0;

	public static final int WIZARD_OPERATE_CHECK = 10;

	public static final int WIZARD_VIEW_CHECK = 20;

	public static final int WIZARD_EDIT_CHECK = 30;

	public static final int WIZARD_EXPORT_CHECK = 40;

	public static final int WIZARD_DELETE_CHECK = 50;

	public static final String WIZARD_PARAM_ID = "wizardId";

	public static final String EXPOSED_WIZARD_KEY = "osp.exposedwizard.wizard.id";

	/**
	 * Checks if the current user is authorized to review all the types of
	 * Reviews. If the user is the owner or the user is authorized then the
	 * Reviews are read in and pushed into the security advisor.
	 * 
	 * @param id
	 *            Id of the wizard to check
	 */
	public void checkWizardAccess(Id id);

	/**
	 * creates a new Wizard in the current site owned by the current user
	 * 
	 * @return Wizard
	 */
	public Wizard createNew();

	public Reference decorateReference(Wizard wizard, String reference);

	public void deleteObjects(List deletedItems);

	/**
	 * Removes a wizard from storage
	 * 
	 * @param wizard
	 */
	public void deleteWizard(Wizard wizard);

   /**
    * Unlock resources and delete completed wizards from a preview wizard
    */
   public void deletePreviewWizardData( Wizard wizard );
   
	/**
	 * 
	 * @param sites
	 *            A list of site Ids (Strings)
	 * @return
	 */
	public List findPublishedWizards(List<String> sites);

	public List findPublishedWizards(String siteId);
   
   public List findPublishedWizards(List<String> sites, boolean lazy);
   
   /**
    ** Find all WizardPageSequence objects associated with this wizard
    ** (e.g. useful for unlocking associated resources prior 
    ** to deleting wizard).
    **/
   public List findPagesByWizard(Id wizardId);
   
	/**
	 * Method to get each wizard page definition for the site identified by the
	 * given site identifier. The wizard page definitions are not loaded with
	 * their wizard pages.
	 * 
	 * @param siteId
	 *            The identifier of the site.
	 * @return A list of wizard page definitions for this site.
	 */
	public List<WizardPageDefinition> findWizardPageDefs(final String siteId);

	/**
	 * Method to get each wizard page definition for the site identified by the
	 * given site identifier, optionally loading each with it's wizard pages.
	 * 
	 * @param siteId
	 *            The identifier of the site.
	 * @param deep
	 *            True if each wizard page definition should be loaded with it's
	 *            wizard pages.
	 * @return A list of wizard page definitions for this site.
	 */
	public List<WizardPageDefinition> findWizardPageDefs(final String siteId,
			final boolean deep);

	public List findWizardsByOwner(String ownerId, String siteId);

	public Collection getAvailableForms(String siteId, String type, String currentUserId);

	public CompletedWizard getCompletedWizard(Id completedWizardId);

	public CompletedWizard getCompletedWizard(Wizard wizard);

	public CompletedWizard getCompletedWizard(Wizard wizard, String userId);

	public CompletedWizard getCompletedWizard(Wizard wizard, String userId,
			boolean create);

	public CompletedWizard getCompletedWizardByPage(Id pageId);

	public List getCompletedWizardPagesByPageDef(Id id);

	public List getCompletedWizardsByWizardId(String wizardId);

	/**
	 * Given a user's completed wizard this takes a look at the number of
	 * submitted pages (not in the READY state)
	 * 
	 * @param wizard
	 *            CompletedWizard to tally the number of submitted pages
	 * @return int
	 */
	public int getSubmittedPageCount(CompletedWizard wizard);

	/**
	 * Gets the total number of pages for the given wizard
	 * 
	 * @param wizard
	 *            Wizard to tally the number of pages
	 * @return int
	 */
	public int getTotalPageCount(Wizard wizard);

	/**
	 * Gets a wizard given its id. This performs a check on the operate
	 * permission
	 * 
	 * @param Id
	 *            wizardId
	 * @return Wizard
	 */
	public Wizard getWizard(Id wizardId);

	/**
	 * gets a wizard given its id. it may perform a check on the view permission
	 * if the checkAuthz is true
	 * 
	 * @param Id
	 *            wizardId
	 * @param boolean
	 *            checkAuthz
	 * @return
	 */
	public Wizard getWizard(Id wizardId, int checkAuthz);

	/**
	 * Gets a wizard given its id. This performs a check on the view permission
	 * 
	 * @param String
	 *            wizardId
	 * @return Wizard
	 */
	public Wizard getWizard(String id);

	/**
	 * gets a wizard given its id. it performs a check on the permission
	 * specified in checkAuthz: WIZARD_NO_CHECK, WIZARD_OPERATE_CHECK,
	 * WIZARD_VIEW_CHECK WIZARD_EDIT_CHECK, WIZARD_EXPORT_CHECK,
	 * WIZARD_DELETE_CHECK
	 * 
	 * @param String
	 *            wizardId
	 * @param boolean
	 *            checkAuthz
	 * @return
	 */
	public Wizard getWizard(String id, int checkAuthz);

	public String getWizardEntityProducer();

	/**
	 * This is the light weight method of getting the owner of a wizard given
	 * its id.
	 * 
	 * @param Id
	 *            wizardId
	 * @return Agent of the owner id
	 */
	public Agent getWizardIdOwner(final Id wizardId);

	/**
	 * This is the light weight method of getting the site id of a wizard given
	 * its id.
	 * 
	 * @param Id
	 *            wizardId
	 * @return Id of the site
	 */
	public String getWizardIdSiteId(final Id wizardId);

	/**
	 * Method to get the wizard page definition identified by the given
	 * identifier object. The wizard page definition is not loaded with it's
	 * wizard pages.
	 * 
	 * @param id
	 *            The identifier object that uniquely identifies the wizard page
	 *            definition.
	 * @return The wizard page definition.
	 */
	public WizardPageDefinition getWizardPageDefinition(Id id);

	/**
	 * Method to get the wizard page definition identified by the given
	 * identifier object, optionally loading it with it's wizard pages.
	 * 
	 * @param id
	 *            The identifier object that uniquely identifies the wizard page
	 *            definition.
	 * @param deep
	 *            True if the wizard page definition should be loaded with it's
	 *            wizard pages.
	 * @return The wizard page definition.
	 */
	public WizardPageDefinition getWizardPageDefinition(Id id, boolean deep);

	public WizardPageSequence getWizardPageSeqByDef(Id id);

	/**
	 * Pulls all wizards, deeping loading all parts of each Wizard
	 * 
	 * @return List of Wizard
	 */
	public List getWizardsForWarehousing();

	public Wizard importWizardResource(Id worksite, String reference)
			throws UnsupportedFileTypeException, ImportException;

	public List listAllWizardsByOwner(String owner, String siteIdStr);

	public List listWizardsByType(String owner, String siteIdStr, String type);
	
	/**
    * changes the settings on the wizard to make it available for preview
    * to the users the site
    * 
    * @param wizard
    */
   public void previewWizard(Wizard wizard);


	/**
	 * changes the settings on the wizard to make it available to the users of
	 * the site
	 * 
	 * @param wizard
	 */
	public void publishWizard(Wizard wizard);

	/**
	 * Saves a completed wizard into storage
	 * 
	 * @param wizard
	 *            CompletedWizard
	 * @return CompletedWizard
	 */
	public CompletedWizard saveWizard(CompletedWizard wizard);

	/**
	 * Saves a Wizard to storage. It returns an updated wizard
	 * 
	 * @param wizard
	 * @return Wizard
	 */
	public Wizard saveWizard(Wizard wizard);

   /**
    * get all the cells, pages, and wizards that this user can evaluate within specified worksite(s)
    * @param agent Agent 
    * @param worksiteIds List of worksite Ids
    * @return List of org.theospi.portfolio.shared.model.EvaluationContentWrapper
    */
   List getEvaluatableItems(Agent agent, List<String> worksiteIds);

   /**
    * get all the cells, pages, and wizards that this user can evaluate within all worksites they are a member of
    * @param agent Agent 
    * @return List of org.theospi.portfolio.shared.model.EvaluationContentWrapper
    */
   List getEvaluatableItems(Agent agent);

}
