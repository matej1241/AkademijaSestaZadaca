package hr.ferit.brunozoric.taskie.model.response

import androidx.appcompat.widget.DialogTitle
import hr.ferit.brunozoric.taskie.model.BackendTask

data class GetTaskByIdResponse(val id: String, val title: String,
                               val content: String, val isFavourite: Boolean,
                               val taskPriority: Int, val isCompleted: Boolean, val dueDate: String)