package com.kevin.cookingassistancecompanion.viewmodels.setup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.kevin.cookingassistancecompanion.activities.MessageManager
import com.kevin.cookingassistancecompanion.coordinators.SetupCoordinator
import com.kevin.cookingassistancecompanion.data.SharePreferenceDatastore
import com.kevin.cookingassistancecompanion.models.gson.User
import com.kevin.cookingassistancecompanion.services.CookingAssistanceService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * ViewModel for setup screen
 */
class SetupActivityViewModel(
    private val coordinator: SetupCoordinator,
    private val datastore: SharePreferenceDatastore,
    private val messageManager: MessageManager
) : ViewModel() {
    companion object {
        private const val TAG = "SetupActivityViewModel"
    }

    val ipAddressObservable = MutableLiveData<String>()
    val selectedUser = MutableLiveData<String>()
    val spinnerEntries = MutableLiveData<List<String>>()

    val isLoading = MutableLiveData(false)
    val showSpinner: LiveData<Boolean> = Transformations.map(spinnerEntries) {
        it.isNotEmpty()
    }
    lateinit var hideKeyBoardMethod: () -> Unit
    private var users: List<User> = emptyList()

    fun confirmIpAddress() {
        // get users with the given ip address
        val ipAddress = ipAddressObservable.value
        if (ipAddress != null) {
            hideKeyBoardMethod.invoke()
            val url = "http://$ipAddress"
            val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(CookingAssistanceService::class.java)

            isLoading.postValue(true)
            service.getUsers().enqueue(object : Callback<List<User>> {
                override fun onResponse(
                    call: Call<List<User>>,
                    response: Response<List<User>>
                ) {
                    val entries = response.body()
                    if (entries != null && entries.isNotEmpty()) {
                        users = entries
                        datastore.updateUrl(url)
                        spinnerEntries.postValue(entries.map { it.name })
                        selectedUser.postValue(entries.map { it.name }.first())
                    } else if (entries != null) {
                        messageManager.showToast("There is existing user. Please create a user first")
                    } else {
                        messageManager.showToast("There is existing user. Please create a user first")
                    }
                    isLoading.postValue(false)
                }

                override fun onFailure(call: Call<List<User>>, t: Throwable) {
                    messageManager.showToast("Fail to fetch users")
                    isLoading.postValue(false)
                }
            })
        } else {
            messageManager.showToast("Please provide a valid ip address")
        }
    }

    fun done() {
        val selectedUser = users.find { it.name == selectedUser.value }

        if (selectedUser != null) {
            datastore.updateSelectedUser(selectedUser.id)
            datastore.setup()
            coordinator.openStoreSelectionActivity()
        } else {
            messageManager.showToast("Please select a user")
        }

    }
}
