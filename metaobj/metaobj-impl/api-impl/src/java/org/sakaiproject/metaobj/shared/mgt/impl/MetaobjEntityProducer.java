/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/shared/mgt/impl/MetaobjEntityProducer.java $
 * $Id: MetaobjEntityProducer.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2006, 2007, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.shared.mgt.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.EntityTransferrer;
import org.sakaiproject.metaobj.shared.mgt.EntityProducerBase;
import org.sakaiproject.metaobj.shared.mgt.MetaobjEntityManager;
import org.sakaiproject.metaobj.shared.mgt.ReferenceParser;
import org.sakaiproject.service.legacy.resource.DuplicatableToolService;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Feb 21, 2006
 * Time: 1:23:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetaobjEntityProducer extends EntityProducerBase implements EntityTransferrer {

   protected final Log logger = LogFactory.getLog(getClass());
   private DuplicatableToolService structuredArtifactDefinitionManager;
   
   public String getLabel() {
      return MetaobjEntityManager.METAOBJ_ENTITY_PREFIX;
   }

   public void init() {
      logger.info("init()");
      try {
         getEntityManager().registerEntityProducer(this, Entity.SEPARATOR + MetaobjEntityManager.METAOBJ_ENTITY_PREFIX);
      }
      catch (Exception e) {
         logger.warn("Error registering MetaObj Entity Producer", e);
      }   
   }

   protected ReferenceParser parseReference(String wholeRef) {
      return new ReferenceParser(wholeRef, this, false);
   }

   public void transferCopyEntities(String fromContext, String toContext, List ids) {
      getStructuredArtifactDefinitionManager().importResources(fromContext, toContext, ids);      
   }

   public String[] myToolIds() {
      String[] toolIds = { "sakai.metaobj" };
      return toolIds;
   }

   public DuplicatableToolService getStructuredArtifactDefinitionManager() {
      return structuredArtifactDefinitionManager;
   }

   public void setStructuredArtifactDefinitionManager(
         DuplicatableToolService structuredArtifactDefinitionManager) {
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }
   
   public void transferCopyEntities(String fromContext, String toContext, List ids, boolean cleanup)
	{	
		//TODO
	}

}
