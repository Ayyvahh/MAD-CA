package org.ca1.studyapp.views

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.ca1.studyapp.R
import org.ca1.studyapp.adapters.TaskAdapter
import org.ca1.studyapp.adapters.TaskListener
import org.ca1.studyapp.controllers.TaskListController
import org.ca1.studyapp.databinding.ActivityTaskListBinding
import org.ca1.studyapp.models.TaskModel

class TaskListView : AppCompatActivity(), TaskListener {

    private lateinit var binding: ActivityTaskListBinding
    lateinit var controller: TaskListController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller = TaskListController(this)

        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = "Study Tasks"
        setSupportActionBar(binding.toolbar)


        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        loadTasks()

        binding.chip3.setOnCheckedChangeListener { _, _ ->
            loadTasks()
        }
    }

    private fun loadTasks() {
        val tasks = controller.getFilteredTasks(binding.chip3.isChecked)
        binding.recyclerView.adapter = TaskAdapter(tasks, this)
    }

    fun onRefresh() {
        loadTasks()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> controller.doAddTask()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onTaskClick(task: TaskModel) {
        controller.doEditTask(task)
    }

    override fun onTaskCheckChanged(task: TaskModel, isChecked: Boolean) {
        controller.doUpdateTask(task, isChecked)
    }

    override fun onTaskDelete(task: TaskModel) {
        controller.doDeleteTask(task)
    }
}
