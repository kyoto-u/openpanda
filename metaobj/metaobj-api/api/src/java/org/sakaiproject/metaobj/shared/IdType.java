/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/IdType.java $
 * $Id: IdType.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.shared;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.sakaiproject.metaobj.shared.model.IdImpl;

/**
 * @author rpembry
 *         <p/>
 *         (based on http://www.hibernate.org/50.html)
 */
public class IdType implements UserType {
   protected final org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory
         .getLog(getClass());

   private final static int[] SQL_TYPES = new int[]{Types.VARCHAR};


   /* (non-Javadoc)
    * @see org.hibernate.UserType#deepCopy(java.lang.Object)
    */
   public Object deepCopy(Object arg0) throws HibernateException {
      return arg0;
   }

   /* (non-Javadoc)
    * @see org.hibernate.UserType#equals(java.lang.Object, java.lang.Object)
    */
   public boolean equals(Object x, Object y) throws HibernateException {
      return (x == y) || (x != null && y != null && x.equals(y));
   }

   /* (non-Javadoc)
    * @see org.hibernate.UserType#isMutable()
    */
   public boolean isMutable() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.hibernate.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)
    */
   public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
         throws HibernateException, SQLException {
      String value;
      try {
         value = rs.getString(names[0]);
      }
      catch (SQLException e) {
         logger.error("Stmt: " + rs.getStatement().toString(), e);
         throw e;
      }
      if (rs.wasNull()) {
         return null;
      }
      return new IdImpl(value, null);
   }

   /* (non-Javadoc)
    * @see org.hibernate.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
    */
   public void nullSafeSet(PreparedStatement st, Object value, int index)
         throws HibernateException, SQLException {
      if (value == null) {
         st.setNull(index, Types.VARCHAR);
      }
      else {
         st.setString(index, ((IdImpl) value).getValue());
      }

   }

   /* (non-Javadoc)
    * @see org.hibernate.UserType#returnedClass()
    */
   public Class returnedClass() {
      return IdImpl.class;
   }

   /* (non-Javadoc)
    * @see org.hibernate.UserType#sqlTypes()
    */
   public int[] sqlTypes() {
      return SQL_TYPES;
   }

	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
      if (cached==null) {
         return null;
      }
      else {
         return deepCopy(cached);
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

	public int hashCode(Object value) throws HibernateException {
		return value.hashCode();
	}

	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

}
