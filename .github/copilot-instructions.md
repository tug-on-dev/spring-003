# Spring PetClinic - Copilot Instructions

## Architecture Overview

This is a Spring Boot 4.0 MVC application using Thymeleaf templates for server-side rendering. The architecture follows a layered pattern organized by domain feature.

### Package Structure
- `model/` - Base entity classes (`BaseEntity` → `NamedEntity` → `Person`)
- `owner/` - Owner, Pet, Visit entities, controllers, and repositories
- `vet/` - Vet-related entities and controllers
- `system/` - Cross-cutting concerns (caching, web config)

### Key Patterns
- **Entity Hierarchy**: All entities extend `BaseEntity` (provides `id` and `isNew()`). Domain objects like `Owner` and `Vet` extend `Person`.
- **Repository Layer**: Uses Spring Data JPA with method name query derivation (e.g., `findByLastNameStartingWith`). No custom `@Query` annotations.
- **Controller Pattern**: Use constructor injection. Load entities via `@ModelAttribute` methods. Return view names as strings, not `ModelAndView` for simple cases.

```java
// Example: Entity loading pattern from OwnerController
@ModelAttribute("owner")
public Owner findOwner(@PathVariable(name = "ownerId", required = false) Integer ownerId) {
    return ownerId == null ? new Owner() : this.owners.findById(ownerId).orElseThrow(...);
}
```

## Build & Run Commands

```bash
# Build and run (uses H2 in-memory database by default)
./mvnw spring-boot:run

# Build with SCSS compilation (required if modifying styles)
./mvnw package -P css

# Run with MySQL
docker compose up mysql
./mvnw spring-boot:run -Dspring.profiles.active=mysql

# Run with PostgreSQL
docker compose up postgres
./mvnw spring-boot:run -Dspring.profiles.active=postgres
```

## Testing Strategy

- **Unit tests**: `@WebMvcTest` for controllers with `@MockitoBean` for repositories
- **Repository tests**: `@DataJpaTest` for JPA layer testing
- **Integration tests**: `@SpringBootTest` with Testcontainers (MySQL) or Docker Compose (Postgres)
- Run `PetClinicIntegrationTests.main()` for fast feedback with H2 + DevTools

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=OwnerControllerTests
```

## Validation Patterns

- Use Jakarta Bean Validation annotations (`@NotBlank`, `@Pattern`) for simple field validation
- Use custom `Validator` implementations for complex cross-field validation (see `PetValidator`)
- Telephone format: exactly 10 digits (`@Pattern(regexp = "\\d{10}")`)

## Database Profiles

| Profile | Database | Config File |
|---------|----------|-------------|
| (default) | H2 in-memory | `application.properties` |
| `mysql` | MySQL | `application-mysql.properties` |
| `postgres` | PostgreSQL | `application-postgres.properties` |

Schema and seed data are in `src/main/resources/db/{h2,mysql,postgres}/`.

## Caching

Vets data is cached using JCache/Caffeine. Cache configuration is in `CacheConfiguration.java`. The cache name is `"vets"`.

## Code Style

This project uses Spring Java Format. Run `./mvnw spring-javaformat:apply` before committing. The build will fail if code doesn't match the format.

## Thymeleaf Templates

- Layout template: `templates/fragments/layout.html`
- Use fragment includes: `th:replace="~{fragments/layout :: layout(~{::body},'owners')}"`
- Messages/i18n: `src/main/resources/messages/messages.properties`


