/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/EditedArtifactStorage.java $
 * $Id: EditedArtifactStorage.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.shared.control;

import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

public class EditedArtifactStorage {
   protected final Log logger = LogFactory.getLog(getClass());

   public static final String STORED_ARTIFACT_FLAG =
         "org_theospi_storedArtifactCall";
   public static final String EDITED_ARTIFACT_STORAGE_SESSION_KEY =
         "org_theospi_editedArtifact";

   private SchemaNode rootSchemaNode = null;
   private SchemaNode currentSchemaNode = null;
   private StructuredArtifact rootArtifact = null;
   private ElementBean currentElement = null;
   private String currentPath = null;
   private ReadableObjectHome home;
   private Stack elementBeanStack = new Stack();
   private Stack pathStack = new Stack();
   private Element oldParentElement = null;

   public EditedArtifactStorage(SchemaNode rootSchemaNode, StructuredArtifact rootArtifact) {
      this.rootSchemaNode = rootSchemaNode;
      this.currentSchemaNode = rootSchemaNode;
      this.rootArtifact = rootArtifact;
      this.currentElement = rootArtifact;
      elementBeanStack.push(rootArtifact);
      pathStack.push("");
      setHome(rootArtifact.getHome());
   }

   public SchemaNode getRootSchemaNode() {
      return rootSchemaNode;
   }

   protected void setRootSchemaNode(SchemaNode rootSchemaNode) {
      this.rootSchemaNode = rootSchemaNode;
   }

   public SchemaNode getCurrentSchemaNode() {
      return currentSchemaNode;
   }

   protected void setCurrentSchemaNode(SchemaNode currentSchemaNode) {
      this.currentSchemaNode = currentSchemaNode;
   }

   public StructuredArtifact getRootArtifact() {
      return rootArtifact;
   }

   protected void setRootArtifact(StructuredArtifact rootArtifact) {
      this.rootArtifact = rootArtifact;
   }

   public ElementBean getCurrentElement() {
      return currentElement;
   }

   protected void setCurrentElement(ElementBean currentElement) {
      this.currentElement = currentElement;
   }

   public String getCurrentPath() {
      return currentPath;
   }

   protected void setCurrentPath(String currentPath) {
      this.currentPath = currentPath;
   }

   public void pushCurrentElement(ElementBean newBean) {
      oldParentElement = (Element) rootArtifact.getBaseElement().clone();
      setCurrentElement(newBean);
      elementBeanStack.push(newBean);
      setCurrentSchemaNode(newBean.getCurrentSchema());
   }

   public void pushCurrentPath(String s) {
      if (getCurrentPath() != null && getCurrentPath().length() > 0) {
         s = getCurrentPath() + "/" + s;
      }
      setCurrentPath(s);
      pathStack.push(s);
   }

   public ReadableObjectHome getHome() {
      return home;
   }

   protected void setHome(ReadableObjectHome home) {
      this.home = home;
   }

   public void popCurrentElement() {
      popCurrentElement(false);
   }

   public void popCurrentElement(boolean cancel) {
      if (cancel) {
         rootArtifact.setBaseElement(oldParentElement);
      }
      elementBeanStack.pop();
      setCurrentElement((ElementBean) elementBeanStack.peek());
      setCurrentSchemaNode(getCurrentElement().getCurrentSchema());
   }

   public void popCurrentPath() {
      pathStack.pop();
      setCurrentPath((String) pathStack.peek());
   }
}
