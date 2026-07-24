# VotreBanque

Application bancaire fullstack : backend Spring Boot (architecture hexagonale) + frontend Angular, avec authentification JWT, activation de compte par email, et gestion de virements/bénéficiaires.

## Stack technique

- **Backend** : Spring Boot 4, Spring Security, Spring Data JPA, PostgreSQL, JWT (JJWT)
- **Frontend** : Angular 22 (standalone components, signals)
- **Email** : Mailpit (serveur SMTP de test, interface web pour visualiser les emails)
- **Conteneurisation** : Docker Compose

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

1. **Connexion admin** : sur `http://localhost:4200/login`, connectez-vous avec `admin` / `password123`
2. **Ouvrir un compte** : remplissez le formulaire — un identifiant client (11 chiffres) est généré
3. **Activation** : consultez Mailpit (`http://localhost:8025`) pour récupérer le lien d'activation envoyé au nouveau client, et suivez-le pour choisir un mot de passe
4. **Connexion client** : reconnectez-vous avec l'identifiant client et le mot de passe choisi
5. Consultez le solde, ajoutez un bénéficiaire, effectuez un virement

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
