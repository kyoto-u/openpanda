package courselink.kyoto_u.ac.jp.logic;

import java.util.Map;

import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.user.api.User;

/**
 * An interface to abstract all Sakai related API calls in a central method that can be injected into our app.
 *
 * @author Mike Jennings (mike_jennings@unc.edu)
 *
 */
public interface SakaiProxy {
	public final static String NO_LOCATION = "noLocationAvailable";

	// permissions
	public final static String ITEM_WRITE_ANY = "courselink.write.any";
	public final static String ITEM_READ_HIDDEN = "courselink.read.hidden";

	/**
	 * Get current url
	 * @return
	 */
	public String getCurrentToolUrl();
	/**
	 * Get current siteid
	 * @return
	 */
	public String getCurrentSiteId();

	/**
	 * Get current user id
	 * @return
	 */
	public String getCurrentUserId();

	/**
	 * Get current user display name
	 * @return
	 */
	public String getCurrentUserDisplayName();

	/**
	 * Is the current user a superUser? (anyone in admin realm)
	 * @return
	 */
	public boolean isSuperUser();

	/**
	 * Has maintain role or not
	 * @param userId
	 * @param siteId
	 * @return
	 */
	public boolean isMaintainRole(String userId, String siteId);
	/**
	 * Post an event to Sakai
	 *
	 * @param event			name of event
	 * @param reference		reference
	 * @param modify		true if something changed, false if just access
	 *
	 */
	public boolean isStudentRole(String userId, String siteId);
	/**
	 * Post an event to Sakai
	 *
	 * @param event			name of event
	 * @param reference		reference
	 * @param modify		true if something changed, false if just access
	 *
	 */
	public void postEvent(String event,String reference,boolean modify);

	/**
	 * Wrapper for ServerConfigurationService.getString("skin.repo")
	 * @return
	 */
	public String getSkinRepoProperty();

	/**
	 * Gets the tool skin CSS first by checking the tool, otherwise by using the default property.
	 * @param	the location of the skin repo
	 * @return
	 */
	public String getToolSkinCSS(String skinRepo);

	/**
	 * Check if this user has super admin access
	 * @param userId the internal user id (not username)
	 * @return true if the user has admin access, false otherwise
	 */
	public boolean isUserAdmin(String userId);

	/**
	 * @return the current sakai user
	 */
	public User getCurrentUser();

	/**
	 * @param siteId
	 * @return site for siteId
	 */
	public Site getSite(String siteId);

	/**
	 * @param siteId
	 * @return true if exist, false otherwise;
	 */
	public boolean isSiteInCm(String siteId);

	/**
	 * @param userId
	 * @param siteId
	 * @return role
	 */
	public String getRoleInCm(String userId, String siteId);

	/**
	 *
	 * @param siteId
	 * @return
	 */
	public Map<String, String> getMembersInCm(String siteId);

	/**
	 * @param siteId
	 * @return created site
	 * @throws IdInvalidException
	 * @throws IdUsedException
	 * @throws PermissionException
	 */
	public Site addSite(String siteId) throws IdInvalidException, IdUsedException, PermissionException;

	/**
	 * save site
	 * @param site
	 * @throws IdUnusedException
	 * @throws PermissionException
	 */
	public void siteSave(Site site) throws IdUnusedException, PermissionException;

	/**
	 * @param param
	 * @return value of Server Property
	 */
	public String getServerProperty(String param);

	/**
	 * @return the current sakai user eid
	 */
	public String getCurrentUserEid();

	/**
	 * @param site
	 * @param toolId
	 * @return pageId created
	 */
	public String createPage(Site site, String toolId);

	/**
	 * Get the display name for a user by their unique id
	 * @param userId the current sakai user id (not username)
	 * @return display name (probably firstname lastname) or "----------" (10 hyphens) if none found
	 */
	public String getUserDisplayName(String userId);

	/**
	 * @param userId
	 * @return user eid
	 */
	public String getUserEid(String userId);

	/**
	 *
	 * @param userEid
	 * @return
	 */
	public User getUserByEid(String userEid);

	/**
	 * Check if a site exist.
	 * @param siteId
	 * @return true if exit, false otherwise
	 */
	public boolean isExistSite(String siteId);

	/**
	 *
	 * @param siteId
	 * @return site Title from CM
	 */
	public String getSiteTitleFromCm(String siteId);
}
