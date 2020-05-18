insert into player (id,login,email,name,password) values (1, 'yuta4', 'yura.berezin@gmail.com','Yura','$2a$10$wp1nBzyZwh6cX3jn31NfW.H4IT3mspVB6USZhVpSqC4dLH0QFHlmi');
insert into player (id,login,email,name,password) values (2, 'Irunia', 'gapchukirina@gmail.com','Irunia','$2a$10$wp1nBzyZwh6cX3jn31NfW.H4IT3mspVB6USZhVpSqC4dLH0QFHlmi');
insert into player (id,login,email,name,password) values (3, 'Dima', 'dima@gmail.com','Dima','$2a$10$wp1nBzyZwh6cX3jn31NfW.H4IT3mspVB6USZhVpSqC4dLH0QFHlmi');
insert into player (id,login,email,name,password) values (4, 'Anna', 'anna@gmail.com','Anna','$2a$10$wp1nBzyZwh6cX3jn31NfW.H4IT3mspVB6USZhVpSqC4dLH0QFHlmi');
insert into player (id,login,email,name,password) values (5, 'Miracle_foxy', 'Miracle_foxy@gmail.com','Miracle_foxy','$2a$10$wp1nBzyZwh6cX3jn31NfW.H4IT3mspVB6USZhVpSqC4dLH0QFHlmi');
insert into player (id,login,email,name,password) values (6, 'Mr_Mayers', 'Mr_Mayers@gmail.com','Mr_Mayers','$2a$10$wp1nBzyZwh6cX3jn31NfW.H4IT3mspVB6USZhVpSqC4dLH0QFHlmi');
DROP sequence player_seq;
create sequence player_seq start 1 increment 50;
SELECT SETVAL('player_seq',MAX(id)+1) FROM player;