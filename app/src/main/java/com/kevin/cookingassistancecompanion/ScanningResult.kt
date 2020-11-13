package com.kevin.cookingassistancecompanion

object ScanningResult {
    private val mutableMap = mutableMapOf<String, Int>()

    @Synchronized
    fun incrementKey(key: String) {
        mutableMap.compute(key) { _, oldValue ->
            return@compute if (oldValue == null) {
                1
            } else {
                oldValue + 1
            }
        }
    }

    fun getSortedResult(): List<String> {
        return mutableMap.toList()
            .sortedByDescending { it.second }
            .map { it.first }
    }

    fun clear() {
        mutableMap.clear()
    }
}