package org.ca1.studyapp.data

/** Refs for all edits and additions for integrating firebase and livedata :
1. https://blog.stackademic.com/building-real-time-apps-with-firebase-realtime-database-and-kotlin-fd368a396e96
2. https://firebase.google.com/docs/database/android/read-and-write
3. https://medium.com/@anandgaur2207/livedata-in-android-12c9950ce9a9
4. https://www.youtube.com/watch?v=rFTJTLdoGDY
5. https://www.youtube.com/watch?v=miJooBq9iwE
6. https://www.youtube.com/watch?v=DW-d0kalMvU
7. https://www.youtube.com/watch?v=y1B8gAA_-cQ

 **/

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.ca1.studyapp.models.TaskModel
import org.ca1.studyapp.models.TaskStore
import timber.log.Timber

class FirebaseStore : TaskStore {
    private val database =
        FirebaseDatabase.getInstance("https://studyapp-faccf-default-rtdb.europe-west1.firebasedatabase.app")
    private val tasksRef = database.getReference("tasks")

    private val tasksList = mutableListOf<TaskModel>()
    private val _tasksLiveData = MutableLiveData<List<TaskModel>>()

    val tasksLiveData: LiveData<List<TaskModel>> = _tasksLiveData


    init {
        tasksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Timber.i("onDataChange called, children count: ${snapshot.childrenCount}")
                tasksList.clear()
                snapshot.children.forEach { child ->
                    val task = child.getValue(TaskModel::class.java)
                    if (task != null) {
                        tasksList.add(task)
                        Timber.i("Loaded: ${task.title}")
                    }
                }
                Timber.i("Total tasks: ${tasksList.size}")
                _tasksLiveData.value = tasksList
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("Firebase error: ${error.message}")
            }
        })
    }

    override fun findAll(): List<TaskModel> {
        return tasksList
    }

    override fun create(task: TaskModel) {
        val key = tasksRef.push().key
        if (key != null) {
            task.id = key
            tasksRef.child(key).setValue(task)
        } else {
            Timber.e("failed to generate firebase key")
        }
    }

    override fun update(task: TaskModel) {
        if (task.id.isNotEmpty()) {
            tasksRef.child(task.id).setValue(task)
        } else {
            Timber.e("no task id")
        }
    }

    override fun delete(task: TaskModel) {
        if (task.id.isNotEmpty()) {
            tasksRef.child(task.id).removeValue()
        } else {
            Timber.e("Cannot delete: task has no id")
        }
    }
}
