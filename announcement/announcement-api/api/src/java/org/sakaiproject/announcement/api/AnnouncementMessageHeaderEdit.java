/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/announcement/tags/sakai-10.2/announcement-api/api/src/java/org/sakaiproject/announcement/api/AnnouncementMessageHeaderEdit.java $
 * $Id: AnnouncementMessageHeaderEdit.java 105078 2012-02-24 23:00:38Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.announcement.api;

import org.sakaiproject.message.api.MessageHeaderEdit;

/**
 * <p>
 * AnnouncementMessageHeader is the Interface for a Sakai Announcement Message header.
 * </p>
 */
public interface AnnouncementMessageHeaderEdit extends AnnouncementMessageHeader, MessageHeaderEdit
{
	/**
	 * Set the subject of the announcement.
	 * 
	 * @param subject
	 *        The subject of the announcement.
	 */
	public void setSubject(String subject);
}
