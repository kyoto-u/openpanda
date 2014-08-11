/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.1/common/api/src/java/org/theospi/portfolio/security/AudienceSelectionHelper.java $
* $Id: AudienceSelectionHelper.java 68687 2009-11-09 16:45:06Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.security;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 16, 2005
 * Time: 4:12:17 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AudienceSelectionHelper {


   public static final String AUDIENCE_FUNCTION =
         "org.theospi.portfolio.security.audienceFunction";
         
   public static final String AUDIENCE_FUNCTION_WIZARD = 
      "osp.wizard.evaluate";
   public static final String AUDIENCE_FUNCTION_MATRIX =
      "osp.matrix.evaluate";
   public static final String AUDIENCE_FUNCTION_MATRIX_REVIEW =
	      "osp.matrix.review";   
   public static final String AUDIENCE_FUNCTION_INVITE_FEEDBACK =
	      "osp.inviteFeedback.evaluate";
   public static final String AUDIENCE_FUNCTION_PORTFOLIO =
      "osp.presentation.view";
   
   public static final String AUDIENCE_QUALIFIER =
         "org.theospi.portfolio.security.audienceQualifier";

   public static final String AUDIENCE_PUBLIC_FLAG =
         "org.theospi.portfolio.security.audiencePublic";

   public static final String AUDIENCE_PUBLIC_URL =
         "org.theospi.portfolio.security.audiencePublicURL";

   public static final String AUDIENCE_CANCEL_TARGET =
         "org.theospi.portfolio.security.audienceCancelTarget";

   public static final String AUDIENCE_SAVE_TARGET =
         "org.theospi.portfolio.security.audienceSaveTarget";

   public static final String AUDIENCE_SAVE_NOTIFY_TARGET =
         "org.theospi.portfolio.security.audienceSaveNotifyTarget";

   public static final String AUDIENCE_BACK_TARGET =
         "org.theospi.portfolio.security.audienceBackTarget";

   public static final String AUDIENCE_PRESENTATION_MANAGER =
         "org.theospi.portfolio.security.PresentationManager";
    
   public static final String AUDIENCE_SITE =
         "org.theospi.portfolio.security.audienceSite";
   
   public static final String CONTEXT =
       "org.theospi.portfolio.security.context";
   
   public static final String CONTEXT2 =
       "org.theospi.portfolio.security.context2";
   
   /**
    * this is the id that MatrixFunctionConstants.REVIEW_MATRIX function uses for matrix reviewers:
    * This ID will either be scaffoldingCell.wizardPageDef.id or scaffolding.id
    */
   public static final String MATRIX_REVIEWER_OBJECT_ID = "org.theospi.portfolio.security.matrixReviewerObjectId";
   
   public static final String MATRIX_REVIEWER_FUNCTION = "org.theospi.portfolio.security.matrixReviewerFunction";
      
}
