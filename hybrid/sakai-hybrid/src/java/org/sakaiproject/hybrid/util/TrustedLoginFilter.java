/**
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.sakaiproject.hybrid.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ComponentManager;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * <pre>
 *  A filter to come after the standard sakai request filter to allow services
 *  to encode a token containing the user id accessing the service. 
 *  
 *  The filter must be configured with a shared secret and requests contain a 
 *  header "x-sakai-token". This token is used to validate the Request and
 *  associate a user with the request.
 *  
 *  The token contains:
 *  hash;user
 *  
 *  hash is a Base64 encoded HMAC hash, user is the username to associate with the request.
 *  
 *  The shared secret must be known by both ends of the conversation, and must not be distributed outside a trusted zone.
 *  
 *  To use this filter add it AFTER the Sakai Request Filter in you web.xml like
 *  
 *  
 *  	&lt;!-- 
 * 	The Sakai Request Hander 
 * 	--&gt;
 * 	&lt;filter&gt;
 * 		&lt;filter-name&gt;sakai.request&lt;/filter-name&gt;
 * 		&lt;filter-class&gt;org.sakaiproject.util.RequestFilter&lt;/filter-class&gt;
 * 	&lt;/filter&gt;
 * 	&lt;filter&gt;
 * 		&lt;filter-name&gt;sakai.trusted&lt;/filter-name&gt;
 * 		&lt;filter-class&gt;org.sakaiproject.hybrid.util.TrustedLoginFilter&lt;/filter-class&gt;
 *       &lt;init-param&gt;
 *       	&lt;param-name&gt;shared.secret&lt;/param-name&gt;
 *           &lt;param-value&gt;The Snow on the Volga falls only under the bridges&lt;/param-value&gt;
 *       &lt;/init-param&gt;
 * 	&lt;/filter&gt;
 * 	
 * 	&lt;!--
 * 	Mapped onto Handler
 * 	--&gt;
 * 	&lt;filter-mapping&gt;
 * 		&lt;filter-name&gt;sakai.request&lt;/filter-name&gt;
 * 		&lt;servlet-name&gt;sakai.mytoolservlet&lt;/servlet-name&gt;
 * 		&lt;dispatcher&gt;REQUEST&lt;/dispatcher&gt;
 * 		&lt;dispatcher&gt;FORWARD&lt;/dispatcher&gt;
 * 		&lt;dispatcher&gt;INCLUDE&lt;/dispatcher&gt;
 * 	&lt;/filter-mapping&gt; 
 * 
 * 	&lt;filter-mapping&gt;
 * 		&lt;filter-name&gt;sakai.trusted&lt;/filter-name&gt;
 * 		&lt;servlet-name&gt;sakai.mytoolservlet&lt;/servlet-name&gt;
 * 		&lt;dispatcher&gt;REQUEST&lt;/dispatcher&gt;
 * 		&lt;dispatcher&gt;FORWARD&lt;/dispatcher&gt;
 * 		&lt;dispatcher&gt;INCLUDE&lt;/dispatcher&gt;
 * 	&lt;/filter-mapping&gt;
 * 
 * </pre>
 * 
 */
@SuppressWarnings({ "PMD.LongVariable", "PMD.CyclomaticComplexity" })
public class TrustedLoginFilter implements Filter {
	private final static Log LOG = LogFactory.getLog(TrustedLoginFilter.class);
	/**
	 * sakai.properties
	 */
	public static final String ORG_SAKAIPROJECT_UTIL_TRUSTED_LOGIN_FILTER_SHARED_SECRET = "org.sakaiproject.hybrid.util.TrustedLoginFilter.sharedSecret";
	/**
	 * sakai.properties
	 */
	public static final String ORG_SAKAIPROJECT_UTIL_TRUSTED_LOGIN_FILTER_ENABLED = "org.sakaiproject.hybrid.util.TrustedLoginFilter.enabled";
	/**
	 * sakai.properties
	 */
	public static final String ORG_SAKAIPROJECT_UTIL_TRUSTED_LOGIN_FILTER_SAFE_HOSTS = "org.sakaiproject.hybrid.util.TrustedLoginFilter.safeHosts";

	protected transient Signature signature = new Signature();
	protected transient XSakaiToken xSakaiToken = null;

	protected transient ComponentManager componentManager;
	protected transient ServerConfigurationService serverConfigurationService;
	protected transient SessionManager sessionManager;
	protected transient UserDirectoryService userDirectoryService;

	/**
	 * Property to contain the shared secret used by all trusted servers. The
	 * shared secret used for server to server trusted tokens.
	 */
	protected transient String sharedSecret = null;
	/**
	 * True if server tokens are enabled. If true, trusted tokens from servers
	 * are accepted considered.
	 */
	protected transient boolean enabled = true;
	/**
	 * A list of all the known safe hosts to trust as servers. A ; separated
	 * list of hosts that this instance trusts to make server connections.
	 */
	protected transient String safeHosts = "localhost;127.0.0.1;0:0:0:0:0:0:0:1%0";

	/**
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.OnlyOneReturn",
			"PMD.AvoidDeeplyNestedIfStmts", "PMD.DataflowAnomalyAnalysis" })
	public void doFilter(final ServletRequest req, final ServletResponse resp,
			final FilterChain chain) throws IOException, ServletException {
		if (enabled && req instanceof HttpServletRequest) {
			HttpServletRequest hreq = (HttpServletRequest) req;
			final String host = req.getRemoteHost();
			if (safeHosts.indexOf(host) < 0) {
				LOG.warn("Ignoring Trusted Token request from: " + host);
				chain.doFilter(req, resp);
				return;
			} else {
				Session currentSession = null;
				Session requestSession = null;
				final String trustedUserName = xSakaiToken.getValidatedEid(
						hreq, sharedSecret);
				if (trustedUserName != null) {
					currentSession = sessionManager.getCurrentSession();
					if (!trustedUserName.equals(currentSession.getUserEid())) {
						User user = null;
						try {
							user = userDirectoryService
									.getUserByEid(trustedUserName);
						} catch (UserNotDefinedException e) {
							LOG.warn(trustedUserName + " not found!");
						}
						if (user != null) {
							requestSession = sessionManager.startSession();
							requestSession.setUserEid(user.getEid());
							requestSession.setUserId(user.getId());
							requestSession.setActive();
							sessionManager.setCurrentSession(requestSession);
							// wrap the request so that we can get the user
							// via getRemoteUser() in other places.
							if (!(hreq instanceof ToolRequestWrapper)) {
								hreq = new ToolRequestWrapper(hreq,
										trustedUserName);
							}
						}
					}
				}
				try {
					chain.doFilter(hreq, resp);
				} finally {
					if (requestSession != null) {
						requestSession.invalidate();
					}
					if (currentSession != null) {
						sessionManager.setCurrentSession(currentSession);
					}
				}
			}
		} else {
			chain.doFilter(req, resp);
			return;
		}
	}

	/**
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(final FilterConfig config) throws ServletException {
		if (componentManager == null) {
			componentManager = org.sakaiproject.component.cover.ComponentManager
					.getInstance();
		}
		serverConfigurationService = (ServerConfigurationService) componentManager
				.get(ServerConfigurationService.class);
		if (serverConfigurationService == null) {
			throw new IllegalStateException(
					"ServerConfigurationService == null");
		}
		sessionManager = (SessionManager) componentManager
				.get(org.sakaiproject.tool.api.SessionManager.class);
		if (sessionManager == null) {
			throw new IllegalStateException("SessionManager == null");
		}
		userDirectoryService = (UserDirectoryService) componentManager
				.get(UserDirectoryService.class);
		if (userDirectoryService == null) {
			throw new IllegalStateException("UserDirectoryService == null");
		}
		xSakaiToken = new XSakaiToken(componentManager);
		// default to true - enabled
		enabled = serverConfigurationService.getBoolean(
				ORG_SAKAIPROJECT_UTIL_TRUSTED_LOGIN_FILTER_ENABLED, enabled);
		sharedSecret = serverConfigurationService.getString(
				ORG_SAKAIPROJECT_UTIL_TRUSTED_LOGIN_FILTER_SHARED_SECRET,
				sharedSecret);
		// default to localhost
		safeHosts = serverConfigurationService.getString(
				ORG_SAKAIPROJECT_UTIL_TRUSTED_LOGIN_FILTER_SAFE_HOSTS,
				safeHosts);
	}

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// nothing to do here
	}

	/**
	 * Only used for unit testing setup.
	 * 
	 * @param componentManager
	 */
	protected void setupTestCase(final ComponentManager componentManager) {
		if (componentManager == null) {
			throw new IllegalArgumentException("componentManager == null");
		}
		this.componentManager = componentManager;
	}

}
