<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://www.theospi.org/jsf/osp" prefix="ospx" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>

<%
      response.setContentType("text/html; charset=UTF-8");
      response.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
      response.addDateHeader("Last-Modified", System.currentTimeMillis());
      response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
      response.addHeader("Pragma", "no-cache");
%>

<f:view>
<sakai:view>

   <%@ include file="steps.jspf"%>
<h:form>
  <sakai:view_title value="#{msgs.edit_wizard}" rendered='#{!wizard.current.newWizard}'/>
   <sakai:view_title value="#{msgs.add_wizard}"  rendered='#{wizard.current.newWizard}'/>
   <sakai:instruction_message value="#{msgs.wizard_instruction_message}" />
<%--   <sakai:instruction_message value=" Last saved: " />
   <sakai:instruction_message value="#{wizard.lastSavedId}" /> --%>
   <sakai:messages rendered="#{!empty facesContext.maximumSeverity}"/>
   
   <%@ include file="wizardPropertiesFrame.jspf"%>
   <h:commandLink action="#{wizard.processActionAudienceHelper}">
      <h:outputText value="#{msgs.select_reviewers}"/>
   </h:commandLink>
   
   <h:selectBooleanCheckbox id="asTool" value="#{wizard.current.exposeAsTool}" />
   <h:outputLabel value="#{msgs.expose_as_tool}" for="asTool" />

   <%@ include file="builderButtons.jspf"%>
   
</h:form>
</sakai:view>

</f:view>
