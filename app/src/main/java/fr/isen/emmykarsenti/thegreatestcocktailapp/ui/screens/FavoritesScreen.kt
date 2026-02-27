package fr.isen.emmykarsenti.thegreatestcocktailapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import fr.isen.emmykarsenti.thegreatestcocktailapp.data.FavoriteManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Écran affichant la liste des cocktails sauvegardés en favoris (stockés en base de données locale)
fun FavoritesScreen(
    onCocktailClick: (String) -> Unit // Action pour rediriger vers la page détail
) {
    // Récupération du contexte Android (nécessaire pour accéder à la base de données Room)
    val context = LocalContext.current

    // MAGIE ROOM : On écoute la base de données en direct !
    // Flow/StateFlow permet à Jetpack Compose d'observer les changements dans la base de données.
    // Si la liste change (ajout/suppression), l'écran se mettra à jour tout seul, sans code supplémentaire.
    // On initialise avec une liste vide (emptyList) pour éviter un crash le temps du chargement de la DB.
    val favorites by FavoriteManager.getFavorites(context).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Favorite Cocktails", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            // Affichage conditionnel selon le contenu de la base de données
            if (favorites.isEmpty()) {
                // Message centré si la liste est vide
                Text(
                    text = "You don't have any favorites yet!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Affichage de la liste des favoris sous forme de colonne défilante (LazyColumn)
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Itération sur chaque CocktailEntity récupéré de la DB Room
                    items(favorites) { cocktail ->
                        // Carte représentant un favori
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCocktailClick(cocktail.id) }, // On utilise l'ID de l'Entity Room pour la navigation
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Image miniature du cocktail
                                AsyncImage(
                                    model = cocktail.imageUrl, // Nouveau nom de la variable correspondant au modèle Room (Entity)
                                    contentDescription = cocktail.name, // Nouveau nom de la variable
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(12.dp)) // Bords légèrement arrondis
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                // Nom et catégorie (prend tout l'espace disponible au centre)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = cocktail.name, // Nouveau nom de la variable (Entity)
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    // Affiche la catégorie uniquement si elle existe
                                    if (cocktail.category.isNotEmpty()) {
                                        Text(
                                            text = cocktail.category,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                // Bouton Supprimer (Poubelle)
                                IconButton(onClick = {
                                    // Action qui va écrire dans la base de données pour supprimer cette entité.
                                    // Le 'collectAsState' plus haut détectera la suppression et rafraîchira la liste automatiquement !
                                    FavoriteManager.removeFavorite(context, cocktail.id)
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error // Couleur rouge par défaut
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