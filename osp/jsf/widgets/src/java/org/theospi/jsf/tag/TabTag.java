/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.3/jsf/widgets/src/java/org/theospi/jsf/tag/TabTag.java $
* $Id: TabTag.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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


public class TabTag extends UIComponentTag
{
	private String title = null;
   private String selected = "false";
   private String disabled = "false";
   private String cssclass = null;

	public String getComponentType()
	{
		return "org.theospi.Tab";
	}

	public String getRendererType()
	{
		return "org.theospi.Tab";
	}

	public String getTitle()		
   {	
      return title;	
   }
   
   public String getSelected()      
   {  
      return selected;  
   }
   
   public String getDisabled() {
      return disabled;
   }
   
   public String getCssclass() {
      return cssclass;
   }

   
   /**
	 * 
	 * @param component		places the attributes in the component
	 */
	protected void setProperties(UIComponent component)
	{
		super.setProperties(component);
		TagUtil.setString(component, "title", title);
      TagUtil.setString(component, "selected", selected);
      TagUtil.setString(component, "disabled", disabled);
      TagUtil.setString(component, "cssclass", cssclass);
	}

	public void setTitle(String title)
	{
      this.title = title;
   }
   
   public void setSelected(String selected)
   {
      this.selected = selected;
   }
   
   public void setDisabled(String disabled) {
      this.disabled = disabled;
   }
   
   public void setCssclass(String cssclass) {
      this.cssclass = cssclass;
   }
}



