/**********************************************************************************
 * $URL$
 * $Id$
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

import org.sakaiproject.message.api.MessageHeader;

/**
 * <p>
 * AnnouncementMessageHeader is the Interface for a Sakai Announcement Message header.
 * </p>
 */
public interface AnnouncementMessageHeader extends MessageHeader
{
	/**
	 * Access the subject of the announcement.
	 * 
	 * @return The subject of the announcement.
	 */
	public String getSubject();
	
	/**
	 * Access the readCheck of the announcement.
	 * 
	 * @return The readCheck of the announcement.
	 */
	public Boolean getReadCheck();

} // AnnouncementMessageHeader

