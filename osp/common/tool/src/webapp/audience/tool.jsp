<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
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

<sakai:view_title rendered="#{audience.matrixAudienceReview}" value="#{common_msgs.audience_review_title}"/>
<sakai:view_title rendered="#{not audience.inviteFeedbackAudience && not audience.matrixAudienceReview}" value="#{common_msgs.audience_eval_title}"/>
<sakai:view_title rendered="#{audience.inviteFeedbackAudience}" value="#{common_msgs.matrixFeedbackTitle}"/>
<f:subview id="audSubV10" rendered="#{audience.inviteFeedbackAudience}">
  <sakai:instruction_message value="#{common_msgs.matrixFeedbackInstructions}"/>
</f:subview>
<f:subview rendered="#{audience.matrixAudienceReview}" id="reviewerInstructs">
	<sakai:instruction_message value="#{common_msgs.audience_reviewersInfo}"/>
</f:subview>
<f:subview rendered="#{audience.matrixAudience}" id="matrixInstructs">
	<sakai:instruction_message value="#{common_msgs.audience_evalInfo}"/>
</f:subview>

<sakai:view>

<f:subview rendered="#{audience.wizardAudience}" id="wizardInstructs">
	<sakai:instruction_message value="#{common_msgs.audience_wizard_instructions}"/>
</f:subview>

<sakai:messages rendered="#{!empty facesContext.maximumSeverity}"/>

<h:form id="mainForm">
    <ospx:splitarea direction="horizontal" width="100%">
        <ospx:splitsection size="100%" valign="top">
            <!-- worksite user drawer -->
            <ospx:xheader>

                  <ospx:xheadertitle id="userTitle" value="#{common_msgs.audience_user_title}" rendered="#{audience.wizardAudience}" />

                  <ospx:xheadertitle id="userTitle1" value="#{common_msgs.audience_user_title}"  rendered="#{audience.matrixAudience}" />

                  <ospx:xheadertitle id="userTitle2" value="#{common_msgs.matrixFeedbackShareUsers}" rendered="#{audience.inviteFeedbackAudience}" /> 

               	  <ospx:xheadertitle id="userTitle4" value="#{common_msgs.audience_user_title}" rendered="#{audience.matrixAudienceReview}" />

               <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
                  <h:panelGrid id="transferUserTable" columns="3" columnClasses="available,transferButtons,selected" summary="#{common_msgs.name_table_summary}">
                     <h:panelGroup>
                        <ospx:splitarea direction="vertical">
                           <ospx:splitsection valign="top">
                              <h:outputFormat value="#{common_msgs.users}"/>
                           </ospx:splitsection>
                           <ospx:splitsection valign="top">
                              <h:selectManyListbox id="availableUsers" value="#{audience.availableUserArray}"
                                                   size="10" style="width:350px;">
                                 <f:selectItems value="#{audience.availableUserList}"/>
                              </h:selectManyListbox>
                           </ospx:splitsection>
                        </ospx:splitarea>
                     </h:panelGroup>
   
                     <h:panelGrid id="userTransferButtons" columns="1" columnClasses="transferButtonTable">
                        <ospx:splitarea width="120" direction="vertical">
                           <ospx:splitsection valign="top" align="center">
                              <sakai:button_bar>
                                 <sakai:button_bar_item id="add_user_button" action="#{audience.processActionAddUser}"
                                                         value="#{common_msgs.add_members}"/>
                              </sakai:button_bar>
                           </ospx:splitsection>
                           <ospx:splitsection valign="top" align="center">
                              <sakai:button_bar>
                                 <sakai:button_bar_item id="remove_user_button" action="#{audience.processActionRemoveUser}"
                                                        value="#{common_msgs.remove_members}"/>
                              </sakai:button_bar>
                           </ospx:splitsection>
                        </ospx:splitarea>
                     </h:panelGrid>
      
                     <h:panelGroup>
                        <ospx:splitarea direction="vertical">
                           <ospx:splitsection valign="top">
                           	<f:subview rendered="#{audience.wizardAudience}" id="wizSubView2">
                                    <h:outputFormat value="#{common_msgs.audience_selected_evaluators}"/>
                              </f:subview>
                              <f:subview rendered="#{audience.matrixAudience}" id="matSubView2">
                                    <h:outputFormat value="#{common_msgs.selected_users}"/>
                              </f:subview>
                              <f:subview rendered="#{audience.inviteFeedbackAudience}" id="feedSubView2">
                                    <h:outputFormat value="#{common_msgs.audience_selected_reviewers}"/>
                              </f:subview>
                              <f:subview rendered="#{audience.matrixAudienceReview}" id="revSubView2">
                                    <h:outputFormat value="#{common_msgs.selected_users}"/>
                              </f:subview>
                           </ospx:splitsection>
                           <ospx:splitsection valign="top">
                              <h:selectManyListbox id="selectedUsers" size="10" value="#{audience.selectedUserArray}"
                                                   style="width:350px;">
                                 <f:selectItems value="#{audience.selectedUserList}"/>
                              </h:selectManyListbox>
                           </ospx:splitsection>
                        </ospx:splitarea>
                     </h:panelGroup>
                  </h:panelGrid>
                  
                  <!-- other user and email user option -->
                  <f:subview id="emailUser" rendered="#{audience.inviteFeedbackAudience}">
                     <f:verbatim><h3></f:verbatim>
                     <h:outputText rendered="#{audience.inviteFeedbackAudience}" value="#{common_msgs.matrixFeedbackGuestUsers}" />
                     <f:verbatim></h3></f:verbatim>
                  
                  	 <f:subview rendered="#{audience.inviteFeedbackAudience}" id="feedSubView3">
					  <sakai:instruction_message value="#{common_msgs.matrixFeedbackGuestInstructions}"/>
					 </f:subview>
                  
                     <f:verbatim><p class='shorttext'></f:verbatim>
                     <f:subview rendered="#{!audience.guestUserEnabled}" id="guestSubView">
                        <h:outputLabel rendered="#{audience.inviteFeedbackAudience}" value="#{common_msgs.matrixFeedbackEnterGuest}:" for="emails"/>
                     </f:subview>
                     <f:subview rendered="#{audience.guestUserEnabled}" id="guestSubView2">
                        <h:outputLabel rendered="#{audience.inviteFeedbackAudience}" value="#{common_msgs.matrixFeedbackEnterGuest}:" for="emails"/>
                     </f:subview>
                                  
                     <h:inputText value="#{audience.searchEmails}" id="emails" size="60"/>
                     <h:outputText value=" "/>
                     <h:commandButton id="add_email_button"
                                      action="#{audience.processActionAddEmailUser}"
                                      value="#{common_msgs.add}"/>
                     <f:verbatim></p></f:verbatim>
                  </f:subview>
                  <!-- other user and email user option -->
                  <f:subview id="authorSelectedReviewers" rendered="#{audience.inviteFeedbackAudience}">                 
                  	 <f:subview rendered="#{audience.inviteFeedbackAudience}" id="authorSelected1">
					  <sakai:instruction_message value="#{common_msgs.matrixFeedbackAdditionalReviewers}"/>
					 </f:subview>
					 
					 <f:subview rendered="#{audience.inviteFeedbackAudience}" id="authorSelected2">
						<h:dataTable id="authorSelectedTable"
		        			 cellpadding="0" 
		                     cellspacing="0"		                    
		                     value="#{audience.externalReviewersForMatrix}"
		                     var="reviewerNames">	          
		          			<h:column>
		           
		            			<h:outputText value="#{reviewerNames}"/>
		          			</h:column>
		          		</h:dataTable>
					 </f:subview>
                  	                                  
                     
                     <f:verbatim></p></f:verbatim>
                  </f:subview>

                  <%-- optional message for browse user selection 
                  <f:subview id="browseUser" rendered="#{audience.maxList}" >
                     <f:verbatim><p class='shorttext'></f:verbatim>
                     <h:outputFormat value = "#{audience.browseMessage}"/>
                         <h:outputFormat value = " "/>
                     <h:commandLink id="browse_button" action="browse" value="#{common_msgs.browse_members}"
                                    style="white-space:nowrap;"/>
                     <h:outputFormat value = " "/>
                     <f:subview id="audSubV1" rendered="#{audience.wizardAudience}">
                        <h:outputFormat value = "#{common_msgs.audience_individual_evaluators}" />
                     </f:subview>
                     <f:subview id="audSubV2" rendered="#{audience.matrixAudience}">
                        <h:outputFormat value = "#{common_msgs.audience_individual_evaluators}" />
                     </f:subview>
                     <f:subview id="audSubV3" rendered="#{audience.inviteFeedbackAudience}">
                        <h:outputFormat value = "#{common_msgs.audience_individual_users}" />
                     </f:subview>
                     <f:subview id="audSubV5" rendered="#{audience.matrixAudienceReview}">
                        <h:outputFormat value = "#{common_msgs.audience_individual_evaluators}" />
                     </f:subview>
                     <f:verbatim></p></f:verbatim>
                  </f:subview>
                  --%>
               </ospx:xheaderdrawer>
            </ospx:xheader>
			
			
			
			<f:subview id="audSubV11" rendered="#{!audience.inviteFeedbackAudience}">
            <!-- worksite role drawer -->
            <ospx:xheader>
              
                  <ospx:xheadertitle id="roleTitle" value="#{common_msgs.audience_role_title}"  rendered="#{audience.wizardAudience}"/>

                  <ospx:xheadertitle id="roleTitle1" value="#{common_msgs.audience_role_title}"  rendered="#{audience.matrixAudienceReview}"/>

                  <ospx:xheadertitle id="roleTitle2" value="#{common_msgs.audience_role_title}"  rendered="#{audience.matrixAudience}"/>
        
               <ospx:xheaderdrawer initiallyexpanded="true" cssclass="drawerBorder">
                  <h:panelGrid id="transferRoleTable" columns="3" columnClasses="available,transferButtons,selected"  summary="#{common_msgs.role_table_summary}">
                     <h:panelGroup>
                        <ospx:splitarea direction="vertical">
                           <ospx:splitsection valign="top">
                              <h:outputFormat value="#{common_msgs.role_label}"/>
                           </ospx:splitsection>
                           <ospx:splitsection valign="top">
                              <h:selectManyListbox id="availableRoles" value="#{audience.availableRoleArray}"
                                                   size="10" style="width:350px;">
                                 <f:selectItems value="#{audience.availableRoleList}"/>
                              </h:selectManyListbox>
                           </ospx:splitsection>
                        </ospx:splitarea>
                     </h:panelGroup>
   
                     <h:panelGrid id="roleTransferButtons" columns="1" columnClasses="transferButtonTable">
                        <ospx:splitarea width="120" direction="vertical">
                           <ospx:splitsection valign="top" align="center">
                              <sakai:button_bar>
                                 <sakai:button_bar_item id="add_role_button" action="#{audience.processActionAddRole}"
                                                         value="#{common_msgs.add_members}"/>
                              </sakai:button_bar>
                           </ospx:splitsection>
                           <ospx:splitsection valign="top" align="center">
                              <sakai:button_bar>
                                 <sakai:button_bar_item id="remove_role_button" action="#{audience.processActionRemoveRole}"
                                                        value="#{common_msgs.remove_members}"/>
                              </sakai:button_bar>
                           </ospx:splitsection>
                        </ospx:splitarea>
                     </h:panelGrid>
   
                     <h:panelGroup>
                        <ospx:splitarea direction="vertical">
                           <ospx:splitsection valign="top">
                           	  <f:subview id="audSubV12" rendered="#{audience.wizardAudience}">
                                    <h:outputFormat value="#{common_msgs.audience_selected_evaluators}"/>
                              </f:subview>
                              <f:subview id="audSubV13" rendered="#{audience.matrixAudienceReview}">
                                    <h:outputFormat value="#{common_msgs.selected_roles}"/>
                              </f:subview>
                              <f:subview id="audSubV14" rendered="#{audience.matrixAudience}">
                                    <h:outputFormat value="#{common_msgs.selected_roles}"/>
                              </f:subview>
                           </ospx:splitsection>
                           <ospx:splitsection valign="top">
                              <h:selectManyListbox id="selectedRoles" size="10" value="#{audience.selectedRoleArray}"
                                                   style="width:350px;">
                                 <f:selectItems value="#{audience.selectedRoleList}"/>
                              </h:selectManyListbox>
                           </ospx:splitsection>
                        </ospx:splitarea>
                     </h:panelGroup>
                   </h:panelGrid>
                </ospx:xheaderdrawer>
            </ospx:xheader>
		</f:subview>
      </ospx:splitsection>
    </ospx:splitarea>
    <sakai:button_bar>
        <sakai:button_bar_item id="save_button" action="#{audience.processActionSave}" rendered="#{!audience.inviteFeedbackAudience}"
                               value="#{common_msgs.button_save}" styleClass="active" accesskey="s" />
        <sakai:button_bar_item id="saveNotify_buttonFeed" action="#{audience.processActionSaveNotify}"
                               rendered="#{audience.inviteFeedbackAudience}"
                               value="#{common_msgs.matrixFeedbackInviteNotify}" />

        <sakai:button_bar_item id="_target1" action="#{audience.processActionCancel}"
                               value="#{common_msgs.cancel_audience}" accesskey="x" />
    </sakai:button_bar>
</h:form>

</sakai:view>
</f:view>