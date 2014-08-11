/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/osp/branches/sakai-2.8.x/matrix/api-impl/src/java/org/theospi/portfolio/matrix/model/impl/WizardPageDefinitionEntityImpl.java $
 * $Id: WizardPageDefinitionEntityImpl.java 74703 2010-03-16 15:54:31Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.matrix.model.impl;

import java.util.Stack;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.spring.util.SpringTool;
import org.sakaiproject.taggable.api.TagList;
import org.sakaiproject.util.BaseResourcePropertiesEdit;
import org.theospi.portfolio.matrix.WizardPageDefinitionEntity;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WizardPageDefinitionEntityImpl implements WizardPageDefinitionEntity
{

	private WizardPageDefinition wpd;
	private String parentTitle;
	
	public WizardPageDefinitionEntityImpl() {
		
	}
	
	public WizardPageDefinitionEntityImpl(WizardPageDefinition wpd, String parentTitle)
	{
		this.wpd = wpd;
		this.parentTitle = parentTitle;
	}
	
	public ResourceProperties getProperties()
	{
		ResourceProperties rp = new BaseResourcePropertiesEdit();
		rp.addProperty(TagList.PARENT, parentTitle);
		rp.addProperty(TagList.CRITERIA, wpd.getTitle());
		
		String server = ServerConfigurationService.getServerUrl();
		
		String foo = "<script type=\"text/javascript\" language=\"JavaScript\" src=\"" + server + "/library/js/jquery-ui-latest/js/jquery.min.js\"></script>" +
			"<script type=\"text/javascript\" language=\"JavaScript\"" +
			"src=\"" + server + "/osp-common-tool/js/thickbox.js\"></script>" +
			"<link href=\"" + server + "/osp-common-tool/css/thickbox.css\" type=\"text/css\"" +
			"rel=\"stylesheet\" media=\"all\" />";
		rp.addProperty(TagList.THICKBOX_INCLUDE, foo);

		return rp;
	}

	public String getReference(String rootProperty)
	{
		return wpd.getReference();
	}

	public String getUrl()
	{
		return getUrl(null);
	}

	public String getUrl(String rootProperty)
	{
		String url = null;
		String page_def_id = wpd.getId().getValue();
		try {
			Site site = SiteService.getSite(wpd.getSiteId());
		
			//try matrix first
			ToolConfiguration toolConfig;
			if(wpd.getType().equals(WizardPageDefinition.WPD_MATRIX_TYPE)){
				toolConfig = site.getToolForCommonId("osp.matrix");
			}else{
				toolConfig = site.getToolForCommonId("osp.wizard");
			}

			if(toolConfig != null){
				String placement = toolConfig.getId();
				url = ServerConfigurationService.getToolUrl() + "/" + placement +
					"/osp.matrix.cell.info.helper/viewCellInformation.osp?sCell_id=" + page_def_id + "&override." + SpringTool.LAST_VIEW_VISITED + "=/viewCell.osp";
			}
		} catch (IdUnusedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
	}

	public Element toXml(Document doc, Stack stack)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getId()
	{
		return wpd.getId().getValue();
	}

	public String getReference()
	{
		return wpd.getReference();
	}

	public WizardPageDefinition getWpd()
	{
		return wpd;
	}

	public void setWpd(WizardPageDefinition wpd)
	{
		this.wpd = wpd;
	}

	public String getParentTitle()
	{
		return parentTitle;
	}

	public void setParentTitle(String parentTitle)
	{
		this.parentTitle = parentTitle;
	}

}
