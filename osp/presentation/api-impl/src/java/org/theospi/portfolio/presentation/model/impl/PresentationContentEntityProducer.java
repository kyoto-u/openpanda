/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.3/presentation/api-impl/src/java/org/theospi/portfolio/presentation/model/impl/PresentationContentEntityProducer.java $
* $Id: PresentationContentEntityProducer.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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
package org.theospi.portfolio.presentation.model.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.EntityTransferrer;
import org.sakaiproject.metaobj.shared.mgt.EntityProducerBase;
import org.sakaiproject.service.legacy.resource.DuplicatableToolService;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 7, 2005
 * Time: 6:48:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class PresentationContentEntityProducer extends EntityProducerBase implements EntityTransferrer {
   protected final Log logger = LogFactory.getLog(getClass());
   protected static final String PRODUCER_NAME = "ospPresentation";
   private DuplicatableToolService presentationManager;

   public String getLabel() {
      return PRODUCER_NAME;
   }

   public void init() {
      logger.info("init()");
      try {
         getEntityManager().registerEntityProducer(this, Entity.SEPARATOR + PRODUCER_NAME);
      }
      catch (Exception e) {
         logger.warn("Error registering Presentation Content Entity Producer", e);
      }
   }
   
   public void transferCopyEntities(String fromContext, String toContext, List ids) {
      presentationManager.importResources(fromContext, toContext, ids);
   }

   /**
    * {@inheritDoc}
    */
   public String[] myToolIds() {
      String[] toolIds = { "osp.presTemplate" };
      return toolIds;
   }

   public void setPresentationManager(DuplicatableToolService presentationManager) {
      this.presentationManager = presentationManager;
   }
   
   public void transferCopyEntities(String fromContext, String toContext, List ids, boolean cleanup)
	{	
		//TODO
	}
}
