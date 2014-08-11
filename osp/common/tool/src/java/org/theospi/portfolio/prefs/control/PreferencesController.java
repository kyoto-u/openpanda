/**********************************************************************************
 * $URL$
 * $Id$
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

package org.theospi.portfolio.prefs.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.event.api.NotificationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.PreferencesEdit;
import org.sakaiproject.user.api.PreferencesService;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class PreferencesController extends SimpleFormController {

	private PreferencesService preferencesService = null;
	private AuthenticationManager authManager = null;
	private ToolManager toolManager = null;
	private WorksiteManager worksiteManager = null;

	protected final Log logger = LogFactory.getLog(getClass());
	
	@Override
	protected void onBindOnNewForm(HttpServletRequest request, Object command) throws Exception {
		super.onBindOnNewForm(request, command);
		NotificationPreferenceBean npb = (NotificationPreferenceBean)command;
		//populateBackingBean(request, npb);
		
		PreferencesEdit prefEdit = getPreferencesEdit();
		if (prefEdit != null) {
			//Find the default
			ResourcePropertiesEdit defautProps = prefEdit.getPropertiesEdit(NotificationService.PREFS_TYPE + npb.getTypeKey());
			String defaultProp = (String)defautProps.get(Integer.valueOf(NotificationService.NOTI_OPTIONAL).toString());
			if (defaultProp != null) {
				npb.setDefaultOption(Integer.valueOf(defaultProp).intValue());
			}		
			
			//Find the site override
			ResourcePropertiesEdit props = prefEdit.getPropertiesEdit(NotificationService.PREFS_TYPE + npb.getTypeKey() + NotificationService.NOTI_OVERRIDE_EXTENSION);
			String prop = (String)props.get(getToolManager().getCurrentPlacement().getContext());
			if (prop != null) {
				npb.setNotificationOption(Integer.valueOf(prop).intValue());
			}
			
			//If nothing is set, use the default (which should be set for the bean)
			if (npb.getNotificationOption() == NotificationService.PREF_NONE) {
				npb.setNotificationOption(npb.getDefaultOption());
			}				
			
			getPreferencesService().cancel(prefEdit);
		}

		logger.debug("onBindOnNewForm(): " + npb.toString());
	}

	@Override
	protected ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)	throws Exception {	
		Map<String, Object> model = new HashMap<String, Object>();

		NotificationPreferenceBean npb = (NotificationPreferenceBean)command;
		logger.debug("processFormSubmission(): " + npb.toString());
		
		passThroughToModel(npb, model);

		model.put(NotificationPreferenceBean.READY_TO_CLOSE_KEY, true);

		PreferencesEdit prefEdit = getPreferencesEdit();

		List<Site> siteList = new ArrayList<Site>();

		if (request.getParameter("update") != null) {
			Site site = getWorksiteManager().getSite(getToolManager().getCurrentPlacement().getContext());
			siteList.add(site);
			model.put(NotificationPreferenceBean.PREFS_SAVED_DIV_TO_RETURN_KEY, npb.getPrefsSiteSavedDiv());
		}
		else if (request.getParameter("updateAll") != null) {
			siteList.addAll(getSitesWithTool(npb.getToolId()));
			model.put(NotificationPreferenceBean.PREFS_SAVED_DIV_TO_RETURN_KEY, npb.getPrefsAllSavedDiv());
		}

		if (prefEdit != null) {

			ResourcePropertiesEdit props = prefEdit.getPropertiesEdit(NotificationService.PREFS_TYPE + npb.getTypeKey() + NotificationService.NOTI_OVERRIDE_EXTENSION);

			for (Site site : siteList) {
				props.addProperty(site.getId(), Integer.toString(npb.getNotificationOption()));
			}

			try {
				getPreferencesService().commit(prefEdit);
			}
			catch (Exception e) {
				logger.warn("Problem saving preferences for site notifications in processFormSubmission().", e);
			}
		}

		return new ModelAndView(getSuccessView(), model);
	}
	
	private void passThroughToModel(NotificationPreferenceBean npb, Map<String, Object> model) {
		model.put(NotificationPreferenceBean.DIALOG_DIV_ID_KEY, npb.getDialogDivId());
		model.put(NotificationPreferenceBean.PREFS_ALL_SAVED_DIV_KEY, npb.getPrefsAllSavedDiv());
		model.put(NotificationPreferenceBean.PREFS_SITE_SAVED_DIV_KEY, npb.getPrefsSiteSavedDiv());
		model.put(NotificationPreferenceBean.QUALIFIER_TEXT_KEY, npb.getQualifier_text());
		model.put(NotificationPreferenceBean.TYPEKEY_KEY, npb.getTypeKey());
		model.put(NotificationPreferenceBean.TOOLID_KEY, npb.getToolId());
		model.put(NotificationPreferenceBean.FRAMEID_KEY, npb.getFrameId());
		model.put(NotificationPreferenceBean.DEFAULTOPTION_KEY, npb.getDefaultOption());
		
	}

	/**
	 * Returns all sites that contain the passed toolId
	 * @param toolId
	 * @return
	 */
	private List<Site> getSitesWithTool(String toolId) {
		List<Site> siteList = new ArrayList<Site>();
		List<Site> fullList = getWorksiteManager().getUserSites();
		for (Site site : fullList) {
			if (site.getToolForCommonId(toolId) != null)
				siteList.add(site);
		}
		
		return siteList;
	}

	/** Return PreferencesEdit object for current user
	 **/
	private PreferencesEdit getPreferencesEdit() {

		if (logger.isDebugEnabled()) {
			logger.debug("In getPreferencesEdit(): ");
			//Thread.dumpStack();
		}

		PreferencesEdit prefEdit = null;
		try {
			prefEdit = (PreferencesEdit) getPreferencesService().add(authManager.getAgent().getId().getValue());
		} catch (PermissionException e) {
			logger.warn("Problem getting preferences for site notifications in getPreferencesEdit().", e);
		} catch (IdUsedException e) {
			// Preferences already exist, just edit
			try {
				prefEdit = (PreferencesEdit) getPreferencesService().edit(authManager.getAgent().getId().getValue());
			} catch (PermissionException e1) {
				logger.warn("Problem getting preferences for site notifications in getPreferencesEdit().", e1);
			} catch (InUseException e1) {
				logger.warn("Problem getting preferences for site notifications in getPreferencesEdit().", e1);
			} catch (IdUnusedException e1) {
				// This should be safe to ignore since we got here because it existed
				logger.warn("Problem getting preferences for site notifications in getPreferencesEdit().", e1);
			}
		}
		return prefEdit;
	}

	public void setPreferencesService(PreferencesService preferencesService) {
		this.preferencesService = preferencesService;
	}

	public PreferencesService getPreferencesService() {
		return preferencesService;
	}

	/**
	 * @return
	 */
	public AuthenticationManager getAuthManager() {
		return authManager;
	}

	/**
	 * @param manager
	 */
	public void setAuthManager(AuthenticationManager manager) {
		authManager = manager;
	}

	public void setToolManager(ToolManager toolManager) {
		this.toolManager = toolManager;
	}

	public ToolManager getToolManager() {
		return toolManager;
	}

	public void setWorksiteManager(WorksiteManager worksiteManager) {
		this.worksiteManager = worksiteManager;
	}

	public WorksiteManager getWorksiteManager() {
		return worksiteManager;
	}
}
