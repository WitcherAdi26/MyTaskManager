package com.example.mytaskmanager.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var title: String,
    var description: String,
    var dueDate: String,
    var priority: String,
    var isCompleted: Boolean = false
)