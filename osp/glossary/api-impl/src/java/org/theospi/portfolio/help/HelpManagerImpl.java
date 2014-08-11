/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/glossary/api-impl/src/java/org/theospi/portfolio/help/HelpManagerImpl.java $
* $Id:HelpManagerImpl.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.help;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.exception.UnsupportedFileTypeException;
import org.sakaiproject.metaobj.shared.DownloadableManager;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.MimeType;
import org.sakaiproject.metaobj.shared.model.PersistenceException;
import org.sakaiproject.metaobj.worksite.mgt.WorksiteManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.ToolManager;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.theospi.portfolio.help.model.Glossary;
import org.theospi.portfolio.help.model.GlossaryDescription;
import org.theospi.portfolio.help.model.GlossaryEntry;
import org.theospi.portfolio.help.model.HelpFunctionConstants;
import org.theospi.portfolio.help.model.HelpManager;
import org.theospi.portfolio.security.AuthorizationFacade;
import org.theospi.portfolio.security.AuthorizationFailedException;
import org.theospi.portfolio.shared.model.Node;
import org.theospi.utils.zip.UncloseableZipInputStream;

/**
 * This implementation uses the spring config to configure the system, and
 * uses as a database for indexing resources, and configuring which contexts
 * are associated with what resources.  Lucene is also responsible for
 * performing help searches.
 *
 * <br/><br/>
 *
 * Contexts are mapped to views in the spring config.  To do this, define
 * a bean of type, org.theospi.portfolio.help.model.HelpContextConfig.
 * Create a map of contexts which are keyed by the view name.  Contexts are
 * just string ids.  An example:
 * <br/><br/>
 *   &lt;bean id="presentationHelpContexts" class="org.theospi.portfolio.help.model.HelpContextConfig"&gt;<br/>
 *     &lt;constructor-arg&gt;<br/>
 *        &lt;map&gt;<br/>
 *          &lt;entry key="addPresentation1"&gt;<br/>
 *              &lt;list&gt;                          <br/>
 *                 &lt;value&gt;Creating a Presentation&lt;/value&gt;<br/>
 *              &lt;/list&gt;                                             <br/>
 *           &lt;/entry&gt;                                                    <br/>
 *  ...
 *  <br/><br/>
 * An explanation: what this means is that when a user navigates to the
 * addPresentation1 view a context called "Creating a Presentation" is created.
 * This context is just an identifier for possible actions the user might perform
 * from this page.
 *  <br/><br/>
 * To create resources define a bean of type, org.theospi.portfolio.help.model.Resource.
 * The name is the display name that is shown on jsp pages.  The location is
 * the url of the resource.  Configure all contexts associated with this resource.
 * An example,
 *   <br/><br/>
 *    &lt;bean id="pres_resource_2" class="org.theospi.portfolio.help.model.Resource"&gt; <br/>
 *     &lt;property name="name"&gt;&lt;value&gt;Creating a Presentation&lt;/value&gt;&lt;/property&gt;   <br/>
 *     &lt;property name="location"&gt;&lt;value&gt;${system.baseUrl}/help/creatingPresentations.html&lt;/value&gt;&lt;/property&gt;<br/>
 *     &lt;property name="contexts"&gt;<br/>
 *        &lt;list&gt;<br/>
 *           &lt;value&gt;Creating a Presentation&lt;/value&gt;<br/>
 *        &lt;/list&gt;<br/>
 *     &lt;/property&gt;<br/>
 *  &lt;/bean&gt;<br/>
 * <br/><br/>
 * If all this is configured correctly, when a user navigates to the addPresentation1
 * view a context of "Creating a Presentation" is created.  If the user navigates
 * to help, the user will be presented with links to all the resources associated with
 * this context.
 * <br/><br/>
 *
 * @see org.theospi.portfolio.help.model.Resource
 * @see org.theospi.portfolio.help.model.Source
 *
 */
public class HelpManagerImpl extends HibernateDaoSupport
   implements HelpManager, HelpFunctionConstants, DownloadableManager {

   protected final Log logger = LogFactory.getLog(getClass());
   private Glossary glossary;
   private IdManager idManager;
   private AuthorizationFacade authzManager;
   private WorksiteManager worksiteManager;
   private ToolManager toolManager;
   private ContentHostingService contentHosting;
   private AgentManager agentManager;
   private List globalSites;
   private List globalSiteTypes;

   public GlossaryEntry searchGlossary(String keyword) {
      return getGlossary().find(keyword, toolManager.getCurrentPlacement().getContext());
   }

   public boolean isPhraseStart(String phraseFragment) {
      return getGlossary().isPhraseStart(phraseFragment, toolManager.getCurrentPlacement().getContext());
   }
  
   public void setIdManager(IdManager idManager){
      this.idManager = idManager;
   }

   public Glossary getGlossary() {
      return glossary;
   }

   public void setGlossary(Glossary glossary) {
      this.glossary = glossary;
   }

   public ContentHostingService getContentHosting() {
      return contentHosting;
   }

   public void setContentHosting(ContentHostingService contentHosting) {
      this.contentHosting = contentHosting;
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

   public GlossaryEntry addEntry(GlossaryEntry newEntry) {
      getAuthzManager().checkPermission(ADD_TERM,
         getToolId());
		if (isGlobal()) {
			//Prepare for Global add
			newEntry.setWorksiteId(null);
		} else {
			//Prepare for Local add
         newEntry.setWorksiteId(getWorksiteManager().getCurrentWorksiteId().getValue());
		}

      if (entryExists(newEntry)) {
         throw new PersistenceException("Glossary term {0} already defined.",
            new Object[]{newEntry.getTerm()}, "term");
      }

		return getGlossary().addEntry(newEntry);
   }

   public void removeEntry(GlossaryEntry entry) {
      getAuthzManager().checkPermission(DELETE_TERM,
         getToolId());
      if (isGlobal()) {
         getGlossary().removeEntry(entry);
      }
      else {
         if (entry.getWorksiteId().equals(getWorksiteManager().getCurrentWorksiteId().getValue())) {
            getGlossary().removeEntry(entry);
         }
         else {
            throw new AuthorizationFailedException("Unable to update from another worksite");
         }
      }
   }

   public void updateEntry(GlossaryEntry entry) {
      getAuthzManager().checkPermission(EDIT_TERM,
         getToolId());
      if (isGlobal()) {
         entry.setWorksiteId(null);
      }
      else {
         if (!entry.getWorksiteId().equals(getWorksiteManager().getCurrentWorksiteId().getValue())) {
            throw new AuthorizationFailedException("Unable to update from another worksite");
         }
      }
      if (entryExists(entry)) {
         throw new PersistenceException("Glossary term {0} already defined.",
            new Object[]{entry.getTerm()}, "term");
      }
      getGlossary().updateEntry(entry);
   }

   public boolean isMaintainer(){
      return getAuthzManager().isAuthorized(WorksiteManager.WORKSITE_MAINTAIN,
         idManager.getId(toolManager.getCurrentPlacement().getContext()));
   }

   public Collection getWorksiteTerms() {
	   return getWorksiteTerms(getWorksiteManager().getCurrentWorksiteId().getValue());
   }
   
   public Collection getWorksiteTerms(String workSite) {
	      if (isGlobal()) {
	         return getGlossary().findAllGlobal();
	      }
	      else {
	         return getGlossary().findAll(workSite);
	      }
	   }

   public boolean isGlobal() {
      String siteId = getWorksiteManager().getCurrentWorksiteId().getValue();

      if (getGlobalSites().contains(siteId)) {
         return true;
      }

      Site site = getWorksiteManager().getSite(siteId);
      if (site.getType() != null && getGlobalSiteTypes().contains(site.getType())) {
         return true;
      }

      return false;
   }
   
   protected boolean entryExists(GlossaryEntry entry){
      return entryExists(entry, toolManager.getCurrentPlacement().getContext());
   }
   protected boolean entryExists(GlossaryEntry entry, String worksite){
	      Collection entryFound = getGlossary().findAll(entry.getTerm(), worksite);
			for (Iterator i = entryFound.iterator();i.hasNext();){
				GlossaryEntry entryIter = (GlossaryEntry)i.next();
				String entryWID = entryIter.getWorksiteId();

	         if (entryIter.getId().equals(entry.getId())) {
	            continue;
	         }
	         else if (entryWID == null && isGlobal()) {
	            return true;
	         }
	         else if (entryWID != null) {
	            return true;
	         }
			}
			
			return false;
	   }
   

   public String packageForDownload(Map params, OutputStream out) throws IOException {

	   packageGlossaryForExport(getWorksiteManager().getCurrentWorksiteId(), out);
          
          //Blank filename for now -- no more dangerous, since the request is in the form of a filename
          return "";
   }
   
   public void packageGlossaryForExport(Id worksiteId, OutputStream os)
			throws IOException {
      getAuthzManager().checkPermission(HelpFunctionConstants.EXPORT_TERMS, 
            getToolId());

		CheckedOutputStream checksum = new CheckedOutputStream(os,
				new Adler32());
		ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(
				checksum));
		
		putWorksiteTermsIntoZip(worksiteId, zos);

		zos.finish();
		zos.flush();
	}
	public void putWorksiteTermsIntoZip(Id worksiteId, ZipOutputStream zout) throws IOException
	{
		Collection terms = getWorksiteTerms(worksiteId.getValue());

	    Element rootNode = new Element("ospiGlossary");
	    
	    rootNode.setAttribute("formatVersion", "2.1");
	    
		for (Iterator iter = terms.iterator(); iter.hasNext();) {
			GlossaryEntry ge = (GlossaryEntry) iter.next();
			
			//loads the long description
			ge = getGlossary().load(ge.getId());
			
		    Element termNode = new Element("ospiTerm");
		    
		    Element attrNode = new Element("term");
		    attrNode.addContent(new CDATA(ge.getTerm()));
		    termNode.addContent(attrNode);
		    
		    attrNode = new Element("description");
		    attrNode.addContent(new CDATA(ge.getDescription()));
		    termNode.addContent(attrNode);
		    
		    attrNode = new Element("longDescription");
		    attrNode.addContent(new CDATA(ge.getLongDescription()));
		    termNode.addContent(attrNode);
			
			rootNode.addContent(termNode);
		}
		storeFileInZip(zout, new java.io.StringReader(
					(new XMLOutputter()).outputString(new Document(rootNode))), "glossaryTerms.xml");
	}

	protected void storeFileInZip(ZipOutputStream zos, Reader in,
			String entryName) throws IOException {

		char data[] = new char[1024 * 10];

		if (File.separatorChar == '\\') {
			entryName = entryName.replace('\\', '/');
		}

		ZipEntry newfileEntry = new ZipEntry(entryName);

		zos.putNextEntry(newfileEntry);

		BufferedReader origin = new BufferedReader(in, data.length);
		OutputStreamWriter osw = new OutputStreamWriter(zos);
		int count;
		while ((count = origin.read(data, 0, data.length)) != -1) {
			osw.write(data, 0, count);
		}
		origin.close();
      osw.flush();
		zos.closeEntry();
		in.close();
	}

	public Node getNode(Id artifactId) {
		String id = getContentHosting().resolveUuid(artifactId.getValue());
		if (id == null) {
			return null;
		}

		try {
			ContentResource resource = getContentHosting().getResource(id);
			String ownerId = resource.getProperties().getProperty(
					resource.getProperties().getNamePropCreator());
			Agent owner = getAgentManager().getAgent(
					idManager.getId(ownerId));
			return new Node(artifactId, resource, owner);
		} catch (PermissionException e) {
			logger.error("", e);
			throw new RuntimeException(e);
		} catch (IdUnusedException e) {
			logger.error("", e);
			throw new RuntimeException(e);
		} catch (TypeException e) {
			logger.error("", e);
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * Given a resource id, this parses out the GlossaryEntries from its input stream.
	 * Once the enties are found, they are inserted into the users current worksite.  If a term exists
	 * in the worksite, then execute based on the last parameter.
	 * @param worksiteId Id
	 * @param resourceId an String
	 * @param replaceExisting boolean
	 */
	public void importTermsResource(String resourceId, boolean replaceExisting) throws IOException, UnsupportedFileTypeException, JDOMException
	{
		importTermsResource(getWorksiteManager().getCurrentWorksiteId(), resourceId, replaceExisting);
	}
	
	
	/**
	 * Given a resource id, this parses out the GlossaryEntries from its input stream.
	 * Once the enties are found, they are inserted into the given worksite.  If a term exists
	 * in the worksite, then execute based on the last parameter.
	 * @param worksiteId Id
	 * @param resourceId an String
	 * @param replaceExisting boolean
	 */
	public void importTermsResource(Id worksiteId, String resourceId, boolean replaceExisting) 
         throws IOException, UnsupportedFileTypeException, JDOMException
	{
		Node node = getNode(idManager.getId(resourceId));
		if(node.getMimeType().equals(new MimeType("text/xml")) || 
				node.getMimeType().equals(new MimeType("application/x-osp")) ||
				node.getMimeType().equals(new MimeType("application/xml"))) {
			importTermsStream(worksiteId, node.getInputStream(), replaceExisting);
		} else if(node.getMimeType().equals(new MimeType("application/zip")) || 
				node.getMimeType().equals(new MimeType("application/x-zip-compressed"))) {
			ZipInputStream zis = new UncloseableZipInputStream(node.getInputStream());

		    ZipEntry currentEntry = zis.getNextEntry();
		    boolean found = false;
		    while(currentEntry != null) {
		    	 if(currentEntry.getName().endsWith("xml")) {
					 importTermsStream(worksiteId, zis, replaceExisting);
                found = true;
		    	 }
	          zis.closeEntry();
	          currentEntry = zis.getNextEntry();
		    }
          
          if(!found)
            throw new UnsupportedFileTypeException("No glossary xml files were found");
          
		} else {
			throw new UnsupportedFileTypeException("Unsupported file type");
		}
	}
	
	
	/**
	 * Given an xml File stream, this parses out the GlossaryEntries from the input stream.
	 * Once the enties are found, they are inserted into the given worksite.  If a term exists
	 * in the worksite, then execute based on the last parameter.
	 * @param worksiteId Id
	 * @param inStream an xml InputStream
	 * @param replaceExisting boolean
	 */
	public void importTermsStream(Id worksiteId, InputStream inStream, boolean replaceExisting)
            throws UnsupportedFileTypeException, JDOMException, IOException
	{

		SAXBuilder builder = new SAXBuilder();
		builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // SAK-23131

		try {
		   Document document	= builder.build(new InputStreamReader(inStream));
		   List		entries		= extractEntries(document);
		   String	worksiteStr = worksiteId.getValue();
		   
		   for(Iterator iter = entries.iterator(); iter.hasNext(); ) {
			   GlossaryEntry entry = (GlossaryEntry)iter.next();
			   entry.setWorksiteId(worksiteStr);
			   boolean exists = entryExists(entry, worksiteStr);
			   if(!exists || (exists && replaceExisting)) {
				   if(!exists) {
					   addEntry(entry);
				   } else {
					   GlossaryEntry existingEntry = getGlossary().find(entry.getTerm(), worksiteStr);
					   existingEntry.setDescription(entry.getDescription());
					   existingEntry.setLongDescription(entry.getLongDescription());
					   updateEntry(existingEntry);
				   }
			   }
		   }

      } catch(UnsupportedFileTypeException ufte) {
         throw ufte;
      } catch(JDOMException jdome) {
         logger.error(jdome);
         throw jdome;
		}
	}
	
	
	/**
	 * Given an xml document this reads out the glossary entries.
	 * @param document XML Dom Document
	 * @return List of GlossaryEntry
	 */
	public List extractEntries(Document document) throws UnsupportedFileTypeException
	{
		Element topNode = document.getRootElement();

		List ospiTerms = topNode.getChildren("ospiTerm");
      
      if(ospiTerms.size() == 0)
         throw new UnsupportedFileTypeException("No glossary term node found");
      
		List entries = new ArrayList();

		for (Iterator iter = ospiTerms.iterator(); iter.hasNext();) {
			Element ospiTerm = (Element) iter.next();
			
			GlossaryEntry entry = new GlossaryEntry();
			entry.setLongDescriptionObject(new GlossaryDescription());
			entry.setTerm(ospiTerm.getChildTextTrim("term"));
			entry.setDescription(ospiTerm.getChildTextTrim("description"));
			entry.setLongDescription(ospiTerm.getChildTextTrim("longDescription"));
			entries.add(entry);
		}
		return entries;
	}
		
   protected Id getToolId() {
      Placement placement = toolManager.getCurrentPlacement();
      return idManager.getId(placement.getId());
   }

   public AuthorizationFacade getAuthzManager() {
      return authzManager;
   }

   public void setAuthzManager(AuthorizationFacade authzManager) {
      this.authzManager = authzManager;
   }

   public WorksiteManager getWorksiteManager() {
      return worksiteManager;
   }

   public void setWorksiteManager(WorksiteManager worksiteManager) {
      this.worksiteManager = worksiteManager;
   }
   
   public void removeFromSession(Object obj) {
      this.getHibernateTemplate().evict(obj);

      // Check whether it is a Hibernate-mapping class
      Class<? extends Object> clazz;
      if (obj instanceof GlossaryEntry) {
         clazz = GlossaryEntry.class;
      } else if (obj instanceof GlossaryDescription) {
         clazz = GlossaryDescription.class;
      } else {
         clazz = obj.getClass();
      }
      try {
         getHibernateTemplate().getSessionFactory().evict(clazz);
      } catch (HibernateException e) {
         logger.error(e);
      }
   }

   public Set getSortedWorksiteTerms() {
      return getGlossary().getSortedWorksiteTerms(toolManager.getCurrentPlacement().getContext());
   }

   public ToolManager getToolManager() {
      return toolManager;
   }

   public void setToolManager(ToolManager toolManager) {
      this.toolManager = toolManager;
   }

   public List getGlobalSites() {
      return globalSites;
   }

   public void setGlobalSites(List globalSites) {
      this.globalSites = globalSites;
   }

   public List getGlobalSiteTypes() {
      return globalSiteTypes;
   }

   public void setGlobalSiteTypes(List globalSiteTypes) {
      this.globalSiteTypes = globalSiteTypes;
   }

}
