@echo off

rem -------------------------------------------------------------------
rem Execute SchemaExport tool
rem -------------------------------------------------------------------

set HIBERNATE_DIALECT=org.hibernate.dialect.HSQLDialect
rem set HIBERNATE_DIALECT=org.hibernate.dialect.MySQLDialect
rem set HIBERNATE_DIALECT=org.hibernate.dialect.Oracle9Dialect

set MAVEN_TARGET=%CD%\api\target\classes
set MAVEN_SRC=%CD%\api\target\classes\org\theospi\portfolio\matrix\model\impl\

set HIBERNATE_HOME=C:\java\hibernate-3.1
set LIB=%HIBERNATE_HOME%\lib
set PROPS=%HIBERNATE_HOME%\src
set CP=%MAVEN_TARGET%;%PROPS%;%HIBERNATE_HOME%\hibernate3.jar;%LIB%\commons-logging-1.0.4.jar;%LIB%\commons-collections-2.1.1.jar;%LIB%\commons-lang-1.0.1.jar;%LIB%\cglib-full-2.0.2.jar;%LIB%\dom4j-1.4.jar;%LIB%\odmg-3.0.jar;%LIB%\xml-apis.jar;%LIB%\xerces-2.4.0.jar;%LIB%\xalan-2.4.0.jar
set CP=%CP%;%MAVEN_TARGET%

echo %HIBERNATE_DIALECT%
echo %MAVEN_TARGET%
echo %MAVEN_SRC%
echo %HIBERNATE_HOME%
echo %LIB%
echo %PROPS%
echo %CP%

java -cp %CP% -Dhibernate.dialect=%HIBERNATE_DIALECT% org.hibernate.tool.hbm2ddl.SchemaExport --text --format --output=%HIBERNATE_DIALECT%-ddl.sql %MAVEN_SRC%\*.hbm.xml
