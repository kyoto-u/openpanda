/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.0/matrix/api/src/java/org/theospi/portfolio/matrix/model/Level.java $
* $Id: Level.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
package org.theospi.portfolio.matrix.model;



/**
 * @author rpembry
 */
public class Level extends AbstractLabel {
   
   
   
   public Level() {}
   
   public Level(LevelTransport level) {
      this.id = level.getId();
      this.description = level.getDescription();
      this.color = level.getColor();
      this.textColor = level.getTextColor();
   }
   
   public Level copy(LevelTransport level) {
      this.id = level.getId();
      this.description = level.getDescription();
      this.color = level.getColor();
      this.textColor = level.getTextColor();
      return this;
   }
}
