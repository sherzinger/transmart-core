--
-- Type: TABLE; Owner: BIOMART; Name: BIO_ASSAY_PLATFORM
--
 CREATE TABLE "BIOMART"."BIO_ASSAY_PLATFORM" 
  (	 BIO_ASSAY_PLATFORM_ID       NUMBER(18)        NOT NULL,
  PLATFORM_NAME               VARCHAR2(200 BYTE),
  PLATFORM_VERSION            VARCHAR2(200 BYTE),
  PLATFORM_DESCRIPTION        VARCHAR2(2000 BYTE),
  PLATFORM_ARRAY              VARCHAR2(50 BYTE),
  PLATFORM_ACCESSION          VARCHAR2(20 BYTE),
  PLATFORM_ORGANISM           VARCHAR2(200 BYTE),
  PLATFORM_VENDOR             VARCHAR2(200 BYTE),
  PLATFORM_TYPE               VARCHAR2(200 BYTE),
  PLATFORM_TECHNOLOGY         VARCHAR2(200 BYTE),
  PLATFORM_IMPUTED_ALGORITHM  VARCHAR2(500 BYTE),
  PLATFORM_IMPUTED_PANEL      VARCHAR2(200 BYTE),
  PLATFORM_IMPUTED            CHAR(1 BYTE),
  CREATED_BY                  VARCHAR2(30 BYTE),
  CREATED_DATE                DATE,
  MODIFIED_BY                 VARCHAR2(30 BYTE),
  MODIFIED_DATE               DATE,
 CONSTRAINT "BIO_ASSAY_PLATFORM_PK" PRIMARY KEY ("BIO_ASSAY_PLATFORM_ID")
 USING INDEX
 TABLESPACE "INDX"  ENABLE VALIDATE
  ) SEGMENT CREATION IMMEDIATE
 TABLESPACE "TRANSMART" ;

--
-- Type: TRIGGER; Owner: BIOMART; Name: TRG_BIO_ASSAY_PLATFORM_ID
--
  CREATE OR REPLACE TRIGGER "BIOMART"."TRG_BIO_ASSAY_PLATFORM_ID" before insert on "BIO_ASSAY_PLATFORM"    for each row begin     if inserting then       if :NEW."BIO_ASSAY_PLATFORM_ID" is null then          select SEQ_BIO_DATA_ID.nextval into :NEW."BIO_ASSAY_PLATFORM_ID" from dual;       end if;    end if; end;







/
ALTER TRIGGER "BIOMART"."TRG_BIO_ASSAY_PLATFORM_ID" ENABLE;