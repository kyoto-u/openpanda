/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.0/jsf/widgets/src/java/org/theospi/jsf/util/OspxTagHelper.java $
* $Id: OspxTagHelper.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
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
package org.theospi.jsf.util;



public class OspxTagHelper {

	public static boolean isVertical(String inValue)
		throws OspxTagAttributeValueException
	{
		if(inValue.equalsIgnoreCase("vertical") || 
				inValue.equalsIgnoreCase("y"))
			return true;
		if(inValue.equalsIgnoreCase("horizontal") || 
				inValue.equalsIgnoreCase("x"))
			return false;
			
		throw new OspxTagAttributeValueException(
					"A direction 'vertical' or 'horizontal' was expected but got '" + inValue + "'"
				);
	}
	
	
	public static boolean parseBoolean(String inValue)
		throws OspxTagAttributeValueException
	{
		if(inValue.equalsIgnoreCase("true") || 
				inValue.equalsIgnoreCase("yes") || 
				inValue.equalsIgnoreCase("1"))
			return true;
		if(inValue.equalsIgnoreCase("false") || 
				inValue.equalsIgnoreCase("no") || 
				inValue.equalsIgnoreCase("0"))
			return false;
			
		throw new OspxTagAttributeValueException(
					"A direction 'vertical' or 'horizontal' was expected but got '" + inValue + "'"
				);
	}
}