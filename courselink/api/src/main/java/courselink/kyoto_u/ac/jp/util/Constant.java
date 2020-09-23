package courselink.kyoto_u.ac.jp.util;

import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;

public class Constant {
	protected static final int ACCEPT_STATUS = 0;
	protected static final int REJECT_STATUS = 1;

	protected static final String DASHBOARD_ID = "sakai.dashboard";
	protected static final String DASHBOARD_AUTO_CREATE = "couselink.dashboard.autocreate";
	protected static final String SITEINFO_ID = "sakai.siteinfo";
	protected static final String SITEINFO_IMPORTSELECTION_PARAM= "sakai_action=doMenu_siteInfo_importSelection_direct";
	protected static final String COURSELINKTOOL_ID = "sakai.courselink";
	protected static final String TRUE_VALUE = "true";
	protected static final String SITE_URL_PARTS = "/portal/site/";
	protected static final String PAGE_VALUE="page/";
	protected static final String USER_LOGIN_ERROR_URL = "/portal/";
	protected static final String INVALID_ERROR_URL = "/courselink-tool/error.jsp";
	protected static final String COURSE_CREATION_CONFIRM_URL = "/courselink-tool/createConfirm.jsp";
	protected static final String SETUP_CONFIRM_URL = "/courselink-tool/setupConfirm.jsp";
	protected static final String ADMIN_ID = "admin";
	protected static final String SPLIT_PARAM_STRING = ":";
	protected static final String SPLIT_ATTR_STRING = "=";
	protected static final String ATTR_SITEID = "_kcd";
	protected static final String ATTR_REQUEST = "_request";
	protected static final String ATTR_USERID = "_uid";
	protected static final String REQUEST_FROM_ID = "KULASIS";
	protected static final String ATTR_CONFIRMED = "confirmed";
	protected static final String USE_TEMPLATE = "useTemplate";
	protected static final String ATTR_SITESETUP = "sitesetup";

	protected static final String TEMPLATE_SITE_PROPERTY = "courselink.templatesite";
	protected static final String MAINTAIN_ROLE = "Instructor";
	protected static final String DATE_FORMAT = "yyyy/MM/dd";

	protected static final int USE_DEFAULT = 0;
	protected static final int USE_DEPARTMENT = 1;
	protected static final int USE_BEGINNER = 2;
	protected static final String BEGINNER_STATUS = "courselink.templatesite.beginner.status";

	protected static final String TEMPLATE_STRING = "courselink.templatesite.";



	   protected String getSiteUrl(String siteId){
		   return SITE_URL_PARTS + siteId + "/";
	   }

	   /**
	    * return page's id. if not then site id.
	    * @param site
	    * @return tool page's id
	    */
	   protected String getToolUrl(Site site, String toolId){
			ToolConfiguration toolConf = site.getToolForCommonId(toolId);
			if (toolConf != null) {
				return site.getTool(toolConf.getId()).getContainingPage().getUrl();
			} else {
				// ツールのページがない場合
				return site.getUrl();
			}
	   }

	   protected String getDirectUrl(Site site, String toolId){
			ToolConfiguration toolConf = site.getToolForCommonId(toolId);
			String directUrl = "/portal/directtool/";
			if (toolConf != null) {
				return directUrl + toolConf.getId();
			}
			// ツールのページがない場合
			return null;
	   }
}
