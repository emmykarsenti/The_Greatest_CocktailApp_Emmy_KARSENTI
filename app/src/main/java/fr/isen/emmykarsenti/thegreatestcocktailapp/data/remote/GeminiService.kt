package fr.isen.emmykarsenti.thegreatestcocktailapp.data.remote

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.gson.Gson
import fr.isen.emmykarsenti.thegreatestcocktailapp.BuildConfig
import fr.isen.emmykarsenti.thegreatestcocktailapp.models.CocktailResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Cette classe s'occupe de toute la communication avec l'IA.
class GeminiService {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    private val gson = Gson()

    suspend fun generateCocktailFromIngredients(ingredients: String): CocktailResponse? {
        return withContext(Dispatchers.IO) {
            try {
                // LE PROMPT : On a ajouté { "drinks": [ ... ] }
                val prompt = """
                    You are an expert mixologist. Create a UNIQUE cocktail recipe using mainly the following ingredients: $ingredients.
                    You can add common base ingredients (sugar, lemon, ice, soda water) if necessary to balance the drink.
                    Give the cocktail a creative name.

                    IMPORTANT: You must reply ONLY with a raw JSON object. All the text (name, instructions, ingredients) MUST BE IN ENGLISH.
                    Do NOT use markdown tags (like ```json ... ```).
                    The JSON must EXACTLY match this structure for my app to read it:
                    {
                      "drinks": [
                        {
                          "idDrink": "gemini_${System.currentTimeMillis()}",
                          "strDrink": "Creative Cocktail Name Here",
                          "strInstructions": "Step-by-step instructions to make the cocktail...",
                          "strDrinkThumb": "https://cdn.midjourney.com/5702468b-3b23-47aa-965d-035ba2472113/0_3.png", 
                          "strIngredient1": "First ingredient used",
                          "strMeasure1": "Quantity (e.g., 2 oz)",
                          "strIngredient2": "Second ingredient",
                          "strMeasure2": "Quantity",
                          "strAlcoholic": "Alcoholic",
                          "strCategory": "Cocktail Created by AI"
                        }
                      ]
                    }
                    For the image (strDrinkThumb), ALWAYS use the example URL provided above.
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                val responseText = response.text

                if (responseText.isNullOrBlank()) {
                    Log.e("GeminiService", "La réponse de l'IA est vide.")
                    return@withContext null
                }
                var cleanText = responseText.trim()
                if (cleanText.startsWith("```json", ignoreCase = true)) {
                    cleanText = cleanText.substringAfter("```json").substringBeforeLast("```").trim()
                } else if (cleanText.startsWith("```")) {
                    cleanText = cleanText.substringAfter("```").substringBeforeLast("```").trim()
                }

                // On utilise cleanText au lieu de responseText
                return@withContext gson.fromJson(cleanText, CocktailResponse::class.java)

            } catch (e: Exception) {
                Log.e("GeminiService", "Erreur lors de la génération du cocktail", e)
                return@withContext null
            }
        }
    }
}