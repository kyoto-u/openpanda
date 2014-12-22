/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.3/wizard/api-impl/src/java/org/theospi/portfolio/wizard/mgt/impl/WizardManagerImpl.java $
* $Id: WizardManagerImpl.java 309396 2014-05-09 21:23:15Z enietzel@anisakai.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
package org.theospi.portfolio.wizard.mgt.impl;

import java.io.*;
import java.util.*;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.sql.SQLException;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Query;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.jdom.CDATA;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.LockManager;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.ImportException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.exception.UnsupportedFileTypeException;
import org.sakaiproject.memory.api.Cache;
import org.sakaiproject.memory.api.MemoryService;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.security.AllowMapSecurityAdvisor;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.shared.DownloadableManager;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.ContentEntityUtil;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.PresentableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.FinderException;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.MimeType;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.shared.model.Type;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.service.legacy.resource.DuplicatableToolService;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.theospi.event.EventService;
import org.theospi.event.EventConstants;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.matrix.MatrixFunctionConstants;
import org.theospi.portfolio.matrix.MatrixManager;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.matrix.model.Attachment;
import org.theospi.portfolio.matrix.model.WizardPageForm;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.impl.AllowAllSecurityAdvisor;
import org.theospi.portfolio.shared.model.EvaluationContentWrapper;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.model.ObjectWithWorkflow;
import org.theospi.portfolio.shared.model.OspException;
import org.theospi.portfolio.shared.model.WizardMatrixConstants;
import org.theospi.portfolio.style.StyleConsumer;
import org.theospi.portfolio.style.mgt.StyleManager;
import org.theospi.portfolio.style.model.Style;
import org.theospi.portfolio.wizard.WizardFunctionConstants;
import org.theospi.portfolio.wizard.impl.WizardEntityProducer;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.CompletedWizard;
import org.theospi.portfolio.wizard.model.CompletedWizardCategory;
import org.theospi.portfolio.wizard.model.CompletedWizardPage;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.model.WizardCategory;
import org.theospi.portfolio.wizard.model.WizardPageSequence;
import org.theospi.portfolio.workflow.mgt.WorkflowManager;
import org.theospi.portfolio.workflow.model.Workflow;
import org.theospi.portfolio.workflow.model.WorkflowItem;

public class WizardManagerImpl extends HibernateDaoSupport
      implements WizardManager, DownloadableManager, ReadableObjectHome, ArtifactFinder, 
            PresentableObjectHome, StyleConsumer, DuplicatableToolService {

   static final private String   DOWNLOAD_WIZARD_ID_PARAM = "wizardId";
   static final private String   IMPORT_BASE_FOLDER_ID = "importedWizards";
   
   /**
	 * property name for site identifier
	 */
	private static final String SITE_ID = "siteId";

   private AuthorizationFacade authorizationFacade;
   private SecurityService securityService;
   private EntityManager entityManager;
   private IdManager idManager;
   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;
   private AgentManager agentManager;
   private AuthenticationManager authManager;
   private GuidanceManager guidanceManager;
   private WorkflowManager workflowManager;
   private ContentHostingService contentHosting;
   private EventService eventService;
   private PresentableObjectHome xmlRenderer;
   private ReviewManager reviewManager;
   private StyleManager styleManager;
   private MatrixManager matrixManager;
   private LockManager lockManager;
   private WorksiteManager worksiteManager;

   private String importFolderName;

   private static boolean allowAllGroups = 
      ServerConfigurationService.getBoolean(WizardMatrixConstants.PROP_GROUPS_ALLOW_ALL_GLOBAL, false);
      
   protected void init() throws Exception {
      
      logger.info("init()");
   }

   
   /**
    * {@inheritDoc}
    */
   public Wizard createNew() {
      Placement placement = ToolManager.getCurrentPlacement();
      String currentSite = placement.getContext();
      Agent agent = getAuthManager().getAgent();
      Wizard wizard = new Wizard(getIdManager().createId(), agent, currentSite);
      return wizard;
   }
   
   protected void removeFromSession(Object obj) {
      this.getHibernateTemplate().evict(obj);
      try {
         getHibernateTemplate().getSessionFactory().evict(obj.getClass());
      } catch (HibernateException e) {
         logger.error(e);
      }
   }
   
   protected void clearSession() {
      this.getHibernateTemplate().clear();
   }

   
   /**
    * {@inheritDoc}
    */
   public Wizard getWizard(Id wizardId) {
      return getWizard(wizardId, WIZARD_OPERATE_CHECK);
   }
   
   
   /**
    * {@inheritDoc}
    */
   public Wizard getWizard(Id wizardId, int checkAuthz) {
      Wizard wizard = (Wizard)getHibernateTemplate().get(Wizard.class, wizardId);

      if (wizard == null) {
         return null;
      }

      if (checkAuthz == WIZARD_OPERATE_CHECK)
         getAuthorizationFacade().checkPermission(WizardFunctionConstants.OPERATE_WIZARD,
               wizardId);
      if (checkAuthz == WIZARD_VIEW_CHECK)
         getAuthorizationFacade().checkPermission(WizardFunctionConstants.VIEW_WIZARD,
        		 getIdManager().getId(wizard.getSiteId()));
      if (checkAuthz == WIZARD_EDIT_CHECK)
         getAuthorizationFacade().checkPermission(WizardFunctionConstants.EDIT_WIZARD,
               wizardId);
      if (checkAuthz == WIZARD_EXPORT_CHECK)
         getAuthorizationFacade().checkPermission(WizardFunctionConstants.EXPORT_WIZARD, 
               idManager.getId(ToolManager.getCurrentPlacement().getContext()));
      if (checkAuthz == WIZARD_DELETE_CHECK)
         getAuthorizationFacade().checkPermission(WizardFunctionConstants.DELETE_WIZARD,
               wizardId);

      // setup access to the files
      List refs = new ArrayList();
      
      if (wizard.getStyle() != null) {
         Node node = getNode(wizard.getStyle().getStyleFile());
         refs.add(node.getResource().getReference());
      }         

      WizardCategory rootCategory = (WizardCategory)wizard.getRootCategory();
      loadCategory(rootCategory, refs);

      getSecurityService().pushAdvisor(new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ,
         refs));
      
      //removeFromSession(wizard);
      return wizard;
   }
   
   
   /**
    * {@inheritDoc}
    */
   public Wizard getWizard(String id, int checkAuthz) {
      return getWizard(getIdManager().getId(id), checkAuthz);
   }
   
   public Wizard getWizard(String id) {
      return getWizard(id, WIZARD_OPERATE_CHECK);
   }
   
   public Node getNode(Id artifactId) {
      String id = getContentHosting().resolveUuid(artifactId.getValue());
      if (id == null) {
         return null;
      }
      
      //This needs to be here so that the getResource(id) down in the try{} doesn't bark
      getSecurityService().pushAdvisor(
            new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ,
                  getContentHosting().getReference(id)));

      try {
         ContentResource resource = getContentHosting().getResource(id);
         String ownerId = resource.getProperties().getProperty(resource.getProperties().getNamePropCreator());
         Agent owner = getAgentManager().getAgent(getIdManager().getId(ownerId));
         return new Node(artifactId, resource, owner);
      }
      catch (PermissionException e) {
         logger.error("", e);
         throw new RuntimeException(e);
      }
      catch (IdUnusedException e) {
         logger.error("", e);
         throw new RuntimeException(e);
      }
      catch (TypeException e) {
         logger.error("", e);
         throw new RuntimeException(e);
      }
   }

   protected void loadCategory(WizardCategory category, List refs) {

      for (Iterator i=category.getChildPages().iterator();i.hasNext();) {
         WizardPageSequence page = (WizardPageSequence) i.next();
         WizardPageDefinition pageDef = page.getWizardPageDefinition(); // make sure this loads
         pageDef.getTitle();
      }

      if (category.getChildCategories() != null) {
         for (Iterator i=category.getChildCategories().iterator();i.hasNext();) {
            loadCategory((WizardCategory) i.next(), refs);
         }
      }
   }
   
   
   /**
    * {@inheritDoc}
    */
   public Wizard saveWizard(Wizard wizard) {
      Date now = new Date(System.currentTimeMillis());
      wizard.setModified(now);

      if (wizard.getExposeAsTool() != null &&
            wizard.getExposeAsTool().booleanValue() &&
            wizard.getExposedPageId() == null) {
         addTool(wizard);
      }
      else if (wizard.getExposeAsTool() != null &&
            !wizard.getExposeAsTool().booleanValue() &&
            wizard.getExposedPageId() != null) {
         removeTool(wizard);
      }

      if (wizard.isNewObject()) {
         // for some reason the save throws a null pointer exception
         //    if the id isn't set, so generate a new one if need be
         if(wizard.getId() == null && wizard.getNewId() == null)
            wizard.setNewId(getIdManager().createId());
         wizard.setCreated(now);
         wizard.getRootCategory().setCreated(now);
         wizard.getRootCategory().setModified(now);
         wizard.getRootCategory().setWizard(null);
         getHibernateTemplate().save(wizard);
         wizard.getRootCategory().setWizard(wizard);
         wizard.setNewObject(false);
         eventService.postEvent(EventConstants.EVENT_WIZARD_ADD, wizard.getId().getValue());
      }
      else {
         getHibernateTemplate().saveOrUpdate(wizard);
         eventService.postEvent(EventConstants.EVENT_WIZARD_REVISE, wizard.getId().getValue());
      }
      return wizard;
   }

   private void removeTool(Wizard wizard) {
      String siteId = wizard.getSiteId();
      try {
         Site siteEdit = SiteService.getSite(siteId);

         SitePage page = siteEdit.getPage(wizard.getExposedPageId());
         siteEdit.removePage(page);
         SiteService.save(siteEdit);
         wizard.setExposedPageId(null);
      } catch (IdUnusedException e) {
         logger.error("", e);
      } catch (PermissionException e) {
         logger.error("", e);
      }
   }

   private void addTool(Wizard wizard) {
      String siteId = wizard.getSiteId();
      try {
         Site siteEdit = SiteService.getSite(siteId);


         SitePage page = siteEdit.addPage();

         page.setTitle(wizard.getName());
         page.setLayout(SitePage.LAYOUT_SINGLE_COL);

         ToolConfiguration tool = page.addTool();
         tool.setTool("osp.exposedwizard", ToolManager.getTool("osp.exposedwizard"));
         tool.setTitle(wizard.getName());
         tool.setLayoutHints("0,0");
         tool.getPlacementConfig().setProperty(WizardManager.EXPOSED_WIZARD_KEY, wizard.getId().getValue());

         //LOG.info(this+": SiteService.commitEdit():" +siteId);

         SiteService.save(siteEdit);
         wizard.setExposedPageId(page.getId());


      } catch (IdUnusedException e) {
         logger.error("", e);
      } catch (PermissionException e) {
         logger.error("", e);
      }
   }
   
   /** 
    * unlock all resources associated with wizard pages (in preparation for delete)
    */
   protected void unlockWizardResources( Wizard wizard ) {
      List wpsList = findPagesByWizard( wizard.getId() );
      for (Iterator wpsIt=wpsList.iterator(); wpsIt.hasNext();) 
      {
         WizardPageSequence wps = (WizardPageSequence)wpsIt.next();
         
         Id defId = wps.getWizardPageDefinition().getId();
         List pageList = matrixManager.getPagesByPageDef(defId);
         
         for ( Iterator pageIt=pageList.iterator(); pageIt.hasNext(); )
         {
            WizardPage page = (WizardPage)pageIt.next();
            
            for (Iterator iter = page.getAttachments().iterator(); iter.hasNext();) {
               Attachment att = (Attachment)iter.next();
               getLockManager().removeLock(att.getArtifactId().getValue(), 
                        page.getId().getValue());
            }
      
            for (Iterator iter = page.getPageForms().iterator(); iter.hasNext();) {
               WizardPageForm pageForm = (WizardPageForm)iter.next();
               getLockManager().removeLock(pageForm.getArtifactId().getValue(), 
                        page.getId().getValue());
            }

            List reviews = getReviewManager().getReviewsByParent(
                  page.getId().getValue(), 
                  page.getPageDefinition().getSiteId(),
                  WizardEntityProducer.WIZARD_PRODUCER);
            for (Iterator iter = reviews.iterator(); iter.hasNext();) {
               Review review = (Review)iter.next();
               getLockManager().removeLock(review.getReviewContent().getValue(), 
                        page.getId().getValue());
            }
         } 
      }
   }
   
   /**
    * Unlock resources and delete completed wizards from a preview wizard
    */
   public void deletePreviewWizardData( Wizard wizard ) {
   
       // Unlock resources associated with wizard pages
      unlockWizardResources(wizard);
      
      // Delete completed wizards
      List completedWizards = getCompletedWizards(wizard);
      for (Iterator i = completedWizards.iterator(); i.hasNext();) {
         CompletedWizard cw = (CompletedWizard)i.next();
         deleteCompletedWizard(cw);
      }
  }
   
   /**
    * {@inheritDoc}
    */
   public void deleteWizard(Wizard wizard) {
   
      // Unlock resources associated with wizard pages
      unlockWizardResources(wizard);
      
      // Delete completed wizards
      Wizard wiz = this.getWizard(wizard.getId(), WIZARD_DELETE_CHECK);
      List completedWizards = getCompletedWizards(wiz);
      for (Iterator i = completedWizards.iterator(); i.hasNext();) {
         CompletedWizard cw = (CompletedWizard)i.next();
         deleteCompletedWizard(cw);
      }
      
      //remove the tool from the menu
      if (wiz.getExposedPageId() != null)
         removeTool(wiz);
         
      getHibernateTemplate().delete(wiz);
      eventService.postEvent(EventConstants.EVENT_WIZARD_DELETE, wizard.getId().getValue());
   }
   
   protected void deleteCompletedWizard(CompletedWizard cw) {
      // Unlock resources associated with this completed wizard
      List reviews = getReviewManager().getReviewsByParent( cw.getId().getValue() );
      for (Iterator iter = reviews.iterator(); iter.hasNext();) {
         Review review = (Review)iter.next();
         getLockManager().removeLock(review.getReviewContent().getValue(), 
                  cw.getId().getValue());
      }
      
      getHibernateTemplate().delete(cw);
   }
   
   public void publishWizard(Wizard wizard) {
      wizard.setPublished(true);
      wizard.setPreview(false);
      wizard.setModified(new Date(System.currentTimeMillis()));
      this.saveWizard(wizard);
   }
   
   public void previewWizard(Wizard wizard) {
      wizard.setPreview(true);
      wizard.setModified(new Date(System.currentTimeMillis()));
      this.saveWizard(wizard);
   }
   
   public String getWizardEntityProducer() {
      return WizardEntityProducer.WIZARD_PRODUCER;
   }

   public Reference decorateReference(Wizard wizard, String reference) {
      String fullRef = ContentEntityUtil.getInstance().buildRef(WizardEntityProducer.WIZARD_PRODUCER,
            wizard.getSiteId(), wizard.getId().getValue(), reference);

      return getEntityManager().newReference(fullRef);
   }

   public List listWizardsByType(String owner, String siteIdStr, String type) {
      Object[] params = new Object[]{getAgentManager().getAgent(owner), Boolean.valueOf(true), siteIdStr, type};
      return getHibernateTemplate().find("from Wizard w where " +
            "(w.owner=? or w.published=?) and w.siteId=? and w.type=? order by seq_num", params);
   }
   
   /**
    * Pulls all wizards, deeping loading all parts of each Wizard
    * @return List of Wizard
    */
   public List getWizardsForWarehousing()
   {
      List wizards = getHibernateTemplate().find("from Wizard w");
      
      return wizards;
   }
   
   /**
    * @param String the wizard id for the completed wizard classes
    * @return List of CompletedWizard
    */
   public List getCompletedWizardsByWizardId(String wizardId)
   {
      return getCompletedWizards(getIdManager().getId(wizardId));
   }

   public List listAllWizardsByOwner(String owner, String siteIdStr) {
      Agent ownerAgent = getAgentManager().getAgent(owner);
      Object[] params = new Object[]{ownerAgent, Boolean.valueOf(true), siteIdStr};
      return getHibernateTemplate().find("from Wizard w where " +
            "(w.owner=? or w.published=?) and w.siteId=? order by seq_num", params);
   }

   public List findWizardsByOwner(String ownerId, String siteId) {
      Object[] params = new Object[]{getAgentManager().getAgent(ownerId), siteId};
      return getHibernateTemplate().find("from Wizard w where w.owner=? and w.siteId=? order by seq_num", params);
   }

   public List findPublishedWizards(List<String> sites) {
      String[] paramNames = new String[] {"published", "siteIds"};
      Object[] params = new Object[]{Boolean.valueOf(true), sites};
      return getHibernateTemplate().findByNamedParam("from Wizard w where w.published=:published " +
            "and w.siteId in ( :siteIds ) order by seq_num", 
            paramNames, params);
   }
   
   /**
    * {@inheritDoc}
    */
   public List findPublishedWizards(List<String> sites, boolean lazy) {
      if (lazy) 
         return findPublishedWizardsLazy(sites);
      else
         return findPublishedWizards(sites);
   }
   
   
   protected List findPublishedWizardsLazy(List<String> sites) {
      Criteria c = this.getSession().createCriteria(Wizard.class);
      Criteria rootCat = c.createCriteria("rootCategory");
      rootCat.setFetchMode("childPages", FetchMode.SELECT);
      rootCat.setFetchMode("childCategories", FetchMode.SELECT);
      c.add(Expression.eq("published", Boolean.valueOf(true)));
      c.add(Expression.in("siteId", sites));
      
      return new ArrayList(c.list());
   }
   
   public List findPublishedWizards(String siteId) {
      Object[] params = new Object[]{Boolean.valueOf(true), siteId};
      return getHibernateTemplate().find("from Wizard w where w.published=? and w.siteId=? order by seq_num", params);
   }
   
   public List<WizardPageDefinition> findWizardPageDefs(final String siteId) {
	   return findWizardPageDefs(siteId, false);
	}

   public List<WizardPageDefinition> findWizardPageDefs(final String siteId,
			final boolean deep) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) {
				List<WizardPageDefinition> defs = (List) session
						.createCriteria(WizardPageDefinition.class).add(
								Restrictions.eq(SITE_ID, siteId)).list();
				if (deep) {
					for (WizardPageDefinition def : defs) {
						Hibernate.initialize(def.getPages());
					}
				}
				return defs;
			}
		});
	}
   
   public List<WizardPageSequence> findPagesByWizard(final Id wizardId)
   {
      HibernateCallback hcb = new HibernateCallback() {
         public Object doInHibernate(Session session) throws HibernateException, SQLException  {
            String queryString = "select page from WizardPageSequence page, WizardCategory category " +
               "where page.category = category.id and category.wizard = ?";

            Query query = session.createQuery(queryString);

            query.setParameter(0, wizardId.getValue(), Hibernate.STRING);

            return query.list();
         }
      };

      return (List)getHibernateTemplate().execute(hcb);
   }
   
   public Collection getAvailableForms(String siteId, String type, String currentUserId) {
      return getStructuredArtifactDefinitionManager().findAvailableHomes(
            getIdManager().getId(siteId), currentUserId, true, true);
   }

   public void deleteObjects(List deletedItems) {

	   for (Iterator i=deletedItems.iterator();i.hasNext();) {
		   try {
			   getSession().delete(i.next());
		   }
		   catch (HibernateException e) {
			   throw new OspException(e);
		   }
	   }

   }
   
   protected List getCompletedWizards(Wizard wizard) {
      return getCompletedWizards(wizard.getId());
   }
   
   private List getCompletedWizards(Id id)
   {
      List completedWizards = getHibernateTemplate().find(" from CompletedWizard cw where cw.wizard.id=?",
            new Object[]{id});
      return completedWizards;
   }

   protected List getCompletedWizards(String owner) {
      List completedWizards = getHibernateTemplate().find(" from CompletedWizard cw where cw.owner=?",
            new Object[]{getAgentManager().getAgent(owner)});
      return completedWizards;
   }

   public CompletedWizard getCompletedWizard(Id completedWizardId) {
      CompletedWizard wizard = (CompletedWizard)getHibernateTemplate().get(CompletedWizard.class, completedWizardId);
      return wizard;
   }

   public CompletedWizard getCompletedWizard(Wizard wizard) {
      Agent agent = getAuthManager().getAgent();

      return getUsersWizard(wizard, agent);
   }

   public CompletedWizard getCompletedWizard(Wizard wizard, String userId) {
      return getCompletedWizard(wizard,  userId, true);
   }

   public CompletedWizard getCompletedWizard(Wizard wizard, String userId, boolean create) {
      Agent agent = getAgentManager().getAgent(userId);

      return getUsersWizard(wizard, agent, create);
   }
   
   public CompletedWizard getCompletedWizardByPage(Id pageId) {
      CompletedWizard cw = null;
      Object[] params = new Object[]{pageId};
      List list = getHibernateTemplate().find("select w.category.wizard from CompletedWizardPage w " +
            "where w.wizardPage.id=?", params);
      
      if (list.size() == 1) {
         cw = (CompletedWizard) list.get(0);
      }
      
      return cw;      
   }
   
   
   /**
    * {@inheritDoc}
    */
   public CompletedWizard saveWizard(CompletedWizard wizard) {
      getHibernateTemplate().saveOrUpdate(wizard);
      return wizard;
   }

   public CompletedWizard getUsersWizard(Wizard wizard, Agent agent) {
      return getUsersWizard(wizard, agent, true);
   }

   public CompletedWizard getUsersWizard(Wizard wizard, Agent agent, boolean create) {
      List completedWizards = getHibernateTemplate().find(" from CompletedWizard cw where cw.wizard.id=? and cw.owner=?",
            new Object[]{wizard.getId(), agent});

      CompletedWizard returned;
      if (completedWizards.size() != 0) {
         returned = (CompletedWizard)completedWizards.get(0);
      }
      else if (create) {
         returned = new CompletedWizard(wizard, agent);
      }
      else {
         return null;
      }

      if (create) {
         returned.setLastVisited(new Date());
         getHibernateTemplate().save(returned);
      }

      return returned;
   }

   public void processWorkflow(int workflowOption, Id id) {
      //TODO Unimplemented
   }

   public void processWorkflow(Id workflowId, Id completedWizardId) {
      Workflow workflow = getWorkflowManager().getWorkflow(workflowId);
      CompletedWizard compWizard = this.getCompletedWizard(completedWizardId);

      Collection items = workflow.getItems();
      for (Iterator i = items.iterator(); i.hasNext();) {
         WorkflowItem wi = (WorkflowItem)i.next();
         //Cell actionCell = this.getMatrixCellByScaffoldingCell(cell.getMatrix(),
         //      wi.getActionObjectId());
         switch (wi.getActionType()) {
            case(WorkflowItem.STATUS_CHANGE_WORKFLOW):
               processStatusChangeWorkflow(wi, compWizard);
               break;
            case(WorkflowItem.NOTIFICATION_WORKFLOW):
               processNotificationWorkflow(wi);
               break;
            case(WorkflowItem.CONTENT_LOCKING_WORKFLOW):
               processContentLockingWorkflow(wi, compWizard);
               break;
         }
      }
   }
   
   private void processStatusChangeWorkflow(String status, CompletedWizard actionWizard) {
      actionWizard.setStatus(status);
   }

   private void processStatusChangeWorkflow(WorkflowItem wi, CompletedWizard actionWizard) {
      processStatusChangeWorkflow(wi.getActionValue(), actionWizard);
   }

   private void processContentLockingWorkflow(String lockAction, CompletedWizard actionWizard) {
      //TODO implement
   }

   private void processContentLockingWorkflow(WorkflowItem wi, CompletedWizard actionWizard) {
      processContentLockingWorkflow(wi.getActionValue(), actionWizard);
   }

   private void processNotificationWorkflow(WorkflowItem wi) {
      // TODO implement

   }
   
   
   /**
    * {@inheritDoc}
    */
   public void checkWizardAccess(Id id) {
      CompletedWizard cw = getCompletedWizard(id);
      
      if (cw == null)
      {
         logger.error("checkWizardAccess: No such wizard " + id.toString());
         return;
      }
      
      boolean canEval = getAuthorizationFacade().isAuthorized(WizardFunctionConstants.EVALUATE_WIZARD, 
            cw.getWizard().getId());
      boolean canReview = getAuthorizationFacade().isAuthorized(WizardFunctionConstants.REVIEW_WIZARD, 
    		  getIdManager().getId(cw.getWizard().getSiteId()));
      boolean canReflect = canEval || canReview;
      
      boolean owns = cw.getOwner().getId().equals(getAuthManager().getAgent().getId());
      
      if (canEval || owns) {
         //can I look at reviews/evals/reflections? - own or eval
         getReviewManager().getReviewsByParentAndType(
               id.getValue(), Review.EVALUATION_TYPE,
               cw.getWizard().getSiteId(),
               WizardEntityProducer.WIZARD_PRODUCER);
      }
      
      if (canReview || owns) {
         //can I look at reviews/evals/reflections? - own or review
         getReviewManager().getReviewsByParentAndType(
               id.getValue(), Review.FEEDBACK_TYPE,
               cw.getWizard().getSiteId(),
               WizardEntityProducer.WIZARD_PRODUCER);         
      }
      
      if (canReflect || owns) {
         //can I look at reviews/evals/reflections? - own or reflect
         getReviewManager().getReviewsByParentAndType(
               id.getValue(), Review.REFLECTION_TYPE,
               cw.getWizard().getSiteId(),
               WizardEntityProducer.WIZARD_PRODUCER);         
      }
   }
   
   
   /**
    * {@inheritDoc}
    */
   public int getTotalPageCount(final Wizard wizard) {
      HibernateCallback hcb = new HibernateCallback() {
         public Object doInHibernate(Session session) throws HibernateException, SQLException  {
            String queryString = "select count(page) from WizardPageSequence page, WizardCategory category " +
               "where page.category = category.id and category.wizard = ?";

            Query query = session.createQuery(queryString);

            query.setParameter(0, wizard.getId().getValue(), Hibernate.STRING);

            Integer results = (Integer) query.uniqueResult();
            return results;
         }
      };

      return ((Integer)getHibernateTemplate().execute(hcb)).intValue();
   }
   
   
   /**
    * {@inheritDoc}
    */
   public String getWizardIdSiteId(final Id wizardId) {

       String siteId;

      HibernateCallback hcb = new HibernateCallback() {
         public Object doInHibernate(Session session) throws HibernateException, SQLException  {
            String queryString = "select wizard.siteId from Wizard wizard where wizard.id = ?";

            Query query = session.createQuery(queryString);

            query.setParameter(0, wizardId.getValue(), Hibernate.STRING);

            String results = (String) query.uniqueResult();
            
            return results;
         }
      };
		
      siteId = ((String)getHibernateTemplate().execute(hcb));
      return siteId;
   }
   
   
   /**
    * {@inheritDoc}
    */
   public Agent getWizardIdOwner(final Id wizardId) {
      HibernateCallback hcb = new HibernateCallback() {
         public Object doInHibernate(Session session) throws HibernateException, SQLException  {
            String queryString = "select wizard.owner from Wizard wizard where wizard.id = ?";

            Query query = session.createQuery(queryString);

            query.setParameter(0, wizardId.getValue(), Hibernate.STRING);

            Agent results = (Agent) query.uniqueResult();
            return results;
         }
      };

      return ((Agent)getHibernateTemplate().execute(hcb));
   }
   
   
   /**
    * {@inheritDoc}
    */
   public int getSubmittedPageCount(final CompletedWizard wizard) {
      HibernateCallback hcb = new HibernateCallback() {
         public Object doInHibernate(Session session) throws HibernateException, SQLException  {
            String queryString = "select count(page) " +
               "from WizardPage page, CompletedWizardPage completedPage, CompletedWizardCategory category " +
               "where page.id = completedPage.wizardPage and " +
               "      completedPage.category = category.id and " +
               "      category.wizard = ? and " +
               "      page.status != ?";

            Query query = session.createQuery(queryString);

            query.setParameter(0, wizard.getId().getValue(), Hibernate.STRING);
            query.setParameter(1, MatrixFunctionConstants.READY_STATUS, Hibernate.STRING);

            Integer results = (Integer) query.uniqueResult();
            return results;
         }
      };

      return ((Integer)getHibernateTemplate().execute(hcb)).intValue();
   }

   public AuthorizationFacade getAuthorizationFacade() {
      return authorizationFacade;
   }

   public void setAuthorizationFacade(AuthorizationFacade authorizationFacade) {
      this.authorizationFacade = authorizationFacade;
   }

   public EntityManager getEntityManager() {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public SecurityService getSecurityService() {
      return securityService;
   }

   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }

   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
      return structuredArtifactDefinitionManager;
   }

   public void setStructuredArtifactDefinitionManager(StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public AuthenticationManager getAuthManager() {
      return authManager;
   }

   public void setAuthManager(AuthenticationManager authManager) {
      this.authManager = authManager;
   }

   public GuidanceManager getGuidanceManager() {
      return guidanceManager;
   }

   public void setGuidanceManager(GuidanceManager guidanceManager) {
      this.guidanceManager = guidanceManager;
   }

   /**
    * @return Returns the workflowManager.
    */
   public WorkflowManager getWorkflowManager() {
      return workflowManager;
   }

   /**
    * @param workflowManager The workflowManager to set.
    */
   public void setWorkflowManager(WorkflowManager workflowManager) {
      this.workflowManager = workflowManager;
   }

   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }


   public Wizard importWizardResource(Id worksiteId, String nodeId) throws UnsupportedFileTypeException, ImportException
   {

      String id = getContentHosting().resolveUuid(nodeId);
      try {
         ContentResource resource = getContentHosting().getResource(id);
         MimeType  mimeType = new MimeType(resource.getContentType());

         if(mimeType.equals(new MimeType("application/zip")) ||
               mimeType.equals(new MimeType("application/x-zip-compressed"))) {
            InputStream zipContent = resource.streamContent();
            Wizard bean = importWizard(worksiteId, zipContent);

            return bean;
         } else {
            throw new UnsupportedFileTypeException("Unsupported file type");
         }
      } catch(ServerOverloadException soe) {
            logger.warn(soe);
      } catch(IOException ioe) {
            logger.warn(ioe);
      } catch(PermissionException pe) {
         logger.warn("Failed loading content: no permission to view file", pe);
      } catch(TypeException te) {
         logger.warn("Wrong type", te);
      } catch(IdUnusedException iue) {
         logger.warn("UnusedId: ", iue);
      }
      return null;
   }
   
   private Wizard importWizard(Id worksiteId, InputStream in) throws IOException, ImportException
   {
      ZipInputStream zis = new ZipInputStream(in);

      Wizard bean = readWizardFromZip(zis, worksiteId.getValue());
      return bean;
   }

   private static final String IMPORT_CREATE_DATE_KEY = "createDate";
   private static final String IMPORT_EVALUATORS_KEY = "evaluators";
   //private static final String IMPORT_STYLES_KEY = "style";
   private Wizard readWizardFromZip(ZipInputStream zis, String worksiteId) throws IOException, ImportException
   {
      ZipEntry currentEntry = zis.getNextEntry();

       if(currentEntry == null)
         return null;

       Map     importData = new HashMap();
      Wizard   wizard = new Wizard(null, getAuthManager().getAgent(), 
											  worksiteId);
      String tempDirName = getIdManager().createId().getValue();

      // set values not coming from the zip
      wizard.setCreated(new Date(System.currentTimeMillis()));
      wizard.setModified(wizard.getCreated());
      wizard.setNewId(getIdManager().createId());

      importData.put(IMPORT_CREATE_DATE_KEY, wizard.getCreated());
      importData.put(IMPORT_EVALUATORS_KEY, new HashMap()); // key: userid  value: isRole
      //importData.put(IMPORT_STYLES_KEY, new HashMap());

      Map formsMap = new Hashtable();
      Map guidanceMap = null;
      Map styleMap = null;
      Map resourceMap = new Hashtable();
      try {
         boolean gotFile = false;
         
         // read the wizard
         readWizardXML(wizard, zis, importData);
         // presentationmanagerimpl: 2642
         ContentCollectionEdit fileParent = getFileDir(tempDirName);

         currentEntry = zis.getNextEntry();
         while(currentEntry != null) {
            if(!currentEntry.isDirectory()) {
               if(currentEntry.getName().startsWith("forms/")) {
                  processMatrixForm(currentEntry, zis, formsMap,
                        getIdManager().getId(worksiteId));
               } else if(currentEntry.getName().startsWith("guidance/")) {
                  guidanceMap = processMatrixGuidance(fileParent, worksiteId, zis);

                  for(Iterator i = guidanceMap.values().iterator(); i.hasNext(); ) {
                     Guidance g = (Guidance) i.next();
                     
                     // hack for:  
                     //    This will only be for wizards exported before r13782
                     if(g.getSecurityViewFunction().equals(WizardFunctionConstants.VIEW_WIZARD))
                        g.setSecurityViewFunction(WizardFunctionConstants.OPERATE_WIZARD);
                  }
                  
                  
                  gotFile = true;
               } else if(currentEntry.getName().startsWith("style/")) {
                  styleMap = processMatrixStyle(fileParent, worksiteId, zis);
                  gotFile = true;
               } else {
                  importAttachmentRef(fileParent, currentEntry, worksiteId, zis, resourceMap);
                  gotFile = true;
               }
            }
            zis.closeEntry();
            currentEntry = zis.getNextEntry();
         }

         if (gotFile) {
            fileParent.getPropertiesEdit().addProperty(
                  ResourceProperties.PROP_DISPLAY_NAME, wizard.getName());
            getContentHosting().commitCollection(fileParent);
         }
         else {
            getContentHosting().cancelCollection(fileParent);
         }
         // the wizard needs to be saved so it has an id
         // the id is needed because guidance needs the security qualifier
         replaceIds(wizard, guidanceMap, formsMap, styleMap);
         wizard = saveWizard(wizard);

         // set the wizard evaluators
         Map wizardEvaluators = (Map)importData.get(IMPORT_EVALUATORS_KEY);
         for(Iterator i = wizardEvaluators.keySet().iterator(); i.hasNext(); ) {
            String userId = (String)i.next();

            if (userId.startsWith("/site/")) {
               // it's a role
               String[] agentValues = userId.split("/");

               userId = userId.replaceAll(agentValues[2], worksiteId);
            }
            Agent agent = agentManager.getAgent(idManager.getId(userId));
            if (agent != null  && agent.getId() != null)
               authorizationFacade.createAuthorization(agent,
                  WizardFunctionConstants.EVALUATE_WIZARD, wizard.getId());
         }

         //set the authorization for the pages
         setAuthnCat(wizard.getRootCategory(), worksiteId);

      } catch(ImportException ie) {
         throw new ImportException(ie);
      } catch(Exception e) {
         throw new RuntimeException(e);
      }
      finally {
         try {
            zis.closeEntry();
         }
         catch (IOException e) {
            logger.error("", e);
         }
      }
      return wizard;
   }

   private void setAuthnCat(WizardCategory cat, String worksite) {

      List pages = cat.getChildPages();
      for(Iterator i = pages.iterator(); i.hasNext(); ) {
         WizardPageSequence sequence = (WizardPageSequence)i.next();
         WizardPageDefinition pageDef = sequence.getWizardPageDefinition();

         for(Iterator ii = pageDef.getEvaluators().iterator(); ii.hasNext(); ) {
            String strId = (String)ii.next();

            if (strId.startsWith("/site/")) {
               // it's a role
               String[] agentValues = strId.split("/");

               strId = strId.replaceAll(agentValues[2], worksite);
            }
            Agent agent = agentManager.getAgent(idManager.getId(strId));

            if (agent != null  && agent.getId() != null)
               authorizationFacade.createAuthorization(agent,
                  MatrixFunctionConstants.EVALUATE_MATRIX, pageDef.getId());
         }
      }
   }

   
   /**
    * gets the current user's resource collection
    * 
    * @return ContentCollection
    * @throws TypeException
    * @throws IdUnusedException
    * @throws PermissionException
    */
   protected ContentCollection getUserCollection() throws TypeException, IdUnusedException, PermissionException {
      User user = UserDirectoryService.getCurrentUser();
      String userId = user.getId();
      String wsId = SiteService.getUserSiteId(userId);
      String wsCollectionId = getContentHosting().getSiteCollection(wsId);
      ContentCollection collection = getContentHosting().getCollection(wsCollectionId);
      return collection;
   }

   protected Map processMatrixGuidance(ContentCollection parent, String siteId,
                                       ZipInputStream zis) throws IOException {
      return getGuidanceManager().importGuidanceList(parent, siteId, zis);
   }

   protected Map processMatrixStyle(ContentCollection parent, String siteId,
         ZipInputStream zis) throws IOException {
      return getStyleManager().importStyleList(parent, siteId, zis);
   }
   
   /**
    * This gets the directory in which the import places files into.
    * 
    * This method gets the current users base collection, creates an imported directory,
    * then uses the param to create a new directory.
    * 
    * this uses the bean property importFolderName to name the
    * 
    * @param origName String
    * @return ContentCollectionEdit
    * @throws InconsistentException
    * @throws PermissionException
    * @throws IdUsedException
    * @throws IdInvalidException
    * @throws IdUnusedException
    * @throws TypeException
    */
   protected ContentCollectionEdit getFileDir(String origName) throws InconsistentException,
         PermissionException, IdUsedException, IdInvalidException, IdUnusedException, TypeException {
      ContentCollection userCollection = getUserCollection();
      
      try {
         //TODO use the bean org.theospi.portfolio.admin.model.IntegrationOption.siteOption 
         // in common/components to get the name and id for this site.
         
         ContentCollectionEdit groupCollection = getContentHosting().addCollection(userCollection.getId() + IMPORT_BASE_FOLDER_ID);
         groupCollection.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, getImportFolderName());
         getContentHosting().commitCollection(groupCollection);
      }
      catch (IdUsedException e) {
         // ignore... it is already there.
          if (logger.isDebugEnabled()) {
              logger.debug(e);
          } 
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }
      
      ContentCollection collection = getContentHosting().getCollection(userCollection.getId() + IMPORT_BASE_FOLDER_ID + "/");
      
      String childId = collection.getId() + origName;
      return getContentHosting().addCollection(childId);
   }

   protected void processMatrixForm(ZipEntry currentEntry, ZipInputStream zis, Map formMap, Id worksite)
         throws IOException {
      File file = new File(currentEntry.getName());
      String fileName = file.getName();
      String oldId = fileName.substring(0, fileName.indexOf(".form"));

      StructuredArtifactDefinitionBean bean;
      try {
         //we want the bean even if it exists already
         bean = getStructuredArtifactDefinitionManager().importSad(
               worksite, zis, true, false, false);
      } catch(ImportException ie) {
         throw new RuntimeException("the structured artifact failed to import", ie);
      }

      formMap.put(oldId, bean.getId().getValue());
   }

   protected void importAttachmentRef(ContentCollection fileParent, ZipEntry currentEntry, String siteId,
                                      ZipInputStream zis, Map resourceMap) {
      File file = new File(currentEntry.getName());

      MimeType mimeType = new MimeType(file.getParentFile().getParentFile().getParentFile().getName(),
         file.getParentFile().getParentFile().getName());

      String contentType = mimeType.getValue();

      String oldId = file.getParentFile().getName();

      try {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         int c = zis.read();

         while (c != -1) {
            bos.write(c);
            c = zis.read();
         }

         String fileId = ((fileParent!=null)?fileParent.getId():"") + file.getName();
         ContentResourceEdit resource = getContentHosting().addResource(fileId);
         ResourcePropertiesEdit resourceProperties = resource.getPropertiesEdit();
         resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, file.getName());
         resource.setContent(bos.toByteArray());
         resource.setContentType(contentType);
         getContentHosting().commitResource(resource);
         resourceMap.put(oldId, resource.getReference());
      }
      catch (Exception exp) {
         throw new RuntimeException(exp);
      }
   }

   private boolean readWizardXML(Wizard wizard, InputStream inStream, Map importData) throws ImportException
   {
      SAXBuilder builder = new SAXBuilder();
      builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // SAK-23131
      Map evaluatorsMap = (Map)importData.get(IMPORT_EVALUATORS_KEY);
      //Map stylesMap = (Map)importData.get(IMPORT_STYLES_KEY);

      try {
         byte []bytes = readStreamToBytes(inStream);
         Document document = builder.build(new ByteArrayInputStream(bytes));

         Element topNode = document.getRootElement();

         wizard.setName(topNode.getChildTextTrim("name"));
         wizard.setDescription(topNode.getChildTextTrim("description"));
         wizard.setKeywords(topNode.getChildTextTrim("keywords"));
         wizard.setType(topNode.getChildTextTrim("type"));
         wizard.setSequence(Integer.parseInt(topNode.getChildTextTrim("sequence")));

         // Read the evaluators
         List evaluators = topNode.getChild("evaluators").getChildren("evaluator");
         for(Iterator i = evaluators.iterator(); i.hasNext(); ) {
            Element evaluator = (Element)i.next();

            String userId = evaluator.getTextTrim();
            boolean isRole = evaluator.getAttribute("isRole").getBooleanValue();

            evaluatorsMap.put(userId, Boolean.valueOf(isRole));
         }

         // read the evaluation, review, reflection
         Element workflow = topNode.getChild("workflow");

         String wfType, wfId;

         wfType = workflow.getChildTextTrim("evaluationDeviceType");
         wfId = workflow.getChildTextTrim("evaluationDevice");
         wizard.setEvaluationDeviceType(wfType);
         wizard.setEvaluationDevice(idManager.getId(wfId));

         wfType = workflow.getChildTextTrim("reflectionDeviceType");
         wfId = workflow.getChildTextTrim("reflectionDevice");
         wizard.setReflectionDeviceType(wfType);
         wizard.setReflectionDevice(idManager.getId(wfId));

         wfType = workflow.getChildTextTrim("reviewDeviceType");
         wfId = workflow.getChildTextTrim("reviewDevice");
         wizard.setReviewDeviceType(wfType);
         wizard.setReviewDevice(idManager.getId(wfId));

         // read the wizard guidance to the list
         String guidanceIdStr = topNode.getChildTextTrim("guidance");
         wizard.setGuidanceId(idManager.getId(guidanceIdStr));

         // read the categories/pages
         readCategoriesAndPages(wizard, wizard.getRootCategory(), topNode.getChild("category"), importData);

          //   pull the styles from the xml
         //    WizardStyleItem only works with Resources not IDs
         
         String styleIdStr = topNode.getChildTextTrim("style");
         wizard.setStyleId(getIdManager().getId(styleIdStr));
         //stylesMap.put(styleId, null);
         
      } catch(Exception jdome) {
            throw new ImportException(jdome);
      }
      return true;
   }

   private void readCategoriesAndPages(Wizard wizard, WizardCategory category, Element categoryNode, Map importData)
            throws DataConversionException
   {
      category.setCreated((Date)importData.get(IMPORT_CREATE_DATE_KEY));
      category.setModified((Date)importData.get(IMPORT_CREATE_DATE_KEY));

      category.setTitle(categoryNode.getChildTextTrim("title"));
      category.setDescription(categoryNode.getChildTextTrim("description"));
      category.setKeywords(categoryNode.getChildTextTrim("keywords"));
      category.setSequence(Integer.parseInt(categoryNode.getChildTextTrim("sequence")));
      category.setWizard(wizard);

      List pageSequences = categoryNode.getChild("pages").getChildren("pageSequence");
      List pages = new ArrayList();
      for(Iterator i = pageSequences.iterator(); i.hasNext(); ) {
         Element pageSequenceNode = (Element)i.next();
         WizardPageSequence pageSequence = new WizardPageSequence();

         
         pageSequence.setCategory(category);
         pageSequence.setTitle(pageSequenceNode.getChildTextTrim("title"));
         pageSequence.setSequence(Integer.parseInt(
               pageSequenceNode.getChildTextTrim("sequence")));

         Element pageDefNode = pageSequenceNode.getChild("pageDef");
         boolean defaults = false;
         WizardPageDefinition wizardPageDefinition = new WizardPageDefinition(wizard.getType().equals(WizardFunctionConstants.WIZARD_TYPE_HIERARCHICAL) ? WizardPageDefinition.WPD_WIZARD_HIER_TYPE : WizardPageDefinition.WPD_WIZARD_SEQ_TYPE, 
        		 defaults, defaults, defaults, defaults, defaults, defaults, defaults);

         wizardPageDefinition.setNewId(getIdManager().createId());

         wizardPageDefinition.setTitle(pageDefNode.getChildTextTrim("title"));
         wizardPageDefinition.setDescription(pageDefNode.getChildTextTrim("description"));
         wizardPageDefinition.setInitialStatus(pageDefNode.getChildTextTrim("initialStatus"));

         // read the page workflow
         String wfType, wfId;
         Element workflow = pageDefNode.getChild("workflow");

         wfType = workflow.getChildTextTrim("evaluationDeviceType");
         wfId = workflow.getChildTextTrim("evaluationDevice");
         wizardPageDefinition.setEvaluationDeviceType(wfType);
         wizardPageDefinition.setEvaluationDevice(idManager.getId(wfId));

         wfType = workflow.getChildTextTrim("reflectionDeviceType");
         wfId = workflow.getChildTextTrim("reflectionDevice");
         wizardPageDefinition.setReflectionDeviceType(wfType);
         wizardPageDefinition.setReflectionDevice(idManager.getId(wfId));

         wfType = workflow.getChildTextTrim("reviewDeviceType");
         wfId = workflow.getChildTextTrim("reviewDevice");
         wizardPageDefinition.setReviewDeviceType(wfType);
         wizardPageDefinition.setReviewDevice(idManager.getId(wfId));

         // read the page guidance
         String guidanceIdStr = pageDefNode.getChildTextTrim("guidance");
         wizardPageDefinition.setGuidanceId(idManager.getId(guidanceIdStr));
         
         // read the page style
         String styleIdStr = pageDefNode.getChildTextTrim("style");
         wizardPageDefinition.setStyleId(getIdManager().getId(styleIdStr));

         // read the info about attachments
         if(pageDefNode.getChild("attachments") != null) {
            List attachments = pageDefNode.getChild("attachments").getChildren("ref");
            List<String> attachList = new ArrayList();
            for(Iterator ii = attachments.iterator(); ii.hasNext(); ) {
               Element attach = (Element)ii.next();

               String attachId = attach.getTextTrim();
               attachList.add(attachId);
            }
            wizardPageDefinition.setAttachments(attachList);
         }
         
         // read the info about additional forms
         if(pageDefNode.getChild("additionalForms") != null) {
            List forms = pageDefNode.getChild("additionalForms").getChildren("form");
            List formsList = new ArrayList();
            for(Iterator ii = forms.iterator(); ii.hasNext(); ) {
               Element form = (Element)ii.next();

               String formId = form.getTextTrim();
               formsList.add(formId);
            }
            wizardPageDefinition.setAdditionalForms(formsList);
         }

         // read the evaluators of the page, they are external to the wizard, store
         if(pageDefNode.getChild("evaluators") != null) {
            List evaluators = pageDefNode.getChild("evaluators").getChildren("evaluator");
            List evaluatorsList = new ArrayList();
            for(Iterator ii = evaluators.iterator(); ii.hasNext(); ) {
               Element evaluator = (Element)ii.next();

               String evaluatorId = evaluator.getTextTrim();
               boolean isRole = false;
               if(evaluator.getAttribute("isRole") != null)
                  isRole = evaluator.getAttribute("isRole").getBooleanValue();
               evaluatorsList.add(evaluatorId);
            }
            wizardPageDefinition.setEvaluators(evaluatorsList);
         }

         pageSequence.setWizardPageDefinition(wizardPageDefinition);
         pages.add(pageSequence);
      }
      category.setChildPages(pages);


      List categoryNodes = categoryNode.getChild("childCategories").getChildren("category");
      List categories = new ArrayList();
      for(Iterator i = categoryNodes.iterator(); i.hasNext(); ) {
         Element pageSequenceNode = (Element)i.next();
         WizardCategory childCategory = new WizardCategory();
         childCategory.setParentCategory(category);
         readCategoriesAndPages(wizard, childCategory, pageSequenceNode, importData);
         categories.add(childCategory);
      }
      category.setChildCategories(categories);
   }


   protected void replaceIds(Wizard wizard, Map guidanceMap, Map formsMap, Map styleMap)
   {
      wizard.setEvalWorkflows(getWorkflowManager().createEvalWorkflows(wizard));
      
      replaceCatIds(wizard.getRootCategory(), guidanceMap, formsMap, styleMap);

      if(wizard.getEvaluationDevice() != null && wizard.getEvaluationDevice().getValue() != null)
         wizard.setEvaluationDevice(idManager.getId(
            (String)formsMap.get(wizard.getEvaluationDevice().getValue())  ));

      if(wizard.getReflectionDevice() != null && wizard.getReflectionDevice().getValue() != null)
         wizard.setReflectionDevice(idManager.getId(
            (String)formsMap.get(wizard.getReflectionDevice().getValue())  ));

      if(wizard.getReviewDevice() != null && wizard.getReviewDevice().getValue() != null)
         wizard.setReviewDevice(idManager.getId(
            (String)formsMap.get(wizard.getReviewDevice().getValue())  ));

      if(wizard.getGuidanceId() != null && wizard.getGuidanceId().getValue() != null &&
            wizard.getGuidanceId().getValue().length() > 0) {
         Guidance wizardGuidance = (Guidance)guidanceMap.get( wizard.getGuidanceId().getValue());
         if(wizardGuidance == null)
            throw new NullPointerException("Guidance for Wizard was not found");
         
         wizardGuidance.setSecurityQualifier(wizard.getNewId());
         getGuidanceManager().saveGuidance(wizardGuidance);
         wizard.setGuidanceId( wizardGuidance.getId() );
      }
      if (wizard.getStyleId() != null && wizard.getStyleId().getValue() != null && 
            wizard.getStyleId().getValue().length() > 0) {
         Style wizardStyle = (Style)styleMap.get( wizard.getStyleId().getValue());
         if(wizardStyle == null)
            throw new NullPointerException("Style for Wizard was not found");
         getStyleManager().storeStyle(wizardStyle, false);
         wizard.setStyle(wizardStyle);
      }
   }
   
   /**
    * runs through each of the pages in the category and maps the guidance and 
    * additional forms from the old ids to the new ids.  It saves and sets the
    * guidance and page style as well.  Lastly, it loops into the sub categories.
    * @param cat  WizardCategory
    * @param guidanceMap Map
    * @param formsMap Map
    * @param styleMap Map
    */
   protected void replaceCatIds(WizardCategory cat, Map guidanceMap, Map formsMap, Map styleMap)
   {
      for(Iterator i = cat.getChildPages().iterator(); i.hasNext(); ) {
         WizardPageSequence sequence = (WizardPageSequence)i.next();
         WizardPageDefinition definition = (WizardPageDefinition)sequence.getWizardPageDefinition();
         definition.setSiteId(cat.getWizard().getSiteId());

         if(definition.getEvaluationDevice() != null && definition.getEvaluationDevice().getValue() != null)
            definition.setEvaluationDevice(idManager.getId(
               (String)formsMap.get(definition.getEvaluationDevice().getValue())  ));

         if(definition.getReflectionDevice() != null && definition.getReflectionDevice().getValue() != null)
            definition.setReflectionDevice(idManager.getId(
               (String)formsMap.get(definition.getReflectionDevice().getValue())  ));

         if(definition.getReviewDevice() != null && definition.getReviewDevice().getValue() != null)
            definition.setReviewDevice(idManager.getId(
               (String)formsMap.get(definition.getReviewDevice().getValue())  ));

         definition.setEvalWorkflows(
               new HashSet(getWorkflowManager().createEvalWorkflows(definition)));
         
         List newAddForms = new ArrayList();
         for(Iterator ii = definition.getAdditionalForms().iterator(); ii.hasNext(); ) {
            String addForm = (String)ii.next();
            if(addForm != null)
               newAddForms.add(formsMap.get(addForm));
         }
         definition.setAdditionalForms(newAddForms);

         if(definition.getGuidanceId() != null && definition.getGuidanceId().getValue() != null && 
               definition.getGuidanceId().getValue().length() > 0) {
            Guidance pageDefGuidance = (Guidance)guidanceMap.get( definition.getGuidanceId().getValue() );
            
            if(pageDefGuidance == null)
               throw new NullPointerException("Guidance for Wizard Page was not found");
            
            pageDefGuidance.setSecurityQualifier(definition.getVirtualId());
            getGuidanceManager().saveGuidance(pageDefGuidance);
            definition.setGuidanceId( pageDefGuidance.getId() );

            definition.setGuidance(pageDefGuidance);
         }
         
         if(definition.getStyleId() != null && definition.getStyleId().getValue() != null && 
               definition.getStyleId().getValue().length() > 0) {
            Style pageDefStyle= (Style)styleMap.get( definition.getStyleId().getValue() );
            
            if(pageDefStyle== null)
               throw new NullPointerException("Style for Wizard Page was not found");
            
            getStyleManager().storeStyle(pageDefStyle, false);
            definition.setStyle(pageDefStyle);
         }
      }
      for(Iterator i = cat.getChildCategories().iterator(); i.hasNext(); ) {
         WizardCategory childCat = (WizardCategory)i.next();

         replaceCatIds(childCat, guidanceMap, formsMap, styleMap);
      }
   }

   public String packageForDownload(Map params, OutputStream out) throws IOException {

      String[] formIdObj = (String[])params.get(DOWNLOAD_WIZARD_ID_PARAM);
      packageWizardForExport(formIdObj[0], out);
            
      //Blank filename for now -- no more dangerous, since the request is in the form of a filename
      return "";
   }

   
   protected void packageWizardForExport(String wizardId, OutputStream os, int checkAuthz) throws IOException
   {
      if (checkAuthz == WIZARD_EXPORT_CHECK)
         getAuthorizationFacade().checkPermission(WizardFunctionConstants.EXPORT_WIZARD, 
               idManager.getId(ToolManager.getCurrentPlacement().getContext()));

      CheckedOutputStream checksum = new CheckedOutputStream(os, new Adler32());

      ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(checksum));
      try {
         putWizardIntoZip(wizardId, zos, checkAuthz);
      } catch(ServerOverloadException soe) {
         logger.warn(soe);
      }

      zos.finish();
      zos.flush();
   }

   public void packageWizardForExport(String wizardId, OutputStream os) throws IOException
   {
      packageWizardForExport(wizardId, os, WIZARD_EXPORT_CHECK);
   }

   /**
    * Puts the wizard definition xml into the zip, then places all the forms
    * into the stream, then
    * @param wizardId String the wizard to export
    * @param zos ZipOutputStream the place to export the wizard too
    * @throws IOException
    * @throws ServerOverloadException
    */
   public void putWizardIntoZip(String wizardId, ZipOutputStream zos)
      throws IOException, ServerOverloadException
   {
      putWizardIntoZip(wizardId, zos, WIZARD_EXPORT_CHECK);
   }
   

   protected void putWizardIntoZip(String wizardId, ZipOutputStream zos, int checkAuthz)
                     throws IOException, ServerOverloadException
   {

      Map  exportForms = new HashMap(); /* key: form id   value: not needed */
      Map  exportFiles = new HashMap(); /* key: uuid   value: ContentResource  */
      List exportGuidanceIds = new ArrayList(); /* List of guidance id */
      Set exportStyleIds = new HashSet(); /* Set of style id */

      Wizard   wiz = getWizard(wizardId, checkAuthz);
      Document document = new Document(wizardToXML(wiz, exportForms, exportFiles, exportGuidanceIds, exportStyleIds));
      ZipEntry newfileEntry = null;


      storeStringInZip(zos, (new XMLOutputter()).outputString(document), "wizardDefinition.xml");

      // Allow access to the various files for export
      getSecurityService().pushAdvisor(new AllowAllSecurityAdvisor());
      
      // put the forms into the zip
      for(Iterator i = exportForms.keySet().iterator(); i.hasNext(); ) {
         String id = (String)i.next();

         if(id != null && id.length() > 0) {
            newfileEntry = new ZipEntry("forms/" + id + ".form");

            zos.putNextEntry(newfileEntry);
            // Put the file into the zip entry without permissions
            structuredArtifactDefinitionManager.packageFormForExport(id, zos, false);
            zos.closeEntry();
         }
      }

      // put the resources into the zip
      for(Iterator i = exportFiles.keySet().iterator(); i.hasNext(); ) {
         String id = (String)i.next();

         if(id != null && id.length() > 0) {
            ContentResource resource = (ContentResource)exportFiles.get(id);
            storeFileInZip(zos,
                  resource.streamContent(),
                  exportPathFromResource(resource, id)
                   );
         }
      }

      // put the guidance into the stream
      //  Guidance doesn't have permissions but the attached files could
      if(exportGuidanceIds.size() > 0) {
         newfileEntry = new ZipEntry("guidance/guidanceList");
         zos.putNextEntry(newfileEntry);
         getGuidanceManager().packageGuidanceForExport(exportGuidanceIds, zos);
         zos.closeEntry();
      }
      // put the guidance into the stream
      // Style doesn't have permissions but attached files could
      if(exportStyleIds.size() > 0) {
         newfileEntry = new ZipEntry("style/styleList");
         zos.putNextEntry(newfileEntry);
         getStyleManager().packageStyleForExport(exportStyleIds, zos);
         zos.closeEntry();
      }

      exportForms.clear();
      exportFiles.clear();
      exportGuidanceIds.clear();
      exportStyleIds.clear();
   }

   private String exportPathFromResource(ContentResource resource, String id)
   {
      String fileName = resource.getProperties().getProperty(
               resource.getProperties().getNamePropDisplayName());
      return resource.getContentType() +"/" + id + "/" +
      fileName.substring(fileName.lastIndexOf('\\')+1);
   }

   private Element wizardToXML(Wizard wiz, Map exportForms, Map exportFiles, List exportGuidanceIds, Set exportStyleIds)
   {
       Element rootNode = new Element("ospiWizard");

       if(wiz == null)
         return rootNode;

       rootNode.setAttribute("formatVersion", "2.1");

       Element attrNode = new Element("name");
       attrNode.addContent(new CDATA(wiz.getName()));
       rootNode.addContent(attrNode);

       attrNode = new Element("description");
       attrNode.addContent(new CDATA(wiz.getDescription()));
       rootNode.addContent(attrNode);

       attrNode = new Element("keywords");
       attrNode.addContent(new CDATA(wiz.getKeywords()));
       rootNode.addContent(attrNode);

       attrNode = new Element("type");
       attrNode.addContent(new CDATA(wiz.getType()));
       rootNode.addContent(attrNode);

       attrNode = new Element("sequence");
       attrNode.addContent(new CDATA(wiz.getSequence()+""));
       rootNode.addContent(attrNode);

       //   put the wizard evaluators into the xml
      Element evaluatorsNode = new Element("evaluators");
      Collection evaluators = getWizardEvaluators(wiz.getId(), true);
      for (Iterator i = evaluators.iterator(); i.hasNext();) {
         Agent agent = (Agent) i.next();
         attrNode = new Element("evaluator");
         attrNode.setAttribute("isRole", Boolean.toString(agent.isRole()));
         attrNode.addContent(new CDATA(agent.getId().getValue()));
         evaluatorsNode.addContent(attrNode);
      }
      rootNode.addContent(evaluatorsNode);


      // put the evaluation, review, reflection
       rootNode.addContent(putWorkflowObjectToXml(wiz, exportForms));


       //   add the wizard guidance to the list
       attrNode = new Element("guidance");
       if(wiz.getGuidanceId() != null && !wiz.getGuidanceId().getValue().equals("")) {
          exportGuidanceIds.add(wiz.getGuidanceId().getValue());
          attrNode.addContent(new CDATA(wiz.getGuidanceId().getValue()));
       }
       rootNode.addContent(attrNode);


       //   put the categories/pages into the xml
       rootNode.addContent(putCategoryToXml(wiz.getRootCategory(), exportForms, exportGuidanceIds, exportStyleIds));


       //  add the wizard style to the list
       attrNode = new Element("style");
       if (wiz.getStyle() != null) {
          exportStyleIds.add(wiz.getStyle().getId().getValue());
          attrNode.addContent(new CDATA(wiz.getStyle().getId().getValue()));
       }
       rootNode.addContent(attrNode);
       
       /*
       //   put the styles into the xml
       
       //for(Iterator i = wiz.getStyle().iterator(); i.hasNext(); ) {
           Style style = wiz.getStyle();
           String          resId = style.getStyleFile().getValue();
           String nodeId = getContentHosting().resolveUuid(resId);

          //Element       styleNode = new Element("style");
           //attrNode.addContent(new CDATA(nodeId));
           attrNode.addContent(new CDATA(style.getId().getValue()));
          //attrNode.addContent(styleNode);

         //String id = getContentHosting().resolveUuid(nodeId);
          ContentResource resource = null;
            try {
               resource = getContentHosting().getResource(nodeId);
            } catch(PermissionException pe) {
              logger.warn("Failed loading content: no permission to view file", pe);
            } catch(TypeException pe) {
              logger.warn("Wrong type", pe);
          } catch(IdUnusedException pe) {
              logger.warn("UnusedId: ", pe);
          }

          exportFiles.put(nodeId, resource);
       //}
       rootNode.addContent(attrNode);
*/
       return rootNode;
   }


   private Element putCategoryToXml(WizardCategory cat, Map exportForms, List exportGuidanceIds, Set exportStyleIds)
   {
      Element categoryNode = new Element("category");

      if(cat == null)
         return categoryNode;

      Element attrNode = new Element("title");
      attrNode.addContent(new CDATA(cat.getTitle()));
      categoryNode.addContent(attrNode);

      attrNode = new Element("description");
      attrNode.addContent(new CDATA(cat.getDescription()));
      categoryNode.addContent(attrNode);

      attrNode = new Element("keywords");
      attrNode.addContent(new CDATA(cat.getKeywords()));
      categoryNode.addContent(attrNode);

      attrNode = new Element("sequence");
      attrNode.addContent(new CDATA(cat.getSequence()+""));
      categoryNode.addContent(attrNode);

      Element pagesNode = new Element("pages");
      for(Iterator i = cat.getChildPages().iterator(); i.hasNext(); ) {
        WizardPageSequence pageSequence = (WizardPageSequence)i.next();
        pagesNode.addContent(putPageSequenceToXml(pageSequence, exportForms, exportGuidanceIds, exportStyleIds));
      }
      categoryNode.addContent(pagesNode);

      Element childCategoriesNode = new Element("childCategories");
      for(Iterator i = cat.getChildCategories().iterator(); i.hasNext(); ) {
         WizardCategory childCat = (WizardCategory)i.next();
         childCategoriesNode.addContent(putCategoryToXml(childCat, exportForms, exportGuidanceIds, exportStyleIds));
      }
      categoryNode.addContent(childCategoriesNode);

      return categoryNode;
   }

   private Element putPageSequenceToXml(WizardPageSequence pageSequence,
                              Map exportForms, List exportGuidanceIds, Set exportStyleIds)
   {
      Element pageSequenceNode = new Element("pageSequence");

      if(pageSequence == null)
         return pageSequenceNode;

      Element attrNode = new Element("title");
      attrNode.addContent(new CDATA(pageSequence.getTitle()));
      pageSequenceNode.addContent(attrNode);

      attrNode = new Element("sequence");
      attrNode.addContent(new CDATA(pageSequence.getSequence() + ""));
      pageSequenceNode.addContent(attrNode);

      pageSequenceNode.addContent(putPageDefinitionToXml(
            pageSequence.getWizardPageDefinition(), exportForms, exportGuidanceIds, exportStyleIds));

      return pageSequenceNode;
   }

   private Element putPageDefinitionToXml(WizardPageDefinition pageDef,
                     Map exportForms, List exportGuidanceIds, Set exportStyleIds)
   {
      Element pageDefNode = new Element("pageDef");

      if(pageDef == null)
         return pageDefNode;

      pageDefNode.addContent(putWorkflowObjectToXml(pageDef, exportForms));

      Element attrNode = new Element("title");
      attrNode.addContent(new CDATA(pageDef.getTitle()));
      pageDefNode.addContent(attrNode);

      attrNode = new Element("description");
      attrNode.addContent(new CDATA(pageDef.getDescription()));
      pageDefNode.addContent(attrNode);

      attrNode = new Element("initialStatus");
      attrNode.addContent(new CDATA(pageDef.getInitialStatus()));
      pageDefNode.addContent(attrNode);

      Element attachmentsNode = new Element("attachments");
      for(Iterator i = pageDef.getAttachments().iterator(); i.hasNext(); ) {
         String attachment = (String)i.next();

         attrNode = new Element("ref"); 
         attrNode.addContent(new CDATA(attachment));
         attachmentsNode.addContent(attrNode);
      }
      pageDefNode.addContent(attachmentsNode);

      Element additionalFormsNode = new Element("additionalForms");
      for(Iterator i = pageDef.getAdditionalForms().iterator(); i.hasNext(); ) {
         String additionalForm = (String)i.next();

         attrNode = new Element("form");
         attrNode.addContent(new CDATA(additionalForm));
         additionalFormsNode.addContent(attrNode);

         exportForms.put(additionalForm, Integer.valueOf(0));
      }
      pageDefNode.addContent(additionalFormsNode);

      attrNode = new Element("guidance");
      if(pageDef.getGuidance() != null && pageDef.getGuidance().getId() != null) {
         exportGuidanceIds.add(pageDef.getGuidance().getId().getValue());
         attrNode.addContent(new CDATA(pageDef.getGuidance().getId().getValue()));
      }
      pageDefNode.addContent(attrNode);

      attrNode = new Element("style");
      if (pageDef.getStyle() != null && pageDef.getStyle().getId() != null) {
         exportStyleIds.add(pageDef.getStyle().getId().getValue());
         attrNode.addContent(new CDATA(pageDef.getStyle().getId().getValue()));
      }
      pageDefNode.addContent(attrNode);

      Element     evaluatorsNode = new Element("evaluators");
      Collection  evaluators = getWizardPageDefEvaluators(pageDef.getId(), true);
      for(Iterator i = evaluators.iterator(); i.hasNext(); ) {
         Agent agent = (Agent) i.next();
         attrNode = new Element("evaluator");
         attrNode.setAttribute("isRole", Boolean.toString(agent.isRole()));
         attrNode.addContent(new CDATA(agent.getId().getValue()));
         evaluatorsNode.addContent(attrNode);
      }
      pageDefNode.addContent(evaluatorsNode);

      return pageDefNode;
   }


   protected Collection getWizardPageDefEvaluators(Id wizardPageDefId, boolean useAgentId) {
         Collection evaluators = new HashSet();
         Collection viewerAuthzs = authorizationFacade.getAuthorizations(null,
               MatrixFunctionConstants.EVALUATE_MATRIX, wizardPageDefId);
         for (Iterator i = viewerAuthzs.iterator(); i.hasNext();) {
            Authorization evaluator = (Authorization) i.next();
            if (useAgentId)
               evaluators.add(evaluator.getAgent());
            else
               evaluators.add(evaluator.getAgent().getId());
         }
         return evaluators;
      }
   
   public WizardPageSequence getWizardPageSeqByDef(Id id) {
      WizardPageSequence wps = null;
      Object[] params = new Object[]{id};
      List seqs = getHibernateTemplate().find("from WizardPageSequence w where w.wizardPageDefinition.id=?", params);
      if (seqs.size() > 0) {
         wps = (WizardPageSequence)seqs.get(0);
      }
      
      return wps;
   }
   
   public WizardPageDefinition getWizardPageDefinition(Id id) {
		return getWizardPageDefinition(id, false);
	}

	public WizardPageDefinition getWizardPageDefinition(final Id id,
			final boolean deep) {
		return (WizardPageDefinition) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) {
						WizardPageDefinition def = (WizardPageDefinition) session
								.get(WizardPageDefinition.class, id);
						if (deep) {
							Hibernate.initialize(def.getPages());
						}
						return def;
					}
				});
	}
  
   public List getCompletedWizardPagesByPageDef(Id id) {
      Object[] params = new Object[]{id};
      return getHibernateTemplate().find("from CompletedWizardPage w where w.wizardPageDefinition.wizardPageDefinition.id=?", params);
   }


   protected Collection getWizardEvaluators(Id wizardId, boolean useAgentId) {
         Collection evaluators = new HashSet();
         Collection viewerAuthzs = authorizationFacade.getAuthorizations(null,
              WizardFunctionConstants.EVALUATE_WIZARD, wizardId);
         for (Iterator i = viewerAuthzs.iterator(); i.hasNext();) {
            Authorization evaluator = (Authorization) i.next();
            if (useAgentId)
               evaluators.add(evaluator.getAgent());
            else
               evaluators.add(evaluator.getAgent().getId());
         }
         return evaluators;
      }

   /**
    * 2.1 - only does type="form".
    * @param objWorkflow
    * @return Element
    */
   private Element putWorkflowObjectToXml(ObjectWithWorkflow objWorkflow, Map exportForms)
   {
      Element workflowObjNode = new Element("workflow");

      Element attrNode = new Element("evaluationDevice");
      if(objWorkflow.getEvaluationDevice() != null)
         attrNode.addContent(new CDATA(objWorkflow.getEvaluationDevice().getValue()));
      workflowObjNode.addContent(attrNode);

      if(objWorkflow.getEvaluationDevice() != null)
         exportForms.put(objWorkflow.getEvaluationDevice().getValue(), Integer.valueOf(0));

      attrNode = new Element("evaluationDeviceType");
      attrNode.addContent(new CDATA(objWorkflow.getEvaluationDeviceType()));
      workflowObjNode.addContent(attrNode);

      attrNode = new Element("reflectionDevice");
      if(objWorkflow.getReflectionDevice() != null)
         attrNode.addContent(new CDATA(objWorkflow.getReflectionDevice().getValue()));
      workflowObjNode.addContent(attrNode);

      if(objWorkflow.getReflectionDevice() != null)
         exportForms.put(objWorkflow.getReflectionDevice().getValue(), Integer.valueOf(0));

      attrNode = new Element("reflectionDeviceType");
      attrNode.addContent(new CDATA(objWorkflow.getReflectionDeviceType()));
      workflowObjNode.addContent(attrNode);

      attrNode = new Element("reviewDevice");
      if(objWorkflow.getReviewDevice() != null)
         attrNode.addContent(new CDATA(objWorkflow.getReviewDevice().getValue()));
      workflowObjNode.addContent(attrNode);

      if(objWorkflow.getReviewDevice() != null)
         exportForms.put(objWorkflow.getReviewDevice().getValue(), Integer.valueOf(0));

      attrNode = new Element("reviewDeviceType");
      attrNode.addContent(new CDATA(objWorkflow.getReviewDeviceType()));
      workflowObjNode.addContent(attrNode);

      return workflowObjNode;
   }


   protected void storeStringInZip(ZipOutputStream zos, String in,
         String entryName) throws IOException {

      ZipEntry newfileEntry = new ZipEntry(entryName);

      zos.putNextEntry(newfileEntry);
      zos.write(in.getBytes("UTF-8"));
      zos.closeEntry();
   }


   protected void storeFileInZip(ZipOutputStream zos, Reader in,
         String entryName) throws IOException {

      char data[] = new char[1024 * 10];

      if (File.separatorChar == '\\') {
         entryName = entryName.replace('\\', '/');
      }

      ZipEntry newfileEntry = new ZipEntry(entryName);

      zos.putNextEntry(newfileEntry);

      BufferedReader origin = new BufferedReader(in, data.length);
      OutputStreamWriter osw = new OutputStreamWriter(zos);
      int count;
      while ((count = origin.read(data, 0, data.length)) != -1) {
         osw.write(data, 0, count);
      }
      origin.close();
      osw.flush();
      zos.closeEntry();
      in.close();
   }


   protected void storeFileInZip(ZipOutputStream zos, InputStream in,
         String entryName) throws IOException {

      byte data[] = new byte[1024 * 10];

      if (File.separatorChar == '\\') {
         entryName = entryName.replace('\\', '/');
      }

      ZipEntry newfileEntry = new ZipEntry(entryName);

      zos.putNextEntry(newfileEntry);

      BufferedInputStream origin = new BufferedInputStream(in, data.length);
      int count;
      while ((count = origin.read(data, 0, data.length)) != -1) {
         zos.write(data, 0, count);
      }
      origin.close();
      zos.closeEntry();
      in.close();
   }

   private byte[] readStreamToBytes(InputStream inStream) throws IOException {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      byte data[] = new byte[10 * 1024];

      int count;
      while ((count = inStream.read(data, 0, 10 * 1024)) != -1) {
         bytes.write(data, 0, count);
      }
      byte[] tmp = bytes.toByteArray();
      bytes.close();
      return tmp;
   }

   public PresentableObjectHome getXmlRenderer() {
      return xmlRenderer;
   }

   public void setXmlRenderer(PresentableObjectHome xmlRenderer) {
      this.xmlRenderer = xmlRenderer;
   }

   public Element getArtifactAsXml(Artifact art) {
      return getXmlRenderer().getArtifactAsXml(art);
   }
   
   public Element getArtifactAsXml(Artifact artifact, String container, String site, String context) {
	   return getXmlRenderer().getArtifactAsXml(artifact, container, site, context);
   }

   public Collection findBySharedOwnerAndType(List ownerList, String type) {
      return null; // not implemented for wizards (only relevant to portfolios)
   }

   public Collection findBySharedOwnerAndType(List ownerList, String type, MimeType mimeType) {
      return null; // not implemented for wizards (only relevant to portfolios)
   }

   public Collection findByOwnerAndType(Id owner, String type) {
      return findByOwner(owner);
   }

   public Collection findByOwnerAndType(Id owner, String type, MimeType mimeType) {
      // TODO Auto-generated method stub
      return null;
   }

   public Collection findByOwner(Id owner) {
      return getCompletedWizards(owner.getValue());
   }

   public Collection findByWorksiteAndType(Id worksiteId, String type) {
      // TODO Auto-generated method stub
      return null;
   }

   public Collection findByWorksite(Id worksiteId) {
      // TODO Auto-generated method stub
      return null;
   }

   public Artifact load(Id id) {
      CompletedWizard cw = this.getCompletedWizard(id);
      if (cw != null) {
	      List reflections = getReviewManager().getReviewsByParentAndType(cw.getId().getValue(), 
	            Review.REFLECTION_TYPE, cw.getWizard().getSiteId(),
	            WizardEntityProducer.WIZARD_PRODUCER);
	      List evaluations = getReviewManager().getReviewsByParentAndType(cw.getId().getValue(), 
	            Review.EVALUATION_TYPE, cw.getWizard().getSiteId(),
	            WizardEntityProducer.WIZARD_PRODUCER);
	      List feedback = getReviewManager().getReviewsByParentAndType(cw.getId().getValue(), 
	            Review.FEEDBACK_TYPE, cw.getWizard().getSiteId(),
	            WizardEntityProducer.WIZARD_PRODUCER);
	      cw.setReflections(reflections);
	      cw.setEvaluations(evaluations);
	      cw.setFeedback(feedback);
	      
	      loadWizardPageReviews(cw.getRootCategory());
	      //cw.get
	      cw.setHome(this);
	      return cw;
      }
      return null;
   }
   
   private void loadWizardPageReviews(CompletedWizardCategory category) {
      for (Iterator pages = category.getChildPages().iterator(); pages.hasNext();) {
         CompletedWizardPage cPage = (CompletedWizardPage) pages.next();
         WizardPage page = cPage.getWizardPage();
         
         List reflections = getReviewManager().getReviewsByParentAndType(page.getId().getValue(), 
               Review.REFLECTION_TYPE, page.getPageDefinition().getSiteId(),
               WizardEntityProducer.WIZARD_PRODUCER);
         List evaluations = getReviewManager().getReviewsByParentAndType(page.getId().getValue(), 
               Review.EVALUATION_TYPE, page.getPageDefinition().getSiteId(),
               WizardEntityProducer.WIZARD_PRODUCER);
         List feedback = getReviewManager().getReviewsByParentAndType(page.getId().getValue(), 
               Review.FEEDBACK_TYPE, page.getPageDefinition().getSiteId(),
               WizardEntityProducer.WIZARD_PRODUCER);
         page.setReflections(reflections);
         page.setEvaluations(evaluations);
         page.setFeedback(feedback);
         
         page.getAttachments().size();
         page.getPageForms().size();
         
         //Make sure that the attachments and forms have been added to the security advisor
         getMatrixManager().getPageContents(page);
         getMatrixManager().getPageForms(page);
         
      }
      
      for (Iterator i = category.getChildCategories().iterator(); i.hasNext();) {
         CompletedWizardCategory cat = (CompletedWizardCategory) i.next();
         loadWizardPageReviews(cat);
      }
   }

   public Collection findByType(String type) {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean getLoadArtifacts() {
      // TODO Auto-generated method stub
      return false;
   }

   public void setLoadArtifacts(boolean loadArtifacts) {
      // TODO Auto-generated method stub

   }

   public Type getType() {
      return new org.sakaiproject.metaobj.shared.model.Type(idManager.getId("completedWizard"), "Completed Wizard");
   }

   public String getExternalType() {
      return getType().getId().getValue();
   }

   public Artifact createInstance() {
      Artifact instance = new CompletedWizard();
      prepareInstance(instance);
      return instance;
   }

   public void prepareInstance(Artifact object) {
      object.setHome(this);
   }

   public Artifact createSample() {
      // TODO Auto-generated method stub
      return null;
   }

   public Collection findByOwner(Agent owner) throws FinderException {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean isInstance(Artifact testObject) {
      // TODO Auto-generated method stub
      return false;
   }

   public void refresh() {
      // TODO Auto-generated method stub

   }

   public String getExternalUri(Id artifactId, String name) {
      // TODO Auto-generated method stub
      return null;
   }

   public InputStream getStream(Id artifactId) {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean isSystemOnly() {
      // TODO Auto-generated method stub
      return false;
   }

   public Class getInterface() {
      // TODO Auto-generated method stub
      return null;
   }
   
   public ReviewManager getReviewManager() {
      return reviewManager;
   }

   public void setReviewManager(ReviewManager reviewManager) {
      this.reviewManager = reviewManager;
   }

   public StyleManager getStyleManager() {
      return styleManager;
   }

   public void setStyleManager(StyleManager styleManager) {
      this.styleManager = styleManager;
   }
   
   protected List getWizardsByStyle(Id styleId) {
      Object[] params = new Object[]{styleId};
      return getHibernateTemplate().find("from Wizard w where w.style.id=? " , 
               params);
   }

   public boolean checkStyleConsumption(Id styleId) {
      List wizards = getWizardsByStyle(styleId);
      if (wizards != null && !wizards.isEmpty() && wizards.size() > 0)
         return true;
      
      return false;
   }

   public List getStyles(Id objectId) {
      CompletedWizard wizard = getCompletedWizardByPage(objectId);

      if (wizard != null) {
         List styles = new ArrayList();
         if (wizard.getWizard().getStyle() != null) {
            styles.add(wizard.getWizard().getStyle());
         }

         WizardPage wp = getMatrixManager().getWizardPage(objectId);
         if (wp.getPageDefinition().getStyle() != null) {
            styles.add(wp.getPageDefinition().getStyle());
         }
         return styles;
      }

      wizard = getCompletedWizard(objectId);

      if (wizard != null) {
         List styles = new ArrayList();
         if (wizard.getWizard().getStyle() != null) {
            styles.add(wizard.getWizard().getStyle());
         }

         return styles;
      }

      return null;
   }

   public void importResources(String fromContext, String toContext, List resourceIds) {
      try {
         List wizards = this.findPublishedWizards(fromContext);
         if (wizards == null) {
            return;
         }

         for (Iterator iter = wizards.iterator(); iter.hasNext();) {
            Wizard wizard = (Wizard)iter.next();
            Id id = wizard.getId();

            getHibernateTemplate().evict(wizard);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            //TODO think it's okay to not check permissions here?
            packageWizardForExport(id.getValue(), bos, WIZARD_NO_CHECK);
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

            importWizard(getIdManager().getId(toContext), bis);
            bos.close();
            bis.close();
         }
      }
      catch (IOException e) {
         logger.error("", e);
         throw new OspException(e);
      } catch (ImportException e) {
         logger.error("", e);
         throw new OspException(e);
      }

   }

   protected List getEvaluatableWizardPages(Agent agent, List<Agent> roles, List<String> worksiteIds, HashMap siteHash) {
	   String[] paramNames;
	   Object[] params;

	   boolean rolesNotEmpty = (roles != null && roles.size() > 0);
	   boolean sitesNotEmpty = (worksiteIds != null && worksiteIds.size() > 0);
	   if(rolesNotEmpty && sitesNotEmpty){
		   paramNames = new String[] {"evaluate", "pendingStatus", "user", "roles", "siteIds"};
		   params =  new Object[]{MatrixFunctionConstants.EVALUATE_MATRIX,
				   MatrixFunctionConstants.PENDING_STATUS,
				   agent, roles, worksiteIds};
	   }else{
		   if(!rolesNotEmpty){
			   if(!sitesNotEmpty){
				   paramNames = new String[] {"evaluate", "pendingStatus", "user"};
				   params =  new Object[]{MatrixFunctionConstants.EVALUATE_MATRIX,
						   MatrixFunctionConstants.PENDING_STATUS,
						   agent};
			   }else{
				   paramNames = new String[] {"evaluate", "pendingStatus", "user", "siteIds"};
				   params =  new Object[]{MatrixFunctionConstants.EVALUATE_MATRIX,
						   MatrixFunctionConstants.PENDING_STATUS,
						   agent, worksiteIds};
			   }
		   }else{
			   paramNames = new String[] {"evaluate", "pendingStatus", "user", "roles"};
			   params =  new Object[]{MatrixFunctionConstants.EVALUATE_MATRIX,
					   MatrixFunctionConstants.PENDING_STATUS,
					   agent, roles};
		   }
	   }


	   String evaulatableSQL = "select distinct new " +
	   "org.theospi.portfolio.wizard.model.EvaluationContentWrapperForWizardPage(" +
	   "cwp.wizardPage.id, " +
	   "cwp.wizardPage.pageDefinition.title, cwp.category.wizard.owner, " +
	   "cwp.wizardPage.modified, " +
	   "cwp.category.wizard.wizard.type, cwp.wizardPage.pageDefinition.siteId) " +
	   "from CompletedWizardPage cwp, " +
	   "Authorization auth " +
	   "where cwp.wizardPage.pageDefinition.id = auth.qualifier " +
	   "and auth.function = :evaluate and cwp.wizardPage.status = :pendingStatus and " +
	   "(auth.agent=:user";
	   if(rolesNotEmpty)
		   evaulatableSQL += " or auth.agent in ( :roles )";
	   evaulatableSQL += ") ";
	   if(sitesNotEmpty)
		   evaulatableSQL += " and cwp.wizardPage.pageDefinition.siteId in ( :siteIds )";

	   List wizardPages = this.getHibernateTemplate().findByNamedParam(evaulatableSQL, paramNames, params );
      
      // filter out group-restricted users
      List filteredWizardPages = new ArrayList();
      for ( Iterator it=wizardPages.iterator(); it.hasNext(); ) {
         EvaluationContentWrapper evalItem = (EvaluationContentWrapper)it.next();
         WizardPage wizPage = matrixManager.getWizardPage( evalItem.getId() );
         
         WizardPageSequence seq = getWizardPageSeqByDef(wizPage.getPageDefinition().getId());
         Wizard wizard = seq.getCategory().getWizard();
         
         if ( !allowAllGroups && wizard.getReviewerGroupAccess() == WizardMatrixConstants.NORMAL_GROUP_ACCESS ) {
            HashSet siteGroupUsers = (HashSet)siteHash.get( wizard.getSiteId() );
            if ( siteGroupUsers != null && siteGroupUsers.contains(wizPage.getOwner().getId().getValue()) )
               filteredWizardPages.add( evalItem );
         }
         else {
            filteredWizardPages.add( evalItem );
         }
      }
      
      return filteredWizardPages;
   }
   
   protected List getEvaluatableWizards(Agent agent, List<Agent> roles, List<String> worksiteIds, HashMap siteHash) {
	   
	   String[] paramNames;
	   Object[] params;

	   boolean rolesNotEmpty = (roles != null && roles.size() > 0);
	   boolean sitesNotEmpty = (worksiteIds != null && worksiteIds.size() > 0);
	   if(rolesNotEmpty && sitesNotEmpty){
		   paramNames = new String[] {"evaluate", "pendingStatus", "user", "roles", "siteIds"};
		   params =  new Object[]{WizardFunctionConstants.EVALUATE_WIZARD,
				   MatrixFunctionConstants.PENDING_STATUS,
				   agent, roles, worksiteIds};
	   }else{
		   if(!rolesNotEmpty){
			   if(!sitesNotEmpty){
				   paramNames = new String[] {"evaluate", "pendingStatus", "user"};
				   params =  new Object[]{WizardFunctionConstants.EVALUATE_WIZARD,
						   MatrixFunctionConstants.PENDING_STATUS,
						   agent};
			   }else{
				   paramNames = new String[] {"evaluate", "pendingStatus", "user", "siteIds"};
				   params =  new Object[]{WizardFunctionConstants.EVALUATE_WIZARD,
						   MatrixFunctionConstants.PENDING_STATUS,
						   agent, worksiteIds};
			   }
		   }else{
			   paramNames = new String[] {"evaluate", "pendingStatus", "user", "roles"};
			   params =  new Object[]{WizardFunctionConstants.EVALUATE_WIZARD,
					   MatrixFunctionConstants.PENDING_STATUS,
					   agent, roles};
		   }
	   }


	   String evaluatableSQL = "select distinct new " +
	   "org.theospi.portfolio.wizard.model.EvaluationContentWrapperForWizard(" +
	   "cw.wizard.id, " +
	   "cw.wizard.name, cw.owner, " +
	   "cw.created, cw.wizard.siteId) " +
	   "from CompletedWizard cw, " +
	   "Authorization auth " +
	   "where cw.wizard.id = auth.qualifier " +
	   "and auth.function = :evaluate and cw.status = :pendingStatus and " +
	   "(auth.agent=:user";
	   
	   if(rolesNotEmpty)
		   evaluatableSQL += " or auth.agent in ( :roles )";
	   
	   evaluatableSQL += ") ";
	   
	   if(sitesNotEmpty)
		   evaluatableSQL += " and cw.wizard.siteId in ( :siteIds )";
	   
	   List wizards = this.getHibernateTemplate().findByNamedParam(evaluatableSQL, paramNames, params );

	   // filter out group-restricted users
      List filteredWizards = new ArrayList();
      for ( Iterator it=wizards.iterator(); it.hasNext(); ) {
         EvaluationContentWrapper evalItem = (EvaluationContentWrapper)it.next();
         Wizard wizard = getWizard( evalItem.getId() );
         CompletedWizard completedWiz = getCompletedWizard( wizard, evalItem.getOwner().getId(), false );
         
         if ( !allowAllGroups && wizard.getReviewerGroupAccess() == WizardMatrixConstants.NORMAL_GROUP_ACCESS ) {
            HashSet siteGroupUsers = (HashSet)siteHash.get( wizard.getSiteId() );
            if ( siteGroupUsers != null && siteGroupUsers.contains(completedWiz.getOwner().getId().getValue()) )
               filteredWizards.add( evalItem );
         }
         else {
            filteredWizards.add( evalItem );
         }
      }
      
      return filteredWizards;
   }
   
   /**
    * get all the cells, pages, and wizards that this user can evaluate within specified worksite(s)
    * @param agent Agent 
    * @param worksiteIds List of worksite Ids
    * @return List of org.theospi.portfolio.shared.model.EvaluationContentWrapper
    */
   public List getEvaluatableItems(Agent agent) {
      List siteList = getWorksiteManager().getUserSites();
		List siteIds = new ArrayList(siteList.size());
		
      for (Iterator i = siteList.iterator(); i.hasNext();) {
         Site site = (Site) i.next();
			siteIds.add( site.getId() );
      }
		
      return getEvaluatableItems(agent, siteIds);
   }
   
   /**
    ** Return set of users who share a group with the specified evalUser
    ** 
    ** @param evalUser evaluator user
    ** @param siteId   worksite id to serach
    ** @return Set of users the evalUser is qualified to evaluate (with group restrictions)
    **/
   private HashSet getSiteGroupUsers( Agent evalUser, String siteId ) {
      HashSet members = new HashSet();
      try {
         Site site = SiteService.getSite(siteId);
         
         if (site.hasGroups()) {
            Iterator groupIt = site.getGroupsWithMember( evalUser.getId().getValue() ).iterator();
            while (groupIt.hasNext()) {
               Iterator memberIt = ((Group)groupIt.next()).getMembers().iterator();
               while ( memberIt.hasNext() )
                  members.add( ((Member)memberIt.next()).getUserId() );
            }
         }
         else {
            Iterator memberIt = site.getMembers().iterator();
            while ( memberIt.hasNext() )
               members.add( ((Member)memberIt.next()).getUserId() );
         }
      }
      catch (IdUnusedException e) {
         logger.warn(this+".getSiteGroupUsers invalid site id: " + siteId );
      }
      
      return members;
   }
   
   /**
    * get all the cells, pages, and wizards that this user can evaluate within all worksites they are a member of
    * @param agent Agent 
    * @return List of org.theospi.portfolio.shared.model.EvaluationContentWrapper
    */
   public List getEvaluatableItems(Agent agent, List<String>siteIds) {
      List roles = new ArrayList();
      HashMap siteHash = new HashMap( siteIds.size() );
      
      // Find user roles in each specified site
      for (Iterator i = siteIds.iterator(); i.hasNext();) {
         String worksiteId = (String)i.next();
         List siteUserRoles = agent.getWorksiteRoles(worksiteId);
         roles.addAll( siteUserRoles );
         
         HashSet siteGroupUsers = getSiteGroupUsers( agent, worksiteId );
         siteHash.put( worksiteId, siteGroupUsers );
      }
      
      List evalItems = matrixManager.getEvaluatableCells(agent, roles, siteIds, siteHash);
      
      evalItems.addAll( getEvaluatableWizardPages(agent, roles, siteIds, siteHash) );
      evalItems.addAll( getEvaluatableWizards(agent, roles, siteIds, siteHash)  );
      
      return evalItems;
   }
   
   //
   // Injected compnent methods
   //
   public String getImportFolderName() {
      return importFolderName;
   }

   public void setImportFolderName(String importFolderName) {
      this.importFolderName = importFolderName;
   }

   /**
    * @return the matrixManager
    */
   public MatrixManager getMatrixManager() {
      return matrixManager;
   }

   /**
    * @param matrixManager the matrixManager to set
    */
   public void setMatrixManager(MatrixManager matrixManager) {
      this.matrixManager = matrixManager;
   }
   
   public LockManager getLockManager() {
      return lockManager;
   }
   public void setLockManager(LockManager lockManager) {
      this.lockManager = lockManager;
   }

   public EventService getEventService() {
	   return eventService;
   }
   public void setEventService(EventService eventService) {
	   this.eventService = eventService;
   }

   /**
    * @return Returns the worksiteManager.
    */
   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }
   /**
    * @param worksiteManager The worksiteManager to set.
    */
   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }

}
