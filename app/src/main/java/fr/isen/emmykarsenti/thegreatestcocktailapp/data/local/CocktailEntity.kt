package fr.isen.emmykarsenti.thegreatestcocktailapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity indique à Room que cette classe représente une table dans la base de données.
// On lui donne le nom spécifique "favorite_cocktails".
@Entity(tableName = "favorite_cocktails")
data class CocktailEntity(
    // Clé primaire : Chaque ligne doit avoir un ID unique.
    // Ici, on utilise directement l'ID fourni par l'API (ex: "11007") pour éviter les doublons.
    @PrimaryKey
    val id: String,              // L'identifiant unique (très important pour Room)

    // Les colonnes de notre table
    val name: String,            // Le nom du cocktail (ex: "Frozen Mint Daiquiri")
    val imageUrl: String,        // L'URL de la photo pour pouvoir l'afficher sans refaire d'appel API
    val category: String,        // La catégorie (ex: "Cocktail", "Cocoa")
    val instructions: String,    // Les étapes de la recette
    val ingredients: String      // Les ingrédients (sauvegardés sous forme d'un seul bloc de texte pour simplifier)
)