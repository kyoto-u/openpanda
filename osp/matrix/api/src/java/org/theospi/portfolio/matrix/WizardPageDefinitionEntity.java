/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/osp/branches/oncourse_osp_enhancements/osp/matrix/api-impl/src/java/org/theospi/portfolio/matrix/model/impl/WizardPageDefinitionEntity.java $
 * $Id: WizardPageDefinitionEntity.java 41530 2008-02-22 19:55:07Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.matrix;


import org.sakaiproject.entity.api.Entity;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;

public interface WizardPageDefinitionEntity extends Entity
{
	public WizardPageDefinition getWpd();

	public void setWpd(WizardPageDefinition wpd);

	public String getParentTitle();
	
	public void setParentTitle(String parentTitle);
}
