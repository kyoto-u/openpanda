/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/common/tags/common-1.2.2/import-parsers/common-cartridge/src/java/org/sakaiproject/importer/impl/translators/CCLearningApplicationResourceTranslator.java $
 * $Id: CCLearningApplicationResourceTranslator.java 67593 2009-10-11 10:57:49Z david.horwitz@uct.ac.za $
 ***********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation
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

package org.sakaiproject.importer.impl.translators;

public class CCLearningApplicationResourceTranslator extends
		CCWebContentTranslator {
	
	public String getTypeName() {
		return "associatedcontent/imscc_xmlv1p0/learning-application-resource";
	}

}
