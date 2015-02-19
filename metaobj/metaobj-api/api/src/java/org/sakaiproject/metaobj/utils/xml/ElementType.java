/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/utils/xml/ElementType.java $
 * $Id: ElementType.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.utils.xml;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jul 25, 2004
 * Time: 3:26:24 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ElementType {
   public Class getObjectType();

   public int getLength();

   public int getMaxLength();

   public int getMinLength();

   public String getDefaultValue();

   public String getFixedValue();

   public List getEnumeration();

   /**
    * @return A regular expression that expresses a constraint on legal value(s) for the element.
    */
   public Pattern getPattern();

   /**
    * @return ValueRange object describing the type's range.
    *         Objects within this range will be of the class
    *         getObjectType().  This is null if there is no restriction
    *         on the value's range
    */
   public ValueRange getRange();

   /**
    * @return the base xml type of the associated node
    */
   public String getBaseType();

}
