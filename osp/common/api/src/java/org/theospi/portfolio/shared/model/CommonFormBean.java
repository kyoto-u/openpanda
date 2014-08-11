/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.1/common/api/src/java/org/theospi/portfolio/shared/model/CommonFormBean.java $
* $Id: CommonFormBean.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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

import java.util.Comparator;
import java.util.Date;

public class CommonFormBean {
   
   private String id;
   private String name;
   private String type;
   private String owner;
   private Date modifiedDate;
   
   public static Comparator beanComparator;
   static {
    beanComparator = new Comparator() {
         public int compare(Object o1, Object o2) {
                return ((CommonFormBean)o1).getName().toLowerCase().compareTo(
                      ((CommonFormBean)o2).getName().toLowerCase());
         }
        };
   }
   
   public CommonFormBean() {}
   
   public CommonFormBean(String id, String name, String type, String owner, Date modifiedDate) {
      this.id = id;
      this.name = name;
      this.type = type;
      this.owner = owner;
      this.modifiedDate = modifiedDate;
   }
   
   public String getId() {
      return id;
   }
   public void setId(String id) {
      this.id = id;
   }
   public String getName() {
      return name;
   }
   public void setName(String name) {
      this.name = name;
   }
   public String getType() {
      return type;
   }
   public void setType(String type) {
      this.type = type;
   }
   public String getOwner() {
      return owner;
   }
   public void setOwner(String owner) {
      this.owner = owner;
   }
   public Date getModifiedDate() {
      return modifiedDate;
   }
   public void setModifiedDate(Date modifiedDate) {
      this.modifiedDate = modifiedDate;
   }
   
   

}
