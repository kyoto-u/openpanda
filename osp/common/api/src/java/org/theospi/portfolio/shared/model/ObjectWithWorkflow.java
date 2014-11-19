/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.2/common/api/src/java/org/theospi/portfolio/shared/model/ObjectWithWorkflow.java $
* $Id: ObjectWithWorkflow.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
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

package org.theospi.portfolio.shared.model;

import java.util.HashSet;
import java.util.Set;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

public class ObjectWithWorkflow extends IdentifiableObject {

   private Id reflectionDevice;
   private String reflectionDeviceType;
   private Id evaluationDevice;
   private String evaluationDeviceType;
   private Id reviewDevice;
   private String reviewDeviceType;
   private Set evalWorkflows = new HashSet();
   

   /**
    * @return Returns the evalWorkflows.
    */
   public Set getEvalWorkflows() {
      return evalWorkflows;
   }

   /**
    * @param evalWorkflows The evalWorkflows to set.
    */
   public void setEvalWorkflows(Set evalWorkflows) {
      this.evalWorkflows = evalWorkflows;
   }

   /**
    * @return Returns the evaluationDevice.
    */
   public Id getEvaluationDevice() {
      return evaluationDevice;
   }

   /**
    * @param evaluationDevice The evaluationDevice to set.
    */
   public void setEvaluationDevice(Id evaluationDevice) {
      this.evaluationDevice = evaluationDevice;
   }

   /**
    * @return Returns the evaluationDeviceType.
    */
   public String getEvaluationDeviceType() {
      return evaluationDeviceType;
   }

   /**
    * @param evaluationDeviceType The evaluationDeviceType to set.
    */
   public void setEvaluationDeviceType(String evaluationDeviceType) {
      this.evaluationDeviceType = evaluationDeviceType;
   }

   /**
    * @return Returns the reflectionDevice.
    */
   public Id getReflectionDevice() {
      return reflectionDevice;
   }

   /**
    * @param reflectionDevice The reflectionDevice to set.
    */
   public void setReflectionDevice(Id reflectionDevice) {
      this.reflectionDevice = reflectionDevice;
   }

   /**
    * @return Returns the reflectionDeviceType.
    */
   public String getReflectionDeviceType() {
      return reflectionDeviceType;
   }

   /**
    * @param reflectionDeviceType The reflectionDeviceType to set.
    */
   public void setReflectionDeviceType(String reflectionDeviceType) {
      this.reflectionDeviceType = reflectionDeviceType;
   }

   /**
    * @return Returns the reviewDevice.
    */
   public Id getReviewDevice() {
      return reviewDevice;
   }

   /**
    * @param reviewDevice The reviewDevice to set.
    */
   public void setReviewDevice(Id reviewDevice) {
      this.reviewDevice = reviewDevice;
   }

   /**
    * @return Returns the reviewDeviceType.
    */
   public String getReviewDeviceType() {
      return reviewDeviceType;
   }

   /**
    * @param reviewDeviceType The reviewDeviceType to set.
    */
   public void setReviewDeviceType(String reviewDeviceType) {
      this.reviewDeviceType = reviewDeviceType;
   }
   
   
}
