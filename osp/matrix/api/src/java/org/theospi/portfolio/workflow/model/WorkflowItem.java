/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.3/matrix/api/src/java/org/theospi/portfolio/workflow/model/WorkflowItem.java $
* $Id: WorkflowItem.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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
package org.theospi.portfolio.workflow.model;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;


public class WorkflowItem extends IdentifiableObject {

   public final static int NOTIFICATION_WORKFLOW = 0;
   public final static int STATUS_CHANGE_WORKFLOW = 1;
   public final static int CONTENT_LOCKING_WORKFLOW = 2;
   
   public final static String CONTENT_LOCKING_LOCK = "LOCK";
   public final static String CONTENT_LOCKING_UNLOCK = "UNLOCK";
   
   
   private int actionType;
   private Id actionObjectId;
   private String actionValue;
   private Workflow workflow;
   
   public WorkflowItem() {;}
   
   public WorkflowItem(int actionType, Id actionObjectId, String actionValue) {
      this.actionType = actionType;
      this.actionObjectId = actionObjectId;
      this.actionValue = actionValue;
   }
   
   /**
    * @return Returns the action.
    */
   public int getActionType() {
      return actionType;
   }
   /**
    * @param action The action to set.
    */
   public void setActionType(int actionType) {
      this.actionType = actionType;
   }
   /**
    * @return Returns the actionObjectId.
    */
   public Id getActionObjectId() {
      return actionObjectId;
   }
   /**
    * @param actionObjectId The actionObjectId to set.
    */
   public void setActionObjectId(Id actionObjectId) {
      this.actionObjectId = actionObjectId;
   }
   /**
    * @return Returns the actionValue.
    */
   public String getActionValue() {
      return actionValue;
   }
   /**
    * @param actionValue The actionValue to set.
    */
   public void setActionValue(String actionValue) {
      this.actionValue = actionValue;
   }
   /**
    * @return Returns the workflow.
    */
   public Workflow getWorkflow() {
      return workflow;
   }
   /**
    * @param workflow The workflow to set.
    */
   public void setWorkflow(Workflow workflow) {
      this.workflow = workflow;
   }
   
   public boolean equals(Object in) {
      // TODO Auto-generated method stub
      //return super.equals(in);
      
      if (this == in) return true;
      if (in == null && this == null) return true;
      if (in == null && this != null) return false;
      if (this == null && in != null) return false;
      if (this.getId() == null && ((WorkflowItem)in).getId() != null) return false;
      if (this.getId() != null && ((WorkflowItem)in).getId() == null) return false;
      if (this.getId() == null && ((WorkflowItem)in).getId() == null && 
            !this.getWorkflow().equals(((WorkflowItem)in).getWorkflow())) return false;
      if (!this.getActionObjectId().equals(((WorkflowItem)in).getActionObjectId())) return false;
      if (this.getActionType() != ((WorkflowItem)in).getActionType()) return false;
      if (!this.getActionValue().equals(((WorkflowItem)in).getActionValue())) return false;
      return this.getId().equals(((WorkflowItem)in).getId());
      
   }
   
}
