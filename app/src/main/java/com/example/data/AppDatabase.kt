package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// --- Entities ---

@Entity(tableName = "case_files")
data class CaseFile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val state: String, // e.g. "Federal", "California", "New York"
    val description: String,
    val aiFeedback: String,
    val imageUri: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Active" // "Active", "Archived", "Closed"
)

@Entity(tableName = "court_reminders")
data class CourtReminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val dateText: String, // e.g., "2026-07-25"
    val timeText: String, // e.g., "10:00 AM"
    val location: String,
    val notes: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_high_scores")
data class QuizHighScore(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val score: Int,
    val total: Int,
    val player: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "avatar_config")
data class AvatarConfig(
    @PrimaryKey val id: Int = 1,
    val faceStyle: String = "The Stern Judge", // "The Stern Judge", "The Slick Litigator", "The Tech Techie", "The Wise Counsel"
    val outfitStyle: String = "The Classic Navy Suit", // "The Classic Navy Suit", "The Royal Purple Tux", "The Casual Blazer", "The Detective Trenchcoat"
    val backgroundStyle: String = "Courtroom Podium" // "Courtroom Podium", "Classic Library", "Modern Office", "City View Night"
)

@Entity(tableName = "law_change_alerts")
data class LawChangeAlert(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val state: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

// --- DAOs ---

@Dao
interface LawDao {
    // Cases
    @Query("SELECT * FROM case_files ORDER BY timestamp DESC")
    fun getAllCasesFlow(): Flow<List<CaseFile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(caseFile: CaseFile)

    @Query("DELETE FROM case_files WHERE id = :id")
    suspend fun deleteCaseById(id: Int)

    // Reminders
    @Query("SELECT * FROM court_reminders ORDER BY timestamp ASC")
    fun getAllRemindersFlow(): Flow<List<CourtReminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: CourtReminder)

    @Query("DELETE FROM court_reminders WHERE id = :id")
    suspend fun deleteReminderById(id: Int)

    // Quiz High Scores
    @Query("SELECT * FROM quiz_high_scores ORDER BY score DESC LIMIT 10")
    fun getHighScoresFlow(): Flow<List<QuizHighScore>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighScore(highScore: QuizHighScore)

    // Avatar
    @Query("SELECT * FROM avatar_config WHERE id = 1")
    suspend fun getAvatarConfig(): AvatarConfig?

    @Query("SELECT * FROM avatar_config WHERE id = 1")
    fun getAvatarConfigFlow(): Flow<AvatarConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvatarConfig(config: AvatarConfig)

    // Law Alerts
    @Query("SELECT * FROM law_change_alerts ORDER BY timestamp DESC")
    fun getLawAlertsFlow(): Flow<List<LawChangeAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLawAlert(alert: LawChangeAlert)

    @Query("UPDATE law_change_alerts SET isRead = 1 WHERE id = :id")
    suspend fun markAlertAsRead(id: Int)
}

// --- Database ---

@Database(
    entities = [CaseFile::class, CourtReminder::class, QuizHighScore::class, AvatarConfig::class, LawChangeAlert::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lawDao(): LawDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ai_lawyer_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
