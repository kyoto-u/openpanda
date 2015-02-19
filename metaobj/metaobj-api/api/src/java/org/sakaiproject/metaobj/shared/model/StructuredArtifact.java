/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/model/StructuredArtifact.java $
 * $Id: StructuredArtifact.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.shared.model;

import org.jdom.Element;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactHomeInterface;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.content.api.ContentResource;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 19, 2004
 * Time: 2:14:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class StructuredArtifact extends ElementBean implements Artifact {
   private Agent owner = null;
   private Id id = null;
   private StructuredArtifactHomeInterface home = null;
   private IdManager idManager = null;
   private String parentFolder = null;

   private static String ID_PARAMETER_NAME = "id";
   private static String ARTIFACT_ID_PARAMETER_NAME = "artifactId";
   private static String DISPLAY_NAME_PARAMETER_NAME = "displayName";
   private String displayName;
   private ContentResource baseResource;

   public StructuredArtifact(String elementName, SchemaNode currentSchema) {
      super(elementName, currentSchema);
   }

   public StructuredArtifact() {
      super();
   }

   public StructuredArtifact(Element baseElement, SchemaNode currentSchema) {
      super(baseElement, currentSchema, true);
   }

   public Agent getOwner() {
      return owner;
   }

   public Id getId() {
      return id;
   }

   public void setId(Id id) {
      this.id = id;
   }

   public void setId(Object id) {
      if (id instanceof Id) {
         this.id = (Id) id;
      }
      else if (id != null) {
         this.id = getIdManager().getId(id.toString());
      }
      else {
         this.id = null;
      }
   }

   public Object get(Object key) {
      if (key.equals(ID_PARAMETER_NAME) || key.equals(ARTIFACT_ID_PARAMETER_NAME)) {
         return getId();
      }
      else if (key.equals(DISPLAY_NAME_PARAMETER_NAME)) {
         return getDisplayName();
      }
      else {
         return super.get(key);
      }
   }

   public Object put(Object key, Object value) {
      if (key.equals(ID_PARAMETER_NAME) || key.equals(ARTIFACT_ID_PARAMETER_NAME)) {
         setId(value);
         return null;
      }
      else if (key.equals(DISPLAY_NAME_PARAMETER_NAME)) {
         setDisplayName((String) value);
         return null;
      }
      else {
         return super.put(key, value);
      }
   }

   public boolean equals(Object other) {
      if (other == null || !(other instanceof StructuredArtifact)) {
         return false;
      }
      StructuredArtifact in = (StructuredArtifact) other;
      return getId().equals(in.getId());
   }

   public int hashCode() {
      if (id == null) {
         return 0;
      }
      return this.id.hashCode();
   }

   public ReadableObjectHome getHome() {
      return home;
   }

   public void setHome(ReadableObjectHome homeObject) {
      home = (StructuredArtifactHomeInterface) homeObject;
      this.setCurrentSchema(home.getSchema().getChild(home.getRootNode()));
   }

   public String getDisplayName() {
      return displayName;
   }

   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }

   public Class getType(String key) {
      if (key.equals(ID_PARAMETER_NAME) || key.equals(ARTIFACT_ID_PARAMETER_NAME)) {
         return Id.class;
      }
      else if (key.equals(DISPLAY_NAME_PARAMETER_NAME)) {
         return String.class;
      }
      else {
         return super.getType(key);
      }
   }

   public IdManager getIdManager() {
      if (idManager == null) {
         idManager = (IdManager) ComponentManager.getInstance().get("idManager");
      }
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public String toString() {
      return "Artifact: id=" + getId();
   }

   public void setOwner(Agent owner) {
      this.owner = owner;
   }

   public String getParentFolder() {
      return parentFolder;
   }

   public void setParentFolder(String parentFolder) {
      this.parentFolder = parentFolder;
   }

   public ContentResource getBaseResource() {
      return baseResource;
   }

   public void setBaseResource(ContentResource baseResource) {
      this.baseResource = baseResource;
   }
}
