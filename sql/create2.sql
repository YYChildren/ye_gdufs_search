    drop table if exists Dump
    drop table if exists Page
    drop table if exists WordUrlPos
    drop table if exists WordUrls
    drop table if exists crawldata_t
    create table CrawlData (
        id bigint not null auto_increment,
        url text,
        content MEDIUMBLOB,
        primary key (id)
    );
    create table Dump (
        keyStr char(16) not null,
        objByte MEDIUMBLOB,
        primary key (keyStr)
    );
    create table Page (
        urlMd5 char(32) not null,
        url text,
        contentMd5 char(32),
        titleFrequency integer not null,
        bodyFrequency integer not null,
        posPath char(64),
        primary key (urlMd5)
    );
    create index page_contentMd5Index on Page(contentMd5);
    create table WordUrlPos (
        urlMd5 char(32) not null,
        word char(64) not null,
        titleWordFrequecy integer not null,
        bodyWordFrequecy integer not null,
        primary key (urlMd5, word)
    );
    create table WordUrls (
        word char(64) not null,
        urlsPath char(64),
        primary key (word)
    );