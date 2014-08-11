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
   <sakai:messages rendered="#{!empty facesContext.maximumSeverity}"/>

<h:form>
	<f:subview id="instructionSV" rendered="#{(guidance.current.instruction.base.text != '' && guidance.current.instruction.base.text != null) || not empty guidance.current.instruction.attachments}">
	<h4>
    	<h:outputText value="#{common_msgs.instruction_title}" />
	</h4>
	
	
	<div class="textPanel">
		<h:outputText value="#{guidance.current.instruction.base.text}" escape="false" />
	</div>	
	<sakai:flat_list value="#{guidance.current.instruction.attachments}" var="material" summary="">
	   <h:column>
		  <h:outputLink title="#{material.displayName}" styleClass="indnt1"
			 value="#{material.fullReference.base.url}" target="_blank">
			 <sakai:contentTypeMap fileType="#{material.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
			 <h:graphicImage id="instrFileIcon" value="#{imagePath}" alt="#{material.displayName}" title="#{material.displayName}" />
			 <h:outputText value="#{material.displayName}"/>
		  </h:outputLink>
	   </h:column>
	</sakai:flat_list>
	</f:subview>


	<f:subview id="exampleSV" rendered="#{(guidance.current.example.base.text != '' && guidance.current.example != null) || not empty guidance.current.example.attachments}">
	<h4>
		<%--TODO need a rendered attribute below checking for example content --%>
		<h:outputText value="#{common_msgs.example_title}" />
	</h4>
	
	<div class="textPanel">	
		<h:outputText value="#{guidance.current.example.base.text}" escape="false" />
		<sakai:flat_list value="#{guidance.current.example.attachments}" var="material"  summary="">
		   <h:column>
			  <h:outputLink title="#{material.displayName}" styleClass="indnt1"
				 value="#{material.fullReference.base.url}" target="_blank">
				 <sakai:contentTypeMap fileType="#{material.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
				 <h:graphicImage id="exampleFileIcon" value="#{imagePath}" alt="#{material.displayName}" title="#{material.displayName}" />
				 <h:outputText value="#{material.displayName}"/>
			  </h:outputLink>
		   </h:column>
		</sakai:flat_list>
		</div>	
	</f:subview>
	
	
	<f:subview id="rationaleSV" rendered="#{(guidance.current.rationale.base.text != '' && guidance.current.rationale != null) || not empty guidance.current.rationale.attachments}">
	<h4>
	<%--TODO need a rendered attribute below checking for rationale content --%>
		<h:outputText value="#{common_msgs.rationale_title}" />
	</h4>
	
	
	<div class="textPanel">	
		<h:outputText value="#{guidance.current.rationale.base.text}" escape="false" />
	</div>	
	<sakai:flat_list value="#{guidance.current.rationale.attachments}" var="material"  summary="">
	   <h:column>
		  <h:outputLink title="#{material.displayName}" styleClass="indnt1"
			 value="#{material.fullReference.base.url}" target="_blank">
			 <sakai:contentTypeMap fileType="#{material.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
			 <h:graphicImage id="rationaleFileIcon" value="#{imagePath}" alt="#{material.displayName}" title="#{material.displayName}" />
			 <h:outputText value="#{material.displayName}"/>
		  </h:outputLink>
	   </h:column>
	</sakai:flat_list>
	</f:subview>
	
	
	<f:subview id="rubricSV" rendered="#{(guidance.current.rubric.base.text != '' && guidance.current.rubric != null) || not empty guidance.current.rubric.attachments}">
	<h4>
	<%--TODO need a rendered attribute below checking for rubric content --%>
		<h:outputText value="#{common_msgs.rubric_title}" />
	</h4>
	
	
	<div class="textPanel">	
		<h:outputText value="#{guidance.current.rubric.base.text}" escape="false" />
	</div>	
	<sakai:flat_list value="#{guidance.current.rubric.attachments}" var="material"  summary="">
	   <h:column>
		  <h:outputLink title="#{material.displayName}" styleClass="indnt1"
			 value="#{material.fullReference.base.url}" target="_blank">
			 <sakai:contentTypeMap fileType="#{material.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
			 <h:graphicImage id="rubricFileIcon" value="#{imagePath}" alt="#{material.displayName}" title="#{material.displayName}" />
			 <h:outputText value="#{material.displayName}"/>
		  </h:outputLink>
	   </h:column>
	</sakai:flat_list>
	</f:subview>
	
	
	<f:subview id="expectationsSV" rendered="#{(guidance.current.expectations.base.text != '' && guidance.current.expectations != null) || not empty guidance.current.expectations.attachments}">
	<h4>
	<%--TODO need a rendered attribute below checking for expectations content --%>
		<h:outputText value="#{common_msgs.expectations_title}" />
	</h4>
	
	
	<div class="textPanel">	
		<h:outputText value="#{guidance.current.expectations.base.text}" escape="false" />
	</div>	
	<sakai:flat_list value="#{guidance.current.expectations.attachments}" var="material"  summary="">
	   <h:column>
		  <h:outputLink title="#{material.displayName}" styleClass="indnt1"
			 value="#{material.fullReference.base.url}" target="_blank">
			 <sakai:contentTypeMap fileType="#{material.mimeType.value}" mapType="image" var="imagePath" pathPrefix="/library/image/"/>
			 <h:graphicImage id="expectationsFileIcon" value="#{imagePath}" alt="#{material.displayName}" title="#{material.displayName}" />
			 <h:outputText value="#{material.displayName}"/>
		  </h:outputLink>
	   </h:column>
	</sakai:flat_list>
	</f:subview>
	
	

	<div class="act">
		<h:commandButton id="cancel" value="#{common_msgs.button_back}" action="#{guidance.processActionCancel}" accesskey="x" styleClass="active"/>
	</div>

</h:form>
</sakai:view>

</f:view>
