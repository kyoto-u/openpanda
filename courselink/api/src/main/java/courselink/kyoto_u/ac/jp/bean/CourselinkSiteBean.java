package courselink.kyoto_u.ac.jp.bean;

import courselink.kyoto_u.ac.jp.logic.SakaiProxy;
import courselink.kyoto_u.ac.jp.model.CourselinkSite;
import lombok.Getter;
import lombok.Setter;

public class CourselinkSiteBean {
	/**
	 * constructor
	 * @param courselinkSite
	 */
	public CourselinkSiteBean(CourselinkSite courselinkSite, SakaiProxy sakaiProxy, Long status){
		this.courselinkSite = courselinkSite;
		this.sakaiProxy = sakaiProxy;
		this.status = status;
	}
	
	@Setter
	@Getter
	private CourselinkSite courselinkSite = null;

	
	@Setter
	@Getter
	private SakaiProxy sakaiProxy;

	@Setter
	@Getter
	public Long status;

	public String getSiteId(){
		return courselinkSite.getSiteId();
	}
	
	public String getCreateDate(){
		return courselinkSite.getCreateDateString();
	}

	public String getCreateUserName(){
		String userName = "";
		String userId = courselinkSite.getCreateUserId();
		if (userId == null){
			return userName;
		}
		userName = sakaiProxy.getUserDisplayName(userId);
		return userName;
	}
	
	public int getRequestNum(){
		return courselinkSite.getRequestNum();
	}
	
	public boolean isExistSite(){
		if ( sakaiProxy.getSite(courselinkSite.getSiteId())== null ){
			return false;
		}
		return true;
	}
	
	public String getTitle(){
		return sakaiProxy.getSiteTitleFromCm(courselinkSite.getSiteId());
	}

	public Integer getCourselinkSiteId(){
		return courselinkSite.getId().intValue();
	}

}
