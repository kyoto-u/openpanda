/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.3/jsf/widgets/src/java/org/theospi/jsf/component/XHeaderDrawerComponent.java $
* $Id: XHeaderDrawerComponent.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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
package org.theospi.jsf.component;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIOutput;

import org.sakaiproject.jsf.model.InitObjectContainer;


public class XHeaderDrawerComponent extends UIOutput implements InitObjectContainer
{

	private String divId = null;
	private List initScripts = null;

	/**
	 * Constructor-
	 * Indicates the component that this class is linked to
	 *
	 */
	public XHeaderDrawerComponent()
	{
		super();
      setInitScripts(new ArrayList());
		this.setRendererType("org.theospi.XHeaderDrawer");
	}
	
	public void setDivId(String inDivId)
	{
		divId = inDivId;
	}
	
	public String getDivId()
	{
		return divId;
	}

   public void addInitScript(String script) {
      getInitScripts().add(script);
   }

   public List getInitScripts() {
      return initScripts;
   }

   public void setInitScripts(List initScripts) {
      this.initScripts = initScripts;
   }

}



