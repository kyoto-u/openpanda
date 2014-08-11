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

   <sakai:view_title value="#{msgs.import_wizard_title}"/>

     <sakai:instruction_message value="#{msgs.import_wizard_instruction}" />
   <sakai:messages rendered="#{!empty facesContext.maximumSeverity}"/>
   
   <sakai:panel_edit>
      <h:outputLabel for="files" id="nameLabel" value="#{msgs.wizard_name}" />
      <h:panelGroup styleClass="shorttext">
         <h:inputText id="files" value="#{wizard.importFilesString}" disabled="true" />
		 <h:outputText value=" " />
         <h:commandLink action="#{wizard.processPickImportFiles}">
            <h:outputText value="#{msgs.pick_import_files}"/>
         </h:commandLink>
		 <h:message for="files" styleClass="alertMessageInline" style="border:none"/>
      </h:panelGroup>
   </sakai:panel_edit>
   
   <sakai:button_bar>
       <sakai:button_bar_item id="import" value="#{msgs.import_wizard_button}"
          action="#{wizard.processImportWizards}" styleClass="active" accesskey="s" />
       <sakai:button_bar_item id="cancel" value="#{msgs.cancel_wizard}" 
          action="#{wizard.processActionCancel}" accesskey="x" />
   </sakai:button_bar>
</h:form>
</sakai:view>

</f:view>
