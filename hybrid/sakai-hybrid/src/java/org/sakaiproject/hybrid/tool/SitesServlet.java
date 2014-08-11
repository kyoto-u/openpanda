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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.app.messageforums.SynopticMsgcntrItem;
import org.sakaiproject.api.app.messageforums.SynopticMsgcntrManager;
import org.sakaiproject.component.api.ComponentManager;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.entity.api.EntityPropertyNotDefinedException;
import org.sakaiproject.entity.api.EntityPropertyTypeException;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.Preferences;
import org.sakaiproject.user.api.PreferencesService;

/**
 * Based on
 * https://source.caret.cam.ac.uk/camtools/trunk/camtools/sdata/tool/sakai
 * -sdata-impl/src/main/java/org/sakaiproject/sdata/services/mcp/
 * MyCoursesAndProjectsBean.java
 * <p>
 * No required get parameters. Runs in the context of the current user. Returns
 * all sites that the user has access to visit.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "MTIA_SUSPECT_SERVLET_INSTANCE_FIELD", justification = "dependencies only mutated only during init()")
@SuppressWarnings({ "PMD.LongVariable", "PMD.CyclomaticComplexity",
		"PMD.ExcessiveImports" })
public class SitesServlet extends HttpServlet {
	private static final long serialVersionUID = 7907409301065984518L;
	private static final Log LOG = LogFactory.getLog(SitesServlet.class);

	/**
	 * Optional GET parameter which categorizes the JSON by site term and type.
	 */
	public static final String CATEGORIZED = "categorized";
	/**
	 * Optional GET parameter which will include Messages and Forums unread
	 * counts.
	 */
	public static final String UNREAD = "unread";

	/**
	 * Optional GET parameter which specifies locale. For example: en_US.
	 * 
	 * @see Locale
	 */
	public static final String LOCALE = "l";

	private static final String UNDERSCORE = "_";
	private static final char TILDE = "~".charAt(0);
	private static final String MSF_MUTABLE_SERVLET_FIELD = "MSF_MUTABLE_SERVLET_FIELD";
	private static final String DEPENDENCY_ONLY_MUTATED_DURING_INIT = "dependency mutated only during init()";

	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = MSF_MUTABLE_SERVLET_FIELD, justification = DEPENDENCY_ONLY_MUTATED_DURING_INIT)
	protected transient SessionManager sessionManager;
	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = MSF_MUTABLE_SERVLET_FIELD, justification = DEPENDENCY_ONLY_MUTATED_DURING_INIT)
	protected transient SiteService siteService;
	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = MSF_MUTABLE_SERVLET_FIELD, justification = DEPENDENCY_ONLY_MUTATED_DURING_INIT)
	protected transient ServerConfigurationService serverConfigurationService;
	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = MSF_MUTABLE_SERVLET_FIELD, justification = DEPENDENCY_ONLY_MUTATED_DURING_INIT)
	protected transient ComponentManager componentManager;
	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = MSF_MUTABLE_SERVLET_FIELD, justification = DEPENDENCY_ONLY_MUTATED_DURING_INIT)
	protected transient SynopticMsgcntrManager synopticMsgcntrManager;
	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = MSF_MUTABLE_SERVLET_FIELD, justification = DEPENDENCY_ONLY_MUTATED_DURING_INIT)
	protected transient PreferencesService preferencesService;
	protected transient MoreSiteViewImpl moreSiteViewImpl;

	@Override
	@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.NPathComplexity",
			"PMD.DataflowAnomalyAnalysis", "PMD.AvoidDeeplyNestedIfStmts",
			"PMD.AvoidInstantiatingObjectsInLoops", "PMD.ExcessiveMethodLength" })
	protected void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("doGet(HttpServletRequest " + request
					+ ", HttpServletResponse " + response + ")");
		}
		final boolean categorized = Boolean.parseBoolean(request
				.getParameter(CATEGORIZED));
		final boolean unread = Boolean.parseBoolean(request
				.getParameter(UNREAD));

		final Locale locale = getLocale(request);
		final ResourceBundle resourceBundle = ResourceBundle.getBundle(
				"sitenav", locale);

		if (LOG.isDebugEnabled()) {
			LOG.debug(CATEGORIZED + "=" + categorized + "; " + UNREAD + "="
					+ unread + "; " + LOCALE + "=" + locale);
		}

		// sites for current user
		Site myWorkSpace = null;
		final JSONObject json = new JSONObject();
		final String uid = sessionManager.getCurrentSessionUserId();
		final String eid = sessionManager.getCurrentSession().getUserEid();
		if (eid == null || "".equals(eid)) {
			json.element("principal", "anonymous");
		} else {
			json.element("principal", eid);
			try {
				myWorkSpace = siteService.getSite(siteService
						.getUserSiteId(uid));
			} catch (IdUnusedException e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("My Workspace could not be found for user: "
							+ eid);
				}
			}
		}
		final List<Site> siteList = siteService.getSites(
				org.sakaiproject.site.api.SiteService.SelectionType.ACCESS,
				null, null, null,
				org.sakaiproject.site.api.SiteService.SortType.TITLE_ASC, null);
		if (siteList != null && siteList.size() > 0) {
			// add My Workspace to beginning of list
			if (myWorkSpace != null) {
				siteList.add(0, myWorkSpace);
			}
			// collect the user's preferences
			final PortalSiteNavUserPreferences userPrefs = new PortalSiteNavUserPreferences(
					preferencesService.getPreferences(uid));
			json.element("display", userPrefs.getPrefTabs());

			// initialize values to an empty map to avoid null check later
			Map<String, Integer> unreadForums = Collections.emptyMap();
			Map<String, Integer> unreadMessages = unreadForums;
			if (unread) {
				final List<SynopticMsgcntrItem> synopticMsgcntrItems = synopticMsgcntrManager
						.getWorkspaceSynopticMsgcntrItems(uid);
				if (synopticMsgcntrItems != null) {
					final int initialCapacity = synopticMsgcntrItems.size();
					unreadForums = new HashMap<String, Integer>(initialCapacity);
					unreadMessages = new HashMap<String, Integer>(
							initialCapacity);
					for (SynopticMsgcntrItem synopticMsgcntrItem : synopticMsgcntrItems) {
						final String siteId = synopticMsgcntrItem.getSiteId();
						final int forumCount = synopticMsgcntrItem
								.getNewForumCount();
						// omit counts < 1
						if (forumCount > 0) {
							unreadForums.put(siteId, forumCount);
						}
						final int messageCount = synopticMsgcntrItem
								.getNewMessagesCount();
						// omit counts < 1
						if (messageCount > 0) {
							unreadMessages.put(siteId, messageCount);
						}
					}
				}
			}
			if (categorized) {
				final List<Map<String, List<Site>>> categorizedSitesList = moreSiteViewImpl
						.categorizeSites(siteList);
				final JSONArray categoriesArrayJson = new JSONArray();
				for (final Map<String, List<Site>> map : categorizedSitesList) {
					if (map.size() != 1) {
						throw new IllegalStateException(
								"The categorized maps must contain only one key per map!");
					}
					for (final Entry<String, List<Site>> entry : map.entrySet()) {
						final String category = entry.getKey();
						final List<Site> sortedSites = entry.getValue();
						final JSONObject categoryJson = new JSONObject();
						if (MoreSiteViewImpl.DEFAULT_SORT_ORDER
								.contains(category)) {
							// well known resourceBundle key
							categoryJson.element("category",
									resourceBundle.getString(category));
						} else {
							// not well known and cannot be i18n
							categoryJson.element("category", category);
						}
						final JSONArray sitesArrayJson = new JSONArray();
						for (final Site site : sortedSites) {
							sitesArrayJson.add(renderSiteJson(site,
									unreadForums, unreadMessages,
									resourceBundle));
						}
						categoryJson.element("sites", sitesArrayJson);
						categoriesArrayJson.add(categoryJson);
					}
				}
				json.element("categories", categoriesArrayJson);
			} else { // not categorized
				final JSONArray sitesArrayJson = new JSONArray();
				for (Site site : siteList) {
					sitesArrayJson.add(renderSiteJson(site, unreadForums,
							unreadMessages, resourceBundle));
				}
				json.element("sites", sitesArrayJson);
			}
		}
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		json.write(response.getWriter());
	}

	private JSONObject renderSiteJson(final Site site,
			final Map<String, Integer> unreadForums,
			final Map<String, Integer> unreadMessages,
			final ResourceBundle resourceBundle) {
		final JSONObject siteJson = new JSONObject();
		final String siteId = site.getId();
		// i18n My Workspace
		if (TILDE == siteId.charAt(0)) { // startsWith "~"
			siteJson.element("title", resourceBundle.getString("sit_mywor"));
		} else {
			siteJson.element("title", site.getTitle());
		}
		siteJson.element("id", siteId);
		siteJson.element("url", site.getUrl());
		siteJson.element("description", site.getDescription());
		siteJson.element("forums", unreadForums.get(siteId));
		siteJson.element("messages", unreadMessages.get(siteId));
		// siteJson.element("iconUrl", site.getIconUrl());
		// siteJson.element("owner",
		// site.getCreatedBy().getDisplayName());
		// siteJson.element("members", site.getMembers().size());
		// siteJson.element("siteType", site.getType());
		// TO DO ISO8601 date format or other?
		// siteJson.element("creationDate", new
		// SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz")
		// .format(site.getCreatedDate()));
		return siteJson;
	}

	/**
	 * 
	 * @param request
	 *            request
	 * @return Null will not be returned. By default, request.getLocale() if
	 *         none is specified as get parameter.
	 * @throws IOException
	 */
	@SuppressWarnings({ "PMD.DataflowAnomalyAnalysis", "PMD.OnlyOneReturn" })
	private Locale getLocale(final HttpServletRequest request)
			throws IOException {
		// Locale parameter
		Locale locale = null;
		final String localeParam = request.getParameter(LOCALE);
		if (localeParam != null) {
			final int underscore = localeParam.indexOf(UNDERSCORE);
			if (underscore > -1) {
				// a multi-part locale has been passed
				final String[] parts = localeParam.split(UNDERSCORE);
				switch (parts.length) {
				case 2:
					// both language and country code
					locale = new Locale(parts[0], parts[1]);
					break;
				case 3:
					// language, country code, and variant passed
					locale = new Locale(parts[0], parts[1], parts[2]);
					break;
				default:
					// language parameter must contain two or three parts!
					if (LOG.isDebugEnabled()) {
						LOG.debug("Illegal locale request parameter: "
								+ localeParam);
					}
					break;
				}
			} else {
				// just language code supplied
				locale = new Locale(localeParam);
			}
		}
		if (locale == null) {
			// default to Accept-Language header if none specified
			locale = request.getLocale();
		}
		return locale;
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
		siteService = (SiteService) componentManager.get(SiteService.class);
		if (siteService == null) {
			throw new IllegalStateException("SiteService == null");
		}
		if (sessionManager == null) {
			throw new IllegalStateException("SessionManager == null");
		}
		serverConfigurationService = (ServerConfigurationService) componentManager
				.get(ServerConfigurationService.class);
		if (serverConfigurationService == null) {
			throw new IllegalStateException(
					"ServerConfigurationService == null");
		}
		synopticMsgcntrManager = (SynopticMsgcntrManager) componentManager
				.get(SynopticMsgcntrManager.class);
		if (synopticMsgcntrManager == null) {
			throw new IllegalStateException("SynopticMsgcntrManager == null");
		}
		preferencesService = (PreferencesService) componentManager
				.get(PreferencesService.class);
		if (preferencesService == null) {
			throw new IllegalStateException("PreferencesService == null");
		}
		moreSiteViewImpl = new MoreSiteViewImpl(serverConfigurationService);
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
	 * Wraps Sakai2 portal functionality around number of sites to display.
	 * Immutable helper class.
	 * <p>
	 * Logic inspired by: <a href=
	 * "https://source.sakaiproject.org/svn/portal/trunk/portal-impl/impl/src/java/org/sakaiproject/portal/charon/CharonPortal.java"
	 * >CharonPortal.java@85021</a> Lines 2412-2442
	 */
	protected static class PortalSiteNavUserPreferences {
		/**
		 * The default number of sites that will be displayed
		 */
		public static final int DEFAULT_TABS = 4;

		private final static Log LOG = LogFactory
				.getLog(PortalSiteNavUserPreferences.class);

		/**
		 * Number of sites to display according to Sakai2
		 */
		private final transient int prefTabs;

		/**
		 * @param preferences
		 *            Null values are supported and will return default
		 *            behavior.
		 */
		@SuppressWarnings({ "PMD.ConfusingTernary" })
		protected PortalSiteNavUserPreferences(final Preferences preferences) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("new PortalSiteNavUserPreferences(final Preferences "
						+ preferences + ")");
			}
			if (preferences != null) {
				final ResourceProperties resourceProperties = preferences
						.getProperties("sakai:portal:sitenav");
				int prefTabs = -1;
				try {
					prefTabs = (int) resourceProperties.getLongProperty("tabs");
				} catch (EntityPropertyNotDefinedException e) {
					// no property defined
					prefTabs = DEFAULT_TABS;
				} catch (EntityPropertyTypeException e) {
					// admin should investigate such a case
					LOG.error(e.getLocalizedMessage(), e);
					throw new IllegalStateException(e);
				}
				this.prefTabs = prefTabs;
			} else { // null Preferences
				this.prefTabs = DEFAULT_TABS;
				return;
			}
		}

		/**
		 * @return the prefTabs
		 */
		protected Integer getPrefTabs() {
			return prefTabs;
		}

	}
}
