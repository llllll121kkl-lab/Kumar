package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "saved_content")
data class SavedContentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // idea, script, title, hashtag, caption, plan
    val content: String,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "content_plans")
data class ContentPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateMs: Long,
    val platform: String,
    val topic: String,
    val title: String,
    val status: String // Idea, Planned, In Progress, Published
)

@Dao
interface AppDao {
    @Query("SELECT * FROM saved_content ORDER BY timestamp DESC")
    fun getAllSavedContent(): Flow<List<SavedContentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedContent(content: SavedContentEntity)

    @Query("DELETE FROM saved_content WHERE id = :id")
    suspend fun deleteSavedContentById(id: Int)

    @Query("SELECT * FROM content_plans ORDER BY dateMs ASC")
    fun getAllContentPlans(): Flow<List<ContentPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContentPlan(plan: ContentPlanEntity)

    @Query("DELETE FROM content_plans WHERE id = :id")
    suspend fun deleteContentPlanById(id: Int)

    @Query("DELETE FROM saved_content")
    suspend fun deleteAllSavedContent()

    @Query("DELETE FROM content_plans")
    suspend fun deleteAllContentPlans()
}

@Database(entities = [SavedContentEntity::class, ContentPlanEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}

class AppRepository(private val dao: AppDao) {
    val savedContents = dao.getAllSavedContent()
    val contentPlans = dao.getAllContentPlans()

    suspend fun saveContent(content: SavedContentEntity) = dao.insertSavedContent(content)
    suspend fun deleteContent(id: Int) = dao.deleteSavedContentById(id)
    
    suspend fun savePlan(plan: ContentPlanEntity) = dao.insertContentPlan(plan)
    suspend fun deletePlan(id: Int) = dao.deleteContentPlanById(id)

    suspend fun clearAllSavedContent() = dao.deleteAllSavedContent()
    suspend fun clearAllPlans() = dao.deleteAllContentPlans()
}
