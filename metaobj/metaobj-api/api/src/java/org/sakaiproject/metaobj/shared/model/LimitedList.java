package org.sakaiproject.metaobj.shared.model;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Feb 9, 2007
 * Time: 12:34:44 PM
 * To change this template use File | Settings | File Templates.
 */
public interface LimitedList extends List {

   public int getUpperLimit();

   public int getLowerLimit();

}
