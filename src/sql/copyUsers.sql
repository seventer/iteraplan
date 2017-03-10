-- empty lines are not allowed in this script
SET DEFINE OFF
--
create view usersToCopyView as select iu.loginName as loginName, iu.firstName as firstName, iu.lastName as lastName from im_user iu where iu.loginName not in (select u.loginName from users u);
--
create view firstEntryOfUsersToCopyView as SELECT loginName, firstName, lastName FROM (select (row_number() over (order by iu.loginName asc)) as rownumber, iu.loginName as loginName, iu.firstName as firstName, iu.lastName as lastName from usersToCopyView iu) where rownumber <= 1;
--
declare
x number(10);
begin
EXECUTE IMMEDIATE 'select count(*) from usersToCopyView' INTO x;
while x > 0
loop
EXECUTE IMMEDIATE 'insert into userentity (id, olVersion) values (HIBERNATE_SEQUENCE.NEXTVAL, 0)';
EXECUTE IMMEDIATE 'insert into users (userentity_iduserentity, loginName, firstName, lastName) select HIBERNATE_SEQUENCE.CURRVAL, ui.loginName, ui.firstName, ui.lastName from firstEntryOfUsersToCopyView ui';
EXECUTE IMMEDIATE 'select count(*) from usersToCopyView' INTO x;
COMMIT;
end loop;
end;
/
SET DEFINE ON
-- eof