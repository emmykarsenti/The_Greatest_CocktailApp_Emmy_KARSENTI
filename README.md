# The Greatest CocktailApp
The Greatest CocktailApp est une application Android complète, développée en Kotlin avec Jetpack Compose. Créée dans le cadre d'un projet académique pour découvrir le développement Android, l'application a été largement enrichie pour offrir une expérience utilisateur moderne, digne d'une application professionnelle publiée sur les stores.

Le projet se distingue par son interface élégante en mode sombre, sa navigation intuitive et l'intégration de fonctionnalités avancées telles que l'Intelligence Artificielle et la monétisation.

# Fonctionnalités de Base (Requises par le projet)
Ces fonctionnalités répondent strictement au cahier des charges initial :


- Écran de détail (DetailCocktailScreen) : Affichage complet d'un cocktail avec son image, son titre, ses catégories, le type de verre requis, les ingrédients et les instructions de préparation.


- Liste de Catégories (CategoriesScreen) : Écran permettant de parcourir les familles de boissons (Cocktails, bières, cafés, etc.).


- Navigation : Implémentation d'un NavHost fluide pour gérer les différentes routes de l'application.


- Connexion Réseau & API : Utilisation de Retrofit2 et Gson pour récupérer les données de l'API TheCocktailDB.


- Images Asynchrones : Intégration de la librairie Coil pour le chargement des images depuis internet.


- Gestion des Favoris : Possibilité de sauvegarder ses cocktails préférés pour les retrouver dans une liste dédiée (FavoriteScreen). (La consigne demandait l'utilisation des SharedPreferences, mais le système a été amélioré vers une vraie base de données locale).


# Fonctionnalités Avancées (Ajouts Personnels)
Pour aller au-delà de la demande initiale et concevoir une véritable application "produit", de nombreuses fonctionnalités ont été ajoutées :

- Interface Graphique (UI/UX) sur mesure : Un design soigné en Dark Theme, intégrant un logo personnalisé et une ergonomie pensée pour l'utilisateur (Bottom Navigation Bar claire, ajout de flèches de retour systématiques sur les écrans secondaires).

- Écran d'Accueil Repensé : Mise en place d'une page de démarrage affichant une "Daily Suggestion" (Suggestion du jour) mise en valeur dans une carte, ainsi qu'une section "Recently Viewed" pour retrouver rapidement les derniers cocktails consultés.

- Mixologue Virtuel (IA Gemini) : Intégration de l'Intelligence Artificielle Google Gemini. L'utilisateur renseigne les ingrédients qu'il a sous la main, et l'IA génère instantanément une recette de cocktail unique et personnalisée.

- Quiz d'Humeur (Mood Pop-up) : Un système interactif sous forme de pop-up pose quelques questions à l'utilisateur sur son humeur du moment pour lui suggérer le cocktail parfait.

- Création Manuelle & Base de Données Locale : Ajout d'un formulaire permettant de créer ses propres recettes de A à Z. Ces créations, tout comme les favoris, sont stockées dans une base de données locale robuste (Room), accessible même hors ligne.

- Recherche Avancée : Amélioration de la barre de recherche avec un système d'auto-complétion (suggestions dynamiques dès la saisie) et l'ajout de filtres de tri.

- Monétisation : Intégration du SDK Google AdMob pour afficher des bannières publicitaires ciblées et non intrusives, respectant la thématique de l'application.

# Stack Technique
- Langage : Kotlin

- Interface Graphique : Jetpack Compose (Architecture Single-Activity)

- Appels Réseaux & JSON : Retrofit2, Gson

- Chargement d'Images : Coil

- Intelligence Artificielle : Google Generative AI SDK (Gemini)

- Monétisation : Google Play Services Ads (AdMob)
