# CoffeeLab Database

This directory contains the MySQL 8 database schema and demo seed data for CoffeeLab.

## Files

- `schema.sql`: creates all application tables, constraints, and indexes.
- `seed.sql`: inserts demo users, classic coffees, DIY recipes, public recipes, ratings, favorites, tries, and fork records.

## Initialization Order

Run the files in this order:

```sql
SOURCE database/schema.sql;
SOURCE database/seed.sql;
```

If you use the MySQL CLI from the project root:

```bash
mysql -u <user> -p <database_name> < database/schema.sql
mysql -u <user> -p <database_name> < database/seed.sql
```

## Naming

API fields follow `camelCase`, while database columns use `snake_case`.

| API field | Database column |
| --- | --- |
| `passwordHash` | `password_hash` |
| `avatarUrl` | `avatar_url` |
| `userId` | `user_id` |
| `cupType` | `cup_type` |
| `temperatureType` | `temperature_type` |
| `coffeeBase` | `coffee_base` |
| `espressoShots` | `espresso_shots` |
| `milkType` | `milk_type` |
| `iceLevel` | `ice_level` |
| `flavorTags` | `flavor_tags` |
| `flavorRadar` | `flavor_radar` |
| `isPublic` | `is_public` |
| `publicRecipeId` | `public_recipe_id` |
| `averageRating` | `average_rating` |
| `ratingCount` | `rating_count` |
| `triedCount` | `tried_count` |
| `favoriteCount` | `favorite_count` |
| `forkCount` | `fork_count` |
| `hotScore` | `hot_score` |

## Table Relationships

- `users` owns `recipes`, `public_recipes`, `recipe_ratings`, `recipe_favorites`, `recipe_try_records`, and `recipe_fork_records`.
- `classic_coffees` stores fixed classic drink cards and JSON recipe templates for `GET /api/classic-coffees/{id}/recipe-template`.
- `recipes` stores user DIY recipes. JSON columns store flexible UI payloads: `syrups`, `toppings`, `flavor_tags`, and `flavor_radar`.
- `recipe_ingredients` stores normalized ingredient details for future analytics and ingredient management. It is intentionally kept alongside the JSON recipe fields.
- `public_recipes` publishes one user recipe to the share square. It has a unique `recipe_id`, cached counters, and `hot_score` for ranking.
- `recipe_ratings` stores one rating per `(public_recipe_id, user_id)`.
- `recipe_favorites` stores one favorite per `(public_recipe_id, user_id)`.
- `recipe_try_records` stores one try record per `(public_recipe_id, user_id)` in v1 to avoid inflated try counts.
- `recipe_fork_records` records public recipe forks and links the source public recipe, source recipe, and newly created personal recipe.

## Index Design

- `users.phone` and `users.email` are unique for registration/login.
- `recipe_ratings(public_recipe_id, user_id)` is unique so re-rating updates the existing rating.
- `recipe_favorites(public_recipe_id, user_id)` is unique to prevent duplicate favorites.
- `recipe_try_records(public_recipe_id, user_id)` is unique in v1 to prevent repeated try inflation.
- `public_recipes` includes indexes for list sorting: latest, rating, tried, favorite, and hot.
- `recipes(user_id, created_at)` supports "my recipes" pagination.

## Hot Score

`public_recipes.hot_score` is a cached value used by Top20 ranking:

```text
hotScore = averageRating * 40 + ratingCount * 2 + triedCount * 1.5 + favoriteCount * 2 + forkCount * 2
```

The backend can update this value whenever ratings, tries, favorites, or forks change. It can also recalculate it with:

```sql
UPDATE public_recipes
SET hot_score = average_rating * 40
              + rating_count * 2
              + tried_count * 1.5
              + favorite_count * 2
              + fork_count * 2;
```

Top20 query:

```sql
SELECT *
FROM public_recipes
ORDER BY hot_score DESC, average_rating DESC, id DESC
LIMIT 20;
```
