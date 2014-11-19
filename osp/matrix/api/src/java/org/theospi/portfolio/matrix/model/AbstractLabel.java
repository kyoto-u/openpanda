/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.2/matrix/api/src/java/org/theospi/portfolio/matrix/model/AbstractLabel.java $
* $Id: AbstractLabel.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
package org.theospi.portfolio.matrix.model;

import java.io.Serializable;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

/**
 * @author apple
 */
public abstract class AbstractLabel extends IdentifiableObject implements Serializable, Label {
   Id id;
   String description;
   String color;
   String textColor;
   private int sequenceNumber = 0;
   private Scaffolding scaffolding;


   /**
    * @return Returns the description.
    */
   public String getDescription() {
      return description;
   }

   /**
    * @param description The description to set.
    */
   public void setDescription(String description) {
      this.description = description;
   }

   /**
    * @return Returns the id.
    */
   public Id getId() {
      return id;
   }

   /**
    * @param id The id to set.
    */
   public void setId(Id id) {
      this.id = id;
   }
   
   public String getColor() {
      return color;
   }

   public void setColor(String color) {
      this.color = color;
   }

   public String getTextColor() {
      return textColor;
   }

   public void setTextColor(String textColor) {
      this.textColor = textColor;
   }
   
   /**
    * This property is unused except for in the data warehouse.
    * it does NOT contain the sequence number of label.  To access that property
    * it is implicit in the list containing this instance.
    * @return int
    */
   public int getSequenceNumber() {
      return sequenceNumber;
   }
   /**
    * Only the datawarehouse is using this right now
    * @param sequenceNumber int
    */
   public void setSequenceNumber(int sequenceNumber) {
      this.sequenceNumber = sequenceNumber;
   }
   
   /**
    * This property is unused except for in the data warehouse.
    * @return int
    */
   public Scaffolding getScaffolding() {
      return scaffolding;
   }
   /**
    * Only the datawarehouse is using this right now
    * @param sequenceNumber int
    */
   public void setScaffolding(Scaffolding scaffolding) {
      this.scaffolding = scaffolding;
   }

   public String toString() {
      return "<(" + this.getClass().getName() + ") " + this.getDescription() + " [" + this.getId() + "]>";
   }
}
