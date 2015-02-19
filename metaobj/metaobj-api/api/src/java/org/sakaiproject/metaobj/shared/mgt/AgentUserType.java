/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/mgt/AgentUserType.java $
 * $Id: AgentUserType.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
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

package org.sakaiproject.metaobj.shared.mgt;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;

public class AgentUserType implements UserType {

   public int[] sqlTypes() {
      return new int[]{Types.VARCHAR};
   }

   public Class returnedClass() {
      return Agent.class;
   }

   public boolean equals(Object x, Object y) throws HibernateException {
      return (x == y) || (x != null && y != null && x.equals(y));
   }

   public Object nullSafeGet(ResultSet resultSet, String[] names, Object o) throws HibernateException, SQLException {
      String result = resultSet.getString(names[0]);
      if (result == null) {
         return null;
      }

      Id agentId = getIdManager().getId(result);
      Agent agent = getAgentManager().getAgent(agentId);
      return agent;
   }

   public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
      Agent agent = (Agent) value;
      if (value == null || agent.getId() == null) {
         st.setNull(index, Types.VARCHAR);
      }
      else {
         st.setString(index, agent.getId().getValue());
      }
   }

   public Object deepCopy(Object o) throws HibernateException {
      return o;
   }

   public boolean isMutable() {
      return false;
   }

   public AgentManager getAgentManager() {
      return (AgentManager) ComponentManager.getInstance().get("agentManager");
   }

   public IdManager getIdManager() {
      return (IdManager) ComponentManager.getInstance().get("idManager");
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

	public int hashCode(Object o) throws HibernateException {
		Agent agent = (Agent) o;
		return agent.getId().getValue().hashCode();
	}

	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

}
