
/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.0/matrix/tool/src/java/org/theospi/portfolio/matrix/control/CellAndNodeForm.java $
* $Id: CellAndNodeForm.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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
package org.theospi.portfolio.matrix.control;

/**
 * @author chmaurer
 */
public class CellAndNodeForm {
   
   private String page_id = null;
   private String node_id = null;

   /**
    * @return Returns the page_id.
    */
   public String getPage_id() {
      return page_id;
   }
   /**
    * @param page_id The page_id to set.
    */
   public void setPage_id(String page_id) {
      this.page_id = page_id;
   }
   /**
    * @return Returns the node_id.
    */
   public String getNode_id() {
      return node_id;
   }
   /**
    * @param node_id The node_id to set.
    */
   public void setNode_id(String node_id) {
      this.node_id = node_id;
   }
}
