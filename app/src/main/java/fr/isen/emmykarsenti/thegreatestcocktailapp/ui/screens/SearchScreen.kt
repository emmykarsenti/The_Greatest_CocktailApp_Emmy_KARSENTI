package fr.isen.emmykarsenti.thegreatestcocktailapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List // CHANGED: Standard icon instead of FilterList
import androidx.compose.material.icons.filled.Search
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
// Écran permettant de rechercher des cocktails par nom et/ou de les filtrer par catégorie
fun SearchScreen(modifier: Modifier = Modifier, onCocktailClick: (String) -> Unit) {
    // État de la barre de recherche textuelle
    var searchQuery by remember { mutableStateOf("") }

    // États liés aux filtres (Bottom Sheet)
    var selectedCategory by remember { mutableStateOf<String?>(null) } // Catégorie actuellement appliquée
    var tempSelectedCategory by remember { mutableStateOf<String?>(null) } // Catégorie sélectionnée temporairement dans le menu de filtre avant validation
    var showFilterSheet by remember { mutableStateOf(false) } // Contrôle l'affichage du menu déroulant (Bottom Sheet)

    // États pour les données récupérées depuis l'API
    var categories by remember { mutableStateOf<List<String>>(emptyList()) } // Liste de toutes les catégories possibles
    var searchResults by remember { mutableStateOf<List<Cocktail>>(emptyList()) } // Résultats à afficher à l'écran
    var isSearching by remember { mutableStateOf(false) } // Indicateur de chargement

    // 1. Fetch categories on load
    // Cet effet se lance une seule fois à l'ouverture de l'écran (Unit) pour récupérer la liste des catégories pour les filtres
    LaunchedEffect(Unit) {
        try {
            val response = NetworkManager.api.getCategories()
            // On extrait uniquement le nom de la catégorie (strCategory) et on ignore les valeurs nulles
            categories = response.drinks?.mapNotNull { it.strCategory } ?: emptyList()
        } catch (_: Exception) { // CHANGED: 'e' replaced with '_'
            // Ignored : En cas d'erreur réseau, la liste restera vide sans faire crasher l'appli
        }
    }

    // 2. Handle search and filtering
    // Cet effet se déclenche à chaque fois que l'utilisateur tape une lettre (searchQuery) ou applique un filtre (selectedCategory)
    LaunchedEffect(searchQuery, selectedCategory) {
        isSearching = true // Affiche la barre de chargement
        try {
            // Cas 1 : Un filtre de catégorie est appliqué
            if (selectedCategory != null) {
                // On récupère d'abord tous les cocktails de cette catégorie
                val response = NetworkManager.api.getCocktailsByCategory(selectedCategory!!)
                val categoryResults = response.drinks ?: emptyList()

                // CHANGED: Lifted assignment out of 'if'
                // Ensuite, on filtre cette liste localeement si l'utilisateur a tapé du texte
                searchResults = if (searchQuery.isNotBlank()) {
                    categoryResults.filter {
                        // ignoreCase = true permet de trouver "Mojito" même si on tape "mojito"
                        it.strDrink?.contains(searchQuery, ignoreCase = true) == true
                    }
                } else {
                    categoryResults // Si pas de texte, on affiche toute la catégorie
                }
            }
            // Cas 2 : Pas de filtre, mais l'utilisateur a tapé au moins 2 lettres
            else if (searchQuery.length >= 2) {
                // Appel à l'API de recherche globale par nom
                val response = NetworkManager.api.searchCocktails(searchQuery)
                searchResults = response.drinks ?: emptyList()
            }
            // Cas 3 : Ni filtre, ni recherche valide -> liste vide
            else {
                searchResults = emptyList()
            }
        } catch (_: Exception) { // CHANGED: 'e' replaced with '_'
            // En cas d'erreur (ex: pas de connexion), on vide les résultats
            searchResults = emptyList()
        } finally {
            isSearching = false // Cache la barre de chargement une fois terminé
        }
    }

    // Structure principale de la page
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        // Barre supérieure avec le titre
        topBar = { TopAppBar(
            title = { Text("Search", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        ) }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            // --- SEARCH BAR & FILTER BUTTON ---
            // Ligne contenant le champ texte et le bouton pour ouvrir les filtres
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Champ de saisie pour la recherche
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it }, // Met à jour l'état à chaque touche pressée
                    label = { Text("Search by name...") },
                    modifier = Modifier.weight(1f), // Prend tout l'espace disponible à gauche du bouton filtre
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }, // Icône de loupe
                    // Bouton en forme de croix pour effacer le texte rapidement
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // FILTER BUTTON
                // Bouton pour afficher la BottomSheet des catégories
                IconButton(onClick = {
                    tempSelectedCategory = selectedCategory // Initialise le choix temporaire avec le choix actuel
                    showFilterSheet = true // Déclenche l'affichage du menu
                }) {
                    Icon(
                        imageVector = Icons.Default.List, // CHANGED: Standard icon
                        contentDescription = "Filter",
                        // L'icône change de couleur si un filtre est actuellement actif
                        tint = if (selectedCategory != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Affiche une barre de progression indéterminée pendant les requêtes réseau
            if (isSearching) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
            }

            // --- SEARCH RESULTS ---
            // Grille affichant les cocktails trouvés (2 colonnes)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(searchResults) { cocktail ->
                    // Carte représentant un cocktail unique
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickable {
                                // Au clic, on appelle le callback pour naviguer vers les détails
                                cocktail.idDrink?.let { onCocktailClick(it) }
                            },
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Chargement asynchrone de l'image du cocktail
                            AsyncImage(
                                model = cocktail.strDrinkThumb,
                                contentDescription = cocktail.strDrink,
                                contentScale = ContentScale.Crop, // L'image remplit la zone
                                modifier = Modifier.fillMaxWidth().aspectRatio(1f) // L'image sera carrée
                            )
                            // Nom du cocktail
                            Text(
                                text = cocktail.strDrink ?: "Unknown",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- FILTER BOTTOM SHEET ---
        // Le menu déroulant du bas pour choisir une catégorie n'est affiché que si showFilterSheet est vrai
        if (showFilterSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false }, // Fermeture en cliquant à l'extérieur
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    // HEADER
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FILTER",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        // Bouton de fermeture manuel du menu
                        IconButton(onClick = { showFilterSheet = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close filters")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "CATEGORY", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))

                    // CATEGORIES LIST (Checkboxes)
                    // Liste déroulante des catégories récupérées depuis l'API
                    LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                        items(categories) { category ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // Permet de cocher/décocher en cliquant sur toute la ligne
                                        tempSelectedCategory = if (tempSelectedCategory == category) null else category
                                    }
                                    .padding(vertical = 12.dp)
                            ) {
                                // Checkbox visuelle
                                Checkbox(
                                    checked = tempSelectedCategory == category,
                                    onCheckedChange = {
                                        // On ne garde qu'une seule catégorie (pas de sélection multiple ici)
                                        tempSelectedCategory = if (it) category else null
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = category, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // APPLY BUTTON
                    // Bouton pour valider le filtre sélectionné et fermer le menu
                    Button(
                        onClick = {
                            selectedCategory = tempSelectedCategory // Application effective du filtre
                            showFilterSheet = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("SHOW RESULTS", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // CLEAR BUTTON
                    // Bouton pour réinitialiser les filtres et tout réafficher
                    OutlinedButton(
                        onClick = {
                            tempSelectedCategory = null
                            selectedCategory = null
                            showFilterSheet = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("CLEAR FILTERS", color = MaterialTheme.colorScheme.onSurface)
                    }

                    Spacer(modifier = Modifier.height(32.dp)) // Espace en bas pour la navigation système
                }
            }
        }
    }
}