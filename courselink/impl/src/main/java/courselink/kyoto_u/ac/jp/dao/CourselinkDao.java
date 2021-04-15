package courselink.kyoto_u.ac.jp.dao;

import java.util.Date;
import java.util.List;

import org.sakaiproject.genericdao.api.GeneralGenericDao;

import courselink.kyoto_u.ac.jp.model.CourselinkRequest;
import courselink.kyoto_u.ac.jp.model.CourselinkSite;

public interface CourselinkDao  extends GeneralGenericDao {
	public List<CourselinkSite> findSitesByRequestOwnerId(final String userId);
	public List<CourselinkRequest> findRequestsByOwnerId(final String userId, final int status);
	public List<CourselinkRequest> findRequests(final int status);
	public List<CourselinkRequest> findRequestsBySiteId(final String siteId, final int status);
	public List<CourselinkRequest> findRequestsByDate(final Date insertDate);
	public Integer removeRequestsByDate(final Date insertDate);
	public Integer removeSitesNoRequest();
	public Long getMaxStatus(final long courselinkSiteId);
	public List<CourselinkSite> getAllCourselinkSite();
}
