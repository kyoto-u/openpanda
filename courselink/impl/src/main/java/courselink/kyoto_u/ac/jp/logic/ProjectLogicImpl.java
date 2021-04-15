package courselink.kyoto_u.ac.jp.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.genericdao.api.search.Search;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.User;

import courselink.kyoto_u.ac.jp.bean.CourselinkRequestBean;
import courselink.kyoto_u.ac.jp.bean.CourselinkSiteBean;
import courselink.kyoto_u.ac.jp.dao.CourselinkDao;
import courselink.kyoto_u.ac.jp.model.CourselinkRequest;
import courselink.kyoto_u.ac.jp.model.CourselinkSite;
import courselink.kyoto_u.ac.jp.util.Constant;

/**
 * Implementation of {@link ProjectLogic}
 *
 * @author Mike Jennings (mike_jennings@unc.edu)
 *
 */
public class ProjectLogicImpl extends Constant implements ProjectLogic {
	private static final Logger log = Logger.getLogger(ProjectLogicImpl.class);
	private int setTemplate = 0;

	/**
	 * {@inheritDoc}
	 */

	/**
	 * init - perform any actions required here for when this bean starts up
	 */
	public void init() {
		log.info("init");
	}
	   private CourselinkDao dao;
	   public void setDao(CourselinkDao dao) {
	      this.dao = dao;
	   }

	   private SakaiProxy sakaiProxy;
	   public void setSakaiProxy(SakaiProxy sakaiProxy) {
	      this.sakaiProxy = sakaiProxy;
	   }
	   public SakaiProxy getSakaiProxy(){
		   return sakaiProxy;
	   }

	   private List<String> defaultTools;
	   public void setDefaultTools(List<String> defaultTools) {
		   this.defaultTools = defaultTools;
	   }

	   public List<CourselinkSiteBean> getAllVisibleSites(){
		   return getAllVisibleSites(sakaiProxy.getCurrentUser());
	   }

	   /**
	    * @param userId
	    * @return all courselinksites to be accessed.
	    */
	   public List<CourselinkSiteBean> getAllVisibleSites(User user) {
		      log.debug("Fetching visible sitess for " + user.getEid() );
		      List<CourselinkSite> l =  dao.findSitesByRequestOwnerId(user.getId());
		      return convertCourselinkSiteBean(l);
	   }

	   public List<CourselinkRequestBean> getAllVisibleRequests(){
		   return getAllVisibleRequests(sakaiProxy.getCurrentUser());
	   }

	   public List<CourselinkRequestBean> getAllVisibleRequests(User user){
		   log.debug("Fetching reuqest for " + user.getEid());
		   List<CourselinkRequest> l = dao.findRequestsByOwnerId(user.getId(),ACCEPT_STATUS);
		   return convertCourselinkRequestBean(l);
	   }

	   /**
	    *
	    * @return reject requests
	    */
	   public List<CourselinkRequestBean> getAllRejectRequests(){
		   log.debug("Fetching reject reuqest for admin");
		   List<CourselinkRequest> l = dao.findRequests(REJECT_STATUS);
		   return convertCourselinkRequestBean(l);
	   }

	   public List<CourselinkRequestBean> getRequests(String siteId){
		   log.debug("Fetching reuqest for site : " + siteId);
		   List<CourselinkRequest> l = dao.findRequestsBySiteId(siteId, ACCEPT_STATUS);
		   return convertCourselinkRequestBean(l);
	   }

	   public List<CourselinkSiteBean> getAllOwnersSites(){
		   return getAllOwnersSites(sakaiProxy.getCurrentUser());
	   }

	   public List<CourselinkSiteBean> getAllOwnersSites(User user) {
		      log.debug("Fetching owners sites for " + user.getEid() );
		      //List<CourselinkSite> courselinkSiteList = dao.findAll(CourselinkSite.class);
		      List<CourselinkSite> courselinkSiteList = dao.getAllCourselinkSite();
		      List<CourselinkSite> result = new ArrayList<CourselinkSite>();
		      boolean isAdmin = sakaiProxy.isUserAdmin(user.getEid());
		      for( int i =0; i < courselinkSiteList.size(); i++){
		    	  CourselinkSite courselinkSite = courselinkSiteList.get(i);
		    	  if(getRequestNum(courselinkSite) > 0){
			    	  if( isAdmin || sakaiProxy.isMaintainRole(user.getEid(), courselinkSite.getSiteId())){
			    		  result.add(courselinkSite);
			    	  }

		    	  }
		      }
		      return convertCourselinkSiteBean(result);
	   }

	   private int getRequestNum(CourselinkSite site){
		   Set<CourselinkRequest> requests = site.getCourselinkRequests();
		   if(requests == null){
			   return 0;
		   }
		   return requests.size();
	   }

	   /**
	    * convert from CourselinkSite to CourselinkSiteBean
	    * @param orgList
	    * @return CourselinksiteBean's list
	    */
	   private List<CourselinkSiteBean> convertCourselinkSiteBean(List<CourselinkSite> orgList){
		      List<CourselinkSiteBean> resultList = new ArrayList<CourselinkSiteBean>();
		   if (orgList == null || orgList.size() < 1){
			   return resultList;
		   }
		   for( int i=0; i < orgList.size(); i++){
			   CourselinkSite courselinkSite = orgList.get(i);
			   resultList.add(convertCourselinkSiteBean(courselinkSite));
		   }
		   return resultList;
	   }

	   private CourselinkSiteBean convertCourselinkSiteBean(CourselinkSite courselinkSite){
		   Long maxStatus;
		   if(courselinkSite == null){
			   maxStatus = (long) 0;
		   }else{
			   maxStatus = getMaxStatus(courselinkSite.getId());
		   }
		   CourselinkSiteBean courselinkSiteBean = new CourselinkSiteBean(courselinkSite, sakaiProxy, maxStatus);
		   return courselinkSiteBean;
	   }

	   private List<CourselinkRequestBean> convertCourselinkRequestBean(List<CourselinkRequest> orgList){
		   List<CourselinkRequestBean> resultList = new ArrayList<CourselinkRequestBean>();
		   if (orgList == null || orgList.size() < 1){
			   return resultList;
		   }
		   for( int i=0; i < orgList.size(); i++){
			   CourselinkRequest courselinkRequest = orgList.get(i);
			   CourselinkSiteBean courselinkSiteBean = convertCourselinkSiteBean(courselinkRequest.getCourselinkSite());
			   CourselinkRequestBean courselinkRequestBean =
				   new CourselinkRequestBean(courselinkRequest, courselinkSiteBean, sakaiProxy);
			   resultList.add(courselinkRequestBean);
		   }
		   return resultList;
	   }

	   public CourselinkSite getCourselinkSite(String siteId){
		      log.debug("Fetching visible item for " + siteId);
		      List<CourselinkSite> l = dao.findBySearch(CourselinkSite.class, new Search("siteId", siteId));
		      if( l == null || l.size()<1){
		    	  return null;
		      }
		      return l.get(0);
	   }

	   /**
	    * save Site log in DB
	    * @param item
	    */
	   public void saveSite(CourselinkSite item) {
	      log.debug("In saveItem with item:" + item.getSiteId());
	      // save item if new OR check if the current user can update the existing item
	      dao.save(item);
	      log.info("Saving item: "  + item.getSiteId());
	   }

	   /**
	    * save Request in DB
	    * @param item
	    */
	   public void saveRequest(CourselinkRequest item) {
	      log.debug("In saveRequest:" + item.getOwnerId());
	      // set the owner and site to current if they are not set
	      if (item.getOwnerId() == null) {
	         item.setOwnerId( sakaiProxy.getCurrentUserId() );
	      }
	      if (item.getInsertDate() == null) {
	         item.setInsertDate( new Date() );
	      }
	      // save item if new OR check if the current user can update the existing item
	      dao.save(item);
	      log.info("Saving request: " +  item.getOwnerId());
	   }

	   public Site createSite (String siteId, String useTemplate){
		   return createSite(sakaiProxy.getCurrentUser(), siteId, useTemplate);
	   }

	   public Site createSite (User user, String siteId, String useTemplate){
		   Site site = sakaiProxy.getSite(siteId);
		   if( site != null ){
			   return site;
		   }
		   String role = sakaiProxy.getRoleInCm(user.getEid(), siteId);
		   if( sakaiProxy.isUserAdmin(user.getId()) || MAINTAIN_ROLE.equals(role)){
			   // create Course Site
			   boolean createFlg = false;
			   try {
				site = sakaiProxy.addSite(siteId);
				// modify 2020/9/10
				addTools(site ,siteId, useTemplate);
			//	addMembers(site);  commented out by Shoji Kajita 2014/03/12
				sakaiProxy.siteSave(site);
				createFlg = true;
				} catch (IdInvalidException e) {
					log.debug("Failed in createSite : " + e.getMessage());
				} catch (IdUsedException e) {
					log.debug("Failed in createSite : " + e.getMessage());
				} catch (PermissionException e) {
					log.debug("Failed in createSite : " + e.getMessage());
				} catch (IdUnusedException e) {
					log.debug("Failed in createSite : " + e.getMessage());
				}
				// record to DB
				CourselinkSite courselinkSite = getCourselinkSite(siteId);
				if(courselinkSite == null){
					courselinkSite = new CourselinkSite(siteId);
				}
				if(createFlg){
					courselinkSite.setCreateUserId(user.getId());
					courselinkSite.setCreateDate(new Date());

					if(setTemplate != USE_DEFAULT){
						CourselinkRequest courselinkRequest =
								   new CourselinkRequest(courselinkSite, user.getId());
						if(setTemplate == USE_DEPARTMENT){
							courselinkRequest.setStatus(Integer.parseInt(siteId.split("-")[1]));
							saveRequest(courselinkRequest);
						}else if(setTemplate == USE_BEGINNER){
							courselinkRequest.setStatus(Integer.parseInt(sakaiProxy.getServerProperty(BEGINNER_STATUS)));
							saveRequest(courselinkRequest);
						}
					}
				}
				saveSite(courselinkSite);
		   }
		   return site;
	   }

	   public Integer removeRequests(Date date){
		   log.debug("Remove reuqests before " + date.toString());
		   Integer removeRequestNum = dao.removeRequestsByDate(date);
		   // delete CourselinkSite without request.
		   Integer removeSiteNum = dao.removeSitesNoRequest();
		   return removeRequestNum;
	   }

	   public List<CourselinkRequestBean> getRemoveRequests(Date date){
		   log.debug("Remove reuqests before " + date.toString());
		   List<CourselinkRequest> l = dao.findRequestsByDate(date);
		   return convertCourselinkRequestBean(l);
	   }


	   /**
	    * add default tools in created site.
	    * @param site
	    */
		private void addTools(Site site ,String siteId, String useTemplate) {
			//Add Tool
			log.info("addTools() read");

			String[] params = siteId.split("-");
			String templateSiteId = "";
			StringBuilder buff = new StringBuilder();

			if(useTemplate != null && useTemplate.equals("experiencedPerson") && params[1] != "" && params[1] != null){
				buff.append(TEMPLATE_STRING);
				buff.append(siteId.split("-")[1]);
				templateSiteId = sakaiProxy.getServerProperty(buff.toString());
				setTemplate = USE_DEPARTMENT;
				if(templateSiteId == null || templateSiteId == ""){
					templateSiteId = sakaiProxy.getServerProperty(TEMPLATE_SITE_PROPERTY);
					setTemplate = USE_DEFAULT;
				}
			}else if(useTemplate != null && useTemplate.equals("beginner")){
				buff.append(TEMPLATE_STRING);
				buff.append("beginner");
				templateSiteId = sakaiProxy.getServerProperty(buff.toString());
				setTemplate = USE_BEGINNER;
				if(templateSiteId == null || templateSiteId == "" || sakaiProxy.getServerProperty(BEGINNER_STATUS) == null){
					templateSiteId = sakaiProxy.getServerProperty(TEMPLATE_SITE_PROPERTY);
					setTemplate = USE_DEFAULT;
				}
			}else{
				templateSiteId = sakaiProxy.getServerProperty(TEMPLATE_SITE_PROPERTY);
				setTemplate = USE_DEFAULT;
			}

			Site templateSite = sakaiProxy.getSite(templateSiteId);

			if(useTemplate != null && templateSite == null){
				templateSiteId = sakaiProxy.getServerProperty(TEMPLATE_SITE_PROPERTY);
				setTemplate = USE_DEFAULT;
				templateSite = sakaiProxy.getSite(templateSiteId);
			}

			if(templateSite != null){
				// copy pages from template site
				for ( SitePage templatePage: (templateSite.getPages())){
					SitePage page = site.addPage();
					page.setTitle(templatePage.getTitle());
					page.setLayout(templatePage.getLayout());
					for(ToolConfiguration templateTool : templatePage.getTools()){
						ToolConfiguration tool = page.addTool();
						tool.setTool(templateTool.getId(), templateTool.getTool());
						tool.setTitle(templateTool.getTitle() != null? templateTool.getTitle():"");
						tool.setLayoutHints(templateTool.getLayoutHints());
						Properties templateProperties = templateTool.getPlacementConfig();
						if(templateProperties != null && templateProperties.size() > 0){
							Set propSet = templateProperties.keySet();
							Iterator propIter = propSet.iterator();
							while( propIter.hasNext()){
								String propName = (String)propIter.next();
								tool.getPlacementConfig().setProperty(propName, templateProperties.getProperty(propName));
							}
						}
					}
				}
			}else{
				// create pages specified by defaultTools
				for(int i = 0;i<defaultTools.size();i++) {
					String toolid = defaultTools.get(i);
		   		//Tool
					SitePage page = site.addPage();
					Tool tool = ToolManager.getTool(toolid);
					page.setTitle(tool.getTitle());
					page.addTool(tool.getId());
				}
			}
		}

		/**
		 * regist members from CM
		 * @param site
		 */
		private void addMembers(Site site){
			Map<String, String> members = sakaiProxy.getMembersInCm(site.getId());
			Iterator it = members.keySet().iterator();
			while ( it.hasNext() ){
				String userEid = (String)it.next();
				String role = members.get(userEid);
				// add current user as the maintainer
				try{
					site.addMember(sakaiProxy.getUserByEid(userEid).getId(), role, true, false);
				}catch(Exception e){
					log.debug(e.getMessage());
				}
			}
		}

		public Long getMaxStatus(long courselink_request_id){
			return dao.getMaxStatus(courselink_request_id);
		}



}
