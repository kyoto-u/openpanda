/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/sam/tags/sakai-10.2/samigo-api/src/java/org/sakaiproject/tool/assessment/data/ifc/questionpool/QuestionPoolItemIfc.java $
 * $Id: QuestionPoolItemIfc.java 106463 2012-04-02 12:20:09Z david.horwitz@uct.ac.za $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008 The Sakai Foundation
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


package org.sakaiproject.tool.assessment.data.ifc.questionpool;


/**
 * Simple interface for question pool items.
 */
public interface QuestionPoolItemIfc
{
  public String getItemId();
  public void setItemId(String itemId);
  public Long getQuestionPoolId();
  public void setQuestionPoolId(Long questionPoolId);
}
