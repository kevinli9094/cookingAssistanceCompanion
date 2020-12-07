package com.kevin.cookingassistancecompanion.viewmodels.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intuit.fuzzymatcher.component.MatchService
import com.intuit.fuzzymatcher.domain.Document
import com.intuit.fuzzymatcher.domain.Document.Builder
import com.intuit.fuzzymatcher.domain.Element
import com.intuit.fuzzymatcher.domain.ElementType
import com.intuit.fuzzymatcher.domain.MatchType
import com.kevin.cookingassistancecompanion.ScanningResult
import com.kevin.cookingassistancecompanion.coordinators.ResultActivityCoordinator
import com.kevin.cookingassistancecompanion.data.RealmIngredientsDatastore
import com.kevin.cookingassistancecompanion.data.RealmItemIngredientMapDatastore
import com.kevin.cookingassistancecompanion.data.RealmItemNamesDatastore
import com.kevin.cookingassistancecompanion.data.SharePreferenceDatastore
import com.kevin.cookingassistancecompanion.models.gson.UpdateIngredientBody
import com.kevin.cookingassistancecompanion.services.CookingAssistanceService
import com.kevin.cookingassistancecompanion.viewmodels.result.ResultItemViewModel.Companion.ITEM_TYPE_RESULT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.max

class ResultActivityViewModel(
    private val coordinator: ResultActivityCoordinator,
    private val sharePreferenceDatastore: SharePreferenceDatastore
) : ViewModel() {
    companion object {
        const val TAG = "ResultActivityViewModel"
    }

    private val mutableData: MutableLiveData<List<ResultItemViewModel>> by lazy {
        MutableLiveData(emptyList())
    }

    private val mutableScrollPosition = MutableLiveData(0)

    private val mutableDataList = mutableListOf<ResultItemViewModel>()

    private val mutableIsLoading: MutableLiveData<Boolean> = MutableLiveData(true)

    val scrollPositionObservable: LiveData<Int> = mutableScrollPosition

    private val ingredientDatastore = RealmIngredientsDatastore()
    private val itemNamesDatastore = RealmItemNamesDatastore()
    private val itemIngredientsDatastore = RealmItemIngredientMapDatastore()

    private val viewModelFactory = ScannedResultItemVMFactory(
        ingredientDatastore,
        itemNamesDatastore,
        itemIngredientsDatastore
    )

    init {
        viewModelScope.launch(Dispatchers.Default) {
            processData()
        }
    }

    private suspend fun processData() {
        val outputResult = ScanningResult.getSortedResult()
        val documentList: List<Document> =
            outputResult.mapIndexed { index, value ->
                Builder(index.toString())
                    .addElement(
                        Element.Builder<String>()
                            .setValue(value)
                            .setType(ElementType.TEXT)
                            .setMatchType(MatchType.EQUALITY)
                            .setPreProcessingFunction {
                                return@setPreProcessingFunction it
                            }
                            .setThreshold(0.9)
                            .createElement()
                    )
                    .createDocument()
            }

        val correctItemList = RealmItemNamesDatastore().getTAndTItemPairList()
        val existingDoc = correctItemList.mapIndexed { index, value ->
            Builder(index.toString())
                .addElement(
                    Element.Builder<String>()
                        .setValue(value.name)
                        .setType(ElementType.TEXT)
                        .setMatchType(MatchType.EQUALITY)
                        .setPreProcessingFunction {
                            return@setPreProcessingFunction it
                        }
                        .setThreshold(0.9)
                        .createElement()
                )
                .createDocument()
        }

        val matchService = MatchService()
        val result = matchService.applyMatch(existingDoc, documentList)

        withContext(Dispatchers.Main) {
            val viewModelScanneds: List<ScannedResultItemViewModel> = result.map {
                viewModelFactory.createExistingVM(it.key.elements.first().value as String)
            }

            mutableDataList.addAll(viewModelScanneds)
            mutableDataList.add(ButtonResultItemViewModel("Add"))
            mutableData.postValue(mutableDataList)
            mutableIsLoading.postValue(false)
            ScanningResult.setResult(emptyList())
        }
    }

    fun getData(): LiveData<List<ResultItemViewModel>> {
        return mutableData
    }

    fun getIsLoading(): LiveData<Boolean> {
        return mutableIsLoading
    }

    /**
     * remove item from result list
     */
    @Synchronized
    fun remove(viewModel: ResultItemViewModel) {
        viewModel.destroyLifecycle()
        mutableDataList.remove(viewModel)
        mutableData.postValue(mutableDataList)
    }

    fun addEditableItem() {
        val position = max(mutableDataList.size - 1, 0)
        mutableDataList.add(
            position,
            viewModelFactory.createNewVM()
        )
        mutableData.postValue(mutableDataList)
        mutableScrollPosition.postValue(mutableDataList.size - 1)
    }

    /**
     * Save the data to the cloud
     */
    fun save() {
        val ingredients: List<String> = mutableDataList
            .filter { it.itemType == ITEM_TYPE_RESULT }
            .mapNotNull {
                val convertedResult = it.convertedTextObservable.value
                return@mapNotNull if (convertedResult != null && convertedResult.isNotBlank()) {
                    convertedResult
                } else {
                    it.textObservable.value
                }
            }

        if (ingredients.isEmpty()) {
            // todo: show message
            return
        }

        val url = sharePreferenceDatastore.getUrl()
        val userId = sharePreferenceDatastore.getSelectedUserId()
        if (url.isNotBlank() && userId.isNotBlank()) {
            val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(CookingAssistanceService::class.java)

            mutableIsLoading.postValue(true)
            service.updateIngredient(
                UpdateIngredientBody(
                    userId,
                    ingredients
                )
            ).enqueue(object : Callback<Void> {
                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {
                    // todo: display successful message
                    mutableIsLoading.postValue(false)
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // todo: show error message
                    mutableIsLoading.postValue(false)
                }
            })
        } else {
            // todo: show warning message
        }
    }

    fun scan() {
        coordinator.openSelectStoreActivity()
    }

    override fun onCleared() {
        mutableDataList.forEach { it.destroyLifecycle() }
        mutableDataList.clear()
    }
}