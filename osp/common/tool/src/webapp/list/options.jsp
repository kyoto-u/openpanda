<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%
		response.setContentType("text/html; charset=UTF-8");
		response.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
		response.addDateHeader("Last-Modified", System.currentTimeMillis());
		response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
		response.addHeader("Pragma", "no-cache");
%>


<f:view>
<sakai:view_container title="#{common_msgs.title_options}">
<h:form>

	<sakai:tool_bar_message value="#{common_msgs.options_message}" />

	<sakai:view_content>

		<h:messages showSummary="false" showDetail="true" rendered="#{!empty facesContext.maximumSeverity}"/>
	
		<sakai:instruction_message value="#{common_msgs.options_instructions}" />
	
		<sakai:group_box title="#{common_msgs.list_config_group}">
			<sakai:panel_edit>
	 
				<h:outputText value="#{common_msgs.prop_hdr_title}"/>
				<h:inputText value="#{ListTool.currentConfig.title}" required="true" />
				<h:outputText value="#{common_msgs.prop_hdr_rows}"/>
				<h:inputText value="#{ListTool.currentConfig.rows}" required="true" />

            <sakai:flat_list value="#{ListTool.currentConfig.columns}" var="column">
      			<h:column>
      				<f:facet name="header">
      					<h:outputText value="#{common_msgs.show_header}"/>
      				</f:facet>
      				<h:selectBooleanCheckbox value="#{column.selected}"/>
      			</h:column>               
      			<h:column>
      				<f:facet name="header">
      					<h:outputText value="#{common_msgs.title_header}"/>
      				</f:facet>
      				<h:outputText value="#{common_msgs[column.name]}"/>
      			</h:column>               
            </sakai:flat_list>

			</sakai:panel_edit>
		</sakai:group_box>

		<sakai:button_bar>
			<sakai:button_bar_item
					action="#{ListTool.processActionOptionsSave}"
					value="#{common_msgs.bar_save}" />
			<sakai:button_bar_item
					immediate="true"
					action="#{ListTool.processMain}"
					value="#{common_msgs.bar_cancel}" />
		</sakai:button_bar>

	</sakai:view_content>

</h:form>
</sakai:view_container>
</f:view>
