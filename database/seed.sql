-- CoffeeLab seed data for MySQL 8
-- Run after schema.sql.

SET NAMES utf8mb4;
SET time_zone = '+00:00';

INSERT INTO users (id, nickname, phone, email, password_hash, avatar_url, created_at, updated_at) VALUES
  (1, 'Latte Researcher', '13800000001', 'latte@example.com', '{mock}123456', '/images/avatar/latte.png', '2026-05-09 10:00:00', '2026-05-09 10:00:00'),
  (2, 'Cold Brew Star', '13800000002', 'coldbrew@example.com', '{mock}123456', '/images/avatar/coldbrew.png', '2026-05-09 10:05:00', '2026-05-09 10:05:00'),
  (3, 'Caramel Lab Guest', '13800000003', 'caramel@example.com', '{mock}123456', NULL, '2026-05-09 10:10:00', '2026-05-09 10:10:00')
ON DUPLICATE KEY UPDATE
  nickname = VALUES(nickname),
  password_hash = VALUES(password_hash),
  avatar_url = VALUES(avatar_url),
  updated_at = VALUES(updated_at);

INSERT INTO classic_coffees
  (id, name, image_url, description, caffeine_level, suitable_crowd, tags, default_recipe, adjustable, sort_order, created_at, updated_at)
VALUES
  (1, 'Americano', '/images/classic/americano.png', 'Clean espresso base with water, bright and simple.', 3, 'Guests who prefer a clean coffee taste.',
   JSON_ARRAY('classic', 'clean', 'espresso'),
   JSON_OBJECT('cupType','hotCup','temperatureType','hot','coffeeBase','americano','espressoShots',2,'milkType','none','sweetness','noSugar','iceLevel','noIce','syrups',JSON_ARRAY(),'foam','none','toppings',JSON_ARRAY(),'flavorTags',JSON_ARRAY('classic','clean'),'flavorRadar',JSON_OBJECT('bitterness',4,'sweetness',0,'acidity',2,'milkiness',0,'richness',3,'freshness',2)),
   TRUE, 10, '2026-05-09 10:20:00', '2026-05-09 10:20:00'),
  (2, 'Latte', '/images/classic/latte.png', 'Espresso blended with milk for a smooth, creamy profile.', 3, 'Guests who enjoy soft milk flavor.',
   JSON_ARRAY('milky', 'smooth', 'classic'),
   JSON_OBJECT('cupType','hotCup','temperatureType','hot','coffeeBase','espresso','espressoShots',2,'milkType','wholeMilk','sweetness','noSugar','iceLevel','noIce','syrups',JSON_ARRAY(),'foam','lightFoam','toppings',JSON_ARRAY(),'flavorTags',JSON_ARRAY('milky','smooth','classic'),'flavorRadar',JSON_OBJECT('bitterness',3,'sweetness',1,'acidity',1,'milkiness',5,'richness',4,'freshness',1)),
   TRUE, 20, '2026-05-09 10:20:00', '2026-05-09 10:20:00'),
  (3, 'Cappuccino', '/images/classic/cappuccino.png', 'Espresso, milk and dense foam with a classic layered mouthfeel.', 3, 'Guests who like a stronger foam texture.',
   JSON_ARRAY('foam', 'classic', 'rich'),
   JSON_OBJECT('cupType','hotCup','temperatureType','hot','coffeeBase','espresso','espressoShots',2,'milkType','wholeMilk','sweetness','noSugar','iceLevel','noIce','syrups',JSON_ARRAY(),'foam','thickFoam','toppings',JSON_ARRAY('cocoaPowder'),'flavorTags',JSON_ARRAY('foam','classic','rich'),'flavorRadar',JSON_OBJECT('bitterness',3,'sweetness',1,'acidity',1,'milkiness',4,'richness',5,'freshness',1)),
   TRUE, 30, '2026-05-09 10:20:00', '2026-05-09 10:20:00'),
  (4, 'Mocha', '/images/classic/mocha.png', 'Espresso, milk and chocolate notes for a dessert-like cup.', 3, 'Guests who prefer chocolate sweetness.',
   JSON_ARRAY('chocolate', 'sweet', 'milky'),
   JSON_OBJECT('cupType','hotCup','temperatureType','hot','coffeeBase','espresso','espressoShots',2,'milkType','wholeMilk','sweetness','halfSugar','iceLevel','noIce','syrups',JSON_ARRAY('mocha'),'foam','lightFoam','toppings',JSON_ARRAY('cocoaPowder'),'flavorTags',JSON_ARRAY('chocolate','sweet','milky'),'flavorRadar',JSON_OBJECT('bitterness',3,'sweetness',4,'acidity',1,'milkiness',4,'richness',5,'freshness',1)),
   TRUE, 40, '2026-05-09 10:20:00', '2026-05-09 10:20:00'),
  (5, 'Caramel Macchiato', '/images/classic/caramel-macchiato.png', 'Layered milk, espresso and caramel for a warm amber finish.', 3, 'Guests who enjoy caramel aroma.',
   JSON_ARRAY('caramel', 'layered', 'sweet'),
   JSON_OBJECT('cupType','hotCup','temperatureType','hot','coffeeBase','espresso','espressoShots',2,'milkType','wholeMilk','sweetness','lessSugar','iceLevel','noIce','syrups',JSON_ARRAY('caramel','vanilla'),'foam','lightFoam','toppings',JSON_ARRAY('caramelCrunch'),'flavorTags',JSON_ARRAY('caramel','layered','sweet'),'flavorRadar',JSON_OBJECT('bitterness',3,'sweetness',4,'acidity',1,'milkiness',4,'richness',4,'freshness',1)),
   TRUE, 50, '2026-05-09 10:20:00', '2026-05-09 10:20:00'),
  (6, 'Cold Brew', '/images/classic/cold-brew.png', 'Slow extracted cold coffee, mellow and refreshing.', 4, 'Guests who like iced coffee with low acidity.',
   JSON_ARRAY('cold', 'refreshing', 'mellow'),
   JSON_OBJECT('cupType','coldCup','temperatureType','cold','coffeeBase','coldBrew','espressoShots',1,'milkType','none','sweetness','noSugar','iceLevel','normalIce','syrups',JSON_ARRAY(),'foam','none','toppings',JSON_ARRAY(),'flavorTags',JSON_ARRAY('cold','refreshing','mellow'),'flavorRadar',JSON_OBJECT('bitterness',4,'sweetness',0,'acidity',1,'milkiness',0,'richness',3,'freshness',5)),
   TRUE, 60, '2026-05-09 10:20:00', '2026-05-09 10:20:00'),
  (7, 'Flat White', '/images/classic/flat-white.png', 'Dense espresso flavor wrapped by fine steamed milk.', 4, 'Guests who want stronger coffee with milk.',
   JSON_ARRAY('rich', 'milky', 'espresso'),
   JSON_OBJECT('cupType','hotCup','temperatureType','hot','coffeeBase','espresso','espressoShots',2,'milkType','wholeMilk','sweetness','noSugar','iceLevel','noIce','syrups',JSON_ARRAY(),'foam','lightFoam','toppings',JSON_ARRAY(),'flavorTags',JSON_ARRAY('rich','milky','espresso'),'flavorRadar',JSON_OBJECT('bitterness',4,'sweetness',1,'acidity',1,'milkiness',4,'richness',5,'freshness',1)),
   TRUE, 70, '2026-05-09 10:20:00', '2026-05-09 10:20:00')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  image_url = VALUES(image_url),
  description = VALUES(description),
  caffeine_level = VALUES(caffeine_level),
  suitable_crowd = VALUES(suitable_crowd),
  tags = VALUES(tags),
  default_recipe = VALUES(default_recipe),
  adjustable = VALUES(adjustable),
  sort_order = VALUES(sort_order),
  updated_at = VALUES(updated_at);

INSERT INTO recipes
  (id, user_id, name, note, cup_type, temperature_type, coffee_base, espresso_shots, milk_type, sweetness, ice_level, syrups, foam, toppings, flavor_tags, flavor_radar, is_public, source_recipe_id, source_public_recipe_id, created_at, updated_at)
VALUES
  (1001, 1, 'Midnight Hazelnut Latte', 'Double espresso, oat milk, half sugar and clear hazelnut aroma.', 'coldCup', 'cold', 'espresso', 2, 'oatMilk', 'halfSugar', 'normalIce',
   JSON_ARRAY('hazelnut','caramel'), 'lightFoam', JSON_ARRAY('cocoaPowder'),
   JSON_ARRAY('milkyRich','hazelnut','iced'),
   JSON_OBJECT('bitterness',4,'sweetness',3,'acidity',1,'milkiness',5,'richness',4,'freshness',3),
   TRUE, NULL, NULL, '2026-05-09 11:00:00', '2026-05-09 11:00:00'),
  (1002, 2, 'Sea Salt Cold Brew Cloud', 'Cold brew with sea salt cream, refreshing but rich.', 'coldCup', 'cold', 'coldBrew', 1, 'none', 'lowSugar', 'normalIce',
   JSON_ARRAY('seaSaltCaramel'), 'seaSaltCream', JSON_ARRAY('cocoaPowder'),
   JSON_ARRAY('coldBrew','seaSalt','refreshing'),
   JSON_OBJECT('bitterness',4,'sweetness',2,'acidity',1,'milkiness',2,'richness',4,'freshness',5),
   TRUE, NULL, NULL, '2026-05-09 11:05:00', '2026-05-09 11:05:00'),
  (1003, 3, 'Coconut Decaf Afternoon', 'A gentle decaf cup with coconut milk for late afternoon.', 'hotCup', 'hot', 'decaf', 1, 'coconutMilk', 'lowSugar', 'noIce',
   JSON_ARRAY('vanilla'), 'coconutCloud', JSON_ARRAY('nutCrumbs'),
   JSON_ARRAY('decaf','coconut','soft'),
   JSON_OBJECT('bitterness',2,'sweetness',2,'acidity',1,'milkiness',4,'richness',3,'freshness',2),
   FALSE, NULL, NULL, '2026-05-09 11:10:00', '2026-05-09 11:10:00'),
  (1004, 3, 'My Hazelnut Latte Remix', 'Forked from Midnight Hazelnut Latte with thicker foam.', 'coldCup', 'cold', 'espresso', 2, 'oatMilk', 'halfSugar', 'normalIce',
   JSON_ARRAY('hazelnut','caramel'), 'thickFoam', JSON_ARRAY('cocoaPowder','nutCrumbs'),
   JSON_ARRAY('milkyRich','hazelnut','remix'),
   JSON_OBJECT('bitterness',4,'sweetness',3,'acidity',1,'milkiness',5,'richness',5,'freshness',3),
   FALSE, 1001, 2001, '2026-05-09 12:20:00', '2026-05-09 12:20:00')
ON DUPLICATE KEY UPDATE
  user_id = VALUES(user_id),
  name = VALUES(name),
  note = VALUES(note),
  cup_type = VALUES(cup_type),
  temperature_type = VALUES(temperature_type),
  coffee_base = VALUES(coffee_base),
  espresso_shots = VALUES(espresso_shots),
  milk_type = VALUES(milk_type),
  sweetness = VALUES(sweetness),
  ice_level = VALUES(ice_level),
  syrups = VALUES(syrups),
  foam = VALUES(foam),
  toppings = VALUES(toppings),
  flavor_tags = VALUES(flavor_tags),
  flavor_radar = VALUES(flavor_radar),
  is_public = VALUES(is_public),
  source_recipe_id = VALUES(source_recipe_id),
  source_public_recipe_id = VALUES(source_public_recipe_id),
  updated_at = VALUES(updated_at);

INSERT INTO recipe_ingredients (recipe_id, category, ingredient_key, ingredient_name, amount, unit, sort_order) VALUES
  (1001, 'coffeeBase', 'espresso', 'Espresso', 2, 'shot', 10),
  (1001, 'milkType', 'oatMilk', 'Oat milk', 180, 'ml', 20),
  (1001, 'syrup', 'hazelnut', 'Hazelnut syrup', 10, 'ml', 30),
  (1001, 'syrup', 'caramel', 'Caramel syrup', 5, 'ml', 40),
  (1001, 'foam', 'lightFoam', 'Light foam', NULL, NULL, 50),
  (1002, 'coffeeBase', 'coldBrew', 'Cold brew', 220, 'ml', 10),
  (1002, 'syrup', 'seaSaltCaramel', 'Sea salt caramel syrup', 8, 'ml', 20),
  (1002, 'foam', 'seaSaltCream', 'Sea salt cream', NULL, NULL, 30),
  (1003, 'coffeeBase', 'decaf', 'Decaf coffee', 1, 'shot', 10),
  (1003, 'milkType', 'coconutMilk', 'Coconut milk', 180, 'ml', 20),
  (1003, 'syrup', 'vanilla', 'Vanilla syrup', 8, 'ml', 30),
  (1004, 'coffeeBase', 'espresso', 'Espresso', 2, 'shot', 10),
  (1004, 'milkType', 'oatMilk', 'Oat milk', 180, 'ml', 20),
  (1004, 'syrup', 'hazelnut', 'Hazelnut syrup', 10, 'ml', 30),
  (1004, 'foam', 'thickFoam', 'Thick foam', NULL, NULL, 40)
ON DUPLICATE KEY UPDATE
  ingredient_name = VALUES(ingredient_name),
  amount = VALUES(amount),
  unit = VALUES(unit);

INSERT INTO public_recipes
  (id, recipe_id, user_id, average_rating, rating_count, tried_count, favorite_count, fork_count, hot_score, published_at, updated_at)
VALUES
  (2001, 1001, 1, 4.50, 2, 2, 2, 1, 193.00, '2026-05-09 11:30:00', '2026-05-09 12:30:00'),
  (2002, 1002, 2, 5.00, 1, 1, 1, 0, 205.50, '2026-05-09 11:40:00', '2026-05-09 12:30:00')
ON DUPLICATE KEY UPDATE
  average_rating = VALUES(average_rating),
  rating_count = VALUES(rating_count),
  tried_count = VALUES(tried_count),
  favorite_count = VALUES(favorite_count),
  fork_count = VALUES(fork_count),
  hot_score = VALUES(hot_score),
  updated_at = VALUES(updated_at);

INSERT INTO recipe_ratings (id, public_recipe_id, user_id, score, comment, created_at, updated_at) VALUES
  (3001, 2001, 2, 5, 'Hazelnut and oat milk work well together.', '2026-05-09 12:00:00', '2026-05-09 12:00:00'),
  (3002, 2001, 3, 4, 'Smooth and balanced, could use a little less caramel.', '2026-05-09 12:05:00', '2026-05-09 12:05:00'),
  (3003, 2002, 1, 5, 'Very refreshing with a clean cold brew base.', '2026-05-09 12:10:00', '2026-05-09 12:10:00')
ON DUPLICATE KEY UPDATE
  score = VALUES(score),
  comment = VALUES(comment),
  updated_at = VALUES(updated_at);

INSERT INTO recipe_favorites (id, public_recipe_id, user_id, created_at) VALUES
  (4001, 2001, 2, '2026-05-09 12:01:00'),
  (4002, 2001, 3, '2026-05-09 12:06:00'),
  (4003, 2002, 1, '2026-05-09 12:11:00')
ON DUPLICATE KEY UPDATE created_at = VALUES(created_at);

INSERT INTO recipe_try_records (id, public_recipe_id, user_id, created_at) VALUES
  (5001, 2001, 2, '2026-05-09 12:02:00'),
  (5002, 2001, 3, '2026-05-09 12:07:00'),
  (5003, 2002, 1, '2026-05-09 12:12:00')
ON DUPLICATE KEY UPDATE created_at = VALUES(created_at);

INSERT INTO recipe_fork_records (id, public_recipe_id, user_id, source_recipe_id, forked_recipe_id, created_at) VALUES
  (6001, 2001, 3, 1001, 1004, '2026-05-09 12:20:00')
ON DUPLICATE KEY UPDATE created_at = VALUES(created_at);
