CREATE SEQUENCE seq_question START 1;
CREATE SEQUENCE seq_reponse START 1;
CREATE SEQUENCE seq_account START 1;
CREATE SEQUENCE seq_user START 1;
CREATE SEQUENCE seq_examen START 1;
CREATE SEQUENCE seq_user_rep START 1;

CREATE TABLE t_question (
pkid_question INT CONSTRAINT pk_question PRIMARY KEY DEFAULT nextval('seq_question'),
label varchar not null,
kind varchar not null
);

CREATE TABLE t_examen (
pkid_examen INT CONSTRAINT pk_examen PRIMARY KEY DEFAULT nextval('seq_examen'),
date_exam date not null,
label varchar not null
);



CREATE TABLE t_reponse (
pkid_reponse INT CONSTRAINT pk_reponse PRIMARY KEY DEFAULT nextval('seq_reponse'),
fkid_question INT not null,
label varchar not null,
is_correct boolean not null,
CONSTRAINT fk_question foreign key(fkid_question) REFERENCES t_question(pkid_question)
);


CREATE TYPE user_role AS ENUM ('Administrateur', 'Student');

CREATE TABLE t_user (
pkid_user INT CONSTRAINT pk_user PRIMARY KEY,
nom varchar not null,
prenom varchar not null,
email varchar not null,
role user_role not null
);

CREATE TABLE t_account (
pkid_account INT CONSTRAINT pk_account PRIMARY KEY,
fkid_user INT not null,
mdp varchar not null,
last_connexion_date timestamp with time zone not null default now(),
CONSTRAINT fk_user foreign key(fkid_user) REFERENCES t_user(pkid_user)
);

-- default user
insert into t_user values(1, 'Sznajderman','fabrice','fabszn@protonmail.com','Administrateur');
insert into t_account values(1,1,'toto');

CREATE TABLE t_examen_reponse (
pkid_examen_reponse INT CONSTRAINT pk_examen_reponse PRIMARY KEY DEFAULT nextval('seq_user_rep'),
fkid_question INT not null,
fkid_examen Int not null,
fkid_user Int not null,
date_time timestamp not null,
CONSTRAINT fk_question foreign key(fkid_question) REFERENCES t_question(pkid_question),
CONSTRAINT fk_examen foreign key(fkid_examen) REFERENCES t_examen(pkid_examen),
CONSTRAINT fk_user foreign key(fkid_user) REFERENCES t_user(pkid_user)
);