-- ============================================
-- Script d'initialisation de la base de données
-- restaurant_db
-- ============================================

-- Créer la base (à exécuter depuis psql en dehors de cette DB)
-- CREATE DATABASE restaurant_db;

-- Table ingredient
CREATE TABLE IF NOT EXISTS ingredient (
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    category VARCHAR(255),
    price    DOUBLE PRECISION NOT NULL
);

-- Table dish
CREATE TABLE IF NOT EXISTS dish (
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    selling_price DOUBLE PRECISION NOT NULL
);

-- Table de liaison dish <-> ingredient
CREATE TABLE IF NOT EXISTS dish_ingredient (
    dish_id       INTEGER REFERENCES dish(id),
    ingredient_id INTEGER REFERENCES ingredient(id),
    PRIMARY KEY (dish_id, ingredient_id)
);

-- Table stock_movement
CREATE TABLE IF NOT EXISTS stock_movement (
    id            SERIAL PRIMARY KEY,
    ingredient_id INTEGER REFERENCES ingredient(id),
    quantity      DOUBLE PRECISION NOT NULL,
    unit          VARCHAR(10) NOT NULL,  -- PCS, KG, L
    moved_at      TIMESTAMP NOT NULL
);

-- ============================================
-- Données de test
-- ============================================

INSERT INTO ingredient (name, category, price) VALUES
    ('Tomate',    'Légume', 100),
    ('Mozzarella','Fromage', 150),
    ('Basilic',   'Herbe',   80),
    ('Farine',    'Céréale', 60),
    ('Oeuf',      'Produit laitier', 120)
ON CONFLICT DO NOTHING;

INSERT INTO dish (name, selling_price) VALUES
    ('Pizza Margherita', 1500),
    ('Salade César',     1200)
ON CONFLICT DO NOTHING;

INSERT INTO dish_ingredient (dish_id, ingredient_id) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4),
    (2, 1), (2, 5)
ON CONFLICT DO NOTHING;

INSERT INTO stock_movement (ingredient_id, quantity, unit, moved_at) VALUES
    (1, 50,  'KG',  '2026-01-01 08:00:00'),
    (1, -10, 'KG',  '2026-01-15 08:00:00'),
    (2, 30,  'KG',  '2026-01-01 08:00:00'),
    (3, 100, 'PCS', '2026-01-01 08:00:00'),
    (4, 20,  'KG',  '2026-01-01 08:00:00')
ON CONFLICT DO NOTHING;
