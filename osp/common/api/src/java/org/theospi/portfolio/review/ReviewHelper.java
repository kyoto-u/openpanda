/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.3/common/api/src/java/org/theospi/portfolio/review/ReviewHelper.java $
* $Id: ReviewHelper.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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
package org.theospi.portfolio.review;

public interface ReviewHelper {

   public static final String REVIEW_TYPE =
      "org.theospi.portfolio.review.type";
   
   public static final String REVIEW_FORM_TYPE =
      "org.theospi.portfolio.review.formType";
   
   public static final String REVIEW_PARENT =
      "org.theospi.portfolio.review.parent";
  
   public static final String REVIEW_BUNDLE_PREFIX = 
      "org.theospi.portfolio.review.bundle_prefix";
   
   public static final String REVIEW_POST_PROCESSOR_WORKFLOWS = 
      "org.theospi.portfolio.review.postProcessorWorkflows";
   
   public static final String REVIEW_TYPE_KEY = 
      "org_theospi_portfolio_review_type";
   
   public static final String REVIEW_ITEM_ID = 
      "org_theospi_portfolio_review_item";

}
