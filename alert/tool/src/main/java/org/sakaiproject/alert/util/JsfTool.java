/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/jsf/tags/sakai-10.6/jsf-tool/src/java/org/sakaiproject/jsf/util/JsfTool.java $
 * $Id: JsfTool.java 128959 2013-08-23 00:01:46Z ottenhoff@longsight.com $
 **********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.alert.util;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Tool;

/**
 * <p>
 * Sakai Servlet to use for all JSF tools.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 128959 $
 */
public class JsfTool extends HttpServlet {
	/**
	 * Our log (commons).
	 */
	private static Log M_log = LogFactory.getLog(JsfTool.class);

	/**
	 * The file extension to get to JSF.
	 */
	protected static final String JSF_EXT = ".jsf";

	protected static final String[] JSF_FACELETS_EXT = new String[]{".jsp", ".xhtml", ".jspx"};
	/**
	 * Session attribute to hold the last view visited.
	 */
	public static final String LAST_VIEW_VISITED = "sakai.jsf.tool.last.view.visited";

	//	 TODO: Note, these two values must match those in jsf-app's SakaiViewHandler

	/**
	 * Request attribute we set to help the return URL know what extension we (or jsf) add (does not need to be in the URL.
	 */
	public static final String URL_EXT = "sakai.jsf.tool.URL.ext";

	/**
	 * Request attribute we set to help the return URL know what path we add (does not need to be in the URL.
	 */
	public static final String URL_PATH = "sakai.jsf.tool.URL.path";

	/**
	 * The default target, as configured.
	 */
	protected String m_default = null;

	/**
	 * if true, we preserve the last visit per placement / user, and use it if we get a request with no path.
	 */
	protected boolean m_defaultToLastView = true;

	/**
	 * The folder to the jsf files, as configured. Does not end with a "/".
	 */
	protected String m_path = null;


	/**
	 * This init parameter should contain an url to the welcome page
	 */
	public static final String FIRST_PAGE = "main-page";

	public static final String TOOL_NATIVE_URL = "tool-native-url";

	/**
	 *  This is Sakai Site Service
     */
	private SiteService siteService;

	protected void service(final HttpServletRequest request,
						   HttpServletResponse response) throws ServletException, IOException {

		final String contextPath = request.getContextPath();
		HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(
				request);
		if (Boolean.valueOf(getInitParameter(TOOL_NATIVE_URL))) {
			request.setAttribute(Tool.NATIVE_URL, Tool.NATIVE_URL);
		}

		if (request.getPathInfo() == null
				&& getInitParameter(FIRST_PAGE) != null
				&& !getInitParameter(FIRST_PAGE).equals("/")) {
			// Do redirect to first-page
			// We don't use welcome-file in the web.xml as it means that
			// realtive URLs don't work if your
			// index.html is outside the toplevel.
			request.removeAttribute(Tool.NATIVE_URL); // This is so we don't get
			// sakai.placement in
			// the URL.
			response.sendRedirect(contextPath + getInitParameter(FIRST_PAGE));
		} else if (request.getPathInfo() == null
				&& !request.getRequestURI().endsWith("/")) {
			// we should do the default redirect to "/"
			response.sendRedirect(contextPath + "/");
		} else if (request.getPathInfo() != null
				&& (request.getPathInfo().startsWith("/WEB-INF/") || request
				.getPathInfo().equals("/WEB-INF"))) {
			// Can't allow people to see WEB-INF
			response.sendRedirect(contextPath + "/");
		} else {
			// otherwise do the dispatch
			RequestDispatcher dispatcher;
			if (request.getPathInfo() == null) {
				dispatcher = request.getRequestDispatcher("");
			} else {
				dispatcher = request
						.getRequestDispatcher(request.getPathInfo());
			}

			dispatcher.forward(wrappedRequest, response);
		}

	}

	public SiteService getSiteService() {
		return siteService;
	}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}
}



