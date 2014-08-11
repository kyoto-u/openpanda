/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/CellFormBean.java $
* $Id:CellFormBean.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.matrix.control;

import java.util.List;
import java.util.Set;

import org.theospi.portfolio.matrix.model.Cell;

/**
 * @author chmaurer
 */
public class CellFormBean {

   private Cell cell;
   private List nodes;
   private List assignments;
   private String[] selectedArtifacts;
   private Set taggableItems;

   private List reflections;
   private List reviews;
   private List evaluations;

   /**
    * @return
    */
   public List getNodes() {
      return nodes;
   }

   /**
    * @return
    */
   public Cell getCell() {
      return cell;
   }

   /**
    * @param list
    */
   public void setNodes(List list) {
      nodes = list;
   }

   /**
    * @param cell
    */
   public void setCell(Cell cell) {
      this.cell = cell;
   }
    
    /**
     * @return Returns the selectedArtifacts.
     */
    public String[] getSelectedArtifacts() {
        return selectedArtifacts;
    }
    
    /**
     * @param selectedArtifacts The selectedArtifacts to set.
     */
    public void setSelectedArtifacts(String[] selectedArtifacts) {
        this.selectedArtifacts = selectedArtifacts;
    }
    
   /**
    * @param boolean
    */
   public void setAssignments(List assignments) {
      this.assignments = assignments;
   }

   /**
    * @param cell
    */
   public List getAssignments() {
      return assignments;
   }
    
   public Set getTaggableItems() {
	   return taggableItems;
   }

   public void setTaggableItems(Set taggableItems) {
	   this.taggableItems = taggableItems;
   }

   public List getReflections() {
       return reflections;
   }

   public void setReflections(List reflections) {
       this.reflections = reflections;
   }

   public List getReviews() {
       return reviews;
   }

   public void setReviews(List reviews) {
       this.reviews = reviews;
   }

   public List getEvaluations() {
       return evaluations;
   }

   public void setEvaluations(List evaluations) {
       this.evaluations = evaluations;
   }

}
