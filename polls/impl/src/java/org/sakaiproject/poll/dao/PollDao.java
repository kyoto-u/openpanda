/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/polls/tags/polls-1.5.1/impl/src/java/org/sakaiproject/poll/dao/PollDao.java $
 * $Id: PollDao.java 60214 2009-04-17 13:50:58Z arwhyte@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation
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

package org.sakaiproject.poll.dao;

import org.sakaiproject.genericdao.api.GeneralGenericDao;
import org.sakaiproject.poll.model.Poll;

public interface PollDao extends GeneralGenericDao {
	
	/**
	 * Get the number of distinct voters on a poll
	 * @param poll
	 * @return
	 */
	 public int getDisctinctVotersForPoll(Poll poll);

}
