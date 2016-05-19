package org.sakaiproject.alert.logic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.alert.dao.EmergencyInfoDao;
import org.sakaiproject.alert.model.EmergencyInfo;

import java.util.Date;
import java.util.List;

/**
 * Created by kajita on 5/10/16.
 */
public class EmergencyInfoManagerImpl implements EmergencyInfoManager {

    private static Log log = LogFactory.getLog(EmergencyInfoManagerImpl.class);

    private EmergencyInfoDao dao;

    public EmergencyInfo getEmergencyInfoByUserId(String userId)  {
       return dao.getEmergencyInfoByUserId(userId);
    }

    public List<EmergencyInfo> getAllEmergencyInfoByUserId(String userId)  {
        return dao.getAllEmergencyInfoByUserId(userId);
    }

    public EmergencyInfo getEmergencyInfoByUserIdSince(String userId, Date sinceDate)  {
        return dao.getEmergencyInfoByUserIdSince(userId, sinceDate);
    }

    public List<EmergencyInfo> getAllEmergencyInfoByUserIdSince(String userId, Date sinceDate)  {
        return dao.getAllEmergencyInfoByUserIdSince(userId, sinceDate);
    }

    public boolean saveEmergencyInfo(EmergencyInfo emergencyInfo)  {
        dao.create(emergencyInfo);
        log.debug(" EmergencyInfo for " + emergencyInfo.getUserId() + " successfully saved");
        return true;
    }

    public EmergencyInfo getEmergencyInfo(String userId)  {

        return dao.getEmergencyInfoByUserId(userId);

    }

    public EmergencyInfoDao getDao() {
        return dao;
    }

    public void setDao(EmergencyInfoDao dao) {
        this.dao = dao;
    }
}
