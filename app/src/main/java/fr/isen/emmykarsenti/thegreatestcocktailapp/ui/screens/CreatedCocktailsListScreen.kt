package fr.isen.emmykarsenti.thegreatestcocktailapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import fr.isen.emmykarsenti.thegreatestcocktailapp.data.local.AppDatabase
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.Star

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Écran qui affiche la liste des cocktails "fait maison" (ceux sauvegardés dans la table CreatedCocktailEntity)
fun CreatedCocktailsListScreen(
    onBackClick: () -> Unit // Callback pour le bouton retour (flèche)
) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)

    // On récupère le flux (Flow) des cocktails créés depuis la base de données Room.
    // L'utilisation de collectAsState permet à l'interface de se mettre à jour automatiquement
    // à chaque fois qu'un nouveau cocktail est ajouté en base.
    val createdCocktails by database.createdCocktailDao().getAllCreatedCocktails()
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Creations", fontWeight = FontWeight.Bold) },
                // Flèche de retour pour revenir à la liste des catégories
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            // Affichage conditionnel selon le contenu de la base
            if (createdCocktails.isEmpty()) {
                // Message central si l'utilisateur n'a rien créé
                Text(
                    text = "You haven't created any cocktails yet!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Liste défilante des créations
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(createdCocktails) { cocktail ->
                        // Carte représentant un cocktail créé
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // --- GESTION DE L'IMAGE ---
                                // Si l'utilisateur a sélectionné une image lors de la création
                                if (cocktail.imageUri != null) {
                                    AsyncImage(
                                        model = cocktail.imageUri,
                                        contentDescription = cocktail.name,
                                        contentScale = ContentScale.Crop, // Recadre au format carré
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                } else {
                                    // Si pas d'image choisie, on affiche une icône étoile par défaut dans un carré coloré
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Default Cocktail Icon",
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .padding(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // --- INFORMATIONS TEXTUELLES ---
                                Column(modifier = Modifier.weight(1f)) {
                                    // Nom du cocktail
                                    Text(
                                        text = cocktail.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    // Catégories du cocktail (affiché sous forme de texte séparé par des virgules)
                                    if (cocktail.category.isNotEmpty()) {
                                        Text(
                                            text = cocktail.category,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
}