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

import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.SignatureException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ComponentManager;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;

/**
 * Utility class for dealing with x-sakai-token semantics. Note: Class is thread
 * safe.
 */
@SuppressWarnings("PMD.LongVariable")
public class XSakaiToken {
	private static final Log LOG = LogFactory.getLog(XSakaiToken.class);
	public static final String X_SAKAI_TOKEN_HEADER = "x-sakai-token";
	public static final String CONFIG_PREFIX = "x.sakai.token";
	public static final String CONFIG_SHARED_SECRET_SUFFIX = "sharedSecret";
	public static final String TOKEN_SEPARATOR = ";";

	protected transient Signature signature = new Signature();
	private transient final SecureRandom secureRandom = new SecureRandom();
	// dependencies
	protected transient ComponentManager componentManager;
	protected transient ServerConfigurationService serverConfigurationService;
	protected transient SessionManager sessionManager;

	/**
	 * @param componentManager
	 *            Used to obtain references to
	 *            {@link ServerConfigurationService}, and {@link SessionManager}
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	public XSakaiToken(final ComponentManager componentManager) {
		LOG.debug("new XSakaiToken(ComponentManager componentManager)");
		if (componentManager == null) {
			throw new IllegalArgumentException("componentManager == null");
		}
		this.componentManager = componentManager;
		serverConfigurationService = (ServerConfigurationService) componentManager
				.get(ServerConfigurationService.class);
		if (serverConfigurationService == null) {
			throw new IllegalStateException(
					"serverConfigurationService == null");
		}
		sessionManager = (SessionManager) componentManager
				.get(SessionManager.class);
		if (sessionManager == null) {
			throw new IllegalStateException("sessionManager == null");
		}
	}

	/**
	 * Simply grab the x-sakai-token from the request. Does not validate
	 * results; i.e. raw data retrieval from request.
	 * 
	 * @param request
	 * @return token
	 * @throws IllegalArgumentException
	 */
	public String getToken(final HttpServletRequest request) {
		LOG.debug("getToken(final HttpServletRequest request)");
		if (request == null) {
			throw new IllegalArgumentException("request == null");
		}
		return request.getHeader(X_SAKAI_TOKEN_HEADER);
	}

	/**
	 * Validate the token using the passed sharedSecret and return username.
	 * 
	 * @param request
	 * @param sharedSecret
	 * @return eid
	 * @throws IllegalArgumentException
	 */
	public String getValidatedEid(final HttpServletRequest request,
			final String sharedSecret) {
		LOG.debug("getValidatedEid(final HttpServletRequest request, final String sharedSecret)");
		if (request == null) {
			throw new IllegalArgumentException("request == null");
		}
		if (sharedSecret == null || "".equals(sharedSecret)) {
			throw new IllegalArgumentException("sharedSecret == null || empty");
		}
		return getValidatedEid(getToken(request), sharedSecret);
	}

	/**
	 * Validate the token using the passed sharedSecret and return username.
	 * 
	 * @param token
	 *            null values are acceptable.
	 * @param sharedSecret
	 * @return eid if valid. null if not valid.
	 * @throws IllegalArgumentException
	 */
	public String getValidatedEid(final String token, final String sharedSecret) {
		LOG.debug("getValidatedEid(final String token, final String sharedSecret)");
		if (sharedSecret == null) {
			throw new IllegalArgumentException("sharedSecret == null");
		}
		@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
		String userId = null;
		if (token != null) {
			final String[] parts = token.split(TOKEN_SEPARATOR);
			if (parts.length == 3) {
				try {
					final String hash = parts[0];
					final String user = parts[1];
					final String nonce = parts[2];
					final String message = user + TOKEN_SEPARATOR + nonce;
					final String hmac = signature.calculateRFC2104HMAC(message,
							sharedSecret);
					if (hmac.equals(hash)) {
						// the user is Ok, we will trust it.
						userId = user;
					}
				} catch (InvalidKeyException e) {
					LOG.error("Failed to validate server token: " + token, e);
				}
			} else {
				LOG.error("Illegal number of elements in trusted server token: "
						+ token);
			}
		}
		return userId;
	}

	/**
	 * This is the preferred signature for the createToken methods as it looks
	 * up the current userId from the current session. Therefore it is a little
	 * safer.
	 * 
	 * @param hostname
	 *            Fully qualified domain name or an IP address. See:
	 *            {@link #getSharedSecret(String)}.
	 * @return token
	 * @throws IllegalArgumentException
	 */
	public String createToken(final String hostname) {
		LOG.debug("String createToken(final String hostname)");
		if (hostname == null || "".equals(hostname)) {
			throw new IllegalArgumentException("hostname == null || empty");
		}
		final Session session = sessionManager.getCurrentSession();
		if (session != null) {
			return createToken(hostname, session.getUserEid());
		} else {
			return createToken(hostname, "anonymous");
		}
	}

	/**
	 * Perform sharedSecret lookup from {@link #getSharedSecret(String)},
	 * compute hash based on eid and return token. If possible, you should use
	 * the {@link #createToken(String)} method signature as it is less error
	 * prone.
	 * 
	 * @param hostname
	 *            Fully qualified domain name or an IP address. See:
	 *            {@link #getSharedSecret(String)}.
	 * @param eid
	 *            Enterprise user id; usually a username.
	 * @return token
	 * @throws Error
	 *             Wrapped exception if there is any unexpected trouble.
	 * @throws IllegalArgumentException
	 */
	public String createToken(final String hostname, final String eid)
			throws Error {
		LOG.debug("createToken(final String hostname, final String eid)");
		if (hostname == null || "".equals(hostname)) {
			throw new IllegalArgumentException("hostname == null OR empty");
		}
		if (eid == null || "".equals(eid)) {
			throw new IllegalArgumentException("eid == null OR empty");
		}
		final String sharedSecret = getSharedSecret(hostname);
		final String token = signMessage(sharedSecret, eid);
		return token;
	}

	/**
	 * Compute hash based on sharedSecret and eid.
	 * 
	 * @param sharedSecret
	 * @param eid
	 *            Enterprise user id; usually a username.
	 * @return Fully computed token.
	 * @throws Error
	 *             Wrapped exception if there is any unexpected trouble.
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 *             Wraps {@link SignatureException} into a
	 *             {@link RuntimeException}.
	 */
	public String signMessage(final String sharedSecret, final String eid) {
		LOG.debug("signMessage(final String sharedSecret, final String eid)");
		if (sharedSecret == null || "".equals(sharedSecret)) {
			throw new IllegalArgumentException("sharedSecret == null OR empty");
		}
		if (eid == null || "".equals(eid)) {
			throw new IllegalArgumentException("eid == null OR empty");
		}
		@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
		String token = null;
		final String message = eid + TOKEN_SEPARATOR + secureRandom.nextLong();
		try {
			final String hash = signature.calculateRFC2104HMAC(message,
					sharedSecret);
			token = hash + TOKEN_SEPARATOR + message;
		} catch (InvalidKeyException e) {
			throw new IllegalStateException(e);
		}
		return token;
	}

	/**
	 * Look up a sharedSecret from sakai.properties. For example:<br/>
	 * <code>x.sakai.token.server.domain.name.sharedSecret=yourSecret</code> or <br/>
	 * <code>x.sakai.token.127.0.0.1.sharedSecret=yourSecret</code>
	 * 
	 * @param hostname
	 *            Fully qualified domain name or an IP address.
	 * @return null if not found.
	 * @throws IllegalArgumentException
	 */
	public String getSharedSecret(final String hostname) {
		LOG.debug("getSharedSecret(final String hostname)");
		if (hostname == null || "".equals(hostname)) {
			throw new IllegalArgumentException("hostname == null OR empty");
		}
		final String key = CONFIG_PREFIX + "." + hostname + "."
				+ CONFIG_SHARED_SECRET_SUFFIX;
		final String sharedSecret = serverConfigurationService.getString(key,
				null);
		return sharedSecret;
	}

}
