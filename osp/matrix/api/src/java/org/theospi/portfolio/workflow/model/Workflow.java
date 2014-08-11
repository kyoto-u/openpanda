/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/branches/sakai-2.8.x/matrix/api/src/java/org/theospi/portfolio/workflow/model/Workflow.java $
* $Id: Workflow.java 73575 2010-02-16 20:55:30Z botimer@umich.edu $
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
package org.theospi.portfolio.workflow.model;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.theospi.portfolio.shared.model.ObjectWithWorkflow;

public class Workflow extends IdentifiableObject {

   private String title;
   private Set<WorkflowItem> items = new HashSet<WorkflowItem>();
   private boolean newObject = false;
   private ObjectWithWorkflow parentObject;
   
   public Workflow() {      
   }
   
   public Workflow(String title, ObjectWithWorkflow parentObject) {
      this.title = title;
      this.parentObject = parentObject;
   }
   
   /**
    * @return Returns the title.
    */
   public String getTitle() {
      return title;
   }
   /**
    * @param title The title to set.
    */
   public void setTitle(String title) {
      this.title = title;
   }
   /**
    * @return Returns the items.
    */
   public Set<WorkflowItem> getItems() {
      return items;
   }
   /**
    * @param items The items to set.
    */
   public void setItems(Set<WorkflowItem> items) {
      this.items = items;
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
   
   public void add(WorkflowItem item) {
      item.setWorkflow(this);
      getItems().add(item);
   }

   /* (non-Javadoc)
    * @see org.sakaiproject.metaobj.shared.model.IdentifiableObject#equals(java.lang.Object)
    */
   public boolean equals(Object in) {
      // TODO Auto-generated method stub
      //return super.equals(in);
      
      if (this == in) return true;
      if (in == null && this == null) return true;
      if (in == null && this != null) return false;
      if (this == null && in != null) return false;
      if (this.getId() == null && ((Workflow)in).getId() != null) return false;
      if (this.getId() != null && ((Workflow)in).getId() == null) return false;
      if (this.getId() == null && ((Workflow)in).getId() == null && 
            !this.getTitle().equals(((Workflow)in).getTitle())) return false;
      return this.getId().equals(((Workflow)in).getId());
      
   }

   public int hashCode() {
      if (this.getId() == null && this.getTitle() == null) return 370404079;
      if (this.getTitle() == null) return this.getId().hashCode();
      if (this.getId() == null) return 13 * this.getTitle().hashCode();
      return this.getId().hashCode() + 13 * this.getTitle().hashCode();
   }

   /**
    * @return Returns the parentObject.
    */
   public ObjectWithWorkflow getParentObject() {
      return parentObject;
   }

   /**
    * @param parentObject The parentObject to set.
    */
   public void setParentObject(ObjectWithWorkflow parentObject) {
      this.parentObject = parentObject;
   }
   
   
   public static class WorkflowComparator implements Comparator {
      public int compare(Object o1, Object o2) {
         return ((Workflow)o1).getTitle().toLowerCase().compareTo(
               ((Workflow)o2).getTitle().toLowerCase());
      }
   }
   
   public static WorkflowComparator getComparator() {
      return new WorkflowComparator();
   }

}
