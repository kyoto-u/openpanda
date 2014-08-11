/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/SequentialWizardPageController.java $
* $Id:SequentialWizardPageController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.matrix.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sakaiproject.metaobj.shared.model.Id;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.HibernateMatrixManagerImpl;
import org.theospi.portfolio.matrix.WizardPageHelper;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.wizard.model.CompletedWizard;
import org.theospi.portfolio.wizard.model.CompletedWizardPage;
import org.sakaiproject.metaobj.shared.control.ToolFinishedView;

/**
 * The steps are referenced from 1 to n.  this way we can render the step number to the interface correctly
 * 
 * openEvaluationPageSeqRedirect will put the user here
 * 
 * User: John Ellis
 * Date: Feb 3, 2006
 * Time: 1:51:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class SequentialWizardPageController extends WizardPageController {

   /* (non-Javadoc)
    * @see org.theospi.utils.mvc.intf.FormController#referenceData(java.util.Map, java.lang.Object, org.springframework.validation.Errors)
    */
   public Map referenceData(Map request, Object command, Errors errors) {
      Map model = super.referenceData(request, command, errors);
      if (request.get(WizardPageHelper.TOTAL_STEPS) != null) {
         model.put("sequential", "true");
         model.put("currentStep", request.get(WizardPageHelper.SEQUENTIAL_WIZARD_CURRENT_STEP));
         model.put("totalSteps", request.get(WizardPageHelper.TOTAL_STEPS));
         model.put("evaluationItem", request.get(WizardPageHelper.EVALUATION_ITEM));
      }
      return model;
   }

   public Object fillBackingObject(Object incomingModel, Map request,
                                   Map session, Map application) throws Exception {
      // get the step and get the appropriate page
      List steps = (List) session.get(WizardPageHelper.SEQUENTIAL_WIZARD_PAGES);

      if (steps == null) {
         Id pageId = getIdManager().getId((String)request.get("page_id"));
         WizardPage page = getMatrixManager().getWizardPage(pageId);
         CompletedWizard cw = getWizardManager().getCompletedWizardByPage(pageId);
         List completedPages = cw.getRootCategory().getChildPages();
         steps = getPageList(completedPages);
         session.put(WizardPageHelper.SEQUENTIAL_WIZARD_CURRENT_STEP, getCurrentStepFromList(steps, page));
         session.put(WizardPageHelper.WIZARD_OWNER, cw.getOwner());
         session.put(WizardPageHelper.SEQUENTIAL_WIZARD_PAGES, steps);

      }
      
      //TODO: Find an appropriate place to delete the session variable being grabbed here.
      //	  it is bad to delete the session var after setting it in the request here b/c
      //      when the user goes to a helper page and returns, the session is null (i.e. 
      //	  selecting an item)
      request.put(WizardPageHelper.EVALUATION_ITEM, session.get(WizardPageHelper.EVALUATION_ITEM));
      
      
      //TODO: It's probably safe to assume that steps will not be null at this point, 
      // but I'm leaving the check here for the time being.
      if (steps != null) {
         int currentStep = getCurrentStep(session);
         request.put(WizardPageHelper.TOTAL_STEPS, Integer.valueOf(steps.size()));
         //if(currentStep == 0)
         //   currentStep = 1;
         currentStep = currentStep + 1;
         WizardPage page = (WizardPage) steps.get(currentStep - 1);
         if (session.get(WizardPageHelper.WIZARD_OWNER) == null)
            session.put(WizardPageHelper.WIZARD_OWNER, page.getOwner());

         request.put(WizardPageHelper.SEQUENTIAL_WIZARD_CURRENT_STEP, Integer.valueOf(currentStep));
         //session.put(WizardPageHelper.SEQUENTIAL_WIZARD_CURRENT_STEP, currentStep);

         session.put(WizardPageHelper.WIZARD_PAGE, page);
      }
      return super.fillBackingObject(incomingModel, request, session, application);
   }

   protected Integer getCurrentStepFromList(List pages, WizardPage curPage) {
      int counter = 0;
      for (Iterator iter = pages.iterator(); iter.hasNext();) {
         WizardPage page = (WizardPage) iter.next();
         if (page.equals(curPage))
            break;
         counter++;
      }
      return Integer.valueOf(counter);
   }

   protected List getPageList(List completedPages) {
      List pageList = new ArrayList();

      for (Iterator i=completedPages.iterator();i.hasNext();) {
         CompletedWizardPage page = (CompletedWizardPage) i.next();
         pageList.add(page.getWizardPage());
      }
      return pageList;
   }

   protected int getCurrentStep(Map session) {
      int currentStep = 0;
      Object stepObj = (Object) session.get(WizardPageHelper.SEQUENTIAL_WIZARD_CURRENT_STEP);
      if (stepObj != null && stepObj instanceof Integer) {
         currentStep = ((Integer)stepObj).intValue();
      }
      return currentStep;
   }

   public ModelAndView handleRequest(Object requestModel, Map request, Map session,
                                     Map application, Errors errors) {
      if (request.get("matrix") != null) {
    	  
			if (getTaggingManager().isTaggable()) {
				session.remove(HibernateMatrixManagerImpl.PROVIDERS_PARAM);
			}
			
         session.put(ToolFinishedView.ALTERNATE_DONE_URL, "finishSeqWizard");
         return new ModelAndView("confirmWizard", "", "");
      }

      session.put(WizardPageHelper.SEQUENTIAL_WIZARD_CURRENT_STEP, getNextStep(request, session));
      if (isLast(request)) {
         session.put(WizardPageHelper.IS_LAST_STEP, "true");
      }
      else {
         session.remove(WizardPageHelper.IS_LAST_STEP);
      }

      String finishAction = (String)request.get("matrix");
      if (finishAction != null) {
         //Clear out some session variables
         session.remove(WizardPageHelper.SEQUENTIAL_WIZARD_CURRENT_STEP);
         session.remove(WizardPageHelper.SEQUENTIAL_WIZARD_PAGES);
      }   

      return super.handleRequest(requestModel, request,
         session, application, errors);
   }

   protected boolean isLast(Map request) {
      return request.get("_last") != null;
   }

   protected Integer getNextStep(Map request, Map session) {
      int currentStep = getCurrentStep(session);

      if (request.get("_next") != null) {
         currentStep++;
      }
      else if (request.get("_back") != null) {
         currentStep--;
      }

      return Integer.valueOf(currentStep);
   }

}
