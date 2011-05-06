-- ======================================================================
-- ===   Sql Script for Database : Geonet
-- ===
-- === Build : 153
-- ======================================================================

CREATE TABLE Relations
  (
    id         int,
    relatedId  int,

    primary key(id,relatedId)
  );

-- ======================================================================

CREATE TABLE Categories
  (
    id    int,
    name  varchar(32)   not null,

    primary key(id),
    unique(name)
  );

-- ======================================================================

CREATE TABLE Settings
  (
    id        int,
    parentId  int,
    name      varchar(32)    not null,
    value     text,

    primary key(id),

    foreign key(parentId) references Settings(id)
  );

-- ======================================================================

CREATE TABLE Languages
  (
    id    varchar(5),
    name  varchar(32)   not null,
    isocode varchar(3)  not null,

    primary key(id)
  );

-- ======================================================================

CREATE TABLE Sources
  (
    uuid     varchar(250),
    name     varchar(250),
    isLocal  char(1)        default 'y',

    primary key(uuid)
  );

-- ======================================================================

CREATE TABLE IsoLanguages
  (
    id    int,
    code  varchar(3)   not null,

    primary key(id),
    unique(code)
  );

-- ======================================================================

CREATE TABLE IsoLanguagesDes
  (
    idDes   int,
    langId  varchar(5),
    label   varchar(96)   not null,

    primary key(idDes,langId),

    foreign key(idDes) references IsoLanguages(id),
    foreign key(langId) references Languages(id)
  );

-- ======================================================================

CREATE TABLE Regions
  (
    id     int,
    north  float   not null,
    south  float   not null,
    west   float   not null,
    east   float   not null,

    primary key(id)
  );

-- ======================================================================

CREATE TABLE RegionsDes
  (
    idDes   int,
    langId  varchar(5),
    label   varchar(96)   not null,

    primary key(idDes,langId),

    foreign key(idDes) references Regions(id),
    foreign key(langId) references Languages(id)
  );

-- ======================================================================

CREATE TABLE Users
  (
    id            int,
    username      varchar(32)    not null,
    password      varchar(40)    not null,
    surname       varchar(32),
    name          varchar(32),
    profile       varchar(32)    not null,
    address       varchar(128),
    city          varchar(128),
    state         varchar(32),
    zip           varchar(16),
    country       varchar(128),
    email         varchar(128),
    organisation  varchar(128),
    kind          varchar(16),

    primary key(id),
    unique(username)
  );

-- ======================================================================

CREATE TABLE Operations
  (
    id        int,
    name      varchar(32)   not null,
    reserved  char(1)       default 'n' not null,

    primary key(id)
  );

-- ======================================================================

CREATE TABLE OperationsDes
  (
    idDes   int,
    langId  varchar(5),
    label   varchar(96)   not null,

    primary key(idDes,langId),

    foreign key(idDes) references Operations(id),
    foreign key(langId) references Languages(id)
  );

-- ======================================================================

CREATE TABLE Groups
  (
    id           int,
    name         varchar(32)    not null,
    description  varchar(255),
    email        varchar(32),
    referrer     int,

    primary key(id),
    unique(name),

    foreign key(referrer) references Users(id)
  );

-- ======================================================================

CREATE TABLE GroupsDes
  (
    idDes   int,
    langId  varchar(5),
    label   varchar(96)   not null,

    primary key(idDes,langId),

    foreign key(idDes) references Groups(id),
    foreign key(langId) references Languages(id)
  );

-- ======================================================================

CREATE TABLE UserGroups
  (
    userId   int,
    groupId  int,

    primary key(userId,groupId),

    foreign key(userId) references Users(id),
    foreign key(groupId) references Groups(id)
  );

-- ======================================================================

CREATE TABLE CategoriesDes
  (
    idDes   int,
    langId  varchar(5),
    label   varchar(96)   not null,

    primary key(idDes,langId),

    foreign key(idDes) references Categories(id),
    foreign key(langId) references Languages(id)
  );

-- ======================================================================

CREATE TABLE Metadata
  (
    id           int,
    uuid         varchar(250)   not null,
    schemaId     varchar(32)    not null,
    isTemplate   char(1)        default 'n' not null,
    isHarvested  char(1)        default 'n' not null,
    createDate   varchar(24)    not null,
    changeDate   varchar(24)    not null,
    data         text           not null,
    source       varchar(250)   not null,
    title        varchar(255),
    root         varchar(255),
    harvestUuid  varchar(250)   default null,
    owner        int            not null,
    groupOwner   int            default null,
    harvestUri   varchar(255)   default null,
    rating       int            default 0 not null,
    popularity   int            default 0 not null,
	displayorder int,

    primary key(id),
    unique(uuid),

    foreign key(owner) references Users(id),
    foreign key(groupOwner) references Groups(id)
  );

CREATE INDEX MetadataNDX1 ON Metadata(uuid);
CREATE INDEX MetadataNDX2 ON Metadata(source);

-- ======================================================================

CREATE TABLE MetadataCateg
  (
    metadataId  int,
    categoryId  int,

    primary key(metadataId,categoryId),

    foreign key(metadataId) references Metadata(id),
    foreign key(categoryId) references Categories(id)
  );

-- ======================================================================

CREATE TABLE OperationAllowed
  (
    groupId      int,
    metadataId   int,
    operationId  int,

    primary key(groupId,metadataId,operationId),

    foreign key(groupId) references Groups(id),
    foreign key(metadataId) references Metadata(id),
    foreign key(operationId) references Operations(id)
  );

-- ======================================================================

CREATE TABLE MetadataRating
  (
    metadataId  int,
    ipAddress   varchar(32),
    rating      int           not null,

    primary key(metadataId,ipAddress),

    foreign key(metadataId) references Metadata(id)
  );

-- ======================================================================

CREATE TABLE MetadataNotifiers
  (
    id         int,
    name       varchar(32)    not null,
    url        varchar(255)   not null,
    enabled    char(1)        default 'n' not null,
    username       varchar(32),
    password       varchar(32),

    primary key(id)
  );

-- ======================================================================

CREATE TABLE MetadataNotifications
  (
    metadataId         int,
    notifierId         int,
    notified           char(1)        default 'n' not null,
    metadataUuid       varchar(250)   not null,
    action             char(1)        not null,
    errormsg           text,

    primary key(metadataId,notifierId),

    foreign key(notifierId) references MetadataNotifiers(id)
  );

-- ======================================================================

CREATE TABLE CswServerCapabilitiesInfo
  (
    idField   int,
    langId    varchar(5)    not null,
    field     varchar(32)   not null,
    label     varchar(96),

    primary key(idField),

    foreign key(langId) references Languages(id)
  );

-- ======================================================================
CREATE TABLE IndexLanguages
  (
    id            int,
    languageName  varchar(32)   not null,
    selected      char(1)       default 'n' not null,

    primary key(id, languageName)

  );
--  -- ======================================================================

--  -- ======================================================================
-- GeOrchestra : adding shared object tables
-- This needs a spatial (postgis) database

CREATE TABLE shared_contacts (
    shared_contacts_fid integer NOT NULL,
    the_geom geometry,
    id integer,
    data text,
    search text,
    CONSTRAINT enforce_dims_the_geom CHECK ((ndims(the_geom) = 2))
);

CREATE SEQUENCE shared_contacts_shared_contacts_fid_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;
    
ALTER SEQUENCE shared_contacts_shared_contacts_fid_seq OWNED BY shared_contacts.shared_contacts_fid;  
  

CREATE TABLE sharedobject_templates (
    typename character varying(32) NOT NULL,
    template text NOT NULL
);


CREATE TABLE spatialindex (
    fid integer NOT NULL,
    id character varying(250),
    the_geom geometry,
    CONSTRAINT enforce_dims_the_geom CHECK ((ndims(the_geom) = 2)),
    CONSTRAINT enforce_geotype_the_geom CHECK (((geometrytype(the_geom) = 'MULTIPOLYGON'::text) OR (the_geom IS NULL))),
    CONSTRAINT enforce_srid_the_geom CHECK ((srid(the_geom) = 4326))
);

ALTER TABLE shared_contacts ALTER COLUMN shared_contacts_fid SET DEFAULT nextval('shared_contacts_shared_contacts_fid_seq'::regclass);

COPY shared_contacts (shared_contacts_fid, the_geom, id, data, search) FROM stdin;
12	\N	5	<gmd:CI_ResponsibleParty xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gco="http://www.isotc211.org/2005/gco" xsi:schemaLocation="http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd">\r\n  <gmd:individualName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>ECAULT Loïc</gco:CharacterString>\r\n  </gmd:individualName>\r\n  <gmd:organisationName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>Region Bretagne</gco:CharacterString>\r\n  </gmd:organisationName>\r\n  <gmd:positionName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>technicien sig</gco:CharacterString>\r\n  </gmd:positionName>\r\n  <gmd:contactInfo>\r\n    <gmd:CI_Contact>\r\n      <gmd:phone>\r\n        <gmd:CI_Telephone>\r\n          <gmd:voice>\r\n            <gco:CharacterString>0290091628</gco:CharacterString>\r\n          </gmd:voice>\r\n        </gmd:CI_Telephone>\r\n      </gmd:phone>\r\n      <gmd:address>\r\n        <gmd:CI_Address>\r\n          <gmd:deliveryPoint xsi:type="gmd:PT_FreeText_PropertyType">\r\n            <gco:CharacterString>avenue patton</gco:CharacterString>\r\n          </gmd:deliveryPoint>\r\n          <gmd:city>\r\n            <gco:CharacterString>rennes</gco:CharacterString>\r\n          </gmd:city>\r\n          <gmd:administrativeArea>\r\n            <gco:CharacterString>france</gco:CharacterString>\r\n          </gmd:administrativeArea>\r\n          <gmd:postalCode>\r\n            <gco:CharacterString>35000</gco:CharacterString>\r\n          </gmd:postalCode>\r\n          <gmd:country>\r\n            <gco:CharacterString>Pays</gco:CharacterString>\r\n          </gmd:country>\r\n          <gmd:electronicMailAddress>\r\n            <gco:CharacterString>loic.ecault@region-bretagne.fr</gco:CharacterString>\r\n          </gmd:electronicMailAddress>\r\n        </gmd:CI_Address>\r\n      </gmd:address>\r\n      <gmd:onlineResource>\r\n        <gmd:CI_OnlineResource>\r\n          <gmd:linkage>\r\n            <gmd:URL>http://www.region-bretagne.fr</gmd:URL>\r\n          </gmd:linkage>\r\n          <gmd:protocol>\r\n            <gco:CharacterString>WWW:LINK-1.0-http--link</gco:CharacterString>\r\n          </gmd:protocol>\r\n          <gmd:applicationProfile>\r\n            <gco:CharacterString>applciation profile</gco:CharacterString>\r\n          </gmd:applicationProfile>\r\n          <gmd:name>\r\n            <gco:CharacterString gco:nilReason="missing" />\r\n          </gmd:name>\r\n          <gmd:description>\r\n            <gco:CharacterString gco:nilReason="missing" />\r\n          </gmd:description>\r\n          <gmd:function>\r\n            <gmd:CI_OnLineFunctionCode codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_OnLineFunctionCode" codeListValue="information" />\r\n          </gmd:function>\r\n        </gmd:CI_OnlineResource>\r\n      </gmd:onlineResource>\r\n      <gmd:hoursOfService>\r\n        <gco:CharacterString>hours of service</gco:CharacterString>\r\n      </gmd:hoursOfService>\r\n      <gmd:contactInstructions>\r\n        <gco:CharacterString>contact instructions</gco:CharacterString>\r\n      </gmd:contactInstructions>\r\n    </gmd:CI_Contact>\r\n  </gmd:contactInfo>\r\n  <gmd:role>\r\n    <gmd:CI_RoleCode codeListValue="originator" codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_RoleCode" />\r\n  </gmd:role>\r\n</gmd:CI_ResponsibleParty>	http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd gmd:pt_freetext_propertytype ecault loic gmd:pt_freetext_propertytype region bretagne gmd:pt_freetext_propertytype technicien sig 0290091628 gmd:pt_freetext_propertytype avenue patton rennes france 35000 pays loic.ecault@region-bretagne.fr http://www.region-bretagne.fr www:link-1.0-http--link applciation profile missing missing information hours of service contact instructions originator
14	\N	3	<gmd:CI_ResponsibleParty xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gco="http://www.isotc211.org/2005/gco" xsi:schemaLocation="http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd">\r\n  <gmd:individualName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>RENAT Pascal</gco:CharacterString>\r\n  </gmd:individualName>\r\n  <gmd:organisationName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>Direction Regionale de l'Environnement de l'Amenagement et du Logement</gco:CharacterString>\r\n  </gmd:organisationName>\r\n  <gmd:positionName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>Administrateur Plateforme</gco:CharacterString>\r\n  </gmd:positionName>\r\n  <gmd:contactInfo>\r\n    <gmd:CI_Contact>\r\n      <gmd:phone>\r\n        <gmd:CI_Telephone>\r\n          <gmd:voice>\r\n            <gco:CharacterString>02 99 33 44 11</gco:CharacterString>\r\n          </gmd:voice>\r\n        </gmd:CI_Telephone>\r\n      </gmd:phone>\r\n      <gmd:address>\r\n        <gmd:CI_Address>\r\n          <gmd:deliveryPoint xsi:type="gmd:PT_FreeText_PropertyType">\r\n            <gco:CharacterString>10 rue Maurice Fabre</gco:CharacterString>\r\n          </gmd:deliveryPoint>\r\n          <gmd:city>\r\n            <gco:CharacterString>RENNES</gco:CharacterString>\r\n          </gmd:city>\r\n          <gmd:administrativeArea>\r\n            <gco:CharacterString>France</gco:CharacterString>\r\n          </gmd:administrativeArea>\r\n          <gmd:postalCode>\r\n            <gco:CharacterString>35000</gco:CharacterString>\r\n          </gmd:postalCode>\r\n          <gmd:country>\r\n            <gco:CharacterString>Pays</gco:CharacterString>\r\n          </gmd:country>\r\n          <gmd:electronicMailAddress>\r\n            <gco:CharacterString>pascal.renat@developpement-durable.gouv.fr</gco:CharacterString>\r\n          </gmd:electronicMailAddress>\r\n        </gmd:CI_Address>\r\n      </gmd:address>\r\n      <gmd:onlineResource>\r\n        <gmd:CI_OnlineResource>\r\n          <gmd:linkage>\r\n            <gmd:URL>http://install.georchestra.org</gmd:URL>\r\n          </gmd:linkage>\r\n          <gmd:protocol>\r\n            <gco:CharacterString>WWW:LINK-1.0-http--link</gco:CharacterString>\r\n          </gmd:protocol>\r\n          <gmd:applicationProfile>\r\n            <gco:CharacterString>applciation profile</gco:CharacterString>\r\n          </gmd:applicationProfile>\r\n          <gmd:name>\r\n            <gco:CharacterString gco:nilReason="missing" />\r\n          </gmd:name>\r\n          <gmd:description>\r\n            <gco:CharacterString gco:nilReason="missing" />\r\n          </gmd:description>\r\n          <gmd:function>\r\n            <gmd:CI_OnLineFunctionCode codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_OnLineFunctionCode" codeListValue="information" />\r\n          </gmd:function>\r\n        </gmd:CI_OnlineResource>\r\n      </gmd:onlineResource>\r\n      <gmd:hoursOfService>\r\n        <gco:CharacterString>hours of service</gco:CharacterString>\r\n      </gmd:hoursOfService>\r\n      <gmd:contactInstructions>\r\n        <gco:CharacterString>contact instructions</gco:CharacterString>\r\n      </gmd:contactInstructions>\r\n    </gmd:CI_Contact>\r\n  </gmd:contactInfo>\r\n  <gmd:role>\r\n    <gmd:CI_RoleCode codeListValue="originator" codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_RoleCode" />\r\n  </gmd:role>\r\n</gmd:CI_ResponsibleParty>	http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd gmd:pt_freetext_propertytype renat pascal gmd:pt_freetext_propertytype direction regionale de l'environnement de l'amenagement et du logement gmd:pt_freetext_propertytype administrateur plateforme 02 99 33 44 11 gmd:pt_freetext_propertytype 10 rue maurice fabre rennes france 35000 pays pascal.renat@developpement-durable.gouv.fr http://install.georchestra.org www:link-1.0-http--link applciation profile missing missing information hours of service contact instructions originator
13	\N	6	<gmd:CI_ResponsibleParty xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gco="http://www.isotc211.org/2005/gco" xsi:schemaLocation="http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd">\r\n  <gmd:individualName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>Pôle Système Régional d'Information Géographique</gco:CharacterString>\r\n  </gmd:individualName>\r\n  <gmd:organisationName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>Region Bretagne</gco:CharacterString>\r\n  </gmd:organisationName>\r\n  <gmd:positionName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>Service SIG</gco:CharacterString>\r\n  </gmd:positionName>\r\n  <gmd:positionName>\r\n    <gco:CharacterString gco:nilReason="missing" />\r\n  </gmd:positionName>\r\n  <gmd:contactInfo>\r\n    <gmd:CI_Contact>\r\n      <gmd:phone>\r\n        <gmd:CI_Telephone>\r\n          <gmd:voice>\r\n            <gco:CharacterString>02 90 09 16 28</gco:CharacterString>\r\n          </gmd:voice>\r\n        </gmd:CI_Telephone>\r\n      </gmd:phone>\r\n      <gmd:address>\r\n        <gmd:CI_Address>\r\n          <gmd:deliveryPoint xsi:type="gmd:PT_FreeText_PropertyType">\r\n            <gco:CharacterString>283 avenue Général Patton - CS 21101</gco:CharacterString>\r\n          </gmd:deliveryPoint>\r\n          <gmd:city>\r\n            <gco:CharacterString>Rennes cedex 7</gco:CharacterString>\r\n          </gmd:city>\r\n          <gmd:administrativeArea>\r\n            <gco:CharacterString>France</gco:CharacterString>\r\n          </gmd:administrativeArea>\r\n          <gmd:postalCode>\r\n            <gco:CharacterString>35711</gco:CharacterString>\r\n          </gmd:postalCode>\r\n          <gmd:country>\r\n            <gco:CharacterString>Pays</gco:CharacterString>\r\n          </gmd:country>\r\n          <gmd:electronicMailAddress>\r\n            <gco:CharacterString>sig@region-bretagne.fr</gco:CharacterString>\r\n          </gmd:electronicMailAddress>\r\n        </gmd:CI_Address>\r\n      </gmd:address>\r\n      <gmd:onlineResource>\r\n        <gmd:CI_OnlineResource>\r\n          <gmd:linkage>\r\n            <gmd:URL>http://www.bretagne.fr</gmd:URL>\r\n          </gmd:linkage>\r\n          <gmd:protocol>\r\n            <gco:CharacterString>WWW:LINK-1.0-http--link</gco:CharacterString>\r\n          </gmd:protocol>\r\n          <gmd:applicationProfile>\r\n            <gco:CharacterString>applciation profile</gco:CharacterString>\r\n          </gmd:applicationProfile>\r\n          <gmd:name>\r\n            <gco:CharacterString gco:nilReason="missing" />\r\n          </gmd:name>\r\n          <gmd:description>\r\n            <gco:CharacterString gco:nilReason="missing" />\r\n          </gmd:description>\r\n          <gmd:function>\r\n            <gmd:CI_OnLineFunctionCode codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_OnLineFunctionCode" codeListValue="information" />\r\n          </gmd:function>\r\n        </gmd:CI_OnlineResource>\r\n      </gmd:onlineResource>\r\n      <gmd:hoursOfService>\r\n        <gco:CharacterString>hours of service</gco:CharacterString>\r\n      </gmd:hoursOfService>\r\n      <gmd:contactInstructions>\r\n        <gco:CharacterString>contact instructions</gco:CharacterString>\r\n      </gmd:contactInstructions>\r\n    </gmd:CI_Contact>\r\n  </gmd:contactInfo>\r\n  <gmd:role>\r\n    <gmd:CI_RoleCode codeListValue="originator" codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_RoleCode" />\r\n  </gmd:role>\r\n</gmd:CI_ResponsibleParty>	http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd gmd:pt_freetext_propertytype pole systeme regional d'information geographique gmd:pt_freetext_propertytype region bretagne gmd:pt_freetext_propertytype service sig missing 02 90 09 16 28 gmd:pt_freetext_propertytype 283 avenue general patton - cs 21101 rennes cedex 7 france 35711 pays sig@region-bretagne.fr http://www.bretagne.fr www:link-1.0-http--link applciation profile missing missing information hours of service contact instructions originator
15	\N	7	<gmd:CI_ResponsibleParty xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gco="http://www.isotc211.org/2005/gco" xsi:schemaLocation="http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd">\r\n  <gmd:individualName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>VINSONNEAU Lydie</gco:CharacterString>\r\n  </gmd:individualName>\r\n  <gmd:organisationName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>Region Bretagne</gco:CharacterString>\r\n  </gmd:organisationName>\r\n  <gmd:positionName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>Responsable Pôle SIG</gco:CharacterString>\r\n  </gmd:positionName>\r\n  <gmd:contactInfo>\r\n    <gmd:CI_Contact>\r\n      <gmd:phone>\r\n        <gmd:CI_Telephone>\r\n          <gmd:voice>\r\n            <gco:CharacterString>0299271478</gco:CharacterString>\r\n          </gmd:voice>\r\n        </gmd:CI_Telephone>\r\n      </gmd:phone>\r\n      <gmd:address>\r\n        <gmd:CI_Address>\r\n          <gmd:deliveryPoint xsi:type="gmd:PT_FreeText_PropertyType">\r\n            <gco:CharacterString>283 avenue du Général Patton</gco:CharacterString>\r\n          </gmd:deliveryPoint>\r\n          <gmd:city>\r\n            <gco:CharacterString>Rennes</gco:CharacterString>\r\n          </gmd:city>\r\n          <gmd:administrativeArea>\r\n            <gco:CharacterString>France</gco:CharacterString>\r\n          </gmd:administrativeArea>\r\n          <gmd:postalCode>\r\n            <gco:CharacterString>35000</gco:CharacterString>\r\n          </gmd:postalCode>\r\n          <gmd:country>\r\n            <gco:CharacterString>Pays</gco:CharacterString>\r\n          </gmd:country>\r\n          <gmd:electronicMailAddress>\r\n            <gco:CharacterString>lydie.vinsonneau@region-bretagne.fr</gco:CharacterString>\r\n          </gmd:electronicMailAddress>\r\n        </gmd:CI_Address>\r\n      </gmd:address>\r\n      <gmd:onlineResource>\r\n        <gmd:CI_OnlineResource>\r\n          <gmd:linkage>\r\n            <gmd:URL>http://www.bretagne.fr</gmd:URL>\r\n          </gmd:linkage>\r\n          <gmd:protocol>\r\n            <gco:CharacterString>WWW:LINK-1.0-http--link</gco:CharacterString>\r\n          </gmd:protocol>\r\n          <gmd:applicationProfile>\r\n            <gco:CharacterString>applciation profile</gco:CharacterString>\r\n          </gmd:applicationProfile>\r\n          <gmd:name>\r\n            <gco:CharacterString gco:nilReason="missing" />\r\n          </gmd:name>\r\n          <gmd:description>\r\n            <gco:CharacterString gco:nilReason="missing" />\r\n          </gmd:description>\r\n          <gmd:function>\r\n            <gmd:CI_OnLineFunctionCode codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_OnLineFunctionCode" codeListValue="information" />\r\n          </gmd:function>\r\n        </gmd:CI_OnlineResource>\r\n      </gmd:onlineResource>\r\n      <gmd:hoursOfService>\r\n        <gco:CharacterString>hours of service</gco:CharacterString>\r\n      </gmd:hoursOfService>\r\n      <gmd:contactInstructions>\r\n        <gco:CharacterString>contact instructions</gco:CharacterString>\r\n      </gmd:contactInstructions>\r\n    </gmd:CI_Contact>\r\n  </gmd:contactInfo>\r\n  <gmd:role>\r\n    <gmd:CI_RoleCode codeListValue="originator" codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_RoleCode" />\r\n  </gmd:role>\r\n</gmd:CI_ResponsibleParty>	http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd gmd:pt_freetext_propertytype vinsonneau lydie gmd:pt_freetext_propertytype region bretagne gmd:pt_freetext_propertytype responsable pole sig 0299271478 gmd:pt_freetext_propertytype 283 avenue du general patton rennes france 35000 pays lydie.vinsonneau@region-bretagne.fr http://www.bretagne.fr www:link-1.0-http--link applciation profile missing missing information hours of service contact instructions originator
16	\N	8	<gmd:CI_ResponsibleParty xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gco="http://www.isotc211.org/2005/gco" xsi:schemaLocation="http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd">\r\n  <gmd:individualName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>BESAND Valerie</gco:CharacterString>\r\n  </gmd:individualName>\r\n  <gmd:organisationName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>Direction Regionale de l'Environnement de l'Amenagement et du Logement de Bretagne</gco:CharacterString>\r\n  </gmd:organisationName>\r\n  <gmd:positionName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>ADL</gco:CharacterString>\r\n  </gmd:positionName>\r\n  <gmd:contactInfo>\r\n    <gmd:CI_Contact>\r\n      <gmd:phone>\r\n        <gmd:CI_Telephone>\r\n          <gmd:voice>\r\n            <gco:CharacterString>02.99.33.43.12</gco:CharacterString>\r\n          </gmd:voice>\r\n        </gmd:CI_Telephone>\r\n      </gmd:phone>\r\n      <gmd:address>\r\n        <gmd:CI_Address>\r\n          <gmd:deliveryPoint xsi:type="gmd:PT_FreeText_PropertyType">\r\n            <gco:CharacterString>10 rue Maurice Fabre</gco:CharacterString>\r\n          </gmd:deliveryPoint>\r\n          <gmd:city>\r\n            <gco:CharacterString>Rennes</gco:CharacterString>\r\n          </gmd:city>\r\n          <gmd:administrativeArea>\r\n            <gco:CharacterString>Incorporation Administrative</gco:CharacterString>\r\n          </gmd:administrativeArea>\r\n          <gmd:postalCode>\r\n            <gco:CharacterString>35065</gco:CharacterString>\r\n          </gmd:postalCode>\r\n          <gmd:country>\r\n            <gco:CharacterString>Pays</gco:CharacterString>\r\n          </gmd:country>\r\n          <gmd:electronicMailAddress>\r\n            <gco:CharacterString>valerie.besand@developpement-durable.gouv.fr</gco:CharacterString>\r\n          </gmd:electronicMailAddress>\r\n        </gmd:CI_Address>\r\n      </gmd:address>\r\n      <gmd:onlineResource>\r\n        <gmd:CI_OnlineResource>\r\n          <gmd:linkage>\r\n            <gmd:URL>http://www.bretagne.developpement-durable.gouv.fr/</gmd:URL>\r\n          </gmd:linkage>\r\n          <gmd:protocol>\r\n            <gco:CharacterString>WWW:LINK-1.0-http--link</gco:CharacterString>\r\n          </gmd:protocol>\r\n          <gmd:applicationProfile>\r\n            <gco:CharacterString>applciation profile</gco:CharacterString>\r\n          </gmd:applicationProfile>\r\n          <gmd:name>\r\n            <gco:CharacterString gco:nilReason="missing" />\r\n          </gmd:name>\r\n          <gmd:description>\r\n            <gco:CharacterString gco:nilReason="missing" />\r\n          </gmd:description>\r\n          <gmd:function>\r\n            <gmd:CI_OnLineFunctionCode codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_OnLineFunctionCode" codeListValue="information" />\r\n          </gmd:function>\r\n        </gmd:CI_OnlineResource>\r\n      </gmd:onlineResource>\r\n      <gmd:hoursOfService>\r\n        <gco:CharacterString>hours of service</gco:CharacterString>\r\n      </gmd:hoursOfService>\r\n      <gmd:contactInstructions>\r\n        <gco:CharacterString>contact instructions</gco:CharacterString>\r\n      </gmd:contactInstructions>\r\n    </gmd:CI_Contact>\r\n  </gmd:contactInfo>\r\n  <gmd:role>\r\n    <gmd:CI_RoleCode codeListValue="originator" codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_RoleCode" />\r\n  </gmd:role>\r\n</gmd:CI_ResponsibleParty>	http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd gmd:pt_freetext_propertytype besand valerie gmd:pt_freetext_propertytype direction regionale de l'environnement de l'amenagement et du logement de bretagne gmd:pt_freetext_propertytype adl 02.99.33.43.12 gmd:pt_freetext_propertytype 10 rue maurice fabre rennes incorporation administrative 35065 pays valerie.besand@developpement-durable.gouv.fr http://www.bretagne.developpement-durable.gouv.fr/ www:link-1.0-http--link applciation profile missing missing information hours of service contact instructions originator
18	\N	1	<gmd:CI_ResponsibleParty xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gco="http://www.isotc211.org/2005/gco" xsi:schemaLocation="http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd">\r\n  <gmd:individualName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>Service de l'Information Géographique</gco:CharacterString>\r\n  </gmd:individualName>\r\n  <gmd:organisationName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>Brest Metropole Oceane</gco:CharacterString>\r\n  </gmd:organisationName>\r\n  <gmd:positionName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString gco:nilReason="missing" />\r\n  </gmd:positionName>\r\n  <gmd:contactInfo>\r\n    <gmd:CI_Contact>\r\n      <gmd:phone>\r\n        <gmd:CI_Telephone>\r\n          <gmd:voice>\r\n            <gco:CharacterString gco:nilReason="missing" />\r\n          </gmd:voice>\r\n          <gmd:voice>\r\n            <gco:CharacterString gco:nilReason="missing" />\r\n          </gmd:voice>\r\n        </gmd:CI_Telephone>\r\n      </gmd:phone>\r\n      <gmd:address>\r\n        <gmd:CI_Address>\r\n          <gmd:deliveryPoint xsi:type="gmd:PT_FreeText_PropertyType">\r\n            <gco:CharacterString>24 rue Coat-ar-Guéven</gco:CharacterString>\r\n          </gmd:deliveryPoint>\r\n          <gmd:city>\r\n            <gco:CharacterString>BREST</gco:CharacterString>\r\n          </gmd:city>\r\n          <gmd:administrativeArea>\r\n            <gco:CharacterString>France</gco:CharacterString>\r\n          </gmd:administrativeArea>\r\n          <gmd:postalCode>\r\n            <gco:CharacterString>29222</gco:CharacterString>\r\n          </gmd:postalCode>\r\n          <gmd:country>\r\n            <gco:CharacterString>Pays</gco:CharacterString>\r\n          </gmd:country>\r\n          <gmd:electronicMailAddress>\r\n            <gco:CharacterString>laurent.dupont@brest-metropole-oceane.fr</gco:CharacterString>\r\n          </gmd:electronicMailAddress>\r\n        </gmd:CI_Address>\r\n      </gmd:address>\r\n      <gmd:onlineResource>\r\n        <gmd:CI_OnlineResource>\r\n          <gmd:linkage>\r\n            <gmd:URL />\r\n          </gmd:linkage>\r\n          <gmd:protocol>\r\n            <gco:CharacterString>WWW:LINK-1.0-http--link</gco:CharacterString>\r\n          </gmd:protocol>\r\n          <gmd:applicationProfile>\r\n            <gco:CharacterString>applciation profile</gco:CharacterString>\r\n          </gmd:applicationProfile>\r\n          <gmd:name>\r\n            <gco:CharacterString gco:nilReason="missing" />\r\n          </gmd:name>\r\n          <gmd:description>\r\n            <gco:CharacterString gco:nilReason="missing" />\r\n          </gmd:description>\r\n          <gmd:function>\r\n            <gmd:CI_OnLineFunctionCode codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_OnLineFunctionCode" codeListValue="information" />\r\n          </gmd:function>\r\n        </gmd:CI_OnlineResource>\r\n      </gmd:onlineResource>\r\n      <gmd:hoursOfService>\r\n        <gco:CharacterString>hours of service</gco:CharacterString>\r\n      </gmd:hoursOfService>\r\n      <gmd:contactInstructions>\r\n        <gco:CharacterString>contact instructions</gco:CharacterString>\r\n      </gmd:contactInstructions>\r\n    </gmd:CI_Contact>\r\n  </gmd:contactInfo>\r\n  <gmd:role>\r\n    <gmd:CI_RoleCode codeListValue="originator" codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_RoleCode" />\r\n  </gmd:role>\r\n</gmd:CI_ResponsibleParty>	http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd gmd:pt_freetext_propertytype service de l'information geographique gmd:pt_freetext_propertytype brest metropole oceane gmd:pt_freetext_propertytype missing missing missing gmd:pt_freetext_propertytype 24 rue coat-ar-gueven brest france 29222 pays laurent.dupont@brest-metropole-oceane.fr www:link-1.0-http--link applciation profile missing missing information hours of service contact instructions originator
19	\N	2	<gmd:CI_ResponsibleParty xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gco="http://www.isotc211.org/2005/gco" xsi:schemaLocation="http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd">\r\n  <gmd:individualName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>ROUILLARD Vincent</gco:CharacterString>\r\n  </gmd:individualName>\r\n  <gmd:organisationName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>Direction Regionale de l'Environnement de l'Amenagement et du Logement de Bretagne</gco:CharacterString>\r\n  </gmd:organisationName>\r\n  <gmd:positionName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>ADL DREAL</gco:CharacterString>\r\n  </gmd:positionName>\r\n  <gmd:contactInfo>\r\n    <gmd:CI_Contact>\r\n      <gmd:phone>\r\n        <gmd:CI_Telephone>\r\n          <gmd:voice>\r\n            <gco:CharacterString>02 99 33 45 34</gco:CharacterString>\r\n          </gmd:voice>\r\n        </gmd:CI_Telephone>\r\n      </gmd:phone>\r\n      <gmd:address>\r\n        <gmd:CI_Address>\r\n          <gmd:deliveryPoint xsi:type="gmd:PT_FreeText_PropertyType">\r\n            <gco:CharacterString>10 rue Maurice Fabre</gco:CharacterString>\r\n          </gmd:deliveryPoint>\r\n          <gmd:city>\r\n            <gco:CharacterString>Rennes</gco:CharacterString>\r\n          </gmd:city>\r\n          <gmd:administrativeArea>\r\n            <gco:CharacterString>France</gco:CharacterString>\r\n          </gmd:administrativeArea>\r\n          <gmd:postalCode>\r\n            <gco:CharacterString>35065</gco:CharacterString>\r\n          </gmd:postalCode>\r\n          <gmd:country>\r\n            <gco:CharacterString>Pays</gco:CharacterString>\r\n          </gmd:country>\r\n          <gmd:electronicMailAddress>\r\n            <gco:CharacterString>vincent.rouillard@developpement-durable.gouv.fr</gco:CharacterString>\r\n          </gmd:electronicMailAddress>\r\n        </gmd:CI_Address>\r\n      </gmd:address>\r\n      <gmd:onlineResource>\r\n        <gmd:CI_OnlineResource>\r\n          <gmd:linkage>\r\n            <gmd:URL />\r\n          </gmd:linkage>\r\n          <gmd:protocol>\r\n            <gco:CharacterString>WWW:LINK-1.0-http--link</gco:CharacterString>\r\n          </gmd:protocol>\r\n          <gmd:applicationProfile>\r\n            <gco:CharacterString>applciation profile</gco:CharacterString>\r\n          </gmd:applicationProfile>\r\n          <gmd:name>\r\n            <gco:CharacterString gco:nilReason="missing" />\r\n          </gmd:name>\r\n          <gmd:description>\r\n            <gco:CharacterString gco:nilReason="missing" />\r\n          </gmd:description>\r\n          <gmd:function>\r\n            <gmd:CI_OnLineFunctionCode codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_OnLineFunctionCode" codeListValue="information" />\r\n          </gmd:function>\r\n        </gmd:CI_OnlineResource>\r\n      </gmd:onlineResource>\r\n      <gmd:hoursOfService>\r\n        <gco:CharacterString>hours of service</gco:CharacterString>\r\n      </gmd:hoursOfService>\r\n      <gmd:contactInstructions>\r\n        <gco:CharacterString>contact instructions</gco:CharacterString>\r\n      </gmd:contactInstructions>\r\n    </gmd:CI_Contact>\r\n  </gmd:contactInfo>\r\n  <gmd:role>\r\n    <gmd:CI_RoleCode codeListValue="originator" codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_RoleCode" />\r\n  </gmd:role>\r\n</gmd:CI_ResponsibleParty>	http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd gmd:pt_freetext_propertytype rouillard vincent gmd:pt_freetext_propertytype direction regionale de l'environnement de l'amenagement et du logement de bretagne gmd:pt_freetext_propertytype adl dreal 02 99 33 45 34 gmd:pt_freetext_propertytype 10 rue maurice fabre rennes france 35065 pays vincent.rouillard@developpement-durable.gouv.fr www:link-1.0-http--link applciation profile missing missing information hours of service contact instructions originator
17	\N	0	<gmd:CI_ResponsibleParty xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gco="http://www.isotc211.org/2005/gco" xsi:schemaLocation="http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd">\r\n  <gmd:individualName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>MARZIOU Stephanie</gco:CharacterString>\r\n  </gmd:individualName>\r\n  <gmd:organisationName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>Direction Regionale de l'Environnement de l'Amenagement et du Logement de Bretagne</gco:CharacterString>\r\n  </gmd:organisationName>\r\n  <gmd:positionName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>Géomaticienne</gco:CharacterString>\r\n  </gmd:positionName>\r\n  <gmd:contactInfo>\r\n    <gmd:CI_Contact>\r\n      <gmd:phone>\r\n        <gmd:CI_Telephone>\r\n          <gmd:voice>\r\n            <gco:CharacterString gco:nilReason="missing" />\r\n          </gmd:voice>\r\n        </gmd:CI_Telephone>\r\n      </gmd:phone>\r\n      <gmd:address>\r\n        <gmd:CI_Address>\r\n          <gmd:deliveryPoint xsi:type="gmd:PT_FreeText_PropertyType">\r\n            <gco:CharacterString>10 rue Maurice Fabre</gco:CharacterString>\r\n          </gmd:deliveryPoint>\r\n          <gmd:city>\r\n            <gco:CharacterString>Rennes</gco:CharacterString>\r\n          </gmd:city>\r\n          <gmd:administrativeArea>\r\n            <gco:CharacterString>France</gco:CharacterString>\r\n          </gmd:administrativeArea>\r\n          <gmd:postalCode>\r\n            <gco:CharacterString>35065</gco:CharacterString>\r\n          </gmd:postalCode>\r\n          <gmd:country>\r\n            <gco:CharacterString>Pays</gco:CharacterString>\r\n          </gmd:country>\r\n          <gmd:electronicMailAddress>\r\n            <gco:CharacterString>stephanie.marziou@developpement-durable.gouv.fr</gco:CharacterString>\r\n          </gmd:electronicMailAddress>\r\n        </gmd:CI_Address>\r\n      </gmd:address>\r\n      <gmd:onlineResource>\r\n        <gmd:CI_OnlineResource>\r\n          <gmd:linkage>\r\n            <gmd:URL />\r\n          </gmd:linkage>\r\n          <gmd:protocol>\r\n            <gco:CharacterString>WWW:LINK-1.0-http--link</gco:CharacterString>\r\n          </gmd:protocol>\r\n          <gmd:applicationProfile>\r\n            <gco:CharacterString>applciation profile</gco:CharacterString>\r\n          </gmd:applicationProfile>\r\n          <gmd:name>\r\n            <gco:CharacterString gco:nilReason="missing" />\r\n          </gmd:name>\r\n          <gmd:description>\r\n            <gco:CharacterString gco:nilReason="missing" />\r\n          </gmd:description>\r\n          <gmd:function>\r\n            <gmd:CI_OnLineFunctionCode codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_OnLineFunctionCode" codeListValue="information" />\r\n          </gmd:function>\r\n        </gmd:CI_OnlineResource>\r\n      </gmd:onlineResource>\r\n      <gmd:hoursOfService>\r\n        <gco:CharacterString>hours of service</gco:CharacterString>\r\n      </gmd:hoursOfService>\r\n      <gmd:contactInstructions>\r\n        <gco:CharacterString>contact instructions</gco:CharacterString>\r\n      </gmd:contactInstructions>\r\n    </gmd:CI_Contact>\r\n  </gmd:contactInfo>\r\n  <gmd:role>\r\n    <gmd:CI_RoleCode codeListValue="originator" codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_RoleCode" />\r\n  </gmd:role>\r\n</gmd:CI_ResponsibleParty>	http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd gmd:pt_freetext_propertytype marziou stephanie gmd:pt_freetext_propertytype direction regionale de l'environnement de l'amenagement et du logement de bretagne gmd:pt_freetext_propertytype geomaticienne missing gmd:pt_freetext_propertytype 10 rue maurice fabre rennes france 35065 pays stephanie.marziou@developpement-durable.gouv.fr www:link-1.0-http--link applciation profile missing missing information hours of service contact instructions originator
\.

COPY sharedobject_templates (typename, template) FROM stdin;
shared_contacts	<gmd:CI_ResponsibleParty xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gco="http://www.isotc211.org/2005/gco" xsi:schemaLocation="http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd">\r\n  <gmd:individualName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>Nom</gco:CharacterString>\r\n  </gmd:individualName>\r\n  <gmd:organisationName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>Organisme</gco:CharacterString>\r\n  </gmd:organisationName>\r\n  <gmd:positionName xsi:type="gmd:PT_FreeText_PropertyType">\r\n    <gco:CharacterString>Position</gco:CharacterString>\r\n  </gmd:positionName>\r\n  <gmd:contactInfo>\r\n    <gmd:CI_Contact>\r\n      <gmd:phone>\r\n        <gmd:CI_Telephone>\r\n          <gmd:voice>\r\n            <gco:CharacterString>Voice 1</gco:CharacterString>\r\n          </gmd:voice>\r\n        </gmd:CI_Telephone>\r\n      </gmd:phone>\r\n      <gmd:address>\r\n        <gmd:CI_Address>\r\n          <gmd:deliveryPoint xsi:type="gmd:PT_FreeText_PropertyType">\r\n            <gco:CharacterString>Adresse</gco:CharacterString>\r\n          </gmd:deliveryPoint>\r\n          <gmd:city>\r\n            <gco:CharacterString>Ville</gco:CharacterString>\r\n          </gmd:city>\r\n          <gmd:administrativeArea>\r\n            <gco:CharacterString>Incorporation Administrative</gco:CharacterString>\r\n          </gmd:administrativeArea>\r\n          <gmd:postalCode>\r\n            <gco:CharacterString>Code postal</gco:CharacterString>\r\n          </gmd:postalCode>\r\n          <gmd:country>\r\n            <gco:CharacterString>Pays</gco:CharacterString>\r\n          </gmd:country>\r\n          <gmd:electronicMailAddress>\r\n            <gco:CharacterString>email@address.com</gco:CharacterString>\r\n          </gmd:electronicMailAddress>\r\n        </gmd:CI_Address>\r\n      </gmd:address>\r\n      <gmd:onlineResource>\r\n        <gmd:CI_OnlineResource>\r\n          <gmd:linkage>\r\n            <gmd:URL>http://website.com</gmd:URL>\r\n          </gmd:linkage>\r\n          <gmd:protocol>\r\n            <gco:CharacterString>WWW:LINK-1.0-http--link</gco:CharacterString>\r\n          </gmd:protocol>\r\n          <gmd:applicationProfile>\r\n            <gco:CharacterString>applciation profile</gco:CharacterString>\r\n          </gmd:applicationProfile>\r\n          <gmd:name>\r\n            <gco:CharacterString gco:nilReason="missing" />\r\n          </gmd:name>\r\n          <gmd:description>\r\n            <gco:CharacterString gco:nilReason="missing" />\r\n          </gmd:description>\r\n          <gmd:function>\r\n            <gmd:CI_OnLineFunctionCode codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_OnLineFunctionCode" codeListValue="information" />\r\n          </gmd:function>\r\n        </gmd:CI_OnlineResource>\r\n      </gmd:onlineResource>\r\n      <gmd:hoursOfService>\r\n        <gco:CharacterString>hours of service</gco:CharacterString>\r\n      </gmd:hoursOfService>\r\n      <gmd:contactInstructions>\r\n        <gco:CharacterString>contact instructions</gco:CharacterString>\r\n      </gmd:contactInstructions>\r\n    </gmd:CI_Contact>\r\n  </gmd:contactInfo>\r\n  <gmd:role>\r\n    <gmd:CI_RoleCode codeListValue="originator" codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_RoleCode" />\r\n  </gmd:role>\r\n</gmd:CI_ResponsibleParty>
\.

COPY spatialindex (fid, id, the_geom) FROM stdin;
\.

ALTER TABLE ONLY shared_contacts
    ADD CONSTRAINT shared_contacts_pkey PRIMARY KEY (shared_contacts_fid);
    
ALTER TABLE ONLY spatialindex
    ADD CONSTRAINT spatialindex_pkey PRIMARY KEY (fid);
    
CREATE INDEX spatial_shared_contacts_the_geom ON shared_contacts USING gist (the_geom);

CREATE INDEX spatialindexndx1 ON spatialindex USING btree (id);

CREATE INDEX spatialindexndx2 ON spatialindex USING gist (the_geom);

--  -- ======================================================================

