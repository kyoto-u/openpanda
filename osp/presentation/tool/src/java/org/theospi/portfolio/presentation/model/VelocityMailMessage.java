/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/model/VelocityMailMessage.java $
* $Id:VelocityMailMessage.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.presentation.model;

import java.util.Map;

import org.apache.velocity.exception.VelocityException;
import org.sakaiproject.metaobj.utils.mvc.impl.LocalVelocityConfigurer;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.ui.velocity.VelocityEngineUtils;

public class VelocityMailMessage extends SimpleMailMessage {
   private String template;
   private LocalVelocityConfigurer velocityConfigurer;

   public VelocityMailMessage() {
      super();
   }

   public VelocityMailMessage(SimpleMailMessage message) {
      super(message);
   }

   public String getTemplate() {
      return template;
   }

   public void setTemplate(String template) {
      this.template = template;
   }

   public void setModel(Map model) throws VelocityException {
     String result = null;
     result = VelocityEngineUtils.mergeTemplateIntoString(
                     getVelocityConfigurer().getVelocityEngine(),
                     getTemplate(), model);
      this.setText(result);
   }

   public LocalVelocityConfigurer getVelocityConfigurer() {
      return velocityConfigurer;
   }

   public void setVelocityConfigurer(LocalVelocityConfigurer velocityConfigurer) {
      this.velocityConfigurer = velocityConfigurer;
   }
}
