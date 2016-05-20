/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sakaiproject.alert.tool.ui.bean;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.sakaiproject.alert.logic.ProjectLogic;
import org.sakaiproject.alert.logic.SakaiProxy;
import org.sakaiproject.alert.logic.SakaiRole;

/**
 *
 * @author kajita
 */
// @ManagedBean
// @SessionScoped
@NoArgsConstructor
@AllArgsConstructor
public class PermissionBean implements Serializable {

    private static final Logger LOG = Logger.getLogger(PermissionBean.class.getName());

    // Navigation Menu
    private boolean updateEmergencyInfo = false;
    private boolean settings = false;

    private SakaiProxy sakaiProxy;

    private ProjectLogic projectLogic;

    @PostConstruct
    private void init() {

        // String role = projectLogic.getSakaiProxy().getCurrentUserRole();
        String role = null;

        if (SakaiRole.ADMIN_ROLE.equals(role)) {
            updateEmergencyInfo = true;
            settings = true;
        } else if (org.sakaiproject.alert.logic.SakaiRole.INSTRUCTOR_ROLE.equals(role)) {
            updateEmergencyInfo = true;
            settings = true;
        }
    }

    public ProjectLogic getProjectLogic() {
        return projectLogic;
    }

    public void setProjectLogic(ProjectLogic projectLogic) {
        this.projectLogic = projectLogic;
    }

    public boolean isSettings() {
        return settings;
    }

    public void setSettings(boolean settings) {
        this.settings = settings;
    }

    public SakaiProxy getSakaiProxy() {
        return sakaiProxy;
    }

    public void setSakaiProxy(SakaiProxy sakaiProxy) {
        this.sakaiProxy = sakaiProxy;
    }

    public boolean isUpdateEmergencyInfo() {
        return updateEmergencyInfo;
    }

    public void setUpdateEmergencyInfo(boolean updateEmergencyInfo) {
        this.updateEmergencyInfo = updateEmergencyInfo;
    }
}
