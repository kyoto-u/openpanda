
/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.1/wizard/api/src/java/org/theospi/portfolio/wizard/WizardFunctionConstants.java $
* $Id: WizardFunctionConstants.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
package org.theospi.portfolio.wizard;

/**
 * @author chmaurer
 */
public interface WizardFunctionConstants {
   
   public final static String COMMENT_TYPE = "comment";
   public final static String REFLECTION_TYPE = "reflection";
   public final static String EVALUATION_TYPE = "evaluation";
  
   public final static String WIZARD_PREFIX = "osp.wizard.";
   public final static String CREATE_WIZARD = WIZARD_PREFIX + "create";
   public final static String EDIT_WIZARD = WIZARD_PREFIX + "edit";
   public final static String DELETE_WIZARD = WIZARD_PREFIX + "delete";
   public final static String PUBLISH_WIZARD = WIZARD_PREFIX + "publish";
   public static final String REVIEW_WIZARD = WIZARD_PREFIX + "review";
   public static final String EVALUATE_WIZARD = WIZARD_PREFIX + "evaluate";
   
   /** This is when the client user fills in a completed wizard */
   public static final String VIEW_WIZARD = WIZARD_PREFIX + "view";
   
   /** The operate permission is a campasite permission of complete (view), review, evaluate.
    * it has no hooks into the interface.
    */
   public static final String OPERATE_WIZARD = WIZARD_PREFIX + "operate";
   
   public static final String COPY_WIZARD = WIZARD_PREFIX + "copy";
   public static final String EXPORT_WIZARD = WIZARD_PREFIX + "export";
   
   public static final String VIEW_WIZARDPAGE_GUIDANCE = WIZARD_PREFIX + "viewWizPageGuidance";
   public static final String EDIT_WIZARDPAGE_GUIDANCE = WIZARD_PREFIX + "editWizPageGuidance";
   
   public static final String EVALUATE_SPECIFIC_WIZARD = WIZARD_PREFIX + "evaluateSpecificWizard";
   public static final String EVALUATE_SPECIFIC_WIZARDPAGE = WIZARD_PREFIX + "evaluateSpecificWizardPage";
   
   public final static String WIZARD_TYPE_SEQUENTIAL = "org.theospi.portfolio.wizard.model.Wizard.sequential";
   public final static String WIZARD_TYPE_HIERARCHICAL = "org.theospi.portfolio.wizard.model.Wizard.hierarchical";
}

