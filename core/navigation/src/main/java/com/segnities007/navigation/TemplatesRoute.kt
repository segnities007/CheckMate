package com.segnities007.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface TemplatesRoute {
    @Serializable
    data object WeeklyTemplateList : TemplatesRoute

    @Serializable
    data object WeeklyTemplateSelector : TemplatesRoute
}
