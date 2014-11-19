/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/kernel/tags/sakai-10.2/api/src/main/java/org/sakaiproject/user/api/IdPwEvidence.java $
 * $Id: IdPwEvidence.java 105077 2012-02-24 22:54:29Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2008 Sakai Foundation
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

package org.sakaiproject.user.api;

/**
 * <p>
 * IdPwEvidence is Authetication evidence made up of a user identifier and a password. Note the "id" used here is something the user offers for authentication purposes, and is *not* the user's Sakai user object UUID.
 * </p>
 */
public interface IdPwEvidence extends Evidence
{
	/**
	 * Access the user identifier.
	 * 
	 * @return The user identifier.
	 */
	String getIdentifier();

	/**
	 * Access the password.
	 * 
	 * @return The password.
	 */
	String getPassword();
}
