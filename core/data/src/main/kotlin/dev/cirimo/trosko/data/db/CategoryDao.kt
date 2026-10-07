package dev.cirimo.trosko.data.db

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CategoryDao {
    @Insert
    suspend fun insert(category: CategoryEntity)

    @Query("SELECT * FROM category WHERE kind = :kind AND archived_at IS NULL ORDER BY sort_order")
    fun observeActive(kind: String): Flow<List<CategoryEntity>>
}
