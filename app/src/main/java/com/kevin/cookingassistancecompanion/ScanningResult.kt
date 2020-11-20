package com.kevin.cookingassistancecompanion

object ScanningResult {
    private val mutableSet = LinkedHashSet<String>()

    @Synchronized
    fun addString(key: String) {
        mutableSet.add(key)
    }

    fun getSortedResult(): List<String> {
        return mutableSet.toList()
    }

    fun setResult(list: List<String>){
        mutableSet.clear()
        mutableSet.addAll(list)
    }
}