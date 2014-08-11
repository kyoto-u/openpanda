/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/control/ViewPresentationControl.java $
* $Id:ViewPresentationControl.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.metaobj.shared.ArtifactFinder;
import org.sakaiproject.metaobj.shared.ArtifactFinderManager;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.utils.mvc.intf.LoadObjectController;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.tool.api.Tool;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.theospi.portfolio.presentation.PresentationFunctionConstants;
import org.theospi.portfolio.presentation.PresentationManager;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.model.PresentationComment;
import org.theospi.portfolio.presentation.model.PresentationLog;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.portfolio.shared.model.OspException;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;


/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 25, 2004
 * Time: 1:52:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ViewPresentationControl extends AbstractPresentationController implements LoadObjectController {

   protected static final Log logger = LogFactory.getLog(ViewPresentationControl.class);
   private HomeFactory homeFactory = null;
   private ArtifactFinder artifactFinder = null;
   private AuthorizationFacade authzManager = null;
   private ArtifactFinderManager artifactFinderManager;
   private Hashtable presentationTemplateCache = new Hashtable();
   private URIResolver uriResolver;
   private static Cache cache = setupCache();

   public static final String XSL_SITE_ID = "sakaiSiteId";
   public static final String XSL_PRESENTATION_TYPE = "sakaiPresentationType";
   public static final String XSL_PRESENTATION_ID = "sakaiPresentationId";

   private static Cache setupCache() {
      // detailed configuration is in presentation/tool/src/bundle/ehcache.xml,
      // which ends up in tomcat/webapps/osp-presentation-tool/WEB-INF/classes/

      if ( !ServerConfigurationService.getBoolean("cache.osp.presentation.data",false) )
         return null;

      String cacheName = "org.theospi.portfolio.presentation.control.ViewPresentationControl.XML";
      CacheManager cacheManager = CacheManager.create();
      if (cacheManager == null)
         return null;
         
      try
      {
         cacheManager.addCache(cacheName);
         return cacheManager.getCache(cacheName);
      }
      catch (Exception e)
      {
         logger.warn("ViewPresentationControl.setupCache failed");
      }
      
      return null;
   }


   public Object fillBackingObject(Object incomingModel, Map request,
                                   Map session, Map application) throws Exception {
      PresentationManager presentationManager = getPresentationManager();
      Presentation presentation = (Presentation) incomingModel;
      if (presentation.getSecretExportKey() != null) {
         String secretExportKey = presentation.getSecretExportKey();
         presentation = presentationManager.getPresentation(presentation.getId(),
               secretExportKey);
         presentation.setSecretExportKey(secretExportKey);
         logger.debug("User " + getAuthManager().getAgent().getId() + " is viewing a presentation with a secret export key: " + presentation.getId().getValue());
         return presentation;
      }
      else {
         // if it exists, get the presentation from memory that is being edited
         Presentation previewPres = (Presentation) session.get("presentation");
         if (previewPres != null && previewPres.getId().getValue().equals(presentation.getId().getValue())) {

            //side step any authz issues as the presentation only exists in the users session
            previewPres.setIsPublic(true);
            previewPres.setIsPreview(true);
            logger.debug("User " + getAuthManager().getAgent().getId() + " is viewing a presentation from session: " + previewPres.getId().getValue());

            return previewPres;
    	 }

         if ( presentation.getId() == null ) {
            logger.warn("Attempt to view invalid/unspecified presentation by user " 
                        + getAuthManager().getAgent().getId() );
            return null;
         }
         else {
            logger.debug("User " + getAuthManager().getAgent().getId() + " is viewing a presentation by id: " + presentation.getId().getValue());
            return getPresentationManager().getLightweightPresentation(presentation.getId());
         }
      }
   }

   public ModelAndView handleRequest(Object requestModel, Map request,
                                     Map session, Map application, Errors errors) {
      Presentation pres = (Presentation) requestModel;
      
      // check for unspecified or invalid portfolio
      if ( pres == null )
         return new ModelAndView("expired");

      if (pres.getSecretExportKey() == null) {
         if (!pres.getIsPublic()) {
            if (getAuthManager().getAgent().isInRole(Agent.ROLE_ANONYMOUS)){
               try {
                  Site site = SiteService.getSite(pres.getSiteId());               
                  ToolConfiguration toolConfig = site.getToolForCommonId(PresentationFunctionConstants.PRES_TOOL_ID);
                  String placement = toolConfig.getId();
                  ToolSession ts = SessionManager.getCurrentSession().getToolSession(placement);               
                  SessionManager.setCurrentToolSession(ts);
                 
                  SessionManager.getCurrentSession().setAttribute(Tool.HELPER_DONE_URL, pres.getExternalUri());

                  Map model = new Hashtable();
                  model.put("sakai.tool.placement.id", placement);
                  return new ModelAndView("authnRedirect", model);
                 
               } catch (IdUnusedException e) {
                  logger.error("", e);
               }
            }
            else {
               boolean viewAll = ServerConfigurationService.getBoolean("osp.presentation.viewall", false);
               boolean canReview = getAuthzManager().isAuthorized(PresentationFunctionConstants.REVIEW_PRESENTATION,
                                                                  getIdManager().getId(pres.getSiteId() ) );
               boolean canView = getAuthzManager().isAuthorized(PresentationFunctionConstants.VIEW_PRESENTATION, pres.getId());
               
               if ( !canView && (!viewAll || !canReview) )
                  return new ModelAndView("expired"); // display expired or invalid message
            }
         }

         if (pres.isExpired() &&
            !pres.getOwner().getId().equals(getAuthManager().getAgent().getId())) {
            return new ModelAndView("expired"); // display expired or invalid message
         }
      }

      if (!pres.isPreview()) {
         logViewedPresentation(pres);
      }
      
      Hashtable model = new Hashtable();

      try {
         model.put("presentation", pres);
         Document doc = null;
         
         if (pres.getPresentationType().equals(Presentation.TEMPLATE_TYPE)) {

            // have to check modified dates rather than depending upon
            // clearing the cache when something changes. In a cluster
            // the event that invalidates the cache may occur on a different
            // system. For previews always force current data. Otherwise
            // invalidate cache if presentation or template on which it is
            // based changes.
            if (cache != null && !pres.isPreview()) {
               Element element = cache.get(pres.getId());
               if (element != null &&
                   pres.getModified().getTime() <= element.getCreationTime() &&
                   pres.getTemplate().getModified().getTime() <= element.getCreationTime()) {
                  doc = (Document)element.getValue();
               }
            }
            
            if (doc == null) {
               doc = getPresentationManager().createDocument(pres);

               if (cache != null && doc != null)
                  cache.put(new Element(pres.getId(), doc));
            }
         } 
         else {
            String page = (String)request.get("page");
            if (pres.isPreview()) {
               doc = getPresentationManager().getPresentationPreviewLayoutAsXml(pres, page);
            }
            else {
               doc = getPresentationManager().getPresentationLayoutAsXml(pres, page);
            }
            if(doc == null){
            	model.put("noPagesFound", true);
            	return new ModelAndView("notFound", model);
            }
         }
         Site site = SiteService.getSite(pres.getSiteId());
         getAuthzManager().pushAuthzGroups(site.getId());
         ToolConfiguration toolConfig = site.getToolForCommonId(PresentationFunctionConstants.PRES_TOOL_ID);
         String placement = toolConfig.getId();
         model.put("placementId", placement); 
         if(doc != null)
            model.put("document", doc);
         else
            return new ModelAndView("notFound", model);
         model.put("renderer", getTransformer(pres, request));
         model.put("uriResolver", getUriResolver());

         if (!getAuthManager().getAgent().isInRole(Agent.ROLE_ANONYMOUS)) {
            model.put("currentAgent", getAuthManager().getAgent());
         }

         if (!pres.isPreview()) {
            model.put("comments", getPresentationManager().getPresentationComments(pres.getId(),
                getAuthManager().getAgent()));

            boolean allowComments = getAuthzManager().isAuthorized( 
                PresentationFunctionConstants.COMMENT_PRESENTATION, pres.getId() );
            model.put("allowComments", allowComments );
         }
         else {
            model.put("allowComments", pres.isAllowComments());
         }
	         
         if (request.get(BindException.ERROR_KEY_PREFIX + "newComment") == null) {
            request.put(BindException.ERROR_KEY_PREFIX + "newComment",
                  new BindException(new PresentationComment(), "newComment"));
         }

      } catch (PersistenceException e) {
         logger.error("",e);
         throw new OspException(e);
      } catch (IdUnusedException e) {
         logger.error("", e);
      }


      boolean headers = pres.getTemplate().isIncludeHeaderAndFooter();
      String viewName = "withoutHeader";

      if (headers) {
         if (ToolManager.getCurrentPlacement() == null) {
            viewName = "withHeaderStandalone";
         }
         else {
            viewName = "withHeader";
         }
      }
      return new ModelAndView(viewName, model);
   }
   
   /**
    * creates a new log that this presentation has been viewed
    * @param pres
    */
   protected void logViewedPresentation(Presentation pres){
      PresentationLog log = new PresentationLog();
      log.setPresentation(pres);
      log.setViewDate(new java.util.Date());
      log.setViewer(getAuthManager().getAgent());
      getPresentationManager().storePresentationLog(log);
   }

   // cache the template...
   protected Transformer getTransformer(Presentation presentation, Map request) throws PersistenceException {
      Id renderer = presentation.getTemplate().getRenderer();
      TransformerWrapper wrapper = (TransformerWrapper) presentationTemplateCache.get(renderer);

      if (wrapper == null) {
         wrapper = new TransformerWrapper();
         wrapper.modified = 0;
      }

      Node xsl = getPresentationManager().getNode(renderer);

      if (xsl.getTechnicalMetadata().getLastModified().getTime() > wrapper.modified) {
         try {
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setURIResolver(getUriResolver());
            wrapper.transformer = tf.newTransformer(new StreamSource(xsl.getInputStream()));
            wrapper.modified = xsl.getTechnicalMetadata().getLastModified()
                  .getTime();
         } catch (TransformerConfigurationException e) {
            throw new OspException(e);
         }
      }

      wrapper.transformer.clearParameters();

      //send request params in as transform params
      for(Iterator i=request.entrySet().iterator();i.hasNext();){
         Entry entry = (Entry) i.next();
         wrapper.transformer.setParameter(entry.getKey().toString(),entry.getValue().toString());
      }

      wrapper.transformer.setParameter(XSL_SITE_ID, presentation.getSiteId());
      if (presentation.getIsFreeFormType()) {
         wrapper.transformer.setParameter(XSL_PRESENTATION_TYPE, presentation.getPresentationType());
      } else if (presentation.getTemplate() != null) {
         wrapper.transformer.setParameter(XSL_PRESENTATION_TYPE, presentation.getTemplate().getId().getValue());
      }
      wrapper.transformer.setParameter(XSL_PRESENTATION_ID, presentation.getId().getValue());

      presentationTemplateCache.put(renderer,wrapper);

      return wrapper.transformer;
   }

   private class TransformerWrapper {
      public long modified;
      public Transformer transformer;
   }

   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }

   public ArtifactFinder getArtifactFinder() {
      return artifactFinder;
   }

   public void setArtifactFinder(ArtifactFinder artifactFinder) {
      this.artifactFinder = artifactFinder;
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public ArtifactFinderManager getArtifactFinderManager() {
      return artifactFinderManager;
   }

   public void setArtifactFinderManager(ArtifactFinderManager artifactFinderManager) {
      this.artifactFinderManager = artifactFinderManager;
   }

   public Hashtable getPresentationTemplateCache() {
      return presentationTemplateCache;
   }

   public void setPresentationTemplateCache(Hashtable presentationTemplateCache) {
      this.presentationTemplateCache = presentationTemplateCache;
   }

   public URIResolver getUriResolver() {
      return uriResolver;
   }

   public void setUriResolver(URIResolver uriResolver) {
      this.uriResolver = uriResolver;
   }
}
