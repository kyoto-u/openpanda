/**********************************************************************************
* $URL : $
* $Id : $
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
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.content.cover.ContentTypeImageService;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.assignment.api.Assignment;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.assignment.cover.AssignmentService;
import org.sakaiproject.assignment.api.AssignmentSubmission;

import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.assignment.AssignmentHelper;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.WizardPage;

public class ViewAssignmentController implements FormController, LoadObjectController {

   private final Log logger = LogFactory.getLog(getClass());
   private MatrixManager matrixManager = null;
   private IdManager idManager = null;
   private SessionManager sessionManager = null;
   
   public Map referenceData(Map request, Object command, Errors errors) {

      Map model = new HashMap();
      
      try {
         AssignmentSubmission submission = 
            AssignmentService.getSubmission( (String)request.get("assign_ref") );
         model.put("submission", submission);
         
         model.put( "assignAttachments", 
                    getAttachmentBeans(submission.getAssignment().getContent().getAttachments()) );
         model.put( "submitAttachments", 
                    getAttachmentBeans(submission.getSubmittedAttachments()) );
         model.put( "feedbackAttachments", 
                    getAttachmentBeans(submission.getFeedbackAttachments()) );
      }
      catch ( Exception e ) {
         logger.error("ViewAssignmentController.referenceData ", e);
      }

      return model;
   }
   
   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception 
   {
      String pageId = (String) request.get("page_id");
      CellFormBean cellBean = (CellFormBean) incomingModel;
		WizardPage page = (WizardPage) session
				.get(WizardPageHelper.WIZARD_PAGE);

      // Check if the cell has been removed, which can happen if:
      // (1) user views matrix
      // (2) owner removes column or row (the code verifies that no one has
      // modified the matrix)
      // (3) user selects a cell that has just been removed with the column or
      // row
      try {
         if ( page != null ) { // wizard page
            Cell cell = getMatrixManager().createCellWrapper(page);
            cellBean.setCell(cell);
         }
         else {   // matrix cell
            Id id = getIdManager().getId(pageId);
            Cell cell = matrixManager.getCellFromPage(id);
            cellBean.setCell(cell);
         }
      }
      catch (Exception e) {
         logger.error("Error with cell: " + pageId + " " + e.toString());
         // tbd how to report error back to user?
      }

      return cellBean;
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, 
                                     Map application, Errors errors)
   {
      String pageId     = (String) request.get("page_id");
      CellFormBean cellBean = (CellFormBean) requestModel;
      Cell cell = cellBean.getCell();

      // Check for cell being deleted while user was attempting to view
      if (cell == null) 
         return new ModelAndView("matrixError");
      else if ( Boolean.valueOf( (String)request.get("isWizard") ) )
         return new ModelAndView("gotoWizard", "page_id", pageId);
      else
         return new ModelAndView("gotoMatrix", "page_id", pageId);
   }
   
   private List getAttachmentBeans( List attachments ) {
      ArrayList beans = new ArrayList( attachments.size() );
      for ( int i=0; i<attachments.size(); i++ )
         beans.add( new AttachmentBean( (Reference)attachments.get(i) ) );
      return beans;
   }
   
   public SessionManager getSessionManager() {
      return sessionManager;
   }

   public void setSessionManager(SessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager manager) {
      idManager = manager;
   }

   public MatrixManager getMatrixManager() {
      return matrixManager;
   }

   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }

   public class AttachmentBean {
      private String displayName;
      private String url;
      private String size;
      private String iconUrl;
      
      public AttachmentBean( Reference ref ) {
         displayName = ref.getProperties().getPropertyFormatted(ResourceProperties.PROP_DISPLAY_NAME);
         size = ref.getProperties().getPropertyFormatted(ResourceProperties.PROP_CONTENT_LENGTH);
         url = ref.getUrl();
         iconUrl = ContentTypeImageService.getContentTypeImage( ref.getProperties().getProperty(ResourceProperties.PROP_CONTENT_TYPE) );
      }
      
      public String getDisplayName() {
         return displayName;
      }
      public String getUrl() {
         return url;
      }
      public String getSize() {
         return size;
      }
      public String getIconUrl() {
         return iconUrl;
      }
      
   }

}
