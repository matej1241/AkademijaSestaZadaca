package hr.ferit.brunozoric.taskie.model.request

data class UpdateRequest(
    val id: String,
    val title: String,
    val content: String,
    val taskPriority: Int,
    val dueDate: String
)