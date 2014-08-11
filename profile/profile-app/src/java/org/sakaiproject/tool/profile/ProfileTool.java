/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/profile/tags/profile-2.9.1/profile-app/src/java/org/sakaiproject/tool/profile/ProfileTool.java $
 * $Id: ProfileTool.java 60850 2009-04-22 15:10:00Z aaronz@vt.edu $
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.app.profile.Profile;
import org.sakaiproject.api.app.profile.ProfileManager;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.util.FormattedText;
import org.sakaiproject.util.ResourceLoader;

//to allow user edits
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.user.api.UserEdit;
import org.sakaiproject.user.api.User;
import org.sakaiproject.entity.api.Entity;

/**
 * @author rshastri
 */
public class ProfileTool
{
	private static final Log LOG = LogFactory.getLog(ProfileTool.class);
	
	/** Resource bundle using current language locale */
    private static ResourceLoader rb = new ResourceLoader("org.sakaiproject.tool.profile.bundle.Messages");
    
    /** Configuration bundle using current language locale */
    private static ResourceLoader cb = new ResourceLoader("org.sakaiproject.tool.profile.config.Config");

	private static final String NONE = "none";

	private static final String UNIVERSITY_PHOTO = "universityId";

	private static final String PICTURE_URL = "pictureUrl";

	private static final String NO_PICTURE = "photoUnavialable";
	
	private static final String NO_UNIVERSITY_PHOTO_AVAILABLE = "officalPhotoUnavailable";
	
	private ProfileManager profileService;

	private Profile profile;

	private boolean loadingFirstTime = true;

	private String pictureIdPreference = NONE;

	private boolean displayPicture = false;

	private boolean displayNoProfileMsg = false;

	private boolean displayEvilTagMsg = false;

	private boolean displayEmptyFirstNameMsg = false;

	private boolean displayEmptyLastNameMsg = false;

	private boolean displayMalformedPictureUrlError = false;
	
	private boolean displayMalformedHomepageUrlError = false;
	
	private boolean displayInvalidEmailError = false;

	private String malformedUrlError = null;

	private String evilTagMsg = null;

	/**
	 * Process data for save action on edit page.
	 * 
	 * @return navigation outcome: return to main page or if no user is present throw permission exception
	 */
	public String processActionEditSave()
	{
		LOG.debug("processActionEditSave()");
		displayEvilTagMsg = false;
		displayEmptyFirstNameMsg = false;
		displayEmptyLastNameMsg = false;
		displayMalformedPictureUrlError = false;
		displayMalformedHomepageUrlError = false;
		displayInvalidEmailError = false;
		if ((profile != null) && (profile.getUserId() == null))
		{
			LOG.error("processActionEditSave :" + "No User Found");

			return "permissionException";
		}
		if (profile.getFirstName() == null || profile.getFirstName().trim().length() < 1)
		{
			displayEmptyFirstNameMsg = true;
			return "edit";
		}
		if (profile.getLastName() == null || profile.getLastName().trim().length() < 1)
		{
			displayEmptyLastNameMsg = true;
			return "edit";
		}
		if (profile.getEmail() == null ||  !isValidEmail(profile.getEmail())) {
			displayInvalidEmailError = true;
			return "edit";
			
		}
		if (profile.getOtherInformation() != null)
		{
			StringBuilder alertMsg = new StringBuilder();
			String errorMsg = null;
			try
			{
				errorMsg = FormattedText.processFormattedText(profile.getOtherInformation(), alertMsg);
				if (alertMsg.length() > 0)
				{
					evilTagMsg = alertMsg.toString();
					displayEvilTagMsg = true;
					return "edit";
				}
			}
			catch (Exception e)
			{
				LOG.error(" " + errorMsg, e);
			}
		}

		if ((getPictureIdPreference() != null) && getPictureIdPreference().equals(UNIVERSITY_PHOTO))
		{
			profile.setInstitutionalPictureIdPreferred(new Boolean(true));
			profile.setPictureUrl(null);
			displayPicture = true;
			this.pictureIdPreference = UNIVERSITY_PHOTO;
		}
		else if ((getPictureIdPreference() != null) && (getPictureIdPreference().equals(PICTURE_URL)))
		{
			profile.setInstitutionalPictureIdPreferred(new Boolean(false));
			displayPicture = true;
			this.pictureIdPreference = PICTURE_URL;
			if (profile.getPictureUrl() != null && profile.getPictureUrl().trim().length() > 0)
			{
				try
				{
					String pictureUrl = validateURL(profile.getPictureUrl());
					profile.setPictureUrl(pictureUrl);
				}
				catch (MalformedURLException e)
				{
					this.displayMalformedPictureUrlError = true;
					this.malformedUrlError = rb.getString("validurl") + " \"" + profile.getPictureUrl() + "\" " + rb.getString("invalid");
					return "edit";
				}
			}
		}
		else
		{
			// returns null or none
			profile.setInstitutionalPictureIdPreferred(new Boolean(false));
			profile.setPictureUrl(null);
			displayPicture = false;
			this.pictureIdPreference = NONE;
		}

		// Catch a bad url passed in homepage.
		if(profile.getHomepage() != null && profile.getHomepage().trim().length()>0)
		{
			try
			{
				String homepageUrl = validateURL(profile.getHomepage().trim());
				profile.setHomepage(homepageUrl);
			}
			catch (MalformedURLException e)
			{
				this.displayMalformedHomepageUrlError = true;
				this.malformedUrlError = rb.getString("validurl") + " \"" + profile.getHomepage() + "\" " + rb.getString("invalid");
				return "edit";
			}
		}
		
		try
		{
			profileService.save(profile);

		}

		catch (Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
		return "main";
	}

	/**
	 * Setup before navigating to edit page
	 * 
	 * @return navigation outcome: return to edit page or if no user is present throw permission exception
	 */
	public String processActionEdit()
	{
		LOG.debug("processActionEdit()");
		try
		{
			if ((profile != null) && (profile.getUserId() == null))
			{
				LOG.error("processActionEdit : " + "No User Found");
				return "PermissionException";
			}
			setPictureIdPreference(profile);
			return "edit";
		}
		catch (Exception e)
		{
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * @return
	 */
	public String processCancel()
	{
		LOG.debug("processCancel()");
		profile = profileService.getProfile();
		return "main";
	}

	/**
	 * Setup to fetch a profile
	 * 
	 * @return Profile for user logged in or empty profile
	 */
	public Profile getProfile()
	{
		LOG.debug("getProfile()");
		if (loadingFirstTime)
		{
			profile = profileService.getProfile();
			setPictureIdPreference(profile);
			loadingFirstTime = false;
		}
		else
		{
			if (profile == null)
			{
				displayNoProfileMsg = true;
			}
			else
			{
				if ((profile.getFirstName() == null) || (profile.getLastName() == null))
				{
					displayNoProfileMsg = true;
				}
				else
				{
					if (profile.getFirstName().equalsIgnoreCase("") || profile.getLastName().equalsIgnoreCase(""))
						displayNoProfileMsg = true;
					else
						displayNoProfileMsg = false;
				}
			}
		}
		return profile;
	}

	/**
	 * Getter for ProfileManager service
	 * 
	 * @return instance of ProfileManager
	 */
	public ProfileManager getProfileService()
	{
		LOG.debug("getProfileService()");
		return profileService;
	}

	/**
	 * Setter for ProfileManager service
	 * 
	 * @param profileService
	 */
	public void setProfileService(ProfileManager profileService)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("setProfileService(ProfileManager" + profileService + ")()");
		}
		this.profileService = profileService;

	}

	
	/**
	 * @return
	 */
	public boolean isDisplayNoProfileMsg()
	{
		LOG.debug("isDisplayNoProfileMsg()");
		return displayNoProfileMsg;
	}

	/**
	 * Getter for property if the tool bean is loaded for first time
	 * 
	 * @return boolean value
	 */
	public boolean isLoadingFirstTime()
	{
		LOG.debug("isLoadingFirstTime()");
		return loadingFirstTime;
	}

	/**
	 * Returns display picture preference
	 * 
	 * @return String
	 */
	public String getPictureIdPreference()
	{
		LOG.debug("getPictureIdPreference()");
		return pictureIdPreference;
	}

	/**
	 * Set display picture preference
	 * 
	 * @param pictureIDPreference
	 */
	public void setPictureIdPreference(String pictureIdPreference)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("setPictureIDPreference(String" + pictureIdPreference + ")");
		}
		this.pictureIdPreference = pictureIdPreference;
	}

	public boolean isShowTool()
	{
		LOG.debug("isShowTool()");
		return profileService.isShowTool();
	}

   public boolean isShowSearch()
   {
      LOG.debug("isShowSearch()");
      return profileService.isShowSearch();
   }
   
	/**
	 * @return
	 */
	public String getTitle()
	{
		LOG.debug("getTitle()");
		return SiteService.findTool(ToolManager.getCurrentPlacement().getId()).getTitle();
	}

	/**
	 * @return
	 */
	public String getEvilTagMsg()
	{
		LOG.debug("getEvilTagMsg()");
		return evilTagMsg;
	}

	/**
	 * @return
	 */
	public boolean isDisplayEvilTagMsg()
	{
		LOG.debug("isDisplayEvilTagMsg()");
		return displayEvilTagMsg;
	}
	
	public boolean isDisplayInvalidEmailMsg() {
		return displayInvalidEmailError;
	}

	/**
	 * @return
	 */
	public boolean isDisplayEmptyFirstNameMsg()
	{
		LOG.debug("isDisplayEmptyFirstNameMsg()");
		return displayEmptyFirstNameMsg;
	}

	/**
	 * @return
	 */
	public boolean isDisplayEmptyLastNameMsg()
	{
		LOG.debug("isDisplayEmptyLastNameMsg()");
		return displayEmptyLastNameMsg;
	}

	/**
	 * @return
	 */
	public boolean isDisplayNoPicture()
	{
		LOG.debug("isDisplayPicture()");
		return profileService.isDisplayNoPhoto(profile);
	}

	/**
	 * @param profile
	 */
	public void setProfile(Profile profile)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("setProfile(Profile" + profile + ")");
		}
		this.profile = profile;
	}

	/**
	 * @return
	 */
	public boolean isDisplayPictureURL()
	{
		LOG.debug("isDisplayPictureURL()");
		return profileService.isDisplayPictureURL(profile);
	}

	/**
	 * @return
	 */
	public boolean isDisplayUniversityPhoto()
	{
		LOG.debug("isDisplayUniversityPhoto()");
		return profileService.isDisplayUniversityPhoto(profile);
	}

	/**
	 * @return
	 */
	public boolean isDisplayUniversityPhotoUnavailable()
	{
		LOG.debug("isDisplayUniversityPhotoUnavailable()");
		return profileService.isDisplayUniversityPhotoUnavailable(profile);
	}

	/**
	 * @param profile
	 */
	private void setPictureIdPreference(Profile profile)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("setPictureIdPreference(Profile" + profile + ")");
		}
		if (profile.isInstitutionalPictureIdPreferred() != null
				&& profile.isInstitutionalPictureIdPreferred().booleanValue() == true)
		{
			this.pictureIdPreference = UNIVERSITY_PHOTO;
			this.displayPicture = true;
		}
		else if (profile.getPictureUrl() != null && profile.getPictureUrl().length() > 0)
		{
			this.pictureIdPreference = PICTURE_URL;
			this.displayPicture = true;
		}
		else
		{
			this.pictureIdPreference = NONE;
			this.displayPicture = false;
		}

	}

	/**
	 * @return
	 */
	public boolean isDisplayMalformedPictureUrlError()
	{
		LOG.debug("isDisplayMalformedPictureUrlError()");
		return displayMalformedPictureUrlError;
	}
	
	/**
	 * @return
	 */
	public boolean isDisplayMalformedHomepageUrlError()
	{
		LOG.debug("isDisplayMalformedHomepageUrlError()");
		return displayMalformedHomepageUrlError;
	}

	/**
	 * @return
	 */
	public String getMalformedUrlError()
	{
		LOG.debug("getMalformedUrlError()");
		return malformedUrlError;
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 */
	private String validateURL(String url) throws MalformedURLException
	{
		if (url == null || url.equals (""))
		{
			// ignore the empty url field
		}
		else if (url.indexOf ("://") == -1)
		{
			// if it's missing the transport, add http://
			url = "http://" + url;
		}

		if(url != null && !url.equals(""))
		{
			// valid protocol?
			try
			{
				// test to see if the input validates as a URL.
				// Checks string for format only.
				URL u = new URL(url);
			}
			catch (MalformedURLException e1)
			{
				try
				{
					Pattern pattern = Pattern.compile("\\s*([a-zA-Z0-9]+)://([^\\n]+)");
					Matcher matcher = pattern.matcher(url);
					if(matcher.matches())
					{
						// if URL has "unknown" protocol, check remaider with
						// "http" protocol and accept input it that validates.
						URL test = new URL("http://" + matcher.group(2));
					}
					else
					{
						throw e1;
					}
				}
				catch (MalformedURLException e2)
				{
					throw e1;
				}
			}
		}
		return url;
	}
	
	/**
	 * Is this a valid email the service will recognize
	 * @param email
	 * @return
	 */
	private boolean isValidEmail(String email) {
		
		// TODO: Use a generic Sakai utility class (when a suitable one exists)
		
		if (email == null || email.equals(""))
			return false;
		
		email = email.trim();
		//must contain @
		if (email.indexOf("@") == -1)
			return false;
		
		//an email can't contain spaces
		if (email.indexOf(" ") > 0)
			return false;
		
		//"^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*$" 
		if (email.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*$")) 
			return true;
	
		return false;
	}
	/**
	 * Returns String for image. Uses the config bundle
	 * to return paths to not available images.  
	 */
	public String getImageUrlToDisplay() {
		String imageUrl = "";
		
		if (isDisplayUniversityPhoto()) {
			imageUrl = "ProfileImageServlet.prf?photo=" + profile.getUserId();
		}
		else if (isDisplayPictureURL()) {
			imageUrl = profile.getPictureUrl();
		}
		else if (isDisplayNoPicture()) {
			imageUrl = cb.getString(NO_PICTURE);
		}
		else { //if (isDisplayUniversityPhotoUnavailable()){
			imageUrl = cb.getString(NO_UNIVERSITY_PHOTO_AVAILABLE);			
		}
		
		return imageUrl; 
	}
}
