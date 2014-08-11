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
<h:form>


   <sakai:view_title value="#{msgs.add_wizard} #{msgs.org_theospi_portfolio_wizard_model_Wizard_hierarchical}" rendered="#{wizard.current.base.type ==
               'org.theospi.portfolio.wizard.model.Wizard.hierarchical' && wizard.current.newWizard}"/>
   <sakai:view_title value="#{msgs.edit_wizard} #{msgs.org_theospi_portfolio_wizard_model_Wizard_hierarchical}" rendered="#{wizard.current.base.type ==
               'org.theospi.portfolio.wizard.model.Wizard.hierarchical' && !wizard.current.newWizard}"/>
   <sakai:view_title value="#{msgs.add_wizard} #{msgs.org_theospi_portfolio_wizard_model_Wizard_sequential}"  rendered="#{wizard.current.base.type !=
               'org.theospi.portfolio.wizard.model.Wizard.hierarchical' && wizard.current.newWizard}"/>
   <sakai:view_title value="#{msgs.edit_wizard} #{msgs.org_theospi_portfolio_wizard_model_Wizard_sequential}"  rendered="#{wizard.current.base.type !=
               'org.theospi.portfolio.wizard.model.Wizard.hierarchical' && !wizard.current.newWizard}"/>
    
   <%@ include file="steps.jspf"%>
   
   <sakai:instruction_message value="#{msgs.wizard_instructions}" />
      
   <sakai:instruction_message value="#{msgs.instructions_requiredFields}"/>
   
<%--   <sakai:instruction_message value=" Last saved: " />
   <sakai:instruction_message value="#{wizard.lastSavedId}" /> --%>
   <sakai:messages rendered="#{!empty facesContext.maximumSeverity}"/>
   <h:panelGrid columns="1"  styleClass="jsfFormTable" cellpadding="0">
      <h:panelGroup styleClass="shorttext">
         <h:outputText value="*" styleClass="reqStar"/>
         <h:outputLabel for="name" id="nameLabel" value="#{msgs.wizard_name}" />
         <h:inputText id="name" value="#{wizard.current.base.name}" required="true" size="30">
            <f:validateLength minimum="1" maximum="255" />
         </h:inputText>
         <h:message for="name" styleClass="alertMessageInline" style="border:none"/>
      </h:panelGroup>
      <h:panelGroup styleClass="longtext" style="padding:0;display:block;margin:0">
	  		<h:outputLabel for="description" id="descriptionLabel" value="#{msgs.wizard_description}" styleClass="block"/>
			<h:inputTextarea id="description" value="#{wizard.current.base.description}" cols="60" rows="6" >
            <f:validateLength minimum="1" maximum="1024" />
         </h:inputTextarea>
         <h:message for="description" styleClass="alertMessageInline" style="border:none" />
      </h:panelGroup>
	  <h:panelGroup styleClass="longtext" style="padding:0;display:block;margin:0">
      <h:outputLabel for="keywords" id="keywordsLabel" value="#{msgs.wizard_keywords}"  styleClass="block"/>
        <h:inputTextarea id="keywords" value="#{wizard.current.base.keywords}" cols="60" rows="6">
            <f:validateLength minimum="1" maximum="1024" />
         </h:inputTextarea>
         <h:message for="keywords" styleClass="alertMessageInline" />
      </h:panelGroup>
	  <h:panelGroup styleClass="shorttext">
	  <h:outputLabel for="styleFile" id="styleLabel" value="#{msgs.wizard_style}" />
         <h:inputText id="styleFile" value="#{wizard.current.styleName}" 
               readonly="true" disabled="true" required="false" />
         <h:commandLink action="#{wizard.current.processActionSelectStyle}" immediate="true">
            <h:outputText value=" "/><h:outputText value="#{msgs.select_style}"/>
         </h:commandLink>
      </h:panelGroup>
    <%--  <h:panelGroup>
	   <h:selectBooleanCheckbox id="asTool" value="#{wizard.current.exposeAsTool}" />
	   <h:outputLabel value="#{msgs.expose_as_tool}" for="asTool" />
      </h:panelGroup> --%>

	<!--  ********** Reviewer Group Access Start ************-->
      <h:panelGroup styleClass="longtext" style="padding:0;display:block;margin:0" rendered="#{!wizard.current.ignoreReviewerGroupAccess}">
      <h:outputText value="#{msgs.group_access}" />
      </h:panelGroup>
      <h:panelGroup styleClass="longtext" style="padding:0;display:block;margin:5" rendered="#{!wizard.current.ignoreReviewerGroupAccess}">
         <h:selectOneRadio id="review_group_access" value="#{wizard.current.base.reviewerGroupAccessString}" layout="pageDirection" disabled="#{wizard.current.isWizardUsed}">
            <f:selectItem itemLabel="#{msgs.normal_group_access_label}"   itemValue="0"/>
            <f:selectItem itemLabel="#{msgs.unrestricted_group_access_label}" itemValue="1"/>
         </h:selectOneRadio>
      </h:panelGroup>
	<!--  ********** Reviewer Group Access End ************-->

      <!--  ********** Feedback Options Start ************-->
   
      <h:panelGroup styleClass="longtext" style="padding:0;display:block;margin:0">
      <h:outputText value="#{msgs.feedback_options_gen}" />
      </h:panelGroup>
      <h:panelGroup styleClass="longtext" style="padding:0;display:block;margin:5">
         <h:selectOneRadio id="feedback_option_gen" value="#{wizard.current.base.generalFeedbackOptionString}" layout="pageDirection" disabled="#{wizard.current.isWizardUsed}">
            <f:selectItem itemLabel="#{msgs.feedback_option_gen_open}"   itemValue="0"/>
            <f:selectItem itemLabel="#{msgs.feedback_option_gen_single}" itemValue="1"/>
            <f:selectItem itemLabel="#{msgs.feedback_option_gen_none}"   itemValue="2"/>
         </h:selectOneRadio>
      </h:panelGroup>
      <h:panelGroup styleClass="longtext" style="padding:0;display:block;margin:0">
      <h:outputText value="#{msgs.feedback_options_item}" />
      </h:panelGroup>
      <h:panelGroup styleClass="longtext" style="padding:0;display:block;margin:5">
         <h:selectOneRadio id="feedback_option_item" value="#{wizard.current.base.itemFeedbackOptionString}" layout="pageDirection" border="0" disabled="#{wizard.current.isWizardUsed}">
            <f:selectItem itemLabel="#{msgs.feedback_option_item_open}"   itemValue="0"/>
            <f:selectItem itemLabel="#{msgs.feedback_option_item_single}" itemValue="1"/>
            <f:selectItem itemLabel="#{msgs.feedback_option_item_none}"   itemValue="2"/>
         </h:selectOneRadio>
      </h:panelGroup>
   </h:panelGrid>

   <%@ include file="builderButtons.jspf"%>
</h:form>
</sakai:view>

</f:view>
