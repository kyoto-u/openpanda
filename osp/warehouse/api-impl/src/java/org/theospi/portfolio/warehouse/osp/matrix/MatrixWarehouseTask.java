/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.1/warehouse/api-impl/src/java/org/theospi/portfolio/warehouse/osp/matrix/MatrixWarehouseTask.java $
* $Id: MatrixWarehouseTask.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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
package org.theospi.portfolio.warehouse.osp.matrix;

import java.util.Collection;

import org.theospi.portfolio.matrix.MatrixManager;
import org.sakaiproject.warehouse.impl.BaseWarehouseTask;


class MatrixWarehouseTask extends BaseWarehouseTask {

   private MatrixManager matrixManager;
   
   protected Collection getItems() {
      Collection matrices = matrixManager.getMatricesForWarehousing();
      return matrices;
      
//      Collection matrices = matrixManager.getMatrixTools();
//      
//      for(Iterator i = matrices.iterator(); i.hasNext(); ) {
//         MatrixTool tool = (MatrixTool)i.next();
//         
//         tool.getId();
//         tool.setMatrix(new HashSet(matrixManager.getMatrices(tool.getId(), null)));
//         Collection mats = tool.getMatrix();
//         
//         for(Iterator ii = mats.iterator(); ii.hasNext(); ) {
//            Matrix mat = (Matrix)ii.next();
//            
//            mat.getId();
//            mat.setMatrixTool(tool);
//         }
//      }
//      
   }

   public MatrixManager getMatrixManager() {
      return matrixManager;
   }

   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }
}