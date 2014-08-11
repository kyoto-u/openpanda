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
<h:form id="runWizardGuidance">
	<style type="text/css">
		.wizard-COMPLETE { background-color: #a6c7ea;}
		.wizard-PENDING { background-color: #f7ef84;}
		.wizard-READY { background-color: #86f283;}
		.wizard-LOCKED { background-color: #ac326b;}
		.wizard-RETURNED { background-color: #6633CC;}
	</style>

	<sakai:tool_bar  rendered="#{wizard.canCreate}">
      <sakai:tool_bar_item
      action="manageWizardStatus"
      value="#{msgs.manage_wizard_status}" />
    </sakai:tool_bar>

   <h:outputText value="#{msgs.wizard_preview_title}" styleClass="information" rendered="#{wizard.current.base.preview}"/>

   <%-- TODO: munge these strings- want the pattern as in matrices: View: "Wizard title" (READ ONLY): Selected user name --%>
   <h3>
   <h:outputText value="#{wizard.current.base.name}"/>:
	<%@ include file="showWizardOwnerMessage.jspf"%>
   </h3>
   <sakai:messages rendered="#{!empty facesContext.maximumSeverity}"/>
   
   <h:outputText value="#{wizard.lastSavePage} #{msgs.page_was_submitted}" styleClass="success" rendered="#{wizard.lastSavePage != ''}" />
   <h:outputText value="#{msgs.changes_saved}" styleClass="success" rendered="#{wizard.pageSaved}" />
   
   <sakai:instruction_message value="#{wizard.current.base.description}" rendered="#{not empty wizard.current.base.description}"/>  
	
	   <p><h:outputText value="#{msgs.users_unavailable}" styleClass="instruction" rendered="#{(wizard.canEvaluateTool || wizard.canReviewTool) && (wizard.current.base.published || wizard.current.base.preview) && empty wizard.current.userListForSelect}"/></p>
		
      <h:panelGrid columns="1" width="100%" border="0">
	  	  <h:panelGroup>
			   <f:subview id="viewGroups" rendered="#{(wizard.canEvaluateTool || wizard.canReviewTool) && (wizard.current.base.published || wizard.current.base.preview) && not empty wizard.current.groupListForSelect}"> <%-- tbd: handle groupList == 1 --%>
					<h:outputLabel for ="groups" value="#{msgs.wizard_select_group}" />
					<h:outputText value=" "/>
					<h:selectOneMenu id="groups" immediate="true" value="#{wizard.currentGroupId}" valueChangeListener="#{wizard.current.processActionFilterGroup}" onchange="this.form.submit();">
						<f:selectItem itemLabel="#{msgs.wizard_groups_showall}" itemValue=""/>
						<f:selectItems value="#{wizard.current.groupListForSelect}"/>
					</h:selectOneMenu>
				</f:subview>
				<f:verbatim>&nbsp;&nbsp;&nbsp;</f:verbatim>
				<f:subview id="viewUsers" rendered="#{(wizard.canEvaluateTool || wizard.canReviewTool) && (wizard.current.base.published || wizard.current.base.preview) && not empty wizard.current.userListForSelect}">
					<h:outputLabel for ="users" value="#{msgs.wizard_select_user}" />
					<h:outputText value=" "/>
					<h:selectOneMenu id="users" immediate="true" value="#{wizard.currentUserId}" valueChangeListener="#{wizard.current.processActionChangeUser}" onchange="this.form.submit();">
						<f:selectItems value="#{wizard.current.userListForSelect}"/>
					</h:selectOneMenu>
			   </f:subview>
			</h:panelGroup>
		</h:panelGrid>

	<f:subview id="status" rendered="#{wizard.current.runningWizard.base.status != 'READY' && wizard.current.runningWizard.base.status != 'RETURNED'}">
		<f:verbatim><div class="information"></f:verbatim>
	            <h:outputText value="#{wizard.statusMessage}"/>
		<f:verbatim></div></f:verbatim>
   </f:subview>
   
   <f:subview id="instructionSV" rendered="#{(wizard.current.instruction.text != null and wizard.current.instruction.text != '' and wizard.current.instruction != null) || not empty wizard.current.instruction.attachments}">
	<h4>	
	<h:outputText value="#{msgs.guidance_instructions}" />
	</h4>
  		 	
   		<div class="textPanel indnt2"><h:outputText value="#{wizard.current.instruction.text}" escape="false" /></div>
   </f:subview>
   <h:dataTable value="#{wizard.current.guidanceInstructionsAttachments}" var="attachment"  rendered="#{not empty wizard.current.instruction.attachments}" border="0" styleClass="indnt2" style="width:50%"
   summary="#{msgs.guidance_instructions_attlist_summary}">
      <h:column>
      <sakai:contentTypeMap fileType="#{attachment.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
      <h:graphicImage id="instrFileIcon" value="#{imagePath}" alt="#{attachment.displayName}" title="#{attachment.displayName}" />
         <h:outputText value=" "/><h:outputLink title="#{attachment.displayName}"
         value="#{attachment.fullReference.base.url}" target="_blank">
		 <h:outputText value="#{attachment.displayName}"/>
      </h:outputLink>
      <h:outputText value=" (#{attachment.contentLength})" styleClass="textPanelFooter"/>
      </h:column>
   </h:dataTable>

   
   <f:subview id="guidanceSV" rendered="#{(wizard.current.rationale.text != null and wizard.current.rationale.text != '' and wizard.current.rationale != null) || not empty wizard.current.rationale.attachments}">
   		<h4>
			<h:outputText value="#{msgs.guidance_rationale}" />
		</h4>	
   		<div class="textPanel indnt2"><h:outputText value="#{wizard.current.rationale.text}" escape="false" /></div>
  	</f:subview>  
	
	
   <h:dataTable value="#{wizard.current.guidanceRationaleAttachments}" var="attachment"  rendered="#{not empty wizard.current.rationale.attachments}"  border="0" styleClass="indnt2" style="width:50%"
   summary="#{msgs.guidance_rationale_attlist_summary}">
      <h:column>
      <sakai:contentTypeMap fileType="#{attachment.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
      <h:graphicImage id="rationaleFileIcon" value="#{imagePath}" alt="#{attachment.displayName}" title="#{attachment.displayName}" />
      <h:outputText value=" "/><h:outputLink title="#{attachment.displayName}"
         value="#{attachment.fullReference.base.url}" target="_blank">
         <h:outputText value="#{attachment.displayName}"/>
      </h:outputLink>
      <h:outputText value=" (#{attachment.contentLength})"  styleClass="textPanelFooter"/>
      </h:column>
   </h:dataTable>
   
   <f:subview id="exapmleSV" rendered="#{(wizard.current.example.text != null and wizard.current.example.text != '' and wizard.current.example != null) || not empty wizard.current.example.attachments}">
		<h4>
				<h:outputText value="#{msgs.guidance_examples}" />
		</h4>		
   	  <div class="textPanel indnt2"><h:outputText value="#{wizard.current.example.text}" escape="false" /></div>
   </f:subview> 
   <h:dataTable value="#{wizard.current.guidanceExamplesAttachments}" var="attachment" border="0" styleClass="indnt2" style="width:50%" rendered="#{not empty wizard.current.example.attachments}" 
   		summary="#{msgs.guidance_examples_attlist_summary}">
      <h:column>
      <sakai:contentTypeMap fileType="#{attachment.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
      <h:graphicImage id="exampleFileIcon" value="#{imagePath}" alt="#{attachment.displayName}" title="#{attachment.displayName}" />
      <h:outputText value=" "/><h:outputLink title="#{attachment.displayName}"
         value="#{attachment.fullReference.base.url}" target="_blank">
         <h:outputText value="#{attachment.displayName}"/>
      </h:outputLink>
      <h:outputText value=" (#{attachment.contentLength})"  styleClass="textPanelFooter"/>
      </h:column>
   </h:dataTable>
   
   
    <f:subview id="rubricSV" rendered="#{(wizard.current.rubric.text != null and wizard.current.rubric.text != '' and wizard.current.rubric != null) || not empty wizard.current.rubric.attachments}">
		<h4>
				<h:outputText value="#{msgs.guidance_rubric}" />
		</h4>		
   	  <div class="textPanel indnt2"><h:outputText value="#{wizard.current.rubric.text}" escape="false" /></div>
   </f:subview> 
   <h:dataTable value="#{wizard.current.guidanceRubricAttachments}" var="attachment" border="0" styleClass="indnt2" style="width:50%" rendered="#{not empty wizard.current.rubric.attachments}" 
   		summary="#{msgs.guidance_rubric_attlist_summary}">
      <h:column>
      <sakai:contentTypeMap fileType="#{attachment.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
      <h:graphicImage id="rubricFileIcon" value="#{imagePath}" alt="#{attachment.displayName}" title="#{attachment.displayName}" />
      <h:outputText value=" "/><h:outputLink title="#{attachment.displayName}"
         value="#{attachment.fullReference.base.url}" target="_blank">
         <h:outputText value="#{attachment.displayName}"/>
      </h:outputLink>
      <h:outputText value=" (#{attachment.contentLength})"  styleClass="textPanelFooter"/>
      </h:column>
   </h:dataTable>
   
    <f:subview id="expectationsSV" rendered="#{(wizard.current.expectations.text != null and wizard.current.expectations.text != '' and wizard.current.expectations != null) || not empty wizard.current.expectations.attachments}">
		<h4>
				<h:outputText value="#{msgs.guidance_expectations}" />
		</h4>		
   	  <div class="textPanel indnt2"><h:outputText value="#{wizard.current.expectations.text}" escape="false" /></div>
   </f:subview> 
   <h:dataTable value="#{wizard.current.guidanceExpectationsAttachments}" var="attachment" border="0" styleClass="indnt2" style="width:50%" rendered="#{not empty wizard.current.expectations.attachments}" 
   		summary="#{msgs.guidance_expectations_attlist_summary}">
      <h:column>
      <sakai:contentTypeMap fileType="#{attachment.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
      <h:graphicImage id="expectationsFileIcon" value="#{imagePath}" alt="#{attachment.displayName}" title="#{attachment.displayName}" />
      <h:outputText value=" "/><h:outputLink title="#{attachment.displayName}"
         value="#{attachment.fullReference.base.url}" target="_blank">
         <h:outputText value="#{attachment.displayName}"/>
      </h:outputLink>
      <h:outputText value=" (#{attachment.contentLength})"  styleClass="textPanelFooter"/>
      </h:column>
   </h:dataTable>
 
    <f:subview id="thePagesCat" >
	
   <h:dataTable value="#{wizard.current.runningWizard.rootCategory.categoryPageList}" var="item" styleClass="listHier lines nolines" summary="#{msgs.wizard_page_list_summary}" columnClasses="nowrap,nowrap,bogus">
   
     <h:column>
     	 <f:facet name="header">
            <f:subview id="header">
               <h:outputText value="#{msgs.pages}" rendered="#{wizard.current.base.type != 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}"/> 
			   <h:outputText value="#{msgs.pages_categ}" rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}"/> 
            </f:subview>
         </f:facet>
     	<f:subview id="categoryView" rendered="#{item.classInfo == 'completedCategory'}" >
         <h:outputLabel value="#{item.category.indentString}"
            rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}"/>

         <h:graphicImage value="/img/categoryExpanded.gif" rendered="#{item.category.category && item.category.hasChildren}" />
         <h:graphicImage value="/img/category.gif" rendered="#{item.category.category && !item.category.hasChildren}" />

         <h:outputText value="#{item.category.title}" rendered="#{item.category.category || item.category.wizard}" />

       </f:subview>
         
         
      <f:subview id="pageView" rendered="#{item.classInfo == 'completedPage'}" >
         <h:outputLabel value="#{item.page.indentString}"
            rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}"/>

         <h:graphicImage value="/img/page.gif" rendered="#{!item.page.category && !item.page.wizard}" />
                  
         <h:outputText value="#{item.page.title}" rendered="#{item.page.category || item.page.wizard ||  wizard.current.base.type != 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}" />
         
         <h:commandLink action="#{item.page.processExecPage}" rendered="#{!item.page.category && !item.page.wizard && wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}">
         	<h:outputText value="#{item.page.title}"/>
         </h:commandLink>
         
       </f:subview>
      </h:column>
	  <%--TODO: have created a new column for the status value - need to reformat that string--%>
	  <h:column>
	     <f:facet name="header">
            <h:outputText value="Status" />
         </f:facet>
		
		  <f:subview id="pageViewStatus" rendered="#{item.classInfo == 'completedPage'}" >
		  	<f:verbatim><table style="width: 100%"><tr><td style="text-align: center;" class="wizard-</f:verbatim><h:outputText value="#{item.base.wizardPage.status}"/><f:verbatim>"></f:verbatim>
		  	<h:outputText value=" #{item.base.wizardPage.status}" rendered="#{item.classInfo == 'completedPage'}" />
		  	<f:verbatim></td></tr></table></f:verbatim>
		  </f:subview>
	  </h:column>
	  <%-- hide this column if sequential wizard, since we are hiding the page descriptions and that is all a seq wiz has --%>

      <h:column rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.hierarchical'}">
         <f:facet name="header">
            <h:outputText value="#{msgs.wizard_description}" />
         </f:facet>
		 
<%-- TODO:  come up with a hint/on demand way of showing the page description or leave it omitted as below
		<f:subview id="pageView2" rendered="#{item.classInfo == 'completedPage'}" >
         	<h:outputText value="#{item.page.description}" escape="false" />
         </f:subview>
--%>					 
         <f:subview id="categoryView2" rendered="#{item.classInfo == 'completedCategory'}" >
         	<h:outputText value="#{item.category.description}" escape="false"/>
         </f:subview>
      </h:column>
      
   </h:dataTable>
   </f:subview>   
   
    <!-- ****************** reflection ****************** -->
	<%--TODO  this layout should match the one in matrix cells --%>
    <f:subview id="reflectionArea" rendered="#{wizard.current.base.reflectionDevice != null && 
      				wizard.current.base.reflectionDevice.value != ''}">
<%--      <h:outputText value="<br><br>" escape="false" rendered="#{wizard.current.base.reflectionDevice != null && 
      				wizard.current.base.reflectionDevice.value != ''}" /> --%>
      <ospx:xheader rendered="#{wizard.current.base.reflectionDevice != null || 
      				wizard.current.base.reflectionDevice.value != ''}">
         <ospx:xheadertitle id="reflectiontitleheader" value="#{msgs.reflection_section_header}" />
      <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
		<f:verbatim>
			<div class="itemAction indnt2" style="margin-bottom:1em;padding-top:0">
		</f:verbatim>
      
     <f:subview id="noReflection" 
      	rendered="#{empty wizard.current.runningWizard.reflections && 
      	(wizard.current.runningWizard.base.status == 'READY' || wizard.current.runningWizard.base.status == 'RETURNED') &&
      		not wizard.current.runningWizard.isReadOnly}">

         <h:commandLink action="#{wizard.processActionReflection}">
         	<h:outputText value="#{msgs.reflection_create}"/>
         </h:commandLink>
		</f:subview>

	  	<f:subview id="showReflection" rendered="#{not empty wizard.current.runningWizard.reflections}">
			<f:subview id="displayReflection" rendered="#{(wizard.current.runningWizard.base.status != 'READY' and wizard.current.runningWizard.base.status != 'RETURNED') ||
				wizard.current.runningWizard.isReadOnly}">
					<f:verbatim>
						<img src = '/library/image/silk/application_form.gif' border= '0' hspace='0' />
					</f:verbatim>
					<h:outputText value=" " />
				<h:outputLink value="#{wizard.current.runningWizard.reflections[0].reviewContentNode.fixedExternalUri}" target="_blank" >
					<h:outputText value="#{wizard.current.runningWizard.reflections[0].reviewContentNode.displayName}"/>
				</h:outputLink>
			</f:subview>
			<f:subview id="editReflection" rendered="#{(wizard.current.runningWizard.base.status == 'READY' || wizard.current.runningWizard.base.status == 'RETURNED') && 
				not wizard.current.runningWizard.isReadOnly}">
				<f:verbatim>
					<img src = '/library/image/silk/application_form.gif' border= '0' hspace='0' />
				</f:verbatim>
				<h:outputText value=" " />
				<h:outputText value="#{wizard.current.runningWizard.reflections[0].reviewContentNode.displayName}" />
				<h:outputText value=" " />
					<f:verbatim>
						<img src = '/library/image/silk/application_form_edit.png' border= '0' hspace='0' />
					</f:verbatim>
					<h:outputText value=" " />
				<h:commandLink action="#{wizard.processEditReflection}">

					<h:outputText value="#{msgs.reflection_edit}"/>
				</h:commandLink>
			</f:subview>
		</f:subview>
		  <f:verbatim>
			  </div>
		   </f:verbatim>

      </ospx:xheaderdrawer>
   </ospx:xheader>
   </f:subview>
   
   
   
   <!-- ****************** feedback ****************** -->
 
   <ospx:xheader rendered="#{wizard.commentItem != ''}">
      <ospx:xheadertitle id="wizardReviews" value="#{msgs.wizard_reviews}" />
      <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
               <f:verbatim>
			      <div class="itemAction indnt2" style="margin-bottom:1em;padding-top:0">
               </f:verbatim>
				  <f:subview id="feedbackAdd" 
				  	rendered="#{wizard.canReview && wizard.current.base.reviewDevice != null &&
				  		wizard.current.base.reviewDevice.value != ''}">
                    <h:commandLink action="#{wizard.processActionReview}">
                       <h:outputText value="#{msgs.review_add}" />
                    </h:commandLink>
				  </f:subview>
				 <h:outputText value=" " rendered="#{empty wizard.current.runningWizard.reviews}" />
				  <h:outputText value="#{msgs.review_empty}" rendered="#{empty wizard.current.runningWizard.reviews}" />
              <f:verbatim>
                 </div>
              </f:verbatim>
         <sakai:flat_list value="#{wizard.current.runningWizard.reviews}" var="review"  rendered="#{not empty wizard.current.runningWizard.reviews}">
            <h:column>
               <f:facet name="header">
                  <h:outputText value="#{msgs.wizard_eval_name}" />
               </f:facet>
		<%-- TODO Dupe? --%>	   
			   		<f:verbatim>
					<img src = '/library/image/silk/comment.gif' border= '0' hspace='0' /><h:outputText value=" " />
				</f:verbatim>
               <h:outputLink value="#{review.reviewContentNode.fixedExternalUri}" target="_blank">
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
     
     
     
   <!-- ****************** evaluation ****************** -->
   <ospx:xheader rendered="#{wizard.evaluationItem != ''}">               
      <ospx:xheadertitle id="wizardEvals" value="#{msgs.wizard_evals}" />
      <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
               <f:verbatim>
			      <div class="itemAction indnt2" style="margin-bottom:1em">
               </f:verbatim>
				  <f:subview id="evaluationAdd" 
				  	rendered="#{wizard.canEvaluate && wizard.current.base.evaluationDevice != null &&
				  		wizard.current.base.evaluationDevice.value != '' &&
				  		wizard.current.runningWizard.base.status == 'PENDING'}">
                    <h:commandLink action="#{wizard.processActionEvaluate}">
                       <h:outputText value="#{msgs.evaluation_add}"/>
                    </h:commandLink>
				  </f:subview>
			   <h:outputText value=" " rendered="#{empty wizard.current.runningWizard.evaluations}" />
			   <h:outputText value="#{msgs.eval_empty}" rendered="#{empty wizard.current.runningWizard.evaluations}" />
              <f:verbatim>
                 </div>
              </f:verbatim>
			   
         <sakai:flat_list value="#{wizard.current.runningWizard.evaluations}" var="eval" rendered="#{not empty wizard.current.runningWizard.evaluations}">
            <h:column>
               <f:facet name="header">
                  <h:outputText value="#{msgs.wizard_eval_name}" />
               </f:facet>
			   <f:verbatim>
					<img src = '/library/image/silk/comments.gif' border= '0' hspace='0' /><h:outputText value=" " />
				</f:verbatim>
               <h:outputLink value="#{eval.reviewContentNode.fixedExternalUri}" target="_blank">
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
      
      
      
   <sakai:button_bar>
		<f:subview id="seqWizardButtons"  rendered="#{wizard.current.base.type == 'org.theospi.portfolio.wizard.model.Wizard.sequential' && (wizard.current.canOperateOnWizardInstance || not empty wizard.current.userListForSelect)}">	
       	<sakai:button_bar_item id="submitContinue" value="#{msgs.save_continue_wizard}" onclick="disableThisClass(1);"
	      	 action="#{wizard.processExecPages}" accesskey="s" styleClass="active disableThis" />
		</f:subview>

    <sakai:button_bar_item id="returnToList" value="#{msgs.wizard_list}" styleClass="disableThis"  onclick="disableThisClass(2);"
       action="#{wizard.processActionCancelRun}" rendered="#{!wizard.fromEvaluation}"  accesskey="l"/>
    <sakai:button_bar_item id="returnToEvaluations" value="#{msgs.evaluation_list}" styleClass="disableThis"  onclick="disableThisClass(3);"
       action="#{wizard.processActionCancelRun}" rendered="#{wizard.fromEvaluation}" />
       
   <f:subview id="evalSubmitSV" rendered="#{wizard.evaluationItem != ''}">
    <sakai:button_bar_item id="submitEvalWizard" value="#{msgs.submit_wizard_for_evaluation}"  styleClass="disableThis"  onclick="disableThisClass(4);"
       rendered="#{(wizard.current.runningWizard.base.status == 'READY' || wizard.current.runningWizard.base.status == 'RETURNED') && wizard.current.runningWizard.isReadOnly == 'false'}"
       action="confirmSubmit" immediate="true"
        />
   </f:subview>
</sakai:button_bar>

</h:form>
</sakai:view>

</f:view>

<script type="text/javaScript">
var aryClassElements = new Array();

function disableThisClass(index) {
	var continueBtn = document.getElementById("runWizardGuidance:seqWizardButtons:submitContinue");
	var returnListBtn = document.getElementById("runWizardGuidance:returnToList");
	var returnEvalBtn = document.getElementById("runWizardGuidance:returnToEvaluations");
	var submitBtn = document.getElementById("runWizardGuidance:evalSubmitSV:submitEvalWizard");

	
	if(continueBtn && index != 1)
		continueBtn.disabled = true;
	if(returnListBtn && index != 2)
		returnListBtn.disabled = true;
	if(returnEvalBtn && index != 3)
		returnEvalBtn.disabled = true;
	if(submitBtn && index != 4)
		submitBtn.disabled = true;	
}
</script>
