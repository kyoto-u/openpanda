/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/sam/tags/sakai-10.6/samigo-services/src/java/org/sakaiproject/tool/assessment/facade/util/PagingUtilQueriesAPI.java $
 * $Id: PagingUtilQueriesAPI.java 106463 2012-04-02 12:20:09Z david.horwitz@uct.ac.za $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
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

package org.sakaiproject.tool.assessment.facade.util;

import java.util.List;

public interface PagingUtilQueriesAPI{

  public List getAll(final int pageSize, final int pageNumber,
      final String queryString, final Integer value);
  
  public List getAll(final int pageSize, final int pageNumber,
	      final String queryString);

}
