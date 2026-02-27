package fr.isen.emmykarsenti.thegreatestcocktailapp.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// --- THÈME SOMBRE (Le look "Cocktail Bar") ---
// On définit comment nos couleurs personnalisées (définies dans Color.kt) s'appliquent
// aux différents éléments standards de Material Design (primary, background, surface...).
private val DarkColorScheme = darkColorScheme(
    primary = CocktailGold,       // Couleur principale (boutons, barres d'outils)
    secondary = MintFresh,        // Couleur secondaire
    tertiary = Pink80,            // Troisième couleur d'accentuation
    background = BackgroundDark,  // Le fond de l'application
    surface = SurfaceDark,        // Le fond des cartes (Card) ou des menus
    onPrimary = Color.Black,      // Couleur du texte qui s'affiche SUR un élément "primary" (texte noir sur bouton Or)
    onBackground = Color.White,   // Couleur du texte principal sur le fond sombre
    onSurface = Color.White       // Couleur du texte sur les cartes
)

// --- THÈME CLAIR (On garde une base élégante au cas où on réactive le mode clair) ---
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFE65100),
    secondary = MintFresh,
    tertiary = Pink40,
    background = Color(0xFFFFFBFE),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

// 👇 C'EST CETTE ANNOTATION QUI EST CRUCIALE 👇
// C'est le composant principal de ton thème. Il englobe toute ton application dans MainActivity.kt.
@Composable
fun TheGreatestCocktailAppTheme(
    darkTheme: Boolean = true, // On force le thème sombre par défaut ici pour coller à l'ambiance bar de nuit !
    dynamicColor: Boolean = false, // On désactive les couleurs dynamiques (Material You) qui prendraient les couleurs du fond d'écran du téléphone de l'utilisateur.
    content: @Composable () -> Unit // Le contenu de l'application (tes écrans)
) {
    // Logique de sélection du thème
    val colorScheme = when {
        // Si les couleurs dynamiques étaient activées (Android 12+)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Sinon, on applique notre thème sombre personnalisé
        darkTheme -> DarkColorScheme
        // Et par défaut (si darkTheme était false), le thème clair
        else -> LightColorScheme
    }

    // MaterialTheme applique les couleurs, la typographie et les formes (bords arrondis) à toute l'app.
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Fait le lien avec les polices définies dans Type.kt
        content = content
    )
}