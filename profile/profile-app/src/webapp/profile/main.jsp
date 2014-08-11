<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 
<%@ taglib uri="http://sakaiproject.org/jsf/profile" prefix="profile" %> 

<% response.setContentType("text/html; charset=UTF-8"); %>
<f:view>
<sakai:view title="#{msgs.profile} " rendered="#{ProfileTool.showTool}">

	<h:form id="profileForm">	 	
	<!--main.jsp -->
	 <%@ include file="profileCommonToolBar.jsp"%>		 
		<h:outputText id="warning" value="#{msgs.no_profile_msg}" rendered="#{ProfileTool.displayNoProfileMsg}" styleClass="alertMessage"/>
	 <table border="0" class="profileTable" cellspacing="0" cellpadding="0" summary="layout">
			<tr>
				<td colspan="2"><sakai:view_title value="#{msgs.profile}: #{ProfileTool.profile.firstName} #{ProfileTool.profile.lastName}" /></td>
				<f:subview id="searchAreaHeader" rendered="#{ProfileTool.showSearch}">
				<td class="profileSearch"><sakai:view_title value="#{msgs.search_for_profile}"/></td>
				</f:subview>
			</tr>	
  	 			<tr>
				<td class="rosterImageCol">
					<h:graphicImage id="image1" value="#{ProfileTool.imageUrlToDisplay}"  alt="#{msgs.profile_picture_alt}" styleClass="rosterImage" /> 
<%--					<h:graphicImage id="image1" value="ProfileImageServlet.prf?photo=#{ProfileTool.profile.userId}"  alt="#{msgs.profile_picture_alt}" styleClass="rosterImage"  rendered="#{ProfileTool.displayUniversityPhoto}" /> 
					<h:graphicImage id="image2" value="#{ProfileTool.profile.pictureUrl}"  styleClass="rosterImage"  alt="#{msgs.alt_picture}" rendered="#{ProfileTool.displayPictureURL}" />
					<h:graphicImage id="image3" url="/images/pictureUnavailable.jpg"       styleClass="rosterImage"  alt="#{msgs.alt_picture_unavailable}" rendered="#{ProfileTool.displayNoPicture}"/>
					<h:graphicImage id="image4" url="/images/officialPhotoUnavailable.jpg" styleClass="rosterImage"  alt="#{msgs.alt_official_id_photo_unavailable}" rendered="#{ProfileTool.displayUniversityPhotoUnavailable}" />	
--%>			 </td>
			 <td class="profileData">
				<h:outputText id="position" value="#{ProfileTool.profile.position}" />
    			 	<h:outputText id="department" value="#{ProfileTool.profile.department}" />
    			 	<h:outputText id="school" value="#{ProfileTool.profile.school}" />
    			 	<h:outputText id="room" value="#{ProfileTool.profile.room}" />
    			 	<h:outputText id="email" value="#{ProfileTool.profile.email}"/>
    			 	<h:outputText id="homepage" value="#{ProfileTool.profile.homepage}"/>
    			 	<h:outputText id="workphone" value="#{ProfileTool.profile.workPhone}"/>
    			 	<h:outputText id="homephone" value="#{ProfileTool.profile.homePhone}"/>
    			 	 <h:outputText id="mobile" value="#{ProfileTool.profile.mobile}"/>
    			 	<span>
	    			 	<profile:profile_display_HTML value="#{ProfileTool.profile.otherInformation}" />
    			 	</span>	
		   	</td>
		   	<f:subview id="searchArea" rendered="#{ProfileTool.showSearch}">
			<td class="profileSearch">
				
		   		<%@ include file="searchModule.jsp"%>
			</td>
			</f:subview>
		</tr>
	</table>	
		 
	</h:form>
</sakai:view>
</f:view> 
