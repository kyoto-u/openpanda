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
   <sakai:view_title value="#{common_msgs.guidance_title}"/>
   <sakai:instruction_message value="#{guidance.guidanceInstructions} #{guidance.pageContext2}"/>
   <sakai:messages rendered="#{!empty facesContext.maximumSeverity}"/>
<h:form>

<ospx:xheader rendered="#{guidance.instructionsRendered}">
	  <ospx:xheader>
			<ospx:xheadertitle id="instructionTitleAdd"  rendered="#{guidance.current.instruction.base.text == ''}">
      			<h:outputText value="#{common_msgs.instruction_title_add}" />
			</ospx:xheadertitle>
      	 	<ospx:xheadertitle id="instructionTitleEdit" rendered="#{guidance.current.instruction.base.text != ''}">
	   			<h:outputText value="#{common_msgs.instruction_title_edit}" />
	   		</ospx:xheadertitle>	   
	  </ospx:xheader>
      <f:subview id="instructView2">
               <sakai:instruction_message value="#{common_msgs.instruction_message}" />
               <sakai:inputRichText value="#{guidance.current.instruction.base.text}"
                  attachedFiles="#{guidance.current.instruction.attachmentLinks}"
                  rows="23"  cols="100"  buttonSet="small" showXPath="false" >
                  <f:validator validatorId="org.sakaiproject.gradebook.jsf.validator.RichTextValidator"/>
               </sakai:inputRichText>
               <f:subview id="instrItems" rendered="#{not empty guidance.current.instruction.attachments}">
               <sakai:flat_list value="#{guidance.current.instruction.attachments}" var="material" style="margin:1em 1em 0 1em;width:auto" summary="#{common_msgs.item_list_summary}">
                  <h:column>
                     <h:outputLink title="#{material.displayName}"
                        value="#{material.fullReference.base.url}" target="_blank">
                        <sakai:contentTypeMap fileType="#{material.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
                        <h:graphicImage id="instrFileIcon" value="#{imagePath}" alt="#{material.displayName}" title="#{material.displayName}" />
                        <h:outputText value="#{material.displayName}"/>
                     </h:outputLink>
                  </h:column>
               </sakai:flat_list>
				<sakai:button_bar rendered="true">
				<sakai:button_bar_item id="manageInstructionItems" action="#{guidance.current.instruction.processActionManageAttachments}"
				   value="#{common_msgs.manage_instruction_edit}" />
				</sakai:button_bar>
               </f:subview>
               <f:subview id="instrNoItems" rendered="#{empty guidance.current.instruction.attachments}">
				  	<sakai:instruction_message value="#{material}"  />
					<sakai:button_bar rendered="true">
					   <sakai:button_bar_item id="manageInstructionItems" action="#{guidance.current.instruction.processActionManageAttachments}"
					   value="#{common_msgs.manage_instruction_add}" />
					</sakai:button_bar>
               </f:subview>
      </f:subview>
  </ospx:xheader>
  <ospx:xheader rendered="#{guidance.examplesRendered}">
      <ospx:xheader>
      		<ospx:xheadertitle id="exampleTitleAdd" rendered="#{guidance.current.example.base.text == ''}">
	   			<h:outputText value="#{common_msgs.example_title_add}" />
	   		</ospx:xheadertitle>	   
      		<ospx:xheadertitle id="exampleTitleEdit" rendered="#{guidance.current.example.base.text != ''}" >
	   			<h:outputText value="#{common_msgs.example_title_edit}" />
	   		</ospx:xheadertitle>	   
      </ospx:xheader>
      <f:subview id="exapmleView2">
               <sakai:instruction_message value="#{common_msgs.example_message}" />
               <sakai:inputRichText value="#{guidance.current.example.base.text}"
                  attachedFiles="#{guidance.current.example.attachmentLinks}"
                  rows="23"  cols="100"  buttonSet="small" showXPath="false" />
               <f:subview id="exampleItems" rendered="#{not empty guidance.current.example.attachments}">
               <sakai:flat_list value="#{guidance.current.example.attachments}" var="material" style="margin:1em 1em 0 1em;width:auto" summary="#{common_msgs.item_list_summary}">
                  <h:column>
                     <h:outputLink title="#{material.displayName}"
                        value="#{material.fullReference.base.url}" target="_blank">
                        <sakai:contentTypeMap fileType="#{material.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
                        <h:graphicImage id="exampleFileIcon" value="#{imagePath}" alt="#{material.displayName}" title="#{material.displayName}" />
                        <h:outputText value="#{material.displayName}"/>
                     </h:outputLink>
                  </h:column>
               </sakai:flat_list>
			   <sakai:button_bar rendered="true">
                  <sakai:button_bar_item id="manageExampleItems" action="#{guidance.current.example.processActionManageAttachments}"
				  	value="#{common_msgs.manage_instruction_edit}" />
				</sakai:button_bar>

               </f:subview>
               <f:subview id="exampleNoItems" rendered="#{empty guidance.current.example.attachments}">
				 <sakai:instruction_message value="#{material}"  />
				<sakai:button_bar rendered="true">
				   <sakai:button_bar_item id="manageExampleItems" action="#{guidance.current.example.processActionManageAttachments}"
				   value="#{common_msgs.manage_instruction_add}" />
				</sakai:button_bar>
               </f:subview>
      </f:subview>
  </ospx:xheader>
  <ospx:xheader rendered="#{guidance.rationaleRendered}">
      <ospx:xheadertitle id="rationaleTitleAdd" rendered="#{guidance.current.rationale.base.text == ''}" >
	   		<h:outputText value="#{common_msgs.rationale_title_add}"  />
	  </ospx:xheadertitle>
      <ospx:xheadertitle id="rationaleTitleEdit" rendered="#{guidance.current.rationale.base.text != ''}">
	   		<h:outputText value="#{common_msgs.rationale_title_edit}" />
	  </ospx:xheadertitle>
      <f:subview id="rationalView">
               <sakai:instruction_message value="#{common_msgs.rationale_message}" />
               <sakai:inputRichText value="#{guidance.current.rationale.base.text}"
                  attachedFiles="#{guidance.current.rationale.attachmentLinks}"
                  rows="23"  cols="100"  buttonSet="small" showXPath="false" />
             <f:subview id="rationaleItems" rendered="#{not empty guidance.current.rationale.attachments}">
			 	<sakai:flat_list value="#{guidance.current.rationale.attachments}" var="material"  style="margin:1em 1em 0 1em;width:auto" summary="#{common_msgs.item_list_summary}">
                  <h:column>
                     <h:outputLink title="#{material.displayName}"
                        value="#{material.fullReference.base.url}" target="_blank">
                        <sakai:contentTypeMap fileType="#{material.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
                        <h:graphicImage id="rationaleFileIcon" value="#{imagePath}" alt="#{material.displayName}" title="#{material.displayName}" />
                        <h:outputText value="#{material.displayName}"/>
                     </h:outputLink>
                  </h:column>
               </sakai:flat_list>
				<sakai:button_bar rendered="true">
				   <sakai:button_bar_item id="manageRationaleItems" action="#{guidance.current.rationale.processActionManageAttachments}"
				   value="#{common_msgs.manage_instruction_edit}" />
				</sakai:button_bar>
               </f:subview>
               <f:subview id="rationaleNoItems" rendered="#{empty guidance.current.rationale.attachments}">
				 <sakai:instruction_message value="#{material}"  />
					<sakai:button_bar rendered="true">
					   <sakai:button_bar_item id="manageRationaleItems" action="#{guidance.current.rationale.processActionManageAttachments}"
					   value="#{common_msgs.manage_instruction_add}" />
					</sakai:button_bar>
               </f:subview>
      </f:subview>
  </ospx:xheader>
  <ospx:xheader rendered="#{guidance.rubricRendered}">
      <ospx:xheadertitle id="rubricTitleAdd" rendered="#{guidance.current.rubric.base.text == ''}" >
	   		<h:outputText value="#{common_msgs.rubric_title_add}"  />
	  </ospx:xheadertitle>
      <ospx:xheadertitle id="rubricTitleEdit" rendered="#{guidance.current.rubric.base.text != ''}">
	   		<h:outputText value="#{common_msgs.rubric_title_edit}" />
	  </ospx:xheadertitle>
      <f:subview id="rubricView">
               <sakai:instruction_message value="#{common_msgs.rubric_message}" />
               <sakai:inputRichText value="#{guidance.current.rubric.base.text}"
                  attachedFiles="#{guidance.current.rubric.attachmentLinks}"
                  rows="23"  cols="100"  buttonSet="small" showXPath="false" />
             <f:subview id="rubricItems" rendered="#{not empty guidance.current.rubric.attachments}">
			 	<sakai:flat_list value="#{guidance.current.rubric.attachments}" var="material"  style="margin:1em 1em 0 1em;width:auto" summary="#{common_msgs.item_list_summary}">
                  <h:column>
                     <h:outputLink title="#{material.displayName}"
                        value="#{material.fullReference.base.url}" target="_blank">
                        <sakai:contentTypeMap fileType="#{material.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
                        <h:graphicImage id="rubricFileIcon" value="#{imagePath}" alt="#{material.displayName}" title="#{material.displayName}" />
                        <h:outputText value="#{material.displayName}"/>
                     </h:outputLink>
                  </h:column>
               </sakai:flat_list>
				<sakai:button_bar rendered="true">
				   <sakai:button_bar_item id="manageRubricItems" action="#{guidance.current.rubric.processActionManageAttachments}"
				   value="#{common_msgs.manage_instruction_edit}" />
				</sakai:button_bar>
               </f:subview>
               <f:subview id="rubricNoItems" rendered="#{empty guidance.current.rubric.attachments}">
				 <sakai:instruction_message value="#{material}"  />
					<sakai:button_bar rendered="true">
					   <sakai:button_bar_item id="manageRubricItems" action="#{guidance.current.rubric.processActionManageAttachments}"
					   value="#{common_msgs.manage_instruction_add}" />
					</sakai:button_bar>
               </f:subview>
      </f:subview>
  </ospx:xheader>


<ospx:xheader rendered="#{guidance.expectationsRendered}">
      <ospx:xheadertitle id="expectationsTitleAdd" rendered="#{guidance.current.expectations.base.text == ''}" >
	   		<h:outputText value="#{common_msgs.expectations_title_add}"  />
	  </ospx:xheadertitle>
      <ospx:xheadertitle id="expectationsTitleEdit" rendered="#{guidance.current.expectations.base.text != ''}">
	   		<h:outputText value="#{common_msgs.expectations_title_edit}" />
	  </ospx:xheadertitle>
      <f:subview id="expectationsView">
               <sakai:instruction_message value="#{common_msgs.expectations_message}" />
               <sakai:inputRichText value="#{guidance.current.expectations.base.text}"
                  attachedFiles="#{guidance.current.expectations.attachmentLinks}"
                  rows="23"  cols="100"  buttonSet="small" showXPath="false" />
             <f:subview id="expectationsItems" rendered="#{not empty guidance.current.expectations.attachments}">
			 	<sakai:flat_list value="#{guidance.current.expectations.attachments}" var="material"  style="margin:1em 1em 0 1em;width:auto" summary="#{common_msgs.item_list_summary}">
                  <h:column>
                     <h:outputLink title="#{material.displayName}"
                        value="#{material.fullReference.base.url}" target="_blank">
                        <sakai:contentTypeMap fileType="#{material.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
                        <h:graphicImage id="expectationsFileIcon" value="#{imagePath}" alt="#{material.displayName}" title="#{material.displayName}" />
                        <h:outputText value="#{material.displayName}"/>
                     </h:outputLink>
                  </h:column>
               </sakai:flat_list>
				<sakai:button_bar rendered="true">
				   <sakai:button_bar_item id="manageExpectationsItems" action="#{guidance.current.expectations.processActionManageAttachments}"
				   value="#{common_msgs.manage_instruction_edit}" />
				</sakai:button_bar>
               </f:subview>
               <f:subview id="expectationsNoItems" rendered="#{empty guidance.current.expectations.attachments}">
				 <sakai:instruction_message value="#{material}"  />
					<sakai:button_bar rendered="true">
					   <sakai:button_bar_item id="manageExpectationsItems" action="#{guidance.current.expectations.processActionManageAttachments}"
					   value="#{common_msgs.manage_instruction_add}" />
					</sakai:button_bar>
               </f:subview>
      </f:subview>
  </ospx:xheader>


   <sakai:button_bar>
      <sakai:button_bar_item id="submit" value="#{common_msgs.button_save}" action="#{guidance.processActionSave}" styleClass="active" accesskey="s"/>
      <sakai:button_bar_item id="cancel" value="#{common_msgs.button_cancel}" action="#{guidance.processActionCancel}" accesskey="x" />
   </sakai:button_bar>

</h:form>
</sakai:view>

</f:view>