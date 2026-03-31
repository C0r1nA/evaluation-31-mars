# Restaurant API - Spring Boot (sans JPA)

## Prérequis
- Java 17+
- Maven 3.6+
- PostgreSQL

## Configuration

Modifier `src/main/resources/application.properties` :
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/restaurant_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

## Initialiser la base de données

```bash
psql -U postgres -c "CREATE DATABASE restaurant_db;"
psql -U postgres -d restaurant_db -f src/main/resources/init.sql
```

## Lancer l'application

```bash
mvn spring-boot:run
```

## Endpoints disponibles

| Méthode | URL | Description |
|---------|-----|-------------|
| GET | /ingredients | Liste tous les ingrédients |
| GET | /ingredients/{id} | Récupère un ingrédient par id |
| GET | /ingredients/{id}/stock?at={datetime}&unit={unit} | Stock d'un ingrédient à une date |
| GET | /dishes | Liste tous les plats avec leurs ingrédients |
| PUT | /dishes/{id}/ingredients | Modifier les ingrédients d'un plat |
| **GET** | **/dishes/{id}/ingredients** | **Ingrédients d'un plat avec filtres** ⭐ |

## Endpoint de l'évaluation

```
GET /dishes/{id}/ingredients?ingredientName={i}&ingredientPriceAround={p}
```

- `ingredientName` *(optionnel)* : filtre par nom (insensible à la casse)
- `ingredientPriceAround` *(optionnel)* : filtre par prix ± 50

### Exemples

```bash
# Tous les ingrédients du plat 1
GET /dishes/1/ingredients

# Filtrer par nom contenant "Tom"
GET /dishes/1/ingredients?ingredientName=Tom

# Filtrer par prix autour de 120 (entre 70 et 170)
GET /dishes/1/ingredients?ingredientPriceAround=120

# Les deux filtres combinés
GET /dishes/1/ingredients?ingredientName=Tom&ingredientPriceAround=120

# Plat inexistant → 404
GET /dishes/999/ingredients
```

## Structure du projet

```
src/main/java/com/example/
├── RestaurantApiApplication.java
├── datasource/
│   └── DataSource.java
├── entity/
│   ├── Ingredient.java
│   └── Dish.java
├── repository/
│   ├── IngredientRepository.java
│   ├── StockMovementRepository.java
│   └── DishRepository.java
└── controller/
    ├── IngredientController.java
    └── DishController.java
```
