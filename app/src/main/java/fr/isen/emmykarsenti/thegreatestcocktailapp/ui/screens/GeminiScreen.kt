package fr.isen.emmykarsenti.thegreatestcocktailapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.isen.emmykarsenti.thegreatestcocktailapp.models.GeminiViewModel

@Composable
fun GeminiScreen(viewModel: GeminiViewModel = viewModel()) {
    // remember { mutableStateOf("") } mémorise le texte tapé par l'utilisateur
    var ingredients by remember { mutableStateOf("") }

    // On "écoute" les données du ViewModel
    val cocktailResponse = viewModel.cocktailResult.value
    // On extrait le premier cocktail de la liste (s'il y en a un)
    val cocktail = cocktailResponse?.drinks?.firstOrNull()
    val isLoading = viewModel.isLoading.value

    // Création d'un pinceau (Brush) pour faire un dégradé de couleur sympa pour le titre
    val geminiGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF4285F4), Color(0xFF9B72CB)) // Bleu et Violet Google
    )

    // Column range les éléments de haut en bas
    Column(
        modifier = Modifier
            .fillMaxSize() // Prend tout l'écran
            .padding(16.dp), // Ajoute des marges sur les côtés
        horizontalAlignment = Alignment.CenterHorizontally // Centre les éléments horizontalement
    ) {
        // 1. Titre de la page avec notre dégradé
        Text(
            text = "Gemini Mixologist",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                brush = geminiGradient
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 2. Champ de texte où l'utilisateur tape ses ingrédients
        OutlinedTextField(
            value = ingredients,
            onValueChange = { ingredients = it }, // Met à jour la variable à chaque lettre tapée
            label = { Text("What ingredients do you have?") },
            placeholder = { Text("e.g.: Rum, mint, lime...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp) // Bords arrondis
        )

        Spacer(modifier = Modifier.height(16.dp)) // Espace vide

        // 3. Bouton pour lancer la génération
        Button(
            onClick = { viewModel.generateCocktail(ingredients) }, // Appelle la fonction du ViewModel
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            // Le bouton est désactivé si ça charge OU si le champ texte est vide
            enabled = !isLoading && ingredients.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
        ) {
            // Affichage conditionnel à l'intérieur du bouton
            if (isLoading) {
                // Roue de chargement pendant que l'IA réfléchit
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                // Texte normal
                Text("Request a unique recipe", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp)) // Espace vide

        // --- AFFICHAGE DES ERREURS (AJOUTÉ POUR SÉCURITÉ) ---
        val errorMessage = viewModel.errorMessage.value
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // 4. Zone d'affichage du résultat
        // Ce bloc n'apparaît QUE si la variable 'cocktail' n'est pas nulle (donc si l'IA a répondu)
        if (cocktail != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = cocktail.strDrink ?: "Cocktail IA", // Nom du cocktail
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4285F4)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = cocktail.strInstructions ?: "", // Instructions de la recette
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}