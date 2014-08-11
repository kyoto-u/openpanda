/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/api/src/java/org/theospi/portfolio/presentation/model/PresentationItemDefinition.java $
* $Id:PresentationItemDefinition.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
package org.theospi.portfolio.presentation.model;

import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.sakaiproject.metaobj.shared.model.MimeType;
import org.theospi.portfolio.shared.model.ItemDefinitionMimeType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class PresentationItemDefinition extends IdentifiableObject implements Serializable {
   private PresentationTemplate presentationTemplate;
   /**
    * the artifact type
    */
   private String type;
   private String name;
   private String title;
   private String description;
   private boolean allowMultiple;
   private Set mimeTypes = new HashSet();
   private String externalType = null;
   private int sequence = -1;
   private transient String action;
   private transient Integer newSequence = null;

   static final long serialVersionUID = -6220810277272518156l;

   public boolean getHasMimeTypes() {
      return (type != null && type.equals("fileArtifact"));
   }
	
   public Boolean getIsFormType() {
      return Boolean.valueOf(type != null 
                         && ! type.equals("fileArtifact")
                         && ! type.equals("completedWizard")
                         && ! type.equals("matrix"));
   }

   public PresentationTemplate getPresentationTemplate() {
      return presentationTemplate;
   }

   public String getAction() {
      return action;
   }

   public void setAction(String action) {
      this.action = action;
   }

   public String getType() {
      return type;
   }

   public String getName() {
      return name;
   }

   public String getTitle() {
      return title;
   }

   public String getDescription() {
      return description;
   }

   public boolean getAllowMultiple() {
      return isAllowMultiple();
   }

   public void setType(String type) {
      this.type = type;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public boolean isAllowMultiple() {
      return allowMultiple;
   }

   public void setAllowMultiple(boolean allowMultiple) {
      this.allowMultiple = allowMultiple;
   }

   public void setPresentationTemplate(PresentationTemplate presentationTemplate) {
      this.presentationTemplate = presentationTemplate;
   }

   public Set getMimeTypes() {
      return mimeTypes;
   }

   public void setMimeTypes(Set mimeTypes) {
      this.mimeTypes = mimeTypes;
   }

   public String getExternalType() {
      return externalType;
   }

   public void setExternalType(String externalType) {
      this.externalType = externalType;
   }

   public int hashCode() {
      if (getId() != null){
         return getId().hashCode();
      }
      return (type != null && name != null ) ?
            (type + name).hashCode() : 0;
   }

   public boolean allowsMimeType(MimeType mimeType) {
      if (!getHasMimeTypes()) {
         return true;
      }

      if (getMimeTypes() == null || getMimeTypes().isEmpty()) {
         return true;
      }

      for (Iterator i = getMimeTypes().iterator(); i.hasNext();) {
         ItemDefinitionMimeType currentType = (ItemDefinitionMimeType) i.next();

         if (currentType.getSecondary() != null) {
            if (mimeType.getSubType().equals(currentType.getSecondary()) &&
               mimeType.getPrimaryType().equals(currentType.getPrimary())) {
               return true;
            }
         } else {
            if (mimeType.getPrimaryType().equals(currentType.getPrimary())) {
               return true;
            }
         }
      }

      return false;
   }

   public int getSequence() {
      return sequence;
   }

   public void setSequence(int sequence) {
      this.sequence = sequence;
      newSequence = null;
   }

   public int getNewSequence() {
      if (newSequence == null) {
         return sequence;
      }
      return newSequence.intValue();
   }

   public void setNewSequence(int newSequence) {
      this.newSequence = Integer.valueOf(newSequence);
   }
}
