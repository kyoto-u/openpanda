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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.site.api.Site;

/**
 * Helper class to sort sites based on terms and site types. Closely derived
 * from logic contained in
 * {@link #org.sakaiproject.portal.charon.site.MoreSiteViewImpl()} and related
 * classes.<br>
 * <a href=
 * "https://source.sakaiproject.org/svn/portal/tags/sakai-2.7.1/portal-impl/impl/src/java/org/sakaiproject/portal/charon/site/MoreSiteViewImpl.java"
 * >MoreSiteViewImpl.java</a>
 * <p>
 * Object is thread safe.
 */
@SuppressWarnings({ "PMD.LongVariable", "PMD.CyclomaticComplexity" })
class MoreSiteViewImpl {
	private static final Log LOG = LogFactory.getLog(MoreSiteViewImpl.class);

	/**
	 * How much additional capacity will the Map&lt String, List&lt Site&gt&gt
	 * terms require? For example: All sites, Portfolio, Project, Other.
	 */
	protected static final int ADDITIONAL_TERMS_CAPACITY = 6;

	/**
	 * i18n key for "All sites"
	 */
	protected static final String I18N_ALL_SITES = "moresite_all_sites";
	/**
	 * i18n key for "(unknown academic term)"
	 */
	protected static final String I18N_UNKNOWN_COURSE_TERM_SITES = "moresite_unknown_term";
	/**
	 * i18n key for "Portfolios"
	 */
	protected static final String I18N_PORTFOLIO_SITES = "moresite_portfolios";
	/**
	 * i18n key for "Other"
	 */
	protected static final String I18N_OTHER_SITES = "moresite_other";
	/**
	 * i18n key for "Projects"
	 */
	protected static final String I18N_PROJECT_SITES = "moresite_projects";
	/**
	 * i18n key for "Administration"
	 */
	protected static final String I18N_ADMIN_SITES = "moresite_administration";

	/**
	 * Default sort order if no sakai.properties->portal.term.order is supplied.
	 * All Sites is assumed as it always goes first in the list.
	 */
	public static final List<String> DEFAULT_SORT_ORDER;
	static {
		final List<String> defaultSortOrder = new ArrayList<String>(5);
		defaultSortOrder.add(I18N_ALL_SITES);
		defaultSortOrder.add(I18N_UNKNOWN_COURSE_TERM_SITES);
		defaultSortOrder.add(I18N_PORTFOLIO_SITES);
		defaultSortOrder.add(I18N_PROJECT_SITES);
		defaultSortOrder.add(I18N_OTHER_SITES);
		defaultSortOrder.add(I18N_ADMIN_SITES);
		DEFAULT_SORT_ORDER = Collections.unmodifiableList(defaultSortOrder);
	}

	/**
	 * Injected via constructor
	 */
	protected transient ServerConfigurationService serverConfigurationService;

	/**
	 * Dependencies get injected at creation of object to help with unit
	 * testing.
	 * 
	 * @param serverConfigurationService
	 */
	public MoreSiteViewImpl(
			final ServerConfigurationService serverConfigurationService) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("new MoreSiteViewImpl(final ServerConfigurationService "
					+ serverConfigurationService + ")");
		}
		if (serverConfigurationService == null) {
			throw new IllegalArgumentException(
					"serverConfigurationService == null");
		}
		this.serverConfigurationService = serverConfigurationService;
	}

	/**
	 * Process a site list into categories based on term and sorted by title.
	 * 
	 * @param siteList
	 *            Note: it is assumed that the passed siteList will already be
	 *            sorted by title ascending.
	 * @return
	 */
	@SuppressWarnings({ "PMD.CyclomaticComplexity",
			"PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public List<Map<String, List<Site>>> categorizeSites(
			final List<Site> siteList) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("categorizeSites(final List<Site> siteList)");
		}
		if (siteList == null) {
			throw new IllegalArgumentException("Illegal siteList parameter!");
		}
		// first sort by site titles (should only have to sort once)
		// FYI sites ARE being sorted by title ascending in the SQL query.
		// Collections.sort(siteList, new TitleSorter()); // not needed

		// Order term column headers according to order specified in
		// portal.term.order
		// Filter out terms for which user is not a member of any sites
		if (serverConfigurationService == null) {
			throw new IllegalStateException(
					"serverConfigurationService == null");
		}
		final String[] termOrder = serverConfigurationService
				.getStrings("portal.term.order");

		// try to determine a good initial capacity for Map terms
		@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
		int size = -1;
		if (termOrder != null) {
			size = termOrder.length + ADDITIONAL_TERMS_CAPACITY;
		} else {
			// Constructs an empty HashMap with the default initial capacity
			// (16) and the default load factor (0.75).
			size = 16;
		}

		final Map<String, List<Site>> terms = new HashMap<String, List<Site>>(
				size);
		// iterate through sites
		for (Site site : siteList) {
			final String type = site.getType();
			// determine term
			@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
			String term = null;
			if ("course".equals(type)) {
				term = site.getProperties().getProperty("term");
				if (term == null) {
					term = I18N_UNKNOWN_COURSE_TERM_SITES;
				}
			} else if ("project".equals(type)) {
				term = I18N_PROJECT_SITES;
			} else if ("portfolio".equals(type)) {
				term = I18N_PORTFOLIO_SITES;
			} else if ("admin".equals(type)) {
				term = I18N_ADMIN_SITES;
			} else {
				term = I18N_OTHER_SITES;
			}
			// get list of sites for term
			List<Site> termSites = terms.get(term);
			if (termSites == null) {
				termSites = new ArrayList<Site>();
			}
			termSites.add(site);
			terms.put(term, termSites);
		}

		// object to contain final rendering of data
		final List<Map<String, List<Site>>> sortedTerms = new ArrayList<Map<String, List<Site>>>(
				terms.size() + 1); // best guess at initial capacity
		// first add all available sites to sortedTerms
		final Map<String, List<Site>> allSites = new HashMap<String, List<Site>>(
				1);
		allSites.put(I18N_ALL_SITES, siteList);
		sortedTerms.add(allSites);
		if (termOrder != null) {
			// admin specified policy applies.
			// only add specified terms and in specified order
			/*
			 * Note: if the system administrator defines an empty
			 * sakai.properties->portal.term.order, then all course sites will
			 * be effectively hidden from the caller (with the exception of
			 * unknown academic terms).
			 */
			for (final String term : termOrder) {
				addSitesToTerm(term, terms, sortedTerms);
			}
		} else { // default sort ordering
			final Set<String> academicTerms = new HashSet<String>(
					terms.keySet());
			/*
			 * Remove the well known categories so that we are just left with
			 * course sites that have a term.
			 */
			academicTerms.removeAll(DEFAULT_SORT_ORDER);
			final SortedSet<String> sortedAcademicTerms = new TreeSet<String>(
					academicTerms);
			for (final String term : sortedAcademicTerms) {
				addSitesToTerm(term, terms, sortedTerms);
			}
		}
		// now add all remaining categories
		for (final String category : DEFAULT_SORT_ORDER) {
			addSitesToTerm(category, terms, sortedTerms);
		}
		return sortedTerms;
	}

	/**
	 * Silly little helper to avoid code duplication.
	 * 
	 * @param term
	 * @param terms
	 *            Source
	 * @param sortedTerms
	 *            Will be mutated by method.
	 */
	protected void addSitesToTerm(final String term,
			final Map<String, List<Site>> terms,
			final List<Map<String, List<Site>>> sortedTerms) {
		final List<Site> sites = terms.get(term);
		if (sites != null) {
			final Map<String, List<Site>> matches = new HashMap<String, List<Site>>(
					1);
			matches.put(term, sites);
			sortedTerms.add(matches);
		}
	}

	// /**
	// * Simple comparator for sorting sites based on site title.
	// *
	// * @see Comparator
	// */
	// static class TitleSorter implements Comparator<Site>, Serializable {
	// private static final long serialVersionUID = -3933901080921177017L;
	//
	// /**
	// * @see Comparator#compare(Object, Object)
	// */
	// public int compare(Site first, Site second) {
	// if (first == null || second == null) {
	// return 0;
	// }
	// final String firstTitle = first.getTitle();
	// final String secondTitle = second.getTitle();
	// if (firstTitle != null) {
	// return firstTitle.compareToIgnoreCase(secondTitle);
	// }
	// return 0;
	// }
	// }
}
