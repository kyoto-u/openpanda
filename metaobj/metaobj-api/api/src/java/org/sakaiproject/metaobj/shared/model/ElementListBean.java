/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/metaobj/tags/sakai-10.4/metaobj-api/api/src/java/org/sakaiproject/metaobj/shared/model/ElementListBean.java $
 * $Id: ElementListBean.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
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

package org.sakaiproject.metaobj.shared.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Mar 11, 2004
 * Time: 3:55:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class ElementListBean extends ArrayList implements LimitedList {
   protected final Log logger = LogFactory.getLog(getClass());

   private Element parentElement;
   private SchemaNode schema;
   private boolean deferValidation = false;

   public ElementListBean() {

   }

   public ElementListBean(Element parentElement, SchemaNode schema, boolean deferValidation) {
      this.parentElement = parentElement;
      this.schema = schema;
      this.deferValidation = deferValidation;
   }

   public ElementListBean(Element parentElement, List elements,
                          SchemaNode schema, boolean deferValidation) {
      super(elements);
      this.parentElement = parentElement;
      this.schema = schema;
      this.deferValidation = deferValidation;
      for (Iterator i=iterator();i.hasNext();) {
         ElementBean bean = (ElementBean) i.next();
         bean.setParent(this);
      }
   }

   public ElementListBean(List elements, SchemaNode schema) {
      super(elements);
      this.schema = schema;
   }

   public Object get(int index) {
      logger.debug("get called with index " + index);
      return super.get(index);
   }


   public ElementBean createBlank() {
      return new ElementBean(new Element(schema.getName()), schema, deferValidation);
   }

   /**
    * Removes the element at the specified position in this list.
    * Shifts any subsequent elements to the left (subtracts one from their
    * indices).
    *
    * @param index the index of the element to removed.
    * @return the element that was removed from the list.
    * @throws IndexOutOfBoundsException if index out of range <tt>(index
    *                                   &lt; 0 || index &gt;= size())</tt>.
    */
   public Object remove(int index) {
      ElementBean bean = (ElementBean) get(index);

      bean.getBaseElement().getParent().removeContent(bean.getBaseElement());

      return super.remove(index);
   }

   /**
    * Appends the specified element to the end of this list.
    *
    * @param o element to be appended to this list.
    * @return <tt>true</tt> (as per the general contract of Collection.add).
    */
   public boolean add(Object o) {
      ElementBean bean = (ElementBean) o;

      parentElement.addContent(bean.getBaseElement());

      bean.setParent(this);

      return super.add(o);
   }

   /**
    * Inserts the specified element at the specified position in this
    * list. Shifts the element currently at that position (if any) and
    * any subsequent elements to the right (adds one to their indices).
    *
    * @param index   index at which the specified element is to be inserted.
    * @param element element to be inserted.
    * @throws IndexOutOfBoundsException if index is out of range
    *                                   <tt>(index &lt; 0 || index &gt; size())</tt>.
    */
   public void add(int index, Object element) {
      add(element);

      super.add(index, element);
   }

   public boolean addAll(Collection collection) {
      for (Iterator i=collection.iterator();i.hasNext();) {
         ElementBean bean = createBlank();
         bean.getBaseElement().addContent((String) i.next());
         add(bean);
      }

      return true;
   }

   public void clear() {
      while (size() > 0) {
         remove(0);
      }
   }

   public int getUpperLimit() {
      if (schema.getMaxOccurs() == -1) {
         return Integer.MAX_VALUE;
      }
      return schema.getMaxOccurs();
   }

   public int getLowerLimit() {
      return schema.getMinOccurs();
   }
}
