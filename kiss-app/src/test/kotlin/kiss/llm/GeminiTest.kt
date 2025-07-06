package kiss.llm

import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotBeEmpty
import kiss.llm.provider.Gemini
import org.junit.jupiter.api.Test

class GeminiTest {

    val gemini = Gemini()

    @Test
    fun chatAndReturnText() {
        val answer = gemini.chat("ChatGPT是哪家公司开发的？")
        answer.shouldContain("OpenAI")
    }

    @Test
    fun chatAndReturnObject() {
        data class Recipe(
            val recipeName: String,
            val ingredients: List<String>,
        )

        val recipes = gemini
            .chat<List<Recipe>>("List a few popular cookie recipes, and include the amounts of ingredients.")
            .shouldNotBeEmpty()

        for (recipe in recipes) {
            recipe.recipeName.shouldNotBeEmpty()
            recipe.ingredients.shouldNotBeEmpty()
        }
    }
}