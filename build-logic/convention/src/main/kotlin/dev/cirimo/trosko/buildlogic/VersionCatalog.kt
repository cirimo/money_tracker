package dev.cirimo.trosko.buildlogic

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

// Convention plugins cannot use the generated `libs` accessors, so they look entries up by alias.
internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun VersionCatalog.version(alias: String): String =
    findVersion(alias)
        .orElseThrow {
            NoSuchElementException("Version '$alias' is missing from libs.versions.toml")
        }.requiredVersion

internal fun VersionCatalog.library(alias: String): Provider<MinimalExternalModuleDependency> =
    findLibrary(alias).orElseThrow { NoSuchElementException("Library '$alias' is missing from libs.versions.toml") }
