
/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.0/matrix/tool/src/java/org/theospi/portfolio/matrix/control/PublishScaffoldingConfirmationController.java $
* $Id: PublishScaffoldingConfirmationController.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Iterator;

import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Matrix;

/**
 * @author chmaurer
 */
public class PublishScaffoldingConfirmationController implements Controller {

   MatrixManager matrixManager = null;
   IdManager idManager = null;
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
    */
   
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      String viewName = "success";
      Id id = idManager.getId((String)request.get("scaffolding_id"));
      
      Map model = new HashMap();
      model.put("scaffolding_id", id);
      
      String cancel = (String)request.get("cancel");
      String next = (String)request.get("continue");
      if (cancel != null) {
         viewName = "cancel";
      }
      else if (next != null) {
         viewName = "publish";
         
         // First delete any associated matrix data from 'preview' mode
         List matrices = getMatrixManager().getMatrices(id);
         for (Iterator matrixIt = matrices.iterator(); matrixIt.hasNext();) 
         {
            Matrix matrix = (Matrix)matrixIt.next();
            getMatrixManager().deleteMatrix(matrix.getId());
         }
         
         matrixManager.publishScaffolding(id);
      }

      return new ModelAndView(viewName, model);
   }


   public IdManager getIdManager() {
      return idManager;
   }
   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }
   public MatrixManager getMatrixManager() {
      return matrixManager;
   }
   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }
}
