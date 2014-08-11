/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api/src/java/org/theospi/portfolio/shared/mgt/ArtifactUserType.java $
* $Id:ArtifactUserType.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.shared.mgt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.PersistenceException;

public class ArtifactUserType {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private Id id = null;
   private ReadableObjectHome home = null;

   public ArtifactUserType() {
   }

   public ArtifactUserType(String id, String type) {
      this.id = getIdManager().getId(id);
      setHome(type);
   }

   protected IdManager getIdManager() {
      return (IdManager) ComponentManager.getInstance().get("idManager");
   }

   protected HomeFactory getHomeFactory() {
      return (HomeFactory) ComponentManager.getInstance().get("homeFactory");
   }

   public Artifact load() throws PersistenceException {
      if (home != null && id != null) {
         return home.load(id);
      }
      else {
         return null;
      }
   }

   public Id getId() {
      return id;
   }

   public void setId(Id id) {
      this.id = id;
   }

   public ReadableObjectHome getHome() {
      return home;
   }

   public void setHome(String homeName) {
      this.home = getHomeFactory().getHome(homeName);
   }

   public void setHome(ReadableObjectHome home) {
      this.home = home;
   }

   public String toString() {
      return "ArtifactUserType{" +
         "id=" + id +
         ", home=" + home +
         "}";
   }

}
