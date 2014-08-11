/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/kernel/branches/kernel-1.2.x/kernel-impl/src/main/java/org/sakaiproject/jcr/jackrabbit/SessionHolder.java $
 * $Id: SessionHolder.java 80991 2010-08-09 15:46:37Z david.horwitz@uct.ac.za $
 ***********************************************************************************
 *
 * Copyright (c) 2007, 2008 Sakai Foundation
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

package org.sakaiproject.jcr.jackrabbit;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.thread_local.api.ThreadBound;

public class SessionHolder implements ThreadBound
{
	private static final Log log = LogFactory.getLog(SessionHolder.class);

	private Session session = null;

	private boolean keepLoggedIn = false;

	@Override
	protected void finalize() throws Throwable
	{
		try
		{
			if ( session != null ) {
				session.logout();
			}
		}
		catch (Throwable t)
		{
			log.warn("Failed to close session ", t);
		}
		super.finalize();
	}

	public SessionHolder(RepositoryBuilder repositoryBuilder,
			Credentials repositoryCredentials, String workspace) throws LoginException, RepositoryException
	{
		Repository repository = repositoryBuilder.getInstance();
		session = repository.login(repositoryCredentials); //, workspace);
	}

/**
	 * @param session2
	 */
	public SessionHolder(Session session)
	{
		this.session = session;
	}

	public Session getSession()
	{
		return session;
	}

	public void unbind()
	{
		if ( keepLoggedIn  ) {
			keepLoggedIn = false;
		} else if (session != null ) {
			session.logout();
			session = null;
		}
	}
	public void keepLoggedIn() {
		keepLoggedIn = true;
	}
}
