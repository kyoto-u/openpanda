package courselink.kyoto_u.ac.jp.dao;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.sakaiproject.genericdao.hibernate.HibernateGeneralGenericDao;
import org.springframework.orm.hibernate4.HibernateCallback;

import courselink.kyoto_u.ac.jp.model.CourselinkRequest;
import courselink.kyoto_u.ac.jp.model.CourselinkSite;

public class CourselinkDaoImpl    extends HibernateGeneralGenericDao
implements CourselinkDao {

	private static final Logger log = Logger.getLogger(CourselinkDaoImpl.class);

	   public void init() {
	      log.debug("init");
	   }

	   public List<CourselinkSite> findSitesByRequestOwnerId(final String userId){
		   if (userId == null){
			   return findAll(CourselinkSite.class);
		   }
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException {
				   Query query = session.getNamedQuery("findSitesByRequestOwnerId");
				   query.setParameter("requestOwnerId", userId);

				   List<CourselinkSite> courselinkSiteList = query.list();
				   // we need to remove duplicates but retain order, so put
				   // in a LinkedHashset and then back into a list
				   if (courselinkSiteList != null){
					   Set<CourselinkSite> courselinkSiteSet = new LinkedHashSet<CourselinkSite>(courselinkSiteList);
					   courselinkSiteList.clear();
					   courselinkSiteList.addAll(courselinkSiteSet);
				   }
				   return courselinkSiteList;
			   }
		   };
		   return (List<CourselinkSite>)getHibernateTemplate().execute(hc);
	   }

	   public List<CourselinkRequest> findRequestsByOwnerId(final String userId, final int status){
		   if (userId == null){
			   return findRequests(status);
		   }
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException {
				   Query query = session.getNamedQuery("findRequestsByOwnerId");
				   query.setParameter("requestOwnerId", userId);
				   query.setParameter("requestStatus", status);

				   List<CourselinkRequest> courselinkRequestList = query.list();
				   // we need to remove duplicates but retain order, so put
				   // in a LinkedHashset and then back into a list
				   if (courselinkRequestList != null){
					   Set<CourselinkRequest> courselinkRequestSet = new LinkedHashSet<CourselinkRequest>(courselinkRequestList);
					   courselinkRequestList.clear();
					   courselinkRequestList.addAll(courselinkRequestSet);
				   }
				   return courselinkRequestList;
			   }
		   };
		   return (List<CourselinkRequest>)getHibernateTemplate().execute(hc);
	   }

	   public List<CourselinkRequest> findRequests(final int status){
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException {
				   Query query = session.getNamedQuery("findRequests");
				   query.setParameter("requestStatus", status);

				   List<CourselinkRequest> courselinkRequestList = query.list();
				   // we need to remove duplicates but retain order, so put
				   // in a LinkedHashset and then back into a list
				   if (courselinkRequestList != null){
					   Set<CourselinkRequest> courselinkRequestSet = new LinkedHashSet<CourselinkRequest>(courselinkRequestList);
					   courselinkRequestList.clear();
					   courselinkRequestList.addAll(courselinkRequestSet);
				   }
				   return courselinkRequestList;
			   }
		   };
		   return (List<CourselinkRequest>)getHibernateTemplate().execute(hc);
	   }

	   public List<CourselinkRequest> findRequestsBySiteId(final String siteId, final int status){
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException {
				   Query query = session.getNamedQuery("findRequestsBySiteId");
				   query.setParameter("requestStatus", status);
				   query.setParameter("siteId", siteId);

				   List<CourselinkRequest> courselinkRequestList = query.list();
				   // we need to remove duplicates but retain order, so put
				   // in a LinkedHashset and then back into a list
				   if (courselinkRequestList != null){
					   Set<CourselinkRequest> courselinkRequestSet = new LinkedHashSet<CourselinkRequest>(courselinkRequestList);
					   courselinkRequestList.clear();
					   courselinkRequestList.addAll(courselinkRequestSet);
				   }
				   return courselinkRequestList;
			   }
		   };
		   return (List<CourselinkRequest>)getHibernateTemplate().execute(hc);
	   }

	   public List<CourselinkRequest> findRequestsByDate(final Date insertDate){
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException {
				   Query query = session.getNamedQuery("findRequestsByDate");
				   query.setDate("insertDate", insertDate);
				   List<CourselinkRequest> courselinkRequestList = query.list();
				   return courselinkRequestList;
			   }
		   };
		   return (List<CourselinkRequest>)getHibernateTemplate().execute(hc);

	   }

	   public Integer removeRequestsByDate(final Date insertDate){
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException {
				   Query query = session.getNamedQuery("removeRequestsByDate");
				   query.setDate("insertDate", insertDate);
				   int n = query.executeUpdate();
				   return n;
			   }
		   };
		   return (Integer)getHibernateTemplate().execute(hc);
	   }

	   public Integer removeSitesNoRequest(){
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException {
				   Query query = session.getNamedQuery("findSitesNoRequests");
				   List<CourselinkSite> courselinkSiteList = query.list();
				   int num = 0;
				   if (courselinkSiteList != null){
					   Set<CourselinkSite> courselinkSiteSet = new LinkedHashSet<CourselinkSite>(courselinkSiteList);
					   num = courselinkSiteSet.size();
					   deleteSet(courselinkSiteSet);
				   }
				   return num;
			   }
		   };
		   return (Integer)getHibernateTemplate().execute(hc);

	   }

	   public Long getMaxStatus(final long courselinkSiteId){
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException {
				   String queryString = "select max(status) from courselink_request where courselink_site_id = ?";
				   Query query = session.createSQLQuery(queryString);
				   query.setLong(0, courselinkSiteId);
				   long n = ((Number)query.uniqueResult()).longValue();
				   return n;
			   }
		   };
		   return (Long)getHibernateTemplate().execute(hc);
	   }

	   public List<CourselinkSite> getAllCourselinkSite(){
		   HibernateCallback hc = new HibernateCallback(){
			   public Object doInHibernate(Session session) throws HibernateException {
				   Query query = session.getNamedQuery("findAll");
				   List<CourselinkSite> courselinkSiteList = query.list();
				   return courselinkSiteList;
			   }
		   };
		   return (List<CourselinkSite>)getHibernateTemplate().execute(hc);
		}

}
