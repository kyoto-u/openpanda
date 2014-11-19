/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.1/jsf/widgets/src/java/org/theospi/jsf/impl/DefaultXmlTagFactory.java $
* $Id: DefaultXmlTagFactory.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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
package org.theospi.jsf.impl;

import org.theospi.jsf.intf.XmlTagFactory;
import org.theospi.jsf.intf.XmlTagHandler;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 29, 2005
 * Time: 10:33:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultXmlTagFactory implements XmlTagFactory {

   private XmlTagHandler defaultHandler;

   public XmlTagHandler getHandler(String uri, String localName, String qName) {
      return null;
   }

   public XmlTagHandler getDefaultHandler() {
      return defaultHandler;
   }

   public void setDefaultHandler(XmlTagHandler defaultHandler) {
      this.defaultHandler = defaultHandler;
   }
}
