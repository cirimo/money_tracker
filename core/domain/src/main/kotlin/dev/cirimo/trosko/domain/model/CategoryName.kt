package dev.cirimo.trosko.domain.model

/**
 * What a category is called. A category that ships with the app has no stored text: its name
 * comes from string resources, so it follows the app language. Once the user renames it, or
 * creates their own, the text they typed is the name.
 */
sealed interface CategoryName {
    /** A category that ships with the app; [key] selects its string resource. */
    data class Builtin(
        val key: String,
    ) : CategoryName

    /** A name the user typed. */
    data class Custom(
        val text: String,
    ) : CategoryName
}
