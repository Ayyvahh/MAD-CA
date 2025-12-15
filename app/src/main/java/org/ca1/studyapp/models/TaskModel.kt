package org.ca1.studyapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskModel(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var type: TaskType = TaskType.TASK,
    var completed: Boolean = false,
    var deadline: String = ""
) : Parcelable

enum class TaskType {
    ASSIGNMENT,
    LAB,
    STUDY,
    TASK
}
