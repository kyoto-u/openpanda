/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.2/matrix/api/src/java/org/theospi/portfolio/matrix/model/WizardPageForm.java $
* $Id: WizardPageForm.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;


/**
 * @author rpembry
 */
public class WizardPageForm extends IdentifiableObject {
   private Id artifactId;
   private WizardPage wizardPage;
   private String formType;

   /**
    * @return Returns the artifactId.
    */
   public Id getArtifactId() {
      return artifactId;
   }

   /**
    * @param artifactId The artifactId to set.
    */
   public void setArtifactId(Id artifactId) {
      this.artifactId = artifactId;
   }

   /**
    * @return Returns the wizardPage that contains this Attachment
    */

   public WizardPage getWizardPage() {
      return wizardPage;
   }

   /**
    * @param wizardPage The parent wizardPage for this Attachment
    */
   public void setWizardPage(WizardPage wizardPage) {
      this.wizardPage = wizardPage;
   }
      
   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    * the OspMigrationJob class creates instances of this class.
    * it doesn't set the id, thus it is null, but each is different from each other
    */
   public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || !(other instanceof WizardPageForm)) return false;
      //TODO need better equals method
      
      WizardPageForm form = (WizardPageForm) other;
      
      if (this.getArtifactId().equals(form.getArtifactId()) && this.getWizardPage().getVirtualId().equals(form.getWizardPage().getVirtualId()))
         return true;
      
      if (this.getVirtualId() == null && form.getVirtualId() != null) return false;
      if (this.getVirtualId() != null && form.getVirtualId() == null) return false;
      //if(this.getId() == null) return false;
      return (this.getVirtualId().equals(form.getVirtualId()));

   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   public int hashCode() {
      String compositeId = "";
      Id id = this.getVirtualId();
      try {
         compositeId = getArtifactId().getValue() + getFormType() + 
            getWizardPage().getId();
      }      
      catch (Exception e) {
         compositeId = id.getValue();
      }
      return compositeId.hashCode();
   }
/*      
   public String toString() {
      return "<Cell id:" + this.wizardPage.getId() + ", artifactId:" + this.getArtifactId() + "]>";
   }
*/

   /**
    * @return Returns the formType.
    */
   public String getFormType() {
      return formType;
   }

   /**
    * @param formType The formType to set.
    */
   public void setFormType(String formType) {
      this.formType = formType;
   }
}
