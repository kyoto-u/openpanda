/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/tool/FreeFormTool.java $
* $Id:FreeFormTool.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.FilePickerHelper;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.theospi.jsf.intf.XmlTagFactory;
import org.theospi.portfolio.presentation.PresentationLayoutHelper;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.intf.FreeFormHelper;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationItem;
import org.theospi.portfolio.presentation.model.PresentationLayout;
import org.theospi.portfolio.presentation.model.PresentationPage;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.tool.HelperToolBase;
import org.theospi.portfolio.style.StyleHelper;
import org.theospi.portfolio.style.model.Style;


/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 31, 2005
 * Time: 9:23:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class FreeFormTool extends HelperToolBase {

   protected static final Log logger = LogFactory.getLog(FreeFormTool.class);
   private PresentationManager presentationManager;
   private IdManager idManager;
   private XmlTagFactory factory;
   private ContentHostingService contentHosting;

   private Presentation presentation = null;

   private DecoratedPage currentPage = null;
   private List pageList;
   private List attachableItems = null;
   private List listableItems = null;
   private List layouts = null;
   private String nextPageId = null;
   private int step = 1;
   private int pageCount;
	
   protected boolean validPages() {
      if (getPageList() == null || getPageList().size() == 0) {
         FacesContext.getCurrentInstance().addMessage(null,
            getFacesMessageFromBundle("one_page_required", new Object[]{}));
         return false;
      }

      return true;
   }

   public String processActionSave() {
      if (!validPages()) {
         return null;
      }
      
      Presentation presentation = getPresentation();
      getPresentationManager().storePresentation(presentation, false, true);
      return "main";
   }

   public String processActionSummary() {
      setAttribute(FreeFormHelper.FREE_FORM_PREFIX + "presentation", getPresentation());
      setRedirectCaller("editPresentation.osp");
      return returnToCaller();
   }
   
   public String processActionShare() {
      setAttribute(FreeFormHelper.FREE_FORM_PREFIX + "presentation", getPresentation());
      setRedirectCaller("sharePresentation.osp");
      return returnToCaller();
   }
   
   public String processActionReturn() {
      setRedirectCaller("listPresentation.osp");
      return returnToCaller();
   }
   
   /** FreeFormTool is currently set up as a helper for historic reasons.
    ** It should be moved and configured as a regular controller to allow proper
    ** navigation between SharePresentationController and EditPresentationController.
    ** For now, this method resets the HELPER_DONE_URL to the navigation target.
    **/
   private String setRedirectCaller( String target ) {
      Tool tool = ToolManager.getCurrentTool();
      ToolSession session = SessionManager.getCurrentToolSession();
      String url = (String) session.getAttribute(tool.getId() + Tool.HELPER_DONE_URL);
      url = url.substring( 0, url.lastIndexOf('/')+1 );
      url = url + target;
      session.setAttribute(tool.getId() + Tool.HELPER_DONE_URL, url);
      return url;
   }
   
   public String processActionCancel() {
      initValues();
      Presentation presentation = getPresentation();
      List pages = getPresentationManager().getPresentationPagesByPresentation(presentation.getId());
      presentation.setPages(pages);
      return "main";
   }

   public String processActionCancelPage() {
       if (getCurrentPage().isNewPage()) {
            deletePage(getCurrentPage());
       }
       cancelBoundValues();
       return "main";
   }

   protected void initValues() {
      currentPage = null;
      pageList = null;
      attachableItems = null;
      listableItems = null;
   }

   public PresentationManager getPresentationManager() {
      return presentationManager;
   }

   public void setPresentationManager(PresentationManager presentationManager) {
      this.presentationManager = presentationManager;
   }

   public Presentation getPresentation() {
      Presentation sessionPresentation =
            (Presentation) getAttribute(FreeFormHelper.FREE_FORM_PREFIX + "presentation");
      if (sessionPresentation != null) {
         removeAttribute(FreeFormHelper.FREE_FORM_PREFIX + "presentation");
         presentation = sessionPresentation;
         List pages = presentation.getPages();
         if (pages == null) {
            pages = getPresentationManager().getPresentationPagesByPresentation(presentation.getId());
            presentation.setPages(pages);
         }
         initValues();
      }
      return presentation;
   }

   public DecoratedPage getCurrentPage() {
       return currentPage;
   }

   public void setCurrentPage(DecoratedPage currentPage) {
      nextPageId = null;
      this.currentPage = currentPage;
   }

   public void processPageSelectChange(ValueChangeEvent event) {

   }

   public List getPageList() {
      Presentation presentation = getPresentation();
      if (pageList == null) {
         List pages = presentation.getPages();

         pageList = new ArrayList();
         for (Iterator i=pages.iterator();i.hasNext();) {
            pageList.add(new DecoratedPage((PresentationPage) i.next(), this));
         }
      }
      return pageList;
   }

   public void setPageList(List pageList) {
      this.pageList = pageList;
   }

   public XmlTagFactory getFactory() {
      return factory;
   }

   public void setFactory(XmlTagFactory factory) {
      this.factory = factory;
   }

   public void processActionManageItems(ActionEvent event) {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      ToolSession session = SessionManager.getCurrentToolSession();
      session.setAttribute(FilePickerHelper.FILE_PICKER_ATTACH_LINKS, Boolean.valueOf(true).toString());

      List attachments = new ArrayList(getPresentation().getItems());
      List attachmentRefs = EntityManager.newReferenceList();

      for (Iterator i=attachments.iterator();i.hasNext();) {
         PresentationItem attachment = (PresentationItem)i.next();
         Node item = getPresentationManager().getNode(attachment.getArtifactId());
         attachmentRefs.add(EntityManager.newReference(item.getResource().getReference()));
      }

      session.setAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS, attachmentRefs);
      
      //Start in user's resources area
      //osp-ui-05
      String siteId = SiteService.getUserSiteId(SessionManager.getCurrentSessionUserId());
      String collectionId = getContentHosting().getSiteCollection(siteId);
      session.setAttribute(FilePickerHelper.DEFAULT_COLLECTION_ID, collectionId);

      try {
         context.redirect("sakai.filepicker.helper/tool");
      }
      catch (IOException e) {
		   logger.warn(e.toString());
      }
   }

   public Set getItems() {
      checkUpdateItems();
      if (getPresentation().getItems() != null) {
         return getPresentation().getItems();
      }
      getPresentation().setItems(new HashSet());
      return getPresentation().getItems();
   }

   protected void checkUpdateItems() {
      ToolSession session = SessionManager.getCurrentToolSession();
      if (session.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
         session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {

         List refs = (List)session.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         List newAttachments = new ArrayList();

         for(int i=0; i<refs.size(); i++) {
            Reference ref = (Reference) refs.get(i);
            PresentationItem item = new PresentationItem();
            Node node = getPresentationManager().getNode(ref);
            item.setArtifactId(node.getId());
            newAttachments.add(item);
         }
         session.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         getPresentation().getItems().clear();
         getPresentation().getItems().addAll(newAttachments);
         attachableItems = null;
         listableItems = null;
      }

   }

   public List getAttachableItems() {
      checkUpdateItems();
      if (attachableItems == null) {
         attachableItems = new ArrayList();
         for (Iterator i=getListableItems().iterator();i.hasNext();) {
            DecoratedItem item = (DecoratedItem)i.next();
            Node node = item.getNode();
            attachableItems.add(createSelect(node.getExternalUri(),
                  node.getDisplayName()));
         }
      }

      List pages = getPageList();
      for (Iterator i=pages.iterator();i.hasNext();) {
         DecoratedPage decoratedPage = (DecoratedPage) i.next();
         PresentationPage page = decoratedPage.getBase();
         SelectItem si = (SelectItem)createSelect(page.getUrl(),
                 decoratedPage.getSafeTitle());
         
         boolean present = false;
         for (Iterator j=attachableItems.iterator();j.hasNext();) {
        	 SelectItem isi = (SelectItem) j.next();
        	 if(isi.getLabel() != null && isi.getValue() != null)
        	 if(isi.getLabel().equals(si.getLabel()) && isi.getValue().equals(si.getValue())) {
        		 present = true;
        		 break;
        	 }
         }
         if(!present)
        	 attachableItems.add(si);
      }

      return attachableItems;
   }

   public List getListableItems() {
      checkUpdateItems();
      if (listableItems == null) {
         listableItems = new ArrayList();
         for (Iterator i=getItems().iterator();i.hasNext();) {
            PresentationItem item = (PresentationItem)i.next();
            listableItems.add(new DecoratedItem(item, this));
         }
      }

      return listableItems;
   }

   public String getCurrentPageId() {
      return getCurrentPage().getBase().getId().getValue();
   }

   public void setCurrentPageId(String pageId) {
      nextPageId = pageId;
   }

   public List getPageDropList() {
      List base = getPageList();
      List returned = new ArrayList();
      for (Iterator i=base.iterator();i.hasNext();) {
         DecoratedPage page = (DecoratedPage)i.next();
         returned.add(createSelect(page.getBase().getId().getValue(), page.getSafeTitle()));
      }
      return returned;
   }

   public void setLayouts(List layouts) {
      this.layouts = layouts;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public String processActionNewPage() {
      PresentationPage page = new PresentationPage();

      page.setNewObject(true);
      page.setId(getIdManager().createId());
      page.setPresentation(getPresentation());
      page.setRegions(new HashSet());
      getPresentation().getPages().add(page);
      reorderPages();
      DecoratedPage decoratedPage = new DecoratedPage(page, this);
      decoratedPage.setNewPage(true);
      setCurrentPage(decoratedPage);
      return "edit";
   }

   protected void reorderPages() {
      int index = 0;
      for (Iterator i=presentation.getPages().iterator();i.hasNext();) {
         PresentationPage page = (PresentationPage) i.next();
         page.setSequence(index);
         index++;
      }
      pageList = null;
      attachableItems = null; // make sure list gets re-created in order
   }

   public String processRemoveSelectedPages() {
      List localPageList = pageList;

      for (Iterator i=localPageList.iterator();i.hasNext();) {
         DecoratedPage page = (DecoratedPage) i.next();
         if (page.isSelected()) {
            deletePage(page);
         }
      }

      reorderPages();
      return "main";
   }

   public void deletePage(DecoratedPage page) {
      getPresentation().getPages().remove(page.getBase());
      pageList = null;
      attachableItems = null; // make sure list gets re-created in order
   }

   public String processChangeCurrentPage() {
      List base = getPageList();
      for (Iterator i=base.iterator();i.hasNext();) {
         DecoratedPage page = (DecoratedPage) i.next();
         if (page.getBase().getId().getValue().equals(nextPageId)) {
            setCurrentPage(page);
            break;
         }
      }
      return "arrange";
   }
   public String processActionSelectStyle() {

	   ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	   ToolSession session = SessionManager.getCurrentToolSession();
	   session.removeAttribute(StyleHelper.CURRENT_STYLE);
	   session.removeAttribute(StyleHelper.CURRENT_STYLE_ID);

	   session.setAttribute(StyleHelper.STYLE_SELECTABLE, "true");
	   if (presentation.getStyle() != null)
		   session.setAttribute(StyleHelper.CURRENT_STYLE_ID, presentation.getStyle().getId().getValue());

	   try {
		   context.redirect("osp.style.helper/listStyle");
	   }
	   catch (IOException e) {
		   logger.warn(e.toString());
	   }
	   return null;
   }

   public String processActionSelectLayout() {
	   ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	   ToolSession session = SessionManager.getCurrentToolSession();
	   session.removeAttribute(PresentationLayoutHelper.CURRENT_LAYOUT);
	   session.removeAttribute(PresentationLayoutHelper.CURRENT_LAYOUT_ID);

	   session.setAttribute(PresentationLayoutHelper.LAYOUT_SELECTABLE, "true");
	   if (presentation.getLayout() != null)
		   session.setAttribute(PresentationLayoutHelper.CURRENT_LAYOUT_ID, presentation.getLayout().getId().getValue());

	   try {
		   context.redirect("osp.presLayout.helper/listLayout");
	   }
	   catch (IOException e) {
		   logger.warn(e.toString());
	   }
	   return null;
   }
   


   public int getStep(){
	   return step;
   }
   public String getStepString() {
        return "" + (step);
    }

    public int getPageCount () {
        return getPageList().size();
    }
    
    
    public String getStyleName() {
       ToolSession session = SessionManager.getCurrentToolSession();
       if (session.getAttribute(StyleHelper.CURRENT_STYLE) != null) {
          Style style = (Style)session.getAttribute(StyleHelper.CURRENT_STYLE);
          presentation.setStyle(style);
       }
       else if (session.getAttribute(StyleHelper.UNSELECTED_STYLE) != null) {
          presentation.setStyle(null);
          session.removeAttribute(StyleHelper.UNSELECTED_STYLE);
          return "";
       }
       
       if (presentation.getStyle() != null)
          return presentation.getStyle().getName();
       return "";
    }
    
    
    public String getLayoutName() {
        ToolSession session = SessionManager.getCurrentToolSession();
        if (session.getAttribute(PresentationLayoutHelper.CURRENT_LAYOUT) != null) {
        	PresentationLayout layout = (PresentationLayout)session.getAttribute(PresentationLayoutHelper.CURRENT_LAYOUT);
           presentation.setLayout(layout);
           session.removeAttribute(PresentationLayoutHelper.CURRENT_LAYOUT);
        }
        else if (session.getAttribute(PresentationLayoutHelper.UNSELECTED_LAYOUT) != null) {
           presentation.setLayout(null);
           session.removeAttribute(PresentationLayoutHelper.UNSELECTED_LAYOUT);
           return "";
        }
        
        if (presentation.getLayout() != null)
           return presentation.getLayout().getName();
        return "";
     }

    public Node getPreviewImage() {
        if (presentation.getLayout() == null || presentation.getLayout().getPreviewImageId() == null) {
           return null;
        }
        return getPresentationManager().getNode(presentation.getLayout().getPreviewImageId(), presentation.getLayout());
     }
    
    public boolean islayoutSelected() {
        return (presentation.getLayout() != null);
     }

     public boolean isLayoutPreviewImage() {
         if (islayoutSelected() && (getPreviewImage() != null)) {
             return true;
         }
         return false;
     }

   /**
    * @return the contentHosting
    */
   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   /**
    * @param contentHosting the contentHosting to set
    */
   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   /**
    * @return The url (String) to the portfolio preivew
    */
   public String getPreviewUrl() {
      String url = setRedirectCaller("viewPresentation.osp");
      setAttribute(FreeFormHelper.FREE_FORM_PREFIX + "presentation", presentation);
      url += "?1=1&id="+presentation.getId().getValue();
      return url;
   }


}
