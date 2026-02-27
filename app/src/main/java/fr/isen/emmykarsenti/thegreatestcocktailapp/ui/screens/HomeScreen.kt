package fr.isen.emmykarsenti.thegreatestcocktailapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import fr.isen.emmykarsenti.thegreatestcocktailapp.data.remote.NetworkManager
import fr.isen.emmykarsenti.thegreatestcocktailapp.models.AppData
import fr.isen.emmykarsenti.thegreatestcocktailapp.R
// IMPORT IMPORTANT
// On dit au fichier d'aller chercher la fonction MoodDialog dans le package 'components' !
import fr.isen.emmykarsenti.thegreatestcocktailapp.ui.components.MoodDialog

@Composable
// Écran d'accueil principal de l'application
fun HomeScreen(
    modifier: Modifier = Modifier,
    onCocktailClick: (String) -> Unit // Action déclenchée lors d'un clic sur un cocktail (navigue vers l'écran de détail)
) {
    // --- ÉTATS LOCAUX (STATE) ---
    // On initialise la suggestion avec la donnée stockée dans AppData pour éviter de recharger l'API à chaque passage sur la page
    var suggestionDuJour by remember { mutableStateOf(AppData.suggestionDuJour) }
    // Vrai si aucune suggestion n'est encore chargée en mémoire (sert à afficher le cercle de chargement)
    var isLoading by remember { mutableStateOf(AppData.suggestionDuJour == null) }

    // État pour afficher le popup du barman.
    // On vérifie d'abord dans la mémoire globale (AppData). Si on l'a déjà vu pendant cette session, la valeur sera fausse.
    var showMoodPopup by remember { mutableStateOf(!AppData.hasMoodPopupBeenShown) }

    // --- CHARGEMENT INITIAL DES DONNÉES ---
    // LaunchedEffect(Unit) lance une coroutine asynchrone uniquement au premier affichage de l'écran
    LaunchedEffect(Unit) {
        // Si on n'a pas encore de suggestion du jour en mémoire...
        if (AppData.suggestionDuJour == null) {
            try {
                // On appelle l'API pour récupérer un cocktail aléatoire
                val responseSuggestion = NetworkManager.api.getRandomCocktail()
                // Si la liste des drinks n'est pas vide, on prend le premier élément et on le stocke dans le singleton AppData
                responseSuggestion.drinks?.firstOrNull().also { AppData.suggestionDuJour = it }
                // On met à jour l'état local pour rafraîchir l'interface immédiatement
                suggestionDuJour = AppData.suggestionDuJour
            } catch (e: Exception) {
                // Gestion basique des erreurs (ex: pas de connexion internet, serveur HS)
                e.printStackTrace()
            } finally {
                isLoading = false // Le chargement est terminé, qu'il y ait eu un succès ou une erreur
            }
        } else {
            // Si la suggestion était déjà en mémoire, pas besoin de charger, on enlève juste l'écran d'attente
            isLoading = false
        }
    }

    // --- STRUCTURE DE L'ÉCRAN ---
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background // Utilise la couleur de fond définie dans ton Theme.kt
    ) { paddingValues ->
        // Si les données sont en cours de chargement, on affiche un cercle de progression au centre de l'écran
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator() //
            }
        } else {
            // Contenu principal de l'écran avec possibilité de défiler verticalement
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // Active le défilement
                    .padding(16.dp)
            ) {
                // --- EN-TÊTE CENTRÉ ---
                // Affiche le logo et le nom de l'application
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp, top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_app), // Charge l'image depuis les ressources locales (res/drawable)
                        contentDescription = "App Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(150.dp).clip(CircleShape) // Découpe l'image carrée en un cercle parfait
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "The Greatest CocktailApp",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // --- SECTION : SUGGESTION DU JOUR (Daily Suggestion) ---
                Text("Daily Suggestion", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))

                // Si la suggestion a bien été chargée depuis l'API, on l'affiche sous forme de grande carte
                suggestionDuJour?.let { cocktail ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clickable { cocktail.idDrink?.let { onCocktailClick(it) } }, // Rend la carte cliquable : redirige vers les détails avec l'ID du cocktail
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Box {
                            // Chargement asynchrone de l'image via la bibliothèque externe Coil
                            AsyncImage(
                                model = cocktail.strDrinkThumb, // L'URL de l'image provenant de l'API
                                contentDescription = cocktail.strDrink,
                                contentScale = ContentScale.Crop, // L'image remplit tout l'espace de la carte (peut couper un peu les bords pour s'adapter)
                                modifier = Modifier.fillMaxSize()
                            )
                            // Bandeau semi-transparent placé en bas de la carte pour s'assurer que le texte (nom du cocktail) est toujours lisible
                            Surface(
                                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f) // 70% d'opacité
                            ) {
                                Text(cocktail.strDrink ?: "", Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- SECTION : RÉCEMMENT CONSULTÉS (Recently Viewed) ---
                Text("Recently Viewed", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))

                // Vérification de la liste d'historique stockée dans AppData
                if (AppData.recemmentConsultes.isEmpty()) {
                    // Message informatif si l'utilisateur n'a encore ouvert aucun cocktail
                    Text("No recently viewed cocktails yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    // Affichage d'un carrousel horizontal (LazyRow) pour faire défiler les cocktails récents
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(AppData.recemmentConsultes) { cocktail ->
                            // Petite carte pour chaque cocktail de l'historique
                            Card(
                                modifier = Modifier.width(130.dp).clickable { cocktail.idDrink?.let { onCocktailClick(it) } },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column {
                                    AsyncImage(
                                        model = cocktail.strDrinkThumb,
                                        contentDescription = null,
                                        modifier = Modifier.height(100.dp).fillMaxWidth(),
                                        contentScale = ContentScale.Crop
                                    )
                                    // Nom du cocktail tronqué sur une seule ligne (maxLines = 1) avec un style de texte plus petit
                                    Text(cocktail.strDrink ?: "", Modifier.padding(8.dp), maxLines = 1, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- AFFICHAGE DU POPUP (QUIZ) ---
    // Ce bloc est exécuté si la variable showMoodPopup est vraie (donc seulement si on ne l'a pas encore vu cette session)
    if (showMoodPopup) {
        // On appelle le composant MoodDialog (qui est maintenant importé depuis le fichier MoodDialog.kt externe)
        MoodDialog(
            onDismiss = {
                // Ce code s'exécute si l'utilisateur clique sur la croix "Close" du popup
                showMoodPopup = false // Fait disparaître le popup de l'écran
                AppData.hasMoodPopupBeenShown = true // Mémorise dans la session qu'il a été fermé (pour éviter qu'il ne revienne)
            },
            onCocktailSuggested = { cocktailId ->
                // Ce code s'exécute quand l'utilisateur a fini le quiz et qu'un ID (cocktailId) a été tiré au hasard
                showMoodPopup = false // Fait disparaître le popup
                AppData.hasMoodPopupBeenShown = true // Mémorise dans la session qu'un choix a été fait
                onCocktailClick(cocktailId) // Déclenche la navigation en redirigeant l'utilisateur directement vers la page détail du cocktail trouvé !
            }
        )
    }
}