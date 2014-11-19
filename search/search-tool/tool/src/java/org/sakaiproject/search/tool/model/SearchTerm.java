/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/search/tags/sakai-10.2/search-tool/tool/src/java/org/sakaiproject/search/tool/model/SearchTerm.java $
 * $Id: SearchTerm.java 105078 2012-02-24 23:00:38Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007, 2008, 2009 The Sakai Foundation
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

package org.sakaiproject.search.tool.model;

/**
 * Model for the search terms on the page
 * @author ieb
 *
 */
public interface SearchTerm
{

	
	/**
	 * the relative weight of the search term 1 - 100
	 * @return
	 */
	String getWeight();
	
	/**
	 * The URL that will search on the term
	 * @return
	 */
	String getUrl();
	
	/**
	 * The name of the search term
	 * @return
	 */
	String getName();

}
