/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.3/matrix/api/src/java/org/theospi/portfolio/matrix/MatrixManager.java $
* $Id: MatrixManager.java 98423 2011-09-20 15:52:28Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
package org.theospi.portfolio.matrix;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.taggable.api.TaggableActivity;
import org.sakaiproject.taggable.api.TaggableItem;
import org.sakaiproject.taggable.api.TaggingManager;
import org.theospi.portfolio.matrix.model.Attachment;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.Level;
import org.theospi.portfolio.matrix.model.Matrix;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.shared.mgt.WorkflowEnabledManager;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.model.ObjectWithWorkflow;
import org.theospi.portfolio.tagging.api.DecoratedTaggableItem;
import org.theospi.portfolio.tagging.api.DecoratedTaggingProvider;

/**
 * @author apple
 */
public interface MatrixManager extends WorkflowEnabledManager {

   public static final String EXPOSED_MATRIX_KEY = "osp.exposedmatrix.scaffolding.id";
   public static final String CONFIRM_PUBLISHED_FLAG = "published";
   public static final String CONFIRM_EVAL_VIEW_ALL_GROUPS_FLAG = "warnViewAllGroupsEval";
   
   Matrix getMatrix(Id scaffoldingId, Id agentId);
   List getCellsByScaffoldingCell(Id scaffoldingCellId);
   List getPagesByPageDef(Id pageDefId);

   Cell getCell(Matrix matrix, Criterion rootCriterion, Level level);

   void unlockNextCell(Cell cell);

   Criterion getCriterion(Id criterionId);
   Level getLevel(Id levelId);

   Cell getCell(Id cellId);

   Cell getCellFromPage(Id pageId);

   List getCells(Matrix matrix);

   Id storeCell(Cell cell);

   Id storePage(WizardPage page);

   Scaffolding storeScaffolding(Scaffolding scaffolding);
   Scaffolding saveNewScaffolding(Scaffolding scaffolding);
   
   Id storeScaffoldingCell(ScaffoldingCell scaffoldingCell);
   
   void publishScaffolding(Id scaffoldingId);
	
   void previewScaffolding(Id scaffoldingId);

   Object store(Object obj);
   Object save(Object obj);
   
   Matrix createMatrix(Agent owner, Scaffolding scaffolding);

   Attachment getAttachment(Id attachmentId);
   
   Attachment attachArtifact(Id pageId, Reference artifactId);

   void detachArtifact(final Id pageId, final Id artifactId);
   void detachForm(final Id pageId, final Id artifactId);
   
   void removeFromSession(Object obj);
   void clearSession();

   Matrix getMatrix(Id matrixId);

   public List getMatricesForWarehousing();

   Scaffolding getScaffolding(Id scaffoldingId);
   
   Scaffolding loadScaffolding(Id scaffoldingId);
   
   /**
    * 
    * @param siteIdStr
    * @param user
    * @param showUnpublished
    * @return
    */
   public List findAvailableScaffolding(String siteIdStr, Agent user, boolean showUnpublished);
   /**
    * 
    * @param sites
    * @param user
    * @param showUnpublished
    * @return
    */
   public List findAvailableScaffolding(List sites, Agent user, boolean showUnpublished);
   
   List<Scaffolding> findPublishedScaffolding(String siteId);
   
   List findPublishedScaffolding(List sites);
   
   ScaffoldingCell getNextScaffoldingCell(ScaffoldingCell scaffoldingCell, 
         int progressionOption);
   ScaffoldingCell getScaffoldingCell(Criterion criterion, Level level);
   ScaffoldingCell getScaffoldingCell(Id id);
   
   /**
    * Get all scaffolding cells for a given scaffolding
    * @param scaffoldingId
    * @return
    */
   public Set<ScaffoldingCell> getScaffoldingCells(Id scaffoldingId);
   
   ScaffoldingCell getScaffoldingCellByWizardPageDef(Id id);
   String getScaffoldingCellsStatus(Id id);

   Set getPageContents(WizardPage page);
   Set getPageForms(WizardPage page);
   //List getPageArtifacts(WizardPage page);
   List getCellsByArtifact(Id artifactId);
   List getCellsByForm(Id artifactId);

   Cell submitCellForEvaluation(Cell cell);

   WizardPage submitPageForEvaluation(WizardPage page);

   List getEvaluatableCells(Agent agent, List<Agent> roles, List<String> worksiteIds, Map siteHash);

   /**
    * @param matrixId
    */
   void deleteMatrix(Id matrixId);
   
   void deleteScaffolding(Id scaffoldingId);
   public void exposeMatrixTool(Scaffolding scaffolding);
   public void removeExposedMatrixTool(Scaffolding scaffolding);
   
   void packageScffoldingForExport(Id scaffoldingId, OutputStream os) throws IOException;

   Node getNode(Id artifactId);

   Node getNode(Reference ref);

   Node getNode(Id artifactId, boolean checkLocks);

   Node getNode(Reference ref, boolean checkLocks);

   Scaffolding uploadScaffolding(Reference uploadedScaffoldingFile,
                                 String toContext) throws IOException;

   //Scaffolding uploadScaffolding(String toContext, ZipInputStream zis) throws IOException;
   
   void checkPageAccess(String id);
   
   Scaffolding createDefaultScaffolding();

   public List getScaffolding();
   public List getScaffoldingForWarehousing();

   public List getMatrices(Id scaffoldingId);
   public List getMatrices(Id scaffoldingId, Id agentId);

   WizardPage getWizardPage(Id pageId);
   WizardPage getWizardPageByPageDefAndOwner(Id pageId, Agent owner);
   
   List getWizardPagesForWarehousing();
   
   Matrix getMatrixByPage(Id pageId);

   public boolean isUseExperimentalMatrix();
   
   public boolean isEnableDafaultMatrixOptions();
   
   List<WizardPageDefinition> getWizardPageDefs(List<Id> ids);
   List<ScaffoldingCell> getScaffoldingCells(List<Id> ids);
   
   public WizardPageDefinition getWizardPageDefinition(Id pageDefId);
	/**
	 * finds the list of evaluators/roles of the site id passed and checks against the current user.
	 * returns true if user or role matches, otherwise false
	 * 
	 * @param id
	 * @param worksiteId
	 * @param function
	 * @return
	 */
	public boolean hasPermission(Id id, Id worksiteId, String function);
	
	public WizardPageDefinitionEntity createWizardPageDefinitionEntity(WizardPageDefinition wpd, String parentTitle);
	
	/**
	 * returns the count of forms that are associated with the pageDefId
	 * @param pageDefId
	 * @return
	 */
	public int getFormCountByPageDef(Id pageDefId);
	
	/**
	 * returns the count of set of all reviews associated with the pageDefId
	 * @param pageDefId
	 * @return
	 */
	public int getReviewCountByPageDef(Id pageDefId);
	
	
	/**
	 * returns a Map of types and counts.  Each  
	 * has the type of review it is (eval, feedback, ect..) and how many reviews of that
	 * type are associated with that pageDefId
	 * 
	 * @param pageDefId
	 * @return
	 */
	public Map<Integer, Integer> getReviewCountListByType(Id pageDefId);
		
	/**
	 * returns the count of all attachments associated with the pageDefId
	 * @param pageDefId
	 * @return
	 */
	public int getAttachmentCountByPageDef(Id pageDefId);
	
	/**
	 * returns true if the scaffolding cell is being used by any users (forms, attachments, reviews, ect..)
	 * @param cell
	 * @return
	 */	
	public boolean isScaffoldingCellUsed(ScaffoldingCell cell);
	
	/**
	 * returns true if the scaffolding is being used anywhere by any user (forms, attachments, reviews, ect..)
	 * @param scaffolding
	 * @return
	 */	
	public boolean isScaffoldingUsed(Scaffolding scaffolding);
	
	/**
	 * Get a count of submitted cells per scaffolding cell for a list of scaffolding
	 * @param scaffolding
	 * @return
	 */
	public Map<Id, Integer> getSubmissionCountByScaffolding(List<Scaffolding> scaffolding);
	
	/**
	 * Returns the set of users that are present in the groups that have been passed
	 * @param worksiteId
	 * @param filterGroupId
	 * @param allowAllGroups
	 * @param groups
	 * @return
	 */
	public Set getUserList(String worksiteId, String filterGroupId, boolean allowAllGroups, List<Group> groups);
	
	/**
	 * Returns the set of groups the current user has access to
	 * 
	 * If allowAllGroups flag is true, the all groups will be returned
	 * 
	 * @param worksiteId
	 * @param allowAllGroups
	 * @return
	 */
	public Set getGroupList(String worksiteId, boolean allowAllGroups);
	
	/**
	 * 
	 * @param worksiteId
	 * @return
	 */
	public boolean hasGroups(String worksiteId);

	/**	 
	 * Returns the set of groups the current user has access to
	 * 
	 * If allowAllGroups flag is true, the all groups will be returned
	 * 
	 * @param site
	 * @param allowAllGroups
	 * @return
	 */
	public Set getGroupList(Site site, boolean allowAllGroups);
	
	/**
	 * Returns a list of users display names (string) who have access
	 * to the function and Object combination
	 * 
	 * @param oWW
	 * @param function
	 * @return
	 */
	public List getSelectedUsers(ObjectWithWorkflow oWW, String function);
	
 	/**
  	 * 
  	 * Will notify an audience who has permission to perform the passed function on the passed objectId.  Each will be emailed specifically
 	 * based on the function.  Any email address listed in sentEmailAddrs be appended to the email list that goes out.  
  	 * 
  	 * If group aware and the site has groups, then the audience is selected by:
  	 * Must be able to perform the function on the objectId and be in the current users group or have the viewAllGroups permission if its a matrix
  	 * 
  	 * If not group aware or the site doesn't have groups then the audience is selected by the function and objectId authorization. 
  	 * 
 	 * If null is passed for function or reviewObjectId, then it will send emails to only the sendExtraEmails list.
  	 * 
  	 * @param wizPage
  	 * @param reviewObjectId
  	 * @param groupAware
 	 * @param sendExtraEmails
  	 * @param parentTitle
  	 * @param function
  	 */
 	public void notifyAudience(WizardPage wizPage, Id reviewObjectId, boolean groupAware, HashMap<String, String> sendExtraEmails, String emailMessage, String parentTitle, String function);	

	public Cell createCellWrapper(WizardPage page);
	
	
	public Set<TaggableItem> getTaggableItems(TaggableItem item, String criteriaRef, String cellOwner);
	
	public Set<DecoratedTaggableItem> getDecoratedTaggableItems(TaggableItem item, String criteriaRef, String cellOwner);
	

	public TaggingManager getTaggingManager();
	
	public List<DecoratedTaggingProvider> getDecoratedProviders(TaggableActivity activity);
	
	/**
	 * Will return true if the current user can access all cells based off the ScaffoldingId
	 * 
	 * This looks for top level permissions only
	 * 
	 * @param scaffoldingId
	 * @return
	 */
	public boolean canAccessAllMatrixCells(Id scaffoldingId);	
	
	/**
	 * 
	 * This will return true if the current user can access this scaffolding cell.  
	 * This is based off canAccessAllMatrixCells and if the user has cell specific
	 * permissions (ie. eval, review, ect)
	 * 
	 * Pass the scaffolding cell Id
	 * 
	 * @param scaffoldingCellId
	 * @return
	 */
	public boolean canAccessScaffoldCellByScaffoldingCellId(Id scaffoldingCellId);
	
	/**
	 * 
	 * This will return true if the current user can access this scaffolding cell.  
	 * This is based off canAccessAllMatrixCells and if the user has cell specific
	 * permissions (ie. eval, review, ect)
	 * 
	 * Pass the wizard page definition Id
	 * 
	 * @param wizPageDefId
	 * @return
	 */
	public boolean canAccessScaffoldCellByWizPageDefId(Id wizPageDefId);
	
	/**
	 * 
	 * This checks if the current user has permission for a single users
	 * cell specifically.  This will look at canAccessScaffoldCell and 
	 * canAccessAllMatrixCells as well as if the user has any cell specific
	 * permissions (ie. Owner selected current users as reviewer, is cell owner, ect)
	 * 
	 * 
	 * @param cell
	 * @return
	 */
	public boolean canAccessMatrixCell(Cell cell);
	
	/**
	 * returns true if the user can access that page and that wiz page has been linked to that linkedArtifactId
	 * 
	 * @param siteId
	 * @param pageId
	 * @param linkedArtifactId
	 * @return
	 */
	public boolean canUserAccessWizardPageAndLinkedArtifcact(String siteId, String pageId, String linkedArtifactId);
	
	/**
	 * Get the user's notification option for this... one of the NotificationService's PREF_
	 * settings.
	 * If the user has no prefs set, the default of NotificationService.PREF_IMMEDIATE will be used.
	 * 
	 * @param userId
	 * @param notificationId
	 * @param siteId
	 * @return
	 */
	public int getNotificationOption(String userId, String notificationId, String siteId);

	/**
	 * returns a map of flags for saving confirmation.  This is used to warn the user of any issues that may be present
	 * by the scaffolding being saved.
	 * 
	 * @param scaffolding
	 * @return
	 */
	public Map getConfirmFlagsForScaffolding(Scaffolding scaffolding);
	
	/**
     * returns a map of flags for saving confirmation.  This is used to warn the user of any issues that may be present
	 * by the scaffoldingCell being saved. 
	 * @param scaffoldingCell
	 * @return
	 */
	public Map getConfirmFlagsForScaffoldingCell(ScaffoldingCell scaffoldingCell);

	/**
	 * Returns a collection of Forms for a specific type: ie. WizardFunctionConstants.EVALUATION_TYPE
	 * 
	 * @param wizards
	 * @param siteId
	 * @param deviceId
	 * @param type
	 * @param currentUserId
	 * @return
	 */
	public Collection getTypeDevices(List wizards, String siteId, Id deviceId, String type, String currentUserId);
	
	/**
	 * 
	 * @param type
	 * @param currentSiteId
	 * @param currentUserId
	 * @return
	 */
	public Collection getFormsForSelect(String type, String currentSiteId, String currentUserId);
	
	/**
	 * 
	 * @param additionalForms
	 * @param siteId
	 * @param currentUserId
	 * @return
	 */
	public Collection getSelectedAdditionalFormDevices(Collection additionalForms, String siteId, String currentUserId);
}
