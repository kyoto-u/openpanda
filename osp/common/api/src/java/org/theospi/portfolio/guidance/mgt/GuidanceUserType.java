/*
 * *********************************************************************************
 *  $URL: https://source.sakaiproject.org/svn/content/trunk/content-api/api/src/java/org/sakaiproject/content/api/ContentCollection.java $
 *  $Id: ContentCollection.java 8537 2006-05-01 02:13:28Z jimeng@umich.edu $
 * **********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2008 The Sakai Foundation
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
 * *********************************************************************************
 *
 */

package org.theospi.portfolio.guidance.mgt;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.sakaiproject.component.cover.ComponentManager;
import org.theospi.portfolio.guidance.model.Guidance;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 29, 2006
 * Time: 8:52:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class GuidanceUserType implements UserType {

   public int[] sqlTypes() {
      return new int[]{Types.VARCHAR};
   }

   public Class returnedClass() {
      return Guidance.class;
   }

   public boolean equals(Object x, Object y) throws HibernateException {
      return (x == y) || (x != null && y != null && x.equals(y));
   }

   public int hashCode(Object object) throws HibernateException {
      return ((Guidance)object).getId().hashCode();
   }

   public Object nullSafeGet(ResultSet resultSet, String[] strings, Object object) throws HibernateException, SQLException {
      String result = resultSet.getString(strings[0]);
      if (result == null) {
         return null;
      }

      return getGuidanceManager().getGuidance(result, false);
   }

   public void nullSafeSet(PreparedStatement st, Object object, int index) throws HibernateException, SQLException {
      Guidance guidance = (Guidance) object;
      if (guidance == null || guidance.getId() == null) {
         st.setNull(index, Types.VARCHAR);
      }
      else {
         st.setString(index, guidance.getId().getValue());
      }
   }

   public Object deepCopy(Object object) throws HibernateException {
      return object;
   }

   public boolean isMutable() {
      return false;
   }

   public Object assemble(Serializable cached, Object owner)
         throws HibernateException {
      if (cached==null) {
         return null;
      }
      else {
         Guidance guidance = (Guidance) cached;
         getGuidanceManager().assureAccess(guidance);
         return guidance;
      }
   }

   public Serializable disassemble(Object value) throws HibernateException {
      if (value==null) {
         return null;
      }
      else {
         return (Serializable) deepCopy(value);
      }
   }

   public Object replace(Object original, Object target, Object owner)
         throws HibernateException {
      return original;
   }

   public GuidanceManager getGuidanceManager() {
      return (GuidanceManager) ComponentManager.getInstance().get("org.theospi.portfolio.guidance.mgt.GuidanceManager");
   }

}
