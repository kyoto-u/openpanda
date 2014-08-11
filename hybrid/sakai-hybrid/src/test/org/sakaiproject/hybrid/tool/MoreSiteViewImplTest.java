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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.sakaiproject.hybrid.test.TestHelper.disableLog4jDebug;
import static org.sakaiproject.hybrid.test.TestHelper.enableLog4jDebug;
import static org.sakaiproject.hybrid.tool.MoreSiteViewImpl.DEFAULT_SORT_ORDER;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.site.api.Site;

/**
 * @see MoreSiteViewImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class MoreSiteViewImplTest {
	protected transient MoreSiteViewImpl moreSiteViewImpl = null;
	protected transient List<Site> siteList;
	protected transient String[] termOrder = null;

	@Mock
	protected transient ServerConfigurationService serverConfigurationService;
	@Mock
	protected transient Site courseSiteGoodTerm;
	@Mock
	protected transient ResourceProperties courseSiteGoodProperties;
	@Mock
	protected transient Site courseSiteNullTerm;
	@Mock
	protected transient ResourceProperties courseSiteNullTermProperties;
	@Mock
	protected transient Site projectSite;
	@Mock
	protected transient Site portfolioSite;
	@Mock
	protected transient Site adminSite;
	@Mock
	protected transient Site otherSite;

	@BeforeClass
	public static void beforeClass() {
		enableLog4jDebug();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		termOrder = new String[] { "FALL 2010", "SUMMER 2010", "SPRING 2010" };
		when(serverConfigurationService.getStrings("portal.term.order"))
				.thenReturn(termOrder);

		siteList = new ArrayList<Site>();
		when(courseSiteGoodTerm.getType()).thenReturn("course");
		when(courseSiteGoodTerm.getProperties()).thenReturn(
				courseSiteGoodProperties);
		when(courseSiteGoodProperties.getProperty("term")).thenReturn(
				termOrder[0]);
		siteList.add(courseSiteGoodTerm);

		when(courseSiteNullTerm.getType()).thenReturn("course");
		when(courseSiteNullTerm.getProperties()).thenReturn(
				courseSiteNullTermProperties);
		when(courseSiteNullTermProperties.getProperty("term")).thenReturn(null);
		siteList.add(courseSiteNullTerm);
		when(projectSite.getType()).thenReturn("project");
		siteList.add(projectSite);
		when(portfolioSite.getType()).thenReturn("portfolio");
		siteList.add(portfolioSite);
		when(adminSite.getType()).thenReturn("admin");
		siteList.add(adminSite);
		when(otherSite.getType()).thenReturn("somethingelse");
		siteList.add(otherSite);

		moreSiteViewImpl = new MoreSiteViewImpl(serverConfigurationService);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.tool.MoreSiteViewImpl#MoreSiteViewImpl(org.sakaiproject.component.api.ServerConfigurationService)}
	 * .
	 */
	@Test
	public void testMoreSiteViewImpl() {
		moreSiteViewImpl = new MoreSiteViewImpl(serverConfigurationService);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.tool.MoreSiteViewImpl#MoreSiteViewImpl(org.sakaiproject.component.api.ServerConfigurationService)}
	 * .
	 */
	@Test
	public void testMoreSiteViewImplLogDebugDisabled() {
		disableLog4jDebug();
		moreSiteViewImpl = new MoreSiteViewImpl(serverConfigurationService);
		enableLog4jDebug();
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.tool.MoreSiteViewImpl#MoreSiteViewImpl(org.sakaiproject.component.api.ServerConfigurationService)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testMoreSiteViewImplNullServerConfigurationService() {
		moreSiteViewImpl = new MoreSiteViewImpl(null);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.tool.MoreSiteViewImpl#categorizeSites(java.util.List)}
	 * .
	 */
	@Test
	public void testCategorizeSites() {
		List<Map<String, List<Site>>> categorizedSites = null;
		categorizedSites = moreSiteViewImpl.categorizeSites(siteList);
		assertNotNull(categorizedSites);
		assertTrue("categorizedSites.size() should equal 7; "
				+ "All Sites + one academic term + Five different site types.",
				categorizedSites.size() == 7);
		for (int i = 0; i < categorizedSites.size(); i++) {
			final Map<String, List<Site>> map = categorizedSites.get(i);
			assertTrue(
					"The categorized maps must contain only one key per map!",
					map.size() == 1);
			// verify sort order
			for (final Entry<String, List<Site>> entry : map.entrySet()) {
				final String key = entry.getKey();
				final List<Site> sites = entry.getValue();
				switch (i) {
				case 0:
					assertTrue("All Sites should be first in list",
							MoreSiteViewImpl.I18N_ALL_SITES.equals(key));
					assertTrue(sites.size() == 6);
					assertTrue(sites.contains(courseSiteGoodTerm));
					assertTrue(sites.contains(courseSiteNullTerm));
					assertTrue(sites.contains(projectSite));
					assertTrue(sites.contains(portfolioSite));
					assertTrue(sites.contains(adminSite));
					assertTrue(sites.contains(otherSite));
					break;
				case 1:
					assertTrue(
							termOrder[0] + " sites should be second in list",
							termOrder[0].equals(key));
					assertTrue(sites.size() == 1);
					assertTrue(sites.contains(courseSiteGoodTerm));
					break;
				case 2:
					assertTrue(
							"Unknown course term sites should be third in list",
							DEFAULT_SORT_ORDER.get(1).equals(key));
					assertTrue(sites.size() == 1);
					assertTrue(sites.contains(courseSiteNullTerm));
					break;
				case 3:
					assertTrue("Portfolio sites should be third in list",
							DEFAULT_SORT_ORDER.get(2).equals(key));
					assertTrue(sites.size() == 1);
					assertTrue(sites.contains(portfolioSite));
					break;
				case 4:
					assertTrue("Project sites should be third in list",
							DEFAULT_SORT_ORDER.get(3).equals(key));
					assertTrue(sites.size() == 1);
					assertTrue(sites.contains(projectSite));
					break;
				case 5:
					assertTrue("Other sites should be third in list",
							DEFAULT_SORT_ORDER.get(4).equals(key));
					assertTrue(sites.size() == 1);
					assertTrue(sites.contains(otherSite));
					break;
				case 6:
					assertTrue("Admin sites should be third in list",
							DEFAULT_SORT_ORDER.get(5).equals(key));
					assertTrue(sites.size() == 1);
					assertTrue(sites.contains(adminSite));
					break;
				default:
					fail("We already know there are 7 categories");
					break;
				}
			}
		}
	}

	/**
	 * Test for default sort order if no portal.term.order has been defined.
	 * Test method for
	 * {@link org.sakaiproject.hybrid.tool.MoreSiteViewImpl#categorizeSites(java.util.List)}
	 * .
	 */
	@Test
	public void testCategorizeSitesNoTermOrder() {
		when(serverConfigurationService.getStrings("portal.term.order"))
				.thenReturn(null);
		List<Map<String, List<Site>>> categorizedSites = null;
		categorizedSites = moreSiteViewImpl.categorizeSites(siteList);
		assertNotNull(categorizedSites);
		assertTrue("categorizedSites.size() should equal 7; "
				+ "All Sites + one academic term + Five different site types.",
				categorizedSites.size() == 7);
		for (int i = 0; i < categorizedSites.size(); i++) {
			final Map<String, List<Site>> map = categorizedSites.get(i);
			assertTrue(
					"The categorized maps must contain only one key per map!",
					map.size() == 1);
			// verify sort order
			for (final Entry<String, List<Site>> entry : map.entrySet()) {
				final String key = entry.getKey();
				final List<Site> sites = entry.getValue();
				switch (i) {
				case 0:
					assertTrue("All Sites should be first in list",
							MoreSiteViewImpl.I18N_ALL_SITES.equals(key));
					assertTrue(sites.size() == 6);
					assertTrue(sites.contains(courseSiteGoodTerm));
					assertTrue(sites.contains(courseSiteNullTerm));
					assertTrue(sites.contains(projectSite));
					assertTrue(sites.contains(portfolioSite));
					assertTrue(sites.contains(adminSite));
					assertTrue(sites.contains(otherSite));
					break;
				case 1:
					// TODO this time only because termOrder[0]...
					assertTrue(
							termOrder[0] + " sites should be second in list",
							termOrder[0].equals(key));
					assertTrue(sites.size() == 1);
					assertTrue(sites.contains(courseSiteGoodTerm));
					break;
				case 2:
					assertTrue(
							"Unknown course term sites should be third in list",
							DEFAULT_SORT_ORDER.get(1).equals(key));
					assertTrue(sites.size() == 1);
					assertTrue(sites.contains(courseSiteNullTerm));
					break;
				case 3:
					assertTrue("Portfolio sites should be third in list",
							DEFAULT_SORT_ORDER.get(2).equals(key));
					assertTrue(sites.size() == 1);
					assertTrue(sites.contains(portfolioSite));
					break;
				case 4:
					assertTrue("Project sites should be third in list",
							DEFAULT_SORT_ORDER.get(3).equals(key));
					assertTrue(sites.size() == 1);
					assertTrue(sites.contains(projectSite));
					break;
				case 5:
					assertTrue("Other sites should be third in list",
							DEFAULT_SORT_ORDER.get(4).equals(key));
					assertTrue(sites.size() == 1);
					assertTrue(sites.contains(otherSite));
					break;
				case 6:
					assertTrue("Admin sites should be third in list",
							DEFAULT_SORT_ORDER.get(5).equals(key));
					assertTrue(sites.size() == 1);
					assertTrue(sites.contains(adminSite));
					break;
				default:
					fail("We already know there are 7 categories");
					break;
				}
			}
		}
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.tool.MoreSiteViewImpl#categorizeSites(java.util.List)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCategorizeSitesNullSiteList() {
		moreSiteViewImpl.categorizeSites(null);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.tool.MoreSiteViewImpl#categorizeSites(java.util.List)}
	 * .
	 */
	@Test(expected = IllegalStateException.class)
	public void testCategorizeSitesNullServerConfigurationService() {
		moreSiteViewImpl.serverConfigurationService = null;
		moreSiteViewImpl.categorizeSites(siteList);
	}

}
