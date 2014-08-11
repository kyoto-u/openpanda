<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://www.theospi.org/jsf/osp" prefix="ospx" %>
<%@ taglib uri="http://www.theospi.org/help/jsf" prefix="help" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>
<%@ taglib prefix="osp" uri="http://www.theospi.org" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>

<%
      response.setContentType("text/html; charset=UTF-8");
      response.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
      response.addDateHeader("Last-Modified", System.currentTimeMillis());
      response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
      response.addHeader("Pragma", "no-cache");
%>

<f:view>
<sakai:view>
<f:verbatim>
	<script type="text/javascript" language="JavaScript" src="/library/js/jquery-ui-latest/js/jquery.min.js"></script>	
	<script type="text/javascript" language="JavaScript" src="/library/js/jquery-ui-latest/js/jquery-ui.min.js"></script>
	<script type="text/javascript" language="JavaScript" src="/osp-common-tool/js/dialog.js"></script>
	<link rel="stylesheet" type="text/css" media="all" href="/osp-common-tool/css/dialog.css" />

<script type="text/javascript" language="JavaScript">
	<!--
	
   function resetHeight() {
	   setMainFrameHeight('</f:verbatim><h:outputText value="#{wizard.panelId}"/><f:verbatim>');
   }

	iframeId = '</f:verbatim><h:outputText value="#{wizard.panelId}"/><f:verbatim>';

    -->
  </script>

</f:verbatim>

<h:form>

<t:aliasBeansScope>
<t:aliasBean alias="#{wizardCreate}" value="#{wizard.canCreate}" />
<t:aliasBean alias="#{wizardMaintainer}" value="#{wizard.maintainer}" />

<sakai:tool_bar>
      <sakai:tool_bar_item rendered="#{wizardCreate}"
      action="#{wizard.processActionNew}"
      value="#{msgs.new_wizard}" />
          
      <sakai:tool_bar_item rendered="#{wizardCreate}"
          action="#{wizard.importWizard}"
          value="#{msgs.import}" />

      <sakai:tool_bar_item rendered="#{wizardMaintainer}"
          action="#{wizard.processPermissions}"
          value="#{msgs.permissions_link}" />
      <f:subview id="prefsView">
      <f:verbatim>
      <a href="#"
         	onclick="dialogutil.openDialog('#dialogDiv', '#dialogFrame', '<osp:url value="osp.prefs.helper/prefs">
         		<osp:param name="dialogDivId" value="#dialogDiv" />
         		<osp:param name="typeKey" value="${wizard.typeKey}" />
         		<osp:param name="qualifier_text" value="${wizard.prefsQualifierText}"/>
         		<osp:param name="prefsSiteSavedDiv" value="#prefsSiteSavedDiv" />
         		<osp:param name="prefsAllSavedDiv" value="#prefsAllSavedDiv" />
         		<osp:param name="toolId" value="osp.wizard" />
         		<osp:param name="frameId" value="#dialogFrame" />
         		</osp:url>');"
               title="${wizard.actionPrefsText}"></f:verbatim><h:outputText value="#{msgs.action_prefs}"/><f:verbatim></a></f:verbatim> 
		</f:subview>
   </sakai:tool_bar>
   
   <f:verbatim>
	  <div id="dialogDiv" style="display:none">
         <iframe id="dialogFrame" width="100%" height="100%" frameborder="0">
         </iframe>
      </div>
   </f:verbatim>
   
   <sakai:view_title value="#{msgs.wizard_title}" rendered="#{wizardCreate ||  wizardMaintainer}"/>
   <sakai:view_title value="#{msgs.wizard_title_user}" rendered="#{not (wizardCreate ||  wizardMaintainer)}"/>
   
</t:aliasBeansScope>   
   <%--
   <sakai:instruction_message value="#{msgs.wizard_instruction_message}" />
  --%>  
<%--   <sakai:instruction_message value=" Last saved: " />
   <sakai:instruction_message value="#{wizard.lastSavedId}" /> --%>
   <sakai:messages rendered="#{!empty facesContext.maximumSeverity}"/>
   	<t:div styleClass="success" forceId="true" id="prefsSiteSavedDiv" style="display:none"><h:outputText value="#{msgs.prefs_saved_site}" /></t:div>	
	<t:div styleClass="success" forceId="true" id="prefsAllSavedDiv" style="display:none"><h:outputText value="#{msgs.prefs_saved_all}" /></t:div>	
   
   
   <h:outputText value="#{wizard.lastSaveWizard} #{msgs.wizard_was_submitted}" styleClass="success" rendered="#{wizard.lastSaveWizard != ''}" />
   <h:outputText value="#{wizard.lastSavePage} #{msgs.page_was_submitted}" styleClass="success" rendered="#{wizard.lastSavePage != ''}" />
   <h:outputText value="#{wizard.lastError} #{msgs.wizard_bad_file_type}" styleClass="validation" rendered="#{wizard.lastError == 'badFileType'}" />
   <h:outputText value="#{wizard.lastError} #{msgs.wizard_bad_import}" styleClass="validation" rendered="#{wizard.lastError == 'badImport'}" />

   <h:dataTable  value="#{wizard.wizards}" var="wizardItem" styleClass="lines listHier nolines" headerClass="exclude" summary="#{msgs.wizard_list_summary}" border="0">
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.wizards}" />
         </f:facet>
         <t:aliasBean alias="#{wizardItemOperate}" value="#{wizardItem.canOperateOnWizardInstance}">
	         <h:outputText value="#{wizardItem.base.name}" rendered="#{!wizardItemOperate}"/>
	         <f:subview id="runLink" rendered="#{wizardItemOperate}">
	            <h:commandLink action="#{wizardItem.processActionRunWizard}" title="#{msgs.run_wizard}">
	               <h:outputText value="#{wizardItem.base.name}"/>
	            </h:commandLink>
	         </f:subview>
         </t:aliasBean>
	     <sakai:separatedList id="wizActionList" separator=" | " styleClass="itemAction">
	           <f:subview id="previewLink" rendered="#{wizardItem.canPublish && wizardItem.totalPages > 0 && !wizardItem.base.preview && !wizardItem.base.published}">
	                 <h:commandLink action="#{wizardItem.processActionPreview}">
	                 <h:outputText value="#{msgs.preview}" />
	              </h:commandLink>
	           </f:subview>
	           <f:subview id="publishLink" rendered="#{wizardItem.canPublish && wizardItem.totalPages > 0 && wizardItem.base.preview}">
	                 <h:commandLink action="#{wizardItem.processActionPublish}">
	                 <h:outputText value="#{msgs.publish}" />
	              </h:commandLink>
	           </f:subview>
	           <f:subview id="editLink" rendered="#{wizardItem.canEdit}">
	              <h:commandLink action="#{wizardItem.processActionEdit}">
	                 <h:outputText value="#{msgs.edit}"/>
	              </h:commandLink>
	           </f:subview>
	           <f:subview id="deleteLink" rendered="#{wizardItem.canDelete}">
	              <h:commandLink action="#{wizardItem.processActionConfirmDelete}">
	                 <h:outputText value="#{msgs.delete}" />
	              </h:commandLink>
	           </f:subview>
	           <f:subview id="exportLink" rendered="#{wizardItem.canExport}">
	              <h:outputLink value="#{wizardItem.currentExportLink}">
	                  <h:outputText value="#{msgs.export}"/>
	              </h:outputLink>
	           </f:subview>
	     </sakai:separatedList>
      </h:column>
<%-- TODO having the description here really throws rendering off -- would be ok as a separate row, but this is JSF  
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.wizard_description}" />
         </f:facet>
         <help:glossary link="true" hover="false"><h:outputText value="#{wizardItem.concatDescription}"/></help:glossary>
      </h:column>
--%>
	  <%--
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.re_order}" />
         </f:facet>
         <h:commandLink action="#{wizardItem.moveUp}" rendered="#{!wizardItem.first}">
            <h:graphicImage value="/img/arrowUp.gif" />
         </h:commandLink>
	     <f:subview id="publishLink" rendered="#{wizardItem.first}">
	        <h:outputText value="&nbsp;&nbsp;&nbsp;&nbsp;" escape="false" />
	     </f:subview>
         <h:commandLink action="#{wizardItem.moveDown}" rendered="#{!wizardItem.last}">
            <h:graphicImage value="/img/arrowDown.gif" />
         </h:commandLink>
      </h:column>  --%>
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.owner_title}" />
         </f:facet>
         <h:outputText value="#{wizardItem.base.owner.displayName}" />
      </h:column>
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.published_title}" />
         </f:facet>
         <h:outputText value="#{msgs.preview}" rendered="#{wizardItem.base.preview}"/>
         <h:outputText value="#{msgs.published}" rendered="#{wizardItem.base.published}"/>
         <h:outputText value="#{msgs.unpublished}" rendered="#{!wizardItem.base.preview && !wizardItem.base.published}"/>
      </h:column>
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.wizard_type}" />
         </f:facet>
         <f:subview id="hiertype" rendered="#{wizardItem.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}">
            <h:outputText value="#{msgs.org_theospi_portfolio_wizard_model_Wizard_hierarchical}"/>
         </f:subview>
         <f:subview id="seqtype" rendered="#{wizardItem.base.type == 'org.theospi.portfolio.wizard.model.Wizard.sequential'}">
            <h:outputText value="#{msgs.org_theospi_portfolio_wizard_model_Wizard_sequential}"/>
         </f:subview>
      </h:column>
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.wizard_list_submitted_pages}" />
         </f:facet>
         <h:outputText value="#{wizardItem.usersWizard.submittedPages}"/>
      </h:column>
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.wizard_list_total_pages}" />
         </f:facet>
         <h:outputText value="#{wizardItem.totalPages}"/>
      </h:column>
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.wizard_list_last_visited}" />
         </f:facet>
         <h:outputFormat id="lastVisited" value="#{msgs.date_format}"
                rendered="#{!empty wizardItem.usersWizard.base.lastVisited}">
             <f:param value="#{wizardItem.usersWizard.base.lastVisited}"/>
         </h:outputFormat>
      </h:column>

   </h:dataTable>
   
   <%-- wizard.wizardListSize needs to be called after wizard.wizards, otherwise it won't be populated --%>
   <sakai:instruction_message   value="#{msgs.no_wizards}" rendered="#{wizard.wizardListSize == 0}" />
   
</h:form>
</sakai:view>

</f:view>
