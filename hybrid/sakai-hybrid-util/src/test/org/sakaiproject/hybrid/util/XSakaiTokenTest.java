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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.security.InvalidKeyException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sakaiproject.component.api.ComponentManager;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * Unit tests for {@link XSakaiToken}.
 */
@RunWith(MockitoJUnitRunner.class)
public class XSakaiTokenTest {
	@SuppressWarnings(value = "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR", justification = "gets initialized in setup()")
	XSakaiToken xSakaiToken;
	private static final String MOCK_TOKEN = "+5JMkE44awf+2SWWZMyyzKFoJkE=;admin;-7838070940753586218";
	private static final String MOCK_BAD_TOKEN = "5JMkE44awf+2SWWZMyyzKFoJkE=;admin;-7838070940753586218";
	private static final String MOCK_MALFORMED_TOKEN = "+5JMkE44awf+2SWWZMyyzKFoJkE=;admin";
	private static final String MOCK_SHARED_SECRET = "e2KS54H35j6vS5Z38nK40";
	private static final String MOCK_HOSTNAME = "localhost";
	private static final String MOCK_EID = "admin";
	/**
	 * @see XSakaiToken#getSharedSecret(String)
	 */
	private static final String MOCK_SAKAI_PROP_KEY = XSakaiToken.CONFIG_PREFIX
			+ "." + MOCK_HOSTNAME + "."
			+ XSakaiToken.CONFIG_SHARED_SECRET_SUFFIX;
	@Mock
	ComponentManager componentManager;
	@Mock
	ServerConfigurationService serverConfigurationService;
	@Mock
	SessionManager sessionManager;
	@Mock
	HttpServletRequest request;
	@Mock
	Session session;
	@Mock
	protected transient Signature signature;

	@BeforeClass
	public static void setupClass() {
		Properties log4jProperties = new Properties();
		log4jProperties.put("log4j.rootLogger", "ALL, A1");
		log4jProperties.put("log4j.appender.A1",
				"org.apache.log4j.ConsoleAppender");
		log4jProperties.put("log4j.appender.A1.layout",
				"org.apache.log4j.PatternLayout");
		log4jProperties.put("log4j.appender.A1.layout.ConversionPattern",
				PatternLayout.TTCC_CONVERSION_PATTERN);
		log4jProperties.put("log4j.threshold", "ALL");
		PropertyConfigurator.configure(log4jProperties);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		when(componentManager.get(ServerConfigurationService.class))
				.thenReturn(serverConfigurationService);
		when(componentManager.get(SessionManager.class)).thenReturn(
				sessionManager);
		when(request.getHeader(XSakaiToken.X_SAKAI_TOKEN_HEADER)).thenReturn(
				MOCK_TOKEN);
		when(serverConfigurationService.getString(MOCK_SAKAI_PROP_KEY, null))
				.thenReturn(MOCK_SHARED_SECRET);
		when(session.getUserEid()).thenReturn(MOCK_EID);
		when(sessionManager.getCurrentSession()).thenReturn(session);
		when(signature.calculateRFC2104HMAC(anyString(), anyString()))
				.thenThrow(new InvalidKeyException());
		xSakaiToken = new XSakaiToken(componentManager);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#XSakaiToken(org.sakaiproject.component.api.ComponentManager)}
	 * .
	 */
	@Test
	public void testXSakaiToken() {
		// test good parameters first
		xSakaiToken = new XSakaiToken(componentManager);
		assertNotNull(xSakaiToken);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#XSakaiToken(org.sakaiproject.component.api.ComponentManager)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testXSakaiTokenIllegalArgumentException() {
		xSakaiToken = new XSakaiToken(null);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#XSakaiToken(org.sakaiproject.component.api.ComponentManager)}
	 * .
	 */
	@Test(expected = IllegalStateException.class)
	public void testXSakaiTokenNullServerConfigurationService() {
		when(componentManager.get(ServerConfigurationService.class))
				.thenReturn(null);
		xSakaiToken = new XSakaiToken(componentManager);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#XSakaiToken(org.sakaiproject.component.api.ComponentManager)}
	 * .
	 */
	@Test(expected = IllegalStateException.class)
	public void testXSakaiTokenNullSessionManager() {
		when(componentManager.get(SessionManager.class)).thenReturn(null);
		xSakaiToken = new XSakaiToken(componentManager);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#getToken(javax.servlet.http.HttpServletRequest)}
	 * .
	 */
	@Test
	public void testGetToken() {
		final String token = xSakaiToken.getToken(request);
		assertNotNull(token);
		assertEquals(MOCK_TOKEN, token);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#getToken(javax.servlet.http.HttpServletRequest)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetTokenIllegalArgumentException() {
		xSakaiToken.getToken(null);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#getValidatedEid(javax.servlet.http.HttpServletRequest, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetValidatedEidHttpServletRequestString() {
		final String eid = xSakaiToken.getValidatedEid(request,
				MOCK_SHARED_SECRET);
		assertNotNull(eid);
		assertEquals(MOCK_EID, eid);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#getValidatedEid(javax.servlet.http.HttpServletRequest, java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetValidatedEidNullHttpServletRequest() {
		// null HttpServletRequest
		xSakaiToken.getValidatedEid((HttpServletRequest) null,
				MOCK_SHARED_SECRET);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#getValidatedEid(javax.servlet.http.HttpServletRequest, java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetValidatedEidNullSharedSecret() {
		// null sharedSecret
		xSakaiToken.getValidatedEid(request, (String) null);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#getValidatedEid(javax.servlet.http.HttpServletRequest, java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetValidatedEidEmptySharedSecret() {
		// empty sharedSecret
		xSakaiToken.getValidatedEid(request, "");
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#getValidatedEid(javax.servlet.http.HttpServletRequest, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetValidatedEidInvalidKeyException() {
		xSakaiToken.signature = signature;
		final String eid = xSakaiToken.getValidatedEid(request,
				MOCK_SHARED_SECRET);
		assertNull(eid);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#getValidatedEid(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetValidatedEidStringString() {
		final String eid = xSakaiToken.getValidatedEid(MOCK_TOKEN,
				MOCK_SHARED_SECRET);
		assertNotNull(eid);
		assertEquals(MOCK_EID, eid);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#getValidatedEid(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetValidatedEidStringStringNullToken() {
		final String eid = xSakaiToken.getValidatedEid((String) null,
				MOCK_SHARED_SECRET);
		assertNull("eid should be null", eid);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#getValidatedEid(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetValidatedEidStringStringMalformedToken() {
		final String eid = xSakaiToken.getValidatedEid(MOCK_MALFORMED_TOKEN,
				MOCK_SHARED_SECRET);
		assertNull(eid);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#getValidatedEid(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetValidatedEidStringStringBadToken() {
		final String eid = xSakaiToken.getValidatedEid(MOCK_BAD_TOKEN,
				MOCK_SHARED_SECRET);
		assertNull(eid);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#getValidatedEid(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetValidatedEidStringStringNullSharedSecret() {
		xSakaiToken.getValidatedEid(MOCK_TOKEN, (String) null);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#getValidatedEid(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetValidatedEidStringStringEmptySharedSecret() {
		xSakaiToken.getValidatedEid(MOCK_TOKEN, "");
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#createToken(java.lang.String)}
	 * .
	 */
	@Test
	public void testCreateTokenString() {
		final String token1 = xSakaiToken.createToken(MOCK_HOSTNAME);
		final String token2 = xSakaiToken.createToken(MOCK_HOSTNAME);
		verifyTokens(token1, token2, MOCK_EID);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#createToken(java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateTokenStringNullHostname() {
		xSakaiToken.createToken(null);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#createToken(java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateTokenStringEmptyHostname() {
		xSakaiToken.createToken("");
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#createToken(java.lang.String)}
	 * .
	 */
	@Test
	public void testCreateTokenStringNullSession() {
		when(sessionManager.getCurrentSession()).thenReturn(null);
		final String token1 = xSakaiToken.createToken(MOCK_HOSTNAME);
		final String token2 = xSakaiToken.createToken(MOCK_HOSTNAME);
		verifyTokens(token1, token2, "anonymous");
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#createToken(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testCreateTokenStringString() {
		final String token1 = xSakaiToken.createToken(MOCK_HOSTNAME, MOCK_EID);
		final String token2 = xSakaiToken.createToken(MOCK_HOSTNAME, MOCK_EID);
		verifyTokens(token1, token2, MOCK_EID);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#createToken(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateTokenStringStringNullHostname() {
		xSakaiToken.createToken(null, MOCK_EID);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#createToken(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateTokenStringStringNullEid() {
		xSakaiToken.createToken(MOCK_HOSTNAME, null);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#createToken(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateTokenStringStringEmptyHostname() {
		xSakaiToken.createToken("", MOCK_EID);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#createToken(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateTokenStringStringEmptyEid() {
		xSakaiToken.createToken(MOCK_HOSTNAME, "");
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#signMessage(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testSignMessage() {
		final String token1 = xSakaiToken.signMessage(MOCK_SHARED_SECRET,
				MOCK_EID);
		final String token2 = xSakaiToken.signMessage(MOCK_SHARED_SECRET,
				MOCK_EID);
		verifyTokens(token1, token2, MOCK_EID);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#signMessage(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSignMessageNullSharedSecret() {
		xSakaiToken.signMessage(null, MOCK_EID);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#signMessage(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSignMessageNullEid() {
		xSakaiToken.signMessage(MOCK_SHARED_SECRET, null);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#signMessage(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSignMessageEmptyHostname() {
		xSakaiToken.signMessage("", MOCK_EID);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#signMessage(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSignMessageEmptyEid() {
		xSakaiToken.signMessage(MOCK_SHARED_SECRET, "");
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#signMessage(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalStateException.class)
	public void testSignMessageInvalidKeyException() {
		xSakaiToken.signature = signature;
		xSakaiToken.signMessage(MOCK_SHARED_SECRET, MOCK_EID);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#getSharedSecret(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetSharedSecretString() {
		final String sharedSecret = xSakaiToken.getSharedSecret(MOCK_HOSTNAME);
		assertNotNull(sharedSecret);
		assertEquals(MOCK_SHARED_SECRET, sharedSecret);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#getSharedSecret(java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetSharedSecretStringNullHostname() {
		xSakaiToken.getSharedSecret(null);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#getSharedSecret(java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetSharedSecretStringEmptyHostname() {
		// bad parameters; empty string
		xSakaiToken.getSharedSecret("");
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.XSakaiToken#getSharedSecret(java.lang.String)}
	 * .
	 */
	@Test()
	public void testGetSharedSecretStringNoSharedSecretDefined() {
		// no shared secret defined in sakai.properties
		when(serverConfigurationService.getString(MOCK_SAKAI_PROP_KEY, null))
				.thenReturn(null);
		final String sharedSecret = xSakaiToken.getSharedSecret(MOCK_HOSTNAME);
		assertNull(sharedSecret);
	}

	/**
	 * Verify that both tokens are not equal (due to changing nonce), but that
	 * they have the right amount of parts and the same eid.
	 * 
	 * @param token1
	 * @param token2
	 */
	private void verifyTokens(String token1, String token2, String eid) {
		assertNotNull(token1);
		assertNotNull(token2);
		// each token should be unique
		assertNotSame(token1, token2);
		final String[] parts1 = token1.split(XSakaiToken.TOKEN_SEPARATOR);
		final String[] parts2 = token2.split(XSakaiToken.TOKEN_SEPARATOR);
		// each token should have three elements
		assertEquals(3, parts1.length);
		assertEquals(3, parts2.length);
		// double check for empty strings
		assertTrue(parts1[0].length() > 0);
		assertTrue(parts2[0].length() > 0);
		assertTrue(parts1[2].length() > 0);
		assertTrue(parts2[2].length() > 0);
		// the eid should all be equal
		assertEquals(eid, parts1[1]);
		assertEquals(eid, parts2[1]);
		// hash should not be equal
		assertNotSame(parts1[0], parts2[0]);
		// nonce should not be equal
		assertNotSame(parts1[2], parts2[2]);
	}
}
