/**
 * Copyright (c) 2003-2017 The Apereo Foundation
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
package org.sakaiproject.announcement.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.sakaiproject.announcement.api.AnnouncementBrowsingHis;
import org.sakaiproject.announcement.api.AnnouncementBrowsingHisDao;
import org.sakaiproject.announcement.dao.hbm.AnnouncementBrowsingHisHbm;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnnouncementBrowsingHisDaoHbm extends HibernateDaoSupport implements AnnouncementBrowsingHisDao {
	
	public boolean save(String channelId, String messageId, String userId) {
		try{
			AnnouncementBrowsingHisHbm data = getAnnouncementBrowsingHis(channelId, messageId, userId);		
			if (data != null) {
				data.setReadDate(new Date());
				getHibernateTemplate().saveOrUpdate(data);
			} else {
				data = new AnnouncementBrowsingHisHbm(channelId, messageId, userId, new Date());
				getHibernateTemplate().save(data);				
			}
			return true;
		} catch (DataAccessException e) {
			log.error("saveAnnouncementBrowsingHis aS '" + channelId + ", " + messageId + ", " + userId + "' failed.", e);
			return false;
		}
	}

	public List<AnnouncementBrowsingHis> getHistories(String channelId, String messageId) {
		AnnouncementBrowsingHisHbm example = new AnnouncementBrowsingHisHbm();
		example.setChannelId(channelId);
		example.setMessageId(messageId);
		return getHibernateTemplate().findByExample(example);
	}

	public void deleteAll(String channelId, String messageId) {
		List<AnnouncementBrowsingHis> histories = getHistories(channelId, messageId);
		if (CollectionUtils.isNotEmpty(histories)) {
			getHibernateTemplate().deleteAll(histories);
		} else {
			log.warn("Nothing to delete for channelId: " + channelId + ", messageId: " + messageId);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private AnnouncementBrowsingHisHbm getAnnouncementBrowsingHis(String channelId, String messageId, String userId) {
		final StringBuilder sb = new StringBuilder("from AnnouncementBrowsingHisHbm as a ");
        sb.append("where a.channelId=:channelId  ");
        sb.append("and a.messageId=:messageId ");
    	sb.append("and a.userId=:userId");
    	
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.createQuery(sb.toString());
				q.setParameter("channelId", channelId);
				q.setParameter("messageId", messageId);
				q.setParameter("userId", userId);
				return q.uniqueResult();
			}
		};
		return (AnnouncementBrowsingHisHbm)getHibernateTemplate().execute(hc);
	}
}
