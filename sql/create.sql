use gdufs_search2;
drop table if exists CrawlData;
drop table if exists Page;
drop table if exists Word;
create table CrawlData (
id bigint not null auto_increment,
uid bigint not null,
url varchar(2048) not null,
contentMd5 char(32) not null,
serName char(16) not null,
primary key (id)
);
create UNIQUE index crawldata_contentMd5Index on CrawlData(contentMd5);
create table Page (
uid bigint not null,
url varchar(2048) not null,
contentMd5 char(32) not null,
titleFrequency integer not null,
bodyFrequency integer not null,
serName char(16) not null,
primary key (uid)
);
create UNIQUE index page_contentMd5Index on Page(contentMd5);
create table Word (
wid bigint(20) not null,
word varchar(2048) not null,
uidTitleCount bigint not null,
uidBodyCount bigint not null,
serName char(16) not null,
primary key (wid)
);