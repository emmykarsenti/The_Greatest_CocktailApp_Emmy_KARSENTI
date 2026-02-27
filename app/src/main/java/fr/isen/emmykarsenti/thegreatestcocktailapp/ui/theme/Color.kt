package fr.isen.emmykarsenti.thegreatestcocktailapp.ui.theme

import androidx.compose.ui.graphics.Color

// Ce fichier centralise toutes les couleurs de l'application.
// Utiliser des variables permet de modifier une couleur ici et de la voir changer partout dans l'app.
// Le format "0xFF..." correspond au code hexadécimal de la couleur, où "FF" au début signifie 100% d'opacité (pas de transparence).

// --- PALETTE COCKTAIL BAR ---

// Couleur de fond (Noir très profond, presque pur, idéal pour le mode sombre)
val BackgroundDark = Color(0xFF0F0F0F)

// Couleur des cartes et des éléments de surface (Gris anthracite pour se détacher du fond noir)
val SurfaceDark = Color(0xFF1E1E1E)

// Couleur d'accentuation : Or / Orange "Aperol" (Utilisée pour les boutons, les titres importants)
val CocktailGold = Color(0xFFFFB74D)
val CocktailGoldVariant = Color(0xFFFFCC80)

// Couleur secondaire : Un vert menthe frais pour les détails (icônes spécifiques, badges)
val MintFresh = Color(0xFF81C784)

// --- COULEURS PAR DÉFAUT (Optionnel, à garder si besoin) ---
val WhiteText = Color(0xFFFFFFFF)
val GreyText = Color(0xFFBDBDBD)

// On garde les noms de variables générés par défaut par Android Studio (Purple80, Pink80...)
// mais on leur assigne nos propres couleurs. Ça évite de devoir renommer toutes les variables
// dans le fichier Theme.kt si on fait une transition rapide.
val Purple80 = CocktailGold
val PurpleGrey80 = SurfaceDark
val Pink80 = MintFresh

val Purple40 = Color(0xFFE65100)
val PurpleGrey40 = Color(0xFF4E342E)
val Pink40 = Color(0xFFBF360C)