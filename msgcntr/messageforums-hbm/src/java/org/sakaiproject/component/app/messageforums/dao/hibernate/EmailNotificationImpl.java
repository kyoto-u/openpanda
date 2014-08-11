/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/msgcntr/tags/msgcntr-3.0.1/messageforums-hbm/src/java/org/sakaiproject/component/app/messageforums/dao/hibernate/EmailNotificationImpl.java $
 * $Id: EmailNotificationImpl.java 68560 2009-11-04 08:31:19Z david.horwitz@uct.ac.za $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.component.app.messageforums.dao.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.app.messageforums.EmailNotification;

public class EmailNotificationImpl implements EmailNotification {

	private static final Log LOG = LogFactory.getLog(EmailNotificationImpl.class);
	private Long id;

	private String notificationLevel;

	private String contextId;

	private String userId;

	private Integer version;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getNotificationLevel() {

		return notificationLevel;
	}

	public void setNotificationLevel(String notilevel) {
		LOG.debug("EmailNotifcationImpl.setnotificaitonlevel");
		this.notificationLevel = notilevel;

	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

}
