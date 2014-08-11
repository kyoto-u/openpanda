/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api/src/java/org/theospi/portfolio/worksite/mgt/impl/ToolConfigurationUserType.java $
* $Id:ToolConfigurationUserType.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.worksite.mgt.impl;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.site.api.ToolConfiguration;
import org.theospi.portfolio.worksite.model.ToolConfigurationWrapper;

public class ToolConfigurationUserType implements UserType, Serializable {
   private static final int ID_COLUMN = 0;

   public int[] sqlTypes() {
      return new int[] {
         Types.VARCHAR};
   }

   public Class returnedClass() {
      return ToolConfiguration.class;
   }

   public boolean equals(Object x, Object y) throws HibernateException {
      return (x == y) || (x != null && y != null && x.equals(y));
   }

   public Object nullSafeGet(ResultSet resultSet, String[] columns, Object o) throws HibernateException, SQLException {
      String idValue = resultSet.getString(columns[ID_COLUMN]);
      if (resultSet.wasNull()) {
         return null;
      }
      ToolConfiguration tool = getWorksiteManager().getTool(idValue);
      if (tool == null) return null;
      return new ToolConfigurationWrapper(tool) ;
   }

   public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index) throws HibernateException, SQLException {
      ToolConfiguration toolConfiguration = (ToolConfiguration) value;
      if (toolConfiguration == null || toolConfiguration.getId() == null) {
         preparedStatement.setNull(index, Types.VARCHAR);
      } else {
         preparedStatement.setString(index, toolConfiguration.getId());
      }
   }

   public Object deepCopy(Object o) throws HibernateException {
      return o;
   }

   public boolean isMutable() {
      return false;
   }

   protected WorksiteManager getWorksiteManager(){
      return (WorksiteManager) ComponentManager.getInstance().get(WorksiteManager.class.getName());
   }

   public int hashCode(Object arg0) throws HibernateException {
      // TODO Auto-generated method stub
      return 0;
   }

   public Serializable disassemble(Object arg0) throws HibernateException {
      // TODO Auto-generated method stub
      return null;
   }

   public Object assemble(Serializable arg0, Object arg1) throws HibernateException {
      // TODO Auto-generated method stub
      return null;
   }

   public Object replace(Object arg0, Object arg1, Object arg2) throws HibernateException {
      // TODO Auto-generated method stub
      return null;
   }
}
