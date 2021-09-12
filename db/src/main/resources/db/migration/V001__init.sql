CREATE SEQUENCE seq_question START 1;
CREATE SEQUENCE seq_user START 1;

CREATE TABLE t_question (
pkid_question INT CONSTRAINT pk_question PRIMARY KEY,
label varchar not null

);


CREATE TABLE t_user (
pkid_user INT CONSTRAINT pk_user PRIMARY KEY,
uuid uuid not null,
nom varchar not null,
prenom varchar not null,
email varchar not null,
mdp varchar not null

);