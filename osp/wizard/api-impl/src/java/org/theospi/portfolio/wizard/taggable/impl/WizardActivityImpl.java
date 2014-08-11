/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.0/wizard/api-impl/src/java/org/theospi/portfolio/wizard/taggable/impl/WizardActivityImpl.java $
 * $Id: WizardActivityImpl.java 112290 2012-09-11 17:44:18Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2007, 2008 The Sakai Foundation
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

package org.theospi.portfolio.wizard.taggable.impl;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.spring.util.SpringTool;
import org.sakaiproject.taggable.api.TaggableActivity;
import org.sakaiproject.taggable.api.TaggableActivityProducer;
import org.sakaiproject.util.ResourceLoader;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;

public class WizardActivityImpl implements TaggableActivity {

	TaggableActivityProducer producer;

	WizardPageDefinition def;

	WizardReference reference;
	
	protected static final ResourceLoader messages = new ResourceLoader(
		"org.theospi.portfolio.wizard.bundle.Messages");

	public WizardActivityImpl(WizardPageDefinition def,
			TaggableActivityProducer producer) {
		this.def = def;
		this.producer = producer;
		reference = new WizardReference(WizardReference.REF_DEF, def.getId()
				.toString());
	}

	public Object getObject() {
		return def;
	}

	public String getContext() {
		return def.getSiteId();
	}

	public String getDescription() {
		return def.getDescription();
	}

	public TaggableActivityProducer getProducer() {
		return producer;
	}

	public String getReference() {
		return reference.toString();
	}

	public String getTitle() {
		return def.getTitle();
	}
	
	public String getActivityDetailUrl()
	{
		String url = null;
		try
		{
			String placement = null;

			//pick one to start with
			String view = "viewCell.osp";
			if (def.getType().equals(WizardPageDefinition.WPD_MATRIX_TYPE)) {
				placement = SiteService.getSite(def.getSiteId()).getToolForCommonId("osp.matrix").getId();
				view = "/osp.matrix.cell.info.helper/viewCellInformation.osp?override." + SpringTool.LAST_VIEW_VISITED + "=/viewCell.osp";
			}				
			else {
				placement = SiteService.getSite(def.getSiteId()).getToolForCommonId("osp.wizard").getId();
				view = "/osp.matrix.cell.info.helper/viewCellInformation.osp?override." + SpringTool.LAST_VIEW_VISITED + "=/viewCell.osp";
			}

			url = ServerConfigurationService.getServerUrl() + "/portal/tool/" + 
				placement + view + "?session.page_def_id=" + def.getId().getValue() +
				"&panel=Main";

		}
		catch (IdUnusedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
	}
	
	public String getTypeName() {
		String retValue = messages.getString("matrix_type");
		if (!def.getType().equals(WizardPageDefinition.WPD_MATRIX_TYPE))
			retValue = messages.getString("wizard_type");
		return retValue;
	}

	public String getActivityDetailUrlParams() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getUseDecoration() {
		// TODO Auto-generated method stub
		return false;
	}
}
