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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sakaiproject.hybrid.test.TestHelper.disableLog4jDebug;
import static org.sakaiproject.hybrid.test.TestHelper.enableLog4jDebug;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
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
import org.sakaiproject.hybrid.tool.SiteVisitToolPlacementServlet.ResponseCommittedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Tool;

@RunWith(MockitoJUnitRunner.class)
public class SiteVisitToolPlacementServletTest {
	protected SiteVisitToolPlacementServlet siteVisitToolPlacementServlet;

	@Mock
	protected transient ComponentManager componentManager;
	@Mock
	protected transient SecurityService securityService;
	@Mock
	protected SessionManager sessionManager;
	@Mock
	protected SiteService siteService;
	@Mock
	protected EventTrackingService eventTrackingService;
	@Mock
	protected AuthzGroupService authzGroupService;
	@Mock
	protected ToolHelperImpl toolHelper;
	@Mock
	protected HttpServletRequest request;
	@Mock
	protected HttpServletResponse response;
	@Mock
	protected Session session;
	@Mock
	protected Site site;
	@Mock
	protected Role role;
	@Mock
	protected SitePage page;
	@Mock
	protected ToolConfiguration toolConfig;
	@Mock
	protected Tool tool;
	@Mock
	protected AuthzGroup group;
	@Mock
	protected PrintWriter writer;
	@Mock
	protected Event event;
	@Mock
	protected ServletConfig config;
	protected List<ToolConfiguration> tools = new ArrayList<ToolConfiguration>();

	@BeforeClass
	public static void beforeClass() {
		enableLog4jDebug();
	}

	@Before
	public void setUp() throws Exception {
		when(componentManager.get(SecurityService.class)).thenReturn(
				securityService);
		when(componentManager.get(SessionManager.class)).thenReturn(
				sessionManager);
		when(componentManager.get(SiteService.class)).thenReturn(siteService);
		when(componentManager.get(EventTrackingService.class)).thenReturn(
				eventTrackingService);
		when(componentManager.get(AuthzGroupService.class)).thenReturn(
				authzGroupService);

		when(toolHelper.allowTool(any(Site.class), any(Placement.class)))
				.thenReturn(true);

		// pass siteId parameter
		when(request.getParameter("siteId")).thenReturn("!admin");

		when(sessionManager.getCurrentSession()).thenReturn(session);
		when(session.getUserEid()).thenReturn("admin");

		when(site.getTitle()).thenReturn("Administration Workspace");
		when(site.getId()).thenReturn("!admin");
		when(siteService.getSiteVisit("!admin")).thenReturn(site);

		when(role.getId()).thenReturn("admin");
		when(role.getDescription()).thenReturn(null);
		final Set<Role> roles = new HashSet<Role>();
		roles.add(role);

		when(page.getId()).thenReturn("!admin-100");
		when(page.getTitle()).thenReturn("Home");
		when(page.getLayout()).thenReturn(0);
		when(page.isPopUp()).thenReturn(false);
		when(toolConfig.getToolId()).thenReturn("sakai.motd");
		when(toolConfig.getId()).thenReturn("!admin-110");
		when(tool.getId()).thenReturn("!admin-110");
		when(tool.getTitle()).thenReturn("Message of The Day");
		when(toolConfig.getTool()).thenReturn(tool);
		tools.add(toolConfig);
		when(page.getTools()).thenReturn(tools);

		List<SitePage> pages = new ArrayList<SitePage>();
		pages.add(page);
		when(site.getOrderedPages()).thenReturn(pages);

		when(group.getRoles()).thenReturn(roles);
		when(authzGroupService.getAuthzGroup(anyString())).thenReturn(group);

		when(response.getWriter()).thenReturn(writer);

		siteVisitToolPlacementServlet = new SiteVisitToolPlacementServlet();
		siteVisitToolPlacementServlet.setupTestCase(componentManager);
		siteVisitToolPlacementServlet.init(config);
		siteVisitToolPlacementServlet.toolHelper = toolHelper;
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#doGet(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Test
	public void testNullSiteId() throws ServletException, IOException {
		when(request.getParameter("siteId")).thenReturn(null);
		siteVisitToolPlacementServlet.doGet(request, response);
		verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 * @throws PermissionException
	 * @throws IdUnusedException
	 * @see SiteVisitToolPlacementServlet#doGet(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Test
	public void testNullSite() throws ServletException, IOException,
			IdUnusedException, PermissionException {
		when(siteService.getSiteVisit("!admin")).thenReturn(null);
		siteVisitToolPlacementServlet.doGet(request, response);
		verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#doGet(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Test
	public void testNormalBehavior() throws ServletException, IOException {
		siteVisitToolPlacementServlet.doGet(request, response);
		verify(response).setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#doGet(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Test
	public void testNormalBehaviorAllowToolFalse() throws ServletException,
			IOException {
		when(toolHelper.allowTool(any(Site.class), any(Placement.class)))
				.thenReturn(false);
		siteVisitToolPlacementServlet.doGet(request, response);
		verify(response).setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#doGet(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Test
	public void testNormalBehaviorAllowToolTrueFalse() throws ServletException,
			IOException {
		when(toolHelper.allowTool(any(Site.class), any(Placement.class)))
				.thenReturn(true).thenReturn(false);
		siteVisitToolPlacementServlet.doGet(request, response);
		verify(response).setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#doGet(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Test
	public void testNormalBehaviorLogDebugDisabled() throws ServletException,
			IOException {
		disableLog4jDebug();
		siteVisitToolPlacementServlet.doGet(request, response);
		verify(response).setStatus(HttpServletResponse.SC_OK);
		enableLog4jDebug();
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#doGet(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Test
	public void testNormalBehaviorNullTool() throws ServletException,
			IOException {
		when(toolConfig.getTool()).thenReturn(null);
		siteVisitToolPlacementServlet.doGet(request, response);
		verify(response).setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#doGet(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Test
	public void testNormalBehaviorNullToolId() throws ServletException,
			IOException {
		when(tool.getId()).thenReturn(null);
		siteVisitToolPlacementServlet.doGet(request, response);
		verify(response).setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#doGet(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Test
	public void testNormalBehaviorNullOrderedPages() throws ServletException,
			IOException {
		when(site.getOrderedPages()).thenReturn(null);
		siteVisitToolPlacementServlet.doGet(request, response);
		verify(response).setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#doGet(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Test
	public void testNormalBehaviorNullTools() throws ServletException,
			IOException {
		when(page.getTools()).thenReturn(null);
		siteVisitToolPlacementServlet.doGet(request, response);
		verify(toolConfig, times(0)).getId();
		verify(toolConfig, times(0)).getTool();
		verify(toolConfig, times(0)).getLayoutHints();
		verify(response).setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#doGet(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Test
	public void testNormalBehaviorNullToolsVariant() throws ServletException,
			IOException {
		when(page.getTools()).thenReturn(tools).thenReturn(null);
		siteVisitToolPlacementServlet.doGet(request, response);
		verify(toolConfig, times(0)).getId();
		verify(toolConfig, times(0)).getTool();
		verify(toolConfig, times(0)).getLayoutHints();
		verify(response).setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#doGet(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Test
	public void testNormalBehaviorEmptyTools() throws ServletException,
			IOException {
		final List<ToolConfiguration> list = Collections.emptyList();
		when(page.getTools()).thenReturn(tools).thenReturn(list);
		siteVisitToolPlacementServlet.doGet(request, response);
		verify(toolConfig, times(0)).getId();
		verify(toolConfig, times(0)).getTool();
		verify(toolConfig, times(0)).getLayoutHints();
		verify(response).setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#doGet(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Test
	public void testNormalBehaviorWriteEvent() throws ServletException,
			IOException {
		when(request.getParameter("writeEvent")).thenReturn("true");
		when(
				eventTrackingService.newEvent(anyString(), anyString(),
						anyBoolean())).thenReturn(event);
		siteVisitToolPlacementServlet.doGet(request, response);
		verify(response).setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 * @throws PermissionException
	 * @throws IdUnusedException
	 * @see SiteVisitToolPlacementServlet#doGet(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Test
	public void testIdUnusedException() throws ServletException, IOException,
			IdUnusedException, PermissionException {
		when(siteService.getSiteVisit("!admin")).thenThrow(
				new IdUnusedException("!admin"));
		siteVisitToolPlacementServlet.doGet(request, response);
		verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	/**
	 * @throws PermissionException
	 * @throws IdUnusedException
	 * @throws IOException
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#doGet(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Test
	public void testPermissionException() throws IdUnusedException,
			PermissionException, ServletException, IOException {
		when(siteService.getSiteVisit("!admin")).thenThrow(
				new PermissionException("w", "w", "w"));
		siteVisitToolPlacementServlet.doGet(request, response);
		verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 * @throws GroupNotDefinedException
	 * @see SiteVisitToolPlacementServlet#doGet(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Test
	public void testGroupNotDefinedException() throws ServletException,
			IOException, GroupNotDefinedException {
		when(authzGroupService.getAuthzGroup(anyString())).thenThrow(
				new GroupNotDefinedException(""));
		siteVisitToolPlacementServlet.doGet(request, response);
		verify(response).setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * @see ResponseCommittedException
	 */
	@Test
	public void testResponseCommittedException() {
		final ResponseCommittedException responseCommittedException = new ResponseCommittedException(
				"message");
		assertNotNull(responseCommittedException);
	}

	/**
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#init(ServletConfig)
	 */
	@Test
	public void testInitConfig() throws ServletException {
		final SiteVisitToolPlacementServlet siteVisitToolPlacementServlet = new SiteVisitToolPlacementServlet();
		siteVisitToolPlacementServlet.componentManager = componentManager;
		siteVisitToolPlacementServlet.init(config);
	}

	/**
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#init(ServletConfig)
	 */
	@Test(expected = NoClassDefFoundError.class)
	public void testInitConfigNoClassDefFoundError() throws ServletException {
		final SiteVisitToolPlacementServlet siteVisitToolPlacementServlet = new SiteVisitToolPlacementServlet();
		siteVisitToolPlacementServlet.init(config);
	}

	/**
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#init(ServletConfig)
	 */
	@Test(expected = IllegalStateException.class)
	public void testInitNullSessionManager() throws ServletException {
		when(componentManager.get(SessionManager.class)).thenReturn(null);
		siteVisitToolPlacementServlet.init(config);
	}

	/**
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#init(ServletConfig)
	 */
	@Test(expected = IllegalStateException.class)
	public void testInitNullSiteService() throws ServletException {
		when(componentManager.get(SiteService.class)).thenReturn(null);
		siteVisitToolPlacementServlet.init(config);
	}

	/**
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#init(ServletConfig)
	 */
	@Test(expected = IllegalStateException.class)
	public void testInitNullEventTrackingService() throws ServletException {
		when(componentManager.get(EventTrackingService.class)).thenReturn(null);
		siteVisitToolPlacementServlet.init(config);
	}

	/**
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#init(ServletConfig)
	 */
	@Test(expected = IllegalStateException.class)
	public void testInitNullAuthzGroupService() throws ServletException {
		when(componentManager.get(AuthzGroupService.class)).thenReturn(null);
		siteVisitToolPlacementServlet.init(config);
	}

	/**
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#init(ServletConfig)
	 */
	@Test(expected = IllegalStateException.class)
	public void testInitNullSecurityService() throws ServletException {
		when(componentManager.get(SecurityService.class)).thenReturn(null);
		siteVisitToolPlacementServlet.init(config);
	}

	/**
	 * @see SiteVisitToolPlacementServlet#init(ServletConfig)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetupTestCase() {
		siteVisitToolPlacementServlet.setupTestCase(null);
	}

	/**
	 * @throws IOException
	 * @see SiteVisitToolPlacementServlet#sendError(HttpServletResponse, int,
	 *      String)
	 */
	@Test
	public void testSendError() throws IOException {
		siteVisitToolPlacementServlet.sendError(response,
				HttpServletResponse.SC_BAD_REQUEST, "message");
		verify(response, times(1))
				.sendError(HttpServletResponse.SC_BAD_REQUEST);
	}

	/**
	 * @throws IOException
	 * @see SiteVisitToolPlacementServlet#sendError(HttpServletResponse, int,
	 *      String)
	 */
	@Test(expected = ResponseCommittedException.class)
	public void testSendErrorWhenResponseCommitted() throws IOException {
		when(response.isCommitted()).thenReturn(true);
		siteVisitToolPlacementServlet.sendError(response,
				HttpServletResponse.SC_BAD_REQUEST, "message");
	}

	/**
	 * @see SiteVisitToolPlacementServlet#canAccessAtLeastOneTool(Site, List)
	 */
	@Test
	public void testCanAccessAtLeastOneToolNoPages() {
		when(site.getOrderedPages()).thenReturn(null);
		assertFalse(siteVisitToolPlacementServlet.canAccessAtLeastOneTool(site,
				new ArrayList<SitePage>()));
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#doGet(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Test(expected = IllegalAccessError.class)
	public void testNormalBehaviorNullSiteIdCommittedResponse()
			throws ServletException, IOException {
		when(request.getParameter("siteId")).thenReturn(null);
		when(response.isCommitted()).thenReturn(true);
		siteVisitToolPlacementServlet.doGet(request, response);
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 * @see SiteVisitToolPlacementServlet#doGet(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Test(expected = IllegalAccessError.class)
	public void testNormalBehaviorEmptySiteIdCommittedResponse()
			throws ServletException, IOException {
		when(request.getParameter("siteId")).thenReturn("");
		when(response.isCommitted()).thenReturn(true);
		siteVisitToolPlacementServlet.doGet(request, response);
	}

}
