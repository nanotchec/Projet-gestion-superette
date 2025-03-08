Ma Supérette sur le Net - Projet de Base de Données et Java

Description du Projet

Ce projet vise à informatiser la gestion d'une supérette en permettant le suivi des fournisseurs, des produits, des achats, des ventes et des stocks. L'application est développée en Java avec une base de données PostgreSQL.

Contenu du Repository

Ce repository contient :

Cahier des charges : Définition des besoins et exigences du projet.

Commandes SQL : Scripts pour la création des tables et l'initialisation de la base de données.

Modélisation de la Base de Données : Diagrammes et explication de la structure.

Code source Java : Tout le code pour l'application, incluant la gestion des fournisseurs, produits, achats, ventes et tableaux de bord.

Documentation : Instructions pour l'installation et l'utilisation de l'application.

Installation

Prérequis

Java JDK (version 8 ou supérieure)

PostgreSQL (Base de données relationnelle)

Eclipse ou tout autre IDE compatible avec Java

Pilote JDBC PostgreSQL (ajouté au Build Path du projet)

Configuration de la Base de Données

Créer la base de données :

CREATE DATABASE superette;

Se connecter à PostgreSQL et exécuter les scripts SQL fournis :

\\ Exécuter les commandes dans le fichier "creation_tables.sql"

Exécution du Projet

Cloner le repository :

git clone https://github.com/utilisateur/superette.git

Ouvrir le projet dans Eclipse et vérifier que le pilote JDBC PostgreSQL est bien ajouté au Build Path.

**Lancer la classe **Main.java pour exécuter l'application.

Fonctionnalités Implémentées

Gestion des Fournisseurs : Ajout, modification, désactivation de fournisseurs.

Gestion des Produits : Suivi des produits avec description, prix, catégorie, etc.

Gestion des Achats : Suivi des commandes et lots d'achat.

Gestion des Ventes : Enregistrement des ventes avec suivi des stocks.

Tableaux de Bord : Statistiques sur les ventes et les meilleures performances.

Sécurisation : Authentification des utilisateurs et gestion des rôles.



