package courselink.kyoto_u.ac.jp.model;

import java.util.Date;
import java.util.Set;

import courselink.kyoto_u.ac.jp.util.DateFormatUtil;

public class CourselinkSite implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

	private Long id;
	private String siteId;	// Sakai siteId
	private String createUserId;
	private Date createDate;
	private Set courselinkRequests;
	/**
	 * Default constructor
	 */
	public CourselinkSite() {
	}

	public CourselinkSite(String siteId){
		this.siteId = siteId;
	}

	/**
	 * Getters and Setters
	 */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public String getCreateDateString(){
		return DateFormatUtil.getDateString(createDate);
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Set getCourselinkRequests() {
		return courselinkRequests;
	}

	public void setCourselinkRequests(Set courselinkRequests) {
		this.courselinkRequests = courselinkRequests;
	}

	public int getRequestNum(){
		if(this.courselinkRequests == null){
			return 0;
		}
		return this.courselinkRequests.size();
	}

}
