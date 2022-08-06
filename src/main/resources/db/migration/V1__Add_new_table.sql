DROP TABLE IF EXISTS COMMENT;
DROP TABLE IF EXISTS FEED;
DROP TABLE IF EXISTS FEED_IMAGE;
DROP TABLE IF EXISTS FEED_LIKE;
DROP TABLE IF EXISTS MEMBER;
DROP TABLE IF EXISTS PREFERRED_MBTIS;
DROP TABLE IF EXISTS STUDY_GROUP;
DROP TABLE IF EXISTS STUDY_GROUP_MEMBER;
DROP TABLE IF EXISTS STUDY_GROUP_QUESTION;

create table COMMENT
(
    COMMENT_ID bigint       not null auto_increment,
    CREATED_AT datetime(6) not null,
    UPDATED_AT datetime(6) not null,
    CREATED_BY bigint       not null,
    UPDATED_BY bigint       not null,
    CONTENTS   varchar(500) not null,
    FEED_ID    bigint,
    MEMBER_ID  bigint,
    PARENT_ID  bigint,
    primary key (COMMENT_ID)
) engine=InnoDB;

create table FEED
(
    FEED_ID    bigint        not null auto_increment,
    CREATED_AT datetime(6) not null,
    UPDATED_AT datetime(6) not null,
    CREATED_BY bigint        not null,
    UPDATED_BY bigint        not null,
    CONTENTS   varchar(1000) not null,
    MEMBER_ID  bigint,
    primary key (FEED_ID)
) engine=InnoDB;

create table FEED_IMAGE
(
    FEED_IMAGE_ID bigint not null auto_increment,
    CREATED_AT    datetime(6) not null,
    UPDATED_AT    datetime(6) not null,
    CREATED_BY    bigint not null,
    UPDATED_BY    bigint not null,
    IMAGE_URL     varchar(150),
    FEED_ID       bigint,
    primary key (FEED_IMAGE_ID)
) engine=InnoDB;

create table FEED_LIKE
(
    FEED_LIKE_ID bigint not null auto_increment,
    CREATED_AT   datetime(6) not null,
    UPDATED_AT   datetime(6) not null,
    CREATED_BY   bigint not null,
    UPDATED_BY   bigint not null,
    FEED_ID      bigint,
    MEMBER_ID    bigint,
    primary key (FEED_LIKE_ID)
) engine=InnoDB;

create table MEMBER
(
    MEMBER_ID         bigint      not null auto_increment,
    CREATED_AT        datetime(6) not null,
    UPDATED_AT        datetime(6) not null,
    BLOG_URL          varchar(100),
    CAREER            varchar(20) not null,
    EMAIL             varchar(40) not null,
    FIELD             varchar(20) not null,
    GITHUB_URL        varchar(100),
    IS_DELETED        bit         not null,
    MBTI              varchar(5)  not null,
    MEMBER_ROLE       varchar(16) not null,
    NICK_NAME         varchar(30) not null,
    PROFILE_IMAGE_URL varchar(150),
    primary key (MEMBER_ID)
) engine=InnoDB;

create table PREFERRED_MBTIS
(
    STUDY_GROUP_ID  bigint       not null,
    PREFERRED_MBTIS varchar(255) not null,
    primary key (STUDY_GROUP_ID, PREFERRED_MBTIS)
) engine=InnoDB;

create table STUDY_GROUP
(
    STUDY_GROUP_ID     bigint        not null auto_increment,
    CREATED_AT         datetime(6) not null,
    UPDATED_AT         datetime(6) not null,
    CREATED_BY         bigint        not null,
    UPDATED_BY         bigint        not null,
    DESCRIPTION        varchar(1000) not null,
    IMAGE_URL          varchar(150),
    IS_DELETED         bit           not null,
    IS_ONLINE          bit           not null,
    NUMBER_OF_MEMBERS  integer       not null,
    NUMBER_OF_RECRUITS integer       not null,
    REGION             varchar(16),
    END_DATE_TIME      datetime(6) not null,
    START_DATE_TIME    datetime(6) not null,
    THUMBNAIL_URL      varchar(150),
    TITLE              varchar(100)  not null,
    TOPIC              varchar(20)   not null,
    primary key (STUDY_GROUP_ID)
) engine=InnoDB;

create table STUDY_GROUP_MEMBER
(
    STUDY_GROUP_MEMBER_ID   bigint      not null auto_increment,
    CREATED_AT              datetime(6) not null,
    UPDATED_AT              datetime(6) not null,
    CREATED_BY              bigint      not null,
    UPDATED_BY              bigint      not null,
    STUDY_GROUP_MEMBER_ROLE varchar(20) not null,
    MEMBER_ID               bigint,
    STUDY_GROUP_ID          bigint,
    primary key (STUDY_GROUP_MEMBER_ID)
) engine=InnoDB;

create table STUDY_GROUP_QUESTION
(
    STUDY_GROUP_QUESTION_ID bigint       not null auto_increment,
    CREATED_AT              datetime(6) not null,
    UPDATED_AT              datetime(6) not null,
    CREATED_BY              bigint       not null,
    UPDATED_BY              bigint       not null,
    CONTENTS                varchar(500) not null,
    MEMBER_ID               bigint,
    PARENT_ID               bigint,
    STUDY_GROUP_ID          bigint,
    primary key (STUDY_GROUP_QUESTION_ID)
) engine=InnoDB;
