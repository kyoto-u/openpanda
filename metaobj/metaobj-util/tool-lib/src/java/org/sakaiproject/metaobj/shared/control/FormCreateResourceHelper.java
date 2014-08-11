package org.sakaiproject.metaobj.shared.control;

import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.CancelableController;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.metaobj.shared.model.StructuredArtifactDefinitionBean;
import org.sakaiproject.content.api.ResourceEditingHelper;
import org.sakaiproject.content.api.ResourceToolActionPipe;
import org.sakaiproject.content.api.ResourceToolAction;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.api.ActiveTool;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.exception.IdUnusedException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.Errors;

import java.util.*;
import java.util.Map.Entry;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Jan 29, 2007
 * Time: 11:07:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class FormCreateResourceHelper implements Controller, FormController, CancelableController {

   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;

   public ModelAndView handleRequest(Object requestModel,
                                     Map request, Map session, Map application, Errors errors) {
      FormCreateHelperBean bean = (FormCreateHelperBean) requestModel;
      
      if (bean.getFormId() == null || bean.getFormId().length() == 0) {
         errors.rejectValue("formId", "FORM_ID_REQUIRED");
         return null;
      }
      
      session.put(ResourceEditingHelper.CREATE_SUB_TYPE, bean.getFormId());

      return new ModelAndView("formHelper");
   }

   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
      return structuredArtifactDefinitionManager;
   }

   public void setStructuredArtifactDefinitionManager(StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }

   /**
    * Create a map of all data the form requries.
    * Useful for building up drop down lists, etc.
    *
    * @param request
    * @param command
    * @param errors
    * @return ref data
    */
   public Map referenceData(Map request, Object command, Errors errors) {
      Map ref = new Hashtable();

      Map<String, List> homes = getStructuredArtifactDefinitionManager().findCategorizedHomes(false);
      List categorizedHomes = new ArrayList();
      for (Iterator i=homes.entrySet().iterator();i.hasNext();) {
          try {
        	  Entry<String, List> entry = (Entry)i.next();
             Site site = SiteService.getSite(entry.getKey());
             List homesList = entry.getValue();
             Collections.sort(homesList);
             categorizedHomes.add(new SiteHomeWrapper(site, homesList));
          } catch (IdUnusedException e) {
             throw new RuntimeException(e);
          }
       }
      
      Collections.sort(categorizedHomes);
      
      ref.put("categorizedFormList", categorizedHomes);
      List globalHomes = getStructuredArtifactDefinitionManager().findGlobalHomes();
      globalHomes = filterHidden(globalHomes);
      Collections.sort(globalHomes);
      ref.put("globalForms", globalHomes);
      return ref;
   }

   protected List filterHidden(List globalHomes) {
      for (Iterator<StructuredArtifactDefinitionBean> i=globalHomes.iterator();i.hasNext();) {
         if (i.next().isSystemOnly()) {
            i.remove();
         }
      }
      
      return globalHomes;
   }

   public boolean isCancel(Map request) {
      Object cancel = request.get("canceling");
      if (cancel == null) {
         return false;
      }
      return cancel.equals("true");
   }

   public ModelAndView processCancel(Map request, Map session, Map application,
                                     Object command, Errors errors) throws Exception {
      ResourceToolActionPipe pipe = (ResourceToolActionPipe)session.get(ResourceToolAction.ACTION_PIPE);
      pipe.setActionCanceled(true);
      pipe.setActionCompleted(false);
      return new ModelAndView("cancel");
   }

}
