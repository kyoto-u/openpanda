package courselink.kyoto_u.ac.jp.logic;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.sakaiproject.alias.api.Alias;
import org.sakaiproject.alias.api.AliasService;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.id.cover.IdManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.util.StringUtil;

import lombok.Getter;
import lombok.Setter;

/**
 * Implementation of {@link SakaiProxy}
 *
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class SakaiProxyImpl implements SakaiProxy {

	private static final Logger log = Logger.getLogger(SakaiProxyImpl.class);
	private static final String INSTRUCTOR_ROLE = "Instructor";
	private static final String STUDENT_ROLE = "Student";
	private static final String SITE_ID_TYPE = "courselink.siteid.type";

	public String getCurrentToolUrl(){
		String toolId = sessionManager.getCurrentToolSession().getPlacementId();
		Site site;
		try {
			site = siteService.getSite(getCurrentSiteId());
			ToolConfiguration tool = site.getTool(toolId);
			return serverConfigurationService.getPortalUrl() + "/directtool/" + tool.getId();
		} catch (IdUnusedException e) {
		}
		return null;
	}

	/**
 	* {@inheritDoc}
 	*/
	public String getCurrentSiteId(){
		return toolManager.getCurrentPlacement().getContext();
	}

	/**
 	* {@inheritDoc}
 	*/
	public String getCurrentUserId() {
		return sessionManager.getCurrentSessionUserId();
	}

	/**
 	* {@inheritDoc}
 	*/
	public String getCurrentUserDisplayName() {
	   return userDirectoryService.getCurrentUser().getDisplayName();
	}

	/**
 	* {@inheritDoc}
 	*/
	public boolean isSuperUser() {
		return securityService.isSuperUser();
	}

	/**
 	* {@inheritDoc}
 	*/
	public void postEvent(String event,String reference,boolean modify) {
		eventTrackingService.post(eventTrackingService.newEvent(event,reference,modify));
	}

	/**
 	* {@inheritDoc}
 	*/
	public String getSkinRepoProperty(){
		return serverConfigurationService.getString("skin.repo");
	}

	/**
 	* {@inheritDoc}
 	*/
	public String getToolSkinCSS(String skinRepo){

		String skin = siteService.findTool(sessionManager.getCurrentToolSession().getPlacementId()).getSkin();

		if(skin == null) {
			skin = serverConfigurationService.getString("skin.default");
		}

		return skinRepo + "/" + skin + "/tool.css";
	}


	/* (non-Javadoc)
	 * @see org.sakaiproject.pretest.logic.ExternalLogic#isUserAdmin(java.lang.String)
	 */
	public boolean isUserAdmin(String userId) {
		return securityService.isSuperUser(userId);
	}

	public boolean isMaintainRole(String userId, String siteId){
		if(INSTRUCTOR_ROLE.equals(getRoleInCm(userId, siteId))){
			return true;
		}
		return false;
	}

	public boolean isStudentRole(String userId, String siteId){
		if(STUDENT_ROLE.equals(getRoleInCm(userId, siteId))){
			return true;
		}
		return false;
	}

	public User getCurrentUser(){
		User user = null;
		try {
			user = userDirectoryService.getUser(getCurrentUserId());
		} catch (UserNotDefinedException e) {

		}
		return user;
	}

	public Site getSite(String sectionId){
		Site site = null;

		// hoge;

		try {
			site = siteService.getSite(sectionId);
		} catch (IdUnusedException e) {
			Set set = authzGroupService.getAuthzGroupIds(sectionId);
			if (set == null || set.isEmpty()) {
				return null;
			}
			for (Iterator i = set.iterator(); i.hasNext();){
				String ref = (String)i.next();
				String siteId = getSiteId(ref);
				try {
					site = siteService.getSite(siteId);
				} catch (IdUnusedException e1) {
				}
				if( site != null){
					return site;
				}
			}
			log.debug("Cannot find a site for sectionId: " + sectionId);
		}
		return site;
	}

	/**
	 * Access the site id extracted from a site reference.
	 *
	 * @param ref
	 *        The site reference string.
	 * @return The the site id extracted from a site reference.
	 */
	private String getSiteId(String ref)
	{
		String start = "site" + Entity.SEPARATOR;
		int i = ref.indexOf(start);
		if (i == -1) return ref;
		String id = ref.substring(i + start.length());
		return id;
	}

 	private void setSiteInfoFromCm(Site site, String siteId){
 		try{
 			CanonicalCourse course = cmService.getCanonicalCourse(siteId);
 			//Set members = cmService.getSectionMemberships(siteId);
 			site.setTitle(course.getTitle());
 			site.setDescription(course.getDescription());
 			AcademicSession term = cmService.getAcademicSession(siteId.split("-")[0]);
 			ResourcePropertiesEdit rp = site.getPropertiesEdit();
 			if(term != null){
				rp.addProperty(Site.PROP_SITE_TERM, term.getTitle());
				rp.addProperty(Site.PROP_SITE_TERM_EID, term.getEid());
			}
 			return;
 		}catch (Exception e1){
 			try{
 				CourseOffering course = cmService.getCourseOffering(siteId);
 				//Set members = cmService.getSectionMemberships(siteId);
 				//Set enrollements = cmService.getEnrollmentSets(siteId);
 				//Map<String, String> usres = groupProvider.getUserRolesForGroup(siteId);
 				site.setTitle(course.getTitle());
 				site.setDescription(course.getDescription());
 				ResourcePropertiesEdit rp = site.getPropertiesEdit();
 				AcademicSession term = course.getAcademicSession();
 				if(term != null){
 					rp.addProperty(Site.PROP_SITE_TERM, term.getTitle());
 					rp.addProperty(Site.PROP_SITE_TERM_EID, term.getEid());
 				}else{
 					term = cmService.getAcademicSession(siteId.split("-")[0]);
 					if(term != null){
 						rp.addProperty(Site.PROP_SITE_TERM, term.getTitle());
 	 	 				rp.addProperty(Site.PROP_SITE_TERM_EID, term.getEid());
 					}
 				}
 				return;
 			}catch (Exception e2){
 				e2.printStackTrace();
 			}
 		}
 		return;
 	}

	public String getSiteTitleFromCm(String siteId){
		try{
			CanonicalCourse course = cmService.getCanonicalCourse(siteId);
			//Set members = cmService.getSectionMemberships(siteId);
			String title = course.getTitle();
			return title;
		}catch (Exception e1){
			try{
				CourseOffering course = cmService.getCourseOffering(siteId);
				//Set members = cmService.getSectionMemberships(siteId);
				//Set enrollements = cmService.getEnrollmentSets(siteId);
				//Map<String, String> usres = groupProvider.getUserRolesForGroup(siteId);
				return course.getTitle();
			}catch (Exception e2){
				e2.printStackTrace();
			}
		}
		return null;
	}

	public boolean isSiteInCm(String siteId){
		boolean result = cmService.isCanonicalCourseDefined(siteId);
		if (result) {
			return true;
		}
		result = cmService.isCourseOfferingDefined(siteId);
		return result;
	}

	public String getRoleInCm(String userId, String siteId){
		try{
			Map<String, String> users = getMembersInCm(siteId);
			return users.get(userId);
		}catch(Exception e1){
		}
		return null;
	}

	public Map<String, String> getMembersInCm(String siteId){
		return groupProvider.getUserRolesForGroup(siteId);
	}

	public Site addSite(String siteIdReadable) throws IdInvalidException, IdUsedException, PermissionException{
		Site site = null;
		String siteId = siteIdReadable;
		boolean siteUuidFlg = !(isSiteidReadable());
		if(siteUuidFlg){
			siteId = IdManager.createUuid();
		}
		site = siteService.addSite(siteId, "course");
 		setSiteInfoFromCm(site, siteIdReadable);
		/*String title = getSiteTitleFromCm(siteId);
		if( title != null && title.length() > 0 ){
			site.setTitle(title);
		}*/
		site.setPublished(true);
 		String realm = siteService.siteReference(siteId);
		// set alias
		if(siteUuidFlg){
			String currentAlias = StringUtil.trimToNull(getSiteAlias(realm));

			if (currentAlias == null || !currentAlias.equals(siteIdReadable))
			{
				try {
					aliasService.setAlias(siteIdReadable, realm);
				} catch (IdUsedException ee) {
					log.warn(this + ".setSiteAlias:IdUsedException: " + siteIdReadable);
				} catch (IdInvalidException ee) {
					log.warn(this + ".setSiteAlias:IdInvalidException: " + siteIdReadable);
				} catch (PermissionException ee) {
					log.warn(this + ".setSiteAlias:PermissionException: " + siteIdReadable);
				}
			}
		}
 		AuthzGroup realmEdit;
 		try {
 			realmEdit = authzGroupService.getAuthzGroup(realm);
 			realmEdit.setProviderGroupId(siteIdReadable);
 			authzGroupService.save(realmEdit);
 		} catch (GroupNotDefinedException e) {
 			e.printStackTrace();
 		} catch (AuthzPermissionException e) {
 			e.printStackTrace();
 		}
		return site;
	}

	private boolean isSiteidReadable(){
		String siteIdType = serverConfigurationService.getString(SITE_ID_TYPE);
		if("uuid".equals(siteIdType)){
			return false;
		}
		return true;
	}

	private String getSiteAlias(String reference)
	{
		String alias = null;
		if (reference != null)
		{
			// get the email alias when an Email Archive tool has been selected
			List aliases = aliasService.getAliases(reference, 1, 1);
			if (aliases.size() > 0) {
				alias = ((Alias) aliases.get(0)).getId();
			}
		}
		return alias;
	}

	public void siteSave(Site site) throws IdUnusedException, PermissionException{
		siteService.save(site);
	}

	public String getServerProperty(String param){
		return serverConfigurationService.getString(param);
	}

	public String getCurrentUserEid(){
		String eid = null;
		String userId = getCurrentUserId();
		try{
			eid = userDirectoryService.getUser(userId).getEid();
		}catch (UserNotDefinedException e) {
			log.warn("Cannot get user  " + userId);
		}
		return eid;
	}

	public String createPage(Site site, String toolId){
		Tool tool = toolManager.getTool(toolId);
		SitePage page = site.addPage();
		page.setTitle(tool.getTitle());
		page.addTool(tool.getId());
		try{
			siteService.save(site);
		}catch(IdUnusedException idu){
			log.debug(idu.getMessage());
			return null;
		}catch(PermissionException pe){
			log.debug(pe.getMessage());
			return null;
		}
		return page.getId();
	}

	public String getUserDisplayName(String userId) {
	   String name = null;
		try {
			name = userDirectoryService.getUser(userId).getDisplayName();
		} catch (UserNotDefinedException e) {
			log.warn("Cannot get user displayname for id: " + userId);
			name = "--------";
		}
		return name;
	}

	public String getUserEid(String userId){
		try {
			User user = userDirectoryService.getUser(userId);
			return user.getEid();
		} catch (UserNotDefinedException e) {
			log.debug(e.getMessage());
		}
		return "";
	}

	public User getUserByEid(String userEid){
		try {
			return userDirectoryService.getUserByEid(userEid);

		} catch (UserNotDefinedException e) {
			log.debug(e.getMessage());
		}
		return null;
	}

	public boolean isExistSite(String siteId){
		if(getSite(siteId) == null){
			return false;
		}
		return true;
	}
	/**
	 * init - perform any actions required here for when this bean starts up
	 */
	public void init() {
		log.info("init");
	}


	@Getter @Setter
	private ToolManager toolManager;

	@Getter @Setter
	private SessionManager sessionManager;

	@Getter @Setter
	private UserDirectoryService userDirectoryService;

	@Getter @Setter
	private SecurityService securityService;

	@Getter @Setter
	private EventTrackingService eventTrackingService;

	@Getter @Setter
	private ServerConfigurationService serverConfigurationService;

	@Getter @Setter
	private SiteService siteService;

	@Getter @Setter
    private CourseManagementService cmService;

 	@Getter @Setter
 	private AuthzGroupService authzGroupService;

 	@Getter @Setter
 	private AliasService aliasService;

	private org.sakaiproject.authz.api.GroupProvider groupProvider = (org.sakaiproject.authz.api.GroupProvider) ComponentManager
	.get(org.sakaiproject.authz.api.GroupProvider.class);
}
