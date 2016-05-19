package org.sakaiproject.alert.tool.ui.bean;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.sakaiproject.message.api.Message;
import org.sakaiproject.alert.logic.ProjectLogic;
import org.sakaiproject.alert.logic.SakaiProxy;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.ToolManager;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kajita on 5/5/16.
 */
// @ManagedBean
// @SessionScoped
@NoArgsConstructor
@AllArgsConstructor
public class AlertListBean {

    private List<Message> notifications = new ArrayList<Message>();
    //private List<Notification> notifications = new ArrayList<Notification>();
    private String siteId;
    private String channelId;

    private SakaiProxy sakaiProxy;

    private ProjectLogic projectLogic;

    private ToolManager toolManager;

    @PostConstruct
    private void init() {

        Placement placement = toolManager.getCurrentPlacement();
        siteId = placement.getContext();
        siteId = sakaiProxy.getCurrentSiteId();

        channelId = "/announcement/channel/" + siteId + "/" + SiteService.MAIN_CONTAINER;

        notifications = sakaiProxy.getMessages(channelId);
        // notifications.add(new Notification("テストのテスト"));

    }

    public List<Message> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Message> notifications) {
        this.notifications = notifications;
    }

    public ProjectLogic getProjectLogic() {
        return projectLogic;
    }

    public void setProjectLogic(ProjectLogic projectLogic) {
        this.projectLogic = projectLogic;
    }

    public SakaiProxy getSakaiProxy() {
        return sakaiProxy;
    }

    public void setSakaiProxy(SakaiProxy sakaiProxy) {
        this.sakaiProxy = sakaiProxy;
    }

    public ToolManager getToolManager() {
        return toolManager;
    }

    public void setToolManager(ToolManager toolManager) {
        this.toolManager = toolManager;
    }
}


