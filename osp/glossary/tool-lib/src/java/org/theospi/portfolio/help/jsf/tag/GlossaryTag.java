/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/trunk/jsf/widgets/src/java/org/theospi/jsf/tag/ScrollableAreaTag.java $
* $Id: ScrollableAreaTag.java 10835 2006-06-17 03:25:03Z lance@indiana.edu $
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
package org.theospi.portfolio.help.jsf.tag;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;

import org.sakaiproject.jsf.util.TagUtil;


public class GlossaryTag extends UIComponentTag
{
	private String hover = null;
   private String link = null;
   private String firstOnly = null;

	public String getComponentType()
	{
		return "org.theospi.help.Glossary";
	}

	public String getRendererType()
	{
		return "org.theospi.help.Glossary";
	}

	public String getHover()      {	return hover;		}
   public String getLink()       {  return link;    }
   public String getFirstOnly()  {  return firstOnly;     }

	/**
	 * 
	 * @param component		places the attributes in the component
	 */
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);
		TagUtil.setString(component, "hover", hover);
      TagUtil.setString(component, "link", link);
      TagUtil.setString(component, "firstOnly", firstOnly);
	}

	public void setHover(String string)		{	hover = string;		}
   public void setLink(String string)     {  link = string;  }
   public void setFirstOnly(String string)    {  firstOnly = string;      }
}



