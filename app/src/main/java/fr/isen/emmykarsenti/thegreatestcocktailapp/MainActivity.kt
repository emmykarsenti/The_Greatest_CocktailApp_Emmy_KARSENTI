package fr.isen.emmykarsenti.thegreatestcocktailapp

// Importations des bibliothèques nécessaires au fonctionnement d'Android et de Jetpack Compose
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Column

// Importations AdMob
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

// Importation de tous vos écrans (Screens) et du thème de l'application
import fr.isen.emmykarsenti.thegreatestcocktailapp.ui.screens.CategoriesScreen
import fr.isen.emmykarsenti.thegreatestcocktailapp.ui.screens.CocktailsListScreen
import fr.isen.emmykarsenti.thegreatestcocktailapp.ui.screens.CreatedCocktailsListScreen
import fr.isen.emmykarsenti.thegreatestcocktailapp.ui.screens.CreationScreen
import fr.isen.emmykarsenti.thegreatestcocktailapp.ui.screens.DetailCocktailScreen
import fr.isen.emmykarsenti.thegreatestcocktailapp.ui.screens.FavoritesScreen
import fr.isen.emmykarsenti.thegreatestcocktailapp.ui.screens.HomeScreen
import fr.isen.emmykarsenti.thegreatestcocktailapp.ui.screens.SearchScreen
import fr.isen.emmykarsenti.thegreatestcocktailapp.ui.screens.GeminiScreen
import fr.isen.emmykarsenti.thegreatestcocktailapp.ui.theme.TheGreatestCocktailAppTheme

/**
 * Point d'entrée principal de l'application.
 * On utilise l'architecture "Single Activity" : MainActivity est la seule fenêtre d'Android,
 * et on change simplement le contenu à l'intérieur grâce à Jetpack Compose.
 */
class MainActivity : ComponentActivity() {

    // onCreate est la première méthode appelée quand l'application se lance
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisation d'AdMob
        MobileAds.initialize(this) {}

        // Permet à l'application de dessiner sous les barres système (barre de statut en haut, barre de navigation Android en bas)
        enableEdgeToEdge()

        // setContent fait le pont entre le système Android classique et Jetpack Compose (l'interface moderne)
        setContent {
            // Application du thème global (définit vos couleurs, typographies et formes)
            TheGreatestCocktailAppTheme {

                // Initialisation du NavController.
                // C'est le "GPS" de l'application : il gère les routes et l'historique des écrans (pour le bouton "Retour").
                val navController = rememberNavController()

                // On "écoute" la pile de navigation actuelle.
                // Dès que l'utilisateur change d'écran, cette variable se met à jour.
                val navBackStackEntry by navController.currentBackStackEntryAsState()

                // On extrait le nom de la route actuelle (ex: "home", "gemini", "favorites")
                // Cela nous permet de savoir quel bouton allumer dans la barre du bas.
                val currentRoute = navBackStackEntry?.destination?.route

                // Scaffold est le composant de base de Material Design.
                // C'est un squelette qui place automatiquement une barre en bas, en haut, et un contenu au milieu.
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,

                    // --- DÉFINITION DE LA BARRE DE NAVIGATION ET DE LA PUB ---
                    bottomBar = {
                        // On empile verticalement la publicité PUIS le menu de navigation
                        Column {
                            // 👇 L'affichage de la bannière AdMob est ici ! 👇
                            AdBanner()
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface
                            ) {
                                // 1. Bouton Accueil
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                    label = { Text("Home") },
                                    selected = currentRoute == "home", // S'allume si on est sur la route "home"
                                    onClick = { navController.navigate("home") } // Va sur "home" quand on clique
                                )

                                // 2. Onglet Gemini AI (Nouvelle fonctionnalité !)
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Face, contentDescription = "Gemini", modifier = Modifier.size(24.dp)) },
                                    label = { Text("Gemini") },
                                    selected = currentRoute == "gemini",
                                    onClick = { navController.navigate("gemini") }
                                )

                                // 3. Bouton pour afficher un cocktail aléatoire
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Refresh, contentDescription = "Random") },
                                    label = { Text("Random") },
                                    selected = currentRoute == "random",
                                    onClick = { navController.navigate("random") }
                                )

                                // 4. Bouton pour afficher la liste des catégories
                                NavigationBarItem(
                                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "List") },
                                    label = { Text("List") },
                                    selected = currentRoute == "list",
                                    onClick = { navController.navigate("list") }
                                )

                                // 5. Bouton pour afficher les cocktails favoris
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                                    label = { Text("Favorites") },
                                    selected = currentRoute == "favorites",
                                    onClick = { navController.navigate("favorites") }
                                )

                                // 6. Bouton pour accéder à l'écran de création manuelle de cocktail
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Add, contentDescription = "Create") },
                                    label = { Text("Create") },
                                    selected = currentRoute == "creation",
                                    onClick = { navController.navigate("creation") }
                                )
                            }
                        }
                    }
                ) { innerPadding -> // innerPadding est la marge générée par la barre du bas pour ne pas cacher le texte derrière

                    // --- DÉFINITION DU GRAPHE DE NAVIGATION (NavHost) ---
                    // C'est la zone centrale de l'écran qui change selon le bouton cliqué
                    NavHost(
                        navController = navController,
                        startDestination = "home", // L'écran affiché au lancement de l'application
                        modifier = Modifier.padding(innerPadding) // On applique la marge protectrice
                    ) {

                        // Définition de la route "home"
                        composable("home") {
                            HomeScreen(onCocktailClick = { cocktailId ->
                                // Navigation vers l'écran de détail en envoyant l'ID du cocktail dans l'URL
                                navController.navigate("cocktailDetail/$cocktailId")
                            })
                        }

                        // Définition de la route "gemini"
                        composable("gemini") {
                            GeminiScreen() // Affiche notre nouvel écran d'intelligence artificielle
                        }

                        // Définition de la route "random"
                        composable("random") {
                            DetailCocktailScreen() // Si aucun ID n'est passé, on affiche un cocktail aléatoire
                        }

                        // Définition de la route "list" (Liste des catégories)
                        composable("list") {
                            CategoriesScreen(
                                onCategoryClick = { category ->
                                    // URLEncoder nettoie le texte (remplace les espaces par des %20) pour ne pas casser l'URL de navigation
                                    val encodedCategory = java.net.URLEncoder.encode(category, "UTF-8")
                                    navController.navigate("cocktails/$encodedCategory")
                                },
                                onSearchClick = {
                                    // Si on clique sur la loupe, on va sur l'écran de recherche
                                    navController.navigate("search")
                                }
                            )
                        }

                        // Route DYNAMIQUE : Liste des cocktails pour une catégorie spécifique.
                        // {category} est une variable passée dans l'URL.
                        composable("cocktails/{category}") { backStackEntry ->
                            // On récupère la catégorie dans l'URL et on la décode
                            val encodedCategory = backStackEntry.arguments?.getString("category") ?: ""
                            val decodedCategory = java.net.URLDecoder.decode(encodedCategory, "UTF-8")

                            // Logique métier : Si la catégorie est "My Creations", on redirige vers l'écran local
                            if (decodedCategory == "My Creations") {
                                navController.navigate("created_cocktails_list") {
                                    // popUpTo évite d'empiler indéfiniment l'écran dans l'historique quand on fait "retour"
                                    popUpTo("list") { inclusive = false }
                                }
                            } else {
                                // Sinon, on affiche la liste classique provenant de l'API
                                CocktailsListScreen(
                                    category = decodedCategory,
                                    onCocktailClick = { cocktailId ->
                                        navController.navigate("cocktailDetail/$cocktailId")
                                    },
                                    // popBackStack simule un appui sur le bouton "Retour" du téléphone
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                        }

                        // Route DYNAMIQUE : Écran de détail d'un cocktail spécifique
                        // {cocktailId} contient l'identifiant unique du cocktail à afficher
                        composable("cocktailDetail/{cocktailId}") { backStackEntry ->
                            val cocktailId = backStackEntry.arguments?.getString("cocktailId")
                            DetailCocktailScreen(
                                cocktailId = cocktailId,
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // Route "favorites" (Cocktails sauvegardés localement)
                        composable("favorites") {
                            FavoritesScreen(onCocktailClick = { cocktailId ->
                                navController.navigate("cocktailDetail/$cocktailId")
                            })
                        }

                        // Route "search" (Barre de recherche textuelle)
                        composable("search") {
                            SearchScreen(onCocktailClick = { cocktailId ->
                                navController.navigate("cocktailDetail/$cocktailId")
                            })
                        }

                        // Route "creation" (Formulaire pour créer son propre cocktail)
                        // On passe directement le navController pour que l'écran puisse gérer sa propre redirection après validation
                        composable("creation") {
                            CreationScreen(navController = navController)
                        }

                        // Route affichant la liste des cocktails créés par l'utilisateur
                        composable("created_cocktails_list") {
                            CreatedCocktailsListScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
// --- COMPOSANT BANNIÈRE ADMOB ---
// On le place en dehors de la classe MainActivity pour qu'il soit réutilisable si besoin.
@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                // ID de test fourni par Google. À remplacer par le vôtre avant la publication Play Store !
                adUnitId = "ca-app-pub-3940256099942544/6300978111"
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}