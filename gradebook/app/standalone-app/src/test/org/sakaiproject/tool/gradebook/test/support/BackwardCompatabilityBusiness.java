/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/gradebook/tags/sakai-10.0/app/standalone-app/src/test/org/sakaiproject/tool/gradebook/test/support/BackwardCompatabilityBusiness.java $
 * $Id: BackwardCompatabilityBusiness.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2006 The Sakai Foundation
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

package org.sakaiproject.tool.gradebook.test.support;

public interface BackwardCompatabilityBusiness {
	public void addGradebook(String uid, String name);
}
