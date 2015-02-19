/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/model/IdImpl.java $
 * $Id: IdImpl.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.shared.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author rpembry
 */
public class IdImpl implements Id {
   static final long serialVersionUID = 5143985783577804880L;

   protected final transient Log logger = LogFactory.getLog(IdImpl.class);

   private String id;
   //TODO: support Type better
   private transient Type type;

   public IdImpl() {
   }

   public IdImpl(String id, Type type) {
      this.id = id;
      this.type = type;
   }

   /* (non-Javadoc)
    * @see org.sakaiproject.metaobj.shared.model.Id#getType()
    */
   public Type getType() {
      return type;
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.writeObject(id);
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      id = (String) in.readObject();
   }

   public String getValue() {
      return id;
   }

   public String toString() {
      return getValue();
   }

   public boolean equals(Object other) {
      if (other == null || !(other instanceof IdImpl)) {
         return false;
      }
      return getValue().equals(((IdImpl) other).getValue());

   }


   public void setValue(String id) {
      this.id = id;
   }


   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   public int hashCode() {
      if (id == null) {
         return 0;
      }
      return this.id.hashCode();
   }
}
