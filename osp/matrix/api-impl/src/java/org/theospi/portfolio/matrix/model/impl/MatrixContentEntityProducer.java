/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/branches/sakai-2.8.x/matrix/api-impl/src/java/org/theospi/portfolio/matrix/model/impl/MatrixContentEntityProducer.java $
* $Id: MatrixContentEntityProducer.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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
package org.theospi.portfolio.matrix.model.impl;

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
 * Date: Nov 8, 2005
 * Time: 5:27:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class MatrixContentEntityProducer extends EntityProducerBase implements EntityTransferrer {
   public static final String MATRIX_PRODUCER = "ospMatrix";
   protected final Log logger = LogFactory.getLog(getClass());
   private DuplicatableToolService matrixManager;
   

   public String getLabel() {
      return MATRIX_PRODUCER;
   }

   public void init() {
      logger.info("init()");
      try {
         getEntityManager().registerEntityProducer(this, Entity.SEPARATOR + MATRIX_PRODUCER);
      }
      catch (Exception e) {
         logger.warn("Error registering Matrix Content Entity Producer", e);
      }
   }
   
   public void transferCopyEntities(String fromContext, String toContext, List ids) {
      matrixManager.importResources(fromContext, toContext, ids);
   }

   /**
    * {@inheritDoc}
    */
   public String[] myToolIds() {
      String[] toolIds = { "osp.matrix" };
      return toolIds;
   }

   public void setMatrixManager(DuplicatableToolService matrixManager) {
      this.matrixManager = matrixManager;
   }
   
   public void transferCopyEntities(String fromContext, String toContext, List ids, boolean cleanup)
	{	
		//TODO
	}
}
