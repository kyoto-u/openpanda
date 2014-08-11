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

import org.sakaiproject.event.api.NotificationService;

/**
 * See NotificationService for constants 
 * @author chrismaurer
 *
 */
public class NotificationPreferenceBean {

	public static String TYPEKEY_KEY = "typeKey";
	public static String QUALIFIER_TEXT_KEY = "qualifier_text";
	public static String DIALOG_DIV_ID_KEY = "dialogDivId";
	public static String READY_TO_CLOSE_KEY = "readyToClose";
	public static String PREFS_SITE_SAVED_DIV_KEY = "prefsSiteSavedDiv";
	public static String PREFS_ALL_SAVED_DIV_KEY = "prefsAllSavedDiv";
	public static String PREFS_SAVED_DIV_TO_RETURN_KEY = "prefsSavedDivToReturn";
	public static String TOOLID_KEY = "toolId";
	public static String FRAMEID_KEY = "frameId";
	public static String DEFAULTOPTION_KEY = "defautOption";
	
	
	private int notificationOption = NotificationService.PREF_NONE;
	private String typeKey = "";
	private String qualifier_text = "";
	private String dialogDivId = "";
	private boolean readyToClose = false;
	private String prefsSiteSavedDiv = "";
	private String prefsAllSavedDiv = "";
	private String prefsSavedDivToReturn = "";
	private String toolId = "";
	private String frameId = "";
	private int defaultOption = NotificationService.PREF_IMMEDIATE;
	
	public NotificationPreferenceBean() {
		;
	}
	
	public int getNotificationOption() {
		return notificationOption;
	}

	public void setNotificationOption(int notificationOption) {
		this.notificationOption = notificationOption;		
	}

	public void setTypeKey(String typeKey) {
		this.typeKey = typeKey;
	}

	public String getTypeKey() {
		return typeKey;
	}

	public void setQualifier_text(String qualifier_text) {
		this.qualifier_text = qualifier_text;
	}

	public String getQualifier_text() {
		return qualifier_text;
	}

	public void setDialogDivId(String dialogDivId) {
		this.dialogDivId = dialogDivId;
	}

	public String getDialogDivId() {
		return dialogDivId;
	}

	public void setReadyToClose(boolean readyToClose) {
		this.readyToClose = readyToClose;
	}

	public boolean isReadyToClose() {
		return readyToClose;
	}

	public String getPrefsSiteSavedDiv() {
		return prefsSiteSavedDiv;
	}

	public void setPrefsSiteSavedDiv(String prefsSiteSavedDiv) {
		this.prefsSiteSavedDiv = prefsSiteSavedDiv;
	}

	public String getPrefsAllSavedDiv() {
		return prefsAllSavedDiv;
	}

	public void setPrefsAllSavedDiv(String prefsAllSavedDiv) {
		this.prefsAllSavedDiv = prefsAllSavedDiv;
	}

	public void setPrefsSavedDivToReturn(String prefsSavedDivToReturn) {
		this.prefsSavedDivToReturn = prefsSavedDivToReturn;
	}

	public String getPrefsSavedDivToReturn() {
		return prefsSavedDivToReturn;
	}

	public void setToolId(String toolId) {
		this.toolId = toolId;
	}

	public String getToolId() {
		return toolId;
	}

	public void setFrameId(String frameId) {
		this.frameId = frameId;
	}

	public String getFrameId() {
		return frameId;
	}

	public void setDefaultOption(int defaultOption) {
		this.defaultOption = defaultOption;
	}

	public int getDefaultOption() {
		return defaultOption;
	}

	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("NotificationPreference(");
		strBuf.append("dialogDivId: " + dialogDivId);
		strBuf.append(",notificationOption: " + String.valueOf(notificationOption));
		strBuf.append(",prefsAllSavedDiv: " + prefsAllSavedDiv);
		strBuf.append(",prefsSavedDivToReturn: " + prefsSavedDivToReturn);
		strBuf.append(",prefsSiteSavedDiv: " + prefsSiteSavedDiv);
		strBuf.append(",qualifierText: " + qualifier_text);
		strBuf.append(",readyToClose: " + Boolean.toString(readyToClose));
		strBuf.append(",typeKey: " + typeKey);
		strBuf.append(",toolId: " + toolId);
		strBuf.append(",frameId: " + frameId);
		strBuf.append(",defaultOption: " + defaultOption);
		strBuf.append(")");
		return strBuf.toString();
	}

}
