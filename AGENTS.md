# CoffeeLab Agent Rules

## Project Shape

- `frontend/` is the Vue3 + Vite client.
- `backend/` is the Spring Boot API.
- `database/` contains MySQL 8 schema and seed scripts.
- `docs/api.md` is the source of truth for API paths, request fields, response fields, and error codes.

## Current Implementation Boundaries

- Frontend list/login/save flows prefer real API calls and fall back to mock data when the API is unavailable.
- Backend prefers MySQL/MyBatis and uses `InMemoryStore` as a fallback when database access fails.
- `PersistenceGuard` caches database failures briefly so local development does not repeatedly block on an unavailable MySQL server.
- Database scripts are ready and should be run before true MySQL end-to-end verification.

## Editing Rules

- Keep API fields in camelCase and database columns in snake_case.
- Do not commit or depend on `frontend/node_modules/`, `frontend/dist/`, or `backend/target/`.
- Preserve the ins-style visual direction in `frontend/src/styles/main.css`: cream background, magazine layout, soft gradients, film grain, stickers, polaroid cup stage, and animated coffee layers.
- Do not reintroduce generic admin-dashboard styling for customer-facing pages.
- When changing endpoints, update `docs/api.md` and the matching frontend API wrapper in `frontend/src/api/index.js`.
- When adding database tables or columns, update `database/schema.sql`, `database/seed.sql` if demo data is needed, and `database/README.md`.

## Verification

Run after frontend changes:

```powershell
cd E:\OnlyTest\CoffeeLab\frontend
npm.cmd run build
```

Run after backend changes:

```powershell
cd E:\OnlyTest\CoffeeLab\backend
mvn -DskipTests package
```
