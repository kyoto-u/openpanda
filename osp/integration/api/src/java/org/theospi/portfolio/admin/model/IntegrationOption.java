/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-10.0/integration/api/src/java/org/theospi/portfolio/admin/model/IntegrationOption.java $
* $Id: IntegrationOption.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
***********************************************************************************
*
 * Copyright (c) 2006, 2008 The Sakai Foundation
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

package org.theospi.portfolio.admin.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IntegrationOption implements Cloneable {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private String label;
   private boolean include = true;

   public IntegrationOption() {
   }

   public IntegrationOption(boolean include, String label) {
      this.include = include;
      this.label = label;
   }

   public IntegrationOption(IntegrationOption copy) {
      this.include = copy.include;
      this.label = copy.label;
   }

   public boolean isInclude() {
      return include;
   }

   public void setInclude(boolean include) {
      this.include = include;
   }

   public String getLabel() {
      return label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   /**
    * Creates and returns a copy of this object.  The precise meaning
    * of "copy" may depend on the class of the object. The general
    * intent is that, for any object <tt>x</tt>, the expression:
    * <blockquote>
    * <pre>
    * x.clone() != x</pre></blockquote>
    * will be true, and that the expression:
    * <blockquote>
    * <pre>
    * x.clone().getClass() == x.getClass()</pre></blockquote>
    * will be <tt>true</tt>, but these are not absolute requirements.
    * While it is typically the case that:
    * <blockquote>
    * <pre>
    * x.clone().equals(x)</pre></blockquote>
    * will be <tt>true</tt>, this is not an absolute requirement.
    * <p/>
    * By convention, the returned object should be obtained by calling
    * <tt>super.clone</tt>.  If a class and all of its superclasses (except
    * <tt>Object</tt>) obey this convention, it will be the case that
    * <tt>x.clone().getClass() == x.getClass()</tt>.
    * <p/>
    * By convention, the object returned by this method should be independent
    * of this object (which is being cloned).  To achieve this independence,
    * it may be necessary to modify one or more fields of the object returned
    * by <tt>super.clone</tt> before returning it.  Typically, this means
    * copying any mutable objects that comprise the internal "deep structure"
    * of the object being cloned and replacing the references to these
    * objects with references to the copies.  If a class contains only
    * primitive fields or references to immutable objects, then it is usually
    * the case that no fields in the object returned by <tt>super.clone</tt>
    * need to be modified.
    * <p/>
    * The method <tt>clone</tt> for class <tt>Object</tt> performs a
    * specific cloning operation. First, if the class of this object does
    * not implement the interface <tt>Cloneable</tt>, then a
    * <tt>CloneNotSupportedException</tt> is thrown. Note that all arrays
    * are considered to implement the interface <tt>Cloneable</tt>.
    * Otherwise, this method creates a new instance of the class of this
    * object and initializes all its fields with exactly the contents of
    * the corresponding fields of this object, as if by assignment; the
    * contents of the fields are not themselves cloned. Thus, this method
    * performs a "shallow copy" of this object, not a "deep copy" operation.
    * <p/>
    * The class <tt>Object</tt> does not itself implement the interface
    * <tt>Cloneable</tt>, so calling the <tt>clone</tt> method on an object
    * whose class is <tt>Object</tt> will result in throwing an
    * exception at run time.
    *
    * @return a clone of this instance.
    * @throws CloneNotSupportedException if the object's class does not
    *                                    support the <code>Cloneable</code> interface. Subclasses
    *                                    that override the <code>clone</code> method can also
    *                                    throw this exception to indicate that an instance cannot
    *                                    be cloned.
    * @see Cloneable
    */
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
