package org.theospi.portfolio.matrix.model.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.EntityProducer;
import org.sakaiproject.entity.api.HttpAccess;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.theospi.portfolio.matrix.MatrixManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ScaffoldingEntityProducer implements EntityProducer {

	protected final Log logger = LogFactory.getLog(getClass());
	private EntityManager entityManager;
	private MatrixManager matrixManager;
	private IdManager idManager;

	public String getLabel() {
		return "/scaffolding";
	}

	public void init() {
		logger.info("init()");
		try {
			getEntityManager().registerEntityProducer(this, getLabel());
		}
		catch (Exception e) {
			logger.warn("Error registering Matrix Content Entity Producer", e);
		}
	}
	
	public void destroy() {
		logger.info("destroy()");
	}

	public String archive(String siteId, Document doc, Stack stack,
			String archivePath, List attachments) {
		// TODO Auto-generated method stub
		return null;
	}

	public Entity getEntity(Reference ref) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getEntityAuthzGroups(Reference ref, String userId) {
		Collection<String> rv = new Vector<String>();
		rv.add(ref.getReference());
		
		try {
			Site site = SiteService.getSite(ref.getContext());
			rv.add(site.getReference());
		} catch (IdUnusedException e) {
			logger.warn("unable to get a site object from entity: " +ref.getReference());
		}
		
		
		return rv;
	}

	public String getEntityDescription(Reference ref) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResourceProperties getEntityResourceProperties(Reference ref) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEntityUrl(Reference ref) {
		// TODO Auto-generated method stub
		return null;
	}

	public HttpAccess getHttpAccess() {
		// TODO Auto-generated method stub
		return null;
	}

	public String merge(String siteId, Element root, String archivePath,
			String fromSiteId, Map attachmentNames, Map userIdTrans,
			Set userListAllowImport) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean parseEntityReference(String reference, Reference ref) {
		if (reference.startsWith(getLabel())) {
			String[] parts = reference.split(Entity.SEPARATOR, 4);
			if (parts.length < 4) {
				return false;
			}
			String type = parts[1];
			/*
			 * This is only really used so we know what kind of object we are
			 * referencing
			 */
			String context = parts[2];

			String id = parts[3];

			ref.set(type, null, id, null, context);
			return true;
		}
		return false;
	}

	public boolean willArchiveMerge() {
		// TODO Auto-generated method stub
		return false;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public MatrixManager getMatrixManager() {
		return matrixManager;
	}

	public void setMatrixManager(MatrixManager matrixManager) {
		this.matrixManager = matrixManager;
	}

	public IdManager getIdManager() {
		return idManager;
	}

	public void setIdManager(IdManager idManager) {
		this.idManager = idManager;
	}

}
