package org.sakaiproject.alert.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.sakaiproject.genericdao.hibernate.HibernateGeneralGenericDao;
import org.sakaiproject.alert.dao.EmergencyInfoDao;
import org.sakaiproject.alert.model.EmergencyInfo;
import org.springframework.orm.hibernate3.HibernateCallback;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by kajita on 5/10/16.
 */
public class EmergencyDaoImpl extends HibernateGeneralGenericDao implements EmergencyInfoDao {

    private static Log log = LogFactory.getLog(EmergencyDaoImpl.class);

    public void init() {
        log.debug("init");
    }

    @SuppressWarnings("unchecked")
    public EmergencyInfo getEmergencyInfoByUserId(final String userId)  {

        HibernateCallback hc = new HibernateCallback() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery("getEmergencyInfoByUserId");
                query.setParameter("userId", userId);
                query.setMaxResults(1);
                return query.uniqueResult();
            }
        };

        return (EmergencyInfo) getHibernateTemplate().execute(hc);

    }

    @SuppressWarnings("unchecked")
    public List<EmergencyInfo> getAllEmergencyInfoByUserId(final String userId)  {

        HibernateCallback hc = new HibernateCallback() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery("getEmergencyInfoByUserId");
                query.setParameter("userId", userId);
                return query.list();
            }
        };

        return (List<EmergencyInfo>) getHibernateTemplate().execute(hc);

    }

    @SuppressWarnings("unchecked")
    public EmergencyInfo getEmergencyInfoByUserIdSince(final String userId, final Date sinceDate)  {

        HibernateCallback hc = new HibernateCallback() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery("getEmergencyInfoByUserIdSince");
                query.setParameter("userId", userId);
                query.setParameter("sinceDate", sinceDate);
                query.setMaxResults(1);
                return query.uniqueResult();
            }
        };

        return (EmergencyInfo) getHibernateTemplate().execute(hc);

    }

    @SuppressWarnings("unchecked")
    public List<EmergencyInfo> getAllEmergencyInfoByUserIdSince(final String userId, final Date sinceDate)  {

        HibernateCallback hc = new HibernateCallback() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery("getEmergencyInfoByUserIdSince");
                query.setParameter("userId", userId);
                query.setParameter("sinceDate", sinceDate);
                return query.list();
            }
        };

        return (List<EmergencyInfo>) getHibernateTemplate().execute(hc);

    }
}
