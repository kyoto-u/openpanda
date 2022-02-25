/**
 * Copyright (c) 2003-2014 The Apereo Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://opensource.org/licenses/ecl2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sakaiproject.announcement.api;

import java.util.Date;

/**
 * AnnouncementBrowsingHis stores the browsing history of announcements.
 */
public interface AnnouncementBrowsingHis {

	/**
	 * The ID for browsing history
	 * @return A ID.
	 */
	public long getId();

	/**
	 * The Channel Id for browsing history
	 * to view it.
	 * @return A Channel Id.
	 */
	public String getChannelId();

	/**
	 * The MessageId for browsing history
	 * @return A Message Id.
	 */
	public String getMessageId();

	/**
	 * The User Id for browsing history
	 * @return A User Id.
	 */
	public String getUserId();

	/**
	 * The User ID to be displayed in the browsing history lis
	 * @return A Channel Id.
	 */
	public String getDisplayUserId();

	/**
	 * The User ID to be displayed in the browsing history list
	 * @param diplayUserId 
	 * @return A Display User Id.
	 */
	public void setDisplayUserId(String diplayUserId);

	/**
	 * The User Name to be displayed in the browsing history list
	 * @param diplayUserName 
	 * @return A Channel Id.
	 */
	public void setDisplayUserName(String diplayUserName);

	/**
	 * The User Name to be displayed in the browsing history list
	 * @return A Display User Name.
	 */
	public String getDisplayUserName();

	/**
	 * The Read Date for browsing history
	 * @return A Read Date.
	 */
	public Date getReadDate();

}
