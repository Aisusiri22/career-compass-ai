package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// --- Profile ---
@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val branch: String,
    val collegeYear: String,
    val interests: String,
    val skills: String,
    val goals: String
)

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<ProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: ProfileEntity)
}

// --- Skill progress tracker ---
@Entity(
    tableName = "skill_progress",
    primaryKeys = ["careerPath", "skillName"]
)
data class SkillProgressEntity(
    val careerPath: String,
    val skillName: String,
    val isCompleted: Boolean = false
)

@Dao
interface SkillProgressDao {
    @Query("SELECT * FROM skill_progress WHERE careerPath = :careerPath")
    fun getProgressForPath(careerPath: String): Flow<List<SkillProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: SkillProgressEntity)

    @Query("UPDATE skill_progress SET isCompleted = :isCompleted WHERE careerPath = :careerPath AND skillName = :skillName")
    suspend fun updateSkillStatus(careerPath: String, skillName: String, isCompleted: Boolean)

    @Query("SELECT COUNT(*) FROM skill_progress WHERE careerPath = :careerPath AND isCompleted = 1")
    suspend fun getCompletedCount(careerPath: String): Int

    @Query("SELECT COUNT(*) FROM skill_progress WHERE careerPath = :careerPath")
    suspend fun getTotalCount(careerPath: String): Int
}

// --- Study Plan ---
@Entity(tableName = "study_plans")
data class StudyPlanEntity(
    @PrimaryKey val careerPath: String,
    val dailyHours: Int,
    val dailyPlan: String,
    val weeklyPlan: String,
    val monthlyPlan: String
)

@Dao
interface StudyPlanDao {
    @Query("SELECT * FROM study_plans WHERE careerPath = :careerPath LIMIT 1")
    fun getStudyPlan(careerPath: String): Flow<StudyPlanEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStudyPlan(studyPlan: StudyPlanEntity)
}

// --- Chat Messages ---
@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String, // "user" or "model"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearHistory()
}

// --- Database Configuration ---
@Database(
    entities = [
        ProfileEntity::class,
        SkillProgressEntity::class,
        StudyPlanEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun skillProgressDao(): SkillProgressDao
    abstract fun studyPlanDao(): StudyPlanDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "career_compass_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
