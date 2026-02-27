package fr.isen.emmykarsenti.thegreatestcocktailapp.data

import android.content.Context
import fr.isen.emmykarsenti.thegreatestcocktailapp.data.local.AppDatabase
import fr.isen.emmykarsenti.thegreatestcocktailapp.data.local.CocktailEntity
import fr.isen.emmykarsenti.thegreatestcocktailapp.models.Cocktail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// Ce gestionnaire fait le pont entre ton interface (UI) et ta base de données locale (Room).
object FavoriteManager {

    // 1. Récupère la liste des favoris (Flow permet à ton écran de se mettre à jour tout seul !)
    // Un Flow est un flux de données continu : dès qu'un favori est ajouté ou supprimé,
    // l'interface qui "écoute" ce Flow sera rafraîchie automatiquement.
    fun getFavorites(context: Context): Flow<List<CocktailEntity>> {
        val db = AppDatabase.getDatabase(context) // Connexion à la base
        return db.favoriteDao().getAllFavorites() // Appel de la requête SQL correspondante
    }

    // 2. Ajoute un cocktail aux favoris (s'exécute en arrière-plan)
    fun addFavorite(context: Context, cocktail: Cocktail) {
        // On lance une Coroutine sur le Dispatcher.IO (optimisé pour les opérations de lecture/écriture de fichiers ou DB).
        // Cela évite de bloquer l'interface utilisateur pendant l'enregistrement.
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)

            // On transforme ton modèle d'API (Cocktail) en modèle de Base de données (CocktailEntity)
            // L'API renvoie souvent des valeurs "null", c'est pourquoi on utilise le "Elvis Operator" (?:)
            // pour fournir des valeurs par défaut si jamais la donnée est manquante.
            val entity = CocktailEntity(
                id = cocktail.idDrink ?: "id_inconnu",
                name = cocktail.strDrink ?: "Name unknown",
                imageUrl = cocktail.strDrinkThumb ?: "",
                category = cocktail.strCategory ?: "",
                instructions = cocktail.strInstructions ?: "",
                ingredients = "" // L'API structure mal les ingrédients, on les ignore ici pour simplifier
            )

            db.favoriteDao().addFavorite(entity) // Insertion dans la table
        }
    }

    // 3. Retire un cocktail des favoris (s'exécute en arrière-plan)
    fun removeFavorite(context: Context, cocktailId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            db.favoriteDao().removeFavorite(cocktailId) // Suppression par ID
        }
    }

    // 4. Vérifie si un cocktail est déjà en favori
    // Cette fonction est "suspend", elle doit donc être appelée depuis une Coroutine (comme dans le LaunchedEffect de ta page détail).
    suspend fun isFavorite(context: Context, cocktailId: String): Boolean {
        val db = AppDatabase.getDatabase(context)
        return db.favoriteDao().isFavorite(cocktailId) // Renvoie true si l'ID existe dans la table
    }
}