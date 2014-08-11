/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-2.9.2/metaobj-impl/api-impl/src/java/org/sakaiproject/metaobj/shared/model/impl/HibernateStructuredArtifact.java $
 * $Id: HibernateStructuredArtifact.java 121316 2013-03-16 17:47:26Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.shared.model.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.WritableObjectHome;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;

public class HibernateStructuredArtifact extends StructuredArtifact implements UserType {
   protected final Log logger = LogFactory.getLog(getClass());
   private final static int[] SQL_TYPES = new int[]{Types.BLOB};

   public HibernateStructuredArtifact() {
   }

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
   public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
         throws HibernateException, SQLException {
      byte[] bytes = rs.getBytes(names[0]);
      if (rs.wasNull()) {
         return null;
      }

      //TODO figure out how to inject this
      HomeFactory homeFactory = (HomeFactory) ComponentManager.getInstance().get("xmlHomeFactory");
      WritableObjectHome home = (WritableObjectHome) homeFactory.getHome("agent");

      StructuredArtifact artifact = (StructuredArtifact) home.createInstance();
      ByteArrayInputStream in = new ByteArrayInputStream(bytes);
      SAXBuilder saxBuilder = new SAXBuilder();
      saxBuilder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // SAK-23245
      try {
         Document doc = saxBuilder.build(in);
         artifact.setBaseElement(doc.getRootElement());
      }
      catch (JDOMException e) {
         throw new HibernateException(e);
      }
      catch (IOException e) {
         throw new HibernateException(e);
      }
      return artifact;
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
         StructuredArtifact artifact = (StructuredArtifact) value;
         Document doc = new Document();
         Element rootElement = artifact.getBaseElement();
         rootElement.detach();
         doc.setRootElement(rootElement);
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
      return StructuredArtifact.class;
   }

	public Object assemble(Serializable arg0, Object arg1)
			throws HibernateException {
		return null;
	}

	public Serializable disassemble(Object arg0) throws HibernateException {
		return null;
	}

	public int hashCode(Object arg0) throws HibernateException {
		return arg0.hashCode();
	}

	public Object replace(Object arg0, Object arg1, Object arg2)
			throws HibernateException {
		return arg0;
	}

}
