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
package org.theospi.portfolio.assignment.tool;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.theospi.utils.mvc.impl.servlet.AbstractFormController;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.assignment.api.AssignmentService;
import org.sakaiproject.assignment.api.Assignment;
import org.theospi.portfolio.assignment.AssignmentHelper;

import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

public class ListAssignmentController extends AbstractFormController implements Controller 
{
   protected final Log logger = LogFactory.getLog(getClass());
   private ListScrollIndexer listScrollIndexer;
   private AssignmentService assignmentService;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
                                     Map application, Errors errors) 
   {
      Hashtable model = new Hashtable();
      String context = (String)session.get(AssignmentHelper.WIZARD_PAGE_CONTEXT);
      List allAssignments = assignmentService.getListAssignmentsForContext(context); 
      String doSave = (String)request.get("_save");
      String doCancel = (String)request.get("_cancel");
      
      String selectAssignments = (String)session.get(AssignmentHelper.WIZARD_PAGE_ASSIGNMENTS);
      
      if ( doCancel != null )
      {
         session.remove(AssignmentHelper.WIZARD_PAGE_ASSIGNMENTS);
         return new ModelAndView("done");
      }
      else if ( doSave != null )
      {
         ArrayList newAssignments = new ArrayList();
         for ( Iterator it=allAssignments.iterator(); it.hasNext(); ) 
         {
            Assignment assign = (Assignment)it.next();
            if ( request.get(assign.getId()) != null )
               newAssignments.add( assign.getId() );
         }
         session.put(AssignmentHelper.WIZARD_PAGE_ASSIGNMENTS, 
                     AssignmentHelper.joinAssignmentIdList(newAssignments));
         return new ModelAndView("done");
      }
      else
      {
         ArrayList assignBeans = new ArrayList();
         ArrayList selectAssignList = new ArrayList();
         if (selectAssignments != null && selectAssignments.length() > 0){
            selectAssignList = AssignmentHelper.splitAssignmentIdList( selectAssignments );
         }
         
         for ( Iterator it=allAssignments.iterator(); it.hasNext(); ) 
         {
            Assignment assign = (Assignment)it.next();
            boolean selected = false;
            if ( selectAssignList.size() > 0 && selectAssignList.contains(assign.getId()) ) {
               selected = true;
            }
            assignBeans.add( new AssignmentBean( assign, selected ) );
         }
         
         model.put("assignments", 
                   getListScrollIndexer().indexList(request, model, assignBeans) );
   
         return new ModelAndView("success", model);
      }

   }

   public ListScrollIndexer getListScrollIndexer() 
   {
      return listScrollIndexer;
   }

   public void setListScrollIndexer(ListScrollIndexer listScrollIndexer) 
   {
      this.listScrollIndexer = listScrollIndexer;
   }

   public AssignmentService getAssignmentService() 
   {
      return assignmentService;
   }

   public void setAssignmentService(AssignmentService assignmentService) 
   {
      this.assignmentService = assignmentService;
   }
   
   public class AssignmentBean 
   {
      Assignment assignment = null;
      boolean selected = false;
   
      public AssignmentBean( Assignment assignment, boolean selected ) 
      {
         this.selected = selected;
         this.assignment = assignment;
      }
      
      public boolean getSelected() 
      {
         return selected;
      }
      
      public void setSelected( boolean selected ) 
      {
         this.selected = selected;
      }
      
      public Assignment getAssignment() 
      {
         return assignment;
      }
   }
}
