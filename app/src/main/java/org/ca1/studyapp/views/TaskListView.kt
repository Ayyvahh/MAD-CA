package org.ca1.studyapp.views

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as MainApp

        if (!app.authManager.isUserSignedIn()) {
            navigateToLogin()
            return
        }

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
            R.id.item_sign_out -> showSignOutDialog()
            R.id.item_clear_all -> showClearAllTasksDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSignOutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Yes") { _, _ ->
                app.authManager.signOut {
                    navigateToLogin()
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showClearAllTasksDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.clear_all_tasks_dialog_title))
            .setMessage(getString(R.string.clear_all_tasks_dialog_message))
            .setPositiveButton(android.R.string.yes) { _, _ ->
                presenter.doClearAllTasks()
                Toast.makeText(this, getString(R.string.tasks_cleared), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(android.R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginView::class.java))
        finish()
    }

    override fun onTaskClick(task: TaskModel) {
        presenter.doEditTask(task)
    }

    override fun onTaskCheckChanged(task: TaskModel, isChecked: Boolean) {
        presenter.doUpdateTask(task, isChecked)
    }

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