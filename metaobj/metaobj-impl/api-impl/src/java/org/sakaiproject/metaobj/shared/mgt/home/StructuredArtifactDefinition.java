/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/shared/mgt/home/StructuredArtifactDefinition.java $
 * $Id: StructuredArtifactDefinition.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.mgt.PresentableObjectHome;
import org.sakaiproject.metaobj.shared.model.*;
import org.sakaiproject.metaobj.utils.xml.SchemaFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.utils.xml.SchemaInvalidException;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Date;


/**
 * @author chmaurer, jbush
 */
public class StructuredArtifactDefinition extends StructuredArtifactHome implements Serializable {
   public static final int STATE_UNPUBLISHED = 0;
   public static final int STATE_WAITING_APPROVAL = 1;
   public static final int STATE_PUBLISHED = 2;

   private Id id;
   private String documentRoot;
   private Agent owner;
   private Date created = new Date();
   private Date modified = new Date();

   private transient String decoratedDescription;

   /**
    * system only SAD's are not available to users to populate via a web form, they are for internal system use only
    */
   private boolean systemOnly = false;
   private String description;
   private boolean modifiable = true;
   private Id xslConversionFileId;
   private String schemaFileName;
   private String xslFileName;
   private SchemaNode schema;
   private Id alternateCreateXslt;
   private String alternateCreateXsltName;
   private Id alternateViewXslt;
   private String alternateViewXsltName;

   /**
    * should be one of the following states
    * <p/>
    * unpublished -> active
    */
   private int siteState;

   /**
    * should be one of the following states
    * <p/>
    * unpublished -> waiting for approval-> active
    */
   private int globalState;

   /**
    * used during edit process to store whether or not xsl transform is necessary
    */
   private boolean requiresXslFile = false;

   /**
    * file id of schema file - used when add/editing artifact homes
    */
   private Id schemaFile;

   /**
    * used in publishing web form to set action (publish to site, global, approve, etc)
    */
   private String action;

   public StructuredArtifactDefinition() {
      
   }

   public StructuredArtifactDefinition(StructuredArtifactDefinitionBean bean) {
      this.action = bean.getAction();
      this.created = bean.getCreated();
      this.description = bean.getDescription();
      this.documentRoot = bean.getDocumentRoot();
      this.globalState = bean.getGlobalState();
      this.id = bean.getId();
      this.modifiable = bean.isModifiable();
      this.modified = bean.getModified();
      this.owner = bean.getOwner();
      this.requiresXslFile = bean.getRequiresXslFile();

      if (bean.getSchema() != null) {
         SchemaFactory schemaFactory = SchemaFactory.getInstance();
         ByteArrayInputStream in = new ByteArrayInputStream(bean.getSchema());

         try {
            this.schema = schemaFactory.getSchema(in);
         }
         catch ( Exception e ) {
            throw new SchemaInvalidException(bean.getDescription(), e);
         }
      }
      else {
         this.schema = null;
      }

      this.schemaFile = bean.getSchemaFile();
      this.schemaFileName = bean.getSchemaFileName();
      this.siteState = bean.getSiteState();
      this.systemOnly = bean.isSystemOnly();
      this.xslConversionFileId = bean.getXslConversionFileId();
      this.xslFileName = bean.getXslFileName();
      this.setSiteId(bean.getSiteId());
      this.setExternalType(bean.getExternalType());
      this.setInstruction(bean.getInstruction());
      this.decoratedDescription = bean.getDecoratedDescription();
      this.alternateCreateXslt = bean.getAlternateCreateXslt();
      this.alternateViewXslt = bean.getAlternateViewXslt();
      this.alternateCreateXsltName = bean.getAlternateCreateXsltName();
      this.alternateViewXsltName = bean.getAlternateViewXsltName();
      
      if (getId() != null)
         this.setTypeId(getId().getValue());
   }

   public boolean equals(Object o) {
      if (!(o instanceof StructuredArtifactDefinition)) {
         return false;
      }
      StructuredArtifactDefinition in = (StructuredArtifactDefinition) o;

      if (getId() == null && in.getId() == null) {
         return true;
      }
      if (getId() == null && in.getId() != null) {
         return false;
      }

      return getId().equals(in.getId());
   }

   public Artifact createInstance() {
      StructuredArtifact instance = new StructuredArtifact(documentRoot, getSchema().getChild(documentRoot));
      prepareInstance(instance);
      return instance;
   }

   public void prepareInstance(Artifact object) {
      object.setHome(this);
      StructuredArtifact xmlObject = (StructuredArtifact) object;
      xmlObject.getBaseElement().setName(documentRoot);
   }

   public String getRootNode() {
      return documentRoot;
   }

   /**
    * @return Returns the created.
    */
   public Date getCreated() {
      return created;
   }

   /**
    * @param created The created to set.
    */
   public void setCreated(Date created) {
      this.created = created;
   }

   /**
    * @return Returns the documentRoot.
    */
   public String getDocumentRoot() {
      return documentRoot;
   }

   /**
    * @param documentRoot The documentRoot to set.
    */
   public void setDocumentRoot(String documentRoot) {
      this.documentRoot = documentRoot;
   }

   /**
    * @return Returns the id.
    */
   public Id getId() {
      return id;
   }

   /**
    * @param id The id to set.
    */
   public void setId(Id id) {
      this.id = id;
   }

   public Date getModified() {
      return modified;
   }

   public void setModified(Date modified) {
      this.modified = modified;
   }

   /**
    * @return Returns the owner.
    */
   public Agent getOwner() {
      return owner;
   }

   /**
    * @param owner The owner to set.
    */
   public void setOwner(Agent owner) {
      this.owner = owner;
   }

   public String getExternalType() {
      return super.getExternalType();
   }

   public void setExternalType(String newType) {
      // stub this out... this property should be read only      
   }

   /**
    * @return Returns the schema.
    */
   public SchemaNode getSchema() {
      return schema;
/*      if (getSchemaFile() == null) {
         return null;
      }
      RepositoryNode rNode = (RepositoryNode) getRepositoryManager().getNode(getSchemaFile());
      if (rNode == null) {
         return null;
      }
      SchemaFactory schemaFactory = SchemaFactory.getInstance();
      return schemaFactory.getSchema(rNode.getStream(RepositoryNode.FILE_TYPE));
*/
   }

   public void setSchema(SchemaNode schema) {
      this.schema = schema;
   }

   /**  todo implement conversion file stuff
    public RepositoryNode getXslConversionFileNode() {
    return (RepositoryNode) getRepositoryManager().getNode(getXslConversionFileId());
    }

    public InputStream getXslConversionFileStream(){
    return getXslConversionFileNode().getStream();
    }
    **/

   /**
    * @return Returns the type.
    */
   public Type getType() {
      Type type = new Type();
      if (getId() != null) {
         type.setId(getId());
      }
      if (getDescription() != null) {
         type.setDescription(getDecoratedDescription());
      }
      type.setSystemOnly(isSystemOnly());
      return type;
   }

   /**
    * @return Returns the systemOnly.
    */
   public boolean isSystemOnly() {
      return systemOnly;
   }

   /**
    * @param systemOnly The systemOnly to set.
    */
   public void setSystemOnly(boolean systemOnly) {
      this.systemOnly = systemOnly;
   }

   /**
    * @return Returns the idManager.
    */
   public IdManager getIdManager() {
      return (IdManager) ComponentManager.getInstance().get("idManager");
   }

   public PresentableObjectHome getRepositoryHelper() {
      return (PresentableObjectHome) ComponentManager.getInstance().get("repositoryHelper");
   }

   /**
    * public StreamStore getStreamStore() {
    * return (StreamStore)BeanFactory.getInstance().getBean(
    * StreamStore.class.getName(), StreamStore.class);
    * }
    * <p/>
    * public NodeMetadataService getNodeMetadataService() {
    * return (NodeMetadataService)BeanFactory.getInstance().getBean(
    * NodeMetadataService.class.getName(), NodeMetadataService.class);
    * }
    */

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public boolean isModifiable() {
      return modifiable;
   }

   public boolean getRequiresXslFile() {
      return requiresXslFile;
   }

   public void setRequiresXslFile(boolean requiresXslFile) {
      this.requiresXslFile = requiresXslFile;
   }

   public Id getSchemaFile() {
      return schemaFile;
   }

   public void setSchemaFile(Id schemaFile) {
      this.schemaFile = schemaFile;
   }

   public Id getXslConversionFileId() {
      return xslConversionFileId;
   }

   public void setXslConversionFileId(Id xslConversionFileId) {
      this.xslConversionFileId = xslConversionFileId;
   }

   public String getSchemaFileName() {
      return schemaFileName;
   }

   public void setSchemaFileName(String schemaFileName) {
      this.schemaFileName = schemaFileName;
   }

   public String getXslFileName() {
      return xslFileName;
   }

   public void setXslFileName(String xslFileName) {
      this.xslFileName = xslFileName;
   }

   public int getSiteState() {
      return siteState;
   }

   public void setSiteState(int siteState) {
      this.siteState = siteState;
   }

   public int getGlobalState() {
      return globalState;
   }

   public void setGlobalState(int globalState) {
      this.globalState = globalState;
   }

   /**
    * This method doesn't do any authz, it simply checks the state
    *
    * @return true, if sad can be published to site.
    */
   public boolean getCanPublish() {
      return (siteState == STATE_UNPUBLISHED && globalState != STATE_PUBLISHED);
   }

   public boolean getCanGlobalPublish() {
      return (globalState == STATE_UNPUBLISHED);
   }

   /**
    * This method doesn't do any authz, it simply checks the state
    *
    * @return true, if sad can be suggested for global publish
    */
   public boolean getCanSuggestGlobalPublish() {
      return (globalState == STATE_UNPUBLISHED);
   }

   /**
    * This method doesn't do any authz, it simply checks the state
    *
    * @return true, if sad can be published globally
    */
   public boolean getCanApproveGlobalPublish() {
      return (globalState == STATE_WAITING_APPROVAL);
   }

   public String getAction() {
      return action;
   }

   public void setAction(String action) {
      this.action = action;
   }

   public String getDecoratedDescription() {
      return decoratedDescription;
   }

   public void setDecoratedDescription(String decoratedDescription) {
      this.decoratedDescription = decoratedDescription;
   }

   public Id getAlternateCreateXslt() {
      return alternateCreateXslt;
   }

   public void setAlternateCreateXslt(Id alternateCreateXslt) {
      this.alternateCreateXslt = alternateCreateXslt;
   }

   public String getAlternateCreateXsltName() {
      return alternateCreateXsltName;
   }

   public void setAlternateCreateXsltName(String alternateCreateXsltName) {
      this.alternateCreateXsltName = alternateCreateXsltName;
   }

   public Id getAlternateViewXslt() {
      return alternateViewXslt;
   }

   public void setAlternateViewXslt(Id alternateViewXslt) {
      this.alternateViewXslt = alternateViewXslt;
   }

   public String getAlternateViewXsltName() {
      return alternateViewXsltName;
   }

   public void setAlternateViewXsltName(String alternateViewXsltName) {
      this.alternateViewXsltName = alternateViewXsltName;
   }

}
