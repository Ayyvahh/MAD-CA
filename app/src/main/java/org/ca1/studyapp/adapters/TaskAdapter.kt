package org.ca1.studyapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import org.ca1.studyapp.databinding.CardTaskBinding
import org.ca1.studyapp.models.TaskModel

interface TaskListener {
    fun onTaskClick(task: TaskModel)
    fun onTaskCheckChanged(task: TaskModel, isChecked: Boolean)
    fun onTaskDelete(task: TaskModel)
}

class TaskAdapter (private var tasks: List<TaskModel>,
                               private val listener: TaskListener) :
    RecyclerView.Adapter<TaskAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardTaskBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val task = tasks[holder.bindingAdapterPosition]
        holder.bind(task, listener)
    }

    override fun getItemCount(): Int = tasks.size

    class MainHolder(private val binding : CardTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: TaskModel, listener: TaskListener) {
            binding.taskTitle.text = task.title
            binding.description.text = task.description
            binding.taskType.text = task.type.name
            binding.taskDone.isChecked = task.completed

            binding.taskDone.setOnCheckedChangeListener(null)
            binding.taskDone.isChecked = task.completed
            binding.taskDone.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && !task.completed) {
                    val context = binding.root.context

                    // Ref: https://chatgpt.com/share/694370be-8bf4-8005-aa6e-52b680eec751
                    val animation = android.view.animation.AnimationUtils.loadAnimation(
                        context,
                        org.ca1.studyapp.R.anim.task_complete
                    )
                    animation.setAnimationListener(object :
                        android.view.animation.Animation.AnimationListener {
                        override fun onAnimationStart(animation: android.view.animation.Animation?) {}
                        override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                            listener.onTaskCheckChanged(task, isChecked)
                        }

                        override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
                    })
                    binding.root.startAnimation(animation)
                    Toast.makeText(context, "Task completed", Toast.LENGTH_SHORT).show()
                } else {
                    listener.onTaskCheckChanged(task, isChecked)
                }
            }

            binding.deleteButton.setOnClickListener { listener.onTaskDelete(task) }

            binding.root.setOnClickListener { listener.onTaskClick(task) }

            if (task.deadline.isNotBlank()) {
                binding.taskDeadline.visibility = View.VISIBLE
                binding.taskDeadline.text = "Due: ${task.deadline}"
            } else {
                binding.taskDeadline.visibility = View.GONE
            }
        }
    }
}