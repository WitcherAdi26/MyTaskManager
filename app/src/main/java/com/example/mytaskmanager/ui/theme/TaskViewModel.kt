package com.example.mytaskmanager.ui.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytaskmanager.data.Task
import com.example.mytaskmanager.data.TaskDao
import com.example.mytaskmanager.data.TaskDatabase
import com.example.mytaskmanager.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    private val taskDao: TaskDao = TaskDatabase.getDatabase(application).taskDao()

    val tasks: StateFlow<List<Task>>

    init {
        repository = TaskRepository(taskDao)
        tasks = repository.getAllTasks()
    }

    fun insert(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(task)
        }
    }

    fun update(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(task)
        }
    }

    fun delete(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(task)
        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.completeTask(task.id, !task.isCompleted)
        }
    }
}