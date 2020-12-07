package com.kevin.cookingassistancecompanion.viewmodels.setup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
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
    private val datastore: SharePreferenceDatastore
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
                        // entries is empty. todo: show message
                    } else {
                        // todo: show error message
                    }
                    isLoading.postValue(false)
                }

                override fun onFailure(call: Call<List<User>>, t: Throwable) {
                    // todo: show error message
                    Log.i(TAG, "onFailure")
                    isLoading.postValue(false)
                }
            })
        } else {
            Log.i(TAG, "ip is null")
            // todo: show error message
        }
    }

    fun done() {
        val selectedUser = users.find { it.name == selectedUser.value }

        if (selectedUser != null) {
            datastore.updateSelectedUser(selectedUser.id)
            datastore.setup()
            coordinator.openStoreSelectionActivity()
        } else {
            Log.w(TAG, "there is no selected user.")
            // todo: add error message
        }

    }
}
