/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/trunk/jsf/widgets/src/java/org/theospi/jsf/component/ScrollableAreaComponent.java $
* $Id: ScrollableAreaComponent.java 10835 2006-06-17 03:25:03Z lance@indiana.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
package org.theospi.portfolio.help.jsf.component;

import javax.faces.component.UIOutput;

/**
 * 
 * @author andersjb
 *
 */
public class GlossaryComponent extends UIOutput
{
	/**
	 * Constructor-
	 * Indicates the component that this class is linked to
	 *
	 */
	public GlossaryComponent()
	{
		super();
		this.setRendererType("org.theospi.help.Glossary");
	}
}



