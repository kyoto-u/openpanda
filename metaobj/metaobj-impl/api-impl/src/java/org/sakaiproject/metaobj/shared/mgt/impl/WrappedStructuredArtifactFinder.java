/*******************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/shared/mgt/impl/WrappedStructuredArtifactFinder.java $
 * $Id: WrappedStructuredArtifactFinder.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 * **********************************************************************************
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
 ******************************************************************************/

package org.sakaiproject.metaobj.shared.mgt.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ResourceType;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.ContentResourceArtifact;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.MimeType;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Dec 11, 2006
 * Time: 8:48:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class WrappedStructuredArtifactFinder  extends FileArtifactFinder {

   private ContentHostingService contentHostingService;
   private AgentManager agentManager;
   private IdManager idManager;
   private int finderPageSize = 1000;
   
   private static Log log = LogFactory.getLog(WrappedStructuredArtifactFinder.class);

   public Collection findByOwnerAndType(Id owner, String type) {
   
      if (owner == null)
      {
         log.info("Null owner passed to findByOwnerAndType -- returning all users' forms");
         return findByType( type );
      }
      
      Set siteIds = new TreeSet();
      Site site = null;
      List siteList = org.sakaiproject.site.cover.SiteService.getSites(
                                           org.sakaiproject.site.api.SiteService.SelectionType.ACCESS,
                                           null, null, null, 
                                           org.sakaiproject.site.api.SiteService.SortType.NONE, null);
                                           
      // find all sites user has access to
      for (Iterator it = siteList.iterator(); it.hasNext();) 
      {
         site = (Site) it.next();
         siteIds.add( site.getId() );
      }
         
      // add user MyWorkspace site
      try
      {
         site = SiteService.getSite(SiteService.getUserSiteId(owner.getValue()));
         siteIds.add( site.getId() );
      }
      catch (Exception e)
      {
         log.info("findOwnerAndType", e);
      }      
   
      Collection<ContentResource> artifacts = 
         getContentHostingService().getContextResourcesOfType( ResourceType.TYPE_METAOBJ, siteIds );
      
      ArrayList<ContentResourceArtifact> returned = new ArrayList<ContentResourceArtifact>();
      
      for (Iterator<ContentResource> i = artifacts.iterator(); i.hasNext();) {
         ContentResource resource = i.next();
         Agent resourceOwner = getAgentManager().getAgent(resource.getProperties().getProperty(ResourceProperties.PROP_CREATOR));
         String actualType = resource.getProperties().getProperty(ResourceProperties.PROP_STRUCTOBJ_TYPE);
         
         // filter list for owner and form type
         if ( owner == null || owner.equals(resourceOwner.getId()) 
              && (type == null  || type.equals(actualType)) ) { 
         
          Id resourceId = getIdManager().getId(getContentHostingService().getUuid(resource.getId()));
          returned.add(new ContentResourceArtifact(resource, resourceId, resourceOwner));
         }
      }
      return returned;
   }

   public Collection findByOwnerAndType(Id owner, String type, MimeType mimeType) {
      return null;
   }

   public Collection findByOwner(Id owner) {
      return null;
   }

   public Collection findByWorksiteAndType(Id worksiteId, String type) {
      return null;
   }

   public Collection findByWorksite(Id worksiteId) {
      return null;
   }

   public Collection findByType(String type) {
      return null;
   }

   public boolean getLoadArtifacts() {
      return false;
   }

   public void setLoadArtifacts(boolean loadArtifacts) {

   }

   public ContentHostingService getContentHostingService() {
      return contentHostingService;
   }

   public void setContentHostingService(ContentHostingService contentHostingService) {
      this.contentHostingService = contentHostingService;
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   /**
    * @return the finderPageSize
    */
   public int getFinderPageSize() {
      return finderPageSize;
   }

   /**
    * @param finderPageSize the finderPageSize to set
    */
   public void setFinderPageSize(int finderPageSize) {
      this.finderPageSize = finderPageSize;
   }
  
}
