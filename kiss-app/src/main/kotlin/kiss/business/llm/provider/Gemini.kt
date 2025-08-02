package kiss.business.llm.provider

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.genai.Client
import com.google.genai.types.Content
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.Part
import com.google.genai.types.Schema
import io.github.smiley4.schemakenerator.core.CoreSteps.initial
import io.github.smiley4.schemakenerator.jsonschema.JsonSchemaSteps.compileInlining
import io.github.smiley4.schemakenerator.jsonschema.JsonSchemaSteps.generateJsonSchema
import io.github.smiley4.schemakenerator.reflection.ReflectionSteps.analyzeTypeUsingReflection

class Gemini {
    val model = "gemini-2.5-flash-lite-preview-06-17"
    val objectMapper = ObjectMapper().registerKotlinModule()
    val client: Client = Client
        .builder()
        .apiKey(System.getenv("GEMINI_API_KEY"))
        .build()

    fun chat(
        userPrompt: String,
        systemPrompt: String? = null,
        maxTokens: Int = 1024,
    ): String? {
        val config = builder(maxTokens, systemPrompt).build()
        val response = client.models.generateContent(model, userPrompt, config)
        return response.text()
    }

    inline fun <reified T> chat(
        userPrompt: String,
        systemPrompt: String? = null,
        maxTokens: Int = 1024,
    ): T? {
        val jsonSchema = initial<T>()
            .analyzeTypeUsingReflection()
            .generateJsonSchema()
            .compileInlining()
            .json
            .prettyPrint()
        val schema = objectMapper.readValue<Schema>(jsonSchema)

        val config = builder(maxTokens, systemPrompt)
            .responseMimeType("application/json")
            .responseSchema(schema)
            .build()
        val response = client.models.generateContent(model, userPrompt, config)
        return response.text()?.let { objectMapper.readValue<T>(it) }
    }

    fun builder(maxTokens: Int, systemPrompt: String?): GenerateContentConfig.Builder {
        val builder = GenerateContentConfig.builder()
            .candidateCount(1)
            .maxOutputTokens(maxTokens)

        if (systemPrompt != null) {
            val systemInstruction = Content.fromParts(Part.fromText(systemPrompt))
            builder.systemInstruction(systemInstruction)
        }

        return builder
    }
}