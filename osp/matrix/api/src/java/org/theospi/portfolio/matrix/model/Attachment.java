/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/api/src/java/org/theospi/portfolio/matrix/model/Attachment.java $
* $Id:Attachment.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
public class Attachment extends IdentifiableObject {
   Id artifactId;
   WizardPage wizardPage;

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
    */
   public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || !(other instanceof Attachment)) return false;
      //TODO need better equals method
      
      Attachment att = (Attachment) other;
      if (this.getArtifactId().equals(att.getArtifactId()) && this.getWizardPage().getVirtualId().equals(att.getWizardPage().getVirtualId()))
         return true;
      //if (getId() == null && getNewId() != null && att.getId() == null && 
      //      att.getNewId() != null) return (this.getNewId().equals(att.getNewId()));
      if (getVirtualId() == null && att.getVirtualId() != null) return false;
      if (getVirtualId() != null && att.getVirtualId() == null) return false;
      return (this.getVirtualId().equals(att.getVirtualId()));

   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   public int hashCode() {
      //TODO need better hashcode
      Id id = this.getVirtualId();
      if (id == null) return 0;
      return id.getValue().hashCode();
   }
/*      
   public String toString() {
      return "<Cell id:" + this.wizardPage.getId() + ", artifactId:" + this.getArtifactId() + "]>";
   }
*/
}
