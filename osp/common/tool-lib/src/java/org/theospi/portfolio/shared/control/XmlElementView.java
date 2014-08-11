/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/tool-lib/src/java/org/theospi/portfolio/shared/control/XmlElementView.java $
* $Id:XmlElementView.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.shared.control;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.sakaiproject.metaobj.shared.control.EditedArtifactStorage;
import org.sakaiproject.metaobj.shared.control.SchemaBean;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactHomeInterface;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;
import org.sakaiproject.metaobj.utils.mvc.impl.TemplateJstlView;
import org.sakaiproject.metaobj.utils.mvc.intf.VelocityEngineFactory;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContextException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Apr 20, 2004
 * Time: 3:32:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlElementView extends TemplateJstlView {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private String templateName = "";
   private VelocityEngine velocityEngine;
   private Template template;
   private String baseUrl = null;

   public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
      setBody(prepareTemplateForRendering(model, request, response));
      super.render(model, request, response);
   }

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
   protected String prepareTemplateForRendering(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
      HomeFactory factory = (HomeFactory) getWebApplicationContext().getBean("homeFactory");

      SchemaNode schema = null;
      StructuredArtifactHomeInterface home = null;
      String schemaName = null;
      schemaName = request.getParameter("schema");
      if (schemaName == null) {

         schemaName = (String) request.getAttribute("schema");

         if (schemaName == null) {
            schemaName = request.getParameter("artifactType");
         }
      }

      home = (StructuredArtifactHomeInterface) factory.getHome(schemaName);

      if (request.getAttribute(EditedArtifactStorage.STORED_ARTIFACT_FLAG) != null) {
         EditedArtifactStorage sessionBean = (EditedArtifactStorage)request.getSession().getAttribute(
            EditedArtifactStorage.EDITED_ARTIFACT_STORAGE_SESSION_KEY);

         if (!(sessionBean.getCurrentElement() instanceof StructuredArtifact)) {
            home = (StructuredArtifactHomeInterface)sessionBean.getHome();
            schema = sessionBean.getCurrentSchemaNode();
            schemaName += "." + sessionBean.getCurrentPath();
         }
      }

      String baseFile = getBaseUrl() + "_" + schemaName;

      File genFile = new File(getWebApplicationContext().getServletContext().getRealPath(""),
         baseFile + ".jsp");
      File customFile = new File(getWebApplicationContext().getServletContext().getRealPath(""),
         baseFile + "_custom.jsp");

      if (customFile.exists()) {
         return baseFile + "_custom.jsp";
      }

      if (genFile.exists()) {
         if (genFile.lastModified() > home.getModified().getTime() &&
            genFile.lastModified() > getVelocityTemplate().getLastModified()) {
            return baseFile + ".jsp";
         }
      }

      return createJspFromTemplate(home, baseFile + ".jsp", genFile, schemaName, schema);
   }


   protected String createJspFromTemplate(StructuredArtifactHomeInterface home, String resultFile, File jspFile,
                                          String schemaName, SchemaNode schema) throws Exception {
      VelocityContext context = new VelocityContext();

      if (schema != null) {
         context.put("schema", new SchemaBean(schema, home.getRootNode(), null, home.getType().getDescription()));
      }
      else {
         context.put("schema", new SchemaBean(home.getRootNode(), home.getSchema(), schemaName, home.getType().getDescription()));
      }

      context.put("instruction", home.getInstruction());

      FileWriter output = null;
      
      try {
         output = new FileWriter(jspFile);

         getVelocityTemplate().merge(context, output);

      } finally {
         try {
            output.close();
         } catch (Exception e) {
            logger.warn("Could not clean up resource", e);
         }
      }

      return resultFile;
   }


   /**
    * Invoked on startup. Looks for a single VelocityConfig bean to
    * find the relevant VelocityEngine for this factory.
    */
   protected void initApplicationContext() throws BeansException {
      super.initApplicationContext();

      if (this.velocityEngine == null) {
         try {
            VelocityEngineFactory velocityConfig = (VelocityEngineFactory)
               BeanFactoryUtils.beanOfTypeIncludingAncestors(getApplicationContext(),
                  VelocityEngineFactory.class, true, true);
            this.velocityEngine = velocityConfig.getVelocityEngine();
         } catch (NoSuchBeanDefinitionException ex) {
            throw new ApplicationContextException("Must define a single VelocityConfig bean in this web application " +
               "context (may be inherited): VelocityConfigurer is the usual implementation. " +
               "This bean may be given any name.", ex);
         }
      }

      try {
         // check that we can get the template, even if we might subsequently get it again
         this.template = getVelocityTemplate();
      } catch (ResourceNotFoundException ex) {
         throw new ApplicationContextException("Cannot find Velocity template for URL [" + getBaseUrl() +
            "]: Did you specify the correct resource loader path?", ex);
      } catch (Exception ex) {
         throw new ApplicationContextException("Cannot load Velocity template for URL [" + getBaseUrl() + "]", ex);
      }
   }

   /**
    * Retrieve the Velocity template.
    *
    * @return the Velocity template to process
    * @throws Exception if thrown by Velocity
    */
   protected Template getVelocityTemplate() throws Exception {
      return this.velocityEngine.getTemplate(templateName);
   }


   public String getTemplateName() {
      return templateName;
   }

   public void setTemplateName(String templateName) {
      this.templateName = templateName;
   }

   public String getBaseUrl() {
      return baseUrl;
   }

   public void setBaseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
   }
}
