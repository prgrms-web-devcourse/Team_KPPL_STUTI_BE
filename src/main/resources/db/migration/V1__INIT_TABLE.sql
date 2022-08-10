create table MEMBER
(
    MEMBER_ID         bigint auto_increment
        primary key,
    CREATED_AT        datetime(6)  not null,
    UPDATED_AT        datetime(6)  not null,
    BLOG_URL          varchar(100) null,
    CAREER            varchar(20)  not null,
    EMAIL             varchar(40)  not null,
    FIELD             varchar(20)  not null,
    GITHUB_URL        varchar(100) null,
    IS_DELETED        bit          not null,
    MBTI              varchar(5)   not null,
    MEMBER_ROLE       varchar(16)  not null,
    NICK_NAME         varchar(30)  not null,
    PROFILE_IMAGE_URL varchar(150) null,
    constraint UK_c619bafwe6254fnnmnb83mf54
        unique (EMAIL),
    constraint UK_tb4f7a4tx2f2ut4699qb2fwey
        unique (NICK_NAME)
);

create table POST
(
    POST_ID    bigint auto_increment
        primary key,
    CREATED_AT datetime(6)   not null,
    UPDATED_AT datetime(6)   not null,
    CREATED_BY bigint        not null,
    UPDATED_BY bigint        not null,
    CONTENTS   varchar(1000) not null,
    MEMBER_ID  bigint        null,
    constraint FKjflcums7yp2nubhuaf5tt21o1
        foreign key (MEMBER_ID) references MEMBER (MEMBER_ID)
);

create table POST_COMMENT
(
    POST_COMMENT_ID bigint auto_increment
        primary key,
    CREATED_AT      datetime(6)  not null,
    UPDATED_AT      datetime(6)  not null,
    CREATED_BY      bigint       not null,
    UPDATED_BY      bigint       not null,
    CONTENTS        varchar(500) not null,
    MEMBER_ID       bigint       null,
    PARENT_ID       bigint       null,
    POST_ID         bigint       null,
    constraint FK3m16uxt84f09ptodrtvq5noh7
        foreign key (POST_ID) references POST (POST_ID),
    constraint FKhq0vwty7s7mkv8frycm2me0wg
        foreign key (PARENT_ID) references POST_COMMENT (POST_COMMENT_ID),
    constraint FKpa7rfwi2jghu34jfxmulayo4e
        foreign key (MEMBER_ID) references MEMBER (MEMBER_ID)
);

create table POST_IMAGE
(
    POST_IMAGE_ID bigint auto_increment
        primary key,
    CREATED_AT    datetime(6)  not null,
    UPDATED_AT    datetime(6)  not null,
    CREATED_BY    bigint       not null,
    UPDATED_BY    bigint       not null,
    IMAGE_URL     varchar(150) null,
    POST_ID       bigint       null,
    constraint UK_h717ov6kyv2aon3fut2h1l61i
        unique (IMAGE_URL),
    constraint FK11guhhhnjmk1uoeihmjuvujpk
        foreign key (POST_ID) references POST (POST_ID)
);

create table POST_LIKE
(
    POST_LIKE_ID bigint auto_increment
        primary key,
    CREATED_AT   datetime(6) not null,
    UPDATED_AT   datetime(6) not null,
    CREATED_BY   bigint      not null,
    UPDATED_BY   bigint      not null,
    MEMBER_ID    bigint      null,
    POST_ID      bigint      null,
    constraint FK44y549hjuiu6m5bvk44i9g1i5
        foreign key (POST_ID) references POST (POST_ID),
    constraint FKf3si4oqpqxdro1ih5auvux5xd
        foreign key (MEMBER_ID) references MEMBER (MEMBER_ID)
);

create table STUDY_GROUP
(
    STUDY_GROUP_ID       bigint auto_increment
        primary key,
    CREATED_AT           datetime(6)   not null,
    UPDATED_AT           datetime(6)   not null,
    CREATED_BY           bigint        not null,
    UPDATED_BY           bigint        not null,
    DESCRIPTION          varchar(1000) not null,
    IMAGE_URL            varchar(150)  null,
    IS_DELETED           bit           not null,
    IS_ONLINE            bit           not null,
    NUMBER_OF_APPLICANTS int           not null,
    NUMBER_OF_MEMBERS    int           not null,
    NUMBER_OF_RECRUITS   int           not null,
    REGION               varchar(16)   not null,
    END_DATE_TIME        datetime(6)   not null,
    START_DATE_TIME      datetime(6)   not null,
    TITLE                varchar(100)  not null,
    TOPIC                varchar(20)   not null
);

create table PREFERRED_MBTI
(
    PREFERRED_MBTI_ID bigint auto_increment
        primary key,
    MBTI              varchar(4) not null,
    STUDY_GROUP_ID    bigint     null,
    constraint FKg0gf4ipci6odedxa9v7df2wvp
        foreign key (STUDY_GROUP_ID) references STUDY_GROUP (STUDY_GROUP_ID)
);

create table STUDY_GROUP_MEMBER
(
    STUDY_GROUP_MEMBER_ID   bigint auto_increment
        primary key,
    CREATED_AT              datetime(6) not null,
    UPDATED_AT              datetime(6) not null,
    CREATED_BY              bigint      not null,
    UPDATED_BY              bigint      not null,
    STUDY_GROUP_MEMBER_ROLE varchar(20) not null,
    MEMBER_ID               bigint      null,
    STUDY_GROUP_ID          bigint      null,
    constraint FK89w0nm3i3vqke3at3f10l58m6
        foreign key (STUDY_GROUP_ID) references STUDY_GROUP (STUDY_GROUP_ID),
    constraint FKgujt6hpyjo0ec5twx2ts0e3ks
        foreign key (MEMBER_ID) references MEMBER (MEMBER_ID)
);

create table STUDY_GROUP_QUESTION
(
    STUDY_GROUP_QUESTION_ID bigint auto_increment
        primary key,
    CREATED_AT              datetime(6)  not null,
    UPDATED_AT              datetime(6)  not null,
    CREATED_BY              bigint       not null,
    UPDATED_BY              bigint       not null,
    CONTENTS                varchar(500) not null,
    MEMBER_ID               bigint       null,
    PARENT_ID               bigint       null,
    STUDY_GROUP_ID          bigint       null,
    constraint FK3ct2c96n5na4i7l5d9k4r9t02
        foreign key (STUDY_GROUP_ID) references STUDY_GROUP (STUDY_GROUP_ID),
    constraint FK5x6qchqtthkcjw1op78cpge8v
        foreign key (MEMBER_ID) references MEMBER (MEMBER_ID),
    constraint FKs5ekehbrprf859sn01qx7shbu
        foreign key (PARENT_ID) references STUDY_GROUP_QUESTION (STUDY_GROUP_QUESTION_ID)
);

