package courselink.kyoto_u.ac.jp.tool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.site.api.Site;
import org.sakaiproject.util.ResourceLoader;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import courselink.kyoto_u.ac.jp.bean.CourselinkRequestBean;
import courselink.kyoto_u.ac.jp.bean.CourselinkSiteBean;
import courselink.kyoto_u.ac.jp.logic.ProjectLogic;
import courselink.kyoto_u.ac.jp.util.Constant;
import lombok.Getter;
import lombok.Setter;

public class CourselinkController extends Constant implements Controller {
	private static final String REJECT_LIST_PAGE = "rejectlist";
	private static final String ACTION_CREATE_SITE = "createSite";
	private static final String ACTION_SHOW_REQUEST = "showRequest";
	private static final String ACTION_TOGGLE_HIDE_CREATED = "toggleHideCreated";
	private static final String ACTION_REMOVE_CONFIRM = "confirmRemoveLogs";
	private static final String ACTION_REMOVE = "removeLogs";
	protected static final String BEGINNER_STATUS = "courselink.templatesite.beginner.status";

	/**
	 * Courselink Controller
	 *
	 * @author
	 *
	 */
	@Setter
	@Getter
	private ProjectLogic projectLogic = null;

	private ResourceLoader rb = null;

	private boolean showCreatedFlg = false;

	public void init(){
		if (rb == null){
			rb = new ResourceLoader("courselink.kyoto_u.ac.jp.bundle.messages");
		}
	}

	public ModelAndView handleRequest(HttpServletRequest req,
			HttpServletResponse res) throws Exception {

		Map<String, Object> map = new HashMap<String,Object>();
		String page = ServletRequestUtils.getStringParameter(req, "page");

		boolean isAdmin = projectLogic.getSakaiProxy().isUserAdmin(projectLogic.getSakaiProxy().getCurrentUserId());

		// Case: display reject list.
		if( REJECT_LIST_PAGE.equals(page) && isAdmin){
			map.put("rejectList", projectLogic.getAllRejectRequests());
			return new ModelAndView("rejectlist", map);
		}

		// Case: display remove page.
		if( ACTION_REMOVE.equals(page) && isAdmin){
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			map.put("initDate", sdf.format(new Date()));
			return new ModelAndView("removeLogs", map);
		}

		String siteId = ServletRequestUtils.getStringParameter(req, "siteId");
		String action = ServletRequestUtils.getStringParameter(req, "action");

		// Case: confirm Remove.
		if( ACTION_REMOVE_CONFIRM.equals(action) && isAdmin){
			String dateString = ServletRequestUtils.getStringParameter(req,"insertDate");
			List<CourselinkRequestBean> resultList = new ArrayList<CourselinkRequestBean>();
			Date date = checkDate(dateString);
			if(date != null){
				resultList = projectLogic.getRemoveRequests(date);
			}else{
				map.put("removedRequestsNum","-1");
				return new ModelAndView("removeLogs", map);
			}
			map.put("insertDate", dateString);
			map.put("removeList", resultList);
			return new ModelAndView("removeConfirm", map);
		}

		// Case: do Remove.
		if( ACTION_REMOVE.equals(action) && isAdmin){
			String dateString = ServletRequestUtils.getStringParameter(req,"insertDate");
			Integer removeRequestsNum = -1;
			Date date = checkDate(dateString);
			if(date != null){
				removeRequestsNum = projectLogic.removeRequests(date);
			}
			map.put("removedRequestsNum",removeRequestsNum);
			return new ModelAndView("removeLogs", map);
		}

		// Case: show request
		if ( ACTION_SHOW_REQUEST.equals(action) && siteId != null  && siteId.length()>0){
			try{
				List<CourselinkRequestBean> requests = projectLogic.getRequests(siteId);
				CourselinkSiteBean siteBean = ((CourselinkRequestBean)requests.get(0)).getCourselinkSiteBean();
				map.put("siteId", siteBean.getSiteId());
				map.put("siteTitle", siteBean.getTitle());
				map.put("status", siteBean.isExistSite());
			}catch(Exception e){}
			map.put("requestDetailList", projectLogic.getRequests(siteId));
			return new ModelAndView("requestDetailList", map);
		}
		// Case: create site // redirect to parentWindow
		if ( ACTION_CREATE_SITE.equals(action) && siteId != null  && siteId.length()>0){
			Site site = projectLogic.createSite(siteId, null);
			boolean siteCreateResult = true;
			if (site == null){
				siteCreateResult = false;
			}
			map.put("siteCreateResult", siteCreateResult);
			map.put("createSiteId", siteId);
			if( siteCreateResult ){
				res.sendRedirect(SETUP_CONFIRM_URL  + "?" + ATTR_SITEID + "=" + site.getId() + "&" + ATTR_REQUEST + "=list");
				return null;
			}else {
				map.put("err","error_create_site");
			}
//			String parentUrl = projectLogic.getSakaiProxy().getCurrentToolUrl();
//			if(parentUrl != null){
//				parentUrl += "/index.htm?err=error_create_site";
//				res.sendRedirect(parentUrl);
//			}
			// redirect to created site. target="_parent"
			/**String toolUrl = getDirectUrl(site, SITEINFO_ID);
			if( toolUrl == null){
				projectLogic.getSakaiProxy().createPage(site, SITEINFO_ID);
				toolUrl = getDirectUrl(site, SITEINFO_ID);
			}
			if ( siteCreateResult){
				res.sendRedirect(toolUrl);
				return null;
			}*/
		}

		List<CourselinkRequestBean> requestList = projectLogic.getAllVisibleRequests();
		List<CourselinkSiteBean> ownerList = projectLogic.getAllOwnersSites();
		// Case: toggle display created site.
		if ( ACTION_TOGGLE_HIDE_CREATED.equals(action)){
			showCreatedFlg = !showCreatedFlg;
		}
		map.put("showCreatedFlg", showCreatedFlg);
		if(! (showCreatedFlg || isAdmin)){
			requestList = hideCreated(requestList);
			ownerList = hideCreated(ownerList);
		}

		String errId = ServletRequestUtils.getStringParameter(req, "err");
		if(errId != null && (! errId.isEmpty())){
			map.put("err", rb.getString(errId));
		}
		map.put("requestList", requestList);
		map.put("ownersList", ownerList);
		map.put("userDisplayName", projectLogic.getSakaiProxy().getCurrentUserDisplayName());
		map.put("userId", projectLogic.getSakaiProxy().getCurrentUserId());
		map.put("beginnerStatus",Long.parseLong(projectLogic.getSakaiProxy().getServerProperty(BEGINNER_STATUS)));

		// Case: display request list for admin.
		if ( isAdmin ){
			return new ModelAndView("adminindex", map);
		}
		// Case: display request list.
		return new ModelAndView("index", map);
	}

	private Date checkDate(String dateString){
		Date date = null;
		Integer removeRequestsNum = -1;
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		try{
			date = sdf.parse(dateString);
		}catch(Exception e){
		}
		return date;
	}

	private List hideCreated(List list){
		List result = new ArrayList();
		if(list == null || list.size()<1){
			return list;
		}
		for(Object obj: list){
			if(obj instanceof CourselinkRequestBean){
				CourselinkRequestBean requestBean = (CourselinkRequestBean)obj;
				if(! requestBean.isExistSite()){
					result.add(obj);
				}
			}else if (obj instanceof CourselinkSiteBean){
				CourselinkSiteBean siteBean = (CourselinkSiteBean)obj;
				if( ! siteBean.isExistSite()){
					result.add(obj);
				}
			}
		}
		return result;
	}
}
