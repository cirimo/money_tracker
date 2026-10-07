package dev.cirimo.trosko.data

import android.content.Context
import dev.cirimo.trosko.data.db.openTroskoDatabase
import dev.cirimo.trosko.data.repository.RoomCategoryRepository
import dev.cirimo.trosko.domain.repository.CategoryRepository

/**
 * The only door into this module. It hands out the domain's repository interfaces and keeps the
 * database, its tables and its queries invisible to the rest of the app.
 *
 * Create one per process. Nothing is opened until a repository is first used, so constructing
 * this at startup costs nothing.
 *
 * @param allowDestructiveMigration true only for builds whose data is disposable; see
 * `openTroskoDatabase`.
 */
class Repositories(
    context: Context,
    allowDestructiveMigration: Boolean,
) {
    private val database by lazy { openTroskoDatabase(context, allowDestructiveMigration) }

    val categories: CategoryRepository by lazy { RoomCategoryRepository(database.categoryDao()) }
}
