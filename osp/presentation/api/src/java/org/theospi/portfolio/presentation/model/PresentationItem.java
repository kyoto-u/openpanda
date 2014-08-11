/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/api/src/java/org/theospi/portfolio/presentation/model/PresentationItem.java $
* $Id:PresentationItem.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.presentation.model;

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.sakaiproject.metaobj.shared.model.PersistenceException;

public class PresentationItem extends IdentifiableObject {
   private PresentationItemDefinition definition;
   private Id artifactId;

   public Artifact getArtifact() throws PersistenceException {
      ReadableObjectHome home = getHomeFactory().getHome(definition.getType());
      return (Artifact) home.load(artifactId);
   }

   public void setArtifact(Artifact artifact) {
      artifactId = artifact.getId();
   }

   public void setDefinition(PresentationItemDefinition definition) {
      this.definition = definition;
   }

   public PresentationItemDefinition getDefinition() {
      return definition;
   }

   public Id getArtifactId() {
      return artifactId;
   }

   public int hashCode() {
      if (getId() != null){
         return getId().hashCode();
      }
      return (artifactId != null) ? (artifactId.getValue() + getHashDefId()).hashCode() : 0;
   }

   protected String getHashDefId() {
      if (definition == null) {
         return "";
      }
      else if (definition.getId() == null) {
         return "";
      }
      else {
         return definition.getId().getValue();
      }
   }

   public void setArtifactId(Id artifactId) {
      this.artifactId = artifactId;
   }

   public HomeFactory getHomeFactory() {
      return (HomeFactory) ComponentManager.getInstance().get("homeFactory");
   }

}
