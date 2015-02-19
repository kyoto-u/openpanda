/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/model/ContentResourceArtifact.java $
 * $Id: ContentResourceArtifact.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.shared.model;

import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Aug 17, 2005
 * Time: 3:24:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContentResourceArtifact implements Artifact {
   private ContentResource base;
   private Id id;
   private Agent owner;
   private String displayName;
   private String type;
   private ReadableObjectHome home;

   public ContentResourceArtifact(ContentResource base, Id id, Agent owner) {
      this.base = base;
      this.id = id;
      this.owner = owner;
      setDisplayName(base.getProperties().getProperty(ResourceProperties.PROP_DISPLAY_NAME));
   }

   public Agent getOwner() {
      return owner;
   }

   public void setOwner(Agent owner) {
      this.owner = owner;
   }

   public Id getId() {
      return id;
   }

   public void setId(Id id) {
      this.id = id;
   }

   public String getDisplayName() {
      return displayName;
   }

   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }

   public ContentResource getBase() {
      return base;
   }

   public void setBase(ContentResource base) {
      this.base = base;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public ReadableObjectHome getHome() {
      return home;
   }

   public void setHome(ReadableObjectHome home) {
      this.home = home;
   }
}
