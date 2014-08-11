/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.0/wizard/api/src/java/org/theospi/portfolio/wizard/model/WizardPageSequence.java $
* $Id: WizardPageSequence.java 73575 2010-02-16 20:55:30Z botimer@umich.edu $
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
package org.theospi.portfolio.wizard.model;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 12, 2006
 * Time: 4:38:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class WizardPageSequence extends IdentifiableObject {

   private WizardPageDefinition wizardPageDefinition;
   private int sequence;
   private WizardCategory category;
   private String title = null;

   public boolean equals(Object in) {
      return super.equals(in);
   }

   public int hashCode() {
      return super.hashCode();
   }
   
   public WizardPageSequence() {

   }

   public WizardPageSequence(WizardPageDefinition wizardPageDefinition) {
      this.wizardPageDefinition = wizardPageDefinition;
   }

   public WizardPageDefinition getWizardPageDefinition() {
      return wizardPageDefinition;
   }

   public void setWizardPageDefinition(WizardPageDefinition wizardPageDefinition) {
      this.wizardPageDefinition = wizardPageDefinition;
   }

   public int getSequence() {
      return sequence;
   }

   public void setSequence(int sequence) {
      this.sequence = sequence;
   }

   public WizardCategory getCategory() {
      return category;
   }

   public void setCategory(WizardCategory category) {
      this.category = category;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }
}
