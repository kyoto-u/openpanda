/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/profile/tags/profile-2.9.1/profile-app/src/java/org/sakaiproject/tool/profile/SearchTool.java $
 * $Id: SearchTool.java 59681 2009-04-03 23:29:54Z arwhyte@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
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

package org.sakaiproject.tool.profile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.app.profile.Profile;
import org.sakaiproject.api.app.profile.ProfileManager;
import org.sakaiproject.util.ResourceLoader;

/**
 * @author rshastri <a href="mailto:rshastri@iupui.edu ">Rashmi Shastri</a>
 */
public class SearchTool
{
	private ResourceLoader msgs = new ResourceLoader("org.sakaiproject.tool.profile.bundle.Messages");

	private static final Log LOG = LogFactory.getLog(SearchTool.class);

	private DecoratedProfile profile;

	private String searchKeyword;

	private List searchResults;

	private List currentSearchResults;

	private int noOfRecDisplayedFrom = 0;

	private int noOfRecDisplayedTo = 0;

	private int numberOfSearchedRecordsDisplayedPerPage = 10;

	private String displayNoOfRec = "10";

	private boolean showPrevious = false;

	private boolean showNext = false;

	private boolean showSearchResults = false;

	private boolean showNoMatchFound = false;

	private boolean redirectToSearchedProfile = false;

	protected ProfileManager profileService;

	public SearchTool()
	{
		this.reset(msgs.getString("java.search_keyword"));
	}

	public String getDisplayPage()
	{
		LOG.debug("getDisplayPage()");
		if (redirectToSearchedProfile)
		{
			return "displaySearchedProfile";
		}
		else
		{
			return "main";
		}
	}

	public void processValueChangeForDisplayNSearchResult(ValueChangeEvent vce)
	{
		if (LOG.isDebugEnabled()) LOG.debug("processValueChangeForDisplayNSearchResult(ValueChangeEvent " + vce + ")");
		setDisplayNoOfRec(((String) vce.getNewValue()));
		LOG.debug("Show these many rec :" + (String) vce.getNewValue());
		processActionDisplayFirst();
	}

	public String processActionDisplayFirst()
	{
		LOG.debug("processActionDisplayFirst()");
		try
		{
			if ((searchResults != null) && (searchResults.size() > 1)
					&& (searchResults.size() <= numberOfSearchedRecordsDisplayedPerPage))
			{
				showPrevious = false;
				showNext = false;
				showSearchResults = true;
				currentSearchResults = searchResults;
				noOfRecDisplayedFrom = 1;
				noOfRecDisplayedTo = searchResults.size();

				return getDisplayPage();
			}
			else if ((searchResults != null) && (searchResults.size() > numberOfSearchedRecordsDisplayedPerPage))
			{
				currentSearchResults = searchResults.subList(0, (numberOfSearchedRecordsDisplayedPerPage));
				noOfRecDisplayedFrom = 1;
				noOfRecDisplayedTo = numberOfSearchedRecordsDisplayedPerPage;
				showPrevious = false;
				showNext = true;
				showSearchResults = true;

				return getDisplayPage();
			}
			else
			{
				return profile.processActionDisplayProfile();
			}
		}
		catch (Exception e)
		{
			LOG.error(e.getMessage(), e);

			return null;
		}
	}

	public String processActionDisplayNext()
	{
		LOG.debug("processActionDisplayNext()");
		try
		{
			if ((searchResults != null) && (searchResults.size() > (noOfRecDisplayedTo)))
			{
				this.showNoMatchFound = false;
				this.showSearchResults = true;
				this.showPrevious = true;

				if (searchResults.size() > (noOfRecDisplayedTo + numberOfSearchedRecordsDisplayedPerPage))
				{
					currentSearchResults = searchResults.subList((noOfRecDisplayedTo),
							(noOfRecDisplayedTo + numberOfSearchedRecordsDisplayedPerPage));
					noOfRecDisplayedFrom = noOfRecDisplayedTo + 1;
					noOfRecDisplayedTo = noOfRecDisplayedTo + numberOfSearchedRecordsDisplayedPerPage;
					showNext = true;

					return getDisplayPage();
				}

				if (searchResults.size() == (noOfRecDisplayedTo + numberOfSearchedRecordsDisplayedPerPage))
				{
					currentSearchResults = searchResults.subList((noOfRecDisplayedTo),
							(noOfRecDisplayedTo + numberOfSearchedRecordsDisplayedPerPage));
					noOfRecDisplayedFrom = noOfRecDisplayedTo + 1;
					noOfRecDisplayedTo = noOfRecDisplayedTo + numberOfSearchedRecordsDisplayedPerPage;
					showNext = false;

					return getDisplayPage();
				}

				if (searchResults.size() < (noOfRecDisplayedTo + numberOfSearchedRecordsDisplayedPerPage))
				{
					currentSearchResults = searchResults.subList((noOfRecDisplayedTo), (searchResults.size()));
					noOfRecDisplayedFrom = noOfRecDisplayedTo + 1;
					noOfRecDisplayedTo = searchResults.size();
					showNext = false;

					return getDisplayPage();
				}
			}

			return getDisplayPage();
		}
		catch (Exception e)
		{
			LOG.error(e.getMessage(), e);

			return null;
		}
	}

	public String processActionDisplayPrevious()
	{
		LOG.debug("processActionDisplayPrevious()");
		try
		{
			if ((searchResults != null) && ((noOfRecDisplayedFrom) > 1)
					&& (noOfRecDisplayedFrom >= numberOfSearchedRecordsDisplayedPerPage))
			{
				this.showNext = true;
				this.showNoMatchFound = false;
				this.showSearchResults = true;

				if ((noOfRecDisplayedFrom - numberOfSearchedRecordsDisplayedPerPage) == 1)
				{
					showPrevious = false;
					currentSearchResults = searchResults.subList(0, (numberOfSearchedRecordsDisplayedPerPage));
					noOfRecDisplayedFrom = 1;
					noOfRecDisplayedTo = numberOfSearchedRecordsDisplayedPerPage;
				}
				else
				{
					showPrevious = true;
					currentSearchResults = searchResults.subList(
							(noOfRecDisplayedFrom - numberOfSearchedRecordsDisplayedPerPage - 1), (noOfRecDisplayedFrom - 1));
					noOfRecDisplayedTo = noOfRecDisplayedFrom - 1;
					noOfRecDisplayedFrom = noOfRecDisplayedFrom - numberOfSearchedRecordsDisplayedPerPage;
				}
			}
			else
			{
				showPrevious = false;
			}

			return getDisplayPage();
		}
		catch (Exception e)
		{
			LOG.error(e.getMessage(), e);

			return null;
		}
	}

	public String processActionDisplayLast()
	{
		LOG.debug("processActionDisplayLast()");
		try
		{
			// Single page display
			if ((searchResults != null) && (searchResults.size() > 1)
					&& (searchResults.size() <= numberOfSearchedRecordsDisplayedPerPage))
			{
				showPrevious = false;
				showNext = false;
				currentSearchResults = searchResults;

				return this.getDisplayPage();
			}
			else if ((searchResults != null) && (searchResults.size() > numberOfSearchedRecordsDisplayedPerPage))
			{
				int displayRecForLastPage = searchResults.size() % numberOfSearchedRecordsDisplayedPerPage;

				if (displayRecForLastPage == 0)
				{
					currentSearchResults = searchResults.subList((searchResults.size() - numberOfSearchedRecordsDisplayedPerPage),
							(searchResults.size()));
					noOfRecDisplayedFrom = searchResults.size() - numberOfSearchedRecordsDisplayedPerPage + 1;
				}
				else
				{
					// there is a remainder
					currentSearchResults = searchResults.subList((searchResults.size() - displayRecForLastPage), (searchResults
							.size()));
					noOfRecDisplayedFrom = searchResults.size() - displayRecForLastPage + 1;
				}

				noOfRecDisplayedTo = searchResults.size();
				showPrevious = true;
				showNext = false;

				return this.getDisplayPage();
			}
			else
			// search result is exactly 1
			{
				return profile.processActionDisplayProfile();
			}
		}
		catch (Exception e)
		{
			LOG.error(e.getMessage(), e);

			return null;
		}
	}

	public String processActionSearch()
	{
		LOG.debug("processActionSearch()");
		try
		{
			this.reset(searchKeyword);
			// Find User mutable profiles only
			if (searchKeyword == null || searchKeyword.trim().length() < 1)
			{
				this.showNoMatchFound = true;
				return "main";
			}
			List profiles = profileService.findProfiles(searchKeyword);
			searchResults = new ArrayList();

			if ((profiles != null) && (profiles.size() > 0))
			{
				Iterator profileIterator = profiles.iterator();

				while (profileIterator.hasNext())
				{
					profile = new DecoratedProfile((Profile) profileIterator.next());
					searchResults.add(profile);
				}

				return processActionDisplayFirst();
			}
			else
			{
				this.showNoMatchFound = true;

				return "main";
			}
		}
		catch (Exception e)
		{
			LOG.error(e.getMessage(), e);

			return null;
		}
	}

	public boolean isShowNext()
	{
		LOG.debug("isShowNext()");
		return showNext;
	}

	public boolean isShowPrevious()
	{
		LOG.debug("isShowPrevious()");
		return showPrevious;
	}

	public List getCurrentSearchResults()
	{
		LOG.debug("getCurrentSearchResults()");
		return currentSearchResults;
	}

	public void setCurrentSearchResults(List currentSearchResults)
	{
		if (LOG.isDebugEnabled()) LOG.debug("setCurrentSearchResults(List" + currentSearchResults + ")");
		this.currentSearchResults = currentSearchResults;
	}

	public boolean isShowNoMatchFound()
	{
		LOG.debug("isShowNoMatchFound()");
		return showNoMatchFound;
	}

	public boolean isShowSearchResults()
	{
		LOG.debug("isShowSearchResults()");
		return (showSearchResults);
	}

	public void reset(String searchKeyword)
	{
		if (LOG.isDebugEnabled()) LOG.debug("reset(String" + searchKeyword + ")");
		this.searchKeyword = searchKeyword;
		this.profile = null;
		this.searchResults = null;
		this.currentSearchResults = null;
		this.noOfRecDisplayedFrom = 0;
		this.noOfRecDisplayedTo = 0;
		this.showPrevious = false;
		this.showNext = false;
		this.showSearchResults = false;
		this.showNoMatchFound = false;
		this.redirectToSearchedProfile = false;
	}

	public String processCancel()
	{
		LOG.debug("processCancel()");
		this.reset(msgs.getString("java.search_keyword"));
		return "main";
	}

	public String getDisplayNoOfRec()
	{
		LOG.debug("getDisplayNoOfRec()");
		try
		{
			if ((this.displayNoOfRec != null) && (Integer.parseInt(displayNoOfRec) != 0))
			{
				numberOfSearchedRecordsDisplayedPerPage = Integer.parseInt(displayNoOfRec);
			}
		}
		catch (NumberFormatException e)
		{
			LOG.error(e.getMessage(), e);
		}

		return displayNoOfRec;
	}

	public void setDisplayNoOfRec(String no_of_searched_rec_per_page)
	{
		if (LOG.isDebugEnabled()) LOG.debug("setDisplayNoOfRec(String " + no_of_searched_rec_per_page + ")");
		displayNoOfRec = no_of_searched_rec_per_page;

		try
		{
			if ((this.displayNoOfRec != null) && (Integer.parseInt(displayNoOfRec) != 0))
			{
				numberOfSearchedRecordsDisplayedPerPage = Integer.parseInt(displayNoOfRec);
			}
		}
		catch (NumberFormatException e)
		{
			LOG.error(e.getMessage(), e);
		}
	}

	public DecoratedProfile getProfile()
	{
		LOG.debug("getProfile()");
		return profile;
	}

	/**
	 * @return
	 */
	public ProfileManager getProfileService()
	{
		LOG.debug("getProfileService()");
		return profileService;
	}

	/**
	 * @return
	 */
	public String getSearchKeyword()
	{
		LOG.debug("getSearchKeyword()");
		return searchKeyword;
	}

	/**
	 * @return
	 */
	public List getSearchResults()
	{
		LOG.debug("getSearchResults()");
		return searchResults;
	}

	/**
	 * @param profileService
	 */
	public void setProfileService(ProfileManager profileService)
	{
		if (LOG.isDebugEnabled()) LOG.debug("setProfileService(ProfileManager " + profileService + ")");
		this.profileService = profileService;
	}

	/**
	 * @param profile
	 */
	public void setProfile(DecoratedProfile profile)
	{
		if (LOG.isDebugEnabled()) LOG.debug("setProfile(DecoratedProfile " + profile + ")");
		this.profile = profile;
	}

	/**
	 * @param searchKeyword
	 */
	public void setSearchKeyword(String searchKeyword)
	{
		if (LOG.isDebugEnabled()) LOG.debug("setSearchResults(String " + searchKeyword + ")");

		this.searchKeyword = searchKeyword;
	}

	/**
	 * @param searchResults
	 */
	public void setSearchResults(List searchResults)
	{
		if (LOG.isDebugEnabled()) LOG.debug("setSearchResults(List " + searchResults + ")");
		this.searchResults = searchResults;
	}

	public class DecoratedProfile
	{
		protected Profile inProfile;

		/**
		 * @param newProfile
		 */
		public DecoratedProfile(Profile newProfile)
		{
			if (LOG.isDebugEnabled()) LOG.debug("DecoratedProfile(Profile" + newProfile + ")");
			inProfile = newProfile;
		}

		/**
		 * @return
		 */
		public Profile getProfile()
		{
			LOG.debug("getProfile()");
			return inProfile;
		}

		/**
		 * @return
		 */
		public String processActionDisplayProfile()
		{
			LOG.debug("processActionDisplayProfile()");
			try
			{
				profile = this;
				redirectToSearchedProfile = true;
				return "displaySearchedProfile";
			}
			catch (Exception e)
			{
				LOG.error(e.getMessage(), e);
				return null;
			}
		}

		public boolean isDisplayCompleteProfile()
		{
			LOG.debug("isDisplayCompleteProfile()");
			return profileService.displayCompleteProfile(inProfile);
		}

		public boolean isDisplayPictureURL()
		{
			LOG.debug("isDisplayPictureURL()");
			return profileService.isDisplayPictureURL(inProfile);
		}

		public boolean isDisplayUniversityPhoto()
		{
			LOG.debug("isDisplayUniversityPhoto()");
			return profileService.isDisplayUniversityPhoto(inProfile);
		}

		public boolean isDisplayUniversityPhotoUnavailable()
		{
			LOG.debug("isDisplayUniversityPhotoUnavailable()");
			return profileService.isDisplayUniversityPhotoUnavailable(inProfile);
		}

		public boolean isDisplayNoPicture()
		{
			LOG.debug("isDisplayPhoto()");
			return profileService.isDisplayNoPhoto(inProfile);
		}

	}
}
