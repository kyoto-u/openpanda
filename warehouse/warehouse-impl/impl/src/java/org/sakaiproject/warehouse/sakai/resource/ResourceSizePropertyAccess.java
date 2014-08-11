package org.sakaiproject.warehouse.sakai.resource;

import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.warehouse.service.PropertyAccess;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 19, 2005
 * Time: 12:50:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResourceSizePropertyAccess implements PropertyAccess {

	private ContentHostingService contentHostingService;

	public ContentHostingService getContentHostingService() {
		return contentHostingService;
	}

	public void setContentHostingService(ContentHostingService contentHostingService) {
		this.contentHostingService = contentHostingService;
	}
	
    public Object getPropertyValue(Object source) throws Exception {

     ContentCollection collection = (ContentCollection) source;
      return getContentHostingService().getQuota(collection);
   }
}
