drop database zkbc_bsd;
create database zkbc_bsd default character set utf8;
grant all privileges on zkbc_bsd.* to 'zkbc_bsd'@'localhost' identified by 'zkbc_bsd' with grant option;
