package fr.isen.emmykarsenti.thegreatestcocktailapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// --- PARTIE ENTITÉ (La table) ---
// @Entity indique à Room de créer une table SQL nommée "created_cocktails".
// Cette table sert à stocker les créations personnelles de l'utilisateur.
@Entity(tableName = "created_cocktails")
data class CreatedCocktailEntity(
    // @PrimaryKey : C'est l'identifiant unique.
    // autoGenerate = true : Room va gérer lui-même les numéros (1, puis 2, puis 3...) automatiquement.
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val ingredients: String,
    val instructions: String,
    // Ce champ stockera le chemin (URI) vers l'image choisie par l'utilisateur dans sa galerie.
    // Il peut être null si l'utilisateur n'a pas mis de photo.
    val imageUri: String? = null
)

// --- PARTIE DAO (Les requêtes) ---
// Le DAO spécifique pour interagir avec la table "created_cocktails"
@Dao
interface CreatedCocktailDao {

    // Insère une nouvelle création dans la table.
    // CORRECTION ICI : On ajoute ": Long" comme type de retour pour récupérer l'ID généré
    // et surtout pour éviter le bug de compilation (signature V).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cocktail: CreatedCocktailEntity): Long

    // Récupérer toutes les créations sous forme de flux (Flow) pour la mise à jour en temps réel de la liste.
    // ORDER BY id DESC : Trie les résultats par ID décroissant, pour afficher la création la plus récente tout en haut.
    @Query("SELECT * FROM created_cocktails ORDER BY id DESC")
    fun getAllCreatedCocktails(): Flow<List<CreatedCocktailEntity>>
}