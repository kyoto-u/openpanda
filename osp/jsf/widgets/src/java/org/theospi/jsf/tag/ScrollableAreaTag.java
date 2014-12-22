/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.3/jsf/widgets/src/java/org/theospi/jsf/tag/ScrollableAreaTag.java $
* $Id: ScrollableAreaTag.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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


public class ScrollableAreaTag extends UIComponentTag
{
	private String cssClass = null;
   private String height = null;
   private String width = null;
   private String scrollXStyle = null;
   private String scrollYStyle = null;

	public String getComponentType()
	{
		return "org.theospi.ScrollableArea";
	}

	public String getRendererType()
	{
		return "org.theospi.ScrollableArea";
	}

	public String getCssclass()		{	return cssClass;		}
   public String getHeight()     {  return height;    }
   public String getWidth()      {  return width;     }
   public String getScrollXStyle()     {  return scrollXStyle;    }
   public String getScrollYStyle()      {  return scrollYStyle;     }

	/**
	 * 
	 * @param component		places the attributes in the component
	 */
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);
		TagUtil.setString(component, "cssclass", cssClass);
      TagUtil.setString(component, "height", height);
      TagUtil.setString(component, "width", width);
      TagUtil.setString(component, "scrollXStyle", scrollXStyle);
      TagUtil.setString(component, "scrollYStyle", scrollYStyle);
	}

	public void setCssclass(String string)		{	cssClass = string;		}
   public void setHeight(String string)      {  height = string;  }
   public void setWidth(String string)       {  width = string;      }
   public void setScrollXStyle(String string){  scrollXStyle = string;  }
   public void setScrollYStyle(String string){  scrollYStyle = string;      }
}



