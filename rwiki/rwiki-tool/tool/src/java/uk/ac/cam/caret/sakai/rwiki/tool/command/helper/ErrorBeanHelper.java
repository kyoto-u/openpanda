/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/rwiki/tags/sakai-2.9.0/rwiki-tool/tool/src/java/uk/ac/cam/caret/sakai/rwiki/tool/command/helper/ErrorBeanHelper.java $
 * $Id: ErrorBeanHelper.java 20354 2007-01-17 10:30:57Z ian@caret.cam.ac.uk $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006 The Sakai Foundation.
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

package uk.ac.cam.caret.sakai.rwiki.tool.command.helper;

import javax.servlet.http.HttpServletRequest;

import uk.ac.cam.caret.sakai.rwiki.tool.RequestScopeSuperBean;
import uk.ac.cam.caret.sakai.rwiki.tool.bean.ErrorBean;

/**
 * @author andrew
 */
public class ErrorBeanHelper
{

	public static ErrorBean getErrorBean(HttpServletRequest request)
	{

		RequestScopeSuperBean rssb = RequestScopeSuperBean
				.getFromRequest(request);

		ErrorBean errorBean = rssb.getErrorBean();
		return errorBean;
	}
}
