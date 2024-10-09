package com.example.mytaskmanager.ui.theme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.mytaskmanager.data.Task
import com.example.mytaskmanager.data.TaskDao
import com.example.mytaskmanager.data.TaskDatabase
import com.example.mytaskmanager.ui.TaskAdapter
import kotlinx.coroutines.launch
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.benchmark.perfetto.ExperimentalPerfettoTraceProcessorApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.mytaskmanager.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import androidx.compose.foundation.layout.*

class MainActivity : ComponentActivity() {
    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskDao: TaskDao
    private lateinit var taskDatabase: TaskDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the database
        taskDatabase = Room.databaseBuilder(
            applicationContext,
            TaskDatabase::class.java, "task_database"
        ).build()

        taskDao = taskDatabase.taskDao() // Initialize taskDao
        taskAdapter = TaskAdapter() // Initialize TaskAdapter

        setContent {
            MyTaskManagerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TaskScreen(taskViewModel)
                }
            }
        }
    }

    @Composable
    fun TaskScreen(taskViewModel: TaskViewModel) {
        val tasks by taskViewModel.tasks.collectAsState(initial = emptyList())

        var showDialog by remember { mutableStateOf(false) }
        var editingTask: Task? by remember { mutableStateOf(null) }

        if (showDialog) {
            AddTaskDialog(
                onAddTask = { task ->
                    taskViewModel.insert(task)
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }

        editingTask?.let { task ->
            EditTaskDialog(
                task = task,
                onUpdateTask = { updatedTask ->
                    taskViewModel.update(updatedTask)
                    editingTask = null
                },
                onDismiss = { editingTask = null }
            )
        }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { showAddTaskDialog() }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Task")
                }
            }
        ) { innerPadding ->
            LazyColumn(contentPadding = innerPadding) {
                items(items = tasks) { task ->
                    TaskItem(
                        task = task,
                        onComplete = { taskViewModel.completeTask(task) },
                        onDelete = { taskViewModel.delete(task) },
                        onEdit = { editingTask = task }
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalPerfettoTraceProcessorApi::class)
    @Composable
    fun TaskItem(task: Task, onComplete: (Task) -> Unit, onDelete: (Task) -> Unit, onEdit: (Task) -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.elevatedCardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = task.title, style = MaterialTheme.typography.titleMedium)
                Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
                Text(text = "Due: ${task.dueDate}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Priority: ${task.priority}", style = MaterialTheme.typography.bodySmall)

                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Button(onClick = { onComplete(task) }) {
                        Text(if (task.isCompleted) "Mark as Incomplete" else "Mark as Completed")
                    }

                    Button(onClick = { onEdit(task) }) {
                        Text("Edit")
                    }

                    Button(onClick = { onDelete(task) }) {
                        Text("Delete")
                    }
                }
            }
        }
    }


    @Composable
    fun AddTaskDialog(onAddTask: (Task) -> Unit, onDismiss: () -> Unit) {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var dueDate by remember { mutableStateOf("") }
        var selectedPriority by remember { mutableStateOf("Low") } // Default priority
        val priorities = listOf("High", "Medium", "Low")
        var expanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Add New Task")
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Task Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Task Description") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("Due Date") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Box(modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { expanded = true }) {
                            Text("Priority: $selectedPriority")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            priorities.forEach { priority ->
                                DropdownMenuItem(
                                    text = { Text(priority) },
                                    onClick = {
                                        selectedPriority = priority
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotEmpty() && description.isNotEmpty()) {
                            val task = Task(
                                title = title,
                                description = description,
                                dueDate = dueDate,
                                priority = selectedPriority,
                                isCompleted = false
                            )
                            onAddTask(task)
                            onDismiss()
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }

    @Composable
    fun EditTaskDialog(task: Task, onUpdateTask: (Task) -> Unit, onDismiss: () -> Unit) {
        var title by remember { mutableStateOf(task.title) }
        var description by remember { mutableStateOf(task.description) }
        var dueDate by remember { mutableStateOf(task.dueDate) }
        var selectedPriority by remember { mutableStateOf(task.priority) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Edit Task") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Task Title") })
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Task Description") })
                    OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Due Date") })
                    // Priority dropdown can be added here as in the AddTaskDialog
                }
            },
            confirmButton = {
                Button(onClick = {
                    val updatedTask = task.copy(title = title, description = description, dueDate = dueDate, priority = selectedPriority)
                    onUpdateTask(updatedTask)
                    onDismiss()
                }) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        )
    }


    @Preview(showBackground = true)
    @Composable
    fun TaskScreenPreview() {
        MyTaskManagerTheme {
            TaskScreen(taskViewModel =taskViewModel)
        }
    }


    private fun showAddTaskDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_add_task, null)

        val dueDateTextView = dialogView.findViewById<TextView>(R.id.dueDateTextView)
        var selectedDueDate = ""

        dialogView.findViewById<Button>(R.id.selectDueDateButton).setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                selectedDueDate = "$day/${month + 1}/$year"
                dueDateTextView.text = "Due Date: $selectedDueDate"
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        builder.setView(dialogView)
            .setTitle("Add New Task")
            .setPositiveButton("Add") { _, _ ->
                val title = dialogView.findViewById<EditText>(R.id.taskTitleInput).text.toString()
                val description = dialogView.findViewById<EditText>(R.id.taskDescriptionInput).text.toString()
                val priority = dialogView.findViewById<Spinner>(R.id.taskPriorityInput).selectedItem.toString()

                if (title.isNotEmpty() && description.isNotEmpty() && selectedDueDate.isNotEmpty()) {
                    val task = Task(title = title, description = description, dueDate = selectedDueDate, priority = priority, isCompleted = false)
                    addTaskToDatabase(task)
                }else {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addTaskToDatabase(task: Task) {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    taskDao.insertTask(task)
                }

                Toast.makeText(this@MainActivity, "Task added successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {

                Toast.makeText(this@MainActivity, "Failed to add task", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyTaskManagerTheme {
        Greeting("Android")
    }
}