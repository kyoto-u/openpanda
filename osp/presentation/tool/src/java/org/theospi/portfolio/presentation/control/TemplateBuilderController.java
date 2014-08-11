/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/TemplateBuilderController.java $
* $Id:TemplateBuilderController.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.mgt.ReadableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.WritableObjectHome;
import org.sakaiproject.metaobj.shared.mgt.home.StructuredArtifactHomeInterface;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.model.PresentationItemDefinition;
import org.theospi.portfolio.presentation.model.PresentationTemplate;
import org.theospi.portfolio.presentation.model.TemplateFileRef;

public class TemplateBuilderController extends AbstractPresentationController implements LoadObjectController{
   private WritableObjectHome fileArtifactHome;
   private HomeFactory homeFactory;

   public ModelAndView handleRequest(Object requestModel, Map request, Map session, Map application, Errors errors) {
      PresentationTemplate template = (PresentationTemplate) requestModel;
      try {
//    	TODO: 20050810 ContentHosting
         //FileArtifact artifact = (FileArtifact) getFileArtifactHome().load(template.getRenderer());
         //artifact.setSize(template.getMarkup().getBytes().length);
         //artifact.setFile(new ByteArrayInputStream(prepareBody(template.getMarkup()).getBytes()));
         //getFileArtifactHome().store(artifact);
         getPresentationManager().storeTemplate(template);
         Map params = new HashMap();
         params.put("id", template.getId().getValue());
         params.put("_target1","1");
         params.put("formSubmission","true");
         return new ModelAndView("success", params);
      } catch (PersistenceException e) {
         //TODO is this right ?
         errors.reject("markup", e.getMessage());
         return new ModelAndView("failure");
      }
   }

    public ModelAndView processCancel(Map request, Map session, Map application,
                                      Object command, Errors errors) throws Exception {
        PresentationTemplate template = (PresentationTemplate) command;
        Map params = new HashMap();
        params.put("id", template.getId().getValue());
        params.put("_target1","1");
        params.put("formSubmission","true");
        return new ModelAndView("success", params);
    }

   protected String prepareBody(String body){
      StringBuilder buffer = new StringBuilder();
      buffer.append("<?xml version=\"1.0\" ?>\n" +
            "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n" +
            "\t\n\t<xsl:template match=\"ospiPresentation\">");
      buffer.append(body.replaceAll("\\$\\{(.*?)\\}","<xsl:copy-of select=\"$1\"/>"));
      buffer.append("\t</xsl:template>\n\n</xsl:stylesheet>");
      return buffer.toString();
   }

   public Object fillBackingObject(Object incomingModel, Map request, Map session, Map application) throws Exception {
      PresentationTemplate template = (PresentationTemplate) incomingModel;
      template = getPresentationManager().getPresentationTemplate(template.getId());

      Map elements = new HashMap();

      for (Iterator i=template.getSortedItems().iterator();i.hasNext();){
         PresentationItemDefinition itemDef = (PresentationItemDefinition) i.next();
         elements.put(itemDef.getName(),findPaths(itemDef));
      }

      request.put("elements",elements);

      Map images = new HashMap();

      for (Iterator i=template.getFiles().iterator();i.hasNext();){
         TemplateFileRef fileRef = (TemplateFileRef) i.next();
//       TODO: 20050810 ContentHosting
         //if (fileRef.getFileArtifact() instanceof FileArtifact){
         //   FileArtifact fileArtifact = (FileArtifact)fileRef.getFileArtifact();
         //   if (fileArtifact.getMimeType().getPrimaryType().equals("image")){
         //      images.put(fileArtifact.getDisplayName(),fileArtifact.getExternalUri());
         //   }
         //}
      }

      session.put("images",images);

      return template;
   }

   /**
    * places ${ } around each path
    * @param paths
    * @return
    */
   protected Collection tagPaths(Collection paths){
      Collection taggedPaths = new ArrayList();
      for (Iterator i=paths.iterator();i.hasNext();){
         taggedPaths.add("${" + i.next() + "}");
      }
      return taggedPaths;
   }

   /**
    * creates collection of possible xpaths associated with the rendered xml for this item definition
    * @param itemDef
    * @return
    */
   protected Collection findPaths(PresentationItemDefinition itemDef){
      ReadableObjectHome home = getHomeFactory().getHome(itemDef.getType());
      Collection paths = new ArrayList();

      paths.add(itemDef.getName() + "/artifact/metaData/id");
      paths.add(itemDef.getName() + "/artifact/metaData/displayName");
      paths.add(itemDef.getName() + "/artifact/metaData/type/id");
      paths.add(itemDef.getName() + "/artifact/metaData/type/description");

      if (home instanceof StructuredArtifactHomeInterface){
         StructuredArtifactHomeInterface structuredArtifactHome = (StructuredArtifactHomeInterface) home;
         addPath(paths, structuredArtifactHome.getRootSchema(),itemDef.getName() + "/artifact/structuredData");
      } else if (home != null && home.getType().getId().equals(getFileArtifactHome().getType().getId())){
         //TODO deal with technical metadata
         paths.add(itemDef.getName() + "/artifact/fileArtifact/uri");
      }
      return tagPaths(paths);
   }

   /**
    * recursively finds all possible xpaths for given schema
    * @param paths
    * @param node
    * @param parent
    */
   protected void addPath(Collection paths, SchemaNode node, String parent){
      String path = parent + "/" + node.getName();
      paths.add(path);
      if (node.getChildren() != null && node.getChildren().size() > 0){
         for (Iterator i= node.getChildren().iterator();i.hasNext();){
            addPath(paths, (SchemaNode)i.next(), path);
         }
      }
   }

   /**
    * loads contents of a file into a string
    * @param fileId
    * @return
    */
   protected String loadContents(Id fileId) throws IOException, PersistenceException {
//	 TODO: 20050810 ContentHosting
	   //FileArtifact artifact = (FileArtifact)getFileArtifactHome().load(fileId);
      //BufferedReader reader = new BufferedReader(new InputStreamReader(artifact.getFile()));
      StringBuilder buffer = new StringBuilder();
      //String line;
      //while ((line = reader.readLine()) != null){
      //   if (line == null) break;
      //   buffer.append(line + "\n");
      //}
      return buffer.toString();
   }

   public WritableObjectHome getFileArtifactHome() {
      return fileArtifactHome;
   }

   public void setFileArtifactHome(WritableObjectHome fileArtifactHome) {
      this.fileArtifactHome = fileArtifactHome;
   }

   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }
}

