/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/edu-services/tags/sakai-10.2/cm-service/cm-api/api/src/java/org/sakaiproject/coursemanagement/api/exception/IdExistsException.java $
 * $Id: IdExistsException.java 105077 2012-02-24 22:54:29Z ottenhoff@longsight.com $
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
package org.sakaiproject.coursemanagement.api.exception;

/**
 * An exception thrown when an an object can not be created because the same
 * type of object with the same ID already exist.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public class IdExistsException extends RuntimeException {

	private static final long serialVersionUID = -8588237050380289434L;

	public IdExistsException(String id, String className) {
		super("An object of type " + className + " with id " + id + " already exists");
	}
}
