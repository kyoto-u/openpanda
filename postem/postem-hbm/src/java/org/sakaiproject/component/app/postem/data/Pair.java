/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/postem/tags/sakai-2.9.0/postem-hbm/src/java/org/sakaiproject/component/app/postem/data/Pair.java $
 * $Id: Pair.java 59680 2009-04-03 23:28:39Z arwhyte@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2008 The Sakai Foundation
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

package org.sakaiproject.component.app.postem.data;

public class Pair {

	private Object first;

	private Object second;

	public Pair(Object first, Object second) {
		this.first = first;
		this.second = second;
	}

	public Object getFirst() {
		return first;
	}

	public Object getSecond() {
		return second;
	}
}
