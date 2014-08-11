/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/user/branches/sakai-2.8.x/user-tool-prefs/tool/src/java/org/sakaiproject/user/jsf/HideDivisionComponent.java $
 * $Id: HideDivisionComponent.java 83342 2010-10-18 17:43:10Z arwhyte@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2010 The Sakai Foundation
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

package org.sakaiproject.user.jsf;

import javax.faces.component.UIComponentBase;

/**
 * @author Chen Wen
 * @version $Id: HideDivisionComponent.java 83342 2010-10-18 17:43:10Z arwhyte@umich.edu $
 * 
 */
public class HideDivisionComponent extends UIComponentBase
{
  public HideDivisionComponent()
	{
		super();
		this.setRendererType("org.sakaiproject.HideDivision");
	}

	public String getFamily()
	{
		return "HideDivision";
	}
	
	public boolean getRendersChildren()
	{
	  return false;
	}	
}
