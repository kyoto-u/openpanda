/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/PresentationPropertyFileView.java $
* $Id:PresentationPropertyFileView.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.sakaiproject.metaobj.shared.control.SchemaBean;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.shared.control.XmlElementView;
import org.theospi.portfolio.shared.model.TechnicalMetadata;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 20, 2004
 * Time: 3:32:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class PresentationPropertyFileView extends XmlElementView {
   protected final transient Log logger = LogFactory.getLog(getClass());

   /**
    * Prepare for rendering, and determine the request dispatcher path
    * to forward to respectively to include.
    * <p>This implementation simply returns the configured URL.
    * Subclasses can override this to determine a resource to render,
    * typically interpreting the URL in a different manner.
    *
    * @param request  current HTTP request
    * @param response current HTTP response
    * @return the request dispatcher path to use
    * @throws Exception if preparations failed
    * @see #getUrl
    * @see org.springframework.web.servlet.view.tiles.TilesView#prepareForRendering
    */
   protected String prepareTemplateForRendering(Map model,HttpServletRequest request, HttpServletResponse response) throws Exception {
      Presentation presentation = (Presentation) model.get("presentation");
      TechnicalMetadata propertyFileMetadata = (TechnicalMetadata) model.get("propertyFileMetadata");

      String baseFile = getBaseUrl() + "_" + presentation.getTemplate().getId().toString();

      File genFile = new File(getWebApplicationContext().getServletContext().getRealPath(""),
         baseFile + ".jsp");
      File customFile = new File(getWebApplicationContext().getServletContext().getRealPath(""),
         baseFile + "_custom.jsp");

      if (customFile.exists()) {
         return baseFile + "_custom.jsp";
      }

      //update jsp only if xsd or velocity template has changed or genFile doesn't exist
      if (!genFile.exists() ||
            (genFile.lastModified() > propertyFileMetadata.getLastModified().getTime() &&
            genFile.lastModified() > getVelocityTemplate().getLastModified())) {
         return createJspFromTemplate(presentation, baseFile + ".jsp", genFile);
      }

      return baseFile + ".jsp";
   }


   protected String createJspFromTemplate(Presentation presentation, String resultFile,
                                          File jspFile) throws Exception {
      VelocityContext context = new VelocityContext();

      SchemaNode schema = presentation.getProperties().getCurrentSchema();
      context.put("schema",
         new SchemaBean(schema, presentation.getTemplate().getDocumentRoot(), null, presentation.getTemplate().getDescription()));

      FileWriter output = null;

      try {
         output = new FileWriter(jspFile);

         getVelocityTemplate().merge(context, output);

      } finally {
         try {
            output.close();
         }
         catch (Exception e) {
            logger.warn("Error cleaning up resources: ", e);
            throw e;
         }
      }

      return resultFile;
   }
}
