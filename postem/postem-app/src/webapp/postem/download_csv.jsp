<%@ page contentType="text/csv; charset=UTF-8"
%><%@ page import="org.sakaiproject.tool.postem.PostemTool"
%><%@ page import="org.sakaiproject.util.Web"
%><%@ page import="javax.faces.context.FacesContext"
%><%@ page import="javax.faces.component.UIViewRoot"
%><%@ page import="java.net.*" %><%
	PostemTool tool = (PostemTool) session.getAttribute("PostemTool");
  String titleName = tool.getCurrentGradebook().getTitle().trim();
  String fileName = "postem_" + titleName + ".csv";
  String escapedFileName = URLEncoder.encode(fileName, "UTF-8");
  String userAgent = request.getHeader("User-Agent");
  String BOM = "\uFEFF";

  if (userAgent != null && userAgent.contains("MSIE")) {
    response.setHeader("Content-Disposition", "attachment; filename=" + escapedFileName);
  }else if (userAgent != null && userAgent.contains("Safari")) {
    String fileName_safari = fileName;
    try {
      fileName_safari  = new String(fileName.getBytes("utf-8"), "8859_1");
    } catch (Exception e) {
    }
    response.setHeader("Content-Disposition", "attachment; filename=" + fileName_safari);
  } else {
    response.setHeader("Content-Disposition", "attachment; filename*=utf-8''" + escapedFileName);
  }
   
//	response.setHeader("Content-disposition", "attachment; filename=" +
//		Web.encodeFileName(request, "postem_" + titleName + ".csv"));
	
	response.setHeader ("Pragma", "public");
	response.setHeader("Cache-control", "must-revalidate");
	
	String csv = tool.getCsv();
	out.print(BOM);
	out.print(csv);
	//out.flush();
	request.getSession(false).invalidate();
	/*FacesContext context = FacesContext.getCurrentInstance();
	UIViewRoot view = context.getApplication().getViewHandler().restoreView(context, "/postem/main.jsp");
	context.setViewRoot(view);*/
		/*context.getApplication().
			getNavigationHandler().
				handleNavigation(context, "processCancelView","main");*/
	
%>
