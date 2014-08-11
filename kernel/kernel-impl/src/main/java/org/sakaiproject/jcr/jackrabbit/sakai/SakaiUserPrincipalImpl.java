/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/kernel/branches/kernel-1.2.x/kernel-impl/src/main/java/org/sakaiproject/jcr/jackrabbit/sakai/SakaiUserPrincipalImpl.java $
 * $Id: SakaiUserPrincipalImpl.java 51317 2008-08-24 04:38:02Z csev@umich.edu $
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

package org.sakaiproject.jcr.jackrabbit.sakai;

import org.sakaiproject.jcr.api.internal.SakaiUserPrincipal;
import org.sakaiproject.user.api.User;

public class SakaiUserPrincipalImpl implements SakaiUserPrincipal
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8344465158464876283L;

	private String userId = null;

	/**
	 * Creates a <code>SystemPrincipal</code>.
	 */
	public SakaiUserPrincipalImpl(User user)
	{
		userId = user.getId();
	}

	@Override
	public String toString()
	{
		return ("SakaiUserPrincipal");
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj instanceof SakaiUserPrincipal)
		{
			SakaiUserPrincipal sup = (SakaiUserPrincipal) obj;
			return sup.getName().equals(getName());
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return getName().hashCode();
	}

	// ------------------------------------------------------------< Principal >
	/**
	 * {@inheritDoc}
	 */
	public String getName()
	{
		return userId;
	}

}
