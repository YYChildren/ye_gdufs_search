use gdufs_search;
drop table if exists crawldata;
drop table if exists Dump;
drop table if exists Page;
drop table if exists WordUrlPos;
drop table if exists WordUrls;
create table crawldata(
    id bigint not null auto_increment,
    url char(32),
    content mediumblob,
    primary key (id)
);
create table Dump (
    keyStr char(32) not null,
    objByte mediumblob,
    primary key (keyStr)
);
create table Page (
    urlMd5 char(32) not null,
    url text,
    contentMd5 char(32),
    titleFrequency integer not null,
    bodyFrequency integer not null,
    serName char(32),
    primary key (urlMd5)
);
create table WordUrlPos (
    word char(64) not null,
    urlMd5 char(32) not null,
    titleWordFrequecy integer not null,
    bodyWordFrequecy integer not null,
    serName char(32),
    primary key (word,urlMd5)
);
create table WordUrls (
    word char(64) not null,
    serName char(32),
    primary key (word)
);
