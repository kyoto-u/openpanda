<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 
<%@ taglib uri="http://sakaiproject.org/jsf/profile" prefix="profile" %> 
 
<% response.setContentType("text/html; charset=UTF-8"); %>

<f:view>
	<sakai:view title="#{msgs.profile}" rendered="#{ProfileTool.showTool}">
		<h:form id="displayProfileForm">	 	
		<%@ include file="profileCommonToolBar.jsp"%>
			<!--displaySearchedProfile.jsp -->
		
			<table border="0" class="profileTable" cellspacing="0" cellpadding="0" summary="layout">
				<tr>
					<td colspan="2">                                                      
						<sakai:view_title  value="#{msgs.profile}:  #{SearchTool.profile.profile.firstName} #{SearchTool.profile.profile.lastName}" />
					</td>
					<td class="profileSearch">
						<sakai:view_title value="#{msgs.search_for_profile}"/>
					</td>
				</tr>
				<tr>
					<td class="rosterImageCol">
						<h:graphicImage id="image1" value="ProfileImageServlet.prf?photo=#{SearchTool.profile.profile.userId}" alt="#{msgs.alt_official_id_photo}" styleClass="rosterImage" rendered="#{SearchTool.profile.displayUniversityPhoto}"/> 
						<h:graphicImage id="image2" value="#{SearchTool.profile.profile.pictureUrl}" styleClass="rosterImage"  alt="#{msgs.alt_picture}" rendered="#{SearchTool.profile.displayPictureURL}"/>
						<h:graphicImage id="image3" url="/images/pictureUnavailable.jpg" styleClass="rosterImage"  alt="#{msgs.alt_picture_unavailable}" rendered="#{SearchTool.profile.displayNoPicture}"/>
						<h:graphicImage id="image4" alt="#{msgs.alt_official_id_photo_unavailable}" url="/images/officialPhotoUnavailable.jpg" styleClass="rosterImage"  rendered="#{SearchTool.profile.displayUniversityPhotoUnavailable}" />			
					</td>
					<td class="profileData">
						<h:outputText id="id1" value="#{SearchTool.profile.profile.position}"/> 
						<h:outputText id="id2" value="#{SearchTool.profile.profile.department}"/>
						<h:outputText id="id3" value="#{SearchTool.profile.profile.school}"/>
						<h:outputText id="id4" value="#{SearchTool.profile.profile.room}"/>
						<h:outputText id="id5" value="#{SearchTool.profile.profile.email} " rendered="#{SearchTool.profile.displayCompleteProfile}"/>
						<h:outputText id="id6" value="#{SearchTool.profile.profile.homepage}" rendered="#{SearchTool.profile.displayCompleteProfile}"/>
						<h:outputText id="id7" value="#{SearchTool.profile.profile.workPhone}" rendered="#{SearchTool.profile.displayCompleteProfile}"/>
						<h:outputText id="id8" value="#{SearchTool.profile.profile.homePhone}" rendered="#{SearchTool.profile.displayCompleteProfile}"/>
						<h:outputText id="id9" value="#{SearchTool.profile.profile.mobile}" rendered="#{SearchTool.profile.displayCompleteProfile}"/>
						<profile:profile_display_HTML value="#{SearchTool.profile.profile.otherInformation}"  rendered="#{SearchTool.profile.displayCompleteProfile}"/>
					</td>	
					<td class="profileSearch">				
						<%@ include file="searchModule.jsp"%>
					</td>
				</tr>
			</table>	
		</h:form>
	</sakai:view>
 
</f:view> 	
