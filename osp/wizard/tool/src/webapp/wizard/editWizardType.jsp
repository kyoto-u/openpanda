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
<h:form styleClass="portletBody">
   <sakai:view_title value="#{msgs.edit_wizard}" rendered='#{!wizard.current.newWizard}'/>
   <sakai:view_title value="#{msgs.add_wizard}"  rendered='#{wizard.current.newWizard}'/>
    
   <sakai:instruction_message value="#{msgs.wizard_type_instructions}" />
   <sakai:messages rendered="#{!empty facesContext.maximumSeverity}"/>
      <h:panelGrid columns="1" styleClass="jsfFormTable">
<%--      <h:outputLabel for="type" id="typeLabel" value="#{msgs.wizard_type}" /> --%>
      <h:panelGroup>
         <h:selectOneRadio layout="pageDirection" id="type" value="#{wizard.current.base.type}" disabled="#{wizard.current.base.published}">
            <f:selectItems value="#{wizard.wizardTypes}"/>
         </h:selectOneRadio>
      </h:panelGroup>
   </h:panelGrid>
   
   <sakai:button_bar>
	   <sakai:button_bar_item id="submitNext" value="#{msgs.save_continue_wizard}"
	      action="#{wizard.processActionNewSteps}" accesskey="s" styleClass="active" />
	   <sakai:button_bar_item id="cancel" value="#{msgs.cancel_wizard}" action="#{wizard.processActionCancel}"
	      immediate="true" accesskey="x" />
   </sakai:button_bar>

</h:form>
</sakai:view>

</f:view>
