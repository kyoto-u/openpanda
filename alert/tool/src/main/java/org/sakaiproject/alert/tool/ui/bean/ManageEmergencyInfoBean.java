package org.sakaiproject.alert.tool.ui.bean;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.sakaiproject.alert.logic.EmergencyInfoManager;
import org.sakaiproject.alert.logic.ProjectLogic;
import org.sakaiproject.alert.logic.SakaiProxy;
import org.sakaiproject.alert.model.EmergencyInfo;

import javax.annotation.PostConstruct;

/**
 * Created by kajita on 5/12/16.
 */
@NoArgsConstructor
@AllArgsConstructor
public class ManageEmergencyInfoBean {

    private String userId;
    private EmergencyInfo emergencyInfo = new EmergencyInfo();
    private EmergencyInfo orig;
    private boolean edited;
    private boolean myWorkspace;

    private SakaiProxy sakaiProxy;
    private ProjectLogic projectLogic;
    private EmergencyInfoManager emergencyInfoManager;

    @PostConstruct
    private void init() {

        userId = sakaiProxy.getCurrentUserId();
        edited = false;
        myWorkspace = sakaiProxy.isMyWorkspace();
        orig = emergencyInfoManager.getEmergencyInfoByUserId(userId);
        emergencyInfo.setTouchedDate(orig.getTouchedDate());
        emergencyInfo.setInjuryStatus(orig.getInjuryStatus());
        emergencyInfo.setStatus(orig.getStatus());
        emergencyInfo.setEmailAddress(orig.getEmailAddress());
        emergencyInfo.setTelephoneNumber(orig.getTelephoneNumber());
        emergencyInfo.setRemark(orig.getRemark());

    }

    public void revise()  {
        edited = true;
    }

    public void save()  {
        orig.setTouchedDate(emergencyInfo.getTouchedDate());
        orig.setInjuryStatus(emergencyInfo.getInjuryStatus());
        orig.setStatus(emergencyInfo.getStatus());
        orig.setEmailAddress(emergencyInfo.getEmailAddress());
        orig.setTelephoneNumber(emergencyInfo.getTelephoneNumber());
        orig.setRemark(emergencyInfo.getRemark());
        emergencyInfoManager.saveEmergencyInfo(orig);
        edited = false;
    }

    public void cancel()  {
        edited = false;
        emergencyInfo.setTouchedDate(orig.getTouchedDate());
        emergencyInfo.setInjuryStatus(orig.getInjuryStatus());
        emergencyInfo.setStatus(orig.getStatus());
        emergencyInfo.setEmailAddress(orig.getEmailAddress());
        emergencyInfo.setTelephoneNumber(orig.getTelephoneNumber());
        emergencyInfo.setRemark(orig.getRemark());
    }


    public EmergencyInfo getEmergencyInfo() {
        return emergencyInfo;
    }

    public void setEmergencyInfo(EmergencyInfo emergencyInfo) {
        this.emergencyInfo = emergencyInfo;
    }

    public SakaiProxy getSakaiProxy() {
        return sakaiProxy;
    }

    public void setSakaiProxy(SakaiProxy sakaiProxy) {
        this.sakaiProxy = sakaiProxy;
    }

    public ProjectLogic getProjectLogic() {
        return projectLogic;
    }

    public void setProjectLogic(ProjectLogic projectLogic) {
        this.projectLogic = projectLogic;
    }

    public EmergencyInfoManager getEmergencyInfoManager() {
        return emergencyInfoManager;
    }

    public void setEmergencyInfoManager(EmergencyInfoManager emergencyInfoManager) {
        this.emergencyInfoManager = emergencyInfoManager;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public boolean isMyWorkspace() {
        return myWorkspace;
    }

    public void setMyWorkspace(boolean myWorkspace) {
        this.myWorkspace = myWorkspace;
    }
}
