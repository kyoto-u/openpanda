/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api/src/java/org/theospi/portfolio/guidance/model/Guidance.java $
* $Id:Guidance.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.guidance.model;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 11, 2005
 * Time: 12:06:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class Guidance extends IdentifiableObject implements Serializable {

   public final static String INSTRUCTION_TYPE = "instruction";
   public final static String EXAMPLE_TYPE = "example";
   public final static String RATIONALE_TYPE = "rationale";
   public final static String RUBRIC_TYPE = "rubric";
   public final static String EXPECTATIONS_TYPE = "expectations";

   private String description;
   private String siteId;
   private Id securityQualifier;
   private String securityViewFunction;
   private String securityEditFunction;

   private List items;

   private boolean newObject = false;

   private static final long serialVersionUID = 7834424504411509616L;

   public Guidance() {
   }

   public Guidance(Id id, String description, String siteId, Id securityQualifier,
                   String securityViewFunction, String securityEditFunction) {
      this.description = description;
      this.siteId = siteId;
      this.securityQualifier = securityQualifier;
      this.securityViewFunction = securityViewFunction;
      this.securityEditFunction = securityEditFunction;
      setId(id);
      newObject = true;
      items = new ArrayList();
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getSiteId() {
      return siteId;
   }

   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }

   public Id getSecurityQualifier() {
      return securityQualifier;
   }

   public void setSecurityQualifier(Id securityQualifier) {
      this.securityQualifier = securityQualifier;
   }

   public String getSecurityViewFunction() {
      return securityViewFunction;
   }

   public void setSecurityViewFunction(String securityViewFunction) {
      this.securityViewFunction = securityViewFunction;
   }

   public String getSecurityEditFunction() {
      return securityEditFunction;
   }

   public void setSecurityEditFunction(String securityEditFunction) {
      this.securityEditFunction = securityEditFunction;
   }

   public List getItems() {
      return items;
   }

   public void setItems(List items) {
      this.items = items;
   }

   public GuidanceItem getItem(String type) {
      for (Iterator i=getItems().iterator();i.hasNext();) {
         GuidanceItem item = (GuidanceItem)i.next();
         if (item.getType().equals(type)) {
            return item;
         }
      }
      GuidanceItem newItem = new GuidanceItem(this, type);
      items.add(newItem);
      return newItem;
   }

   public GuidanceItem getInstruction() {
      return getItem(INSTRUCTION_TYPE);
   }

   public GuidanceItem getExample() {
      return getItem(EXAMPLE_TYPE);
   }

   public GuidanceItem getRationale() {
      return getItem(RATIONALE_TYPE);
   }

   public GuidanceItem getRubric() {
	   return getItem(RUBRIC_TYPE);
   }

   public GuidanceItem getExpectations() {
	   return getItem(EXPECTATIONS_TYPE);
   }
   
   public boolean isNewObject() {
      return newObject;
   }

   public void setNewObject(boolean newObject) {
      this.newObject = newObject;
   }

}


