--
-- Type: TABLE; Owner: BIOMART; Name: BIO_DATA_PLATFORM
--
 CREATE TABLE "BIOMART"."BIO_DATA_PLATFORM" 
  (	"BIO_DATA_ID" NUMBER NOT NULL ENABLE, 
"BIO_ASSAY_PLATFORM_ID" NUMBER NOT NULL ENABLE, 
"ETL_SOURCE" VARCHAR2(100 BYTE)
  ) SEGMENT CREATION DEFERRED
 TABLESPACE "TRANSMART" ;
