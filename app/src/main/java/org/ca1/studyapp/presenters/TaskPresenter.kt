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

    fun validateTask(task: TaskModel): String? {
        if (task.title.isEmpty()) return "Enter a task title"
        if (task.title.length < 3) return "Task title must be at least 3 characters"
        if (task.title.length > 100) return "Task title must not exceed 100 characters"
        if (task.description.length > 300) return "Task description must not exceed 300 characters"
        return null
    }
}