/**********************************************************************************
* $URL:https://source.sakaiproject.org/svn/osp/trunk/warehouse/api-impl/src/java/org/theospi/portfolio/util/db/GenericTableHandler.java $
* $Id:GenericTableHandler.java 9134 2006-05-08 20:28:42Z chmaurer@iupui.edu $
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
package org.sakaiproject.warehouse.util.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.sakaiproject.warehouse.util.db.Cascade;
import org.sakaiproject.warehouse.util.db.DbLoader;

/**
 * Generic table handler will work for most databases. If not,
 * then you can create database specific handlers having this as the
 * starting point.
 *
 * This code was inspired from the Dbloader.java from uPortal by JASIG
 *
 * @author <a href="felipeen@udel.edu">Luis F.C. Mendes</a> - University of Delaware
 * @version $Revision 1.0 $
 */
public class GenericTableHandler implements ContentHandler{

   private static final int UNSET = -1;
   private static final int DROP = 0;
   private static final int CREATE = 1;
   private static final int ALTER = 2;
   private static final int INDEX = 3;
   private static int mode = UNSET;
   private static StringBuilder stmtBuffer;
   private int treeLevel;
   private String tmpType = "";
   private String tmpParm = "";
   private DbLoader loader;

   protected final Log logger = LogFactory.getLog(getClass());

   public GenericTableHandler(DbLoader loader){
     this.loader = loader;
     logger.debug("Generic table handler for " + this.loader.getDbName() + " ...");
   }


   public void startDocument ()
   {
   }

   public void endDocument ()
   {
      //System.out.println();
   }

   public void startElement (String namespaceURI, String localName,
                             String qName, Attributes atts)
   {
      if (qName.equals("statement"))
      {
       stmtBuffer = new StringBuilder(1024);
       String statementType = atts.getValue("type");

       if (mode == UNSET || mode != DROP
          && statementType != null && statementType.equals("drop"))
       {
         mode = DROP;

         logger.debug("Dropping tables...");

         if (!this.loader.isDropTables())
            logger.debug("disabled.");
       }
       else if (mode == UNSET || mode != CREATE
              && statementType != null && statementType.equals("create"))
       {
         mode = CREATE;

         logger.debug("Creating tables...");

         if (!this.loader.isCreateTables())
            logger.debug("disabled.");
       }
       else if (mode == UNSET || mode != ALTER
              && statementType != null && statementType.equals("alter"))
       {
         mode = ALTER;

         logger.debug("Altering tables...");

         if (!this.loader.isAlterTables())
            logger.debug("disabled.");
       }
       else if (mode == UNSET || mode != INDEX
              && statementType != null && statementType.equals("index"))
       {
         mode = INDEX;

         logger.debug("Indexing tables...");

         if (!this.loader.isIndexTables())
            logger.debug("disabled.");
       }

      }

     if (qName.equals("column-type")){
       ++treeLevel;
       tmpType = "";
     }
     if (qName.equals("type-param")){
       ++treeLevel;
       tmpParm = "";
     }
   }

   public void endElement (String namespaceURI, String localName, String qName)
   {
      if (qName.equals("statement"))
      {
       treeLevel = 0;
       String statement = stmtBuffer.toString();

       switch (mode)
       {
         case DROP:
            if (this.loader.isDropTables())
               this.loader.dropTable(Cascade.cascadeConstraint(statement));
            //System.out.println(statement);
            break;
         case CREATE:
            if (this.loader.isCreateTables())
              this.loader.createTable(statement);
            //System.out.println(statement);
            break;
         case ALTER:
            if (this.loader.isAlterTables())
              this.loader.alterTable(statement);
            //System.out.println(statement);
            break;
         case INDEX:
            if (this.loader.isIndexTables())
              this.loader.indexTable(statement);
            //System.out.println(statement);
            break;
         default:
            break;
       }
      }

     if(qName.equals("column-type"))
       --treeLevel;
     if(qName.equals("type-param"))
       --treeLevel;
     if(treeLevel == 0)
       parseParamToDatabase();
   }

   public void characters (char ch[], int start, int length)
   {
     if(treeLevel == 0)
       stmtBuffer.append(ch, start, length);
     else
       if(treeLevel == 1)
         tmpType +=  new String(ch, start, length);
       else
         if(treeLevel == 2)
            tmpParm +=  new String(ch, start, length);
      //chmaurer 1/24/06
      //Changed the tmpType and tmpParm to do a += due to some strange
      // buffering issues.  We were seeing data types being truncated 
      // if they fell at the end of the ch[] and were cut off.
   }

   protected void parseParamToDatabase(){
     if(tmpParm != null && tmpParm.length() > 0){
       stmtBuffer.append(tmpType.trim());
       stmtBuffer.append("(" + tmpParm.trim() + ")");
     }
     else
       if(tmpType != null && tmpType.length() > 0)
         stmtBuffer.append(tmpType.trim());

     tmpParm = "";
     tmpType = "";
   }



   public void setDocumentLocator (Locator locator)
   {
   }

   public void processingInstruction (String target, String data)
   {
   }

   public void ignorableWhitespace (char[] ch, int start, int length)
   {
   }

   public void startPrefixMapping (String prefix, String uri)
     throws SAXException {};
   public void endPrefixMapping (String prefix) throws SAXException  {};
   public void skippedEntity(String name) throws SAXException {};
}
