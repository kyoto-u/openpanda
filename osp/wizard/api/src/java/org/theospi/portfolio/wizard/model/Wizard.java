/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.0/wizard/api/src/java/org/theospi/portfolio/wizard/model/Wizard.java $
* $Id: Wizard.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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
package org.theospi.portfolio.wizard.model;

import java.util.Date;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.shared.model.ObjectWithWorkflow;
import org.theospi.portfolio.style.model.Style;
import org.theospi.portfolio.wizard.WizardFunctionConstants;
import org.theospi.portfolio.shared.model.WizardMatrixConstants;

/**
 * The super class has the evaluation, reflection and review
 * 
 */

public class Wizard extends ObjectWithWorkflow {

   public final static String ROOT_TITLE = "root";
	
   private String name;
   private String description;
   private String keywords;
   private Date created;
   private Date modified;
   private transient Agent owner;
   private Id guidanceId;
   private boolean published = false;
   private boolean preview = false;
   private String type = WizardFunctionConstants.WIZARD_TYPE_SEQUENTIAL;
   private String exposedPageId;
   private transient Boolean exposeAsTool = null;
   
   private String siteId;
   private WizardCategory rootCategory;
   private int sequence = 0;
   private Style style;
   private transient Id styleId;

   private transient Guidance guidance;
   
   private boolean newObject = false;
   
   private int generalFeedbackOption = WizardMatrixConstants.FEEDBACK_OPTION_OPEN;
   private int itemFeedbackOption = WizardMatrixConstants.FEEDBACK_OPTION_OPEN;
	
   private int reviewerGroupAccess = WizardMatrixConstants.NORMAL_GROUP_ACCESS;
	
   public Wizard() {
   }

   public Wizard(Id id, Agent owner, String siteId) {
      setId(id);
      this.owner = owner;
      this.siteId = siteId;
      newObject = true;
      rootCategory = new WizardCategory(this);
      rootCategory.setTitle(ROOT_TITLE);
   }
   
   public String getType() {
      return type;
   }
   public void setType(String type) {
      this.type = type;
   }
   
   public Date getCreated() {
      return created;
   }
   public void setCreated(Date created) {
      this.created = created;
   }
   public String getDescription() {
      return description;
   }
   public void setDescription(String description) {
      this.description = description;
   }

   public Date getModified() {
      return modified;
   }
   public void setModified(Date modified) {
      this.modified = modified;
   }
   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   public Agent getOwner() {
      return owner;
   }
   public void setOwner(Agent owner) {
      this.owner = owner;
   }
   public boolean isNewObject() {
      return newObject;
   }
   public void setNewObject(boolean newObject) {
      this.newObject = newObject;
   }
   public String getSiteId() {
      return siteId;
   }
   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }
   public String getKeywords() {
      return keywords;
   }
   public void setKeywords(String keywords) {
      this.keywords = keywords;
   }
   public Guidance getGuidance() {
      return guidance;
   }
   public void setGuidance(Guidance guidance) {
      this.guidance = guidance;
   }

   public Id getGuidanceId() {
      return guidanceId;
   }

   public void setGuidanceId(Id guidanceId) {
      this.guidanceId = guidanceId;
   }

   public boolean isPreview() {
      return preview;
   }
   
   public void setPreview(boolean preview) {
      this.preview = preview;
   }

   public boolean isPublished() {
      return published;
   }

   public void setPublished(boolean published) {
      this.published = published;
   }

   public Boolean getExposeAsTool() {
      return exposeAsTool;
   }

   public void setExposeAsTool(Boolean exposeAsTool) {
      this.exposeAsTool = exposeAsTool;
   }

   public String getExposedPageId() {
      return exposedPageId;
   }

   public void setExposedPageId(String exposedPageId) {
      this.exposedPageId = exposedPageId;
   }

   public WizardCategory getRootCategory() {
      return rootCategory;
   }

   public void setRootCategory(WizardCategory rootCategory) {
      this.rootCategory = rootCategory;
   }

   public int getSequence() {
      return sequence;
   }

   public void setSequence(int sequence) {
      this.sequence = sequence;
   }

   public Style getStyle() {
      return style;
   }

   public void setStyle(Style style) {
      this.style = style;
   }

   public Id getStyleId() {
      return styleId;
   }

   public void setStyleId(Id styleId) {
      this.styleId = styleId;
   }

   public String getGeneralFeedbackOptionString() {
	   return String.valueOf(generalFeedbackOption);
   }
   public void setGeneralFeedbackOptionString(String feedbackOption) {
	   this.generalFeedbackOption = Integer.valueOf(feedbackOption).intValue();
   }
   public int getGeneralFeedbackOption() {
	   return generalFeedbackOption;
   }
   public void setGeneralFeedbackOption(int feedbackOption) {
	   this.generalFeedbackOption = feedbackOption;
   }

   public boolean isGeneralFeedbackOpen() {
	   return generalFeedbackOption == WizardMatrixConstants.FEEDBACK_OPTION_OPEN;
   }
   public boolean isGeneralFeedbackSingle() {
	   return generalFeedbackOption == WizardMatrixConstants.FEEDBACK_OPTION_SINGLE;
   }
   public boolean isGeneralFeedbackNone() {
	   return generalFeedbackOption == WizardMatrixConstants.FEEDBACK_OPTION_NONE;
   }
   
   public String getReviewerGroupAccessString() {
      return String.valueOf(reviewerGroupAccess);
   }
   public void setReviewerGroupAccessString(String reviewerGroupAccess) {
      this.reviewerGroupAccess = Integer.valueOf(reviewerGroupAccess).intValue();
   }
   public int getReviewerGroupAccess() {
      return reviewerGroupAccess;
   }
   public void setReviewerGroupAccess(int reviewerGroupAccess) {
      this.reviewerGroupAccess = reviewerGroupAccess;
   }

   public String getItemFeedbackOptionString() {
	   return String.valueOf(itemFeedbackOption);
   }
   public void setItemFeedbackOptionString(String feedbackOption) {
	   this.itemFeedbackOption = Integer.valueOf(feedbackOption).intValue();
   }
   public int getItemFeedbackOption() {
	   return itemFeedbackOption;
   }
   public void setItemFeedbackOption(int feedbackOption) {
	   this.itemFeedbackOption = feedbackOption;
   }

   public boolean isItemFeedbackOpen() {
	   return itemFeedbackOption == WizardMatrixConstants.FEEDBACK_OPTION_OPEN;
   }
   public boolean isItemFeedbackSingle() {
	   return itemFeedbackOption == WizardMatrixConstants.FEEDBACK_OPTION_SINGLE;
   }
   public boolean isItemFeedbackNone() {
	   return itemFeedbackOption == WizardMatrixConstants.FEEDBACK_OPTION_NONE;
   }
   
}
