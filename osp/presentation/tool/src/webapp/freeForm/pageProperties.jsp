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

<link media="all" rel="stylesheet" type="text/css" href="/osp-common-tool/css/eport.css" />
<table width="100%" style="margin-top:1em">
	<tr>
		<td style="vertical-align:middle;text-align:left">
				<div id="presentationName" class="shorttext">
					<span class="editableText portfolio_name"><h:outputText value="#{freeForm.presentation.name}"/></span>
				</div>
		</td>
	</tr>
</table>

<ul class="tabNav specialLink">
	<li>
		<h:commandLink action="#{freeForm.processActionSummary}" title="#{msgs.pres_summary}">
					<h:outputText value="#{msgs.pres_summary}"/> 
		</h:commandLink>
	</li>
	<li  class="selected">
		<span>
			<h:outputText value="#{msgs.pres_content}"/>
		</span>	
	</li>
	<li>
		<h:commandLink action="#{freeForm.processActionShare}" title="#{msgs.pres_share}">
					<h:outputText value="#{msgs.pres_share}"/> 
		</h:commandLink>
	</li>
</ul>	

<div class="tabNavPanel">
	<sakai:view_title value="#{msgs.add_page}" rendered="#{freeForm.presentation.newObject}"/>
	<sakai:view_title value="#{msgs.edit_page}" rendered="#{!freeForm.presentation.newObject}"/>
	
	<sakai:instruction_message value=""/>
	<sakai:messages rendered="#{!empty facesContext.maximumSeverity}"/>
	<ospx:xheader>
	<ospx:xheadertitle id="styleTitle" value="#{msgs.page_information_title}"/>
	<ospx:xheaderdrawer initiallyexpanded="#{freeForm.currentPage.expandedInformationSection}" cssclass="drawerBorder">
		<ospx:splitarea direction="horizontal" width="100%">
			<ospx:splitsection size="75%" valign="top">
				<ospx:splitarea direction="horizontal" width="100%">
					<ospx:splitsection size="80%" valign="top">
						<h:outputText  id="modifiedLabel" value="#{msgs.page_modified} "
							rendered="#{freeForm.currentPage.base.modified != null}"/>
						<h:outputFormat id="modified" value="#{msgs.date_format}"
							rendered="#{freeForm.currentPage.base.modified != null}">
							<f:param value="#{freeForm.currentPage.base.modified}"/>
						</h:outputFormat>
							<f:verbatim><br /></f:verbatim>
						<h:panelGrid styleClass="itemSummary" columns="2" border="0"  cellpadding="0" cellspacing="0" columnClasses="bogus;shorttext">
							<ospx:formLabel valueRequired="true">
								<h:outputLabel for="title" id="titleLabel" value="#{msgs.page_title}" />
							</ospx:formLabel>
							<h:panelGroup>
								<h:inputText id="title" value="#{freeForm.currentPage.base.title}" required="true">
									<f:validateLength minimum="1" maximum="255"/>
								</h:inputText>
								<h:message for="title" styleClass="alertMessageInline" style="border:none"/>
							</h:panelGroup>
							<h:outputLabel for="description" id="descriptionLabel"
										   value="#{msgs.page_description}"/>
							<h:panelGroup>
								<h:inputTextarea id="description" value="#{freeForm.currentPage.base.description}"
												 required="false">
									<f:validateLength minimum="0" maximum="255"/>
								</h:inputTextarea>
								<h:message for="description" styleClass="alertMessageInline" style="border:none"/>
							</h:panelGroup>
	
							<h:outputLabel for="keywords" id="keywordsLabel" value="#{msgs.page_keywords}"/>
							<h:panelGroup>
								<h:inputTextarea id="keywords" value="#{freeForm.currentPage.base.keywords}"
												 required="false">
									<f:validateLength minimum="0" maximum="255"/>
								</h:inputTextarea>
								<h:message for="keywords"  styleClass="alertMessageInline" style="border:none"/>
							</h:panelGroup>
	
							<ospx:formLabel valueRequired="true">
								<h:outputLabel for="layoutFile" id="layoutLabel" value="#{msgs.page_layout}"/>
							</ospx:formLabel>
							<h:panelGroup>
								<f:subview id="originalLayout" rendered="#{freeForm.currentPage.hasLayout}">
									<sakai:doc_properties>
										<h:outputLabel for="layout" id="layoutLabel"
													   value="#{msgs.original_layout}: "/>
										<h:outputText id="layout" value="#{freeForm.currentPage.base.layout.name}"/>
									</sakai:doc_properties>
								</f:subview>
								<h:inputText id="layoutFile" value="#{freeForm.currentPage.layoutName}"
											 readonly="true" rendered="#{freeForm.currentPage.renderLayoutName}"/>

								<h:inputHidden id="layoutFileHidden" value="" required="true"
									rendered="#{freeForm.currentPage.selectedLayout.base == null}" />
								<h:outputText value=" "/>
								<h:commandLink action="#{freeForm.currentPage.processActionSelectLayout}"
											   immediate="true">
									<h:outputText value="#{msgs.select_layout}"/>
								</h:commandLink>
								<h:message for="layoutFileHidden"
									styleClass="alertMessageInline" style="border:none" />
							</h:panelGroup>
							<h:outputLabel id="blank" value=""/>
							<h:panelGroup>
							<h:graphicImage id="defaultLayoutImage" height="125" width="100"
																		value="/img/page-nopreview.png"
																		rendered="#{!freeForm.currentPage.layoutPreviewImage and freeForm.currentPage.layoutSelected}"/>
							<h:graphicImage id="selectedLayoutImage" height="125" width="100"
														  value="#{freeForm.currentPage.selectedLayout.previewImage.externalUri}"
														  rendered="#{freeForm.currentPage.layoutPreviewImage}"
									/></h:panelGroup>
							<h:outputLabel for="styleFile" id="styleLabel" value="#{msgs.page_style}"/>
							<h:panelGroup>
								<h:inputText id="styleFile" value="#{freeForm.currentPage.styleName}"
											 readonly="true" required="false"/>
								<h:outputText value=" "/> 
								<h:commandLink action="#{freeForm.currentPage.processActionSelectStyle}"
											   immediate="true">
									<h:outputText value="#{msgs.select_style}"/>
								</h:commandLink>
							</h:panelGroup>
	
	
						</h:panelGrid>
					</ospx:splitsection>
				</ospx:splitarea>
			</ospx:splitsection>
		</ospx:splitarea>
	</ospx:xheaderdrawer>
	</ospx:xheader>
	<ospx:xheader>
		<ospx:xheadertitle id="styleTitle2" value="#{msgs.page_content_title}"/>
		<ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
			<f:subview id="arrange">
				<h:panelGrid columns="1" border="0">
					<sakai:instruction_message value = "#{msgs.manage_items_instructions}"/>
							<h:commandButton actionListener="#{freeForm.processActionManageItems}"
											 value="#{msgs.manage_items}"/>
						</h:panelGrid>
				<ospx:xmlDocument factory="#{freeForm.factory}"
								  xmlFile="#{freeForm.currentPage.xmlFile}"
								  var="freeForm.currentPage.regionMap" rendered ="#{freeForm.currentPage.xmlFileNotNull}"/>
			</f:subview>             
		</ospx:xheaderdrawer>
	</ospx:xheader>
	
	<f:subview id="navigation">
		<%@ include file="navigationFromPage.jspf" %>
	</f:subview>
	</div>
</h:form>

</sakai:view>
</f:view>
