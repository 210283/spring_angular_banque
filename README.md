![CI](https://github.com/210283/spring_angular_banque/actions/workflows/ci.yml/badge.svg)
# VotreBanque
Application bancaire fullstack : backend Spring Boot (architecture hexagonale) + frontend Angular, avec authentification JWT, activation de compte par email, comptes courants et comptes épargne (Livret A, LDD) avec calcul d'intérêts, gestion de virements/bénéficiaires, historique des transactions et prélèvements automatiques récurrents.

## Démo en ligne
🔗 **Frontend** : https://votrebanque-frontend.onrender.com
🔗 **Backend (API)** : https://votrebanque-backend.onrender.com

⚠️ Hébergé sur les tiers gratuits de Render : le backend peut mettre **jusqu'à une minute** à répondre lors de la première requête après une période d'inactivité (mise en veille automatique). Patientez et réessayez si le premier chargement semble bloqué.

## Fonctionnalités
- **Authentification** : JWT, rôles admin/client, verrouillage de compte après échecs de connexion répétés
- **Ouverture de compte** : compte courant (identifiant + activation par email) ou compte épargne/Livret A/LDD (lié automatiquement au compte courant comme bénéficiaire réciproque, sans identifiants propres)
- **Intérêts** : calcul quotidien automatique (job planifié `@Scheduled`) sur les comptes épargne, avec taux différencié par type de compte
- **Virements & bénéficiaires** : ajout de bénéficiaires avec vérification du titulaire, virements entre comptes autorisés
- **Historique des transactions** : ledger complet par compte (dépôt initial, virements, intérêts, prélèvements), consultable depuis l'interface
- **Prélèvements automatiques** : mise en place de prélèvements récurrents (hebdomadaire/mensuel) vers un bénéficiaire, exécutés automatiquement chaque nuit par un job planifié, avec rattrapage automatique en cas d'indisponibilité temporaire du serveur
- **Aperçu d'email en local** : consultation directe du contenu de l'email d'activation depuis l'interface (via l'API Mailpit interrogée côté serveur), sans exposer Mailpit publiquement

## Stack technique
- **Backend** : Spring Boot 4, Spring Security, Spring Data JPA, PostgreSQL, JWT (JJWT), tâches planifiées (`@Scheduled`)
- **Frontend** : Angular 22 (standalone components, signals)
- **Email** : envoi SMTP réel via Mailpit en local/Docker Compose (pour se rapprocher du fonctionnement d'une vraie appli bancaire). Le lien d'activation est aussi renvoyé directement dans la réponse de l'API à l'ouverture de compte, donc l'application reste utilisable même sans serveur mail configuré (c'est le cas en démo publique)
- **Conteneurisation** : Docker Compose
- **CI/CD** : GitHub Actions (tests backend, build/tests frontend, validation des images Docker)
- **Déploiement** : Render (backend en Web Service Docker, frontend en Static Site, PostgreSQL managé), avec profil Spring dédié (`application-render.properties`) et configuration CORS entre les deux domaines

## Prérequis
- Docker et Docker Compose installés

## Démarrage rapide
```bash
git clone <url-du-repo>
cd github_banque
cp .env.example .env
# Éditez .env et renseignez un vrai JWT_SECRET (voir section ci-dessous)
docker compose up --build
```

## Générer un secret JWT
```bash
openssl rand -base64 32
```
Copiez la valeur générée dans `.env`, à la variable `JWT_SECRET`.

## Accès une fois les conteneurs démarrés
| Service | URL |
|---|---|
| Frontend | http://localhost:4200 |
| Backend (API) | http://localhost:8080 |
| Mailpit (emails interceptés) | http://localhost:8025 |
| PostgreSQL | localhost:5432 |

## Comptes de démonstration
Trois comptes bancaires sont créés automatiquement au premier démarrage (voir `backend/src/main/resources/import.sql`) :
| Compte | Propriétaire | Solde |
|---|---|---|
| FR761234567 | Alice | 1000.00 € |
| FR769876567 | Bob | 500.00 € |
| FR769876589 | John | 600.00 € |

⚠️ Ces comptes n'ont pas d'identifiants de connexion associés (pas de `Credentials`) — ils servent uniquement de données de test pour les virements. Pour tester le parcours complet (connexion, activation), ouvrez un nouveau compte via l'interface admin.

## Parcours de test complet
1. **Connexion admin** : sur `/login` (local ou démo en ligne), connectez-vous avec `admin` / `password123`
2. **Ouvrir un compte courant** : remplissez le formulaire — un identifiant client (11 chiffres) est généré, ainsi que le lien d'activation correspondant, affiché directement dans l'interface
3. **Activation** : suivez ce lien pour choisir un mot de passe (en local/Docker, un email est aussi réellement envoyé et consultable dans Mailpit via le bouton « View the activation email »)
4. **Connexion client** : reconnectez-vous avec l'identifiant client et le mot de passe choisi
5. **Ouvrir un compte épargne/Livret A/LDD** : depuis l'interface admin, ouvrez un nouveau compte en indiquant le numéro du compte courant à lier — aucune activation requise, il apparaît immédiatement dans « Linked savings accounts » sur le résumé du compte courant
6. Ajoutez un bénéficiaire, effectuez un virement, puis consultez l'**historique des transactions**
7. Mettez en place un **prélèvement automatique** vers un bénéficiaire (fréquence hebdomadaire/mensuelle), consultable et annulable depuis « Manage direct debits »

## Endpoints de démonstration (dev uniquement)
Ces routes déclenchent manuellement des traitements normalement exécutés par les jobs planifiés nocturnes, pour ne pas attendre 24h en test :
```bash
# Calcule et crédite les intérêts sur tous les comptes épargne
curl -X POST http://localhost:8080/api/dev/accrue-interest

# Exécute tous les prélèvements automatiques arrivés à échéance
curl -X POST http://localhost:8080/api/dev/execute-direct-debits
```

## Arrêter l'application
```bash
docker compose down
```
Pour repartir d'une base de données vierge (supprime aussi les comptes créés manuellement) :
```bash
docker compose down -v
```

## Développement local (hors Docker)
Le backend et le frontend peuvent aussi être lancés séparément en local — voir les README respectifs dans `backend/` et `frontend/` si présents, ou la configuration `application.properties` / `proxy.conf.json` de chaque projet.

## Structure du projet
```
github_banque/
├── backend/          # API Spring Boot (architecture hexagonale)
├── frontend/         # Application Angular
├── docker-compose.yml
└── .env              # Secrets locaux (non versionné)
```
