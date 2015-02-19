/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/mgt/ReferenceParser.java $
 * $Id: ReferenceParser.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.shared.mgt;

import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.EntityProducer;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 8, 2005
 * Time: 2:43:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReferenceParser {

   private String context;
   private String id;
   private String siteId;
   private String ref;

   public ReferenceParser(String reference, EntityProducer parent, boolean siteInfo) {
      parse(reference, parent, siteInfo);
   }

   public ReferenceParser(String reference, EntityProducer parent) {
      parse(reference, parent, true);
   }

   protected void parse(String reference, EntityProducer parent, boolean siteInfo) {
      // with /pres/<siteid>/<preseId>/content/etc/etc.xml
      String baseRef = reference.substring(parent.getLabel().length() + 2); // lenght of 2 sperators

      if (siteInfo) {
         int sep = baseRef.indexOf(Entity.SEPARATOR);
         siteId = baseRef.substring(0, sep);
         baseRef = baseRef.substring(sep + 1);

         sep = baseRef.indexOf(Entity.SEPARATOR);
         id = baseRef.substring(0, sep);
         ref = baseRef.substring(sep);
      }
      else {
         ref = Entity.SEPARATOR + baseRef;
      }
      context = parent.getLabel();
   }

   public String getContext() {
      return context;
   }

   public void setContext(String context) {
      this.context = context;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getRef() {
      return ref;
   }

   public void setRef(String ref) {
      this.ref = ref;
   }

   public String getSiteId() {
      return siteId;
   }

   public void setSiteId(String siteId) {
      this.siteId = siteId;
   }

}
