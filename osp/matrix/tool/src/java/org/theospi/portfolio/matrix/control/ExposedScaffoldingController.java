/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.0/matrix/tool/src/java/org/theospi/portfolio/matrix/control/ExposedScaffoldingController.java $
* $Id: ExposedScaffoldingController.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.theospi.portfolio.matrix.control;

import java.util.Map;

import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Scaffolding;

public class ExposedScaffoldingController implements FormController {
   
   private MatrixManager matrixManager;
   private IdManager idManager = null;

   public Map referenceData(Map request, Object command, Errors errors) {
      // TODO Auto-generated method stub
      return null;
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      Id scaffoldingId = getIdManager().getId((String)request.get("scaffolding_id"));
      String expose = (String)request.get("expose");
      Scaffolding scaffolding = getMatrixManager().getScaffolding(scaffoldingId);
      if (expose.equals("true") &&
            scaffolding.getExposedPageId() == null) {
         getMatrixManager().exposeMatrixTool(scaffolding);
      }
      else if (expose.equals("false") &&
            scaffolding.getExposedPageId() != null) {
         getMatrixManager().removeExposedMatrixTool(scaffolding);
      }
      getMatrixManager().storeScaffolding(scaffolding);
      return new ModelAndView("success");
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
