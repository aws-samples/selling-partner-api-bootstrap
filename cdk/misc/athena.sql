-- JSON https://docs.aws.amazon.com/athena/latest/ug/json-serde.html#hive-json-serde
-- https://docs.aws.amazon.com/athena/latest/ug/create-table.html
CREATE EXTERNAL TABLE IF NOT EXISTS orders_ext (
    sellerid                string,
    amazonorderid       	string,
    purchasedate        	TIMESTAMP,
    lastupdatedate      	TIMESTAMP,
    orderstatus         	string,
    fulfillmentchannel  	string,
    numberofitemsshipped	int,
    numberofitemsunshipped	int,
    paymentmethod       	string,
    paymentmethoddetails	array<string>,
    marketplaceid       	string,
    shipmentservicelevelcategory	string,
    ordertype           	string,
    earliestshipdate    	TIMESTAMP,
    latestshipdate      	TIMESTAMP,
    isbusinessorder     	boolean,
    isprime             	boolean,
    isglobalexpressenabled	boolean,
    ispremiumorder      	boolean,
    ordertotalamount        int,
    issoldbyab          	boolean
    )
PARTITIONED BY (date string)
ROW FORMAT  serde 'org.apache.hive.hcatalog.data.JsonSerDe'
    -- with serdeproperties ( 'paths'='amazonorderid, purchasedate, lastupdatedate, orderstatus, fulfillmentchannel, numberofitemsshipped,	numberofitemsunshipped,	paymentmethod, paymentmethoddetails, marketplaceid, shipmentservicelevelcategory, ordertype, earliestshipdate, isbusinessorder, isprime, isglobalexpressenabled, ispremiumorder, issoldbyab' )
LOCATION 's3://spapi-reporting/orders/raw_events/' ;


alter table add partition orders_ext;
msck repair table orders_ext;
stored as GZIP


CREATE EXTERNAL TABLE IF NOT EXISTS fba_ext (
    sellerid                    string,
    asin                        string,
    fnSku                       string,
    sellerSku                   string,
    condition                   string,
    fulfillableQuantity         int,
    inboundShippedQuantity      int,
    inboundWorkingQuantity      int,
    inboundReceivingQuantity    int,
    eventTime                   TIMESTAMP
    )
PARTITIONED BY (date string)
ROW FORMAT  serde 'org.apache.hive.hcatalog.data.JsonSerDe'
LOCATION 's3://spapi-reporting/fba/';



MSCK REPAIR TABLE fba_ext;



CREATE EXTERNAL TABLE IF NOT EXISTS spapistatus_ext (
    sellerid                    string,
    apiName                     string,
    resourceName                string,
    resourceAction              string,
    invocationStatus            string,
    datetime                    TIMESTAMP
    )
PARTITIONED BY (date string)
ROW FORMAT  serde 'org.apache.hive.hcatalog.data.JsonSerDe'
LOCATION 's3://spapi-reporting/crawler/events/';



MSCK REPAIR TABLE spapistatus_ext;


CREATE EXTERNAL TABLE IF NOT EXISTS orderitems_ext (
    sellerid                   string,
    amazonorderid              string,
    orderitemid                string,
    sellersku                  string,
    asin                       string,
    title                      string,
    quantityordered            int,
    quantityshipped            int,
    itemprice_currencycode     string,
    itemprice_amount           int,
    eventtime                  TIMESTAMP
    )
PARTITIONED BY (date string)
ROW FORMAT  serde 'org.apache.hive.hcatalog.data.JsonSerDe'
LOCATION 's3://spapi-reporting/order-items/';



MSCK REPAIR TABLE orderitems_ext;