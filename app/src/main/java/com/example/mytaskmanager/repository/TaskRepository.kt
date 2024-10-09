package com.example.mytaskmanager.repository

import com.example.mytaskmanager.data.Task
import com.example.mytaskmanager.data.TaskDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class TaskRepository(private val taskDao: TaskDao) {

    fun getAllTasks(): StateFlow<List<Task>> {
        return taskDao.getAllTasks()
            .stateIn(
                scope = CoroutineScope(Dispatchers.IO),
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )
    }

    fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    fun update(task: Task) {
        taskDao.updateTask(task)
    }

    fun delete(task: Task) {
        taskDao.deleteTask(task)
    }

    fun completeTask(taskId: Int, completed: Boolean) {
        taskDao.completeTask(taskId, completed)
    }
}
