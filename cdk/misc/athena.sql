-- JSON https://docs.aws.amazon.com/athena/latest/ug/json-serde.html#hive-json-serde
-- https://docs.aws.amazon.com/athena/latest/ug/create-table.html
CREATE EXTERNAL TABLE IF NOT EXISTS orders_ext (
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
    issoldbyab          	boolean
    )
PARTITIONED BY (date string)
ROW FORMAT  serde 'org.apache.hive.hcatalog.data.JsonSerDe'
    -- with serdeproperties ( 'paths'='amazonorderid, purchasedate, lastupdatedate, orderstatus, fulfillmentchannel, numberofitemsshipped,	numberofitemsunshipped,	paymentmethod, paymentmethoddetails, marketplaceid, shipmentservicelevelcategory, ordertype, earliestshipdate, isbusinessorder, isprime, isglobalexpressenabled, ispremiumorder, issoldbyab' )
LOCATION 's3://spapi-reporting/orders/raw_events/' ;


alter table add partition orders_ext;
msck repair table orders_ext;
stored as GZIP