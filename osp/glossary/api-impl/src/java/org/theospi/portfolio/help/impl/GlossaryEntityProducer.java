/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/branches/sakai-2.8.x/glossary/api-impl/src/java/org/theospi/portfolio/help/impl/GlossaryEntityProducer.java $
* $Id: GlossaryEntityProducer.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
***********************************************************************************
*
 * Copyright (c) 2006, 2007, 2008 The Sakai Foundation
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

package org.theospi.portfolio.help.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entity.api.EntityTransferrer;
import org.sakaiproject.metaobj.shared.mgt.EntityProducerBase;
import org.theospi.portfolio.help.model.Glossary;

public class GlossaryEntityProducer extends EntityProducerBase implements EntityTransferrer {

   protected final Log logger = LogFactory.getLog(getClass());
   public static final String GLOSSARY_PRODUCER = "ospGlossary";
   private Glossary glossary;
  
   public String getLabel() {
      return GLOSSARY_PRODUCER;
   }

   public void init() {
      logger.info("init()");
      try {
         getEntityManager().registerEntityProducer(this, GLOSSARY_PRODUCER);
      }
      catch (Exception e) {
         logger.warn("Error registering Glossary Entity Producer", e);
      }
   }

   public void transferCopyEntities(String fromContext, String toContext, List ids) {
      glossary.importResources(fromContext, toContext, ids);
   }

   /**
    * {@inheritDoc}
    */
   public String[] myToolIds() {
      String[] toolIds = { "osp.glossary" };
      return toolIds;
   }
   
   public void setGlossary(Glossary glossary) {
      this.glossary = glossary;
   }
   
   public void transferCopyEntities(String fromContext, String toContext, List ids, boolean cleanup)
	{	
		//TODO
	}

}



