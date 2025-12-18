package org.ca1.studyapp.presenters

import org.ca1.studyapp.data.FirebaseStore
import org.ca1.studyapp.models.TaskModel
import org.ca1.studyapp.views.TaskView

class TaskPresenter(private val view: TaskView, private val store: FirebaseStore) {


    fun addTask(task: TaskModel) {
        store.create(task)
    }

    fun updateTask(task: TaskModel) {
        store.update(task)
    }

    // Chatgpt log for deadline validation: https://chatgpt.com/share/694341fc-5ea0-8005-90a7-86b1ef41e67b
    fun validateTask(task: TaskModel): String? {
        if (task.title.isEmpty()) return "Enter a task title"
        if (task.title.length < 3) return "Task title must be at least 3 characters"
        if (task.title.length > 100) return "Task title must not exceed 100 characters"
        if (task.description.length > 300) return "Task description must not exceed 300 characters"
        if (task.description.isEmpty()) return "Task description must be minimum 5 chars"
        if (task.deadline.isNotBlank()) {
            try {
                val dateFormat =
                    java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                val deadlineDate = dateFormat.parse(task.deadline)
                val today = dateFormat.parse(dateFormat.format(java.util.Date()))
                if (deadlineDate != null && today != null && deadlineDate.before(today)) {
                    return "Deadline cannot be before today's date"
                }
            } catch (_: Exception) {
                return "Invalid deadline format"
            }
        }
        return null
    }
}