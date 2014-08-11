/**********************************************************************************
 * $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/tool/src/java/org/theospi/portfolio/matrix/control/EditScaffoldingCellConfirmationController.java $
 * $Id:EditScaffoldingCellConfirmationController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

package org.theospi.portfolio.matrix.control;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;

public class EditScaffoldingCellConfirmationController extends
		BaseScaffoldingCellController implements Controller, FormController,
		LoadObjectController {

	protected final Log logger = LogFactory.getLog(getClass());

	public ModelAndView handleRequest(Object requestModel, Map request,
			Map session, Map application, Errors errors) {
		ScaffoldingCell scaffoldingCell = (ScaffoldingCell) requestModel;
		String viewName = "success";
		Id id = scaffoldingCell.getId();

		Map model = new HashMap();
		model.put("scaffoldingCell_id", id);

		String cancel = (String) request.get("cancel");
		String next = (String) request.get("continue");
		if (cancel != null) {
			viewName = "cancel";
		} else if (next != null) {
			saveScaffoldingCell(request, scaffoldingCell);
			model.put("scaffolding_id", scaffoldingCell.getScaffolding()
					.getId());
			if (getTaggingManager().isTaggable()) {
				session.remove(PROVIDERS_PARAM);
			}
		}

		session.remove(EditedScaffoldingStorage.STORED_SCAFFOLDING_FLAG);
		session
				.remove(EditedScaffoldingStorage.EDITED_SCAFFOLDING_STORAGE_SESSION_KEY);

		return new ModelAndView(viewName, model);
	}

	public Map referenceData(Map request, Object command, Errors errors) {
		Map model = new HashMap();
		model.put("label", "Scaffolding Cell");
		if(request.containsKey(MatrixManager.CONFIRM_PUBLISHED_FLAG)){
			model.put("published", request.get(MatrixManager.CONFIRM_PUBLISHED_FLAG));
		}
		if(request.containsKey(MatrixManager.CONFIRM_EVAL_VIEW_ALL_GROUPS_FLAG)){
			model.put("warnViewAllGroupsEval", request.get(MatrixManager.CONFIRM_EVAL_VIEW_ALL_GROUPS_FLAG));
		}
		return model;
	}
}
