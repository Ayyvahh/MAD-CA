package org.ca1.studyapp.presenters

import android.content.Intent
import org.ca1.studyapp.data.FirebaseStore
import org.ca1.studyapp.models.TaskModel
import org.ca1.studyapp.views.TaskListView
import org.ca1.studyapp.views.TaskView

class TaskListPresenter(
    private val view: TaskListView,
    private val store: FirebaseStore
) {

    fun getFilteredTasks(includeCompleted: Boolean): List<TaskModel> {
        val allTasks = store.findAll()
        return if (includeCompleted) {
            allTasks.filter { it.completed }
        } else {
            allTasks.filter { !it.completed }
        }
    }

    fun doAddTask() {
        val intent = Intent(view, TaskView::class.java)
        view.startActivity(intent) // Simple startActivity
    }

    fun doEditTask(task: TaskModel) {
        val intent = Intent(view, TaskView::class.java)
        intent.putExtra("task_edit", task)
        view.startActivity(intent)
    }

    fun doUpdateTask(task: TaskModel, isChecked: Boolean) {
        task.completed = isChecked
        store.update(task)
    }

    fun doDeleteTask(task: TaskModel) {
        store.delete(task)
    }
}