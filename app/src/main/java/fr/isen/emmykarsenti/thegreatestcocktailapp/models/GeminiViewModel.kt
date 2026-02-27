package fr.isen.emmykarsenti.thegreatestcocktailapp.models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.isen.emmykarsenti.thegreatestcocktailapp.data.remote.GeminiService
import kotlinx.coroutines.launch

// ViewModel : Conserve les données en mémoire même si l'écran pivote ou change de configuration
class GeminiViewModel : ViewModel() {
    // Instanciation de notre service qui communique avec l'API Google Gemini
    private val geminiService = GeminiService()

    // --- VARIABLES D'ÉTAT (State) ---
    // mutableStateOf permet à l'interface (Compose) de "surveiller" ces variables.
    // Si leur valeur change, l'écran se mettra à jour automatiquement (Recomposition).

    // Stocke la réponse complète de l'IA (qui contient la liste de cocktails)
    var cocktailResult = mutableStateOf<CocktailResponse?>(null)

    // Indique si une requête est en cours (pour afficher la roue de chargement)
    var isLoading = mutableStateOf(false)

    // Stocke un message d'erreur si la requête échoue (ex: pas d'internet)
    var errorMessage = mutableStateOf<String?>(null)

    // --- FONCTION PRINCIPALE ---
    // Appelé quand l'utilisateur clique sur le bouton "Générer"
    fun generateCocktail(ingredients: String) {
        // viewModelScope.launch lance une "Coroutine".
        // Cela permet de faire du travail en arrière-plan sans bloquer l'application.
        viewModelScope.launch {
            isLoading.value = true // Début du chargement
            errorMessage.value = null // On réinitialise l'erreur précédente s'il y en avait une

            try {
                // On met l'application en pause *ici* en attendant la réponse de Gemini
                val result = geminiService.generateCocktailFromIngredients(ingredients)
                // On gère le cas où le service renvoie null ---
                if (result == null || result.drinks.isNullOrEmpty()) {
                    errorMessage.value = "Error: The AI could not generate the recipe. Check your API key and connection."
                } else {
                    cocktailResult.value = result
                }
            } catch (e: Exception) {
                // Si quelque chose plante (ex: JSON mal formé, pas de réseau), on capture l'erreur
                errorMessage.value = "Erreur : ${e.message}"
            } finally {
                // Le bloc 'finally' s'exécute TOUJOURS, qu'il y ait eu une erreur ou un succès
                isLoading.value = false // Fin du chargement
            }
        }
    }
}