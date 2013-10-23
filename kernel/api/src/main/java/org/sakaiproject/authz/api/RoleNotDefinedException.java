/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/kernel/tags/kernel-1.3.3/api/src/main/java/org/sakaiproject/authz/api/RoleNotDefinedException.java $
 * $Id: RoleNotDefinedException.java 62985 2009-05-28 09:13:52Z david.horwitz@uct.ac.za $
 ***********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2008 Sakai Foundation
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

package org.sakaiproject.authz.api;

/**
 * <p>
 * RoleNotDefinedException is thrown whenever something tries to use an Authz Role that does not exist in a particular Authz Group.
 * </p>
 */
public class RoleNotDefinedException extends Exception
{
	private String m_id = null;

	public RoleNotDefinedException(String id)
	{
		m_id = id;
	}

	public String toString()
	{
		return super.toString() + " id=" + m_id;
	}
}
