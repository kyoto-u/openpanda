/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/shared/model/impl/HibernateSchemaNode.java $
 * $Id: HibernateSchemaNode.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.shared.model.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;
import org.sakaiproject.metaobj.utils.xml.SchemaFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.sakaiproject.metaobj.utils.xml.impl.SchemaNodeImpl;

public class HibernateSchemaNode implements UserType {


   protected final Log logger = LogFactory.getLog(getClass());
   private final static int[] SQL_TYPES = new int[]{Types.BLOB};


   public int[] sqlTypes() {
      return SQL_TYPES;
   }

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
   public Object nullSafeGet(ResultSet rs, String[] names, Object object)
         throws HibernateException, SQLException {
      InputStream in = rs.getBinaryStream(names[0]);
      if (rs.wasNull()) {
         return null;
      }

      SchemaFactory schemaFactory = SchemaFactory.getInstance();
      return schemaFactory.getSchema(in);
   }

   /* (non-Javadoc)
    * @see org.hibernate.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
    */
   public void nullSafeSet(PreparedStatement st, Object value, int index)
         throws HibernateException, SQLException {
      if (value == null) {
         st.setNull(index, Types.VARBINARY);
      }
      else {
         SchemaNode schemaNode = (SchemaNode) value;
         Document doc = schemaNode.getSchemaElement().getDocument();

         ByteArrayOutputStream out = new ByteArrayOutputStream();
         XMLOutputter xmlOutputter = new XMLOutputter();
         try {
            xmlOutputter.output(doc, out);
         }
         catch (IOException e) {
            throw new HibernateException(e);
         }
         st.setBytes(index, out.toByteArray());
      }
   }

   /* (non-Javadoc)
    * @see org.hibernate.UserType#returnedClass()
    */
   public Class returnedClass() {
      return SchemaNodeImpl.class;
   }

	public Object assemble(Serializable arg0, Object arg1)
			throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	public Serializable disassemble(Object arg0) throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	public int hashCode(Object o) throws HibernateException {
		return o.hashCode();
	}

	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

}
