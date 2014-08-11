/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api/src/java/org/theospi/portfolio/guidance/model/GuidanceItemAttachment.java $
* $Id:GuidanceItemAttachment.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
package org.theospi.portfolio.guidance.model;

import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.metaobj.shared.mgt.ReferenceHolder;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.sakaiproject.metaobj.shared.model.MimeType;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 11, 2005
 * Time: 12:38:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuidanceItemAttachment extends IdentifiableObject {

   private GuidanceItem item;
   private ReferenceHolder baseReference;
   private ReferenceHolder fullReference;

   public GuidanceItemAttachment() {
   }

   public GuidanceItemAttachment(GuidanceItem item, Reference baseReference, Reference fullReference) {
      this.item = item;
      this.baseReference = new ReferenceHolder(baseReference);
      this.fullReference = new ReferenceHolder(fullReference);
   }

   public GuidanceItem getItem() {
      return item;
   }

   public void setItem(GuidanceItem item) {
      this.item = item;
   }

   public ReferenceHolder getBaseReference() {
      return baseReference;
   }

   public void setBaseReference(ReferenceHolder baseReference) {
      this.baseReference = baseReference;
   }

   public void setBaseReference(Reference baseReference) {
      this.baseReference = new ReferenceHolder(baseReference);
   }

   public ReferenceHolder getFullReference() {
      return fullReference;
   }

   public void setFullReference(Reference fullReference) {
      this.fullReference = new ReferenceHolder(fullReference);
   }

   public void setFullReference(ReferenceHolder fullReference) {
      this.fullReference = fullReference;
   }

   public String getDisplayName() {
      ContentResource resource = (ContentResource)baseReference.getBase().getEntity();

      if(resource == null)
         throw new NullPointerException("the content resource is null for " 
                                        + baseReference.getBase().getReference());
      
      String displayNameProp = resource.getProperties().getNamePropDisplayName();
      return resource.getProperties().getProperty(displayNameProp);
   }

   public MimeType getMimeType() {
      ContentResource resource = (ContentResource)baseReference.getBase().getEntity();

      if(resource == null)
         throw new NullPointerException("the content resource is null for " 
                                        + baseReference.getBase().getReference());
      
      String contentTypeProp = resource.getProperties().getNamePropContentType();
      return new MimeType(resource.getProperties().getProperty(contentTypeProp));
   }

	/**
    * This function gets the content length of the resource.
    *  It also formats it into kilobytes, megabytes and gigabytes
	 *@returns String
    */
   public String getContentLength() {
      ContentResource resource = (ContentResource)baseReference.getBase().getEntity();

      String displayNameProp = resource.getProperties().getNamePropContentLength();
      String size = resource.getProperties().getProperty(displayNameProp);
      
      int length = Integer.parseInt(size);
      
      if(length < 1024*100) return (length/1024) + "." + ((length * 10 / 1024) % 10 ) + " KB";
      else if(length < 1024*1024) return (length/1024) + " KB";
      else if(length < 1024*1024*100) return (length/(1024*1024)) + "." + ((length * 10 / (1024*1024)) % 10 ) + " MB";
      else if(length < 1024*1024*1024) return (length/(1024*1024)) + " MB";
      else if(length < 1024*1024*1024*100) return (length/(1024*1024*1024)) + "." + ((length * 10 / (1024*1024*1024)) % 10 ) + " GB";
      else return (length/(1024*1024*1024)) + " GB";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof GuidanceItemAttachment)) {
         return false;
      }

      final GuidanceItemAttachment guidanceItemAttachment = (GuidanceItemAttachment) o;

      if (fullReference != null ? !fullReference.equals(guidanceItemAttachment.fullReference) : guidanceItemAttachment.fullReference != null) {
         return false;
      }
      if (item != null ? !item.equals(guidanceItemAttachment.item) : guidanceItemAttachment.item != null) {
         return false;
      }

      return true;
   }

   public int hashCode() {
      int result = 0;
      result = 29 * result + (item != null ? item.hashCode() : 0);
      result = 29 * result + (fullReference != null ? fullReference.hashCode() : 0);
      return result;
   }

}
