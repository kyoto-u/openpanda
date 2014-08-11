/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/wizard/api/src/java/org/theospi/portfolio/wizard/model/CompletedWizard.java $
* $Id:CompletedWizard.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.wizard.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 21, 2006
 * Time: 3:16:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompletedWizard extends IdentifiableObject implements Artifact {

   private Wizard wizard;
   private CompletedWizardCategory rootCategory;
   private Agent owner;
   private Date created;
   private Date lastVisited;
   private String status;
   private ReadableObjectHome home;

   private transient List reflections = new ArrayList();
   private transient List evaluations = new ArrayList();
   private transient List feedback = new ArrayList();

   public final static String TYPE = "wizard_type";
   public final static String PROCESS_TYPE_KEY = "completed_wizard_id";
   
   public CompletedWizard() {
   }

   public CompletedWizard(Wizard wizard, Agent owner) {
      this.wizard = wizard;
      this.owner = owner;
      setStatus(MatrixFunctionConstants.READY_STATUS);
      setCreated(new Date());
      setLastVisited(new Date());
      setRootCategory(new CompletedWizardCategory(this, wizard.getRootCategory()));
      getRootCategory().setExpanded(true); // root should alway be expanded
   }

   public Wizard getWizard() {
      return wizard;
   }

   public void setWizard(Wizard wizard) {
      this.wizard = wizard;
   }

   public Agent getOwner() {
      return owner;
   }

   public void setOwner(Agent owner) {
      this.owner = owner;
   }

   public Date getCreated() {
      return created;
   }

   public void setCreated(Date created) {
      this.created = created;
   }

   public Date getLastVisited() {
      return lastVisited;
   }

   public void setLastVisited(Date lastVisited) {
      this.lastVisited = lastVisited;
   }

   public CompletedWizardCategory getRootCategory() {
      return rootCategory;
   }

   public void setRootCategory(CompletedWizardCategory rootCategory) {
      this.rootCategory = rootCategory;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public ReadableObjectHome getHome() {
      return home;
   }

   public void setHome(ReadableObjectHome home) {
      this.home = home;
      
   }

   public String getDisplayName() {
      return wizard.getName();
   }

   /**
    * @return the evaluations
    */
   public List getEvaluations() {
      return evaluations;
   }

   /**
    * @param evaluations the evaluations to set
    */
   public void setEvaluations(List evaluations) {
      this.evaluations = evaluations;
   }

   /**
    * @return the feedback
    */
   public List getFeedback() {
      return feedback;
   }

   /**
    * @param feedback the feedback to set
    */
   public void setFeedback(List feedback) {
      this.feedback = feedback;
   }

   /**
    * @return the reflections
    */
   public List getReflections() {
      return reflections;
   }

   /**
    * @param reflections the reflections to set
    */
   public void setReflections(List reflections) {
      this.reflections = reflections;
   }



}
