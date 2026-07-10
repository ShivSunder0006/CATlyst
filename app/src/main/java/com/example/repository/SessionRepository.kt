package com.example.repository

import com.example.data.Session
import com.example.data.SessionDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionRepository(private val sessionDao: SessionDao) {
    val allSessions: Flow<List<Session>> = sessionDao.getAllSessions()
    
    val totalQuestionsSolved: Flow<Int> = sessionDao.getTotalQuestionsSolved().map { it ?: 0 }
    
    fun getTotalQuestionsBySection(section: String): Flow<Int> = sessionDao.getTotalQuestionsBySection(section).map { it ?: 0 }
    
    fun getTotalQuestionsBetween(startDate: Long, endDate: Long): Flow<Int> = sessionDao.getTotalQuestionsBetween(startDate, endDate).map { it ?: 0 }

    fun getSessionsBetween(startDate: Long, endDate: Long): Flow<List<Session>> = sessionDao.getSessionsBetween(startDate, endDate)

    suspend fun insertSession(session: Session) {
        sessionDao.insertSession(session)
    }
    
    suspend fun updateSession(session: Session) {
        sessionDao.updateSession(session)
    }
    
    suspend fun deleteSession(session: Session) {
        sessionDao.deleteSession(session)
    }

    suspend fun deleteAllSessions() {
        sessionDao.deleteAllSessions()
    }
}
