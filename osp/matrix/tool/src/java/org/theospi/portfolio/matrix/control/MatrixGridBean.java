/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/MatrixGridBean.java $
* $Id:MatrixGridBean.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.theospi.portfolio.matrix.model.Scaffolding;

/**
 * @author chmaurer
 */
public class MatrixGridBean {

   private List columnLabels;
   private List rowLabels;
   private List matrixContents;
   private Id matrixId;
   private Scaffolding scaffolding;
   private Agent matrixOwner;

   /**
    * @return
    */
   public List getColumnLabels() {
      return columnLabels;
   }

   /**
    * @return
    */
   public List getMatrixContents() {
      return matrixContents;
   }

   /**
    * @return
    */
   public List getRowLabels() {
      return rowLabels;
   }

   /**
    * @param list
    */
   public void setColumnLabels(List list) {
      columnLabels = list;
   }

   /**
    * @param list
    */
   public void setMatrixContents(List list) {
      matrixContents = list;
   }

   /**
    * @param list
    */
   public void setRowLabels(List list) {
      rowLabels = list;
   }

   /**
    * @return Returns the matrix.
    */
   public Id getMatrixId() {
      return matrixId;
   }

   /**
    * @param matrix The matrix to set.
    */
   public void setMatrixId(Id matrixId) {
      this.matrixId = matrixId;
   }

   /**
    * @return Returns the scaffolding.
    */
   public Scaffolding getScaffolding() {
      return scaffolding;
   }
   /**
    * @param scaffolding The scaffolding to set.
    */
   public void setScaffolding(Scaffolding scaffolding) {
      this.scaffolding = scaffolding;
   }

   /**
    * @return Returns the matrixOwner.
    */
   public Agent getMatrixOwner() {
      return matrixOwner;
   }
   /**
    * @param matrixOwner The matrixOwner to set.
    */
   public void setMatrixOwner(Agent matrixOwner) {
      this.matrixOwner = matrixOwner;
   }
}
