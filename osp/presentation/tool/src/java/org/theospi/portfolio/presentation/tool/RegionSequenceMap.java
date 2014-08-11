/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/tool/RegionSequenceMap.java $
* $Id:RegionSequenceMap.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.presentation.tool;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;

import org.theospi.portfolio.presentation.model.PresentationPage;
import org.theospi.portfolio.presentation.model.PresentationPageItem;
import org.theospi.portfolio.presentation.model.PresentationPageRegion;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 2, 2006
 * Time: 2:45:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class RegionSequenceMap extends Hashtable {

   private PresentationPage page;
   private int regionSeqNo = 0;
   private List childRegions;

   public RegionSequenceMap(RegionMap map, int regionSeqNo) {
      this.page = map.getPage();
      setRegionSeqNo(regionSeqNo);
      for (Iterator i=map.getPage().getRegions().iterator();i.hasNext();) {
         PresentationPageRegion region = (PresentationPageRegion) i.next();
         put(region.getRegionId(), new DecoratedRegion(this, region, regionSeqNo));
      }
   }

   public void remove(ActionEvent event) {
      for (Iterator i=getChildRegions().iterator();i.hasNext();) {
         ValueBinding binding = (ValueBinding) i.next();
         DecoratedRegion region = (DecoratedRegion) binding.getValue(FacesContext.getCurrentInstance());
         removeItem(region.getBase());
         region.setRegionItemList(null);
         region.initRegionList();
      }
   }

   protected void removeItem(PresentationPageRegion region) {
      for (Iterator i=region.getItems().iterator();i.hasNext();) {
         PresentationPageItem item = (PresentationPageItem) i.next();
         if (item.getRegionItemSeq() == getRegionSeqNo()) {
            i.remove();
         }
      }

      region.reorderItems();
   }

   public List getChildRegions() {
      return childRegions;
   }

   public void setChildRegions(List childRegions) {
      this.childRegions = childRegions;
   }

   public PresentationPage getPage() {
      return page;
   }

   public void setPage(PresentationPage page) {
      this.page = page;
   }

   public int getRegionSeqNo() {
      return regionSeqNo;
   }

   public void setRegionSeqNo(int regionSeqNo) {
      this.regionSeqNo = regionSeqNo;
   }

   public synchronized Object get(Object key) {
      return super.get(key);
   }

}
