package org.sakaiproject.alert.model;

import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by kajita on 5/10/16.
 */
@NoArgsConstructor
public class EmergencyInfo {

    private long id;
    private String userId;
    private Date touchedDate;
    private String injuryStatus;
    private String status;
    private String emailAddress;
    private String telephoneNumber;
    private String remark;
    private String ipAddress;
    private String userAgent;
    private String delegatedUserId;

    public EmergencyInfo(String userId, Date touchedDate) {
        this.userId = userId;
        this.touchedDate = touchedDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInjuryStatus() {
        return injuryStatus;
    }

    public void setInjuryStatus(String injuryStatus) {
        this.injuryStatus = injuryStatus;
    }

    public Date getTouchedDate() {
        return touchedDate;
    }

    public void setTouchedDate(Date touchedDate) {
        this.touchedDate = touchedDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDelegatedUserId() {
        return delegatedUserId;
    }

    public void setDelegatedUserId(String delegatedUserId) {
        this.delegatedUserId = delegatedUserId;
    }
}
