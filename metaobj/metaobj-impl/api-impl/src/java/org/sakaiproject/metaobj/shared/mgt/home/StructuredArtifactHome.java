/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/shared/mgt/home/StructuredArtifactHome.java $
 * $Id: StructuredArtifactHome.java 120216 2013-02-18 19:44:04Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.shared.mgt.home;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.mgt.*;
import org.sakaiproject.metaobj.shared.model.*;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.utils.Config;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.worksite.intf.WorksiteAware;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.metaobj.registry.FormResourceType;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.exception.*;
import org.sakaiproject.util.ResourceLoader;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 17, 2004
 * Time: 2:52:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class StructuredArtifactHome extends XmlElementHome
      implements WorksiteAware, ApplicationContextAware, Comparable, StreamableObjectHome {

   protected final static org.apache.commons.logging.Log logger =
         org.apache.commons.logging.LogFactory.getLog(StructuredArtifactHome.class);

   private boolean modifiable = false;
   private PresentableObjectHome repositoryHelper;
   private IdManager idManager;
   private String siteId;
   private ArtifactFinder artifactFinder;
   private ResourceLoader rl = new ResourceLoader("org.sakaiproject.metaobj.messages");

   private static final MessageFormat format =
         new MessageFormat("<META HTTP-EQUIV=\"Refresh\" CONTENT=\"0;URL={0}/member/viewArtifact.osp?artifactId={1}&artifactType={2}&pid={3}\">");

   public Artifact store(Artifact object) throws PersistenceException {
      Id id = object.getId();

      if (id == null) {
         return addArtifact(object);
      }
      else {
         return updateArtifact(object);
      }
   }

   protected Artifact updateArtifact(Artifact object) throws PersistenceException {
      StructuredArtifact artifact = (StructuredArtifact) object;
      String resourceId = getContentHostingService().resolveUuid(artifact.getId().getValue());
      try {
         ContentResourceEdit resourceEdit = getContentHostingService().editResource(resourceId);
         resourceEdit.setContent(getInfoBytes(object));
         resourceEdit.getProperties().addProperty(
            ResourceProperties.PROP_DISPLAY_NAME, object.getDisplayName());
         getContentHostingService().commitResource(resourceEdit);
      } catch (PermissionException e) {
         throw new PersistenceException(e, rl.getString("perm_file_err"),
            null, null);
      } catch (IdUnusedException e) {
         throw new PersistenceException(e, rl.getString("unknown_file_err"),
            null, null);
      } catch (TypeException e) {
         throw new PersistenceException(e, rl.getString("unknown_file_err"),
            null, null);
      } catch (InUseException e) {
         throw new PersistenceException(e, rl.getString("unknown_file_err"),
            null, null);
      } catch (ServerOverloadException e) {
         throw new PersistenceException(e, rl.getString("unknown_file_err"),
            null, null);
      } catch (OverQuotaException e) {
         throw new PersistenceException(e, rl.getString("quota_file_err"),
            null, null);
      }

      return object;
   }
   
   public StructuredArtifact load(ContentResource resource, Id artifactId) {
      try {
         Agent resourceOwner = getAgentManager().getAgent(
            resource.getProperties().getProperty(ResourceProperties.PROP_CREATOR));

         SAXBuilder builder = new SAXBuilder();
         builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // SAK-23245
         Document doc = builder.build(resource.streamContent());

         StructuredArtifact xmlObject =
            new StructuredArtifact(doc.getRootElement(), getSchema().getChild(getRootNode()));

         xmlObject.setBaseResource(resource);
         xmlObject.setId(artifactId);
         xmlObject.setDisplayName(
            (String) resource.getProperties().get(
               resource.getProperties().getNamePropDisplayName()));
         xmlObject.setHome(this);
         xmlObject.setOwner(resourceOwner);

         return xmlObject;
      }
      catch (Exception e) {
         throw new PersistenceException(e, "", null, null);
      }
   }
   
   public StructuredArtifact load(ContentResource resource) {
	   Id artifactId = getIdManager().getId(getContentHostingService().getUuid(resource.getId()));
	   return load(resource, artifactId);
   }

   private AgentManager getAgentManager() {
      return (AgentManager) ComponentManager.get("agentManager");
   }

   public Artifact load(Id id) throws PersistenceException {
      /** todo
       NodeMetadata node = getNodeMetadataService().getNode(id);
       SAXBuilder builder = new SAXBuilder();

       try {
       Document doc = builder.build(
       getStreamStore().load(node, RepositoryNode.TECH_MD_TYPE));

       StructuredArtifact xmlObject =
       new StructuredArtifact(doc.getRootElement(), getSchema().getChild(getRootNode()));

       xmlObject.setId(id);
       xmlObject.setDisplayName(node.getName());
       xmlObject.setHome(this);
       xmlObject.setOwner(node.getOwner());

       return xmlObject;
       } catch (Exception e) {
       throw new SchemaInvalidException(e);
       }
       */
      return null;
   }


   public void remove(Artifact object) throws PersistenceException {
      /**
       getStreamStore().delete(getNodeMetadata(object.getId()));
       getNodeMetadataService().delete(object.getId());
       */
   }

   public Artifact cloneArtifact(Artifact copy, String newName) throws PersistenceException {
      /** todo
       NodeMetadata oldMetadata = getNodeMetadataService().getNode(copy.getId());
       String origName = oldMetadata.getName();
       oldMetadata.setName(newName);
       NodeMetadata newMetadata = getNodeMetadataService().copyNode(oldMetadata);
       oldMetadata.setName(origName);
       getStreamStore().copyStreams(getNodeMetadataService().getNode(copy.getId()), newMetadata);

       return new LightweightArtifact(this, newMetadata);
       */
      return null;
   }

   protected Artifact addArtifact(Artifact object) throws PersistenceException {
      StructuredArtifact artifact = (StructuredArtifact) object;
      String newFileId = artifact.getParentFolder() + getIdManager().createId().getValue();

      try {
         ContentResourceEdit resource = getContentHostingService().addResource(newFileId);
         resource.setResourceType(FormResourceType.FORM_TYPE_ID);
         ResourcePropertiesEdit resourceProperties = resource.getPropertiesEdit();
         resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, object.getDisplayName());
         resourceProperties.addProperty (ResourceProperties.PROP_DESCRIPTION, object.getDisplayName());
         resourceProperties.addProperty(ResourceProperties.PROP_CONTENT_ENCODING, "UTF-8");
         resourceProperties.addProperty(ResourceProperties.PROP_STRUCTOBJ_TYPE, getTypeId());
         resourceProperties.addProperty(ResourceProperties.PROP_IS_COLLECTION, Boolean.FALSE.toString());
         resourceProperties.addProperty(ContentHostingService.PROP_ALTERNATE_REFERENCE,
            org.sakaiproject.metaobj.shared.mgt.MetaobjEntityManager.METAOBJ_ENTITY_PREFIX);

         resource.setContent(getInfoBytes(artifact));
         resource.setContentType("application/x-osp");
			
         getContentHostingService().commitResource(resource);
         artifact.setId(getIdManager().getId(getContentHostingService().getUuid(newFileId)));
         return object;
      } catch (PermissionException e) {
         throw new PersistenceException("No permission to write file",
            null, null);
      } catch (IdUsedException e) {
         throw new PersistenceException("Resource exists {0}",
            new Object[]{artifact.getDisplayName()}, "displayName");
      } catch (IdInvalidException e) {
         throw new PersistenceException("Invalid name {0}",
            new Object[]{artifact.getDisplayName()}, "displayName");
      } catch (InconsistentException e) {
         throw new PersistenceException("Invalid name {0}",
            new Object[]{artifact.getDisplayName()}, "displayName");
      } catch (ServerOverloadException e) {
         throw new PersistenceException(e, "Unknown file error",
            null, null);
      } catch (OverQuotaException e) {
         throw new PersistenceException("Over quota",
            null, null);
      }
   }

   /**
    * protected InputStream getFileStream(StructuredArtifact xmlObject) {
    * StringBuilder sb = new StringBuilder();
    * <p/>
    * format.format(new Object[]{getHostBaseUrl(), xmlObject.getId().getValue(),
    * getType().getId().getValue(),
    * getWorksiteManager().getCurrentWorksiteId() },
    * sb, new FieldPosition(0));
    * logger.debug("redirecting to: " + sb.toString());
    * InputStream is = new ByteArrayInputStream(sb.toString().getBytes());
    * <p/>
    * return is;
    * }
    */

   protected byte[] getInfoBytes(Artifact object) throws PersistenceException {

      XMLOutputter outputter = new XMLOutputter();
      StructuredArtifact xmlObject = (StructuredArtifact) object;
      ByteArrayOutputStream os = new ByteArrayOutputStream();

      try {
         Format format = Format.getPrettyFormat();
         outputter.setFormat(format);
         outputter.output(xmlObject.getBaseElement(),
               os);
         return os.toByteArray();
      }
      catch (IOException e) {
         throw new PersistenceException(e, "Unable to write object", null, null);
      }
   }

   public byte[] getBytes(StructuredArtifact artifact) {
      return getInfoBytes(artifact);
   }

   protected InputStream getInfoStream(Artifact object) throws PersistenceException {
      return new ByteArrayInputStream(getInfoBytes(object));
   }

   public String getHostBaseUrl() {
      return Config.getInstance().getProperties().getProperty("baseUrl");
   }

   public Element getArtifactAsXml(Artifact art) {
      StructuredArtifact sa = (StructuredArtifact) art;

      Element root = new Element("artifact");
      root.addContent(getMetadata(sa));

      Element data = new Element("structuredData");
      Element baseElement = (Element) sa.getBaseElement().clone();
      data.addContent(baseElement);
      root.addContent(data);

      Element schemaData = new Element("schema");
      schemaData.addContent(createInstructions());
      try {
         schemaData.addContent(addSchemaInfo(getRootSchema()));
      } catch (JDOMException e) {
         throw new RuntimeException("Invalid schema info");
      }

      // for now, don't call this... we may add this when
      // we need to store id rather than ref... for now, keep
      // this backward compatible with existing templates
      // replaceFileRefs(schemaData, data);

      root.addContent(schemaData);

      return root;
   }

   protected void replaceFileRefs(Element schemaData, Element data) {
      try {
         XPath fileAttachPath = XPath.newInstance(".//element[@type='xs:anyURI']");
         List fileElements = fileAttachPath.selectNodes(schemaData);
         for (Iterator i=fileElements.iterator();i.hasNext();) {
            processFileElement((Element)i.next(), data);
         }
      } catch (JDOMException e) {
         throw new RuntimeException(e);
      }
   }

   protected void processFileElement(Element element, Element data) throws JDOMException {
      String path = "";

      while (element != null) {
         if (path.length() > 0) {
            path = "/" + path;
         }
         path = element.getAttributeValue("name") + path;
         element = element.getParentElement();
         if (element != null) {
            element = element.getParentElement();
         }
      }

      List fileElements = XPath.selectNodes(data, path);

      for (Iterator i=fileElements.iterator();i.hasNext();) {
         Element filePath = (Element) i.next();
         String fileId = filePath.getTextTrim();
         Artifact fileArt = getArtifactFinder().load(getIdManager().getId(fileId));
         PresentableObjectHome home = (PresentableObjectHome) fileArt.getHome();
         Element file = home.getArtifactAsXml(fileArt);
         file.setName("artifact");
         filePath.addContent(file);
      }
   }

   protected Element getMetadata(Artifact art) {
      Element root = new Element("metaData");

      if (art.getId() != null) {
         root.addContent(createNode("id", art.getId().getValue()));
      }
      root.addContent(createNode("displayName", art.getDisplayName()));

      Element type = new Element("type");
      root.addContent(type);

      type.addContent(createNode("id", "file"));
      type.addContent(createNode("description", "file"));

      ContentResource contentResource = ((StructuredArtifact)art).getBaseResource();
      if (contentResource != null) {
         Element repositoryNode =
            ContentHostingUtil.createRepoNode(contentResource);
         root.addContent(repositoryNode);
      }
      
      return root;
   }

   protected Element createNode(String name, String value) {
      Element newNode = new Element(name);
      newNode.addContent(value);
      return newNode;
   }

   protected Element createInstructions() {
      Element instructions = new Element("instructions");
      instructions.setContent(new CDATA(getInstruction()));
      return instructions;
   }

   protected Element addSchemaInfo(SchemaNode schema) throws JDOMException {
      Element schemaElement = new Element("element");
      schemaElement.setAttribute("name", schema.getName());
      if (schema.getType() != null && schema.getType().getBaseType() != null) {
         schemaElement.setAttribute("type", schema.getType().getBaseType());
      }
      schemaElement.setAttribute("minOccurs", schema.getMinOccurs() + "");
      schemaElement.setAttribute("maxOccurs", schema.getMaxOccurs() + "");
      Element annotation = schema.getSchemaElement().getChild("annotation", schema.getSchemaElement().getNamespace());

      if (annotation != null) {
         schemaElement.addContent(i18nFilterAnnotations(annotation));
      }

      Element simpleType = schema.getSchemaElement().getChild("simpleType", schema.getSchemaElement().getNamespace());

      if (simpleType != null) {
         schemaElement.addContent((Content) simpleType.clone());
      }
      else if (schema.getSchemaElement().getAttribute("type") != null) {
         Element externalType = getSimpleType(schema.getSchemaElement().getAttributeValue("type"), 
            schema.getSchemaElement().getDocument().getRootElement());
         if (externalType != null) {
            schemaElement.addContent((Content) externalType.clone());
         }
      }

      List children = schema.getChildren();
      Element childElement = new Element("children");
      boolean found = false;
      for (Iterator i = children.iterator(); i.hasNext();) {
         childElement.addContent(addSchemaInfo((SchemaNode) i.next()));
         found = true;
      }

      if (found) {
         schemaElement.addContent(childElement);
      }

      return schemaElement;
   }

   protected Element getSimpleType(String attribute, Element rootElement) throws JDOMException {
      XPath docPath = XPath.newInstance("xs:simpleType[@name='"+attribute+"']");
      List elements = docPath.selectNodes(rootElement);

      if (elements.size() == 0) {
         return null;
      }

      return (Element) elements.get(0);
   }

   protected Content i18nFilterAnnotations(Element content) throws JDOMException {
      Locale locale = rl.getLocale();
      Map<String, Element> documentElements = new Hashtable<String, Element>();

      filterElements(documentElements, content, null);
      filterElements(documentElements, content, locale.getLanguage());
      filterElements(documentElements, content, locale.getLanguage()
         + "_" + locale.getCountry());
      filterElements(documentElements, content, locale.getLanguage()
         + "_" + locale.getCountry() + "_" + locale.getVariant());

      Element returned = (Element) content.clone();
      returned.removeChildren("documentation", content.getNamespace());

      for (Iterator<Element> i=documentElements.values().iterator();i.hasNext();) {
         returned.addContent((Content) i.next().clone());
      }

      return returned;
   }

   protected void filterElements(Map<String, Element> documentElements, Element content, String lang) throws JDOMException {
      XPath docPath;
      if (lang == null) {
         docPath = XPath.newInstance("xs:documentation[not(@xml:lang)]");   
      }
      else {
         docPath = XPath.newInstance("xs:documentation[lang('"+lang+"')]");
      }
      List elements = docPath.selectNodes(content);
      for (Iterator<Element> i=elements.iterator();i.hasNext();) {
         Element child = i.next();
         String source = child.getAttributeValue("source");
         
         if (source != null) {
            documentElements.put(child.getAttributeValue("source"), child);
         }
      }
   }

   public String getSiteId() {
      return siteId;
   }

   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }

   public boolean isModifiable() {
      return modifiable;
   }

   /**
    * @return true if SAD is global (available to all worksites)
    */
   public boolean isGlobal() {
      return (getSiteId() == null || getSiteId().length() == 0);
   }

   public PresentableObjectHome getRepositoryHelper() {
      return repositoryHelper;
   }

   public void setRepositoryHelper(PresentableObjectHome repositoryHelper) {
      this.repositoryHelper = repositoryHelper;
   }

   public IdManager getIdManager() {
      if (idManager == null) {
         setIdManager((IdManager) ComponentManager.get("idManager"));
      }
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      setIdManager((IdManager) applicationContext.getBean("idManager"));
      setRepositoryHelper((PresentableObjectHome) applicationContext.getBean("repositoryHelper"));
   }

   public int compareTo(Object o) {
      StructuredArtifactHome that = (StructuredArtifactHome) o;
      return this.getType().getDescription().toLowerCase().compareTo(that.getType().getDescription().toLowerCase());
   }

   public InputStream getStream(Id artifactId) {
      try {
         StructuredArtifact artifact = (StructuredArtifact) load(artifactId);
         return getInfoStream(artifact);
      }
      catch (PersistenceException e) {
         throw  new RuntimeException(e);
      }
   }

   public boolean isSystemOnly() {
      return false;
   }

   protected WorksiteManager getWorksiteManager() {
      return (WorksiteManager) ComponentManager.getInstance().get(WorksiteManager.class.getName());
   }

   public ArtifactFinder getArtifactFinder() {
      if (artifactFinder == null) {
         setArtifactFinder((ArtifactFinder) ComponentManager.get(
            "org.sakaiproject.metaobj.shared.ArtifactFinder.fileArtifact"));
      }
      return artifactFinder;
   }

   public void setArtifactFinder(ArtifactFinder artifactFinder) {
      this.artifactFinder = artifactFinder;
   }

   protected ContentHostingService getContentHostingService() {
      return (ContentHostingService)ComponentManager.get(
         "org.sakaiproject.content.api.ContentHostingService");
   }
   
}
