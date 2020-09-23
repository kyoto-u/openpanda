package courselink.kyoto_u.ac.jp.logic;

import java.util.Date;
import java.util.List;

import org.sakaiproject.site.api.Site;
import org.sakaiproject.user.api.User;

import courselink.kyoto_u.ac.jp.bean.CourselinkRequestBean;
import courselink.kyoto_u.ac.jp.bean.CourselinkSiteBean;
import courselink.kyoto_u.ac.jp.model.CourselinkRequest;
import courselink.kyoto_u.ac.jp.model.CourselinkSite;

/**
 * An example logic interface
 *
 * @author Mike Jennings (mike_jennings@unc.edu)
 *
 */
public interface ProjectLogic {

	   /**
	    * This returns a List of sites that are
	    * visible to the current user
	    * @return a List of CourselinkSiteBean objects
	    */
	   public List<CourselinkSiteBean> getAllVisibleSites();

	   /**
	    * This returns a List of sites that are
	    * visible to the specified user
	    * @param user
	    * @return a List of CourselinkSiteBean objects
	    */
	   public List<CourselinkSiteBean> getAllVisibleSites(User user);

	   /**
	    * This returns a List of requests that are
	    * visible to the current user
	    * @return a List of CourselinkRequest objects
	    */
	   public List<CourselinkRequestBean> getAllVisibleRequests();

	   /**
	    * This returns a List of requests that are
	    * visible to the specified user
	    * @param user
	    * @return a List of CourselinkRequest objects
	    */
	   public List<CourselinkRequestBean> getAllVisibleRequests(User user);

	   /**
	    * This returns a List of reject requests
	    * @return
	    */
	   public List<CourselinkRequestBean> getAllRejectRequests();

	   /**
	    * This returns a List of requests.
	    * @param siteId
	    * @return
	    */
	   public List<CourselinkRequestBean> getRequests(String siteId);
	   /**
	    * This returns a List of sites that are
	    * to create by current user
	    * @param userId the internal user id (not username)
	    * @return a List of CourselinkSiteBean objects
	    */
	   public List<CourselinkSiteBean> getAllOwnersSites();

	   /**
	    * This returns a List of sites that are
	    * to create by the specified user
	    * @param user
	    * @return a List of CourselinkSiteBean objects
	    */
	   public List<CourselinkSiteBean> getAllOwnersSites(User user);

	   /**
	    * This returns a Sakai Proxy
	    * @return
	    */
	   public SakaiProxy getSakaiProxy();

	   /**
	    * This returns an coureselinkSite based on an id
	    * @param siteId of the coureselinkSite to fetch
	    * @return a CourselinkSite or null if none found
	    */
	   public CourselinkSite getCourselinkSite(String siteId);

	   /**
	    * Save (Create or Update) an item (uses the current site)
	    * @param item the Item to create or update
	    */
	   public void saveSite(CourselinkSite item);

	   /**
	    * Save (Create or Update) an item (uses the current site)
	    * @param item the PretestItem to create or update
	    */
	   public void saveRequest(CourselinkRequest item);

	   /**
	    *
	    * @param siteId
	    * @return
	    */
	   public Site createSite (String siteId, String useTemplate);
	   /**
	    *
	    * @param user
	    * @param siteId
	    * @return
	    */
	   public Site createSite (User user, String siteId, String useTemplate);

	   /**
	    *
	    * @param date
	    * @return
	    */
	   public List<CourselinkRequestBean> getRemoveRequests(Date date);

	   /**
	    *
	    * @param date
	    * @return
	    */
	   public Integer removeRequests(Date date);

	   /**
	    *
	    *
	    *
	    */
	   public Long getMaxStatus(long courselink_site_id);
}
