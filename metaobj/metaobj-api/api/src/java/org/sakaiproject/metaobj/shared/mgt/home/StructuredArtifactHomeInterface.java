/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/mgt/home/StructuredArtifactHomeInterface.java $
 * $Id: StructuredArtifactHomeInterface.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

import java.util.Date;

import org.sakaiproject.metaobj.shared.mgt.CloneableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.PresentableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.WritableObjectHome;
import org.sakaiproject.metaobj.shared.model.Type;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.content.api.ContentResource;

/**
 * marker for structured artifact home
 */
public interface StructuredArtifactHomeInterface extends WritableObjectHome, CloneableObjectHome, PresentableObjectHome {

   public String getSiteId();

   public SchemaNode getRootSchema();

   public Type getType();

   public String getInstruction();

   public Date getModified();

   public String getRootNode();

   public SchemaNode getSchema();

   public StructuredArtifact load(ContentResource resource);
   
   public StructuredArtifact load(ContentResource resource, Id artifactId);

   public String getTypeId();

   public byte[] getBytes(StructuredArtifact artifact);
   
   public StructuredArtifactHomeInterface getParentHome();
}
