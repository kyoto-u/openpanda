package jp.ac.kyoto_u.sakai.user;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.user.api.ContextualUserDisplayService;
import org.sakaiproject.user.api.PreferencesService;
import org.sakaiproject.user.api.User;

public class MaskingContextualUserDisplayService implements ContextualUserDisplayService {
	private static Log M_log = LogFactory.getLog(MaskingContextualUserDisplayService.class);

	private String userNameProperty = "displayName";
	private String userTypeProperty = "employeeType";
	private String userRegidProperty = "employeeNumber";

	private String maskString = "*";
	private int defaultMaskLength = 4;

	private Map<String, Pattern> eidMaskPatterns;
	private Map<String, Pattern> regidMaskPatterns;

	private boolean unmaskForSuperUsers = false;

	/* For Spring */

	public void init() {
		M_log.info("init()");
	}

	public void destroy() {
		M_log.info("destroy()");
	}


	/* Properties */

	public void setUserTypeProperty(String value) {
		userTypeProperty = value;
	}

	public void setUserRegidProperty(String value) {
		userRegidProperty = value;
	}

	public void setMaskString(String value) {
		maskString = value;
	}

	public void setDefaultMaskLength(int value) {
		defaultMaskLength = value;
	}

	public void setEidMaskPatterns(Map<String, Pattern> value) {
		eidMaskPatterns = value;
	}

	public void setRegidMaskPatterns(Map<String, Pattern> value) {
		regidMaskPatterns = value;
	}

	public void setUnmaskForSuperUsers(boolean value) {
		unmaskForSuperUsers = value;
	}

	/* Implement ContextualUserDisplayService */

	public String getUserDisplayId(User user) {
		if (user == null) {
			return null;
		}

		return getEmployeeNumber(user, checkMask(user));
	}

	public String getUserDisplayId(User user, String contextReference) {
		if (user == null) {
			return null;
		}

		if (contextReference == null) {
			return getUserDisplayId(user);
		}

		if (contextReference.equals("employeeNumber") || contextReference.equals("Site Info") || contextReference.equals("course") || contextReference.equals("training")) {
			return getRegid(user, checkMask(user));
		}

		if (contextReference.equals("messageForum")) {
			return getEid(user, !SecurityService.isSuperUser());
		}

		return null;
	}

	public String getUserDisplayName(User user) {
		if(!"ja_JP".equals(getUserLocaleString())){
			if(user.getProperties().getProperty("displayName;lang-en") != null){
				return user.getProperties().getProperty("displayName;lang-en");
			}
		}
		return null;
	}

	public String getUserDisplayName(User user, String contextReference) {
		if (user == null)  {
			return null;
		}
		if (contextReference == null)  {
			return getUserDisplayName(user);
		}
		if(!"ja_JP".equals(getUserLocaleString())){
			if(user.getProperties().getProperty("displayName;lang-en") != null){
				return user.getProperties().getProperty("displayName;lang-en");
			}
		}

		return user.getDisplayName();
	}

	/* */

	private static String getUserProperty(User user, String key) {
		assert user != null;

		ResourceProperties props = user.getProperties();
		if (props == null) {
			return null;
		}

		return props.getProperty(key);
	}

	private String getUserType(User user) {
		assert user != null;

		return getUserProperty(user, userTypeProperty);
	}

	private String getEid(User user, boolean masked) {
		assert user != null;

		String rawId = user.getEid();
		if (rawId == null) {
			return null;
		}

		return masked ? maskStringForUser(user, rawId, eidMaskPatterns) : rawId;
	}

	private String getRegid(User user, boolean masked) {
		assert user != null;

		String rawId = getUserProperty(user, userRegidProperty);
		if (rawId == null) {
			return null;
		}

		return masked ? maskStringForUser(user, rawId, regidMaskPatterns) : rawId;
	}

	private String maskStringForUser(User user, String rawString, Map<String, Pattern> maskPatterns) {
		assert user != null;
		assert rawString != null;
		assert maskPatterns != null;

		String userType = getUserType(user);
		if (userType == null || maskPatterns == null || !maskPatterns.containsKey(userType)) {
			// hide the entire id for unknown userType
			return StringUtils.repeat(maskString, defaultMaskLength);
		} else {
			Pattern maskPattern = maskPatterns.get(userType);
			if (maskPattern == null) {
				return rawString;
			}

			return maskString(rawString, maskPattern);
		}
	}


	private String maskString(String rawString, Pattern maskPattern) {
		assert rawString != null;
		assert maskPattern != null;

		StringBuffer buffer = new StringBuffer();

		Matcher m = maskPattern.matcher(rawString);
		while (m.find()) {
			int matchLength = m.group().length();
			String replacement = StringUtils.repeat(maskString, matchLength);
			m.appendReplacement(buffer, replacement);
		}
		m.appendTail(buffer);

		return buffer.toString();
	}

	private String getEmployeeNumber(User user,boolean masked){
		assert user != null;
		String employeeNumber = (String)user.getProperties().get(userRegidProperty);
		if( employeeNumber == null || employeeNumber.isEmpty()){
			employeeNumber = user.getEid();
		}
		return masked ? maskStringForUser(user, employeeNumber, eidMaskPatterns) : employeeNumber;
	}

	private boolean checkMask(User user){

		assert user != null;
		boolean checkMask = true;
		String loginUserType = null;
		try {
			loginUserType = getSiteRoleId(SessionManager.getCurrentSessionUserId());
		} catch (IdUnusedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		if(SecurityService.isSuperUser()){
			checkMask = false;
		}else{
			if("Instructor".equals(loginUserType)){
				String siteUserType = null;
				try{
					siteUserType = getSiteRoleId(user.getId());
					if("Instructor".equals(siteUserType)){
						checkMask = true;
					}else{
						checkMask = false;
					}
				}catch(IdUnusedException e){
					e.printStackTrace();
				}
			}else if("Student".equals(loginUserType) || "TA".equals(loginUserType) || "Teaching Assistant".equals(loginUserType)){
				checkMask = true;
			}else{
				checkMask = true;
			}
		}
		return checkMask;
	}

	@SuppressWarnings({ "deprecation", "static-access" })
	private String getSiteRoleId(String userId) throws IdUnusedException{

		String siteId = "";
		Placement placement = org.sakaiproject.tool.cover.ToolManager.getCurrentPlacement();
		if(placement == null){
			if(SessionManager.getCurrentSession() != null){

				if(SessionManager.getCurrentToolSession() == null){
					return null;
				}else{

					ToolConfiguration toolConfig = SiteService.findTool(SessionManager.getCurrentToolSession().getId());
					if(toolConfig != null){
						siteId = toolConfig.getSiteId();
					}else{
						return null;
					}
				}
			}
		}else{
			siteId = placement.getContext();
		}

		return SiteService.getSite(siteId).getMember(userId) != null ? SiteService.getSite(siteId).getMember(userId).getRole().getId() : null;

	}

	private String getUserLocaleString(){
		PreferencesService preferencesService = (PreferencesService) ComponentManager.get(PreferencesService.class.getName());
		Locale locale = preferencesService.getLocale(SessionManager.getCurrentSessionUserId());
		if(locale == null){
			locale= Locale.US;
		}
		return locale.getLanguage() + "_" + locale.getCountry();
	}

}
