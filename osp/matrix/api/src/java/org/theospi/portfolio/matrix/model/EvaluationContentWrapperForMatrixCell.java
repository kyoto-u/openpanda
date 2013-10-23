/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.3/matrix/api/src/java/org/theospi/portfolio/matrix/model/EvaluationContentWrapperForMatrixCell.java $
* $Id: EvaluationContentWrapperForMatrixCell.java 68687 2009-11-09 16:45:06Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2006, 2007, 2008 The Sakai Foundation
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

package org.theospi.portfolio.matrix.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.theospi.portfolio.shared.model.EvaluationContentWrapper;

/**
 * This class is created when looking up cells to evaluate.  Hibernate calls the constructor.
 * This is used in the listEvaluationItem
 * @author andersjb
 *
 */
public class EvaluationContentWrapperForMatrixCell extends EvaluationContentWrapper{
   
   public EvaluationContentWrapperForMatrixCell(Id id, String title, Agent owner, 
         Date submittedDate, String siteId) throws UserNotDefinedException {

      super(id, title, owner, submittedDate, siteId);
		
      Set params = new HashSet();
      if (owner != null && owner.getId() != null) {
         params.add(new ParamBean("view_user", owner.getId().getValue()));
         setUrl("openEvaluationCellRedirect");
      }
      
      setEvalType(Cell.TYPE);
      
      params.add(new ParamBean("page_id", getId().getValue()));
      params.add(new ParamBean("readOnlyMatrix", "true"));
      
      setUrlParams(params);      
   }
}
