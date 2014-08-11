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
<sakai:view_container title="#{common_msgs.title_list}">
<h:form>

	<sakai:tool_bar>	
		<sakai:tool_bar_item
			action="#{ListTool.processActionOptions}"
			value="#{common_msgs.bar_options}" />
	</sakai:tool_bar>

	<sakai:view_content>

		<h:messages showSummary="false" showDetail="true" rendered="#{!empty facesContext.maximumSeverity}"/>
	
		<sakai:flat_list value="#{ListTool.entries}" var="co">
			<h:column rendered="#{ListTool.selectedColumns[0].base.selected}">
				<f:facet name="header">
					<f:subview id="col0">
						<h:commandLink id="sortCol0Asc" 
							action="#{ListTool.selectedColumns[0].processActionSortAsc}" 
							rendered="#{ListTool.selectedColumns[0].sortable && ListTool.currentSortDir == -1}" 
							value="#{common_msgs[ListTool.selectedColumns[0].base.name]}">
						
							<h:graphicImage id="col0Desc" value="#{ListTool.serverUrl}/library/image/sakai/sortdescending.gif" 
								rendered="#{ListTool.selectedColumns[0].currentSortField}" />       
						</h:commandLink>
						<h:commandLink id="sortCol0Desc" 
							action="#{ListTool.selectedColumns[0].processActionSortDesc}" 
							rendered="#{ListTool.selectedColumns[0].sortable && ListTool.currentSortDir == 1}" 
							value="#{common_msgs[ListTool.selectedColumns[0].base.name]}">
							
							<h:graphicImage id="col0Asc" value="#{ListTool.serverUrl}/library/image/sakai/sortascending.gif" 
								rendered="#{ListTool.selectedColumns[0].currentSortField}" />
						</h:commandLink>
						<h:outputText rendered="#{!ListTool.selectedColumns[0].sortable}" 
							value="#{common_msgs[ListTool.selectedColumns[0].base.name]}" />
					</f:subview>
				</f:facet>
				<h:outputLink rendered="#{!co.newWindow}" value="#{co.redirectUrl}" target="_top" >
					<h:outputText value="#{co.columnValues['0']}"/>
				</h:outputLink>
				<h:outputLink rendered="#{co.newWindow}" value="#{co.redirectUrl}" target="_new" >
					<h:outputText value="#{co.columnValues['0']}"/>
				</h:outputLink>
			</h:column>
			<h:column rendered="#{ListTool.selectedColumns['1'].base.selected}">
				<f:facet name="header">
					<f:subview id="col1">
						<h:commandLink id="sortCol1Asc" 
							action="#{ListTool.selectedColumns[1].processActionSortAsc}" 
							rendered="#{ListTool.selectedColumns[1].sortable && ListTool.currentSortDir == -1}" 
							value="#{common_msgs[ListTool.selectedColumns[1].base.name]}">
							<h:graphicImage id="col1Desc" value="#{ListTool.serverUrl}/library/image/sakai/sortdescending.gif" 
								rendered="#{ListTool.selectedColumns[1].currentSortField}" />   
						</h:commandLink>
						<h:commandLink id="sortCol1Desc" 
							action="#{ListTool.selectedColumns[1].processActionSortDesc}" 
							rendered="#{ListTool.selectedColumns[1].sortable && ListTool.currentSortDir == 1}" 
							value="#{common_msgs[ListTool.selectedColumns[1].base.name]}">
							<h:graphicImage id="col1Asc" value="#{ListTool.serverUrl}/library/image/sakai/sortascending.gif" 
								rendered="#{ListTool.selectedColumns[1].currentSortField}" />
						</h:commandLink>
						<h:outputText rendered="#{!ListTool.selectedColumns[1].sortable}" 
							value="#{common_msgs[ListTool.selectedColumns[1].base.name]}" />
					</f:subview>
				</f:facet>
				<h:outputText value="#{co.columnValues['1']}"/>
			</h:column>
			<h:column rendered="#{ListTool.selectedColumns['2'].base.selected}">
				<f:facet name="header">
					<f:subview id="col2">
						<h:commandLink id="sortCol2Asc" 
							action="#{ListTool.selectedColumns[2].processActionSortAsc}" 
							rendered="#{ListTool.selectedColumns[2].sortable && ListTool.currentSortDir == -1}" 
							value="#{common_msgs[ListTool.selectedColumns[2].base.name]}">
							<h:graphicImage id="col2Desc" value="#{ListTool.serverUrl}/library/image/sakai/sortdescending.gif" 
								rendered="#{ListTool.selectedColumns[2].currentSortField}" />   
						</h:commandLink>
						<h:commandLink id="sortCol2Desc" 
							action="#{ListTool.selectedColumns[2].processActionSortDesc}" 
							rendered="#{ListTool.selectedColumns[2].sortable && ListTool.currentSortDir == 1}" 
							value="#{common_msgs[ListTool.selectedColumns[2].base.name]}">
							<h:graphicImage id="col2Asc" value="#{ListTool.serverUrl}/library/image/sakai/sortascending.gif" 
								rendered="#{ListTool.selectedColumns[2].currentSortField}" />
						</h:commandLink>
						<h:outputText rendered="#{!ListTool.selectedColumns[2].sortable}" 
							value="#{common_msgs[ListTool.selectedColumns[2].base.name]}" />
					</f:subview>
				</f:facet>
				<h:outputText value="#{co.columnValues['2']}"/>
			</h:column>
			<h:column rendered="#{ListTool.selectedColumns['3'].base.selected}">
				<f:facet name="header">
					<f:subview id="col3">
						<h:commandLink id="sortCol3Asc" 
							action="#{ListTool.selectedColumns[3].processActionSortAsc}" 
							rendered="#{ListTool.selectedColumns[3].sortable && ListTool.currentSortDir == -1}" 
							value="#{common_msgs[ListTool.selectedColumns[3].base.name]}">
							<h:graphicImage id="col3Desc" value="#{ListTool.serverUrl}/library/image/sakai/sortdescending.gif" 
								rendered="#{ListTool.selectedColumns[3].currentSortField}" />   
						</h:commandLink>
						<h:commandLink id="sortCol3Desc" 
							action="#{ListTool.selectedColumns[3].processActionSortDesc}" 
							rendered="#{ListTool.selectedColumns[3].sortable && ListTool.currentSortDir == 1}" 
							value="#{common_msgs[ListTool.selectedColumns[3].base.name]}">
							<h:graphicImage id="col3Asc" value="#{ListTool.serverUrl}/library/image/sakai/sortascending.gif" 
								rendered="#{ListTool.selectedColumns[3].currentSortField}" />
						</h:commandLink>
						<h:outputText rendered="#{!ListTool.selectedColumns[3].sortable}" 
							value="#{common_msgs[ListTool.selectedColumns[3].base.name]}" />
					</f:subview>
				</f:facet>
				<h:outputText value="#{co.columnValues['3']}"/>
			</h:column>
			<h:column rendered="#{ListTool.selectedColumns['4'].base.selected}">
				<f:facet name="header">
					<f:subview id="col4">
						<h:commandLink id="sortCol4Asc" 
							action="#{ListTool.selectedColumns[4].processActionSortAsc}" 
							rendered="#{ListTool.selectedColumns[4].sortable && ListTool.currentSortDir == -1}" 
							value="#{common_msgs[ListTool.selectedColumns[4].base.name]}">
							<h:graphicImage id="col4Desc" value="#{ListTool.serverUrl}/library/image/sakai/sortdescending.gif" 
								rendered="#{ListTool.selectedColumns[4].currentSortField}" />   
						</h:commandLink>
						<h:commandLink id="sortCol4Desc" 
							action="#{ListTool.selectedColumns[4].processActionSortDesc}" 
							rendered="#{ListTool.selectedColumns[4].sortable && ListTool.currentSortDir == 1}" 
							value="#{common_msgs[ListTool.selectedColumns[4].base.name]}">
							<h:graphicImage id="col4Asc" value="#{ListTool.serverUrl}/library/image/sakai/sortascending.gif" 
								rendered="#{ListTool.selectedColumns[4].currentSortField}" />
						</h:commandLink>
						<h:outputText rendered="#{!ListTool.selectedColumns[4].sortable}" 
							value="#{common_msgs[ListTool.selectedColumns[4].base.name]}" />
					</f:subview>
				</f:facet>
				<h:outputText value="#{co.columnValues['4']}"/>
			</h:column>
			<h:column rendered="#{ListTool.selectedColumns['5'].base.selected}">
				<f:facet name="header">
					<f:subview id="col5">
						<h:commandLink id="sortCol5Asc" 
							action="#{ListTool.selectedColumns[5].processActionSortAsc}" 
							rendered="#{ListTool.selectedColumns[5].sortable && ListTool.currentSortDir == -1}" 
							value="#{common_msgs[ListTool.selectedColumns[5].base.name]}">
							<h:graphicImage id="col5Desc" value="#{ListTool.serverUrl}/library/image/sakai/sortdescending.gif" 
								rendered="#{ListTool.selectedColumns[5].currentSortField}" />   
						</h:commandLink>
						<h:commandLink id="sortCol5Desc" 
							action="#{ListTool.selectedColumns[5].processActionSortDesc}" 
							rendered="#{ListTool.selectedColumns[5].sortable && ListTool.currentSortDir == 1}" 
							value="#{common_msgs[ListTool.selectedColumns[5].base.name]}">
							<h:graphicImage id="col5Asc" value="#{ListTool.serverUrl}/library/image/sakai/sortascending.gif" 
								rendered="#{ListTool.selectedColumns[5].currentSortField}" />
						</h:commandLink>
						<h:outputText rendered="#{!ListTool.selectedColumns[5].sortable}" 
							value="#{common_msgs[ListTool.selectedColumns[5].base.name]}" />
					</f:subview>
				</f:facet>
				<h:outputText value="#{co.columnValues['5']}"/>
			</h:column>
			<h:column rendered="#{ListTool.selectedColumns['6'].base.selected}">
				<f:facet name="header">
					<f:subview id="col6">
						<h:commandLink id="sortCol6Asc" 
							action="#{ListTool.selectedColumns[6].processActionSortAsc}" 
							rendered="#{ListTool.selectedColumns[6].sortable && ListTool.currentSortDir == -1}" 
							value="#{common_msgs[ListTool.selectedColumns[6].base.name]}">
							<h:graphicImage id="col6Desc" value="#{ListTool.serverUrl}/library/image/sakai/sortdescending.gif" 
								rendered="#{ListTool.selectedColumns[6].currentSortField}" />   
						</h:commandLink>
						<h:commandLink id="sortCol6Desc" 
							action="#{ListTool.selectedColumns[6].processActionSortDesc}" 
							rendered="#{ListTool.selectedColumns[6].sortable && ListTool.currentSortDir == 1}" 
							value="#{common_msgs[ListTool.selectedColumns[6].base.name]}">
							<h:graphicImage id="col6Asc" value="#{ListTool.serverUrl}/library/image/sakai/sortascending.gif" 
								rendered="#{ListTool.selectedColumns[6].currentSortField}" />
						</h:commandLink>
						<h:outputText rendered="#{!ListTool.selectedColumns[6].sortable}" 
							value="#{common_msgs[ListTool.selectedColumns[6].base.name]}" />
					</f:subview>
				</f:facet>
				<h:outputText value="#{co.columnValues['6']}"/>
			</h:column>
			<h:column rendered="#{ListTool.selectedColumns['7'].base.selected}">
				<f:facet name="header">
					<f:subview id="col7">
						<h:commandLink id="sortCol7Asc" 
							action="#{ListTool.selectedColumns[7].processActionSortAsc}" 
							rendered="#{ListTool.selectedColumns[7].sortable && ListTool.currentSortDir == -1}" 
							value="#{common_msgs[ListTool.selectedColumns[7].base.name]}">
							<h:graphicImage id="col7Desc" value="#{ListTool.serverUrl}/library/image/sakai/sortdescending.gif" 
								rendered="#{ListTool.selectedColumns[7].currentSortField}" />   
						</h:commandLink>
						<h:commandLink id="sortCol7Desc" 
							action="#{ListTool.selectedColumns[7].processActionSortDesc}" 
							rendered="#{ListTool.selectedColumns[7].sortable && ListTool.currentSortDir == 1}" 
							value="#{common_msgs[ListTool.selectedColumns[7].base.name]}">
							<h:graphicImage id="col7Asc" value="#{ListTool.serverUrl}/library/image/sakai/sortascending.gif" 
								rendered="#{ListTool.selectedColumns[7].currentSortField}" />
						</h:commandLink>
						<h:outputText rendered="#{!ListTool.selectedColumns[7].sortable}" 
							value="#{common_msgs[ListTool.selectedColumns[7].base.name]}" />
					</f:subview>
				</f:facet>
				<h:outputText value="#{co.columnValues['7']}"/>
			</h:column>
			<h:column rendered="#{ListTool.selectedColumns['8'].base.selected}">
				<f:facet name="header">
					<h:commandLink id="sortCol8Asc" 
						action="#{ListTool.selectedColumns[8].processActionSortAsc}" 
						rendered="#{ListTool.selectedColumns[8].sortable && ListTool.currentSortDir == -1}" 
						value="#{common_msgs[ListTool.selectedColumns[8].base.name]}">
							<h:graphicImage id="col8Desc" value="#{ListTool.serverUrl}/library/image/sakai/sortdescending.gif" 
								rendered="#{ListTool.selectedColumns[8].currentSortField}" />   
						</h:commandLink>
					<h:commandLink id="sortCol8Desc" 
							action="#{ListTool.selectedColumns[8].processActionSortDesc}" 
							rendered="#{ListTool.selectedColumns[8].sortable && ListTool.currentSortDir == 1}" 
							value="#{common_msgs[ListTool.selectedColumns[8].base.name]}">
							<h:graphicImage id="col8Asc" value="#{ListTool.serverUrl}/library/image/sakai/sortascending.gif" 
								rendered="#{ListTool.selectedColumns[8].currentSortField}" />
						</h:commandLink>
						<h:outputText rendered="#{!ListTool.selectedColumns[8].sortable}" 
							value="#{common_msgs[ListTool.selectedColumns[8].base.name]}" />
				</f:facet>
				<h:outputText value="#{co.columnValues['8']}"/>
			</h:column>
			<h:column rendered="#{ListTool.selectedColumns['9'].base.selected}">
				<f:facet name="header">
					<h:commandLink id="sortCol9Asc" 
						action="#{ListTool.selectedColumns[9].processActionSortAsc}" 
						rendered="#{ListTool.selectedColumns[9].sortable && ListTool.currentSortDir == -1}" 
						value="#{common_msgs[ListTool.selectedColumns[9].base.name]}">
							<h:graphicImage id="col9Desc" value="#{ListTool.serverUrl}/library/image/sakai/sortdescending.gif" 
								rendered="#{ListTool.selectedColumns[9].currentSortField}" />   
						</h:commandLink>
					<h:commandLink id="sortCol9Desc" 
							action="#{ListTool.selectedColumns[9].processActionSortDesc}" 
							rendered="#{ListTool.selectedColumns[9].sortable && ListTool.currentSortDir == 1}" 
							value="#{common_msgs[ListTool.selectedColumns[9].base.name]}">
							<h:graphicImage id="col9Asc" value="#{ListTool.serverUrl}/library/image/sakai/sortascending.gif" 
								rendered="#{ListTool.selectedColumns[9].currentSortField}" />
						</h:commandLink>
						<h:outputText rendered="#{!ListTool.selectedColumns[9].sortable}" 
							value="#{common_msgs[ListTool.selectedColumns[9].base.name]}" />
				</f:facet>
				<h:outputText value="#{co.columnValues['9']}"/>
			</h:column>
		</sakai:flat_list>

	</sakai:view_content>

</h:form>
</sakai:view_container>
</f:view>
