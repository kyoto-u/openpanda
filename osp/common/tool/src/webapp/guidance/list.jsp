<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://www.theospi.org/jsf/osp" prefix="ospx" %>

<%
		response.setContentType("text/html; charset=UTF-8");
		response.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
		response.addDateHeader("Last-Modified", System.currentTimeMillis());
		response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
		response.addHeader("Pragma", "no-cache");
%>

<f:view>
<sakai:view>
<h:form>
   <sakai:tool_bar>
      <sakai:tool_bar_item
      action="#{guidance.processActionNew}"
      value="New Sample" />
      <sakai:tool_bar_item
      action="#{guidance.processActionNewInstruction}"
      value="New Sample Instruction" />
      <sakai:tool_bar_item
      action="#{guidance.processActionNewExample}"
      value="New Sample Example" />
      <sakai:tool_bar_item
      action="#{guidance.processActionNewRationale}"
      value="New Sample Rationale" />
   </sakai:tool_bar>

   <sakai:view_title value="#{common_msgs.guidance_title}"/>
   <sakai:instruction_message value="Guidance Test Tool" />
   <sakai:instruction_message value=" Last saved: " />
   <sakai:instruction_message value="#{guidance.lastSavedId}" />
   <sakai:messages rendered="#{!empty facesContext.maximumSeverity}"/>

   <h:inputText value="#{guidance.formDisplayName}" />
   <h:inputText value="#{guidance.formTypeId}" />
   <h:commandButton value="test resource helper" action="#{guidance.processTestResourceHelper}" />

   <h:inputText value="#{guidance.formId}" />
   <h:commandButton value="test resource edit" action="#{guidance.processTestResourceEditHelper}" />

   <h:commandButton value="test resource view" action="#{guidance.processTestResourceViewHelper}" />

   <sakai:flat_list value="#{guidance.sampleGuidances}" var="sampleGuidance">
      <h:column>
         <f:facet name="header">
            <h:outputText value="sample guidance" />
         </f:facet>
      </h:column>
      <h:column>
         <h:outputText value="#{sampleGuidance.base.id}"/>
         <h:commandLink action="#{sampleGuidance.processActionView}">
            <h:outputText value="view"/>
         </h:commandLink>
         <h:outputText value=" | " />
         <h:commandLink action="#{sampleGuidance.processActionEdit}">
            <h:outputText value="edit"/>
         </h:commandLink>
         <h:outputText value=" | " />
         <h:commandLink action="#{sampleGuidance.processActionEditInstruction}">
            <h:outputText value="edit instruction"/>
         </h:commandLink>
         <h:outputText value=" | " />
         <h:commandLink action="#{sampleGuidance.processActionEditExample}">
            <h:outputText value="edit example"/>
         </h:commandLink>
         <h:outputText value=" | " />
         <h:commandLink action="#{sampleGuidance.processActionEditRationale}">
            <h:outputText value="edit rationale"/>
         </h:commandLink>
         <h:outputText value=" | " />
         <h:commandLink action="#{sampleGuidance.processActionDelete}">
            <h:outputText value="delete" />
         </h:commandLink>
      </h:column>
   </sakai:flat_list>
</h:form>
</sakai:view>

</f:view>
