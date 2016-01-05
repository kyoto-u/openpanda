/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/edu-services/tags/sakai-10.6/sections-service/sections-impl/integration-support/src/java/org/sakaiproject/component/section/support/UserManager.java $
 * $Id: UserManager.java 105077 2012-02-24 22:54:29Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
package org.sakaiproject.component.section.support;

import org.sakaiproject.section.api.coursemanagement.User;

public interface UserManager {
	public User createUser(String userUid, String displayName, String sortName, String displayUid);
	public User findUser(String userUid);
}
