/**********************************************************************************
 * $URL: $
 * $Id: $
 **********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2007, 2008, 2009, 2010 The Sakai Foundation
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

package org.sakaiproject.tool.rutgers;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;                                                    

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.entity.api.EntityNotDefinedException;
import org.sakaiproject.entity.api.EntityPermissionException;
import org.sakaiproject.entity.api.EntityProducer;
import org.sakaiproject.entity.api.EntityTransferrer;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.entity.api.HttpAccess;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.time.cover.TimeService;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.util.StringUtil;
import org.sakaiproject.util.Web;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author hedrick
 * The goal is to get sites to save and copy. However there's actually no data 
 * involved in this tool. The only configuration is the URL, which is a tool
 * configuration property. That's handled separately in site.xml
 *
 */
public class LinkToolEntityProducer implements EntityProducer, EntityTransferrer, Serializable {

   protected final Log logger = LogFactory.getLog(getClass());
   
   private static final String ARCHIVE_VERSION = "2.4"; // in case new features are added in future exports
   private static final String VERSION_ATTR = "version";
   private static final String NAME = "name";
   private static final String VALUE = "value";
   
   private static final String PROPERTIES = "properties";
   private static final String PROPERTY = "property";
   public static final String REFERENCE_ROOT = "/linktool";
   public static final String LINKTOOL_ID = "sakai.rutgers.linktool";
   public static final String LINKTOOL = "linktool";
   public static final String ATTR_TOP_REFRESH = "sakai.vppa.top.refresh";

   public void init() {
      logger.info("init()");
      
      try {
         EntityManager.registerEntityProducer(this, REFERENCE_ROOT);
      }
      catch (Exception e) {
         logger.warn("Error registering Link Tool Entity Producer", e);
      }

      try {
	  ComponentManager.loadComponent("org.sakaiproject.tool.rutgers.LinkToolEntityProducer", this);
      } catch (Exception e) {
	  logger.warn("Error registering Link Tool Entity Producer with Spring. Linktool will work, but linktool tools won't be imported from site archives. This normally happens only if you redeploy linktool. Suggest restarting Sakai", e);
      }

   }
   
   /**
    * Destroy
    */
   public void destroy()
   {
      logger.info("destroy()");
   }

    // linktool allows new tools to be created that use linktool. They will have
    // different tool ID's. The best way to find them seems to be to look
    // for all tools that set "linktool" as a keyword. Perhaps I should cache
    // this value. However in theory it would be possible to dynamically add
    // tools. Note that the tools are loaded when LinkTool.class is loaded. That's
    // often after this class, so at init time these lists would be empty.
   
   /**
    * {@inheritDoc}
    */
   public String[] myToolIds()
   {
      Set<String>keywords = new HashSet<String>();
      keywords.add("linktool");
      Set<Tool> tools = ToolManager.findTools(null, keywords);
      String[] toolIds = new String[tools.size()];
      int i = 0;
      for (Tool tool: tools)
	  toolIds[i++] = tool.getId();
      //      System.out.println("mytoolids " + toolIds);
      return toolIds;
   }
   
   public List<String> myToolList()
   {
      Set<String>keywords = new HashSet<String>();
      keywords.add("linktool");
      Set<Tool> tools = ToolManager.findTools(null, keywords);
      List<String> toolList = new ArrayList<String>();
      int i = 0;
      for (Tool tool: tools)
	  toolList.add(tool.getId());
      //      System.out.println("mytoollist " + toolList);
      return toolList;
   }

   /**
    * Get the service name for this class
    * @return
    */
   protected String serviceName() {
      return LinkToolEntityProducer.class.getName();
   }
   
   /**
    * {@inheritDoc}
    */
   public String archive(String siteId, Document doc, Stack stack, String archivePath, List attachments)
   {
      //prepare the buffer for the results log
	   StringBuilder results = new StringBuilder();

      try 
      {
	 Site site = SiteService.getSite(siteId);
         // start with an element with our very own (service) name         
         Element element = doc.createElement(serviceName());
         element.setAttribute(VERSION_ATTR, ARCHIVE_VERSION);
         ((Element) stack.peek()).appendChild(element);
         stack.push(element);

         Element linktool = doc.createElement(LINKTOOL);
         Collection<ToolConfiguration> tools = site.getTools(myToolIds());
         if (tools != null && !tools.isEmpty()) 
         {
	     for (ToolConfiguration config: tools) {
		 element = doc.createElement(LINKTOOL);

		 Attr attr = doc.createAttribute("toolid");
		 attr.setValue(config.getToolId());
		 element.setAttributeNode(attr);

		 attr = doc.createAttribute("name");
		 attr.setValue(config.getContainingPage().getTitle());
		 element.setAttributeNode(attr);

		 Properties props = config.getConfig();
		 if (props == null)
		     continue;

		 String url = props.getProperty("url", null);
		 if (url == null && props != null) {
		     String urlProp = props.getProperty("urlProp", null);
		     if (urlProp != null) {
			 url = ServerConfigurationService.getString(urlProp);
		     }
		 }

		 attr = doc.createAttribute("url");
		 attr.setValue(url);
		 element.setAttributeNode(attr);

		 String height = "600";
		 String heights =  props.getProperty("height", "600");
		 if (heights != null) {
		     heights = heights.trim();
		     if (heights.endsWith("px"))
			 heights = heights.substring(0, heights.length()-2).trim();
		     height = heights;
		 }

		 attr = doc.createAttribute("height");
		 attr.setValue(height);
		 element.setAttributeNode(attr);


		 linktool.appendChild(element);
	     }
		 
	     results.append("archiving " + getLabel() + ": (" + tools.size() + ") linktool instances archived successfully.\n");
            
         } 
         else 
         {
            results.append("archiving " + getLabel()
                  + ": no linktools.\n");
         }

         ((Element) stack.peek()).appendChild(linktool);
         stack.push(linktool);

         stack.pop();
      }
      catch (Exception any)
      {
         logger.warn("archive: exception archiving service: " + serviceName());
      }

      stack.pop();

      return results.toString();
   }
   
   /**
    * {@inheritDoc}
    */
   public Entity getEntity(Reference ref)
   {
      // I don't see how there could be a reference of this kind
       return null;
   }

   /**
    * {@inheritDoc}
    */
   public Collection getEntityAuthzGroups(Reference ref, String userId)
   {
      //TODO implement this
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String getEntityDescription(Reference ref)
   {
       // not needed
       return null;
   }

   /* (non-Javadoc)
    * @see org.sakaiproject.entity.api.EntityProducer#getEntityResourceProperties(org.sakaiproject.entity.api.Reference)
    */
   public ResourceProperties getEntityResourceProperties(Reference ref) {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String getEntityUrl(Reference ref)
   {
       // not needed
       return null;
   }

   /**
    * {@inheritDoc}
    */
   public HttpAccess getHttpAccess()
   {
       // not for now
       return null;
   }

   /**
    * {@inheritDoc}
    */
   public String getLabel() {
       return LINKTOOL;
   }



   /**
    * {@inheritDoc}
    */
   public String merge(String siteId, Element root, String archivePath, String fromSiteId, Map attachmentNames, Map userIdTrans,
         Set userListAllowImport)
   {
      logger.debug("trying to merge linktool");
      // buffer for the results log
      StringBuilder results = new StringBuilder();

      int count = 0;

      if (siteId != null && siteId.trim().length() > 0)
      {
         try
         {
            NodeList allChildrenNodes = root.getChildNodes();
            int length = allChildrenNodes.getLength();
            for (int i = 0; i < length; i++)
            {
               Node siteNode = allChildrenNodes.item(i);
               if (siteNode.getNodeType() == Node.ELEMENT_NODE)
               {
                  Element element = (Element) siteNode;
                  if (element.getTagName().equals(LINKTOOL))
                  {
                     Site site = SiteService.getSite(siteId);

		     // in the model, this code was there. It would require
		     // one tool to already be present. I'm not sure whether that's right or not.
		     // if (site.getToolForCommonId(LINKTOOL_ID) != null) {

		     if (true) {
                        // add the link tools
                        NodeList nodes = element.getChildNodes();
                        int lengthNodes = nodes.getLength();
                        for (int cn = 0; cn < lengthNodes; cn++)
                        {
                           Node node = nodes.item(cn);
                           if (node.getNodeType() == Node.ELEMENT_NODE)
                           {
                              Element linkElement = (Element) node;
                              if (linkElement.getTagName().equals(LINKTOOL)) {
				  String toolId = linkElement.getAttribute("toolid");
				  String trimBody = null;
				  if(toolId != null && toolId.length() >0) {
				      trimBody = trimToNull(toolId);
				      if (trimBody != null && trimBody.length() >0) {
					  toolId = trimBody;
				      }
				  }

				  String toolTitle = linkElement.getAttribute("name");
				  trimBody = null;
				  if(toolTitle != null && toolTitle.length() >0) {
				      trimBody = trimToNull(toolTitle);
				      if (trimBody != null && trimBody.length() >0) {
					  toolTitle = trimBody;
				      }
				  }
				  
				  String contentUrl = linkElement.getAttribute("url");
				  trimBody = null;
				  if(contentUrl != null && contentUrl.length() >0) {
				      trimBody = trimToNull(contentUrl);
				      if (trimBody != null && trimBody.length() >0) {
					  contentUrl = trimBody;
				      }
				  }

				  String height = linkElement.getAttribute("height");

				  if(toolTitle != null && toolTitle.length() >0) {
				      Tool tr = ToolManager.getTool(toolId);
				      SitePage page = site.addPage(); 
				      page.setTitle(toolTitle);
				      ToolConfiguration tool = page.addTool();
				      tool.setTool(toolId, tr);
				      tool.setTitle(toolTitle);
				      tool.getPlacementConfig().setProperty("url", contentUrl);
				      count++;

				      if (height != null) {
					  tool.getPlacementConfig().setProperty("height", height);
				      }
				  }
			      }
			   }
                        }
                        SiteService.save(site);
                    }
                  }
               }
            }

            results.append("merging link tool " + siteId + " (" + count
                  + ") items.\n");
         }
         catch (DOMException e)
         {
            logger.error(e.getMessage(), e);
            results.append("merging " + getLabel()
                  + " failed during xml parsing.\n");
         }
         catch (Exception e)
         {
            logger.error(e.getMessage(), e);
            results.append("merging " + getLabel() + " failed.\n");
         }
      }
      return results.toString();

   } // merge


   /**
    * {@inheritDoc}
    */
   public boolean parseEntityReference(String reference, Reference ref)
   {
       // not for the moment
       return false;
   }

    public String trimToNull(String value)
    {
	if (value == null) return null;
	value = value.trim();
	if (value.length() == 0) return null;
	return value;
    }

   /**
    * {@inheritDoc}
    */
   public boolean willArchiveMerge()
   {
      return true;
   }
   
	public void transferCopyEntities(String fromContext, String toContext, List ids)
	{
	        logger.debug("linktool transferCopyEntities");
		try
		{				
			// retrieve all of the web content tools to copy
			Site fromSite = SiteService.getSite(fromContext);
			Site toSite = SiteService.getSite(toContext);
			List fromSitePages = fromSite.getPages();

			if (fromSitePages != null && !fromSitePages.isEmpty()) {
				Iterator pageIter = fromSitePages.iterator();
				while (pageIter.hasNext()) {
					SitePage currPage = (SitePage) pageIter.next();

					List toolList = currPage.getTools();
					Iterator toolIter = toolList.iterator();
					List<String> toolIds = myToolList();
					while (toolIter.hasNext()) {
						ToolConfiguration toolConfig = (ToolConfiguration)toolIter.next();
						
						if (toolIds.contains(toolConfig.getToolId())) {
						        String contentUrl = toolConfig.getPlacementConfig().getProperty("url");
							String toolTitle = toolConfig.getTitle();
							String pageTitle = currPage.getTitle();
							String height = toolConfig.getPlacementConfig().getProperty("height");

							if(toolTitle != null && toolTitle.length() >0 && pageTitle !=null && pageTitle.length() > 0)
							{
								Tool tr = ToolManager.getTool(LINKTOOL_ID);
								SitePage page = toSite.addPage(); 
								page.setTitle(pageTitle);
								page.setLayout(0);
								ToolConfiguration tool = page.addTool();
								tool.setTool(LINKTOOL_ID, tr);
								tool.setTitle(toolTitle);
								if (contentUrl != null) {
									tool.getPlacementConfig().setProperty("url", contentUrl);
								}

								if (height != null) {
									tool.getPlacementConfig().setProperty("height", height);
								}

								if (currPage.isPopUp()) 
									page.setPopup(true);
								else
									page.setPopup(false);
							}
						}
					}
				}
			}
			SiteService.save(toSite);
			ToolSession session = SessionManager.getCurrentToolSession();

			if (session.getAttribute(ATTR_TOP_REFRESH) == null)
			{
				session.setAttribute(ATTR_TOP_REFRESH, Boolean.TRUE);
			}
		}

		catch (Exception any)
		{
			logger.warn("transferCopyEntities(): exception in handling webcontent data: ", any);
		}

	}

	public void transferCopyEntities(String fromContext, String toContext, List ids, boolean cleanup)
	{	
		try
		{
			if(cleanup == true)
			{
				Site toSite = SiteService.getSite(toContext);
				
				List toSitePages = toSite.getPages();
				if (toSitePages != null && !toSitePages.isEmpty()) 
				{
					Vector removePageIds = new Vector();
					Iterator pageIter = toSitePages.iterator();
					while (pageIter.hasNext()) 
					{
						SitePage currPage = (SitePage) pageIter.next();

						List<String> toolIds = myToolList();

						List toolList = currPage.getTools();
						Iterator toolIter = toolList.iterator();
						while (toolIter.hasNext()) 
						{
							ToolConfiguration toolConfig = (ToolConfiguration)toolIter.next();

							if (toolIds.contains(toolConfig.getToolId()))
							{
								removePageIds.add(toolConfig.getPageId());
							}
						}
					}
					for (int i = 0; i < removePageIds.size(); i++) 
					{
						String removeId = (String) removePageIds.get(i);
						SitePage sitePage = toSite.getPage(removeId);
						toSite.removePage(sitePage);
					}
					
				}
				SiteService.save(toSite);
				ToolSession session = SessionManager.getCurrentToolSession();

				if (session.getAttribute(ATTR_TOP_REFRESH) == null)
				{
					session.setAttribute(ATTR_TOP_REFRESH, Boolean.TRUE);
				}
				 
			} 
			transferCopyEntities(fromContext, toContext, ids);
		}
		catch (Exception e)
		{
			logger.info("WebContent transferCopyEntities Error" + e);
		}
	}


}
