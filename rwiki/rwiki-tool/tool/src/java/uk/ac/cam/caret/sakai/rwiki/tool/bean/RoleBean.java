/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/rwiki/tags/sakai-2.9.0/rwiki-tool/tool/src/java/uk/ac/cam/caret/sakai/rwiki/tool/bean/RoleBean.java $
 * $Id: RoleBean.java 20354 2007-01-17 10:30:57Z ian@caret.cam.ac.uk $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006 The Sakai Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package uk.ac.cam.caret.sakai.rwiki.tool.bean;

import org.sakaiproject.authz.api.Role;

import uk.ac.cam.caret.sakai.rwiki.service.api.RWikiSecurityService;


public class RoleBean
{

	private Role role;

	public RoleBean()
	{

	}

	public RoleBean(Role role)
	{
		this.role = role;
	}

	public void setRole(Role role)
	{
		this.role = role;
	}

	public String getId()
	{
		return role.getId();
	}

	public String getDescription()
	{
		return role.getDescription();
	}

	public boolean isSecureRead()
	{
		return role.isAllowed(RWikiSecurityService.SECURE_READ);
	}

	public boolean isSecureUpdate()
	{
		return role.isAllowed(RWikiSecurityService.SECURE_UPDATE);
	}

	public boolean isSecureAdmin()
	{
		return role.isAllowed(RWikiSecurityService.SECURE_ADMIN);
	}

	public boolean isSecureDelete()
	{
		return role.isAllowed(RWikiSecurityService.SECURE_DELETE);
	}

	public boolean isSecureCreate()
	{
		return role.isAllowed(RWikiSecurityService.SECURE_CREATE);
	}

	public boolean isSecureSuperAdmin()
	{
		return role.isAllowed(RWikiSecurityService.SECURE_SUPER_ADMIN);
	}
}
