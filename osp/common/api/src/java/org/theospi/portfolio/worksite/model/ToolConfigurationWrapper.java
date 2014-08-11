/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api/src/java/org/theospi/portfolio/worksite/model/ToolConfigurationWrapper.java $
* $Id:ToolConfigurationWrapper.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.worksite.model;

import java.io.Serializable;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.Tool;
import org.theospi.portfolio.shared.model.OspException;

public class ToolConfigurationWrapper implements Serializable, ToolConfiguration{
   protected final transient Log logger = LogFactory.getLog(getClass());

   private ToolConfiguration toolConfig;
   public ToolConfigurationWrapper(ToolConfiguration toolConfig){
      this.toolConfig = toolConfig;
   }

   public String getToolId() {
      return toolConfig.getTool().getId();
   }

   public String getTitle() {
      return toolConfig.getTitle();
   }

   public String getLayoutHints() {
      return toolConfig.getLayoutHints();
   }

   public int[] parseLayoutHints() {
      return toolConfig.parseLayoutHints();
   }

   public String getSkin() {
      return toolConfig.getSkin();
   }

   public String getPageId() {
      return toolConfig.getPageId();
   }

   public String getSiteId() {
      return toolConfig.getSiteId();
   }

   public SitePage getContainingPage() {
      SitePage returned = toolConfig.getContainingPage();

      if (returned == null) {
         Site site = null;
         try {
            site = SiteService.getSite(getSiteId());
         } catch (IdUnusedException e) {
            logger.error("", e);
            throw new OspException(e);
         }
         returned = site.getPage(getPageId());
      }
      return returned;
   }

   public String getId() {
      if (toolConfig == null) return null;
      return toolConfig.getId();
   }

	public void setLayoutHints(String layoutHints) {
		toolConfig.setLayoutHints(layoutHints);	
	}

	public void moveUp() {
		toolConfig.moveUp();	
	}
	
	public void moveDown() {
		toolConfig.moveDown();
	}
	
	public int getPageOrder() {
		return toolConfig.getPageOrder();
	}
	
	public Properties getConfig() {
		return toolConfig.getConfig();
	}
	
	public String getContext() {
		return toolConfig.getContext();
	}
	
	public Properties getPlacementConfig() {
		return toolConfig.getPlacementConfig();
	}
	
	public Tool getTool() {
		return toolConfig.getTool();
	}
	
	public void setTitle(String title) {
		toolConfig.setTitle(title);	
	}
	
	public void setTool(String string, Tool tool) {
		toolConfig.setTool(string, tool);
	}
	
	public void save() {
		toolConfig.save();	
	}
}
