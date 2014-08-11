package org.sakaiproject.metaobj.shared.mgt.home;

import org.sakaiproject.metaobj.shared.mgt.WritableObjectHome;
import org.sakaiproject.metaobj.shared.model.*;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.content.api.ResourceToolActionPipe;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.entity.api.ResourceProperties;
import org.jdom.Element;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Jan 30, 2007
 * Time: 9:31:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class ResourceHelperArtifactHome implements StructuredArtifactHomeInterface {

   private StructuredArtifactHomeInterface parentHome;
   private ResourceToolActionPipe pipe;

   public ResourceHelperArtifactHome(StructuredArtifactHomeInterface parentHome,
                                     ResourceToolActionPipe pipe) {
      this.parentHome = parentHome;
      this.pipe = pipe;
      // assume the user is gonna cancel unless they store
      pipe.setActionCanceled(true);
      pipe.setActionCompleted(false);
   }

   public Artifact store(Artifact object) throws PersistenceException {
      pipe.setRevisedContent(getParentHome().getBytes((StructuredArtifact) object));
      pipe.setRevisedMimeType("application/x-osp");
      pipe.setRevisedResourceProperty(ResourceProperties.PROP_STRUCTOBJ_TYPE,
         getParentHome().getTypeId());
      pipe.setRevisedResourceProperty(ContentHostingService.PROP_ALTERNATE_REFERENCE,
         org.sakaiproject.metaobj.shared.mgt.MetaobjEntityManager.METAOBJ_ENTITY_PREFIX);
      pipe.setActionCompleted(true);
      pipe.setActionCanceled(false);
      return object;
   }

   public Artifact load(Id id) throws PersistenceException {
      return load((ContentResource) pipe.getContentEntity());
   }

   public void remove(Artifact object) throws PersistenceException {
      getParentHome().remove(object);
   }

   public Artifact store(String displayName, String contentType, Type type, InputStream in) throws PersistenceException {
      return getParentHome().store(displayName, contentType, type, in);
   }

   public Artifact update(Artifact object, InputStream in) throws PersistenceException {
      return getParentHome().update(object, in);
   }

   public Type getType() {
      return getParentHome().getType();
   }

   public String getExternalType() {
      return getParentHome().getExternalType();
   }

   public Artifact createInstance() {
      return getParentHome().createInstance();
   }

   public void prepareInstance(Artifact object) {
      getParentHome().prepareInstance(object);
   }

   public Artifact createSample() {
      return getParentHome().createSample();
   }

   public Collection findByOwner(Agent owner) throws FinderException {
      return getParentHome().findByOwner(owner);
   }

   public boolean isInstance(Artifact testObject) {
      return getParentHome().isInstance(testObject);
   }

   public void refresh() {
      getParentHome().refresh();
   }

   public String getExternalUri(Id artifactId, String name) {
      return getParentHome().getExternalUri(artifactId, name);
   }

   public InputStream getStream(Id artifactId) {
      return getParentHome().getStream(artifactId);
   }

   public boolean isSystemOnly() {
      return getParentHome().isSystemOnly();
   }

   public Class getInterface() {
      return getParentHome().getInterface();
   }

   public String getSiteId() {
      return getParentHome().getSiteId();
   }

   public SchemaNode getRootSchema() {
      return getParentHome().getRootSchema();
   }

   public String getInstruction() {
      return getParentHome().getInstruction();
   }

   public Date getModified() {
      return getParentHome().getModified();
   }

   public String getRootNode() {
      return getParentHome().getRootNode();
   }

   public SchemaNode getSchema() {
      return getParentHome().getSchema();
   }
   
   public StructuredArtifact load(ContentResource resource, Id artifactId) {
	   return getParentHome().load(resource, artifactId);
   }

   public StructuredArtifact load(ContentResource resource) {
      return getParentHome().load(resource);
   }

   public String getTypeId() {
      return getParentHome().getTypeId();
   }

   public byte[] getBytes(StructuredArtifact artifact) {
      return getParentHome().getBytes(artifact);
   }

   public Artifact cloneArtifact(Artifact copy, String newName) throws PersistenceException {
      return getParentHome().cloneArtifact(copy, newName);
   }

   public Element getArtifactAsXml(Artifact art) {
      return getParentHome().getArtifactAsXml(art);
   }
   
   public Element getArtifactAsXml(Artifact artifact, String container, String site, String context) {
	   return getParentHome().getArtifactAsXml(artifact, container, site, context);
   }
   
   public StructuredArtifactHomeInterface getParentHome() {
      return parentHome;
   }

   public void setParentHome(StructuredArtifactHomeInterface parentHome) {
      this.parentHome = parentHome;
   }
}
