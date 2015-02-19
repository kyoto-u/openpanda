/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/shared/mgt/impl/ContentResourceHome.java $
 * $Id: ContentResourceHome.java 120216 2013-02-18 19:44:04Z ottenhoff@longsight.com $
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.entity.api.EntityPropertyNotDefinedException;
import org.sakaiproject.entity.api.EntityPropertyTypeException;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.metaobj.shared.mgt.*;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactHomeInterface;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.ContentResourceArtifact;
import org.sakaiproject.metaobj.shared.model.FinderException;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.MimeType;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.shared.model.Type;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.time.api.Time;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Sep 14, 2005
 * Time: 10:08:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class ContentResourceHome implements ReadableObjectHome, PresentableObjectHome {

   private HomeFactory homeFactory;
   private IdManager idManager;

   public Type getType() {
      return new Type(getIdManager().getId("fileArtifact"), "Uploaded File");
   }

   public String getExternalType() {
      return "fileArtifact";
   }

   public Artifact load(Id id) throws PersistenceException {
      throw new UnsupportedOperationException("not implemented");
   }

   public Artifact createInstance() {
      throw new UnsupportedOperationException("not implemented");
   }

   public void prepareInstance(Artifact object) {
      throw new UnsupportedOperationException("not implemented");
   }

   public Artifact createSample() {
      throw new UnsupportedOperationException("not implemented");
   }

   public Collection findByOwner(Agent owner) throws FinderException {
      throw new UnsupportedOperationException("not implemented");
   }

   public boolean isInstance(Artifact testObject) {
      throw new UnsupportedOperationException("not implemented");
   }

   public void refresh() {
      throw new UnsupportedOperationException("not implemented");
   }

   public String getExternalUri(Id artifactId, String name) {
      throw new UnsupportedOperationException("not implemented");
   }

   public InputStream getStream(Id artifactId) {
      throw new UnsupportedOperationException("not implemented");
   }

   public boolean isSystemOnly() {
      throw new UnsupportedOperationException("not implemented");
   }

   public Class getInterface() {
      throw new UnsupportedOperationException("not implemented");
   }

   public Element getArtifactAsXml(Artifact art) {
      return getArtifactAsXml(art, null, null, null);
   }
   
   public Element getArtifactAsXml(Artifact art, String container, String site, String context) {
	   ContentResourceArtifact artifact = (ContentResourceArtifact) art;
	   ContentResource resource = null;
	   if (container != null) {
		   resource = new ContentEntityWrapper(artifact.getBase(), ContentEntityUtil.getInstance().buildRef(container, site, context, artifact.getBase().getReference()));
	   }
	   else {
		   resource = artifact.getBase();
	   }
	      Element root = new Element("artifact");

	      root.addContent(getMetadata(artifact));

	      String type = artifact.getBase().getProperties().getProperty(ResourceProperties.PROP_STRUCTOBJ_TYPE);

	      if (type == null) {
	         addFileContent(resource, root);
	      }
	      else {
	         addStructuredObjectContent(type, resource, root);
	      }

	      return root;
   }

   protected void addStructuredObjectContent(String type, ContentResource resource, Element root) {
      Element data = new Element("structuredData");
      Element baseElement = null;

      byte[] bytes = null;
      try {
         bytes = resource.getContent();
      }
      catch (ServerOverloadException e) {
         throw new RuntimeException(e);
      }
      SAXBuilder builder = new SAXBuilder();
      builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // SAK-23245
      Document doc = null;

      try {
         doc = builder.build(new ByteArrayInputStream(bytes));
      }
      catch (JDOMException e) {
         throw new RuntimeException(e);
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      }

      baseElement = (Element) doc.getRootElement().detach();

      data.addContent(baseElement);
      root.addContent(data);

      StructuredArtifactHomeInterface home = (StructuredArtifactHomeInterface) getHomeFactory().getHome(type);

      Element schemaData = new Element("schema");
      schemaData.addContent(createInstructions(home));
      schemaData.addContent(addSchemaInfo(home.getRootSchema()));
      root.addContent(schemaData);
   }

   protected Element createInstructions(StructuredArtifactHomeInterface home) {
      Element instructions = new Element("instructions");
      instructions.setContent(new CDATA(home.getInstruction()));
      return instructions;
   }

   protected Element addSchemaInfo(SchemaNode schema) {
      Element schemaElement = new Element("element");
      schemaElement.setAttribute("name", schema.getName());
      if (schema.getType() != null && schema.getType().getBaseType() != null) {
         schemaElement.setAttribute("type", schema.getType().getBaseType());
      }
      schemaElement.setAttribute("minOccurs", schema.getMinOccurs() + "");
      schemaElement.setAttribute("maxOccurs", schema.getMaxOccurs() + "");
      Element annotation = schema.getSchemaElement().getChild("annotation", schema.getSchemaElement().getNamespace());

      if (annotation != null) {
         schemaElement.addContent(annotation.detach());
      }

      Element simpleType = schema.getSchemaElement().getChild("simpleType", schema.getSchemaElement().getNamespace());

      if (simpleType != null) {
         schemaElement.addContent(simpleType.detach());
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

   protected void addFileContent(ContentResource resource, Element root) {
      Element fileData = new Element("fileArtifact");
      Element uri = new Element("uri");
      uri.addContent(resource.getUrl());
      fileData.addContent(uri);

      root.addContent(fileData);
   }

   protected Element getMetadata(ContentResourceArtifact art) {
      Element root = new Element("metaData");
      root.addContent(ContentHostingUtil.createNode("id", art.getId().getValue()));
      root.addContent(ContentHostingUtil.createNode("displayName", art.getDisplayName()));

      Element type = new Element("type");
      root.addContent(type);

      type.addContent(ContentHostingUtil.createNode("id", "file"));
      type.addContent(ContentHostingUtil.createNode("description", "file"));

      ContentResource contentResource = art.getBase();
      Element repositoryNode =
         ContentHostingUtil.createRepoNode(contentResource);
      root.addContent(repositoryNode);

      repositoryNode.addContent(ContentHostingUtil.createNode("size", "" +
            contentResource.getContentLength()));

      Element mimeType = new Element("mimeType");
      repositoryNode.addContent(mimeType);
      String mimeTypeString = contentResource.getContentType();
      MimeType mime = new MimeType(mimeTypeString);
      mimeType.addContent(ContentHostingUtil.createNode("primary", mime.getPrimaryType()));
      mimeType.addContent(ContentHostingUtil.createNode("sub", mime.getSubType()));

      return root;
   }

   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

}
