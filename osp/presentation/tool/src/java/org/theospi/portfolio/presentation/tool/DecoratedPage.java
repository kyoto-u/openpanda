/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/tool/DecoratedPage.java $
* $Id:DecoratedPage.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.presentation.tool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.apache.commons.lang.StringEscapeUtils;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.theospi.portfolio.presentation.PresentationLayoutHelper;
import org.theospi.portfolio.presentation.model.PresentationLayout;
import org.theospi.portfolio.presentation.model.PresentationPage;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.style.StyleHelper;
import org.theospi.portfolio.style.model.Style;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 1, 2006
 * Time: 7:32:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedPage implements Comparable {

   protected final transient Log logger = LogFactory.getLog(getClass());
   
   private FreeFormTool parent;
   private PresentationPage base;
   private RegionMap regionMap;
   private boolean selected;
   private DecoratedLayout selectedLayout = null;
   private String layoutName;
   private String lastModified;
   private String expandedInformationSection = "true";
   private boolean newPage = false;


   public DecoratedPage(PresentationPage base, FreeFormTool parent) {
      this.base = base;
      this.parent = parent;
      
      if(base.getPresentation().getLayout() != null && base.getLayout() == null){
    	  base.setLayout(base.getPresentation().getLayout());
      }
      
      initLayout();
   }

   protected void initLayout() {
      if (base.getLayout() != null) {
         setSelectedLayout(new DecoratedLayout(parent, base.getLayout()));
      }
   }

   public String getStyleName() {
      ToolSession session = SessionManager.getCurrentToolSession();
      if (session.getAttribute(StyleHelper.CURRENT_STYLE) != null) {
         Style style = (Style)session.getAttribute(StyleHelper.CURRENT_STYLE);
         base.setStyle(style);
      }
      else if (session.getAttribute(StyleHelper.UNSELECTED_STYLE) != null) {
         base.setStyle(null);
         session.removeAttribute(StyleHelper.UNSELECTED_STYLE);
         return "";
      }

      if (base.getStyle() != null)
         return base.getStyle().getName();
      return "";
   }

   public boolean isRenderLayoutName() {
      getLayoutName();
      return true;
   }

   public String getLayoutName() {
      ToolSession session = SessionManager.getCurrentToolSession();
      if (session.getAttribute(PresentationLayoutHelper.CURRENT_LAYOUT) != null) {
         PresentationLayout layout = (PresentationLayout)session.getAttribute(PresentationLayoutHelper.CURRENT_LAYOUT);
         //base.setLayout(layout);
         clearRegionsIfDirtyLayout(layout);
         setSelectedLayout(new DecoratedLayout(getParent(), layout));
         session.removeAttribute(PresentationLayoutHelper.CURRENT_LAYOUT);
      }
      else if (session.getAttribute(PresentationLayoutHelper.UNSELECTED_LAYOUT) != null) {
         //base.setLayout(null);
         clearRegions();
         setSelectedLayout(new DecoratedLayout(getParent(), null));
         session.removeAttribute(PresentationLayoutHelper.UNSELECTED_LAYOUT);
         setSelectedLayoutId(null);
         return null;
      }

      if (getSelectedLayout() != null && getSelectedLayout().getBase() != null)
         return getSelectedLayout().getBase().getName();
      //return layoutName;
      setSelectedLayoutId(null);
      return null;
   }
   
   protected boolean isLayoutDirty(PresentationLayout layout) {
      if (getSelectedLayout() != null && getSelectedLayout().getBase() != null)
         return !getSelectedLayout().getBase().equals(layout);
      
      return false;
   }
   
   protected void clearRegionsIfDirtyLayout(PresentationLayout layout) {
      if (isLayoutDirty(layout)) {
         clearRegions();
      }
   }
   
   protected void clearRegions() {
      getBase().getRegions().clear();
      regionMap = null;
   }

   public void setLayoutName(String name) {
      this.layoutName = name;
   }

   public String getSafeTitle() {
      return StringEscapeUtils.escapeHtml( base.getTitle() );
   }

   public PresentationPage getBase() {
      return base;
   }

   public void setBase(PresentationPage base) {
      this.base = base;
   }

   /**
    * Any time this is called the calling method MUST close the input stream!!
    * This has the potential of causing memory leaks if the calling method does not close the stream
    * @return InputStream
    */
   public InputStream getXmlFile() {
      InputStream  inputStream = null;
      if (getSelectedLayout().getBase() != null){
         Node node = getParent().getPresentationManager().getNode(
               getSelectedLayout().getBase().getXhtmlFileId(), getSelectedLayout().getBase());
         inputStream = node.getInputStream();
         
         // we want to read the entire file into memory so wo can close the inputStream
         //    and thus release the database connection / file connection
         
         ByteArrayOutputStream bytesOS = new ByteArrayOutputStream();
         int buffersize = 1024, s;
         byte[] buffer = new byte[buffersize];
         try {
            while((s = inputStream.read(buffer)) != -1) {
               bytesOS.write(buffer, 0, s);
            }
            inputStream.close();
            inputStream = new ByteArrayInputStream(bytesOS.toByteArray());
         } catch(IOException ioe) {
            logger.error(ioe);
            inputStream = null;
         }
      }

      return inputStream;
   }

   public boolean isXmlFileNotNull() {
      InputStream  inputStream = getXmlFile();
      boolean isNotNull = inputStream != null;
      if(isNotNull) {
         try {
            inputStream.close();
         } catch(IOException ioe) {
            logger.error("Failed to close: " + ioe);
         }
      }
      return isNotNull;
   }

   public String getXmlFileId() {
      return getSelectedLayout().getBase().getId().getValue() + getSelectedLayout().getBase().getModified().toString();
   }

   public RegionMap getRegionMap() {
      if (regionMap == null) {
         regionMap = new RegionMap(getBase());
      }
      return regionMap;
   }

   public void setRegionMap(RegionMap regionMap) {
      this.regionMap = regionMap;
   }

   public FreeFormTool getParent() {
      return parent;
   }

   public void setParent(FreeFormTool parent) {
      this.parent = parent;
   }

   public String processActionArrange() {
      getParent().setCurrentPage(this);
      initLayout();
      return "arrange";
   }

   public String processActionEdit() {
      getParent().setCurrentPage(this);
      initLayout();
      return "edit";
   }

   public String processActionConfirmDelete() {
      getParent().setCurrentPage(this);
      return "confirm";
   }

   public String processActionDelete() {
      getParent().deletePage(this);
      getParent().reorderPages();
      return "main";
   }

   public String processActionSelectStyle() {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();
      session.removeAttribute(StyleHelper.CURRENT_STYLE);
      session.removeAttribute(StyleHelper.CURRENT_STYLE_ID);

      session.setAttribute(StyleHelper.STYLE_SELECTABLE, "true");
      if (base.getStyle() != null)
         session.setAttribute(StyleHelper.CURRENT_STYLE_ID, base.getStyle().getId().getValue());

      try {
         context.redirect("osp.style.helper/listStyle");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
      return null;
   }

   public String processActionSelectLayout() {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();
      session.removeAttribute(PresentationLayoutHelper.CURRENT_LAYOUT);
      session.removeAttribute(PresentationLayoutHelper.CURRENT_LAYOUT_ID);

      session.setAttribute(PresentationLayoutHelper.LAYOUT_SELECTABLE, "true");
      if (getSelectedLayout() != null && getSelectedLayout().getBase() != null)
         session.setAttribute(PresentationLayoutHelper.CURRENT_LAYOUT_ID, getSelectedLayout().getBase().getId().getValue());

      try {
         context.redirect("osp.presLayout.helper/listLayout");
      }
      catch (IOException e) {
         throw new RuntimeException("Failed to redirect to helper", e);
      }
      return null;
   }

   public boolean isSelected() {
      return selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   public DecoratedLayout getSelectedLayout() {
      return selectedLayout;
   }

   public void setSelectedLayout(DecoratedLayout selectedLayout) {
      this.selectedLayout = selectedLayout;
   }

   /**
    * Sets the layout to null as well
    * @param layoutId
    */
   public void setSelectedLayoutId(String layoutId) {

      Id id = getParent().getIdManager().getId(layoutId);
      PresentationLayout layout = getParent().getPresentationManager().getPresentationLayout(id);
      setSelectedLayout(new DecoratedLayout(getParent(), layout));
   }

   public String getSelectedLayoutId() {
      if (getSelectedLayout() != null && getSelectedLayout().getBase() != null) {
         return getSelectedLayout().getBase().getId().getValue();
      }
      return null;
   }

   public boolean islayoutSelected() {
      return (getSelectedLayout() != null && getSelectedLayout().getBase() != null);
   }

   public boolean isLayoutPreviewImage() {
       if (islayoutSelected() && (getSelectedLayout().getPreviewImage() != null)) {
           return true;
       }
       return false;
   }
   public int compareTo(Object o) {
      DecoratedPage other = (DecoratedPage) o;
      return getBase().compareTo(other.getBase());
   }

   public String pagePropertiesSaved() {
      getBase().setLayout(getSelectedLayout().getBase());
      //Make sure the page list is being refreshed after a save.
      getParent().setPageList(null);
      return "main";
   }

   public boolean getHasLayout() {
      return getBase().getLayout() != null;
   }

   public String moveUp() {
      if (getBase().getSequence() != 0) {
         Collections.swap(getParent().getPresentation().getPages(),
               getBase().getSequence(), getBase().getSequence() - 1);
         getParent().reorderPages();
      }
      return null;
   }

   public String moveDown() {
      if (getBase().getSequence() < getParent().getPresentation().getPages().size() - 1) {
         Collections.swap(getParent().getPresentation().getPages(),
               getBase().getSequence(), getBase().getSequence() + 1);
         getParent().reorderPages();
      }
      return null;
   }

   public boolean isLast() {
      return getBase().getSequence() >= getParent().getPresentation().getPages().size() - 1;
   }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }


     public String getExpandedInformationSection() {
       if (this.getBase().getTitle() == null || this.getBase().getTitle().equals("")){
           return "true";
       }
         return "false";
    }
     
   public boolean isNewPage() {
      return newPage;
   }

   public void setNewPage(boolean newPage) {
      this.newPage = newPage;
   }


}
