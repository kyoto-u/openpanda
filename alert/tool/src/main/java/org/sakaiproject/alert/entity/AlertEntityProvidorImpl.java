package org.sakaiproject.alert.entity;

import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.entitybroker.entityprovider.EntityProvider;
import org.sakaiproject.entitybroker.entityprovider.annotations.EntityCustomAction;
import org.sakaiproject.entitybroker.entityprovider.capabilities.ActionsExecutable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.AutoRegisterEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Describeable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Outputable;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.util.AbstractEntityProvider;
import org.sakaiproject.alert.logic.EmergencyInfoManager;
import org.sakaiproject.alert.model.EmergencyInfo;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.time.api.TimeService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.util.ResourceLoader;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by kajita on 5/2/16.
 */
public class AlertEntityProvidorImpl extends AbstractEntityProvider implements EntityProvider, AutoRegisterEntityProvider, ActionsExecutable, Outputable, Describeable {

    public final static String ENTITY_PREFIX = "alert";

    private static final Log log = LogFactory.getLog(AlertEntityProvidorImpl.class);

    private static ResourceLoader rb = new ResourceLoader("alert");

    @Setter
    private EmergencyInfoManager emergencyInfoManager;

    @Setter
    private EntityManager entityManager;

    @Setter
    private SecurityService securityService;

    @Setter
    private SessionManager sessionManager;

    @Setter
    private SiteService siteService;

    /*
    @Setter
    private AnnouncementService announcementService;
    */

    @Setter
    private UserDirectoryService userDirectoryService;

    @Setter
    private TimeService timeService;

    @Setter
    private ToolManager toolManager;


    @Override
    public String getEntityPrefix() {
        return ENTITY_PREFIX;
    }

    @Override
    public String[] getHandledOutputFormats() {
        return new String[]{Formats.TXT, Formats.JSON};
    }

    @EntityCustomAction(action = "touch", viewKey = EntityView.VIEW_SHOW)
    public String handleTouch(EntityReference ref) {

        User currentUser = userDirectoryService.getCurrentUser();
        User anon = userDirectoryService.getAnonymousUser();

        if (anon.equals(currentUser)) {
            throw new SecurityException("You must be logged in to use this service");
        }

        String userId = currentUser.getId();
        Date now = new Date();

        EmergencyInfo emergencyInfo = new EmergencyInfo(userId, now);

        emergencyInfoManager.saveEmergencyInfo(emergencyInfo);

        return "success";

    }

    @EntityCustomAction(action = "report", viewKey = EntityView.VIEW_NEW)
    public String handleReport(EntityView view, Map<String, Object> params)  {

        User currentUser = userDirectoryService.getCurrentUser();
        User anon = userDirectoryService.getAnonymousUser();

        if (anon.equals(currentUser)) {
            throw new SecurityException("You must be logged in to use this service");
        }

        String userId = currentUser.getId();
        Date now = new Date();

        EmergencyInfo emergencyInfo = new EmergencyInfo(userId, now);

        emergencyInfo.setInjuryStatus((String) params.get("injuryStatus"));
        emergencyInfo.setStatus((String) params.get("status"));
        emergencyInfo.setEmailAddress((String) params.get("emailAddress"));
        emergencyInfo.setTelephoneNumber((String) params.get("telephoneNumber"));
        emergencyInfo.setRemark((String) params.get("remark"));

        emergencyInfoManager.saveEmergencyInfo(emergencyInfo);

        return "success";

    }

    @EntityCustomAction(action = "confirm", viewKey = EntityView.VIEW_LIST)
    public List<EmergencyInfo> getAllEmergencyInfoByUserId(EntityView view) {

        User currentUser = userDirectoryService.getCurrentUser();
        User anon = userDirectoryService.getAnonymousUser();

        if (anon.equals(currentUser)) {
            throw new SecurityException("You must be logged in to use this service");
        }

        return emergencyInfoManager.getAllEmergencyInfoByUserId(currentUser.getId());
    }
    /*
    private List<?> getNotificationedAnnouncements(String siteId, Map<String, Object> params, boolean onlyPublic)  {

        String currentUserId = sessionManager.getCurrentSessionUserId();

        if (log.isDebugEnabled())  {
            log.debug("currentUserId: " + currentUserId);
        }
    }
*/
}
