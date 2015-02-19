/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.4/common/api/src/java/org/theospi/portfolio/style/model/Style.java $
* $Id: Style.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2006, 2008 The Sakai Foundation
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

package org.theospi.portfolio.style.model;

import java.util.Date;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

public class Style extends IdentifiableObject {
   
   private String name;
   private String description;
   private Id styleFile;
   private Agent owner;
   private Date created = new Date();
   private Date modified = new Date();
   private String siteId;
   private String styleHash;
   
   transient private String styleFileName;
   transient private String filePickerAction;
   transient private boolean validate = true;
   transient private String nodeRef;
   
   public static final int STATE_UNPUBLISHED = 0;
   public static final int STATE_WAITING_APPROVAL = 1;
   public static final int STATE_PUBLISHED = 2;


   /**
    * should be one of the following states
    *
    * unpublished -> waiting for approval-> active
    */
   private int globalState;
   
   
   
   public String getDescription() {
      return description;
   }
   public void setDescription(String description) {
      this.description = description;
   }
   public Id getStyleFile() {
      return styleFile;
   }
   public void setStyleFile(Id styleFile) {
      this.styleFile = styleFile;
   }
   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   public Date getCreated() {
      return created;
   }
   public void setCreated(Date created) {
      this.created = created;
   }
   public int getGlobalState() {
      return globalState;
   }
   public void setGlobalState(int globalState) {
      this.globalState = globalState;
   }
   public Date getModified() {
      return modified;
   }
   public void setModified(Date modified) {
      this.modified = modified;
   }
   public Agent getOwner() {
      return owner;
   }
   public void setOwner(Agent owner) {
      this.owner = owner;
   }
   public String getSiteId() {
      return siteId;
   }
   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }
   public String getStyleFileName() {
      return styleFileName;
   }
   public void setStyleFileName(String styleFileName) {
      this.styleFileName = styleFileName;
   }
   public String getFilePickerAction() {
      return filePickerAction;
   }
   public void setFilePickerAction(String filePickerAction) {
      this.filePickerAction = filePickerAction;
   }
   public boolean isValidate() {
      return validate;
   }
   public void setValidate(boolean validate) {
      this.validate = validate;
   }
   public String getNodeRef() {
      return nodeRef;
   }
   public void setNodeRef(String nodeRef) {
      this.nodeRef = nodeRef;
   }
   /**
    * @return the styleHash
    */
   public String getStyleHash() {
      return styleHash;
   }
   /**
    * @param styleHash the styleHash to set
    */
   public void setStyleHash(String styleHash) {
      this.styleHash = styleHash;
   }

}
