package dev.cirimo.trosko.data.db

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
internal interface RecordDao {
    @Insert
    suspend fun insert(record: RecordEntity)

    /**
     * Rewrites what the row with the id of [content] says and returns how many rows that touched:
     * one, or none when there is no such record.
     */
    @Update(entity = RecordEntity::class)
    suspend fun replace(content: RecordContent): Int

    /** Removes the record [id] and returns how many rows that was: one, or none. */
    @Query("DELETE FROM record WHERE id = :id")
    suspend fun delete(id: String): Int

    @Transaction
    @Query("SELECT * FROM record WHERE id = :id")
    fun observe(id: String): Flow<RecordWithCategory?>

    /** Records dated from [fromInclusive] up to but not including [toExclusive], newest first. */
    @Query(
        "SELECT * FROM record WHERE occurred_on >= :fromInclusive AND occurred_on < :toExclusive " +
            "ORDER BY occurred_on DESC, created_at DESC",
    )
    fun observeInRange(
        fromInclusive: String,
        toExclusive: String,
    ): Flow<List<RecordEntity>>

    /**
     * The [limit] records written most recently, newest first, whatever their dates. The row id
     * breaks a tie between two records written in the same millisecond.
     */
    @Transaction
    @Query("SELECT * FROM record ORDER BY created_at DESC, rowid DESC LIMIT :limit")
    fun observeLatest(limit: Int): Flow<List<RecordWithCategory>>
}
