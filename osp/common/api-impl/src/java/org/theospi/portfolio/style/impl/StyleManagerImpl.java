/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api-impl/src/java/org/theospi/portfolio/style/impl/StyleManagerImpl.java $
* $Id:StyleManagerImpl.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2006, 2007, 2008 The Sakai Foundation
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

package org.theospi.portfolio.style.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.content.api.LockManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.security.AllowMapSecurityAdvisor;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.MimeType;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService.SelectionType;
import org.sakaiproject.site.cover.SiteService;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.theospi.event.EventService;
import org.theospi.event.EventConstants;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.style.StyleConsumer;
import org.theospi.portfolio.style.StyleFunctionConstants;
import org.theospi.portfolio.style.mgt.StyleManager;
import org.theospi.portfolio.style.model.Style;

public class StyleManagerImpl extends HibernateDaoSupport
   implements StyleManager {

   private AuthorizationFacade authzManager = null;
   private IdManager idManager = null;
   private WorksiteManager worksiteManager;
   private AuthenticationManager authnManager = null;
   private LockManager lockManager;
   private ContentHostingService contentHosting = null;
   private AgentManager agentManager;
   private SecurityService securityService = null;
   private EventService eventService = null;
   private List<String> globalSites;
   private List<String> globalSiteTypes;
   private List<StyleConsumer> consumers;
   private boolean autoDdl = false;

   
   public Style storeStyle(Style style) {
      return storeStyle(style, true);
   }
   
   public Style storeStyle (Style style, boolean checkAuthz) {
      updateFields(style, checkAuthz);
      getHibernateTemplate().saveOrUpdate(style);
      lockStyleFiles(style);
      eventService.postEvent(EventConstants.EVENT_STYLE_ADD,style.getId().getValue());

      return style;
   }
   
   public Style mergeStyle(Style style) {
      return mergeStyle(style, true);
   }
   
   public Style mergeStyle (Style style, boolean checkAuthz) {
      if (style.getId() == null) {
         return storeStyle(style, checkAuthz);
      }
      updateFields(style, checkAuthz);
      getHibernateTemplate().merge(style);
      lockStyleFiles(style);
      eventService.postEvent(EventConstants.EVENT_STYLE_REVISE,style.getId().getValue());

      return style;
   }
   protected void updateFields(Style style, boolean checkAuthz)
   {
      style.setModified(new Date(System.currentTimeMillis()));
      style.setStyleHash(calculateStyleHash(style));

      boolean newStyle = (style.getId() == null);

      if (newStyle) {
         style.setCreated(new Date(System.currentTimeMillis()));

         if (checkAuthz) {
            getAuthzManager().checkPermission(StyleFunctionConstants.CREATE_STYLE,
               getIdManager().getId(style.getSiteId()));
         }
      } else {
         if (checkAuthz) {
            getAuthzManager().checkPermission(StyleFunctionConstants.EDIT_STYLE,
                  style.getId());
         }
      }
   }
   
   protected void lockStyleFiles(Style style){
      getLockManager().removeAllLocks(style.getId().getValue());
      getLockManager().lockObject(style.getStyleFile().getValue(), 
            style.getId().getValue(), "saving a style", true);
      
   }
   
   public Style getStyle(Id styleId) {
      return (Style) getHibernateTemplate().get(Style.class, styleId);
   }
   
   public Style getLightWeightStyle(final Id styleId) {
      HibernateCallback callback = new HibernateCallback() {

         public Object doInHibernate(Session session) throws HibernateException, SQLException {
            Style style = (Style) session.get(Style.class, styleId);
            return style;
         }

      };

      try {
         Style style = (Style) getHibernateTemplate().execute(callback);
         return style;
      } catch (HibernateObjectRetrievalFailureException e) {
         logger.debug(e);
         return null;
      }
   }
   
   public Collection findSiteStyles(String currentWorksiteId) {
      Object[] params = new Object[]{currentWorksiteId,
                                     getAuthnManager().getAgent()};
      return getHibernateTemplate().findByNamedQuery("findSiteStyles", params);
   }
   
   public Collection findPublishedStyles(String currentWorksiteId) {
      Object[] params = new Object[]{Integer.valueOf(Style.STATE_PUBLISHED),
                                     currentWorksiteId,
                                     getAuthnManager().getAgent()};
      return getHibernateTemplate().findByNamedQuery("findPublishedStyles", params);
   }
   
   protected String buildGlobalSiteList() {
	   StringBuffer queryBuffer = new StringBuffer();
	   queryBuffer.append("(");
      
      for (Iterator<String> i=getGlobalSites().iterator();i.hasNext();) {
         String site = (String)i.next();
         queryBuffer.append("'").append(site).append("',");
      }
      
      for (Iterator<String> j = getGlobalSiteTypes().iterator(); j.hasNext();) {
         String type = (String)j.next();
         List<Site> sites = SiteService.getSites(SelectionType.ANY, type, null, null, null, null);
         for (Iterator<Site> k = sites.iterator(); k.hasNext();) {
            Site theSite = (Site) k.next();
            queryBuffer.append("'").append(theSite.getId()).append("',");
         }
      }
      
      queryBuffer.append("'')");
      
      return queryBuffer.toString();
   }
   
   public Collection findGlobalStyles(Agent agent) {
      String query = "from Style s where ((s.siteId in " + buildGlobalSiteList() + 
            " and (s.globalState = ? or s.owner = ?)) or s.globalState = 1)";
      Object[] params = new Object[]{Integer.valueOf(Style.STATE_PUBLISHED),
                                     agent};
      return getHibernateTemplate().find(query, params);
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
   
   public Node getNode(Id artifactId) {
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
   
   public Node getNode(Reference ref) {
      String nodeId = getContentHosting().getUuid(ref.getId());

      return getNode(getIdManager().getId(nodeId));
   }
   
   public boolean deleteStyle(final Id styleId) {
      Style style = getStyle(styleId);
      getAuthzManager().checkPermission(StyleFunctionConstants.DELETE_STYLE, style.getId());
      
      // handle things that are using this style
      if (!checkStyleConsumption(styleId)) {
      
         getLockManager().removeAllLocks(styleId.getValue());
         
         getHibernateTemplate().delete(style);
      }
      else {
         return false;
      }
      eventService.postEvent(EventConstants.EVENT_STYLE_DELETE,style.getId().getValue());
      // If we get here, I think it was deleted
      return true;
   }
   
   protected boolean checkStyleConsumption(Id styleId) {
      for (Iterator i = getConsumers().iterator(); i.hasNext();) {
         StyleConsumer sc = (StyleConsumer) i.next();
         if (sc.checkStyleConsumption(styleId)) {
            return true;
         }
      }
      return false;
   }
   
   public List<StyleConsumer> getConsumers() {
      return this.consumers;
   }

   public List getStyles(Id consumerId) {
      for (Iterator<StyleConsumer> i = consumers.iterator();i.hasNext();) {
         List returned = i.next().getStyles(consumerId);
         if (returned != null) {
            return returned;
         }
      }
      return null;
   }

   public void setConsumers(List<StyleConsumer> consumers) {
      this.consumers = consumers;
   }
   
   public Collection getStylesForWarehouse()
   {
      return getHibernateTemplate().findByNamedQuery("findStyles");
   }
   
   public void packageStyleForExport(Set styleIds, OutputStream os) throws IOException {
      CheckedOutputStream checksum = new CheckedOutputStream(os,
            new Adler32());
      ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(
            checksum));
      for (Iterator<String> i=styleIds.iterator();i.hasNext();) {
         String id = (String) i.next();
         processStyle(id, zos);
      }

      zos.finish();
      zos.flush();
   }
   
   protected void processStyle(String styleId, ZipOutputStream zos) throws IOException {
      Style style = getStyle(getIdManager().getId(styleId));
      
      Node node = getNode(style.getStyleFile());
      
      try {
         processStyleFile(zos, node);
      }
      catch (ServerOverloadException e) {
         throw new RuntimeException(e);
      }
   
      ZipEntry definitionFile = new ZipEntry("style-"+styleId+".xml");
      zos.putNextEntry(definitionFile);
      Document doc = createStyleAsXml(style);
      String docStr = (new XMLOutputter()).outputString(doc);
      zos.write(docStr.getBytes("UTF-8"));
      zos.closeEntry();
   }
   
   protected void processStyleFile(ZipOutputStream zos, Node node)
   throws ServerOverloadException, IOException {
      ContentResource entity = node.getResource();
      
      String newName = entity.getProperties().getProperty(entity.getProperties().getNamePropDisplayName());
      String cleanedName = newName.substring(newName.lastIndexOf('\\')+1);
      String entryName = "attachments/" + entity.getContentType() + "/" +
         getNodeHash(node) + "/" + cleanedName;
      
      storeFileInZip(zos, entity.streamContent(), entryName);
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
   
   protected Document createStyleAsXml(Style style) {
      Element rootNode = new Element("style");
      
      rootNode.setAttribute("formatVersion", "2.1");
      addNode(rootNode, "id", style.getId().getValue());
      addNode(rootNode, "name", style.getName());
      addNode(rootNode, "description", style.getDescription());
      addNode(rootNode, "owner", style.getOwner().getId().getValue());
      addNode(rootNode, "siteId", style.getSiteId());
      
      Node styleFileNode = getNode(style.getStyleFile());
      
      addNode(rootNode, "nodeRef", styleFileNode.getResource().getReference());
      
      
      Element items = new Element("items");

      rootNode.addContent(items);

      return new Document(rootNode);
   }
   
   protected void addNode(Element parentNode, String name, String value) {
      Element attrNode = new Element(name);
      attrNode.addContent(new CDATA(value));
      parentNode.addContent(attrNode);
   }
   
   protected int getNodeHash(Node node) {
      return node.getResource().getReference().hashCode();
   }
   
   /**
    * @param parent the parent resource folder where attachments go
    * @param siteId the site which will recieve the imported "stuff"
    * @param in  The Input stream representing the output stream of the export
    * @return Map contains a map with keys being of type String as old Ids and the 
    * values as being the Style object
    */
   public Map importStyleList(ContentCollection parent, String siteId, InputStream in) throws IOException {
      Map styleMap = new Hashtable();
      ZipInputStream zis = new ZipInputStream(in);
      ZipEntry currentEntry = zis.getNextEntry();

      Map attachmentMap = new Hashtable();

      while (currentEntry != null) {
          if (!currentEntry.isDirectory()) {
            if (currentEntry.getName().startsWith("style-")) {
               importStyle(siteId, zis, styleMap);
            }
            else if (currentEntry.getName().startsWith("attachments/")) {
               importAttachmentRef(parent, currentEntry, siteId, zis, attachmentMap);
            }
          }

         zis.closeEntry();
         currentEntry = zis.getNextEntry();
      }

      postPocessAttachments(styleMap.values(), attachmentMap);

      for (Iterator i=styleMap.entrySet().iterator();i.hasNext();) {
    	  Map.Entry entry = (Map.Entry) i.next(); 
         String key = entry.getKey().toString();
         Style style = (Style)entry.getValue();
         Style found = findMatchingStyle(style);
         if (found == null) {
            storeStyle(style, false);
         } else {
            removeFromSession(style);
            removeFromSession(found);
            styleMap.put(key, found);
         }
      }

      return styleMap;
   }
   
   /**
    * given a stream this reads in an xml style and places the new style into the map
    * @param siteId
    * @param is
    * @param styleMap
    * @throws IOException
    */
   protected void importStyle(String siteId, InputStream is, Map styleMap)
         throws IOException {
      SAXBuilder builder = new SAXBuilder();
      builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // SAK-23131
      Document document = null;
   
      byte []bytes = readStreamToBytes(is);
   
      try {
         document = builder.build(new ByteArrayInputStream(bytes));
      }
      catch (JDOMException e) {
         throw new RuntimeException(e);
      }
      Element docRoot = document.getRootElement();
   
      Id oldStyleId = getIdManager().getId(docRoot.getChildText("id"));
      String name = docRoot.getChildText("name");
      String description = docRoot.getChildText("description");
      String nodeRef = docRoot.getChildTextNormalize("nodeRef");
      
      Style style = new Style();
      //style.setId(getIdManager().createId());
      style.setName(name);
      style.setDescription(description);
      style.setOwner(getAuthnManager().getAgent());
      style.setSiteId(siteId);
      style.setCreated(new Date(System.currentTimeMillis()));
      style.setModified(style.getCreated());
      style.setGlobalState(Style.STATE_UNPUBLISHED);
      style.setNodeRef(nodeRef);
      
      styleMap.put(oldStyleId.getValue(), style);
   }
   
   protected byte[] readStreamToBytes(InputStream inStream) throws IOException {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      byte data[] = new byte[10*1024];

      int count;
      while ((count = inStream.read(data, 0, 10*1024)) != -1) {
         bytes.write(data, 0, count);
      }
      byte []tmp = bytes.toByteArray();
      bytes.close();
      return tmp;
   }
      
   protected void importAttachmentRef(ContentCollection fileParent, ZipEntry currentEntry, String siteId,
         ZipInputStream zis, Map attachmentMap) {
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
         
         String fileId = fileParent.getId() + file.getName();
         ContentResource rez = null;
         try {
            rez = getContentHosting().getResource(fileId);
         } catch(IdUnusedException iduue) {
            logger.info(iduue);
         }
         if(rez == null) {
            ContentResourceEdit resource = getContentHosting().addResource(fileId);
            ResourcePropertiesEdit resourceProperties = resource.getPropertiesEdit();
            resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, file.getName());
            resource.setContent(bos.toByteArray());
            resource.setContentType(contentType);
            getContentHosting().commitResource(resource);
            rez = resource;
         }
         attachmentMap.put(oldId, rez.getId());
      }
      catch (Exception exp) {
         throw new RuntimeException(exp);
      }
   }
   
   protected void postPocessAttachments(Collection styles, Map attachmentMap) {
      for (Iterator i=styles.iterator();i.hasNext();) {
         Style style = (Style) i.next();
         postProcessStyleFile(style, attachmentMap);
      }
   }
   
   protected void postProcessStyleFile(Style style, Map attachmentMap) {
      int nodeHash = style.getNodeRef().hashCode();
      String id = (String)attachmentMap.get("" + nodeHash);

      if (id != null) {
         String nodeId = getContentHosting().getUuid(id);
         style.setStyleFile(getIdManager().getId(nodeId));
         style.setStyleHash(calculateStyleHash(style));
      } else {
         style.setStyleFile(null);
      }
   }
   
   /**
    * 
    * @param style
    * @return The calculated hash string using the Style file's content (the css file)
    */
   protected String calculateStyleHash(Style style) {
      String hashString = "";
      try {
         Node styleFileNode = getNode(style.getStyleFile());
         
         if (styleFileNode != null) {
            hashString += new String(styleFileNode.getResource().getContent());
            //hashString += Integer.toString(styleFileNode.getInputStream().hashCode());
         }
      }
      catch (ServerOverloadException e) {
         return null;
      }
      return hashString.hashCode() + "";
   }
   
   protected void updateStyleHash() {
      List styles = getHibernateTemplate().findByNamedQuery("findByNullStyleHash");

      for (Iterator i = styles.iterator(); i.hasNext();) {
         Style style = (Style) i.next();
         style.setStyleHash(calculateStyleHash(style));
         getHibernateTemplate().saveOrUpdate(style);
      }
   }
   
   public void init() {
      logger.info("init()");
      
      if (isAutoDdl()) {
         try {
            updateStyleHash();
         }
         catch (Exception e) {
            logger.error("Error in StyleManager.init()", e);
         }
      }
   }
   
   /**
    * 
    * @param style
    * @return Returns the first Style object it finds that is either globally published,
    *  or in the same site and also has the same styleHash value.
    */
   protected Style findMatchingStyle(Style style) {
      Object[] params = new Object[]{Integer.valueOf(Style.STATE_PUBLISHED),
                                     style.getSiteId(), style.getStyleHash()};
      List styles = getHibernateTemplate().findByNamedQuery("findMatchingStyle", params);

      if (styles.size() > 0) {
         return (Style) styles.get(0);
      }
      return null;
   }
   
   public void removeFromSession(Object obj) {
      this.getHibernateTemplate().evict(obj);
      try {
         getHibernateTemplate().getSessionFactory().evict(obj.getClass());
      } catch (HibernateException e) {
         logger.error(e);
      }
   }
   
   /**
    * {@inheritDoc}
    */
   public List<String> createStyleUrlList(List<Style> styles) {
	   if (styles != null) {
		   List<String> returned = new ArrayList<String>(styles.size());
		   for (Iterator<Style> i=styles.iterator();i.hasNext();) {
			   returned.add(getStyleUrl(i.next()));
		   }
	
		   return returned;
	   }
	   return new ArrayList<String>();
   }

   /**
    * {@inheritDoc}
    */
   public String getStyleUrl(Style style) {
	   Node styleNode = getNode(style.getStyleFile());
	   return styleNode.getExternalUri();
   }

   public AuthenticationManager getAuthnManager() {
      return authnManager;
   }

   public void setAuthnManager(AuthenticationManager authnManager) {
      this.authnManager = authnManager;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public List<String> getGlobalSites() {
      return globalSites;
   }

   public void setGlobalSites(List<String> globalSites) {
      this.globalSites = globalSites;
   }

   public List<String> getGlobalSiteTypes() {
      return globalSiteTypes;
   }

   public void setGlobalSiteTypes(List<String> globalSiteTypes) {
      this.globalSiteTypes = globalSiteTypes;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
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

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   /**
    * @return the securityService
    */
   public SecurityService getSecurityService() {
      return securityService;
   }

   /**
    * @param securityService the securityService to set
    */
   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }

   public boolean isAutoDdl() {
      return autoDdl;
   }

   public void setAutoDdl(boolean autoDdl) {
      this.autoDdl = autoDdl;
   }

   public EventService getEventService() {
	   return eventService;
   }

   public void setEventService(EventService eventService) {
	   this.eventService = eventService;
   }
}
