/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.4/common/api/src/java/org/theospi/portfolio/guidance/mgt/GuidanceHelper.java $
* $Id: GuidanceHelper.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2006, 2008 The Sakai Foundation
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

package org.theospi.portfolio.guidance.mgt;

public interface GuidanceHelper {

   public static final String SHOW_INSTRUCTION_FLAG =
      "org.theospi.portfolio.guidance.instructionFlag";
   
   public static final String SHOW_RATIONALE_FLAG =
      "org.theospi.portfolio.guidance.rationaleFlag";
   
   public static final String SHOW_EXAMPLE_FLAG =
      "org.theospi.portfolio.guidance.exampleFlag";
   
   public static final String SHOW_RUBRIC_FLAG =
	      "org.theospi.portfolio.guidance.rubricFlag";
   
   public static final String SHOW_EXPECTATIONS_FLAG =
	      "org.theospi.portfolio.guidance.expectationsFlag";

   public static final String CONTEXT =
       "org.theospi.portfolio.guidance.context";
   
   public static final String CONTEXT2 =
       "org.theospi.portfolio.guidance.context2";
}

