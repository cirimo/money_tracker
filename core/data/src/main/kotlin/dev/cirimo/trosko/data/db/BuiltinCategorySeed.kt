package dev.cirimo.trosko.data.db

import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteConnection
import dev.cirimo.trosko.domain.model.BuiltinCategories
import dev.cirimo.trosko.domain.model.BuiltinCategory

// Both timestamps are zero on purpose: a built-in category is the same row on every device, and
// when a restore compares `updated_at`, anything the user has changed must win over the seed.
private const val SEED_TIMESTAMP = 0L

private const val INSERT_BUILTIN =
    "INSERT OR IGNORE INTO category " +
        "(id, kind, builtin_key, custom_name, colour, icon, sort_order, archived_at, created_at, updated_at) " +
        "VALUES (?, ?, ?, NULL, ?, ?, ?, NULL, ?, ?)"

/**
 * Writes the categories that ship with the app into a database that has just been created, as
 * part of creating it, so the app never sees a database without them.
 *
 * It runs again after a destructive migration, which only a debuggable build allows, because
 * that leaves the tables just as empty as a new database.
 */
internal class BuiltinCategorySeed : RoomDatabase.Callback() {
    override suspend fun onCreate(connection: SQLiteConnection) = seed(connection)

    override suspend fun onDestructiveMigration(connection: SQLiteConnection) = seed(connection)

    private fun seed(connection: SQLiteConnection) {
        BuiltinCategories.all.forEach { category -> insert(connection, category) }
    }

    @Suppress("MagicNumber") // The numbers are the positions of the placeholders in the statement.
    private fun insert(
        connection: SQLiteConnection,
        category: BuiltinCategory,
    ) {
        connection.prepare(INSERT_BUILTIN).use { statement ->
            statement.bindText(1, category.id.value.toString())
            statement.bindText(2, category.kind.toColumn())
            statement.bindText(3, category.key)
            statement.bindText(4, category.colour.toColumn())
            statement.bindText(5, category.icon.toColumn())
            statement.bindLong(6, category.sortOrder.toLong())
            statement.bindLong(7, SEED_TIMESTAMP)
            statement.bindLong(8, SEED_TIMESTAMP)
            statement.step()
        }
    }
}
