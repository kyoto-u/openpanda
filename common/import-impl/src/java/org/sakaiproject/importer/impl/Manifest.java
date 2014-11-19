/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/common/tags/sakai-10.1/import-impl/src/java/org/sakaiproject/importer/impl/Manifest.java $
 * $Id: Manifest.java 105077 2012-02-24 22:54:29Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2006, 2008 The Sakai Foundation
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

package org.sakaiproject.importer.impl;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public interface Manifest {
	
	Node getResourceForId(String resourceId, Document manifest);
	
	List getItemNodes(Document manifest);
	
	List getResourceNodes(Document manifest);
	
	List getTopLevelItemNodes(Document manifest);

}
