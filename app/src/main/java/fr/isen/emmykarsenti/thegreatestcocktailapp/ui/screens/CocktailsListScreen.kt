package fr.isen.emmykarsenti.thegreatestcocktailapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import fr.isen.emmykarsenti.thegreatestcocktailapp.data.remote.NetworkManager
import fr.isen.emmykarsenti.thegreatestcocktailapp.models.Cocktail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Écran générique affichant une liste de cocktails provenant de l'API TheCocktailDB pour une catégorie donnée.
fun CocktailsListScreen(
    category: String, // Le nom de la catégorie à charger (ex: "Ordinary Drink", "Cocktail")
    modifier: Modifier = Modifier,
    onCocktailClick: (String) -> Unit, // Navigation vers le détail d'un cocktail
    // NOUVEAU PARAMÈTRE : La fonction pour revenir en arrière à l'écran des catégories
    onBackClick: () -> Unit
) {
    // États locaux
    var cocktails by remember { mutableStateOf<List<Cocktail>>(emptyList()) } // Liste des résultats de l'API
    var isLoading by remember { mutableStateOf(true) } // Indicateur de chargement

    // Déclencheur du chargement de l'API au lancement de l'écran, lié au paramètre 'category'
    LaunchedEffect(category) {
        try {
            // Appel réseau pour récupérer tous les cocktails de cette catégorie
            val response = NetworkManager.api.getCocktailsByCategory(category)
            if (response.drinks != null) {
                cocktails = response.drinks // Stockage dans l'état Compose pour affichage
            }
        } catch (e: Exception) {
            e.printStackTrace() // En cas d'erreur (pas de réseau, etc.), on loggue simplement pour l'instant
        } finally {
            isLoading = false // Le chargement est terminé, on cache le spinner
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                // Affiche le nom de la catégorie courante en titre
                title = { Text(category, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                // NAVIGATION : On ajoute la flèche retour
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = MaterialTheme.colorScheme.primary // Flèche colorée avec le thème (Or)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                // Animation de chargement centrée
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                // Affichage sous forme de grille à 2 colonnes
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(cocktails) { cocktail ->
                        // Carte individuelle pour un cocktail de la liste
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .clickable {
                                    val id = cocktail.idDrink
                                    // Au clic, on appelle le callback si on a bien un ID valide
                                    if (id != null) {
                                        onCocktailClick(id)
                                    }
                                },
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Image carrée (aspectRatio 1f) du cocktail
                                AsyncImage(
                                    model = cocktail.strDrinkThumb,
                                    contentDescription = cocktail.strDrink,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                )
                                // Nom du cocktail sous l'image
                                Text(
                                    text = cocktail.strDrink ?: "Inconnu",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(12.dp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}