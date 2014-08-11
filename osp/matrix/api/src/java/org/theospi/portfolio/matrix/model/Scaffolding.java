/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/api/src/java/org/theospi/portfolio/matrix/model/Scaffolding.java $
* $Id:Scaffolding.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.theospi.portfolio.shared.model.ObjectWithWorkflow;
import org.theospi.portfolio.style.model.Style;
import org.theospi.portfolio.shared.model.WizardMatrixConstants;

/**
 */
public class Scaffolding extends ObjectWithWorkflow implements Serializable {
   private Id id;
   private List levels = new ArrayList();
   private List criteria = new ArrayList();
   private Set scaffoldingCells = new HashSet();
   private Agent owner;
   private String title;
   private String columnLabel;
   private String rowLabel;
   private String readyColor;
   private String pendingColor;
   private String completedColor;
   private String lockedColor;
   private String returnedColor;
   
   private Style style;
   
   private String description;
   private Id worksiteId;
   
   private boolean preview = false;
	
   private boolean published = false;
   private Agent publishedBy;
   private Date publishedDate;
   private Date modifiedDate;
   
   private String exposedPageId;
   private transient Boolean exposeAsTool = null;
   
   transient private boolean validate;
   
   private int workflowOption;
   private int generalFeedbackOption;
   private int itemFeedbackOption;
   private Set matrix = new HashSet();
   
	// Dependent on ordering of <c:forTokens> in addScaffolding.jsp
   public static final int NO_PROGRESSION = 0;
   public static final int HORIZONTAL_PROGRESSION = 1;
   public static final int VERTICAL_PROGRESSION = 2;
   public static final int OPEN_PROGRESSION = 3;
   public static final int MANUAL_PROGRESSION = 4;
   
   private List additionalForms = new ArrayList();
   private List<String> attachments = new ArrayList();
   private Collection evaluators = new HashSet();
   private Collection reviewers = new HashSet();   
   private boolean allowRequestFeedback = true;
   private boolean hideEvaluations = false;
   
   //this variable is used for version control: if this is null when importing a matrix,
   //then the matrix is an older version and set all defaults to false
   private boolean defaultFormsMatrixVersion = false;
   
   public Scaffolding() {}
   
   public Scaffolding (String columnLabel, String rowLabel) {
      this.columnLabel = columnLabel;
      this.rowLabel = rowLabel;
   }
   
   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object other) {
      //TODO need better equals
      if (other == this) return true;
      if (other == null || !(other instanceof Scaffolding)) return false;
      return (this.getId().equals(((Scaffolding) other).getId()));

   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   public int hashCode() {
      //TODO need better hashcode
      Id id = this.getId();
      if (id == null) return 0;
      return id.getValue().hashCode();
   }

   /**
    * Typical levels might be Beginner, Intermediate, Advanced
    */
   public List getLevels() {
      return levels;
   }

   /**
    * @return List of Criteria
    */
   public List getCriteria() {
      return criteria;
   }


   /**
    * @return Returns the owner.
    */
   public Agent getOwner() {
      return owner;
   }

   /**
    * @param owner The owner to set.
    */
   public void setOwner(Agent owner) {
      this.owner = owner;
   }

   /**
    * @param criteria The criteria to set.
    */
   public void setCriteria(List criteria) {
      this.criteria = criteria;
   }

   /**
    * @param levels The levels to set.
    */
   public void setLevels(List levels) {
      this.levels = levels;
   }

   /**
    * @return Returns the id.
    */
   public Id getId() {
      return id;
   }

   /**
    * @param id The id to set.
    */
   public void setId(Id id) {
      this.id = id;
   }

   public void add(Criterion criterion) {
      this.getCriteria().add(criterion);
   }

   public void add(Level level) {
      this.getLevels().add(level);
   }
   
   public void add(ScaffoldingCell scaffoldingCell) {
      this.getScaffoldingCells().add(scaffoldingCell);
   }

   /**
    * @return Returns the title.
    */
   public String getTitle() {
      return title;
   }

   /**
    * @param title The title to set.
    */
   public void setTitle(String title) {
      this.title = title;
   }
   /**
    * @return Returns the expectations.
    */
   public Set getScaffoldingCells() {
      return scaffoldingCells;
   }
   /**
    * @param expectations The expectations to set.
    */
   public void setScaffoldingCells(Set scaffoldingCells) {
      this.scaffoldingCells = scaffoldingCells;
   }

   /**
    * @return Returns the description.
    */
   public String getDescription() {
      return description;
   }
   /**
    * @param description The description to set.
    */
   public void setDescription(String description) {
      this.description = description;
   }
  
   /**
    * @return Returns the worksiteId.
    */
   public String getWorksiteName() {
	   String worksiteName = "";
		
      try
      {
         Site site = SiteService.getSite(worksiteId.getValue());
			worksiteName = site.getTitle();
      }
      catch (IdUnusedException e)
      {
         // tbd
      }

      return worksiteName;
   }
	
   /**
    * @return Returns the worksiteId.
    */
   public Id getWorksiteId() {
      return worksiteId;
   }
   /**
    * @param worksiteId The worksiteId to set.
    */
   public void setWorksiteId(Id worksiteId) {
      this.worksiteId = worksiteId;
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
   public Agent getPublishedBy() {
      return publishedBy;
   }
   public void setPublishedBy(Agent publishedBy) {
      this.publishedBy = publishedBy;
   }
   public Date getPublishedDate() {
      return publishedDate;
   }
   public void setPublishedDate(Date publishedDate) {
      this.publishedDate = publishedDate;
   }

   public String getColumnLabel() {
      return columnLabel;
   }

   public void setColumnLabel(String columnLabel) {
      this.columnLabel = columnLabel;
   }

   public String getRowLabel() {
      return rowLabel;
   }

   public void setRowLabel(String rowLabel) {
      this.rowLabel = rowLabel;
   }

   public String getCompletedColor() {
      return completedColor;
   }

   public void setCompletedColor(String completedColor) {
      this.completedColor = completedColor;
   }

   public String getLockedColor() {
      return lockedColor;
   }

   public void setLockedColor(String lockedColor) {
      this.lockedColor = lockedColor;
   }

   public String getPendingColor() {
      return pendingColor;
   }

   public void setPendingColor(String pendingColor) {
      this.pendingColor = pendingColor;
   }

   public String getReadyColor() {
      return readyColor;
   }

   public void setReadyColor(String readyColor) {
      this.readyColor = readyColor;
   }
   
   /**
    * @return Returns the workflowOption.
    */
   public int getWorkflowOption() {
      return workflowOption;
   }

   /**
    * @param workflowOption The workflowOption to set.
    */
   public void setWorkflowOption(int workflowOption) {
      this.workflowOption = workflowOption;
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
   
   public Set getMatrix() {
      return matrix;
   }

   public void setMatrix(Set matrix) {
      this.matrix = matrix;
   }
   
   public void add(Matrix matrix) {
      this.getMatrix().add(matrix);
      matrix.setScaffolding(this);
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

   public Style getStyle() {
	   return style;
   }

   public void setStyle(Style style) {
	   this.style = style;
   }

   public List getAdditionalForms() {
	   return additionalForms;
   }

   public void setAdditionalForms(List additionalForms) {
	   this.additionalForms = additionalForms;
   }

   public List<String> getAttachments() {
	   return attachments;
   }

   public void setAttachments(List<String> attachments) {
	   this.attachments = attachments;
   }

   public Collection getEvaluators() {
	   return evaluators;
   }

   public void setEvaluators(Collection evaluators) {
	   this.evaluators = evaluators;
   }

   public String getReturnedColor() {
	   return returnedColor;
   }

   public void setReturnedColor(String returnedColor) {
	   this.returnedColor = returnedColor;
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

public boolean isDefaultFormsMatrixVersion() {
	return defaultFormsMatrixVersion;
}

public void setDefaultFormsMatrixVersion(boolean defaultFormsMatrixVersion) {
	this.defaultFormsMatrixVersion = defaultFormsMatrixVersion;
}

public Date getModifiedDate() {
	return modifiedDate;
}

public void setModifiedDate(Date modifiedDate) {
	this.modifiedDate = modifiedDate;
}
public String getReference() {
	return "/scaffolding/" + getWorksiteId() + "/" + getId().getValue();
}

public boolean isHideEvaluations() {
	return hideEvaluations;
}

public void setHideEvaluations(boolean hideEvaluations) {
	this.hideEvaluations = hideEvaluations;
}
}
