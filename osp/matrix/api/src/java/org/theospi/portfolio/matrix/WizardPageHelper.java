/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/api/src/java/org/theospi/portfolio/matrix/WizardPageHelper.java $
* $Id:WizardPageHelper.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.matrix;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 18, 2006
 * Time: 3:27:42 PM
 * To change this template use File | Settings | File Templates.
 */
public interface WizardPageHelper {

   public final static String WIZARD_PAGE = "org.theospi.portfolio.matrix.WizardPageHelper.page";

   public final static String CLONED_WIZARD_PAGE = "org.theospi.portfolio.matrix.WizardPageHelper.clonedPage";

   public final static String CANCELED = "org.theospi.portfolio.matrix.WizardPageHelper.canceled";

   public final static String SEQUENTIAL_WIZARD_CURRENT_STEP = "org.theospi.portfolio.matrix.WizardPageHelper.step";

   public final static String SEQUENTIAL_WIZARD_PAGES = "org.theospi.portfolio.matrix.WizardPageHelper.pages";
   
   public final static String TOTAL_STEPS =
      "org.theospi.portfolio.matrix.control.SequentialWizardPageController.totalSteps";
   
   public final static String WIZARD_OWNER = "wizardowner";

   public final static String IS_LAST_STEP = "org.theospi.portfolio.matrix.WizardPageHelper.isLast";

   public final static String EVALUATION_ITEM = "org.theospi.portfolio.matrix.WizardPageHelper.evaluationItem";
}
