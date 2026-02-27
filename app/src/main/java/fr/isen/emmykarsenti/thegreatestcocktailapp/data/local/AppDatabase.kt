package fr.isen.emmykarsenti.thegreatestcocktailapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// @Database configure la base de données principale de l'application.
// On indique à Room quelles sont les tables (entities) autorisées dans cette base.
// NOUVEAU : On a ajouté CreatedCocktailEntity au tableau des entités et on a passé la version de 1 à 2.
// Changer la version est obligatoire quand on modifie la structure de la base (ajout d'une table, d'une colonne...).
@Database(entities = [CocktailEntity::class, CreatedCocktailEntity::class], version = 2, exportSchema = false)
internal abstract class AppDatabase : RoomDatabase() {

    // On déclare nos DAO. Room génèrera le code de ces fonctions pour nous donner accès aux requêtes.
    abstract fun favoriteDao(): FavoriteDao
    abstract fun createdCocktailDao(): CreatedCocktailDao // NOUVEAU : Accès aux créations

    // Le 'companion object' permet de créer un "Singleton" (une seule instance de la base de données pour toute l'application).
    // Ouvrir plusieurs connexions à la base de données en même temps pourrait créer des fuites de mémoire.
    companion object {
        // @Volatile garantit que la valeur de INSTANCE est toujours à jour et visible pour tous les threads instantanément.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Fonction pour récupérer la base de données
        fun getDatabase(context: Context): AppDatabase {
            // Si INSTANCE n'est pas nulle, on la renvoie directement.
            // Sinon (grâce à l'opérateur Elvis ?:), on la crée dans un bloc 'synchronized'.
            // 'synchronized' empêche deux threads de créer la base de données en même temps par accident.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, // La classe de notre base
                    "cocktail_database" // Le nom du fichier physique qui sera créé sur le téléphone
                )
                    // En cas de changement de version (ex: passage de 1 à 2), s'il n'y a pas de script de migration complexe fourni,
                    // Room va détruire l'ancienne table et la recréer. (Attention: les anciennes données sont perdues).
                    // Très utile en phase de développement !
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()

                INSTANCE = instance // On sauvegarde l'instance pour les prochains appels
                instance // On la renvoie
            }
        }
    }
}