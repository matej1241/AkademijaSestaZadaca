package hr.ferit.brunozoric.taskie.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import hr.ferit.brunozoric.taskie.R
import hr.ferit.brunozoric.taskie.Taskie
import hr.ferit.brunozoric.taskie.common.EXTRA_TASK_ID
import hr.ferit.brunozoric.taskie.common.RESPONSE_OK
import hr.ferit.brunozoric.taskie.common.displayToast
import hr.ferit.brunozoric.taskie.common.gone
import hr.ferit.brunozoric.taskie.model.BackendTask
import hr.ferit.brunozoric.taskie.model.PriorityColor
import hr.ferit.brunozoric.taskie.model.request.UpdateRequest
import hr.ferit.brunozoric.taskie.model.response.GetTaskByIdResponse
import hr.ferit.brunozoric.taskie.model.response.UpdateResponse
import hr.ferit.brunozoric.taskie.networking.BackendFactory
import hr.ferit.brunozoric.taskie.persistence.Repository
import hr.ferit.brunozoric.taskie.ui.adapters.TaskAdapter
import hr.ferit.brunozoric.taskie.ui.fragments.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_task_details.*
import kotlinx.android.synthetic.main.fragment_tasks.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskDetailsFragment : BaseFragment() {

    private val interactor = BackendFactory.getTaskieInteractor()
    private var taskId: String? = ""

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_task_details
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val taskID = arguments?.getString(EXTRA_TASK_ID)
        taskId = taskID
        tryDisplayTask(taskID)
        initListeners()
    }

    private fun tryDisplayTask(id: String?) {
        try {
            interactor.getTaskById(id.toString(), getTaskByIdCallback())
        } catch (e: NoSuchElementException) {
            context?.displayToast(getString(R.string.noTaskFound))
        }
    }

    private fun displayTask(title: String?, content: String?, priority: Int?) {
        detailsTaskTitle.setText(title)
        detailsTaskDescription.setText(content)
        when(priority){
            1 -> detailsPriorityView.setBackgroundResource(PriorityColor.LOW.getColor())
            2 -> detailsPriorityView.setBackgroundResource(PriorityColor.MEDIUM.getColor())
            3 -> detailsPriorityView.setBackgroundResource(PriorityColor.HIGH.getColor())
        }
        prioritySelector.adapter = ArrayAdapter<PriorityColor>(Taskie.instance, android.R.layout.simple_spinner_dropdown_item, PriorityColor.values())
        when(priority){
            1 -> prioritySelector.setSelection(0)
            2 -> prioritySelector.setSelection(1)
            3 -> prioritySelector.setSelection(2)
        }
    }

    private fun initListeners() {
        editTaskButton.setOnClickListener { editTask() }
    }

    private fun editTask(){
        val title = detailsTaskTitle.text.toString()
        val description = detailsTaskDescription.text.toString()
        var priority: Int = 1
        when(prioritySelector.selectedItem){
            PriorityColor.LOW -> priority = 1
            PriorityColor.MEDIUM -> priority = 2
            PriorityColor.HIGH -> priority = 3
        }
        val dueDate = "1.10.1000"
        interactor.updateTask(
            UpdateRequest(this.taskId!!, title, description, priority, dueDate),
            updateTaskCallback()
        )
    }

    private fun getTaskByIdCallback(): Callback<GetTaskByIdResponse> = object : Callback<GetTaskByIdResponse> {
        override fun onFailure(call: Call<GetTaskByIdResponse>, t: Throwable) {
            progress.gone()
        }

        override fun onResponse(call: Call<GetTaskByIdResponse>?, response: Response<GetTaskByIdResponse>) {
            if (response.isSuccessful) {
                when (response.code()) {
                    RESPONSE_OK -> handleOkResponse(response)
                    else -> handleSomethingWentWrong()
                }
            }
        }
    }

    private fun updateTaskCallback(): Callback<UpdateResponse> = object : Callback<UpdateResponse> {
        override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
            progress.gone()
        }

        override fun onResponse(call: Call<UpdateResponse>?, response: Response<UpdateResponse>) {
            if (response.isSuccessful) {
                when (response.code()) {
                    RESPONSE_OK -> handleOkUpdateResponse()
                    else -> handleSomethingWentWrong()
                }
            }
        }
    }

    private fun handleOkResponse(response: Response<GetTaskByIdResponse>){
        displayTask(response.body()?.title, response.body()?.content, response.body()?.taskPriority)
    }

    private fun handleOkUpdateResponse() = this.activity?.displayToast("Updated")

    private fun handleSomethingWentWrong() = this.activity?.displayToast("Something went wrong!")

    companion object {
        const val NO_TASK = -1

        fun newInstance(taskId: String): TaskDetailsFragment {
            val bundle = Bundle().apply { putString(EXTRA_TASK_ID, taskId) }
            return TaskDetailsFragment().apply { arguments = bundle }
        }
    }
}
