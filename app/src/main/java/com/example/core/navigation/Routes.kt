package com.example.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object HomeRoute

@Serializable
object VocabularyRoute

@Serializable
object ReadingRoute

@Serializable
data class ArticleDetailRoute(val articleId: String)

@Serializable
object ProfileRoute

@Serializable
object AdminRoute

@Serializable
object FlashCardRoute

@Serializable
object QuizRoute

@Serializable
object PaywallRoute
