/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.6/jsf/widgets/src/java/org/theospi/jsf/tag/XHeaderTitleTag.java $
* $Id: XHeaderTitleTag.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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


public class XHeaderTitleTag extends UIComponentTag
{
	private String cssclass = null;
   private String value = null;

	public String getComponentType()
	{
		return "org.theospi.XHeaderTitle";
	}

	public String getRendererType()
	{
		return "org.theospi.XHeaderTitle";
	}

	public String getCssclass()		{	return cssclass;			}

	/**
	 * 
	 * @param component		places the attributes in the component
	 */
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);
      TagUtil.setString(component, "cssclass", cssclass);
      TagUtil.setString(component, "value", value);
	}

	public void setCssclass(String string)		{	cssclass = string;	}

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }
}



