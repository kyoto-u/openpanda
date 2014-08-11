/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/portal/branches/sakai-2.8.x/portal-render-impl/impl/src/java/org/sakaiproject/portal/render/portlet/services/state/Base64Recoder.java $
 * $Id: Base64Recoder.java 59680 2009-04-03 23:28:39Z arwhyte@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.portal.render.portlet.services.state;

import org.apache.commons.codec.binary.Base64;

/**
 * WebRecoder which uses a modified base64 encoding scheme to ensure web safety.
 * This sheme provides obfuscation as well as websafety.
 * 
 * @since Sakai 2.2.4
 * @version $Rev: 59680 $
 */
public class Base64Recoder implements WebRecoder
{

	/**
	 * Map of Base64 "unsafe" values to their "safe" counterparts.
	 */
	static final char[][] REPLACEMENTS = new char[][] { { '=', '_' }, { '+', '-' },
			{ '/', '!' } };

	public String encode(byte[] bits)
	{
		byte[] encoded = Base64.encodeBase64(bits);
		String unsafe = new String(encoded);
		for (int i = 0; i < REPLACEMENTS.length; i++)
		{
			unsafe = unsafe.replace(REPLACEMENTS[i][0], REPLACEMENTS[i][1]);
		}
		return unsafe;
	}

	public byte[] decode(String safe)
	{
		String unsafe = safe;
		for (int i = 0; i < REPLACEMENTS.length; i++)
		{
			unsafe = unsafe.replace(REPLACEMENTS[i][1], REPLACEMENTS[i][0]);
		}
		return Base64.decodeBase64(unsafe.getBytes());
	}
}
