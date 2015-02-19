/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/shared/mgt/impl/StructuredArtifactFinder.java $
 * $Id: StructuredArtifactFinder.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ResourceType;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactHomeInterface;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Aug 17, 2005
 * Time: 2:33:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class StructuredArtifactFinder extends WrappedStructuredArtifactFinder {

   private HomeFactory homeFactory;

   protected Artifact createArtifact(ContentResource resource, Id artifactId) {
	   return createArtifact(resource);
   }
   
   protected Artifact createArtifact(ContentResource resource) {
      String formType = (String) resource.getProperties().get(
         resource.getProperties().getNamePropStructObjType());

      StructuredArtifactHomeInterface home =
         (StructuredArtifactHomeInterface) getHomeFactory().getHome(formType);

      return home.load(resource);
   }

   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }

   public Collection findByType(String type) {
      ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
      ArrayList<ContentResource> resources = new ArrayList<ContentResource>();
      
      int page = 0;
      Collection<ContentResource> resourcePagelist = getContentHostingService().getResourcesOfType(
            ResourceType.TYPE_METAOBJ, getFinderPageSize(), page);
      
      while (resourcePagelist != null && resourcePagelist.size() > 0) {
         resources.addAll(resourcePagelist);
         resourcePagelist = getContentHostingService().getResourcesOfType(
            ResourceType.TYPE_METAOBJ, getFinderPageSize(), ++page);
      }
            
      for (Iterator<ContentResource> i = resources.iterator(); i.hasNext();) {
         ContentResource resource = i.next();
         String actualType = resource.getProperties().getProperty(ResourceProperties.PROP_STRUCTOBJ_TYPE);
         
         // filter list for form type
         if ( type == null || type.equals(actualType) ) { 
            artifacts.add(createArtifact(resource));
         }
      }
      
      return artifacts;
   }
}
