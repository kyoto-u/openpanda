/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.4/matrix/tool/src/java/org/theospi/portfolio/matrix/control/EditedScaffoldingStorage.java $
* $Id: EditedScaffoldingStorage.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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
package org.theospi.portfolio.matrix.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.theospi.portfolio.matrix.model.Scaffolding;
import org.theospi.portfolio.matrix.model.ScaffoldingCell;


public class EditedScaffoldingStorage {
   protected final Log logger = LogFactory.getLog(getClass());

   public static final String STORED_SCAFFOLDING_FLAG =
      "org_theospi_storedScaffoldingCall";
   public static final String EDITED_SCAFFOLDING_STORAGE_SESSION_KEY =
      "org_theospi_editedScaffolding";
   
   private Scaffolding scaffolding = null;
   private ScaffoldingCell scaffoldingCell = null;

   public EditedScaffoldingStorage(Scaffolding scaffolding) {

      this.scaffolding = scaffolding;
   }
   
   public EditedScaffoldingStorage(ScaffoldingCell scaffoldingCell) {

      this.scaffoldingCell = scaffoldingCell;
   }

 
   /**
    * @return Returns the scaffolding.
    */
   public Scaffolding getScaffolding() {
      return scaffolding;
   }
   /**
    * @param scaffolding The scaffolding to set.
    */
   public void setScaffolding(Scaffolding scaffolding) {
      this.scaffolding = scaffolding;
   }
   /**
    * @return Returns the scaffoldingCell.
    */
   public ScaffoldingCell getScaffoldingCell() {
      return scaffoldingCell;
   }
   /**
    * @param scaffoldingCell The scaffoldingCell to set.
    */
   public void setScaffoldingCell(ScaffoldingCell scaffoldingCell) {
      this.scaffoldingCell = scaffoldingCell;
   }
}
