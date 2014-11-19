/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.1/wizard/api-impl/src/java/org/theospi/portfolio/wizard/impl/WizardEntityProducer.java $
* $Id: WizardEntityProducer.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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
package org.theospi.portfolio.wizard.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entity.api.EntityTransferrer;
import org.sakaiproject.metaobj.shared.mgt.EntityProducerBase;
import org.sakaiproject.service.legacy.resource.DuplicatableToolService;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 14, 2005
 * Time: 6:03:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class WizardEntityProducer extends EntityProducerBase implements EntityTransferrer {
           
   protected final Log logger = LogFactory.getLog(getClass());
   public static final String WIZARD_PRODUCER = "ospWizard";
   private DuplicatableToolService wizardManager;

   public String getLabel() {
      return WIZARD_PRODUCER;
   }

   public void init() {
      logger.info("init()");
      try {
         getEntityManager().registerEntityProducer(this, WIZARD_PRODUCER);
      }
      catch (Exception e) {
         logger.warn("Error registering Wizard Entity Producer", e);
      }
   }
   
   public void transferCopyEntities(String fromContext, String toContext, List ids) {
      wizardManager.importResources(fromContext, toContext, ids);
   }

   /**
    * {@inheritDoc}
    */
   public String[] myToolIds() {
      String[] toolIds = { "osp.wizard" };
      return toolIds;
   }

   public void setWizardManager(DuplicatableToolService wizardManager) {
      this.wizardManager = wizardManager;
   }
   
   public void transferCopyEntities(String fromContext, String toContext, List ids, boolean cleanup)
	{	
		//TODO
	}

}
