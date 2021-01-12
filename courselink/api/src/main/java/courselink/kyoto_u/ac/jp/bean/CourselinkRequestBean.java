package courselink.kyoto_u.ac.jp.bean;


import lombok.Getter;
import lombok.Setter;
import courselink.kyoto_u.ac.jp.logic.SakaiProxy;
import courselink.kyoto_u.ac.jp.model.CourselinkRequest;

public class CourselinkRequestBean {

	@Setter
	@Getter
	private CourselinkRequest courselinkRequest;

	@Setter
	@Getter
    private CourselinkSiteBean courselinkSiteBean = null;

	@Setter
	@Getter
	private SakaiProxy sakaiProxy;

	/**
	 * constructor
	 * @param courselinkRequest
	 */
	public CourselinkRequestBean(CourselinkRequest courselinkRequest, CourselinkSiteBean courselinkSiteBean, SakaiProxy sakaiProxy){
		this.courselinkRequest = courselinkRequest;
		this.courselinkSiteBean = courselinkSiteBean;
		this.sakaiProxy = sakaiProxy;
	}

	public String getSiteId(){
		try{
			return courselinkSiteBean.getSiteId();
		}catch(Exception e){}
		return "";
	}

	public String getInsertDate(){
		return courselinkRequest.getInsertDateString();
	}

	public String getSiteTitle(){
		try{
			return courselinkSiteBean.getTitle();
		}catch(Exception e){}
		return "";
	}

	public boolean isExistSite(){
		try{
			return courselinkSiteBean.isExistSite();
		}catch(Exception e){}
		return false;
	}

	public String getInsertUserName(){
		String displayName = "";
		try{
			String userId = courselinkRequest.getOwnerId();
			displayName = sakaiProxy.getUserDisplayName(userId);
		}catch (Exception e){
		}
		return displayName;
	}

	public String getInsertUserId(){
		String userEid = "";
		try{
			String userId = courselinkRequest.getOwnerId();
			userEid = sakaiProxy.getUserEid(userId);
		}catch (Exception e){}
		return userEid;
	}

	public String getMemo(){
		return courselinkRequest.getMemo();
	}

	public CourselinkSiteBean getCourselinkSiteBean() {
		return courselinkSiteBean;
	}
}
