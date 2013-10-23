/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.3/jsf/widgets/src/java/org/theospi/jsf/tag/SplitSectionTag.java $
* $Id: SplitSectionTag.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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
package org.theospi.jsf.tag;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;

import org.sakaiproject.jsf.util.TagUtil;


public class SplitSectionTag extends UIComponentTag
{
	private String cssclass = null;
	private String valign = null;
	private String align = null;
	private String size = null;

	public String getComponentType()
	{
		return "org.theospi.SplitSection";
	}

	public String getRendererType()
	{
		return "org.theospi.SplitSection";
	}

	public String getCssclass()	{	return cssclass;	}
	public String getValign()	{	return valign;		}
	public String getAlign()	{	return align;		}
	public String getSize()		{	return size;		}

	/**
	 * 
	 * @param component		places the attributes in the component
	 */
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);
		TagUtil.setString(component, "cssclass", cssclass);
		TagUtil.setString(component, "valign", valign);
		TagUtil.setString(component, "align", align);
		TagUtil.setString(component, "size", size);
	}

	public void setCssclass(String string)			{	cssclass = string;	}
	public void setValign(String string)			{	valign = string;	}
	public void setAlign(String string)				{	align = string;		}
	public void setSize(String string)				{	size = string;		}
}



