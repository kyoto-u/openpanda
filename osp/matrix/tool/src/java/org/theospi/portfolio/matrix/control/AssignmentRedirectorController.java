/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
 * Copyright (c) 2007, 2008 The Sakai Foundation
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

import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;

import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.assignment.api.Assignment;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.assignment.AssignmentHelper;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;

public class AssignmentRedirectorController implements LoadObjectController {

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors)
   {
      String assignPickerAction = (String) request.get("assignPickerAction");
      
      // Redirect to Assignment Picker
      if (assignPickerAction != null) 
      {
         EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage)session.get(
            EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
         ScaffoldingCell sCell = sessionBean.getScaffoldingCell();
         WizardPageDefinition pageDef = sCell.getWizardPageDefinition();
         ArrayList<Assignment> assignList = 
            AssignmentHelper.getSelectedAssignments(sCell.getWizardPageDefinition().getAttachments());
            
         String assignments = AssignmentHelper.joinAssignmentList( assignList );
         session.put(AssignmentHelper.WIZARD_PAGE_ASSIGNMENTS, assignments);
         
         String context = pageDef.getSiteId();
         session.put(AssignmentHelper.WIZARD_PAGE_CONTEXT, context);
      
         session.put("assignReturnView", request.get("assignReturnView"));
         return new ModelAndView("assignRedirector");
      }

      // Return from Assignment Picker
      session.put(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG, "true");
      String retView = (String)session.get("assignReturnView");
      session.remove("assignReturnView");
      return new ModelAndView(retView);
   }

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception 
   {
      EditedScaffoldingStorage sessionBean = (EditedScaffoldingStorage)session.get(
            EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);
      ScaffoldingCell scaffoldingCell = sessionBean.getScaffoldingCell();
      WizardPageDefinition pageDef = scaffoldingCell.getWizardPageDefinition();
      
      String assignments = (String)session.get(AssignmentHelper.WIZARD_PAGE_ASSIGNMENTS);
      session.remove(AssignmentHelper.WIZARD_PAGE_ASSIGNMENTS);
      session.remove(AssignmentHelper.WIZARD_PAGE_CONTEXT);

      // Save new assignments, if specified      
      if ( assignments != null && !assignments.equals("") )
      {
         ArrayList<String> assignList = AssignmentHelper.splitAssignmentIdList( assignments );
         for ( int i=0; i<assignList.size(); i++ )
            assignList.set( i, AssignmentHelper.getReference(assignList.get(i)) );
         pageDef.setAttachments( assignList );
      }
      // Delete all assignments, if specified
      else if ( assignments != null && assignments.equals("") )
      {
         pageDef.setAttachments( new ArrayList() );
      }
      
      return null;
   }

}
