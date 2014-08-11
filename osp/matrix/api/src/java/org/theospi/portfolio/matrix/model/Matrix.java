/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/matrix/api/src/java/org/theospi/portfolio/matrix/model/Matrix.java $
* $Id:Matrix.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.matrix.model;


import java.util.HashSet;
import java.util.Set;

import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.IdentifiableObject;

/**
 * @author rpembry
 */
public class Matrix extends IdentifiableObject implements Artifact {

   private Id id;
   private Agent owner;
   private Scaffolding scaffolding;
   private Set cells = new HashSet();
   private ReadableObjectHome home;


   /**
    * @return Returns the cells.
    */
   public Set getCells() {
      return cells;
   }

   /**
    * @param cells The cells to set.
    */
   public void setCells(Set cells) {
      this.cells = cells;
   }

   /**
    * @return Returns the id.
    */
   public Id getId() {
      return id;
   }

   /**
    * @param id The id to set.
    */
   public void setId(Id id) {
      this.id = id;
   }


   public void add(Cell cell) {
      this.getCells().add(cell);
      cell.setMatrix(this);
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || !(other instanceof Matrix)) return false;
      //TODO need better equals method
      return (this.getId().equals(((Matrix) other).getId()));

   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   public int hashCode() {
      //TODO need better hashcode
      Id id = this.getId();
      if (id == null) return 0;
      return id.getValue().hashCode();
   }

/* (non-Javadoc)
 * @see org.theospi.portfolio.shared.model.Artifact#getOwner()
 */
   public Agent getOwner() {
      return owner;
   }
   
   public void setOwner(Agent owner) {
      this.owner = owner;
   }

/* (non-Javadoc)
 * @see org.theospi.portfolio.shared.model.Artifact#getHome()
 */
   public ReadableObjectHome getHome() {
      return home;
   }

/* (non-Javadoc)
 * @see org.theospi.portfolio.shared.model.Artifact#setHome(org.theospi.portfolio.shared.mgt.ReadableObjectHome)
 */
   public void setHome(ReadableObjectHome home) {
      this.home = home;

   }

/* (non-Javadoc)
 * @see org.theospi.portfolio.shared.model.Artifact#getDisplayName()
 */
   public String getDisplayName() {
      return scaffolding.getTitle();
   }

   public Scaffolding getScaffolding() {
      return scaffolding;
   }

   public void setScaffolding(Scaffolding scaffolding) {
      this.scaffolding = scaffolding;
   }
}
