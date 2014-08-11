/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/MatrixFormBean.java $
* $Id:MatrixFormBean.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.matrix.control;

import java.util.List;

import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.Matrix;

/**
 * @author apple
 */
public class MatrixFormBean {

   private String action;
   private Id nodeId;
   private Id cellId;
   private Matrix matrix;
   private List criteria;
   private String[] selectedCriteria;


   /**
    * @return Returns the action.
    */
   public String getAction() {
      return action;
   }

   /**
    * @param action The action to set.
    */
   public void setAction(String action) {
      this.action = action;
   }

   /**
    * @return Returns the matrix.
    */
   public Matrix getMatrix() {
      return matrix;
   }

   /**
    * @param matrix The matrix to set.
    */
   public void setMatrix(Matrix matrix) {
      this.matrix = matrix;
   }

   /**
    * @return Returns the nodeId.
    */
   public Id getNodeId() {
      return nodeId;
   }

   /**
    * @param nodeId The nodeId to set.
    */
   public void setNodeId(Id nodeId) {
      this.nodeId = nodeId;
   }

   /**
    * @return Returns the criteria.
    */
   public List getCriteria() {
      return criteria;
   }

   /**
    * @param criteria The criteria to set.
    */
   public void setCriteria(List criteria) {
      this.criteria = criteria;
   }

   /**
    * @return Returns the cellId.
    */
   public Id getCellId() {
      return cellId;
   }

   /**
    * @param cellId The cellId to set.
    */
   public void setCellId(Id cellId) {
      this.cellId = cellId;
   }

   /**
    * @return Returns the selectedCriteria.
    */
   public String[] getSelectedCriteria() {
      return selectedCriteria;
   }

   /**
    * @param selectedCriteria The selectedCriteria to set.
    */
   public void setSelectedCriteria(String[] selectedCriteria) {
      this.selectedCriteria = selectedCriteria;
   }

   /**
    * @param list
    */
   public void setSelectedCriteria(List list) {
      if (list == null) return;
      int size = list.size();
      if (size == 0) return;
      String[] result = new String[size];
      for (int i = 0; i < size; i++) {
         result[i] = ((Criterion) list.get(i)).getId().getValue();
      }
      setSelectedCriteria(result);
   }

}
