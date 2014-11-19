/**
 * $Id: InvalidEmailTemplateException.java 105078 2012-02-24 23:00:38Z ottenhoff@longsight.com $
 * $URL: https://source.sakaiproject.org/svn/polls/tags/sakai-10.2/impl/src/java/org/sakaiproject/poll/logic/impl/InvalidEmailTemplateException.java $
 * 
 **************************************************************************
 * Copyright (c) 2008, 2009 The Sakai Foundation
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
 */
package org.sakaiproject.poll.logic.impl;

/**
 * @author branden
 *
 */
public class InvalidEmailTemplateException extends Exception {
	
	private static final long serialVersionUID = 1L;

	private String
		key,
		fileName;
	
	public InvalidEmailTemplateException(String key, String fileName) {
		super();
		this.key = key;
		this.fileName = fileName;
	}

	public InvalidEmailTemplateException(String key, String fileName, Throwable cause) {
		super(cause);
		this.key = key;
		this.fileName = fileName;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getFileName() {
		return this.fileName;
	}
}

