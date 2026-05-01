# AgriConnect — Backend Services

Mono-repo Spring Boot 3 / Java 21 pour la plateforme AgriConnect.

## Services inclus dans ce livrable

| Service | Port | Base de données | Description |
|---------|------|----------------|-------------|
| `auth-service` | 8081 | PostgreSQL (agriconnect_auth) | JWT, OTP SMS, refresh tokens |
| `user-service` | 8082 | PostgreSQL (agriconnect_users) | Profils, rôles, avatar S3 |
| `labor-service` | 8084 | PostgreSQL + PostGIS (agriconnect_labor) | Offres, candidatures, contrats, missions |
| `commons` | — | — | DTOs partagés, exceptions, utilitaires |

## Prérequis

- Java 21+
- Maven 3.9+
- Docker & Docker Compose
- PostgreSQL 16 + PostGIS 3

## Démarrage rapide (développement)

```bash
# 1. Copier les variables d'environnement
cp .env.example .env
# Éditer .env avec vos vraies valeurs

# 2. Démarrer l'infrastructure (PostgreSQL, Redis, Kafka)
docker-compose up -d postgres-auth postgres-users postgres-labor redis kafka

# 3. Compiler tous les modules
mvn clean install -DskipTests

# 4. Démarrer les services en dev
cd auth-service  && mvn spring-boot:run -Dspring-boot.run.profiles=dev &
cd user-service  && mvn spring-boot:run -Dspring-boot.run.profiles=dev &
cd labor-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev &
```

## Démarrage complet (Docker)

```bash
# Construire et lancer tout
docker-compose up --build -d

# Voir les logs
docker-compose logs -f auth-service
docker-compose logs -f user-service
docker-compose logs -f labor-service
```

## Documentation API (Swagger)

Une fois les services démarrés :

- Auth Service  → http://localhost:8081/api/v1/swagger-ui.html
- User Service  → http://localhost:8082/api/v1/swagger-ui.html
- Labor Service → http://localhost:8084/api/v1/swagger-ui.html
- Kafka UI      → http://localhost:8090

## Topics Kafka produits par ce livrable

| Topic | Producteur | Consommateurs |
|-------|-----------|---------------|
| `user.registered` | auth-service | user-service |
| `labor.application.accepted` | labor-service | notification-service |
| `labor.contract.signed` | labor-service | payment-service, notification-service |
| `labor.mission.completed` | labor-service | payment-service, review-service |
| `labor.mission.disputed` | labor-service | admin-service, payment-service |

## Structure des packages

```
com.agriconnect.{service}/
├── config/          ← Spring Security, Kafka, Redis, OpenAPI, S3
├── controller/      ← REST controllers (@RestController)
├── service/         ← Interfaces + implémentations métier
├── repository/      ← JPA repositories (Spring Data)
├── domain/
│   ├── entity/      ← Entités JPA (@Entity)
│   ├── enums/       ← Enums métier
│   └── vo/          ← Value Objects (@Embeddable)
├── dto/
│   ├── request/     ← DTOs entrants avec validation @Valid
│   └── response/    ← DTOs sortants
├── mapper/          ← MapStruct mappers
├── event/
│   ├── model/       ← Payloads Kafka
│   ├── publisher/   ← KafkaTemplate producers
│   └── listener/    ← @KafkaListener consumers
├── client/          ← Feign clients (inter-services)
├── security/        ← JwtTokenProvider, JwtAuthFilter, SecurityUtils
└── exception/       ← GlobalExceptionHandler, exceptions custom
```

## Variables d'environnement clés

Voir `.env.example` pour la liste complète.

| Variable | Description |
|----------|-------------|
| `JWT_SECRET` | Clé secrète JWT (base64, 256-bit minimum) |
| `TWILIO_ENABLED` | `false` en dev (OTP affiché dans les logs) |
| `DB_HOST` | Hôte PostgreSQL |
| `KAFKA_BOOTSTRAP_SERVERS` | Adresse Kafka |

## Tests

```bash
# Tous les tests
mvn test

# Un service spécifique
cd auth-service && mvn test
```

---
*AgriConnect Backend v1.0 — Spring Boot 3.3 · Java 21 · PostGIS 3 · Kafka 3 · Redis 7*
