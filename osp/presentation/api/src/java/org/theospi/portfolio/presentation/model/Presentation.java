/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/api/src/java/org/theospi/portfolio/presentation/model/Presentation.java $
* $Id:Presentation.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdImpl;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.exception.IdUnusedException;
import org.theospi.portfolio.shared.model.DateBean;
import org.theospi.portfolio.style.model.Style;

public class Presentation extends IdentifiableObject {
   private String name;
   private String description;
   private Agent owner;
   private PresentationTemplate template;
   private Set items = new HashSet();
   private Collection viewers = new HashSet();
   private Date expiresOn;
   private boolean isPublic;
   private boolean isCollab;
   private boolean isDefault;
   private Date created;
   private Date modified;
   private ElementBean properties;
   private String toolId;
   private DateBean expiresOnBean = new DateBean();
   private Map authz; 
   private String presentationType = TEMPLATE_TYPE;
   private String secretExportKey;
   private List pages;
   private String siteId;
   private boolean newObject = false;
   private boolean advancedNavigation = false;
   private Style style;
   private String styleName;
   private PresentationLayout layout;
   private String layoutName;
   private boolean allowComments = false;
   private Id propertyForm;
   private boolean preview = false;

   public final static String FREEFORM_TYPE = "osp.presentation.type.freeForm";
   public final static String TEMPLATE_TYPE = "osp.presentation.type.template";
   public static final Id FREEFORM_TEMPLATE_ID = new IdImpl("freeFormTemplate", null);
   private String externalUriForDownload;


   public String getToolId() {
      return toolId;
   }

   public void setToolId(String toolId) {
      this.toolId = toolId;
   }

   public Agent getOwner() {
      return owner;
   }

   public PresentationTemplate getTemplate() {
      return template;
   }

   public Collection getPresentationItems() {
      return getItems();
   }

   public void setOwner(Agent owner) {
      this.owner = owner;
   }

   public void setTemplate(PresentationTemplate template) {
      this.template = template;
   }

   public Set getItems() {
      if (items == null) {
         setItems(new HashSet());
      }
      return items;
   }

   public void setItems(Set items) {
      this.items = items;
   }

   public void setViewers(Collection viewers) {
      this.viewers = viewers;
   }

   public Collection getViewers() {
      return viewers;
   }


   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public Date getExpiresOn() {
      return expiresOn;
   }

   public void setExpiresOn(Date expiresOn) {
      this.expiresOnBean = new DateBean(expiresOn);
      this.expiresOn = expiresOn;
   }

   public boolean getIsPublic() {
      return isPublic;
   }

   public void setIsCollab(boolean isCollab) {
      this.isCollab = isCollab;
   }

   public boolean getIsCollab() {
      return this.isCollab;
   }

   public void setIsPublic(boolean aPublic) {
      isPublic = aPublic;
   }

   public boolean getIsDefault() {
      return isDefault;
   }

   public void setIsDefault(boolean aDefault) {
      isDefault = aDefault;
   }

   public Date getCreated() {
      return created;
   }

   public void setCreated(Date created) {
      this.created = created;
   }

   public Date getModified() {
      return modified;
   }

   public void setModified(Date modified) {
      this.modified = modified;
   }
   
   public ElementBean getProperties() {
      return properties;
   }
   
   public void setProperties(ElementBean properties) {
      this.properties = properties;
   }

   public DateBean getExpiresOnBean() {
      return expiresOnBean;
   }

   public void setExpiresOnBean(DateBean expiresOnBean) {
      this.expiresOnBean = expiresOnBean;
   }

   public Map getAuthz() {
      return authz;
   }

   public void setAuthz(Map authz) {
      this.authz = authz;
   }

   public boolean isExpired() {
      if (getExpiresOn() == null) {
         return false;
      }
      return getExpiresOn().getTime() < System.currentTimeMillis();
   }


   public String getExternalUri() {
      return getExternalUri(ServerConfigurationService.getServerUrl());
   }

   public String getPresentationType() {
      return presentationType;
   }
   
   public boolean getIsFreeFormType() {
      return (presentationType.equals(FREEFORM_TYPE));
   }

   public void setPresentationType(String presentationType) {
      this.presentationType = presentationType;
   }

   public String getSecretExportKey() {
      return secretExportKey;
   }

   public void setSecretExportKey(String secretExportKey) {
      this.secretExportKey = secretExportKey;
   }

   public List getPages() {
      return pages;
   }

   public void setPages(List pages) {
      this.pages = pages;
   }

   /**
    * @return Returns the worksite name
    */
   public String getWorksiteName() {
	   String worksiteName = "";
		
      try
      {
         worksiteName = SiteService.getSite(siteId).getTitle();
      }
      catch (IdUnusedException e)
      {
         // tbd
      }

      return worksiteName;
   }
	
   public String getSiteId() {
      return siteId;
   }

   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }

   public boolean isNewObject() {
      return newObject;
   }

   public void setNewObject(boolean newObject) {
      this.newObject = newObject;
   }

    public boolean isAdvancedNavigation() {
        return advancedNavigation;
    }

    public void setAdvancedNavigation(boolean advancedNavigation) {
        this.advancedNavigation = advancedNavigation;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }
    
    public PresentationLayout getLayout() {
        return layout;
    }

    public void setLayout(PresentationLayout layout) {
        this.layout = layout;
    }

    public String getStyleName() {
        if (getStyle() != null){
            return getStyle().getName();
        }
        return null;
    }
    
    public String getLayoutName() {
        if (getLayout() != null){
            return getLayout().getName();
        }
        return null;
    }

    public boolean isAllowComments() {
        return allowComments;
    }

    public void setAllowComments(boolean allowComments) {
        this.allowComments = allowComments;
    }

   /**
    * @return the propertyForm
    */
   public Id getPropertyForm() {
      return propertyForm;
   }

   /**
    * @param propertyForm the propertyForm to set
    */
   public void setPropertyForm(Id propertyForm) {
      this.propertyForm = propertyForm;
   }

   public void setIsPreview(boolean preview) {
      this.preview = preview;
   }
   
   public boolean isPreview() {
      return preview;
   }


   /**
    * allows setting a special internal url for portfolio download.  This solves problems
    * trying to spider the portfolio over https
    * @return
    */
   public String getExternalUri(String uri) {
      if (uri == null || uri.length() == 0) {
         uri = ServerConfigurationService.getServerUrl();
      }
      uri += "/osp-presentation-tool/viewPresentation.osp?panel=presentation&id=" + getId().getValue();
      uri += "&" + Tool.PLACEMENT_ID + "=" + getToolId();
      return uri;
   }
}
