/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/sam/tags/sakai-10.3/samigo-services/src/java/org/sakaiproject/tool/assessment/integration/helper/integrated/ServerConfigurationServiceHelperImpl.java $
 * $Id: ServerConfigurationServiceHelperImpl.java 106521 2012-04-04 08:14:42Z david.horwitz@uct.ac.za $
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


package org.sakaiproject.tool.assessment.integration.helper.integrated;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.tool.assessment.integration.helper.ifc.ServerConfigurationServiceHelper;
/**
 * An implementation of Samigo-specific authorization (based on Gradebook's) needs based
 * on the shared Section Awareness API.
 */
public class ServerConfigurationServiceHelperImpl implements ServerConfigurationServiceHelper {
    private static final Log log = LogFactory.getLog(ServerConfigurationServiceHelperImpl.class);

    public String getString(String key, String defaultValue){
	return (ServerConfigurationService.getString(key, defaultValue));
    }

}
