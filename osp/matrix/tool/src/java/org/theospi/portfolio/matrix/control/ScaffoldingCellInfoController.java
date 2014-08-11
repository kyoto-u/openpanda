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
package org.theospi.portfolio.matrix.control;

import java.util.HashMap;
import java.util.Map;

import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;

public class ScaffoldingCellInfoController extends AbstractMatrixController
{

	public ModelAndView handleRequest(Object requestModel, Map request, Map session,
			Map application, Errors errors)
	{
		Map<String, Object> model = new HashMap<String, Object>();
		String pageId = (String)request.get("pageId");
		
		if (pageId != null) {
			ScaffoldingCell sCell = (ScaffoldingCell) requestModel;
			sCell = getMatrixManager().getScaffoldingCell(getIdManager().getId(pageId));
			model.put("scaffoldingCell", sCell);
		}
		
		return new ModelAndView("success", model);
	}

}
