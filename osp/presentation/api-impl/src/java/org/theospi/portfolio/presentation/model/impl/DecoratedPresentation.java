package org.theospi.portfolio.presentation.model.impl;

import org.sakaiproject.site.api.Site;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.theospi.portfolio.presentation.model.Presentation;
import org.theospi.portfolio.shared.model.SortableListObject;

/**
 * Created by IntelliJ IDEA.
 * User: bbiltimier
 * Date: Mar 7, 2006
 * Time: 2:13:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecoratedPresentation extends SortableListObject {

   private Presentation presentation;

   public DecoratedPresentation(Presentation pres, Site site) throws UserNotDefinedException {
      super(pres.getId().getValue(), pres.getName(), 
            pres.getDescription(), 
            pres.getOwner(), site, pres.getPresentationType(), 
            pres.getModified());
      
      this.presentation = pres;
   }

   /**
    * @return the presentation
    */
   public Presentation getPresentation() {
      return presentation;
   }

   /**
    * @param presentation the presentation to set
    */
   public void setPresentation(Presentation presentation) {
      this.presentation = presentation;
   }


}
