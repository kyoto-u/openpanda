/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.2/matrix/api/src/java/org/theospi/portfolio/matrix/model/Cell.java $
* $Id: Cell.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
package org.theospi.portfolio.matrix.model;



import java.util.Set;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

/**
 * @author rpembry
 */
public class Cell extends IdentifiableObject {

   private Id id;
   private Matrix matrix;
   private ScaffoldingCell scaffoldingCell;
   private WizardPage wizardPage;
   
   public final static String TYPE = "matrix_cell_type";

   public Cell() {
      setWizardPage(new WizardPage());
   }

   /**
    * gets the attachments from the wizard page
    * @return Returns Set of Attachments
    */
   public Set getAttachments() {
      return wizardPage.getAttachments();
   }

   /**
    * This sets the attachments in the contained wizard page
    * @param attachments A Set of Attachments to set.
    */
   public void setAttachments(Set attachments) {
      wizardPage.setAttachments(attachments);
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

   /**
    * @return Returns the status.
    */
   public String getStatus() {
      return wizardPage.getStatus().toUpperCase();
   }

   /**
    * @param status The status to set.
    */
   public void setStatus(String status) {
      wizardPage.setStatus(status.toUpperCase());
   }

   /**
    * @return Returns the matrix.
    */
   public Matrix getMatrix() {
      return matrix;
   }

   /**
    * @param matrix The matrix to set.
    */
   public void setMatrix(Matrix matrix) {
      this.matrix = matrix;
   }


   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || !(other instanceof Cell)) return false;
      //TODO need better equals method
      if (this.getId() == null) return false;
      return (this.getId().equals(((Cell) other).getId()));

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
    * @return Returns the scaffoldingCell.
    */
   public ScaffoldingCell getScaffoldingCell() {
      return scaffoldingCell;
   }
   /**
    * @param scaffoldingCell The scaffoldingCell to set.
    */
   public void setScaffoldingCell(ScaffoldingCell scaffoldingCell) {
      this.scaffoldingCell = scaffoldingCell;
      wizardPage.setPageDefinition(scaffoldingCell.getWizardPageDefinition());
   }


   /**
    * @return Returns the cellForms.
    */
   public Set getCellForms() {
      return wizardPage.getPageForms();
   }

   /**
    * @param cellForms The cellForms to set.
    */
   public void setCellForms(Set cellForms) {
      wizardPage.setPageForms(cellForms);
   }

   public WizardPage getWizardPage() {
      return wizardPage;
   }

   public void setWizardPage(WizardPage wizardPage) {
      this.wizardPage = wizardPage;
   }
}
