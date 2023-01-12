package org.sakaiproject.tool.assessment.services.caliper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.imsglobal.caliper.CaliperSendable;
import org.imsglobal.caliper.Envelope;
import org.imsglobal.caliper.Sensor;
import org.imsglobal.caliper.actions.Action;
import org.imsglobal.caliper.clients.HttpClient;
import org.imsglobal.caliper.clients.HttpClientOptions;
import org.imsglobal.caliper.config.Config;
import org.imsglobal.caliper.context.JsonldStringContext;
import org.imsglobal.caliper.entities.agent.Person;
import org.imsglobal.caliper.entities.outcome.Score;
import org.imsglobal.caliper.entities.resource.Attempt;
import org.imsglobal.caliper.events.GradeEvent;
import org.joda.time.DateTime;
//import org.springframework.format.annotation.DateTimeFormat;
import org.joda.time.format.DateTimeFormat;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingData;
import org.sakaiproject.tool.assessment.data.ifc.assessment.PublishedAssessmentIfc;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.sakaiproject.tool.assessment.services.GradingService;

/**
 * Servlet implementation class CaliperAssessmentServlet
 */
public class CaliperGradeSession {

  private Log log = LogFactory.getLog(GradingService.class);

  private static final long serialVersionUID = 1L;
  static String currentSiteId = null;
  static String currentSiteName = null;
  static String currentUserId = null;
  static String displayName = null;
  static String serverUrl = null;
  static Long publishedAssessmentId = null;

  /**
   * Default constructor.
   */
  public CaliperGradeSession(String siteId,AssessmentGradingData data,String siteName,String userEid) {
    currentSiteId = siteId;
    currentSiteName = siteName;
    currentUserId = userEid;
    serverUrl = ServerConfigurationService.getString("serverUrl") + "/";
    displayName = AgentFacade.getDisplayNameByAgentId(data.getAgentId());
    publishedAssessmentId = data.getPublishedAssessmentId();
  }

  public void generate(String host, String apikey, AssessmentGradingData data,PublishedAssessmentIfc pub) throws Exception {

        Sensor sensor = Sensor.create(ServerConfigurationService.getString("samigo.caliper.sensorId","mySensorId"));

    HttpClientOptions opts = HttpClientOptions.builder()
      .apiKey(apikey)
      .host(host)
      .build();

    HttpClient client = HttpClient.create(sensor.getId(),opts);

    sensor.registerClient(client);

    GradeEvent gradeEvent = buildGradeEvent(data, pub);
    List<CaliperSendable> eventData = new ArrayList<CaliperSendable>();
    eventData.add(gradeEvent);
    Envelope envelope = sensor.create(client.getId(), DateTime.now(), Config.DATA_VERSION, eventData);
    sensor.send(client,envelope);
    StringBuffer output = new StringBuffer()
    .append("Generated gradeEvent \n")
    .append("action : " + gradeEvent.getAction() + "\n")
    .append("eventTime : " + (DateTime)gradeEvent.getEventTime() + "\n")
    .append("FINIS\n\n");
    log.info(output);
  }

  /**
   * GradeEvent builder
   * @return GradeEvent
   */
  private GradeEvent buildGradeEvent(AssessmentGradingData data, PublishedAssessmentIfc pub){
    return GradeEvent.builder()
            .context(JsonldStringContext.getDefault())
            .id("urn:id:" + UUID.randomUUID().toString())
            .actor(buildPerson())
            .action(Action.GRADED)
            .object(buildAttempt())
            .eventTime(DateTime.now())
            .generated(buildScore(data,pub))
            .build();
  }

  /**
   * Person builder
   * @return Person
   */
  private static Person buildPerson() {
    return Person.builder()
            .id(serverUrl + "user/" + currentUserId)
            .dateCreated(DateTime.now())
            .dateModified(null)
            .name(displayName)
            .build();
  }

  /**
   * Attempt builder
   * @Attempt
   */
  private static Attempt buildAttempt() {
    return Attempt.builder()
            .id(serverUrl + currentSiteId + "/assessment/" + publishedAssessmentId)
            .dateCreated(DateTime.now())
            .build();
  }

  /**
   * Score builder
   * @Attempt
   */
  private Score buildScore(AssessmentGradingData data,PublishedAssessmentIfc pub) {
    return Score.builder()
            .id(serverUrl + currentSiteId + "/assessment/" + publishedAssessmentId)
            .dateCreated(getDateTime(data.getAttemptDate()))
            .comment(data.getComments())
            .maxScore(data.getFinalScore())
            .name(pub.getTitle())
            .build();
  }

  /**
   * Conversion Date to Date Time
   * @return date time
   */
  private static DateTime getDateTime(Date date) {
      SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
      return DateTime.parse(f.format(date) +"000000",DateTimeFormat.forPattern("yyyyMMddHHmmss"));
      //return DateTimeFormat.forPattern("yyyyMMddHHmmss").parseDateTime(f.format(date));
  }

}