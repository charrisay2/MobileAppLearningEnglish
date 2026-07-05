package com.example.lingua.features.news.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable
data class NewsResponseDto(
    @SerialName("status") val status: String? = null,
    @SerialName("copyright") val copyright: String? = null,
    @SerialName("response") val response: ArticleResponseDto? = null
)

@Serializable
data class ArticleResponseDto(
    @SerialName("docs") val docs: List<ArticleDto> = emptyList(),
    @SerialName("meta") val meta: MetaDto? = null
)

@Serializable
data class ArticleDto(
    @SerialName("_id") val id: String,
    @SerialName("abstract") val abstract: String? = null,
    @SerialName("snippet") val snippet: String? = null,
    @SerialName("lead_paragraph") val leadParagraph: String? = null,
    @SerialName("web_url") val webUrl: String? = null,
    @SerialName("source") val source: String? = null,
    @Serializable(with = MultimediaListSerializer::class)
    @SerialName("multimedia") val multimedia: List<MultimediaDto> = emptyList(),
    @SerialName("headline") val headline: HeadlineDto? = null,
    @SerialName("pub_date") val pubDate: String? = null,
    @SerialName("section_name") val sectionName: String? = null,
    @SerialName("byline") val byline: BylineDto? = null
)

object MultimediaListSerializer : KSerializer<List<MultimediaDto>> {
    private val delegateSerializer = ListSerializer(MultimediaDto.serializer())
    override val descriptor: SerialDescriptor = delegateSerializer.descriptor

    override fun deserialize(decoder: Decoder): List<MultimediaDto> {
        val input = decoder as? JsonDecoder ?: throw SerializationException("This serializer can be used only with Json format")
        val element = input.decodeJsonElement()

        return when (element) {
            is JsonArray -> input.json.decodeFromJsonElement(delegateSerializer, element)
            is JsonObject -> listOf(input.json.decodeFromJsonElement(MultimediaDto.serializer(), element))
            else -> emptyList()
        }
    }

    override fun serialize(encoder: Encoder, value: List<MultimediaDto>) {
        encoder.encodeSerializableValue(delegateSerializer, value)
    }
}

@Serializable
data class HeadlineDto(
    @SerialName("main") val main: String? = null
)

@Serializable
data class BylineDto(
    @SerialName("original") val original: String? = null
)

@Serializable
data class MultimediaDto(
    @SerialName("url") val url: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("subtype") val subtype: String? = null
)

@Serializable
data class MetaDto(
    @SerialName("hits") val hits: Int? = null,
    @SerialName("offset") val offset: Int? = null,
    @SerialName("time") val time: Int? = null
)
