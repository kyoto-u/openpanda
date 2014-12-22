/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.3/common/api-impl/src/java/org/theospi/portfolio/list/service/ListServiceImpl.java $
* $Id: ListServiceImpl.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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

package org.theospi.portfolio.list.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.ToolManager;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.theospi.portfolio.list.intf.CustomLinkListGenerator;
import org.theospi.portfolio.list.intf.ListGenerator;
import org.theospi.portfolio.list.intf.ListService;
import org.theospi.portfolio.list.model.Column;
import org.theospi.portfolio.list.model.ListConfig;

public class ListServiceImpl  extends HibernateDaoSupport implements ListService {
   protected final transient Log logger = LogFactory.getLog(getClass());
   public final static String LIST_GEN_ID_TAG = "listGenId";
   public final static String SITE_TYPE_LIST_TAG = "siteTypeList";

   private Map listGenerators;
   private IdManager idManager;
   private AuthenticationManager authnManager;
   private ToolManager toolManager;

   public List getCurrentDisplayColumns() {
      return getCurrentGenerator().getColumns();
   }
   
   public List getSortableColumns() {
      return ((CustomLinkListGenerator)getCurrentGenerator()).getSortableColumns();
   }

   public List getBundleLookupColumns() {
      return ((CustomLinkListGenerator)getCurrentGenerator()).getBundleLookupColumns();
   }
   
   public String getEntryLink(Object entry) {
      if (getCurrentGenerator() instanceof CustomLinkListGenerator) {
         String uri = ((CustomLinkListGenerator)getCurrentGenerator()).getCustomLink(entry);
         if (uri != null) {
            return uri;
         }
      }

      return null;
   }
   
   public String getDefaultSortColumn() {
      return getCurrentGenerator().getDefaultSortColumn();
   }

   public List getList() {
      return getCurrentGenerator().getObjects();
   }

   protected Placement getCurrentTool() {
      return getToolManager().getCurrentPlacement();
   }

   protected ListGenerator getCurrentGenerator() {
      Placement current = getCurrentTool();

      String generatorName = current.getPlacementConfig().getProperty(LIST_GEN_ID_TAG);

      if (generatorName == null) {
         generatorName = current.getTool().getMutableConfig().getProperty(LIST_GEN_ID_TAG);
      }

      return getListGenerator(generatorName);
   }

   public ListGenerator getListGenerator(String generatorName) {
      return (ListGenerator)getListGenerators().get(generatorName);
   }

   public ListConfig getCurrentConfig() {
      ListGenerator listGen = getCurrentGenerator();
      ListConfig currentConfig = loadCurrentConfig();

      if (currentConfig == null) {
         currentConfig = initConfig(listGen);
      }

      List columns = new ArrayList();
      List columnStringList = listGen.getColumns();
      List selected = currentConfig.getSelectedColumns();

      for (Iterator i=columnStringList.iterator();i.hasNext();) {
         String name = (String)i.next();
         Column column = new Column(name, selected.contains(name));
         columns.add(column);
      }

      currentConfig.setColumns(columns);
      return currentConfig;
   }

   private ListConfig initConfig(ListGenerator listGen) {
      ListConfig currentConfig = new ListConfig();
      currentConfig.setSelectedColumns(listGen.getDefaultColumns());
      currentConfig.setTitle(getCurrentTool().getTitle());
      currentConfig.setToolId(getIdManager().getId(
         getCurrentTool().getId()));
      currentConfig.setOwner(getAuthnManager().getAgent());

      return currentConfig;
   }

   public void saveOptions(ListConfig currentConfig) {
      List newSelected = new ArrayList();

      for (Iterator i = currentConfig.getColumns().iterator();i.hasNext();) {
         Column col = (Column)i.next();
         if (col.isSelected()) {
            newSelected.add(col.getName());
         }
      }

      currentConfig.setSelectedColumns(newSelected);
      getHibernateTemplate().saveOrUpdate(currentConfig);
   }

   public boolean isNewWindow(Object entry) {
      return getCurrentGenerator().isNewWindow(entry);
   }

   protected ListConfig loadCurrentConfig() {
      Agent currentAgent = getAuthnManager().getAgent();
      Id toolId = getIdManager().getId(getCurrentTool().getId());

      Collection configs =
         getHibernateTemplate().findByNamedQuery("loadCurrentConfig",
            new Object[]{currentAgent, toolId});

      if (configs.size() >= 1) {
         return (ListConfig)configs.iterator().next();
      }
      else {
         return null;
      }
   }
   public void register(String id, ListGenerator listGenerator)
   {
       listGenerators.put(id, listGenerator);
   }
   
   public List getSiteTypeList() {
      Placement current = getCurrentTool();

      String types = current.getPlacementConfig().getProperty(SITE_TYPE_LIST_TAG);
      if (types != null && types.length() > 0) {
         String siteTypes[] = types.split(",");
         return Arrays.asList(siteTypes);
      }
      return new ArrayList();
   }

   public Map getListGenerators() {
      return listGenerators;
   }

   public void setListGenerators(Map listGenerators) {
      this.listGenerators = listGenerators;
   }

   public AuthenticationManager getAuthnManager() {
      return authnManager;
   }

   public void setAuthnManager(AuthenticationManager authnManager) {
      this.authnManager = authnManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public ToolManager getToolManager() {
      return toolManager;
   }

   public void setToolManager(ToolManager toolManager) {
      this.toolManager = toolManager;
   }

}
