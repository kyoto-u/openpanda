/**
 * $URL: https://source.sakaiproject.org/svn/sitestats/tags/sakai-10.1/sitestats-api/src/java/org/sakaiproject/sitestats/api/Stat.java $
 * $Id: Stat.java 105078 2012-02-24 23:00:38Z ottenhoff@longsight.com $
 *
 * Copyright (c) 2006-2009 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sakaiproject.sitestats.api;

import java.util.Date;

/**
 * Represents common fields of records from the SST_EVENTS and SST_RESOURCES tables.
 * @author Nuno Fernandes
 */
public interface Stat {
	/** Get the db record id. */ 
	public long getId();
	/** Set the db record id. */	
	public void setId(long id);
	
	/** Get the user Id this record refers to. */ 
	public String getUserId();
	/** Set the user Id this record refers to. */
	public void setUserId(String userId);
	
	/** Get the context (site Id) this record refers to. */
	public String getSiteId();
	/** Set the context (site Id) this record refers to. */
	public void setSiteId(String siteId);
	
	/** Get the total value. */
	public long getCount();
	/** Set the total value. */
	public void setCount(long count);
	
	/** Get the date this record refers to. Only year,month and day are important. */
	public Date getDate();
	/** Set the date this record refers to. Only year,month and day are important. */
	public void setDate(Date date);
}
