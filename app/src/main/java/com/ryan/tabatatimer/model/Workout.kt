package com.ryan.tabatatimer.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "workouts")
@Parcelize
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val workDurationSeconds: Int,
    val restDurationSeconds: Int,
    val rounds: Int,
    val warmupSeconds: Int,
    val colorCheck: Int = 0 // Placeholder for potential color customization
) : Parcelable
