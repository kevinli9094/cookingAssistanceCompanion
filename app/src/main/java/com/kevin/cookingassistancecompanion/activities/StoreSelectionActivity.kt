package com.kevin.cookingassistancecompanion.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kevin.cookingassistancecompanion.data.RealmIngredientsDatastore
import com.kevin.cookingassistancecompanion.data.RealmItemNamesDatastore
import com.kevin.cookingassistancecompanion.databinding.ActivitySelectStoreBinding
import com.kevin.cookingassistancecompanion.viewmodels.storeselection.StoreSelectionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class StoreSelectionActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "StoreSelectionActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySelectStoreBinding.inflate(layoutInflater)
        val model = StoreSelectionViewModel.getViewModel(this)
        binding.model = model
        setContentView(binding.root)
        loadAssetToDatabase()
    }

    /**
     * Use this to load asset file to database
     */
    private fun loadAssetToDatabase() {
        loadChineseIngredient()
        loadItemMap()
    }

    private fun loadChineseIngredient() {
        val datastore = RealmIngredientsDatastore()

        GlobalScope.launch(Dispatchers.IO) {
            if (datastore.getChineseIngredients().isNotEmpty()) {
                return@launch
            }
            var reader: BufferedReader? = null
            val mutableSet = mutableSetOf<String>()
            try {
                reader = BufferedReader(
                    InputStreamReader(assets.open("chineseIngredients.txt"))
                )

                // do reading, usually loop until end of file reading
                var mLine = reader.readLine()
                while (mLine != null) {
                    mutableSet.add(mLine)
                    mLine = reader.readLine()
                }
            } catch (e: IOException) {
                Log.e(TAG, e.stackTraceToString())
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (e: IOException) {
                        Log.e(TAG, e.stackTraceToString())
                    }
                }
            }

            datastore.insertChineseIngredients(mutableSet.toList())
        }
    }

    private fun loadItemMap() {
        val datastore = RealmItemNamesDatastore()

        GlobalScope.launch(Dispatchers.IO) {
            if (datastore.getTAndTItemPairList().isNotEmpty()) {
                return@launch
            }
            var reader: BufferedReader? = null
            val mutableMap = mutableMapOf<String, String>()
            try {
                reader = BufferedReader(
                    InputStreamReader(assets.open("t&tItemMap.txt"))
                )

                // do reading, usually loop until end of file reading
                var mLine = reader.readLine()
                while (mLine != null) {
                    Log.i(TAG, mLine)
                    val name = mLine.substringBefore("--->")
                    val chineseName = mLine.substringAfter("--->")
                    mutableMap[name] = chineseName
                    mLine = reader.readLine()
                }
            } catch (e: IOException) {
                Log.e(TAG, e.stackTraceToString())
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (e: IOException) {
                        Log.e(TAG, e.stackTraceToString())
                    }
                }
            }

            datastore.insertTAndTItemNames(mutableMap)
        }
    }
}