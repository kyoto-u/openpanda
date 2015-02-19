
/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.4/matrix/api/src/java/org/theospi/portfolio/matrix/MatrixFunctionConstants.java $
* $Id: MatrixFunctionConstants.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
package org.theospi.portfolio.matrix;

/**
 * @author chmaurer
 */
public interface MatrixFunctionConstants {
  
   public final static String READY_STATUS = "READY";
   public final static String PENDING_STATUS = "PENDING";
   public final static String COMPLETE_STATUS = "COMPLETE";
   public final static String LOCKED_STATUS = "LOCKED";
   public final static String WAITING_STATUS = "WAITING";
   public final static String CHECKED_OUT_STATUS = "CHECKED_OUT";
   public final static String RETURNED_STATUS = "RETURNED";

   public final static String SCAFFOLDING_PREFIX = "osp.matrix.scaffolding.";
    public final static String CREATE_SCAFFOLDING = SCAFFOLDING_PREFIX + "create";
   public final static String REVISE_SCAFFOLDING_ANY = SCAFFOLDING_PREFIX + "revise.any";
   public final static String REVISE_SCAFFOLDING_OWN = SCAFFOLDING_PREFIX + "revise.own";
   public final static String DELETE_SCAFFOLDING_ANY = SCAFFOLDING_PREFIX + "delete.any";
   public final static String DELETE_SCAFFOLDING_OWN = SCAFFOLDING_PREFIX + "delete.own";
   public final static String PUBLISH_SCAFFOLDING_ANY = SCAFFOLDING_PREFIX + "publish.any";
   public final static String PUBLISH_SCAFFOLDING_OWN = SCAFFOLDING_PREFIX + "publish.own";
   public final static String EXPORT_SCAFFOLDING_ANY = SCAFFOLDING_PREFIX + "export.any";
   public final static String EXPORT_SCAFFOLDING_OWN = SCAFFOLDING_PREFIX + "export.own";
   
   public static final String VIEW_SCAFFOLDING_GUIDANCE = SCAFFOLDING_PREFIX + "viewScaffGuidance";
   public static final String EDIT_SCAFFOLDING_GUIDANCE = SCAFFOLDING_PREFIX + "editScaffGuidance";
      
   public final static String MATRIX_PREFIX = "osp.matrix.";
   public static final String REVIEW_MATRIX = MATRIX_PREFIX + "review";
   public static final String EVALUATE_MATRIX = MATRIX_PREFIX + "evaluate";
   public static final String VIEW_OWNER_MATRIX = MATRIX_PREFIX + "viewOwner";
   public static final String FEEDBACK_MATRIX = "osp.inviteFeedback.evaluate";
   
   public static final String EVALUATE_SPECIFIC_MATRIXCELL = MATRIX_PREFIX + "evaluateSpecificMatrix";
   
   
   public final static String SCAFFOLDING_SPECIFIC_PREFIX = "osp.matrix.scaffoldingSpecific.";
   public final static String ACCESS_ALL_CELLS = SCAFFOLDING_SPECIFIC_PREFIX + "accessAll";
   public final static String VIEW_EVAL_OTHER = SCAFFOLDING_SPECIFIC_PREFIX + "viewEvalOther";
   public final static String VIEW_FEEDBACK_OTHER = SCAFFOLDING_SPECIFIC_PREFIX + "viewFeedbackOther";
   public final static String MANAGE_STATUS = SCAFFOLDING_SPECIFIC_PREFIX + "manageStatus";
   public final static String ACCESS_USERLIST = SCAFFOLDING_SPECIFIC_PREFIX + "accessUserList";
   public final static String VIEW_ALL_GROUPS = SCAFFOLDING_SPECIFIC_PREFIX + "viewAllGroups";
   public final static String CAN_USE_SCAFFOLDING = SCAFFOLDING_SPECIFIC_PREFIX + "use";
   
   
   
	//Reflection review type = 0
	//Evaluation review type = 1
	//Feedback review type   = 2
   public static final int REFLECTION_REVIEW_TYPE = 0;
   public static final int EVALUATION_REVIEW_TYPE = 1;
   public static final int FEEDBACK_REVIEW_TYPE = 2;
   
   
   
}
