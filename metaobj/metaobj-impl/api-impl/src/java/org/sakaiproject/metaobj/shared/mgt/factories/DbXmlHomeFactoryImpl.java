/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/shared/mgt/factories/DbXmlHomeFactoryImpl.java $
 * $Id: DbXmlHomeFactoryImpl.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008, 2009 The Sakai Foundation
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

package org.sakaiproject.metaobj.shared.mgt.factories;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactDefinition;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.utils.xml.SchemaInvalidException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 9, 2004
 * Time: 12:51:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class DbXmlHomeFactoryImpl extends HomeFactoryBase implements HomeFactory {

   protected final Log logger = LogFactory.getLog(getClass());
   
   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;

   public boolean handlesType(String objectType) {
      return (getHome(objectType) != null);
   }

   public Map getHomes(Class requiredHomeType) {
      return super.getHomes(requiredHomeType);
   }

   public ReadableObjectHome findHomeByExternalId(String externalId, Id worksiteId) {
      return createHome(getStructuredArtifactDefinitionManager().loadHomeByExternalType(externalId, worksiteId));
   }

   public ReadableObjectHome getHome(String objectType) {
      StructuredArtifactDefinitionBean sad = getStructuredArtifactDefinitionManager().loadHome(objectType);
      if ( sad == null ) {
         logger.warn(this+" Null StructuredArtifactDefinitionBean (perhaps multiple submits) for: " + objectType);
         return null;
      }
      else {
         return createHome( sad );
      }
   }

   public Map getWorksiteHomes(Id worksiteId) {
      return createHomes(getStructuredArtifactDefinitionManager().getWorksiteHomes(worksiteId));
   }

   public Map getWorksiteHomes(Id worksiteId, boolean includeHidden) {
      return createHomes(getStructuredArtifactDefinitionManager().getWorksiteHomes(worksiteId, true));
   }

   public Map getWorksiteHomes(Id worksiteId, String currentUserId, boolean includeHidden) {
      return createHomes(getStructuredArtifactDefinitionManager().getWorksiteHomes(worksiteId, currentUserId, true));
   }

   public Map getHomes() {
      return createHomes(getStructuredArtifactDefinitionManager().getHomes());
   }

   protected Map createHomes(Map homeBeans) {
      Map homeBeansSet = new HashMap();
      for (Iterator i = homeBeans.entrySet().iterator(); i.hasNext();) {
         Map.Entry entry = (Map.Entry) i.next();
         ReadableObjectHome roh = createHome((StructuredArtifactDefinitionBean) entry.getValue());
         if ( roh != null )
            homeBeansSet.put( entry.getKey(), roh );
      }
      return homeBeansSet;
   }

   protected ReadableObjectHome createHome(StructuredArtifactDefinitionBean sadBean) {
      try{
         return new StructuredArtifactDefinition(sadBean);
      }
      catch (SchemaInvalidException e) {
         logger.warn(e);
         return null; 
      }
   }

   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
      return structuredArtifactDefinitionManager;
   }

   public void setStructuredArtifactDefinitionManager(StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }

}
