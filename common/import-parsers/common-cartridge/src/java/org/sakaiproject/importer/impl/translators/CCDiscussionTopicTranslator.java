/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/common/tags/common-1.2.2/import-parsers/common-cartridge/src/java/org/sakaiproject/importer/impl/translators/CCDiscussionTopicTranslator.java $
 * $Id: CCDiscussionTopicTranslator.java 67593 2009-10-11 10:57:49Z david.horwitz@uct.ac.za $
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

import org.sakaiproject.importer.api.IMSResourceTranslator;
import org.sakaiproject.importer.api.Importable;
import org.sakaiproject.importer.impl.importables.DiscussionTopic;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.sakaiproject.importer.impl.XPathHelper;

public class CCDiscussionTopicTranslator implements IMSResourceTranslator {

	public String getTypeName() {
		return "imsdt_xmlv1p0";
	}

	public boolean processResourceChildren() {
		// we don't want discussion thread XML descriptors to be dropped into Resources
		return false;
	}

	public Importable translate(Node resourceNode, Document descriptor,
			String contextPath, String archiveBasePath) {
		DiscussionTopic rv = new DiscussionTopic();
		rv.setTitle(XPathHelper.getNodeValue("//title[1]", descriptor));
		rv.setDescription(XPathHelper.getNodeValue("//text[1]", descriptor));
		return rv;
	}

}
