package fr.isen.emmykarsenti.thegreatestcocktailapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import fr.isen.emmykarsenti.thegreatestcocktailapp.data.FavoriteManager
import fr.isen.emmykarsenti.thegreatestcocktailapp.data.remote.NetworkManager
import fr.isen.emmykarsenti.thegreatestcocktailapp.models.AppData
import fr.isen.emmykarsenti.thegreatestcocktailapp.models.Cocktail
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Écran affichant toutes les informations détaillées d'un cocktail (image, ingrédients, instructions)
// Sert aussi bien pour afficher un cocktail spécifique (ID fourni) qu'un cocktail aléatoire (ID null)
fun DetailCocktailScreen(
    modifier: Modifier = Modifier,
    cocktailId: String? = null, // L'ID passé depuis la navigation. Si null, on charge un cocktail random.
    onBackClick: (() -> Unit)? = null // Callback pour gérer la flèche de retour (absente si appel depuis l'onglet "Random")
) {
    val context = LocalContext.current // Nécessaire pour accéder à la BDD Room
    // États pour gérer les données et l'UI
    var cocktail by remember { mutableStateOf<Cocktail?>(null) } // Le cocktail à afficher
    var isLoading by remember { mutableStateOf(true) } // Indicateur de chargement API
    var errorMessage by remember { mutableStateOf<String?>(null) } // Message en cas de problème réseau
    var isFavorite by remember { mutableStateOf(false) } // État local du bouton cœur (favori)

    // Logique de chargement des données. Se relance uniquement si l'ID (cocktailId) change.
    LaunchedEffect(cocktailId) {
        try {
            // Choix de la requête API en fonction de la présence ou non de l'ID
            val response = if (cocktailId != null) {
                NetworkManager.api.getCocktailById(cocktailId) // Cocktail spécifique
            } else {
                NetworkManager.api.getRandomCocktail() // Cocktail aléatoire (Onglet Random)
            }

            // Vérification que la réponse n'est pas vide
            if (!response.drinks.isNullOrEmpty()) {
                cocktail = response.drinks[0] // Récupération du premier objet de la liste (qui est unique)

                // Ajout à l'historique des récents (utilisé sur la page d'accueil)
                cocktail?.let { AppData.ajouterAuxRecents(it) }

                // Vérification synchrone dans la base de données Room si le cocktail est DÉJÀ en favori
                // Cela permet d'afficher le cœur plein ou vide au moment où la page s'affiche.
                val currentId = cocktail?.idDrink
                if (currentId != null) {
                    isFavorite = FavoriteManager.isFavorite(context, currentId)
                }
            } else {
                errorMessage = "No cocktail found."
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "Connection error" // Capture d'erreur réseau (ex: pas d'internet)
            e.printStackTrace()
        } finally {
            isLoading = false // Arrête l'animation de chargement
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        // Barre supérieure
        topBar = {
            TopAppBar(
                title = {
                    // Affiche le titre de l'application uniquement dans l'onglet Random (quand il n'y a pas de flèche de retour)
                    if (cocktailId == null) {
                        Text(
                            "The Greatest CocktailApp",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                // Icône de navigation à gauche (Flèche retour)
                navigationIcon = {
                    if (onBackClick != null) { // N'affiche la flèche que si le callback est fourni
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retour",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                // Actions à droite (Bouton Favoris)
                actions = {
                    // On n'affiche le cœur que si le cocktail a bien été chargé
                    if (cocktail != null) {
                        IconButton(onClick = {
                            val currentCocktail = cocktail
                            val id = currentCocktail?.idDrink

                            if (currentCocktail != null && id != null) {
                                // Logique de bascule (Toggle) : Ajout ou suppression en BDD
                                if (isFavorite) {
                                    FavoriteManager.removeFavorite(context, id) // Supprime de Room
                                    isFavorite = false // Met à jour l'UI instantanément (cœur vide)
                                } else {
                                    FavoriteManager.addFavorite(context, currentCocktail) // Ajoute dans Room
                                    isFavorite = true // Met à jour l'UI instantanément (cœur plein)
                                }
                            }
                        }) {
                            // Sélectionne l'icône selon l'état actuel de isFavorite
                            val icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                            Icon(icon, contentDescription = "Favoris", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        // Contenu principal de l'écran
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            // 1. État de chargement
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            // 2. État d'erreur
            else if (errorMessage != null) {
                Text(
                    text = "Error :\n$errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            }
            // 3. État succès (Affichage du cocktail)
            else if (cocktail != null) {
                val currentCocktail = cocktail!!

                // Colonne permettant le défilement (scroll) de la page si le contenu dépasse de l'écran
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // IMAGE PRINCIPALE
                    AsyncImage(
                        model = currentCocktail.strDrinkThumb,
                        contentDescription = currentCocktail.strDrink,
                        contentScale = ContentScale.Crop, // Crop pour remplir le rectangle sans déformer
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)) // Arrondit uniquement le bas de l'image
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // NOM DU COCKTAIL
                    Text(
                        text = currentCocktail.strDrink ?: "Unknown name",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ÉTIQUETTES (Tags : Catégorie, Alcoolisé, Verre)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        currentCocktail.strCategory?.let { SuggestionChip(onClick = { }, label = { Text(it) }) }
                        currentCocktail.strAlcoholic?.let { SuggestionChip(onClick = { }, label = { Text(it) }) }
                        currentCocktail.strGlass?.let { SuggestionChip(onClick = { }, label = { Text(it) }) }
                    }

                    // CARTE DES INGRÉDIENTS
                    // L'API sépare les ingrédients (strIngredient1, 2...) et les mesures (strMeasure1, 2...).
                    // Il faut les recomposer à la main. Ici, seulement 3 sont gérés pour l'exemple.
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Ingredients", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            // Vérification pour ne pas afficher de lignes vides si l'ingrédient est null ou vide
                            if (!currentCocktail.strIngredient1.isNullOrBlank()) Text("• ${currentCocktail.strMeasure1 ?: ""} ${currentCocktail.strIngredient1}", color = MaterialTheme.colorScheme.onSurface)
                            if (!currentCocktail.strIngredient2.isNullOrBlank()) Text("• ${currentCocktail.strMeasure2 ?: ""} ${currentCocktail.strIngredient2}", color = MaterialTheme.colorScheme.onSurface)
                            if (!currentCocktail.strIngredient3.isNullOrBlank()) Text("• ${currentCocktail.strMeasure3 ?: ""} ${currentCocktail.strIngredient3}", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // CARTE DES INSTRUCTIONS (Recette)
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 32.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Instructions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Traitement du texte des instructions fourni par l'API
                            val fullInstructions = currentCocktail.strInstructions ?: ""

                            // Découpage du texte en étapes.
                            // Le Regex "(?<=\\.)\\s+|\\r?\\n" sépare le texte après chaque point (suivi d'un espace) ou chaque retour à la ligne.
                            val steps = fullInstructions.split(Regex("(?<=\\.)\\s+|\\r?\\n")).filter { it.isNotBlank() }

                            // Affichage de chaque étape sous forme de liste numérotée
                            steps.forEachIndexed { index, step ->
                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                    // Numéro de l'étape
                                    Text(
                                        text = "${index + 1}.",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.width(25.dp) // Largeur fixe pour aligner correctement le texte
                                    )
                                    // Texte de l'étape
                                    Text(
                                        text = step.trim(),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f) // Prend le reste de l'espace disponible
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}