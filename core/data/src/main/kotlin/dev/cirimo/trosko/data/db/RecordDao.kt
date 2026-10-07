package dev.cirimo.trosko.data.db

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface RecordDao {
    @Insert
    suspend fun insert(record: RecordEntity)

    /** Records dated from [fromInclusive] up to but not including [toExclusive], newest first. */
    @Query(
        "SELECT * FROM record WHERE occurred_on >= :fromInclusive AND occurred_on < :toExclusive " +
            "ORDER BY occurred_on DESC, created_at DESC",
    )
    fun observeInRange(
        fromInclusive: String,
        toExclusive: String,
    ): Flow<List<RecordEntity>>
}
