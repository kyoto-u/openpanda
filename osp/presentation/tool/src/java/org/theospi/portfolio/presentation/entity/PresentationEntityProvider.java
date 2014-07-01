package org.theospi.portfolio.presentation.entity;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RESTful;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RequestAware;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RequestStorable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Statisticable;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.entityprovider.extension.RequestGetter;
import org.sakaiproject.entitybroker.entityprovider.extension.RequestStorage;
import org.sakaiproject.entitybroker.entityprovider.search.Search;
import org.sakaiproject.entitybroker.util.AbstractEntityProvider;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.presentation.support.PresentationService;

public class PresentationEntityProvider extends AbstractEntityProvider implements
CoreEntityProvider, RESTful, RequestStorable, RequestAware /*, Statisticable */ {

    private static Log log = LogFactory.getLog(PresentationEntityProvider.class);
    private PresentationService presentationService;


    public static String PREFIX = "osp-presentations";
    public PresentationService getPresentationService() {
        return presentationService;
    }

    private RequestStorage requestStorage;
    @Override
    public void setRequestStorage(RequestStorage requestStorage) {
        this.requestStorage = requestStorage;
    }

    private RequestGetter requestGetter;
    @Override
    public void setRequestGetter(RequestGetter requestGetter) {
        this.requestGetter = requestGetter;
    }


    public void setPresentationService(PresentationService presentationService) {
        this.presentationService = presentationService;
    }

    public String getEntityPrefix() {
        return PREFIX;
    }

    @Override
    public String createEntity(EntityReference ref, Object entity,
            Map<String, Object> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getSampleEntity() {
        return new Presentation();
    }

    @Override
    public void updateEntity(EntityReference ref, Object entity,
            Map<String, Object> params) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Object getEntity(EntityReference ref) {
        Presentation presentation = presentationService.getPresentation(ref.getId());
        
        // workaround as we are getting Unable to handle output request for format
        // if I return the presentation itself
        Presentation outputPresentation = new Presentation();
        
        outputPresentation.setName(presentation.getName());
        
        return outputPresentation;
    }

    @Override
    public void deleteEntity(EntityReference ref, Map<String, Object> params) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<?> getEntities(EntityReference ref, Search search) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getHandledOutputFormats() {
        return new String[] {Formats.XML, Formats.JSON, Formats.HTML };
    }

    @Override
    public String[] getHandledInputFormats() {
        return new String[] {Formats.XML, Formats.JSON, Formats.HTML };
    }

/*    @Override
    public String getAssociatedToolId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getEventKeys() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String> getEventNames(Locale locale) {
        // TODO Auto-generated method stub
        return null;
    }
*/
    @Override
    public boolean entityExists(String id) {
        boolean exists = false;
        Presentation presentation = null;
        
        presentation = presentationService.getPresentation(id);
        
        if (presentation != null) {
            exists = true;
        }
        
        return exists;
    }
}
