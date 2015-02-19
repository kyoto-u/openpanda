/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/shared/mgt/home/XmlElementHome.java $
 * $Id: XmlElementHome.java 120216 2013-02-18 19:44:04Z ottenhoff@longsight.com $
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.FinderException;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;
import org.sakaiproject.metaobj.shared.model.Type;
import org.sakaiproject.metaobj.utils.xml.SchemaFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaInvalidException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.content.api.ContentResource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 9, 2004
 * Time: 1:22:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlElementHome implements StructuredArtifactHomeInterface, InitializingBean, ResourceLoaderAware {

   private SchemaNode schema = null;
   private String rootNode = null;
   private Date schemaDate = null;
   protected final Log logger = LogFactory.getLog(getClass());
   private File homeDirectory = null;
   private String schemaFileName;
   private Type type = null;
   private String typeId = null;
   private IdManager idManager = null;
   public static final String XSD_DIR = "xsd";
   public static final String XML_HOME_PATH = "xmlHome";
   private ResourceLoader resourceLoader;
   /**
    * help information supplied to the user when creating an instance of this xmlelement
    */
   private String instruction;


   public XmlElementHome() {
   }

   public XmlElementHome(String rootNode) {
      this.rootNode = rootNode;
   }

   public SchemaNode getSchema() {
      if (schema == null) {
         File schemaFile = getSchemaFile(schemaFileName);
         schema = SchemaFactory.getInstance().getSchema(schemaFile);
         schemaDate = new Date(schemaFile.lastModified());
      }
      return schema;
   }

   public String getDocumentRoot() {
      return null;
   }

   protected File getSchemaFile(String schemaFileName) {
      return new File(this.pathToWebInf() + File.separator + XSD_DIR + File.separator + schemaFileName);
   }

   public void setSchema(SchemaNode schema) {
      this.schema = schema;
   }

   public Artifact store(Artifact object) throws PersistenceException {
      String id = (String) object.getId().getValue();

      File objectFile = null;

      if (id == null) {
         try {
            objectFile = File.createTempFile(rootNode, ".xml", homeDirectory);
         }
         catch (IOException e) {
            logger.error("", e);
            throw new OspException(e);
         }
      }
      else {
         objectFile = new File(homeDirectory, id);
         if (objectFile.exists()) {
            objectFile.delete();
         }
      }

      XMLOutputter outputter = new XMLOutputter();
      StructuredArtifact xmlObject = (StructuredArtifact) object;

      xmlObject.setId(objectFile.getName());

      FileOutputStream outstream = null;
      try {
         outstream = new FileOutputStream(objectFile);
         Format format = Format.getPrettyFormat();
         outputter.setFormat(format);
         outputter.output(xmlObject.getBaseElement(), outstream);
      }
      catch (IOException e) {
         logger.error("", e);
         throw new OspException(e);
      }
      finally {
         try {
            if (outstream != null)
                outstream.close();
         }
         catch (Exception e2) {
            logger.warn("Problem closing stream: ", e2);
         }
      }

      return object;
   }

   public void remove(Artifact object) {
      File objectFile = null;
      if (object != null && object.getId() != null)
         objectFile = new File(homeDirectory, object.getId().getValue());

      boolean deleted = false;
      if (objectFile != null)
         deleted = objectFile.delete();

      if (!deleted)
         logger.warn("Could not delete file: " + objectFile.getPath());
   }

   public Artifact store(String displayName, String contentType, Type type,
                         InputStream in) throws PersistenceException {
      // todo complete
      return null;
   }

   public Artifact update(Artifact object, InputStream in) throws PersistenceException {
      return null;//todo
   }

   public Type getType() {
      return type;
   }

   public String getExternalType() {
      if (getSchema() == null) {
         return "";
      }
      return getSchema().getTargetNamespace().getURI() + "?" + getRootNode();
   }

   public void setType(Type type) {
      this.type = type;
   }

   public Artifact load(Id id) throws PersistenceException {
      return load(id.getValue());
   }

   public StructuredArtifact load(ContentResource resource) {
      return null;
   }
   
   public StructuredArtifact load(ContentResource resource, Id artifactId) {
	   return null;
   }

   protected Artifact load(String id) throws PersistenceException {
      File objectFile = new File(homeDirectory, id);

      SAXBuilder builder = new SAXBuilder();
      builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // SAK-23245

      try {
         Document doc = builder.build(objectFile);

         StructuredArtifact xmlObject =
               new StructuredArtifact(doc.getRootElement(), getSchema().getChild(rootNode));

         xmlObject.setId(id);

         xmlObject.setHome(this);

         return xmlObject;
      }
      catch (Exception e) {
         throw new SchemaInvalidException(e);
      }
   }

   public Artifact createInstance() {
      StructuredArtifact instance = new StructuredArtifact(rootNode, getSchema().getChild(rootNode));
      prepareInstance(instance);
      return instance;
   }

   public void prepareInstance(Artifact object) {
      object.setHome(this);
      StructuredArtifact xmlObject = (StructuredArtifact) object;
      xmlObject.getBaseElement().setName(rootNode);
   }

   public Artifact createSample() {
      return createInstance();
   }

   public Collection findByOwner(Agent owner) throws FinderException {
      // really just list all here for now...
      String[] files = homeDirectory.list();

      List returnedList = new ArrayList();

      for (int i = 0; i < files.length; i++) {
         try {
            returnedList.add(load(files[i]));
         }
         catch (PersistenceException e) {
            throw new FinderException();
         }
      }

      return returnedList;
   }

   public boolean isInstance(Artifact testObject) {
      return (testObject instanceof StructuredArtifact);
   }

   public void refresh() {
      schema = null;
      getSchema();
   }

   public String getExternalUri(Id artifactId, String name) {
      //http://johnellis.rsmart.com:8080/osp/member/viewNode.osp?pid=1107451588272-643&nodeId=48D2AFE5A98453AD673579E14405607C
      return "viewNode.osp?pid=" + ToolManager.getCurrentPlacement().getId() +
            "&nodeId=" + artifactId.getValue();
   }

   public InputStream getStream(Id artifactId) {
      // todo ... implement this
      return null;
   }

   public boolean isSystemOnly() {
      return false;
   }

   public Class getInterface() {
      return StructuredArtifactHomeInterface.class;
   }

   public String getRootNode() {
      return rootNode;
   }

   public void setRootNode(String rootNode) {
      this.rootNode = rootNode;
   }

   public Date getModified() {
      return schemaDate;
   }

   public void setModified(Date schemaDate) {
      this.schemaDate = schemaDate;
   }

   public String getSchemaFileName() {
      return schemaFileName;
   }

   public void setSchemaFileName(String schemaFileName) {
      this.schemaFileName = schemaFileName;
   }

   /**
    * Invoked by a BeanFactory after it has set all bean properties supplied
    * (and satisfied BeanFactoryAware and ApplicationContextAware).
    * <p>This method allows the bean instance to perform initialization only
    * possible when all bean properties have been set and to throw an
    * exception in the event of misconfiguration.
    *
    * @throws SchemaInvalidException in the event of misconfiguration (such
    *                   as failure to set an essential property) or if initialization fails.
    */
   public void afterPropertiesSet() throws SchemaInvalidException {
      homeDirectory = new File(pathToWebInf(), XML_HOME_PATH + File.separator + rootNode);

      if (!homeDirectory.exists()) {
         if (!homeDirectory.mkdirs()) {
            logger.warn("Couldn't create homeDirectory: " + homeDirectory.getPath());
         }
      }

      getSchema();
      getType().setId(getIdManager().getId(getTypeId()));
   }

   protected String pathToWebInf() {
      try {
         return resourceLoader.getResource("WEB-INF").getFile().getCanonicalPath();
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public String getTypeId() {
      return typeId;
   }

   public byte[] getBytes(StructuredArtifact artifact) {
      return new byte[0];
   }

   public void setTypeId(String typeId) {
      this.typeId = typeId;
   }

   public void setResourceLoader(ResourceLoader resourceLoader) {
      this.resourceLoader = resourceLoader;
   }

   public String getInstruction() {
      return instruction;
   }

   public void setInstruction(String instruction) {
      this.instruction = instruction;
   }

   public SchemaNode getRootSchema() {
      return getSchema().getChild(getRootNode());
   }

   public String getSiteId() {
      return null;
   }

   public Artifact cloneArtifact(Artifact copy, String newName) throws PersistenceException {
      return null;
   }

   public Element getArtifactAsXml(Artifact art) {
      return null;
   }
   
   public Element getArtifactAsXml(Artifact artifact, String container, String site, String context) {
	   return null;
   }
   
   public StructuredArtifactHomeInterface getParentHome() {
      return this;
   }
}
