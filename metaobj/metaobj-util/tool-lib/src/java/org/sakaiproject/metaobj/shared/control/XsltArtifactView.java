/*******************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/XsltArtifactView.java $
 * $Id: XsltArtifactView.java 123167 2013-04-23 18:26:31Z chmaurer@iupui.edu $
 * **********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008, 2009 The Sakai Foundation
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
 ******************************************************************************/

package org.sakaiproject.metaobj.shared.control;

import org.springframework.web.servlet.view.xslt.AbstractXsltView;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.FieldError;
import org.springframework.validation.BindingResultUtils;
import org.jdom.transform.JDOMSource;
import org.jdom.Element;
import org.jdom.Document;
import org.sakaiproject.content.api.ResourceEditingHelper;
import org.sakaiproject.content.api.ResourceToolAction;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.model.Artifact;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.FormHelper;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.util.Web;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.Map.Entry;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Oct 30, 2006
 * Time: 10:10:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class XsltArtifactView extends AbstractXsltView {

   private ResourceLoader resourceLoader = new ResourceLoader();
   private String bundleLocation;
   private static final String STYLESHEET_PARAMS =
      "org.sakaiproject.metaobj.shared.control.XsltArtifactView.paramsMap";
   private static final String STYLESHEET_LOCATION =
      "org.sakaiproject.metaobj.shared.control.XsltArtifactView.stylesheetLocation";
   private String uriResolverBeanName;
   private URIResolver uriResolver;
   private boolean readOnly;

   // These parameter names are reserved. Anything not on this list may be passed
   // as an HTTP query parameter to pass through from the browser to the stylesheet.
   protected static final List<String> reservedParams = Collections.unmodifiableList(Arrays.asList(
         "preview", "fromResources", "edit", "panelId", "subForm",
         FormHelper.XSL_SITE_ID, FormHelper.XSL_PRESENTATION_TYPE,
         FormHelper.XSL_PRESENTATION_ID, FormHelper.XSL_PRESENTATION_ITEM_ID,
         FormHelper.XSL_PRESENTATION_ITEM_NAME, FormHelper.XSL_FORM_TYPE,
         FormHelper.XSL_ARTIFACT_ID, FormHelper.XSL_ARTIFACT_REFERENCE,
         FormHelper.XSL_OBJECT_ID, FormHelper.XSL_OBJECT_TITLE, FormHelper.XSL_WIZARD_PAGE_ID
   ));

   protected Source createXsltSource(Map map, String string, HttpServletRequest httpServletRequest,
                                     HttpServletResponse httpServletResponse) throws Exception {

      httpServletResponse.setContentType(getContentType());
      WebApplicationContext context = getWebApplicationContext();
      setUriResolver((URIResolver)context.getBean(uriResolverBeanName));

      ToolSession toolSession = SessionManager.getCurrentToolSession();

      String homeType = null;

      ElementBean bean = (ElementBean) map.get("bean");

      Element root = null;
      Map paramsMap = new Hashtable();

      for (Enumeration e = httpServletRequest.getParameterNames(); e.hasMoreElements(); ) {
         String k = e.nextElement().toString();
         // Do not allow reserved parameter names to be overwritten
         if (!reservedParams.contains(k)) {
            paramsMap.put(k, httpServletRequest.getParameter(k));
         }
      }

      httpServletRequest.setAttribute(STYLESHEET_PARAMS, paramsMap);
      if (toolSession.getAttribute(FormHelper.PREVIEW_HOME_TAG) != null) {
         paramsMap.put("preview", "true");
      }

      if (toolSession.getAttribute(ResourceToolAction.ACTION_PIPE) != null) {
         paramsMap.put("fromResources", "true");
      }
      
      if (httpServletRequest.getAttribute(FormHelper.URL_DECORATION) != null) {
          paramsMap.put("urlDecoration", httpServletRequest.getAttribute(FormHelper.URL_DECORATION));
       }

      HashMap<String, String> xslParams = new HashMap<String, String>();
      xslParams.put(FormHelper.XSL_PRESENTATION_ID,        FormHelper.PRESENTATION_ID);
      xslParams.put(FormHelper.XSL_PRESENTATION_TYPE,      FormHelper.PRESENTATION_TEMPLATE_ID);
      xslParams.put(FormHelper.XSL_PRESENTATION_ITEM_ID,   FormHelper.PRESENTATION_ITEM_DEF_ID);
      xslParams.put(FormHelper.XSL_PRESENTATION_ITEM_NAME, FormHelper.PRESENTATION_ITEM_DEF_NAME);
      xslParams.put(FormHelper.XSL_FORM_TYPE,              ResourceEditingHelper.CREATE_SUB_TYPE);
      xslParams.put(FormHelper.XSL_ARTIFACT_REFERENCE,     ResourceEditingHelper.ATTACHMENT_ID);
      xslParams.put(FormHelper.XSL_OBJECT_ID,              FormHelper.XSL_OBJECT_ID);
      xslParams.put(FormHelper.XSL_OBJECT_TITLE,           FormHelper.XSL_OBJECT_TITLE);
      xslParams.put(FormHelper.XSL_WIZARD_PAGE_ID,         FormHelper.XSL_WIZARD_PAGE_ID);

      // Load up our XSL parameters according to the mapping into the tool session above.
      // Note that this is not always one-to-one due to some string/key inconsistencies around the tools.
      for (Entry<String, String> item : xslParams.entrySet()) {
         Object val = toolSession.getAttribute(item.getValue());
         if (val != null) {
            paramsMap.put(item.getKey(), val);
         }
      }

      Id id = null;

      if (bean instanceof Artifact) {
         root = getStructuredArtifactDefinitionManager().createFormViewXml(
            (Artifact) bean, null);
         homeType = getHomeType((Artifact) bean);
         id = ((Artifact) bean).getId();
      }
      else {
         EditedArtifactStorage sessionBean = (EditedArtifactStorage)httpServletRequest.getSession().getAttribute(
            EditedArtifactStorage.EDITED_ARTIFACT_STORAGE_SESSION_KEY);

         if ( sessionBean != null ) {
            root = getStructuredArtifactDefinitionManager().createFormViewXml(
                                         (Artifact) sessionBean.getRootArtifact(), null);
            
            replaceNodes(root, bean, sessionBean);
            paramsMap.put("subForm", "true");
            homeType = getHomeType(sessionBean.getRootArtifact());
            id = sessionBean.getRootArtifact().getId();
         }
         else {
            return new javax.xml.transform.dom.DOMSource();
         }
      }

      if (id != null) {
         paramsMap.put("edit", "true");
         paramsMap.put(FormHelper.XSL_ARTIFACT_ID, id.getValue());
      }

      httpServletRequest.setAttribute(STYLESHEET_LOCATION,
         getStructuredArtifactDefinitionManager().getTransformer(homeType, readOnly));

      Errors errors = BindingResultUtils.getBindingResult(map, "bean");
      if (errors != null && errors.hasErrors()) {
         Element errorsElement = new Element("errors");

         List errorsList = errors.getAllErrors();

         for (Iterator i=errorsList.iterator();i.hasNext();) {
            Element errorElement = new Element("error");
            ObjectError error = (ObjectError) i.next();
            if (error instanceof FieldError) {
               FieldError fieldError = (FieldError) error;
               errorElement.setAttribute("field", fieldError.getField());
               Element rejectedValue = new Element("rejectedValue");
               if (fieldError.getRejectedValue() != null) {
                  rejectedValue.addContent(fieldError.getRejectedValue().toString());
               }
               errorElement.addContent(rejectedValue);
            }
            Element message = new Element("message");
            message.addContent(context.getMessage(error, getResourceLoader().getLocale()));
            errorElement.addContent(message);
            errorsElement.addContent(errorElement);
         }

         root.addContent(errorsElement);
      }

      if (httpServletRequest.getParameter("success") != null) {
         Element success = new Element("success");
         success.setAttribute("messageKey", httpServletRequest.getParameter("success"));
         root.addContent(success);
      }

      if (toolSession.getAttribute(ResourceEditingHelper.CUSTOM_CSS) != null) {
         Element uri = new Element("uri");
         uri.setText((String) toolSession.getAttribute(ResourceEditingHelper.CUSTOM_CSS));
         root.getChild("css").addContent(uri);
         uri.setAttribute("order", "100");
      }

      if (toolSession.getAttribute(FormHelper.FORM_STYLES) != null) {
         List styles = (List) toolSession.getAttribute(FormHelper.FORM_STYLES);
         int index = 101;
         for (Iterator<String> i=styles.iterator();i.hasNext();) {
            Element uri = new Element("uri");
            uri.setText(i.next());
            root.getChild("css").addContent(uri);
            uri.setAttribute("order", "" + index);
            index++;
         }
      }

      Document doc = new Document(root);
      return new JDOMSource(doc);
   }

   protected String getHomeType(Artifact bean) {
      if (bean.getHome().getType() == null) {
         return "new bean";
      }
      else if (bean.getHome().getType().getId() == null) {
         return "new bean";
      }
      return bean.getHome().getType().getId().getValue();
   }

   protected void replaceNodes(Element root, ElementBean bean, EditedArtifactStorage sessionBean) {
      Element structuredData = root.getChild("formData").getChild("artifact").getChild("structuredData");
      structuredData.removeContent();
      structuredData.addContent((Element)bean.getBaseElement().clone());

      Element schema = root.getChild("formData").getChild("artifact").getChild("schema");
      Element schemaRoot = schema.getChild("element");
      StringTokenizer st = new StringTokenizer(sessionBean.getCurrentPath(), "/");
      Element newRoot = schemaRoot;

      while (st.hasMoreTokens()) {
         String schemaName = st.nextToken();
         List children = newRoot.getChild("children").getChildren("element");
         for (Iterator i=children.iterator();i.hasNext();) {
            Element schemaElement = (Element) i.next();
            if (schemaName.equals(schemaElement.getAttributeValue("name"))) {
               newRoot = schemaElement;
               break;
            }
         }
      }

      schema.removeChild("element");
      schema.addContent(newRoot.detach());
   }

   protected Map getParameters(HttpServletRequest request) {
      Map params = super.getParameters(request);

      if (params == null) {
         params = new Hashtable();
      }

      if (ToolManager.getCurrentPlacement() != null) {
         params.put("panelId", Web.escapeJavascript("Main" + ToolManager.getCurrentPlacement().getId()));
         params.put(FormHelper.XSL_SITE_ID, ToolManager.getCurrentPlacement().getContext());
      }

      if ( request.getAttribute(STYLESHEET_PARAMS) != null )
         params.putAll((Map) request.getAttribute(STYLESHEET_PARAMS));

      if ( request.getAttribute(STYLESHEET_LOCATION) != null )
         params.put(STYLESHEET_LOCATION, request.getAttribute(STYLESHEET_LOCATION));
      return params;
   }

   /**
    * Perform the actual transformation, writing to the given result.
    * @param source the Source to transform
    * @param parameters a Map of parameters to be applied to the stylesheet
    * @param result the result to write to
    * @throws Exception we let this method throw any exception; the
    * AbstractXlstView superclass will catch exceptions
    */
   protected void doTransform(Source source, Map parameters, Result result, String encoding)
         throws Exception {

      InputStream stylesheetLocation = null;
      // Nulls gets logged by getTransformer, so don't bother logging again.
      if (parameters != null)
         stylesheetLocation = (InputStream) parameters.get(STYLESHEET_LOCATION);
      Transformer trans = getTransformer(stylesheetLocation);

      // Explicitly apply URIResolver to every created Transformer.
      if (getUriResolver() != null) {
         trans.setURIResolver(getUriResolver());
      }

      // Apply any subclass supplied parameters to the transformer.
      if (parameters != null) {
         for (Iterator it = parameters.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            trans.setParameter(entry.getKey().toString(), entry.getValue());
         }
         if (logger.isDebugEnabled()) {
            logger.debug("Added parameters [" + parameters + "] to transformer object");
         }
      }

      // Specify default output properties.
      //trans.setOutputProperty(OutputKeys.ENCODING, encoding);
      trans.setOutputProperty(OutputKeys.INDENT, "yes");
      
      // Xalan-specific, but won't do any harm in other XSLT engines.
      trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

      // Perform the actual XSLT transformation.
      try {
         trans.transform(source, result);
         if (logger.isDebugEnabled()) {
            logger.debug("XSLT transformed with stylesheet [" + stylesheetLocation + "]");
         }
      }
      catch (TransformerException ex) {
         throw new NestedServletException("Couldn't perform transform with stylesheet [" +
               stylesheetLocation + "] in XSLT view with name [" + getBeanName() + "]", ex);
      }
   }

   protected Transformer getTransformer(InputStream transformer) throws TransformerException {
      try {
         if ( transformer == null ) {
            logger.warn(this+".getTransformer passed null InputStream");
            return getTransformerFactory().newTransformer();
         }
         else {
            return getTransformerFactory().newTransformer(new StreamSource(transformer));
         }
      }
      catch (TransformerConfigurationException ex) {
         logger.warn("Couldn't create XSLT transformer for stylesheet in XSLT view with name [" + getBeanName() + "]", ex);
         return getTransformerFactory().newTransformer();
      }
   }

   protected StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
      return (StructuredArtifactDefinitionManager)
         ComponentManager.get("structuredArtifactDefinitionManager");
   }

   public String getBundleLocation() {
      return bundleLocation;
   }

   public void setBundleLocation(String bundleLocation) {
      this.bundleLocation = bundleLocation;
      setResourceLoader(new ResourceLoader(bundleLocation));
   }

   public ResourceLoader getResourceLoader() {
      return resourceLoader;
   }

   public void setResourceLoader(ResourceLoader resourceLoader) {
      this.resourceLoader = resourceLoader;
   }

   public String getUriResolverBeanName() {
      return uriResolverBeanName;
   }

   public void setUriResolverBeanName(String uriResolverBeanName) {
      this.uriResolverBeanName = uriResolverBeanName;
   }

   public URIResolver getUriResolver() {
      return uriResolver;
   }

   public void setUriResolver(URIResolver uriResolver) {
      this.uriResolver = uriResolver;
      getTransformerFactory().setURIResolver(uriResolver);
   }

   public boolean isReadOnly() {
      return readOnly;
   }

   public void setReadOnly(boolean readOnly) {
      this.readOnly = readOnly;
   }

   protected void dumpDocument(Element node) {
	   try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
			transformer.transform( new JDOMSource(node), new StreamResult(System.out) );
		}
		catch ( Exception e )
		{
		   e.printStackTrace();
		}
   }

}
