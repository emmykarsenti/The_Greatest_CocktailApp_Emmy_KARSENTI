package fr.isen.emmykarsenti.thegreatestcocktailapp.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Singleton chargé d'initialiser et de configurer Retrofit (la bibliothèque qui gère les requêtes réseau).
object NetworkManager {
    // L'adresse principale de l'API. Toutes nos requêtes partiront de cette base.
    // Attention : L'URL de base de Retrofit DOIT toujours se terminer par un slash (/).
    private const val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/"

    // Configuration de Retrofit
    // Le 'by lazy' signifie que Retrofit ne sera construit que la toute première fois qu'on appellera NetworkManager.api.
    // Cela permet d'économiser de la mémoire au lancement de l'application.
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // GsonConverterFactory permet de traduire automatiquement la réponse texte (JSON) de l'API
            // en objets Kotlin (nos data classes CocktailResponse, etc.).
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java) // On lie la configuration à notre interface contenant les routes
    }
}