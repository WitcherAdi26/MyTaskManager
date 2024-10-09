package com.example.mytaskmanager.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mytaskmanager.R
import com.example.mytaskmanager.data.Task

class TaskAdapter : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskTitle: TextView = view.findViewById(R.id.taskTitle)
        val taskDescription: TextView = view.findViewById(R.id.taskDescription)
        val taskDueDate: TextView = view.findViewById(R.id.taskDueDate)
        val taskPriority: TextView = view.findViewById(R.id.taskPriority)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position) // Use getItem instead of taskList[position]
        holder.taskTitle.text = task.title
        holder.taskDescription.text = task.description
        holder.taskDueDate.text = "Due Date: ${task.dueDate}"
        holder.taskPriority.text = "Priority: ${task.priority}"
    }
}

class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }
}










//package com.example.mytaskmanager.ui
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.example.mytaskmanager.R
//import com.example.mytaskmanager.data.Task
//
//class TaskAdapter(private val taskList: List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
//
//    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val taskTitle: TextView = view.findViewById(R.id.taskTitle)
//        val taskDescription: TextView = view.findViewById(R.id.taskDescription)
//        val taskDueDate: TextView = view.findViewById(R.id.taskDueDate)
//        val taskPriority: TextView = view.findViewById(R.id.taskPriority)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
//        return TaskViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
//        val task = taskList[position]
//        holder.taskTitle.text = task.title
//        holder.taskDescription.text = task.description
//        holder.taskDueDate.text = "Due Date: ${task.dueDate}"
//        holder.taskPriority.text = "Priority: ${task.priority}"
//    }
//
//    override fun getItemCount(): Int {
//        return taskList.size
//    }
//
////    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
////        fun bind(task: Task) {
////            // Bind data to views
////        }
////    }
//}
//
//class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
//    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
//        return oldItem.id == newItem.id
//    }
//
//    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
//        return oldItem == newItem
//    }
//}


