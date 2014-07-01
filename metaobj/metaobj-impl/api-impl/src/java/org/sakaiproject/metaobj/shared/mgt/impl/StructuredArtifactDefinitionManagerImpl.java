/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.0/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/shared/mgt/impl/StructuredArtifactDefinitionManagerImpl.java $
 * $Id: StructuredArtifactDefinitionManagerImpl.java 308891 2014-04-28 15:49:48Z enietzel@anisakai.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.shared.mgt.impl;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.sakaiproject.authz.cover.FunctionManager;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.exception.*;
import org.sakaiproject.metaobj.security.AllowChildrenMapSecurityAdvisor;
import org.sakaiproject.metaobj.security.AllowMapSecurityAdvisor;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.security.AuthorizationFacade;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.shared.DownloadableManager;
import org.sakaiproject.metaobj.shared.SharedFunctionConstants;
import org.sakaiproject.metaobj.shared.mgt.*;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactDefinition;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactHomeInterface;
import org.sakaiproject.metaobj.shared.model.*;
import org.sakaiproject.metaobj.utils.xml.SchemaFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.service.legacy.resource.DuplicatableToolService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.event.cover.NotificationService;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.thread_local.cover.ThreadLocalManager;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.*;
import java.util.zip.*;


/**
 * @author chmaurer
 * @author jbush
 */
public class StructuredArtifactDefinitionManagerImpl extends HibernateDaoSupport
      implements StructuredArtifactDefinitionManager, DuplicatableToolService, DownloadableManager, FormConsumer {

   static final private String DOWNLOAD_FORM_ID_PARAM = "formId";
   private static final String SYSTEM_COLLECTION_ID = "/system/";
   static final private String IMPORT_BASE_FOLDER_ID = "importedForms";

   private static final String HAS_HOMES_TAG = "org.sakaiproject.metaobj.hasHomes";

   private AuthorizationFacade authzManager = null;
   private IdManager idManager;
   private WorksiteManager worksiteManager;
   private ContentHostingService contentHosting;
   private ToolManager toolManager;
   private List globalSites;
   private List globalSiteTypes;
   private ArtifactFinder artifactFinder;
   private ArtifactFinder structuredArtifactFinder;
   private int expressionMax = 999;
   private boolean replaceViews = true;
   private List formConsumers;
   private SecurityService securityService;
   private boolean autoDdl = true;
   private boolean enableLocksConversion = true;
   
   private static ResourceLoader messages = new ResourceLoader(
         "org.sakaiproject.metaobj.messages");

   public StructuredArtifactDefinitionManagerImpl() {
   }

   public Map getHomes() {
      Map<String, StructuredArtifactDefinitionBean> returnMap = new HashMap<String, StructuredArtifactDefinitionBean>();
      List list = findHomes();
      for (Iterator iter = list.iterator(); iter.hasNext();) {
         StructuredArtifactDefinitionBean sad = (StructuredArtifactDefinitionBean) iter.next();
         returnMap.put(sad.getId().getValue(), sad);
      }

      return returnMap;
   }

   /**
    * @param worksiteId
    * @return a map with all worksite and global homes
    */
   public Map getWorksiteHomes(Id worksiteId) {
      return getWorksiteHomes(worksiteId, false);
   }

   public Map getWorksiteHomes(Id worksiteId, boolean includeHidden) {
	   return getWorksiteHomes(worksiteId, null, includeHidden);
   }
   
   public Map getWorksiteHomes(Id worksiteId, String currentUserId, boolean includeHidden) {
      Map<String, StructuredArtifactDefinitionBean> returnMap = new HashMap<String, StructuredArtifactDefinitionBean>();
      List<StructuredArtifactDefinitionBean> list = findGlobalHomes();
      
      if (currentUserId == null)
    	  list.addAll(findHomes(worksiteId, includeHidden, false));
      else
    	  list.addAll(findAvailableHomes(worksiteId, currentUserId, includeHidden, false));
      
      for (Iterator iter = list.iterator(); iter.hasNext();) {
         StructuredArtifactDefinitionBean sad = (StructuredArtifactDefinitionBean) iter.next();
         returnMap.put(sad.getId().getValue(), sad);
      }

      return returnMap;
   }

   /**
    * @return list of published sads or sads owned by current user
    */
   public List findHomes() {
      return findHomes(true);
   }

   public List findHomes(boolean includeHidden) {
      return findHomes(true, includeHidden);
   }
   
   public List findHomes(boolean includeGlobal, boolean includeHidden) {
      // only for the appropriate worksites
      List sites = getWorksiteManager().getUserSites();
      List<StructuredArtifactDefinitionBean> returned = new ArrayList<StructuredArtifactDefinitionBean>();
      while (sites.size() > getExpressionMax()) {
         returned.addAll(findHomes(sites.subList(0, getExpressionMax() - 1), false, includeHidden));
         sites.subList(0, getExpressionMax() - 1).clear();
      }
      returned.addAll(findHomes(sites, includeGlobal, includeHidden));
      return returned;
   }

   public Map findCategorizedHomes(boolean includeHidden) {
      List homes = findHomes(false, includeHidden);
      
      Map<String, List> catHomes = new Hashtable<String, List>();
      
      for (Iterator<StructuredArtifactDefinitionBean> i = homes.iterator();i.hasNext();) {
         StructuredArtifactDefinitionBean bean = i.next();
         
         List beanList = catHomes.get(bean.getSiteId());
         
         if (beanList == null) {
            beanList = new ArrayList();
            catHomes.put(bean.getSiteId(), beanList);
         }
         
         beanList.add(bean);
      }
      
      return catHomes;
   }

   protected List findHomes(List sites, boolean includeGlobal, boolean includeHidden) {
      String query;
      Object[] params;

      if (includeGlobal) {
         query = "from StructuredArtifactDefinitionBean where ((globalState = ? or (owner = ? and siteId = null)) or " +
            "((owner = ? or siteState = ?)  and siteId in (";
         params = new Object[]{new Integer(StructuredArtifactDefinitionBean.STATE_PUBLISHED),
                               getAuthManager().getAgent(),getAuthManager().getAgent(),
                               new Integer(StructuredArtifactDefinitionBean.STATE_PUBLISHED)};
      }
      else {
         query = "from StructuredArtifactDefinitionBean where ((globalState != ? and (owner = ? or siteState = ?) and siteId in (";
         params = new Object[]{new Integer(StructuredArtifactDefinitionBean.STATE_PUBLISHED),
                               getAuthManager().getAgent(),
                               new Integer(StructuredArtifactDefinitionBean.STATE_PUBLISHED)};
      }

      StringBuffer bufQuery = new StringBuffer();
      for (Iterator i = sites.iterator(); i.hasNext();) {
         Site site = (Site) i.next();
         bufQuery.append("'" + site.getId() + "',"); 
      }
      query += bufQuery.toString();

      query += "''))";

      if (includeHidden) {
         query += ")";
      }
      else {
         query += ") and systemOnly != true";
      }

      return getHibernateTemplate().find(query, params);
   }
   
   /**
    * Find all homes
    * @return
    */
   private List<StructuredArtifactDefinitionBean> findAllHomes() {
	   String query = "from StructuredArtifactDefinitionBean";
	   return getHibernateTemplate().find(query);
   }


   public List findBySchema(ContentResource resource) {
      try {
         Object[] params = new Object[]{resource.getContent()};
         return getHibernateTemplate().findByNamedQuery("findBySchema", params);
      } catch (ServerOverloadException e) {
         
      } 
      return new ArrayList();
   }

   /**
    * @return list of all published globals or global sad owned by current user or waiting for approval
    */
   public List findGlobalHomes() {
      Object[] params = new Object[]{new Integer(StructuredArtifactDefinitionBean.STATE_PUBLISHED),
                                     getAuthManager().getAgent()};
      return getHibernateTemplate().findByNamedQuery("findGlobalHomes", params);
   }

   /**
    * @param currentWorksiteId
    * @return list of globally published sads or published sad in currentWorksiteId or sads in
    *         currentWorksiteId owned by current user
    */
   public List findHomes(Id currentWorksiteId) {
      String queryName = "findHomes";
      return findHomes(currentWorksiteId, queryName);
   }

   protected List findHomes(Id currentWorksiteId, String queryName) {
      Object[] params = new Object[]{new Integer(StructuredArtifactDefinitionBean.STATE_PUBLISHED),
                                     currentWorksiteId.getValue(),
                                     getAuthManager().getAgent(),
                                     new Integer(StructuredArtifactDefinitionBean.STATE_PUBLISHED)};
      return getHibernateTemplate().findByNamedQuery(queryName, params);
   }

   public List findHomes(Id currentWorksiteId, boolean includeHidden) {
      return findHomes(currentWorksiteId, includeHidden, true);
   }

   public List findHomes(Id currentWorksiteId, boolean includeHidden, boolean includeGlobal) {
      if (includeGlobal) {
         return findHomes(currentWorksiteId, includeHidden?"findHomesIncludeHidden":"findHomes");
      }
      else {
         return findHomes(currentWorksiteId, includeHidden?"findWorksiteHomesIncludeHidden":"findWorksiteHomes");
      }
   }
   
   public List<StructuredArtifactDefinitionBean> findAvailableHomes(Id currentWorksiteId, String currentUserId, boolean includeHidden, boolean includeGlobal) {
	   List<StructuredArtifactDefinitionBean> homes = new ArrayList<StructuredArtifactDefinitionBean>();
	   List<StructuredArtifactDefinitionBean> filteredHomes = new ArrayList<StructuredArtifactDefinitionBean>();
	   String queryName = "";
	   if (includeGlobal) {
		   queryName = includeHidden?"findAvailableHomesIncludeHidden":"findAvailableHomes";
	   }
	   else {
		   queryName = includeHidden?"findAvailableWorksiteHomesIncludeHidden":"findAvailableWorksiteHomes";
	   }
	   
	   Object[] params = new Object[]{new Integer(StructuredArtifactDefinitionBean.STATE_PUBLISHED),
               currentWorksiteId.getValue(),};
	   homes = getHibernateTemplate().findByNamedQuery(queryName, params);
	   
	   String siteRef = getWorksiteManager().getSite(currentWorksiteId.getValue()).getReference();
	   boolean canEdit = getSecurityService().unlock(SharedFunctionConstants.EDIT_ARTIFACT_DEF, siteRef);
	   
	   
	   for (StructuredArtifactDefinitionBean sadb : homes) {
		   // check for perms as well as ownership
		   Agent owner = sadb.getOwner();
		   if (canEdit || owner.getId().getValue().equals(currentUserId) || sadb.isPublished()) {
			   filteredHomes.add(sadb);
		   }
	   }
	   
	   return filteredHomes;
   }

   public StructuredArtifactDefinitionBean loadHome(String type) {
      return loadHome(getIdManager().getId(type));
   }

   public StructuredArtifactDefinitionBean loadHome(Id id) {
      return (StructuredArtifactDefinitionBean) getHibernateTemplate().get(StructuredArtifactDefinitionBean.class, id);
   }

   public StructuredArtifactDefinitionBean loadHomeByExternalType(String externalType, Id worksiteId) {
      List homes = (List) getHibernateTemplate().findByNamedQuery("loadHomeByExternalType", new Object[]{
               externalType, new Integer(StructuredArtifactDefinitionBean.STATE_PUBLISHED),
               worksiteId.getValue()});

      if (homes.size() == 0) {
         return null;
      }

      if (homes.size() == 1) {
         return (StructuredArtifactDefinitionBean) homes.get(0);
      }
      else {
         for (Iterator i = homes.iterator(); i.hasNext();) {
            StructuredArtifactDefinitionBean def = (StructuredArtifactDefinitionBean) i.next();
            if (def.getSiteId() != null) {
               if (def.getSiteId().equals(worksiteId.getValue())) {
                  return def;
               }
            }
         }
         return (StructuredArtifactDefinitionBean) homes.get(0);
      }
   }

   public StructuredArtifactDefinitionBean save(StructuredArtifactDefinitionBean bean) {
      return save(bean, true);
   }

   public StructuredArtifactDefinitionBean save(StructuredArtifactDefinitionBean bean, boolean updateModTime) {
      if (!sadExists(bean)) {
         if (updateModTime) {
            bean.setModified(new Date(System.currentTimeMillis()));
         }

         StructuredArtifactDefinition sad = null;
         try {
            if (bean.getId() == null) {
               loadNode(bean);
               bean.setCreated(new Date(System.currentTimeMillis()));
            }
            else if (bean.getSchemaFile() != null) {
               loadNode(bean);
               sad = new StructuredArtifactDefinition(bean);
               updateExistingArtifacts(sad);
            }
         }
         catch (Exception e) {
            throw new OspException("Invalid schema", e);
         }
         sad = new StructuredArtifactDefinition(bean);
         bean.setExternalType(sad.getExternalType());
         bean.setSchemaHash(calculateSchemaHash(bean));
         getHibernateTemplate().saveOrUpdate(bean);
         lockSADFiles(bean);
         //         getHibernateTemplate().saveOrUpdateCopy(bean);
      }
      else {
         throw new PersistenceException("Form name {0} exists", new Object[]{bean.getDescription()}, "description");
      }
      return bean;
   }
   
   /**
    * remove all the locks associated with this template
    */
   protected void clearLocks(Id id) {
      getContentHosting().removeAllLocks(id.getValue());
   }
   
   /**
    * locks all the files associated with this template.
    * @param template
    */
   protected void lockSADFiles(StructuredArtifactDefinitionBean bean){
      clearLocks(bean.getId());
      
      if (bean.getAlternateCreateXslt() != null) {
    	  getContentHosting().lockObject(bean.getAlternateCreateXslt().getValue(),
    		  bean.getId().getValue(), "saving a form definition", true);
      }

      if (bean.getAlternateViewXslt() != null) {
    	  getContentHosting().lockObject(bean.getAlternateViewXslt().getValue(),
              bean.getId().getValue(), "saving a form definition", true);
      }
   }

   public void delete(StructuredArtifactDefinitionBean sad) {
      for (Iterator<FormConsumer> i=getFormConsumers().iterator();i.hasNext();) {
         if (i.next().checkFormConsumption(sad.getId())) {
            throw new PersistenceException("unable_to_delete_published", new Object[]{}, "siteState");
         }
      }
      clearLocks(sad.getId());
      getHibernateTemplate().delete(sad);
   }
   
   /**
    * {@inheritDoc}
    */
   public Collection<FormConsumptionDetail> findFormUsage(StructuredArtifactDefinitionBean sad) {
      Collection<FormConsumptionDetail> results = new ArrayList<FormConsumptionDetail>();
      for (Iterator<FormConsumer> i=getFormConsumers().iterator(); i.hasNext();) {
         FormConsumer cons = (FormConsumer) i.next();
         results.addAll(cons.getFormConsumptionDetails(sad.getId()));
      }
      return results;
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

   public boolean isGlobal() {
      String siteId = getWorksiteManager().getCurrentWorksiteId().getValue();
      return isGlobal(siteId);
   }

   protected boolean isGlobal(String siteId) {

      if (getGlobalSites().contains(siteId)) {
         return true;
      }

      Site site = getWorksiteManager().getSite(siteId);
      if (site.getType() != null && getGlobalSiteTypes().contains(site.getType())) {
         return true;
      }

      return false;
   }

   protected Site getCurrentSite() {
      String siteId = getWorksiteManager().getCurrentWorksiteId().getValue();
      return getWorksiteManager().getSite(siteId);
   }

   public Collection getRootElements(StructuredArtifactDefinitionBean sad) {
      try {
         SchemaNode node = loadNode(sad);
         return node.getRootChildren();
      }
      catch (Exception e) {
         throw new OspException("Invalid schema.", e);
      }
   }

   public void validateSchema(StructuredArtifactDefinitionBean sad) {
      SchemaNode node = null;

      try {
         node = loadNode(sad);
      }
      catch (Exception e) {
         throw new OspException("Invlid schema file.", e);
      }

      if (node == null) {
         throw new OspException("Invlid schema file.");
      }
   }

   public StructuredArtifactHomeInterface convertToHome(StructuredArtifactDefinitionBean sad) {
      return new StructuredArtifactDefinition(sad);
   }

   protected SchemaNode loadNode(StructuredArtifactDefinitionBean sad)
         throws TypeException, IdUnusedException, PermissionException, ServerOverloadException {
      if (sad.getSchemaFile() != null) {
         ContentResource resource = getContentHosting().getResource(sad.getSchemaFile().getValue());
         sad.setSchema(resource.getContent());
      }

      if (sad.getSchema() == null) {
         return null;
      }

      SchemaFactory schemaFactory = SchemaFactory.getInstance();
      return schemaFactory.getSchema(new ByteArrayInputStream(sad.getSchema()));
   }

   protected boolean sadExists(StructuredArtifactDefinitionBean sad) throws PersistenceException {
      String query = "from StructuredArtifactDefinitionBean where description = ? ";
      List<Object> params = new ArrayList<Object>();
      params.add(sad.getDescription());

      if (sad.getId() != null) {
         query += " and id != ? ";
         params.add(sad.getId());
      }

      if (sad.getSiteId() != null) {
         query += " and siteId = ? ";
         params.add(sad.getSiteId());
      }
      else {
         query += " and siteId is null";
      }

      List sads = getHibernateTemplate().find(query, params.toArray());

      return sads.size() > 0;
   }

   /**
    * @param sad
    * @param artifact
    * @throws OspException if artifact doesn't validate
    */
   protected void validateAfterTransform(StructuredArtifactDefinition sad, StructuredArtifact artifact) throws OspException {
      //TODO figure out how to do the validator
//      StructuredArtifactValidator validator = new StructuredArtifactValidator();
//      artifact.setHome(sad);
//      Errors artifactErrors = new BindExceptionBase(artifact, "bean");
//      validator.validate(artifact, artifactErrors);
//      if (artifactErrors.getErrorCount() > 0) {
//         StringBuilder buf = new StringBuilder();
//         for (Iterator i=artifactErrors.getAllErrors().iterator();i.hasNext();){
//            ObjectError error = (ObjectError) i.next();
//            buf.append(error.toString() + " ");
//         }
//         throw new OspException(buf.toString());
//      }
   }

   protected void saveAll(StructuredArtifactDefinition sad, Collection artifacts) {
      for (Iterator i = artifacts.iterator(); i.hasNext();) {
         StructuredArtifact artifact = (StructuredArtifact) i.next();
         try {
            sad.store(artifact);
         }
         catch (PersistenceException e) {
            logger.error("problem saving artifact with id " + artifact.getId().getValue() + ":" + e);
         }
      }
   }

   /**
    * Uses the submitted xsl file to transform the existing artifacts into the schema.
    * This process puts the artifact home into system only start while is does its work.
    * This is necessary so that users won't be able to update artifacts while this is going on.
    * The system transforms every object in memory and validates before writing any artifact back out.
    * This way if something fails the existing data will stay intact.
    * <p/>
    * TODO possible memory issues
    * TODO all this work need to be atomic
    *
    * @param sad
    * @throws OspException
    */
   protected void updateExistingArtifacts(StructuredArtifactDefinition sad) throws OspException {

      //if we don't have an xsl file and don't need one, return
      if (!sad.getRequiresXslFile()) {
         return;
      }

      if (sad.getRequiresXslFile() && (sad.getXslConversionFileId() == null || sad.getXslConversionFileId().getValue().length() == 0)) {
         throw new OspException("xsl conversion file required");
      }

      // put artifact home in system only state while we do this work.
      // this along with repository authz prevents someone from updating an artifact
      // while this is going on
      StructuredArtifactDefinitionBean currentHome = this.loadHome(sad.getId());
      boolean originalSystemOnlyState = currentHome.isSystemOnly();
      currentHome.setSystemOnly(true);
      getHibernateTemplate().saveOrUpdate(currentHome);

      boolean finished = false;
      String type = sad.getType().getId().getValue();
      Collection artifacts = getArtifactFinder().findByType(type);
      Collection<StructuredArtifact> modifiedArtifacts = new ArrayList<StructuredArtifact>();

      // perform xsl transformations on existing artifacts
      try {
         for (Iterator i = artifacts.iterator(); i.hasNext();) {
            StructuredArtifact artifact = (StructuredArtifact) i.next();
            try {
               transform(sad, artifact);
               validateAfterTransform(sad, artifact);
               // don't persist yet, in case error is found in some other artifact
               modifiedArtifacts.add(artifact);
            }
            catch (TransformerException e) {
               throw new OspException("problem transforming item with id=" + artifact.getId().getValue(), e);
            }
            catch (IOException e) {
               throw new OspException(e);
            }
            catch (JDOMException e) {
               throw new OspException("problem with xsl file: " + e.getMessage(), e);
            }
         }
         finished = true;
      } finally {
         // reset systemOnly state back to whatever if was
         // but only if there was an error
         if (!originalSystemOnlyState && !finished) {
            currentHome.setSystemOnly(false);
            getHibernateTemplate().saveOrUpdate(currentHome);
         }
      }

      // since all artifacts validated go ahead and persist changes
      saveAll(sad, modifiedArtifacts);
   }

   protected Element getStructuredArtifactRootElement(StructuredArtifactDefinition sad, StructuredArtifact artifact) {
      return sad.getArtifactAsXml(artifact).getChild("structuredData").getChild(sad.getRootNode());
   }

   protected void transform(StructuredArtifactDefinition sad, StructuredArtifact artifact) throws IOException, TransformerException, JDOMException {
      /* todo transform
      logger.debug("transforming artifact " + artifact.getId().getValue() + " owned by " + artifact.getOwner().getDisplayName());
      JDOMResult result = new JDOMResult();
      SAXBuilder builder = new SAXBuilder();
      Document xslDoc = builder.build(sad.getXslConversionFileStream());
      Transformer transformer = TransformerFactory.newInstance().newTransformer(new JDOMSource(xslDoc));
      Element rootElement = getStructuredArtifactRootElement(sad, artifact);

      transformer.transform(new JDOMSource(rootElement), result);

      artifact.setBaseElement((Element) result.getResult().get(0));
      */
   }

   public AuthenticationManager getAuthManager() {
      return (AuthenticationManager) ComponentManager.getInstance().get("authManager");
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }

   public ToolManager getToolManager() {
      return toolManager;
   }

   public void setToolManager(ToolManager toolManager) {
      this.toolManager = toolManager;
   }

   protected Id getToolId() {
      Placement placement = toolManager.getCurrentPlacement();
      return idManager.getId(placement.getId());
   }

   public void importResources(String fromContext, String toContext, List resourceIds) {
      // select all this worksites forms and create them for the new worksite
      Map homes = getWorksiteHomes(getIdManager().getId(fromContext), true);

      for (Iterator i = homes.entrySet().iterator(); i.hasNext();) {
         Map.Entry entry = (Map.Entry) i.next();
         StructuredArtifactDefinitionBean bean = (StructuredArtifactDefinitionBean) entry.getValue();

         if (fromContext.equals(bean.getSiteId())) {
            getHibernateTemplate().evict(bean);
            bean.setSiteId(toContext);
            bean.setId(null);
            bean.setSiteState(StructuredArtifactDefinitionBean.STATE_UNPUBLISHED);

            //Check for an existing form
            if (findBean(bean) == null) {
               getHibernateTemplate().save(bean);
            }
         }
      }
   }

   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   protected void init() throws Exception {
      logger.info("init()");
      // register functions
      FunctionManager.registerFunction(SharedFunctionConstants.CREATE_ARTIFACT_DEF);
      FunctionManager.registerFunction(SharedFunctionConstants.EDIT_ARTIFACT_DEF);
      FunctionManager.registerFunction(SharedFunctionConstants.EXPORT_ARTIFACT_DEF);
      FunctionManager.registerFunction(SharedFunctionConstants.DELETE_ARTIFACT_DEF);
      FunctionManager.registerFunction(SharedFunctionConstants.PUBLISH_ARTIFACT_DEF);
      FunctionManager.registerFunction(SharedFunctionConstants.SUGGEST_GLOBAL_PUBLISH_ARTIFACT_DEF);

      addConsumer(this);
      
      boolean runOnInit = ServerConfigurationService.getBoolean("metaobj.schemahash.runOnInit", false);
      if (runOnInit) {
    	  boolean updateSchemaHashes = ServerConfigurationService.getBoolean("metaobj.schemahash.update", false);
    	  verifySchemaHashes(updateSchemaHashes);
      }      
      
      if (isAutoDdl()) {

         updateSchemaHash();
         org.sakaiproject.tool.api.Session sakaiSession = SessionManager.getCurrentSession();
         String userId = sakaiSession.getUserId();
         sakaiSession.setUserId("admin");
         sakaiSession.setUserEid("admin");
   
         try {
            logger.info("Updating base Metaobj XSLT files (auto.ddl is on).");
            createResource("/org/sakaiproject/metaobj/shared/control/formCreate.xslt", "formCreate.xslt",
               "used for default rendering of form add and update", "text/xml", isReplaceViews(), true);
   
            createResource("/org/sakaiproject/metaobj/shared/control/formFieldTemplate.xslt", "formFieldTemplate.xslt",
               "used for default rendering of form fields", "text/xml", isReplaceViews(), true);
   
            createResource("/org/sakaiproject/metaobj/shared/control/formView.xslt", "formView.xslt",
               "used for default rendering of form viewing", "text/xml", isReplaceViews(), true);
         }
         finally{
            sakaiSession.setUserEid(userId);
            sakaiSession.setUserId(userId);
         }
      }
      
      if (isEnableLocksConversion()) {
    	  List<StructuredArtifactDefinitionBean> homes = findAllHomes();
    	  for (StructuredArtifactDefinitionBean bean : homes) {
    		  lockSADFiles(bean);
    	  }
      }

   }

   protected void updateSchemaHash() {
      List forms = getHibernateTemplate().findByNamedQuery("findByNullSchemaHash");

      for (Iterator i = forms.iterator(); i.hasNext();) {
         StructuredArtifactDefinitionBean bean = (StructuredArtifactDefinitionBean) i.next();
         bean.setSchemaHash(calculateSchemaHash(bean));
         getHibernateTemplate().saveOrUpdate(bean);
      }
   }

   protected String calculateSchemaHash(StructuredArtifactDefinitionBean bean) {
      String hashString = "";
      if (bean.getSchema() != null) {
         hashString += new String(bean.getSchema());
      }
      hashString += convertNull2Empty(bean.getDocumentRoot());
      hashString += convertNull2Empty(bean.getDescription());
      hashString += convertNull2Empty(bean.getInstruction());
      return hashString.hashCode() + "";
   }
   
   /**
    * If the input is null, return an empty string instead, 
    * otherwise return the input
    * @param input
    * @return
    */
   private String convertNull2Empty(String input) {
	   String output = "";
	   if (input!= null) {
		   output = input;
	   }
	   return output;
   }
   
   public void verifySchemaHashes(boolean updateInvalid) {
	   List<StructuredArtifactDefinitionBean> homes = findAllHomes();
	   int badCount = 0;
	   for (StructuredArtifactDefinitionBean bean : homes) {
		   String calcHash = calculateSchemaHash(bean);
		   if (!bean.getSchemaHash().equalsIgnoreCase(calcHash)) {
			   String text = "Form has invalid schema hash: " + bean.getId() + "; stored: " + bean.getSchemaHash() + "; calc: " + calcHash;
			   logger.warn(text);
			   badCount++;
			   if (updateInvalid) {
				   if (bean.getOwner() == null || bean.getOwner().getId() == null) {
					   text = "Unable to update schema hash because unable to get owner for bean: " + bean.getId();
					   logger.warn(text);
				   }
				   else {
					   bean.setSchemaHash(calculateSchemaHash(bean));
					   getHibernateTemplate().saveOrUpdate(bean);
					   text = "Form schema hash has been updated: " + bean.getId();
					   logger.info(text);
				   }
			   }
		   }
	   }
	   String text = "There are " + badCount + " forms with invalid schema hashes.";
	   logger.warn(text);
   }

   public String packageForDownload(Map params, OutputStream out) throws IOException {

      String[] formIdObj = (String[]) params.get(DOWNLOAD_FORM_ID_PARAM);
      packageFormForExport(formIdObj[0], out);
      
      //Blank filename for now -- no more dangerous, since the request is in the form of a filename
      return "";
   }

   
   /**
    * This is the default method for exporting a form into a stream.  This method does check the
    * form export permission.
    * @param formId String
    * @param os OutputStream
    * @throws IOException
    */
   public void packageFormForExport(String formId, OutputStream os)
         throws IOException {
      packageFormForExport(formId, os, true);
   }


   /**
    * This method will export a form into a stream.  It has the ability to turn off checking
    * for the export form permission.
    * @param formId String
    * @param os OutputStream
    * @param checkPermission boolean
    * @throws IOException
    */
   public void packageFormForExport(String formId, OutputStream os, boolean checkPermission)
         throws IOException {
      if (checkPermission) {
         getAuthzManager().checkPermission(SharedFunctionConstants.EXPORT_ARTIFACT_DEF,
            getToolId());
      }

      CheckedOutputStream checksum = new CheckedOutputStream(os,
            new Adler32());
      ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(checksum));

      StructuredArtifactDefinitionBean bean = loadHome(formId);
      writeSADtoZip(bean, zos, "");

      zos.finish();
      zos.flush();
   }

   /**
    * Given a bean this method will convert it into a new XML document.
    * This does not put the schema into XML
    * @param bean StructuredArtifactDefinitionBean
    * @return Document - XML
    */
   public Document exportSADAsXML(StructuredArtifactDefinitionBean bean) {
      Element rootNode = new Element("metaobjForm");

      rootNode.setAttribute("formatVersion", "2.1");

      Element attrNode = new Element("description");
      attrNode.addContent(new CDATA(bean.getDescription()));
      rootNode.addContent(attrNode);

      attrNode = new Element("instruction");
      attrNode.addContent(new CDATA(bean.getInstruction()));
      rootNode.addContent(attrNode);

      attrNode = new Element("documentRootNode");
      attrNode.addContent(new CDATA(bean.getDocumentRoot()));
      rootNode.addContent(attrNode);
      
      if ( bean.getAlternateCreateXslt() != null ) {
         attrNode = new Element("altCreateXslt");
         attrNode.addContent(new CDATA(bean.getAlternateCreateXslt().getValue()));
         rootNode.addContent(attrNode);
      }
      
      if ( bean.getAlternateViewXslt() != null ) {
         attrNode = new Element("altViewXslt");
         attrNode.addContent(new CDATA(bean.getAlternateViewXslt().getValue()));
         rootNode.addContent(attrNode);
      }
      
      return new Document(rootNode);
   }

   
   /**
    * Given a bean, this method puts it into a stream via UTF-8 encoding
    * @param bean StructuredArtifactDefinitionBean
    * @param os OutputStream
    * @throws IOException
    */
   public void writeSADasXMLtoStream(StructuredArtifactDefinitionBean bean, OutputStream os) throws IOException {
      Document doc = exportSADAsXML(bean);
      String docStr = (new XMLOutputter()).outputString(doc);
      os.write(docStr.getBytes("UTF-8"));
   }

   public void writeSADtoZip(StructuredArtifactDefinitionBean bean, ZipOutputStream zos) throws IOException {
      writeSADtoZip(bean, zos, "");
   }

   public void writeSADtoZip(StructuredArtifactDefinitionBean bean, ZipOutputStream zos, String path) throws IOException {
      // if the path is a directory without an end slash, then add one
      if (!path.endsWith("/") && path.length() > 0) {
         path += "/";
      }
      ZipEntry definitionFile = new ZipEntry(path + "formDefinition.xml");

      zos.putNextEntry(definitionFile);
      writeSADasXMLtoStream(bean, zos);
      zos.closeEntry();

      ZipEntry schemeFile = new ZipEntry(path + "schema.xsd");

      zos.putNextEntry(schemeFile);
      zos.write(bean.getSchema());
      zos.closeEntry();
      
      List existingEntries = new ArrayList();
      storeFile(zos, bean.getAlternateCreateXslt(), existingEntries);
      storeFile(zos, bean.getAlternateViewXslt(), existingEntries);

   }

   /**
    * Given a resource id, this parses out the Form from its input stream.
    * Once the enties are found, they are inserted into the given worksite.
    *
    * @param worksiteId   Id
    * @param resourceId   an String
    * @param findExisting
    */
   public boolean importSADResource(Id worksiteId, String resourceId, boolean findExisting)
         throws IOException, ServerOverloadException, PermissionException, 
               IdUnusedException, ImportException, UnsupportedFileTypeException
         {
      String id = getContentHosting().resolveUuid(resourceId);

      try {
         ContentResource resource = getContentHosting().getResource(id);
         MimeType mimeType = new MimeType(resource.getContentType());

         if (!mimeType.equals(new MimeType("application/zip")) &&
               !mimeType.equals(new MimeType("application/x-zip-compressed"))) {
        	 logger.warn(".importSADResource has identified the mime type as something unsupported: " + mimeType.toString() + ".");
        	 logger.warn("The import file must be a zip file for the import to work properly.");
        	 logger.warn("It's possible that the browser has identified the mime type incorrectly, so the import may still work.");
         }
         InputStream zipContent = resource.streamContent();
         StructuredArtifactDefinitionBean bean = importSad(worksiteId, zipContent, findExisting, false);

         return bean != null;
      }
      catch (TypeException te) {
         logger.error(".importSADResource",te);
      }
      return false;
   }

   public StructuredArtifactDefinitionBean importSad(Id worksiteId, InputStream in,
                                                     boolean findExisting, boolean publish)
         throws IOException, ImportException {
      return importSad(worksiteId, in, findExisting, publish, true);
   }
   public StructuredArtifactDefinitionBean importSad(Id worksiteId, InputStream in,
                                                     boolean findExisting, boolean publish, boolean foundThrowsException)
         throws IOException, ImportException {
      ZipInputStream zis = new ZipInputStream(in);

      StructuredArtifactDefinitionBean bean = readSADfromZip(zis, worksiteId.getValue(), publish);
      if (bean != null) {
         if (findExisting) {
            StructuredArtifactDefinitionBean found = findBean(bean);
            if (found != null) {
               if (foundThrowsException) {
                  throw new ImportException("The Form being imported already exists and has been published");
               } else {
                  return found;
               }
            }
         }

         String origTitle = bean.getDescription();
         int index = 0;
         while (sadExists(bean)) {
            index++;
            bean.setDescription(origTitle + " " + index);
         }

         save(bean);
         // doesn't like imported beans in batch mode???
         getHibernateTemplate().flush();
      }
      return bean;
   }

   /**
    * 
    * @param bean
    * @return
    */
   protected StructuredArtifactDefinitionBean findBean(StructuredArtifactDefinitionBean bean) {
      Object[] params = new Object[]{new Integer(StructuredArtifactDefinitionBean.STATE_PUBLISHED),
                                     bean.getSiteId(), bean.getSchemaHash()};
      List beans = getHibernateTemplate().findByNamedQuery("findBean", params);

      //There's an order by on this query so that the global form (if any) will be listed first
      if (beans.size() > 0) {
         return (StructuredArtifactDefinitionBean) beans.get(0);
      }
      return null;
   }


   public StructuredArtifactDefinitionBean readSADfromZip(ZipInputStream zis,
                                                          String worksite, boolean publish)
         throws IOException {
      StructuredArtifactDefinitionBean bean = new StructuredArtifactDefinitionBean();
      boolean hasXML = false, hasXSD = false;

      bean.setCreated(new Date(System.currentTimeMillis()));
      bean.setModified(bean.getCreated());

      bean.setOwner(getAuthManager().getAgent());
      bean.setSiteId(worksite);
      bean.setSiteState(publish ? StructuredArtifactDefinitionBean.STATE_PUBLISHED :
            StructuredArtifactDefinitionBean.STATE_UNPUBLISHED);

      if (isGlobal(worksite)) {
         bean.setGlobalState(publish ? StructuredArtifactDefinitionBean.STATE_PUBLISHED :
            StructuredArtifactDefinitionBean.STATE_UNPUBLISHED);
         bean.setSiteId(null);
      }

      ZipEntry currentEntry = zis.getNextEntry();

      if (currentEntry == null) {
         return null;
      }
      
      // If the zip was opened and re-zipped, then the directory was
      //    compressed with the files.  we need to deal with 
      //    the directory
      if(currentEntry.getName().endsWith("/")) {
         zis.closeEntry();
         currentEntry = zis.getNextEntry();
      }
      
      try {
         Hashtable<Id, Id> fileMap = new Hashtable<Id, Id>();
         String tempDirName = getIdManager().createId().getValue();
         ContentCollectionEdit fileParent = getExpandedFileDir(tempDirName);
         boolean gotFile = false;
         
         while (currentEntry != null) {
            logger.debug("current entry name: " + currentEntry.getName());
            
            File entryFile = new File( currentEntry.getName() );
            
            if (entryFile.getName().startsWith(".")) {
               logger.warn(".readSADfromZip skipping control file: " + currentEntry.getName() );
            }   
            else if (currentEntry.getName().endsWith("xml")) {
               readSADfromXML(bean, zis);
               hasXML = true;
            }
            else if (currentEntry.getName().endsWith("xsd")) {
               readSADSchemaFromXML(bean, zis);
               hasXSD = true;
            }
            else if (!currentEntry.isDirectory()) {
               gotFile = true;
               processFile(currentEntry, zis, fileMap, fileParent);
               
            }
   
            zis.closeEntry();
            currentEntry = zis.getNextEntry();
         }
         
         if (gotFile) {
            fileParent.getPropertiesEdit().addProperty(
                  ResourceProperties.PROP_DISPLAY_NAME, bean.getDescription());
            getContentHosting().commitCollection(fileParent);
         }
         else {
            getContentHosting().cancelCollection(fileParent);
         }
         
         if (bean.getAlternateCreateXslt() != null)
            bean.setAlternateCreateXslt((Id)fileMap.get(bean.getAlternateCreateXslt()));
         
         if (bean.getAlternateViewXslt() != null)
            bean.setAlternateViewXslt((Id)fileMap.get(bean.getAlternateViewXslt()));
         
      }
      catch (Exception exp) {
         logger.error(".readSADFromZip", exp);
         return null;
      }
      

      bean.setSchemaHash(calculateSchemaHash(bean));
      return bean;
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
   protected ContentCollectionEdit getExpandedFileDir(String origName) throws TypeException, IdUnusedException, PermissionException, IdUsedException, IdInvalidException, InconsistentException {
      ContentCollection userCollection = getUserCollection();
      
      try {
         //TODO use the bean org.theospi.portfolio.admin.model.IntegrationOption.siteOption 
         // in common/components to get the name and id for this site.
         
         ResourceLoader rb = new ResourceLoader("org/sakaiproject/metaobj/registry/messages");
         
         ContentCollectionEdit groupCollection = getContentHosting().addCollection(userCollection.getId() + IMPORT_BASE_FOLDER_ID);
         groupCollection.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, rb.getString("form_import_folder"));
         getContentHosting().commitCollection(groupCollection);
      }
      catch (IdUsedException e) {
         // ignore... it is already there.
          if (logger.isDebugEnabled()) {
              logger.debug(e);
          } 
      }
      catch (Exception e) {
         logger.error(".getExpandedFileDir",e);
         return null;
      }
      
      ContentCollection collection = getContentHosting().getCollection(userCollection.getId() + IMPORT_BASE_FOLDER_ID + "/");
      
      String childId = collection.getId() + origName;
      return getContentHosting().addCollection(childId);
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

   private StructuredArtifactDefinitionBean readSADfromXML(StructuredArtifactDefinitionBean bean, InputStream inStream) {
      SAXBuilder builder = new SAXBuilder();
      builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // SAK-23245

      try {
         byte[] bytes = readStreamToBytes(inStream);
         //  for some reason the SAX Builder sometimes won't recognize
         //these bytes as correct utf-8 characters.  So we want to read it in
         //as utf-8 and spot it back out as utf-8 and this will correct the
         //bytes.  In my test, it added two bytes somewhere in the string.
         //and adding those two bytes made the string work for saxbuilder.
         //
         bytes = (new String(bytes, "UTF-8")).getBytes("UTF-8");
         Document document = builder.build(new ByteArrayInputStream(bytes));

         Element topNode = document.getRootElement();

         bean.setDescription(new String(topNode.getChildTextTrim("description").getBytes(), "UTF-8"));
         bean.setInstruction(new String(topNode.getChildTextTrim("instruction").getBytes(), "UTF-8"));
         bean.setDocumentRoot(new String(topNode.getChildTextTrim("documentRootNode").getBytes(), "UTF-8"));
         if (topNode.getChildTextTrim("altCreateXslt") != null)
            bean.setAlternateCreateXslt(getIdManager().getId(new String(topNode.getChildTextTrim("altCreateXslt").getBytes(), "UTF-8")));
         if (topNode.getChildTextTrim("altViewXslt") != null)
            bean.setAlternateViewXslt(getIdManager().getId(new String(topNode.getChildTextTrim("altViewXslt").getBytes(), "UTF-8")));
      }
      catch (Exception jdome) {
         logger.error(".readSADfromXML", jdome);
         return null;
      }
      return bean;
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

   private StructuredArtifactDefinitionBean readSADSchemaFromXML(StructuredArtifactDefinitionBean bean, InputStream inStream) throws IOException {
      bean.setSchema(readStreamToBytes(inStream));
      return bean;
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

   public Element createFormViewXml(String formId, String returnUrl) {
      formId = getContentHosting().getUuid(formId);
      Artifact art = getArtifactFinder().load(getIdManager().getId(formId));
      return createFormViewXml(art, returnUrl);
   }

   public Element createFormViewXml(Artifact art, String returnUrl) {
      Element root = new Element("formView");
      Element data = new Element("formData");

      ReadableObjectHome home = (ReadableObjectHome) art.getHome();
      if (home instanceof PresentableObjectHome) {
         data.addContent(((PresentableObjectHome) home).getArtifactAsXml(art));
      }

      root.addContent(data);

      if (returnUrl != null) {
         Element returnUrlElement = new Element("returnUrl");
         returnUrlElement.addContent(new CDATA(returnUrl));
         root.addContent(returnUrlElement);
      }

      Element css = new Element("css");
      String skin = null;
      try {
         skin = getCurrentSite().getSkin();
      }
      catch (NullPointerException npe) {
         //Couldn't find the site, just use default skin
      }
      if (skin == null || skin.length() == 0) {
         skin = ServerConfigurationService.getString("skin.default");
      }
      String skinRepo = ServerConfigurationService.getString("skin.repo");
      Element uri = new Element("uri");
      uri.setAttribute("order", "1");
      uri.setText(skinRepo + "/tool_base.css");
      css.addContent(uri);
      uri = new Element("uri");
      uri.setAttribute("order", "2");
      uri.setText(skinRepo + "/" + skin + "/tool.css");
      css.addContent(uri);
      root.addContent(css);
      return root;
   }

   public Element createFormViewXml(ElementBean bean, String returnUrl) {
      Element root = new Element("formView");
      Element data = new Element("formData");

      //data.addContent(((PresentableObjectHome) home).getArtifactAsXml(art));

      root.addContent(data);

      if (returnUrl != null) {
         Element returnUrlElement = new Element("returnUrl");
         returnUrlElement.addContent(new CDATA(returnUrl));
         root.addContent(returnUrlElement);
      }

      Element css = new Element("css");
      String skin = null;
      try {
         skin = getCurrentSite().getSkin();
      }
      catch (NullPointerException npe) {
         //Couldn't find the site, just use default skin
      }
      if (skin == null || skin.length() == 0) {
         skin = ServerConfigurationService.getString("skin.default");
      }
      String skinRepo = ServerConfigurationService.getString("skin.repo");
      Element uri = new Element("uri");
      uri.setText(skinRepo + "/tool_base.css");
      css.addContent(uri);
      uri = new Element("uri");
      uri.setText(skinRepo + "/" + skin + "/tool.css");
      css.addContent(uri);
      root.addContent(css);
      return root;
   }

   public InputStream getTransformer(String type, boolean readOnly) {
      try {
         String viewLocation = "/group/PortfolioAdmin/system/formCreate.xslt";
         StructuredArtifactDefinitionBean sadb = loadHome(type);
         if (sadb == null) {
            ToolSession toolSession = SessionManager.getCurrentToolSession();
            sadb = (StructuredArtifactDefinitionBean)toolSession.getAttribute(SAD_SESSION_TAG);
         }
         if (sadb != null && sadb.getAlternateCreateXslt() != null) {
            String id = getContentHosting().resolveUuid(sadb.getAlternateCreateXslt().getValue());
            if ( id != null )
               viewLocation = id;
         }
         
         if (readOnly) {
            viewLocation = "/group/PortfolioAdmin/system/formView.xslt";
            if (sadb != null && sadb.getAlternateViewXslt() != null) {
               String id = getContentHosting().resolveUuid(sadb.getAlternateViewXslt().getValue());
               if ( id != null )
                  viewLocation = id;
            }
         }
         List refs = new ArrayList();
         refs.add(getContentHosting().getReference(viewLocation));
         refs.add(getContentHosting().getReference("/group/PortfolioAdmin/system"));
         Map funcs = new HashMap();
         funcs.put(ContentHostingService.EVENT_RESOURCE_READ, refs);
         funcs.put(ContentHostingService.AUTH_RESOURCE_HIDDEN, refs);
         getSecurityService().pushAdvisor(new AllowChildrenMapSecurityAdvisor(funcs));
         return getContentHosting().getResource(viewLocation).streamContent();
      } catch (Exception e) {
         logger.error(".getTransformer",e);
         return null;
      }
   }

   public boolean hasHomes() {
      if (ThreadLocalManager.get(HAS_HOMES_TAG) == null) {
         ThreadLocalManager.set(HAS_HOMES_TAG, new Boolean(findHomes(false).size() > 0));
      }
      return ((Boolean)ThreadLocalManager.get(HAS_HOMES_TAG)).booleanValue();
   }

   public void addConsumer(FormConsumer consumer) {
      getFormConsumers().add(consumer);
   }

   public ArtifactFinder getArtifactFinder() {
      return artifactFinder;
   }

   public void setArtifactFinder(ArtifactFinder artifactFinder) {
      this.artifactFinder = artifactFinder;
   }

   public int getExpressionMax() {
      return expressionMax;
   }

   public void setExpressionMax(int expressionMax) {
      this.expressionMax = expressionMax;
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
         logger.error(".loadResource",e);
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

   protected String createResource(String resourceLocation, String name,
                                   String description, String type, boolean replace, boolean pubview) {
      ByteArrayOutputStream bos = loadResource(resourceLocation);
      ContentResource resource;
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
         logger.error(".createResource",e);
         return null;
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
         logger.error(".createResource",e);
         return null;
      }

      String resourceId = folder + name;
      try {
         if (!replace) {
            ContentResource testResource = getContentHosting().getResource(resourceId);
            if (testResource != null) {
               return testResource.getId();
            }
         }
         getContentHosting().removeResource(resourceId);
      }
      catch (TypeException e) {
         // should not happen -- requested resource should not be a collection
         logger.warn(e);
      }
      catch (IdUnusedException e) {
         // ignore, must be new resource
         if (logger.isDebugEnabled()) {
             logger.debug(e);
         }
      }
      catch (PermissionException e) {
         // should not happen - log unexpected error
         logger.warn(e);
      }
      catch (InUseException e) {
         // should not happen - log unexpected error
         logger.warn(e);
      }

      try {
         resource = getContentHosting().addResource(name, folder, 1, type,
                     bos.toByteArray(), resourceProperties, NotificationService.NOTI_NONE);
         getContentHosting().setPubView(resource.getId(), pubview);
      }
      catch (IdUniquenessException e) {
         //Odd case -- tried to add new, but failed; return the existent ID attempted
         logger.info("Failure trying to write Metaobj file: " + folder + name, e);
         return resourceId;
      }
      catch (Exception e) {
         logger.error(".createResource",e);
         return null;
      }
      return resource.getId();
   }
   
   protected void storeFile(ZipOutputStream zos, Id fileId, List existingEntries) throws IOException {
      if (fileId == null) {
         return;
      }
      try {
         String userId = SessionManager.getCurrentSessionUserId();
         String id = getContentHosting().resolveUuid(fileId.getValue());
         String ref = getContentHosting().getReference(id);
         //getSecurityService().pushAdvisor(
         //      new SimpleSecurityAdvisor(userId, ContentHostingService.EVENT_RESOURCE_READ, ref));
         
         getSecurityService().pushAdvisor(new SecurityAdvisor()
            {
               public SecurityAdvice isAllowed(String userId, String function, String reference)
               {
                  return SecurityAdvice.ALLOWED;
               }
            });
            
         ContentResource resource = getContentHosting().getResource(id);
         String newName = resource.getProperties().getProperty(
               resource.getProperties().getNamePropDisplayName());
         String cleanedName = newName.substring(newName.lastIndexOf('\\')+1);
         
         if (!existingEntries.contains(fileId)) {
            existingEntries.add(fileId);
            storeFileInZip(zos, resource.streamContent(),
                  resource.getContentType() + File.separator +
                  fileId.getValue() + File.separator + cleanedName);
         }
         getSecurityService().popAdvisor();
      } catch (PermissionException e) {
         logger.error(".storeFile", e);
      } catch (IdUnusedException e) {
         logger.error(".storeFile", e);
      } catch (TypeException e) {
         logger.error(".storeFile", e);
      } catch (ServerOverloadException e) {
         logger.error(".storeFile", e);
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
   
   protected void processFile(ZipEntry currentEntry, ZipInputStream zis,
         Hashtable fileMap, ContentCollection fileParent) throws IOException {

      File file = new File(currentEntry.getName());

      // Unclear what the original intention is of checking for a files great-grandparent for mime-type
      // but for now we'll make sure grand-parent exists to avoid throwing NPE. 
      // In testing, NPE was thrown for __MACOSX files
      if ( file.getParentFile() == null || file.getParentFile().getParentFile() == null || file.getParentFile().getParentFile().getParentFile() == null )
      {
         logger.warn("StructuredArtifactDefinitionManagerImpl.processFile() found unexpected file "+ currentEntry.getName() );
         return;
      }
      
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
         logger.error(".processFile", exp);
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

   public boolean isReplaceViews() {
      return replaceViews;
   }

   public void setReplaceViews(boolean replaceViews) {
      this.replaceViews = replaceViews;
   }

   public List getFormConsumers() {
      return formConsumers;
   }

   public void setFormConsumers(List formConsumers) {
      this.formConsumers = formConsumers;
   }

   public boolean checkFormConsumption(Id formId) {
      String type = formId.getValue();

      getSecurityService().pushAdvisor(new SecurityAdvisor() {
         public SecurityAdvice isAllowed(String userId, String function, String reference) {
            return SecurityAdvice.ALLOWED;
         }
      });

      try {
         Collection arts = getStructuredArtifactFinder().findByType(type);

         return arts != null && arts.size() > 0;
      }
      finally {
         getSecurityService().popAdvisor();
      }
   }
   
   public Collection<FormConsumptionDetail> getFormConsumptionDetails(Id formId) {
      Collection<FormConsumptionDetail> results = new ArrayList<FormConsumptionDetail>();
      String type = formId.getValue();

      getSecurityService().pushAdvisor(new SecurityAdvisor() {
         public SecurityAdvice isAllowed(String userId, String function, String reference) {
            return SecurityAdvice.ALLOWED;
         }
      });

      try {
         Collection<StructuredArtifact> arts = getStructuredArtifactFinder().findByType(type);
         String formConsumptionType = messages.getString("content_resource_type");
         String formNameText = messages.getString("form_name");
         String formOwnerText = messages.getString("form_owner");
         for (Iterator<StructuredArtifact> i = arts.iterator(); i.hasNext();) {
            StructuredArtifact art = (StructuredArtifact) i.next();
            
            Reference ref = EntityManager.newReference(art.getBaseResource().getReference());
            String context = ref.getContext();
            
            FormConsumptionDetail fcd = new FormConsumptionDetail(
                  art.getHome().getType().getId().getValue(), 
                  context, 
                  formConsumptionType, 
                  formNameText + art.getDisplayName(), 
                  formOwnerText + art.getOwner().getDisplayName());
            
            results.add(fcd);
         }
         return results;
      }
      finally {
         getSecurityService().popAdvisor();
      }
   }
   
   public void checkFormAccess(String resource_uuid) {
	   String resourceId = getContentHosting().resolveUuid(resource_uuid);
	   boolean allowed = getContentHosting().allowGetResource(resourceId);
	   if (!allowed)
		   return;
	   
	   Artifact art = getArtifactFinder().load(getIdManager().getId(resource_uuid));
	   PresentableObjectHome home = (PresentableObjectHome)art.getHome();
	   Element elm = home.getArtifactAsXml(art);
	   List<String> files = new ArrayList<String>();
	   //try to get all the attachments

	   List<Element> elms = findElementNamesForFileType(elm);
	   for (Element element : elms) {
		   String name = element.getAttributeValue("name");
		   try {
			   XPath fileAttachPath = XPath.newInstance(".//" + name);
			   List<Element> fileElements = fileAttachPath.selectNodes(elm);
			   for (Element theElm : fileElements) {
				   String file = theElm.getText();
				   String fileRef = getContentHosting().getReference(file);
				   logger.debug("Pushing " + fileRef + " on advisor stack");
				   files.add(fileRef);
			   }

		   } catch (JDOMException e) {
			   logger.error("unable to get element", e);
		   }
	   }

	  getSecurityService().pushAdvisor(
			  new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ, files));
   }
   
   /**
    * Return a list of Element objects from the passed root that are of type xs:anyURI
    * @param root
    * @return
    */
   private List<Element> findElementNamesForFileType(Element root) {
	   List<Element> fileElements = new ArrayList<Element>();
	   try {
		   XPath fileAttachPath = XPath.newInstance(".//element[@type='xs:anyURI']");
		   fileElements = fileAttachPath.selectNodes(root);
	   } catch (JDOMException e) {
		   logger.error("unable to get element", e);
	   }
	   return fileElements;
   }

   public SecurityService getSecurityService() {
      return securityService;
   }

   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }

   public ArtifactFinder getStructuredArtifactFinder() {
      return structuredArtifactFinder;
   }

   public void setStructuredArtifactFinder(ArtifactFinder structuredArtifactFinder) {
      this.structuredArtifactFinder = structuredArtifactFinder;
   }

   public boolean isAutoDdl() {
      return autoDdl;
   }

   public void setAutoDdl(boolean autoDdl) {
      this.autoDdl = autoDdl;
   }

   public boolean isEnableLocksConversion() {
	   return enableLocksConversion;
   }

   public void setEnableLocksConversion(boolean enableLocksConversion) {
	   this.enableLocksConversion = enableLocksConversion;
   }
}
