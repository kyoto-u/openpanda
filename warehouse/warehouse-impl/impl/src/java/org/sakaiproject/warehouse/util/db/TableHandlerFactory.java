/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/warehouse/tags/sakai-2.9.2/warehouse-impl/impl/src/java/org/sakaiproject/warehouse/util/db/TableHandlerFactory.java $
* $Id: TableHandlerFactory.java 59691 2009-04-03 23:46:45Z arwhyte@umich.edu $
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
package org.sakaiproject.warehouse.util.db;

import org.xml.sax.ContentHandler;
import org.sakaiproject.warehouse.util.db.Cascade;
import org.sakaiproject.warehouse.util.db.DbLoader;
import org.sakaiproject.warehouse.util.db.GenericTableHandler;
import org.sakaiproject.warehouse.util.db.MySqlHandler;

/**
 * Returns a handler implementation according to the database vendor
 *
 * @author <a href="felipeen@udel.edu">Luis F.C. Mendes</a> - University of Delaware
 * @version $Revision 1.0 $
 */
public class TableHandlerFactory{


   /**
   * @param loader instance of the DbLoader class
   * @return ContentHandler inplementation
   */
   public static ContentHandler getTableHandler(DbLoader loader){

      //set loader for cascade
      Cascade.setLoader(loader);

     if(loader.getDbName().equalsIgnoreCase("mysql"))
       return new MySqlHandler(loader);
     else
       return new GenericTableHandler(loader);

   }
}
