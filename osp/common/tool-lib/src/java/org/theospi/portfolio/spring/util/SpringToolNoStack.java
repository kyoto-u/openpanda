/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.2/common/tool-lib/src/java/org/theospi/portfolio/spring/util/SpringToolNoStack.java $
 * $Id: SpringToolNoStack.java 82737 2010-09-24 13:52:32Z chmaurer@iupui.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2010 The Sakai Foundation
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

package org.theospi.portfolio.spring.util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.spring.util.SpringTool;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;

public class SpringToolNoStack extends SpringTool {

	@Override
	protected void dispatch(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		super.dispatch(req, res);
		ToolSession session = SessionManager.getCurrentToolSession();
		session.removeAttribute(LAST_VIEW_VISITED);
	}

}
