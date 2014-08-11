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

   <sakai:tool_bar>
      <sakai:tool_bar_item
         action="main"
         value="#{msgs.wizard_list}" />
      <sakai:tool_bar_item
         rendered="#{wizard.current.runningWizard.base.status == 'READY' &&
            wizard.current.base.type ==
               'org.theospi.portfolio.wizard.model.Wizard.hierarchical' &&
            wizard.current.base.owner.id.value == wizard.currentUserId}"
         action="confirmSubmit"
         value="#{msgs.submit_wizard}" />
      <sakai:tool_bar_item
         rendered="#{wizard.canReview &&
            wizard.current.base.reviewDevice != null &&
            wizard.current.base.reviewDevice.value != ''}"
         action="#{wizard.processActionReview}"
         value="#{msgs.review_wizard}" />
      <sakai:tool_bar_item
         rendered="#{wizard.current.runningWizard.base.status == 'PENDING' &&
            wizard.canEvaluate &&
            wizard.current.base.evaluationDevice != null &&
            wizard.current.base.evaluationDevice.value != ''}"
         action="#{wizard.processActionEvaluate}"
         value="#{msgs.eval_wizard}" />
   </sakai:tool_bar>

   <sakai:view_title value="#{msgs.run_wizard}"/>

   <%@ include file="showWizardOwnerMessage.jspf"%>
   
   <f:subview id="instructionsHier" rendered="#{wizard.current.base.type ==
               'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}">
      <sakai:instruction_message value="#{msgs.run_wizard_pages_instructions_hier}" />
   </f:subview>
   <f:subview id="instructionsSeq" rendered="#{wizard.current.base.type ==
               'org.theospi.portfolio.wizard.model.Wizard.sequential'}">
      <sakai:instruction_message value="#{msgs.run_wizard_pages_instructions_seq}" />
   </f:subview>

   <sakai:messages rendered="#{!empty facesContext.maximumSeverity}"/>

   <%@ include file="wizardGuidance.jspf"%>

   <sakai:flat_list value="#{wizard.current.runningWizard.rootCategory.categoryPageList}" var="item">
      <h:column>
         <f:facet name="header">
            <f:subview id="header">
               <h:outputText value="#{msgs.category_page_title_column_header_hier}"
               		rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}" />
               <h:outputText value="#{msgs.category_page_title_column_header_seq}"
               		rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.sequential'}"/>
            </f:subview>
         </f:facet>
         <h:outputLabel value="#{item.categoryChild.indentString}"
            rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}"/>
         <f:subview id="expander" rendered="#{item.categoryChild.category}">
            <h:commandLink action="#{item.processActionExpandToggle}">
               <h:graphicImage value="/img/categoryExpanded.gif" rendered="#{item.base.expanded}" />
               <h:graphicImage value="/img/category.gif" rendered="#{!item.base.expanded}" />
            </h:commandLink>
         </f:subview>
         <h:graphicImage value="/img/page.gif" rendered="#{!item.categoryChild.category}" />
         <h:outputLabel value="#{item.categoryChild.title}"/>
      </h:column>
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.actions_column_header}" />
         </f:facet>
         <h:commandLink action="#{item.processActionEdit}"
                  rendered="#{!item.categoryChild.category}">
            <h:outputText value="#{msgs.view_page}"/>
         </h:commandLink>
      </h:column>
      <h:column>
         <f:facet name="header">
            <h:outputText value="#{msgs.page_stats_header}" />
         </f:facet>
      </h:column>
   </sakai:flat_list>

   <ospx:xheader rendered="#{not empty wizard.current.runningWizard.reviews}">
      <ospx:xheadertitle id="wizardReviews" value="#{msgs.wizard_reviews}" />
      <ospx:xheaderdrawer initiallyexpanded="false" cssclass="drawerBorder">

         <sakai:flat_list value="#{wizard.current.runningWizard.reviews}" var="review">
            <h:column>
               <f:facet name="header">
                  <h:outputText value="#{msgs.wizard_eval_name}" />
               </f:facet>
               <h:outputLink value="#{review.reviewContentNode.externalUri}" target="_blank">
                  <h:outputText value="#{review.reviewContentNode.displayName}" />
               </h:outputLink>               
            </h:column>
            <h:column>
               <f:facet name="header">
                  <h:outputText value="#{msgs.wizard_eval_owner}" />
               </f:facet>
               <h:outputText value="#{review.reviewContentNode.technicalMetadata.owner.displayName}" />
            </h:column>
            <h:column>
               <f:facet name="header">
                  <h:outputText value="#{msgs.wizard_eval_date}" />
               </f:facet>
               <h:outputText value="#{review.reviewContentNode.technicalMetadata.creation}" />
            </h:column>
         </sakai:flat_list>
      </ospx:xheaderdrawer>
  </ospx:xheader>
     
   <ospx:xheader rendered="#{not empty wizard.current.runningWizard.evaluations}">
      <ospx:xheadertitle id="wizardEvals" value="#{msgs.wizard_evals}" />
      <ospx:xheaderdrawer initiallyexpanded="false" cssclass="drawerBorder">

         <sakai:flat_list value="#{wizard.current.runningWizard.evaluations}" var="eval">
            <h:column>
               <f:facet name="header">
                  <h:outputText value="#{msgs.wizard_eval_name}" />
               </f:facet>
               <h:outputLink value="#{eval.reviewContentNode.externalUri}" target="_blank">
                  <h:outputText value="#{eval.reviewContentNode.displayName}" />
               </h:outputLink>
            </h:column>
            <h:column>
               <f:facet name="header">
                  <h:outputText value="#{msgs.wizard_eval_owner}" />
               </f:facet>
               <h:outputText value="#{eval.reviewContentNode.technicalMetadata.owner.displayName}" />
            </h:column>
            <h:column>
               <f:facet name="header">
                  <h:outputText value="#{msgs.wizard_eval_date}" />
               </f:facet>
               <h:outputText value="#{eval.reviewContentNode.technicalMetadata.creation}" />
            </h:column>
         </sakai:flat_list>
      </ospx:xheaderdrawer>
  </ospx:xheader>

</h:form>
</sakai:view>

</f:view>
