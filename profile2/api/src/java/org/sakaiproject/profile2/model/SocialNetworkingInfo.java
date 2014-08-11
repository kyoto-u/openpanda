/**
 * Copyright (c) 2008-2010 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sakaiproject.profile2.model;

import java.io.Serializable;

/**
 * <code>SocialNetworkingInfo</code> is a model for storing a user's social
 * networking details.
 */
public class SocialNetworkingInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userUuid;
	private String facebookUrl;
	private String linkedinUrl;
	private String myspaceUrl;
	private String skypeUsername;
	private String twitterUrl;
	
	public SocialNetworkingInfo() {
		
	}

	public SocialNetworkingInfo(String userUuid) {

		this.userUuid = userUuid;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public String getFacebookUrl() {
		return facebookUrl;
	}

	public void setFacebookUrl(String facebookUrl) {
		this.facebookUrl = facebookUrl;
	}

	public String getLinkedinUrl() {
		return linkedinUrl;
	}

	public void setLinkedinUrl(String linkedinUrl) {
		this.linkedinUrl = linkedinUrl;
	}

	public String getMyspaceUrl() {
		return myspaceUrl;
	}

	public void setMyspaceUrl(String myspaceUrl) {
		this.myspaceUrl = myspaceUrl;
	}

	public String getSkypeUsername() {
		return skypeUsername;
	}

	public void setSkypeUsername(String skypeUsername) {
		this.skypeUsername = skypeUsername;
	}

	public String getTwitterUrl() {
		return twitterUrl;
	}

	public void setTwitterUrl(String twitterUrl) {
		this.twitterUrl = twitterUrl;
	}
	
	
}
