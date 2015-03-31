    drop table if exists CrawlData;
    drop table if exists Dump;
    drop table if exists Page;
    drop table if exists WordUrlPos;
    drop table if exists WordUrls;
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
        titleSegments MEDIUMBLOB,
        titlePostags MEDIUMBLOB,
        titleNers MEDIUMBLOB,
        bodyFrequency integer not null,
        bodySegments MEDIUMBLOB,
        bodyPostags MEDIUMBLOB,
        bodyNers MEDIUMBLOB,
        primary key (urlMd5)
    );
    create index page_contentMd5Index on Page(contentMd5);
    create table WordUrlPos (
        word char(64) not null,
        urlMd5 char(32) not null,
        titleWordFrequecy integer not null,
        bodyWordFrequecy integer not null,
        titleWordPos MEDIUMBLOB,
        bodyWordPos MEDIUMBLOB,
        primary key (urlMd5, word)
    );
    create table WordUrls (
        word char(64) not null,
        urls LONGBLOB,
        primary key (word)
    );
    