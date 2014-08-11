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

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ComponentManager;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Tool;

/**
 * Based on
 * https://source.caret.cam.ac.uk/camtools/trunk/camtools/sdata/tool/sakai
 * -sdata-impl/src/main/java/org/sakaiproject/sdata/services/site/SiteBean.java
 * <p>
 * Requires one getParameter: siteId. Option getParameter: writeEvent=true --
 * Records presence.begin and site.visit events.
 * <p>
 * Servlet runs in the context of the current user, so they must have access to
 * the siteId specified. Normal HTTP error codes to expect are:
 * HttpServletResponse.SC_NOT_FOUND for an invalid siteId, or
 * HttpServletResponse.SC_FORBIDDEN if the current user does not have permission
 * to access the specified site.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "MTIA_SUSPECT_SERVLET_INSTANCE_FIELD", justification = "dependencies only mutated only during init()")
@SuppressWarnings({ "PMD.LongVariable", "PMD.CyclomaticComplexity" })
public class SiteVisitToolPlacementServlet extends HttpServlet {
	private static final long serialVersionUID = -1182601175544873164L;
	private static final Log LOG = LogFactory
			.getLog(SiteVisitToolPlacementServlet.class);
	private static final String SITE_ID = "siteId";

	private static final String MSF_MUTABLE_SERVLET_FIELD = "MSF_MUTABLE_SERVLET_FIELD";
	private static final String DEPENDENCY_ONLY_MUTATED_DURING_INIT = "dependency mutated only during init()";

	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = MSF_MUTABLE_SERVLET_FIELD, justification = DEPENDENCY_ONLY_MUTATED_DURING_INIT)
	protected transient ComponentManager componentManager;
	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = MSF_MUTABLE_SERVLET_FIELD, justification = DEPENDENCY_ONLY_MUTATED_DURING_INIT)
	private transient SessionManager sessionManager;
	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = MSF_MUTABLE_SERVLET_FIELD, justification = DEPENDENCY_ONLY_MUTATED_DURING_INIT)
	private transient SiteService siteService;
	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = MSF_MUTABLE_SERVLET_FIELD, justification = DEPENDENCY_ONLY_MUTATED_DURING_INIT)
	private transient EventTrackingService eventTrackingService;
	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = MSF_MUTABLE_SERVLET_FIELD, justification = DEPENDENCY_ONLY_MUTATED_DURING_INIT)
	private transient AuthzGroupService authzGroupService;
	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = MSF_MUTABLE_SERVLET_FIELD, justification = DEPENDENCY_ONLY_MUTATED_DURING_INIT)
	private transient SecurityService securityService;
	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = MSF_MUTABLE_SERVLET_FIELD, justification = DEPENDENCY_ONLY_MUTATED_DURING_INIT)
	protected transient ToolHelperImpl toolHelper;

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@SuppressWarnings({ "PMD.CyclomaticComplexity",
			"PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	protected void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("doGet(HttpServletRequest " + req
					+ ", HttpServletResponse " + resp + ")");
		}
		// ensure siteId getParameter
		final String siteId = req.getParameter(SITE_ID);
		if (siteId == null || "".equals(siteId)) {
			if (!resp.isCommitted()) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			} else {
				throw new IllegalAccessError(
						"HttpServletResponse.SC_BAD_REQUEST");
			}
		}
		// should we record a site visit event?
		@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
		final boolean writeEvent = Boolean.parseBoolean(req
				.getParameter("writeEvent"));
		// current user
		@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
		final String principal = sessionManager.getCurrentSession()
				.getUserEid();
		// 1) get the Site object for siteId
		// 2) ensure user has access to Site via SiteService.getSiteVisit()
		@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
		Site site = null;
		try {
			site = siteService.getSiteVisit(siteId);
		} catch (IdUnusedException e) {
			LOG.debug("Site not found: " + siteId, e);
			sendError(resp, HttpServletResponse.SC_NOT_FOUND,
					"HttpServletResponse.SC_NOT_FOUND: " + siteId);
			return;
		} catch (PermissionException e) {
			LOG.warn("Permission denied: " + principal
					+ " could not access site " + siteId);
			sendError(resp, HttpServletResponse.SC_FORBIDDEN,
					"HttpServletResponse.SC_FORBIDDEN");
			return;
		}
		if (site != null) { // normal program flow
			final JSONObject json = new JSONObject();
			json.element("principal", sessionManager.getCurrentSession()
					.getUserEid());
			final JSONObject siteJson = new JSONObject();
			siteJson.element("title", site.getTitle());
			siteJson.element("id", site.getId());
			siteJson.element("icon", site.getIconUrlFull());
			siteJson.element("skin", site.getSkin());
			siteJson.element("type", site.getType());
			// get the list of site pages
			final List<SitePage> pages = site.getOrderedPages();
			int number = 0;
			if (pages != null && canAccessAtLeastOneTool(site, pages)) {
				final JSONArray pagesArray = new JSONArray();
				for (SitePage page : pages) { // for each page
					if (!canAccessAtLeastOneTool(site, page)) {
						continue;
					}
					final JSONObject pageJson = new JSONObject();
					pageJson.element("id", page.getId());
					pageJson.element("name", page.getTitle());
					pageJson.element("layout", page.getLayout());
					pageJson.element("number", ++number);
					pageJson.element("popup", page.isPopUp());
					// get list of tools for the page
					final List<ToolConfiguration> tools = page.getTools();
					if (tools != null && !tools.isEmpty()) {
						pageJson.element(
								"iconclass",
								"icon-"
										+ tools.get(0).getToolId()
												.replaceAll("[.]", "-"));
						final JSONArray toolsArray = new JSONArray();
						for (ToolConfiguration toolConfig : tools) {
							// for each toolConfig
							if (toolHelper.allowTool(site, toolConfig)) {
								final JSONObject toolJson = new JSONObject();
								toolJson.element("url", toolConfig.getId());
								final Tool tool = toolConfig.getTool();
								if (tool != null && tool.getId() != null) {
									toolJson.element("title", tool.getTitle());
									toolJson.element("layouthint",
											toolConfig.getLayoutHints());
								} else {
									toolJson.element("title", page.getTitle());
								}
								toolsArray.add(toolJson);
							}
						}
						pageJson.element("tools", toolsArray);
					}
					pagesArray.add(pageJson);
				}
				siteJson.element("pages", pagesArray);
			}
			// get roles for site
			final JSONArray rolesArray = new JSONArray();
			try {
				final AuthzGroup group = authzGroupService
						.getAuthzGroup("/site/" + siteId);
				final Set<Role> roles = group.getRoles();
				for (Role role : roles) {
					final JSONObject roleJson = new JSONObject();
					roleJson.element("id", role.getId());
					roleJson.element("description", role.getDescription());
					rolesArray.add(roleJson);
				}
			} catch (GroupNotDefinedException e) {
				LOG.warn("No AuthzGroup found for site: " + siteId);
			}
			siteJson.element("roles", rolesArray);

			// write siteJson to containing json
			json.element("site", siteJson);
			// dump json to response writer
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			resp.setStatus(HttpServletResponse.SC_OK);
			json.write(resp.getWriter());
			// post events if requested
			if (writeEvent) {
				final Event presenceBegin = eventTrackingService
						.newEvent("pres.begin", "/presence/" + siteId
								+ "-presence", true);
				eventTrackingService.post(presenceBegin);
				final Event siteVisit = eventTrackingService.newEvent(
						"site.visit", "/site/" + siteId, true);
				eventTrackingService.post(siteVisit);
			}
		} else {
			sendError(resp, HttpServletResponse.SC_NOT_FOUND,
					"HttpServletResponse.SC_NOT_FOUND: " + siteId);
			return;
		}
	}

	/**
	 * Loops through all of the site pages and checks to see if the current user
	 * can access at least one of those tools.
	 * 
	 * @param site
	 * @return true if at least one tool can be accessed.
	 */
	protected boolean canAccessAtLeastOneTool(final Site site,
			final List<SitePage> pages) {
		if (pages != null) {
			for (SitePage page : pages) {
				final List<ToolConfiguration> tools = page.getTools();
				if (tools != null) {
					for (ToolConfiguration tool : tools) {
						if (toolHelper.allowTool(site, tool)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Goes through any tools on a single particular page to see if the current
	 * user can access at least one of the tools.
	 * 
	 * @param site The site we're using.
	 * @param page The page whose tools to check.
	 * @return true if at least one tool can be accessed.
	 */
	protected boolean canAccessAtLeastOneTool(final Site site, final SitePage page) {
		final List<ToolConfiguration> tools = page.getTools();
		if (tools != null) {
			for (ToolConfiguration tool : tools) {
				if (toolHelper.allowTool(site, tool)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Simple little wrapper for HttpServletResponse.sendError - just to improve
	 * readability of main-line code.
	 * 
	 * @param resp
	 * @param errorCode
	 * @param message
	 * @throws IOException
	 * @throws ResponseCommittedException
	 */
	protected void sendError(final HttpServletResponse resp,
			final int errorCode, final String message) throws IOException {
		if (!resp.isCommitted()) {
			resp.sendError(errorCode);
			return;
		} else {
			throw new ResponseCommittedException(message);
		}
	}

	@Override
	public void init(final ServletConfig config) throws ServletException {
		super.init(config);
		if (componentManager == null) {
			componentManager = org.sakaiproject.component.cover.ComponentManager
					.getInstance();
		}
		sessionManager = (SessionManager) componentManager
				.get(SessionManager.class);
		if (sessionManager == null) {
			throw new IllegalStateException("SessionManager == null");
		}
		siteService = (SiteService) componentManager.get(SiteService.class);
		if (siteService == null) {
			throw new IllegalStateException("SiteService == null");
		}
		eventTrackingService = (EventTrackingService) componentManager
				.get(EventTrackingService.class);
		if (eventTrackingService == null) {
			throw new IllegalStateException("EventTrackingService == null");
		}
		authzGroupService = (AuthzGroupService) componentManager
				.get(AuthzGroupService.class);
		if (authzGroupService == null) {
			throw new IllegalStateException("AuthzGroupService == null");
		}
		securityService = (SecurityService) componentManager
				.get(SecurityService.class);
		if (securityService == null) {
			throw new IllegalStateException("SecurityService == null");
		}
		toolHelper = new ToolHelperImpl(securityService);
	}

	/**
	 * Only used for unit testing setup.
	 * 
	 * @param componentManager
	 */
	protected void setupTestCase(final ComponentManager componentManager) {
		if (componentManager == null) {
			throw new IllegalArgumentException("componentManager == null");
		}
		this.componentManager = componentManager;
	}

	/**
	 * @see ServletResponse#isCommitted()
	 */
	public static class ResponseCommittedException extends RuntimeException {
		private static final long serialVersionUID = -288866672761140745L;

		/**
		 * @see RuntimeException#RuntimeException(String)
		 * @param message
		 */
		public ResponseCommittedException(final String message) {
			super(message);
		}
	}
}
