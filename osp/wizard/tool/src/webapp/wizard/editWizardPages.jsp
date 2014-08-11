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
	<f:subview id="instructionsHier">
		<sakai:instruction_message value="#{msgs.wizard_pages_instructions_hier}" rendered="#{wizard.current.base.type ==
		'org.theospi.portfolio.wizard.model.Wizard.hierarchical' && !wizard.moving  && !wizard.current.base.published}"/>
		<sakai:instruction_message value="#{msgs.wizard_pages_instructions_hier_pub}" rendered="#{wizard.current.base.type ==
		'org.theospi.portfolio.wizard.model.Wizard.hierarchical' && !wizard.moving  && wizard.current.base.published}"/>
	</f:subview>
	<f:subview id="instructionsSeq" rendered="#{wizard.current.base.type ==
		'org.theospi.portfolio.wizard.model.Wizard.sequential' && !wizard.moving}">
		<sakai:instruction_message value="" />
	</f:subview>
   <f:subview id="instructionsMove" rendered="#{wizard.moving}">
      <sakai:instruction_message value="#{wizard.movingInstructions}" />
   </f:subview>
<%--   <sakai:instruction_message value=" Last saved: " />
   <sakai:instruction_message value="#{wizard.lastSavedId}" /> --%>
   <sakai:messages rendered="#{!empty facesContext.maximumSeverity}"/>

   <%-- <f:subview id="addPageBar" rendered="#{wizard.current.base.type != 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}">
   <sakai:tool_bar>
      <sakai:tool_bar_item
         action="#{wizard.current.rootCategory.processActionNewPage}"
         value="#{msgs.new_root_wizard_page_seq}" 
         rendered="#{!wizard.moving && !wizard.current.base.published}"/>
      <sakai:tool_bar_item
         rendered="#{wizard.current.base.type ==
               'org.theospi.portfolio.wizard.model.Wizard.hierarchical' && !wizard.moving && !wizard.current.base.published}"
         action="#{wizard.current.rootCategory.processActionNewCategory}"
         value="#{msgs.new_root_wizard_category}" />
   </sakai:tool_bar>
   </f:subview>--%>
	  <f:subview id="addPageBar" rendered="#{wizard.current.base.type != 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}">
	  <h:commandLink 
         action="#{wizard.current.rootCategory.processActionNewPage}"
         value="#{msgs.wizard_pages_instructions_seq}" 
         rendered="#{!wizard.moving && !wizard.current.base.published}"
		 />
      <h:commandLink
         rendered="#{wizard.current.base.type ==
               'org.theospi.portfolio.wizard.model.Wizard.hierarchical' && !wizard.moving && !wizard.current.base.published}"
         action="#{wizard.current.rootCategory.processActionNewCategory}"
         value="#{msgs.new_root_wizard_category}" />
		 
		<sakai:instruction_message value="#{msgs.wizard_pages_instructions_seq_pub}"  rendered="#{wizard.current.base.type ==
		'org.theospi.portfolio.wizard.model.Wizard.sequential' && !wizard.moving && wizard.current.base.published}" />
	 </f:subview>
  
   
     <h:dataTable value="#{wizard.current.rootCategory.categoryPageList}" var="item" styleClass="lines listHier nolines"  border="0" cellpadding="0" cellspacing="0" columnClasses="attach,nowrap,nowrap,bogus" summary="#{msgs.pages_list_summary}" rendered="#{not empty wizard.current.rootCategory.categoryPageList}">
      <h:column rendered="#{wizard.moving}">
         <f:facet name="header">
            <h:outputText value="" />
         </f:facet>
         <h:graphicImage value="/img/arrowhere.gif" rendered="#{item.moveTarget}" />
      </h:column>
	  <h:column rendered="#{not wizard.moving}">	
         <f:facet name="header">
            <h:outputText value="" />
         </f:facet>
      </h:column>
      <h:column>
         <f:facet name="header">
            <f:subview id="header">
               <h:outputText value="#{msgs.category_page_title_column_header_hier}"
               		rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}" />
               <h:outputText value="#{msgs.category_page_title_column_header_seq}"
               		rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.sequential'}"/>
            </f:subview>
         </f:facet>
         <h:outputLabel value="#{item.indentString}"
            rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}"/>

         <h:graphicImage value="/img/categoryExpanded.gif" rendered="#{item.category && item.hasChildren}" />
         <h:graphicImage value="/img/category.gif" rendered="#{item.category && !item.hasChildren}" />

         <h:graphicImage value="/img/page.gif" rendered="#{!item.category && !item.wizard}" />
         <!--h:selectBooleanCheckbox id="itemSelect" value="#{item.selected}" /-->
         <h:outputLabel value="#{item.title}"/>
         
           </h:column>
		   <h:column  rendered="#{wizard.current.base.type != 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}">
		   <f:subview id="underActions" rendered="#{wizard.current.base.type != 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}">

		 <f:verbatim><div class="itemAction"></f:verbatim>
<%--	         <h:outputLabel value="#{item.indentString}"
	            rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}"/> --%>
	         <h:commandLink action="#{item.processActionEdit}" rendered="#{!wizard.moving}">
	            <h:outputText value="#{msgs.editProperties}" />
	         </h:commandLink>
	         <h:outputText value=" | "  rendered="#{!wizard.moving && !wizard.current.base.published}"/>
	         <h:commandLink action="#{item.processActionConfirmDelete}" rendered="#{!wizard.moving && !wizard.current.base.published}">
	            <h:outputText value="#{msgs.delete}" />
	         </h:commandLink>
		         <h:outputText value=" | " rendered="#{item.category && !wizard.moving && !wizard.current.base.published}"/>
	         <h:commandLink action="#{item.processActionNewCategory}" rendered="#{item.category && !wizard.moving && !wizard.current.base.published}">
	            <h:outputText value="#{msgs.new_category}" />
	         </h:commandLink>
	         <h:outputText value=" | "  rendered="#{item.category && !wizard.moving && !wizard.current.base.published}"/>
	         <h:commandLink action="#{item.processActionNewPage}" rendered="#{item.category && !wizard.moving && !wizard.current.base.published}">
	            <h:outputText value="#{msgs.new_page}" />
	         </h:commandLink>
	
	         <h:outputText value=" | " rendered="#{!wizard.moving && !wizard.current.base.published &&
	               wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}"/>
	         <h:commandLink action="#{item.processActionMove}" rendered="#{!wizard.moving && !wizard.current.base.published &&
	               wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}">
	            <h:outputText value="#{msgs.move_category}" rendered="#{item.category}"/>
	            <h:outputText value="#{msgs.move_page}" rendered="#{!item.category}"/>
	         </h:commandLink>
		 <f:verbatim></div></f:verbatim>
		 </f:subview>
			
         <f:facet name="footer">
            <f:subview id="moveFooter" rendered="#{wizard.moving && false}">
			<h:panelGroup  styleClass="itemAction">
               <h:commandLink action="#{wizard.current.rootCategory.processActionMoveTo}"
                  rendered="#{wizard.current.rootCategory.containerForMove}">
                  <h:outputText value="#{msgs.move_to_here_category}" rendered="#{wizard.moveCategoryChild.category}"/>
                  <h:outputText value="#{msgs.move_to_here_page}" rendered="#{!wizard.moveCategoryChild.category}"/>
               </h:commandLink>
               <h:outputText value=" | " rendered="#{wizard.current.rootCategory.containerForMove}"/>
               <h:commandLink action="#{wizard.moveCategoryChild.processActionCancelMove}" rendered="#{wizard.moving}">
                  <h:outputText value="#{msgs.cancel_move}" />
               </h:commandLink>
			   </h:panelGroup>
            </f:subview>
         </f:facet>
      </h:column>
      
      
      
      
      
      
      
      
      <h:column rendered="#{(item.category && item.containerForMove) || 
      			wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}">
         <f:facet name="header">
            <h:outputText value="#{msgs.actions_column_header}" />
         </f:facet>
         
         <f:subview id="columnActions" rendered="#{!wizard.moving && wizard.current.base.type == 
         					'org.theospi.portfolio.wizard.model.Wizard.hierarchical' && !item.wizard}">
		 <h:panelGroup  styleClass="itemAction">
<%--	         <h:outputLabel value="#{item.indentString}"
	            rendered="#{!wizard.moving && wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}"/>
--%>				
	         <h:commandLink action="#{item.processActionEdit}">
	            <h:outputText value="#{msgs.editProperties}" />
	         </h:commandLink>
	         <h:outputText value=" | "  rendered="#{!wizard.moving && !wizard.current.base.published}"/>
	         <h:commandLink action="#{item.processActionConfirmDelete}" rendered="#{!wizard.moving && !wizard.current.base.published}">
	            <h:outputText value="#{msgs.delete}" />
	         </h:commandLink>
	
	         <h:outputText value=" | " rendered="#{item.category && !wizard.moving && !wizard.current.base.published}"/>
	         <h:commandLink action="#{item.processActionNewCategory}" rendered="#{item.category &&  !wizard.current.base.published}">
	            <h:outputText value="#{msgs.new_category}" />
	         </h:commandLink>
	         <h:outputText value=" | "  rendered="#{item.category && !wizard.moving && !wizard.current.base.published}"/>
	         <h:commandLink action="#{item.processActionNewPage}" rendered="#{item.category &&  !wizard.current.base.published}">
	            <h:outputText value="#{msgs.new_page}" />
	         </h:commandLink>
	
	         <h:outputText value=" | " rendered="#{!wizard.moving && !wizard.current.base.published &&
	               wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}"/>
	         <h:commandLink action="#{item.processActionMove}" rendered="#{!wizard.moving && !wizard.current.base.published &&
	               wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}">
	            <h:outputText value="#{msgs.move_category}" rendered="#{item.category}"/>
	            <h:outputText value="#{msgs.move_page}" rendered="#{!item.category}"/>
	         </h:commandLink>
			</h:panelGroup> 
		 </f:subview>
		 
         <f:subview id="columnWizardActions" rendered="#{item.wizard && !wizard.moving && !wizard.current.base.published}">
		 <h:panelGroup  styleClass="itemAction">
		      <h:commandLink
		         rendered="#{wizard.current.base.type ==
		               'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}"
		         action="#{wizard.current.rootCategory.processActionNewCategory}"
		         value="#{msgs.new_root_wizard_category}" />

		 <h:outputText value=" | " />
		      <h:commandLink
		         action="#{wizard.current.rootCategory.processActionNewPage}"
		         value="#{msgs.new_root_wizard_page}" />
			</h:panelGroup>	 
		 </f:subview>
         <f:subview id="moveIntoWizard" rendered="#{wizard.moving && item.wizard}">
		 <h:panelGroup  styleClass="itemAction">
               <h:outputText value=" | " rendered="#{!wizard.moving && wizard.current.rootCategory.containerForMove}"/>
               <h:commandLink action="#{wizard.current.rootCategory.processActionMoveTo}"
                  rendered="#{wizard.current.rootCategory.containerForMove}">
                  <h:outputText value="#{msgs.move_to_here_category}" rendered="#{wizard.moveCategoryChild.category}"/>
                  <h:outputText value="#{msgs.move_to_here_page}" rendered="#{!wizard.moveCategoryChild.category}"/>
               </h:commandLink>
               <h:outputText value=" | " rendered="#{wizard.current.rootCategory.containerForMove}"/>
               <h:commandLink action="#{wizard.moveCategoryChild.processActionCancelMove}" rendered="#{wizard.moving}">
                  <h:outputText value="#{msgs.cancel_move}" />
               </h:commandLink>
			  </h:panelGroup> 
         </f:subview>
		 <h:panelGroup  styleClass="itemAction">
         <h:outputText value=" | " rendered="#{!wizard.moving && item.category && item.containerForMove}"/>
         <h:commandLink action="#{item.processActionMoveTo}" rendered="#{item.category && item.containerForMove}">
            <h:outputText value="#{msgs.move_to_here_category}" rendered="#{wizard.moveCategoryChild.category}"/>
            <h:outputText value="#{msgs.move_to_here_page}" rendered="#{!wizard.moveCategoryChild.category}"/>
         </h:commandLink>
		 </h:panelGroup>
      </h:column>
      
      
      <h:column rendered="#{!wizard.moving && !wizard.current.base.published}">
         <f:facet name="header">
            <f:subview id="rearrangeTitle">
               <h:outputText value="#{msgs.re_order_hier}" 
            		rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}" />
               <h:outputText value="#{msgs.re_order_seq}" 
            		rendered="#{wizard.current.base.type != 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}" />
            </f:subview>
         </f:facet>
         
	     <h:outputText value="" escape="false" />
         <h:commandLink action="#{item.moveUp}" rendered="#{!item.first}">
            <h:graphicImage value="/img/arrowUp.gif" />
         </h:commandLink>
	     <f:subview id="publishLink" rendered="#{item.first}">
	        <h:outputText value="&nbsp;&nbsp;&nbsp;&nbsp;" escape="false" />
	     </f:subview>
         <h:commandLink action="#{item.moveDown}" rendered="#{!item.last}">
            <h:graphicImage value="/img/arrowDown.gif" />
         </h:commandLink>
      </h:column>
   </h:dataTable>

   <f:subview id="buttonBar" rendered="#{!wizard.moving}">
      <%@ include file="builderButtons.jspf"%>
   </f:subview>

</h:form>
</sakai:view>

</f:view>
