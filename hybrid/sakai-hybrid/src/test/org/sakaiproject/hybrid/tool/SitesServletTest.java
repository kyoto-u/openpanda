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

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sakaiproject.hybrid.test.TestHelper.disableLog4jDebug;
import static org.sakaiproject.hybrid.test.TestHelper.enableLog4jDebug;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import org.sakaiproject.api.app.messageforums.SynopticMsgcntrItem;
import org.sakaiproject.api.app.messageforums.SynopticMsgcntrManager;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.component.api.ComponentManager;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.entity.api.EntityPropertyNotDefinedException;
import org.sakaiproject.entity.api.EntityPropertyTypeException;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.Preferences;
import org.sakaiproject.user.api.PreferencesService;
import org.sakaiproject.user.api.User;

@RunWith(MockitoJUnitRunner.class)
public class SitesServletTest {
	private static final String UID = "admin";
	private static final String EID = UID;

	@Mock
	protected SitesServlet sitesServlet;
	@Mock
	protected SessionManager sessionManager;
	@Mock
	protected SiteService siteService;
	@Mock
	protected SynopticMsgcntrManager synopticMsgcntrManager;
	@Mock
	protected HttpServletRequest request;
	@Mock
	protected HttpServletResponse response;
	@Mock
	protected ComponentManager componentManager;
	@Mock
	protected ServerConfigurationService serverConfigurationService;
	@Mock
	protected transient PreferencesService preferencesService;
	@Mock
	protected ServletConfig config;
	@Mock
	protected Session session;
	@Mock(name = "!admin")
	protected Site site;
	@Mock(name = "courseSiteGoodTerm")
	protected transient Site courseSiteGoodTerm;
	@Mock
	protected transient ResourceProperties courseSiteGoodProperties;
	@Mock(name = "My Workspace")
	protected Site myWorkSpace;
	@Mock
	protected User user;
	@Mock
	protected Set<Member> members;
	@Mock
	protected PrintWriter writer;
	@Mock
	protected Preferences preferences;
	@Mock
	protected ResourceProperties resourceProperties;
	@Mock
	SynopticMsgcntrItem synopticMsgcntrItem1;
	@Mock
	MoreSiteViewImpl moreSiteViewImpl;
	List<Map<String, List<Site>>> categorizedSitesList = null;
	@Mock
	Map<String, List<Site>> map;
	protected transient String[] termOrder = null;

	@BeforeClass
	public static void beforeClass() {
		enableLog4jDebug();
	}

	@Before
	public void setUp() throws Exception {
		when(sessionManager.getCurrentSession()).thenReturn(session);
		when(session.getUserEid()).thenReturn(EID);
		when(sessionManager.getCurrentSessionUserId()).thenReturn(UID);
		when(site.getTitle()).thenReturn("Administration Workspace");
		when(site.getId()).thenReturn("!admin");
		when(site.getUrl())
				.thenReturn(
						"http://sakai3-nightly.uits.indiana.edu:8080/portal/site/!admin");
		when(site.getIconUrl()).thenReturn(null);
		when(user.getDisplayName()).thenReturn("Sakai Administrator");
		when(site.getCreatedBy()).thenReturn(user);
		when(members.size()).thenReturn(1);
		when(site.getMembers()).thenReturn(members);
		when(site.getDescription()).thenReturn("Administration Workspace");
		when(site.getType()).thenReturn(null);
		when(site.getCreatedDate()).thenReturn(new Date());
		List<Site> siteList = new ArrayList<Site>();
		siteList.add(site);
		termOrder = new String[] { "FALL 2010", "SUMMER 2010", "SPRING 2010" };
		when(serverConfigurationService.getStrings("portal.term.order"))
				.thenReturn(termOrder);
		when(courseSiteGoodTerm.getId()).thenReturn("1q2w3e4r");
		when(courseSiteGoodTerm.getType()).thenReturn("course");
		when(courseSiteGoodTerm.getProperties()).thenReturn(
				courseSiteGoodProperties);
		when(courseSiteGoodProperties.getProperty("term")).thenReturn(
				termOrder[0]);
		siteList.add(courseSiteGoodTerm);
		when(
				siteService
						.getSites(
								org.sakaiproject.site.api.SiteService.SelectionType.ACCESS,
								null,
								null,
								null,
								org.sakaiproject.site.api.SiteService.SortType.TITLE_ASC,
								null)).thenReturn(siteList);
		when(siteService.getUserSiteId(UID)).thenReturn("~admin");
		when(siteService.getSite("~admin")).thenReturn(myWorkSpace);
		when(myWorkSpace.getId()).thenReturn("~admin");
		when(preferencesService.getPreferences(UID)).thenReturn(preferences);
		when(preferences.getProperties("sakai:portal:sitenav")).thenReturn(
				resourceProperties);
		when(resourceProperties.getLongProperty("tabs")).thenReturn(11L);
		when(response.getWriter()).thenReturn(writer);
		when(componentManager.get(SessionManager.class)).thenReturn(
				sessionManager);
		when(componentManager.get(SiteService.class)).thenReturn(siteService);
		when(componentManager.get(ServerConfigurationService.class))
				.thenReturn(serverConfigurationService);
		when(componentManager.get(SynopticMsgcntrManager.class)).thenReturn(
				synopticMsgcntrManager);
		when(componentManager.get(PreferencesService.class)).thenReturn(
				preferencesService);
		when(request.getParameter(SitesServlet.CATEGORIZED)).thenReturn("true");
		when(request.getParameter(SitesServlet.UNREAD)).thenReturn("true");
		when(request.getParameter(SitesServlet.LOCALE)).thenReturn("en_US");
		when(request.getLocale()).thenReturn(Locale.getDefault());
		when(synopticMsgcntrItem1.getSiteId()).thenReturn("!admin");
		when(synopticMsgcntrItem1.getNewForumCount()).thenReturn(7);
		when(synopticMsgcntrItem1.getNewMessagesCount()).thenReturn(13);
		final List<SynopticMsgcntrItem> synopticMsgcntrItems = new ArrayList<SynopticMsgcntrItem>();
		synopticMsgcntrItems.add(synopticMsgcntrItem1);
		when(synopticMsgcntrManager.getWorkspaceSynopticMsgcntrItems(UID))
				.thenReturn(synopticMsgcntrItems);
		sitesServlet = new SitesServlet();
		sitesServlet.setupTestCase(componentManager);
		sitesServlet.init(config);
	}

	/**
	 * Tests {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testDoGetNormalBehavior() throws ServletException, IOException {
		sitesServlet.doGet(request, response);
		verifyDoGet(response);
	}

	/**
	 * Increases cobertura coverage report. Tests
	 * {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testDoGetNormalBehaviorLogDebugDisabled()
			throws ServletException, IOException {
		disableLog4jDebug();
		sitesServlet.doGet(request, response);
		verifyDoGet(response);
		enableLog4jDebug();
	}

	/**
	 * Tests {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testDoGetNullEid() throws ServletException, IOException {
		when(session.getUserEid()).thenReturn(null);
		sitesServlet.doGet(request, response);
		verifyDoGet(response);
	}

	/**
	 * Tests {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testDoGetEmptyEid() throws ServletException, IOException {
		when(session.getUserEid()).thenReturn("");
		sitesServlet.doGet(request, response);
		verifyDoGet(response);
	}

	/**
	 * Tests {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testDoGetLocaleLanguage() throws ServletException, IOException {
		when(request.getParameter(SitesServlet.LOCALE)).thenReturn("es");
		sitesServlet.doGet(request, response);
		verifyDoGet(response);
	}

	/**
	 * Tests {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testDoGetLocaleWithVariant() throws ServletException,
			IOException {
		when(request.getParameter(SitesServlet.LOCALE)).thenReturn(
				"es_ES_Traditional");
		sitesServlet.doGet(request, response);
		verifyDoGet(response);
	}

	/**
	 * Tests {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testDoGetLocaleWithVariant2() throws ServletException,
			IOException {
		/*
		 * While multiple variants is not currently supported in the get
		 * parameter parser, they should not break anything.
		 */
		when(request.getParameter(SitesServlet.LOCALE)).thenReturn(
				"es_ES_Traditional_MAC");
		sitesServlet.doGet(request, response);
		verifyDoGet(response);
	}

	/**
	 * Tests {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testDoGetLocaleWithVariant2LogDebugDisabled()
			throws ServletException, IOException {
		disableLog4jDebug();
		/*
		 * While multiple variants is not currently supported in the get
		 * parameter parser, they should not break anything.
		 */
		when(request.getParameter(SitesServlet.LOCALE)).thenReturn(
				"es_ES_Traditional_MAC");
		sitesServlet.doGet(request, response);
		verifyDoGet(response);
		enableLog4jDebug();
	}

	/**
	 * Tests {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test(expected = IOException.class)
	public void testIOException() throws ServletException, IOException {
		when(response.getWriter()).thenThrow(new IOException());
		sitesServlet.doGet(request, response);
	}

	/**
	 * @see SitesServlet#init(ServletConfig)
	 * @throws ServletException
	 */
	@Test
	public void testInitConfig() throws ServletException {
		final SitesServlet sitesServlet = new SitesServlet();
		sitesServlet.componentManager = componentManager;
		sitesServlet.init(config);
	}

	/**
	 * @see SitesServlet#init(ServletConfig)
	 * @throws ServletException
	 */
	@Test(expected = NoClassDefFoundError.class)
	public void testInitConfigNoClassDefFoundError() throws ServletException {
		final SitesServlet sitesServlet = new SitesServlet();
		sitesServlet.init(config);
	}

	/**
	 * Tests {@link SitesServlet#init(ServletConfig)}
	 * 
	 * @throws ServletException
	 */
	@Test(expected = IllegalStateException.class)
	public void testInitNullSessionManager() throws ServletException {
		when(componentManager.get(SessionManager.class)).thenReturn(null);
		sitesServlet.init(config);
	}

	/**
	 * Tests {@link SitesServlet#init(ServletConfig)}
	 * 
	 * @throws ServletException
	 */
	@Test(expected = IllegalStateException.class)
	public void testInitNullSiteService() throws ServletException {
		when(componentManager.get(SiteService.class)).thenReturn(null);
		sitesServlet.init(config);
	}

	/**
	 * Tests {@link SitesServlet#init(ServletConfig)}
	 * 
	 * @throws ServletException
	 */
	@Test(expected = IllegalStateException.class)
	public void testInitNullServerConfigurationService()
			throws ServletException {
		when(componentManager.get(ServerConfigurationService.class))
				.thenReturn(null);
		sitesServlet.init(config);
	}

	/**
	 * Tests {@link SitesServlet#init(ServletConfig)}
	 * 
	 * @throws ServletException
	 */
	@Test(expected = IllegalStateException.class)
	public void testInitNullSynopticMsgcntrManager() throws ServletException {
		when(componentManager.get(SynopticMsgcntrManager.class)).thenReturn(
				null);
		sitesServlet.init(config);
	}

	/**
	 * Tests {@link SitesServlet#init(ServletConfig)}
	 * 
	 * @throws ServletException
	 */
	@Test(expected = IllegalStateException.class)
	public void testInitNullPreferencesService() throws ServletException {
		when(componentManager.get(PreferencesService.class)).thenReturn(null);
		sitesServlet.init(config);
	}

	/**
	 * Tests {@link SitesServlet#setupTestCase(ComponentManager)}
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetupTestCase() {
		sitesServlet.setupTestCase(null);
	}

	/**
	 * Tests {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @throws IOException
	 * @throws ServletException
	 * @throws EntityPropertyTypeException
	 * @throws EntityPropertyNotDefinedException
	 */
	@Test
	public void testEntityPropertyNotDefinedException()
			throws ServletException, IOException,
			EntityPropertyNotDefinedException, EntityPropertyTypeException {
		when(resourceProperties.getLongProperty("tabs")).thenThrow(
				new EntityPropertyNotDefinedException());
		sitesServlet.doGet(request, response);
		verifyDoGet(response);
	}

	/**
	 * Tests {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @throws IOException
	 * @throws ServletException
	 * @throws EntityPropertyTypeException
	 * @throws EntityPropertyNotDefinedException
	 */
	@Test(expected = IllegalStateException.class)
	public void testEntityPropertyTypeException() throws ServletException,
			IOException, EntityPropertyNotDefinedException,
			EntityPropertyTypeException {
		when(resourceProperties.getLongProperty("tabs")).thenThrow(
				new EntityPropertyTypeException("message"));
		sitesServlet.doGet(request, response);
	}

	/**
	 * Tests {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testNullPreferences() throws ServletException, IOException {
		when(preferencesService.getPreferences(UID)).thenReturn(null);
		sitesServlet.doGet(request, response);
		verifyDoGet(response);
	}

	/**
	 * Tests {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @throws IOException
	 * @throws ServletException
	 * @throws IdUnusedException
	 */
	@Test
	public void testIdUnusedException() throws ServletException, IOException,
			IdUnusedException {
		when(siteService.getSite("~admin")).thenThrow(
				new IdUnusedException("message"));
		sitesServlet.doGet(request, response);
		verifyDoGet(response);
	}

	/**
	 * Tests {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @throws IdUnusedException
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testIdUnusedException2() throws IdUnusedException,
			ServletException, IOException {
		disableLog4jDebug();
		when(siteService.getSite("~admin")).thenThrow(
				new IdUnusedException("message"));
		sitesServlet.doGet(request, response);
		verifyDoGet(response);
		enableLog4jDebug();
	}

	/**
	 * Tests {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testDoGetNotCategorized() throws ServletException, IOException {
		when(request.getParameter(SitesServlet.CATEGORIZED))
				.thenReturn("false");
		sitesServlet.doGet(request, response);
		verifyDoGet(response);
	}

	/**
	 * Tests {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testDoGetNoUnread() throws ServletException, IOException {
		when(request.getParameter(SitesServlet.UNREAD)).thenReturn("false");
		sitesServlet.doGet(request, response);
		verifyDoGet(response);
	}

	/**
	 * Tests {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testDoGetNoLocale() throws ServletException, IOException {
		when(request.getParameter(SitesServlet.LOCALE)).thenReturn(null);
		sitesServlet.doGet(request, response);
		verifyDoGet(response);
	}

	/**
	 * Tests {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testDoGetZeroCountSynoptic() throws ServletException,
			IOException {
		when(synopticMsgcntrItem1.getNewForumCount()).thenReturn(0);
		when(synopticMsgcntrItem1.getNewMessagesCount()).thenReturn(0);
		sitesServlet.doGet(request, response);
		verifyDoGet(response);
	}

	/**
	 * Tests {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testDoGetNullSiteList() throws ServletException, IOException {
		when(
				siteService
						.getSites(
								org.sakaiproject.site.api.SiteService.SelectionType.ACCESS,
								null,
								null,
								null,
								org.sakaiproject.site.api.SiteService.SortType.TITLE_ASC,
								null)).thenReturn(null);
		sitesServlet.doGet(request, response);
		verifyDoGet(response);
	}

  /**
   * Tests {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
   * 
   * @throws IOException
   * @throws ServletException
   */
  @Test
  public void testDoGetEmptySiteList() throws ServletException, IOException {
    when(
        siteService
            .getSites(
                org.sakaiproject.site.api.SiteService.SelectionType.ACCESS,
                null,
                null,
                null,
                org.sakaiproject.site.api.SiteService.SortType.TITLE_ASC,
                null)).thenReturn(new ArrayList<Site>());
    sitesServlet.doGet(request, response);
    verifyDoGet(response);
  }

	/**
	 * Tests {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testDoGetNullSynopticMsgcntrItems() throws ServletException,
			IOException {
		when(synopticMsgcntrManager.getWorkspaceSynopticMsgcntrItems(UID))
				.thenReturn(null);
		sitesServlet.doGet(request, response);
		verifyDoGet(response);
	}

	/**
	 * Tests {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = IllegalStateException.class)
	public void testDoGetMapSizeIllegalStateException()
			throws ServletException, IOException {
		when(map.size()).thenReturn(7);
		categorizedSitesList = new ArrayList<Map<String, List<Site>>>();
		categorizedSitesList.add(map);
		when(moreSiteViewImpl.categorizeSites((List<Site>) anyObject()))
				.thenReturn(categorizedSitesList);
		sitesServlet.moreSiteViewImpl = moreSiteViewImpl;
		sitesServlet.doGet(request, response);
	}

	/**
	 * Verifies the normal flow of
	 * {@link SitesServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 * 
	 * @param response
	 *            response
	 * @throws IOException
	 */
	private void verifyDoGet(final HttpServletResponse response)
			throws IOException {
		verify(response, times(1)).setContentType("application/json");
		verify(response, times(1)).setCharacterEncoding("UTF-8");
		verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
		verify(response, times(1)).getWriter();
	}
}
