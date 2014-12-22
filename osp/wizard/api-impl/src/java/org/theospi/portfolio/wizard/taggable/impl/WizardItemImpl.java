/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.3/wizard/api-impl/src/java/org/theospi/portfolio/wizard/taggable/impl/WizardItemImpl.java $
 * $Id: WizardItemImpl.java 108812 2012-05-31 17:47:53Z chmaurer@iupui.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.theospi.portfolio.wizard.taggable.impl;

import java.util.Date;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.spring.util.SpringTool;
import org.sakaiproject.taggable.api.TaggableActivity;
import org.sakaiproject.taggable.api.TaggableItem;
import org.sakaiproject.util.ResourceLoader;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;

public class WizardItemImpl implements TaggableItem {

	TaggableActivity activity;

	WizardPage page;

	WizardReference reference;
	
	protected static final ResourceLoader messages = new ResourceLoader(
		"org.theospi.portfolio.wizard.bundle.Messages");

	public WizardItemImpl(WizardPage page, TaggableActivity activity) {
		this.page = page;
		this.activity = activity;

		reference = new WizardReference(WizardReference.REF_PAGE, page.getId()
				.toString());
	}

	public Object getObject() {
		return page;
	}

	public TaggableActivity getActivity() {
		return activity;
	}

	public String getContent() {
		return "";
	}

	public String getReference() {
		return reference.toString();
	}

	public String getTitle() {
		return page.getOwner().getDisplayName() + " - "
				+ page.getPageDefinition().getTitle();
	}

	public String getUserId() {
		return page.getOwner().getId().getValue();
	}
	
	public String getItemDetailPrivateUrl(){
		return getItemDetailUrl();
	}

	public String getItemDetailUrl()
	{
		String url = null;
		Site site = getSite(page.getPageDefinition().getSiteId());
		ToolConfiguration tc_matrix = site.getToolForCommonId("osp.matrix");
		ToolConfiguration tc_wizard = site.getToolForCommonId("osp.wizard");
		String placement = null;

		//pick one to start with
		String view = "viewCell.osp";
		if (page.getPageDefinition().getType().equals(WizardPageDefinition.WPD_WIZARD_HIER_TYPE)) {
			view="wizardPage.osp";
			if (tc_wizard != null)
				placement = tc_wizard.getId();
		}
		else if (page.getPageDefinition().getType().equals(WizardPageDefinition.WPD_WIZARD_SEQ_TYPE)) {
			view="sequentialWizardPage.osp";
			if (tc_wizard != null)
				placement = tc_wizard.getId();
		}
		else {
			if (tc_matrix != null)
				placement = tc_matrix.getId();
		}

		url = ServerConfigurationService.getServerUrl() + "/direct/matrixcell/"
				+ page.getId().getValue() + "/" + placement + "/" + view;
		return url;
	}
	
	public String getItemDetailUrlParams() {
		return "?panel=Main&TB_iframe=true&override." + SpringTool.LAST_VIEW_VISITED + "=/viewCell.osp";
	}
	
	public boolean getUseDecoration() {
		return true;
	}

	public String getIconUrl()
	{
		String url = ServerConfigurationService.getServerUrl() + "/library/image/silk/wand.png";
		
		if (page.getPageDefinition().getType().equals(WizardPageDefinition.WPD_MATRIX_TYPE))
			url = ServerConfigurationService.getServerUrl() + "/library/image/silk/table.png";
		return url;
	}
	
	public String getTypeName() {
		String type = messages.getString("wizard_pages_type");
		
		if (page.getPageDefinition().getType().equals(WizardPageDefinition.WPD_MATRIX_TYPE))
			type = messages.getString("matrix_cells_type");
		return type;
	}
	
	public boolean equals(Object obj)
	{
		if (!(obj instanceof WizardItemImpl))
			return false;
		else if (!((TaggableItem) obj).getReference().equals(this.getReference()))
			return false;
		
		return true;
	}
	
	public int hashCode()
	{
		return this.getReference().hashCode();
	}

	public String getOwner() {
		return ((WizardPage)getObject()).getOwner().getDisplayName();
	}

	public String getSiteTitle() {
		String siteId = ((WizardPage)getObject()).getPageDefinition().getSiteId();
		String title = getSite(siteId).getTitle();
		
		return title;
	}
	
	public Date getLastModifiedDate() {
		return ((WizardPage)getObject()).getModified();
	}
	
	private Site getSite(String siteId) {
		Site site = null;
		try {
			site = SiteService.getSite(siteId);
		} catch (IdUnusedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return site;
	}
}
