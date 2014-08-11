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
   
   <ospx:xheader>
      <ospx:xheadertitle id="styleTitle" value="#{msgs.style_title}" />
      <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
         <sakai:instruction_message value="#{msgs.style_instruction_message}" />
            <h:outputLabel for="styleFile" id="styleLabel" value="#{msgs.wizard_style}" />
            <h:panelGroup>
               <h:inputText id="styleFile" value="#{wizard.current.styleName}" 
                     readonly="true" required="false" />
               <h:commandLink action="#{wizard.current.processActionSelectStyle}" immediate="true">
                  <h:outputText value="#{msgs.select_style}"/>
               </h:commandLink>
            </h:panelGroup>
      </ospx:xheaderdrawer>
   </ospx:xheader>   
   
   <%@ include file="builderButtons.jspf"%>

</h:form>
</sakai:view>

</f:view>
