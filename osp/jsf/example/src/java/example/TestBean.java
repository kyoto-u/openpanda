/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/osp/tags/sakai-2.9.0/jsf/example/src/java/example/TestBean.java $
* $Id: TestBean.java 59678 2009-04-03 23:20:50Z arwhyte@umich.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*
**********************************************************************************/
package example;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.theospi.jsf.impl.DefaultXmlTagFactory;
import org.theospi.jsf.impl.DefaultXmlTagHandler;
import org.theospi.jsf.intf.XmlTagFactory;

import example.xml.TestXmlTagFactory;

public class TestBean {

   private String label = "Select Content";
   private boolean disabled = true;
   private boolean rendered = false;
   private String currentStep = "1";
   private List subBeans = new ArrayList();
   private XmlTagFactory factory = null;

   public TestBean() {
      for (int i=0;i<10;i++) {
         subBeans.add(new TestSubBean());
      }
   }

   public String getCurrentStep() {
      return currentStep;
   }
   public void setCurrentStep(String currentStep) {
      this.currentStep = currentStep;
   }
   public boolean isDisabled() {
      return disabled;
   }
   public void setDisabled(boolean disabled) {
      this.disabled = disabled;
   }
   public String getLabel() {
      return label;
   }
   public void setLabel(String label) {
      this.label = label;
   }
   public boolean isRendered() {
      return rendered;
   }
   public void setRendered(boolean rendered) {
      this.rendered = rendered;
   }

   public void processTestButton(ActionEvent event) {
      getSubBeans().add(new TestSubBean());
   }

   public List getSubBeans() {
      return subBeans;
   }

   public void setSubBeans(List subBeans) {
      this.subBeans = subBeans;
   }

   public XmlTagFactory getFactory() {
      if (factory == null) {
         factory = new TestXmlTagFactory();
         ((DefaultXmlTagFactory)factory).setDefaultHandler(new DefaultXmlTagHandler(factory));
      }
      return factory;
   }

   public InputStream getSampleXmlFile() {
      return this.getClass().getResourceAsStream("/xmlDocTagSample.xhtml");
   }

}
