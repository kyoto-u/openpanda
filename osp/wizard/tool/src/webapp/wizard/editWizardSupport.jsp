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
<link href="/osp-common-tool/css/eport.css" type="text/css" rel="stylesheet" media="all" />
   <sakai:view_title value="#{msgs.add_wizard} #{msgs.org_theospi_portfolio_wizard_model_Wizard_hierarchical}" rendered="#{wizard.current.base.type ==
               'org.theospi.portfolio.wizard.model.Wizard.hierarchical' && wizard.current.newWizard}"/>
   <sakai:view_title value="#{msgs.edit_wizard} #{msgs.org_theospi_portfolio_wizard_model_Wizard_hierarchical}" rendered="#{wizard.current.base.type ==
               'org.theospi.portfolio.wizard.model.Wizard.hierarchical' && !wizard.current.newWizard}"/>
   <sakai:view_title value="#{msgs.add_wizard} #{msgs.org_theospi_portfolio_wizard_model_Wizard_sequential}"  rendered="#{wizard.current.base.type !=
               'org.theospi.portfolio.wizard.model.Wizard.hierarchical' && wizard.current.newWizard}"/>
   <sakai:view_title value="#{msgs.edit_wizard} #{msgs.org_theospi_portfolio_wizard_model_Wizard_sequential}"  rendered="#{wizard.current.base.type !=
               'org.theospi.portfolio.wizard.model.Wizard.hierarchical' && !wizard.current.newWizard}"/>
    
               
   <%@ include file="steps.jspf"%>
   <sakai:instruction_message value="#{msgs.wizard_instruction_message}" />
   <sakai:messages rendered="#{!empty facesContext.maximumSeverity}"/>
	  
	<fieldset class="fieldsetVis">
	<legend>
      	<h:outputText value="#{msgs.guidance_title}"/>
   </legend>
   <h4>
	<h:outputText value="#{msgs.instruction_title}"/>
	</h4>
	<p class="indnt1">
    <h:commandLink id="addInstructions" value="#{msgs.guidance_instructions_add}"
    action="#{wizard.current.processActionEditInstructions}" rendered="#{(empty wizard.current.guidanceInstructions) && (empty wizard.current.guidanceInstructionsAttachments) }" />
	 <h:outputText  value="#{msgs.guidance_instructions_empty_msg}" rendered="#{empty wizard.current.guidanceInstructions && empty wizard.current.guidanceInstructionsAttachments}" styleClass="instruction" />
	 </p>
	 
	 <h:panelGrid columns="1"  styleClass="listHier bordered-l" cellpadding="0" cellspacing="0"  border="0" summary="" style="width:70%" rendered="#{not empty wizard.current.guidanceInstructions || not empty wizard.current.guidanceInstructionsAttachments}"
	 	headerClass="itemAction  wizardSupportTableHead">
   	<f:facet name="header">
		   <h:commandLink id="editInstructions" value="#{msgs.guidance_instructions_revise}"
			  action="#{wizard.current.processActionEditInstructions}"/>
	</f:facet>
	<h:panelGroup>
	     		<h:outputText value="#{wizard.current.instruction.text}" escape="false" />
            <sakai:flat_list value="#{wizard.current.guidanceInstructionsAttachments}" var="attachment" rendered="#{not empty wizard.current.guidanceInstructionsAttachments}">
               <h:column>
               		<sakai:contentTypeMap fileType="#{attachment.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
                     <h:graphicImage id="instrFileIcon" value="#{imagePath}" alt="#{attachment.displayName}" title="#{attachment.displayName}" />
                     <h:outputLink title="#{attachment.displayName}"
                        value="#{attachment.fullReference.base.url}" target="_blank">
                        <h:outputText value="#{attachment.displayName}"/>
                     </h:outputLink>
                        <h:outputText value=" (#{attachment.contentLength})"/>
               </h:column>
            </sakai:flat_list>
		</h:panelGroup>
	</h:panelGrid>	

   <h4>
	<h:outputText value="#{msgs.guidance_rationale}"/>
	</h4>
	<p class="indnt1">
		<h:commandLink  id="addRationale" value="#{msgs.guidance_rationale_add}"
			 action="#{wizard.current.processActionEditRationale}" rendered="#{empty wizard.current.guidanceRationale && empty wizard.current.guidanceRationaleAttachments}" />
		 <h:outputText  value="#{msgs.guidance_rationale_empty_msg}" rendered="#{empty wizard.current.guidanceRationale && empty  wizard.current.guidanceRationaleAttachments}" styleClass="instruction"/>
		</p> 
	
	<h:panelGrid columns="1"  styleClass="listHier bordered-l" cellpadding="0" cellspacing="0"  border="0" summary="" style="width:70%" rendered="#{(not empty wizard.current.guidanceRationale) || (not empty wizard.current.guidanceRationaleAttachments)}"
		 	headerClass="itemAction  wizardSupportTableHead">
			<f:facet name="header">
				<h:commandLink id="editRationale" value="#{msgs.guidance_rationale_revise}"
     	 	      action="#{wizard.current.processActionEditRationale}"/>
			</f:facet> 
			<h:panelGroup>
					<h:outputText value="#{wizard.current.rationale.text}" escape="false"/>
            <sakai:flat_list value="#{wizard.current.guidanceRationaleAttachments}" var="attachment" rendered="#{not empty wizard.current.guidanceRationaleAttachments}">
               <h:column>
               		<sakai:contentTypeMap fileType="#{attachment.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
                     <h:graphicImage id="rationaleFileIcon" value="#{imagePath}" alt="#{attachment.displayName}" title="#{attachment.displayName}" />
                     <h:outputLink title="#{attachment.displayName}"
                        value="#{attachment.fullReference.base.url}" target="_new">
                        <h:outputText value="#{attachment.displayName}"/>
                     </h:outputLink>
                        <h:outputText value=" (#{attachment.contentLength})"/>
               </h:column>
            </sakai:flat_list>
			</h:panelGroup>
</h:panelGrid>			
   <h4>
	<h:outputText value="#{msgs.guidance_examples}"/>
	</h4>
	<p class="indnt1">
	   <h:commandLink   id="addExamples" value="#{msgs.guidance_examples_add}"
		  action="#{wizard.current.processActionEditExamples}" 
		rendered="#{empty wizard.current.guidanceExamples && empty wizard.current.guidanceExamplesAttachments}" />
			<h:outputText  value="#{msgs.guidance_examples_empty_msg}" 
		rendered="#{empty wizard.current.guidanceExamples && empty wizard.current.guidanceExamplesAttachments}" styleClass="instruction"/>
</p>
	<h:panelGrid columns="1"  styleClass="listHier bordered-l" cellpadding="0" cellspacing="0"  border="0" summary="" style="width:70%"  rendered="#{not empty wizard.current.guidanceExamples || not empty wizard.current.guidanceExamplesAttachments}"
		 	headerClass="itemAction  wizardSupportTableHead">
			<f:facet name="header">
			
				   <h:commandLink   id="editExamples" value="#{msgs.guidance_examples_revise}"
					  action="#{wizard.current.processActionEditExamples}"/>
				</f:facet>
				<h:panelGroup>	
					<h:outputText value="#{wizard.current.example.text}" escape="false"/>
	
					<sakai:flat_list value="#{wizard.current.guidanceExamplesAttachments}" var="attachment" rendered="#{not empty wizard.current.guidanceExamplesAttachments}">
					   <h:column>
							<sakai:contentTypeMap fileType="#{attachment.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
							 <h:graphicImage id="exampleFileIcon" value="#{imagePath}" alt="#{attachment.displayName}" title="#{attachment.displayName}" />
							 <h:outputLink title="#{attachment.displayName}"
								value="#{attachment.fullReference.base.url}" target="_new">
								<h:outputText value="#{attachment.displayName}"/>
							 </h:outputLink>
								<h:outputText value=" (#{attachment.contentLength})"/>
					   </h:column>
					</sakai:flat_list>
				</h:panelGroup>
			</h:panelGrid>

			<h4><h:outputText value="#{msgs.guidance_rubric}" /></h4>
			<p class="indnt1"><h:commandLink id="addRubric"
				value="#{msgs.guidance_rubric_add}"
				action="#{wizard.current.processActionEditRubric}"
				rendered="#{empty wizard.current.guidanceRubric && empty wizard.current.guidanceRubricAttachments}" />
			<h:outputText value="#{msgs.guidance_rubric_empty_msg}"
				rendered="#{empty wizard.current.guidanceRubric && empty  wizard.current.guidanceRubricAttachments}"
				styleClass="instruction" /></p>

			<h:panelGrid columns="1" styleClass="listHier bordered-l"
				cellpadding="0" cellspacing="0" border="0" summary=""
				style="width:70%"
				rendered="#{(not empty wizard.current.guidanceRubric) || (not empty wizard.current.guidanceRubricAttachments)}"
				headerClass="itemAction  wizardSupportTableHead">
				<f:facet name="header">
					<h:commandLink id="editRubric"
						value="#{msgs.guidance_rubric_revise}"
						action="#{wizard.current.processActionEditRubric}" />
				</f:facet>
				<h:panelGroup>
					<h:outputText value="#{wizard.current.rubric.text}" escape="false" />
					<sakai:flat_list
						value="#{wizard.current.guidanceRubricAttachments}"
						var="attachment"
						rendered="#{not empty wizard.current.guidanceRubricAttachments}">
						<h:column>
							<sakai:contentTypeMap fileType="#{attachment.mimeType.value}"
								mapType="image" var="imagePath" pathPrefix="/library/image/" />
							<h:graphicImage id="rubricFileIcon" value="#{imagePath}"
								alt="#{attachment.displayName}"
								title="#{attachment.displayName}" />
							<h:outputLink title="#{attachment.displayName}"
								value="#{attachment.fullReference.base.url}" target="_new">
								<h:outputText value="#{attachment.displayName}" />
							</h:outputLink>
							<h:outputText value=" (#{attachment.contentLength})" />
						</h:column>
					</sakai:flat_list>
				</h:panelGroup>
			</h:panelGrid>


			<h4><h:outputText value="#{msgs.guidance_expectations}" /></h4>
			<p class="indnt1"><h:commandLink id="addExpectations"
				value="#{msgs.guidance_expectations_add}"
				action="#{wizard.current.processActionEditExpectations}"
				rendered="#{empty wizard.current.guidanceExpectations && empty wizard.current.guidanceExpectationsAttachments}" />
			<h:outputText value="#{msgs.guidance_expectations_empty_msg}"
				rendered="#{empty wizard.current.guidanceExpectations && empty  wizard.current.guidanceExpectationsAttachments}"
				styleClass="instruction" /></p>

			<h:panelGrid columns="1" styleClass="listHier bordered-l"
				cellpadding="0" cellspacing="0" border="0" summary=""
				style="width:70%"
				rendered="#{(not empty wizard.current.guidanceExpectations) || (not empty wizard.current.guidanceExpectationsAttachments)}"
				headerClass="itemAction  wizardSupportTableHead">
				<f:facet name="header">
					<h:commandLink id="editExpectations"
						value="#{msgs.guidance_expectations_revise}"
						action="#{wizard.current.processActionEditExpectations}" />
				</f:facet>
				<h:panelGroup>
					<h:outputText value="#{wizard.current.expectations.text}" escape="false" />
					<sakai:flat_list
						value="#{wizard.current.guidanceExpectationsAttachments}"
						var="attachment"
						rendered="#{not empty wizard.current.guidanceExpectationsAttachments}">
						<h:column>
							<sakai:contentTypeMap fileType="#{attachment.mimeType.value}"
								mapType="image" var="imagePath" pathPrefix="/library/image/" />
							<h:graphicImage id="expectationsFileIcon" value="#{imagePath}"
								alt="#{attachment.displayName}"
								title="#{attachment.displayName}" />
							<h:outputLink title="#{attachment.displayName}"
								value="#{attachment.fullReference.base.url}" target="_new">
								<h:outputText value="#{attachment.displayName}" />
							</h:outputLink>
							<h:outputText value=" (#{attachment.contentLength})" />
						</h:column>
					</sakai:flat_list>
				</h:panelGroup>
			</h:panelGrid>
			
		</fieldset>
		 
		 <fieldset class="fieldsetVis">
		 	<legend>User forms</legend>
			<h:panelGrid columns="1">
				<h:column>
					<sakai:instruction_message value="#{msgs.com_ref_instruction}" />
					<h:panelGroup styleClass="shorttext">
						<h:outputLabel for="reflectionItems" id="reflectionLabel" value="#{msgs.reflection_item}" />
						<h:selectOneMenu id="reflectionItems"
							immediate="true" 
							disabled="#{not empty wizard.reflectionItem && wizard.current.isWizardUsed}"
							value="#{wizard.reflectionItem}">
							<f:selectItem itemLabel="#{msgs.choose_reflection_item}" itemValue=""/>
							<f:selectItems value="#{wizard.reflectionFormsForSelect}"/>
						</h:selectOneMenu>
					</h:panelGroup>
				</h:column>
			</h:panelGrid>
		</fieldset>	
		<fieldset class="fieldsetVis">
		 	<legend>Feedback and Evaluation</legend>
			<h:panelGrid columns="1">
				<h:column>
						
					<sakai:instruction_message value="#{msgs.com_feedb_instruction}" />
					<h:panelGroup styleClass="shorttext">
						<h:outputLabel for="commentItems" id="commentLabel" value="#{msgs.comment_item}" />
						<h:selectOneMenu id="commentItems"
							immediate="true" 
							disabled="#{not empty wizard.commentItem && wizard.current.isWizardUsed}"
							value="#{wizard.commentItem}">
							<f:selectItem itemLabel="#{msgs.choose_comment_item}" itemValue=""/>
							<f:selectItems value="#{wizard.commentFormsForSelect}"/>
						</h:selectOneMenu>
					</h:panelGroup>

					<sakai:instruction_message value="#{msgs.eval_instruction}" />
					<h:panelGroup styleClass="shorttext">
					<h:outputLabel for="evaluationItems" id="evaluationLabel" value="#{msgs.evaluation_item}" />
					<h:selectOneMenu id="evaluationItems"
						immediate="true" 
						disabled="#{not empty wizard.evaluationItem && wizard.current.isWizardUsed}"
						value="#{wizard.evaluationItem}">
						<f:selectItem itemLabel="#{msgs.choose_evaluation_item}" itemValue=""/>
						<f:selectItems value="#{wizard.evaluationFormsForSelect}"/>
					</h:selectOneMenu>
					</h:panelGroup>
				</h:column>
			</h:panelGrid>
			<h4>
				<h:outputText value="#{msgs.audience_title}"/>
			</h4>
			<p class="indnt1">
            <f:subview id="moveFooter" rendered="#{empty wizard.current.evaluators}">
          		 	<h:commandLink id="selectEvaluators" value="#{msgs.select_reviewers}"
     	 	      action="#{wizard.processActionAudienceHelper}"/>
				  <h:outputText value="#{msgs.no_evaluators}"  styleClass="instruction"/>
            </f:subview>
			<f:subview id="evaluatersPresent" rendered="#{not empty wizard.current.evaluators}">
				<h:commandLink id="selectEvaluators" value="#{msgs.select_reviewers_edit}"
				action="#{wizard.processActionAudienceHelper}"/>
            </f:subview>                                                                                                                                                  

			
			</p>
			<sakai:flat_list value="#{wizard.current.evaluators}" var="evaluator" rendered="#{not empty wizard.current.evaluators}" style="width:70%;margin-left:1em">
               <h:column>
               		<h:outputText value="#{evaluator}" />
               </h:column>
            </sakai:flat_list>
			</fieldset>
	   
   <%@ include file="builderButtons.jspf"%>
   
</h:form>
</sakai:view>

</f:view>
