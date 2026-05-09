-- CoffeeLab MySQL 8 schema
-- Database field names use snake_case. API request/response fields use camelCase.

SET NAMES utf8mb4;
SET time_zone = '+00:00';

CREATE TABLE IF NOT EXISTS users (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  nickname VARCHAR(64) NOT NULL,
  phone VARCHAR(32) NULL,
  email VARCHAR(255) NULL,
  password_hash VARCHAR(255) NOT NULL,
  avatar_url VARCHAR(512) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_phone (phone),
  UNIQUE KEY uk_users_email (email),
  KEY idx_users_created_at (created_at),
  CONSTRAINT chk_users_contact CHECK (phone IS NOT NULL OR email IS NOT NULL)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS classic_coffees (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  image_url VARCHAR(512) NULL,
  description TEXT NOT NULL,
  caffeine_level TINYINT UNSIGNED NOT NULL,
  suitable_crowd VARCHAR(255) NULL,
  tags JSON NOT NULL,
  default_recipe JSON NOT NULL,
  adjustable BOOLEAN NOT NULL DEFAULT TRUE,
  sort_order INT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_classic_coffees_sort_order (sort_order),
  CONSTRAINT chk_classic_coffees_caffeine CHECK (caffeine_level BETWEEN 0 AND 5),
  CONSTRAINT chk_classic_coffees_tags_json CHECK (JSON_VALID(tags)),
  CONSTRAINT chk_classic_coffees_default_recipe_json CHECK (JSON_VALID(default_recipe))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS recipes (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NOT NULL,
  name VARCHAR(100) NOT NULL,
  note TEXT NULL,
  cup_type VARCHAR(32) NOT NULL,
  temperature_type VARCHAR(32) NOT NULL,
  coffee_base VARCHAR(32) NOT NULL,
  espresso_shots TINYINT UNSIGNED NOT NULL DEFAULT 1,
  milk_type VARCHAR(32) NOT NULL DEFAULT 'none',
  sweetness VARCHAR(32) NOT NULL DEFAULT 'noSugar',
  ice_level VARCHAR(32) NOT NULL DEFAULT 'noIce',
  syrups JSON NOT NULL,
  foam VARCHAR(32) NOT NULL DEFAULT 'none',
  toppings JSON NOT NULL,
  flavor_tags JSON NOT NULL,
  flavor_radar JSON NOT NULL,
  is_public BOOLEAN NOT NULL DEFAULT FALSE,
  source_recipe_id BIGINT UNSIGNED NULL,
  source_public_recipe_id BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_recipes_user_created (user_id, created_at DESC),
  KEY idx_recipes_user_public (user_id, is_public),
  KEY idx_recipes_name (name),
  KEY idx_recipes_source_recipe (source_recipe_id),
  KEY idx_recipes_source_public_recipe (source_public_recipe_id),
  CONSTRAINT fk_recipes_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_recipes_source_recipe FOREIGN KEY (source_recipe_id) REFERENCES recipes (id) ON DELETE SET NULL,
  CONSTRAINT chk_recipes_cup_type CHECK (cup_type IN ('coldCup', 'hotCup')),
  CONSTRAINT chk_recipes_temperature_type CHECK (temperature_type IN ('cold', 'hot')),
  CONSTRAINT chk_recipes_coffee_base CHECK (coffee_base IN ('espresso', 'americano', 'coldBrew', 'decaf')),
  CONSTRAINT chk_recipes_espresso_shots CHECK (espresso_shots BETWEEN 1 AND 3),
  CONSTRAINT chk_recipes_milk_type CHECK (milk_type IN ('none', 'wholeMilk', 'lowFatMilk', 'oatMilk', 'coconutMilk', 'thickMilk')),
  CONSTRAINT chk_recipes_sweetness CHECK (sweetness IN ('noSugar', 'lowSugar', 'halfSugar', 'lessSugar', 'fullSugar')),
  CONSTRAINT chk_recipes_ice_level CHECK (ice_level IN ('noIce', 'lessIce', 'normalIce', 'extraIce')),
  CONSTRAINT chk_recipes_foam CHECK (foam IN ('none', 'lightFoam', 'thickFoam', 'seaSaltCream', 'coconutCloud')),
  CONSTRAINT chk_recipes_syrups_json CHECK (JSON_VALID(syrups)),
  CONSTRAINT chk_recipes_toppings_json CHECK (JSON_VALID(toppings)),
  CONSTRAINT chk_recipes_flavor_tags_json CHECK (JSON_VALID(flavor_tags)),
  CONSTRAINT chk_recipes_flavor_radar_json CHECK (JSON_VALID(flavor_radar))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS recipe_ingredients (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  recipe_id BIGINT UNSIGNED NOT NULL,
  category VARCHAR(64) NOT NULL,
  ingredient_key VARCHAR(64) NOT NULL,
  ingredient_name VARCHAR(100) NOT NULL,
  amount DECIMAL(8,2) NULL,
  unit VARCHAR(32) NULL,
  sort_order INT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_recipe_ingredients_recipe (recipe_id, sort_order),
  KEY idx_recipe_ingredients_category_key (category, ingredient_key),
  UNIQUE KEY uk_recipe_ingredients_recipe_item (recipe_id, category, ingredient_key, sort_order),
  CONSTRAINT fk_recipe_ingredients_recipe FOREIGN KEY (recipe_id) REFERENCES recipes (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS public_recipes (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  recipe_id BIGINT UNSIGNED NOT NULL,
  user_id BIGINT UNSIGNED NOT NULL,
  average_rating DECIMAL(3,2) NOT NULL DEFAULT 0.00,
  rating_count INT UNSIGNED NOT NULL DEFAULT 0,
  tried_count INT UNSIGNED NOT NULL DEFAULT 0,
  favorite_count INT UNSIGNED NOT NULL DEFAULT 0,
  fork_count INT UNSIGNED NOT NULL DEFAULT 0,
  hot_score DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  published_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_public_recipes_recipe (recipe_id),
  KEY idx_public_recipes_user_published (user_id, published_at DESC),
  KEY idx_public_recipes_latest (published_at DESC, id DESC),
  KEY idx_public_recipes_rating (average_rating DESC, rating_count DESC, id DESC),
  KEY idx_public_recipes_tried (tried_count DESC, id DESC),
  KEY idx_public_recipes_favorite (favorite_count DESC, id DESC),
  KEY idx_public_recipes_hot (hot_score DESC, average_rating DESC, id DESC),
  CONSTRAINT fk_public_recipes_recipe FOREIGN KEY (recipe_id) REFERENCES recipes (id) ON DELETE CASCADE,
  CONSTRAINT fk_public_recipes_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT chk_public_recipes_average_rating CHECK (average_rating BETWEEN 0 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS recipe_ratings (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  public_recipe_id BIGINT UNSIGNED NOT NULL,
  user_id BIGINT UNSIGNED NOT NULL,
  score TINYINT UNSIGNED NOT NULL,
  comment VARCHAR(500) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_recipe_ratings_public_user (public_recipe_id, user_id),
  KEY idx_recipe_ratings_user (user_id, created_at DESC),
  KEY idx_recipe_ratings_public_score (public_recipe_id, score),
  CONSTRAINT fk_recipe_ratings_public_recipe FOREIGN KEY (public_recipe_id) REFERENCES public_recipes (id) ON DELETE CASCADE,
  CONSTRAINT fk_recipe_ratings_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT chk_recipe_ratings_score CHECK (score BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS recipe_favorites (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  public_recipe_id BIGINT UNSIGNED NOT NULL,
  user_id BIGINT UNSIGNED NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_recipe_favorites_public_user (public_recipe_id, user_id),
  KEY idx_recipe_favorites_user (user_id, created_at DESC),
  KEY idx_recipe_favorites_public (public_recipe_id, created_at DESC),
  CONSTRAINT fk_recipe_favorites_public_recipe FOREIGN KEY (public_recipe_id) REFERENCES public_recipes (id) ON DELETE CASCADE,
  CONSTRAINT fk_recipe_favorites_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS recipe_try_records (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  public_recipe_id BIGINT UNSIGNED NOT NULL,
  user_id BIGINT UNSIGNED NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_recipe_try_records_public_user (public_recipe_id, user_id),
  KEY idx_recipe_try_records_user (user_id, created_at DESC),
  KEY idx_recipe_try_records_public (public_recipe_id, created_at DESC),
  CONSTRAINT fk_recipe_try_records_public_recipe FOREIGN KEY (public_recipe_id) REFERENCES public_recipes (id) ON DELETE CASCADE,
  CONSTRAINT fk_recipe_try_records_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS recipe_fork_records (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  public_recipe_id BIGINT UNSIGNED NOT NULL,
  user_id BIGINT UNSIGNED NOT NULL,
  source_recipe_id BIGINT UNSIGNED NOT NULL,
  forked_recipe_id BIGINT UNSIGNED NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_recipe_fork_records_public (public_recipe_id, created_at DESC),
  KEY idx_recipe_fork_records_user (user_id, created_at DESC),
  KEY idx_recipe_fork_records_forked_recipe (forked_recipe_id),
  CONSTRAINT fk_recipe_fork_records_public_recipe FOREIGN KEY (public_recipe_id) REFERENCES public_recipes (id) ON DELETE CASCADE,
  CONSTRAINT fk_recipe_fork_records_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_recipe_fork_records_source_recipe FOREIGN KEY (source_recipe_id) REFERENCES recipes (id) ON DELETE CASCADE,
  CONSTRAINT fk_recipe_fork_records_forked_recipe FOREIGN KEY (forked_recipe_id) REFERENCES recipes (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
