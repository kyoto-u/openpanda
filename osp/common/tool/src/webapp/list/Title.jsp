<%
		response.setContentType("text/html; charset=UTF-8");
		response.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
		response.addDateHeader("Last-Modified", System.currentTimeMillis());
		response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
		response.addHeader("Pragma", "no-cache");
%>


<f:view>
<sakai:view_container title="#{ListTool.currentConfig.title}">

	<sakai:title_bar value="#{ListTool.currentConfig.title}"/>

</sakai:view_container>
</f:view>
