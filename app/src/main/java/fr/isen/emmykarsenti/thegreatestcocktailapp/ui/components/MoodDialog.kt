package fr.isen.emmykarsenti.thegreatestcocktailapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

// Imports pour le réseau et les requêtes asynchrones
import fr.isen.emmykarsenti.thegreatestcocktailapp.data.remote.NetworkManager
import kotlinx.coroutines.launch

@Composable
fun MoodDialog(
    onDismiss: () -> Unit,
    onCocktailSuggested: (String) -> Unit
) {
    // --- 1. GESTION DES ÉTATS ---
    // 'step' permet de naviguer entre la question 1 et la question 2
    var step by remember { mutableStateOf(1) }
    // 'mood' stocke le choix de l'utilisateur fait à la question 1
    var mood by remember { mutableStateOf("") }

    // Coroutine pour lancer la requête API en arrière-plan (sans bloquer le téléphone)
    val coroutineScope = rememberCoroutineScope()
    // 'isFetching' active l'animation de chargement pendant la requête réseau
    var isFetching by remember { mutableStateOf(false) }

    // --- 2. INTERFACE GRAPHIQUE ---
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- EN-TÊTE : TITRE ET BOUTON FERMER ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (step == 1) "What's your mood?" else "What's your taste?",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- AFFICHAGE (CHARGEMENT OU QUESTIONS) ---
                if (isFetching) {
                    // Affichage de la roue de chargement pendant l'appel à l'API
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    Text("The bartender is mixing your drink...", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 16.dp))
                }
                else if (step == 1) {
                    // --- ÉTAPE 1 : CHOIX DE L'HUMEUR ---
                    // Enregistre l'humeur et passe à la question 2
                    Button(onClick = { mood = "party"; step = 2 }, modifier = Modifier.fillMaxWidth()) {
                        Text("Ready to Party 🥳")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { mood = "chill"; step = 2 }, modifier = Modifier.fillMaxWidth()) {
                        Text("Just Chilling 🛋️")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { mood = "tired"; step = 2 }, modifier = Modifier.fillMaxWidth()) {
                        Text("Need Energy 😴")
                    }
                }
                else if (step == 2) {
                    // --- ÉTAPE 2 : REQUÊTES API DYNAMIQUES ET FILTRAGE INTELLIGENT ---

                    // --- OPTION 1 : DOUX / SUCRÉ ---
                    Button(onClick = {
                        isFetching = true

                        coroutineScope.launch {
                            try {
                                if (mood == "tired") {
                                    // TECHNIQUE AVANCÉE : On récupère tous les cocktails sans alcool
                                    val response = NetworkManager.api.getCocktailsByAlcohol("Non_Alcoholic")

                                    // On filtre la liste localement pour ne garder que ceux qui ont un nom réconfortant (Chocolat, Frappé...)
                                    val sweetEnergyDrinks = response.drinks?.filter { drink ->
                                        val name = drink.strDrink?.lowercase() ?: ""
                                        name.contains("chocolate") || name.contains("cocoa") || name.contains("frappe") || name.contains("shake")
                                    }

                                    // On pioche au hasard dans notre liste filtrée (ou 12730 = Hot Chocolate par défaut)
                                    val randomCocktail = sweetEnergyDrinks?.randomOrNull()
                                    onCocktailSuggested(randomCocktail?.idDrink ?: "12730")

                                } else {
                                    // Pour les autres modes (Party/Chill), on utilise les catégories standards
                                    val apiCategory = if (mood == "party") "Punch / Party Drink" else "Ordinary Drink"
                                    val response = NetworkManager.api.getCocktailsByCategory(apiCategory)
                                    val randomCocktail = response.drinks?.randomOrNull()
                                    onCocktailSuggested(randomCocktail?.idDrink ?: "11000") // 11000 = Mojito
                                }
                            } catch (e: Exception) {
                                onCocktailSuggested("11000")
                            }
                        }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Something Sweet 🍓")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- OPTION 2 : FORT / INTENSE ---
                    Button(onClick = {
                        isFetching = true

                        coroutineScope.launch {
                            try {
                                if (mood == "tired") {
                                    // TECHNIQUE AVANCÉE : On récupère tous les cocktails sans alcool
                                    val response = NetworkManager.api.getCocktailsByAlcohol("Non_Alcoholic")

                                    // On filtre la liste pour ne garder QUE les vrais coups de fouet (Café, Thé, Espresso)
                                    val strongEnergyDrinks = response.drinks?.filter { drink ->
                                        val name = drink.strDrink?.lowercase() ?: ""
                                        name.contains("coffee") || name.contains("tea") || name.contains("espresso")
                                    }

                                    // On pioche au hasard dans notre sélection 100% caféine et 0% alcool ! (12770 = Iced Coffee par défaut)
                                    val randomCocktail = strongEnergyDrinks?.randomOrNull()
                                    onCocktailSuggested(randomCocktail?.idDrink ?: "12770")

                                } else {
                                    // Pour les autres modes, on cherche des alcools forts (Shots) ou des classiques
                                    val apiCategory = if (mood == "party") "Shot" else "Cocktail"
                                    val response = NetworkManager.api.getCocktailsByCategory(apiCategory)
                                    val randomCocktail = response.drinks?.randomOrNull()
                                    onCocktailSuggested(randomCocktail?.idDrink ?: "11007") // 11007 = Margarita
                                }
                            } catch (e: Exception) {
                                onCocktailSuggested("11007")
                            }
                        }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Something Strong ☕")
                    }
                }
            }
        }
    }
}