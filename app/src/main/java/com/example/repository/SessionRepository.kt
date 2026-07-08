package com.example.repository

import com.example.data.Session
import com.example.data.SessionDao
import kotlinx.coroutines.flow.Flow

class SessionRepository(private val sessionDao: SessionDao) {
    val allSessions: Flow<List<Session>> = sessionDao.getAllSessions()

    suspend fun insertSession(session: Session) {
        sessionDao.insertSession(session)
    }

    suspend fun deleteAllSessions() {
        sessionDao.deleteAllSessions()
    }
}
