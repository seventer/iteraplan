-- drop foreign key constraints
if OBJECT_ID('ref_im_role_id') is not NULL alter table im_user_has_role drop constraint ref_im_role_id;
if OBJECT_ID('ref_im_user_id') is not NULL alter table im_user_has_role drop constraint ref_im_user_id;

-- drop tables
if OBJECT_ID('im_user') is not NULL drop table im_user;
if OBJECT_ID('im_role') is not NULL drop table im_role;
if OBJECT_ID('im_user_has_role') is not NULL drop table im_user_has_role;
if OBJECT_ID('im_id_table') is not NULL drop table im_id_table;


-- create user table
create table im_user (
	id int not null,
	olVersion int not null,
	loginName varchar(255) not null unique,
	firstName varchar(255) not null,
	lastName varchar(255) not null,
	password varchar(255) not null,
	lastPasswordChange datetime not null,
	primary key (id)
);

-- create role table
create table im_role (
	id int not null,
	olVersion int not null,
	roleName varchar(255) not null unique,
	description text,
	primary key (id)
);

-- create user/role mapping table
create table im_user_has_role (
	im_user_id int not null,
	im_role_id int not null,
	primary key (im_user_id, im_role_id)
);

-- create id generation table
create table im_id_table (
	nextId int not null unique
);


-- add foreign key constraints
alter table im_user_has_role add constraint ref_im_role_id foreign key (im_role_id) references im_role (id);
alter table im_user_has_role add constraint ref_im_user_id foreign key (im_user_id) references im_user (id);

-- add indexes
create index index_im_role_id on im_user_has_role (im_role_id);
create index index_im_user_id on im_user_has_role (im_user_id);

-- insert start value in id generation table
insert into im_id_table(nextId) values(1);
