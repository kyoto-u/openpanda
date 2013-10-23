/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/profile/tags/profile-2.9.3/profile-component-shared/src/java/org/sakaiproject/component/app/profile/ProfileManagerImpl.java $
 * $Id: ProfileManagerImpl.java 69837 2009-12-14 22:58:05Z steve.swinsburg@gmail.com $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007, 2008, 2009 The Sakai Foundation
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

package org.sakaiproject.component.app.profile;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.api.app.profile.Profile;
import org.sakaiproject.api.app.profile.ProfileManager;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.cover.ServerConfigurationService;

/**
 * This is now a proxy bean, which delegates to whichever implementation is set as the value for "profile.manager.integration.bean" in sakai.properties.
 * It defaults to the LegacyProfileManager bean which is provided by the original Profile. Profile uses the LegacyProfileManager bean directly so will not be affected.
 * 
 * To enable other tools (eg Roster) to use Profile2's implementation, set:
 * profile.manager.integration.bean=org.sakaiproject.api.app.profile.Profile2ProfileManager
 *
 */
public class ProfileManagerImpl implements ProfileManager
{

	ProfileManager profileManagerImplementation;
	
	public void init() {
		String classBean = ServerConfigurationService.getString("profile.manager.integration.bean", "org.sakaiproject.api.app.profile.LegacyProfileManager");
		
		if (profileManagerImplementation == null) {
			profileManagerImplementation =  (ProfileManager) ComponentManager.get(classBean);
		}
		
	}
	
	public void destroy() {
		profileManagerImplementation = null;
	}

	public boolean displayCompleteProfile(Profile profile) {
		return profileManagerImplementation.displayCompleteProfile(profile);
	}

	public List findProfiles(String searchString) {
		return profileManagerImplementation.findProfiles(searchString);
	}

	public byte[] getInstitutionalPhotoByUserId(String uid) {
		return profileManagerImplementation.getInstitutionalPhotoByUserId(uid);
	}

	
	public byte[] getInstitutionalPhotoByUserId(String uid, boolean siteMaintainer) {
		return profileManagerImplementation.getInstitutionalPhotoByUserId(uid, siteMaintainer);
	}

	public Profile getProfile() {
		return profileManagerImplementation.getProfile();
	}

	public Map<String, Profile> getProfiles(Set<String> userIds) {
		return profileManagerImplementation.getProfiles(userIds);
	}

	public Profile getUserProfileById(String id) {
		return profileManagerImplementation.getUserProfileById(id);
	}

	

	public boolean isCurrentUserProfile(Profile profile) {
		return profileManagerImplementation.isCurrentUserProfile(profile);
	}

	public boolean isDisplayNoPhoto(Profile profile) {
		return profileManagerImplementation.isDisplayNoPhoto(profile);
	}

	public boolean isDisplayPictureURL(Profile profile) {
		return profileManagerImplementation.isDisplayPictureURL(profile);
	}

	public boolean isDisplayUniversityPhoto(Profile profile) {
		return profileManagerImplementation.isDisplayUniversityPhoto(profile);
	}

	public boolean isDisplayUniversityPhotoUnavailable(Profile profile) {
		return profileManagerImplementation.isDisplayUniversityPhotoUnavailable(profile);
	}

	public boolean isShowSearch() {
		return profileManagerImplementation.isShowSearch();
	}

	public boolean isShowTool() {
		return profileManagerImplementation.isShowTool();
	}

	public void save(Profile profile) {
		profileManagerImplementation.save(profile);
	}
	
	
	

}
