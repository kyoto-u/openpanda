/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.2/warehouse/api-impl/src/java/org/theospi/portfolio/warehouse/osp/wizard/WizardWarehouseTask.java $
* $Id: WizardWarehouseTask.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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
package org.theospi.portfolio.warehouse.osp.wizard;

import java.util.Collection;

import org.sakaiproject.warehouse.impl.BaseWarehouseTask;
import org.theospi.portfolio.wizard.mgt.WizardManager;

class WizardWarehouseTask extends BaseWarehouseTask {

   private WizardManager wizardManager;
   
   protected Collection getItems() {
      return wizardManager.getWizardsForWarehousing();
   }

   public WizardManager getWizardManager() {
      return wizardManager;
   }

   public void setWizardManager(WizardManager wizardManager) {
      this.wizardManager = wizardManager;
   }
}