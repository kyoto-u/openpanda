/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/rwiki/tags/sakai-10.2/rwiki-api/api/src/java/uk/ac/cam/caret/sakai/rwiki/service/api/dao/RWikiObjectContentDao.java $
 * $Id: RWikiObjectContentDao.java 9108 2006-05-08 14:30:57Z ian@caret.cam.ac.uk $
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

package uk.ac.cam.caret.sakai.rwiki.service.api.dao;

import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiObject;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiObjectContent;

// FIXME: Service

public interface RWikiObjectContentDao
{
	/**
	 * get the content object for the RWikiObject
	 * 
	 * @param parent
	 *        the RWikiObject
	 * @return
	 */
	RWikiObjectContent getContentObject(RWikiObject parent);

	/**
	 * Create a new content object and associate it with the RWikiObject
	 * 
	 * @param parent
	 * @return
	 */
	RWikiObjectContent createContentObject(RWikiObject parent);

	/**
	 * Update the content object
	 * 
	 * @param content
	 */
	void update(RWikiObjectContent content);

}
