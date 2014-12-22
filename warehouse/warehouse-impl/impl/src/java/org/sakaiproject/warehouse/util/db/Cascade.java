/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/warehouse/tags/sakai-10.3/warehouse-impl/impl/src/java/org/sakaiproject/warehouse/util/db/Cascade.java $
* $Id: Cascade.java 105080 2012-02-24 23:10:31Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
package org.sakaiproject.warehouse.util.db;

/**
 * Adds cascade constraint to database specific
 *
 * @author <a href="felipeen@udel.edu">Luis F.C. Mendes</a> - University of Delaware
 * @version $Revision 1.0 $
 */
public class Cascade{

	private static DbLoader loader = null;

	protected static void setLoader(DbLoader dbloader){
		loader = dbloader;
	}

	protected static String cascadeConstraint(String statement){
		if(loader.getDbName().equalsIgnoreCase("postgresql"))
			return (statement + " CASCADE");
		else
			if(loader.getDbName().equalsIgnoreCase("oracle"))
				return (statement + " CASCADE CONSTRAINTS");
		else
			return statement;
   }
}
