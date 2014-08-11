package org.theospi.portfolio.warehouse.osp.presentation;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 20, 2005
 * Time: 1:56:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class PresentationLayoutWarehouseTask extends PresentationWarehouseTask {

   protected Collection getItems() {
      return getPresentationManager().getAllPresentationLayouts();
   }

}
