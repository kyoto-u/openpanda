package org.sakaiproject.warehouse.sakai.resource;

import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.warehouse.impl.BaseWarehouseTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 19, 2005
 * Time: 12:38:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContentResourceCollectionWarehouseTask extends BaseWarehouseTask {

    private ContentHostingService contentHostingService;

    protected Collection getItems() {

        List collectionList = new ArrayList();
        try {

            List entities = getContentHostingService().getAllEntities("/");
            for (Iterator i = entities.iterator(); i.hasNext();) {
                ContentEntity entity = (ContentEntity) i.next();
                if (entity.isCollection()) {

                    collectionList.add(entity);
                }
            }

        }
        catch (Exception ignore) {
            logger.warn("trying to get resource, found collection: " + "/");
        }

        return collectionList;

    }

    public ContentHostingService getContentHostingService() {
        return contentHostingService;
    }

    public void setContentHostingService(ContentHostingService contentHostingService) {
        this.contentHostingService = contentHostingService;
    }

}
