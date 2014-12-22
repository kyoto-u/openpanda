/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.3/jsf/widgets/src/java/org/theospi/jsf/tag/XHeaderTag.java $
* $Id: XHeaderTag.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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
package org.theospi.jsf.tag;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;

import org.sakaiproject.jsf.util.TagUtil;


public class XHeaderTag extends UIComponentTag
{
	private String title = null;
	private String expandable = null;
	private String initiallyexpanded = null;
	private String italicize = null;
	private String small = null;
	private String expandclass = null;

	public String getComponentType()
	{
		return "org.theospi.XHeader";
	}

	public String getRendererType()
	{
		return "org.theospi.XHeader";
	}

	public String getTitle()			{	return title;				}
	public String getExpandable()		{	return expandable;			}
	public String getInitiallyexpanded(){	return initiallyexpanded;	}
	public String getItalicize()		{	return italicize;			}
	public String getSmall()			{	return small;				}
	public String getExpandclass()		{	return expandclass;			}

	/**
	 * 
	 * @param component		places the attributes in the component
	 */
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);
		TagUtil.setString(component, "title", title);
		TagUtil.setString(component, "expandable", expandable);
		TagUtil.setString(component, "initiallyexpanded", initiallyexpanded);
		TagUtil.setString(component, "italicize", italicize);
		TagUtil.setString(component, "small", small);
		TagUtil.setString(component, "expandclass", expandclass);
	}

	public void setTitle(String string)				{	title = string;		}
	public void setExpandable(String string)		{	expandable = string;		}
	public void setInitiallyexpanded(String string) {	initiallyexpanded = string;		}
	public void setItalicize(String string)			{	italicize = string;		}
	public void setSmall(String string)				{	small = string;		}
	public void setExpandclass(String string)		{	expandclass = string;		}
}



