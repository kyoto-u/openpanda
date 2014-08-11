/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/

package org.theospi.portfolio.matrix.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.entitybroker.EntityBroker;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.access.HttpServletAccessProvider;
import org.sakaiproject.entitybroker.access.HttpServletAccessProviderManager;
import org.sakaiproject.tool.api.ActiveTool;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolException;
import org.sakaiproject.tool.cover.ActiveToolManager;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.util.Web;
import org.theospi.portfolio.matrix.MatrixCellEntityProvider;

/**
 * Does a redirect to allow basic DirectServlet access to matrix cell Entities
 * 
 */

public class RedirectingMatrixCellEntityServlet extends HttpServlet
  implements HttpServletAccessProvider {

  private static final long serialVersionUID = 0L;
  private EntityBroker entityBroker;
  private HttpServletAccessProviderManager accessProviderManager;
  
  /**
   * Initialize the servlet.
   * 
   * @param config
   *        The servlet config.
   * @throws ServletException
   */
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    entityBroker = (EntityBroker) ComponentManager
        .get("org.sakaiproject.entitybroker.EntityBroker");
    accessProviderManager = (HttpServletAccessProviderManager) ComponentManager
        .get("org.sakaiproject.entitybroker.access.HttpServletAccessProviderManager");
    if (accessProviderManager != null)
      accessProviderManager.registerProvider(MatrixCellEntityProvider.ENTITY_PREFIX, this);
  }
  
  public void handleAccess(HttpServletRequest req, HttpServletResponse res, EntityReference ref) {    
	  Session session = SessionManager.getCurrentSession();
	  if (SessionManager.getCurrentSessionUserId() == null) {
		  doLogin(req, res, session, req.getPathInfo());
		  return;
	  }
	  Map<String, String> props = entityBroker.getProperties(req.getPathInfo());
	  String target = props.get("url");
	  String user = props.get("security.user");
	  String site_function = props.get("security.site.function");
	  String site_secref = props.get("security.site.ref");
	  //decPageId is used to determine if a user has access to this cell.  It is a suggestion to make sure 
	  //the user has access to that page and that that page is linked to the current cell.
	  String decPageId = props.get("decPageId");

	  try {
		  
		  Object sessionAdvisors = session.getAttribute("sitevisit.security.advisor");
		  Set<SecurityAdvisor> siteAdvisors = new HashSet<SecurityAdvisor>();
		  if (sessionAdvisors != null) {
			  siteAdvisors = (Set<SecurityAdvisor>)sessionAdvisors;  
		  }
		  
		  siteAdvisors.add(new MySecurityAdvisor(user, site_function, site_secref));
		   		  
		  //dump a couple of advisors into session so we can get at them outside of this threadlocal
		  session.setAttribute("sitevisit.security.advisor", siteAdvisors);
		  session.setAttribute("decPageId", decPageId);
		  res.sendRedirect(target);		  
	  }
	  catch (IOException e) {
		  e.printStackTrace();
	  }
	  return;
  }

  private void doLogin(HttpServletRequest req, HttpServletResponse res, Session session, String returnPath) {
	  session.setAttribute(Tool.HELPER_DONE_URL, Web.returnUrl(req, returnPath));

	  ActiveTool tool = ActiveToolManager.getActiveTool("sakai.login");

	  // to skip container auth for this one, forcing things to be handled
	  // internaly, set the "extreme" login path

	  String loginPath = "/login";

	  String context = req.getContextPath() + req.getServletPath() + loginPath;
	  try {
		  tool.help(req, res, context, loginPath);
	  } catch (ToolException e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	  }
  }
  
  
  /**
   * A simple SecurityAdviser that can be used to override permissions on one reference string for one user for one function.
   */
  private class MySecurityAdvisor implements SecurityAdvisor
  {
	  protected String m_userId;

	  protected String m_function;

	  protected String m_reference;

	  public MySecurityAdvisor(String userId, String function, String reference)
	  {
		  m_userId = userId;
		  m_function = function;
		  m_reference = reference;
	  }

	  public SecurityAdvice isAllowed(String userId, String function, String reference)
	  {
		  SecurityAdvice rv = SecurityAdvice.PASS;
		  if (m_userId.equals(userId) && m_function.equals(function) && m_reference.equals(reference))
		  {
			  rv = SecurityAdvice.ALLOWED;
		  }
		  return rv;
	  }
	  
	  public boolean equals(Object obj) {
		  MySecurityAdvisor mine = (MySecurityAdvisor)obj;
		  if (mine == null) return false;
		  if (mine.m_userId == null && m_userId != null) return false;
		  if (mine.m_function == null && m_function != null) return false;
		  if (mine.m_reference == null && m_reference != null) return false;
		  if (mine.m_userId != null && m_userId == null) return false;
		  if (mine.m_function != null && m_function == null) return false;
		  if (mine.m_reference != null && m_reference == null) return false;
		  
		  if (m_userId.equals(mine.m_userId) && m_function.equals(mine.m_function) && m_reference.equals(mine.m_reference))
			  return true;
		  
		  return false;
	  }

	  public int hashCode() {
		  int result;
	      result = m_userId.hashCode();
	      result = 29 * result + (m_function != null ? m_function.hashCode() : 0);
	      result = 29 * result + (m_reference != null ? m_reference.hashCode() : 0);
	      return result;
	  }


  }
}
