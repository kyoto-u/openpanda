/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/tool/DecoratedRegion.java $
* $Id:DecoratedRegion.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

import org.theospi.portfolio.presentation.component.SequenceComponent;
import org.theospi.portfolio.presentation.model.PresentationPageItem;
import org.theospi.portfolio.presentation.model.PresentationPageRegion;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 1, 2006
 * Time: 5:59:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedRegion {

   private PresentationPageRegion region;
   private int regionItemSeq = 0;
   private RegionMap regionMap;
   private List regionItemList = null;

   public DecoratedRegion(RegionMap regionMap, PresentationPageRegion region) {
      this.regionMap = regionMap;
      this.region = region;
      initRegionList();
   }

   public DecoratedRegion(RegionSequenceMap regionMap, PresentationPageRegion region, int regionItemSeq) {
      this.region = region;
      this.regionItemSeq = regionItemSeq;
   }

   public PresentationPageRegion getBase() {
      return region;
   }

   public PresentationPageItem getItem() {
      if (getRegion().getItems().size() <= 0) {
         return null;
      }
      return (PresentationPageItem) getRegion().getItems().get(regionItemSeq);
   }

   public PresentationPageRegion getRegion() {
      return region;
   }

   public void setRegion(PresentationPageRegion region) {
      this.region = region;
   }

   public int getRegionItemSeq() {
      return regionItemSeq;
   }

   public void setRegionItemSeq(int regionItemSeq) {
      this.regionItemSeq = regionItemSeq;
   }

   public RegionMap getRegionMap() {
      return regionMap;
   }

   public void setRegionMap(RegionMap regionMap) {
      this.regionMap = regionMap;
   }

   public List getRegionItemList() {
      return regionItemList;
   }

   public void setRegionItemList(List regionItemList) {
      this.regionItemList = regionItemList;
   }

   public void initRegionList() {
      regionItemList = new ArrayList();
      for (int i=0;i<getBase().getItems().size();i++) {
         regionItemList.add(new RegionSequenceMap(getRegionMap(), i));
      }
   }

   public void addToSequence(ActionEvent event) {
      UIComponent component = event.getComponent();

      while (!(component instanceof SequenceComponent) && component != null) {
         component = component.getParent();
      }

      if (component != null) {
         ((SequenceComponent)component).addToSequence();
      }
   }
}
