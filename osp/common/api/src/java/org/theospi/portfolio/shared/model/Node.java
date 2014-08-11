
/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api/src/java/org/theospi/portfolio/shared/model/Node.java $
* $Id:Node.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.shared.model;

import java.io.InputStream;

import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.metaobj.shared.FormHelper;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.MimeType;

public class Node {

   private Id id;
   private String name;
   private String displayName;
   private TechnicalMetadata technicalMetadata;
   private MimeType mimeType;
   private String externalUri;
   private String fileType;
   private ContentResource resource;
   private boolean hasCopyright = false;
   private boolean locked = false;

   public Node(Id id, ContentResource resource, Agent owner) {
      createNode( id, resource, owner );
   }
   
   public Node(Id id, ContentResource resource, Agent owner, boolean locked) {
      createNode( id, resource, owner );
      this.locked = locked;
   }
   
   /**
    * This constructor will override the "normal" externalUri by appending with a decorator
    * 	<code>FormHelper.URL_DECORATION + "=" + decoration</code>
    * @param id
    * @param resource
    * @param owner
    * @param locked
    * @param decoration
    */
   public Node(Id id, ContentResource resource, Agent owner, boolean locked, String decoration) {
	      createNode( id, resource, owner );
	      this.locked = locked;
	      externalUri = externalUri + "?" + FormHelper.URL_DECORATION + "=" + decoration;
	   }
   
   private void createNode(Id id, ContentResource resource, Agent owner) {
      this.resource = resource;
      this.id = id;
      name = resource.getProperties().getProperty(
            resource.getProperties().getNamePropDisplayName());
      displayName = name;
      locked = false;
      
      //check for copyright
      hasCopyright = Boolean.getBoolean(resource.getProperties().getProperty(
            resource.getProperties().getNamePropCopyrightAlert()));
      
      externalUri = resource.getUrl();
      mimeType = new MimeType(resource.getContentType());
      String propName = resource.getProperties().getNamePropStructObjType();
      String saType = resource.getProperties().getProperty(propName);
      fileType = (saType != null && !saType.equals("")) ? saType : "fileArtifact"; 
         
      setTechnicalMetadata(new TechnicalMetadata(id, resource, owner));
   }
   
   /**
    * @return Returns the externalUri.
    */
   public String getExternalUri() {
      return externalUri;
   }
   
   /**
    * @return Returns the externalUri.
    */
   public String getFixedExternalUri() {
      return externalUri.replaceAll(" ", "%20");
   }



   /**
    * @param externalUri The externalUri to set.
    */
   public void setExternalUri(String externalUri) {
      this.externalUri = externalUri;
   }



   /**
    * @return Returns the mimeType.
    */
   public MimeType getMimeType() {
      return mimeType;
   }



   /**
    * @param mimeType The mimeType to set.
    */
   public void setMimeType(MimeType mimeType) {
      this.mimeType = mimeType;
   }



   /**
    * @return Returns the name.
    */
   public String getName() {
      return name;
   }



   /**
    * @param name The name to set.
    */
   public void setName(String name) {
      this.name = name;
   }



   /**
    * @return Returns the displayName.
    */
   public String getDisplayName() {
      return displayName;
   }
   /**
    * @param displayName The displayName to set.
    */
   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }
   /**
    * @return Returns the technicalMetadata.
    */
   public TechnicalMetadata getTechnicalMetadata() {
      return technicalMetadata;
   }



   /**
    * @param technicalMetadata The technicalMetadata to set.
    */
   public void setTechnicalMetadata(TechnicalMetadata technicalMetadata) {
      this.technicalMetadata = technicalMetadata;
   }

   public Id getId() {
      return id;
   }

   public void setId(Id id) {
      this.id = id;
   }

   public InputStream getInputStream() {
      try {
         return resource.streamContent();
      }
      catch (ServerOverloadException e) {
         throw new RuntimeException(e);
      }
   }
   
   public ContentResource getResource() {
      return resource;
   }

   public String getFileType() {
      return fileType;
   }

   public void setFileType(String fileType) {
      this.fileType = fileType;
   }

   public boolean isHasCopyright() {
      return hasCopyright;
   }

   public void setHasCopyright(boolean hasCopyright) {
      this.hasCopyright = hasCopyright;
   }

   public boolean getIsLocked() {
      return locked;
   }

   public void setIsLocked(boolean locked) {
      this.locked = locked;
   }
}
