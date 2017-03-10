--create or replace procedure dropTableIfExists(tab_name IN varchar2) IS
--begin
--execute immediate 'drop table ' || tab_name || ' cascade constraints purge';
--commit;
--exception
--WHEN OTHERS THEN NULL;
--end dropTableIfExists;
--/
--exec dropTableIfExists ('im_user');
--exec dropTableIfExists ('im_role');
--exec dropTableIfExists ('im_user_has_role');
--exec dropTableIfExists ('im_id_table');
drop table im_user cascade constraints purge;
drop table im_role cascade constraints purge;
drop table im_user_has_role cascade constraints purge;
drop table im_id_table cascade constraints purge;
create table im_user (id number(10,0) not null, olVersion number(10,0) not null, loginName varchar2(255) not null unique, firstName varchar2(255) not null, lastName varchar2(255) not null, password varchar2(255) not null, lastPasswordChange date not null, primary key (id));
create table im_role (id number(10,0) not null, olVersion number(10,0) not null, roleName varchar2(255) not null unique, description varchar2(4000), primary key (id));
create table im_user_has_role (im_user_id number(10,0) not null, im_role_id number(10,0) not null, primary key (im_user_id, im_role_id));
create table im_id_table (nextId number(10,0) not null unique);
alter table im_user_has_role add constraint ref_im_role_id foreign key (im_role_id) references im_role;
alter table im_user_has_role add constraint ref_im_user_id foreign key (im_user_id) references im_user;
insert into im_id_table(nextId) values(1);