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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

@RunWith(MockitoJUnitRunner.class)
public class ToolRequestWrapperTest {
	ToolRequestWrapper toolRequestWrapper;

	@Mock
	HttpServletRequest request;

	@BeforeClass
	public static void beforeClass() {
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

	@Before
	public void setUp() throws Exception {
		request = mock(HttpServletRequest.class);
		when(request.getRemoteUser()).thenReturn("username");
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.ToolRequestWrapper#ToolRequestWrapper(javax.servlet.http.HttpServletRequest)}
	 * .
	 */
	@Test
	public void testToolRequestWrapperHttpServletRequest() {
		toolRequestWrapper = new ToolRequestWrapper(request);
		assertEquals("username", toolRequestWrapper.getRemoteUser());
	}

	/**
	 * Test method for
	 * {@link org.sakaiproject.hybrid.util.ToolRequestWrapper#ToolRequestWrapper(javax.servlet.http.HttpServletRequest, java.lang.String)}
	 * .
	 */
	@Test
	public void testToolRequestWrapperHttpServletRequestString() {
		toolRequestWrapper = new ToolRequestWrapper(request, "username2");
		assertEquals("username2", toolRequestWrapper.getRemoteUser());
	}

	@Test
	public void testToString() {
		toolRequestWrapper = new ToolRequestWrapper(request);
		final String str = toolRequestWrapper.toString();
		assertNotNull(str);
		// toString should contain wrapped username
		assertTrue(str.indexOf("username") > -1);
	}

}
