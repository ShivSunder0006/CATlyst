package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<Session>>
    
    @Query("SELECT SUM(questionsSolved) FROM sessions")
    fun getTotalQuestionsSolved(): Flow<Int?>
    
    @Query("SELECT SUM(questionsSolved) FROM sessions WHERE section = :section")
    fun getTotalQuestionsBySection(section: String): Flow<Int?>

    @Query("SELECT SUM(questionsSolved) FROM sessions WHERE date >= :startDate AND date <= :endDate")
    fun getTotalQuestionsBetween(startDate: Long, endDate: Long): Flow<Int?>
    
    @Query("SELECT * FROM sessions WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC")
    fun getSessionsBetween(startDate: Long, endDate: Long): Flow<List<Session>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: Session): Long

    @Update
    suspend fun updateSession(session: Session)

    @Delete
    suspend fun deleteSession(session: Session)

    @Query("DELETE FROM sessions")
    suspend fun deleteAllSessions()
}
