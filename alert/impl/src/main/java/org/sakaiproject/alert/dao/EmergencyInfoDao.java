package org.sakaiproject.alert.dao;

import org.sakaiproject.genericdao.api.GeneralGenericDao;
import org.sakaiproject.alert.model.EmergencyInfo;

import java.util.Date;
import java.util.List;

/**
 * Created by kajita on 5/10/16.
 */
public interface EmergencyInfoDao extends GeneralGenericDao {

    public EmergencyInfo getEmergencyInfoByUserId(String userId);
    public List<EmergencyInfo> getAllEmergencyInfoByUserId(String userId);
    public EmergencyInfo getEmergencyInfoByUserIdSince(String userId, Date sinceDate);
    public List<EmergencyInfo> getAllEmergencyInfoByUserIdSince(String userId, Date sinceDate);

}
