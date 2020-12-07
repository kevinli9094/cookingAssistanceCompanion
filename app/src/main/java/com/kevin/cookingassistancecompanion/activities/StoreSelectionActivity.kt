package com.kevin.cookingassistancecompanion.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kevin.cookingassistancecompanion.data.RealmIngredientsDatastore
import com.kevin.cookingassistancecompanion.data.RealmItemNamesDatastore
import com.kevin.cookingassistancecompanion.data.SharePreferenceDatastore
import com.kevin.cookingassistancecompanion.databinding.ActivitySelectStoreBinding
import com.kevin.cookingassistancecompanion.viewmodels.ViewModelFactory
import com.kevin.cookingassistancecompanion.viewmodels.storeselection.StoreSelectionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Activity for store selection screen
 */
class StoreSelectionActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "StoreSelectionActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySelectStoreBinding.inflate(layoutInflater)
        val model = ViewModelFactory(this).create(StoreSelectionViewModel::class.java)
        binding.model = model
        setContentView(binding.root)
        loadAssetToDatabase()
        detectSetup()
    }

    /**
     * Open setup page if user has not setup the ip address and user
     */
    private fun detectSetup() {
        val setup = SharePreferenceDatastore(this).isSetup()

        if (!setup) {
            val intent = Intent(this, SetupActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Use this to load asset file to database.
     * Should do nothing if database is not empty
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