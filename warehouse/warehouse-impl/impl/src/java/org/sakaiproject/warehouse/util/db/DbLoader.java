/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/warehouse/tags/sakai-2.9.3/warehouse-impl/impl/src/java/org/sakaiproject/warehouse/util/db/DbLoader.java $
* $Id: DbLoader.java 59691 2009-04-03 23:46:45Z arwhyte@umich.edu $
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/**
 * <p>A tool to set up a OSPI database. This tool was created so that OSPI
 * developers would only have to maintain a single set of xml documents to define
 * the OSPI database schema and data.  Previously it was necessary to maintain
 * different scripts for each database we wanted to support.</p>
 *
 * <p>DbLoader reads the generic types that are specified in tables.xml and
 * tries to map them to local types by querying the database metadata via methods
 * implemented by the JDBC driver.  Fallback mappings can be supplied in
 * dbloader.xml for cases where the JDBC driver is not able to determine the
 * appropriate mapping.  Such cases will be reported to standard out.</p>
 *
 * <p>An xsl transformation is used to produce the DROP TABLE and CREATE TABLE
 * SQL statements. These statements can be altered by modifying tables.xsl</p>
 *
 * <p> all table names should have lower case names</p>
 *
 * <p>Generic data types (as defined in java.sql.Types) which may be specified
 * in tables.xml include:
 * <code>BIT, TINYINT, SMALLINT, INTEGER, BIGINT, FLOAT, REAL, DOUBLE,
 * NUMERIC, DECIMAL, CHAR, VARCHAR, LONGVARCHAR, DATE, TIME, TIMESTAMP,
 * BINARY, VARBINARY, LONGVARBINARY, NULL, OTHER, JAVA_OBJECT, DISTINCT,
 * STRUCT, ARRAY, BLOB, CLOB, REF</code>
 *
 * <p><strong>WARNING: YOU MAY WANT TO MAKE A BACKUP OF YOUR DATABASE BEFORE RUNNING DbLoader</strong></p>
 *
 * <p>DbLoader will perform the following steps:
 * <ol>
 * <li>Read configurable properties from dbloader.xml</li>
 * <li>Get database connection from DbService</li>
 * <li>Read tables.xml and issue corresponding DROP TABLE and CREATE TABLE SQL statements.</li>
 * <li>Read data.xml and issue corresponding INSERT/UPDATE/DELETE SQL statements.</li>
 * </ol>
 * </p>
 *
 * @author <a href="kweiner@interactivebusiness.com">Ken Weiner</a>, kweiner@interactivebusiness.com
 * @author modified and adapted to OSPI by <a href="felipeen@udel.edu">Luis F.C. Mendes</a> - University of Delaware
 * @version $Revision: 59691 $
 * @see java.sql.Types
 */
public class DbLoader {

   protected final Log logger = LogFactory.getLog(getClass());

    private Connection con;
    private Statement stmt;
    private PreparedStatement pstmt;
    private Document tablesDoc;
    private Document tablesDocGeneric;
    private boolean createTableScript;
    private boolean populateTables;
    private PrintWriter tableScriptOut;
    private boolean dropTables;
    private boolean createTables;
    private String dbName;
    private String dbVersion;
    private String driverName;
    private String driverVersion;
    // Added version 1.8/1.6.2.4
    private boolean alterTables;
    private boolean indexTables;
    private Hashtable tableColumnTypes = new Hashtable();
    private PropertiesHandler propertiesHandler;

   public DbLoader(Connection con) {
      this.con = con;
   }

   public void runLoader(InputStream tables) {
        try {

// Read in the properties
            XMLReader parser = getXMLReader();
            readProperties(parser, getClass().getResourceAsStream("dbloader.xml"));

            //override default properties
            propertiesHandler.properties.setDropTables(
                  ServerConfigurationService.getString("sakai.datawarehouse.dbLoader.properties.dropTables", 
                        propertiesHandler.properties.getDropTables()));
            propertiesHandler.properties.setCreateTables(
                  ServerConfigurationService.getString("sakai.datawarehouse.dbLoader.properties.createTables", 
                        propertiesHandler.properties.getCreateTables()));
            propertiesHandler.properties.setAlterTables(
                  ServerConfigurationService.getString("sakai.datawarehouse.dbLoader.properties.alterTables", 
                        propertiesHandler.properties.getAlterTables()));
            propertiesHandler.properties.setIndexTables(
                  ServerConfigurationService.getString("sakai.datawarehouse.dbLoader.properties.indexTables", 
                        propertiesHandler.properties.getIndexTables()));
            propertiesHandler.properties.setPopulateTables(
                  ServerConfigurationService.getString("sakai.datawarehouse.dbLoader.properties.populateTables", 
                        propertiesHandler.properties.getPopulateTables()));
            propertiesHandler.properties.setCreateTableScript(
                  ServerConfigurationService.getString("sakai.datawarehouse.dbLoader.properties.createTableScript", 
                        propertiesHandler.properties.getCreateTableScript()));
             
            propertiesHandler.properties.setTableScriptFileName(
                  ServerConfigurationService.getString("sakai.datawarehouse.dbLoader.properties.tableScriptFileName", 
                        propertiesHandler.properties.getTableScriptFileName()));
            
           //print db info
            printInfo();

// Read drop/create/populate table settings
            dropTables = Boolean.valueOf(propertiesHandler.properties.getDropTables()).booleanValue();
            createTables = Boolean.valueOf(propertiesHandler.properties.getCreateTables()).booleanValue();
            populateTables = Boolean.valueOf(propertiesHandler.properties.getPopulateTables()).booleanValue();
            alterTables = Boolean.valueOf( propertiesHandler.properties.getAlterTables() ).booleanValue();
            indexTables = Boolean.valueOf( propertiesHandler.properties.getIndexTables() ).booleanValue();

// Set up script
            createTableScript = Boolean.valueOf(propertiesHandler.properties.getCreateTableScript()).booleanValue();
            
            if (createTableScript)
                initTableScript();

// read command line arguements to override properties in dbloader.xml

            boolean usetable = false;
            boolean usedata = false;

// okay, start processing


            try {
// Read tables.xml
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder domParser = dbf.newDocumentBuilder();

// Eventually, write and validate against a DTD
//domParser.setFeature ("http://xml.org/sax/features/validation", true);
//domParser.setEntityResolver(new DTDResolver("tables.dtd"));
//tablesURL = DbLoader.class.getResource(PropertiesHandler.properties.getTablesUri());
               tablesDoc = domParser.parse(new InputSource(tables));
            } catch (ParserConfigurationException pce) {
                throw new RuntimeException(pce);
            } catch (Exception e) {
               throw new RuntimeException(e);
            }

// Hold on to tables xml with generic types
            tablesDocGeneric = (Document) tablesDoc.cloneNode(true);

// Replace all generic data types with local data types
            replaceDataTypes(tablesDoc);

// tables.xml + tables.xsl --> DROP TABLE and CREATE TABLE sql statements

            try {
                Result xmlResult = new SAXResult(TableHandlerFactory.getTableHandler(this));
                Source xmlSource = new DOMSource(tablesDoc);
                TransformerFactory tFactory = TransformerFactory.newInstance();
                Transformer transformer = tFactory.newTransformer(
                      new StreamSource(getClass().getResourceAsStream("tables.xsl")));
                transformer.transform(xmlSource, xmlResult);
                //transformer.transform(xmlSource, new StreamResult(new FileOutputStream("tables.out")));
            } catch (TransformerException te) {
               throw new RuntimeException(te);
            }


        } catch (Exception e) {
           throw new RuntimeException(e);
        }
   }

    protected XMLReader getXMLReader() throws SAXException, ParserConfigurationException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        return spf.newSAXParser().getXMLReader();
    }

    protected void printInfo() throws SQLException {
        DatabaseMetaData dbMetaData = con.getMetaData();
        dbName = dbMetaData.getDatabaseProductName();
        dbVersion = dbMetaData.getDatabaseProductVersion();
        driverName = dbMetaData.getDriverName();
        driverVersion = dbMetaData.getDriverVersion();

        logger.debug("Starting DbLoader...");
        logger.debug("Database name: '" + dbName + "'");
        logger.debug("Database version: '" + dbVersion + "'");
        logger.debug("Driver name: '" + driverName + "'");
        logger.debug("Driver version: '" + driverVersion + "'");
        logger.debug("Database url: '" + dbMetaData.getURL() + "'");
    }

    protected void initTableScript() throws java.io.IOException {
        String scriptFileName = System.getProperty("sakai.home") +propertiesHandler.properties.getTableScriptFileName();
        //String scriptFileName = System.getProperty("sakai.home") +propertiesHandler.properties.getScriptFileName() + "." + System.currentTimeMillis();
        //String scriptFileName = propertiesHandler.properties.getScriptFileName() + "." + System.currentTimeMillis();
        //File scriptFile = new File(scriptFileName);
        //if (scriptFile.exists())
        //    scriptFile.delete();
        //scriptFile.createNewFile();
        //if (!scriptFile.exists())
        //   scriptFile.createNewFile();
        String initProperty = "sakai.dw.initializedTables";
        String inited = System.getProperty(initProperty);
        if (inited == null) {
           System.getProperties().setProperty(initProperty, "true");
           File scriptFile = new File(scriptFileName);
           if (scriptFile.exists())
              scriptFile.delete();
        }
        tableScriptOut = new PrintWriter(new BufferedWriter(new FileWriter(scriptFileName, true)), true);
    }

    protected void replaceDataTypes(Document tablesDoc) {
        Element tables = tablesDoc.getDocumentElement();
        NodeList types = tables.getElementsByTagName("type");

        for (int i = 0; i < types.getLength(); i++) {
            Node type = (Node) types.item(i);
            NodeList typeChildren = type.getChildNodes();

            for (int j = 0; j < typeChildren.getLength(); j++) {
                Node text = (Node) typeChildren.item(j);
                String genericType = text.getNodeValue();

                // Replace generic type with mapped local type
                text.setNodeValue(getLocalDataTypeName(genericType));
            }
        }
    }

    protected int getJavaSqlDataTypeOfColumn(Document tablesDocGeneric, String tableName, String columnName) {
        int dataType = 0;
        String hashKey = tableName + File.separator + columnName;

        // try to use cached version first
        if (tableColumnTypes.get(hashKey) != null)
            return ((Integer)tableColumnTypes.get(hashKey)).intValue();

        // Find the right table element
        Element table = getTableWithName(tablesDocGeneric, tableName);

        // Find the columns element within
        Element columns = getFirstChildWithName(table, "columns");

        // Search for the first column who's name is columnName
        for (Node ch = columns.getFirstChild(); ch != null; ch = ch.getNextSibling()) {
            if (ch instanceof Element && ch.getNodeName().equals("column")) {
                Element name = getFirstChildWithName((Element) ch, "name");
                if (getNodeValue(name).equals(columnName)) {
                    // Get the corresponding type and return it's type code
                    Element value = getFirstChildWithName((Element) ch, "type");
                    dataType = getJavaSqlType(getNodeValue(value));
                }
            }
        }

        // store value is hashtable for next call to this method, prevents
        // repeating xml parsing which takes a very long time
        tableColumnTypes.put(hashKey, new Integer(dataType));

        return dataType;
    }

    protected Element getFirstChildWithName(Element parent, String name) {
        Element child = null;
        for (Node ch = parent.getFirstChild(); ch != null; ch = ch.getNextSibling()) {
            if (ch instanceof Element && ch.getNodeName().equals(name)) {
                child = (Element) ch;
                break;
            }
        }

        return child;
    }

    protected Element getTableWithName(Document tablesDoc, String tableName) {
        Element tableElement = null;
        NodeList tables = tablesDoc.getElementsByTagName("table");

        for (int i = 0; i < tables.getLength(); i++) {
            Node table = (Node) tables.item(i);

            for (Node tableChild = table.getFirstChild(); tableChild != null; tableChild = tableChild.getNextSibling()) {
                if (tableChild instanceof Element && tableChild.getNodeName() != null && tableChild.getNodeName().equals("name")) {
                    if (tableName.equals(getNodeValue(tableChild))) {
                        tableElement = (Element) table;
                        break;
                    }
                }
            }
        }

        return tableElement;
    }

    protected String getNodeValue(Node node) {
        String nodeVal = null;

        for (Node ch = node.getFirstChild(); ch != null; ch = ch.getNextSibling()) {
            if (ch instanceof Text)
                nodeVal = ch.getNodeValue();
        }

        return nodeVal;
    }

    protected String getLocalDataTypeName(String genericDataTypeName) {


        String localDataTypeName = null;

        try {
            DatabaseMetaData dbmd = con.getMetaData();
            String dbName = dbmd.getDatabaseProductName();
            String dbVersion = dbmd.getDatabaseProductVersion();
            String driverName = dbmd.getDriverName();
            String driverVersion = dbmd.getDriverVersion();

            // Check for a mapping in DbLoader.xml
            localDataTypeName = propertiesHandler.properties.getMappedDataTypeName(dbName, dbVersion, driverName, driverVersion, genericDataTypeName);

            if (localDataTypeName != null)
                return localDataTypeName;


            // Find the type code for this generic type name
            int dataTypeCode = getJavaSqlType(genericDataTypeName);

            // Find the first local type name matching the type code
            ResultSet rs = dbmd.getTypeInfo();
            try {
                while (rs.next()) {
                    int localDataTypeCode = rs.getInt("DATA_TYPE");

                    if (dataTypeCode == localDataTypeCode) {
                        try {
                            localDataTypeName = rs.getString("TYPE_NAME");
                        } catch (SQLException sqle) {
                        }
                        break;
                    }
                }
            } finally {
                rs.close();
            }

            if (localDataTypeName != null)
                return localDataTypeName;

            // No matching type found, report an error
            logger.error("Error in DbLoader.getLocalDataTypeName()");
            logger.error("Your database driver, '" + driverName + "', version '" + driverVersion + "', was unable to find a local type name that matches the generic type name, '" + genericDataTypeName + "'.");
            logger.error("Please add a mapped type for database '" + dbName + "', version '" + dbVersion + "' inside your properties file and run this program again.");
            logger.error("Exiting...");
        } catch (Exception e) {
            logger.error("Error in DbLoader.getLocalDataTypeName()", e);
        }

        return null;
    }

    protected int getJavaSqlType(String genericDataTypeName) {
        // Find the type code for this generic type name
        int dataTypeCode = 0;

        if (genericDataTypeName.equalsIgnoreCase("BIT"))
            dataTypeCode = Types.BIT; // -7
        else if (genericDataTypeName.equalsIgnoreCase("TINYINT"))
            dataTypeCode = Types.TINYINT; // -6
        else if (genericDataTypeName.equalsIgnoreCase("SMALLINT"))
            dataTypeCode = Types.SMALLINT; // 5
        else if (genericDataTypeName.equalsIgnoreCase("INTEGER"))
            dataTypeCode = Types.INTEGER; // 4
        else if (genericDataTypeName.equalsIgnoreCase("BIGINT"))
            dataTypeCode = Types.BIGINT; // -5
        else if (genericDataTypeName.equalsIgnoreCase("FLOAT"))
            dataTypeCode = Types.FLOAT; // 6
        else if (genericDataTypeName.equalsIgnoreCase("REAL"))
            dataTypeCode = Types.REAL; // 7
        else if (genericDataTypeName.equalsIgnoreCase("DOUBLE"))
            dataTypeCode = Types.DOUBLE; // 8
        else if (genericDataTypeName.equalsIgnoreCase("NUMERIC"))
            dataTypeCode = Types.NUMERIC; // 2
        else if (genericDataTypeName.equalsIgnoreCase("DECIMAL"))
            dataTypeCode = Types.DECIMAL; // 3

        else if (genericDataTypeName.equalsIgnoreCase("CHAR"))
            dataTypeCode = Types.CHAR; // 1
        else if (genericDataTypeName.equalsIgnoreCase("VARCHAR"))
            dataTypeCode = Types.VARCHAR; // 12
        else if (genericDataTypeName.equalsIgnoreCase("LONGVARCHAR"))
            dataTypeCode = Types.LONGVARCHAR; // -1

        else if (genericDataTypeName.equalsIgnoreCase("DATE"))
            dataTypeCode = Types.DATE; // 91
        else if (genericDataTypeName.equalsIgnoreCase("TIME"))
            dataTypeCode = Types.TIME; // 92
        else if (genericDataTypeName.equalsIgnoreCase("TIMESTAMP"))
            dataTypeCode = Types.TIMESTAMP; // 93

        else if (genericDataTypeName.equalsIgnoreCase("BINARY"))
            dataTypeCode = Types.BINARY; // -2
        else if (genericDataTypeName.equalsIgnoreCase("VARBINARY"))
            dataTypeCode = Types.VARBINARY; // -3
        else if (genericDataTypeName.equalsIgnoreCase("LONGVARBINARY"))
            dataTypeCode = Types.LONGVARBINARY;  // -4

        else if (genericDataTypeName.equalsIgnoreCase("NULL"))
            dataTypeCode = Types.NULL; // 0

        else if (genericDataTypeName.equalsIgnoreCase("OTHER"))
            dataTypeCode = Types.OTHER; // 1111

        else if (genericDataTypeName.equalsIgnoreCase("JAVA_OBJECT"))
            dataTypeCode = Types.JAVA_OBJECT; // 2000
        else if (genericDataTypeName.equalsIgnoreCase("DISTINCT"))
            dataTypeCode = Types.DISTINCT; // 2001
        else if (genericDataTypeName.equalsIgnoreCase("STRUCT"))
            dataTypeCode = Types.STRUCT; // 2002

        else if (genericDataTypeName.equalsIgnoreCase("ARRAY"))
            dataTypeCode = Types.ARRAY; // 2003
        else if (genericDataTypeName.equalsIgnoreCase("BLOB"))
            dataTypeCode = Types.BLOB; // 2004
        else if (genericDataTypeName.equalsIgnoreCase("CLOB"))
            dataTypeCode = Types.CLOB; // 2005
        else if (genericDataTypeName.equalsIgnoreCase("REF"))
            dataTypeCode = Types.REF; // 2006

        return dataTypeCode;
    }

    protected  void dropTable(String dropTableStatement) {
        if (createTableScript)
            tableScriptOut.println(dropTableStatement + propertiesHandler.properties.getStatementTerminator());
        else {
           try {
               stmt = con.createStatement();
               try {
                   stmt.executeUpdate(dropTableStatement);
               } catch (SQLException sqle) {/*Table didn't exist*/
               }
           } catch (Exception e) {
               logger.error("Error in DbLoader.dropTable()", e);
           } finally {
               try {
                   stmt.close();
               } catch (Exception e) {
               }
           }
        }
    }

    protected  void createTable(String createTableStatement) {
        if (createTableScript)
           tableScriptOut.println(createTableStatement + propertiesHandler.properties.getStatementTerminator());
        else {
           try {
               stmt = con.createStatement();
               stmt.executeUpdate(createTableStatement);
           } catch (Exception e) {
               logger.error("error creating table with this sql: " + createTableStatement);
               logger.error("", e);
           } finally {
               try {
                   stmt.close();
               } catch (Exception e) {
               }
           }
        }
    }



    protected  void alterTable( String alterTableStatement ) {
        if ( createTableScript )
           tableScriptOut.println( alterTableStatement + propertiesHandler.properties.getStatementTerminator() );
        else {
           try {
               stmt = con.createStatement();
               stmt.executeUpdate( alterTableStatement );
           } catch ( Exception e ) {
              logger.error("error altering table with this sql: " + alterTableStatement);
              logger.error("", e);
           } finally {
               try {
                   stmt.close();
               } catch ( Exception e ) {
               }
           }
        }
    }

    protected  void indexTable( String indexTableStatement ) {
        if ( createTableScript )
           tableScriptOut.println( indexTableStatement + propertiesHandler.properties.getStatementTerminator() );
        else {
           try {
               stmt = con.createStatement();
               stmt.executeUpdate( indexTableStatement );
           } catch ( Exception e ) {
              logger.error("error indexing table with this sql: " + indexTableStatement);
              logger.error("", e);
           } finally {
               try {
                   stmt.close();
               } catch ( Exception e ) {
               }
           }
        }
    }

    protected void readProperties(XMLReader parser, InputStream properties) throws SAXException, IOException {
        propertiesHandler = new PropertiesHandler();
        parser.setContentHandler(propertiesHandler);
        parser.setErrorHandler(propertiesHandler);
        parser.parse(new InputSource(properties));
    }

     private class PropertiesHandler extends DefaultHandler {
        private  StringBuilder charBuff = null;

         private Properties properties;
         private DbTypeMapping dbTypeMapping;
         private Type type;

        public void startDocument() {
        }

        public void endDocument() {
        }

        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
            charBuff = new StringBuilder();

            if (qName.equals("properties"))
                properties = new Properties();
            else if (qName.equals("db-type-mapping"))
                dbTypeMapping = new DbTypeMapping();
            else if (qName.equals("type"))
                type = new Type();
        }

        public void endElement(String namespaceURI, String localName, String qName) {
            if (qName.equals("drop-tables")) // drop tables ("true" or "false")
                properties.setDropTables(charBuff.toString());
            else if (qName.equals("create-tables")) // create tables ("true" or "false")
                properties.setCreateTables(charBuff.toString());
            else if (qName.equals("populate-tables")) // populate tables ("true" or "false")
                properties.setPopulateTables(charBuff.toString());
            else if (qName.equals("create-table-script")) // create table script ("true" or "false")
                properties.setCreateTableScript(charBuff.toString());
            else if (qName.equals("table-script-file-name")) // script file name
                properties.setTableScriptFileName(charBuff.toString());
            else if (qName.equals("statement-terminator")) // statement terminator
                properties.setStatementTerminator(charBuff.toString());
            else if (qName.equals("db-type-mapping"))
                properties.addDbTypeMapping(dbTypeMapping);
            else if (qName.equals("db-name")) // database name
                dbTypeMapping.setDbName(charBuff.toString());
            else if (qName.equals("db-version")) // database version
                dbTypeMapping.setDbVersion(charBuff.toString());
            else if (qName.equals("driver-name")) // driver name
                dbTypeMapping.setDriverName(charBuff.toString());
            else if (qName.equals("driver-version")) // driver version
                dbTypeMapping.setDriverVersion(charBuff.toString());
            else if (qName.equals("type"))
                dbTypeMapping.addType(type);
            else if (qName.equals("generic")) // generic type
                type.setGeneric(charBuff.toString());
            else if (qName.equals("local")) // local type
                type.setLocal(charBuff.toString());
            else if ( qName.equals( "alter-tables" ) ) // alter tables ("true" or "false")
                    properties.setAlterTables( charBuff.toString() );
            else if ( qName.equals( "index-tables" ) ) // index tables ("true" or "false")
                    properties.setIndexTables( charBuff.toString() );
        }

        public void characters(char ch[], int start, int length) {
            charBuff.append(ch, start, length);
        }

        class Properties {
            private String dropTables;
            private String createTables;
            private String populateTables;
            private String createTableScript;
            private String tableScriptFileName;
            private String statementTerminator;
            private ArrayList dbTypeMappings = new ArrayList();

            private String alterTables;
            private String indexTables;

            public String getDropTables() {
                return dropTables;
            }

            public String getCreateTables() {
                return createTables;
            }

            public String getPopulateTables() {
                return populateTables;
            }

            public String getCreateTableScript() {
                return createTableScript;
            }
            
            public String getTableScriptFileName() {
                return tableScriptFileName;
            }
            
            public String getStatementTerminator() {
                return statementTerminator;
            }

            public ArrayList getDbTypeMappings() {
                return dbTypeMappings;
            }

            public void setDropTables(String dropTables) {
                this.dropTables = dropTables;
            }

            public void setCreateTables(String createTables) {
                this.createTables = createTables;
            }

            public void setPopulateTables(String populateTables) {
                this.populateTables = populateTables;
            }

            public void setCreateTableScript(String createTableScript) {
                this.createTableScript = createTableScript;
            }
            
            public void setTableScriptFileName(String tableScriptFileName) {
                this.tableScriptFileName = tableScriptFileName;
            }
            
            public void setStatementTerminator(String statementTerminator) {
                this.statementTerminator = statementTerminator;
            }

            public void addDbTypeMapping(DbTypeMapping dbTypeMapping) {
                dbTypeMappings.add(dbTypeMapping);
            }

            public String getAlterTables() {
                return alterTables;
            }
            public void setAlterTables( String alterTables ) {
                this.alterTables = alterTables;
            }
            public String getIndexTables() {
                return indexTables;
            }
            public void setIndexTables( String indexTables ) {
                this.indexTables = indexTables;
            }

            public String getMappedDataTypeName(String dbName, String dbVersion, String driverName, String driverVersion, String genericDataTypeName) {
                String mappedDataTypeName = null;
                Iterator iterator = dbTypeMappings.iterator();

                while (iterator.hasNext()) {
                    DbTypeMapping dbTypeMapping = (DbTypeMapping) iterator.next();
                    String dbNameProp = dbTypeMapping.getDbName();
                    String dbVersionProp = dbTypeMapping.getDbVersion();
                    String driverNameProp = dbTypeMapping.getDriverName();
                    String driverVersionProp = dbTypeMapping.getDriverVersion();

                    if (dbNameProp.equalsIgnoreCase(dbName) && dbVersionProp.equalsIgnoreCase(dbVersion) &&
                            driverNameProp.equalsIgnoreCase(driverName) && driverVersionProp.equalsIgnoreCase(driverVersion)) {
                        // Found a matching database/driver combination
                        mappedDataTypeName = dbTypeMapping.getMappedDataTypeName(genericDataTypeName);
                    }
                }
                return mappedDataTypeName;
            }

        }

        class DbTypeMapping {
            String dbName;
            String dbVersion;
            String driverName;
            String driverVersion;
            ArrayList types = new ArrayList();

            public String getDbName() {
                return dbName;
            }

            public String getDbVersion() {
                return dbVersion;
            }

            public String getDriverName() {
                return driverName;
            }

            public String getDriverVersion() {
                return driverVersion;
            }

            public ArrayList getTypes() {
                return types;
            }

            public void setDbName(String dbName) {
                this.dbName = dbName;
            }

            public void setDbVersion(String dbVersion) {
                this.dbVersion = dbVersion;
            }

            public void setDriverName(String driverName) {
                this.driverName = driverName;
            }

            public void setDriverVersion(String driverVersion) {
                this.driverVersion = driverVersion;
            }

            public void addType(Type type) {
                types.add(type);
            }

            public String getMappedDataTypeName(String genericDataTypeName) {
                String mappedDataTypeName = null;
                Iterator iterator = types.iterator();

                while (iterator.hasNext()) {
                    Type type = (Type) iterator.next();

                    if (type.getGeneric().equalsIgnoreCase(genericDataTypeName))
                        mappedDataTypeName = type.getLocal();
                }
                return mappedDataTypeName;
            }
        }

        class Type {
            String genericType; // "generic" is a Java reserved word
            String local;

            public String getGeneric() {
                return genericType;
            }

            public String getLocal() {
                return local;
            }

            public void setGeneric(String genericType) {
                this.genericType = genericType;
            }

            public void setLocal(String local) {
                this.local = local;
            }
        }
    }


     class DataHandler extends DefaultHandler {
        protected StringBuilder charBuff = null;

        protected boolean insideData = false;
        private  boolean insideTable = false;
        private  boolean insideName = false;
        private  boolean insideRow = false;
        private  boolean insideColumn = false;
        private  boolean insideValue = false;
        private  boolean supportsPreparedStatements = false;

         Table table;
         Row row;
         Column column;
         String action;  //determines sql function for a table row
         String type;    //determines type of column

        public void startDocument() {
            logger.debug("Populating tables...");

            if (!populateTables)
               logger.debug("disabled.");

            supportsPreparedStatements = supportsPreparedStatements();
        }

        public void endDocument() {
           //logger.debug("");
        }

        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
            charBuff = new StringBuilder();

            if (qName.equals("data"))
                insideData = true;
            else if (qName.equals("table")) {
                insideTable = true;
                table = new Table();
                action = atts.getValue("action");
            } else if (qName.equals("name"))
                insideName = true;
            else if (qName.equals("row")) {
                insideRow = true;
                row = new Row();
            } else if (qName.equals("column")) {
                insideColumn = true;
                column = new Column();
                type = atts.getValue("type");
            } else if (qName.equals("value"))
                insideValue = true;
        }

        public void endElement(String namespaceURI, String localName, String qName) {
            if (qName.equals("data"))
                insideData = false;
            else if (qName.equals("table"))
                insideTable = false;
            else if (qName.equals("name")) {
                insideName = false;

                if (!insideColumn) // table name
                    table.setName(charBuff.toString().toLowerCase());
                else // column name
                    column.setName(charBuff.toString());
            } else if (qName.equals("row")) {
                insideRow = false;

                if (action != null) {
                    if (action.equals("delete"))
                        executeSQL(table, row, "delete");
                    else if (action.equals("modify"))
                        executeSQL(table, row, "modify");
                    else if (action.equals("add"))
                        executeSQL(table, row, "insert");
                } else if (populateTables)
                    executeSQL(table, row, "insert");
            } else if (qName.equals("column")) {
                insideColumn = false;
                if (type != null) column.setType(type);
                row.addColumn(column);
            } else if (qName.equals("value")) {
                insideValue = false;

                if (insideColumn) // column value
                    column.setValue(charBuff.toString());
            }
        }

        public void characters(char ch[], int start, int length) {
            charBuff.append(ch, start, length);
        }

        private String prepareInsertStatement(Row row, boolean preparedStatement) {
            StringBuilder sb = new StringBuilder("INSERT INTO ");
            sb.append(table.getName()).append(" (");

            ArrayList columns = row.getColumns();
            Iterator iterator = columns.iterator();

            while (iterator.hasNext()) {
                Column column = (Column) iterator.next();
                sb.append(column.getName()).append(", ");
            }

            // Delete comma and space after last column name (kind of sloppy, but it works)
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);

            sb.append(") VALUES (");
            iterator = columns.iterator();

            while (iterator.hasNext()) {
                Column column = (Column) iterator.next();

                if (preparedStatement)
                    sb.append("?");
                else {
                    String value = column.getValue();

                    if (value != null) {
                        if (value.equals("SYSDATE"))
                            sb.append(value);
                        else if (value.equals("NULL"))
                            sb.append(value);
                        else if (getJavaSqlDataTypeOfColumn(tablesDocGeneric, table.getName(), column.getName()) == Types.INTEGER)
                        // this column is an integer, so don't put quotes (Sybase cares about this)
                            sb.append(value);
                        else {
                            sb.append("'");
                            sb.append(sqlEscape(value.trim()));
                            sb.append("'");
                        }
                    } else
                        sb.append("''");
                }

                sb.append(", ");
            }

            // Delete comma and space after last value (kind of sloppy, but it works)
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);

            sb.append(")");

            return sb.toString();
        }

        private String prepareDeleteStatement(Row row, boolean preparedStatement) {

            StringBuilder sb = new StringBuilder("DELETE FROM ");
            sb.append(table.getName()).append(" WHERE ");

            ArrayList columns = row.getColumns();
            Iterator iterator = columns.iterator();
            Column column;

            while (iterator.hasNext()) {
                column = (Column) iterator.next();
                if (preparedStatement)
                    sb.append(column.getName() + " = ? and ");
                else if (getJavaSqlDataTypeOfColumn(tablesDocGeneric, table.getName(), column.getName()) == Types.INTEGER)
                    sb.append(column.getName() + " = " + sqlEscape(column.getValue().trim()) + " and ");
                else
                    sb.append(column.getName() + " = " + "'" + sqlEscape(column.getValue().trim()) + "' and ");
            }

            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);

            if (!preparedStatement)
                sb.deleteCharAt(sb.length() - 1);

            return sb.toString();

        }

        private String prepareUpdateStatement(Row row, boolean preparedStatement) {

            StringBuilder sb = new StringBuilder("UPDATE ");
            sb.append(table.getName()).append(" SET ");

            ArrayList columns = row.getColumns();
            Iterator iterator = columns.iterator();

            Hashtable setPairs = new Hashtable();
            Hashtable wherePairs = new Hashtable();
            String type;
            Column column;

            while (iterator.hasNext()) {
                column = (Column) iterator.next();
                type = column.getType();

                if (type != null && type.equals("select")) {
                    if (getJavaSqlDataTypeOfColumn(tablesDocGeneric, table.getName(), column.getName()) == Types.INTEGER)
                        wherePairs.put(column.getName(), column.getValue().trim());
                    else
                        wherePairs.put(column.getName(), "'" + column.getValue().trim() + "'");
                } else {
                    if (getJavaSqlDataTypeOfColumn(tablesDocGeneric, table.getName(), column.getName()) == Types.INTEGER)
                        setPairs.put(column.getName(), column.getValue().trim());
                    else
                        setPairs.put(column.getName(), "'" + column.getValue().trim() + "'");
                }
            }

            String nm;
            String val;

            Enumeration sKeys = setPairs.keys();
            while (sKeys.hasMoreElements()) {
                nm = (String) sKeys.nextElement();
                val = (String) setPairs.get(nm);
                sb.append(nm + " = " + sqlEscape(val) + ", ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);

            sb.append(" WHERE ");

            Enumeration wKeys = wherePairs.keys();
            while (wKeys.hasMoreElements()) {
                nm = (String) wKeys.nextElement();
                val = (String) wherePairs.get(nm);
                sb.append(nm + "=" + sqlEscape(val) + " and ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);

            return sb.toString();

        }

        /**
         * Make a string SQL safe
         * @param sql the string that is not necessarily safe
         * @return SQL safe string
         */
        public  final String sqlEscape(String sql) {
            if (sql == null) {
                return "";
            } else {
                int primePos = sql.indexOf("'");
                if (primePos == -1) {
                    return sql;
                } else {
                    StringBuilder sb = new StringBuilder(sql.length() + 4);
                    int startPos = 0;
                    do {
                        sb.append(sql.substring(startPos, primePos + 1));
                        sb.append("'");
                        startPos = primePos + 1;
                        primePos = sql.indexOf("'", startPos);
                    } while (primePos != -1);
                    sb.append(sql.substring(startPos));
                    return sb.toString();
                }
            }
        }

        private void executeSQL(Table table, Row row, String action) {
            if (createTableScript) {
                if (action.equals("delete"))
                   tableScriptOut.println(prepareDeleteStatement(row, false) + propertiesHandler.properties.getStatementTerminator());
                else if (action.equals("modify"))
                   tableScriptOut.println(prepareUpdateStatement(row, false) + propertiesHandler.properties.getStatementTerminator());
                else if (action.equals("insert"))
                   tableScriptOut.println(prepareInsertStatement(row, false) + propertiesHandler.properties.getStatementTerminator());
            }

            if (supportsPreparedStatements) {
                String preparedStatement = "";
                try {
                    if (action.equals("delete"))
                        preparedStatement = prepareDeleteStatement(row, true);
                    else if (action.equals("modify"))
                        preparedStatement = prepareUpdateStatement(row, true);
                    else if (action.equals("insert"))
                        preparedStatement = prepareInsertStatement(row, true);
                    //System.out.println(preparedStatement);
                    pstmt = con.prepareStatement(preparedStatement);
                    pstmt.clearParameters();

                    // Loop through parameters and set them, checking for any that excede 4k
                    ArrayList columns = row.getColumns();
                    Iterator iterator = columns.iterator();

                    for (int i = 1; iterator.hasNext(); i++) {
                        Column column = (Column) iterator.next();
                        String value = column.getValue();

                        // Get a java sql data type for column name
                        int javaSqlDataType = getJavaSqlDataTypeOfColumn(tablesDocGeneric, table.getName(), column.getName());
                        if (value == null || (value != null && value.equalsIgnoreCase("NULL")))
                            pstmt.setNull(i, javaSqlDataType);
                        else if (javaSqlDataType == Types.TIMESTAMP) {
                            if (value.equals("SYSDATE"))
                                pstmt.setTimestamp(i, new java.sql.Timestamp(System.currentTimeMillis()));
                            else
                                pstmt.setTimestamp(i, java.sql.Timestamp.valueOf(value));
                        } else {
                            value = value.trim(); // can't read xml properly without this, don't know why yet
                            int valueLength = value.length();
                            //System.out.println("Value: " + value);
                            //System.out.println("Value.length: " + value.length());
                            //System.out.println("SQL DATATPYE: " + javaSqlDataType);
                            //System.out.println("For loop I: " + i);
                            if (valueLength <= 4000) {
                                try {
                                    // Needed for Sybase and maybe others
                                    pstmt.setObject(i, value, javaSqlDataType);
                                } catch (Exception e) {
                                    // Needed for Oracle and maybe others
                                    pstmt.setObject(i, value);
                                }
                            } else {
                                try {
                                    try {
                                        // Needed for Sybase and maybe others
                                        pstmt.setObject(i, value, javaSqlDataType);
                                    } catch (Exception e) {
                                        // Needed for Oracle and maybe others
                                        pstmt.setObject(i, value);
                                    }
                                } catch (SQLException sqle) {
                                    // For Oracle and maybe others
                                    pstmt.setCharacterStream(i, new StringReader(value), valueLength);
                                }
                            }
                        }
                    }
                    pstmt.executeUpdate();
                } catch (SQLException sqle) {
                   logger.error("Error in DbLoader.DataHandler.executeSQL()", sqle);
                   logger.error("Error in DbLoader.DataHandler.executeSQL(): " + preparedStatement);
                } catch (Exception e) {
                   logger.error("Error in DbLoader.DataHandler.executeSQL()", e);
                } finally {
                    try {
                        pstmt.close();
                    } catch (Exception e) {
                    }
                }
            } else {

                // If prepared statements aren't supported, try a normal sql statement
                String statement = "";
                if (action.equals("delete"))
                    statement = prepareDeleteStatement(row, false);
                else if (action.equals("modify"))
                    statement = prepareUpdateStatement(row, false);
                else if (action.equals("insert"))
                    statement = prepareInsertStatement(row, false);
                //System.out.println(statement);

                try {
                    stmt = con.createStatement();
                    stmt.executeUpdate(statement);
                } catch (Exception e) {
                   logger.error("Error in DbLoader.DataHandler.executeSQL()", e);
                   logger.error("Error in DbLoader.DataHandler.executeSQL(): " + statement); 
                } finally {
                    try {
                        stmt.close();
                    } catch (Exception e) {
                    }
                }
            }
        }

        private  boolean supportsPreparedStatements() {
            boolean supportsPreparedStatements = true;

            try {
                // Issue a prepared statement to see if database/driver accepts them.
                // The assumption is that if a SQLException is thrown, it doesn't support them.
                // I don't know of any other way to check if the database/driver accepts
                // prepared statements.  If you do, please change this method!
                Statement stmt;
                stmt = con.createStatement();
                try {
                    stmt.executeUpdate("CREATE TABLE PREP_TEST (A VARCHAR(1))");
                } catch (Exception e) {/* Assume it already exists */
                } finally {
                    try {
                        stmt.close();
                    } catch (Exception e) {
                    }
                }

                pstmt = con.prepareStatement("SELECT A FROM PREP_TEST WHERE A=?");
                pstmt.clearParameters();
                pstmt.setString(1, "D");
                ResultSet rs = pstmt.executeQuery();
                rs.close();
            } catch (SQLException sqle) {
                supportsPreparedStatements = false;
                logger.error("Error in DbLoader.DataHandler.supportsPreparedStatements()", sqle);
            } finally {
                try {
                    stmt = con.createStatement();
                    stmt.executeUpdate("DROP TABLE PREP_TEST");
                } catch (Exception e) {/* Assume it already exists */
                } finally {
                    try {
                        stmt.close();
                    } catch (Exception e) {
                    }
                }

                try {
                    pstmt.close();
                } catch (Exception e) {
                }
            }
            return supportsPreparedStatements;
        }

        class Table {
            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        class Row {
            ArrayList columns = new ArrayList();

            public ArrayList getColumns() {
                return columns;
            }

            public void addColumn(Column column) {
                columns.add(column);
            }
        }

        class Column {
            private String name;
            private String value;
            private String type;

            public String getName() {
                return name;
            }

            public String getValue() {
                return value;
            }

            public String getType() {
                return type;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }


   public Connection getCon() {
      return con;
   }

   public void setCon(Connection con) {
      this.con = con;
   }

   public Statement getStmt() {
      return stmt;
   }

   public void setStmt(Statement stmt) {
      this.stmt = stmt;
   }

   public PreparedStatement getPstmt() {
      return pstmt;
   }

   public void setPstmt(PreparedStatement pstmt) {
      this.pstmt = pstmt;
   }

   public Document getTablesDoc() {
      return tablesDoc;
   }

   public void setTablesDoc(Document tablesDoc) {
      this.tablesDoc = tablesDoc;
   }

   public Document getTablesDocGeneric() {
      return tablesDocGeneric;
   }

   public void setTablesDocGeneric(Document tablesDocGeneric) {
      this.tablesDocGeneric = tablesDocGeneric;
   }

   public boolean isCreateTableScript() {
      return createTableScript;
   }

   public void setCreateTableScript(boolean createTableScript) {
      this.createTableScript = createTableScript;
   }

   public boolean isPopulateTables() {
      return populateTables;
   }

   public void setPopulateTables(boolean populateTables) {
      this.populateTables = populateTables;
   }

   public PrintWriter getTableScriptOut() {
      return tableScriptOut;
   }

   public void setTableScriptOut(PrintWriter tableScriptOut) {
      this.tableScriptOut = tableScriptOut;
   }
   
   public boolean isDropTables() {
      return dropTables;
   }

   public void setDropTables(boolean dropTables) {
      this.dropTables = dropTables;
   }

   public boolean isCreateTables() {
      return createTables;
   }

   public void setCreateTables(boolean createTables) {
      this.createTables = createTables;
   }

   public String getDbName() {
      return dbName;
   }

   public void setDbName(String dbName) {
      this.dbName = dbName;
   }

   public String getDbVersion() {
      return dbVersion;
   }

   public void setDbVersion(String dbVersion) {
      this.dbVersion = dbVersion;
   }

   public String getDriverName() {
      return driverName;
   }

   public void setDriverName(String driverName) {
      this.driverName = driverName;
   }

   public String getDriverVersion() {
      return driverVersion;
   }

   public void setDriverVersion(String driverVersion) {
      this.driverVersion = driverVersion;
   }

   public boolean isAlterTables() {
      return alterTables;
   }

   public void setAlterTables(boolean alterTables) {
      this.alterTables = alterTables;
   }

   public boolean isIndexTables() {
      return indexTables;
   }

   public void setIndexTables(boolean indexTables) {
      this.indexTables = indexTables;
   }

   public Hashtable getTableColumnTypes() {
      return tableColumnTypes;
   }

   public void setTableColumnTypes(Hashtable tableColumnTypes) {
      this.tableColumnTypes = tableColumnTypes;
   }

   public PropertiesHandler getPropertiesHandler() {
      return propertiesHandler;
   }

   public void setPropertiesHandler(PropertiesHandler propertiesHandler) {
      this.propertiesHandler = propertiesHandler;
   }
}
