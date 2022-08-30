ALTER TABLE if exists products
ALTER COLUMN description TYPE varchar(20000);

ALTER TABLE if exists messages
ALTER COLUMN content TYPE varchar(600);