package dev.cirimo.trosko.data.db

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

/**
 * The one database of the app. Its schema is exported to `core/data/schemas` on every build.
 *
 * Version 1 is still a draft and may change in place until the first release build is installed
 * on a real phone. From then on every change is a new version with a hand-written, tested
 * migration. See docs/ARCHITECTURE.md, persistence.
 */
@Database(
    entities = [CategoryEntity::class, RecordEntity::class],
    version = 1,
    exportSchema = true,
)
internal abstract class TroskoDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao

    abstract fun recordDao(): RecordDao
}

private const val DATABASE_FILE_NAME = "trosko.db"

/**
 * Opens the on-device database. [allowDestructiveMigration] must be true only for builds whose
 * data is disposable: it lets Room wipe the database when no migration path exists, which is
 * convenient on a development build and unforgivable on a user's phone.
 */
internal fun openTroskoDatabase(
    context: Context,
    allowDestructiveMigration: Boolean,
): TroskoDatabase {
    val builder =
        Room
            .databaseBuilder<TroskoDatabase>(context.applicationContext, DATABASE_FILE_NAME)
            .setDriver(BundledSQLiteDriver())
            .addCallback(BuiltinCategorySeed())
    if (allowDestructiveMigration) {
        builder.fallbackToDestructiveMigration(dropAllTables = true)
    }
    return builder.build()
}
