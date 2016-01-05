/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.6/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/utils/mvc/intf/VelocityEngineFactory.java $
 * $Id: VelocityEngineFactory.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.utils.mvc.intf;

import org.apache.velocity.app.VelocityEngine;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jul 29, 2005
 * Time: 3:07:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface VelocityEngineFactory {
   VelocityEngine getVelocityEngine();
}
