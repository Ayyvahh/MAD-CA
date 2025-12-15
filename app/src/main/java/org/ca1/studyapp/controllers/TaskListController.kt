package org.ca1.studyapp.controllers

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import org.ca1.studyapp.main.MainApp
import org.ca1.studyapp.models.TaskModel
import org.ca1.studyapp.views.TaskListView
import org.ca1.studyapp.views.TaskView

class TaskListController(val view: TaskListView) {

    var app: MainApp = view.application as MainApp
    private lateinit var refreshIntentLauncher: ActivityResultLauncher<Intent>

    init {
        registerRefreshCallback()
    }

    fun getTasks() = app.tasks.findAll()

    fun getFilteredTasks(includeCompleted: Boolean): List<TaskModel> {
        return if (includeCompleted) {
            getTasks().filter { it.completed }
        } else {
            getTasks().filter { !it.completed }
        }
    }

    fun doAddTask() {
        val launcherIntent = Intent(view, TaskView::class.java)
        refreshIntentLauncher.launch(launcherIntent)
    }

    fun doEditTask(task: TaskModel) {
        val launcherIntent = Intent(view, TaskView::class.java)
        launcherIntent.putExtra("task_edit", task)
        refreshIntentLauncher.launch(launcherIntent)
    }

    fun doUpdateTask(task: TaskModel, isChecked: Boolean) {
        task.completed = isChecked
        app.tasks.update(task)
        view.onRefresh()
    }

    fun doDeleteTask(task: TaskModel) {
        app.tasks.delete(task)
        view.onRefresh()
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher = view.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) view.onRefresh()
        }
    }
}
