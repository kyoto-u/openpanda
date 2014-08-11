/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/api/src/java/org/theospi/portfolio/matrix/model/WizardPageDefinition.java $
* $Id:WizardPageDefinition.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.matrix.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.shared.model.ObjectWithWorkflow;
import org.theospi.portfolio.style.model.Style;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 11, 2006
 * Time: 4:14:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class WizardPageDefinition extends ObjectWithWorkflow {

   private String title;
   private String description;
   private String initialStatus = "";
   private boolean suppressItems = false;
   private Collection evaluators = new HashSet();
   private Collection reviewers = new HashSet();
   transient private boolean validate;
   private Set pages = new HashSet();
   transient private Id guidanceId;
   private Guidance guidance;
   transient private Id deleteGuidanceId;
   
   //default it to matrix
   private String type = WPD_MATRIX_TYPE;
   
   private boolean defaultCustomForm = true;
   private boolean defaultReflectionForm = true;
   private boolean defaultFeedbackForm = true;
   private boolean defaultReviewers = true;
   private boolean defaultEvaluationForm = true;
   private boolean defaultEvaluators = true;
   
   private boolean allowRequestFeedback = true;
   private boolean hideEvaluations = false;
   
   private String siteId;
   private Style style;
   
   transient private Id styleId;

   private List additionalForms = new ArrayList();
	
	private List<String> attachments = new ArrayList();
	
	public static String ATTACHMENT_ASSIGNMENT = "assignment";
	
	public static String WPD_ENTITY_STRING = "ospWizPageDef";
	
	public static String WPD_MATRIX_TYPE = "0";
	public static String WPD_WIZARD_HIER_TYPE = "1";
	public static String WPD_WIZARD_SEQ_TYPE = "2";

	public WizardPageDefinition() {
	}
	
	public WizardPageDefinition(String type) {
		this.type = type;
	}
	
	public WizardPageDefinition(String type, boolean defaultCustomForm, 
			boolean defaultReflectionForm, boolean defaultFeedbackForm, 
			boolean defaultReviewers, boolean defaultEvaluationForm, 
			boolean defaultEvaluators, boolean allowRequestFeedback) {
		this.type = type;
		this.defaultCustomForm = defaultCustomForm;
		this.defaultReflectionForm = defaultReflectionForm;
		this.defaultFeedbackForm = defaultFeedbackForm;
		this.defaultReviewers = defaultReviewers;
		this.defaultEvaluationForm = defaultEvaluationForm;
		this.defaultEvaluators = defaultEvaluators;
		this.allowRequestFeedback = allowRequestFeedback;
	}
	
   /**
    * @return Returns the initialStatus.
    */
   public String getInitialStatus() {
      return initialStatus.toUpperCase();
   }
   /**
    * @param initialStatus The initialStatus to set.
    */
   public void setInitialStatus(String initialStatus) {
      this.initialStatus = initialStatus.toUpperCase();
   }

   /**
    * @return Returns the evaluators.
    */
   public Collection getEvaluators() {
      return evaluators;
   }
   /**
    * @param reviewers The evaluators to set.
    */
   public void setEvaluators(Collection evaluators) {
      this.evaluators = evaluators;
   }
   /**
    * @return Returns the validate.
    */
   public boolean isValidate() {
      return validate;
   }
   /**
    * @param validate The validate to set.
    */
   public void setValidate(boolean validate) {
      this.validate = validate;
   }
   public Set getPages() {
      return pages;
   }
   public void setPages(Set pages) {
      this.pages = pages;
   }

   /**
    * This is the transient property.
    * @return Returns the guidanceId.
    */
   public Id getGuidanceId() {
      return guidanceId;
   }
   /**
    * This is the transient property.  This will not save to the database.
    * Use setGuidance to save the guidance to the database.
    * @param guidanceId The guidanceId to set.
    */
   public void setGuidanceId(Id guidanceId) {
      this.guidanceId = guidanceId;
   }
   /**
    * @return Returns the guidance.
    */
   public Guidance getGuidance() {
      return guidance;
   }
   /**
    * @param guidance The guidance to set.
    */
   public void setGuidance(Guidance guidance) {
      this.guidance = guidance;
   }

   /**
    * @return Returns the deleteGuidanceId.
    */
   public Id getDeleteGuidanceId() {
      return deleteGuidanceId;
   }
   /**
    * @param deleteGuidanceId The deleteGuidanceId to set.
    */
   public void setDeleteGuidanceId(Id deleteGuidanceId) {
      this.deleteGuidanceId = deleteGuidanceId;
   }
   /**
    * List of Strings of the form Ids
    * @return Returns the additionalForms.
    */
   public List getAdditionalForms() {
      return additionalForms;
   }
   /**
    * @param additionalForms The additionalForms to set.
    */
   public void setAdditionalForms(List additionalForms) {
      this.additionalForms = additionalForms;
   }

   /**
    * List of WizardPageDefAttachments
    * @return Returns the attachments list.
    */
   public List<String> getAttachments() {
      return attachments;
   }
   /**
    * @param additionalForms The attachments to set.
    */
   public void setAttachments(List<String> attachments) {
      this.attachments = attachments;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getSiteId() {
      return siteId;
   }

   public void setSiteId(String siteId) {
      this.siteId = siteId;
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
   /**
    * @return the suppressItems
    */
   public boolean isSuppressItems() {
	   return suppressItems;
   }
   /**
    * @param suppressItems the suppressItems to set
    */
   public void setSuppressItems(boolean suppressItems) {
	   this.suppressItems = suppressItems;
   }
   
   public String getReference() {
	   StringBuffer sb = new StringBuffer(Entity.SEPARATOR);
	   	sb.append(WPD_ENTITY_STRING);
		sb.append(Entity.SEPARATOR);
		sb.append(getContext());
		sb.append(Entity.SEPARATOR);
		sb.append(getId());
		sb.append(Entity.SEPARATOR);
		sb.append(getType());
		return sb.toString();
   }


   public String getContext()
   {
	   return getSiteId();
   }
   public boolean isPublished()
   {
	   //TODO how to do this for real?
	   return true;
   }

   public String getParentTitle()
   {
	   // TODO Auto-generated method stub
	   return null;
   }
   
   public boolean isDefaultCustomForm() {
	   return defaultCustomForm;
   }
   public void setDefaultCustomForm(boolean defaultCustomForm) {
	   this.defaultCustomForm = defaultCustomForm;
   }
   public boolean isDefaultReflectionForm() {
	   return defaultReflectionForm;
   }
   public void setDefaultReflectionForm(boolean defaultReflectionForm) {
	   this.defaultReflectionForm = defaultReflectionForm;
   }
   public boolean isDefaultFeedbackForm() {
	   return defaultFeedbackForm;
   }
   public void setDefaultFeedbackForm(boolean defaultFeedbackForm) {
	   this.defaultFeedbackForm = defaultFeedbackForm;
   }
   
   public boolean isDefaultReviewers() {
	   return defaultReviewers;
   }
   public void setDefaultReviewers(boolean defaultReviewers) {
	   this.defaultReviewers = defaultReviewers;
   }
   public boolean isDefaultEvaluationForm() {
	   return defaultEvaluationForm;
   }
   public void setDefaultEvaluationForm(boolean defaultEvaluationForm) {
	   this.defaultEvaluationForm = defaultEvaluationForm;
   }
   public boolean isDefaultEvaluators() {
	   return defaultEvaluators;
   }
   public void setDefaultEvaluators(boolean defaultEvaluators) {
	   this.defaultEvaluators = defaultEvaluators;
   }
public boolean isAllowRequestFeedback() {
	return allowRequestFeedback;
}
public void setAllowRequestFeedback(boolean allowRequestFeedback) {
	this.allowRequestFeedback = allowRequestFeedback;
}
public Collection getReviewers() {
	return reviewers;
}
public void setReviewers(Collection reviewers) {
	this.reviewers = reviewers;
}
public String getType()
{
	return type;
}
public void setType(String type)
{
	this.type = type;
}

public boolean isHideEvaluations() {
	return hideEvaluations;
}

public void setHideEvaluations(boolean hideEvaluations) {
	this.hideEvaluations = hideEvaluations;
}



}
