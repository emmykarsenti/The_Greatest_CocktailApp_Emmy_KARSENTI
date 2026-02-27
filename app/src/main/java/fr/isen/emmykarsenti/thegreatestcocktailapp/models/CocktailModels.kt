package fr.isen.emmykarsenti.thegreatestcocktailapp.models

import java.io.Serializable

// Modèle de données (Data classes) utilisés pour mapper/parser les réponses JSON de l'API TheCocktailDB.
// L'API renvoie souvent une enveloppe contenant un tableau nommé "drinks".

// Modèle pour recevoir une liste de boissons (que ce soit pour une catégorie ou un cocktail par ID)
data class CocktailResponse(
    val drinks: List<Cocktail>? // L'API renvoie un objet JSON avec une clé "drinks" contenant un tableau
) : Serializable // Permet de faire transiter l'objet via des Intents/NavArgs si besoin

// Modèle détaillé d'un cocktail. Les noms des variables doivent correspondre aux clés JSON de l'API.
data class Cocktail(
    val idDrink: String?,         // Identifiant unique du cocktail
    val strDrink: String?,        // Nom du cocktail
    val strDrinkThumb: String?,   // Lien (URL) de l'image d'illustration
    val strCategory: String?,     // Catégorie (ex: Ordinary Drink)
    val strAlcoholic: String?,    // Précise si c'est alcoolisé ou non
    val strGlass: String?,        // Type de verre recommandé
    val strInstructions: String?, // Instructions de préparation

    // Ingrédients. L'API (version gratuite) renvoie ces données "à plat" (jusqu'à 15).
    // On en prend quelques-uns pour l'exemple.
    val strIngredient1: String?,
    val strIngredient2: String?,
    val strIngredient3: String?,

    // Mesures associées aux ingrédients (ex: "1 oz", "2 parts").
    val strMeasure1: String?,
    val strMeasure2: String?,
    val strMeasure3: String?
) : Serializable

// Modèle enveloppe pour récupérer spécifiquement la liste déroulante des catégories.
// L'API renvoie "drinks": [{"strCategory": "Ordinary Drink"}, {"strCategory": "Cocktail"}...]
data class CategoryResponse(
    val drinks: List<CategoryItem>?
) : Serializable

// Représente un élément unique de la liste des catégories de l'API.
data class CategoryItem(
    val strCategory: String? // Nom de la catégorie
) : Serializable