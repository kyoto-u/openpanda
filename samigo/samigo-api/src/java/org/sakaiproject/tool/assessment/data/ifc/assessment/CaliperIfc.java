package org.sakaiproject.tool.assessment.data.ifc.assessment;


public interface CaliperIfc
    extends java.io.Serializable
{
  public static final String  DEFAULT_ENDPOINT = "end_point";
  public static final String  DEFAULT_APIKEY = "api_key";
  public static final double  DEFAULT_THRESHOLD = 0;
  public static final String  DEFAULT_MAIL = "mail_address";
  public static final boolean DEFAULT_RETRY = false;

  Long getId();

  void setId(Long id);

  void setAssessmentBase(AssessmentBaseIfc assessmentBase);

  AssessmentBaseIfc getAssessmentBase();

  boolean getSend();

  void setSend(boolean send);

  String getEndPoint();

  void setEndPoint(String endPoint);

  String getApiKey();

  void setApiKey(String apiKey);

  Double getThreshold();

  void setThreshold(Double threshold);

  String getMail();

  void setMail(String mail);

  boolean getRetry();

  void setRetry(boolean retry);

}