package org.sakaiproject.metaobj.shared.mgt;

import java.util.List;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Mar 12, 2007
 * Time: 10:24:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class AdditionalFormConsumers {
   private List additionalConsumers;
   private StructuredArtifactDefinitionManager manager;

   public List getAdditionalConsumers() {
      return additionalConsumers;
   }

   public void setAdditionalConsumers(List additionalConsumers) {
      this.additionalConsumers = additionalConsumers;
   }

   public StructuredArtifactDefinitionManager getManager() {
      return manager;
   }

   public void setManager(StructuredArtifactDefinitionManager manager) {
      this.manager = manager;
   }

   public void init() {
      for (Iterator<FormConsumer> i = getAdditionalConsumers().iterator();i.hasNext();) {
         getManager().addConsumer(i.next());
      }
   }
}
