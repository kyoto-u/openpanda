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

package org.theospi.portfolio.assignment;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.assignment.api.Assignment;
import org.sakaiproject.assignment.cover.AssignmentService;

public class AssignmentHelper 
{
   public static final String WIZARD_PAGE_ASSIGNMENTS =
      "org.theospi.portfolio.assignment.wizPageDefList";
      
   public static final String WIZARD_PAGE_CONTEXT =
      "org.theospi.portfolio.assignment.wizPageContext";
      
   public static final String SEPARATOR = Entity.SEPARATOR;
      
   protected static Log log = LogFactory.getLog("org.theospi.portfolio.assignment.AssignmentHelper");

   /**
    ** Parse list of attachments (reference ids) and return collection of
    ** assignments corresponding to reference ids
    **/
   public static ArrayList<Assignment> getSelectedAssignments( List attachments ) 
   {
      ArrayList assignments = new ArrayList();
      for ( Iterator it = attachments.iterator(); it.hasNext();) 
      {
         String artifactId = (String)it.next();
         Assignment thisAssignment = getAssignment(artifactId);
         if ( thisAssignment != null )
            assignments.add( thisAssignment );
      }
      
      return assignments;
   }
   
   /**
    ** Parse assignment references and remove assignments from other sites
    **/
   public static List filterAssignmentsBySite( List attachments, String siteId )
   {
      ArrayList<Assignment> assignments = getSelectedAssignments( attachments );
      for ( int i=0; i<assignments.size(); i++ )
      {
         if ( !siteId.equals( assignments.get(i).getContext() ) )
              attachments.remove(i);
      } 
      return attachments;
   }
   
   /**
    ** Parse reference and return associated Assignment if it
    ** is a valid assignment reference. Otherwise return null.
    **/
   public static Assignment getAssignment( String ref ) 
   {
      Assignment assignment = null;
      try
      {
         if ( ! ref.startsWith( AssignmentService.REFERENCE_ROOT) )
            return null;
         String assignId = ref.split(Entity.SEPARATOR)[2];
         assignment = AssignmentService.getAssignment(assignId);
      }
      catch (Exception e)
      {
         log.debug(".getAssignment: Invalid assignment reference: " + ref );
      }
      
      return assignment;
   }
   
   /**
    ** Return reference string for specified assignment id
    **/
   public static String getReference( String assignmentId ) 
   {
      return AssignmentService.REFERENCE_ROOT + Entity.SEPARATOR + assignmentId;
   }
   
   /**
    ** Join a list of assignments into one string separated by SEPARATOR
    **/
   public static String joinAssignmentList( List<Assignment> assignments )
   {
      StringBuilder assignBuf = new StringBuilder("");
      for ( Iterator it=assignments.iterator(); it.hasNext(); ) 
      {
         Assignment assign = (Assignment)it.next();
         if ( assignBuf.length() > 0 )
            assignBuf.append( AssignmentHelper.SEPARATOR );
         assignBuf.append( assign.getId() );
      }
      
      return assignBuf.toString();
   }
   
   /**
    ** Join a list of assignments ids into one string separated by SEPARATOR
    **/
   public static String joinAssignmentIdList( List<String> assignments )
   {
      StringBuilder assignBuf = new StringBuilder("");
      for ( Iterator it=assignments.iterator(); it.hasNext(); ) 
      {
         String assignId = (String)it.next();
         if ( assignBuf.length() > 0 )
            assignBuf.append( AssignmentHelper.SEPARATOR );
         assignBuf.append( assignId );
      }
      
      return assignBuf.toString();
   }
   
   /**
    ** Split a string of assignments ids separated by SEPARATOR into a list
    **/
   public static ArrayList<String> splitAssignmentIdList( String assignments )
   {
      String[] assignArray = assignments.split(AssignmentHelper.SEPARATOR);
      ArrayList assignList = new ArrayList();
      for ( int i=0; i<assignArray.length; i++ )
      {
         assignList.add( assignArray[i] ); 
      }
      
      return assignList;
   }

}
