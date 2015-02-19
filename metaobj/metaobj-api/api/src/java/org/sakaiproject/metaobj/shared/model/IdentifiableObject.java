/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/model/IdentifiableObject.java $
 * $Id: IdentifiableObject.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * any derived class should use the generator in the hibernate xml definitian:
 *     org.sakaiproject.metaobj.shared.IdentifiableIdGenerator
 * This way the newId field will be supported when creating 
 * new objects with predetermined ids.
 * 
 * User: jbush
 * Date: May 15, 2004
 * Time: 1:55:47 PM
 * To change this template use File | Settings | File Templates.
 */
abstract public class IdentifiableObject {
   private Id id;
   private Id newId;
   protected final Log logger = LogFactory.getLog(this.getClass());

   public boolean equals(Object in) {
      if (this == in) {
         return true;
      }
      if (in == null && this == null) {
         return true;
      }
      if (in == null && this != null) {
         return false;
      }
      if (this == null && in != null) {
         return false;
      }
      if (!this.getClass().isAssignableFrom(in.getClass())) {
         return false;
      }
      if (this.getId() == null && ((IdentifiableObject) in).getId() == null) {
         return true;
      }
      if (this.getId() == null || ((IdentifiableObject) in).getId() == null) {
         return false;
      }
      return this.getId().equals(((IdentifiableObject) in).getId());
   }

   public int hashCode() {
      return (id != null ? id.hashCode() : 0);
   }

   /**
    * returns the id of the object stored in the database.  if the object is new and hasn't
    * been saved to the database then it may have an id...  check getNewId.
    * @return Id
    */
   public Id getId() {
      return id;
   }

   /**
    * sets the id of the object.  If the object is new then this parameter will be ignored
    * and a new id will be generated.  Use setNewId to set the id of a new object.
    * 
    * @param id
    */
   public void setId(Id id) {
      this.id = id;
   }

   /**
    * If this object is new and we want it to have a specific id then this is set before
    * the object is saved.
    * @return Id
    */
   public Id getNewId() {
      return newId;
   }

   /**
    * if create an identifiable object with a specific id, then this is what you set.
    * The id generator won't create a new id but will use this field as the id
    * @param newId
    */
   public void setNewId(Id newId) {
      this.newId = newId;
   }

   /**
    * this returns the effective id.  First it checks for the real id,
    * then if it's not good then it returns the newId.
    * @return Id
    */
   public Id getVirtualId() {
      if(id != null && id.getValue() != null && id.getValue().length() > 0)
         return id;
      return newId;
   }
}
