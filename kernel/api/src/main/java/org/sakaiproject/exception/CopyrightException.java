/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/kernel/tags/sakai-10.6/api/src/main/java/org/sakaiproject/exception/CopyrightException.java $
 * $Id: CopyrightException.java 107067 2012-04-13 15:20:04Z azeckoski@unicon.net $
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

package org.sakaiproject.exception;

/**
 * <p>
 * CopyrightException is thrown when a resource is accessed that requires a copyright agreement from the end user.
 * </p>
 */
public class CopyrightException extends SakaiException
{
	private String m_ref = null;

	public CopyrightException()
	{
		super();
	}

	public CopyrightException(String ref)
	{
		super(ref);
	}

	public String toString()
	{
		return super.toString() + ((m_ref != null) ? (" : " + m_ref) : "");
	}
}
