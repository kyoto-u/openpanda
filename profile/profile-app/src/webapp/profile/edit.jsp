<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/profile" prefix="profile" %> 

<% response.setContentType("text/html; charset=UTF-8"); %>
<f:view>
	<sakai:view title="My Profile" rendered="#{ProfileTool.showTool}">
	<h:form id="editProfileForm">
	<!--edit.jsp -->
		<div class="navIntraTool">
		 	<h:outputText id="editprofile" value="#{msgs.profile_edit}" styleClass="currentView" />
			<h:outputText id="seperaror" value=" | "/>
			<h:commandLink id="cancel" immediate="true" action="#{SearchTool.processCancel}"  value="#{msgs.profile_show}" />
		</div>
		<sakai:tool_bar_message value="#{msgs.profile_edit_title}" />
 			 <div class="instruction">
  			    <h:outputText id="er1"  value="#{msgs.info_A}"/>
			 	<h:outputText id="er2" styleClass="reqStarInline" value="'#{msgs.info_required_sign}'"/>
			    <h:outputText id="er3" value="#{msgs.info_required}"/>
  			 </div>
  			
			 <h4> <h:outputText  value="#{msgs.title_public_info}"/></h4>

			 <h:panelGrid columns="1" styleClass="jsfFormTable" summary="layout">

			 <h:panelGroup styleClass="checkbox labelindnt">
					<h:selectBooleanCheckbox id="checkhide" title= "#{msgs.profile_hide_entire}"  value="#{ProfileTool.profile.hidePublicInfo}"  /> 
					<h:outputLabel   value="#{msgs.profile_hide_entire}" for="checkhide"/>
			 </h:panelGroup>
			 <h:panelGroup styleClass="shorttext required">
				<%--http://www.thoughtsabout.net/blog/archives/000031.html--%>
						<h:outputText id="inputid"  value="#{msgs.info_required_sign}" styleClass="reqStar"/>
						<h:outputLabel id="outputLabel" for="first_name"  value="#{msgs.profile_first_name}"/>
						<h:inputText  id="first_name"  size="30" value="#{ProfileTool.profile.firstName}"/>
						<h:outputText id="er4" value="#{msgs.error_empty_first_name}" styleClass="alertMessage labelindnt" rendered="#{ProfileTool.displayEmptyFirstNameMsg}"/>
				</h:panelGroup>
				<h:panelGroup styleClass="shorttext required">
						<h:outputText id="er5" value="#{msgs.info_required_sign}" styleClass="reqStar"/>
						<h:outputLabel id="outputLabel111" for="lname"  value="#{msgs.profile_last_name}"/>
						<h:inputText id="lname" size="30"  value="#{ProfileTool.profile.lastName}"/>
						<h:outputText id="er6" value="#{msgs.error_empty_last_name}" styleClass="alertMessage labelindnt" rendered="#{ProfileTool.displayEmptyLastNameMsg}"/>
				</h:panelGroup>
				<h:panelGroup styleClass="shorttext">
						<h:outputLabel id="outputLabel2" for="nickname"  value="#{msgs.profile_nick_name}"/>
						<h:inputText id="nickname" size="30"  value="#{ProfileTool.profile.nickName}"/>
				</h:panelGroup>
				<h:panelGroup styleClass="shorttext">
						<h:outputLabel id="outputLabel3"  for="position"  value="#{msgs.profile_position}"/>
						<h:inputText id="position" size="30"  value="#{ProfileTool.profile.position}"/>
				</h:panelGroup>
				<h:panelGroup styleClass="shorttext">
						<h:outputLabel id="outputLabel4" for="department"  value="#{msgs.profile_department}"/>
						<h:inputText id="department" size="30"  value="#{ProfileTool.profile.department}"/>
				</h:panelGroup>	
				<h:panelGroup styleClass="shorttext">
						<h:outputLabel id="outputLabel5" for="school"  value="#{msgs.profile_school}"/>
						<h:inputText id="school" size="30"  value="#{ProfileTool.profile.school}" />
				</h:panelGroup>
				<h:panelGroup styleClass="shorttext">
					<h:outputLabel id="outputLabel6" for="room"  value="#{msgs.profile_room}"/>
					<h:inputText id="room" size="30"  value="#{ProfileTool.profile.room}"/>
				</h:panelGroup>
			</h:panelGrid>	
	
				<h4><h:outputText id="personal" value="#{msgs.title_personal_info}" /></h4>
	
				<h:panelGrid columns="1" styleClass="jsfFormTable" summary="layout">
				<h:panelGroup styleClass=" checkbox labelindnt " >
					<h:selectBooleanCheckbox  id="hideMyPersonalInfo" title= "#{msgs.profile_hide_personal}"   value="#{ProfileTool.profile.hidePrivateInfo}" />
					<h:outputLabel id="id5"  value="#{msgs.profile_hide_personal}" for="hideMyPersonalInfo"/>
				</h:panelGroup>
				
				<h:panelGroup styleClass="shorttext">
					<h:outputLabel id="outputLabel7"   value="#{msgs.profile_picture}" />
					<h:selectOneRadio id="picture"  title="#{msgs.edit_pic_preference}"  value="#{ProfileTool.pictureIdPreference}" layout="pageDirection" styleClass="checkbox" style="margin-bottom:1em">
						<f:selectItem itemLabel="#{msgs.edit_pic_preference_none}" itemValue="none"/>
						<f:selectItem itemLabel="#{msgs.edit_pic_preference_univ}" itemValue="universityId"/>
						<f:selectItem itemLabel="#{msgs.edit_pic_preference_url}" itemValue="pictureUrl"/>
					</h:selectOneRadio>
				</h:panelGroup>	
				
				<h:panelGroup styleClass="shorttext">
						<h:inputText size="30" id="inputPictureUrl" value="#{ProfileTool.profile.pictureUrl}" styleClass="labelindnt"/>
						<h:outputText id="er7" value="#{msgs.error_msg} #{ProfileTool.malformedUrlError}" styleClass="alertMessageInline"  rendered="#{ProfileTool.displayMalformedPictureUrlError}"/>
				</h:panelGroup>
				<h:panelGroup styleClass="shorttext required">
					<h:outputText id="inputid1"  value="#{msgs.info_required_sign}" styleClass="reqStar"/>
					<h:outputLabel id="outputLabel17" for="email"  value="#{msgs.profile_email}"/>	
					<h:inputText size="30" id="email"  value="#{ProfileTool.profile.email}"/>
					<h:outputText id="er71" value="#{msgs.error_invalid_email}" styleClass="alertMessage labelindnt" rendered="#{ProfileTool.displayInvalidEmailMsg}"/>
				</h:panelGroup>
				<h:panelGroup styleClass="shorttext">
					<h:outputLabel id="outputLabel8" for="homepage"  value="#{msgs.profile_homepage}"/>
					<h:inputText id="homepage" size="30"  value="#{ProfileTool.profile.homepage}"/>
					<h:outputText id="er8" value="#{msgs.error_msg} #{ProfileTool.malformedUrlError}" styleClass="alertMessageInline"  rendered="#{ProfileTool.displayMalformedHomepageUrlError}"/>
				</h:panelGroup>
				<h:panelGroup styleClass="shorttext">
					<h:outputLabel id="outputLabel9" for="workphone"  value="#{msgs.profile_work_phone}"/>
					<h:inputText size="30" id="workphone" value="#{ProfileTool.profile.workPhone}"/>
				</h:panelGroup>
				<h:panelGroup styleClass="shorttext">
						<h:outputLabel id="outputLabel10" for="homephone"  value="#{msgs.profile_home_phone}"/>
						<h:inputText size="30" id="homephone" value="#{ProfileTool.profile.homePhone}"/>
				</h:panelGroup>	
				<h:panelGroup styleClass="shorttext">
                         <h:outputLabel id="outputLabel101" for="mobile"  value="#{msgs.profile_mobile}"/>
                         <h:inputText size="30" id="mobile" value="#{ProfileTool.profile.mobile}"/>
                 </h:panelGroup>	
				<h:panelGroup styleClass="shorttext">
					<h:panelGrid >
						<h:panelGroup><h:outputLabel id="outputLabel11" for="otherInformation" value="#{msgs.profile_other_information}"/></h:panelGroup>
						<h:panelGroup id="otherInformation"><sakai:rich_text_area value="#{ProfileTool.profile.otherInformation}" rows="17" columns="70"/></h:panelGroup>
						<h:outputText value="#{msgs.error_msg} #{ProfileTool.evilTagMsg}" styleClass="alertMessage"  rendered="#{ProfileTool.displayEvilTagMsg}"/>
					</h:panelGrid>
				</h:panelGroup>
			</h:panelGrid>
			<p class="act">
				<h:commandButton id="editSaveButton" styleClass="active" action ="#{ProfileTool.processActionEditSave}" onkeypress="document.forms[0].submit;" value="#{msgs.bar_save}" accesskey="s" />
				<h:commandButton id="editCancelButton" action="#{ProfileTool.processCancel}" onkeypress="document.forms[0].cancel;"	 value="#{msgs.bar_cancel}" accesskey="x" />
			</p>
			</h:form>
	</sakai:view>
	
</f:view>
