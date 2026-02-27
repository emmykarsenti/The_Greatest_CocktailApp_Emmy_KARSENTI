package fr.isen.emmykarsenti.thegreatestcocktailapp.models

import androidx.compose.runtime.mutableStateListOf

// 'object' en Kotlin crée un Singleton : il n'y a qu'une seule instance de AppData dans toute l'application.
// Ces données sont conservées en mémoire tant que l'application est ouverte (elles sont perdues si on la ferme complètement).
object AppData {
    // Garde en mémoire la suggestion du jour (pour éviter de recharger l'API à chaque retour sur l'accueil).
    var suggestionDuJour: Cocktail? = null

    // Mémorise si le popup du barman (MoodDialog) a déjà été affiché pendant cette session.
    // Cela empêche le popup de réapparaître à chaque fois qu'on revient sur l'écran d'accueil.
    var hasMoodPopupBeenShown = false

    // Liste observable des cocktails récents.
    // 'mutableStateListOf' prévient Jetpack Compose à chaque modification : l'interface se mettra à jour toute seule.
    val recemmentConsultes = mutableStateListOf<Cocktail>()

    // Fonction appelée depuis la page de détail d'un cocktail pour l'ajouter à l'historique
    fun ajouterAuxRecents(cocktail: Cocktail) {
        // 1. On évite les doublons : si le cocktail est déjà dans la liste, on le supprime d'abord
        // pour pouvoir le remettre tout en haut ensuite.
        recemmentConsultes.removeAll { it.idDrink == cocktail.idDrink }

        // 2. On l'ajoute tout au début de la liste (index 0), car c'est le plus récent.
        recemmentConsultes.add(0, cocktail)

        // 3. On limite la taille de l'historique pour ne pas surcharger la mémoire.
        // Si la liste dépasse 5 éléments, on supprime le plus ancien (le dernier de la liste).
        if (recemmentConsultes.size > 5) {
            recemmentConsultes.removeAt(recemmentConsultes.lastIndex)
        }
    }
}