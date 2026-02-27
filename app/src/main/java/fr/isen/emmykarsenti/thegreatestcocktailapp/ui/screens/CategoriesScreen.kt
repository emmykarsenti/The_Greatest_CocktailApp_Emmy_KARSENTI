package fr.isen.emmykarsenti.thegreatestcocktailapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.isen.emmykarsenti.thegreatestcocktailapp.data.remote.NetworkManager
import fr.isen.emmykarsenti.thegreatestcocktailapp.models.CategoryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Écran affichant toutes les catégories de cocktails disponibles sous forme de grille
fun CategoriesScreen(
    modifier: Modifier = Modifier,
    onCategoryClick: (String) -> Unit, // Callback appelé lors d'un clic sur une catégorie
    onSearchClick: () -> Unit // Callback appelé lors d'un clic sur la loupe (recherche)
) {
    // États locaux
    var categories by remember { mutableStateOf<List<CategoryItem>>(emptyList()) } // Liste des catégories
    var isLoading by remember { mutableStateOf(true) } // Indicateur de chargement

    // Appelé une seule fois au lancement de l'écran (grâce à Unit)
    LaunchedEffect(Unit) {
        try {
            // Appel réseau pour récupérer la liste des catégories via TheCocktailDB API
            val response = NetworkManager.api.getCategories()
            if (response.drinks != null) {
                // NOUVEAU : On ajoute "My Creations" au début de la liste manuellement.
                // Cela permet d'avoir un raccourci vers nos propres créations directement dans la liste des catégories de l'API.
                val myCreationsItem = CategoryItem(strCategory = "My Creations")
                // On fusionne notre élément personnalisé avec la liste renvoyée par l'API
                categories = listOf(myCreationsItem) + response.drinks!!
            }
        } catch (e: Exception) {
            e.printStackTrace() // Log de l'erreur en cas de problème réseau
        } finally {
            isLoading = false // Le chargement est terminé
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Cocktail Bar", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                // Bouton d'action à droite de la barre d'outils (Icône Loupe)
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { paddingValues ->
        // Contenu principal
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                // Roue de chargement centrée
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                // Affichage des catégories sous forme de grille à 2 colonnes
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(categories) { categoryItem ->
                        // Carte individuelle pour chaque catégorie
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .height(100.dp)
                                // Au clic, on renvoie le nom de la catégorie (ex: "Ordinary Drink" ou "My Creations")
                                .clickable {
                                    categoryItem.strCategory?.let { onCategoryClick(it) }
                                },
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            // NOUVEAU : Couleur spéciale si c'est la carte "My Creations" pour la mettre en valeur
                            colors = CardDefaults.cardColors(
                                containerColor = if (categoryItem.strCategory == "My Creations")
                                    MaterialTheme.colorScheme.primaryContainer // Couleur accentuée
                                else MaterialTheme.colorScheme.surface // Couleur standard
                            )
                        ) {
                            // Centrage du texte à l'intérieur de la carte
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = categoryItem.strCategory ?: "Unknown",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(8.dp),
                                    // Le texte s'adapte à la couleur de fond de la carte
                                    color = if (categoryItem.strCategory == "My Creations")
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}