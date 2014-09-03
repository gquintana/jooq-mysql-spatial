-- Create mysql_jooq schema and user
-- for testing purpose
create schema mysql_jooq character set utf8;
create user 'mysql_jooq'@'%' identified by 'mysql_jooq';
grant all on mysql_jooq.* to 'mysql_jooq'@'%';
