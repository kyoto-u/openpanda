package courselink.kyoto_u.ac.jp.model;

import java.util.Date;

import courselink.kyoto_u.ac.jp.util.DateFormatUtil;

public class CourselinkRequest implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

	private Long id;
	private CourselinkSite courselinkSite;
	private String ownerId; // Sakai userId
	private Date insertDate;
	private int status;
	private String memo;

	/**
	 * Default constructor
	 */
	public CourselinkRequest() {
	}

	/**
	 * Minimum constructor
	 */
	public CourselinkRequest(CourselinkSite courselinkSite, String ownerId) {
		this.courselinkSite = courselinkSite;
		this.ownerId = ownerId;
		this.insertDate = new Date();
	}

	/**
	 * Full constructor
	 */
	public CourselinkRequest(CourselinkSite courselinkSite,
			String ownerId, Date insertDate) {
		this.courselinkSite = courselinkSite;
		this.ownerId = ownerId;
		this.insertDate = insertDate;
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

	public CourselinkSite getCourselinkSite() {
		return courselinkSite;
	}

	public void setCourselinkSite(CourselinkSite courselinkSite) {
		this.courselinkSite = courselinkSite;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public Date getInsertDate() {
		return insertDate;
	}

	public String getInsertDateString() {
		return DateFormatUtil.getDateString(insertDate);
	}

	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}


}
