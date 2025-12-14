package org.ca1.studyapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.ca1.studyapp.R
import org.ca1.studyapp.adapters.TaskAdapter
import org.ca1.studyapp.adapters.TaskListener
import org.ca1.studyapp.databinding.ActivityTaskListBinding
import org.ca1.studyapp.main.MainApp
import org.ca1.studyapp.models.TaskModel

class TaskListActivity : AppCompatActivity(), TaskListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityTaskListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = "Study Tasks"
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        refreshList(binding.chip3.isChecked)

        binding.chip3.setOnCheckedChangeListener { _, isChecked ->
            refreshList(isChecked)
        }
    }

    private fun refreshList(includeCompleted: Boolean) {
        val tasks = if (includeCompleted) {
            app.tasks.findAll().filter { it.completed }
        } else {
            app.tasks.findAll().filter { !it.completed }
        }
        binding.recyclerView.adapter = TaskAdapter(tasks, this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, TaskActivity::class.java)
                getResult.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                refreshList(binding.chip3.isChecked)
            }
        }

    override fun onTaskClick(task: TaskModel) {
        val launcherIntent = Intent(this, TaskActivity::class.java)
        launcherIntent.putExtra("task_edit", task)
        getClickResult.launch(launcherIntent)
    }

    override fun onTaskCheckChanged(task: TaskModel, isChecked: Boolean) {
        task.completed = isChecked
        app.tasks.update(task)
        refreshList(binding.chip3.isChecked)
    }

    override fun onTaskDelete(task: TaskModel) {
        app.tasks.delete(task)
        refreshList(binding.chip3.isChecked)
    }

    private val getClickResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                refreshList(binding.chip3.isChecked)
            }
        }
}
