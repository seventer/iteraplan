-- remove existing tables
alter table im_user_has_role drop foreign key ref_im_user_id;
alter table im_user_has_role drop foreign key ref_im_role_id;
drop table if exists im_user;
drop table if exists im_role;
drop table if exists im_user_has_role;
drop table if exists im_id_table;
-- create tables
create table im_user (id integer not null auto_increment, olVersion integer not null, loginName varchar(255) not null unique, firstName varchar(255) not null, lastName varchar(255) not null, password varchar(255) not null, lastPasswordChange datetime not null, primary key (id));
create table im_role (id integer not null auto_increment, olVersion integer not null, roleName varchar(255) not null unique, description text, primary key (id));
create table im_user_has_role (im_user_id integer not null, im_role_id integer not null, primary key (im_user_id, im_role_id));
create table im_id_table (nextId integer not null unique);
alter table im_user_has_role add index index_im_role_id (im_role_id), add constraint ref_im_role_id foreign key (im_role_id) references im_role (id);
alter table im_user_has_role add index index_im_user_id (im_user_id), add constraint ref_im_user_id foreign key (im_user_id) references im_user (id);
-- initialize id table
insert into im_id_table(nextId) values(1);