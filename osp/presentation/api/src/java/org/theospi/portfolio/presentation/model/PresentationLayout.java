/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.2/presentation/api/src/java/org/theospi/portfolio/presentation/model/PresentationLayout.java $
* $Id: PresentationLayout.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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
import java.util.Date;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

public class PresentationLayout extends IdentifiableObject implements Serializable {
   private String name;
   private String description;
   private Date created;
   private Date modified;
   private transient Agent owner;
   private Id xhtmlFileId;
   private Id previewImageId;
   private String toolId;
   private String siteId;   
   
   /**
    * should be one of the following states
    *
    * unpublished -> waiting for approval-> active
    */
   private int globalState;
   
   transient private String xhtmlFileName;
   transient private String previewImageName;
   
   
   transient private boolean validate = true;
   transient private String filePickerAction;

   static final long serialVersionUID = -6220810277272518156l;
   
   public static final int STATE_UNPUBLISHED = 0;
   public static final int STATE_WAITING_APPROVAL = 1;
   public static final int STATE_PUBLISHED = 2;


   public String getName() {
      return name;
   }

   public String getDescription() {
      return description;
   }

   public Date getCreated() {
      return created;
   }

   public Date getModified() {
      return modified;
   }

   public Agent getOwner() {
      return owner;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setCreated(Date created) {
      this.created = created;
   }

   public void setModified(Date modified) {
      this.modified = modified;
   }

   public void setOwner(Agent owner) {
      this.owner = owner;
   }

   public boolean isValidate() {
      return validate;
   }

   public void setValidate(boolean validate) {
      this.validate = validate;
   }

   public Id getPreviewImageId() {
      return previewImageId;
   }

   public void setPreviewImageId(Id previewImageId) {
      this.previewImageId = previewImageId;
   }

   public Id getXhtmlFileId() {
      return xhtmlFileId;
   }

   public void setXhtmlFileId(Id xhtmlFileId) {
      this.xhtmlFileId = xhtmlFileId;
   }

   public String getSiteId() {
      return siteId;
   }

   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }

   public String getToolId() {
      return toolId;
   }

   public void setToolId(String toolId) {
      this.toolId = toolId;
   }

   public String getPreviewImageName() {
      return previewImageName;
   }

   public void setPreviewImageName(String previewImageName) {
      this.previewImageName = previewImageName;
   }

   public String getXhtmlFileName() {
      return xhtmlFileName;
   }

   public void setXhtmlFileName(String xhtmlFileName) {
      this.xhtmlFileName = xhtmlFileName;
   }

   public String getFilePickerAction() {
      return filePickerAction;
   }

   public void setFilePickerAction(String filePickerAction) {
      this.filePickerAction = filePickerAction;
   }

   public int getGlobalState() {
      return globalState;
   }

   public void setGlobalState(int globalState) {
      this.globalState = globalState;
   }
}
