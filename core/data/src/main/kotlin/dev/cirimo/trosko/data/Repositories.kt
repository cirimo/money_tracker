package dev.cirimo.trosko.data

import android.content.Context
import dev.cirimo.trosko.data.db.openTroskoDatabase
import dev.cirimo.trosko.data.repository.RoomCategoryRepository
import dev.cirimo.trosko.data.repository.RoomRecordRepository
import dev.cirimo.trosko.domain.repository.CategoryRepository
import dev.cirimo.trosko.domain.repository.RecordRepository
import kotlinx.coroutines.CoroutineScope
import java.time.Clock

/**
 * The only door into this module. It hands out the domain's repository interfaces and keeps the
 * database, its tables and its queries invisible to the rest of the app.
 *
 * Create one per process. Nothing is opened until a repository is first used, so constructing
 * this at startup costs nothing.
 *
 * @param allowDestructiveMigration true only for builds whose data is disposable; see
 * `openTroskoDatabase`.
 * @param writeScope the application-wide scope writes run in, so that a write the user asked for
 * finishes even when the screen that asked is gone. It must live as long as the process.
 * @param clock stamps rows with the time they were written.
 */
class Repositories(
    context: Context,
    allowDestructiveMigration: Boolean,
    writeScope: CoroutineScope,
    clock: Clock,
) {
    private val database by lazy { openTroskoDatabase(context, allowDestructiveMigration) }

    val categories: CategoryRepository by lazy { RoomCategoryRepository(database.categoryDao()) }

    val records: RecordRepository by lazy { RoomRecordRepository(database.recordDao(), writeScope, clock) }
}
