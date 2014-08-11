/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/InsertImageController.java $
* $Id:InsertImageController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.presentation.control;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.theospi.portfolio.presentation.model.TemplateFileRef;

public class InsertImageController extends AbstractPresentationController implements Controller{

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      PresentationTemplate template = (PresentationTemplate) requestModel;
      Map images = new HashMap();
      Hashtable model = new Hashtable();

      if (template.getId() != null){
         template = getPresentationManager().getPresentationTemplate(template.getId());
         for (Iterator i=template.getFiles().iterator();i.hasNext();){
            TemplateFileRef fileRef = (TemplateFileRef) i.next();
//          TODO: 20050810 ContentHosting
            //if (fileRef.getFileArtifact() instanceof FileArtifact){
            //   FileArtifact fileArtifact = (FileArtifact)fileRef.getFileArtifact();
            //   if (fileArtifact.getMimeType().getPrimaryType().equals("image")){
            //      images.put(fileArtifact.getDisplayName(),fileArtifact.getExternalUri());
            //   }
            //}
         }
      }

      model.put("images",images);
      return new ModelAndView("success", model);
   }

}

