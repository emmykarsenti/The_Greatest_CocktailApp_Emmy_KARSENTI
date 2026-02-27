package fr.isen.emmykarsenti.thegreatestcocktailapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// @Dao (Data Access Object) : C'est ici qu'on définit les requêtes SQL pour interagir avec la table.
// Room va générer automatiquement le code complexe pour exécuter ces requêtes.
@Dao
interface FavoriteDao {

    // Récupère absolument toutes les lignes de la table des favoris.
    // Le type de retour 'Flow' est magique : il crée un canal de communication ouvert.
    // Si un favori est ajouté ou supprimé ailleurs, cette requête mettra l'interface à jour automatiquement !
    @Query("SELECT * FROM favorite_cocktails")
    fun getAllFavorites(): Flow<List<CocktailEntity>>

    // Vérifie si un cocktail spécifique existe déjà dans la base.
    // Renvoie 'true' si le SELECT trouve au moins 1 résultat (le cocktail est en favori).
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_cocktails WHERE id = :cocktailId)")
    suspend fun isFavorite(cocktailId: String): Boolean

    // Ajoute un cocktail à la base de données.
    // OnConflictStrategy.REPLACE : Si on essaie d'ajouter un cocktail qui a le même ID qu'un cocktail existant,
    // Room va simplement l'écraser (mettre à jour) au lieu de planter.
    // ASTUCE : On demande à Room de renvoyer un Long (l'ID de la ligne insérée) au lieu de rien (Void/Unit).
    // Ça contourne le bug KSP "signature V" (un bug connu du compilateur Kotlin avec Room) !
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(cocktail: CocktailEntity): Long

    // Supprime un cocktail en fonction de son identifiant.
    // ASTUCE : On demande à Room de renvoyer un Int (le nombre de lignes supprimées) pour éviter le même bug.
    @Query("DELETE FROM favorite_cocktails WHERE id = :cocktailId")
    suspend fun removeFavorite(cocktailId: String): Int
}