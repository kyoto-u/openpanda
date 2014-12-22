/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.3/matrix/api-impl/src/java/org/theospi/portfolio/matrix/HibernateMatrixManagerImpl.java $
* $Id: HibernateMatrixManagerImpl.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
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
package org.theospi.portfolio.matrix;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.hibernate.AssertionFailure;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.jdom.Element;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupAlreadyDefinedException;
import org.sakaiproject.authz.api.GroupIdInvalidException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.content.api.LockManager;
import org.sakaiproject.email.cover.DigestService;
import org.sakaiproject.email.cover.EmailService;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.event.api.NotificationService;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.ImportException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.OverQuotaException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.security.AllowMapSecurityAdvisor;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.shared.DownloadableManager;
import org.sakaiproject.metaobj.shared.EntityContextFinder;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.ContentEntityUtil;
import org.sakaiproject.metaobj.shared.mgt.ContentEntityWrapper;
import org.sakaiproject.metaobj.shared.mgt.FormConsumer;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.MetaobjEntityManager;
import org.sakaiproject.metaobj.shared.mgt.PresentableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.FinderException;
import org.sakaiproject.metaobj.shared.model.FormConsumptionDetail;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.InvalidUploadException;
import org.sakaiproject.metaobj.shared.model.MimeType;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.service.legacy.resource.DuplicatableToolService;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.site.util.SiteConstants;
import org.sakaiproject.taggable.api.Link;
import org.sakaiproject.taggable.api.LinkManager;
import org.sakaiproject.taggable.api.TaggableActivity;
import org.sakaiproject.taggable.api.TaggableActivityProducer;
import org.sakaiproject.taggable.api.TaggableItem;
import org.sakaiproject.taggable.api.TaggingManager;
import org.sakaiproject.taggable.api.TaggingProvider;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.Preferences;
import org.sakaiproject.user.api.PreferencesService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.api.UserNotificationPreferencesRegistration;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.util.ResourceLoader;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.theospi.event.EventConstants;
import org.theospi.event.EventService;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.matrix.model.Attachment;
import org.theospi.portfolio.matrix.model.Cell;
import org.theospi.portfolio.matrix.model.Criterion;
import org.theospi.portfolio.matrix.model.Level;
import org.theospi.portfolio.matrix.model.Matrix;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;
import org.theospi.portfolio.matrix.model.WizardPage;
import org.theospi.portfolio.matrix.model.WizardPageDefinition;
import org.theospi.portfolio.matrix.model.WizardPageForm;
import org.theospi.portfolio.matrix.model.impl.MatrixContentEntityProducer;
import org.theospi.portfolio.matrix.model.impl.WizardPageDefinitionEntityImpl;
import org.theospi.portfolio.review.mgt.ReviewManager;
import org.theospi.portfolio.review.model.Review;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.CommonFormBean;
import org.theospi.portfolio.shared.model.EvaluationContentWrapper;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.model.ObjectWithWorkflow;
import org.theospi.portfolio.shared.model.OspException;
import org.theospi.portfolio.shared.model.WizardMatrixConstants;
import org.theospi.portfolio.style.StyleConsumer;
import org.theospi.portfolio.style.mgt.StyleManager;
import org.theospi.portfolio.style.model.Style;
import org.theospi.portfolio.tagging.api.DecoratedTaggableItem;
import org.theospi.portfolio.tagging.api.DecoratedTaggingProvider;
import org.theospi.portfolio.tagging.impl.DecoratedTaggableItemImpl;
import org.theospi.portfolio.tagging.impl.DecoratedTaggingProviderImpl;
import org.theospi.portfolio.wizard.WizardFunctionConstants;
import org.theospi.portfolio.wizard.mgt.WizardManager;
import org.theospi.portfolio.wizard.model.Wizard;
import org.theospi.portfolio.wizard.taggable.cover.WizardActivityProducer;
import org.theospi.portfolio.workflow.mgt.WorkflowManager;
import org.theospi.portfolio.workflow.model.Workflow;
import org.theospi.portfolio.workflow.model.WorkflowItem;
import org.theospi.utils.zip.UncloseableZipInputStream;

/**
 * @author rpembry
 */
public class HibernateMatrixManagerImpl extends HibernateDaoSupport
   implements MatrixManager, ReadableObjectHome, ArtifactFinder, DownloadableManager,
   PresentableObjectHome, DuplicatableToolService, StyleConsumer, FormConsumer {
   
   static final private String   IMPORT_BASE_FOLDER_ID = "importedMatrices";
   public static final String PROVIDERS_PARAM = "providers";

   private IdManager idManager;
   private AuthenticationManager authnManager = null;
   private AuthorizationFacade authzManager = null;
   private AgentManager agentManager = null;
   private PresentableObjectHome xmlRenderer;
   private WorksiteManager worksiteManager;
   private LockManager lockManager;
   private boolean loadArtifacts = true;
   private ContentHostingService contentHosting = null;
   private SecurityService securityService;
   private EventService eventService;
   private DefaultScaffoldingBean defaultScaffoldingBean;
   private WorkflowManager workflowManager;
   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;
   private GuidanceManager guidanceManager;
   private ReviewManager reviewManager;
   private StyleManager styleManager;
   private LinkManager linkManager = null;
   private TaggingManager taggingManager;
   public final static String FORM_TYPE = "form";

   private PreferencesService preferencesService = null;
   private EntityManager entityManager = null;
   private UserNotificationPreferencesRegistration matrixPreferencesConfig = null;
   private UserNotificationPreferencesRegistration wizardPreferencesConfig = null;

private static final String SCAFFOLDING_ID_TAG = "scaffoldingId";
   private EntityContextFinder contentFinder = null;
   private String importFolderName;
   private boolean useExperimentalMatrix = false;
   private boolean enableDafaultMatrixOptions = true;
   private static boolean allowAllGroups = 
      ServerConfigurationService.getBoolean(WizardMatrixConstants.PROP_GROUPS_ALLOW_ALL_GLOBAL, false);
      
   private static ResourceLoader messages = new ResourceLoader(
         "org.theospi.portfolio.matrix.bundle.Messages");

   /** This accepts email addresses */
   private static final Pattern emailPattern = Pattern.compile(
         "^" +
            "(?>" +
               "\\.?[a-zA-Z\\d!#$%&'*+\\-/=?^_`{|}~]+" +
            ")+" + 
         "@" + 
            "(" +
               "(" +
                  "(?!-)[a-zA-Z\\d\\-]+(?<!-)\\." +
               ")+" +
               "[a-zA-Z]{2,}" +
            "|" +
               "(?!\\.)" +
               "(" +
                  "\\.?" +
                  "(" +
                     "25[0-5]" +
                  "|" +
                     "2[0-4]\\d" +
                  "|" +
                     "[01]?\\d?\\d" +
                  ")" +
               "){4}" +
            ")" +
         "$"
         );
   
   public Scaffolding createDefaultScaffolding() {
      return getDefaultScaffoldingBean().createDefaultScaffolding();
   }

   public List getScaffolding() {
      return getHibernateTemplate().find("from Scaffolding");
   }
   

   /**
    *  {@inheritDoc}
    */
   public List findAvailableScaffolding(String siteIdStr, Agent user, boolean showUnpublished) {
	   Object[] params = new Object[]{getIdManager().getId(siteIdStr), user, Boolean.valueOf(true), Boolean.valueOf(true)};
	   Object[] params2 = new Object[]{getIdManager().getId(siteIdStr), user, Boolean.valueOf(true)};

	   //if showUnpublished, then you can see unpublisehd matrix's, otherwise you can
	   //only see you own matrix's or published ones
	   if(showUnpublished)
		   return getHibernateTemplate().find("from Scaffolding s where s.worksiteId=? " +
			   "and (s.owner=? or s.published=? or s.preview=?) ", params);
	   else
		   return getHibernateTemplate().find("from Scaffolding s where s.worksiteId=? " +
				   "and (s.owner=? or s.published=?)", params2);
   }
   
   /**
    *  {@inheritDoc}
    */
   public List findAvailableScaffolding(List sites, Agent user, boolean showUnpublished) {
      
      if ( sites == null || sites.size() == 0 )
         return new ArrayList();
      
      String[] paramNames = new String[] {"siteIds", "owner", "true"};
      Object[] params = new Object[]{sites, user, Boolean.valueOf(true)};
      
      //if showUnpublished, then you can see unpublisehd matrix's, otherwise you can
      //only see you own matrix's or published ones
      if(showUnpublished)
         return getHibernateTemplate().findByNamedParam("from Scaffolding s where s.worksiteId in ( :siteIds ) and ( s.owner = :owner or s.published=:true or s.preview=:true)",
              paramNames, params);
      else
         return getHibernateTemplate().findByNamedParam("from Scaffolding s where s.worksiteId in ( :siteIds ) and ( s.owner = :owner or s.published=:true)",
    	            paramNames, params);
   }
   
   /**
    * gathers all the published scaffolding from the given site (id)
    * @param siteId String
    * @return List of Scaffolding
    */
   public List<Scaffolding> findPublishedScaffolding(String siteId) {
      Object[] params = new Object[]{getIdManager().getId(siteId), Boolean.valueOf(true)};
      return getHibernateTemplate().find("from Scaffolding s where s.worksiteId=? " +
            "and s.published=?",
            params);
   }

   /**
    * 
    * @param sites A list of site Ids (Ids)
    * @return list of all published scaffolding within specified sites
    */
   public List findPublishedScaffolding(List sites) {
      String[] paramNames = new String[] {"siteIds", "published"};
      Object[] params = new Object[]{sites, Boolean.valueOf(true)};
      return getHibernateTemplate().findByNamedParam("from Scaffolding s where s.worksiteId in ( :siteIds ) " +
            "and s.published=:published",
            paramNames, params);
   }
   
   /**
    * Gets all the scaffolding for the data warehouse.  It preloads all the cells, levels, criterion.
    * It sets the back trace from the level and criterion back to the scaffolding and sets the sequence 
    * index number for ordering.  
    * @return List of Scaffolding
    */
   public List getScaffoldingForWarehousing() {
      List scaffolding = getHibernateTemplate().find("from Scaffolding");
      
      for(Iterator i = scaffolding.iterator(); i.hasNext(); ) {
         Scaffolding scaff = (Scaffolding)i.next();
         Set cells = scaff.getScaffoldingCells();
         
         //Load the evaluators for the cells as well.
         for(Iterator ii = cells.iterator(); ii.hasNext(); ) {
            ScaffoldingCell cell = (ScaffoldingCell)ii.next();
            
            cell.setEvaluators(this.getWizardPageFunctionUserList(cell.getWizardPageDefinition().getId(), true, MatrixFunctionConstants.EVALUATE_MATRIX));
         }
         
         List levels = scaff.getLevels();
         int n = 0;
         for(Iterator ii = levels.iterator(); ii.hasNext(); ) {
            Level level = (Level)ii.next();
            
            level.setSequenceNumber(n++);
            level.setScaffolding(scaff);
         }
         
         List criteria = scaff.getCriteria();
         criteria.size();
         n = 0;
         for(Iterator ii = criteria.iterator(); ii.hasNext(); ) {
            Criterion criterion = (Criterion)ii.next();
            
            criterion.setSequenceNumber(n++);
            criterion.setScaffolding(scaff);
         }
      }
      
      return scaffolding;
   }

   public List getMatrices(Id scaffoldingId) {
      List matrices = getHibernateTemplate().find(
            "from Matrix matrix where matrix.scaffolding.id = ?", new Object[]{scaffoldingId});

      return matrices;
   }

   public List getCellsByScaffoldingCell(Id scaffoldingCellId) {
      List list = getHibernateTemplate().find("from Cell cell where cell.scaffoldingCell.id=?", scaffoldingCellId);
      return list;
   }
   
   public List getPagesByPageDef(Id pageDefId) {
      List list = getHibernateTemplate().find("from WizardPage page where page.pageDefinition.id=?", pageDefId);
      return list;
   }
   
   public List getMatrices(Id scaffoldingId, Id agentId) {
      String query = "from Matrix matrix";
      Object[] params = new Object[]{};

      if (scaffoldingId == null && agentId == null) {
      } else if(scaffoldingId == null) {
         query += " where matrix.owner like ?";
         params = new Object[]{getAgentManager().getAgent(agentId)};
      } else if (agentId == null) {
         query += " where matrix.scaffolding.id like ?";
         params = new Object[]{scaffoldingId};
      } else {
         query += " where matrix.scaffolding.id like ? and matrix.owner like ?";
         params = new Object[]{scaffoldingId, getAgentManager().getAgent(agentId)};
      }

      //TODO move this into a callback

      List list = getHibernateTemplate().find(query, params);

      return list;
   }

   public Matrix getMatrix(Id scaffoldingId, Id agentId) {
      List list = getMatrices(scaffoldingId, agentId);

      if (list.size() > 0)
         return (Matrix) list.get(0);
      else
         return null;
   }
   
   public List getCells(Matrix matrix) {
      return getHibernateTemplate().find("from Cell cell where cell.matrix.id=?",
            matrix.getId());
      
   }

   public Cell getCell(Matrix matrix, Criterion rootCriterion, Level level) {
      //TODO should be something easier for this HQL
      
      Object[] params = new Object[]{matrix.getId(),
                                     rootCriterion.getId(), level.getId()};
      List list = getHibernateTemplate()
            .find("from Cell cell where cell.matrix.id=? and cell.scaffoldingCell.rootCriterion.id=? and cell.scaffoldingCell.level.id=?",
                  params);
      return (Cell) list.get(0);
   }

   public void unlockNextCell(Cell cell) {
      Matrix matrix = cell.getMatrix();
      List levels = matrix.getScaffolding().getLevels();
      int i = levels.indexOf(cell.getScaffoldingCell().getLevel());
      if (i < levels.size() - 1) {
         Level nextLevel = (Level) levels.get(i + 1);
         Cell nextCell = getCell(cell.getMatrix(), cell.getScaffoldingCell().getRootCriterion(), nextLevel);
         if (nextCell.getStatus().equals(MatrixFunctionConstants.LOCKED_STATUS)) {
            nextCell.setStatus(MatrixFunctionConstants.READY_STATUS);
            storeCell(nextCell);
         }
      }
   }
   
   public ScaffoldingCell getNextScaffoldingCell(ScaffoldingCell scaffoldingCell, 
         int progressionOption) {
      Scaffolding scaffolding = scaffoldingCell.getScaffolding();      
      ScaffoldingCell nextCell = null;
      
      if (progressionOption == Scaffolding.HORIZONTAL_PROGRESSION) {
         List columns = scaffolding.getLevels();
         int i = columns.indexOf(scaffoldingCell.getLevel());
         if (i < columns.size() - 1) {
            Level column = (Level)columns.get(i+1);
            nextCell = getScaffoldingCell(scaffoldingCell.getRootCriterion(), column);
         }
      }
      else if (progressionOption == Scaffolding.VERTICAL_PROGRESSION) {
         List rows = scaffolding.getCriteria();
         int i = rows.indexOf(scaffoldingCell.getRootCriterion());
         if (i < rows.size() - 1) {
            Criterion row = (Criterion)rows.get(i+1);
            nextCell = getScaffoldingCell(row, scaffoldingCell.getLevel());
         }
      }
      return nextCell;
   }
   
   public Cell getNextCell(Cell cell, int progressionOption) {
      ScaffoldingCell scaffoldingCell = cell.getScaffoldingCell();
      Scaffolding scaffolding = scaffoldingCell.getScaffolding();      
      Cell nextCell = null;
      
      if (progressionOption == Scaffolding.HORIZONTAL_PROGRESSION) {
         List columns = scaffolding.getLevels();
         int i = columns.indexOf(scaffoldingCell.getLevel());
         if (i < columns.size() - 1) {
            Level column = (Level)columns.get(i+1);
            nextCell = getCell(cell.getMatrix(), scaffoldingCell.getRootCriterion(), column);
         }
      }
      else if (progressionOption == Scaffolding.VERTICAL_PROGRESSION) {
         List rows = scaffolding.getCriteria();
         int i = rows.indexOf(scaffoldingCell.getRootCriterion());
         if (i < rows.size() - 1) {
            Criterion row = (Criterion)rows.get(i+1);
            nextCell = getCell(cell.getMatrix(), row, scaffoldingCell.getLevel());
         }
      }
      return nextCell;
   }
   
   protected Cell getMatrixCellByWizardPageDef(Matrix matrix, Id wizardPageDefId) {
      for (Iterator cells = matrix.getCells().iterator(); cells.hasNext();) {
         Cell cell = (Cell)cells.next();
         if (cell.getScaffoldingCell().getWizardPageDefinition()
               .getId().getValue().equals(wizardPageDefId.getValue())) {
            return cell;
         }
      }
      return null;
   }

   public Criterion getCriterion(Id criterionId) {
      return (Criterion) this.getHibernateTemplate().load(Criterion.class, criterionId);
   }

   public Level getLevel(Id levelId) {
      return (Level) this.getHibernateTemplate().load(Level.class, levelId);
   }
   
   public Cell getCell(Id cellId) {
      Cell cell = (Cell) this.getHibernateTemplate().get(Cell.class, cellId);
      return cell;
   }

   public Cell getCellFromPage(Id pageId) {
      List cells = getHibernateTemplate().find(
         "from Cell where wizard_page_id=?", new Object[]{pageId.getValue()});

      if (cells.size() > 0) {
         return (Cell)cells.get(0);
      }

      return null;
   }
   
   public Id storeCell(Cell cell) {
      this.getHibernateTemplate().saveOrUpdate(cell);
      return cell.getId();
   }

   public Id storePage(WizardPage page) {
      this.getHibernateTemplate().saveOrUpdate(page);
      eventService.postEvent(EventConstants.EVENT_FORM_ADD,page.getId().getValue());
      return page.getId();
   }

   public void publishScaffolding(Id scaffoldingId) {
      Scaffolding scaffolding = this.getScaffolding(scaffoldingId);
      scaffolding.setPreview(false);
      scaffolding.setPublished(true);
      scaffolding.setPublishedBy(authnManager.getAgent());
      scaffolding.setPublishedDate(new Date(System.currentTimeMillis()));
      scaffolding.setModifiedDate(new Date(System.currentTimeMillis()));
      this.storeScaffolding(scaffolding);
      eventService.postEvent(EventConstants.EVENT_SCAFFOLD_PUBLISH,scaffolding.getId().getValue());

   }
   public void previewScaffolding(Id scaffoldingId) {
      Scaffolding scaffolding = this.getScaffolding(scaffoldingId);
      //this variable is used for version control: if this is null when importing a matrix,
      //then the matrix is an older version and set all defaults to false
      scaffolding.setDefaultFormsMatrixVersion(true);
      scaffolding.setPreview(true);
      scaffolding.setModifiedDate(new Date(System.currentTimeMillis()));
      this.storeScaffolding(scaffolding);

   }
   public Scaffolding storeScaffolding(Scaffolding scaffolding) {
	  scaffolding.setModifiedDate(new Date(System.currentTimeMillis()));
      scaffolding = (Scaffolding)this.store(scaffolding);
      getHibernateTemplate().flush();
      eventService.postEvent(EventConstants.EVENT_SCAFFOLD_ADD_REVISE,scaffolding.getId().getValue());
      generateScaffoldingRealm(scaffolding);
      return scaffolding;
   }
   public Scaffolding saveNewScaffolding(Scaffolding scaffolding) {
	  scaffolding.setModifiedDate(new Date(System.currentTimeMillis()));
      Id id = (Id)this.save(scaffolding);
      getHibernateTemplate().flush();
      scaffolding = getScaffolding(id);
      generateScaffoldingRealm(scaffolding);
      return scaffolding;
   }
   
   /**
    * Make sure that a realm with default perms is created when the scaffolding is 
    * saved.  Otherwise, you have to enter the perms screen first.
    * @param scaffolding
    */
   private void generateScaffoldingRealm(Scaffolding scaffolding) {
	   AuthzGroup templateAzg = null;
	   AuthzGroup azg = null;
	   Site site = null;
	   try {
		   String realmTemplate = "!matrix.template.";
		   site = SiteService.getSite(scaffolding.getWorksiteId().getValue());
		   templateAzg = AuthzGroupService.getInstance().getAuthzGroup(realmTemplate + site.getType());
	   }
	   catch (GroupNotDefinedException gnde) {
		   logger.warn("group with id: " + gnde.getId() + " not defined", gnde);
	   } catch (IdUnusedException iue) {
		   logger.warn("id: " + iue.getId() + " not found", iue);
	   }
	   try {
		   if (templateAzg != null) {
			   azg = AuthzGroupService.getInstance().addAuthzGroup(
					   scaffolding.getReference(), templateAzg, null);
			   if (site != null && azg != null) {
				   purgeBadRoles(azg, site);
				   try {
					   AuthzGroupService.save(azg);
				   } catch (GroupNotDefinedException e1) {
					   logger.warn("azg no defined", e1);
					   //shouldn't need to do anything since it was just created
				   }
			   }
		   }
	   } catch (GroupIdInvalidException giie) {
		   logger.warn("group id invalid", giie);
	   } catch (GroupAlreadyDefinedException gade) {
		   logger.warn("group already defined");
		   //this should be pretty common, so don't really need the full stack trace here
	   } catch (AuthzPermissionException ape) {
		   logger.warn("Permission exception", ape);
	   } 
   }
   
   private void purgeBadRoles(AuthzGroup azg, Site site) {
	   Set<Role> activeRoles = site.getRoles();
	   Set<Role> azgRoles = azg.getRoles();
	   for (Role role: azgRoles) {
		   if (!activeRoles.contains(role))
			   azg.removeRole(role.getId());
	   }
	   
   }
   
   public Id storeScaffoldingCell(ScaffoldingCell scaffoldingCell) {
	  scaffoldingCell.getScaffolding().setModifiedDate(new Date(System.currentTimeMillis()));	 
      scaffoldingCell = (ScaffoldingCell)store(scaffoldingCell);
      return scaffoldingCell.getId();
   }

   public Object store(Object obj) {
      obj = this.getHibernateTemplate().merge(obj);
      return obj;
   }

   public Object save(Object obj) {
      obj = this.getHibernateTemplate().save(obj);
      return obj;
   }

   public Matrix createMatrix(Agent owner, Scaffolding scaffolding) {
      Matrix matrix = new Matrix();
      matrix.setOwner(owner);
      matrix.setScaffolding(scaffolding);

      List levels = scaffolding.getLevels();
      List criteria = scaffolding.getCriteria();

      Criterion criterion = null;
      Level level = null;

      for (Iterator criteriaIterator = criteria.iterator(); criteriaIterator.hasNext();) {
         criterion = (Criterion) criteriaIterator.next();

         for (Iterator levelsIterator = levels.iterator(); levelsIterator.hasNext();) {
            level = (Level) levelsIterator.next();

            Cell cell = new Cell();
            cell.getWizardPage().setOwner(owner);
            ScaffoldingCell sCell = getScaffoldingCell(criterion, level);
            cell.setScaffoldingCell(sCell);
            if (sCell != null) {
               String status = sCell.getInitialStatus();
               cell.setStatus(status);
            }
            else
               cell.setStatus(MatrixFunctionConstants.LOCKED_STATUS);

            matrix.add(cell);
         }
      }

      this.getHibernateTemplate().save(matrix);
      return matrix;
   }

   public Attachment attachArtifact(Id pageId, Reference artifactRef) {
      Id artifactId = convertRef(artifactRef);
      detachArtifact(pageId, artifactId);
      WizardPage page = getWizardPage(pageId);
      Attachment attachment = new Attachment();
      attachment.setArtifactId(artifactId);
      attachment.setWizardPage(page);
      attachment.setNewId(getIdManager().createId());
      
      page.getAttachments().add(attachment);

      this.getHibernateTemplate().saveOrUpdate(page);
      return attachment;
   }

   public WizardPage getWizardPage(Id pageId) {
      WizardPage page = (WizardPage) this.getHibernateTemplate().get(WizardPage.class, pageId);
      
      // check for invalid page (in case wizard/matrix is deleted)
      // might also be looking up a scaffolding, so removing the logging of the warning
      if ( page == null )
      {
         //logger.warn("Invalid wizard or matrix page: " + pageId.toString() );
         return null;
      }
      
      page.getAttachments().size();
      page.getPageForms().size();

      removeFromSession(page);
      return page;
   }
   
   public WizardPage getWizardPageByPageDefAndOwner(Id pageId, Agent owner) {
      Object[] params = new Object[]{pageId, owner};
      List pageList = getHibernateTemplate().find("from WizardPage w where w.pageDefinition.id=? and w.owner=?", params);
      
      // check for invalid page (in case wizard/matrix is deleted)
      if ( pageList == null || pageList.size() < 1 )
      {
         logger.warn("Invalid wizard or matrix page: " + pageId.toString() );
         return null;
      }
      
      WizardPage page = (WizardPage)pageList.get(0); 
      page.getAttachments().size();
      page.getPageForms().size();

      removeFromSession(page);
      return page;
   }
   
   protected List getWizardPages() {
      return this.getHibernateTemplate().find("from WizardPage");
   }

   protected Id convertRef(Reference artifactRef) {
      String uuid = getContentHosting().getUuid(artifactRef.getId());
      return getIdManager().getId(uuid);
   }

   public void detachArtifact(final Id pageId, final Id artifactId) {

      HibernateCallback callback = new HibernateCallback() {
         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            WizardPage page = (WizardPage) session.load(WizardPage.class, pageId);
            Set attachments = page.getAttachments();
            Iterator iter = attachments.iterator();
            List toRemove = new ArrayList();
            while (iter.hasNext()) {
               Attachment a = (Attachment) iter.next();
               if (a.getArtifactId()==null || artifactId.equals(a.getArtifactId())) {
                  toRemove.add(a);
               }
            }
            attachments.removeAll(toRemove);
            page.setAttachments(attachments);

            session.saveOrUpdate(page);
            return null;
         }

      };

      getHibernateTemplate().execute(callback);

   }
   
   public void detachForm(final Id pageId, final Id artifactId) {

      HibernateCallback callback = new HibernateCallback() {
         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            WizardPage page = (WizardPage) session.load(WizardPage.class, pageId);
            Set forms = page.getPageForms();
            
            Iterator iter = forms.iterator();
            List toRemove = new ArrayList();
            while (iter.hasNext()) {
               WizardPageForm wpf = (WizardPageForm) iter.next();
               if (wpf.getArtifactId()==null || artifactId.equals(wpf.getArtifactId())) {
                  toRemove.add(wpf);
               }
            }
            forms.removeAll(toRemove);
            page.setPageForms(forms);
            
            session.saveOrUpdate(page);  
            eventService.postEvent(EventConstants.EVENT_FORM_DELETE, pageId.getValue());
            return null;
         }

      };

      getHibernateTemplate().execute(callback);

   }
   
   public Matrix getMatrixByPage(Id pageId) {
      Matrix matrix = null;
      Object[] params = new Object[]{pageId};
      
      List list = this.getHibernateTemplate().find("select cell.matrix from " +
            "Cell cell where cell.wizardPage.id=? ", params);
      if (list.size() == 1) {
         matrix = (Matrix) list.get(0);
      }
         
      return matrix;
   }

   public Matrix getMatrix(Id matrixId) {
      return (Matrix) this.getHibernateTemplate().get(Matrix.class, matrixId);
   }
   
   public List getMatricesForWarehousing() {
      
      List matrices = getMatrices(null, null);

        
        for(Iterator ii = matrices.iterator(); ii.hasNext(); ) {
           Matrix mat = (Matrix)ii.next();
           
           mat.getId();
           //mat.setMatrixTool(tool);
           
           mat.getCells().size();
           
           for(Iterator iii= mat.getCells().iterator(); iii.hasNext(); ) {
              Cell cell = (Cell)iii.next();

              cell.getWizardPage().getPageForms().size();
              cell.getWizardPage().getAttachments().size();
           }
           
           getHibernateTemplate().evict(mat);
        }
      return matrices;
   }
   
   public List getWizardPagesForWarehousing() {
      
      List wizardPages = this.getWizardPages();

        
        for(Iterator ii = wizardPages.iterator(); ii.hasNext(); ) {
           WizardPage wizardPage = (WizardPage)ii.next();
           
           wizardPage.getId();
           wizardPage.getPageForms().size();
           wizardPage.getAttachments().size();
           
           getHibernateTemplate().evict(wizardPage);
        }
      return wizardPages;
   }

   public Scaffolding getScaffolding(Id scaffoldingId) {
      return (Scaffolding) this.getHibernateTemplate().get(Scaffolding.class, scaffoldingId);
      //return getScaffolding(scaffoldingId, false);
   }
   
   public Scaffolding loadScaffolding(Id scaffoldingId) {
	   Scaffolding scaffolding = (Scaffolding) this.getHibernateTemplate().load(Scaffolding.class, scaffoldingId);
	   scaffolding.getLevels().size();
	   scaffolding.getCriteria().size();
	   return scaffolding;
   }

   protected Scaffolding getScaffoldingForExport(Id scaffoldingId) {
      Scaffolding scaffolding = (Scaffolding) this.getHibernateTemplate().get(Scaffolding.class, scaffoldingId);

      
      //scaffolding evaluators:
      Collection reviewers = this.getWizardPageFunctionUserList(scaffolding.getId(), false, MatrixFunctionConstants.REVIEW_MATRIX);
      scaffolding.setReviewers(new HashSet(reviewers));      
      
      //scaffolding cells evaluators:
      for (Iterator iter = scaffolding.getScaffoldingCells().iterator(); iter.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell) iter.next();
         reviewers = this.getWizardPageFunctionUserList(sCell.getWizardPageDefinition().getId(), false, MatrixFunctionConstants.REVIEW_MATRIX);
         sCell.setReviewers(new HashSet(reviewers));
      } 
      
      //scaffolding evaluators:
      Collection evaluators = this.getWizardPageFunctionUserList(scaffolding.getId(), false, MatrixFunctionConstants.EVALUATE_MATRIX);
      scaffolding.setEvaluators(new HashSet(evaluators));      
      
      //scaffolding cells evaluators:
      for (Iterator iter = scaffolding.getScaffoldingCells().iterator(); iter.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell) iter.next();
         evaluators = this.getWizardPageFunctionUserList(sCell.getWizardPageDefinition().getId(), false, MatrixFunctionConstants.EVALUATE_MATRIX);
         sCell.setEvaluators(new HashSet(evaluators));
      }      
      
      
      

      return scaffolding;
   }
   
   protected List getScaffoldingByStyle(Id styleId) {
      Object[] params = new Object[]{styleId};
      return getHibernateTemplate().find("from Scaffolding s where s.style.id=? " , 
               params);
      
   }
   
   protected List getWizardPageDefByStyle(Id styleId) {
      Object[] params = new Object[]{styleId};
      return getHibernateTemplate().find("from WizardPageDefinition wpd where wpd.style.id=? " , 
               params);
      
   }
   
   public ScaffoldingCell getScaffoldingCell(Criterion criterion, Level level) {
      ScaffoldingCell scaffoldingCell = null;
      Object[] params = new Object[]{criterion.getId(), 
            level.getId()};
      
      List list = this.getHibernateTemplate().find("from " +
            "ScaffoldingCell scaffoldingCell where scaffoldingCell.rootCriterion.id=? " +
            "and scaffoldingCell.level.id=?", params);
      if (list.size() == 1) {
         scaffoldingCell = (ScaffoldingCell) list.get(0);
      }
         
      return scaffoldingCell;
   }
   
   public String getScaffoldingCellsStatus(Id scaffoldingCellId) {
      ScaffoldingCell scaffoldingCell = null;
      String result = "";
      Criteria c = this.getSession().createCriteria(ScaffoldingCell.class);
      try {
         c.add(Expression.eq("id", scaffoldingCellId));
      
         scaffoldingCell = (ScaffoldingCell)c.uniqueResult();
         result = scaffoldingCell.getInitialStatus();
         this.removeFromSession(scaffoldingCell);
      } catch (HibernateException e) {
         logger.error("Error returning scaffoldingCell with id: " + scaffoldingCellId);
         return null;
      }
      return result;
   }
   
   public ScaffoldingCell getScaffoldingCell(Id id) {
      ScaffoldingCell scaffoldingCell = (ScaffoldingCell)this.getHibernateTemplate().load(ScaffoldingCell.class, id);
      
      scaffoldingCell.setEvaluators(getWizardPageFunctionUserList(scaffoldingCell.getWizardPageDefinition().getId(), true, MatrixFunctionConstants.EVALUATE_MATRIX));

      return scaffoldingCell;
   }
   
   //FIXME: These queries should be externalized and possibly promoted to the API 
   public int getFormCountByPageDef(Id pageDefId) {
	   Object[] params = new Object[] { pageDefId };
	   return (Integer) getHibernateTemplate().find(
			   "select count(*) from WizardPage wp join wp.pageForms where wp.pageDefinition.id=?", params).get(0);	   
   }
   
   //FIXME: This should be in the ReviewManager, but is special-cased here for now
   //NOTE: This is a theta-join because Review.parent and WizardPage.id are not mapped
   public int getReviewCountByPageDef(Id pageDefId) {
	   Object[] params = new Object[] { pageDefId };
	   return (Integer) getHibernateTemplate().find(
			   "select count(*) from Review r, WizardPage wp where wp.id = r.parent and wp.pageDefinition.id=?", params).get(0);
   }
   
   public int getAttachmentCountByPageDef(Id pageDefId) {
	   Object[] params = new Object[] { pageDefId };
	   return (Integer) getHibernateTemplate().find(
			   "select count(*) from WizardPage wp join wp.attachments where wp.pageDefinition.id=?", params).get(0);
   }
   
   public boolean isScaffoldingCellUsed(ScaffoldingCell cell) {
	   Id pageDefId = cell.getWizardPageDefinition().getId();
	   
	   return  getFormCountByPageDef(pageDefId) > 0
	   		|| getAttachmentCountByPageDef(pageDefId) > 0
	   		|| getReviewCountByPageDef(pageDefId) > 0;
   }
   
   /**
    * {@inheritDoc}
    */
   public Set<ScaffoldingCell> getScaffoldingCells(Id scaffoldingId) {
      Object[] params = new Object[]{scaffoldingId};
      
      List list = this.getHibernateTemplate().find("from " +
            "ScaffoldingCell scaffoldingCell where scaffoldingCell.scaffolding.id=?", 
            params);
      return new HashSet<ScaffoldingCell>(list);
   }
   
   public ScaffoldingCell getScaffoldingCellByWizardPageDef(Id id) {
      ScaffoldingCell scaffoldingCell = null;
      Object[] params = new Object[]{id};
      
      List list = this.getHibernateTemplate().find("from " +
            "ScaffoldingCell scaffoldingCell where scaffoldingCell.wizardPageDefinition.id=?", 
            params);
      if (list.size() == 1) {
         scaffoldingCell = (ScaffoldingCell) list.get(0);
      }
         
      return scaffoldingCell;
   }

   
   protected Collection getWizardPageFunctionUserList(Id wizardPageDefId, boolean useAgentId, String function) {
      Collection evaluators = new HashSet();
      Collection viewerAuthzs = getAuthzManager().getAuthorizations(null,
    		  function, wizardPageDefId);

      for (Iterator i = viewerAuthzs.iterator(); i.hasNext();) {
         Authorization evaluator = (Authorization) i.next();
         if (useAgentId)
            evaluators.add(evaluator.getAgent());
         else
            evaluators.add(evaluator.getAgent().getId());
      }
      return evaluators;
   }
   
   public void removeFromSession(Object obj) {
      this.getHibernateTemplate().evict(obj);
   }
   
   public void clearSession() {
      this.getHibernateTemplate().clear();
   }
   
   private Scaffolding getScaffoldingByArtifact(Id artifactId) {

      List list = this.getHibernateTemplate().find("from " +
            "Scaffolding scaffolding where scaffolding.artifactId=?", 
            artifactId.getValue());
      if (list == null) return null;
      if (list.size() == 1) return (Scaffolding)list.get(0);
      return null;      
   }

   List getCellAttachments(Id cellId) {
      return this.getHibernateTemplate().find("from Attachment attachment where attachment.cell=?", cellId.getValue());
   }

   public Attachment getAttachment(Id attachmentId) {
      return (Attachment) this.getHibernateTemplate().load(Attachment.class, attachmentId);
   }
   
   public Set getPageForms(WizardPage page) {
      Set result = new HashSet();
      Set removes = new HashSet();
      if (page != null && page.getPageForms() != null) {
         for (Iterator iter = page.getPageForms().iterator(); iter.hasNext();) {
            WizardPageForm wpf = (WizardPageForm) iter.next();
            Node node = getNode(wpf.getArtifactId(), page, true);
            if (node != null) {
               result.add(node);
            }
            else if ( !isNodeHidden(wpf.getArtifactId()) ) {
               removes.add(wpf.getArtifactId());
            }
         }
         for (Iterator iter2 = removes.iterator(); iter2.hasNext();) {
            Id id = (Id) iter2.next();
            logger.warn("Cell contains stale form references (null node encountered) for Cell: " + page.getId().getValue() + ". Detaching");
            detachForm(page.getId(), id);
         }
      }
      return result;
   }
   
   public Set getPageContents(WizardPage page) {
      Set result = new HashSet();
      Set removes = new HashSet();
      if (page != null && page.getAttachments() != null) {
         for (Iterator iter = page.getAttachments().iterator(); iter.hasNext();) {
            Attachment attachment = (Attachment) iter.next();
            Node node = getNode(attachment.getArtifactId(), page, false);
            if (node != null) {
               result.add(node);
            }
            else if ( !isNodeHidden(attachment.getArtifactId()) ) {
               removes.add(attachment.getArtifactId());
            }
         }
         for (Iterator iter2 = removes.iterator(); iter2.hasNext();) {
            Id id = (Id) iter2.next();
            logger.warn("Cell contains stale artifact references (null node encountered) for Cell: " + page.getId().getValue() + ". Detaching");
            detachArtifact(page.getId(), id);
         }
      }
      return result;
   }

   /**
    * 
    * @param artifactId
    * @param page
    * @param isForm Flag indication that the artifact in question is a form or not
    * @return
    */
   protected Node getNode(Id artifactId, WizardPage page, boolean isForm) {
      Node node = getNode(artifactId);
      if (node == null) {
         return null;
      }
      String siteId = page.getPageDefinition().getSiteId();
      ContentResource wrapped = new ContentEntityWrapper(node.getResource(),
            buildRef(siteId, page.getId().getValue(), node.getResource()));

      if (!isForm)
    	  return new Node(artifactId, wrapped, node.getTechnicalMetadata().getOwner(), node.getIsLocked());
      else 
    	  return new Node(artifactId, wrapped, node.getTechnicalMetadata().getOwner(), node.getIsLocked(), 
    			  buildRefDecorator(siteId, page.getId().getValue(), MatrixContentEntityProducer.MATRIX_PRODUCER) + 
    			  buildRefDecorator(siteId, artifactId.getValue(), MetaobjEntityManager.METAOBJ_CONTENT_ENTITY_PREFIX));
   }

   private boolean isNodeHidden( Id artifactId ) {
      try {
         String id = getContentHosting().resolveUuid(artifactId.getValue());
         if ( id == null )
            return false; // non-existant node is not "hidden"
         getContentHosting().checkResource(id);
      }
      catch (PermissionException e) {
         return true; // not permitted to view indicates "hidden"
      }
      catch (Exception e) {
         return false;  // any other error does not constitute "hidden"
      }
      return false;
   }
   
   public Node getNode(Id artifactId) {
      return getNode(artifactId, true);
   }

   public Node getNode(Id artifactId, boolean checkLocks) {
      String id = getContentHosting().resolveUuid(artifactId.getValue());
      if (id == null) {
         return null;
      }

      getSecurityService().pushAdvisor(
         new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ,
               getContentHosting().getReference(id)));

      try {
         ContentResource resource = getContentHosting().getResource(id);
         String ownerId = resource.getProperties().getProperty(resource.getProperties().getNamePropCreator());
         Agent owner = getAgentFromId(getIdManager().getId(ownerId));
         boolean locked = checkLocks && getLockManager().isLocked(artifactId.getValue());

         return new Node(artifactId, resource, owner, locked);
      }
      catch (PermissionException e) {
         logger.warn(this+".getNode "+e.toString());
         return null;
      }
      catch (Exception e) {
         logger.error(this+".getNode "+e.toString());
         return null;
      }
   }

   public Node getNode(Reference ref) {
      return getNode(ref, true);
   }

   public Node getNode(Reference ref, boolean checkLocks) {
      String nodeId = getContentHosting().getUuid(ref.getId());

      return getNode(getIdManager().getId(nodeId), checkLocks);
   }

   public List getCellsByArtifact(Id artifactId) {
      Criteria c = null;
      try {
         c = this.getSession().createCriteria(Cell.class);
         c.setFetchMode("scaffoldingCell", FetchMode.JOIN);
         c.setFetchMode("scaffoldingCell.scaffolding", FetchMode.JOIN);
         Criteria att = c.createCriteria("attachments");
         att.add(Expression.eq("artifactId", artifactId));
         
         return new ArrayList(c.list());
      } catch (DataAccessResourceFailureException e) {
         logger.error("", e);
      } catch (HibernateException e) {
         logger.error("", e);
      } catch (IllegalStateException e) {
         logger.error("", e);
      }
      return new ArrayList();
   }

   public List getCellsByForm(Id formId) {
      Criteria c = null;
      try {
         c = this.getSession().createCriteria(Cell.class);
         c.setFetchMode("scaffoldingCell", FetchMode.JOIN);
         c.setFetchMode("scaffoldingCell.scaffolding", FetchMode.JOIN);
         Criteria att = c.createCriteria("pageForms");
         att.add(Expression.eq("artifactId", formId));
         
         return new ArrayList(c.list());
      } catch (DataAccessResourceFailureException e) {
         logger.error("", e);
      } catch (HibernateException e) {
         logger.error("", e);
      } catch (IllegalStateException e) {
         logger.error("", e);
      }
      return new ArrayList();
   }

   protected Agent getAgentFromId(Id agentId) {
      return agentManager.getAgent(agentId);
   }

   /**
    * @return Returns the idManager.
    */
   public IdManager getIdManager() {
      return idManager;
   }

   /**
    * @param idManager The idManager to set.
    */
   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }



   /**
    * @return Returns the authManager.
    */
   public AuthenticationManager getAuthnManager() {
      return authnManager;
   }

   /**
    * @param authnManager The authnManager to set.
    */
   public void setAuthnManager(AuthenticationManager authnManager) {
      this.authnManager = authnManager;
   }

   /**
    * @return Returns the authzManager.
    */
   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   /**
    * @param authzManager The authzManager to set.
    */
   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   /**
    * @return Returns the agentManager.
    */
   public AgentManager getAgentManager() {
      return agentManager;
   }

   /**
    * @param agentManager The agentManager to set.
    */
   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   /* (non-Javadoc)
    * @see org.theospi.portfolio.matrix.model.hibernate.impl.MatrixManager#deleteMatrix(org.theospi.portfolio.shared.model.Id)
    */
   public void deleteMatrix(Id matrixId) {
      Matrix matrix = getMatrix( matrixId );
      Set cells = matrix.getCells();
       
      // first unlock all resources associated with this matrix
      for (Iterator cellIt=cells.iterator(); cellIt.hasNext();) 
      {
         Cell cell = (Cell)cellIt.next();
         WizardPage page = cell.getWizardPage();
         
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
               MatrixContentEntityProducer.MATRIX_PRODUCER);
         for (Iterator iter = reviews.iterator(); iter.hasNext();) {
            Review review = (Review)iter.next();
            getLockManager().removeLock(review.getReviewContent().getValue(), 
                     page.getId().getValue());
         } 
      }
      
      // Now delete matrix
      this.getHibernateTemplate().delete(getMatrix(matrixId));
   }

   public void deleteScaffolding(Id scaffoldingId) {
	   Scaffolding scaffolding = getScaffolding(scaffoldingId);
	   String scaffoldingRef = scaffolding.getReference();
	   
      this.getHibernateTemplate().delete(scaffolding);
      eventService.postEvent(EventConstants.EVENT_SCAFFOLD_DELETE,scaffoldingId.getValue());
      
    //remove azg
	   try {
		AuthzGroupService.removeAuthzGroup(scaffoldingRef);
	} catch (AuthzPermissionException e) {
		logger.warn("could not remove azg for scaffolding", e);
	}
   }
   
   public Cell submitCellForEvaluation(Cell cell) {
      Date now = new Date(System.currentTimeMillis());
      getHibernateTemplate().refresh(cell); //TODO not sure if this is necessary
      ScaffoldingCell sCell = cell.getScaffoldingCell();
      WizardPage page = cell.getWizardPage();
      
      //    Actions for current cell
      processContentLockingWorkflow(true, page);
      processStatusChangeWorkflow(MatrixFunctionConstants.PENDING_STATUS, page);
      cell.getWizardPage().setModified(now);
      
      if (sCell.getScaffolding().getWorkflowOption() > 0)
         processWorkflow(sCell.getScaffolding().getWorkflowOption(), cell.getId());

      return cell;
   }

   public WizardPage submitPageForEvaluation(WizardPage page) {
      Date now = new Date(System.currentTimeMillis());
      
      WizardPage thePage = getWizardPage(page.getId());
      getHibernateTemplate().refresh(thePage); //TODO not sure if this is necessary

      processContentLockingWorkflow(true, thePage);
      
      thePage.setStatus(MatrixFunctionConstants.PENDING_STATUS);
      thePage.setModified(now);
      getHibernateTemplate().merge(thePage);
      
      return page;
   }
   
   public List getEvaluatableCells(Agent agent, List<Agent> roles, List<String> worksiteIds, Map siteHash) {
      String[] paramNames;
      
      boolean rolesNotEmpty = (roles != null && roles.size() > 0);
      boolean sitesNotEmpty = (worksiteIds != null && worksiteIds.size() > 0);
      Object[] params;
      if(rolesNotEmpty && sitesNotEmpty){
    	  paramNames = new String[] {"false", "true", "evaluate", "pendingStatus", "user", "roles", "siteIds"};
    	  params =  new Object[]{Boolean.valueOf(false), Boolean.valueOf(true),
    		  						  MatrixFunctionConstants.EVALUATE_MATRIX,
                                      MatrixFunctionConstants.PENDING_STATUS,
                                      agent, roles, worksiteIds};
      }else{
    	  if(!rolesNotEmpty){
    		  if(!sitesNotEmpty){
    			  paramNames = new String[] {"false", "true", "evaluate", "pendingStatus", "user"};
    			  params =  new Object[]{Boolean.valueOf(false), Boolean.valueOf(true),
  						  MatrixFunctionConstants.EVALUATE_MATRIX,
                          MatrixFunctionConstants.PENDING_STATUS,
                          agent};
    		  }else{
    			  paramNames = new String[] {"false", "true", "evaluate", "pendingStatus", "user", "siteIds"};
    			  params =  new Object[]{Boolean.valueOf(false), Boolean.valueOf(true),
  						  MatrixFunctionConstants.EVALUATE_MATRIX,
                          MatrixFunctionConstants.PENDING_STATUS,
                          agent, worksiteIds};
    		  }
    	  }else{
    		  paramNames = new String[] {"false", "true", "evaluate", "pendingStatus", "user", "roles"};
    		  params =  new Object[]{Boolean.valueOf(false), Boolean.valueOf(true),
						  MatrixFunctionConstants.EVALUATE_MATRIX,
                      MatrixFunctionConstants.PENDING_STATUS,
                      agent, roles};
    	  }
      }

      String evaluatableQuery = "select distinct new " +
      "org.theospi.portfolio.matrix.model.EvaluationContentWrapperForMatrixCell(" +
      "wp.id, " +
      "wp.pageDefinition.title, c.matrix.owner, " +
      "c.wizardPage.modified, wp.pageDefinition.siteId) " +
      "from WizardPage wp, Authorization auth, Cell c " +
      "where ((wp.pageDefinition.id = auth.qualifier and wp.pageDefinition.defaultEvaluators=:false) or " +
      "(c.scaffoldingCell.scaffolding.id = auth.qualifier and wp.pageDefinition.defaultEvaluators=:true))" +
      "and wp.id = c.wizardPage.id " +
      "and auth.function = :evaluate and wp.status = :pendingStatus and " +
      "(auth.agent=:user";      		
      if(rolesNotEmpty)
    	  evaluatableQuery += " or auth.agent in ( :roles )";
      evaluatableQuery += ") ";
      if(sitesNotEmpty)
    	  evaluatableQuery += "and wp.pageDefinition.siteId in ( :siteIds )";

      List matrixCells = this.getHibernateTemplate().findByNamedParam(evaluatableQuery, 
    		  paramNames, params );

      // filter out group-restricted users
      
      List filteredMatrixCells = new ArrayList();
      for ( Iterator it=matrixCells.iterator(); it.hasNext(); ) {
         EvaluationContentWrapper evalItem = (EvaluationContentWrapper)it.next();
         WizardPage wizPage = getWizardPage( evalItem.getId() );
         Scaffolding scaffolding = 
            getScaffoldingCellByWizardPageDef( wizPage.getPageDefinition().getId() ).getScaffolding();
         boolean scaffoldCanViewAllGroups = getAuthzManager().isAuthorized(MatrixFunctionConstants.VIEW_ALL_GROUPS, getIdManager().getId(scaffolding.getReference()));
       //if the current user doesn't have Access User List permission for the specific matrix, then
         //this means they must not be able to see the owner for blind evaluation
         if(!getAuthzManager().isAuthorized(MatrixFunctionConstants.ACCESS_USERLIST, getIdManager().getId(scaffolding.getReference()))) {
        	 evalItem.setHideOwnerDisplay(true);
         }

         if ( !allowAllGroups && !scaffoldCanViewAllGroups ) {
            HashSet siteGroupUsers = (HashSet)siteHash.get( scaffolding.getWorksiteId().getValue() );
            if ( siteGroupUsers != null && siteGroupUsers.contains(wizPage.getOwner().getId().getValue()) )
               filteredMatrixCells.add( evalItem );
         }
         else {
            filteredMatrixCells.add( evalItem );
         }
         
      }
      
      
      
      return filteredMatrixCells;
   }
   
   /**
    *  {@inheritDoc}
    */
   /*
   public List getEvaluatableItems(Agent agent, Id worksiteId) {
      List roles = agent.getWorksiteRoles(worksiteId.getValue());
      Agent role = null;
      if (roles.size() > 0)
    	  role= (Agent)roles.get(0);

      List returned = getEvaluatableCells(agent, roles, worksiteId);
      List wizardPages = getEvaluatableWizardPages(agent, role, worksiteId);
      List wizards = getEvaluatableWizards(agent, role, worksiteId);

      returned.addAll(wizardPages);
      returned.addAll(wizards);

      return returned;
   }
   */

   public void packageScffoldingForExport(Id scaffoldingId, OutputStream os) throws IOException {
      Scaffolding oldScaffolding = this.getScaffoldingForExport(scaffoldingId); 
      

	   CheckedOutputStream checksum = new CheckedOutputStream(os, new Adler32());
	   ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(checksum));

	   List levels = oldScaffolding.getLevels();
	   List criteria = oldScaffolding.getCriteria();
	   Set scaffoldingCells = oldScaffolding.getScaffoldingCells();
	   List guidanceIds = new ArrayList();
	   Set styleIds = new HashSet();
	   List formIds = new ArrayList();      
	   Collection scaffEvaluators = oldScaffolding.getEvaluators();
	   Collection scaffReviewers = oldScaffolding.getReviewers();
	   List scaffAttachments = oldScaffolding.getAttachments();

	   levels.size();
	   criteria.size();
	   scaffoldingCells.size();
	   for (Iterator iter = scaffoldingCells.iterator(); iter.hasNext();) {
		   ScaffoldingCell sCell = (ScaffoldingCell)iter.next();
		   Collection evalWorkflows = sCell.getWizardPageDefinition().getEvalWorkflows();
		   for (Iterator iter2 = evalWorkflows.iterator(); iter2.hasNext();) {
			   Workflow wf = (Workflow)iter2.next();
			   wf.getItems().size();
		   }
	   }
	   //initialize scaffolding workflow into session
	   Collection scaffoldingEvalWorkflows = oldScaffolding.getEvalWorkflows();
	   for (Iterator iter2 = scaffoldingEvalWorkflows.iterator(); iter2.hasNext();) {
		   Workflow wf = (Workflow)iter2.next();
		   wf.getItems().size();
	   }

	   removeFromSession(oldScaffolding);

	   if (oldScaffolding.getStyle() != null) {
		   styleIds.add(oldScaffolding.getStyle().getId().getValue());
	   }



	   Collection formsScaffolding = oldScaffolding.getAdditionalForms();
	   oldScaffolding.setAdditionalForms(new ArrayList(formsScaffolding));

	   for (Iterator iter = scaffoldingCells.iterator(); iter.hasNext();) {
		   ScaffoldingCell sCell = (ScaffoldingCell)iter.next();
		   sCell.setCells(new HashSet());
		   Collection evaluators = sCell.getEvaluators();
		   sCell.setEvaluators(new HashSet(evaluators));

		   Collection reviewers = sCell.getReviewers();
		   sCell.setReviewers(new HashSet(reviewers));

		   List attachments = sCell.getWizardPageDefinition().getAttachments();
		   sCell.getWizardPageDefinition().setAttachments( new ArrayList(attachments) );

		   sCell.getWizardPageDefinition().setPages(new HashSet());

		   Collection forms = sCell.getWizardPageDefinition().getAdditionalForms();
		   sCell.getWizardPageDefinition().setAdditionalForms(new ArrayList(forms));

		   Collection evalWorkflows = sCell.getWizardPageDefinition().getEvalWorkflows();
		   for (Iterator iter2 = evalWorkflows.iterator(); iter2.hasNext();) {
			   Workflow wf = (Workflow)iter2.next();
			   Collection items = wf.getItems();
			   wf.setItems(new HashSet(items));
		   }
		   sCell.getWizardPageDefinition().setEvalWorkflows(new HashSet(evalWorkflows));
		   exportWorkflowForms(zos, sCell.getWizardPageDefinition(), sCell.getAdditionalForms(), formIds);

		   if (sCell.getGuidance() != null) {
			   guidanceIds.add(sCell.getGuidance().getId().getValue());
		   }
		   if (sCell.getWizardPageDefinition().getStyle() != null) {
			   styleIds.add(sCell.getWizardPageDefinition().getStyle().getId().getValue());
		   }
	   }

	   if (guidanceIds.size() > 0) {
		   exportGuidance(zos, guidanceIds);
	   }

	   if (styleIds.size() > 0) {
		   exportStyle(zos, styleIds);
	   }


	   //workflow
	   Collection scaffEvalWorkflows = oldScaffolding.getEvalWorkflows();
	   for (Iterator iter2 = scaffEvalWorkflows.iterator(); iter2.hasNext();) {
		   Workflow wf = (Workflow)iter2.next();
		   Collection items = wf.getItems();
		   wf.setItems(new HashSet(items));
	   }

	   //scaffolding variables:
	   exportWorkflowForms(zos, oldScaffolding, oldScaffolding.getAdditionalForms(), formIds);

	   oldScaffolding.setLevels(new ArrayList(levels));
	   oldScaffolding.setCriteria(new ArrayList(criteria));
	   oldScaffolding.setScaffoldingCells(new HashSet(scaffoldingCells));
	   oldScaffolding.setMatrix(new HashSet());

	   oldScaffolding.setEvalWorkflows(new HashSet(scaffEvalWorkflows));
	   oldScaffolding.setEvaluators(new HashSet(scaffEvaluators));
	   oldScaffolding.setReviewers(new HashSet(scaffReviewers));
	   oldScaffolding.setAttachments(new ArrayList(scaffAttachments));

	   removeFromSession(oldScaffolding);


	   //Saving the agent is not necessary and causes a StackOverflowError when XMLEncoder tries
	   // to serialize.  So, we clear out the agents.
	   oldScaffolding.setOwner(null);
	   oldScaffolding.setPublishedBy(null);

	   // We also strip the Agent from the attached Style if there is one.
	   if (oldScaffolding.getStyle() != null) {
		   oldScaffolding.getStyle().setOwner(null);
	   }

	   ByteArrayOutputStream bos = new ByteArrayOutputStream();
	   XMLEncoder xenc=new XMLEncoder(bos);
	   try {
		   xenc.writeObject(oldScaffolding);
	   }
	   catch (StackOverflowError e) {
		   logger.error("Stack Overflow when serializing Scaffolding with ID: " + oldScaffolding.getId().getValue()
                        + ". This is likely due to an unexpected Agent being referenced.");
		   throw new IllegalArgumentException("Caught Stack Overflow serializing Scaffolding. It likely contains a nested Agent reference.", e);
	   }
	   xenc.close();

	   removeFromSession(oldScaffolding);

	   storeFileInZip(zos, new ByteArrayInputStream(bos.toByteArray()),
			   "scaffolding");
	   this.getHibernateTemplate().clear();
	   try {
		   this.getHibernateTemplate().flush();
	   }
	   catch (AssertionFailure af) {
		   //TODO There's got to be a better way to catch/prevent this error
		   logger.warn("Catching AssertionFailure from Hibernate during a flush");
		   this.getSession().clear();
	   }
	   bos.close();

	   zos.finish();
	   zos.flush();
   }

   protected void exportGuidance(ZipOutputStream zos, List guidanceIds)
         throws IOException {
      ZipEntry newfileEntry = new ZipEntry("guidance/guidanceList");
      zos.putNextEntry(newfileEntry);
      getGuidanceManager().packageGuidanceForExport(guidanceIds, zos);
      zos.closeEntry();
   }
   
   protected void exportStyle(ZipOutputStream zos, Set styleIds)
         throws IOException {

      ZipEntry newfileEntry = new ZipEntry("style/styleList");
      zos.putNextEntry(newfileEntry);
      getStyleManager().packageStyleForExport(styleIds, zos);
      zos.closeEntry();
   }

   protected void exportWorkflowForms(ZipOutputStream zos, ObjectWithWorkflow obj, List additionalForms, List formIds) throws IOException {

      for (Iterator i=additionalForms.iterator();i.hasNext();) {
         String formId = (String) i.next();
         if (!formIds.contains(formId)) {
            storeFormInZip(zos, formId);
            formIds.add(formId);
         }
      }

      if (obj.getEvaluationDevice() != null) {
         String evalDevId = obj.getEvaluationDevice().getValue();
         if (!formIds.contains(evalDevId)) {
            storeFormInZip(zos, evalDevId);
            formIds.add(evalDevId);
         }
      }

      if (obj.getReflectionDevice() != null) {
         String reflDevId = obj.getReflectionDevice().getValue();
         if (!formIds.contains(reflDevId)) {
            storeFormInZip(zos, reflDevId);
            formIds.add(reflDevId);
         }
      }

      if (obj.getReviewDevice() != null) {
         String revDevId = obj.getReviewDevice().getValue();
         if (!formIds.contains(revDevId)) {
            storeFormInZip(zos, revDevId);
            formIds.add(revDevId);
         }
      }
   }


   protected void fixPageForms(WizardPageDefinition wizardPage, Map formsMap) {
      List forms = wizardPage.getAdditionalForms();
      List newForms = new ArrayList();
      for (Iterator i=forms.iterator();i.hasNext();) {
         String formId = (String) i.next();
         newForms.add(formsMap.get(formId));
      }
      wizardPage.setAdditionalForms(newForms);

      if (wizardPage.getEvaluationDevice() != null) {
         wizardPage.setEvaluationDevice(getIdManager().getId((String) formsMap.get(
               wizardPage.getEvaluationDevice().getValue())));
      }

      if (wizardPage.getReflectionDevice() != null) {
         wizardPage.setReflectionDevice(getIdManager().getId((String) formsMap.get(
               wizardPage.getReflectionDevice().getValue())));
      }

      if (wizardPage.getReviewDevice() != null) {
         wizardPage.setReviewDevice(getIdManager().getId((String) formsMap.get(
               wizardPage.getReviewDevice().getValue())));
      }
   }
   
   protected void fixPageForms(Scaffolding scaffolding, Map formsMap) {
	      List forms = scaffolding.getAdditionalForms();
	      List newForms = new ArrayList();
	      for (Iterator i=forms.iterator();i.hasNext();) {
	         String formId = (String) i.next();
	         newForms.add(formsMap.get(formId));
	      }
	      scaffolding.setAdditionalForms(newForms);

	      if (scaffolding.getEvaluationDevice() != null) {
	    	  scaffolding.setEvaluationDevice(getIdManager().getId((String) formsMap.get(
	    			  scaffolding.getEvaluationDevice().getValue())));
	      }

	      if (scaffolding.getReflectionDevice() != null) {
	    	  scaffolding.setReflectionDevice(getIdManager().getId((String) formsMap.get(
	    			  scaffolding.getReflectionDevice().getValue())));
	      }

	      if (scaffolding.getReviewDevice() != null) {
	    	  scaffolding.setReviewDevice(getIdManager().getId((String) formsMap.get(
	    			  scaffolding.getReviewDevice().getValue())));
	      }
	   }

   protected void storeFormInZip(ZipOutputStream zos, String formId) throws IOException {

      ZipEntry newfileEntry = new ZipEntry("forms/" + formId + ".form");

      zos.putNextEntry(newfileEntry);

      getStructuredArtifactDefinitionManager().packageFormForExport(formId, zos, false);

      zos.closeEntry();
   }

   protected void storeScaffoldingFile(ZipOutputStream zos, Id fileId) throws IOException {
      if (fileId == null) {
         return;
      }

      Node oldNode = getNode(fileId);

      String newName = oldNode.getName();

      storeFileInZip(zos, oldNode.getInputStream(),
         oldNode.getMimeType().getValue() + File.separator +
         fileId.getValue() + File.separator + newName);
   }
   
   protected void storeFileInZip(ZipOutputStream zos, InputStream in, String entryName)
      throws IOException {

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
      zos.closeEntry();
      in.close();
   }
   
   /**
    * This unpacks a zipped scaffolding and places it into the siteId. It saves the guidance, styles,
    * and forms, resets the ids, and saves the scaffolding.  It returns the new unpacked scaffolding.
    * 
    * The owner becomes the current agent.
    * 
    * @param siteId String of the site id
    * @param zis ZipInputStream of the packed scaffolding
    * @throws IOException
    */
   protected Scaffolding uploadScaffolding(String siteId, ZipInputStream zis)  throws IOException {
      
      ZipEntry currentEntry = zis.getNextEntry();
      Scaffolding scaffolding = null;

      String tempDirName = getIdManager().createId().getValue();

      boolean itWorked = false;

      Map formsMap = new Hashtable();
      Map guidanceMap = null;
      Map styleMap = null;

      try {
         ContentCollectionEdit fileParent = getFileDir(tempDirName);
         boolean gotFile = false;
         while (currentEntry != null) {
            logger.debug("current entry name: " + currentEntry.getName());

            if (currentEntry.getName().equals("scaffolding")) {
               try {
                  scaffolding = processScaffolding(zis);
               } catch (ClassNotFoundException e) {
                  logger.error("Class not found loading scaffolding", e);
                  throw new OspException(e);
               }
            }
            else if (!currentEntry.isDirectory()) {
               if (currentEntry.getName().startsWith("forms/")) {
                  processMatrixForm(currentEntry, zis, formsMap,
                        getIdManager().getId(siteId));
               }
               else if (currentEntry.getName().equals("guidance/guidanceList")) {
                  gotFile = true;
                  guidanceMap = processMatrixGuidance(fileParent, siteId, zis);
               }
               else if (currentEntry.getName().equals("style/styleList")) {
                  gotFile = true;
                  styleMap = processMatrixStyle(fileParent, siteId, zis);
               }
            }

            zis.closeEntry();
            currentEntry = zis.getNextEntry();
         }
         if(scaffolding == null)
            throw new InvalidUploadException("The scaffolding file was not found in the import file");
         scaffolding.setId(null);
         scaffolding.setPublished(false);
         scaffolding.setPublishedBy(null);
         scaffolding.setPublishedDate(null);
         scaffolding.setModifiedDate(new Date(System.currentTimeMillis()));
         scaffolding.setOwner(getAuthnManager().getAgent());
         scaffolding.setWorksiteId(getIdManager().getId(siteId));
         
         resetIds(scaffolding, guidanceMap, formsMap, styleMap, siteId);
         
         //this variable is used for version control: if this is null when importing a matrix,
         //then the matrix is an older version and set all defaults to false
         if(!scaffolding.isDefaultFormsMatrixVersion()){
        	 //This means the matrix being imported is from an earlier version that didn't have defaultForm values
        	 //therefore, we must set all defaultForm values to false (their default value is true)
        	 setAllDefaultFormValuesToFalse(scaffolding);
        	 //now that this matrix is in the current version, we must set defualtMatrix flag to true
        	 scaffolding.setDefaultFormsMatrixVersion(true);
         }
         
         

         scaffolding = saveNewScaffolding(scaffolding);         

         if (gotFile) {
            fileParent.getPropertiesEdit().addProperty(
                  ResourceProperties.PROP_DISPLAY_NAME, scaffolding.getTitle());
            getContentHosting().commitCollection(fileParent);
         }
         else {
            getContentHosting().cancelCollection(fileParent);
         }

         scaffolding = storeScaffolding(scaffolding);
         
         createEvaluatorAuthzForImport(scaffolding);
         createReviewersAuthzForImport(scaffolding);
         
         itWorked = true;
         return scaffolding;
      }
      catch (Exception exp) {
         throw new RuntimeException(exp);
      }
      finally {
         try {
            zis.closeEntry();
         }
         catch (IOException e) {
            logger.error("", e);
         }
      }
   }
   
   public void setAllDefaultFormValuesToFalse(Scaffolding scaffolding){
	   for(int i=0; i < scaffolding.getScaffoldingCells().size(); i++){
		   ((ScaffoldingCell) ((Set) scaffolding.getScaffoldingCells()).toArray()[i]).setAllowRequestFeedback(false);
		   ((ScaffoldingCell) ((Set) scaffolding.getScaffoldingCells()).toArray()[i]).setDefaultCustomForm(false);
		   ((ScaffoldingCell) ((Set) scaffolding.getScaffoldingCells()).toArray()[i]).setDefaultEvaluationForm(false);
		   ((ScaffoldingCell) ((Set) scaffolding.getScaffoldingCells()).toArray()[i]).setDefaultEvaluators(false);
		   ((ScaffoldingCell) ((Set) scaffolding.getScaffoldingCells()).toArray()[i]).setDefaultFeedbackForm(false);
		   ((ScaffoldingCell) ((Set) scaffolding.getScaffoldingCells()).toArray()[i]).setDefaultReflectionForm(false);
		   ((ScaffoldingCell) ((Set) scaffolding.getScaffoldingCells()).toArray()[i]).setDefaultReviewers(false);
	   }
	   
   }

   public Scaffolding uploadScaffolding(Reference uploadedScaffoldingFile, String siteId)
         throws IOException {
      Node file = getNode(uploadedScaffoldingFile);

      ZipInputStream zis = new UncloseableZipInputStream(file.getInputStream());
      return uploadScaffolding(siteId, zis);
      
   }

   protected Map processMatrixGuidance(ContentCollection parent, String siteId,
                                       ZipInputStream zis) throws IOException {
      return getGuidanceManager().importGuidanceList(parent, siteId, zis);
   }
   
   protected Map processMatrixStyle(ContentCollection parent, String siteId,
         ZipInputStream zis) throws IOException {
      return getStyleManager().importStyleList(parent, siteId, zis);
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

   public void checkPageAccess(String id) {
      Id pageId = getIdManager().getId(id);
      WizardPage page = getWizardPage(pageId);
      WizardPageDefinition wpd = null;
      Scaffolding scaffolding = null;
      boolean owns = false;
      if (page == null) {
    	  wpd = getWizardPageDefinition(pageId);
    	  scaffolding = this.getScaffoldingCellByWizardPageDef(wpd.getId()).getScaffolding();
    	  owns = scaffolding.getOwner().getId().equals(getAuthnManager().getAgent().getId());
      } else {
    	  wpd = page.getPageDefinition();
    	  scaffolding = this.getMatrixByPage(page.getId()).getScaffolding();
    	  owns = page.getOwner().getId().equals(getAuthnManager().getAgent().getId());
      }
      // todo need to figure out matrix or wizard authz stuff here

      // this should set the security advisor for the attached artifacts.
      //getPageArtifacts(page);
      
      boolean isMatrix = wpd.getType().equals(wpd.WPD_MATRIX_TYPE);
      
      boolean canEval = false, canReview = false, hideEvaluations = false;
      
      if(wpd.isDefaultEvaluators()){
    	  if(isMatrix){
    		  canEval = hasPermission(scaffolding.getId(), scaffolding.getWorksiteId(), MatrixFunctionConstants.EVALUATE_MATRIX);
    	  }
      }else{
    	  canEval = getAuthzManager().isAuthorized(MatrixFunctionConstants.EVALUATE_MATRIX, 
  	            wpd.getId());
      }
      
      //this is user specified reviewer access:
      canReview = hasPermission(pageId, this.getIdManager().getId(wpd.getSiteId()), MatrixFunctionConstants.FEEDBACK_MATRIX);
      
      
      if(!canReview){
    	  if(wpd.isDefaultReviewers()){
    		  if(isMatrix){
    			  //currently, this can only be true if its a matrix
    			  canReview = hasPermission(scaffolding.getId(), scaffolding.getWorksiteId(), MatrixFunctionConstants.REVIEW_MATRIX);
    		  }
    	  }else{
    		  canReview = getAuthzManager().isAuthorized(MatrixFunctionConstants.REVIEW_MATRIX, 
    				  wpd.getId());
    	  }
      }

      if (!canReview) {
         canReview = getAuthzManager().isAuthorized(WizardFunctionConstants.REVIEW_WIZARD, 
            getIdManager().getId(wpd.getSiteId()));
      }
      
      if(isMatrix){

    	  if(wpd.isDefaultEvaluationForm()){
    		  hideEvaluations = scaffolding.isHideEvaluations();
    	  }else{
    		  hideEvaluations = wpd.isHideEvaluations();
    	  }
      }

      
      
      
      boolean canAccessAllCells = false;
      boolean canViewOtherReviews = false;
      boolean canViewOtherEvals = false;
      
      if(isMatrix){
    	  canAccessAllCells = getAuthzManager().isAuthorized(MatrixFunctionConstants.ACCESS_ALL_CELLS, getIdManager().getId(scaffolding.getReference()));
    	  canViewOtherReviews = getAuthzManager().isAuthorized(MatrixFunctionConstants.VIEW_FEEDBACK_OTHER, getIdManager().getId(scaffolding.getReference()));
    	  canViewOtherEvals = getAuthzManager().isAuthorized(MatrixFunctionConstants.VIEW_EVAL_OTHER, getIdManager().getId(scaffolding.getReference()));
      }
      org.sakaiproject.tool.api.Session session = SessionManager.getCurrentSession();
      
      boolean hasAccessThroughLink = false;

      String decPageId = (String) session.getAttribute("decPageId");
      if(decPageId != null && !decPageId.equals("")){
    	  hasAccessThroughLink = canUserAccessWizardPageAndLinkedArtifcact(wpd.getSiteId(), decPageId, "/wizard/page/" + pageId.getValue());
      }

      
      if (hasAccessThroughLink || canEval || canReview || owns || canAccessAllCells || canViewOtherReviews || canViewOtherEvals) {
         //can I look at files? - own, review or eval
         getPageContents(page);
         
         //can I look at forms? - own, review or eval
         getPageForms(page);
         
         //can I look at reviews/evals/reflections? - own, review or eval
         getReviewManager().getReviewsByParentAndType(
               id, Review.REFLECTION_TYPE,
               wpd.getSiteId(),
               MatrixContentEntityProducer.MATRIX_PRODUCER);
         
         
         SecurityAdvisor contentAdvisor = (SecurityAdvisor)session.getAttribute("assignment.content.security.advisor");
         if (contentAdvisor != null)
        	 securityService.pushAdvisor(contentAdvisor);
      }
      
      if (hasAccessThroughLink || (owns && !hideEvaluations) || (isMatrix && canViewOtherEvals) || (!isMatrix && canEval)) {
         //can I look at reviews/evals/reflections? - own or eval
         getReviewManager().getReviewsByParentAndType(
               id, Review.EVALUATION_TYPE,
               wpd.getSiteId(),
               MatrixContentEntityProducer.MATRIX_PRODUCER);
      }
      
      if (hasAccessThroughLink || owns || (isMatrix && canViewOtherReviews) || (!isMatrix && (canEval || canReview))){
         //can I look at reviews/evals/reflections? - own or review
         getReviewManager().getReviewsByParentAndType(
               id, Review.FEEDBACK_TYPE,
               wpd.getSiteId(),
               MatrixContentEntityProducer.MATRIX_PRODUCER);         
      }
   }

   
   /**
    * this creates authorizations for each cell from the reviewers contained in the cell
    * @param scaffolding
    */
   private void createReviewersAuthzForImport(Scaffolding scaffolding) {
	   
	   //scaffolding reviewers:
	   for (Iterator i = scaffolding.getReviewers().iterator(); i.hasNext();) {
           Id id = (Id)i.next();
           if (id.getValue().startsWith("/site/")) {
              // it's a role
              String[] agentValues = id.getValue().split("/");
              
              String newStrId = id.getValue().replaceAll(agentValues[2], 
                    scaffolding.getWorksiteId().getValue());
              id = idManager.getId(newStrId);
           }
           Agent agent = this.getAgentFromId(id);

           if (agent != null  && agent.getId() != null) {
              this.getAuthzManager().createAuthorization(agent, 
                    MatrixFunctionConstants.REVIEW_MATRIX, scaffolding.getId());
           }
        }
	   
	   
	   
	   //all cell's Reviewers:
	   
      for (Iterator iter = scaffolding.getScaffoldingCells().iterator(); iter.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell) iter.next();
         Collection evals = sCell.getReviewers();
         for (Iterator i = evals.iterator(); i.hasNext();) {
            Id id = (Id)i.next();
            if (id.getValue().startsWith("/site/")) {
               // it's a role
               String[] agentValues = id.getValue().split("/");
               
               String newStrId = id.getValue().replaceAll(agentValues[2], 
                     scaffolding.getWorksiteId().getValue());
               id = idManager.getId(newStrId);
            }
            Agent agent = this.getAgentFromId(id);

            if (agent != null  && agent.getId() != null) {
               this.getAuthzManager().createAuthorization(agent, 
                     MatrixFunctionConstants.REVIEW_MATRIX, sCell.getWizardPageDefinition().getId());
            }
         }
      }
   }
   
   /**
    * this creates authorizations for each cell from the evaluators contained in the cell
    * @param scaffolding
    */
   private void createEvaluatorAuthzForImport(Scaffolding scaffolding) {
	   
	   //scaffolding evaluators:
	   for (Iterator i = scaffolding.getEvaluators().iterator(); i.hasNext();) {
           Id id = (Id)i.next();
           if (id.getValue().startsWith("/site/")) {
              // it's a role
              String[] agentValues = id.getValue().split("/");
              
              String newStrId = id.getValue().replaceAll(agentValues[2], 
                    scaffolding.getWorksiteId().getValue());
              id = idManager.getId(newStrId);
           }
           Agent agent = this.getAgentFromId(id);

           if (agent != null  && agent.getId() != null) {
              this.getAuthzManager().createAuthorization(agent, 
                    MatrixFunctionConstants.EVALUATE_MATRIX, scaffolding.getId());
           }
        }
	   
	   
	   
	   //all cell's evaluators:
	   
      for (Iterator iter = scaffolding.getScaffoldingCells().iterator(); iter.hasNext();) {
         ScaffoldingCell sCell = (ScaffoldingCell) iter.next();
         Collection evals = sCell.getEvaluators();
         for (Iterator i = evals.iterator(); i.hasNext();) {
            Id id = (Id)i.next();
            if (id.getValue().startsWith("/site/")) {
               // it's a role
               String[] agentValues = id.getValue().split("/");
               
               String newStrId = id.getValue().replaceAll(agentValues[2], 
                     scaffolding.getWorksiteId().getValue());
               id = idManager.getId(newStrId);
            }
            Agent agent = this.getAgentFromId(id);

            if (agent != null  && agent.getId() != null) {
               this.getAuthzManager().createAuthorization(agent, 
                     MatrixFunctionConstants.EVALUATE_MATRIX, sCell.getWizardPageDefinition().getId());
            }
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

   /**
    * This unpacks the scaffolding in a zip stream and returns it
    * @param zis
    * @return
    * @throws IOException
    * @throws ClassNotFoundException
    */
   protected Scaffolding processScaffolding(ZipInputStream zis) throws IOException, ClassNotFoundException {
      XMLDecoder dec = new XMLDecoder(zis);
      return (Scaffolding)dec.readObject();
   }


   protected void processFile(ZipEntry currentEntry, ZipInputStream zis,
         Hashtable fileMap, ContentCollection fileParent) throws IOException, InconsistentException, PermissionException, IdUsedException, IdInvalidException, OverQuotaException, ServerOverloadException {
      File file = new File(currentEntry.getName());

      MimeType mimeType = new MimeType(file.getParentFile().getParentFile().getParentFile().getName(),
         file.getParentFile().getParentFile().getName());

      String contentType = mimeType.getValue();

      Id oldId = getIdManager().getId(file.getParentFile().getName());

      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      int c = zis.read();

      while (c != -1) {
         bos.write(c);
         c = zis.read();
      }

      String fileId = fileParent.getId() + file.getName();
      ContentResourceEdit resource = getContentHosting().addResource(fileId);
      ResourcePropertiesEdit resourceProperties =
            resource.getPropertiesEdit();
      resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, file.getName());
      resource.setContent(bos.toByteArray());
      resource.setContentType(contentType);
      getContentHosting().commitResource(resource);

      Id newId = getIdManager().getId(getContentHosting().getUuid(resource.getId()));

      fileMap.put(oldId, newId);
   }

   /**
    * resets the style, criteria, levels, and scaffolding cells
    * @param scaffolding
    * @param guidanceMap
    * @param formsMap
    * @param styleMap
    * @param siteId
    */
   protected void resetIds(Scaffolding scaffolding, Map guidanceMap, Map formsMap, Map styleMap, String siteId) {
      
      if (scaffolding.getStyle() != null) {
         if (styleMap != null) {
            scaffolding.setStyle((Style) styleMap.get(scaffolding.getStyle().getId().getValue()));
         }
         else {
            scaffolding.getStyle().setId(null);
         }
      }      
      substituteScaffoldingForms(scaffolding, guidanceMap, formsMap);
      substituteCriteria(scaffolding);
      substituteLevels(scaffolding);
      substituteScaffoldingCells(scaffolding, guidanceMap, formsMap, styleMap, siteId);
   }

   protected void substituteCriteria(Scaffolding scaffolding) {
      List newCriteria = new ArrayList();
      for (Iterator i=scaffolding.getCriteria().iterator(); i.hasNext();) {
         Criterion criterion = (Criterion)i.next();
         criterion.setId(null);
         newCriteria.add(criterion);
      }
      scaffolding.setCriteria(newCriteria);
   }
   
   protected void substituteLevels(Scaffolding scaffolding) {
      List newLevels = new ArrayList();
      for (Iterator i=scaffolding.getLevels().iterator(); i.hasNext();) {
         Level level = (Level)i.next();

         level.setId(null);
         newLevels.add(level);
      }
      scaffolding.setLevels(newLevels);
   }
   
   protected void substituteScaffoldingForms(Scaffolding scaffolding, Map guidanceMap, Map formsMap){

       fixPageForms(scaffolding, formsMap);

       Set newWorkflows = new HashSet();
       for (Iterator jiter=scaffolding.getEvalWorkflows().iterator(); jiter.hasNext();) {
          Workflow w = (Workflow)jiter.next();
          w.setId(null);
          Set newItems = new HashSet();
          for (Iterator kiter=w.getItems().iterator(); kiter.hasNext();) {
             WorkflowItem wfi = (WorkflowItem)kiter.next();
             wfi.setId(null);
             newItems.add(wfi);
          }
          
          newWorkflows.add(w);
       }
   }
   
   protected void substituteScaffoldingCells(Scaffolding scaffolding, Map guidanceMap, Map formsMap, Map styleMap, String siteId) {
      Set sCells = new HashSet(); 
      for (Iterator iter=scaffolding.getScaffoldingCells().iterator(); iter.hasNext();) {
         ScaffoldingCell scaffoldingCell = (ScaffoldingCell) iter.next();
         scaffoldingCell.setId(null);
         
         WizardPageDefinition wpd = scaffoldingCell.getWizardPageDefinition();
         wpd.setId(null);
         wpd.setSiteId(siteId);
         if (wpd.getGuidance() != null) {
            if (guidanceMap != null) {
               Guidance guidance = (Guidance) guidanceMap.get(wpd.getGuidance().getId().getValue());
               wpd.setNewId(getIdManager().createId());
               guidance.setSecurityQualifier(wpd.getNewId());
               wpd.setGuidance(guidance);
            }
            else {
               wpd.getGuidance().setId(null);
            }
         }
         
         if (wpd.getStyle() != null) {
            if (styleMap != null) {
               wpd.setStyle((Style) styleMap.get(wpd.getStyle().getId().getValue()));
            }
            else {
               wpd.getStyle().setId(null);
            }
         }

         fixPageForms(wpd, formsMap);

         Set newWorkflows = new HashSet();
         for (Iterator jiter=wpd.getEvalWorkflows().iterator(); jiter.hasNext();) {
            Workflow w = (Workflow)jiter.next();
            w.setId(null);
            Set newItems = new HashSet();
            for (Iterator kiter=w.getItems().iterator(); kiter.hasNext();) {
               WorkflowItem wfi = (WorkflowItem)kiter.next();
               wfi.setId(null);
               newItems.add(wfi);
            }
            
            newWorkflows.add(w);
         }
         
         scaffoldingCell.setCells(new HashSet());
         sCells.add(scaffoldingCell);
      }   
      scaffolding.setScaffoldingCells(sCells);
   }
   
   public void removeExposedMatrixTool(Scaffolding scaffolding) {
      String siteId = scaffolding.getWorksiteId().getValue();
      try {
         Site siteEdit = SiteService.getSite(siteId);

         SitePage page = siteEdit.getPage(scaffolding.getExposedPageId());
         siteEdit.removePage(page);
         SiteService.save(siteEdit);
         scaffolding.setExposedPageId(null);
      } catch (IdUnusedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (PermissionException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
   public void exposeMatrixTool(Scaffolding scaffolding) {
      //TODO add logging errors back
      String siteId = scaffolding.getWorksiteId().getValue();
      try {
         Site siteEdit = SiteService.getSite(siteId);


         SitePage page = siteEdit.addPage();

         page.setTitle(scaffolding.getTitle());
         page.setLayout(SitePage.LAYOUT_SINGLE_COL);

         ToolConfiguration tool = page.addTool();
         
         tool.setTool("osp.exposedmatrix", ToolManager.getTool("osp.exposedmatrix"));
         tool.setTitle(scaffolding.getTitle());
         tool.setLayoutHints("0,0");
         tool.getPlacementConfig().setProperty(MatrixManager.EXPOSED_MATRIX_KEY, scaffolding.getId().getValue());

         //LOG.info(this+": SiteService.commitEdit():" +siteId);

         SiteService.save(siteEdit);
         scaffolding.setExposedPageId(page.getId());


      } catch (IdUnusedException e) {
//       TODO Auto-generated catch block
         e.printStackTrace();
      } catch (PermissionException e) {
//       TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   private boolean findInAuthz(Id qualifier, Agent agent, List authzs) {
      for (Iterator iter = authzs.iterator(); iter.hasNext();) {
         Authorization authz = (Authorization) iter.next();
         // Same item, different agent
         if (!authz.getAgent().equals(agent) && authz.getQualifier().equals(qualifier))
            return true;
      }
      return false;
   }
   
   

   public boolean checkStyleConsumption(Id styleId) {
      //Check Scaffolding and WizardPageDef
      List scaffolding = getScaffoldingByStyle(styleId);
      if (scaffolding != null && !scaffolding.isEmpty() && scaffolding.size() > 0)
         return true;
      
      //Also check for WizardPageDef
      List wizPageDefs = this.getWizardPageDefByStyle(styleId);
      if (wizPageDefs != null && !wizPageDefs.isEmpty() && wizPageDefs.size() > 0)
         return true;
      
      return false;
   }

   public List getStyles(Id objectId) {
      WizardPage wp = getWizardPage(objectId);
      ScaffoldingCell sCell = getScaffoldingCellByWizardPageDef(objectId);

      if (wp != null || sCell != null) {
    	  if (sCell == null) {
    		  sCell = getScaffoldingCellByWizardPageDef(
                     wp.getPageDefinition().getId());
    	  }
         if (sCell != null) {
            List styles = new ArrayList();
            if (sCell.getScaffolding().getStyle() != null) {
               styles.add(sCell.getScaffolding().getStyle());
            }
            if (sCell.getWizardPageDefinition().getStyle() != null) {
               styles.add(sCell.getWizardPageDefinition().getStyle());
            }
            return styles;
         }
      }

      Scaffolding scaffolding = (Scaffolding) getHibernateTemplate().get(Scaffolding.class, objectId);

      if (scaffolding != null) {
         List styles = new ArrayList();
         if (scaffolding.getStyle() != null) {
            styles.add(scaffolding.getStyle());
         }
         return styles;
      }

      return null;
   }

   /* (non-Javadoc)
    * @see org.theospi.portfolio.shared.mgt.ReadableObjectHome#getType()
    */
   public org.sakaiproject.metaobj.shared.model.Type getType() {
      return new org.sakaiproject.metaobj.shared.model.Type(idManager.getId("matrix"), "Matrix");
   }

   public String getExternalType() {
      return getType().getId().getValue();
   }


   /* (non-Javadoc)
    * @see org.theospi.portfolio.shared.mgt.ReadableObjectHome#load(org.theospi.portfolio.shared.model.Id)
    */
   public Artifact load(Id id) {
	   Matrix matrix = getMatrix(id);
	   if (matrix != null) {
		   loadMatrixCellReviews(matrix);
		   matrix.setHome(this);
		   return matrix;
	   }
	   return null;
   }
   
   private void loadMatrixCellReviews(Matrix matrix) {
      for (Iterator cells = matrix.getCells().iterator(); cells.hasNext();) {
         Cell cell = (Cell) cells.next();
         WizardPage page = cell.getWizardPage();
         List reflections = new ArrayList();
         List evaluations = new ArrayList();
         List feedback = new ArrayList();

         List reviews = getReviewManager().getReviewsByParentAndTypes(page.getId().getValue(),
                 new int[]{Review.REFLECTION_TYPE, Review.EVALUATION_TYPE, Review.FEEDBACK_TYPE},
                 page.getPageDefinition().getSiteId(),
              MatrixContentEntityProducer.MATRIX_PRODUCER);


         for (Iterator reviewsIter = reviews.iterator(); reviewsIter.hasNext();) {
             Review review = (Review) reviewsIter.next();
             if (review.getType() == Review.EVALUATION_TYPE) {
                 evaluations.add(review);
             } else if (review.getType() == Review.REFLECTION_TYPE) {
                 reflections.add(review);
             } else if (review.getType() == Review.FEEDBACK_TYPE) {
                 feedback.add(review);
             }
         }

         page.setReflections(reflections);
         page.setEvaluations(evaluations);
         page.setFeedback(feedback);
         page.getAttachments().size();
         page.getPageForms().size();
         
         //Make sure that the attachments and forms have been added to the security advisor
         getPageContents(page);
         getPageForms(page);
      }
   }

   public Collection findByType(String type) {
      return getHibernateTemplate().find("from Matrix");
   }

   public boolean getLoadArtifacts() {
      return loadArtifacts;
   }

   public void setLoadArtifacts(boolean loadArtifacts) {
      this.loadArtifacts = loadArtifacts;
   }

   /* (non-Javadoc)
    * @see org.theospi.portfolio.shared.mgt.ReadableObjectHome#createInstance()
    */
   public Artifact createInstance() {
      Artifact instance = new Matrix();
      prepareInstance(instance);
      return instance;
   }

   /* (non-Javadoc)
    * @see org.theospi.portfolio.shared.mgt.ReadableObjectHome#prepareInstance(org.theospi.portfolio.shared.model.Artifact)
    */
   public void prepareInstance(Artifact object) {
      object.setHome(this);

   }

   /* (non-Javadoc)
    * @see org.theospi.portfolio.shared.mgt.ReadableObjectHome#createSample()
    */
   public Artifact createSample() {
      return createInstance();
   }

   /* (non-Javadoc)
    * @see org.theospi.portfolio.shared.mgt.ReadableObjectHome#findByOwner(org.theospi.portfolio.shared.model.Agent)
    */
   public Collection findByOwner(Agent owner) throws FinderException {
      return getMatrices(null, owner.getId());
   }

   /* (non-Javadoc)
    * @see org.theospi.portfolio.shared.mgt.ReadableObjectHome#isInstance(org.theospi.portfolio.shared.model.Artifact)
    */
   public boolean isInstance(Artifact testObject) {
      return (testObject instanceof Matrix);
   }

   /* (non-Javadoc)
    * @see org.theospi.portfolio.shared.mgt.ReadableObjectHome#refresh()
    */
   public void refresh() {
      // TODO Auto-generated method stub

   }

   public String getExternalUri(Id artifactId, String name) {
      throw new UnsupportedOperationException();
   }

   public InputStream getStream(Id artifactId) {
      throw new UnsupportedOperationException();
   }

   public boolean isSystemOnly() {
      return false;
   }

   public Class getInterface() {
      return this.getClass();
   }

   public Collection findBySharedOwnerAndType(List ownerList, String type) {
      return null; // not implemented for matrices (only relevant to portfolios)
   }

   public Collection findBySharedOwnerAndType(List ownerList, String type, MimeType mimeType) {
      return null; // not implemented for matrices (only relevant to portfolios)
   }

   /* (non-Javadoc)
    * @see org.theospi.portfolio.repository.ArtifactFinder#findByOwnerAndType(org.theospi.portfolio.shared.model.Id, java.lang.String)
    */
   public Collection findByOwnerAndType(Id owner, String type) {
      return findByOwner(owner);
   }

   public Collection findByOwnerAndType(Id owner, String type, MimeType mimeType) {
      // not gonna find mime types
      return null;
   }

   /* (non-Javadoc)
    * @see org.theospi.portfolio.repository.ArtifactFinder#findByOwner(org.theospi.portfolio.shared.model.Id)
    */
   public Collection findByOwner(Id owner) {
      try {
         return this.findByOwner(agentManager.getAgent(owner));
      } catch (FinderException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         return null;
      }
   }

   public Collection findByWorksiteAndType(Id worksiteId, String type) {
      //TODO implement this
      return new ArrayList();
   }

   public Collection findByWorksite(Id worksiteId) {
      //TODO implement this
      return new ArrayList();
   }
   
   public Element getArtifactAsXml(Artifact artifact) {
      return getXmlRenderer().getArtifactAsXml(artifact);
   }

   public Element getArtifactAsXml(Artifact artifact, String container, String site, String context) {
	   return getXmlRenderer().getArtifactAsXml(artifact, container, site, context);
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
   
   public LockManager getLockManager() {
      return lockManager;
   }
   public void setLockManager(LockManager lockManager) {
      this.lockManager = lockManager;
   }

   /**
    * This is called by the download manager to package a scaffolding for download as zip
    * @param params Map of url parameters
    * @param out  OutputStream to push the file
    */
   public String packageForDownload(Map params, OutputStream out) throws IOException {
      packageScffoldingForExport(
         getIdManager().getId(((String[])params.get(SCAFFOLDING_ID_TAG))[0]),
         out);
      
      //Blank filename for now -- no more dangerous, since the request is in the form of a filename
      return "";      
   }

   /**
    * This is the method called when duplicating a site.  It copies all published scaffolding
    * from the fromContext site id to the toContext site id
    * @param fromContext   String  from site id
    * @param toContext     String  to site id
    * @param resourceIds   List
    */
   public void importResources(String fromContext, String toContext, List resourceIds) {      
      ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
      try {
         Thread.currentThread().setContextClassLoader(getClass().getClassLoader());


         List scaffolding = this.findPublishedScaffolding(fromContext);
         if (scaffolding == null) {
            return;
         }
         
         for (Iterator iter = scaffolding.iterator(); iter.hasNext();) {
            Scaffolding scaffold = (Scaffolding)iter.next();
            Id id = scaffold.getId();
   
            getHibernateTemplate().evict(scaffold);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            
            packageScffoldingForExport(id, bos);
            
            InputStream is = new ByteArrayInputStream(bos.toByteArray());
            ZipInputStream zis = new UncloseableZipInputStream(is);
            bos = null;
            
            uploadScaffolding(toContext, zis);
            is = null;
            zis = null;
         }
      } catch (IOException e) {
         logger.error("", e);
         throw new OspException(e);
      }
      finally {
         Thread.currentThread().setContextClassLoader(currentLoader);
      }
      
   }

   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   public PresentableObjectHome getXmlRenderer() {
      return xmlRenderer;
   }

   public void setXmlRenderer(PresentableObjectHome xmlRenderer) {
      this.xmlRenderer = xmlRenderer;
   }

   public SecurityService getSecurityService() {
      return securityService;
   }

   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }

   public EntityContextFinder getContentFinder() {
      return contentFinder;
   }

   public void setContentFinder(EntityContextFinder contentFinder) {
      this.contentFinder = contentFinder;
   }
   
   protected String buildRef(String siteId, String contextId, ContentResource resource) {
      return ContentEntityUtil.getInstance().buildRef(
            MatrixContentEntityProducer.MATRIX_PRODUCER, siteId, contextId, resource.getReference());
   }
   
   /**
    * Build a new reference with the given params
    * @param siteId
    * @param contextId
    * @param producer
    * @return A String reference like so: "/&lt;producer&gt;/&lt;siteId&gt;/&lt;contextId&gt;"
    */
   protected String buildRefDecorator(String siteId, String contextId, String producer) {
	   return ContentEntityUtil.getInstance().buildRef(
		         producer, siteId, contextId, "");
   }

   public DefaultScaffoldingBean getDefaultScaffoldingBean() {
      return defaultScaffoldingBean;
   }

   public void setDefaultScaffoldingBean(
         DefaultScaffoldingBean defaultScaffoldingBean) {
      this.defaultScaffoldingBean = defaultScaffoldingBean;
   }
   
   public void processWorkflow(Id workflowId, Id pageId) {
      Workflow workflow = getWorkflowManager().getWorkflow(workflowId);
      WizardPage page = getWizardPage(pageId);
      
      Collection items = workflow.getItems();
      for (Iterator i = items.iterator(); i.hasNext();) {
         WorkflowItem wi = (WorkflowItem)i.next();
         switch (wi.getActionType()) {
            // complete / return part 2
            case(WorkflowItem.STATUS_CHANGE_WORKFLOW):
               processStatusChangeWorkflow(wi, page);
               break;
            
            case(WorkflowItem.NOTIFICATION_WORKFLOW):
               processNotificationWorkflow(wi);
               break;
               
            // Return part 1
            case(WorkflowItem.CONTENT_LOCKING_WORKFLOW):
               processContentLockingWorkflow(wi, page);
               break;
         } // end processWorkflow
      } // end items.iterator
      storePage(page);
   } // end processWorkflow
   
   public void processWorkflow(int workflowOption, Id cellId) {
      Cell cell = getCell(cellId);
      WizardPage page = cell.getWizardPage();
      Date now = new Date(System.currentTimeMillis());

      //Actions for "next" cell
      if (workflowOption == Scaffolding.HORIZONTAL_PROGRESSION || 
            workflowOption == Scaffolding.VERTICAL_PROGRESSION) {

         Cell actionCell = getNextCell(cell, workflowOption);
         //If action cell is null, that means we are at the end of the row/column and have no next cell.
         if (actionCell != null) {
            WizardPage actionPage = actionCell.getWizardPage();
            if (actionPage != null) {               
               processContentLockingWorkflow(false, actionPage);
               processStatusChangeWorkflow(MatrixFunctionConstants.READY_STATUS, actionPage);
               page.setModified(now);
            }             
         }
      }
   }

   /**
    * This method locks/unlocks the page file attachments and the additional filled in forms.
    * @param lock boolean true locks the resources, false unlocks
    * @param page WizardPage of the content to lock
    */
   private void processContentLockingWorkflow(boolean lock, WizardPage page) {
      for (Iterator iter = page.getAttachments().iterator(); iter.hasNext();) {
         Attachment att = (Attachment)iter.next();
         if (lock) {
            getLockManager().lockObject(att.getArtifactId().getValue(), 
                  page.getId().getValue(), 
                  "Submitting cell, 4 eval", true);
         }
         else {
            getLockManager().removeLock(att.getArtifactId().getValue(), 
                  page.getId().getValue());
         }         
      }
      
      //the expectations, additional forms
      for (Iterator iter = page.getPageForms().iterator(); iter.hasNext();) {
         WizardPageForm pageForm = (WizardPageForm)iter.next();
         
         if (lock) {
            getLockManager().lockObject(pageForm.getArtifactId().getValue(), 
                  page.getId().getValue(), 
                  "Submitting cell, 4 eval", true);
         }
         else {
            getLockManager().removeLock(pageForm.getArtifactId().getValue(), 
                  page.getId().getValue());
         }         
      }
   }

   private void processContentLockingWorkflow(WorkflowItem wi, WizardPage page) {
      processContentLockingWorkflow(wi.getActionValue().equals(WorkflowItem.CONTENT_LOCKING_LOCK), page);     
   }

   private void processNotificationWorkflow(WorkflowItem wi) {
      // TODO implement
      
   }

   private void processStatusChangeWorkflow(String status, WizardPage page) {
      Date now = new Date(System.currentTimeMillis());
      page.setStatus(status);
      page.setModified(now);
   }
   
   private void processStatusChangeWorkflow(WorkflowItem wi, WizardPage page) {
      processStatusChangeWorkflow(wi.getActionValue(), page);
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

   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
      return structuredArtifactDefinitionManager;
   }

   public void setStructuredArtifactDefinitionManager(StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }

   public GuidanceManager getGuidanceManager() {
      return guidanceManager;
   }

   public void setGuidanceManager(GuidanceManager guidanceManager) {
      this.guidanceManager = guidanceManager;
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
   
   public String getImportFolderName() {
      return importFolderName;
   }

   public void setImportFolderName(String importFolderName) {
      this.importFolderName = importFolderName;
   }

   /**
    * @return the useExperimentalMatrix
    */
   public boolean isUseExperimentalMatrix() {
      return useExperimentalMatrix;
   }

   /**
    * @param useExperimentalMatrix the useExperimentalMatrix to set
    */
   public void setUseExperimentalMatrix(boolean useExperimentalMatrix) {
      this.useExperimentalMatrix = useExperimentalMatrix;
   }

   public boolean checkFormConsumption(Id formId) {
      Collection objectsWithForms = getHibernateTemplate().find("from ObjectWithWorkflow where " +
         "reflection_device_id = ? or evaluation_device_id = ? or review_device_id = ?",
         new Object[] {formId.getValue(), formId.getValue(), formId.getValue()});

      if (objectsWithForms.size() > 0) {
         return true;
      }

      String queryString = "from WizardPageDefinition as wpd " +
      		"left join wpd.additionalForms as af where af = ?";
      Collection additionalForms = getHibernateTemplate().find(queryString,
         new Object[] {formId.getValue()});

      if (additionalForms.size() > 0)
    	  return true;
      
      String queryString2 = "from Scaffolding as s " +
      		"left join s.additionalForms as af where af = ?";
      Collection defaultAdditionalForms = getHibernateTemplate().find(queryString2,
    		  new Object[] {formId.getValue()});

      if (defaultAdditionalForms.size() > 0)
    	  return true;
      
      return false;      
   }

   /**
    * {@inheritDoc}
    */
   public Collection<FormConsumptionDetail> getFormConsumptionDetails(Id formId) {
      Collection results = new ArrayList();

      String refl_type = messages.getString("reflection_device");
      String eval_type = messages.getString("evaluation_device");
      String review_type = messages.getString("review_device");
      String page_form = messages.getString("page_form");
      String defaultTxt = messages.getString("default") + " ";
      
      String cellNameText = messages.getString("cell_name_text");
      String matrixNameText = messages.getString("matrix_name_text");
      String wizPageNameText = messages.getString("wiz_page_name_text");
      String wizardNameText = messages.getString("wiz_name_text");
      
      String matrixReflection = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "sc.wizardPageDefinition.reflectionDevice, " +
      		   "sc.scaffolding.worksiteId, " +
      		   "'" + refl_type + "', " +
      		   "concat('" + cellNameText + "', sc.wizardPageDefinition.title), " +
      		   "concat('" + matrixNameText + "', sc.scaffolding.title)) " +
            "from ScaffoldingCell sc " +
            "where sc.wizardPageDefinition.reflectionDevice = :formId ";
      String matrixEval = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "sc.wizardPageDefinition.evaluationDevice, " +
      		   "sc.scaffolding.worksiteId, " +
      		   "'" + eval_type + "', " +
      		   "concat('" + cellNameText + "', sc.wizardPageDefinition.title), " +
      		   "concat('" + matrixNameText + "', sc.scaffolding.title)) " +
            "From ScaffoldingCell sc " +
            "where sc.wizardPageDefinition.evaluationDevice = :formId ";
      String matrixReview = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "sc.wizardPageDefinition.reviewDevice, " +
      		   "sc.scaffolding.worksiteId, " +
      		   "'" + review_type + "', " +
      		   "concat('" + cellNameText + "', sc.wizardPageDefinition.title), " +
      		   "concat('" + matrixNameText + "', sc.scaffolding.title)) " +
            "From ScaffoldingCell sc " +
            "where sc.wizardPageDefinition.reviewDevice = :formId ";
      
      String scaffoldingReflection = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
		      "s.reflectionDevice, " +
		      "s.worksiteId, " +
		      "'" + defaultTxt + refl_type + "', " +
		      "concat('" + matrixNameText + "', s.title)) " +
	      "from Scaffolding s " +
	      "where s.reflectionDevice = :formId ";
      String scaffoldingEval = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
		      "s.evaluationDevice, " +
		      "s.worksiteId, " +
		      "'" + defaultTxt + eval_type + "', " +
		      "concat('" + matrixNameText + "', s.title)) " +
	      "From Scaffolding s " +
	      "where s.evaluationDevice = :formId ";
      String scaffoldingReview = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
		      "s.reviewDevice, " +
		      "s.worksiteId, " +
		      "'" + defaultTxt + review_type + "', " +
		      "concat('" + matrixNameText + "', s.title)) " +
	      "From Scaffolding s " +
	      "where s.reviewDevice = :formId ";

      String wizardPageReflection = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "wpd.reflectionDevice, " +
      		   "w.siteId, " +
      		   "'" + refl_type + "', " +
      		   "concat('" + wizPageNameText + "', wpd.title), " +
      		   "concat('" + wizardNameText + "', w.name)) " +
            "From WizardPageSequence wps " +
            "join wps.wizardPageDefinition wpd " +
            "join wps.category c " +
            "join c.wizard w " +
            "where wpd.reflectionDevice = :formId ";
      String wizardPageEval = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "wpd.evaluationDevice, " +
      		   "w.siteId, " +
      		   "'" + eval_type + "', " +
      		   "concat('" + wizPageNameText + "', wpd.title), " +
      		   "concat('" + wizardNameText + "', w.name)) " +
            "From WizardPageSequence wps " +
            "join wps.wizardPageDefinition wpd " +
            "join wps.category c " +
            "join c.wizard w " +
            "where wpd.evaluationDevice = :formId ";
      String wizardPageReview ="select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "wpd.reviewDevice, " +
      		   "w.siteId, " +
      		   "'" + review_type + "', " +
      		   "concat('" + wizPageNameText + "', wpd.title), " +
      		   "concat('" + wizardNameText + "', w.name)) " +
            "From WizardPageSequence wps " +
            "join wps.wizardPageDefinition wpd " +
            "join wps.category c " +
            "join c.wizard w " +
            "where wpd.reviewDevice = :formId ";

      String wizardReflection = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "w.reflectionDevice, " +
      		   "w.siteId, " +
      		   "'" + refl_type + "', " +
      		   "concat('" + wizardNameText + "', w.name)) " +
            "from Wizard w " + 
            "where w.reflectionDevice = :formId ";
      String wizardEvalation = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "w.evaluationDevice, " +
      		   "w.siteId, " +
      		   "'" + eval_type + "', " +
      		   "concat('" + wizardNameText + "', w.name)) " +
            "from Wizard w " +
            "where w.evaluationDevice = :formId ";
      String wizardReview = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "w.reviewDevice, " +
      		   "w.siteId, " +
      		   "'" + review_type + "', " +
      		   "concat('" + wizardNameText + "', w.name)) " +
            "from Wizard w " +
            "where w.reviewDevice = :formId";
      
      Collection objectsWithForms = getHibernateTemplate().findByNamedParam(matrixReflection, "formId", formId);
      objectsWithForms.addAll(getHibernateTemplate().findByNamedParam(matrixEval, "formId", formId));
      objectsWithForms.addAll(getHibernateTemplate().findByNamedParam(matrixReview, "formId", formId));
      objectsWithForms.addAll(getHibernateTemplate().findByNamedParam(scaffoldingReflection, "formId", formId));
      objectsWithForms.addAll(getHibernateTemplate().findByNamedParam(scaffoldingEval, "formId", formId));
      objectsWithForms.addAll(getHibernateTemplate().findByNamedParam(scaffoldingReview, "formId", formId));
      objectsWithForms.addAll(getHibernateTemplate().findByNamedParam(wizardPageReflection, "formId", formId));
      objectsWithForms.addAll(getHibernateTemplate().findByNamedParam(wizardPageEval, "formId", formId));
      objectsWithForms.addAll(getHibernateTemplate().findByNamedParam(wizardPageReview, "formId", formId));
      objectsWithForms.addAll(getHibernateTemplate().findByNamedParam(wizardReflection, "formId", formId));
      objectsWithForms.addAll(getHibernateTemplate().findByNamedParam(wizardEvalation, "formId", formId));
      objectsWithForms.addAll(getHibernateTemplate().findByNamedParam(wizardReview, "formId", formId));
      
		results.addAll(objectsWithForms);
      
      String cellQueryString = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "af, " +
      		   "sc.scaffolding.worksiteId, " +
      		   "'" + page_form + "', " +
      		   "concat('" + cellNameText + "', sc.wizardPageDefinition.title), " +
               "concat('" + matrixNameText + "', sc.scaffolding.title)) " +
      		"from ScaffoldingCell sc " +
      		"left join sc.wizardPageDefinition.additionalForms as af " +
      		"where af = :formId";
      Collection cellAdditionalForms = getHibernateTemplate().findByNamedParam(
            cellQueryString, "formId", formId.getValue());
      results.addAll(cellAdditionalForms);
      
      String wizPageQueryString = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "af, " +
      		   "w.siteId, " +
      		   "'" + page_form + "', " +
      		   "concat('" + wizPageNameText + "', wpd.title), " +
      		   "concat('" + wizardNameText + "', w.name)) " +
      		"From WizardPageSequence wps " +
            "join wps.wizardPageDefinition wpd " +
            "join wps.category c " +
            "join c.wizard w " +
            "join wpd.additionalForms af " +
            "where af = :formId";
      Collection wizPageAdditionalForms = getHibernateTemplate().findByNamedParam(
            wizPageQueryString, "formId", formId.getValue());
      results.addAll(wizPageAdditionalForms);

      String scaffoldingQueryString = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
	      "af, " +
	      "s.worksiteId, " +
	      "'" + defaultTxt + page_form + "', " +
	      "concat('" + matrixNameText + "', s.title), " +
	      "'') " +
	      "from Scaffolding s " +
	      "left join s.additionalForms as af " +
	      "where af = :formId";
      Collection scaffoldingAdditionalForms = getHibernateTemplate().findByNamedParam(
    		  scaffoldingQueryString, "formId", formId.getValue());
      results.addAll(scaffoldingAdditionalForms);


      return results;
   }
   
   public List<WizardPageDefinition> getWizardPageDefs(List<Id> ids)
   {
	   if (ids.size() > 0) {
		   String[] paramNames = new String[] {"ids"};
		      Object[] params = new Object[]{ids};
		   List<WizardPageDefinition> pageDefs = getHibernateTemplate().findByNamedParam("from WizardPageDefinition wpd where wpd.id in ( :ids )",
		            paramNames, params);
		   return pageDefs;
	   }
	   return new ArrayList<WizardPageDefinition>();
   }

   public List<ScaffoldingCell> getScaffoldingCells(List<Id> ids)
   {
	   if (ids.size() > 0) {
		   String[] paramNames = new String[] {"ids"};
		      Object[] params = new Object[]{ids};
		   List<ScaffoldingCell> sCells = getHibernateTemplate().findByNamedParam("from ScaffoldingCell sCell where sCell.wizardPageDefinition.id in ( :ids )",
		            paramNames, params);
		   return sCells;
	   }
	   return new ArrayList<ScaffoldingCell>();
   }
   
   public WizardPageDefinition getWizardPageDefinition(Id pageDefId) {
	   WizardPageDefinition wizPageDef = (WizardPageDefinition)this.getHibernateTemplate().load(WizardPageDefinition.class, pageDefId);
	   return wizPageDef;
   }
   
   public EventService getEventService() {
	   return eventService;
   }

   public void setEventService(EventService eventService) {
	   this.eventService = eventService;
   }
	
	/**
	 * finds the list of evaluators/roles of the site id passed and checks against the current user.
	 * returns true if user or role matches, otherwise false
	 * 
	 * @param id
	 * @param worksiteId
	 * @param function
	 * @return
	 */
	public boolean hasPermission(Id id, Id worksiteId, String function){
		if(getSecurityService().isSuperUser(getAuthnManager().getAgent().getId().getValue()))
			return true;
		
		if(id == null)
			return false;
		
		Site site = null;
		try {
			site = SiteService.getSite(worksiteId.getValue());
		} catch (IdUnusedException e) {
			e.printStackTrace();
		}

		if(site == null)
			return false;
		
		Role userRole = site.getUserRole(getAuthnManager().getAgent().getId().getValue());
		
		List evaluators = getAuthzManager().getAuthorizations(null,
				function, id);

		for (Iterator iter = evaluators.iterator(); iter.hasNext();) {
			Authorization az = (Authorization) iter.next();
			Agent agent = az.getAgent();
			if (agent.isRole()) {
				if(userRole != null){
					// see if the user's role matches with the evaluation role: 
					//(fyi, display name returns the role if the agent is a role)
					if (userRole.getId().compareTo(
							agent.getDisplayName()) == 0)
						return true;
				}
			} 
			else if (agent.getId() != null) {
				// see if the user matches with the evaluator user
				if (getAuthnManager().getAgent().getId().getValue().compareTo(
						agent.getId().toString()) == 0)
					return true;
			}
		}

		return false;
	}

	public WizardPageDefinitionEntity createWizardPageDefinitionEntity(
			WizardPageDefinition wpd, String parentTitle)
	{
		return new WizardPageDefinitionEntityImpl(wpd, parentTitle);
	}

	public Map<Integer, Integer> getReviewCountListByType(Id pageDefId) {
		Object[] params = new Object[] { pageDefId };
		List results = getHibernateTemplate()
				.find(
						"select r.type, count(*) from Review r, WizardPage wp where wp.id = r.parent and wp.pageDefinition.id=? GROUP BY r.type",
						params);
		
		Map resultMap = new HashMap(results.size());
		
		for (Iterator i = results.iterator(); i.hasNext();) {
			Object[] rs = (Object[]) i.next();
			Integer type = (Integer)rs[0];
			Integer count = (Integer)rs[1];
			resultMap.put(type, count);
		}
		
		
		return resultMap;
		
	}


	public boolean isScaffoldingUsed(Scaffolding scaffolding) {

		return getFormCountByScaffolding(scaffolding.getId()) > 0
				|| getAttachmentCountByScaffolding(scaffolding.getId()) > 0
				|| getReviewCountByScaffolding(scaffolding.getId()) > 0;
	}
	
	public int getFormCountByScaffolding(Id scaffoldingId) {
		Object[] params = new Object[] { scaffoldingId };
		return (Integer) getHibernateTemplate()
				.find(
						"select count(*) from WizardPage wp, ScaffoldingCell sc join wp.pageForms where wp.pageDefinition.id=sc.wizardPageDefinition.id and sc.scaffolding.id=?",
						params).get(0);
		//		
		// SELECT count(*) FROM osp_scaffolding s inner join
		// osp_scaffolding_cell sc on sc.scaffolding_id = s.id
		// join osp_wizard_page wp on wp.wiz_page_def_id=sc.wiz_page_def_id
		// join osp_wiz_page_form wpf on wpf.page_id=wp.id
		// where s.id ='AED66AB3B9AE98218C808C207A08EB63'
		// GO
	}

	public int getAttachmentCountByScaffolding(Id scaffoldingId) {
		Object[] params = new Object[] { scaffoldingId };
		return (Integer) getHibernateTemplate()
				.find(
						"select count(*) from WizardPage wp, ScaffoldingCell sc join wp.attachments where wp.pageDefinition.id=sc.wizardPageDefinition.id and sc.scaffolding.id=?",
						params).get(0);

		// SELECT * FROM osp_scaffolding s inner join osp_scaffolding_cell sc on
		// sc.scaffolding_id = s.id
		// join osp_wizard_page wp on wp.wiz_page_def_id=sc.wiz_page_def_id
		// join osp_wiz_page_attachment wpa on wpa.page_id=wp.id
		// where s.id ='AED66AB3B9AE98218C808C207A08EB63'
		// GO
	}

	public int getReviewCountByScaffolding(Id scaffoldingId) {
		Object[] params = new Object[] { scaffoldingId };
		return (Integer) getHibernateTemplate()
				.find(
						"select count(*) from WizardPage wp, Review r, ScaffoldingCell sc where wp.id = r.parent and wp.pageDefinition.id=sc.wizardPageDefinition.id and sc.scaffolding.id=?",
						params).get(0);
		// SELECT count(*) FROM osp_scaffolding s inner join
		// osp_scaffolding_cell sc on sc.scaffolding_id = s.id
		// join osp_wizard_page wp on wp.wiz_page_def_id=sc.wiz_page_def_id
		// join osp_review r on r.parent_id=wp.id
		// where s.id ='AED66AB3B9AE98218C808C207A08EB63'
		//		GO
	}

	
	public Map<Id, Integer> getSubmissionCountByScaffolding(List<Scaffolding> scaffolding) {
		/*
		 * SELECT sc.id, count(sc.id) FROM osp_matrix_cell mc
			join osp_wizard_page wp on mc.wizard_page_id = wp.id
			join osp_scaffolding_cell sc on mc.scaffolding_cell_id = sc.id
			join osp_scaffolding s on sc.scaffolding_id = s.id
			where wp.status in ('PENDING', 'COMPLETE')
			and s.id in ()
			group by sc.id
		 */
		List results = new ArrayList();
		
		if (scaffolding.size() > 0) {
			String[] paramNames = new String[] { "scaffolding" };
			Object[] params = new Object[] { scaffolding };
			results = getHibernateTemplate().findByNamedParam(
					"select sc.id, count(sc.id) from Cell mc join mc.wizardPage wp join mc.scaffoldingCell sc where sc.scaffolding in (:scaffolding) AND wp.status in ('PENDING', 'COMPLETE') GROUP BY sc.id",
					paramNames,
					params);

		}
		
		Map resultMap = new HashMap(results.size());
		
		for (Iterator i = results.iterator(); i.hasNext();) {
			Object[] rs = (Object[]) i.next();
			Id sc_id = (Id)rs[0];
			Integer count = (Integer)rs[1];
			resultMap.put(sc_id, count);
		}
		
		
		return resultMap;
		
		
	}
	
	
	  
	public boolean hasGroups(String worksiteId) {
		try {
			Site site = SiteService.getSite(worksiteId);
			return site.hasGroups();
		} catch (IdUnusedException e) {
			logger.error("", e);
		}
		return false;
	}


	public Set getGroupList(String worksiteId, boolean allowAllGroups) {
		try {
			Site site = SiteService.getSite(worksiteId);
			return getGroupList(site, allowAllGroups);
		} catch (IdUnusedException e) {
			logger.error("", e);
		}
		return new HashSet();    	
	}

	public Set getGroupList(Site site, boolean allowAllGroups) {
		Set groupSet = new HashSet();
		Collection siteGroups = null;

		boolean includeSections = ServerConfigurationService.getBoolean(WizardMatrixConstants.PROP_GROUPS_INCLUDE_SECTIONS, false);
      
		if (site.hasGroups()) {
			String currentUser = SessionManager.getCurrentSessionUserId();
			if (allowAllGroups) {
				siteGroups = site.getGroups();
			}
			else {
				siteGroups = site.getGroupsWithMember(currentUser);
			}
      
			// Only add worksite groups (e.g. not section groups)
			for (Iterator it = siteGroups.iterator(); it.hasNext(); ) {
				Group group = (Group)it.next();
				if ( includeSections || group.getProperties().getProperty(Group.GROUP_PROP_WSETUP_CREATED) != null )
					groupSet.add(group);
			}
		}
      
		return groupSet;
	}

	public Set getUserList(String worksiteId, String filterGroupId, boolean allowAllGroups, List<Group> groups) {
		Set members = new HashSet();
		Set users = new HashSet();

		try {
			Site site = SiteService.getSite(worksiteId);
			if (site.hasGroups()) {
				String currentUser = SessionManager.getCurrentSessionUserId();

				if (allowAllGroups && (filterGroupId == null || filterGroupId.equals(""))) {
					members.addAll(site.getMembers());
				}
				else {
					for (Iterator iter = groups.iterator(); iter.hasNext();) {
						Group group = (Group) iter.next();
						// TODO: Determine if Java loop invariants are optimized out
						if (filterGroupId == null || "".equals(filterGroupId)
								|| filterGroupId.equals(group.getId())) {
							members.addAll(group.getMembers());
						}
					}
				}
			} else {
				members.addAll(site.getMembers());
			}

			for (Iterator memb = members.iterator(); memb.hasNext();) {
				try {
					Member member = (Member) memb.next();
					users.add(UserDirectoryService.getUser(member.getUserId()));
				} catch (UserNotDefinedException e) {
					logger.warn("Unable to find user: " + e.getId() + " "
							+ e.toString());
				}
			}
		} catch (IdUnusedException e) {
			logger.error("", e);
		}
		return users;
	}
	
	public List getSelectedUsers(ObjectWithWorkflow oWW, String function) {
		List returnList = new ArrayList();
		Id id = oWW.getId() == null ? oWW.getNewId() : oWW.getId();
		if (id != null) {
			List evaluators = getAuthzManager().getAuthorizations(null,
					function, id);

			for (Iterator iter = evaluators.iterator(); iter.hasNext();) {
				Authorization az = (Authorization) iter.next();
				Agent agent = az.getAgent();
				if (agent == null || agent.getEid() == null)
					continue;
				String userId = agent.getEid().getValue();
				if (agent.isRole()) {
					returnList.add(MessageFormat.format(messages
							.getString("decorated_role_format"),
							new Object[] { agent.getDisplayName() }));
				} else {
					returnList.add(MessageFormat.format(messages
							.getString("decorated_user_format"), new Object[] {
						agent.getDisplayName(), userId }));
				}
			}
		}
		return returnList;
	}
	
	public void notifyAudience(WizardPage wizPage, Id reviewObjectId, boolean groupAware, HashMap<String, String> sendExtraEmails, String emailMessage, String parentTitle, String function){

    	String url;
    	String emailBody = "";
    	String subject = "";
    	String emailBodyAnon = "";
    	String subjectAnon = "";
    	User user = UserDirectoryService.getCurrentUser();
    	boolean isOwner = wizPage.getOwner().getId().getValue().equals(user.getEid());
    	Boolean isMatrix = wizPage.getPageDefinition().getType().equals(wizPage.getPageDefinition().WPD_MATRIX_TYPE);
    	String uCasePageType = isMatrix ? messages.getString("email_uppercase_matrixCell") : messages.getString("email_uppercase_wizardPage");
    	String pageType = isMatrix ? messages.getString("email_matrixCell") : messages.getString("email_wizardPage");
    	String pageTool = isMatrix ? messages.getString("email_matrix") : messages.getString("email_wizard");
    	String toolTitle = isMatrix ? messages.getString("email_matrices") : messages.getString("email_wizards");
    	boolean evaluation = MatrixFunctionConstants.EVALUATE_MATRIX.equals(function);
    	String notificationId = "";
    	
    	//String id = wizPage.getId()!=null ? wizPage.getId().getValue() : wizPage.getNewId().getValue();
    	
    	String userNameHeader = user.getDisplayName();
    	String userNameHeaderAnon = messages.getString("anon_user_header");
		String userName = user.getDisplayName();
		String userNameAnon = messages.getString("anon_user");
    	
    	if(evaluation){
    		subject = messages.getFormattedMessage("matrixEvaluationSubject", new Object[]{uCasePageType, userNameHeader});
    		subjectAnon = messages.getFormattedMessage("matrixEvaluationSubject", new Object[]{uCasePageType, userNameHeaderAnon});
    	}else{
    		subject = messages.getFormattedMessage("matrixFeedbackSubject", new Object[]{userNameHeader});
    		subjectAnon = messages.getFormattedMessage("matrixFeedbackSubject", new Object[]{userNameHeaderAnon});
    	}

    	ToolConfiguration toolConfig;
    	try {
    		Site wpSite = SiteService.getSite(wizPage.getPageDefinition().getSiteId());
    		String placement;
    		if(isMatrix){
    			toolConfig = wpSite.getToolForCommonId("osp.matrix");
    			placement = toolConfig.getId();
    			notificationId = getMatrixPreferencesConfig().getType();
    		}else{
    			toolConfig = wpSite.getToolForCommonId("osp.wizard");
    			placement = toolConfig.getId();
    			notificationId = getWizardPreferencesConfig().getType();
    		}
    		url =	ServerConfigurationService.getServerUrl() + 
    		"/direct/matrixcell/" + wizPage.getId().getValue() + "/" + placement + "/viewCell.osp";


    		if(evaluation){
    			
    			emailBody = messages.getFormattedMessage(
    					"matrixEvaluationBody", new Object[] {userName, pageType, wizPage.getPageDefinition().getTitle(),
    							pageType, pageTool, parentTitle, wpSite.getTitle(), pageType, wpSite.getTitle(), toolTitle, url, pageType, pageType, pageType});
    			emailBodyAnon = messages.getFormattedMessage(
    					"matrixEvaluationBody", new Object[] {userNameAnon, pageType, wizPage.getPageDefinition().getTitle(),
    							pageType, pageTool, parentTitle, wpSite.getTitle(), pageType, wpSite.getTitle(), toolTitle, url, pageType, pageType, pageType});
    		}else{
    			emailBody = messages.getFormattedMessage(
    					"matrixFeedbackBody", new Object[] {userName, wizPage.getPageDefinition().getTitle(),
    							parentTitle, wpSite.getTitle(), url}); 
    			emailBodyAnon = messages.getFormattedMessage(
    					"matrixFeedbackBody", new Object[] {userNameAnon, wizPage.getPageDefinition().getTitle(),
    							parentTitle, wpSite.getTitle(), url}); 			
    		}
    		
    		if(emailMessage != null && !"".equals(emailMessage)){
				emailBody += messages.getFormattedMessage("matrixFeedbackBodyPersonalMessage", new Object[]{userName, emailMessage});
				emailBodyAnon += messages.getFormattedMessage("matrixFeedbackBodyPersonalMessage", new Object[]{userNameAnon, emailMessage});
			}
    		
    		try {

    			String from = ServerConfigurationService.getString("setup.request", 
    					"postmaster@".concat(ServerConfigurationService.getServerName()));

    			//Username, Email
    			HashMap<String, String> emails = new HashMap<String, String>();
    			if(function != null && reviewObjectId != null){
    				// add email addresses to the list based on function and reviewObjectId
    				
    				//get a list of users who have view all groups permission
    				List<User> usersAllowedToViewAllGroupsForObject = null;
    				if(isMatrix){
    					String reference = getScaffoldingCellByWizardPageDef(wizPage.getPageDefinition().getId()).getScaffolding().getReference();

    					usersAllowedToViewAllGroupsForObject = getSecurityService().unlockUsers(MatrixFunctionConstants.VIEW_ALL_GROUPS, reference);
    				}
    				
    				emails = getAuthzEmails(reviewObjectId, wpSite, groupAware, function, usersAllowedToViewAllGroupsForObject);
    			}
    			
    			//add any extra email addresses:
    			if(sendExtraEmails != null && sendExtraEmails.keySet() != null){
    				if(emails != null && emails.entrySet() != null){
    					for (Iterator iterator = sendExtraEmails.entrySet().iterator(); iterator.hasNext();) {    						
    						Entry entry = (Entry) iterator.next();
    						if(!emails.containsValue(entry.getValue()))
    							emails.put(entry.getKey().toString(), entry.getValue().toString());
    					}
    				}
    			}


    			String scaffoldRef = "";
    			if(isMatrix){
    				scaffoldRef = getScaffoldingCellByWizardPageDef(wizPage.getPageDefinition().getId()).getScaffolding().getReference();  				   		
    			}
    			
    			//send the emails
    			sendEmailNotifications(emails, isMatrix, from, subject, emailBody, subjectAnon, emailBodyAnon, scaffoldRef, isOwner, evaluation, wpSite.getId(), notificationId);
    			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	} catch (IdUnusedException e1) {
    		e1.printStackTrace();
    	}
	}
	
	public void sendEmailNotifications(HashMap<String, String> emails, boolean isMatrix, 
			String from, String subject, String emailBody, String subjectAnon, String emailBodyAnon, 
			String scaffoldRef, boolean isOwner, boolean isEvaluation, String siteId, String notificationId){
		Id scaffoldId = getIdManager().getId(scaffoldRef);
		//Send the emails
		if(emails != null && emails.keySet() != null){
			for (String userId : emails.keySet()) {
				String email = emails.get(userId);
				if (validateEmail(email)) {
					boolean canViewUsername = true;
					if(isMatrix && !isOwner && isEvaluation){
						//check if the reviewer/evaluator has privilage to view the uses name 
						canViewUsername = getAuthzManager().isAuthorized(getAgentManager().getAgent(userId), MatrixFunctionConstants.ACCESS_USERLIST, scaffoldId);
					}
					int userPref = getNotificationOption(userId, notificationId, siteId);
					
					if (userPref == NotificationService.PREF_DIGEST) {
						logger.debug("sendEmailNotifications() - Sending digest to " + email);
						if(canViewUsername){
							DigestService.digest(userId, subject, emailBody);
						}else{
							DigestService.digest(userId, subjectAnon, emailBodyAnon);
						}
					}
					else if (userPref == NotificationService.PREF_IMMEDIATE) {
						logger.debug("sendEmailNotifications() - Sending message to " + email);
						if(canViewUsername){
							EmailService.send(from, email,
									subject, emailBody, null, null, null);
						}else{
							EmailService.send(from, email,
									subjectAnon, emailBodyAnon, null, null, null);
						}
					}
					else {
						logger.debug("sendEmailNotifications() - Sending nothing to " + email);
					}
				}
			}
		}		
	}
	

	public int getNotificationOption(String userId, String notificationId, String siteId)
	{
		String priStr = Integer.toString(NotificationService.NOTI_OPTIONAL);

		Preferences prefs = getPreferencesService().getPreferences(userId);

		// get the user's site override preference for this notification
		ResourceProperties props = prefs.getProperties(NotificationService.PREFS_TYPE + notificationId + NotificationService.NOTI_OVERRIDE_EXTENSION);
		try
		{
			int option = (int) props.getLongProperty(siteId);
			if (option != NotificationService.PREF_NONE) return option;
		}
		catch (Throwable ignore)
		{
		}

		// get the user's preference for this notification
		props = prefs.getProperties(NotificationService.PREFS_TYPE + notificationId);
		try
		{
			int option = (int) props.getLongProperty(priStr);
			if (option != NotificationService.PREF_NONE) return option;
		}
		catch (Throwable ignore)
		{
		}
		
		// nothing defined...
		return NotificationService.PREF_IMMEDIATE;
	}
	
	/**
	 * 
	 * Returns a list of email addresses to based off the parameters:
	 * 
	 * 
	 * @param reviewObjectId	This is the reference ID to which the authorization function you want to be checked against (ie. site, scaffolding, ect)  Can't be null.
	 * @param site				The site you are concerned with.  Can't be null.
	 * @param groupAware		This will determine if you want to filter the emails based off group access.
	 * @param function			This is the function (review, evaluate, ect) that you are concerned with	Can't be null.
	 * @param usersAllowedToViewAllGroupsForObject	A list of users who have the ability to view all groups.  These users will be added in the list if they have the function permission
	 * 												but aren't in the current users group.  Null value is ok.
	 * @return
	 */
	protected HashMap<String, String> getAuthzEmails(Id reviewObjectId, Site site, boolean groupAware, String function, List<User> usersAllowedToViewAllGroupsForObject){
		
		HashMap<String, String> returnMap = new HashMap<String, String>();
		
		if(reviewObjectId == null || site == null || function == null){
			logger.warn("Invalid null argument passed to HibernateMatrixManager.getAuthzEmails");
			return returnMap;
		}
		
		//no need to look for groups if the site doesn't have groups
		groupAware = groupAware && site.hasGroups();
				
		List evaluators = getAuthzManager().getAuthorizations(null, function, reviewObjectId);
		Set<User> usersInGroup = new HashSet<User>();
		if(groupAware){
			// allow all groups needs to be set to false for getUserList (even if user has the permission) b/c this is creating a list of users who can 
			// evaluate/review/ect for this user, not who the current user can evaluate/review/ect.	
			boolean allowAllGroups = false;
			//filterGroupId is null so it will return all users for all groups
			String filterGroupId = null;		
			usersInGroup = getUserList(site.getId(),
					filterGroupId,
					allowAllGroups,
					new ArrayList<Group>(getGroupList(site,	allowAllGroups)));


			if(usersAllowedToViewAllGroupsForObject != null){
				//add all users who have the View All Groups permission to the group user list
				for (Iterator iterator = usersAllowedToViewAllGroupsForObject.iterator(); iterator.hasNext();) {
					User user = (User) iterator.next();
					if(!usersInGroup.contains(user)){
						usersInGroup.add(user);
					}
				}
			}
		}


		User user = null;
		for (Iterator iterator = evaluators.iterator(); iterator.hasNext();) {
			Authorization az = (Authorization) iterator.next();
			Agent agent = az.getAgent();

			if (agent.isRole()) {
				for (String userId : (Set<String>)site.getUsersHasRole(agent.getDisplayName())) {
					try {
						user = UserDirectoryService.getUser(userId);

						if(!groupAware || (groupAware && usersInGroup.contains(user))) {
							String email;

							email = user.getEmail();
							if (validateEmail(email)
									&& !returnMap.containsValue(email)) {
								returnMap.put(user.getId(), email);
							}
						}
					} catch (UserNotDefinedException e) {
						e.printStackTrace();

					}
				}
			} else {
				try {
					user = UserDirectoryService.getUserByEid(
							agent.getEid().toString());

					if(!groupAware || (groupAware && usersInGroup.contains(user))){
						String email = user.getEmail();
						if (validateEmail(email) && !returnMap.containsValue(email)) {
							returnMap.put(user.getId(), email);
						}
					}
				} catch (UserNotDefinedException e) {
					e.printStackTrace();
				}
			}
		}
		
		return returnMap;		
	}
	
	protected boolean validateEmail(String displayName)
	{
		if (!emailPattern.matcher(displayName).matches()) {
			return false;
		}

		return true;
	}
	
	public Cell createCellWrapper(WizardPage page) {
		Cell cell = new Cell();
		cell.setWizardPage(page);
		if (page.getId() == null) {
			cell.setId(page.getNewId());
		} else {
			cell.setId(page.getId());
		}

		WizardPageDefinition pageDef = page.getPageDefinition();

		boolean defaults = isEnableDafaultMatrixOptions();
		ScaffoldingCell cellDef = new ScaffoldingCell(defaults, defaults, defaults, defaults, defaults, defaults, defaults);
		cellDef.setWizardPageDefinition(pageDef);
		if (pageDef.getId() == null) {
			cellDef.setId(pageDef.getNewId());
		} else {
			cellDef.setId(pageDef.getId());
		}

		cell.setScaffoldingCell(cellDef);
		return cell;
	}
	
	public Set<TaggableItem> getTaggableItems(TaggableItem item, String criteriaRef, String cellOwner) {
		Set<DecoratedTaggableItem> taggableItems = getTaggableItems(item, criteriaRef, cellOwner, false);
		Set<TaggableItem> tmpSet = new HashSet<TaggableItem>();
		for (DecoratedTaggableItem decoItem : taggableItems) {
			tmpSet.addAll(decoItem.getTaggableItems());
		}
		return tmpSet;
	}
	
	public Set<DecoratedTaggableItem> getDecoratedTaggableItems(TaggableItem item, String criteriaRef, String cellOwner) {
		return getTaggableItems(item, criteriaRef, cellOwner, true);
	}
	
	private Set<DecoratedTaggableItem> getTaggableItems(TaggableItem item, String criteriaRef, String cellOwner, boolean decorate) {
		Set<TaggableActivity> activities = new HashSet<TaggableActivity>();
		Map<String, DecoratedTaggableItem> decoTaggableItems = new HashMap<String, DecoratedTaggableItem>();
		Set<DecoratedTaggableItem> allDecoratedTaggableItems = new HashSet<DecoratedTaggableItem>();
		List<Link> links;
		try
		{
			ToolSession toolSession = SessionManager.getCurrentToolSession();
			List<DecoratedTaggingProvider> providers;
			if(toolSession == null){
				providers = getDecoratedProviders(item.getActivity());
			}else{
				providers = (List) toolSession.getAttribute(HibernateMatrixManagerImpl.PROVIDERS_PARAM);
				if (providers == null) {
					providers = getDecoratedProviders(item.getActivity());
					toolSession.setAttribute(HibernateMatrixManagerImpl.PROVIDERS_PARAM, providers);
				}
			}
			
			links = getLinkManager().getLinks(criteriaRef, true);
			//TODO: Make sure it's always okay to ignore the provider
			for (DecoratedTaggingProvider provider : providers) {
				Iterator<Link> linkIter = links.iterator();
				while (linkIter.hasNext()) { 
					Link link = linkIter.next(); 

					TaggableActivityProducer producer = getTaggingManager().findProducerByRef(link.getActivityRef());
					SecurityAdvisor myAdv = null;
					if (producer.getItemPermissionOverride() != null) {
						myAdv = new SimpleSecurityAdvisor(
								SessionManager.getCurrentSessionUserId(), 
								producer.getItemPermissionOverride());
						getSecurityService().pushAdvisor(myAdv);
					}
					TaggableActivity activity = getTaggingManager().getActivity(link.getActivityRef(), provider.getProvider());
					if (activity != null) {
						List<TaggableItem> items = producer.getItems(activity, cellOwner, provider.getProvider(), true, criteriaRef);
						
						for (TaggableItem tagItem : items) {
							DecoratedTaggableItem curItem = decoTaggableItems.get(tagItem.getTypeName());
							if (curItem == null) {
								curItem = new DecoratedTaggableItemImpl(tagItem.getTypeName());
								allDecoratedTaggableItems.add(curItem);
							}
							curItem.addTaggableItem(tagItem);
							decoTaggableItems.put(tagItem.getTypeName(), curItem);							
						}						
						
						activities.add(activity);
						
					}
					else {
						logger.warn("Link with ref " + link.getActivityRef() + " no longer exists.  Removing link.");
						getLinkManager().removeLink(link);
						linkIter.remove();
					}
					if (producer.getItemPermissionOverride() != null && myAdv != null) {
						getSecurityService().popAdvisor(myAdv);
					}
				}
			}
		}
		catch (PermissionException pe)
		{
			logger.warn("unable to get links for criteriaRef " + criteriaRef, pe);
		}
		return allDecoratedTaggableItems;
		
	}
	
	public List<DecoratedTaggingProvider> getDecoratedProviders(
			TaggableActivity activity) {
		List<DecoratedTaggingProvider> providers = new ArrayList<DecoratedTaggingProvider>();
		for (TaggingProvider provider : getTaggingManager().getProviders()) {
			providers.add(new DecoratedTaggingProviderImpl(activity, provider));
		}
		return providers;
	}

	/**
	 * A simple SecurityAdviser that can be used to override permissions for one user for one function.
	 */
	protected class SimpleSecurityAdvisor implements SecurityAdvisor
	{
		protected String m_userId;
		protected String m_function;

		public SimpleSecurityAdvisor(String userId, String function)
		{
			m_userId = userId;
			m_function = function;
		}

		public SecurityAdvice isAllowed(String userId, String function, String reference)
		{
			SecurityAdvice rv = SecurityAdvice.PASS;
			if (m_userId.equals(userId) && m_function.equals(function))
			{
				rv = SecurityAdvice.ALLOWED;
			}
			return rv;
		}
	}


	public boolean isEnableDafaultMatrixOptions() {
		return enableDafaultMatrixOptions;
	}

	public void setEnableDafaultMatrixOptions(boolean enableDafaultMatrixOptions) {
		this.enableDafaultMatrixOptions = enableDafaultMatrixOptions;
	}

	public TaggingManager getTaggingManager() {
		return taggingManager;
	}

	public void setTaggingManager(TaggingManager taggingManager) {
		this.taggingManager = taggingManager;
	}

	public LinkManager getLinkManager() {
		return linkManager;
	}

	public void setLinkManager(LinkManager linkManager) {
		this.linkManager = linkManager;
	}

	public boolean canAccessAllMatrixCells(Id scaffoldingId){
		Scaffolding scaffold = getScaffolding(scaffoldingId);
		if(scaffold == null)
	           throw new NullPointerException("The scaffolding was not found: " + scaffoldingId.getValue());
		
		return canAccessAllMatrixCellsHelper(scaffold);
	}
	
	public boolean canAccessAllMatrixCellsHelper(Scaffolding scaffold){
		if(scaffold.getOwner().getId().getValue().equals(UserDirectoryService.getCurrentUser().getId()))
        	return true;

        if (getAuthzManager().isAuthorized(MatrixFunctionConstants.ACCESS_ALL_CELLS, getIdManager().getId(scaffold.getReference()))) {
        	return true;
        }
        
        return false;
	}

	public boolean canAccessScaffoldCellByScaffoldingCellId(Id scaffoldingCellId){
		ScaffoldingCell sCell = getScaffoldingCell(scaffoldingCellId);

		if(sCell == null)
			throw new NullPointerException("The cell was not found.  ScaffoldingCell id for cell: " + scaffoldingCellId.getValue());
		
		return canAccessMatrixScaffoldCellHelper(sCell);
	}
	
	public boolean canAccessScaffoldCellByWizPageDefId(Id wizPageDefId){
		ScaffoldingCell sCell = getScaffoldingCellByWizardPageDef(wizPageDefId);

		if(sCell == null)
			throw new NullPointerException("The cell was not found.  Wizard Page Def for cell: " + wizPageDefId.getValue());
		
		return canAccessMatrixScaffoldCellHelper(sCell);
	}
	
	public boolean canAccessMatrixScaffoldCellHelper(ScaffoldingCell sCell){
		if(canAccessAllMatrixCellsHelper(sCell.getScaffolding())){
			return true;
		}

		if(hasPermission(sCell.isDefaultEvaluators() ? sCell.getScaffolding().getId() : sCell.getWizardPageDefinition().getId(),
				sCell.getScaffolding().getWorksiteId(),
				MatrixFunctionConstants.EVALUATE_MATRIX)){
			return true;
		}

		if(hasPermission(sCell.isDefaultReviewers() ? sCell.getScaffolding().getId() : sCell.getWizardPageDefinition().getId(),
				sCell.getScaffolding().getWorksiteId(),
				MatrixFunctionConstants.REVIEW_MATRIX)){
			return true;
		}
		
		
		return false;
		
	}
	
	public boolean canAccessMatrixCell(Cell cell){
		if(cell == null)
			throw new NullPointerException("The cell passed was null");

		//is owner of cell?
		if(cell.getWizardPage().getOwner().getId()
				.getValue().equals(SessionManager.getCurrentSessionUserId())){
			return true;
		}
		
		//canAccessMatrixScaffoldCellHelper will also check canAccessAnyMatrixCell
		if(canAccessMatrixScaffoldCellHelper(cell.getScaffoldingCell())){
			return true;
		}

		boolean allowParticipantFeedback = cell.getScaffoldingCell()
		.isDefaultReviewers() ? cell.getScaffoldingCell()
				.getScaffolding().isAllowRequestFeedback() : cell
				.getScaffoldingCell().getWizardPageDefinition()
				.isAllowRequestFeedback();

		if(allowParticipantFeedback){
			if(hasPermission(cell.getWizardPage().getId(), cell
					.getScaffoldingCell().getScaffolding()
					.getWorksiteId(),
					MatrixFunctionConstants.FEEDBACK_MATRIX)){
				return true;
			}
		}

		return false;	
	}

	public boolean canUserAccessWizardPageAndLinkedArtifcact(String siteId, String pageId, String linkedArtifactId){
		boolean canAccessCell = false;
		boolean isPageLinked = false;
		
		if(canAccessMatrixCell(getCellFromPage(idManager.getId(pageId)))){
			canAccessCell = true;
		}
		
		if(canAccessCell){
			WizardPage wizPage = getWizardPage(idManager.getId(pageId));
			String wizPageOwnerId = wizPage.getOwner().getId().getValue();
			String linkedArtifactReference = linkedArtifactId + "@" + wizPageOwnerId;
			TaggableItem item = WizardActivityProducer.getItem(wizPage);
			Set<DecoratedTaggableItem> taggableItems = getDecoratedTaggableItems(item, wizPage.getPageDefinition().getReference(), wizPage.getOwner().getId().getValue());
			for (DecoratedTaggableItem decoratedTaggableItem : taggableItems) {
				for (TaggableItem taggableItem : decoratedTaggableItem.getTaggableItems()) {
					if(linkedArtifactReference.equals(taggableItem.getReference()) || linkedArtifactId.equals(taggableItem.getReference())){
						isPageLinked = true;
						break;
					}
				}
				if(isPageLinked)
					break;		
			}
		}
		
		return canAccessCell && isPageLinked;
	}

	public PreferencesService getPreferencesService() {
		return preferencesService;
	}

	public void setPreferencesService(PreferencesService preferencesService) {
		this.preferencesService = preferencesService;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void setMatrixPreferencesConfig(UserNotificationPreferencesRegistration matrixPreferencesConfig) {
		this.matrixPreferencesConfig = matrixPreferencesConfig;
	}

	public UserNotificationPreferencesRegistration getMatrixPreferencesConfig() {
		return matrixPreferencesConfig;
	}

	public void setWizardPreferencesConfig(UserNotificationPreferencesRegistration wizardPreferencesConfig) {
		this.wizardPreferencesConfig = wizardPreferencesConfig;
	}

	public UserNotificationPreferencesRegistration getWizardPreferencesConfig() {
		return wizardPreferencesConfig;
	}

	public Map getConfirmFlagsForScaffolding(Scaffolding scaffolding){
		Map model = new HashMap();
		
		//if scaffolding is published, warn user;    
		if (scaffolding.isPublished()){
			model.put(CONFIRM_PUBLISHED_FLAG, true);
		}
		
		//Get reference of scaffolding, if scaffolding is new, grab default template:
		String reference = "";
		if(scaffolding.getId() == null){
			//If site has not been created, grab the default template for scaffolding
			try {
				String realmTemplate = "!matrix.template.";
				Site site = SiteService.getSite(scaffolding.getWorksiteId().getValue());
				reference = realmTemplate + site.getType();
			} catch (IdUnusedException e) {
				e.printStackTrace();
			}
		}else{
			reference = scaffolding.getReference();
		}
		
		//get ID of scaffolding, if new, get default ID
		Id scaffid;
		//if scaffolding id does not exists (add matrix), 
		//check if there is a "new"id, which acts like a temp id,
		//if not, create one, then use the "new"id as a reference
		if(scaffolding.getId() == null){
			if(scaffolding.getNewId() == null){
				scaffolding.setNewId(getIdManager().createId());
			}
			scaffid = scaffolding.getNewId();
		}else{
			scaffid = scaffolding.getId();
		}
		
		model.putAll(getConfirmFlagsForRef(scaffolding.getWorksiteId().getValue(), scaffid, reference));

		return model;
	}
	
	public Map getConfirmFlagsForScaffoldingCell(ScaffoldingCell scaffoldingCell){
		Map model = new HashMap();
		if (scaffoldingCell.getScaffolding() == null) {
			return model;
		}
		//if scaffolding is published, warn user;    
		if (scaffoldingCell.getScaffolding().isPublished()){
			model.put(CONFIRM_PUBLISHED_FLAG, true);
		}
		
		model.putAll(getConfirmFlagsForRef(scaffoldingCell.getScaffolding()
				.getWorksiteId().getValue(), scaffoldingCell
				.isDefaultEvaluators() ? scaffoldingCell.getScaffolding()
				.getId() : scaffoldingCell.getWizardPageDefinition().getId(), scaffoldingCell
				.getScaffolding().getReference()));

		return model;
	}
	
	public Map getConfirmFlagsForRef(String siteId, Id id, String reference){
		Map model = new HashMap();
  	  
  	  try {
  		  //if site has groups, check to see evaluators have VIEW_ALL_GROUPS permission,
  		  //if not, then warn the user about this
  		  Site site = SiteService.getSite(siteId);
  		  if(site != null && site.hasGroups()){
  			  List evaluators = getAuthzManager().getAuthorizations(null,
  					  MatrixFunctionConstants.EVALUATE_MATRIX, id);

  			  
  			  boolean warnUser = false;
  			  HashSet rolesAllowedViewAllGroups = null;
				try {
					rolesAllowedViewAllGroups = (HashSet) AuthzGroupService.getAuthzGroup(reference).getRolesIsAllowed(MatrixFunctionConstants.VIEW_ALL_GROUPS);
				} catch (GroupNotDefinedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

  			  for (Iterator iter = evaluators.iterator(); iter.hasNext();) {
  				  boolean evaluatorViewAllGroups = true;	
    			  boolean allEvalsAssignedToAGroup = true;
  				  Authorization az = (Authorization) iter.next();
  				  Agent agent = az.getAgent();
  				  if(agent.isRole()){
  					  if(rolesAllowedViewAllGroups != null)
  						  evaluatorViewAllGroups = evaluatorViewAllGroups && rolesAllowedViewAllGroups.contains(agent.getDisplayName());
  					  else
  						  evaluatorViewAllGroups = false;

  					  // check every user with this role to make sure they have at least 1 group assigned to them
  					  if (!evaluatorViewAllGroups) {
  						  Set<String> usersWithRole = (Set<String>) site
  						  .getUsersHasRole(agent.getDisplayName());
  						  for (String user : usersWithRole) {
  							  Collection groups = site.getGroupsWithMember(user);
  							  if (groups == null || groups.size() == 0) {
  								  allEvalsAssignedToAGroup = false;
  							  }
  						  }
  					  }

  				  } else {
  					  evaluatorViewAllGroups = evaluatorViewAllGroups
  					  && getAuthzManager().isAuthorized(
  							  agent,
  							  MatrixFunctionConstants.VIEW_ALL_GROUPS,
  							  getIdManager()
  							  .getId(reference));
  					  Collection groups = site.getGroupsWithMember(agent.getId().getValue());
  					  if(groups == null || groups.size() == 0){
  						  allEvalsAssignedToAGroup = false;
  					  }
  				  }
  				  if(!evaluatorViewAllGroups && !allEvalsAssignedToAGroup){
  					  warnUser = true;
  					  break;
  				  }
  			  }
  			  if(evaluators.size() > 0 && warnUser){
  				  model.put(CONFIRM_EVAL_VIEW_ALL_GROUPS_FLAG, true);
  			  }
  		  }
  	  } catch (IdUnusedException e) {
  		  e.printStackTrace();
  	  }
  	  
  	  return model;
	}
	

	public Collection getFormsForSelect(String type, String currentSiteId, String currentUserId) {
		Collection commentForms = getAvailableForms(currentSiteId, type, currentUserId);

		List retForms = new ArrayList();
		for (Iterator iter = commentForms.iterator(); iter.hasNext();) {
			StructuredArtifactDefinitionBean sad = (StructuredArtifactDefinitionBean) iter
			.next();
			retForms.add(new CommonFormBean(sad.getId().getValue(), sad
					.getDecoratedDescription(), FORM_TYPE, sad.getOwner()
					.getName(), sad.getModified()));
		}

		Collections.sort(retForms, CommonFormBean.beanComparator);
		return retForms;
	}

	protected Collection getAvailableForms(String siteId, String type, String currentUserId) {
		return getStructuredArtifactDefinitionManager().findAvailableHomes(
				getIdManager().getId(siteId), currentUserId, true, true);
	}

	protected Collection getWizardsForSelect(List wizards, String type, String currentSiteId) {		
		List retWizards = new ArrayList();
		for (Iterator iter = wizards.iterator(); iter.hasNext();) {
			Wizard wizard = (Wizard) iter.next();
			retWizards.add(new CommonFormBean(wizard.getId().getValue(), wizard
					.getName(), WizardFunctionConstants.WIZARD_TYPE_SEQUENTIAL,
					wizard.getOwner().getName(), wizard.getModified()));
		}

		Collections.sort(retWizards, CommonFormBean.beanComparator);
		return retWizards;
	}

	public Collection getTypeDevices(List wizards, String siteId, Id deviceId, String type, String currentUserId) {
		Collection all = getFormsForSelect(type, siteId, currentUserId);
		all.addAll(getWizardsForSelect(wizards, type, siteId));

		//add any of the forms that the user does not have access to but has been added to the matrix
		//Id selectedId = scaffolding.getReviewDevice();

		if (deviceId != null && !sadCollectionContainsId(all, deviceId.getValue())){
			StructuredArtifactDefinitionBean sad = getStructuredArtifactDefinitionManager().loadHome(deviceId);
			all.add(new CommonFormBean(sad.getId().getValue(), sad
					.getDecoratedDescription(), FORM_TYPE, sad.getOwner()
					.getName(), sad.getModified()));
		}



		return all;
	}

	private boolean sadCollectionContainsId(Collection sadCol, String id){
		boolean contains = false;

		for (Iterator iter = sadCol.iterator(); iter.hasNext();) {
			CommonFormBean bean = (CommonFormBean) iter.next();

			if(bean.getId().equals(id)){
				contains = true;
				break;
			}
		}

		return contains;
	}

	public Collection getSelectedAdditionalFormDevices(Collection additionalForms, String siteId, String currentUserId) {
		// cwm need to preserve the ordering
		Collection returnCol = new ArrayList();
		Collection col = getAdditionalFormDevices(siteId, currentUserId);
		for (Iterator iter = col.iterator(); iter.hasNext();) {
			CommonFormBean bean = (CommonFormBean) iter.next();
			if (additionalForms.contains(bean.getId()))
				returnCol.add(bean);
		}

		//add any of the forms that the user does not have access to but has been added to the matrix
		//	Collection selectedIds = sCell.getAdditionalForms();
		for (Iterator iterator = additionalForms.iterator(); iterator.hasNext();) {
			String id = (String) iterator.next();
			if (!sadCollectionContainsId(returnCol, id)){
				StructuredArtifactDefinitionBean sad = getStructuredArtifactDefinitionManager().loadHome(getIdManager().getId(id));
				returnCol.add(new CommonFormBean(sad.getId().getValue(), sad
						.getDecoratedDescription(), FORM_TYPE, sad.getOwner()
						.getName(), sad.getModified()));
			}
		}


		return returnCol;
	}

	protected Collection getAdditionalFormDevices( String siteId, String currentUserId ) {
		// Return all forms
		return getFormsForSelect(null, siteId, currentUserId);
	}

}
