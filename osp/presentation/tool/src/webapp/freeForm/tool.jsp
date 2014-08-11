<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://www.theospi.org/jsf/osp" prefix="ospx" %>
<%@ taglib uri="http://www.theospi.org" prefix="osp" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>

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
		<td style="vertical-align:middle; text-align:right">

		<h:commandLink action="#{freeForm.processActionReturn}" title="#{msgs.return_to_list}">
					<h:outputText value="#{msgs.return_to_list}"/> 
		</h:commandLink> 
		|
      <a href="${freeForm.previewUrl}" target="_blank"><h:outputText value="#{msgs.pres_preview}"/></a>
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
				<sakai:instruction_message value="#{msgs.instructions_freeForm}"/>
				<sakai:messages rendered="#{!empty facesContext.maximumSeverity}"/>
		
				
		
				<f:subview id="newPage">
					<h:panelGrid columns="1000">
						<h:panelGrid columns="1000">
							<sakai:dataLine value="#{freeForm.pageList}" rows="#{freeForm.pageCount}" separator="" var="page">
								<h:column>
									<f:verbatim><td width='120' style="padding:0 .2em;text-align:center;padding-bottom:1em" ></f:verbatim>
										<h:commandLink action="#{page.moveUp}" rendered="#{page.base.sequence != 0}" title="#{msgs.move_page_left_hint}">
											<h:graphicImage value="/img/arrowLeft.png" alt="#{msgs.move_page_left_hint}" />
										</h:commandLink>
										<h:outputText value=" "/>
										<h:commandLink action="#{page.moveDown}" rendered="#{!page.last}" title="#{msgs.move_page_right_hint}">
											<h:graphicImage value="/img/arrowRight.png" alt="#{msgs.move_page_right_hint}"/>
										</h:commandLink>
										<h:commandLink action="#{page.processActionEdit}" style="display:block;text-align:center;text-decoration:none" 
											title="#{msgs.edit_page}">
											<h:graphicImage height="125" width="100"
												value="/img/page-nopreview.png"
												rendered="#{!page.layoutPreviewImage}" style="border:none;" alt="#{msgs.edit_page_img_hint}"/>
											<h:graphicImage height="125" width="100"
												value="#{page.selectedLayout.previewImage.externalUri}"
												rendered="#{page.layoutPreviewImage}" style="border:none;"/>
										</h:commandLink>
										<h:outputText value="#{page.base.title}"/>
										<f:verbatim><div style="white-space:nowrap;margin-top:1em"></f:verbatim>
										<h:commandLink action="#{page.processActionEdit}" title="#{msgs.edit_page}">
											<h:graphicImage 
												value="/img/page_edit.png"
												 style="border:none;"
												 alt="#{msgs.edit_page}"/>
										</h:commandLink>
										<h:outputText value=" | "/> 
										<h:commandLink action="#{page.processActionConfirmDelete}"  title="#{msgs.remove_page}">
											<h:graphicImage 
												value="/img/page_delete.png"
												 style="border:none;"
												 alt="#{msgs.remove_page}"/>
										</h:commandLink>
									<f:verbatim></div></f:verbatim>	
									<f:verbatim></td></f:verbatim>
								</h:column>
							</sakai:dataLine>
						</h:panelGrid>
						<h:panelGroup>
							<h:commandLink action="#{freeForm.processActionNewPage}" style="text-decoration:none !important" title="#{msgs.add_page}">
								<h:graphicImage id="addPageImage" value="/img/page_add.png" alt="#{msgs.add_page}"/>
								<h:outputText value="  "/> 
								<h:outputText value="#{msgs.add_page}"/>
							</h:commandLink>
						</h:panelGroup>
					</h:panelGrid>
				</f:subview>
											  
				<h:panelGrid columns="3" cellspacing="1" styleClass="jsfFormTable" columnClasses="shorttext,bogus" border="0">
					<h:outputLabel for="layoutFile" id="layoutLabel" value="#{msgs.page_layout}"/>
					<h:panelGroup>
						<h:inputText id="layoutFile" value="#{freeForm.layoutName}"
							readonly="true" required="false"/>
						<h:outputText value=" "/>
						<h:commandLink action="#{freeForm.processActionSelectLayout}" immediate="true">
							<h:outputText value="#{msgs.select_layout}"/>
						</h:commandLink>
					</h:panelGroup>	
				</h:panelGrid>
				<%-- gsilver: really need below? --%>
				<h:outputLabel id="blank" value=""/>
					
				<h:panelGroup>
					<h:graphicImage id="defaultLayoutImage" height="125" width="100"
						value="/img/page-nopreview.png"
						rendered="#{!freeForm.layoutPreviewImage and freeForm.layoutSelected}"/>
					<h:graphicImage id="selectedLayoutImage" height="125" width="100"
						value="#{freeForm.previewImage.externalUri}"
						rendered="#{freeForm.layoutPreviewImage}"/>
				</h:panelGroup>
			
				<h:panelGrid columns="3" cellspacing="1" styleClass="jsfFormTable" columnClasses="shorttext,bogus" border="0">
					<h:outputLabel for="styleFile" id="styleLabel" value="#{msgs.page_style}"/>
					<h:panelGroup>
						<h:inputText id="styleFile" value="#{freeForm.styleName}"
							readonly="true" required="false"/>
						<h:outputText value=" "/>
						<h:commandLink action="#{freeForm.processActionSelectStyle}" immediate="true">
							<h:outputText value="#{msgs.select_style}"/>
						</h:commandLink>
					</h:panelGroup>	
				</h:panelGrid>
			
				<h:panelGrid id="advNavGrid" columns="1" style="margin-top:1em">
					<h:panelGroup id="advNavGrp">
						<h:selectBooleanCheckbox disabled="true"
							value="#{freeForm.presentation.advancedNavigation}" rendered="#{freeForm.pageCount == 0}"/>
						<h:selectBooleanCheckbox id="advancedNavigation"
							value="#{freeForm.presentation.advancedNavigation}" rendered="#{freeForm.pageCount > 0}"/>
						<h:outputLabel for="advancedNavigation" id="advancedNavigationLabel"
							value="#{msgs.advanced_navigation}" />
						<h:outputText value=" "/>
						<h:outputText id="advancedNavInstr" value="#{msgs.advanced_navigation_disclaimer}" styleClass="instruction"/>
					</h:panelGroup>
				</h:panelGrid>
				<f:subview id="navigation">
					<%@ include file="navigation.jspf" %>
				</f:subview>
			</div>	
		</h:form>
	</sakai:view>
</f:view>
