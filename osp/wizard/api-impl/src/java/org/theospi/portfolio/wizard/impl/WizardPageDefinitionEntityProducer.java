/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/osp/branches/oncourse_osp_enhancements/osp/matrix/api-impl/src/java/org/theospi/portfolio/matrix/model/impl/WizardPageDefinitionEntityProducer.java $
 * $Id: WizardPageDefinitionEntityProducer.java 41530 2008-02-22 19:55:07Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.wizard.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.metaobj.shared.mgt.EntityProducerBase;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.WizardPageDefinitionEntity;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.WizardPageSequence;

public class WizardPageDefinitionEntityProducer extends EntityProducerBase
{
	protected final Log logger = LogFactory.getLog(getClass());
	protected WizardManager wizardManager;
	protected MatrixManager matrixManager;
	protected IdManager idManager;

	public void init() {
		logger.info("init()");
		try {
			getEntityManager().registerEntityProducer(this, Entity.SEPARATOR + WizardPageDefinition.WPD_ENTITY_STRING);
		}
		catch (Exception e) {
			logger.warn("Error registering WizardPageDefinition Entity Producer", e);
		}
	}
	
	public void destroy() {
		logger.info("destroy()");
	}

	public boolean parseEntityReference(String reference, Reference ref) {
		if (reference.startsWith(getContext())) {
			String[] parts = reference.split(Entity.SEPARATOR, 5);
			if (parts.length < 5) {
				return false;
			}
			String type = parts[1];
			/*
			 * This is only really used so we know what kind of object we are
			 * referencing
			 */
			String subtype = parts[4];

			String context = parts[2];
			/*
			 * This is only used when we have a reference to a specific object
			 */
			String id = parts[3];

			ref.set(type, subtype, id, null, context);
			
			return true;
		}
		return false;
	}

	public String getLabel()
	{
		return WizardPageDefinition.WPD_ENTITY_STRING;
	}
	
	public Entity getEntity(Reference ref) {
		WizardPageDefinitionEntity wpde = null;
		
		try {
		if (ref.getSubType().equals(WizardPageDefinition.WPD_MATRIX_TYPE)) {
			ScaffoldingCell sCell = matrixManager.getScaffoldingCellByWizardPageDef(idManager.getId(ref.getId()));
			Scaffolding scaff = matrixManager.getScaffolding(sCell.getScaffolding().getId());

			wpde = matrixManager.createWizardPageDefinitionEntity(sCell.getWizardPageDefinition(), scaff.getTitle());
		}
		else {
			WizardPageSequence wps = wizardManager.getWizardPageSeqByDef(idManager.getId(ref.getId()));
			String title = wps.getCategory().getWizard().getName();
			wpde = matrixManager.createWizardPageDefinitionEntity(wps.getWizardPageDefinition(), title);
		}
		}
		catch (NullPointerException npe) {
			logger.error("Unable to get entity with reference: " + ref.getReference(), npe);
		}

		return wpde;
	}


	public WizardManager getWizardManager()
	{
		return wizardManager;
	}

	public void setWizardManager(WizardManager wizardManager)
	{
		this.wizardManager = wizardManager;
	}
	
	public IdManager getIdManager()
	{
		return idManager;
	}

	public void setIdManager(IdManager idManager)
	{
		this.idManager = idManager;
	}

	public MatrixManager getMatrixManager()
	{
		return matrixManager;
	}

	public void setMatrixManager(MatrixManager matrixManager)
	{
		this.matrixManager = matrixManager;
	}

}
