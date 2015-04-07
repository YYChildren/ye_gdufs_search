drop table if exists Dump;
drop table if exists Page;
drop table if exists PageMd5;
drop table if exists Word;
drop table if exists crawldata;
create table crawldata (
id bigint not null auto_increment,
url varchar(2048),
content mediumblob,
primary key (id)
);
create table Dump (
keyStr char(16) not null,
objByte mediumblob,
primary key (keyStr)
);
create table Page (
uid bigint not null,
url varchar(2048),
titleFrequency integer not null,
bodyFrequency integer not null,
serName char(16),
primary key (uid)
);
create table PageMd5 (
contentMd5 char(32) not null,
primary key (contentMd5)
);
create table Word (
word char(64),
urlCount bigint not null,
serName char(16),
primary key (word)
);
