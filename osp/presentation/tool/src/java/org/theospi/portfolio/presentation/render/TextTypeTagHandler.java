/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/presentation/tool/src/java/org/theospi/portfolio/presentation/render/TextTypeTagHandler.java $
* $Id:TextTypeTagHandler.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
***********************************************************************************
*
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
package org.theospi.portfolio.presentation.render;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import org.sakaiproject.jsf.component.InputRichTextComponent;
import org.sakaiproject.jsf.util.RendererUtil;
import org.theospi.jsf.intf.ComponentWrapper;
import org.theospi.jsf.intf.XmlDocumentContainer;
import org.theospi.jsf.intf.XmlTagFactory;
import org.theospi.portfolio.presentation.component.RegionComponent;
import org.theospi.portfolio.presentation.component.SequenceComponent;
import org.theospi.portfolio.presentation.model.PresentationPageRegion;
import org.theospi.portfolio.presentation.tool.DecoratedRegion;
import org.theospi.portfolio.presentation.tool.RegionMap;
import org.theospi.portfolio.shared.tool.RichTextValidator;
import org.xml.sax.Attributes;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 4, 2006
 * Time: 6:59:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class TextTypeTagHandler extends LayoutPageHandlerBase {
   
   public final static String ATTR_COLS = "cols";
   public final static String ATTR_ROWS = "rows";
   public final static String ATTR_WIDTH = "width";
   public final static String ATTR_HEIGHT = "height";
   public final static String ATTR_ISRICHTEXT = "isRichText";
   

   public TextTypeTagHandler(XmlTagFactory factory) {
      super(factory);
   }

   public ComponentWrapper startElement(FacesContext context, ComponentWrapper parent, String uri,
                                        String localName, String qName, Attributes attributes) throws IOException {
      UIViewRoot root = context.getViewRoot();
      XmlDocumentContainer parentContainer = getParentContainer(parent.getComponent());
      String mapVar = parentContainer.getVariableName();
      RegionComponent parentRegion = (RegionComponent) parent.getComponent();
      Map sizeAttributeMap = new HashMap();

      boolean richEdit = false;

      if (attributes.getValue("isRichText") != null) {
         richEdit = Boolean.valueOf(attributes.getValue("isRichText")).booleanValue();
      }
      
      if (attributes.getValue(ATTR_COLS) != null) {
         sizeAttributeMap.put(ATTR_COLS, (String)attributes.getValue(ATTR_COLS));
      }
      if (attributes.getValue(ATTR_ROWS) != null) {
         sizeAttributeMap.put(ATTR_ROWS, (String)attributes.getValue(ATTR_ROWS));
      }
      if (attributes.getValue(ATTR_WIDTH) != null) {
         sizeAttributeMap.put(ATTR_WIDTH, (String)attributes.getValue(ATTR_WIDTH));
      }
      if (attributes.getValue(ATTR_HEIGHT) != null) {
         sizeAttributeMap.put(ATTR_HEIGHT, (String)attributes.getValue(ATTR_HEIGHT));
      }
      
      UIComponent input;

      if (richEdit) {
         input = createRichTextRegion(context, root, mapVar, parentRegion.getRegionId(),  parent, sizeAttributeMap);
      }
      else {
         input = createTextRegion(context, root, mapVar, parentRegion.getRegionId(),  parent, sizeAttributeMap);
      }

      ValueBinding vbValue = context.getApplication().createValueBinding(
            "#{"+mapVar+ "." + parentRegion.getRegionId() + ".item.value}");
      input.setValueBinding("value", vbValue);

      boolean sequenceParent = false;
      if (parentContainer instanceof SequenceComponent) {
         sequenceParent = true;
         XmlDocumentContainer parentParentContainer = getParentContainer(((UIComponent)parentContainer).getParent());
         mapVar = parentParentContainer.getVariableName();
      }

      ValueBinding vbRegion = context.getApplication().createValueBinding(
            "#{"+mapVar+ "." + parentRegion.getRegionId() + "}");
      if (vbRegion.getValue(context) == null) {
         // need to add default region
         ValueBinding vbRegionMap = context.getApplication().createValueBinding(
               "#{"+mapVar + "}");
         RegionMap map = (RegionMap) vbRegionMap.getValue(context);
         PresentationPageRegion region = new PresentationPageRegion();
         region.setRegionId(parentRegion.getRegionId());
         region.setType(richEdit?"richtext":"text");
         region.setItems(new ArrayList());
         region.setHelpText(attributes.getValue("helpText"));
         map.getPage().getRegions().add(region);
         if (!sequenceParent) {
            region.addBlank();
         }
         DecoratedRegion decoratedRegion = new DecoratedRegion(map, region);
         map.put(parentRegion.getRegionId(), decoratedRegion);
      }
      return new ComponentWrapper(parent, parent.getComponent(), this);
   }

   public void characters(FacesContext context, ComponentWrapper current, char[] ch, int start, int length) throws IOException {
   }

   public void endElement(FacesContext context, ComponentWrapper current, String uri, String localName, String qName) throws IOException {
   }

   protected UIComponent createRichTextRegion(FacesContext context, UIViewRoot root, String mapVar,
                                   String regionId, ComponentWrapper parent, Map sizeAttributeMap) {
      InputRichTextComponent input = (InputRichTextComponent) context.getApplication().createComponent(
         "org.sakaiproject.InputRichText");
      input.setId(root.createUniqueId());
      ValueBinding attachedFiles = context.getApplication().createValueBinding("#{freeForm.attachableItems}");
      input.setValueBinding("attachedFiles", attachedFiles);
      
      if (sizeAttributeMap.get(ATTR_ROWS) != null) {
         String rows = (String)sizeAttributeMap.get(ATTR_ROWS);
         RendererUtil.setAttribute(context, input, ATTR_ROWS, Integer.valueOf(rows));
      }
      if (sizeAttributeMap.get(ATTR_COLS) != null) {
         String cols = (String)sizeAttributeMap.get(ATTR_COLS);
         RendererUtil.setAttribute(context, input, ATTR_COLS, Integer.valueOf(cols));
      }
      
      if (sizeAttributeMap.get(ATTR_WIDTH) != null) {
         String width = (String)sizeAttributeMap.get(ATTR_WIDTH);
         RendererUtil.setAttribute(context, input, ATTR_WIDTH, Integer.valueOf(width));
      }
      
      if (sizeAttributeMap.get(ATTR_HEIGHT) != null) {
         String height = (String)sizeAttributeMap.get(ATTR_HEIGHT);
         RendererUtil.setAttribute(context, input, ATTR_HEIGHT, Integer.valueOf(height));
      }
      
      parent.getComponent().getChildren().add(input);
      input.addValidator(new RichTextValidator());
      return input;
   }

   protected UIComponent createTextRegion(FacesContext context, UIViewRoot root, String mapVar,
                                   String regionId, ComponentWrapper parent, Map sizeAttributeMap) {
      UIComponent input = null;
      
      if (sizeAttributeMap.get(ATTR_ROWS) != null || sizeAttributeMap.get(ATTR_COLS) != null) {
         input = (HtmlInputTextarea) context.getApplication().createComponent(HtmlInputTextarea.COMPONENT_TYPE);
      }
      else {
         input = (HtmlInputText) context.getApplication().createComponent(HtmlInputText.COMPONENT_TYPE);
      }
      
      input.setId(root.createUniqueId());
      if (sizeAttributeMap.get(ATTR_ROWS) != null) {
         String rows = (String)sizeAttributeMap.get(ATTR_ROWS);
         ((HtmlInputTextarea)input).setRows(Integer.parseInt(rows));
      }
      if (sizeAttributeMap.get(ATTR_COLS) != null) {
         String cols = (String)sizeAttributeMap.get(ATTR_COLS);
         ((HtmlInputTextarea)input).setCols(Integer.parseInt(cols));
      }
      
      parent.getComponent().getChildren().add(input);
      return input;
   }

}
