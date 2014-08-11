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

import java.security.InvalidKeyException;
import java.util.Properties;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests for {@link Signature} class.
 */
public class SignatureTest {
	protected transient Signature signature = new Signature();
	private static final String MOCK_DATA = "data";
	private static final String MOCK_KEY = "key";
	/**
	 * Used to test return vales for {@link #MOCK_DATA} and {@link #MOCK_KEY}
	 */
	private static final String TEST_HMAC = "EEFSxb/coHvGM+69RhmfAlXJ9J0=";
	/**
	 * Used to test return vales for {@link #MOCK_DATA} and {@link #MOCK_KEY}
	 */
	private static final String TEST_HMAC_URLSAFE = "EEFSxb_coHvGM-69RhmfAlXJ9J0";

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
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.Signature#calculateRFC2104HMAC(java.lang.String, java.lang.String)}
	 * .
	 * 
	 * @throws InvalidKeyException
	 */
	@Test
	public void testCalculateRFC2104HMACStringString()
			throws InvalidKeyException {
		final String hmac = signature.calculateRFC2104HMAC(MOCK_DATA, MOCK_KEY);
		assertNotNull(hmac);
		assertEquals(TEST_HMAC, hmac);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.Signature#calculateRFC2104HMAC(java.lang.String, java.lang.String)}
	 * .
	 * 
	 * @throws InvalidKeyException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCalculateRFC2104HMACStringStringNullData()
			throws InvalidKeyException {
		signature.calculateRFC2104HMAC(null, MOCK_KEY);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.Signature#calculateRFC2104HMAC(java.lang.String, java.lang.String)}
	 * .
	 * 
	 * @throws InvalidKeyException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCalculateRFC2104HMACStringStringNullKey()
			throws InvalidKeyException {
		signature.calculateRFC2104HMAC(MOCK_DATA, null);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.Signature#calculateRFC2104HMACWithEncoding(java.lang.String, java.lang.String, boolean)}
	 * .
	 * 
	 * @throws InvalidKeyException
	 */
	@Test
	public void testCalculateRFC2104HMACStringStringBoolean()
			throws InvalidKeyException {
		final String hmac = signature.calculateRFC2104HMACWithEncoding(
				MOCK_DATA, MOCK_KEY, true);
		assertNotNull(hmac);
		assertEquals(TEST_HMAC_URLSAFE, hmac);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.Signature#calculateRFC2104HMACWithEncoding(java.lang.String, java.lang.String, boolean)}
	 * .
	 * 
	 * @throws InvalidKeyException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCalculateRFC2104HMACStringStringBooleanNullData()
			throws InvalidKeyException {
		signature.calculateRFC2104HMACWithEncoding(null, MOCK_KEY, true);
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.Signature#calculateRFC2104HMACWithEncoding(java.lang.String, java.lang.String, boolean)}
	 * .
	 * 
	 * @throws InvalidKeyException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCalculateRFC2104HMACStringStringBooleanNullKey()
			throws InvalidKeyException {
		signature.calculateRFC2104HMACWithEncoding(MOCK_DATA, null, true);
	}

	/**
	 * @see Signature#Signature()
	 * @see Signature#Signature(String)
	 */
	@Test(expected = IllegalStateException.class)
	public void testSignatureNoSuchAlgorithmException() {
		new Signature("some bad algorithm");
	}

	/**
	 * @see Signature#Signature()
	 * @see Signature#Signature(String)
	 */
	@Test()
	public void testSignatureString() {
		new Signature("HmacSHA1");
	}
}
