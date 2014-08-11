/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/common/api-impl/src/java/org/theospi/portfolio/guidance/impl/GuidanceManagerImpl.java $
* $Id:GuidanceManagerImpl.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.theospi.portfolio.guidance.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.metaobj.shared.mgt.ContentEntityUtil;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.MimeType;
import org.sakaiproject.metaobj.security.AllowMapSecurityAdvisor;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.theospi.portfolio.guidance.mgt.GuidanceManager;
import org.theospi.portfolio.guidance.model.Guidance;
import org.theospi.portfolio.guidance.model.GuidanceItem;
import org.theospi.portfolio.guidance.model.GuidanceItemAttachment;
import org.theospi.portfolio.security.AuthorizationFacade;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Nov 11, 2005
 * Time: 1:00:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuidanceManagerImpl extends HibernateDaoSupport implements GuidanceManager {

   protected final Log logger = LogFactory.getLog(getClass());

   private AuthorizationFacade authorizationFacade;
   private SecurityService securityService;
   private EntityManager entityManager;
   private IdManager idManager;
   private ContentHostingService contentHostingService;

   public Guidance createNew(String description, String siteId, Id securityQualifier,
                             String securityViewFunction, String securityEditFunction) {
      Guidance guidance = new Guidance(getIdManager().createId(),
         description, siteId, securityQualifier, securityViewFunction, securityEditFunction);
/*
      GuidanceItem instruction = new GuidanceItem(guidance, Guidance.INSTRUCTION_TYPE);
      guidance.getItems().add(instruction);

      GuidanceItem example = new GuidanceItem(guidance, Guidance.EXAMPLE_TYPE);
      guidance.getItems().add(example);

      GuidanceItem rationale = new GuidanceItem(guidance, Guidance.RATIONALE_TYPE);
      guidance.getItems().add(rationale);

      GuidanceItem rubric = new GuidanceItem(guidance, Guidance.RUBRIC_TYPE);
      guidance.getItems().add(rubric);
      
      GuidanceItem expectations = new GuidanceItem(guidance, Guidance.EXPECTATIONS_TYPE);
      guidance.getItems().add(expectations);
    */  
      return guidance;
   }

   public Guidance getGuidance(Id guidanceId) {
      return getGuidance(guidanceId, true);
   }

   protected Guidance getGuidance(Id guidanceId, boolean checkAuthz) {
      Guidance guidance = (Guidance)getHibernateTemplate().get(Guidance.class, guidanceId);

      if (guidance == null) {
         return null;
      }

      if (guidance.getSecurityQualifier() != null && checkAuthz) {
         getAuthorizationFacade().checkPermission(guidance.getSecurityViewFunction(),
            guidance.getSecurityQualifier());
      }

      if (assureAccess(guidance)) {
         getHibernateTemplate().save(guidance);
      }

      return guidance;
   }

   /**
    * Pushes the files in the guidance into the security bypass advisor thus allowing the 
    * files to be read.
    * @return boolean whether or not the guidance has been changed
    */
   public boolean assureAccess(Guidance guidance) {
      boolean changed = false;
      // setup access to the files
      List refs = new ArrayList();
      for (Iterator i=guidance.getItems().iterator();i.hasNext();) {
         GuidanceItem item = (GuidanceItem)i.next();
         for (Iterator j=item.getAttachments().iterator();j.hasNext();) {
            GuidanceItemAttachment attachment = (GuidanceItemAttachment)j.next();
            if (checkAttachment(attachment)) {
               refs.add(attachment.getBaseReference().getBase().getReference());
            }
            else {
               j.remove();
               String guidanceText = item.getText();
               int fileLocation = -1;

                  String encodedRef = attachment.getBaseReference().getBase().getReference();
                  encodedRef = encodedRef.replaceAll(" ", "%20");
                  do {
                     fileLocation = guidanceText.indexOf(encodedRef);
                     if(fileLocation >= 0) {
                        int startChar = guidanceText.lastIndexOf("<a", fileLocation);
                        int lastChar = guidanceText.indexOf("</a", fileLocation);
                        lastChar = guidanceText.indexOf(">", lastChar)+ 1;
                        guidanceText = guidanceText.substring(0, startChar) + "--File Deleted--" + guidanceText.substring(lastChar);
                     }
                  } while(fileLocation > 0);
                  item.setText(guidanceText);

               changed = true;
            }
         }
      }

      getSecurityService().pushAdvisor(new AllowMapSecurityAdvisor(ContentHostingService.EVENT_RESOURCE_READ,
         refs));

      return changed;
   }

   /**
    * This checks for the existance of a resource in a non-permission checking way.
    * If there isn't a uuid for a resource or there isn't a resolved id then it doesn't exist
    * @param attachment
    * @return boolean true if a resource exists, false if it does not
    */
   protected boolean checkAttachment(GuidanceItemAttachment attachment) {
      String id = attachment.getBaseReference().getBase().getId();

      String uuid = getContentHostingService().getUuid(id);
      if(uuid == null)
            return false;
      return getContentHostingService().resolveUuid(uuid) != null;
   }

   public Guidance saveGuidance(Guidance guidance) {
      if (guidance.isNewObject()) {
         guidance.setNewId(guidance.getId());
         guidance.setId(null);
         getHibernateTemplate().save(guidance);
         guidance.setNewObject(false);
      }
      else {
         getHibernateTemplate().saveOrUpdate(guidance);
      }

      return guidance;
   }

   public void deleteGuidance(Guidance guidance) {
      getHibernateTemplate().delete(guidance);
   }

   public Reference decorateReference(Guidance guidance, String reference) {
      String fullRef = ContentEntityUtil.getInstance().buildRef(GuidanceEntityProducer.GUIDANCE_PRODUCER,
         guidance.getSiteId(), guidance.getId().getValue(), reference);

      return getEntityManager().newReference(fullRef);
   }

   public List listGuidances(String siteId) {
      return getHibernateTemplate().findByNamedQuery("listGuidancesBySite", siteId);
   }

   public Guidance getGuidance(String id) {
      return getGuidance(id, true);
   }

   public Guidance getGuidance(String id, boolean checkAuthz) {
      return getGuidance(getIdManager().getId(id), checkAuthz);
   }

   public void packageGuidanceForExport(List guidanceIds, OutputStream os) throws IOException {
      CheckedOutputStream checksum = new CheckedOutputStream(os,
            new Adler32());
      ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(
            checksum));
      List exportedRefs = new ArrayList();
      for (Iterator i=guidanceIds.iterator();i.hasNext();) {
         String id = (String) i.next();
         processGuidance(id, zos, exportedRefs);
      }

      zos.finish();
      zos.flush();
   }

   protected void processGuidance(String guidanceId, ZipOutputStream zos, List exportedRefs) throws IOException {
      Guidance guidance = getGuidance(guidanceId);
      for (Iterator i=guidance.getItems().iterator();i.hasNext();) {
         GuidanceItem item = (GuidanceItem) i.next();
         try {
            processItem(zos, item, exportedRefs);
         }
         catch (ServerOverloadException e) {
            throw new RuntimeException(e);
         }
      }

      ZipEntry definitionFile = new ZipEntry("guidance-"+guidanceId+".xml");
      zos.putNextEntry(definitionFile);
      Document doc = createGuidanceAsXml(guidance);
      String docStr = (new XMLOutputter()).outputString(doc);
      zos.write(docStr.getBytes("UTF-8"));
      zos.closeEntry();
   }

   protected void processItem(ZipOutputStream zos, GuidanceItem item, List exportedRefs)
         throws IOException, ServerOverloadException {
      for (Iterator i=item.getAttachments().iterator();i.hasNext();) {
         GuidanceItemAttachment attachment = (GuidanceItemAttachment)i.next();
         if (!exportedRefs.contains(attachment.getBaseReference().getBase().getReference())) {
            processAttachment(zos, attachment);
            exportedRefs.add(attachment.getBaseReference().getBase().getReference());
         }
      }
   }

   protected void processAttachment(ZipOutputStream zos, GuidanceItemAttachment attachment)
         throws ServerOverloadException, IOException {
      ContentResource entity = (ContentResource) attachment.getBaseReference().getBase().getEntity();

      String newName = entity.getProperties().getProperty(entity.getProperties().getNamePropDisplayName());
      String cleanedName = newName.substring(newName.lastIndexOf('\\')+1);
      String entryName = "attachments/" + entity.getContentType() + "/" +
         getAttachmentRefHash(attachment) + "/" + cleanedName;

      storeFileInZip(zos, entity.streamContent(), entryName);
   }

   protected int getAttachmentRefHash(GuidanceItemAttachment attachment) {
      return attachment.getBaseReference().getBase().getReference().hashCode();
   }

   protected void storeFileInZip(ZipOutputStream zos, InputStream in, String entryName)
      throws IOException {

      byte data[] = new byte[1024 * 10];

      if (File.separatorChar == '\\') {
         entryName = entryName.replace('\\', '/');
      }

      ZipEntry newfileEntry = new ZipEntry(entryName);

      zos.putNextEntry(newfileEntry);

      BufferedInputStream origin = new BufferedInputStream(in, data.length);

      int count;
      while ((count = origin.read(data, 0, data.length)) != -1) {
         zos.write(data, 0, count);
      }
      zos.closeEntry();
      in.close();
   }

   protected Document createGuidanceAsXml(Guidance guidance) {
      Element rootNode = new Element("guidance");

      rootNode.setAttribute("formatVersion", "2.1");
      addNode(rootNode, "id", guidance.getId().getValue());
      addNode(rootNode, "description", guidance.getDescription());
      addNode(rootNode, "securityEditFunction", guidance.getSecurityEditFunction());
      addNode(rootNode, "securityViewFunction", guidance.getSecurityViewFunction());
      Element items = new Element("items");

      for (Iterator i=guidance.getItems().iterator();i.hasNext();) {
         GuidanceItem item = (GuidanceItem) i.next();
         addItem(items, item);
      }

      rootNode.addContent(items);

      return new Document(rootNode);
   }

   protected void addItem(Element items, GuidanceItem item) {
      Element itemElement = new Element("item");

      addNode(itemElement, "type", item.getType());
      addNode(itemElement, "text", item.getText());

      Element attachments = new Element("attachments");

      for (Iterator i=item.getAttachments().iterator();i.hasNext();) {
         GuidanceItemAttachment attachment = (GuidanceItemAttachment) i.next();
         addAttachment(attachments, attachment);
      }
      itemElement.addContent(attachments);
      items.addContent(itemElement);
   }

   protected void addAttachment(Element attachments, GuidanceItemAttachment attachment) {
      Element attachmentElement = new Element("attachment");
      addNode(attachmentElement, "ref", attachment.getBaseReference().getBase().getReference());
      addNode(attachmentElement, "url", attachment.getFullReference().getBase().getUrl());
      attachments.addContent(attachmentElement);
   }

   protected void addNode(Element parentNode, String name, String value) {
      Element attrNode = new Element(name);
      attrNode.addContent(new CDATA(value));
      parentNode.addContent(attrNode);
   }

   /**
    * This function is up to spec but incomplete.  The guidance security qualifier
    * needs to be set on these objects so they can be retrieved.
    * 
    * @param parent the parent resource folder where attachments go
    * @param siteId the site which will recieve the imported "stuff"
    * @param in  The Input stream representing the output stream of the export
    * @return Map contains a map with keys being of type String as old Ids and the 
    * values as being the Guidance object
    */
   public Map importGuidanceList(ContentCollection parent, String siteId, InputStream in) throws IOException {
      Map guidanceMap = new Hashtable();
      ZipInputStream zis = new ZipInputStream(in);
      ZipEntry currentEntry = zis.getNextEntry();

      Map attachmentMap = new Hashtable();

      while (currentEntry != null) {
          if (!currentEntry.isDirectory()) {
            if (currentEntry.getName().startsWith("guidance-")) {
               importGuidance(siteId, zis, guidanceMap);
            }
            else if (currentEntry.getName().startsWith("attachments/")) {
               importAttachmentRef(parent, currentEntry, siteId, zis, attachmentMap);
            }
          }

         zis.closeEntry();
         currentEntry = zis.getNextEntry();
      }

      postPocessAttachments(guidanceMap.values(), attachmentMap);

      for (Iterator i=guidanceMap.values().iterator();i.hasNext();) {
         Guidance guidance = (Guidance) i.next();
         saveGuidance(guidance);
      }

      return guidanceMap;
   }

   protected void postPocessAttachments(Collection guidances, Map attachmentMap) {
      for (Iterator i=guidances.iterator();i.hasNext();) {
         Guidance guidance = (Guidance) i.next();
         postProcessGuidance(guidance, attachmentMap);
      }
   }

   protected void postProcessGuidance(Guidance guidance, Map attachmentMap) {
      for (Iterator i=guidance.getItems().iterator();i.hasNext();) {
         GuidanceItem item = (GuidanceItem)i.next();
         postProcessGuidanceItem(item, attachmentMap);
      }
   }

   protected void postProcessGuidanceItem(GuidanceItem item, Map attachmentMap) {
      List guidanceAttachments = new ArrayList();

      for (Iterator i=item.getAttachments().iterator();i.hasNext();) {
         AttachmentImportWrapper wrapper = (AttachmentImportWrapper) i.next();
         Reference baseRef = getEntityManager().newReference(
               (String)attachmentMap.get("" + wrapper.getOldRef().hashCode()));
         Reference fullRef = decorateReference(item.getGuidance(), baseRef.getReference());
         GuidanceItemAttachment newAttachment = new GuidanceItemAttachment(item, baseRef, fullRef);
         item.setText(substitueText(wrapper.getOldUrl(), newAttachment, item.getText()));
         guidanceAttachments.add(newAttachment);
      }

      item.setAttachments(guidanceAttachments);
   }

   protected String substitueText(String oldUrl, GuidanceItemAttachment newAttachment, String text) {
      return text.replaceAll(oldUrl, newAttachment.getFullReference().getBase().getUrl());
   }

   protected void importAttachmentRef(ContentCollection fileParent, ZipEntry currentEntry, String siteId,
                                      ZipInputStream zis, Map attachmentMap) {
      File file = new File(currentEntry.getName());

      MimeType mimeType = new MimeType(file.getParentFile().getParentFile().getParentFile().getName(),
         file.getParentFile().getParentFile().getName());

      String contentType = mimeType.getValue();

      String oldId = file.getParentFile().getName();

      try {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         int c = zis.read();

         while (c != -1) {
            bos.write(c);
            c = zis.read();
         }

         String fileId = fileParent.getId() + file.getName();
         ContentResource rez = null;
         try {
            rez = getContentHostingService().getResource(fileId);
         } catch(IdUnusedException iduue) {
            logger.info(iduue);
         }
         if(rez == null) {
            ContentResourceEdit resource = getContentHostingService().addResource(fileId);
            ResourcePropertiesEdit resourceProperties = resource.getPropertiesEdit();
            resourceProperties.addProperty (ResourceProperties.PROP_DISPLAY_NAME, file.getName());
            resource.setContent(bos.toByteArray());
            resource.setContentType(contentType);
            getContentHostingService().commitResource(resource);
            rez = resource;
         }
         attachmentMap.put(oldId, rez.getReference());
      }
      catch (Exception exp) {
         throw new RuntimeException(exp);
      }
   }

   protected void importGuidance(String siteId, InputStream is, Map guidanceMap)
      throws IOException {
      SAXBuilder builder = new SAXBuilder();
      builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // SAK-23131
      Document document	= null;

      byte []bytes = readStreamToBytes(is);

      try {
         document = builder.build(new ByteArrayInputStream(bytes));
      }
      catch (JDOMException e) {
         throw new RuntimeException(e);
      }
      Element docRoot = document.getRootElement();

      Id oldGuidanceId = getIdManager().getId(docRoot.getChildText("id"));
      String description = docRoot.getChildText("description");
      String viewFunc = docRoot.getChildText("securityViewFunction");
      String editFunc = docRoot.getChildText("securityEditFunction");

      Guidance guidance = new Guidance(getIdManager().createId(), description,
            siteId, null, viewFunc, editFunc);

      List itemElements = docRoot.getChild("items").getChildren("item");
      List items = new ArrayList();
      for (Iterator i=itemElements.iterator();i.hasNext();) {
         items.add(importItem(guidance, (Element)i.next()));
      }
      guidance.setItems(items);
      guidanceMap.put(oldGuidanceId.getValue(), guidance);
   }

   protected byte[] readStreamToBytes(InputStream inStream) throws IOException {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      byte data[] = new byte[10*1024];

      int count;
      while ((count = inStream.read(data, 0, 10*1024)) != -1) {
         bytes.write(data, 0, count);
      }
      byte []tmp = bytes.toByteArray();
      bytes.close();
      return tmp;
   }

   protected GuidanceItem importItem(Guidance guidance, Element element) {
      String type = element.getChildText("type");
      GuidanceItem item = new GuidanceItem(guidance, type);
      item.setText(element.getChildText("text"));

      List attachmentElements = element.getChild("attachments").getChildren("attachment");
      List attachments = new ArrayList();
      for (Iterator i=attachmentElements.iterator();i.hasNext();) {
         attachments.add(importAttachment(item, (Element)i.next()));
      }
      item.setAttachments(attachments);

      return item;
   }

   protected AttachmentImportWrapper importAttachment(GuidanceItem item, Element element) {
      return new AttachmentImportWrapper(element.getChildText("ref"),
            element.getChildText("url"));
   }


   /**
    * Pulls all Guidance
    * @return List of Guidance
    */
   public List getGuidanceForWarehousing()
   {
      List guidance = getHibernateTemplate().findByNamedQuery("listGuidances");

      for(Iterator i = guidance.iterator(); i.hasNext(); ) {
         Guidance w = (Guidance)i.next();
      }

      return guidance;
   }

   public AuthorizationFacade getAuthorizationFacade() {
      return authorizationFacade;
   }

   public void setAuthorizationFacade(AuthorizationFacade authorizationFacade) {
      this.authorizationFacade = authorizationFacade;
   }

   public SecurityService getSecurityService() {
      return securityService;
   }

   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }

   public EntityManager getEntityManager() {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }

   public IdManager getIdManager() {
      return idManager;
   }

   public void setIdManager(IdManager idManager) {
      this.idManager = idManager;
   }

   public ContentHostingService getContentHostingService() {
      return contentHostingService;
   }

   public void setContentHostingService(ContentHostingService contentHostingService) {
      this.contentHostingService = contentHostingService;
   }
}
