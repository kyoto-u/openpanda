/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.0/wizard/api-impl/src/java/org/theospi/portfolio/wizard/taggable/impl/WizardReference.java $
 * $Id: WizardReference.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2007, 2008 The Sakai Foundation
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

package org.theospi.portfolio.wizard.taggable.impl;

/**
 * Represents a reference to a wizard activity or item.
 * 
 * @author The Sakai Foundation.
 */
public class WizardReference {

	/**
	 * Separates parts of a wizard activity/item reference
	 */
	static final String REF_SEPARATOR = "/";

	/**
	 * First part of every wizard activity/item reference
	 */
	static final String REF_BASE = "wizard";

	/**
	 * Second part of a reference to identify that the reference is for a wizard
	 * page definition
	 */
	static final String REF_DEF = "def";

	/**
	 * Second part of a reference to identify that the reference is for a wizard
	 * page
	 */
	static final String REF_PAGE = "page";

	String type, id;

	public WizardReference(String type, String id) {
		this.type = type;
		this.id = id;
	}

	/**
	 * Method to get the type part of this wizard reference.
	 * 
	 * @return The type part of this wizard reference.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Method to get the identifier part of this wizard reference.
	 * 
	 * @return The identifier part of this wizard reference.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Method to get a wizard activity/item reference object given a reference
	 * string.
	 * 
	 * @param ref
	 *            The reference string.
	 * @return A wizard activity/item reference.
	 */
	public static WizardReference getReference(String ref) {
		WizardReference reference = null;
		String[] parts = ref.split(REF_SEPARATOR);
		if (parts.length == 4 && REF_BASE.equals(parts[1])
				&& (REF_DEF.equals(parts[2]) || REF_PAGE.equals(parts[2]))) {
			reference = new WizardReference(parts[2], parts[3]);
		}
		return reference;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(REF_SEPARATOR);
		sb.append(REF_BASE);
		sb.append(REF_SEPARATOR);
		sb.append(type);
		sb.append(REF_SEPARATOR);
		sb.append(id);
		return sb.toString();
	}
}
