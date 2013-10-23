/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.3/presentation/api-impl/src/java/org/theospi/portfolio/presentation/model/impl/PresentationManagerImpl.java $
* $Id: PresentationManagerImpl.java 118450 2013-01-17 21:05:56Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008, 2009 The Sakai Foundation
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
package org.theospi.portfolio.presentation.model.impl;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.content.api.*;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.event.cover.NotificationService;
import org.sakaiproject.exception.*;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.security.AllowMapSecurityAdvisor;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.shared.ArtifactFinderManager;
import org.sakaiproject.metaobj.shared.DownloadableManager;
import org.sakaiproject.metaobj.shared.mgt.*;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactHomeInterface;
import org.sakaiproject.metaobj.shared.model.*;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.metaobj.shared.model.ContentResourceArtifact;
import org.sakaiproject.service.legacy.resource.DuplicatableToolService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.util.ResourceLoader;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.theospi.event.EventService;
import org.theospi.event.EventConstants;
import org.theospi.portfolio.presentation.CommentSortBy;
import org.theospi.portfolio.presentation.PresentationFunctionConstants;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.export.PresentationExport;
import org.theospi.portfolio.presentation.model.*;
import org.theospi.portfolio.security.Authorization;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.AuthorizationFailedException;
import org.theospi.portfolio.security.impl.AllowAllSecurityAdvisor;
import org.sakaiproject.metaobj.shared.EntityContextFinder;
import org.theospi.portfolio.shared.model.ItemDefinitionMimeType;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.model.OspException;
import org.theospi.portfolio.style.StyleConsumer;
import org.theospi.portfolio.style.model.Style;
import org.theospi.utils.zip.UncloseableZipInputStream;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.zip.*;

public class PresentationManagerImpl extends HibernateDaoSupport
   implements PresentationManager, DuplicatableToolService, DownloadableManager,
   StyleConsumer, FormConsumer {

   private List definedLayouts;
   private AgentManager agentManager;
   private AuthorizationFacade authzManager = null;
   private AuthenticationManager authnManager = null;
   private IdManager idManager = null;
   private WritableObjectHome fileHome;
   private HomeFactory homeFactory = null;
   private WorksiteManager worksiteManager;
   private LockManager lockManager;
   private ArtifactFinderManager artifactFinderManager;
   private ContentHostingService contentHosting = null;
   private SecurityService securityService = null;
   private EventService eventService = null;
   private String tempPresDownloadDir;
   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;
   private List globalSites;
   private List globalSiteTypes;
   private List initializedServices;
   private boolean autoDdl = true;
   private boolean portfolioPropertyFormConversion = true;
   private String downloadExternalUri;

   static final private String   IMPORT_BASE_FOLDER_ID = "importedPresentations";
   private String importFolderName;
   
   private static final String TEMPLATE_ID_TAG = "templateId";
   private static final String PRESENTATION_ID_TAG = "presentationId";
   private static final String SYSTEM_COLLECTION_ID = "/system/";
   
   private static ResourceLoader messages = new ResourceLoader(
         "org.theospi.portfolio.presentation.bundle.Messages");
   
   
   public PresentationTemplate storeTemplate(final PresentationTemplate template) {
      return storeTemplate(template, true, true);
   }

   protected PresentationTemplate storeTemplate(final PresentationTemplate template, boolean checkAuthz) {
      return storeTemplate(template, checkAuthz, true);
   }
   
   public PresentationTemplate storeTemplate(final PresentationTemplate template, boolean checkAuthz, boolean updateDates) {
      if (updateDates || template.getModified() == null) {
         template.setModified(new Date(System.currentTimeMillis()));
      }

      boolean newTemplate = (template.getId() == null);

      if (newTemplate || template.isNewObject()) {
         if (updateDates || template.getCreated() == null) {
            template.setCreated(new Date(System.currentTimeMillis()));
         }

         if (checkAuthz) {
            getAuthzManager().checkPermission(PresentationFunctionConstants.CREATE_TEMPLATE,
               getIdManager().getId(template.getSiteId()));
         }
      } else {
         deleteUnusedItemDefinition(template);
         if (checkAuthz) {
            getAuthzManager().checkPermission(PresentationFunctionConstants.EDIT_TEMPLATE,
                  template.getId());
         }
      }

      if (template.isNewObject() || newTemplate) {
         if (template.getId() != null) {
            template.setNewId(template.getId());
            template.setId(null);
         }
         getHibernateTemplate().save(template);
         template.setNewObject(false);
         eventService.postEvent(EventConstants.EVENT_TEMPLATE_ADD,template.getId().getValue());
      }
      else {
         getHibernateTemplate().merge(template);
         eventService.postEvent(EventConstants.EVENT_TEMPLATE_REVISE,template.getId().getValue());
      }

      lockTemplateFiles(template);

      return template;
   }

   /**
    * remove all the locks associated with this template
    */
   protected void clearLocks(Id id) {
      getLockManager().removeAllLocks(id.getValue());
   }


   /**
    * locks all the files associated with this template.
    * @param template
    */
   protected void lockTemplateFiles(PresentationTemplate template){
      clearLocks(template.getId());
      for (Iterator i = template.getFiles().iterator();i.hasNext();){
         TemplateFileRef fileRef = (TemplateFileRef) i.next();
         getLockManager().lockObject(fileRef.getFileId(),
        		 template.getId().getValue(), "saving a presentation template", true);
      }
      getLockManager().lockObject(template.getRenderer().getValue(),
    		  template.getId().getValue(), "saving a presentation template", true);

      if (template.getPropertyPage() != null) {
         getLockManager().lockObject(template.getPropertyPage().getValue(),
              template.getId().getValue(), "saving a presentation template", true);
      }
   }

   public PresentationTemplate getPresentationTemplate(final Id id) {
      HibernateCallback callback = new HibernateCallback() {

         public Object doInHibernate(Session session) throws HibernateException {
            PresentationTemplate template = (PresentationTemplate) session.load(PresentationTemplate.class, id);
            template.getItems().size(); //force load
            if (template.getOwner() == null){
                reassignOwner(template);
            }

            for (Iterator i = template.getItemDefinitions().iterator(); i.hasNext();) {
               PresentationItemDefinition itemDef = (PresentationItemDefinition) i.next();
                  itemDef.getMimeTypes().size();
            }

            return template;
         }

      };

      try {
         PresentationTemplate template = (PresentationTemplate) getHibernateTemplate().execute(callback);
         if (template.getOwner() == null){
                reassignOwner(template);
         }
         if (template.getPropertyPage() != null) {
            String propPage = getContentHosting().resolveUuid(template.getPropertyPage().getValue());
            getSecurityService().pushAdvisor(
               new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ,
                     getContentHosting().getReference(propPage)));
         }
         return template;
      } catch (HibernateObjectRetrievalFailureException e) {
         logger.debug(e);
         return null;
      }
   }

   protected List getTemplatesForConversion() {
      
      return getHibernateTemplate().findByNamedQuery("findTemplatesForConversion");
   }
   
   protected List getPortfoliosForConversion() {
      
      return getHibernateTemplate().findByNamedQuery("findPortfoliosForConversion");
   }

   public void reassignOwner(PresentationTemplate templateId)
   {
          templateId.setOwner(getAgentManager().getAgent("admin"));
   }
	
   public Presentation getPresentation(final Id id, String secretExportKey) {
      Presentation pres = findPresentationByLogID( idManager.getId(secretExportKey) );
      if (pres == null || !id.equals(pres.getId())) {
         throw new AuthorizationFailedException("Exporting inappropriate presentation");
      }

      switchUser(pres.getOwner());

      return pres;
   }

   protected void switchUser(Agent owner) {
      org.sakaiproject.tool.api.Session sakaiSession = SessionManager.getCurrentSession();
      sakaiSession.setUserId(owner.getId().getValue());
      sakaiSession.setUserEid(owner.getId().getValue());
   }

   public Presentation getLightweightPresentation(final Id id) {
      HibernateCallback callback = new HibernateCallback() {

         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            Presentation pres = (Presentation) session.load(Presentation.class, id);
            return pres;
         }

      };

      try {
         Presentation presentation = (Presentation) getHibernateTemplate().execute(callback);

         return presentation;
      } catch (HibernateObjectRetrievalFailureException e) {
         logger.debug(e);
         return null;
      }
   }

   /** Return Presentation object corresponding to given id
    ** (exception is thrown if user not authorized)
    **/
   public Presentation getPresentation(final Id id) {
      return getPresentation(id, true);
   }
   
   /** Return Presentation object corresponding to given id,
    ** optionally bypassing authorization check (for local export)
    **/
   public Presentation getPresentation(final Id id, boolean checkAuth) {

      HibernateCallback callback = new HibernateCallback() {

         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            Presentation pres = (Presentation) session.load(Presentation.class, id);

            viewingPresentation(pres);

            //remove any artifacts that have been removed from the repository
            for (Iterator i= pres.getPresentationItems().iterator();i.hasNext();){
               PresentationItem item = (PresentationItem) i.next();
               ArtifactFinder artifactFinder = getArtifactFinderManager().getArtifactFinderByType(item.getDefinition().getType());
               if (artifactFinder.load(item.getArtifactId()) == null){
                  deleteArtifactReference(item.getArtifactId());
               }
            }
            pres.getTemplate().getItemDefinitions().size(); //force load

            return pres;
         }

      };

      try {
         Presentation presentation = (Presentation) getHibernateTemplate().execute(callback);

         if (!presentation.getIsPublic() && checkAuth &&
             !presentation.getOwner().equals(getAuthnManager().getAgent())) {
            getAuthzManager().checkPermission(PresentationFunctionConstants.VIEW_PRESENTATION, presentation.getId());
         }

         Collection viewerAuthzs = getAuthzManager().getAuthorizations(null,
            PresentationFunctionConstants.VIEW_PRESENTATION, presentation.getId());

         for (Iterator i = viewerAuthzs.iterator(); i.hasNext();) {
            Authorization viewer = (Authorization) i.next();
            presentation.getViewers().add(viewer.getAgent());
         }
         return presentation;
      } 
      catch (HibernateObjectRetrievalFailureException e) {
         logger.debug(e);
         return null;
      }
   }
   
   /** Return the XML document string corresponding to the specified public portfolio's propertyForm
    ** (portfolio must be publicly viewable).
    **
    ** @param portfolioId public portfolio
    ** @return XML document string or null if error
    **/
   public String getPublicPropertyForm( Presentation presentation )
   {
      StringBuilder options = new StringBuilder();
      
      if ( presentation == null || !presentation.getIsPublic() || presentation.getPropertyForm() == null)
         return null;
         
      String formId = contentHosting.resolveUuid(presentation.getPropertyForm().getValue());
      String ref = contentHosting.getReference(formId);
      
      BufferedReader rdr = null;
      try {
         securityService.pushAdvisor(new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ, ref));
         
         ContentResource resource = contentHosting.getResource(formId);
         rdr = new BufferedReader( new InputStreamReader( resource.streamContent() ) );
          
          String line = rdr.readLine();
          while ( line != null )
          {
             options.append( line );
             line = rdr.readLine();
          }
      }
      catch ( Exception e )
      {
         logger.warn(e);
         return null;
      }
      finally {
         try {
            rdr.close();
         }
         catch (Exception e) {
            if (logger.isDebugEnabled()) {
               logger.debug("Error closing stream reader for resource: " + formId);
            }
         }
      }
      
      return options.toString();
   }

   protected boolean artifactExists(Id artifactId) {
      return (getNode(artifactId) != null);
   }

   public void deleteUnusedItemDefinition(PresentationTemplate template) {
      Set deletedItems = template.getDeletedItems();

      if (deletedItems == null) {
         return;
      }

      for (Iterator i=deletedItems.iterator();i.hasNext();) {
         PresentationItemDefinition item = (PresentationItemDefinition)i.next();
         if (item.getId() != null) {
            deleteUnusedItemDefinition(item.getId());
         }
      }
   }

   public void deleteUnusedItemDefinition(Id itemDefId) {
      String statement = "delete from osp_presentation_item " +
               " where osp_presentation_item.item_definition_id = ?";
       deleteByStatementHelper(statement, itemDefId);
   }

   /* 
    * generic delete by Id using prepared statement helper function
    * to reduce redundency.
    */
   private void deleteByStatementHelper(String statement, Id targetId) {
      Connection connection = null;
      PreparedStatement stmt = null;
      Session session = getSession();
      try {
         connection = session.connection();
         stmt = connection.prepareStatement(statement);
         stmt.setString(1, targetId.getValue());
         stmt.execute();
      } catch (SQLException e) {
         logger.error("",e);
         throw new OspException(e);
      } catch (HibernateException e) {
         logger.error("",e);
         throw new OspException(e);
      } finally {
         if (stmt != null) {
            //ensure the statement is closed
            try {
               stmt.close();
            } 
            catch (Exception e) {
               if (logger.isDebugEnabled()) {
                  logger.debug(e);
               }
            }
         }
         if (connection != null) {
            //ensure the connection is closed
            //as of hibernate 3.1 we are responsible for this here
            try {
                connection.close();  
            } 
            catch (Exception e) {
               if (logger.isDebugEnabled()) {
                  logger.debug(e);
               }
            }
         }         
      }
   }
   
   public boolean deletePresentationTemplate(final Id id) {
      PresentationTemplate template = getPresentationTemplate(id);
      getAuthzManager().checkPermission(PresentationFunctionConstants.DELETE_TEMPLATE, template.getId());
      clearLocks(template.getId());

      Collection presentations = getHibernateTemplate().findByNamedQuery("findPortfolioByTemplate", id.getValue());
      // Don't think we want to delete template presentations -- return false if any exist
      if ( presentations.size() > 0 )
         return false;

      HibernateCallback callback = new HibernateCallback() {

         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            PresentationTemplate template =
               (PresentationTemplate) session.load(PresentationTemplate.class, id);
            
            session.delete(template);
            
            return null;
         }

      };
      getHibernateTemplate().execute(callback);
      eventService.postEvent(EventConstants.EVENT_TEMPLATE_DELETE,template.getId().getValue());
      return true;

   }

   protected void deleteViewers(Id presId) {
      Collection authzs = getAuthzManager().getAuthorizations(null, PresentationFunctionConstants.VIEW_PRESENTATION, presId);

      for (Iterator i = authzs.iterator(); i.hasNext();) {
         Authorization authz = (Authorization) i.next();
         getAuthzManager().deleteAuthorization(authz.getAgent(),
            authz.getFunction(), authz.getQualifier());
      }
   }

   public void deletePresentation(final Id id) {
      deletePresentation(id, true);
   }

   public void deletePresentation(final Id id, boolean checkAuthz) {
      if (checkAuthz) {
         getAuthzManager().checkPermission(PresentationFunctionConstants.DELETE_PRESENTATION, id);
      }

      HibernateCallback callback = new HibernateCallback() {
         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            Presentation presentation =
               (Presentation) session.load(Presentation.class, id);

            if (presentation.getPropertyForm() != null) {
               deleteResource(presentation.getId(), presentation.getPropertyForm());
            }
            
            // delete viewer authz
            deleteViewers(id);

            deleteComments(id);
            deleteLogs(id);
            deletePresentationPages(id);
            session.delete(presentation);
            return null;
         }
      };

      getHibernateTemplate().execute(callback);
      eventService.postEvent(EventConstants.EVENT_PORTFOLIO_DELETE,id.getValue());
   }

   protected void deleteLogs(final Id presentationId) throws HibernateException {
      HibernateCallback callback = new HibernateCallback() {
         public Object doInHibernate(Session session) throws HibernateException, SQLException {
      
            Query q = session.createQuery("from PresentationLog where presentation_id=?");
            q.setString(0, presentationId.getValue());
            return q.list();
         }
      };
      List logs = (List)getHibernateTemplate().execute(callback);
      getHibernateTemplate().deleteAll(logs);
   }

   protected void deleteComments(final Id presentationId) throws HibernateException {
      HibernateCallback callback = new HibernateCallback() {
         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            

            Query q = session.createQuery("from PresentationComment where presentation_id=?");
            q.setString(0, presentationId.getValue());
            return q.list();
         }
      };
      List comments = (List)getHibernateTemplate().execute(callback);
      getHibernateTemplate().deleteAll(comments);
   }

   protected void deletePresentationPages(final Id presentationId) throws HibernateException {
      
      HibernateCallback callback = new HibernateCallback() {
         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            Query q = session.createQuery("from PresentationPage where presentation_id=?");
            q.setString(0, presentationId.getValue());
            return q.list();
         }
      };
      
      List pages = (List)getHibernateTemplate().execute(callback);
      getHibernateTemplate().deleteAll(pages);
   }

   public PresentationItemDefinition getPresentationItemDefinition(final Id id) {
      HibernateCallback callback = new HibernateCallback() {

         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            PresentationItemDefinition itemDef = (PresentationItemDefinition) session.load(PresentationItemDefinition.class, id);
            itemDef.getMimeTypes().size(); //force load
            return itemDef;
         }

      };

      try {
         return (PresentationItemDefinition) getHibernateTemplate().execute(callback);
      } catch (HibernateObjectRetrievalFailureException e) {
         logger.debug(e);
         return null;
      }
   }

   public void deletePresentationItemDefinition(Id id) {
      PresentationItemDefinition item = getPresentationItemDefinition(id);
      if (item == null) {
         return;
      }
      getHibernateTemplate().delete(item);
   }

   public PresentationItem getPresentationItem(Id id) {
      try {
         return (PresentationItem) getHibernateTemplate().load(PresentationItem.class, id);
      } catch (HibernateObjectRetrievalFailureException e) {
         logger.error("",e);
         throw new OspException(e);
      }
   }

   public void updateItemDefintion(PresentationItemDefinition itemDef) {
      getHibernateTemplate().saveOrUpdate(itemDef);
   }

   public void deletePresentationItem(Id id) {
      PresentationItem item = getPresentationItem(id);
      if (item == null) {
         return;
      }
      getHibernateTemplate().delete(item);
   }

   /**
    * saves or updates a presentation and any associated presentention_items.
    * This method does not persist the viewer list, for that use addViewer(), or deleteViewer()
    *
    * @param presentation
    * @return
    */
   public Presentation storePresentation(Presentation presentation, boolean checkAuthz, boolean updateDates) {
      if (updateDates || presentation.getModified() == null) {
         presentation.setModified(new Date(System.currentTimeMillis()));
      }

      if (presentation.getSiteId() == null || presentation.getSiteId().equals("")) {
         presentation.setSiteId(ToolManager.getCurrentPlacement().getContext());
      }
      
      setupPresItemDefinition(presentation);

      if (presentation.getOwner() == null) {
         presentation.setOwner(getAuthnManager().getAgent());
      }

      if (presentation.isNewObject()) {
         if (updateDates || presentation.getCreated() == null) {
            presentation.setCreated(new Date(System.currentTimeMillis()));
         }

         if (checkAuthz) {
            getAuthzManager().checkPermission(PresentationFunctionConstants.CREATE_PRESENTATION,
               getIdManager().getId(ToolManager.getCurrentPlacement().getId()));
         }
         
         if (presentation.getId() != null) {
            presentation.setNewId(presentation.getId());
            presentation.setId(null);
         }
         getHibernateTemplate().save(presentation);
         eventService.postEvent(EventConstants.EVENT_PORTFOLIO_ADD, presentation.getId().getValue());
      } else {
         if (checkAuthz) {
            getAuthzManager().checkPermission(PresentationFunctionConstants.EDIT_PRESENTATION,
               presentation.getId());
         }
         getHibernateTemplate().merge(presentation);
         eventService.postEvent(EventConstants.EVENT_PORTFOLIO_REVISE, presentation.getId().getValue());
      }
      
      storePresentationPages(presentation.getPages(), presentation.getId());

      return presentation;
   }
   
   public Presentation storePresentation(Presentation presentation) {
      return storePresentation(presentation, true, true);
   }

   protected void setupPresItemDefinition(Presentation presentation) {
      if (presentation.getPresentationType().equals(Presentation.FREEFORM_TYPE)) {
         PresentationTemplate template = getPresentationTemplate(Presentation.FREEFORM_TEMPLATE_ID);
         presentation.setTemplate(template);
         PresentationItemDefinition itemDef =
               (PresentationItemDefinition) template.getItemDefinitions().iterator().next();
         for (Iterator i = presentation.getItems().iterator();i.hasNext();) {
            PresentationItem item = (PresentationItem) i.next();
            item.setDefinition(itemDef);
         }
      }
   }

	
   /** Save Presentation Pages to database.
    **/
   protected void storePresentationPages(List pages, Id presentationId) {
      if (pages == null) {
         return;
      }

      try {
         // Save added/modified pages
         for (Iterator i=pages.iterator();i.hasNext();) {
            PresentationPage page = (PresentationPage) i.next();
            page.setModified(new Date(System.currentTimeMillis()));
            fixupRegions(page);
            if (page.isNewObject()) {
               page.setCreated(new Date(System.currentTimeMillis()));
               page.setNewId(page.getId());
               page.setId(null);
               page.setNewObject(false);
               getHibernateTemplate().save(page);
            }
            else {
               getHibernateTemplate().merge(page);
            }
         }

         // Remove deleted pages
         List allPages = getPresentationPagesByPresentation(presentationId);
         for (Iterator i=allPages.iterator();i.hasNext();) {
            PresentationPage page = (PresentationPage) i.next();
            if (!pages.contains(page)) {
               getHibernateTemplate().delete(page);
            }
         }
      }
      catch (Exception e) {
         // Hibernate errors generated if user hits save twice
         logger.warn(e);
      }
   }

   protected void fixupRegions(PresentationPage page) {
      for (Iterator i = page.getRegions().iterator();i.hasNext();) {
         PresentationPageRegion region = (PresentationPageRegion) i.next();
         region.setPage(page);
      }
   }      

   public Collection findTemplatesByOwner(Agent owner, String siteId) {
      return getHibernateTemplate().findByNamedQuery("findTemplateByOwnerAndSite",
            new Object[]{owner, siteId});
   }


   public Collection findTemplatesByOwner(Agent owner) {
      return getHibernateTemplate().findByNamedQuery("findTemplateByOwner", owner);
   }

   public Collection findPublishedTemplates() {
      return getHibernateTemplate().findByNamedQuery("findPublishedTemplates",
         new Object[]{Boolean.valueOf(true), getAuthnManager().getAgent()});
   }

   public Collection findPublishedTemplates(String siteId) {
      return getHibernateTemplate().findByNamedQuery("findPublishedTemplatesBySite",
         new Object[]{Boolean.valueOf(true), getAuthnManager().getAgent(), siteId});
   }

   public Collection findGlobalTemplates() {
   
      StringBuilder query = new StringBuilder( "from PresentationTemplate where published=? and site_id in (" );

      for (Iterator i = getGlobalSites().iterator(); i.hasNext();) {
         String site = (String) i.next();
         query.append( "'" );
         query.append( site );
         query.append( "',");
      }
      query.append( "'')");

      // if this is a global site, exclude owner's, which are included in findTemplatesByOwner()
      if ( isGlobal() ) 
         query.append(" and owner_id!=?");

      query.append( " Order by name");

      Object[] parms = null;      
      if ( isGlobal() ) 
         parms = new Object[]{Boolean.valueOf(true), getAuthnManager().getAgent().getId().getValue()};
      else
         parms = new Object[]{Boolean.valueOf(true)};
         
      return getHibernateTemplate().find(query.toString(), parms );
   }

   protected Collection findPublishedTemplatesBySite(String siteId) {
      return getHibernateTemplate().findByNamedQuery(
         "findAllPublishedTemplatesBySite",
         new Object[]{Boolean.valueOf(true), siteId});
   }

   /**
    * {@inheritDoc}
    */
   public Collection findPublicPresentations(String siteId)
   {
      Collection presList;
      if ( siteId != null )
      {
         presList = getHibernateTemplate().findByNamedQuery(
                                                       "findPublicPortfoliosBySite", 
                                                       new Object[]{siteId});
      }
      else
      {
         presList = getHibernateTemplate().findByNamedQuery("findPublicPortfolios"); 
      }
      
      return presList;
   }
   
   /**
    * {@inheritDoc}
    */
   public Collection findPublicPresentations(Agent viewer, String toolId, String showHidden) 
   {
      // Build list of hidden presentation authzs
      Collection hiddenAuthzs = getAuthzManager().getAuthorizations(viewer, 
            PresentationFunctionConstants.HIDE_PRESENTATION, null);
      List<Id> hiddenIds = new ArrayList<Id>();
      hiddenIds.add(getIdManager().getId("last")); // ensure list not empty
      
      if ( !showHidden.equals(PRESENTATION_VIEW_ALL) ) 
         hiddenIds = buildPresList(hiddenAuthzs);

      Collection presList;
      
      if ( toolId != null )
      {
         String[] paramNames = new String[] {"toolId", "hiddenId"};
         Object[] params = new Object[]{toolId, hiddenIds};
         
         if ( showHidden.equals(PRESENTATION_VIEW_HIDDEN) ) 
            presList = getHibernateTemplate().findByNamedQueryAndNamedParam(
                                                                            "findPublicPortfoliosByToolInclusive", 
                                                                            paramNames, params);
         else if ( showHidden.equals(PRESENTATION_VIEW_VISIBLE) ) 
            presList = getHibernateTemplate().findByNamedQueryAndNamedParam(
                                                                            "findPublicPortfoliosByToolExclusive", 
                                                                            paramNames, params);
         else // ( showHidden.equals(PRESENTATION_VIEW_ALL) ) 
            presList = getHibernateTemplate().findByNamedQueryAndNamedParam(
                                                                            "findPublicPortfoliosByToolExclusive", 
                                                                            paramNames, params);
      }
      else
      {
         String[] paramNames = new String[] {"hiddenId"};
         Object[] params = new Object[]{hiddenIds};
         
         if ( showHidden.equals(PRESENTATION_VIEW_HIDDEN) ) 
            presList = getHibernateTemplate().findByNamedQueryAndNamedParam(
                                                                            "findPublicPortfoliosInclusive", 
                                                                            paramNames, params);
         else if ( showHidden.equals(PRESENTATION_VIEW_VISIBLE) ) 
            presList = getHibernateTemplate().findByNamedQueryAndNamedParam(
                                                                            "findPublicPortfoliosExclusive", 
                                                                            paramNames, params);
         else // ( showHidden.equals(PRESENTATION_VIEW_ALL) ) 
            presList = getHibernateTemplate().findByNamedQueryAndNamedParam(
                                                                            "findPublicPortfoliosExclusive", 
                                                                            paramNames, params);
      }
      
      // Make sure all presentations have valid owner
      Collection finalPresList = new ArrayList();
      for (Iterator i=presList.iterator();i.hasNext();) {
         Presentation pres = (Presentation)i.next();
         if ( pres.getOwner().getId() != null )
         {
            pres.setAuthz(new PresentationAuthzMap(viewer, pres));
            finalPresList.add(pres);
         }
         else
         {
            getHibernateTemplate().evict(pres);
         }
      }
      
      return finalPresList;
   }
   
   /**
    * {@inheritDoc}
    */
   public Collection findOtherPresentationsUnrestricted(Agent viewer, String toolId, String showHidden) {
      
      // No support for aggregating all sites (permission constraints)
      if ( toolId == null )
         return new Vector();
      
      // Build list of hidden presentation authzs
      Collection hiddenAuthzs = getAuthzManager().getAuthorizations(viewer, 
            PresentationFunctionConstants.HIDE_PRESENTATION, null);
      List<Id> hiddenIds = new ArrayList<Id>();
      hiddenIds.add(getIdManager().getId("last")); // ensure list not empty
      
      if ( !showHidden.equals(PRESENTATION_VIEW_ALL) ) 
         hiddenIds = buildPresList(hiddenAuthzs);

      Collection presList;
      String[] paramNames = new String[] {"toolId", "hiddenId"};
      Object[] params = new Object[]{toolId, hiddenIds};
      
      if ( showHidden.equals(PRESENTATION_VIEW_HIDDEN) ) 
         presList = getHibernateTemplate().findByNamedQueryAndNamedParam(
                                                                         "findPortfoliosUnrestrictedInclusive", 
                                                                         paramNames, params);
      else if ( showHidden.equals(PRESENTATION_VIEW_VISIBLE) ) 
         presList = getHibernateTemplate().findByNamedQueryAndNamedParam(
                                                                         "findPortfoliosUnrestrictedExclusive", 
                                                                         paramNames, params);
      else // ( showHidden.equals(PRESENTATION_VIEW_ALL) ) 
         presList = getHibernateTemplate().findByNamedQueryAndNamedParam(
                                                                         "findPortfoliosUnrestrictedExclusive", 
                                                                         paramNames, params);
      
      // Make sure all presentations have valid owner (and the owner is not the current user)
      Collection finalPresList = new ArrayList();
      for (Iterator i=presList.iterator();i.hasNext();) {
         Presentation pres = (Presentation)i.next();
         if ( pres.getOwner().getId() != null && ! pres.getOwner().getId().equals(viewer.getId()) )
         {
            pres.setAuthz(new PresentationAuthzMap(viewer, pres));
            finalPresList.add(pres);
         }
         else
         {
            getHibernateTemplate().evict(pres);
         }
      }
      
      return finalPresList;
   }
   
   /**
    * {@inheritDoc}
    */
   public Collection findAllPresentations(Agent viewer, String toolId, String showHidden) {
      Collection ownerList = findOwnerPresentations( viewer, toolId, showHidden );
      Collection sharedList = findSharedPresentations( viewer, toolId, showHidden );
      Collection allList = ownerList;
      
      // Filter out shared presentations if owned by current user
      for (Iterator i=sharedList.iterator();i.hasNext();) {
         Presentation pres = (Presentation)i.next();

         if ( !ownerList.contains(pres) ) 
            allList.add(pres);
         else
            getHibernateTemplate().evict(pres);
      }
      
      return allList;
   }
   
   /**
    * {@inheritDoc}
    */
   public Collection findSharedPresentations(Agent viewer, String toolId, String showHidden) {
      // Build list of hidden presentation authzs
      Collection hiddenAuthzs = getAuthzManager().getAuthorizations(viewer, 
            PresentationFunctionConstants.HIDE_PRESENTATION, null);
      List<Id> hiddenIds = new ArrayList<Id>();
      hiddenIds.add(getIdManager().getId("last")); // ensure list not empty
      
      if ( !showHidden.equals(PRESENTATION_VIEW_ALL) ) 
         hiddenIds = buildPresList(hiddenAuthzs);

      // Build list of presentations shared with user
      Collection viewAuthzs = getAuthzManager().getAuthorizations(viewer,
            PresentationFunctionConstants.VIEW_PRESENTATION,  null);
      
      Collection presList;
      if ( toolId != null )
      {
         String[] paramNames = new String[] {"toolId", "id", "hiddenId"};
         Object[] params = new Object[]{toolId, buildPresList(viewAuthzs), hiddenIds};
         
         
         if ( showHidden.equals(PRESENTATION_VIEW_HIDDEN) ) 
            presList = getHibernateTemplate().findByNamedQueryAndNamedParam(
                           "findPortfoliosByToolInclusive", paramNames, params);
         else if ( showHidden.equals(PRESENTATION_VIEW_VISIBLE) ) 
            presList = getHibernateTemplate().findByNamedQueryAndNamedParam(
                           "findPortfoliosByToolExclusive", paramNames, params);
         else // ( showHidden.equals(PRESENTATION_VIEW_ALL) ) 
            presList = getHibernateTemplate().findByNamedQueryAndNamedParam(
                           "findPortfoliosByToolExclusive", paramNames, params);
      }
      else
      {
         String[] paramNames = new String[] {"id", "hiddenId"};
         Object[] params = new Object[]{buildPresList(viewAuthzs), hiddenIds};
         
         if ( showHidden.equals(PRESENTATION_VIEW_HIDDEN) ) 
            presList = getHibernateTemplate().findByNamedQueryAndNamedParam(
                           "findPortfoliosInclusive", paramNames, params);
         else if ( showHidden.equals(PRESENTATION_VIEW_VISIBLE) ) 
            presList = getHibernateTemplate().findByNamedQueryAndNamedParam(
                           "findPortfoliosExclusive", paramNames, params);
         else // ( showHidden.equals(PRESENTATION_VIEW_ALL) ) 
            presList = getHibernateTemplate().findByNamedQueryAndNamedParam(
                           "findPortfoliosExclusive", paramNames, params);
      }

      // Make sure all presentations have valid owner and are not expired
      Collection finalPresList = new ArrayList();
      for (Iterator i=presList.iterator();i.hasNext();) 
      {
         Presentation pres = (Presentation)i.next();
         if ( !pres.isExpired() && pres.getOwner().getId() != null ) 
         {
            pres.setAuthz(new PresentationAuthzMap(viewer, pres));
            finalPresList.add(pres);
         }
         else
         {
            getHibernateTemplate().evict(pres);
         }
      }
      
      return finalPresList;
   }

   public Collection findOwnerPresentations(Agent owner, String toolId, String showHidden) {
   
      // Build list of hidden presentation authzs
      Collection hiddenAuthzs = getAuthzManager().getAuthorizations(owner, 
            PresentationFunctionConstants.HIDE_PRESENTATION, null);
      List<Id> hiddenIds = new ArrayList<Id>();
      hiddenIds.add(getIdManager().getId("last")); // ensure list not empty
      
      if ( !showHidden.equals(PRESENTATION_VIEW_ALL) ) 
         hiddenIds = buildPresList(hiddenAuthzs);

      Collection presList;
      if ( toolId == null )
      {
         String[] paramNames = new String[] {"owner", "hiddenId"};
         Object[] params = new Object[]{owner, hiddenIds};
         
         if ( showHidden.equals(PRESENTATION_VIEW_HIDDEN) ) 
            presList = getHibernateTemplate().findByNamedQueryAndNamedParam(
                                                       "findPortfolioByOwnerInclusive", 
                                                       paramNames, params);
         else if ( showHidden.equals(PRESENTATION_VIEW_VISIBLE) ) 
            presList = getHibernateTemplate().findByNamedQueryAndNamedParam(
                                                       "findPortfolioByOwnerExclusive", 
                                                       paramNames, params);
         else // ( showHidden.equals(PRESENTATION_VIEW_ALL) ) 
            presList = getHibernateTemplate().findByNamedQueryAndNamedParam(
                                                       "findPortfolioByOwnerExclusive", 
                                                       paramNames, params);
      }
      else
      {
         String[] paramNames = new String[] {"owner", "toolId", "hiddenId"};
         Object[] params = new Object[]{owner, toolId, hiddenIds};
         
         if ( showHidden.equals(PRESENTATION_VIEW_HIDDEN) ) 
            presList = getHibernateTemplate().findByNamedQueryAndNamedParam(
                                                       "findPortfolioByOwnerAndToolInclusive", 
                                                       paramNames, params);
         else if ( showHidden.equals(PRESENTATION_VIEW_VISIBLE) ) 
            presList = getHibernateTemplate().findByNamedQueryAndNamedParam(
                                                       "findPortfolioByOwnerAndToolExclusive", 
                                                       paramNames, params);
         else // ( showHidden.equals(PRESENTATION_VIEW_ALL) ) 
            presList = getHibernateTemplate().findByNamedQueryAndNamedParam(
                                                       "findPortfolioByOwnerAndToolExclusive", 
                                                       paramNames, params);
      }
      
      for (Iterator i=presList.iterator();i.hasNext();) {
         Presentation pres = (Presentation)i.next();
         pres.setAuthz(new PresentationAuthzMap(owner, pres));
      }
      
      return presList;
   }

   protected List<Id> buildPresList(Collection presentationAuthzs) {
      List<Id> presIdList = new ArrayList<Id>();

      for (Iterator i=presentationAuthzs.iterator();i.hasNext();) {
         Authorization authz = (Authorization)i.next();
         presIdList.add(authz.getQualifier());
      }

      //hibernate seems to be unhappy when the list is empty.
      presIdList.add(getIdManager().getId("last"));

      return presIdList;
   }
   
   public void createComment(PresentationComment comment) {
      createComment(comment, true, true);
   }
   
   public void createComment(PresentationComment comment, boolean checkAuthz, boolean updateDates) {
      if (checkAuthz) {
         getAuthzManager().checkPermission(PresentationFunctionConstants.COMMENT_PRESENTATION,
            comment.getPresentationId());
      }

      if (updateDates || comment.getCreated() == null) {
         comment.setCreated(new Date(System.currentTimeMillis()));
      }

      if (comment.getCreator() == null) {
         comment.setCreator(getAuthnManager().getAgent());
      }

      getHibernateTemplate().save(comment);
   }

   public List getPresentationComments(Id presentationId, Agent viewer) {
      Session session = getSession();

      SQLQuery query = session.createSQLQuery("SELECT {osp_presentation_comment.*} " +
         " FROM osp_presentation_comment {osp_presentation_comment}, osp_presentation p " +
         " WHERE {osp_presentation_comment}.presentation_id = p.id and p.id = :presentationId and" +
         " (visibility = " + PresentationComment.VISABILITY_PUBLIC + " or " +
         "   (visibility = " + PresentationComment.VISABILITY_SHARED + " and " +
         "    p.owner_id = :viewerId) or " +
         " creator_id = :viewerId)" +
         " ORDER BY {osp_presentation_comment}.created");

      query.addEntity("osp_presentation_comment", PresentationComment.class);
      query.setString("presentationId", presentationId.getValue());
      query.setString("viewerId", viewer.getId().getValue());

      try {
         return query.list();
      } catch (HibernateException e) {
         logger.error("",e);
         throw new OspException(e);
      } 
   }

   public PresentationComment getPresentationComment(Id id) {
      try {
         return (PresentationComment) getHibernateTemplate().load(PresentationComment.class, id);
      } catch (HibernateObjectRetrievalFailureException e) {
         logger.error("",e);
         throw new OspException(e);
      }
   }

   public void deletePresentationComment(PresentationComment comment) {
      getHibernateTemplate().delete(comment);
   }

   public void updatePresentationComment(PresentationComment comment) {
      getHibernateTemplate().saveOrUpdate(comment);
   }

   public List getOwnerComments(Agent owner, CommentSortBy sortBy) {
      return  getOwnerComments(owner, sortBy, false);
   }

   public List getOwnerComments(Agent owner, CommentSortBy sortBy, boolean excludeOwner) {
      String orderBy = sortBy.getSortByColumn();

      if (orderBy.startsWith("owner_id") || orderBy.startsWith("name")) {
         orderBy = "p." + orderBy;
      } else {
         orderBy = "{osp_presentation_comment}." + orderBy;
      }

      Session session = getSession();

      String includeOwnerCondition = "";
      if (!excludeOwner) {
         includeOwnerCondition = " or creator_id = :ownerId ) ";
      } else {
         includeOwnerCondition = " ) and ( creator_id != :ownerId )";
      }

      SQLQuery query = session.createSQLQuery("SELECT {osp_presentation_comment.*} " +
         " FROM osp_presentation_comment {osp_presentation_comment}, osp_presentation p " +
         " WHERE {osp_presentation_comment}.presentation_id = p.id and " +
         " (visibility = " + PresentationComment.VISABILITY_PUBLIC + " or " +
         "  visibility = " + PresentationComment.VISABILITY_SHARED +
         includeOwnerCondition + " and " +
         "  p.owner_id = :ownerId " +
         " ORDER BY " + orderBy + " " + sortBy.getDirection());

      query.addEntity("osp_presentation_comment", PresentationComment.class);
      query.setString("ownerId", owner.getId().getValue());

      try {
         return query.list();
      } catch (HibernateException e) {
         logger.error("",e);
         throw new OspException(e);
      } 
   }

   public List getOwnerComments(Agent owner, String toolId, CommentSortBy sortBy) {
      return  getOwnerComments(owner, toolId, sortBy, false);
   }

   public List getOwnerComments(Agent owner, String toolId, CommentSortBy sortBy, boolean excludeOwner) {
      String orderBy = sortBy.getSortByColumn();

      if (orderBy.startsWith("owner_id") || orderBy.startsWith("name")) {
         orderBy = "p." + orderBy;
      } else {
         orderBy = "{osp_presentation_comment}." + orderBy;
      }

      Session session = getSession();
      String includeOwnerCondition = "";
      if (!excludeOwner) {
         includeOwnerCondition = " or creator_id = :ownerId ) ";
      } else {
         includeOwnerCondition = " ) and ( creator_id != :ownerId )";
      }

      StringBuilder queryBuf = new StringBuilder( "SELECT {osp_presentation_comment.*} " +
         " FROM osp_presentation_comment {osp_presentation_comment}, osp_presentation p " +
         " WHERE {osp_presentation_comment}.presentation_id = p.id and " );
                                              
      if ( ! isOnWorkspaceTab() ) {
         queryBuf.append( " p.tool_id = :toolId and " );
      }
      
      queryBuf.append( " (visibility = " + PresentationComment.VISABILITY_PUBLIC + " or " );
      queryBuf.append( "  visibility = " + PresentationComment.VISABILITY_SHARED );
      queryBuf.append( includeOwnerCondition + " and " );
      queryBuf.append( "  p.owner_id = :ownerId " );
      queryBuf.append( " ORDER BY " + orderBy + " " + sortBy.getDirection() );
         
      SQLQuery query = session.createSQLQuery( queryBuf.toString() );

      query.addEntity("osp_presentation_comment", PresentationComment.class);
      if ( ! isOnWorkspaceTab() ) {
         query.setString("toolId", toolId);
      }
      query.setString("ownerId", owner.getId().getValue());

      try {
         return query.list();
      } catch (HibernateException e) {
         logger.error("",e);
         throw new OspException(e);
      } 
   }


   public List getCreatorComments(Agent creator, CommentSortBy sortBy) {
      String orderBy = sortBy.getSortByColumn();

      if (orderBy.startsWith("owner_id") || orderBy.startsWith("name")) {
         orderBy = "p." + orderBy;
      } else {
         orderBy = "{osp_presentation_comment}." + orderBy;
      }

      String queryString = "SELECT {osp_presentation_comment.*} " +
         " FROM osp_presentation_comment {osp_presentation_comment}, osp_presentation p " +
         " WHERE {osp_presentation_comment}.presentation_id = p.id and " +
         " creator_id = :creatorId" +
         " ORDER BY " + orderBy + " " + sortBy.getDirection();

      Session session = getSession();

      SQLQuery query = session.createSQLQuery(queryString);

      query.addEntity("osp_presentation_comment", PresentationComment.class);
      query.setString("creatorId", creator.getId().getValue());

      try {
         return query.list();
      } catch (HibernateException e) {
         logger.error("",e);
         throw new OspException(e);
      } 
   }

   public List getCreatorComments(Agent creator, String toolId, CommentSortBy sortBy) {
      String orderBy = sortBy.getSortByColumn();

      if (orderBy.startsWith("owner_id") || orderBy.startsWith("name")) {
         orderBy = "p." + orderBy;
      } else {
         orderBy = "{osp_presentation_comment}." + orderBy;
      }

      StringBuilder queryBuf = new StringBuilder( "SELECT {osp_presentation_comment.*} " +
         " FROM osp_presentation_comment {osp_presentation_comment}, osp_presentation p " +
            " WHERE {osp_presentation_comment}.presentation_id = p.id and " );
            
      if ( !isOnWorkspaceTab() ) {
         queryBuf.append( " tool_id = :toolId and" );
      }
      queryBuf.append( " creator_id = :creatorId" );
      queryBuf.append( " ORDER BY " + orderBy + " " + sortBy.getDirection() );

      Session session = getSession();

      SQLQuery query = session.createSQLQuery( queryBuf.toString() );
      
      query.addEntity("osp_presentation_comment", PresentationComment.class);
      if ( !isOnWorkspaceTab() ) {
         query.setString("toolId", toolId);
      }
      query.setString("creatorId", creator.getId().getValue());

      try {
         return query.list();
      } catch (HibernateException e) {
         logger.error("",e);
         throw new OspException(e);
      } 
   }

   public boolean isGlobal() {
      String siteId = getWorksiteManager().getCurrentWorksiteId().getValue();

      if (getGlobalSites().contains(siteId)) {
         return true;
      }

      Site site = getWorksiteManager().getSite(siteId);
      if (site.getType() != null && getGlobalSiteTypes().contains(site.getType())) {
         return true;
      }

      return false;
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }


   public Collection getPresentationItems(Id artifactId) {
      Session session = getSession();

      SQLQuery query = session.createSQLQuery("SELECT {osp_presentation.*} " +
         " FROM osp_presentation {osp_presentation}, osp_presentation_item pi " +
         " WHERE {osp_presentation}.id = pi.presentation_id and pi.artifact_id = :artifactId");

      query.addEntity("osp_presentation", Presentation.class);
      query.setString("artifactId", artifactId.getValue());

      try {
         return query.list();
      } catch (HibernateException e) {
         logger.error("",e);
         throw new OspException(e);
      }
   }

   public Collection getPresentationsBasedOnTemplateFileRef(Id artifactId) {
      Session session = getSession();

      try {
         SQLQuery query = session.createSQLQuery("SELECT {osp_presentation.*} " +
            " FROM osp_presentation {osp_presentation}, osp_template_file_ref tfr" +
            " WHERE {osp_presentation}.template_id = tfr.template_id and tfr.file_id = :artifactId");

         query.addEntity("osp_presentation", Presentation.class);
         query.setString("artifactId", artifactId.getValue());

         Collection tfr = query.list();
         query = session.createSQLQuery("SELECT {osp_presentation.*} " +
            " FROM osp_presentation {osp_presentation}, osp_presentation_template templ " +
            " WHERE {osp_presentation}.template_id = templ.id and (templ.renderer = :artifactId " +
            "       or templ.propertyPage = :artifactId)");
         
         query.addEntity("osp_presentation", Presentation.class);
         query.setString("artifactId", artifactId.getValue());
         tfr.addAll(query.list());
         return tfr;
      } catch (HibernateException e) {
         logger.error("",e);
         throw new OspException(e);
      } 
   }

   public Collection findPresentationsByTool(Id id) {
      return getHibernateTemplate().find("from Presentation where tool_id=?", id.getValue());
   }

   public void deleteArtifactReference(Id artifactId) {
      String statement = "delete from osp_presentation_item where artifact_id = ?";
      deleteByStatementHelper(statement, artifactId);
   }

   public PresentationTemplate copyTemplate(Id templateId) {
      return copyTemplate(templateId, ToolManager.getCurrentPlacement().getContext(), true, true);
   }

   public String packageTemplateForExport(Id templateId, OutputStream os) throws IOException {
      getAuthzManager().checkPermission(PresentationFunctionConstants.EXPORT_TEMPLATE,
         templateId);
      return packageTemplateForExportInternal(templateId, os);
   }

   protected String packageTemplateForExportInternal(Id templateId, OutputStream os) throws IOException {
      PresentationTemplate oldTemplate = this.getPresentationTemplate(templateId);
      
      String filename = oldTemplate.getName() + ".zip";
      
      Set items = oldTemplate.getItems();
      Set files = oldTemplate.getFiles();

      CheckedOutputStream checksum = new CheckedOutputStream(os, new Adler32());
      ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(checksum));

      List existingEntries = new ArrayList();
      storeTemplateFile(zos, oldTemplate.getRenderer(), existingEntries);
      storeTemplateFile(zos, oldTemplate.getPropertyPage(), existingEntries);
      
      Collection exportedFormIds = new ArrayList();
      
      if (oldTemplate.getPropertyFormType() != null) {
         ReadableObjectHome propFormHome =
            (ReadableObjectHome)getHomeFactory().getHome(oldTemplate.getPropertyFormType().getValue());
   
         if (propFormHome instanceof StructuredArtifactHomeInterface &&
               !exportedFormIds.contains(propFormHome.getType().getId().getValue())) {
            // need to store the form
            storeFormInZip(zos, propFormHome);
            exportedFormIds.add(propFormHome.getType().getId().getValue());
         }
      }
      
      // go through each associated file... store them...
      if (files != null) {
         for (Iterator i=files.iterator();i.hasNext();) {
            TemplateFileRef fileRef = (TemplateFileRef)i.next();
            storeTemplateFile(zos, getIdManager().getId(fileRef.getFileId()), existingEntries);
         }
         oldTemplate.setFiles(new HashSet(files));
      }

      if (items != null) {
         oldTemplate.setItems(new HashSet(items));
         for (Iterator i=oldTemplate.getItems().iterator();i.hasNext();) {
            PresentationItemDefinition item = (PresentationItemDefinition)i.next();

            ReadableObjectHome home =
               (ReadableObjectHome)getHomeFactory().getHome(item.getType());

            if (home != null) {
               item.setExternalType(home.getExternalType());
            }

            if (home instanceof StructuredArtifactHomeInterface &&
                  !exportedFormIds.contains(home.getType().getId().getValue())) {
               // need to store the form
               storeFormInZip(zos, home);
               exportedFormIds.add(home.getType().getId().getValue());
            }

            if (item.getMimeTypes() != null) {
               item.setMimeTypes(new HashSet(item.getMimeTypes()));
            }
         }
      }

      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(bos);
      oos.writeObject(oldTemplate);

      storeFileInZip(zos, new ByteArrayInputStream(bos.toByteArray()),
         "template");

      oldTemplate.setFiles(files);
      oldTemplate.setItems(items);

      bos.close();

      zos.finish();
      zos.flush();
      
      return filename;
   }

   protected void storeFormInZip(ZipOutputStream zos, ReadableObjectHome home) throws IOException {

      ZipEntry newfileEntry = new ZipEntry("forms/" + home.getType().getId().getValue() + ".form");

      zos.putNextEntry(newfileEntry);

      getStructuredArtifactDefinitionManager().packageFormForExport(home.getType().getId().getValue(), zos, false);

      zos.closeEntry();
   }

   public PresentationTemplate uploadTemplate(String templateFileName, String toContext,
                                              InputStream zipFileStream) throws IOException {
      try {
         return uploadTemplate(templateFileName, toContext, zipFileStream, true);
      }
      catch (InvalidUploadException exp) {
         throw exp;
      }
      catch (Exception exp) {
         throw new InvalidUploadException("Invalid template file.", exp, "uploadedTemplate");
      }
   }

   protected PresentationTemplate uploadTemplate(String templateFileName, String toContext,
                                              InputStream zipFileStream, boolean checkAuthz) throws IOException {

      if (checkAuthz) {
         getAuthzManager().checkPermission(PresentationFunctionConstants.CREATE_TEMPLATE,
            getIdManager().getId(toContext));
      }

      ZipInputStream zis = new UncloseableZipInputStream(zipFileStream);

      ZipEntry currentEntry = zis.getNextEntry();
      Hashtable<Id, Id> fileMap = new Hashtable<Id, Id>();
      Hashtable<String, String> formMap = new Hashtable<String, String>();
      PresentationTemplate template = null;

      String tempDirName = getIdManager().createId().getValue();

      boolean itWorked = false;

      try {
         ContentCollectionEdit fileParent = getTemplateFileDir(tempDirName);
         boolean gotFile = false;
         
         while (currentEntry != null) {
            logger.debug("current entry name: " + currentEntry.getName());

            if (currentEntry.getName().equals("template")) {
               try {
                  template = processTemplate(zis);
               } catch (ClassNotFoundException e) {
                  logger.error("Class not found loading template", e);
                  throw new OspException(e);
               }
            }
            else if (!currentEntry.isDirectory()) {
               if (currentEntry.getName().startsWith("forms/")) {
                  processTemplateForm(currentEntry, zis, formMap,
                        getIdManager().getId(toContext));
               }
               else {
                  gotFile = true;
                  processTemplateFile(currentEntry, zis, fileMap, fileParent);
               }
            }

            zis.closeEntry();
            currentEntry = zis.getNextEntry();
         }

         if (template == null) {
            throw new InvalidUploadException("Template zip must contain template definition", "uploadedTemplate");
         }
         
         if (gotFile) {
            fileParent.getPropertiesEdit().addProperty(
                  ResourceProperties.PROP_DISPLAY_NAME, template.getName());
            getContentHosting().commitCollection(fileParent);
         }
         else {
            getContentHosting().cancelCollection(fileParent);
         }

         template.setId(null);
         template.setOwner(getAuthnManager().getAgent());
         template.setRenderer((Id)fileMap.get(template.getRenderer()));
         
         if (template.getPropertyFormType() != null)
            template.setPropertyFormType(getIdManager().getId((String)formMap.get(template.getPropertyFormType().getValue())));
         
         
         //template.setToolId(toolConfiguration.getId());
         template.setSiteId(toContext);
         template.setPublished(false);

         if (template.getPropertyPage() != null) {
            template.setPropertyPage((Id)fileMap.get(template.getPropertyPage()));
         }

         for (Iterator i=template.getFiles().iterator();i.hasNext();) {
            TemplateFileRef ref = (TemplateFileRef)i.next();
            Id refFileId = getIdManager().getId(ref.getFileId());
            ref.setFileId(((Id)fileMap.get(refFileId)).getValue());
            ref.setPresentationTemplate(template);
         }

         int index = 100;
         for (Iterator i=template.getItems().iterator();i.hasNext();) {
            PresentationItemDefinition item = (PresentationItemDefinition)i.next();

            if (item.getSequence() == 0) {
               item.setSequence(index);
            }
            index++;

            if (formMap.containsKey(item.getType())) {
               item.setType((String) formMap.get(item.getType()));
            }

            item.setId(null);
            item.setNewId(idManager.createId());
            item.setPresentationTemplate(template);
         }
         template.orderItemDefs();

         substituteIds(fileMap);

         storeTemplate(template, checkAuthz);
         //TODO: 20050810 ContentHosting
         //fileParent.persistent().rename(getUniqueTemplateName(fileParent, template.getName()));
         itWorked = true;
         return template;
      } catch (Exception exp) {
         throw new RuntimeException(exp);
      }
      finally {
         //if (!itWorked) {
         //   fileParent.persistent().destroy();
         //}
               
         try {
            zis.closeEntry();
         }
         catch (IOException e) {
            logger.error("", e);
         }
      }
   }

// TODO: 20050810 ContentHosting
   /*
   protected String getUniqueTemplateName(Node currentNode, String name) {
      Node parent = currentNode.getParent();
      String newName = name;
      int count = 1;

      while (parent.hasChild(newName)) {
         count++;
         newName = name + "_" + count;
      }

      return newName;
   }
*/
   protected void processTemplateForm(ZipEntry currentEntry, ZipInputStream zis, Map formMap, Id worksite)
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

   protected void processTemplateFile(ZipEntry currentEntry, ZipInputStream zis,
                                      Hashtable fileMap, ContentCollection fileParent) throws IOException {

      File file = new File(currentEntry.getName());

      MimeType mimeType = new MimeType(file.getParentFile().getParentFile().getParentFile().getName(),
         file.getParentFile().getParentFile().getName());

      String contentType = mimeType.getValue();

      Id oldId = getIdManager().getId(file.getParentFile().getName());

      try {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         int c = zis.read();
   
         while (c != -1) {
            bos.write(c);
            c = zis.read();
         }
         
         String fileName = findUniqueFileName(fileParent.getId(), file.getName());
         ResourcePropertiesEdit resourceProperties = getContentHosting().newResourceProperties();
         resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, fileName);
         ContentResource /*Edit*/ resource;

         resource = getContentHosting().addResource(fileParent.getId() + fileName, contentType, bos.toByteArray(),
            resourceProperties, NotificationService.NOTI_NONE);
         
         Id newId = getIdManager().getId(getContentHosting().getUuid(resource.getId()));
         fileMap.put(oldId, newId);
      }
      catch (Exception exp) {
         throw new RuntimeException(exp);
      }
   }

   protected String findUniqueFileName(String id, String name) throws TypeException, PermissionException {
      String orig = name;
      String testId = id + name;
      int current = 0;
      while (resourceExists(testId)) {
         current++;
         int dotPos = orig.lastIndexOf('.');
         if (dotPos == -1) {
            name = orig + current;
         }
         else {
            name = orig.substring(0, dotPos) + "-" + current + orig.substring(dotPos);
         }
         testId = id + name;
      }

      return name;
   }

   protected boolean resourceExists(String returned) throws TypeException, PermissionException {
      try {
         return getContentHosting().getResource(returned) != null;
      } catch (IdUnusedException e) {
         return false;
      }
   }

   protected PresentationTemplate processTemplate(ZipInputStream zis) throws IOException, ClassNotFoundException {
      ObjectInputStream oos = new ObjectInputStream(zis);

      return (PresentationTemplate)oos.readObject();
   }

   protected void storeTemplateFile(ZipOutputStream zos, Id fileId, List existingEntries) throws IOException {
      if (fileId == null) {
         return;
      }
      //TODO: Need to add file to security authorizer
      Node oldNode = getNode(fileId);
      String newName = oldNode.getName();
      String cleanedName = newName.substring(newName.lastIndexOf('\\')+1);
      
      if (!existingEntries.contains(fileId)) {
         existingEntries.add(fileId);
         storeFileInZip(zos, oldNode.getInputStream(),
               oldNode.getMimeType().getValue() + File.separator +
               fileId.getValue() + File.separator + cleanedName);
      }
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

   protected WritableObjectHome getFileHome() {
      return fileHome;
   }

   protected void substituteIds(Hashtable fileMap) {
      // go through each file....  all text mime types, do the subst on
	      
      for (Iterator i=fileMap.values().iterator();i.hasNext();) {
         Node node = getNode((Id)i.next());

         if (node.getMimeType().getPrimaryType().equals("text")) {
            try {
               processFile(node, fileMap);
            } catch (IOException e) {
               logger.error("error processing file.", e);
               throw new OspException(e);
            }
         }
      }
      

   }
   
   protected void processFile(Node node, Hashtable fileMap) throws IOException {
      // read file into StringBuilder
      InputStream is = null;
      try {
         is = node.getInputStream();

         byte[] buffer = new byte[1024 * 10];

         StringBuilder sb = new StringBuilder();

         int read = is.read(buffer);

         while (read != -1) {
            sb.append(new String(buffer, 0, read));
            read = is.read(buffer);
         }

         is.close();
         is = null;

         boolean changed = false;

         // subst.
         for (Iterator i=fileMap.entrySet().iterator();i.hasNext();) {
        	 Entry entry = (Entry) i.next();

            if (substituteFileId(sb, (Id) entry.getKey(), (Id)entry.getValue())) {
               changed = true;
            }
         }

         if (changed) {
            // write StringBuilder out
            ContentResourceEdit cre = (ContentResourceEdit)node.getResource();
            cre.setContent(sb.toString().getBytes());
            getContentHosting().commitResource(cre);
         }
      } catch (OverQuotaException e) {
         // TODO Better error message here?
         logger.error("", e);
      }
      catch (ServerOverloadException e) {
         // TODO Better error message here?
         logger.error("", e);
      } finally {
         try {
            if (is != null) {
               is.close();
            }
         } catch (Exception e){
            logger.warn("",e);
         }
      }
   }

   protected boolean substituteFileId(StringBuilder sb, Id oldId, Id newId) {
      int index = sb.indexOf(oldId.getValue());
      boolean changed = false;
      while (index != -1) {
         sb.replace(index, index + oldId.getValue().length(), newId.getValue());
         changed = true;
         index = sb.indexOf(oldId.getValue());
      }

      return changed;
   }

   protected void handleChildren(PresentationTemplate oldTemplate, ContentCollection templateParent, Hashtable fileMap) {
      Set files = oldTemplate.getFiles();
      oldTemplate.setFiles(new HashSet());
      for (Iterator i=files.iterator();i.hasNext();) {
         TemplateFileRef fileRef = (TemplateFileRef)i.next();

         fileRef.setId(null);
         fileRef.setFileId(
               copyTemplateFile(templateParent, 
                     getIdManager().getId(fileRef.getFileId()), fileMap).getValue());
         oldTemplate.getFiles().add(fileRef);
      }

      Set items = oldTemplate.getItems();
      oldTemplate.setItems(new HashSet());
      for (Iterator i=items.iterator();i.hasNext();) {
         PresentationItemDefinition itemDef = (PresentationItemDefinition)i.next();
         itemDef.setId(null);
         Set itemMimeTypes = new HashSet();

         for (Iterator j=itemDef.getMimeTypes().iterator();j.hasNext();) {
            ItemDefinitionMimeType mimeType = (ItemDefinitionMimeType)j.next();

            itemMimeTypes.add(
               new ItemDefinitionMimeType(mimeType.getPrimary(), mimeType.getSecondary()));
         }

         itemDef.setMimeTypes(itemMimeTypes);
         oldTemplate.getItems().add(itemDef);
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
    * See if the current tab is the workspace tab.
    * @return true if we are currently on the "My Workspace" tab.
    */
   private boolean isOnWorkspaceTab()
   {
      return SiteService.isUserSite(ToolManager.getCurrentPlacement().getContext());
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
   protected ContentCollectionEdit getTemplateFileDir(String origName) throws TypeException, IdUnusedException, PermissionException, IdUsedException, IdInvalidException, InconsistentException {
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

   protected Id copyTemplateFile(ContentCollection templateParent, Id oldFileId, Hashtable fileMap) {
      if (oldFileId == null) {
         return null;
      }

      Node oldNode = (Node) getNode(oldFileId);

      String newName = oldNode.getName();
//    TODO: 20050810 ContentHosting
      /*
      int index = 1;
      while (templateParent.hasChild(newName)) {
         newName = "copy_" + index + "_" + oldNode.getName();
         index++;
      }

      RepositoryNode newNode = oldNode.copy(oldNode.getName(), templateParent.getId());

      fileMap.put(oldFileId, newNode.getId());

      return newNode.getId();
      */
      return null;
   }
   
   public Document createDocument(Presentation presentation) {
      // build up the document from objects...
      viewingPresentation(presentation);

      Collection items = presentation.getItems();

      Element root = new Element("ospiPresentation");
      
      Element name = new Element("name");
      name.setText(presentation.getName());
      root.addContent(name);
      
      Element description = new Element("description");
      description.setText(presentation.getDescription());
      root.addContent(description);
      
      Element site = new Element("siteId");
      site.setText(presentation.getSiteId());
      root.addContent(site);

      for (Iterator i = items.iterator(); i.hasNext();) {
         PresentationItem item = (PresentationItem) i.next();
         Element itemElement = root.getChild(item.getDefinition().getName());

         if (itemElement == null) {
            itemElement = new Element(item.getDefinition().getName());
            root.addContent(itemElement);
         }
         
         Artifact art = getPresentationItem(item.getDefinition().getType(), item.getArtifactId(), presentation);

         if (art != null && art.getHome() instanceof PresentableObjectHome) {
            PresentableObjectHome home = (PresentableObjectHome) art.getHome();
            Element node = home.getArtifactAsXml(art, PresentationContentEntityProducer.PRODUCER_NAME, presentation.getSiteId(), presentation.getId().getValue());
            node.setName("artifact");
            itemElement.addContent(node);
         }
      }

      if (presentation.getProperties() != null) {
         Element presProperties = new Element("deprecatedPresentationProperties");
         presProperties.addContent((Element) presentation.getProperties().currentElement().clone());
         root.addContent(presProperties);
      }

      Node propNode = getNode(presentation.getPropertyForm());
      if (presentation.getPropertyForm() != null && propNode != null) {
         Element presProperties = new Element("presentationProperties");
         if (propNode.getResource() != null) {
	         String ref = propNode.getResource().getReference();
	         String uuid = presentation.getPropertyForm().getValue();
	         String id = propNode.getResource().getId();
	         presProperties.setAttribute("formRef", ref);
	         presProperties.setAttribute("formId", id);
	         presProperties.setAttribute("formUuid", uuid);
         }
                  
         Document doc = new Document();
         SAXBuilder saxBuilder = new SAXBuilder();
         saxBuilder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // SAK-23131
         try {
            doc = saxBuilder.build(propNode.getInputStream());
         } catch (JDOMException e) {
            throw new OspException(e);
         } catch (IOException e) {
            throw new OspException(e);
         }
         
         
         presProperties.addContent((Element)doc.getRootElement().clone());
         root.addContent(presProperties);
      }
      
      if (presentation.getTemplate().getFiles() != null) {
         Element presFiles = new Element("presentationFiles");
         root.addContent(presFiles);

         for (Iterator files = presentation.getTemplate().getFiles().iterator(); files.hasNext(); ){
            TemplateFileRef fileRef = (TemplateFileRef) files.next();
            presFiles.addContent(getFileRefAsXml(presentation, fileRef));
         }
      }

      return new Document(root);
   }

   public Collection getAllPresentationsForWarehouse() {
      Collection presentations = getHibernateTemplate().findByNamedQuery("findPortfolios");
      //need to load up all of the pages, since hibernate isn't linking them.
      for (Iterator i = presentations.iterator(); i.hasNext();) {
         Presentation presentation = (Presentation) i.next();
         List pages = getPresentationPagesByPresentation(presentation.getId());
         presentation.setPages(pages);
      }
      
      return presentations;
   }

   public Collection getAllPresentationTemplates() {
      HibernateCallback callback = new HibernateCallback() {
         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            List templates = getHibernateTemplate().findByNamedQuery("findTemplates");
            for (Iterator i = templates.iterator();i.hasNext();) {
               PresentationTemplate template = (PresentationTemplate) i.next();
               for (Iterator j = template.getItems().iterator();j.hasNext();) {
                  PresentationItemDefinition itemDef = (PresentationItemDefinition) j.next();
                  itemDef.getMimeTypes().size();
               }
            }
            return templates;
         }
      };

      try {
         return (Collection) getHibernateTemplate().execute(callback);
      } catch (HibernateObjectRetrievalFailureException e) {
         logger.debug(e);
         return new ArrayList();
      }
   }
   public Collection getAllPresentationLayouts() {

            return getHibernateTemplate().findByNamedQuery("findLayouts");

   }
   public void viewingPresentation(Presentation presentation) {
      // go through and setup all pres and pres template files for read access
      List readableFiles = new ArrayList();
      Collection artifacts = presentation.getItems();

      for (Iterator i=artifacts.iterator();i.hasNext();) {
         PresentationItem item = (PresentationItem) i.next();
         String id = getContentHosting().resolveUuid(item.getArtifactId().getValue());
         if (id != null) {
            readableFiles.add(getContentHosting().getReference(id));
         }              
      }

      if (presentation.getTemplate().getFiles() != null) {

         for (Iterator files = presentation.getTemplate().getFiles().iterator(); files.hasNext(); ){
            TemplateFileRef fileRef = (TemplateFileRef) files.next();
            String id = getContentHosting().resolveUuid(fileRef.getFileId());
            if (id != null) {
               readableFiles.add(getContentHosting().getReference(id));
            }
         }
      }

      String id = null;
      if (presentation.getTemplate() == null || presentation.getTemplate().getRenderer() == null) {
         setupPresItemDefinition(presentation);
      }
      	
      id = getContentHosting().resolveUuid(presentation.getTemplate().getRenderer().getValue());

      if (id != null) {
         readableFiles.add(getContentHosting().getReference(id));
      }
      
      //Files related to layouts
      List pages = null;
      if (presentation.getPages() != null) {
         pages = presentation.getPages();
      }
      else {
         pages = getPresentationPagesByPresentation(presentation.getId());
      }
      for (Iterator pagesIter = pages.iterator(); pagesIter.hasNext();) {
         PresentationPage page = (PresentationPage) pagesIter.next();
         String xhtmlFileId = getContentHosting().resolveUuid(page.getLayout().getXhtmlFileId().getValue());
         if (xhtmlFileId != null) {
            readableFiles.add(getContentHosting().getReference(xhtmlFileId));
         }
         if (page.getLayout().getPreviewImageId() != null) {
            String previewImageId = getContentHosting().resolveUuid(page.getLayout().getPreviewImageId().getValue());
            if (previewImageId != null) {
               readableFiles.add(getContentHosting().getReference(previewImageId));
            }
         }
         Style pageStyle = page.getStyle() != null ? page.getStyle() : page.getPresentation().getStyle();
         if (pageStyle != null && pageStyle.getStyleFile() != null) {
            String styleFileId = getContentHosting().resolveUuid(pageStyle.getStyleFile().getValue());
            readableFiles.add(getContentHosting().getReference(styleFileId));
         }
         
         for (Iterator regions = page.getRegions().iterator(); regions.hasNext();) {
            PresentationPageRegion region = (PresentationPageRegion) regions.next();
            for (Iterator items = region.getItems().iterator(); items.hasNext();) {
               PresentationPageItem pageItem = (PresentationPageItem) items.next();
               String itemId = getContentHosting().resolveUuid(pageItem.getValue());
               if (itemId != null) {
                  readableFiles.add(getContentHosting().getReference(itemId));
               }
            }
         }
      }
      
      if (presentation.getPropertyForm() != null) {
    	  String propform_uuid = presentation.getPropertyForm().getValue();
    	  String propform_id = getContentHosting().resolveUuid(propform_uuid);
    	  readableFiles.add(getContentHosting().getReference(propform_id));
      }

      getSecurityService().pushAdvisor(
         new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ, readableFiles));
   }

   protected Element getFileRefAsXml(Presentation presentation, TemplateFileRef fileRef) {
      Element fileRefElement = new Element(fileRef.getUsage());
      String fileId = fileRef.getFileId();
            
      Artifact art = getPresentationItem(fileRef.getFileType(), 
            getIdManager().getId(fileId), presentation);

      PresentableObjectHome home = (PresentableObjectHome) art.getHome();
      fileRefElement.addContent(home.getArtifactAsXml(art));
      return fileRefElement;
   }

   /** Save PresentationLog object to database.
    ** Portfolio Export will query the log immediately after write, so:
    **    - FlushMode is set to FLUSH_EAGER wtihin this method
    **    - auto-commit is set wtihin this method
    **/
   public synchronized void storePresentationLog(PresentationLog log) {
      try {
         int oldFlushMode = getHibernateTemplate().getFlushMode();
         boolean oldAutoCommit = getSession().connection().getAutoCommit();
         try {
            getHibernateTemplate().setFlushMode(getHibernateTemplate().FLUSH_EAGER);
            getSession().connection().setAutoCommit(true); 
            getHibernateTemplate().save(log);
         }
         catch (Exception e ) {
            logger.warn(e);
         }
         finally {
            getHibernateTemplate().setFlushMode(oldFlushMode);
            getSession().connection().setAutoCommit(oldAutoCommit); 
         }
      }
      catch (Exception e ) {
         logger.warn(e);
      }
   }
   
   /** findLogsByPresID
    ** Return Collection of PresentationLog objects corresponding
    ** to all requests to view given portfolio.
    **/
   public Collection findLogsByPresID(Id presID) {
      return getHibernateTemplate().findByNamedQuery("findLogsByPortfolio", presID.getValue());
   }

   /** findPresentationByLogID
    ** Return Presentation corresponding to given PresentationLog id
    **/
   public Presentation findPresentationByLogID(Id logID) {
      PresentationLog pLog = 
         (PresentationLog)getHibernateTemplate().findByNamedQuery("findPortfolioByLogID", logID).get(0);
      return getPresentation( pLog.getPresentation().getId(), false );
   }

   public TemplateFileRef getTemplateFileRef(Id refId) {
      return (TemplateFileRef) getHibernateTemplate().load(TemplateFileRef.class,  refId);
   }

   public void updateTemplateFileRef(TemplateFileRef ref) {
      getHibernateTemplate().saveOrUpdate(ref);
   }

   public void deleteTemplateFileRef(Id refId) {
      getHibernateTemplate().delete(getTemplateFileRef(refId));
   }


   public AuthenticationManager getAuthnManager() {
      return authnManager;
   }

   public void setAuthnManager(AuthenticationManager authnManager) {
      this.authnManager = authnManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public void setFileHome(WritableObjectHome fileHome) {
      this.fileHome = fileHome;
   }

   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }

   public LockManager getLockManager() {
      return lockManager;
   }

   public void setLockManager(LockManager lockManager) {
      this.lockManager = lockManager;
   }

   public ArtifactFinderManager getArtifactFinderManager() {
      return artifactFinderManager;
   }

   public void setArtifactFinderManager(ArtifactFinderManager artifactFinderManager) {
      this.artifactFinderManager = artifactFinderManager;
   }


   public void importResources(String fromContext, String toContext, List resourceIds) {
      Collection templates = findPublishedTemplatesBySite(fromContext);

      for (Iterator i=templates.iterator();i.hasNext();) {
         PresentationTemplate template = (PresentationTemplate)i.next();
         copyTemplate(template.getId(), toContext, false, false);
      }
   }

   protected PresentationTemplate copyTemplate(Id templateId, String toContext,
                                               boolean checkAuthz, boolean rename) {
      try {
         if (checkAuthz) {
            getAuthzManager().checkPermission(PresentationFunctionConstants.COPY_TEMPLATE, templateId);
         }

         ByteArrayOutputStream bos = new ByteArrayOutputStream();

         PresentationTemplate oldTemplate = this.getPresentationTemplate(templateId);

         packageTemplateForExportInternal(templateId, bos);

         ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());


         PresentationTemplate newTemplate = uploadTemplate(oldTemplate.getName() + ".zip",
            toContext, bis, false);

         if (rename) {
            newTemplate.setName(newTemplate.getName() + " Copy");
            storeTemplate(newTemplate, false);
         }
         return newTemplate;
      } catch (IOException e) {
         logger.error("", e);
         throw new OspException(e);
      }
   }

   public String packageForDownload(Map params, OutputStream out) throws IOException {

      String filename = "";
      if (params.get(TEMPLATE_ID_TAG) != null) {
         filename = packageTemplateForExport(getIdManager().getId(((String[])params.get(TEMPLATE_ID_TAG))[0]),
            out);
      }
      else if (params.get(PRESENTATION_ID_TAG) != null) {
         filename = packagePresentationForExport(getIdManager().getId(((String[])params.get(PRESENTATION_ID_TAG))[0]), out);
      }
      return filename;
   }

   protected String packagePresentationForExport(Id presentationId, OutputStream out) throws IOException {
      Presentation presentation = getLightweightPresentation(presentationId);
      
      String filename = presentation.getName() + ".zip";

      if (!presentation.getOwner().equals(getAuthnManager().getAgent())) {
         throw new AuthorizationFailedException("Only the presentation owner can export a presentation");
      }

      File tempDir = new File(tempPresDownloadDir);
      if (!tempDir.exists()) {
         tempDir.mkdirs();
      }

      // Create log of presentation view and use log id as cluster-independent secretExportKey
      PresentationLog pLog = new PresentationLog();
      pLog.setPresentation(presentation);
      pLog.setViewDate(new java.util.Date());
      pLog.setViewer(presentation.getOwner()); // assumes only owner can download
      storePresentationLog(pLog);
      String secretExportKey = pLog.getId().getValue();     
      
      String url = presentation.getExternalUri(downloadExternalUri) + "&secretExportKey=" + secretExportKey;
      
      File tempDirectory = new File(tempDir, secretExportKey);

      PresentationExport export = new PresentationExport(
        url, tempDirectory.getPath());

      try {
         export.run();
         export.createZip(out);
      }
      finally {
         export.deleteTemp();
      }
      return filename;
   }

   public Node getNode(Id artifactId) {
      if (artifactId == null) {
         return null;
      }
   
      String id = getContentHosting().resolveUuid(artifactId.getValue());
      if (id == null) {
         return null;
      }

      try {
         String ref = getContentHosting().getReference(id);
         getSecurityService().pushAdvisor(
               new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ, ref));
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

   public Node getNode(Reference ref, Presentation presentation) {
     return getNode(getNode(ref), presentation);
   }

   public Node getNode(Id nodeId, Presentation presentation) {
      Node node = getNode(nodeId);
      return getNode(node, presentation);
   }

   public Node getNode(Id artifactId, PresentationLayout layout) {
      String id = getContentHosting().resolveUuid(artifactId.getValue());
      String ref = getContentHosting().getReference(id);
      getSecurityService().pushAdvisor(
            new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ, ref));
      Node node = getNode(artifactId);
      return getNode(node, layout);
   }

   protected Node getNode(Node node, Presentation presentation) {
      if (node == null) {
         return null;
      }

      ContentResource wrapped = new ContentEntityWrapper(node.getResource(),
            buildRef(presentation.getSiteId(), presentation.getId().getValue(), node.getResource()));

      return new Node(node.getId(), wrapped, node.getTechnicalMetadata().getOwner());
   }

   protected Node getNode(Node node, PresentationLayout layout) {
      if (node == null) {
         return null;
      }
      String siteId = layout.getSiteId();
      if (siteId == null) {
         siteId = "~admin";
      }
      ContentResource wrapped = new ContentEntityWrapper(node.getResource(),
            buildLayoutRef(layout.getSiteId(), layout.getId().getValue(), node.getResource()));

      return new Node(node.getId(), wrapped, node.getTechnicalMetadata().getOwner());
   }

   protected String buildRef(String siteId, String contextId, ContentResource resource) {
      return ContentEntityUtil.getInstance().buildRef(
         PresentationContentEntityProducer.PRODUCER_NAME, siteId, contextId, resource.getReference());
   }

   protected String buildLayoutRef(String siteId, String contextId, ContentResource resource) {
      return ContentEntityUtil.getInstance().buildRef(
         LayoutEntityProducer.PRODUCER_NAME, siteId, contextId, resource.getReference());
   }

   public Node getNode(Reference ref) {
      String nodeId = getContentHosting().getUuid(ref.getId());

      return getNode(getIdManager().getId(nodeId));
   }

   /**
    ** Given a PresentationItem, load the corresponding ContentResourceArtifact
    **
    ** @param item PresentationItem
    ** @return corresponding ContentResourceArtifact
    **/
   public ContentResourceArtifact loadArtifactForItem(PresentationItem item) {
      String contentId = contentHosting.resolveUuid( item.getArtifactId().getValue() );
      getSecurityService().pushAdvisor(
            new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ,
                                        contentHosting.getReference(contentId)));
      try
      {
         ContentResource resource = contentHosting.getResource(contentId);
         Agent resourceOwner = getAgentManager().getAgent(resource.getProperties().getProperty(ResourceProperties.PROP_CREATOR));
         Id resourceId = getIdManager().getId(contentHosting.getUuid(resource.getId()));
         return new ContentResourceArtifact(resource,resourceId,resourceOwner);
      }  
      catch (Exception e) {
         logger.warn(this+e.toString());
         return null;
      }
   }
   
   public Collection loadArtifactsForItemDef(PresentationItemDefinition itemDef, Agent agent) {
      ArtifactFinder artifactFinder = getArtifactFinderManager().getArtifactFinderByType(itemDef.getType());
      // for performance, don't do a deep load, only load id, displayName
      artifactFinder.setLoadArtifacts(false);

      if (itemDef.getHasMimeTypes()) {
         Collection items = new ArrayList();
         if (itemDef.getMimeTypes().size() > 0) {
            for (Iterator i=itemDef.getMimeTypes().iterator();i.hasNext();) {
               ItemDefinitionMimeType mimeType = (ItemDefinitionMimeType)i.next();
               items.addAll(artifactFinder.findByOwnerAndType(agent.getId(), itemDef.getType(),
                  new MimeType(mimeType.getPrimary(), mimeType.getSecondary())));
            }
         }
         else {
            return artifactFinder.findByOwnerAndType(agent.getId(), itemDef.getType());
         }

         return items;
      }
      else {
         return artifactFinder.findByOwnerAndType(agent.getId(), itemDef.getType());
      }
   }

   public void cleanupTool(Id toolId) {
      for (Iterator i=findPresentationsByTool(toolId).iterator();i.hasNext();){
         Presentation presentation = (Presentation) i.next();
         deletePresentation(presentation.getId());
      }
   }

   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   public SecurityService getSecurityService() {
      return securityService;
   }

   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }

   public Collection findPublishedLayouts(String siteId) {
      /*
      return getHibernateTemplate().find(
            "from PresentationLayout where globalState=? and owner_id!=? and site_id=? Order by name",
            new Object[]{Integer.valueOf(PresentationLayout.STATE_PUBLISHED), 
                  getAuthnManager().getAgent().getId().getValue(), siteId});
      */
      return new ArrayList();
   }

   
   
   public Collection findLayoutsByOwner(Agent owner, String siteId) {
      return getHibernateTemplate().findByNamedQuery("findLayoutsByOwner",
            new Object[]{owner, siteId});
   }

   public Collection findMyGlobalLayouts() {
      return getHibernateTemplate().findByNamedQuery("findPublishedLayouts",
         new Object[]{Integer.valueOf(PresentationLayout.STATE_PUBLISHED)});
   }

   public Collection findAllGlobalLayouts() {
      return getHibernateTemplate().findByNamedQuery("findGlobalLayouts",
         new Object[]{Integer.valueOf(PresentationLayout.STATE_PUBLISHED), Integer.valueOf(PresentationLayout.STATE_WAITING_APPROVAL)});
   }
   
   public PresentationLayout storeLayout (PresentationLayout layout) {
      return storeLayout(layout, true);
   }
   
   public PresentationLayout storeLayout (PresentationLayout layout, boolean checkAuthz) {
      layout.setModified(new Date(System.currentTimeMillis()));

      boolean newLayout = (layout.getId() == null);

      if (newLayout) {
          layout.setCreated(new Date(System.currentTimeMillis()));

          if (checkAuthz) {
             getAuthzManager().checkPermission(PresentationFunctionConstants.CREATE_LAYOUT,
                getIdManager().getId(layout.getSiteId()));
          }
       } else {
          if (checkAuthz) {
             getAuthzManager().checkPermission(PresentationFunctionConstants.EDIT_LAYOUT,
                   layout.getId());
          }
       }
      getHibernateTemplate().saveOrUpdate(layout);
      lockLayoutFiles(layout);

      if (newLayout) {
    	  eventService.postEvent(EventConstants.EVENT_LAYOUT_ADD,layout.getId().getValue());
      } else {
    	  eventService.postEvent(EventConstants.EVENT_LAYOUT_REVISE,layout.getId().getValue());
      }

      return layout;
   }
   
   protected void lockLayoutFiles(PresentationLayout layout){
      clearLocks(layout.getId());
      getLockManager().lockObject(layout.getXhtmlFileId().getValue(), 
           layout.getId().getValue(), "saving a presentation layout", true);
      
      if (layout.getPreviewImageId() != null) {
         getLockManager().lockObject(layout.getPreviewImageId().getValue(), 
              layout.getId().getValue(), "saving a presentation layout", true);
      }
   }
   
   protected void lockStyleFiles(Style style){
      clearLocks(style.getId());
      getLockManager().lockObject(style.getStyleFile().getValue(), 
            style.getId().getValue(), "saving a style", true);
      
   }
   
   public PresentationLayout getPresentationLayout(Id id) {
      return (PresentationLayout) getHibernateTemplate().get(PresentationLayout.class, id);
   }
   
   public List getPresentationPagesByPresentation(Id presentationId) {
      return getHibernateTemplate().findByNamedQuery(
            "findPortfolioPagesByPortfolio",
            new Object[]{presentationId});
   }

   public void deletePresentationLayout(final Id id) {
      PresentationLayout layout = getPresentationLayout(id);
      getAuthzManager().checkPermission(PresentationFunctionConstants.DELETE_LAYOUT, layout.getId());
      clearLocks(layout.getId());
      
      //TODO handle things that are using this layout
      // first delete all presentations that use this template
      // this will delete all authorization as well
      //Collection presentations = getHibernateTemplate().find("from Presentation where template_id=?", id.getValue(), Hibernate.STRING);
      //for (Iterator i = presentations.iterator(); i.hasNext();) {
      //   Presentation presentation = (Presentation) i.next();
      //   deletePresentation(presentation.getId(), false);
      //}

      HibernateCallback callback = new HibernateCallback() {

         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            
            PresentationLayout layout =
               (PresentationLayout) session.load(PresentationLayout.class, id);
            session.delete(layout);
            return null;
         }

      };
      getHibernateTemplate().execute(callback);
      eventService.postEvent(EventConstants.EVENT_LAYOUT_DELETE,layout.getId().getValue() );
   }
   
   public PresentationPage getPresentationPage(Id id) {
      PresentationPage page = (PresentationPage) getHibernateTemplate().get(PresentationPage.class, id);

      if(page != null) {
         for (Iterator i=page.getRegions().iterator();i.hasNext();) {
            PresentationPageRegion region = (PresentationPageRegion) i.next();
            for (Iterator j=region.getItems().iterator();j.hasNext();) {
               PresentationPageItem item = (PresentationPageItem) j.next();
               item.getProperties().size();
            }
         }
      }
      return page;
   }
   
   public PresentationPage getFirstPresentationPage(Id presentationId) {
      return getPresentationPage(presentationId, 0);
   }
   
   public PresentationPage getPresentationPage(Id presentationId, int pageIndex) {
      List pages = getHibernateTemplate().findByNamedQuery("findPortfolioPagesByPortfolioAndSequence", 
            new Object[]{presentationId, Integer.valueOf(pageIndex)});

      return (pages == null || pages.size() == 0) ? null : (PresentationPage)pages.get(0);
   }
   
   
   public Document getPresentationLayoutAsXml(Presentation presentation, String pageId) {
      viewingPresentation(presentation);
      PresentationPage page;
      if (pageId == null || pageId.equals("")) {
         page = getFirstPresentationPage(presentation.getId());
      } else {
         page = getPresentationPage(getIdManager().getId(pageId));
      }
      if (page == null) {
         return null;
      }
      return getPresentationLayoutAsXml(page.getId());
   }

   
   /**
    * Create an xml document represenation of the requested page from the 
    * presentation passed in.
    * 
    * @param presentation
    * @param pageId
    * @return xml representation of the requested page or null
    */
   public Document getPresentationPreviewLayoutAsXml(Presentation presentation, String pageId) {
      viewingPresentation(presentation);
      PresentationPage page = null;
      List pages = presentation.getPages();
      if (pageId == null || pageId.equals("")) {
         page = (PresentationPage) pages.get(0);
      }
      else {
         for (Iterator i = pages.iterator(); i.hasNext();) {
            PresentationPage iterPage = (PresentationPage)i.next();
            if (iterPage != null && iterPage.getId() != null 
                      && pageId.equals(iterPage.getId().toString())) {
               page = iterPage;
            }
         }
      }

      if (page == null) {
         return null;
      }

      page.setPresentation(presentation);      
      return getPresentationPageLayoutAsXml(page);
   }
	   
	   
   protected Document getPresentationLayoutAsXml(Id pageId) {
    
      PresentationPage page = getPresentationPage(pageId);
      return getPresentationPageLayoutAsXml(page);
   }
 
   
   protected Document getPresentationPageLayoutAsXml(PresentationPage page) {

      Element root = new Element("ospiPresentation");
      Element pageStyleElement = new Element("pageStyle");
      Element layoutElement = new Element("layout");
      Element regionsElement = new Element("regions");
	      
      Id fileId = page.getLayout().getXhtmlFileId();
      Artifact art = getPresentationItem("fileArtifact", fileId, page.getPresentation());

      PresentableObjectHome home = (PresentableObjectHome) art.getHome();
      layoutElement.addContent(home.getArtifactAsXml(art));

      Style pageStyle = page.getStyle() != null ? page.getStyle() : page.getPresentation().getStyle();
      if (pageStyle != null && pageStyle.getStyleFile() != null) {
         Id cssFileId = pageStyle.getStyleFile();
         Artifact cssArt = getPresentationItem("fileArtifact", cssFileId, page.getPresentation());
         PresentableObjectHome cssHome = (PresentableObjectHome) cssArt.getHome();
         pageStyleElement.addContent(cssHome.getArtifactAsXml(cssArt));
         root.addContent(pageStyleElement);
      }

      for (Iterator regions = page.getRegions().iterator(); regions.hasNext();) {
         PresentationPageRegion region = (PresentationPageRegion) regions.next();
         int itemSeq = 0;
         for (Iterator items = region.getItems().iterator(); items.hasNext();) {
            PresentationPageItem item = (PresentationPageItem) items.next();
            Element regionElement = new Element("region");
            regionElement.setAttribute("id", region.getRegionId());
            if (region.getItems().size() > 1) {
               regionElement.setAttribute("sequence", String.valueOf(itemSeq));
            }
            regionElement.setAttribute("type", item.getType());
            Element itemPropertiesElement = new Element("itemProperties");
            String contentType = "";
               if (item.getProperties() != null) {
               for (Iterator properties = item.getProperties().iterator(); properties.hasNext();) {
                  PresentationItemProperty prop = (PresentationItemProperty) properties.next();
                  itemPropertiesElement.addContent(createElementNode(prop.getKey(), prop.getValue()));
                  if (prop.getKey().equals(PresentationItemProperty.CONTENT_TYPE)) {
                     contentType = prop.getValue();
                  }
               }
            }
            regionElement.addContent(itemPropertiesElement);
            regionElement.addContent(outputTypedContent(item.getType(), 
                  item.getValue(), page.getPresentation(), contentType));
            regionsElement.addContent(regionElement);
            itemSeq++;
         }
      }      
      
      root.addContent(layoutElement);
      root.addContent(createNavigationElement(page));
      root.addContent(regionsElement);
      return new Document(root);
   }
	   
   
   protected Element outputTypedContent(String type, String value, 
         Presentation presentation, String contentType) {
      if (type.equals("text") || type.equals("richtext")) {
         Element textRegion = new Element("value");
         textRegion.addContent(new CDATA(value));
         return textRegion;
      }
      else if (type.equals("form") || type.equals("link") || type.equals("inline")) {         
         //String fileId = value;
         Element artifactAsXml = null;
         Id itemId = getIdManager().getId(value);
         if (!contentType.equals("page")) {
            Artifact art = getPresentationItem(contentType, itemId, presentation);

            PresentableObjectHome home = (PresentableObjectHome) art.getHome();
            artifactAsXml = home.getArtifactAsXml(art);
         }
         else {
            artifactAsXml = getPresentationPageAsXml(getPresentationPage(itemId));
         }
         return artifactAsXml;
      }
      return new Element("empty");
   }

   protected Artifact getPresentationItem(String type, Id itemId, Presentation presentation) {
      ArtifactFinder finder = getArtifactFinderManager().getArtifactFinderByType(type);
      Artifact art;

      if (finder instanceof EntityContextFinder && !presentation.isPreview()) {
         art = ((EntityContextFinder)finder).loadInContext(itemId,
               PresentationContentEntityProducer.PRODUCER_NAME, 
               presentation.getSiteId(),
               presentation.getId().getValue());
      }
      else {
         art = finder.load(itemId);
      }

      return art;
   }
   
   protected Element getPresentationPageAsXml(PresentationPage page) {
      Element root = new Element("artifact");
      
      Element metadata = new Element("metaData");
      metadata.addContent(createElementNode("id", page.getId().getValue()));
      metadata.addContent(createElementNode("displayName", page.getTitle()));

      Element type = new Element("type");
      metadata.addContent(type);

      type.addContent(createElementNode("id", "page"));
      type.addContent(createElementNode("description", "Presentation Page"));
      
      Element fileData = new Element("fileArtifact");
      Element uri = new Element("uri");
      uri.addContent(page.getUrl());
      fileData.addContent(uri);

      root.addContent(metadata);
      root.addContent(fileData);
      
      return root;
   }
   
   protected Element createElementNode(String name, String value) {
      Element newNode = new Element(name);
      newNode.addContent(value);
      return newNode;
   }
   
   
   protected Element createNavigationElement(PresentationPage page) {
      int currentPage = page.getSequence();
      Element navigationElement = new Element("navigation");
      Element previousPage = new Element("previousPage");
      Element nextPage = new Element("nextPage");

      boolean isAdvancedNavigation = page.getPresentation().isAdvancedNavigation();

      if (isAdvancedNavigation) {
         List pages = null;
         if (page.getPresentation().isPreview()) {
            pages = page.getPresentation().getPages();
         } else {
            pages = getPresentationPagesByPresentation(page.getPresentation().getId());
         }
         PresentationPage lastNavPage = null;
         PresentationPage nextNavPage = null;
         boolean foundCurrent = false;

         for (Iterator i = pages.iterator(); i.hasNext();) {
            PresentationPage iterPage = (PresentationPage)i.next();
            if (iterPage.getSequence() == currentPage) {
               foundCurrent = true;
            }
            else if (!foundCurrent) {
               lastNavPage = iterPage;
            }
            else {
               nextNavPage = iterPage;
               break;
            }
         }

         if (lastNavPage != null) {
            previousPage.addContent(getPresentationPageAsXml(lastNavPage));
            navigationElement.addContent(previousPage);
         }
         if (nextNavPage != null) {
            nextPage.addContent(getPresentationPageAsXml(nextNavPage));
            navigationElement.addContent(nextPage);
         }
      }
      return navigationElement;
   }

   public String getTempPresDownloadDir() {
      return tempPresDownloadDir;
   }

   public void setTempPresDownloadDir(String tempPresDownloadDir) {
      this.tempPresDownloadDir = tempPresDownloadDir;
   }

   public void init() {
      logger.info("init()");
      if (isAutoDdl()) {
         try {
            initFreeFormTemplate();
            initGlobalLayouts();
         }
         catch (Exception e) {
            logger.warn("Temporarily catching all exceptions in osp.PresentationManagerImpl.init()", e);
         }
      }
      
      if (isPortfolioPropertyFormConversion()) {
         final String convertProperty = "osp.portfolio.propertyConversion";
         String inited = System.getProperty(convertProperty);
         if (inited == null) {
            System.setProperty(convertProperty, "true");
            try {
               // do conversion for template property form types
               List templates = getTemplatesForConversion();
               logger.debug("There are " + templates.size() + " templates needing conversion");
               convertPortfolioTemplates(templates);
            }
            catch (Exception e) {
               logger.warn("Error converting portfolio template property form types", e);
            }
            try {
               // do conversion for portfolio property form data
               List portfolios = getPortfoliosForConversion();
               logger.debug("There are " + portfolios.size() + " portfolios needing conversion");
               convertPortfolios(portfolios);
            }
            catch (Exception e) {
               logger.warn("Error converting portfolio property form data", e);
            }
         }
      }
      
   }

   protected void initGlobalLayouts() {
      getSecurityService().pushAdvisor(new AllowAllSecurityAdvisor());

      org.sakaiproject.tool.api.Session sakaiSession = SessionManager.getCurrentSession();
      String userId = sakaiSession.getUserId();
      sakaiSession.setUserId("admin");
      sakaiSession.setUserEid("admin");
      List layouts = new ArrayList();

      try {
         for (Iterator i=getDefinedLayouts().iterator();i.hasNext();) {
            layouts.add(processDefinedLayout((PresentationLayoutWrapper)i.next()));
         }

         for (Iterator i=layouts.iterator();i.hasNext();) {
            PresentationLayout layout = (PresentationLayout) i.next();
            getHibernateTemplate().saveOrUpdate(layout);
            lockLayoutFiles(layout);
         }

      } finally {
         getSecurityService().popAdvisor();
         sakaiSession.setUserEid(userId);
         sakaiSession.setUserId(userId);
      }

   }

   protected PresentationLayout processDefinedLayout(PresentationLayoutWrapper wrapper) {
      PresentationLayout layout = getPresentationLayout(getIdManager().getId(wrapper.getIdValue()));

      if (layout == null) {
         layout = new PresentationLayout();
         layout.setCreated(new Date());
         layout.setNewId(getIdManager().getId(wrapper.getIdValue()));
      }

      updateLayout(wrapper, layout);
      return layout;
   }

   protected void updateLayout(PresentationLayoutWrapper wrapper, PresentationLayout layout) {
      getContentHosting().removeAllLocks(wrapper.getIdValue());
      getLockManager().removeAllLocks(wrapper.getIdValue());
		
      layout.setPreviewImageId(createResource(wrapper.getPreviewFileLocation(),
         wrapper.getPreviewFileName(), wrapper.getIdValue() + " layout preview", wrapper.getPreviewFileType()));
      layout.setXhtmlFileId(createResource(wrapper.getLayoutFileLocation(),
         wrapper.getIdValue() + ".xml", wrapper.getIdValue() + " layout file", "text/xml"));

      layout.setModified(new Date());
      layout.setName(wrapper.getName());
      layout.setDescription(wrapper.getDescription());
      layout.setGlobalState(PresentationLayout.STATE_PUBLISHED);
      layout.setSiteId(null);
      layout.setToolId(null);
      layout.setOwner(getAgentManager().getAgent("admin"));
   }

   protected void deleteResource(Id qualifierId, Id resourceId) {
      try {
         getContentHosting().removeAllLocks(qualifierId.getValue());
         String id = getContentHosting().resolveUuid(resourceId.getValue());
         if (id == null) {
            return;
         }
         getContentHosting().removeResource(id);
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   protected void initFreeFormTemplate() {
      getSecurityService().pushAdvisor(new AllowAllSecurityAdvisor());

      org.sakaiproject.tool.api.Session sakaiSession = SessionManager.getCurrentSession();
      String userId = sakaiSession.getUserId();
      sakaiSession.setUserId("admin");
      sakaiSession.setUserEid("admin");
      String resourceLocation = "/org/theospi/portfolio/presentation/freeform_template.xsl";

      try {
         PresentationTemplate template = getPresentationTemplate(getFreeFormTemplateId());
         if (template == null) {
            template = createFreeFormTemplate(createResource(resourceLocation,
               "freeFormRenderer.xml", "used for rendering the free form template", "text/xml"));
         }
         else {
            Id rendererId = updateResource(template.getId(), template.getRenderer(), resourceLocation, 
            		"freeFormRenderer.xml", "used for rendering the free form template", "text/xml");
            //There have been issues where the renderer referenced in the template doesn't exist so the
            // update may need to create a new one (or use a different uuid)
            // So, if they are different, set to use the one returned from the update.
            if (!rendererId.getValue().equals(template.getRenderer().getValue())) {
            	template.setRenderer(rendererId);
            }
            if (template.getItemDefinitions().size() == 0) {
               template.getItemDefinitions().add(createFreeFormItemDef(template));
            }
         }
         storeTemplate(template, false);
      } finally {
         getSecurityService().popAdvisor();
         sakaiSession.setUserEid(userId);
         sakaiSession.setUserId(userId);
      }
   }

   protected PresentationTemplate createFreeFormTemplate(Id rendererId) {
      PresentationTemplate template = new PresentationTemplate();
      template.setId(getFreeFormTemplateId());
      template.setNewId(getFreeFormTemplateId());
      template.setName("Free Form Presentation");
      template.setRenderer(rendererId);
      template.setNewObject(true);
      template.setSiteId(getIdManager().createId().getValue());
      template.setOwner(getAgentManager().getAnonymousAgent());
      template.getItemDefinitions().add(createFreeFormItemDef(template));
      return template;
   }

   protected PresentationItemDefinition createFreeFormItemDef(PresentationTemplate template) {
      PresentationItemDefinition def = new PresentationItemDefinition();
      def.setPresentationTemplate(template);
      def.setAllowMultiple(true);
      def.setName("freeFormItem");
      def.setSequence(0);
      return def;
   }

   protected Id updateResource(Id qualifierId, Id resourceId, String resourceLocation, String name, String description, String type) {
      ByteArrayOutputStream bos = loadResource(resourceLocation);

      try {
         getContentHosting().removeAllLocks(qualifierId.getValue());
         ContentResourceEdit resourceEdit =
               getContentHosting().editResource(getContentHosting().resolveUuid(resourceId.getValue()));
         resourceEdit.setContent(bos.toByteArray());
         getContentHosting().commitResource(resourceEdit, NotificationService.NOTI_NONE);
         return resourceId;
      }
      catch (IdUnusedException iue) {
    	  //couldn't find resource...better create another one.
    	  logger.warn("Couldn't find resource with uuid: "+resourceId.getValue() + ".  Creating a new one for " + resourceLocation);
    	  Id newId = createResource(resourceLocation, name, description, type);
    	  return newId;
      }
      catch (Exception e) {
         logger.warn("updateResource: "+e);
      }
		
      return null;
   }

   protected ByteArrayOutputStream loadResource(String name) {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      InputStream is = getClass().getResourceAsStream(name);

      try {
         int c = is.read();
         while (c != -1) {
            bos.write(c);
            c = is.read();
         }
         bos.flush();
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      } finally {
         try {
            is.close();
         }
         catch (IOException e) {
            //can't do anything now..
            if (logger.isDebugEnabled()) {
                logger.debug(e);
            } 
         }
      }
      return bos;
   }

   protected Id createResource(String resourceLocation,
                               String name, String description, String type) {
      ByteArrayOutputStream bos = loadResource(resourceLocation);
      ContentResource resource = null;
      ResourcePropertiesEdit resourceProperties = getContentHosting().newResourceProperties();
      resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, name);
      resourceProperties.addProperty (ResourceProperties.PROP_DESCRIPTION, description);
      resourceProperties.addProperty(ResourceProperties.PROP_CONTENT_ENCODING, "UTF-8");
      
      String folder = "/group/PortfolioAdmin" + SYSTEM_COLLECTION_ID;
      
      try {
         //TODO use the bean org.theospi.portfolio.admin.model.IntegrationOption.siteOption 
         // in common/components to get the name and id for this site.
         
         ContentCollectionEdit groupCollection = getContentHosting().addCollection("/group/PortfolioAdmin");
         groupCollection.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, "Portfolio Admin");
         getContentHosting().commitCollection(groupCollection);
      }
      catch (IdUsedException e) {
         // ignore... it is already there.
          if (logger.isDebugEnabled()) {
              logger.debug(e);
          } 
      }
      catch (Exception e) {
         logger.warn("createResource(PortfolioAdmin): "+e);
      }

      try {
         ContentCollectionEdit collection = getContentHosting().addCollection(folder);
         collection.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, "system");
         getContentHosting().commitCollection(collection);
         
      }
      catch (IdUsedException e) {
         // ignore... it is already there.
         if (logger.isDebugEnabled()) {
             logger.debug(e);
         } 
      }
      catch (Exception e) {
         logger.warn("createResource(system): "+e);
      }

      // If resource exists, just update it
      String resourceId = folder + name;
      try {
         resource = getContentHosting().updateResource( resourceId, type, bos.toByteArray() );
      }
      catch (IdUnusedException e) {
         // ignore, must be new
         if (logger.isDebugEnabled()) {
             logger.debug(e);
         } 
      }
      catch (InUseException e) {
         // ignore, must be new
         if (logger.isDebugEnabled()) {
             logger.debug(e);
         } 
      }
      catch (PermissionException e) {
         // ignore, must be new
         if (logger.isDebugEnabled()) {
             logger.debug(e);
         } 
      }
      catch (Exception e) {
         // unexpected error: unable to update existing resource
         logger.warn("createResource(updateResource): " + e);
      }

      // Otherwise, resource doesn't exist, so create it
      if ( resource == null ) {
         try {
            resource = getContentHosting().addResource(name, folder, 1, type,
                                                       bos.toByteArray(), resourceProperties, NotificationService.NOTI_NONE);
         }
         catch (Exception e) {
            // unexpected error: tried to add new resource and failed
            logger.warn("createResource(addResource): "+e);
         }
      }
      
      String uuid = getContentHosting().getUuid(resource.getId());
      return getIdManager().getId(uuid);
   }
   
   /**
    * 
    * @param portfolios A list of Presentation objects
    */
   protected void convertPortfolios(List portfolios) {
      org.sakaiproject.tool.api.Session sakaiSession = SessionManager.getCurrentSession();
      String userId = sakaiSession.getUserId();
      String userEid = sakaiSession.getUserEid();
      try {
         sakaiSession.setUserId("admin");
         sakaiSession.setUserEid("admin");
      
      
         for (Iterator i = portfolios.iterator(); i.hasNext();) {
            Presentation presentation = (Presentation) i.next();
            byte[] formData = convertFormData(presentation.getProperties());
            Id propForm = saveForm(presentation.getOwner().getId().getValue(), 
                  presentation.getName() + " Properties", 
                  formData, presentation.getTemplate().getPropertyFormType().getValue());
            presentation.setPropertyForm(propForm);
            storePresentation(presentation, false, true);
            
            logger.info("OSP Portfolio Conversion: For Portfolio with id " + presentation.getId().getValue() + ": Creating new Form Resource with id of " + propForm.getValue());
   
         }
      } catch (Exception e) {
         logger.warn("Unexpected error occurred in PresentationManagerImpl.convertPortfolios()", e);
      } finally {
         sakaiSession.setUserEid(userEid);
         sakaiSession.setUserId(userId);
      }
   }
   
   private byte[] convertFormData(ElementBean elementBean) {
      Document doc = new Document();
      Element rootElement = elementBean.getBaseElement();
      rootElement.detach();
      doc.setRootElement(rootElement);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      XMLOutputter xmlOutputter = new XMLOutputter();
      try {
         xmlOutputter.output(doc, out);
      } catch (IOException e) {
         throw new HibernateException(e);
      }
      return out.toByteArray();
   }
   
   /**
    * 
    * @param templates A list of PresentationTemplate objects
    */
   protected void convertPortfolioTemplates(List templates) {
      org.sakaiproject.tool.api.Session sakaiSession = SessionManager.getCurrentSession();
      String userId = sakaiSession.getUserId();
      String userEid = sakaiSession.getUserEid();
      try {
         sakaiSession.setUserId("admin");
         sakaiSession.setUserEid("admin");
      
      
         for (Iterator i = templates.iterator(); i.hasNext();) {
            PresentationTemplate template = (PresentationTemplate) i.next();
            Id fileId = template.getPropertyPage();
            StructuredArtifactDefinitionBean tempFormDef = null;
            
            logger.info("OSP Portfolio Template Conversion: For Template with id " + template.getId().getValue() + ": Attempting to locate a Form using a File id of " + fileId.getValue());
   
            //Doing this to make sure the file is setup in the security stack
            Node node = getNode(fileId);
   
            List formDefs = structuredArtifactDefinitionManager.findBySchema(node.getResource());
            if (formDefs == null || formDefs.isEmpty()) {
               //create a new form
               tempFormDef = new StructuredArtifactDefinitionBean();
               //tempFormDef.setSchemaFile(fileId);
               tempFormDef.setSchema(node.getResource().getContent());
               tempFormDef.setDocumentRoot(template.getDocumentRoot());
               tempFormDef.setDescription("Portfolio Properties for " + template.getName());
               tempFormDef.setOwner(template.getOwner());
               tempFormDef.setSiteId(template.getSiteId());
               tempFormDef.setSystemOnly(true);
               if (template.isPublished()) {
                  tempFormDef.setSiteState(StructuredArtifactDefinitionBean.STATE_PUBLISHED);
               }
               structuredArtifactDefinitionManager.save(tempFormDef);
               logger.info("OSP Portfolio Template Conversion: Template with id " + template.getId().getValue() + " needs to create a new Form object.");
            }
            else {
               int counter = 0;
               while (tempFormDef == null && counter < 3) {
                  tempFormDef = findUsableFormDef(formDefs, counter, template.getSiteId());
                  counter ++;
               }
            }
            //make sure it is not null
            if (tempFormDef != null) {
               template.setPropertyFormType(tempFormDef.getId());
               logger.info("OSP Portfolio Template Conversion: Template with id " + template.getId().getValue() + " is being updated to use form with id " + tempFormDef.getId().getValue());
            }
            
            storeTemplate(template, false);
         }
      } catch (Exception e) {
         logger.warn("Unexpected error occurred in PresentationManagerImpl.convertPortfolioTemplates()", e);
      } finally {
         sakaiSession.setUserEid(userEid);
         sakaiSession.setUserId(userId);
      }
   }
   
   /**
    * 
    * @param formDefs A List of StructuredArtifactDefinitionBean objects to search through
    * @param caseSwitch 0 for global published, 1 for site published, 2 for any in site
    * @param siteId The id of site to search in
    * @return The StructuredArtifactDefinitionBean object that was found, null of none found
    */
   protected StructuredArtifactDefinitionBean findUsableFormDef(List formDefs, int caseSwitch, String siteId) {
      StructuredArtifactDefinitionBean retVal = null;
      switch (caseSwitch) {
      case 0:
         for (Iterator i=formDefs.iterator(); i.hasNext();) {
            StructuredArtifactDefinitionBean iterVal = (StructuredArtifactDefinitionBean) i.next();
            if (iterVal.getGlobalState() == StructuredArtifactDefinitionBean.STATE_PUBLISHED)
            {
               retVal = iterVal;
               break;
            }            
         }
         break;
      case 1:
         for (Iterator i=formDefs.iterator(); i.hasNext();) {
            StructuredArtifactDefinitionBean iterVal = (StructuredArtifactDefinitionBean) i.next();
            if (iterVal.getSiteState() == StructuredArtifactDefinitionBean.STATE_PUBLISHED && iterVal.getSiteId().equals(siteId))
            {
               retVal = iterVal;
               break;
            }            
         }
         break;
      case 2:
         for (Iterator i=formDefs.iterator(); i.hasNext();) {
            StructuredArtifactDefinitionBean iterVal = (StructuredArtifactDefinitionBean) i.next();
            if (iterVal.getSiteId().equals(siteId))
            {
               retVal = iterVal;
               break;
            }            
         }
         break;
      }
      return retVal;
   }
   
   private Id saveForm(String owner, String name, byte[] fileContent, String formType) {
      getSecurityService().pushAdvisor(new AllowAllSecurityAdvisor());

      org.sakaiproject.tool.api.Session sakaiSession = SessionManager.getCurrentSession();
      String userId = sakaiSession.getUserId();
      sakaiSession.setUserId(owner);
      sakaiSession.setUserEid(owner);
      
      String description = "";
      String folder = "/user/" + owner;
      String type = "application/x-osp";

      try {
         ContentCollectionEdit groupCollection = getContentHosting().addCollection(folder);
         groupCollection.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, owner);
         getContentHosting().commitCollection(groupCollection);
      }
      catch (IdUsedException e) {
         // ignore... it is already there.
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }

      folder = "/user/" + owner + PRESENTATION_PROPERTIES_FOLDER_PATH;
      
      try {
         ContentCollectionEdit groupCollection = getContentHosting().addCollection(folder);
         groupCollection.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, PRESENTATION_PROPERTIES_FOLDER);
         groupCollection.getPropertiesEdit().addProperty(ResourceProperties.PROP_DESCRIPTION, "Folder for Portfolio Property Forms");
         getContentHosting().commitCollection(groupCollection);
      }
      catch (IdUsedException e) {
         // ignore... it is already there.
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      }
      
      try {
         ResourcePropertiesEdit resourceProperties = getContentHosting().newResourceProperties();
         resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, name);
         resourceProperties.addProperty (ResourceProperties.PROP_DESCRIPTION, description);
         resourceProperties.addProperty(ResourceProperties.PROP_CONTENT_ENCODING, "UTF-8");
         resourceProperties.addProperty(ResourceProperties.PROP_STRUCTOBJ_TYPE, formType);
         resourceProperties.addProperty(ContentHostingService.PROP_ALTERNATE_REFERENCE, MetaobjEntityManager.METAOBJ_ENTITY_PREFIX);
         
         ContentResource resource = getContentHosting().addResource(name, folder, 0, type,
               fileContent, resourceProperties, NotificationService.NOTI_NONE);
         return idManager.getId(getContentHosting().getUuid(resource.getId()));
      }
      catch (Exception e) {
         throw new RuntimeException(e);
      } finally {
         getSecurityService().popAdvisor();
         sakaiSession.setUserEid(userId);
         sakaiSession.setUserId(userId);
      }
   }

   public Id getFreeFormTemplateId() {
      return Presentation.FREEFORM_TEMPLATE_ID;
   }

   public List getDefinedLayouts() {
      return definedLayouts;
   }

   public void setDefinedLayouts(List definedLayouts) {
      this.definedLayouts = definedLayouts;
   }

   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
      return structuredArtifactDefinitionManager;
   }

   public void setStructuredArtifactDefinitionManager(StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }

   public List getGlobalSites() {
      return globalSites;
   }

   public void setGlobalSites(List globalSites) {
      this.globalSites = globalSites;
   }

   public List getGlobalSiteTypes() {
      return globalSiteTypes;
   }

   public void setGlobalSiteTypes(List globalSiteTypes) {
      this.globalSiteTypes = globalSiteTypes;
   }

   public List getInitializedServices() {
      return initializedServices;
   }

   public void setInitializedServices(List initializedServices) {
      this.initializedServices = initializedServices;
   }

   protected List getPresentationPagesByStyle(Id styleId) {
      Object[] params = new Object[]{styleId};
      return getHibernateTemplate().findByNamedQuery("findPortfolioPagesByStyle", 
               params);
   }
   
   protected List getPresentationsByStyle(Id styleId) {
      Object[] params = new Object[]{styleId};
      return getHibernateTemplate().findByNamedQuery("findPortfoliosByStyle", 
               params);
   }
   
   public boolean checkStyleConsumption(Id styleId) {
      List pages = getPresentationPagesByStyle(styleId);
      if (pages != null && !pages.isEmpty() && pages.size() > 0) {
         return true;
      }
      
      List presentations = getPresentationsByStyle(styleId);
      if (presentations != null && !presentations.isEmpty() && presentations.size() > 0) {
         return true;
      }
      
      return false;
   }

   public List getStyles(Id objectId) {
      PresentationPage page = getPresentationPage(objectId);
      if (page != null) {
         Presentation pres = page.getPresentation();
         List styles = new ArrayList();
         if (pres.getStyle() != null) {
            styles.add(pres.getStyle());
         }
         if (page.getStyle() != null) {
            styles.add(page.getStyle());
         }
         return styles;
      }

      Presentation pres = (Presentation) getHibernateTemplate().get(Presentation.class, objectId);
      if (pres != null) {
         pres = getPresentation(objectId);
         List styles = new ArrayList();
         if (pres.getStyle() != null) {
            styles.add(pres.getStyle());
         }
         return styles;
      }

      return null;
   }

   public String getImportFolderName() {
      return importFolderName;
   }

   public void setImportFolderName(String importFolderName) {
      this.importFolderName = importFolderName;
   }

   public boolean checkFormConsumption(Id formId) {
      Collection objectsWithForms = getHibernateTemplate().find(
         "from PresentationTemplate where propertyFormType = ?", 
         new Object[] {formId});

      if (objectsWithForms.size() > 0) {
         return true;
      }

      String queryString = "from PresentationItemDefinition where " +
         "type = ?";
      Collection additionalForms = getHibernateTemplate().find(queryString,
         new Object[] {formId.getValue()});

      return additionalForms.size() > 0;
   }
   
   /**
    * {@inheritDoc}
    */
   public Collection<FormConsumptionDetail> getFormConsumptionDetails(Id formId) {
      Collection results = new ArrayList();
      
      String propFormType = messages.getString("template_property_form");
      String itemDefType = messages.getString("item_definition");
      String templateNameText = messages.getString("template_name_text");
      String itemDefNameText = messages.getString("item_def_text");
      
      Collection objectsWithForms = getHibernateTemplate().find(
         "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
            "t.propertyFormType, " +
            "t.siteId, " +
            "'" + propFormType + "', " +
            "concat('" + templateNameText + "', t.name)) " +
         "from PresentationTemplate t where t.propertyFormType = ?", 
         new Object[] {formId});
      results.addAll(objectsWithForms);

      String queryString = "select new org.sakaiproject.metaobj.shared.model.FormConsumptionDetail(" +
      		   "def.type, " +
      		   "def.presentationTemplate.siteId, " +
      		   "'" + itemDefType + "', " +
      		   "concat('" + itemDefNameText + "', def.title), " +
      		   "concat('" + templateNameText + "', def.presentationTemplate.name)) " +
      		"from PresentationItemDefinition def where " +
      		"def.type = ?";
      Collection additionalForms = getHibernateTemplate().find(queryString,
         new Object[] {formId.getValue()});

      results.addAll(additionalForms);

      return results;
   }

   public boolean isAutoDdl() {
      return autoDdl;
   }

   public void setAutoDdl(boolean autoDdl) {
      this.autoDdl = autoDdl;
   }

   public boolean isPortfolioPropertyFormConversion() {
      return portfolioPropertyFormConversion;
   }

   public void setPortfolioPropertyFormConversion(
         boolean portfolioPropertyFormConversion) {
      this.portfolioPropertyFormConversion = portfolioPropertyFormConversion;
   }

   public EventService getEventService() {
	   return eventService;
   }

   public void setEventService(EventService eventService) {
	   this.eventService = eventService;
   }

   public String getDownloadExternalUri() {
      return downloadExternalUri;
   }

   public void setDownloadExternalUri(String downloadExternalUri) {
      this.downloadExternalUri = downloadExternalUri;
   }

   /* (non-Javadoc)
    * @see org.theospi.portfolio.presentation.PresentationManager#copyPresentation(org.sakaiproject.metaobj.shared.model.Id)
    */
   @SuppressWarnings("unchecked")
   public Presentation copyPresentation(Id presentationId) {
       // http://jira.sakaiproject.org/browse/SAK-17351
       if (presentationId == null) {
           throw new IllegalArgumentException("invalid presentation id ("+presentationId+"), must be set");
       }
       Presentation original = getPresentation(presentationId);
       if (original == null) {
           throw new IllegalArgumentException("invalid presentation id ("+presentationId+") for copy, could not find presentation");
       }

       // ready to copy
       logger.info("Ready to copy presentation: "+original.getName());
       Presentation copy = new Presentation();
       copy.setNewObject(true);
       copy.setAdvancedNavigation(original.isAdvancedNavigation());
       copy.setAllowComments(false); // FORCED
       copy.setDescription(original.getDescription());
       copy.setExpiresOn(original.getExpiresOn());
       copy.setIsCollab(original.getIsCollab());
       copy.setIsDefault(original.getIsDefault());
       copy.setIsPublic(false); // FORCED
       HashSet<PresentationItem> copiedItems = new HashSet<PresentationItem>(original.getItems().size());
       for (PresentationItem item : (Set<PresentationItem>) original.getItems()) {
           PresentationItem copiedItem = new PresentationItem();
           copiedItem.setArtifactId(item.getArtifactId());
           copiedItem.setDefinition(item.getDefinition());
    	   copiedItems.add(copiedItem);
       }
       copy.setItems(copiedItems); // list (ref)
       copy.setLayout(original.getLayout()); // obj (ref)
       copy.setName("Copy of "+original.getName());
       copy.setOwner(original.getOwner()); // should we copy this?
       List<PresentationPage> origPages = getPresentationPagesByPresentation(original.getId());
       if (origPages != null && ! origPages.isEmpty()) {
           ArrayList<PresentationPage> copiedPages = new ArrayList<PresentationPage>(origPages.size());
           for (PresentationPage page : origPages) {
               PresentationPage cp = new PresentationPage();
               cp.setNewObject(true);
               cp.setDescription(page.getDescription());
               cp.setKeywords(page.getKeywords());
               cp.setLayout(page.getLayout());
               cp.setPresentation(copy); // NOTE: this should be set automatically when null -AZ
               if (page.getRegions() != null) {
                   HashSet<PresentationPageRegion> copiedRegions = new HashSet<PresentationPageRegion>();
                   for (PresentationPageRegion region : (Set<PresentationPageRegion>) page.getRegions()) {
                       PresentationPageRegion newRegion = new PresentationPageRegion();
                       newRegion.setHelpText(region.getHelpText());
                       
                       List<PresentationPageItem> regionItems = new ArrayList<PresentationPageItem>(region.getItems().size());
                       for (PresentationPageItem item : (List<PresentationPageItem>) region.getItems()) {
                    	   PresentationPageItem copiedItem = new PresentationPageItem();
                           copiedItem.setLayoutRegionId(item.getLayoutRegionId());
                    	   
                           Set<PresentationItemProperty> properties = new HashSet<PresentationItemProperty>(item.getProperties().size());
                           for (PresentationItemProperty property : (Set<PresentationItemProperty>)item.getProperties()) {
                        	   PresentationItemProperty newProperty = new PresentationItemProperty();
                        	   newProperty.setItem(copiedItem);
                        	   newProperty.setKey(property.getKey());
                        	   newProperty.setValue(property.getValue());
                        	   properties.add(newProperty);
                           }
                           
                           copiedItem.setProperties(properties);
                           copiedItem.setRegion(newRegion);
                           copiedItem.setRegionItemSeq(item.getRegionItemSeq());
                           copiedItem.setType(item.getType());
                           copiedItem.setValue(item.getValue());
                           regionItems.add(copiedItem);
                       }
                       
                       newRegion.setItems(regionItems);
                       newRegion.setPage(cp);
                       newRegion.setRegionId(region.getRegionId());
                       newRegion.setType(region.getType());
                	   copiedRegions.add(newRegion);
                   }
                   cp.setRegions(copiedRegions);
               }
               cp.setSequence(page.getSequence());
               cp.setStyle(page.getStyle());
               cp.setTitle(page.getTitle());
               copiedPages.add(cp);
           }
           copy.setPages(copiedPages); // list (ref)
       }
       //copy.set(original.getPresentationItems()); // list
       copy.setPresentationType(original.getPresentationType());
       copy.setProperties(original.getProperties()); // obj (ref)
       copy.setPropertyForm(original.getPropertyForm()); // ref (id)
       copy.setSecretExportKey(original.getSecretExportKey());
       copy.setSiteId(original.getSiteId());
       copy.setStyle(original.getStyle()); // obj (ref)
       copy.setTemplate(original.getTemplate()); // obj (ref)
       copy.setToolId(original.getToolId());
       Presentation savedCopy = storePresentation(copy, true, true);
       logger.info("Copied presentation from "+original.getId()+" to "+savedCopy.getId());
       return savedCopy;
   }

}
