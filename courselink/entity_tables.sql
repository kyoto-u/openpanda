CREATE TABLE ENTITY_PROPERTIES ( 
    ID            bigint(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,
    entityRef       varchar(255) NOT NULL,
    entityPrefix    varchar(255) NOT NULL,
    propertyName    varchar(255) NOT NULL,
    propertyValue   text NOT NULL);
    
CREATE TABLE ENTITY_TAG_APPLICATIONS ( 
    ID            bigint(20) AUTO_INCREMENT NOT NULL PRIMARY KEY,
    entityRef       varchar(255) NOT NULL,
    entityPrefix    varchar(255) NOT NULL,
    tag             varchar(255) NOT NULL);    
        
