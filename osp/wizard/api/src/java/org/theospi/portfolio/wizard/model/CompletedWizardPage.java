/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/wizard/api/src/java/org/theospi/portfolio/wizard/model/CompletedWizardPage.java $
* $Id:CompletedWizardPage.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.util.Date;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.theospi.portfolio.matrix.model.WizardPage;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 21, 2006
 * Time: 3:20:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompletedWizardPage extends IdentifiableObject {

   private CompletedWizardCategory category;
   private WizardPageSequence wizardPageDefinition;
   private WizardPage wizardPage;
   private Date created;
   private Date lastVisited;
   private int sequence;

   public CompletedWizardPage() {
   }

   public CompletedWizardPage(WizardPageSequence wizardPageDefinition, CompletedWizardCategory category) {
      this.wizardPageDefinition = wizardPageDefinition;
      this.category = category;
      setCreated(new Date());
      setSequence(wizardPageDefinition.getSequence());
      setWizardPage(new WizardPage());
      getWizardPage().setOwner(category.getWizard().getOwner());
      getWizardPage().setPageDefinition(wizardPageDefinition.getWizardPageDefinition());
      getWizardPage().setStatus(wizardPageDefinition.getWizardPageDefinition().getInitialStatus());
   }

   public CompletedWizardCategory getCategory() {
      return category;
   }

   public void setCategory(CompletedWizardCategory category) {
      this.category = category;
   }

   public WizardPageSequence getWizardPageDefinition() {
      return wizardPageDefinition;
   }

   public void setWizardPageDefinition(WizardPageSequence wizardPageDefinition) {
      this.wizardPageDefinition = wizardPageDefinition;
   }

   public WizardPage getWizardPage() {
      return wizardPage;
   }

   public void setWizardPage(WizardPage wizardPage) {
      this.wizardPage = wizardPage;
   }

   public Date getCreated() {
      return created;
   }

   public void setCreated(Date created) {
      this.created = created;
   }

   public Date getLastVisited() {
      return lastVisited;
   }

   public void setLastVisited(Date lastVisited) {
      this.lastVisited = lastVisited;
   }

   public int getSequence() {
      return sequence;
   }

   public void setSequence(int sequence) {
      this.sequence = sequence;
   }

}
