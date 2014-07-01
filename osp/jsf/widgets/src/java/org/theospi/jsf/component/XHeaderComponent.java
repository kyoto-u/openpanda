/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.0/jsf/widgets/src/java/org/theospi/jsf/component/XHeaderComponent.java $
* $Id: XHeaderComponent.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
package org.theospi.jsf.component;

import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;



public class XHeaderComponent extends UIOutput
{

	/**
	 * Constructor-
	 * Indicates the component that this class is linked to
	 *
	 */
	public XHeaderComponent()
	{
		super();
		this.setRendererType("org.theospi.XHeader");
	}
	
	
	/**
	 * pulls out the drawer component
	 *
	 */
	public XHeaderDrawerComponent getDrawerComponent()
	{
      for (Iterator iter = this.getChildren().iterator(); iter.hasNext();)
		{ 
	    	UIComponent child = (UIComponent) iter.next();

	    	if (child instanceof XHeaderDrawerComponent) {
				return (XHeaderDrawerComponent)child;
	        }
	    }
	    return null;
	}
}



