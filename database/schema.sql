create database vente_voiture;
\c vente_voiture;

CREATE TABLE role(
   id SERIAL,
   nom VARCHAR(50)  NOT NULL,
   reference VARCHAR(20)  NOT NULL,
   PRIMARY KEY(id),
   UNIQUE(nom),
   UNIQUE(reference)
);

CREATE TABLE categorie(
   id SERIAL,
   nom VARCHAR(250)  NOT NULL,
   PRIMARY KEY(id),
   UNIQUE(nom)
);

CREATE TABLE marque(
   id SERIAL,
   nom VARCHAR(250)  NOT NULL,
   logo VARCHAR(250) ,
   PRIMARY KEY(id),
   UNIQUE(nom)
);

CREATE TABLE couleur(
   id SERIAL,
   nom VARCHAR(250)  NOT NULL,
   hexa VARCHAR(10) ,
   PRIMARY KEY(id),
   UNIQUE(nom),
   UNIQUE(hexa)
);

CREATE TABLE energie(
   id SERIAL,
   nom VARCHAR(250)  NOT NULL,
   PRIMARY KEY(id),
   UNIQUE(nom)
);

CREATE TABLE boite_vitesse(
   id SERIAL,
   nom VARCHAR(250)  NOT NULL,
   PRIMARY KEY(id),
   UNIQUE(nom)
);

CREATE TABLE etat_voiture(
   id SERIAL,
   nom VARCHAR(250)  NOT NULL,
   valeur INTEGER NOT NULL,
   PRIMARY KEY(id),
   UNIQUE(valeur)
);

CREATE TABLE commission(
   id SERIAL,
   pourcentage NUMERIC(3,2)   NOT NULL,
   date_debut DATE NOT NULL,
   PRIMARY KEY(id)
);

CREATE TABLE suggestion_message(
   id SERIAL,
   contenu TEXT NOT NULL,
   PRIMARY KEY(id)
);

CREATE TABLE message(
   id SERIAL,
   contenu TEXT NOT NULL,
   date_envoi TIMESTAMP NOT NULL,
   type VARCHAR(50)  NOT NULL,
   PRIMARY KEY(id)
);

CREATE TABLE utilisateur_recherche(
   id SERIAL,
   mot_cle VARCHAR(255) ,
   categorie VARCHAR(255) ,
   couleur VARCHAR(255) ,
   modele VARCHAR(255) ,
   prix NUMERIC(15,2)  ,
   id_utilisateur INTEGER,
   PRIMARY KEY(id)
);

CREATE TABLE utilisateur(
   id SERIAL,
   nom VARCHAR(150)  NOT NULL,
   prenom VARCHAR(150)  NOT NULL,
   email VARCHAR(150)  NOT NULL,
   mot_de_passe VARCHAR(50)  NOT NULL,
   date_inscription TIMESTAMP NOT NULL,
   adresse VARCHAR(150)  NOT NULL,
   id_role INTEGER NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_role) REFERENCES role(id)
);

CREATE TABLE modele(
   id SERIAL,
   nom VARCHAR(250)  NOT NULL,
   nb_place INTEGER NOT NULL,
   nb_porte INTEGER NOT NULL,
   annee_sortie INTEGER NOT NULL,
   id_categorie INTEGER NOT NULL,
   id_marque INTEGER NOT NULL,
   PRIMARY KEY(id),
   UNIQUE(nom),
   FOREIGN KEY(id_categorie) REFERENCES categorie(id),
   FOREIGN KEY(id_marque) REFERENCES marque(id)
);

CREATE TABLE voiture(
   id SERIAL,
   consommation NUMERIC(4,2)   NOT NULL,
   kilometrage INTEGER NOT NULL,
   etat INTEGER NOT NULL,
   id_couleur INTEGER NOT NULL,
   id_modele INTEGER NOT NULL,
   id_boite_vitesse INTEGER NOT NULL,
   id_energie INTEGER NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_couleur) REFERENCES couleur(id),
   FOREIGN KEY(id_modele) REFERENCES modele(id),
   FOREIGN KEY(id_boite_vitesse) REFERENCES boite_vitesse(id),
   FOREIGN KEY(id_energie) REFERENCES energie(id)
);

-- 0 : cree 
-- 5: valide
-- -5: refuse
-- 10: vendu
CREATE TABLE annonce(
   id SERIAL,
   reference VARCHAR(50)  NOT NULL,
   description TEXT,
   status INTEGER NOT NULL,
   date_creation TIMESTAMP NOT NULL,
   prix NUMERIC(15,2)   NOT NULL,
   commission NUMERIC(15,2)   NOT NULL,
   nb_vue INTEGER,
   id_utilisateur INTEGER NOT NULL,
   id_voiture INTEGER NOT NULL,
   PRIMARY KEY(id),
   UNIQUE(reference),
   FOREIGN KEY(id_utilisateur) REFERENCES utilisateur(id),
   FOREIGN KEY(id_voiture) REFERENCES voiture(id)
);

CREATE TABLE historique_annonce(
   id SERIAL,
   date_maj TIMESTAMP NOT NULL,
   status INTEGER NOT NULL,
   id_annonce INTEGER NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_annonce) REFERENCES annonce(id)
);

CREATE TABLE utilisateur_notif_token(
   id SERIAL,
   token VARCHAR(255)  NOT NULL,
   id_utilisateur INTEGER NOT NULL,
   PRIMARY KEY(id),
   UNIQUE(token),
   FOREIGN KEY(id_utilisateur) REFERENCES utilisateur(id)
);

CREATE TABLE annonce_favori(
   id_utilisateur INTEGER,
   id_annonce INTEGER,
   date_ajout TIMESTAMP NOT NULL,
   PRIMARY KEY(id_utilisateur, id_annonce),
   FOREIGN KEY(id_utilisateur) REFERENCES utilisateur(id),
   FOREIGN KEY(id_annonce) REFERENCES annonce(id)
);

CREATE TABLE annonce_vendu(
   id SERIAL,
   date_vente TIMESTAMP NOT NULL,
   id_acheteur INTEGER NOT NULL,
   id_annonce INTEGER NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY(id_acheteur) REFERENCES utilisateur(id),
   FOREIGN KEY(id_annonce) REFERENCES annonce(id)
);

-- 2024-11-01 11:01
alter table utilisateur alter column date_inscription set default now();
--2024-01-13 23:58
create table vue_annonce (
   id SERIAL PRIMARY key, 
   id_utilisateur INTEGER not null references utilisateur(id),
   id_annonce INTEGER not null references annonce(id),
   date_vue TIMESTAMP default now()
);


--2024-01-13 22:19 
ALTER TABLE annonce_favori
DROP CONSTRAINT annonce_favori_pkey;
alter table annonce_favori add column id serial PRIMARY key;

--2024-01-13 22:07
ALTER TABLE commission
ALTER COLUMN date_debut TYPE TIMESTAMP;

-- 2024-01-13 18:44
alter table annonce alter column date_creation set default now();
alter table etat_voiture alter column nom drop default;

--2024-01-15 09:17
ALTER TABLE vue_annonce
ADD CONSTRAINT unique_annonce_user UNIQUE (id_annonce, id_utilisateur);

-- 2024-01-15 8:54
create table annonce_photo (
   id_annonce int not null REFERENCES annonce(id),
   photo_url VARCHAR(255) not null,
   unique(photo_url)
);

-- 2024-01-15 17:58
alter table commission alter column prourcentage type numeric;
alter table commission alter column date_debut default now();

-- 2024-01-18 18:18
drop table annonce_vendu
