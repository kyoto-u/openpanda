/**
 * Copyright (c) 2003-2012 The Apereo Foundation
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
package org.sakaiproject.announcement.dao.hbm;

import java.util.Date;

import org.sakaiproject.announcement.api.AnnouncementBrowsingHis;

public class AnnouncementBrowsingHisHbm implements AnnouncementBrowsingHis {

	private long id;
	private String channelId;
	private String messageId;
	private String userId;
	private String displayUserId;
	private String displayUserName;
	private Date readDate;

	public AnnouncementBrowsingHisHbm() {
	}

	public AnnouncementBrowsingHisHbm(String channelId, String messageId, String userId, Date readDate) {
		this.channelId = channelId;
		this.messageId = messageId;
		this.userId = userId;
		this.readDate = readDate;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setDisplayUserId(String displayUserId) {
		this.displayUserId = displayUserId;
	}

	public String getDisplayUserId() {
		return displayUserId;
	}

	public String getDisplayUserName() {
		return displayUserName;
	}

	public void setDisplayUserName(String displayUserName) {
		this.displayUserName = displayUserName;
	}

	public Date getReadDate() {
		return readDate;
	}

	public void setReadDate(Date readDate) {
		this.readDate = readDate;
	}
}
