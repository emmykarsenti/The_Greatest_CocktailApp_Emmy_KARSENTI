package fr.isen.emmykarsenti.thegreatestcocktailapp.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import fr.isen.emmykarsenti.thegreatestcocktailapp.data.local.AppDatabase
import fr.isen.emmykarsenti.thegreatestcocktailapp.data.local.CocktailEntity
import fr.isen.emmykarsenti.thegreatestcocktailapp.data.local.CreatedCocktailEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Écran permettant à l'utilisateur de créer et sauvegarder son propre cocktail en base de données
fun CreationScreen(navController: NavController) {
    // Contexte Android et CoroutineScope pour lancer des tâches asynchrones (comme l'insertion en BDD)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Initialisation de la base de données Room et récupération du DAO dédié aux créations
    val database = AppDatabase.getDatabase(context)
    val createdCocktailDao = database.createdCocktailDao()

    // États locaux pour stocker les saisies de l'utilisateur dans le formulaire
    var name by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }

    // Variables spécifiques pour la gestion multiple des catégories (système de "tags" ou "bulles")
    var currentCategory by remember { mutableStateOf("") } // Texte en cours de saisie
    var categoryList by remember { mutableStateOf(listOf<String>()) } // Liste des catégories validées

    // État pour stocker l'URI de l'image sélectionnée depuis la galerie
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher pour ouvrir le sélecteur de documents d'Android (galerie photo)
    // Demande à obtenir du contenu ("GetContent") de type "image/*"
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Quand l'utilisateur choisit une image, on met à jour l'état avec son Uri
        imageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Your Cocktail", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        // Formulaire complet avec défilement vertical
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Mix your own magic! ✨",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // --- ZONE DE SÉLECTION D'IMAGE ---
            // Un grand bloc cliquable qui affiche l'image choisie ou un bouton "Ajouter" par défaut
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    // Au clic, on lance l'intent pour choisir n'importe quel type d'image
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    // Si une image est sélectionnée, on l'affiche avec Coil
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop // Recadre l'image pour remplir la boîte
                    )
                } else {
                    // Sinon, affichage d'un placeholder (icône + texte)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = "Add Photo", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Add a Photo", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // --- CHAMP NOM DU COCKTAIL ---
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Cocktail Name") },
                placeholder = { Text("e.g. Emmy's Special") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // --- CHAMP CATÉGORIE ---
            // Champ texte avec un bouton "+" intégré ou validation clavier pour ajouter des "tags"
            OutlinedTextField(
                value = currentCategory,
                onValueChange = { currentCategory = it },
                label = { Text("Category") },
                placeholder = { Text("Ex: Sweet, Fruity...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), // Change le bouton "Entrée" du clavier en "Terminé"
                keyboardActions = KeyboardActions(
                    // Action quand on appuie sur "Terminé" sur le clavier
                    onDone = {
                        // On vérifie que le texte n'est pas vide et que la catégorie n'existe pas déjà
                        if (currentCategory.isNotBlank() && !categoryList.contains(currentCategory.trim())) {
                            categoryList = categoryList + currentCategory.trim() // Ajout à la liste
                            currentCategory = "" // On vide le champ pour la prochaine saisie
                        }
                    }
                ),
                trailingIcon = {
                    // Bouton "+" physique dans le champ pour ceux qui ne valident pas par le clavier
                    IconButton(onClick = {
                        if (currentCategory.isNotBlank() && !categoryList.contains(currentCategory.trim())) {
                            categoryList = categoryList + currentCategory.trim()
                            currentCategory = ""
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Category")
                    }
                }
            )

            // --- AFFICHAGE DES BULLES DE CATÉGORIES EN DESSOUS ---
            // N'apparaît que s'il y a au moins une catégorie ajoutée
            if (categoryList.isNotEmpty()) {
                // Ligne défilante horizontalement pour afficher les tags
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoryList.forEach { cat ->
                        // Chaque catégorie est une petite "pilule" (Surface avec coins très arrondis)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            // Si on clique sur la bulle, on la supprime de la liste
                            modifier = Modifier.clickable {
                                categoryList = categoryList - cat
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                // Petite croix pour indiquer que c'est supprimable
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // --- CHAMP INGRÉDIENTS ---
            OutlinedTextField(
                value = ingredients,
                onValueChange = { ingredients = it },
                label = { Text("Ingredients") },
                placeholder = { Text("1 oz Rum\n2 oz Pineapple Juice...") },
                modifier = Modifier.fillMaxWidth().height(120.dp), // Hauteur fixe pour donner de la place
                shape = RoundedCornerShape(12.dp)
            )

            // --- CHAMP INSTRUCTIONS ---
            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Instructions") },
                placeholder = { Text("Shake well with ice and pour...") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- BOUTON DE CRÉATION (SAUVEGARDE) ---
            Button(
                onClick = {
                    // Validation basique : on exige au moins un nom et des instructions
                    if (name.isNotBlank() && instructions.isNotBlank()) {
                        // Lancement de la coroutine pour écrire dans la base de données Room (qui doit se faire en asynchrone)
                        coroutineScope.launch {
                            // CORRECTION ICI : On transforme la liste de catégories en un seul texte séparé par des virgules
                            // Si la liste est vide, on met "Creation" par défaut.
                            val finalCategory = if (categoryList.isNotEmpty()) categoryList.joinToString(", ") else "Creation"

                            // Création de l'objet Entity à insérer
                            val newCocktail = CreatedCocktailEntity(
                                name = name,
                                category = finalCategory,
                                ingredients = ingredients,
                                instructions = instructions,
                                imageUri = imageUri?.toString() // On convertit l'Uri en String pour la BDD
                            )

                            // Insertion en BDD via le DAO
                            createdCocktailDao.insert(newCocktail)

                            // Feedback utilisateur
                            Toast.makeText(context, "Cocktail created! 🍸", Toast.LENGTH_SHORT).show()

                            // Navigation vers la liste des créations
                            navController.navigate("created_cocktails_list") {
                                // On dépile l'écran de création pour éviter de pouvoir y retourner avec le bouton "Retour"
                                popUpTo("creation") { inclusive = true }
                            }
                        }
                    } else {
                        // Message d'erreur si les champs obligatoires sont vides
                        Toast.makeText(context, "Please enter a name and instructions.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Create Cocktail", fontSize = MaterialTheme.typography.titleMedium.fontSize)
            }
        }
    }
}