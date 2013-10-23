/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.3/common/api/src/java/org/theospi/portfolio/review/model/Review.java $
* $Id: Review.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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
package org.theospi.portfolio.review.model;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.theospi.portfolio.shared.model.Node;

public class Review extends IdentifiableObject {
   
   public static final int REFLECTION_TYPE = 0;
   public static final int EVALUATION_TYPE = 1;
   public static final int FEEDBACK_TYPE = 2;

   private String siteId; 
   private String parent; // e.g. pageId
   private String deviceId; // e.g. form type
   private String itemId; // (optional) review related to a specific item
   private int type;  // reflection, evaluation or feedback
   private Id reviewContent;
   transient private Node reviewContentNode;
   
   private boolean newObject = false;
   
   public Review() {}
   
   public Review(Id id, String description, String siteId) {
      this.siteId = siteId;
      setId(id);
      newObject = true;

   }
   
   
   /**
    * @return Returns the reviewContent.
    */
   public Id getReviewContent() {
      return reviewContent;
   }
   /**
    * @param reviewContent The reviewContent to set.
    */
   public void setReviewContent(Id reviewContent) {
      this.reviewContent = reviewContent;
   }
   /**
    * @return Returns the reviewDevice.
    */
   public String getParent() {
      return parent;
   }
   /**
    * @param reviewDevice The reviewDevice to set.
    */
   public void setParent(String parent) {
      this.parent = parent;
   }
   /**
    * @return Returns the newObject.
    */
   public boolean isNewObject() {
      return newObject;
   }
   /**
    * @param newObject The newObject to set.
    */
   public void setNewObject(boolean newObject) {
      this.newObject = newObject;
   }

   /**
    * @return Returns the siteId.
    */
   public String getSiteId() {
      return siteId;
   }


   /**
    * @param siteId The siteId to set.
    */
   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }

   /**
    * @return Returns the deviceId.
    */
   public String getDeviceId() {
      return deviceId;
   }

   /**
    * @param deviceId The deviceId to set.
    */
   public void setDeviceId(String deviceId) {
      this.deviceId = deviceId;
   }

   /**
    * @return Returns the itemId 
    * (e.g. feedback may be related to specific item)
    */
   public String getItemId() {
      return itemId;
   }

   /**
    * @param itemId The itemId to set.
    * (e.g. feedback may be related to specific item)
    */
   public void setItemId(String itemId) {
      this.itemId = itemId;
   }

   /**
    * @return Returns the type.
    */
   public int getType() {
      return type;
   }

   /**
    * @param type The type to set.
    */
   public void setType(int type) {
      this.type = type;
   }

   /**
    * @return Returns the reviewContentNode.
    */
   public Node getReviewContentNode() {
      return reviewContentNode;
   }

   /**
    * @param reviewContentNode The reviewContentNode to set.
    */
   public void setReviewContentNode(Node reviewContentNode) {
      this.reviewContentNode = reviewContentNode;
   }

}
