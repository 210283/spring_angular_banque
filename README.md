# VotreBanque

Application bancaire fullstack avec une architecture Clean Architecture / Hexagonale côté backend et une organisation feature-first côté frontend.

Le projet associe :
- un backend Java/Spring avec séparation Domain / Application / Infrastructure
- un frontend Angular avec modules par domaine fonctionnel
- une authentification JWT
- une gestion de comptes, virements, bénéficiaires, prélèvements et intérêts

## Vue d'ensemble

Cette application est conçue pour montrer un exemple de projet bancaire structuré selon les bonnes pratiques d'architecture logicielle :
- le noyau métier reste indépendant des frameworks
- la logique applicative passe par des ports explicites
- les adapters techniques sont isolés dans l'infrastructure
- le frontend garde la logique de domaine dans des modèles purs et des services d'adaptation HTTP

## Architecture backend

Le backend suit le schéma suivant :

```text
backend/
└── src/main/java/com/votrebanque/
    ├── domain/                 # Règles métier pures, modèles, exceptions, validateurs
    │   ├── model/
    │   ├── exception/
    │   ├── validator/
    │   └── service/
    │
    ├── application/            # Cas d'usage / logique applicative
    │   ├── port/
    │   ├── service/
    │   └── mapper/
    │
    └── infrastructure/         # Adaptateurs Spring, JPA, sécurité, email, scheduling
        ├── adapters/
        ├── config/
        ├── persistence/
        ├── security/
        ├── notification/
        ├── scheduling/
        └── ...
```

### Rôle de chaque couche

- Domain : entités métier, valeurs, règles et exceptions sans dépendance Spring/JPA.
- Application : use cases, orchestration, ports d'entrée et de sortie.
- Infrastructure : implémentations concrètes des ports, Spring Data, JWT, security, mail, etc.

### Analogie Laravel

Si vous venez de Laravel, on peut comparer la structure comme ceci :
- Domain = le cœur métier, proche d'un "Service / Model business" pur, sans dépendance framework
- Application = les Actions / Use Cases, comme des services d'application qui orchestrent la logique
- Infrastructure = les Repository, Providers, Notifications, Security adapters, comme le côté "Service Container / Laravel Service Provider"
- Ports = les interfaces de contrat, équivalents aux abstractions que Laravel utilise souvent via interfaces de repository ou services

L'objectif est clair : le code métier ne doit pas savoir qu'il tourne sous Spring ou JPA.

## Architecture frontend

Le frontend est ordonné par domaine fonctionnel, avec des couches distinctes :

```text
frontend/src/app/
├── app.config.ts
├── app.routes.ts
├── accounts/
│   ├── domain/
│   │   ├── entities/
│   │   ├── models/
│   │   └── ports/
│   ├── application/
│   │   └── account.use-cases.ts
│   ├── infrastructure/
│   │   ├── adapters/
│   │   └── dto/
│   ├── features/
│   └── ui/
├── auth/
│   ├── domain/
│   ├── infrastructure/
│   ├── presentation/
│   └── ...
└── shared/
```

### Rôle de chaque couche

- domain : modèles et ports métier, sans dépendance Angular HTTP directe
- application : orchestration des cas d'usage pour les composants
- infrastructure : adaptateurs HTTP, DTOs, appels API
- features / presentation : composants, pages, formulaires et écrans

### Analogie React

Pour quelqu'un venant de React, l'architecture est proche d'une séparation par features :
- domain = logique métier pure et types de données
- application = hooks / use cases / orchestrateurs de logique
- infrastructure = appels API et adaptateurs externes
- presentation = composants visuels et pages

Les services Angular et les providers jouent ici un rôle comparable à la couche d'accès aux données en React, avec un meilleur découpage par responsabilité.

## Fonctionnalités

- Authentification JWT avec rôles admin/client
- Ouverture de compte courant et comptes d'épargne
- Calcul automatisé des intérêts
- Gestion de bénéficiaires et de virements
- Historique des transactions
- Prélèvements automatiques récurrents
- Activation de compte par email
- Interface administrative de gestion des comptes

## Stack technique

- Backend : Java, Spring Boot, Spring Security, Spring Data JPA, PostgreSQL, JWT
- Frontend : Angular 22, standalone components, signals, router, HttpClient
- Email : Mailpit + SMTP local/Docker
- Conteneurisation : Docker Compose
- CI/CD : GitHub Actions

## Prérequis

- Docker
- Docker Compose
- Java 21 pour le développement local backend
- Node.js + npm pour le frontend local

## Démarrage rapide

```bash
git clone <url-du-repo>
cd github_banque
docker compose up --build
```

## Accès local

| Service | URL |
|---|---|
| Frontend | http://localhost:4200 |
| Backend API | http://localhost:8080 |
| Mailpit | http://localhost:8025 |
| PostgreSQL | localhost:5432 |

## Comptes de démonstration

Trois comptes d'exemple sont créés au premier démarrage via le script d'initialisation SQL :

| Compte | Propriétaire | Solde |
|---|---|---|
| FR761234567 | Alice | 1000.00 € |
| FR769876567 | Bob | 500.00 € |
| FR769876589 | John | 600.00 € |

> Ces comptes servent surtout pour tester les mouvements, les virements et l'historique. Pour tester le flux complet d'authentification, créez un client via l'interface.

## Parcours de test complet

1. Connectez-vous en tant qu'admin avec les identifiants de démonstration.
2. Ouvrez un compte courant.
3. Activez le compte via le lien d'activation envoyé par email ou affiché dans l'API.
4. Connectez-vous avec le compte client nouvellement activé.
5. Ouvrez un compte épargne ou un livret.
6. Ajoutez un bénéficiaire puis réalisez un virement.
7. Vérifiez l'historique des transactions et les prélèvements automatiques.

## Endpoints de démonstration

Quelques endpoints d'assistance permettent d'exécuter des traitements manuellement pendant le développement :

```bash
curl -X POST http://localhost:8080/api/dev/accrue-interest
curl -X POST http://localhost:8080/api/dev/execute-direct-debits
```

## Structure du projet

```text
github_banque/
├── backend/                # API Spring Boot en Clean Architecture
│   └── src/main/java/com/votrebanque/
│       ├── application/
│       ├── domain/
│       └── infrastructure/
├── frontend/               # Application Angular en architecture feature-based
│   └── src/app/
├── docker-compose.yml
├── README.md
└── .env                    # secrets locaux non versionnés
```

## Bonnes pratiques appliquées

- Le noyau métier ne dépend pas de Spring ou de l'infrastructure.
- Les ports décrivent les contrats des dépendances externes.
- Les adaptateurs implémentent ces ports de manière technique.
- Les composants UI ne portent pas la logique métier complète.
- Les modèles de domaine restent purs et réutilisables.
- La structure est cohérente avec le principe de séparation des responsabilités.

## Développement local

Pour lancer les projets séparément :

```bash
cd backend && ./mvnw spring-boot:run
cd frontend && npm install && npm start
```

## Arrêt

```bash
docker compose down
```

Pour repartir sur une base vide :

```bash
docker compose down -v
```
