/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.3/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/mgt/ContentEntityWrapper.java $
 * $Id: ContentEntityWrapper.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.shared.mgt;

import java.io.InputStream;
import java.util.Collection;
import java.util.Stack;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentHostingHandler;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.time.api.Time;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis Date: Nov 7, 2005 Time: 3:12:50 PM
 */
public class ContentEntityWrapper implements ContentResource
{
	private ContentResource base;

	private String reference;

	public ContentEntityWrapper(ContentResource base, String reference)
	{
		this.base = base;
		this.reference = reference;
	}

	public long getContentLength()
	{
		return base.getContentLength();
	}

	public String getContentType()
	{
		return base.getContentType();
	}

	public byte[] getContent() throws ServerOverloadException
	{
		return base.getContent();
	}

	public InputStream streamContent() throws ServerOverloadException
	{
		return base.streamContent();
	}

	public String getUrl()
	{
		return ServerConfigurationService.getAccessUrl() + getReference();
	}

	public String getReference()
	{
		return reference;
	}

	public String getId()
	{
		return base.getId();
	}

	public ResourceProperties getProperties()
	{
		return base.getProperties();
	}

	public Element toXml(Document doc, Stack stack)
	{
		return base.toXml(doc, stack);
	}

	public String getUrl(String rootProperty)
	{
		return base.getUrl(rootProperty);
	}

	public String getReference(String rootProperty)
	{
		return base.getUrl(rootProperty);
	}

	public ContentResource getBase()
	{
		return base;
	}

	public void setBase(ContentResource base)
	{
		this.base = base;
	}

	public Collection getGroups()
	{
		return base.getGroups();
	}

	public AccessMode getAccess()
	{
		return base.getAccess();
	}

	public Time getReleaseDate()
	{
		return base.getReleaseDate();
	}

	public Time getRetractDate()
	{
		return base.getRetractDate();
	}

	public boolean isResource()
	{
		return base.isResource();
	}

	public boolean isCollection()
	{
		return base.isCollection();
	}

	public ContentCollection getContainingCollection()
	{
		return base.getContainingCollection();
	}

	public Collection getGroupObjects()
	{
		return base.getGroupObjects();
	}

	public AccessMode getInheritedAccess()
	{
		return base.getInheritedAccess();
	}

	public Collection getInheritedGroupObjects()
	{
		return base.getInheritedGroupObjects();
	}

	public Collection getInheritedGroups()
	{
		return base.getInheritedGroups();
	}
	
	public boolean isHidden()
	{
		return base.isHidden();
	}
	
	public boolean isAvailable()
	{
		return base.isAvailable();
	}

   public String getResourceType() 
   {
      return base.getResourceType();
   }

   public ContentHostingHandler getContentHandler() {
      return base.getContentHandler();
   }

   public ContentEntity getMember(String nextId) {
      return base.getMember(nextId);
   }

   public ContentEntity getVirtualContentEntity() {
      return base.getVirtualContentEntity();
   }

   public void setContentHandler(ContentHostingHandler chh) {
      base.setContentHandler(chh);      
   }

   public void setVirtualContentEntity(ContentEntity ce) {
      base.setVirtualContentEntity(ce);      
   }

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentEntity#getUrl(boolean)
	 */
	public String getUrl(boolean relative)
	{
		return base.getUrl(relative);
	}

}
