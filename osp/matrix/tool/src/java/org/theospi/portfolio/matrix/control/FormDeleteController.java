
/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.1/matrix/tool/src/java/org/theospi/portfolio/matrix/control/FormDeleteController.java $
* $Id: FormDeleteController.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008, 2009 The Sakai Foundation
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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.CustomCommandController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.sakaiproject.content.api.LockManager;

/**
 * @author chmaurer
 */
public class FormDeleteController implements LoadObjectController, CustomCommandController {

   protected final Log logger = LogFactory.getLog(getClass());
	
   private IdManager idManager = null;
   private MatrixManager matrixManager = null;
   private ReviewManager reviewManager;
   private ContentHostingService contentHosting;
   private LockManager lockManager;
   
	private String DELETE_FORM = "delete";
	private String DELETE_FEEDBACK = "deleteReview";
	
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.CustomCommandController#formBackingObject(java.util.Map, java.util.Map, java.util.Map)
    */
   public Object formBackingObject(Map request, Map session, Map application) {
      return new HashMap();
   }
   
   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.LoadObjectController#fillBackingObject(java.lang.Object, java.util.Map, java.util.Map, java.util.Map)
    */
   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      return incomingModel;
   }

   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.Controller#handleRequest(java.lang.Object, java.util.Map, java.util.Map, java.util.Map, org.springframework.validation.Errors)
    */
   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      WizardPage page = (WizardPage) session.get(WizardPageHelper.WIZARD_PAGE);
      Id cellId = idManager.getId((String) request.get("page_id"));
      Id formId = idManager.getId((String) request.get("current_form_id"));
      Cell cell = getMatrixManager().getCellFromPage(cellId);
      boolean sessionPage = true;
      if (page == null) {
         sessionPage = false;
         page = cell.getWizardPage();
      }
      
      String submitAction = (String)request.get("submit");
      session.remove(WizardPageHelper.WIZARD_PAGE);
      getMatrixManager().removeFromSession(page);
      if ( submitAction.equals(DELETE_FORM) )
      {
         getMatrixManager().detachForm(page.getId(), formId);
      }
      else // (submitAction.equals(DELETE_FEEDBACK)) 
      {
         Id reviewId = idManager.getId((String)request.get("review_id"));
         Review review = getReviewManager().getReview(reviewId);
         if ( review != null )
            getReviewManager().deleteReview(review);
         else
            logger.warn("Null feedback form (perhaps multiple submits):" + reviewId );
      }
            
      if (sessionPage) 
         session.put(WizardPageHelper.WIZARD_PAGE, getMatrixManager().getWizardPage(page.getId()));
         
      try {
         // unlock and delete content
         String reviewContentId = contentHosting.getUuid( formId.getValue() );
         if ( getLockManager().isLocked(reviewContentId) ) 
            getLockManager().removeLock(reviewContentId, cellId.getValue() );
         getContentHosting().removeResource(formId.getValue());
      } 
      catch(Exception e) {
         logger.warn("Error removing form: "+ e.toString() );
      }
      
      // if not submit, then cancel, but both submit and cancel have the some view, so
      return new ModelAndView("continue", "page_id", page.getId().getValue());
   }
   
   

   /**
    * @return Returns the idManager.
    */
   public IdManager getIdManager() {
      return idManager;
   }
   /**
    * @param idManager The idManager to set.
    */
   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }
   /**
    * @return Returns the matrixManager.
    */
   public MatrixManager getMatrixManager() {
      return matrixManager;
   }
   /**
    * @param matrixManager The matrixManager to set.
    */
   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }
   /**
    * @return the contentHosting
    */
   public ContentHostingService getContentHosting() {
      return contentHosting;
   }
   /**
    * @param contentHosting the contentHosting to set
    */
   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }
   /**
    * @return Returns the reviewManager.
    */
   public ReviewManager getReviewManager() {
      return reviewManager;
   }
   /**
    * @param reviewManager The reviewManager to set.
    */
   public void setReviewManager(ReviewManager reviewManager) {
      this.reviewManager = reviewManager;
   }
   public LockManager getLockManager() {
      return lockManager;
   }
   public void setLockManager(LockManager lockManager) {
      this.lockManager = lockManager;
   }


}
