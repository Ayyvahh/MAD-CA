package org.ca1.studyapp.views

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.ca1.studyapp.R
import org.ca1.studyapp.adapters.TaskAdapter
import org.ca1.studyapp.adapters.TaskListener
import org.ca1.studyapp.databinding.ActivityTaskListBinding
import org.ca1.studyapp.main.MainApp
import org.ca1.studyapp.models.TaskModel
import org.ca1.studyapp.presenters.TaskListPresenter

class TaskListView : AppCompatActivity(), TaskListener {
    private lateinit var binding: ActivityTaskListBinding
    lateinit var presenter: TaskListPresenter
    lateinit var app: MainApp


    private val taskLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as MainApp
        presenter = TaskListPresenter(this, app.store)

        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = "Study Tasks"
        setSupportActionBar(binding.toolbar)
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        app.store.tasksLiveData.observe(this) {
            loadTasks()
        }

        binding.chip3.setOnCheckedChangeListener { _, _ ->
            loadTasks()
        }
    }

    private fun loadTasks() {
        val filtered = presenter.getFilteredTasks(binding.chip3.isChecked)
        binding.recyclerView.adapter = TaskAdapter(filtered, this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> presenter.doAddTask()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onTaskClick(task: TaskModel) {
        presenter.doEditTask(task)
    }

    override fun onTaskCheckChanged(task: TaskModel, isChecked: Boolean) {
        presenter.doUpdateTask(task, isChecked)
    }

    // Ref: https://stackoverflow.com/questions/59340099/how-to-set-confirm-delete-alertdialogue-box-in-kotlin
    override fun onTaskDelete(task: TaskModel) {
        AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Yes") { _, _ ->
                presenter.doDeleteTask(task)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}