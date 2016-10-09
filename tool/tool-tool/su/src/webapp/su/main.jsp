<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>

<%
	response.setContentType("text/html; charset=UTF-8");
	response.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
	response.addDateHeader("Last-Modified", System.currentTimeMillis());
	response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
	response.addHeader("Pragma", "no-cache");
%>

<f:view>
<sakai:view_container title="#{msgs.title}">

	<f:subview id="allowed" rendered="#{SuTool.allowed}">
		<h:message for="allowed" errorClass="alertMessage" infoClass="instruction" />
	</f:subview>

	<h:form id="su">
	  <sakai:instruction_message value="#{msgs.instructions}" />
	  <sakai:instruction_message value="#{msgs.instructions_da}" rendered="#{SuTool.delegatedAccessUser && !SuTool.superUser}"/>

		<p><h:message for="su" errorClass="alertMessage" infoClass="instruction" showSummary="true" showDetail="false"/></p>


        <h:outputText rendered="#{SuTool.userinfo == null}" escape="false" value="<div style=\"display:none\">" />
            <h3><h:outputText value="#{msgs.userinfoheader}"/></h3>
            <table class="itemSummary" summary="<h:outputText value="#{msgs.userinfoheader}"/>">
                <tr><th scope="row"><h:outputText value="#{msgs.name}" /></th><td><h:outputText value="#{SuTool.userinfo.displayName}" /></td></tr>
                <c:if test="#{SuTool.allowKyotoUserPropertiesExtention}">
				<tr><th scope="row"><h:outputText value="#{msgs.displayName}" /></th><td><h:outputText value="#{SuTool.displayName}" /></td></tr>
				<tr><th scope="row"><h:outputText value="#{msgs.displayName_en}" /></th><td><h:outputText value="#{SuTool.displayNameEn}" /></td></tr>
				<tr><th scope="row"><h:outputText value="#{msgs.displayName_ja_phonetic}" /></th><td><h:outputText value="#{SuTool.displayNameJaPhonetic}" /></td></tr>
				<tr><th scope="row"><h:outputText value="#{msgs.employeeNumber}" /></th><td><h:outputText value="#{SuTool.employeeNumber}" /></td></tr>
				<tr><th scope="row"><h:outputText value="#{msgs.title}" /></th><td><h:outputText value="#{SuTool.title}" /></td></tr>
				<tr><th scope="row"><h:outputText value="#{msgs.title1}" /></th><td><h:outputText value="#{SuTool.title1}" /></td></tr>
				<tr><th scope="row"><h:outputText value="#{msgs.title2}" /></th><td><h:outputText value="#{SuTool.title2}" /></td></tr>
				<tr><th scope="row"><h:outputText value="#{msgs.title3}" /></th><td><h:outputText value="#{SuTool.title3}" /></td></tr>
				<tr><th scope="row"><h:outputText value="#{msgs.affiliation}" /></th><td><h:outputText value="#{SuTool.affiliation}" /></td></tr>
				<tr><th scope="row"><h:outputText value="#{msgs.department}" /></th><td><h:outputText value="#{SuTool.department}" /></td></tr>
				<tr><th scope="row"><h:outputText value="#{msgs.externalAffiliation}" /></th><td><h:outputText value="#{SuTool.externalAffiliation}" /></td></tr>
				<tr><th scope="row"><h:outputText value="#{msgs.enrollment}" /></th><td><h:outputText value="#{SuTool.enrollment}" /></td></tr>
				</c:if>
                <tr><th scope="row"><h:outputText value="#{msgs.email}" /></th><td><h:outputText value="#{SuTool.userinfo.email}" /></td></tr>
                <tr><th scope="row"><h:outputText value="#{msgs.eid}" /></th><td><h:outputText value="#{SuTool.userinfo.eid}" /></td></tr>
                <tr><th scope="row"><h:outputText value="#{msgs.id}" /></th><td><h:outputText value="#{SuTool.userinfo.id}" /></td></tr>
                <tr><th scope="row"><h:outputText value="#{msgs.type}" /></th><td><h:outputText value="#{SuTool.userinfo.type}" /></td></tr>
                <tr><th scope="row"><h:outputText value="#{msgs.created}" /></th><td><h:outputText value="#{SuTool.userinfo.createdTime.display}" /></td></tr>
            </table>
        <h:outputText rendered="#{SuTool.userinfo == null}" escape="false" value="</div>" />

		<h:panelGroup>
			<h:message for="username" errorClass="alertMessage" infoClass="instruction" />
			<p>
				<h:outputLabel for="username" value="#{msgs.eid}" /><h:outputText value="&nbsp;&nbsp;" escape="false"/>
				<h:inputText id="username" value="#{SuTool.username}" required="true" />
			</p>
		</h:panelGroup>
	
		<p class="act">
			<h:commandButton id="become" styleClass="active" action="#{SuTool.confirm}" value="#{msgs.become_user_button}" /> 
			&nbsp;
			<h:commandButton id="view" action="#{SuTool.su}" value="#{msgs.view_user_info_button}" />
		</p>

	</h:form>

</sakai:view_container>
</f:view>
