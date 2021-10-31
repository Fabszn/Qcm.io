CREATE SEQUENCE seq_question START 1;
CREATE SEQUENCE seq_reponse START 1;
CREATE SEQUENCE seq_user START 1;
CREATE SEQUENCE seq_account START 1;
CREATE SEQUENCE seq_candidat START 1;

CREATE TABLE t_question (
pkid_question INT CONSTRAINT pk_question PRIMARY KEY,
label varchar not null
);

CREATE TABLE t_reponse (
pkid_reponse INT CONSTRAINT pk_reponse PRIMARY KEY,
fkid_question INT not null,
label varchar not null,
CONSTRAINT fk_question foreign key(fkid_question) REFERENCES t_question(pkid_question)
);


CREATE TABLE t_candidat (
pkid_candidat INT CONSTRAINT pk_user PRIMARY KEY,
uuid uuid not null,
nom varchar not null,
prenom varchar not null,
email varchar not null,
mdp varchar not null
);

CREATE TABLE t_account (
pkid_account INT CONSTRAINT pk_account PRIMARY KEY,
fkid_candidat INT not null,
last_connexion_date timestamp with time zone not null default now(),
CONSTRAINT fk_candidat foreign key(fkid_candidat) REFERENCES t_candidat(pkid_candidat)
);