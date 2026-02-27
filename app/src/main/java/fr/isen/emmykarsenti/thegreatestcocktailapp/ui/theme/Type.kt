package fr.isen.emmykarsenti.thegreatestcocktailapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Fichier dédié à la configuration des polices d'écriture (typographie) de l'application.

// On initialise l'objet Typography de Material3 qui contient tous les styles de texte
// (titres, corps de texte, labels...).
val Typography = Typography(

    // On redéfinit ici le style "bodyLarge", qui est le style par défaut utilisé
    // par la majorité des composants Text() dans Jetpack Compose.
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default, // Utilise la police système par défaut du téléphone (Roboto sur Android)
        fontWeight = FontWeight.Normal,  // Poids du texte normal (ni gras, ni fin)
        fontSize = 16.sp,                // Taille de la police (on utilise 'sp' pour que ça s'adapte aux réglages d'accessibilité du téléphone)
        lineHeight = 24.sp,              // Espacement entre les lignes
        letterSpacing = 0.5.sp           // Espacement entre les lettres
    )

    /* Bloc commenté généré par défaut.
    Il montre comment on pourrait personnaliser d'autres styles de texte si besoin :

    // Pour les grands titres (ex: TopAppBar)
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    // Pour les petits textes (ex: Textes en dessous des icônes)
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)