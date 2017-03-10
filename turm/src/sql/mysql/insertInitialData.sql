--
-- Create role 'iteraplan_Supervisor'
--
insert into im_role(id, olVersion, roleName, description) values((select max(nextId) from im_id_table), 0, 'iteraplan_Supervisor', '');
update im_id_table set nextId=(nextId+1);
--
-- Create user 'system' with password 'password' and role 'iteraplan_Supervisor'
--
insert into im_user(id, olVersion, loginName, firstName, lastName, password, lastPasswordChange) values((select max(nextId) from im_id_table), 0, 'system', '-', '-', '5F4DCC3B5AA765D61D8327DEB882CF99', now());
update im_id_table set nextId=(nextId+1);
insert into im_user_has_role(im_user_id, im_role_id) values ((select max(id) from im_user), (select max(id) from im_role));

--
-- Create role 'Administrator iteraplan'
--
insert into im_role(id, olVersion, roleName, description) values((select max(nextId) from im_id_table), 0, 'Administrator iteraplan', '');
update im_id_table set nextId=(nextId+1);
--
-- Create user 'admin' with password 'password' and role 'Administrator iteraplan'
--
insert into im_user(id, olVersion, loginName, firstName, lastName, password, lastPasswordChange) values((select max(nextId) from im_id_table), 0, 'admin', 'Administrator', '-', '5F4DCC3B5AA765D61D8327DEB882CF99', now());
update im_id_table set nextId=(nextId+1);
insert into im_user_has_role(im_user_id, im_role_id) values ((select max(id) from im_user), (select max(id) from im_role));

--
-- Create role 'Application Manager'
--
insert into im_role(id, olVersion, roleName, description) values((select max(nextId) from im_id_table), 0, 'Application Manager', '');
update im_id_table set nextId=(nextId+1);
--
-- Create user 'sue' with password 'password' and role 'Application Manager'
--
insert into im_user(id, olVersion, loginName, firstName, lastName, password, lastPasswordChange) values((select max(nextId) from im_id_table), 0, 'sue', 'Sue', '-', '5F4DCC3B5AA765D61D8327DEB882CF99', now());
update im_id_table set nextId=(nextId+1);
insert into im_user_has_role(im_user_id, im_role_id) values ((select max(id) from im_user), (select max(id) from im_role));

--
-- Create role 'Application-Enterprise-Architect'
--
insert into im_role(id, olVersion, roleName, description) values((select max(nextId) from im_id_table), 0, 'Application-Enterprise-Architect', '');
update im_id_table set nextId=(nextId+1);
--
-- Create user 'joe' with password 'password' and role 'Application-Enterprise-Architect'
--
insert into im_user(id, olVersion, loginName, firstName, lastName, password, lastPasswordChange) values((select max(nextId) from im_id_table), 0, 'Joe', 'Joe', '-', '5F4DCC3B5AA765D61D8327DEB882CF99', now());
update im_id_table set nextId=(nextId+1);
insert into im_user_has_role(im_user_id, im_role_id) values ((select max(id) from im_user), (select max(id) from im_role));

--
-- Create role 'Business-Enterprise-Architect'
--
insert into im_role(id, olVersion, roleName, description) values((select max(nextId) from im_id_table), 0, 'Business-Enterprise-Architect', '');
update im_id_table set nextId=(nextId+1);
--
-- Create user 'bob' with password 'password' and role 'Business-Enterprise-Architect'
--
insert into im_user(id, olVersion, loginName, firstName, lastName, password, lastPasswordChange) values((select max(nextId) from im_id_table), 0, 'bob', 'Bob', '-', '5F4DCC3B5AA765D61D8327DEB882CF99', now());
update im_id_table set nextId=(nextId+1);
insert into im_user_has_role(im_user_id, im_role_id) values ((select max(id) from im_user), (select max(id) from im_role));

--
-- Create role 'CEO/CIO/Strategist'
--
insert into im_role(id, olVersion, roleName, description) values((select max(nextId) from im_id_table), 0, 'CEO/CIO/Strategist', '');
update im_id_table set nextId=(nextId+1);
--
-- Create user 'cio' with password 'password' and role 'CEO/CIO/Strategist'
--
insert into im_user(id, olVersion, loginName, firstName, lastName, password, lastPasswordChange) values((select max(nextId) from im_id_table), 0, 'cio', '-', '-', '5F4DCC3B5AA765D61D8327DEB882CF99', now());
update im_id_table set nextId=(nextId+1);
insert into im_user_has_role(im_user_id, im_role_id) values ((select max(id) from im_user), (select max(id) from im_role));

--
-- Create role 'IT-Architect'
--
insert into im_role(id, olVersion, roleName, description) values((select max(nextId) from im_id_table), 0, 'IT-Architect', '');
update im_id_table set nextId=(nextId+1);
--
-- Create user 'tom' with password 'password' and role 'IT-Architect'
--
insert into im_user(id, olVersion, loginName, firstName, lastName, password, lastPasswordChange) values((select max(nextId) from im_id_table), 0, 'tom', 'Tom', '-', '5F4DCC3B5AA765D61D8327DEB882CF99', now());
update im_id_table set nextId=(nextId+1);
insert into im_user_has_role(im_user_id, im_role_id) values ((select max(id) from im_user), (select max(id) from im_role));

--
-- Create role 'MainUser'
--
insert into im_role(id, olVersion, roleName, description) values((select max(nextId) from im_id_table), 0, 'MainUser', '');
update im_id_table set nextId=(nextId+1);
--
-- Create user 'MainUser' with password 'password' and role 'MainUser'
--
insert into im_user(id, olVersion, loginName, firstName, lastName, password, lastPasswordChange) values((select max(nextId) from im_id_table), 0, 'mainuser', 'MainUser', '-', '5F4DCC3B5AA765D61D8327DEB882CF99', now());
update im_id_table set nextId=(nextId+1);
insert into im_user_has_role(im_user_id, im_role_id) values ((select max(id) from im_user), (select max(id) from im_role));
