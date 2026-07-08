package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long,
    val section: String, // "VARC", "LRDI", "Quant"
    val questionsSolved: Int,
    val goal: Int?
)
