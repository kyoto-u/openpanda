/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/api/src/java/org/theospi/portfolio/presentation/model/TemplateFileRef.java $
* $Id:TemplateFileRef.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.shared.mgt.ArtifactUserType;
import org.theospi.portfolio.shared.model.Node;


/** @author Hibernate CodeGenerator */
public class TemplateFileRef extends IdentifiableObject implements Serializable {
   protected final transient Log logger = LogFactory.getLog(getClass());

   //private ArtifactUserType file;
   private String fileId;
   private String fileType;

   //private transient Artifact cachedArtifact = null;
   private transient String artifactName = null;
   private transient String action;

   /** nullable persistent field */
   private String usage;

   private transient PresentationTemplate presentationTemplate;

   static final long serialVersionUID = -6220810277272518156l;

   /** full constructor */
   public TemplateFileRef(ArtifactUserType file, String usage, PresentationTemplate presentationTemplate) {
       this.usage = usage;
       this.presentationTemplate = presentationTemplate;
   }

   /** default constructor */
   public TemplateFileRef() {
      // todo 8/10
      // setFile(new ArtifactUserType());
      //getFile().setHome("fileArtifact");
   }

   /** minimal constructor */
   public TemplateFileRef(PresentationTemplate presentationTemplate) {
       this.presentationTemplate = presentationTemplate;
   }


   public String getAction() {
      return action;
   }

   public void setAction(String action) {
      this.action = action;
   }

//   public ArtifactUserType getFile() {
//       return this.file;
//   }
//
//   public void setFile(ArtifactUserType file) {
//      this.cachedArtifact = null;
//       this.file = file;
//   }

   public String getUsage() {
       return this.usage;
   }

   public void setUsage(String use) {
       this.usage = use;
   }

   public PresentationTemplate getPresentationTemplate() {
       return this.presentationTemplate;
   }

   public void setPresentationTemplate(PresentationTemplate presentationTemplate) {
       this.presentationTemplate = presentationTemplate;
   }
   
   private Node getNode() {
      return getPresentationManager().getNode(getIdManager().getId(fileId));
   }

   public String getArtifactName() {
      if (artifactName == null) {
         if (getNode() != null) {
            artifactName = getNode().getDisplayName();
         }
      }

      return artifactName;
   }

   public void setArtifactName(String artifactName) {
      this.artifactName = artifactName;
   }

   public String toString() {
      return "TemplateFileRef{" +
         "id=" + getId() +
         ", fileId=" + fileId +
         ", usage='" + usage + "'" +
         ", presentationTemplate=" + presentationTemplate +
         "}";
   }

   public int hashCode() {
      if (getId() != null){
         return getId().hashCode();
      }

      if (fileId != null) {
         return fileId.hashCode();
      }
      else {
         return 0;
      }
   }
/*
   protected void setupFile() {
      if (fileType != null && fileId != null) {
         setFile(new ArtifactUserType(fileId, fileType));
      }
   }
*/
   public String getFileId() {
      //return getFile().getId().getValue();
      return this.fileId;
   }

   public void setFileId(String fileId) {
      this.fileId = fileId;
      //setupFile();
   }

   public String getFileType() {
      //return getFile().getHome().getType().getId().getValue();
      return this.fileType;
   }

   public void setFileType(String fileType) {
      this.fileType = fileType;
      //setupFile();
   }

   public PresentationManager getPresentationManager() {
      return (PresentationManager) ComponentManager.getInstance().get("presentationManager");
   }
   
   public IdManager getIdManager() {
      return (IdManager) ComponentManager.getInstance().get("idManager");
   }
}
