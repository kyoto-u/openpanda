<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>
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
	
	<t:stylesheet path="/../../osp-common-tool/css/thickbox.css" />
	<t:stylesheet path="/../../osp-common-tool/css/eport.css" />
<f:verbatim>
<script type="text/javascript" language="JavaScript" src="/library/js/jquery-ui-latest/js/jquery.min.js"></script>
	<script type="text/javascript" language="JavaScript" src="/osp-common-tool/js/thickbox.js"></script>
	
	
</f:verbatim>
		<h:outputText escape="fase" value="#{matrixLinkTool.frameId}" />
	
		<h:form>
			<sakai:view_title value="#{matrixLinkTool.viewTitle}" />
			<sakai:instruction_message value="#{matrix_msgs.matrix_links_desc}" />
			<sakai:messages rendered="#{!empty facesContext.maximumSeverity}"/>

			<h:panelGrid columns="2">
				<h:column>
					<h:outputLabel value="#{matrix_msgs.site_heading}" />
				</h:column>

				<h:column>
					<h:selectOneMenu value="#{matrixLinkTool.selectedSiteId}" 
							onchange="this.form.submit();"
							valueChangeListener="#{matrixLinkTool.changeSite}">
						<f:selectItem  itemValue="" itemLabel="#{matrix_msgs.select_site}" />
						<f:selectItems value="#{matrixLinkTool.availableSites}" />
					</h:selectOneMenu>
				</h:column>

			</h:panelGrid>
			
			
			<h:dataTable id="datatable" value="#{matrixLinkTool.grids}" var="grid" width="100%">
				<h:column id="column">
					<ospx:xheader>
						<ospx:xheadertitle id="title" value="#{grid.scaffolding.title}" />
						<ospx:xheaderdrawer initiallyexpanded="true">

							<f:verbatim>
								<table id="tableId" cellspacing="0" width="100%">
									<tr>
										<th class="matrix-row-heading" width="10%" scope="col"></f:verbatim> <h:outputText
											value="#{grid.scaffolding.title}" /> <f:verbatim></th>
										</f:verbatim>
										<t:dataList id="colList" value="#{grid.columnLabels}"
											var="head">
											<f:verbatim>
												<th class="matrix-column-heading matriColumnDefault"
													width="10%"
													bgcolor="</f:verbatim> <h:outputText value="#{head.color}" /><f:verbatim>"
													style='color: </f:verbatim >   <h:outputText value="#{head.textColor}" rendered="#{not empty head.textColor}"/><f:verbatim >'
													scope="col">
											</f:verbatim>
											<h:outputText value="#{head.description}" />
											<f:verbatim>
												</th>
											</f:verbatim>
										</t:dataList>
										<f:verbatim>
									</tr>

									</f:verbatim>
									<t:dataList id="rowList" value="#{grid.rowLabels}"
										var="rowLabel" rowIndexVar="loopStatus">
										<f:verbatim>
											<tr>
												<th class="matrix-row-heading matriRowDefault"
													bgcolor="</f:verbatim><h:outputText value="#{rowLabel.color}"/><f:verbatim>"
													style='color: </f:verbatim><h:outputText value="#{rowLabel.textColor}" rendered="#{rowLabel.textColor}"/><f:verbatim>'>
										</f:verbatim>
										<h:outputText value="#{rowLabel.description}" />
										<f:verbatim>
											</th>
										</f:verbatim>
										<t:dataList var="cell"
											value="#{grid.matrixContents[loopStatus]}">
											<f:verbatim>
												<td
													class="matrix-cell-border matrix-</f:verbatim><h:outputText value="#{cell.scaffoldingCell.initialStatus}"/><f:verbatim>">
												<p style="position: relative; height: 100%;">
											</f:verbatim>

											<h:selectBooleanCheckbox
												style="position: absolute; top:0px; right:0px;"
												id="cellLinkBox" value="#{cell.linked}"
												valueChangeListener="#{cell.checkBoxChanged}" disabled="#{cell.disabled}" />
											<f:verbatim>
												<center>
											</f:verbatim>
											<h:outputLink styleClass="thickbox"
												value="osp.matrix.cell.info.helper/viewCellInformation.osp?session.sCell_id=#{cell.scaffoldingCell.id.value}">
												<h:graphicImage
													value="/../../library/image/sakai/information.png" />
											</h:outputLink>
											
											<f:verbatim>
											
												</center>
												</p>
												</td>
											</f:verbatim>
										</t:dataList>
										<f:verbatim>
											</tr>
										</f:verbatim>
									</t:dataList>
									<f:verbatim>
								</table>

							</f:verbatim>

						</ospx:xheaderdrawer>
					</ospx:xheader>
				</h:column>

			</h:dataTable>
			
			<h:panelGrid columns="1">
				<h:outputText value="<br/>#{matrix_msgs.none_to_display}" escape="false" rendered="#{matrixLinkTool.gridSize == 0}" />
				<h:outputText value="#{matrix_msgs.select_to_view}" escape="false" rendered="#{matrixLinkTool.gridSize == 0 && (matrixLinkTool.selectedSiteId == '' || empty matrixLinkTool.selectedSiteId)}" />
			</h:panelGrid>
			
			<sakai:button_bar>
   <sakai:button_bar_item action="#{matrixLinkTool.processActionBack}"
      value="#{matrix_msgs.save_exit}" styleClass="active" accesskey="b" />
      <sakai:button_bar_item action="#{matrixLinkTool.processActionCancel}"
      value="#{matrix_msgs.cancel}" styleClass="active" accesskey="c" />
      
 </sakai:button_bar>

		</h:form>
	</sakai:view>
</f:view>
