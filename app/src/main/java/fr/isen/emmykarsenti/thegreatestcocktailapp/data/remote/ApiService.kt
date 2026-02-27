package fr.isen.emmykarsenti.thegreatestcocktailapp.data.remote

import fr.isen.emmykarsenti.thegreatestcocktailapp.models.CategoryResponse
import fr.isen.emmykarsenti.thegreatestcocktailapp.models.CocktailResponse
import retrofit2.http.GET
import retrofit2.http.Query

// Interface contenant toutes les routes ("endpoints") de notre API TheCocktailDB.
// Retrofit va générer le code complexe automatiquement en lisant ces annotations.
interface ApiService {

    // 1. Obtenir un cocktail aléatoire
    // Appelle la route : https://www.thecocktaildb.com/api/json/v1/1/random.php
    // 'suspend' signifie que la fonction s'exécute en asynchrone (ne bloque pas l'écran).
    @GET("random.php")
    suspend fun getRandomCocktail(): CocktailResponse

    // 2. Obtenir la liste de toutes les catégories
    // Appelle la route : https://www.thecocktaildb.com/api/json/v1/1/list.php?c=list
    @GET("list.php?c=list")
    suspend fun getCategories(): CategoryResponse

    // 3. Obtenir les boissons d'une catégorie spécifique
    // L'annotation @Query("c") ajoute un paramètre à l'URL.
    // Exemple : si category = "Cocktail", l'URL sera -> filter.php?c=Cocktail
    @GET("filter.php")
    suspend fun getCocktailsByCategory(@Query("c") category: String): CocktailResponse

    // 4. Obtenir les détails d'une boisson via son ID
    // Exemple : lookup.php?i=11007
    @GET("lookup.php")
    suspend fun getCocktailById(@Query("i") idDrink: String): CocktailResponse

    // 5. Chercher un cocktail par son nom complet ou une partie du nom (barre de recherche)
    // Exemple : search.php?s=margarita
    @GET("search.php")
    suspend fun searchCocktails(@Query("s") name: String): CocktailResponse
    // 6. Permet de filtrer par type d'alcool (ex: "Non_Alcoholic")
    @GET("filter.php")
    suspend fun getCocktailsByAlcohol(@Query("a") alcoholFilter: String): CocktailResponse
}