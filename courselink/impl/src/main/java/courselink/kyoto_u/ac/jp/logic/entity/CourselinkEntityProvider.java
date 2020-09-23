/**
 * $Id: AppNameEntityProvider.java 61603 2009-07-03 14:18:25Z aaronz@vt.edu CourselinkEntityProvider.java 48619 2008-05-03 18:59:16Z aaronz@vt.edu $
 * $URL: https://source.sakaiproject.org/contrib/programmerscafe/appbuilder/trunk/templates/crud/entitybroker/AppNameEntityProvider.java $
 * CourselinkEntityProvider.java - Courselink - Apr 20, 2008 5:13:25 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Sakai Project/Sakai Foundation
 * Licensed under the Educational Community License version 1.0
 *
 * A copy of the Educational Community License has been included in this
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 */

package courselink.kyoto_u.ac.jp.logic.entity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.EntityProducer;
import org.sakaiproject.entity.api.HttpAccess;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.entitybroker.EntityBroker;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.entityprovider.capabilities.AutoRegisterEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RESTful;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RequestAware;
import org.sakaiproject.entitybroker.entityprovider.extension.RequestGetter;
import org.sakaiproject.entitybroker.entityprovider.search.Search;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.user.api.User;
import org.sakaiproject.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import courselink.kyoto_u.ac.jp.logic.ProjectLogic;
import courselink.kyoto_u.ac.jp.logic.SakaiProxy;
import courselink.kyoto_u.ac.jp.model.CourselinkRequest;
import courselink.kyoto_u.ac.jp.model.CourselinkSite;
import courselink.kyoto_u.ac.jp.util.Constant;

/**
 * Sample Courselink provider
 * @author Sakai App Builder -AZ
 */
@SuppressWarnings("deprecation")
public class CourselinkEntityProvider extends Constant implements RESTful, AutoRegisterEntityProvider, RequestAware, EntityProducer {

	private static final Logger log = Logger.getLogger(CourselinkEntityProvider.class);

	private static final boolean DUPLICATE_REQUEST = false;

   private ProjectLogic logic;
   public void setLogic(ProjectLogic logic) {
      this.logic = logic;
   }

	private SakaiProxy sakaiProxy;
	public void setSakaiProxy(SakaiProxy sakaiProxy) {
		this.sakaiProxy = sakaiProxy;
	}

   private DeveloperHelperService developerHelperService;
   public void setDeveloperHelperService(DeveloperHelperService developerHelperService) {
      this.developerHelperService = developerHelperService;
   }

   public static String PREFIX = "courselink";
   public String getEntityPrefix() {
      return PREFIX;
   }

   private static String REFERENCE_ROOT =  Entity.SEPARATOR + PREFIX;
   private static String APPLICATION_ID = "sakai." + PREFIX;

   private EntityBroker entityBroker;
   public void setEntityBroker(EntityBroker entityBroker){
	   this.entityBroker = entityBroker;
   }


   private EntityManager entityManager;
   public void setEntityManager(EntityManager entityManager){
	   this.entityManager = entityManager;
   }

   public String[] getHandledOutputFormats() {
      return null;
   }

   public String[] getHandledInputFormats() {
      return null;
   }

	  /**
	   * Initialize the servlet.
	   *
	   * @param config
	   *        The servlet config.
	   * @throws ServletException
	   */
	  public void init() throws ServletException {
		  System.out.println("courselink entity init:" + Entity.SEPARATOR + PREFIX);
		  log.info("courselink entity init:" + Entity.SEPARATOR + PREFIX);
	    entityManager.registerEntityProducer(this, Entity.SEPARATOR + PREFIX);
	  }

	  public Object getEntity(EntityReference ref) {
	   Object object = new Object();
	   if(! checkAuthentication()){
		   object = null;
		   return object;
	   }
	   String param[] = ref.getId().split(SPLIT_PARAM_STRING);
	   Map<String, String>paramMap = getParameter(param);
	   String userId = paramMap.get(ATTR_USERID);
	   String siteId = paramMap.get(ATTR_SITEID);
	   String requestFrom = paramMap.get(ATTR_REQUEST);
	   String confirmed = paramMap.get(ATTR_CONFIRMED);
	   String useTemplate = paramMap.get(USE_TEMPLATE);
	   String siteSetup = paramMap.get(ATTR_SITESETUP);
	   doProcess(siteId, requestFrom, userId, confirmed, useTemplate, requestGetter.getResponse(), ref.getId(), siteSetup);
	   return object;
   }

	  private void doProcess(String sectionId, String requestFrom, String userId, String confirmed, String useTemplate, HttpServletResponse res, String param, String siteSetup){
		   User user = sakaiProxy.getCurrentUser();
		   /**if( ! requestCheck(requestFrom) ){
			   // user not login or user invalid
			   saveRejectRequest(user.getId(), "Invalid Location Error(" + convertNull(requestFrom) + "):" + param);
			   doRedirect(res, INVALID_ERROR_URL + "?errKind=invalid" );
			   return;
		   }
		   if( ! userCheck(user, userId)) {
			   // user not login or user invalid
			   saveRejectRequest(user.getId(), "Invalid User Error(" + convertNull(userId) + "):" + param);
			   doRedirect(res, INVALID_ERROR_URL + "?errKind=invalid" );
			   return;
		   }*/
		   if (sectionId == null || sectionId.length()<1 || sectionId.equals("myworkspace")){
			   // Redirect to Dashboard in myworkspace.
			   String url = openMyworkspacePage(DASHBOARD_ID);
			   doRedirect(res, url);
			   return;
		   }
		   Site site = sakaiProxy.getSite(sectionId);
		   // after course created
		   if ( siteSetup != null ){
			   if ("true".equals(siteSetup)){
				   String nextUrl = openCourseSite(site, SITEINFO_ID);
				   doRedirect(res,nextUrl);
				   return;
			   }
			   if ( "list".equals(requestFrom)){
				   String nextUrl = openMyworkspacePage(COURSELINKTOOL_ID);
				   doRedirect(res,nextUrl);
				   return;
			   }
			   doRedirectDashboard(site, res);
			   return;
		   }
		   //end
		   if(! sakaiProxy.isSiteInCm(sectionId)){
			   // siteId not registered in roster
			   saveRejectRequest(user.getId(), "Site Error(" + convertNull(sectionId) + "):" + param);
			   doRedirect(res, INVALID_ERROR_URL + "?errKind=siteInvalid" );
			   return;
		   }

		   boolean createSiteFlg = false;
		   if (site == null)  { // siteId not exist
			   if( sakaiProxy.isUserAdmin(user.getEid()) || sakaiProxy.isMaintainRole(user.getEid(), sectionId))  {
				   if (confirmed != null && confirmed.equals("true"))  {
					   site = logic.createSite(user, sectionId, useTemplate);
					   createSiteFlg = true;
				   }  else  {
					   doRedirect(res, COURSE_CREATION_CONFIRM_URL + "?" + ATTR_SITEID + "=" + sectionId);
					   return;
				   }
			   }  else if (sakaiProxy.isStudentRole(user.getEid(), sectionId))  {
				   // Save request
				   saveRequest(user, sectionId);
			   }  else  {
				   saveRejectRequest(user.getId(), "Site Error(" + convertNull(sectionId) + "):" + param);
				   doRedirect(res, INVALID_ERROR_URL + "?errKind=studentInvalid" );
				   return;
			   }
		   }  else if (! (sakaiProxy.isStudentRole(user.getEid(), sectionId) || sakaiProxy.isMaintainRole(user.getEid(), sectionId) ) )  {
			   saveRejectRequest(user.getId(), "Site Error(" + convertNull(sectionId) + "):" + param);
			   doRedirect(res, INVALID_ERROR_URL + "?errKind=studentInvalid" );
			   return;
		   }

		   if (createSiteFlg) {
			   doRedirect(res, SETUP_CONFIRM_URL  + "?" + ATTR_SITEID + "=" + site.getId());
			   return;
		   }
		   // Redirect to Dashboard in course site.
		   doRedirectDashboard(site, res);
		   return;
	  }

	  private void doRedirectDashboard(Site site, HttpServletResponse res){
		   String url = openCourseSiteDashboard(site);
		   if ( url == null){
			   url = openMyworkspacePage(COURSELINKTOOL_ID);
		   }
		   doRedirect(res, url);
	  }

   private boolean requestCheck(String requestFrom ){
	   if (REQUEST_FROM_ID.equals(requestFrom)){
		   return true;
	   }
	   return false;
   }

   private boolean userCheck(User user, String userId){
	   if( user == null){ return false; }
	   if( userId == null || userId.length() < 1){ return false; }
	   if( userId.equals(user.getEid())){ return true; }
	   return false;
   }

   private String convertNull(String value){
	   if(value == null){
		   return "";
	   }
	   return value;
   }

   private Map<String, String> getParameter(String[] params){
	   Map<String, String> map = new HashMap<String, String>();
	   if(params == null || params.length < 1){
		   return map;
	   }
	   for(int i=0; i < params.length; i++){
		   String param[] = params[i].split(SPLIT_ATTR_STRING);
		   if(param == null || param.length < 2){
			   continue;
		   }
		   map.put(param[0], param[1]);
	   }
	   return map;
   }

   /**
    * If site exist return site. If not create site.
    * @param user
    * @param siteId
    * @return
    */
   private Site createCourseSite(User user, String siteId){

	   Site site = sakaiProxy.getSite(siteId);
	   if( site != null ){
		   return site;
	   }
	   if(! sakaiProxy.isSiteInCm(siteId)){
		   // siteId not registered in roster
		   return null;
	   }
	   return logic.createSite(user, siteId, null);
   }


   /**
    * save request if request not or DUPLICATE_REQUEST is true
    * @param user
    * @param siteId
    */
   private void saveRequest(User user,String siteId){
	   CourselinkSite courselinkSite = logic.getCourselinkSite(siteId);
	   if(courselinkSite == null){
		   courselinkSite = new CourselinkSite(siteId);
		   logic.saveSite(courselinkSite);
	   }
	   if ((! hasRequest(user.getId(), courselinkSite)) || DUPLICATE_REQUEST){
		   CourselinkRequest courselinkRequest =
			   new CourselinkRequest(courselinkSite, user.getId());
		   logic.saveRequest(courselinkRequest);
	   }
   }

   private void saveRejectRequest(String userId, String error){
	   CourselinkRequest courselinkRequest =
		   new CourselinkRequest(null, userId);
	   courselinkRequest.setStatus(REJECT_STATUS);
	   courselinkRequest.setMemo(error);
	   logic.saveRequest(courselinkRequest);
   }

   private boolean hasRequest(String userId, CourselinkSite courselinkSite){
	   Set courselinkRequests = courselinkSite.getCourselinkRequests();
	   if(courselinkRequests == null || courselinkRequests.size()<1){
		   return false;
	   }
	   Iterator ite = courselinkRequests.iterator();
	   while(ite.hasNext()){
		   CourselinkRequest courselinkRequest = (CourselinkRequest)ite.next();
		   if(courselinkRequest.getOwnerId().equals(userId)){
			   return true;
		   }
	   }
	   return false;

   }

   /**
    * Return URL of Dashboard page in Site .
    */
   private String openCourseSiteDashboard(Site site){
	   if ( site == null){
		   return null;
	   }
	   String dashboardPageId = getPageIdAndCreate(site,DASHBOARD_ID);
	   String url = getSiteUrl(site.getId());
	   if(dashboardPageId != null){
		   url += getPageParts(dashboardPageId);
	   }
	   return url;
   }

   private String openCourseSite(Site site, String toolId){
	   if ( site == null){
		   return null;
	   }
	   String siteinfoPageId = getPageIdAndCreate(site,toolId);
	   String url = getSiteUrl(site.getId());
	   if(siteinfoPageId != null){
		   url += getPageParts(siteinfoPageId);
	   }
	   return url;
   }

   private String openCourseSiteSiteInfoImportSelection(Site site){
	   String url = openCourseSite(site, SITEINFO_ID);
	   if ( url == null){
		   return null;
	   }
	   return url += "?" + SITEINFO_IMPORTSELECTION_PARAM;
   }

   /**
    * Return URL of  page in current user's MyWorkspace .
    * if not redirect to home.
    */
   private String openMyworkspacePage(String toolId){
	   String userEid = sakaiProxy.getCurrentUserEid();
	   String userId = sakaiProxy.getCurrentUserId();
	   Site site = sakaiProxy.getSite(getUserSiteId(userId));
	   String pageId = getPageIdAndCreate(site,toolId);
	   String url = getMyworkspaceUrl(userEid);
	   if(pageId != null){
		   url += getPageParts(pageId);
	   }
	   return url;
   }

   private String getPageParts(String id){
	   return PAGE_VALUE + id;
   }

   /**
    * return page's id. if not then create page.
    * @param site
    * @return tool page's id
    */
   private String getPageIdAndCreate(Site site, String toolId){
	   List pages = site.getOrderedPages();
	   String pageId = null;
		for (Iterator i = pages.iterator(); i.hasNext();)
		{
			// check if current user has permission to see page
			SitePage p = (SitePage) i.next();
			List pTools = p.getTools();
			Iterator iPt = pTools.iterator();

			boolean allowPage = false;
			while (iPt.hasNext())
			{
				ToolConfiguration placement = (ToolConfiguration) iPt.next();
				if(placement.getToolId().equals(toolId)){
					pageId = p.getId();
					break;
				}
			}
			if(pageId != null){
				break;
			}
		}
		// if autocrate is on and dashboard's Page not exist then create dashboard's page.
		/**if(dashboardPageId == null && sakaiProxy.getServerProperty(DASHBOARD_AUTO_CREATE).equals(TRUE_VALUE)){
			dashboardPageId = sakaiProxy.createPage(site, DASHBOARD_ID);
		}*/
		if(pageId == null ){
			try{
				pageId = sakaiProxy.createPage(site, toolId);
			}catch (Exception e){}
		}
		return pageId;
   }

   private void doRedirect(HttpServletResponse res, String url){
		try {
			res.sendRedirect(url);
			return ;
		}catch (IOException e) {
			log.debug(e.getMessage());
		}
   }

   private boolean checkAuthentication(){
	   HttpServletRequest req = requestGetter.getRequest();
	   HttpServletResponse res = requestGetter.getResponse();
	   return checkAuthentication(req, res);
   }
   private boolean checkAuthentication(HttpServletRequest req, HttpServletResponse res){
       if (req.getRemoteUser() == null) {
           try {
               String url = req.getRequestURL().toString();
               String context = req.getContextPath();
               String prefix = url.substring(0,url.lastIndexOf(context));

               res.sendRedirect(prefix + "/authn/login?url="
                                + URLEncoder.encode(req.getRequestURL().toString(), "UTF-8"));
               return false;
           }
           catch (UnsupportedEncodingException e) {
               e.printStackTrace();
               return false;
           }
           catch (IOException e) {
               e.printStackTrace();
               return false;
           }
       }
       return true ;

   }
   private String getUserSiteId(String userId){
	   return "~" + userId;
   }

   private String getMyworkspaceUrl(String userEid){
	   return getSiteUrl(getUserSiteId(userEid));
   }

   public String createEntity(EntityReference ref, Object entity) {
      return createEntity(ref, entity, null);
   }

   public void updateEntity(EntityReference ref, Object entity) {
      updateEntity(ref, entity, null);
   }

   public void deleteEntity(EntityReference ref) {
	   deleteEntity(ref, null);
   }

   private String getCurrentUser() {
      String userRef = developerHelperService.getCurrentUserReference();
      if (userRef == null) {
         throw new SecurityException("Must be logged in to create/update/delete entities");
      }
      return developerHelperService.getUserIdFromRef(userRef);
   }

	private RequestGetter requestGetter;
	public void setRequestGetter(RequestGetter requestGetter) {
		this.requestGetter = requestGetter;
	}

	@Override
	public String createEntity(EntityReference ref, Object entity,
			Map<String, Object> params) {
		return null;
	}

	@Override
	public Object getSampleEntity() {
		return null;
	}

	@Override
	public void updateEntity(EntityReference ref, Object entity,
			Map<String, Object> params) {
	}

	@Override
	public void deleteEntity(EntityReference ref, Map<String, Object> params) {
	}

	@Override
	public List<?> getEntities(EntityReference ref, Search search) {
		return null;
	}

	@Override
	public String archive(String arg0, Document arg1, Stack<Element> arg2,
			String arg3, List<Reference> arg4) {
		return null;
	}

	@Override
	public Entity getEntity(Reference arg0) {
		return null;
	}

	@Override
	public Collection<String> getEntityAuthzGroups(Reference arg0, String arg1) {
		return null;
	}

	@Override
	public String getEntityDescription(Reference arg0) {
		return null;
	}

	@Override
	public ResourceProperties getEntityResourceProperties(Reference arg0) {
		return null;
	}

	@Override
	public String getEntityUrl(Reference arg0) {
		return null;
	}

	@Override
	public HttpAccess getHttpAccess() {
			return new HttpAccess()
			{
				  public void handleAccess(HttpServletRequest req, HttpServletResponse res, Reference ref,
							Collection<String> copyrightAcceptedRefs) {
					   if(! checkAuthentication(req, res)){
						   return;
					   }
					  Map<String, String> props = entityBroker.getProperties(req.getPathInfo());
					  String userId = ref.getId();
					  String sectionId = ref.getContext();
					  String requestFrom = ref.getContainer();
					  doProcess(sectionId, requestFrom, userId, "false", null, res, ref.getReference(),null);
				  }

			};

	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public String merge(String arg0, Element arg1, String arg2, String arg3,
			Map<String, String> arg4, Map<String, String> arg5, Set<String> arg6) {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean parseEntityReference(String reference, Reference ref) {
		if (reference.startsWith(REFERENCE_ROOT))
		{
			// /courselink/siteId/requestFrom/userEid/
			String[] parts = StringUtil.split(reference, Entity.SEPARATOR);

			String siteId = null;
			String requestFrom = null;
			String userEid = null;

			if (parts.length > 3)
			{
				siteId = parts[2];
				requestFrom = parts[3];
				userEid = parts[4];
			}
			ref.set(APPLICATION_ID, null, userEid, requestFrom, siteId);
			return true;
		}

		return false;

	}

	@Override
	public boolean willArchiveMerge() {
		return false;
	}


}
