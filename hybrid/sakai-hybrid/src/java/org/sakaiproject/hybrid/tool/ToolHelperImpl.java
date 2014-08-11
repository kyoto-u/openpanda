/**
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.sakaiproject.hybrid.tool;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.tool.api.Placement;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * Copied from <a href=
 * "https://source.sakaiproject.org/svn/portal/tags/sakai-2.7.1/portal-impl/impl/src/java/org/sakaiproject/portal/charon/ToolHelperImpl.java"
 * >portal/tags/sakai-2.7.1/portal-impl/impl/src/java/org/sakaiproject/portal/
 * charon/ToolHelperImpl.java</a> at revision 82452.
 * <p>
 * Inclusion helps avoid class loader issue with sakai-portal-impl.
 */
@SuppressWarnings({ "PMD.LongVariable", "PMD.CyclomaticComplexity" })
public class ToolHelperImpl {
	protected transient SecurityService securityService;

	private static final Log LOG = LogFactory.getLog(ToolHelperImpl.class);

	public static final String TOOLCONFIG_REQUIRED_PERMISSIONS = "functions.require";

	/**
	 * 
	 * @param securityService
	 *            Required
	 */
	public ToolHelperImpl(final SecurityService securityService) {
		if (securityService == null) {
			throw new IllegalArgumentException(
					"SecurityService cannot be null!");
		}
		this.securityService = securityService;
	}

	/**
	 * The optional tool configuration tag "functions.require" describes a set
	 * of permission lists which decide the visibility of the tool link for this
	 * site user. Lists are separated by "|" and permissions within a list are
	 * separated by ",". Users must have all the permissions included in at
	 * least one of the permission lists.
	 * 
	 * For example, a value like "section.role.student,annc.new|section.role.ta"
	 * would let a user with "section.role.ta" see the tool, and let a user with
	 * both "section.role.student" AND "annc.new" see the tool, but not let a
	 * user who only had "section.role.student" see the tool.
	 * 
	 * If the configuration tag is not set or is null, then all users see the
	 * tool.
	 */
	@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.NPathComplexity",
			"PMD.DataflowAnomalyAnalysis", "PMD.OnlyOneReturn" })
	public boolean allowTool(final Site site, final Placement placement) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("allowTool(Site " + site + ", Placement " + placement
					+ ")");
		}
		// No way to render an opinion
		if (placement == null || site == null) {
			return true;
		}

		String requiredPermissionsString = placement.getConfig().getProperty(
				TOOLCONFIG_REQUIRED_PERMISSIONS);
		if (LOG.isDebugEnabled()) {
			LOG.debug("requiredPermissionsString=" + requiredPermissionsString
					+ " for " + placement.getToolId());
		}
		if (requiredPermissionsString == null) {
			return true;
		}
		requiredPermissionsString = requiredPermissionsString.trim();
		if (requiredPermissionsString.length() == 0) {
			return true;
		}

		final String[] allowedPermissionSets = requiredPermissionsString
				.split("\\|");
		for (int i = 0; i < allowedPermissionSets.length; i++) {
			final String[] requiredPermissions = allowedPermissionSets[i]
					.split(",");
			if (LOG.isDebugEnabled()) {
				LOG.debug("requiredPermissions="
						+ Arrays.asList(requiredPermissions));
			}
			boolean gotAllInList = true;
			for (int j = 0; j < requiredPermissions.length; j++) {
				if (!securityService.unlock(requiredPermissions[j].trim(),
						site.getReference())) {
					gotAllInList = false;
					break;
				}
			}
			if (gotAllInList) {
				return true;
			}
		}

		// No permission sets were matched.
		return false;
	}

}
