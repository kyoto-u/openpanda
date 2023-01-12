package org.sakaiproject.tool.assessment.services.caliper;

import java.util.ArrayList;
import java.util.Date;

import javax.mail.MessagingException;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.email.api.AddressValidationException;
import org.sakaiproject.email.api.ContentType;
import org.sakaiproject.email.api.EmailAddress;
import org.sakaiproject.email.api.EmailAddress.RecipientType;
import org.sakaiproject.email.api.EmailMessage;
import org.sakaiproject.email.api.EmailService;
import org.sakaiproject.email.api.NoRecipientsException;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.UsageSession;
import org.sakaiproject.event.api.UsageSessionService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.assessment.data.dao.assessment.Caliper;
import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingData;
import org.sakaiproject.tool.assessment.data.ifc.assessment.CaliperIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.PublishedAssessmentIfc;
import org.sakaiproject.tool.assessment.facade.PublishedAssessmentFacade;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetryCaliperSendJob implements Job {
    private AuthzGroupService authzGroupService = ComponentManager.get(AuthzGroupService.class);
    private EventTrackingService eventTrackingService = ComponentManager.get(EventTrackingService.class);
    private SessionManager sessionManager = ComponentManager.get(SessionManager.class);
    private UsageSessionService usageSessionService = ComponentManager.get(UsageSessionService.class);
    private UserDirectoryService userDirectoryService = ComponentManager.get(UserDirectoryService.class);
    @Setter private SiteService siteService = ComponentManager.get(SiteService.class);
    public void init() {
        log.debug("RetryCaliperSendJob init()  ");
    }
    public void destroy() {
        log.debug("RetryCaliperSendJob destroy()");
    }
    public RetryCaliperSendJob() {
        super();
    }
    /*
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext jobInfo) throws JobExecutionException {
        loginToSakai("admin");
        String jobName = jobInfo.getJobDetail().getKey().getName();
        String triggerName = jobInfo.getTrigger().getKey().getName();
         Date requestedFire = jobInfo.getScheduledFireTime();
        Date actualfire = jobInfo.getFireTime();
        StringBuffer whoAmI = new StringBuffer("RetryCaliperSendJob $");
        whoAmI.append(" Job: ");
        whoAmI.append(jobName);
        whoAmI.append(" Trigger: ");
        whoAmI.append(triggerName);
        if (requestedFire != null) {
            whoAmI.append(" Fire scheduled: ");
            whoAmI.append(requestedFire.toString());
        }
        if (actualfire != null) {
            whoAmI.append(" Fire actual: ");
            whoAmI.append(actualfire.toString());
        }
        log.info("Start Job: " + whoAmI.toString());
        JobDataMap jobDataMap = jobInfo.getJobDetail().getJobDataMap();
        String currentSiteId = jobDataMap.getString("currentSiteId");
        AssessmentGradingData data=new AssessmentGradingData();
        data.setAgentId( jobDataMap.getString("agentId"));
        data.setPublishedAssessmentId(new Long(jobDataMap.getString("publishedAssessmentId")));
        data.setAttemptDate(new Date(new Long(jobDataMap.getString("attemptDate"))));
        data.setComments(jobDataMap.getString("comments"));
        data.setFinalScore(new Double(jobDataMap.getString("finalScore")));
        PublishedAssessmentIfc pub =new PublishedAssessmentFacade();
        pub.setTitle( jobDataMap.getString("title"));
        CaliperIfc caliper = new Caliper();
        caliper.setEndPoint( jobDataMap.getString("endPoint"));
        caliper.setApiKey( jobDataMap.getString("apiKey"));
        caliper.setMail( jobDataMap.getString("mail"));
        int retryCount = 1;
        try{
            retryCount = new Integer(ServerConfigurationService.getString("caliper.send.retry.count")).intValue();
        }catch(Exception e){}
        boolean success = true;
        String siteName = null;
        try {
            siteName = siteService.getSite(currentSiteId).getTitle();
        } catch (IdUnusedException e) {}
        String userEid = null;
        try {
            userEid = userDirectoryService.getUserEid(data.getAgentId());
        } catch (UserNotDefinedException e) {}
        CaliperGradeSession caliperSession = new CaliperGradeSession(currentSiteId,data,siteName,userEid);
        StringBuffer error = new StringBuffer();
        int count = 1;
        while (retryCount > 0){
          if(count > retryCount){
              break;
          }
          try {
              caliperSession.generate(caliper.getEndPoint(),caliper.getApiKey(),data,pub);
              retryCount=0;
              success = true;
          }catch(Exception e) {
              error.append("Retry count " + count + ":" + e.getMessage() + "\n ");
              success = false;
          }
          count++;
        }
        if(!success){
            sendEmail(caliper,caliperSession,pub,error);
        }
        logoutFromSakai();
    }

    private void sendEmail(CaliperIfc caliper,CaliperGradeSession caliperSession ,PublishedAssessmentIfc pub, StringBuffer error){
            StringBuffer subject = new StringBuffer();
            StringBuffer text = new StringBuffer();
            subject.append("【IMS Caliper Event Retry result】Failed");
            text.append("[Site Name]\n "+ CaliperGradeSession.currentSiteName +"\n");
            text.append("[Eid]\n "+ CaliperGradeSession.currentUserId +"\n");
            text.append("[Assessment Title]\n "+ pub.getTitle()+"\n");
            text.append("[Error Detail]\n "+ error.toString());
            String systemName  = ServerConfigurationService.getString("caliper.send.retry.mail.systemName","retryCaliperSend");
            String systemEmail = ServerConfigurationService.getString("caliper.send.retry.mail.systemEmail","noreply@localhost");
            try {
                if (caliper.getMail() == null || caliper.getMail().isEmpty()){
                  log.info("No To Address");
                  return;
                }
                ArrayList<EmailAddress> tos = new ArrayList<EmailAddress>();
                tos.add(new EmailAddress(caliper.getMail(), ""));
                EmailMessage msg = new EmailMessage();
                msg.setFrom(new EmailAddress(systemEmail, systemName));
                msg.setSubject(subject.toString());
                msg.setContentType(ContentType.TEXT_PLAIN);
                msg.setBody(text.toString());
                // add all recipients to the to field
                msg.addRecipients(RecipientType.TO, tos);
                msg.addHeader("Content-Transfer-Encoding", "quoted-printable");
                try{
                    EmailService emailService = (EmailService) ComponentManager.get(EmailService.class);
                    emailService.send(msg,true);
                    return;
                }catch (AddressValidationException e){
                    log.info(e.getMessage());
                    return;
                }catch (NoRecipientsException e){
                    log.info(e.getMessage());
                    return;
                } catch (MessagingException e) {
                    log.info(e.getMessage());
                    return;
                }
            } catch (Exception e) {
                log.info(e.getMessage());
            }
    }

    /**
     * @param whoAs - who to log in as
     */
    protected void loginToSakai(String whoAs) {
        String serverName = ServerConfigurationService.getServerName();
        log.debug("RetryCaliperSendJob Logging into Sakai on {} as {}", serverName, whoAs);
        UsageSession session = usageSessionService.startSession(whoAs, serverName, "RetryCaliperSendJob");
        if (session == null) {
            eventTrackingService.post(eventTrackingService.newEvent("sam.auto-submit.job.error", whoAs + " unable to log into " + serverName, true));
            return;
        }
        Session sakaiSession = sessionManager.getCurrentSession();
        sakaiSession.setUserId(whoAs);
        sakaiSession.setUserEid(whoAs);
        // update the user's externally provided realm definitions
        authzGroupService.refreshUser(whoAs);
        // post the login events
        eventTrackingService.post(eventTrackingService.newEvent(UsageSessionService.EVENT_LOGIN, whoAs + " running " + serverName, true));
    }

    protected void logoutFromSakai() {
        String serverName = ServerConfigurationService.getServerName();
        log.debug(" RetryCaliperSendJob Logging out of Sakai on " + serverName);
        eventTrackingService.post(eventTrackingService.newEvent(UsageSessionService.EVENT_LOGOUT, null, true));
        usageSessionService.logout(); // safe to logout? what if other jobs are running?
    }
}