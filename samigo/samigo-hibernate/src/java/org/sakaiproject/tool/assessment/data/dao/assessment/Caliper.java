package org.sakaiproject.tool.assessment.data.dao.assessment;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentBaseIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.CaliperIfc;
public class Caliper
    implements java.io.Serializable,CaliperIfc
{
        private static final long serialVersionUID = 2592581779541143409L;
  private Long id;
  private AssessmentBaseIfc assessmentBase;
  private boolean send;
  private String endPoint;
  private String apiKey;
  private Double threshold;
  private String mail;
  private boolean retry;

  public Caliper()
  {
  }

  public Caliper(boolean send, String endPoint, String apiKey, Double threshold, String mail,boolean retry)
  {
    this.send = send;
    this.endPoint = endPoint;
    this.apiKey = apiKey;
    this.threshold = threshold;
    this.mail =  mail;
    this.retry = retry;
  }

  public Object clone() throws CloneNotSupportedException{
    Object cloned = new Caliper(this.send, this.endPoint, this.apiKey,this.threshold,this.mail,this.retry);
    return cloned;
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public void setAssessmentBase(AssessmentBaseIfc assessmentBase)
  {
    this.assessmentBase = assessmentBase;
  }

  public AssessmentBaseIfc getAssessmentBase()
  {
    if (assessmentBase.getIsTemplate().equals(Boolean.TRUE))
      return (AssessmentTemplateData)assessmentBase;
    else
      return (AssessmentData)assessmentBase;
  }

  public boolean getSend() {
    return send;
  }

  public void setSend(boolean send) {
    this.send = send;
  }

  public String getEndPoint() {
    return endPoint;
  }

  public void setEndPoint(String endPoint) {
    this.endPoint = endPoint;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public Double getThreshold() {
    return threshold;
  }

  public void setThreshold(Double threshold) {
    this.threshold = threshold;
  }

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public boolean getRetry() {
    return retry;
  }

  public void setRetry(boolean retry) {
    this.retry = retry;
  }
}