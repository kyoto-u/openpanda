/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/model/Type.java $
 * $Id: Type.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import java.io.Serializable;


/**
 * Created by IntelliJ IDEA.
 * User: jbush
 * Date: Apr 29, 2004
 * Time: 11:11:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class Type implements Serializable {

   private Id id;
   private String description;
   private boolean systemOnly = false;

   public Type() {
   }

   public Type(Id id) {
      setId(id);
   }

   public Type(Id id, String description) {
      setDescription(description);
      setId(id);
   }

   public Type(Id id, String description, boolean systemOnly) {
      setId(id);
      this.description = description;
      this.systemOnly = systemOnly;
   }

   /**
    * @return Returns the id.
    */
   public Id getId() {
      return id;
   }

   public void setId(Id id) {
      this.id = id;
      if (getDescription() == null) {
         setDescription(id.getValue());
      }
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public boolean isSystemOnly() {
      return systemOnly;
   }

   public void setSystemOnly(boolean systemOnly) {
      this.systemOnly = systemOnly;
   }

}
